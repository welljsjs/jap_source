/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.HTTPConnection;
import HTTPClient.RoRequest;
import HTTPClient.Util;
import java.io.Serializable;
import java.net.ProtocolException;
import java.util.Date;

public class Cookie
implements Serializable {
    protected String name;
    protected String value;
    protected Date expires;
    protected String domain;
    protected String path;
    protected boolean secure;
    protected boolean httponly;

    public Cookie(String string, String string2, String string3, String string4, Date date, boolean bl, boolean bl2) {
        if (string == null) {
            throw new NullPointerException("missing name");
        }
        if (string2 == null) {
            throw new NullPointerException("missing value");
        }
        if (string3 == null) {
            throw new NullPointerException("missing domain");
        }
        if (string4 == null) {
            throw new NullPointerException("missing path");
        }
        this.name = string;
        this.value = string2;
        this.domain = string3;
        this.path = string4;
        this.expires = date;
        this.secure = bl;
        this.httponly = bl2;
        if (this.domain.indexOf(46) == -1) {
            this.domain = this.domain + ".local";
        }
    }

    protected Cookie(RoRequest roRequest) {
        this.name = null;
        this.value = null;
        this.expires = null;
        this.domain = roRequest.getConnection().getHost();
        if (this.domain.indexOf(46) == -1) {
            this.domain = this.domain + ".local";
        }
        this.path = Util.getPath(roRequest.getRequestURI());
        String string = roRequest.getConnection().getProtocol();
        this.secure = string.equals("https") || string.equals("shttp");
    }

    protected static Cookie[] parse(String string, RoRequest roRequest) throws ProtocolException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        char[] arrc = string.toCharArray();
        int n4 = arrc.length;
        Cookie[] arrcookie = new Cookie[]{};
        while ((n = Util.skipSpace(arrc, n)) < n4) {
            if (arrc[n] == ',') {
                ++n;
                continue;
            }
            Cookie cookie = new Cookie(roRequest);
            n3 = n;
            boolean bl = true;
            while (n < n4 && arrc[n] != ',') {
                int n5;
                if (arrc[n] == ';') {
                    n = Util.skipSpace(arrc, n + 1);
                    continue;
                }
                if (string.regionMatches(true, n, "secure", 0, 6)) {
                    cookie.secure = true;
                    n += 6;
                    if ((n = Util.skipSpace(arrc, n)) < n4 && arrc[n] == ';') {
                        n = Util.skipSpace(arrc, n + 1);
                        continue;
                    }
                    if (n >= n4 || arrc[n] == ',') continue;
                    throw new ProtocolException("Bad Set-Cookie header: " + string + "\nExpected " + "';' or ',' at position " + n);
                }
                if (string.regionMatches(true, n, "HttpOnly", 0, 8)) {
                    cookie.httponly = true;
                    n += 8;
                    if ((n = Util.skipSpace(arrc, n)) < n4 && arrc[n] == ';') {
                        n = Util.skipSpace(arrc, n + 1);
                        continue;
                    }
                    if (n >= n4 || arrc[n] == ',') continue;
                    throw new ProtocolException("Bad Set-Cookie header: " + string + "\nExpected " + "';' or ',' at position " + n);
                }
                n2 = string.indexOf(61, n);
                if (n2 == -1) {
                    throw new ProtocolException("Bad Set-Cookie header: " + string + "\nNo '=' found " + "for token starting at " + "position " + n);
                }
                String string2 = string.substring(n, n2).trim();
                n = Util.skipSpace(arrc, n2 + 1);
                if (string2.equalsIgnoreCase("expires") && (n5 = string.indexOf(44, n)) != -1) {
                    n = n5 + 1;
                }
                n5 = string.indexOf(44, n);
                int n6 = string.indexOf(59, n);
                n2 = n5 == -1 && n6 == -1 ? n4 : (n5 == -1 ? n6 : (n6 == -1 ? n5 : Math.min(n5, n6)));
                String string3 = string.substring(n, n2).trim();
                if (string2.equalsIgnoreCase("expires")) {
                    try {
                        cookie.expires = Util.parseDate(string3);
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                } else if (string2.equalsIgnoreCase("domain")) {
                    if ((string3 = string3.toLowerCase()).charAt(0) != '.' && !string3.equals(cookie.domain)) {
                        string3 = '.' + string3;
                    }
                    if (!cookie.domain.endsWith(string3)) {
                        bl = false;
                    }
                    if (!string3.equals(".local") && string3.indexOf(46, 1) == -1) {
                        bl = false;
                    }
                    String string4 = null;
                    if (string3.length() > 3) {
                        string4 = string3.substring(string3.length() - 4);
                    }
                    if (!(string4 != null && (string4.equalsIgnoreCase(".com") || string4.equalsIgnoreCase(".edu") || string4.equalsIgnoreCase(".net") || string4.equalsIgnoreCase(".org") || string4.equalsIgnoreCase(".gov") || string4.equalsIgnoreCase(".mil") || string4.equalsIgnoreCase(".int")) || cookie.domain.substring(0, cookie.domain.length() - string3.length()).indexOf(46) == -1)) {
                        bl = false;
                    }
                    cookie.domain = string3;
                } else if (string2.equalsIgnoreCase("path")) {
                    cookie.path = string3;
                } else {
                    cookie.name = string2;
                    cookie.value = string3;
                }
                if ((n = n2) >= n4 || arrc[n] != ';') continue;
                n = Util.skipSpace(arrc, n + 1);
            }
            if (cookie.name == null || cookie.value == null) {
                throw new ProtocolException("Bad Set-Cookie header: " + string + "\nNo Name=Value found" + " for cookie starting at " + "posibition " + n3);
            }
            if (!bl) continue;
            arrcookie = Util.resizeArray(arrcookie, arrcookie.length + 1);
            arrcookie[arrcookie.length - 1] = cookie;
        }
        return arrcookie;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public Date expires() {
        return this.expires;
    }

    public boolean discard() {
        return this.expires == null;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public boolean isHttpOnly() {
        return this.httponly;
    }

    public boolean hasExpired() {
        return this.expires != null && this.expires().before(new Date());
    }

    protected boolean sendWith(RoRequest roRequest) {
        HTTPConnection hTTPConnection = roRequest.getConnection();
        String string = hTTPConnection.getHost();
        if (string.indexOf(46) == -1) {
            string = string + ".local";
        }
        return (this.domain.charAt(0) == '.' && string.endsWith(this.domain) || this.domain.charAt(0) != '.' && string.equals(this.domain)) && Util.getPath(roRequest.getRequestURI()).startsWith(this.path) && (!this.secure || hTTPConnection.getProtocol().equals("https") || hTTPConnection.getProtocol().equals("shttp"));
    }

    public int hashCode() {
        return this.name.hashCode() + this.path.hashCode() + this.domain.hashCode();
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof Cookie) {
            Cookie cookie = (Cookie)object;
            return this.name.equals(cookie.name) && this.path.equals(cookie.path) && this.domain.equals(cookie.domain);
        }
        return false;
    }

    protected String toExternalForm() {
        return this.name + "=" + this.value;
    }

    public String toString() {
        String string = this.name + "=" + this.value;
        if (this.expires != null) {
            string = string + "; expires=" + this.expires;
        }
        if (this.path != null) {
            string = string + "; path=" + this.path;
        }
        if (this.domain != null) {
            string = string + "; domain=" + this.domain;
        }
        if (this.secure) {
            string = string + "; secure";
        }
        return string;
    }
}

