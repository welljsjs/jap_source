/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.ParseException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.BitSet;

public class URI {
    protected static BitSet alphanumChar;
    protected static BitSet markChar;
    protected static BitSet reservedChar;
    protected static BitSet unreservedChar;
    protected static BitSet uricChar;
    protected static BitSet pcharChar;
    protected static BitSet userinfoChar;
    protected static BitSet schemeChar;
    protected static BitSet reg_nameChar;
    protected boolean is_generic;
    protected String scheme;
    protected String opaque;
    protected String userinfo;
    protected String host;
    protected int port = -1;
    protected String path;
    protected String query;
    protected String fragment;
    protected URL url = null;
    private static final char[] hex;

    public URI(String string) throws ParseException {
        this((URI)null, string);
    }

    public URI(URI uRI, String string) throws ParseException {
        int n;
        int n2;
        char[] arrc = string.toCharArray();
        int n3 = arrc.length;
        for (n2 = 0; n2 < n3 && Character.isWhitespace(arrc[n2]); ++n2) {
        }
        while (n3 > 0 && Character.isWhitespace(arrc[n3 - 1])) {
            --n3;
        }
        if (!(n2 >= n3 - 3 || arrc[n2 + 3] != ':' || arrc[n2 + 0] != 'u' && arrc[n2 + 0] != 'U' || arrc[n2 + 1] != 'r' && arrc[n2 + 1] != 'R' || arrc[n2 + 2] != 'i' && arrc[n2 + 2] != 'I' && arrc[n2 + 2] != 'l' && arrc[n2 + 2] != 'L')) {
            n2 += 4;
        }
        for (n = n2; n < n3 && arrc[n] != ':' && arrc[n] != '/' && arrc[n] != '?' && arrc[n] != '#'; ++n) {
        }
        if (n < n3 && arrc[n] == ':') {
            this.scheme = string.substring(n2, n).trim().toLowerCase();
            n2 = n + 1;
        }
        String string2 = this.scheme;
        if (this.scheme == null) {
            if (uRI == null) {
                throw new ParseException("No scheme found");
            }
            string2 = uRI.scheme;
        }
        this.is_generic = URI.usesGenericSyntax(string2);
        if (!this.is_generic) {
            if (uRI != null && this.scheme == null) {
                throw new ParseException("Can't resolve relative URI for scheme " + string2);
            }
            this.opaque = string.substring(n2);
            return;
        }
        if (n2 < n3 - 1 && arrc[n2] == '/' && arrc[n2 + 1] == '/') {
            for (n = n2 += 2; n < n3 && arrc[n] != '/' && arrc[n] != '?' && arrc[n] != '#'; ++n) {
            }
            this.parse_authority(string.substring(n2, n), string2);
            n2 = n;
        }
        for (n = n2; n < n3 && arrc[n] != '?' && arrc[n] != '#'; ++n) {
        }
        this.path = string.substring(n2, n);
        n2 = n;
        if (n2 < n3 && arrc[n2] == '?') {
            for (n = ++n2; n < n3 && arrc[n] != '#'; ++n) {
            }
            this.query = URI.unescape(string.substring(n2, n));
            n2 = n;
        }
        if (n2 < n3 && arrc[n2] == '#') {
            this.fragment = URI.unescape(string.substring(n2 + 1, n3));
        }
        if (uRI != null) {
            if (this.scheme != null) {
                return;
            }
            this.scheme = uRI.scheme;
            if (this.host != null) {
                return;
            }
            this.userinfo = uRI.userinfo;
            this.host = uRI.host;
            this.port = uRI.port;
            if (this.path.length() == 0 && this.query == null) {
                this.path = uRI.path;
                this.query = uRI.query;
                return;
            }
            if (this.path.length() == 0 || this.path.charAt(0) != '/') {
                n = uRI.path.lastIndexOf(47);
                if (n == -1) {
                    return;
                }
                this.path = uRI.path.substring(0, n + 1) + this.path;
                n3 = this.path.length();
                n = this.path.indexOf("/.");
                if (n == -1 || n != n3 - 2 && this.path.charAt(n + 2) != '/' && (this.path.charAt(n + 2) != '.' || n != n3 - 3 && this.path.charAt(n + 3) != '/')) {
                    return;
                }
                char[] arrc2 = new char[this.path.length()];
                this.path.getChars(0, arrc2.length, arrc2, 0);
                for (n = 1; n < n3; ++n) {
                    int n4;
                    if (arrc2[n] != '.' || arrc2[n - 1] != '/') continue;
                    if (n == n3 - 1) {
                        n4 = n++;
                    } else if (arrc2[n + 1] == '/') {
                        n4 = n - 1;
                        ++n;
                    } else {
                        if (arrc2[n + 1] != '.' || n != n3 - 2 && arrc2[n + 2] != '/') continue;
                        for (n4 = n - 2; n4 > 0 && arrc2[n4] != '/'; --n4) {
                        }
                        if (arrc2[n4] != '/') continue;
                        if (n == n3 - 2) {
                            ++n4;
                        }
                        n += 2;
                    }
                    System.arraycopy(arrc2, n, arrc2, n4, n3 - n);
                    n3 -= n - n4;
                    n = n4;
                }
                this.path = new String(arrc2, 0, n3);
            }
        }
    }

    private void parse_authority(String string, String string2) throws ParseException {
        int n;
        char[] arrc = string.toCharArray();
        int n2 = 0;
        int n3 = arrc.length;
        for (n = n2; n < n3 && arrc[n] != '@'; ++n) {
        }
        if (n < n3 && arrc[n] == '@') {
            this.userinfo = URI.unescape(string.substring(n2, n));
            n2 = n + 1;
        }
        for (n = n2; n < n3 && arrc[n] != ':'; ++n) {
        }
        this.host = string.substring(n2, n);
        n2 = n;
        if (n2 < n3 - 1 && arrc[n2] == ':') {
            int n4;
            try {
                n4 = Integer.parseInt(string.substring(n2 + 1, n3));
                if (n4 < 0) {
                    throw new NumberFormatException();
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new ParseException(string.substring(n2 + 1, n3) + " is an invalid port number");
            }
            this.port = n4 == URI.defaultPort(string2) ? -1 : n4;
        }
    }

    public URI(String string, String string2, String string3) throws ParseException {
        this(string, null, string2, -1, string3, null, null);
    }

    public URI(String string, String string2, int n, String string3) throws ParseException {
        this(string, null, string2, n, string3, null, null);
    }

    public URI(String string, String string2, String string3, int n, String string4, String string5, String string6) throws ParseException {
        if (string == null) {
            throw new ParseException("missing scheme");
        }
        this.scheme = string.trim();
        if (string2 != null) {
            this.userinfo = URI.unescape(string2.trim());
        }
        if (string3 != null) {
            this.host = string3.trim();
        }
        if (n != URI.defaultPort(string)) {
            this.port = n;
        }
        if (string4 != null) {
            this.path = string4.trim();
        }
        if (string5 != null) {
            this.query = string5.trim();
        }
        if (string6 != null) {
            this.fragment = string6.trim();
        }
        this.is_generic = true;
    }

    public URI(String string, String string2) throws ParseException {
        if (string == null) {
            throw new ParseException("missing scheme");
        }
        this.scheme = string.trim().toLowerCase();
        this.opaque = string2;
        this.is_generic = false;
    }

    public static boolean usesGenericSyntax(String string) {
        return (string = string.trim()).equalsIgnoreCase("http") || string.equalsIgnoreCase("https") || string.equalsIgnoreCase("shttp") || string.equalsIgnoreCase("coffee") || string.equalsIgnoreCase("ftp") || string.equalsIgnoreCase("file") || string.equalsIgnoreCase("gopher") || string.equalsIgnoreCase("nntp") || string.equalsIgnoreCase("telnet") || string.equalsIgnoreCase("imap") || string.equalsIgnoreCase("wais") || string.equalsIgnoreCase("nfs") || string.equalsIgnoreCase("ldap") || string.equalsIgnoreCase("prospero");
    }

    public static final int defaultPort(String string) {
        String string2 = string.trim();
        if (string2.equalsIgnoreCase("http") || string2.equalsIgnoreCase("shttp") || string2.equalsIgnoreCase("http-ng") || string2.equalsIgnoreCase("coffee")) {
            return 80;
        }
        if (string2.equalsIgnoreCase("https")) {
            return 443;
        }
        if (string2.equalsIgnoreCase("ftp")) {
            return 21;
        }
        if (string2.equalsIgnoreCase("telnet")) {
            return 23;
        }
        if (string2.equalsIgnoreCase("nntp")) {
            return 119;
        }
        if (string2.equalsIgnoreCase("smtp")) {
            return 25;
        }
        if (string2.equalsIgnoreCase("gopher")) {
            return 70;
        }
        if (string2.equalsIgnoreCase("wais")) {
            return 210;
        }
        if (string2.equalsIgnoreCase("whois")) {
            return 43;
        }
        if (string2.equalsIgnoreCase("imap")) {
            return 143;
        }
        if (string2.equalsIgnoreCase("prospero")) {
            return 1525;
        }
        if (string2.equalsIgnoreCase("ldap")) {
            return 389;
        }
        if (string2.equalsIgnoreCase("nfs")) {
            return 2049;
        }
        return 0;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getOpaque() {
        return this.opaque;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getUserinfo() {
        return this.userinfo;
    }

    public String getPath() {
        if (this.query != null) {
            if (this.path != null) {
                return this.path + "?" + this.query;
            }
            return "?" + this.query;
        }
        return this.path;
    }

    public String getQueryString() {
        return this.query;
    }

    public String getFragment() {
        return this.fragment;
    }

    public boolean isGenericURI() {
        return this.is_generic;
    }

    public URL toURL() throws MalformedURLException {
        if (this.url != null) {
            return this.url;
        }
        if (this.opaque != null) {
            this.url = new URL(this.scheme + ":" + this.opaque);
            return this.url;
        }
        StringBuffer stringBuffer = new StringBuffer(100);
        if (this.path != null) {
            stringBuffer.append(URI.escape(this.path.toCharArray(), uricChar));
        }
        if (this.query != null) {
            stringBuffer.append('?');
            stringBuffer.append(URI.escape(this.query.toCharArray(), uricChar));
        }
        if (this.fragment != null) {
            stringBuffer.append('#');
            stringBuffer.append(URI.escape(this.fragment.toCharArray(), uricChar));
        }
        this.url = new URL(this.scheme, this.host, this.port, stringBuffer.toString());
        return this.url;
    }

    public String toExternalForm() {
        StringBuffer stringBuffer = new StringBuffer(100);
        if (this.scheme != null) {
            stringBuffer.append(this.scheme);
            stringBuffer.append(':');
        }
        if (this.opaque != null) {
            stringBuffer.append(URI.escape(this.opaque.toCharArray(), uricChar));
            return stringBuffer.toString();
        }
        if (this.userinfo != null || this.host != null || this.port != -1) {
            stringBuffer.append("//");
        }
        if (this.userinfo != null) {
            stringBuffer.append(URI.escape(this.userinfo.toCharArray(), userinfoChar));
            stringBuffer.append('@');
        }
        if (this.host != null) {
            stringBuffer.append(this.host.toCharArray());
        }
        if (this.port != -1) {
            stringBuffer.append(':');
            stringBuffer.append(this.port);
        }
        if (this.path != null) {
            stringBuffer.append(this.path.toCharArray());
        }
        if (this.query != null) {
            stringBuffer.append('?');
            stringBuffer.append(URI.escape(this.query.toCharArray(), uricChar));
        }
        if (this.fragment != null) {
            stringBuffer.append('#');
            stringBuffer.append(URI.escape(this.fragment.toCharArray(), uricChar));
        }
        return stringBuffer.toString();
    }

    public String toString() {
        return this.toExternalForm();
    }

    public boolean equals(Object object) {
        if (object instanceof URI) {
            URI uRI = (URI)object;
            return this.scheme.equalsIgnoreCase(uRI.scheme) && (!this.is_generic && (this.opaque == null && uRI.opaque == null || this.opaque != null && uRI.opaque != null && this.opaque.equals(uRI.opaque)) || this.is_generic && (this.userinfo == null && uRI.userinfo == null || this.userinfo != null && uRI.userinfo != null && this.userinfo.equals(uRI.userinfo)) && (this.host == null && uRI.host == null || this.host != null && uRI.host != null && this.host.equalsIgnoreCase(uRI.host)) && this.port == uRI.port && (this.path == null && uRI.path == null || this.path != null && uRI.path != null && URI.unescapeNoPE(this.path).equals(URI.unescapeNoPE(uRI.path))) && (this.query == null && uRI.query == null || this.query != null && uRI.query != null && URI.unescapeNoPE(this.query).equals(URI.unescapeNoPE(uRI.query))) && (this.fragment == null && uRI.fragment == null || this.fragment != null && uRI.fragment != null && URI.unescapeNoPE(this.fragment).equals(URI.unescapeNoPE(uRI.fragment))));
        }
        if (object instanceof URL) {
            URL uRL = (URL)object;
            String string = this.userinfo != null ? this.userinfo + "@" + this.host : this.host;
            String string2 = this.query != null ? this.path + "?" + this.query : this.path;
            return this.scheme.equalsIgnoreCase(uRL.getProtocol()) && (!this.is_generic && this.opaque.equals(uRL.getFile()) || this.is_generic && (string == null && uRL.getHost() == null || string != null && uRL.getHost() != null && string.equalsIgnoreCase(uRL.getHost())) && (this.port == uRL.getPort() || uRL.getPort() == URI.defaultPort(this.scheme)) && (string2 == null && uRL.getFile() == null || string2 != null && uRL.getFile() != null && URI.unescapeNoPE(string2).equals(URI.unescapeNoPE(uRL.getFile()))) && (this.fragment == null && uRL.getRef() == null || this.fragment != null && uRL.getRef() != null && URI.unescapeNoPE(this.fragment).equals(URI.unescapeNoPE(uRL.getRef()))));
        }
        return false;
    }

    private static char[] escape(char[] arrc, BitSet bitSet) {
        int n = 0;
        for (int i = 0; i < arrc.length; ++i) {
            if (bitSet.get(arrc[i])) continue;
            ++n;
        }
        if (n == 0) {
            return arrc;
        }
        char[] arrc2 = new char[arrc.length + 2 * n];
        int n2 = 0;
        int n3 = 0;
        while (n2 < arrc.length) {
            if (bitSet.get(arrc[n2])) {
                arrc2[n3] = arrc[n2];
            } else {
                if (arrc[n2] > '\u00ff') {
                    throw new RuntimeException("Can't handle non 8-bt chars");
                }
                arrc2[n3++] = 37;
                arrc2[n3++] = hex[arrc[n2] >> 4 & 0xF];
                arrc2[n3] = hex[arrc[n2] & 0xF];
            }
            ++n2;
            ++n3;
        }
        return arrc2;
    }

    static final String unescape(String string) throws ParseException {
        if (string == null || string.indexOf(37) == -1) {
            return string;
        }
        char[] arrc = string.toCharArray();
        char[] arrc2 = new char[arrc.length];
        int n = 0;
        int n2 = 0;
        while (n2 < arrc.length) {
            if (arrc[n2] == '%') {
                int n3;
                try {
                    n3 = Integer.parseInt(string.substring(n2 + 1, n2 + 3), 16);
                    if (n3 < 0) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException numberFormatException) {
                    throw new ParseException(string.substring(n2, n2 + 3) + " is an invalid code");
                }
                arrc2[n] = (char)n3;
                n2 += 2;
            } else {
                arrc2[n] = arrc[n2];
            }
            ++n2;
            ++n;
        }
        return new String(arrc2, 0, n);
    }

    private static final String unescapeNoPE(String string) {
        try {
            return URI.unescape(string);
        }
        catch (ParseException parseException) {
            return string;
        }
    }

    public static void main(String[] arrstring) throws Exception {
        System.err.println();
        System.err.println("*** URI Tests ...");
        URI uRI = new URI("http://a/b/c/d;p?q");
        URI.testParser(uRI, "g:h", "g:h");
        URI.testParser(uRI, "g", "http://a/b/c/g");
        URI.testParser(uRI, "./g", "http://a/b/c/g");
        URI.testParser(uRI, "g/", "http://a/b/c/g/");
        URI.testParser(uRI, "/g", "http://a/g");
        URI.testParser(uRI, "//g", "http://g");
        URI.testParser(uRI, "?y", "http://a/b/c/?y");
        URI.testParser(uRI, "g?y", "http://a/b/c/g?y");
        URI.testParser(uRI, "g?y", "http://a/b/c/g?y");
        URI.testParser(uRI, "#s", "http://a/b/c/d;p?q#s");
        URI.testParser(uRI, "g#s", "http://a/b/c/g#s");
        URI.testParser(uRI, "g?y#s", "http://a/b/c/g?y#s");
        URI.testParser(uRI, ";x", "http://a/b/c/;x");
        URI.testParser(uRI, "g;x", "http://a/b/c/g;x");
        URI.testParser(uRI, "g;x?y#s", "http://a/b/c/g;x?y#s");
        URI.testParser(uRI, ".", "http://a/b/c/");
        URI.testParser(uRI, "./", "http://a/b/c/");
        URI.testParser(uRI, "..", "http://a/b/");
        URI.testParser(uRI, "../", "http://a/b/");
        URI.testParser(uRI, "../g", "http://a/b/g");
        URI.testParser(uRI, "../..", "http://a/");
        URI.testParser(uRI, "../../", "http://a/");
        URI.testParser(uRI, "../../g", "http://a/g");
        URI.testParser(uRI, "", "http://a/b/c/d;p?q");
        URI.testParser(uRI, "/./g", "http://a/./g");
        URI.testParser(uRI, "/../g", "http://a/../g");
        URI.testParser(uRI, "g.", "http://a/b/c/g.");
        URI.testParser(uRI, ".g", "http://a/b/c/.g");
        URI.testParser(uRI, "g..", "http://a/b/c/g..");
        URI.testParser(uRI, "..g", "http://a/b/c/..g");
        URI.testParser(uRI, "./../g", "http://a/b/g");
        URI.testParser(uRI, "./g/.", "http://a/b/c/g/");
        URI.testParser(uRI, "g/./h", "http://a/b/c/g/h");
        URI.testParser(uRI, "g/../h", "http://a/b/c/h");
        URI.testParser(uRI, "g;x=1/./y", "http://a/b/c/g;x=1/y");
        URI.testParser(uRI, "g;x=1/../y", "http://a/b/c/y");
        URI.testParser(uRI, "g?y/./x", "http://a/b/c/g?y/./x");
        URI.testParser(uRI, "g?y/../x", "http://a/b/c/g?y/../x");
        URI.testParser(uRI, "g#s/./x", "http://a/b/c/g#s/./x");
        URI.testParser(uRI, "g#s/../x", "http://a/b/c/g#s/../x");
        URI.testParser(uRI, "http:g", "http:g");
        URI.testNotEqual("http://a/", "nntp://a/");
        URI.testNotEqual("http://a/", "https://a/");
        URI.testNotEqual("http://a/", "shttp://a/");
        URI.testEqual("http://a/", "Http://a/");
        URI.testEqual("http://a/", "hTTP://a/");
        URI.testEqual("url:http://a/", "hTTP://a/");
        URI.testEqual("urI:http://a/", "hTTP://a/");
        URI.testEqual("http://a/", "Http://A/");
        URI.testEqual("http://a.b.c/", "Http://A.b.C/");
        URI.testEqual("http:///", "Http:///");
        URI.testNotEqual("http:///", "Http://a/");
        URI.testEqual("http://a.b.c/", "Http://A.b.C:80/");
        URI.testEqual("nntp://a", "nntp://a:119");
        URI.testEqual("nntp://a/", "nntp://a:119/");
        URI.testNotEqual("nntp://a", "nntp://a:118");
        URI.testNotEqual("nntp://a", "nntp://a:0");
        URI.testEqual("telnet://:23/", "telnet:///");
        URI.testPE("ftp://:a/");
        URI.testPE("ftp://:-1/");
        URI.testPE("ftp://::1/");
        URI.testNotEqual("ftp://me@a", "ftp://a");
        URI.testNotEqual("ftp://me@a", "ftp://Me@a");
        URI.testEqual("ftp://Me@a", "ftp://Me@a");
        URI.testEqual("ftp://Me:My@a:21", "ftp://Me:My@a");
        URI.testNotEqual("ftp://Me:My@a:21", "ftp://Me:my@a");
        URI.testEqual("ftp://a/b%2b/", "ftp://a/b+/");
        URI.testEqual("ftp://a/b%2b/", "ftp://a/b+/");
        URI.testEqual("ftp://a/b%5E/", "ftp://a/b^/");
        URI.testNotEqual("ftp://a/b%3f/", "ftp://a/b?/");
        System.err.println("*** Tests finished successfuly");
    }

    private static void testParser(URI uRI, String string, String string2) throws Exception {
        if (!new URI(uRI, string).toString().equals(string2)) {
            String string3 = System.getProperty("line.separator");
            throw new Exception("Test failed: " + string3 + "  base-URI = <" + uRI + ">" + string3 + "  rel-URI  = <" + string + ">" + string3 + "  expected   <" + string2 + ">" + string3 + "  but got    <" + new URI(uRI, string) + ">");
        }
    }

    private static void testEqual(String string, String string2) throws Exception {
        if (!new URI(string).equals(new URI(string2))) {
            String string3 = System.getProperty("line.separator");
            throw new Exception("Test failed: " + string3 + "  <" + string + "> != <" + string2 + ">");
        }
    }

    private static void testNotEqual(String string, String string2) throws Exception {
        if (new URI(string).equals(new URI(string2))) {
            String string3 = System.getProperty("line.separator");
            throw new Exception("Test failed: " + string3 + "  <" + string + "> == <" + string2 + ">");
        }
    }

    private static void testPE(String string) throws Exception {
        boolean bl = false;
        try {
            new URI(string);
        }
        catch (ParseException parseException) {
            bl = true;
        }
        if (!bl) {
            String string2 = System.getProperty("line.separator");
            throw new Exception("Test failed: " + string2 + "  <" + string + "> should be invalid");
        }
    }

    static {
        int n;
        alphanumChar = new BitSet(128);
        for (n = 48; n <= 57; ++n) {
            alphanumChar.set(n);
        }
        for (n = 65; n <= 90; ++n) {
            alphanumChar.set(n);
        }
        for (n = 97; n <= 122; ++n) {
            alphanumChar.set(n);
        }
        markChar = new BitSet(128);
        markChar.set(45);
        markChar.set(95);
        markChar.set(46);
        markChar.set(33);
        markChar.set(126);
        markChar.set(42);
        markChar.set(39);
        markChar.set(40);
        markChar.set(41);
        reservedChar = new BitSet(128);
        reservedChar.set(59);
        reservedChar.set(47);
        reservedChar.set(63);
        reservedChar.set(58);
        reservedChar.set(64);
        reservedChar.set(38);
        reservedChar.set(61);
        reservedChar.set(43);
        reservedChar.set(36);
        reservedChar.set(44);
        unreservedChar = new BitSet(128);
        unreservedChar.or(alphanumChar);
        unreservedChar.or(markChar);
        uricChar = new BitSet(128);
        uricChar.or(unreservedChar);
        uricChar.or(reservedChar);
        pcharChar = new BitSet(128);
        pcharChar.or(unreservedChar);
        pcharChar.set(58);
        pcharChar.set(64);
        pcharChar.set(38);
        pcharChar.set(61);
        pcharChar.set(43);
        pcharChar.set(36);
        pcharChar.set(44);
        userinfoChar = new BitSet(128);
        userinfoChar.or(unreservedChar);
        userinfoChar.set(59);
        userinfoChar.set(58);
        userinfoChar.set(38);
        userinfoChar.set(61);
        userinfoChar.set(43);
        userinfoChar.set(36);
        userinfoChar.set(44);
        schemeChar = new BitSet(128);
        schemeChar.or(alphanumChar);
        schemeChar.set(43);
        schemeChar.set(45);
        schemeChar.set(46);
        reg_nameChar = new BitSet(128);
        reg_nameChar.or(unreservedChar);
        reg_nameChar.set(36);
        reg_nameChar.set(44);
        reg_nameChar.set(59);
        reg_nameChar.set(58);
        reg_nameChar.set(64);
        reg_nameChar.set(38);
        reg_nameChar.set(61);
        reg_nameChar.set(43);
        hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }
}

