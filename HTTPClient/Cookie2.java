/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Cookie;
import HTTPClient.HTTPConnection;
import HTTPClient.HttpHeaderElement;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.RoRequest;
import HTTPClient.Util;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class Cookie2
extends Cookie {
    protected int version;
    protected boolean discard;
    protected String comment;
    protected URL comment_url;
    protected int[] port_list;
    protected String port_list_str;
    protected boolean path_set;
    protected boolean port_set;
    protected boolean domain_set;

    public Cookie2(String string, String string2, String string3, int[] arrn, String string4, Date date, boolean bl, boolean bl2, boolean bl3, String string5, URL uRL) {
        super(string, string2, string3, string4, date, bl2, bl3);
        this.discard = bl;
        this.port_list = arrn;
        this.comment = string5;
        this.comment_url = uRL;
        this.path_set = true;
        this.domain_set = true;
        if (arrn != null && arrn.length > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(arrn[0]);
            for (int i = 1; i < arrn.length; ++i) {
                stringBuffer.append(',');
                stringBuffer.append(arrn[i]);
            }
            this.port_list_str = stringBuffer.toString();
            this.port_set = true;
        }
        this.version = 1;
    }

    protected Cookie2(RoRequest roRequest) {
        super(roRequest);
        int n = this.path.lastIndexOf(47);
        if (n != -1) {
            this.path = this.path.substring(0, n + 1);
        }
        if (this.domain.indexOf(46) == -1) {
            this.domain = this.domain + ".local";
        }
        this.version = -1;
        this.discard = false;
        this.comment = null;
        this.comment_url = null;
        this.port_list = null;
        this.port_list_str = null;
        this.path_set = false;
        this.port_set = false;
        this.domain_set = false;
    }

    protected static Cookie[] parse(String string, RoRequest roRequest) throws ProtocolException {
        Vector vector;
        try {
            vector = Util.parseHeader(string);
        }
        catch (ParseException parseException) {
            throw new ProtocolException(parseException.getMessage());
        }
        Cookie[] arrcookie = new Cookie[vector.size()];
        int n = 0;
        for (int i = 0; i < arrcookie.length; ++i) {
            HttpHeaderElement httpHeaderElement = (HttpHeaderElement)vector.elementAt(i);
            if (httpHeaderElement.getValue() == null) {
                throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nMissing value " + "for cookie '" + httpHeaderElement.getName() + "'");
            }
            Cookie2 cookie2 = new Cookie2(roRequest);
            cookie2.name = httpHeaderElement.getName();
            cookie2.value = httpHeaderElement.getValue();
            NVPair[] arrnVPair = httpHeaderElement.getParams();
            boolean bl = false;
            boolean bl2 = false;
            for (int j = 0; j < arrnVPair.length; ++j) {
                String string2 = arrnVPair[j].getName().toLowerCase();
                if ((string2.equals("version") || string2.equals("max-age") || string2.equals("domain") || string2.equals("path") || string2.equals("comment") || string2.equals("commenturl")) && arrnVPair[j].getValue() == null) {
                    throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nMissing value " + "for " + arrnVPair[j].getName() + " attribute in cookie '" + httpHeaderElement.getName() + "'");
                }
                if (string2.equals("version")) {
                    if (cookie2.version != -1) continue;
                    try {
                        cookie2.version = Integer.parseInt(arrnVPair[j].getValue());
                        continue;
                    }
                    catch (NumberFormatException numberFormatException) {
                        throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nVersion '" + arrnVPair[j].getValue() + "' not a number");
                    }
                }
                if (string2.equals("path")) {
                    if (cookie2.path_set) continue;
                    cookie2.path = arrnVPair[j].getValue();
                    cookie2.path_set = true;
                    continue;
                }
                if (string2.equals("domain")) {
                    if (cookie2.domain_set) continue;
                    String string3 = arrnVPair[j].getValue().toLowerCase();
                    cookie2.domain = string3.charAt(0) != '.' && !string3.equals(cookie2.domain) ? "." + string3 : string3;
                    cookie2.domain_set = true;
                    continue;
                }
                if (string2.equals("max-age")) {
                    int n2;
                    if (cookie2.expires != null) continue;
                    try {
                        n2 = Integer.parseInt(arrnVPair[j].getValue());
                    }
                    catch (NumberFormatException numberFormatException) {
                        throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nMax-Age '" + arrnVPair[j].getValue() + "' not a number");
                    }
                    cookie2.expires = new Date(System.currentTimeMillis() + (long)n2 * 1000L);
                    continue;
                }
                if (string2.equals("port")) {
                    if (cookie2.port_set) continue;
                    if (arrnVPair[j].getValue() == null) {
                        cookie2.port_list = new int[1];
                        cookie2.port_list[0] = roRequest.getConnection().getPort();
                        cookie2.port_set = true;
                        continue;
                    }
                    cookie2.port_list_str = arrnVPair[j].getValue();
                    StringTokenizer stringTokenizer = new StringTokenizer(arrnVPair[j].getValue(), ",");
                    cookie2.port_list = new int[stringTokenizer.countTokens()];
                    for (int k = 0; k < cookie2.port_list.length; ++k) {
                        String string4 = stringTokenizer.nextToken().trim();
                        try {
                            cookie2.port_list[k] = Integer.parseInt(string4);
                            continue;
                        }
                        catch (NumberFormatException numberFormatException) {
                            throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nPort '" + string4 + "' not a number");
                        }
                    }
                    cookie2.port_set = true;
                    continue;
                }
                if (string2.equals("discard")) {
                    if (bl) continue;
                    cookie2.discard = true;
                    bl = true;
                    continue;
                }
                if (string2.equals("secure")) {
                    if (bl2) continue;
                    cookie2.secure = true;
                    bl2 = true;
                    continue;
                }
                if (string2.equals("comment")) {
                    if (cookie2.comment != null) continue;
                    cookie2.comment = arrnVPair[j].getValue();
                    continue;
                }
                if (!string2.equals("commenturl") || cookie2.comment_url != null) continue;
                try {
                    cookie2.comment_url = new URL(arrnVPair[j].getValue());
                    continue;
                }
                catch (MalformedURLException malformedURLException) {
                    throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nCommentURL '" + arrnVPair[j].getValue() + "' not a valid URL");
                }
            }
            if (cookie2.version == -1) {
                throw new ProtocolException("Bad Set-Cookie2 header: " + string + "\nMissing Version " + "attribute");
            }
            if (cookie2.version != 1) continue;
            if (cookie2.expires == null) {
                cookie2.discard = true;
            }
            if (!Util.getPath(roRequest.getRequestURI()).startsWith(cookie2.path)) continue;
            String string5 = roRequest.getConnection().getHost();
            if (string5.indexOf(46) == -1) {
                string5 = string5 + ".local";
            }
            if (!cookie2.domain.equals(".local") && cookie2.domain.indexOf(46, 1) == -1 || !string5.endsWith(cookie2.domain) || string5.substring(0, string5.length() - cookie2.domain.length()).indexOf(46) != -1) continue;
            if (cookie2.port_set) {
                int n3 = 0;
                for (n3 = 0; n3 < cookie2.port_list.length && cookie2.port_list[n3] != roRequest.getConnection().getPort(); ++n3) {
                }
                if (n3 == cookie2.port_list.length) continue;
            }
            arrcookie[n++] = cookie2;
        }
        if (n < arrcookie.length) {
            arrcookie = Util.resizeArray(arrcookie, n);
        }
        return arrcookie;
    }

    public int getVersion() {
        return this.version;
    }

    public String getComment() {
        return this.comment;
    }

    public URL getCommentURL() {
        return this.comment_url;
    }

    public int[] getPorts() {
        return this.port_list;
    }

    public boolean discard() {
        return this.discard;
    }

    protected boolean sendWith(RoRequest roRequest) {
        String string;
        boolean bl;
        HTTPConnection hTTPConnection = roRequest.getConnection();
        boolean bl2 = bl = !this.port_set;
        if (this.port_set) {
            for (int i = 0; i < this.port_list.length; ++i) {
                if (this.port_list[i] != hTTPConnection.getPort()) continue;
                bl = true;
                break;
            }
        }
        if ((string = hTTPConnection.getHost()).indexOf(46) == -1) {
            string = string + ".local";
        }
        return (this.domain.charAt(0) == '.' && string.endsWith(this.domain) || this.domain.charAt(0) != '.' && string.equals(this.domain)) && bl && Util.getPath(roRequest.getRequestURI()).startsWith(this.path) && (!this.secure || hTTPConnection.getProtocol().equals("https") || hTTPConnection.getProtocol().equals("shttp"));
    }

    protected String toExternalForm() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.version == 1) {
            stringBuffer.append(this.name);
            stringBuffer.append("=");
            stringBuffer.append(this.value);
            if (this.path_set) {
                stringBuffer.append("; ");
                stringBuffer.append("$Path=");
                stringBuffer.append(this.path);
            }
            if (this.domain_set) {
                stringBuffer.append("; ");
                stringBuffer.append("$Domain=");
                stringBuffer.append(this.domain);
            }
            if (this.port_set) {
                stringBuffer.append("; ");
                stringBuffer.append("$Port");
                if (this.port_list_str != null) {
                    stringBuffer.append("=\"");
                    stringBuffer.append(this.port_list_str);
                    stringBuffer.append('\"');
                }
            }
        } else {
            throw new Error("Internal Error: unknown version " + this.version);
        }
        return stringBuffer.toString();
    }

    public String toString() {
        String string = this.name + "=" + this.value;
        if (this.version == 1) {
            string = string + "; Version=" + this.version;
            string = string + "; Path=" + this.path;
            string = string + "; Domain=" + this.domain;
            if (this.port_set) {
                string = string + "; Port=\"" + this.port_list[0];
                for (int i = 1; i < this.port_list.length; ++i) {
                    string = string + "," + this.port_list[i];
                }
                string = string + "\"";
            }
            if (this.expires != null) {
                string = string + "; Max-Age=" + (this.expires.getTime() - new Date().getTime()) / 1000L;
            }
            if (this.discard) {
                string = string + "; Discard";
            }
            if (this.secure) {
                string = string + "; Secure";
            }
            if (this.comment != null) {
                string = string + "; Comment=\"" + this.comment + "\"";
            }
            if (this.comment_url != null) {
                string = string + "; CommentURL=\"" + this.comment_url + "\"";
            }
        } else {
            throw new Error("Internal Error: unknown version " + this.version);
        }
        return string;
    }
}

