/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthorizationInfo;
import HTTPClient.Cookie;
import HTTPClient.HttpHeaderElement;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.Vector;

public class Util {
    private static final BitSet Separators;
    private static final BitSet TokenChar;
    private static final BitSet UnsafeChar;
    private static SimpleDateFormat http_format;
    static final char[] hex_map;
    private static final String nl;
    static /* synthetic */ Class class$HTTPClient$Util;

    private Util() {
    }

    static final Object[] resizeArray(Object[] arrobject, int n) {
        Object[] arrobject2 = new Object[n];
        System.arraycopy(arrobject, 0, arrobject2, 0, arrobject.length < n ? arrobject.length : n);
        return arrobject2;
    }

    static final NVPair[] resizeArray(NVPair[] arrnVPair, int n) {
        NVPair[] arrnVPair2 = new NVPair[n];
        System.arraycopy(arrnVPair, 0, arrnVPair2, 0, arrnVPair.length < n ? arrnVPair.length : n);
        return arrnVPair2;
    }

    static final AuthorizationInfo[] resizeArray(AuthorizationInfo[] arrauthorizationInfo, int n) {
        AuthorizationInfo[] arrauthorizationInfo2 = new AuthorizationInfo[n];
        System.arraycopy(arrauthorizationInfo, 0, arrauthorizationInfo2, 0, arrauthorizationInfo.length < n ? arrauthorizationInfo.length : n);
        return arrauthorizationInfo2;
    }

    static final Cookie[] resizeArray(Cookie[] arrcookie, int n) {
        Cookie[] arrcookie2 = new Cookie[n];
        System.arraycopy(arrcookie, 0, arrcookie2, 0, arrcookie.length < n ? arrcookie.length : n);
        return arrcookie2;
    }

    static final String[] resizeArray(String[] arrstring, int n) {
        String[] arrstring2 = new String[n];
        System.arraycopy(arrstring, 0, arrstring2, 0, arrstring.length < n ? arrstring.length : n);
        return arrstring2;
    }

    static final boolean[] resizeArray(boolean[] arrbl, int n) {
        boolean[] arrbl2 = new boolean[n];
        System.arraycopy(arrbl, 0, arrbl2, 0, arrbl.length < n ? arrbl.length : n);
        return arrbl2;
    }

    static final byte[] resizeArray(byte[] arrby, int n) {
        byte[] arrby2 = new byte[n];
        System.arraycopy(arrby, 0, arrby2, 0, arrby.length < n ? arrby.length : n);
        return arrby2;
    }

    static final char[] resizeArray(char[] arrc, int n) {
        char[] arrc2 = new char[n];
        System.arraycopy(arrc, 0, arrc2, 0, arrc.length < n ? arrc.length : n);
        return arrc2;
    }

    static final int[] resizeArray(int[] arrn, int n) {
        int[] arrn2 = new int[n];
        System.arraycopy(arrn, 0, arrn2, 0, arrn.length < n ? arrn.length : n);
        return arrn2;
    }

    static String[] splitProperty(String string) {
        if (string == null) {
            return new String[0];
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, "|");
        String[] arrstring = new String[stringTokenizer.countTokens()];
        for (int i = 0; i < arrstring.length; ++i) {
            arrstring[i] = stringTokenizer.nextToken().trim();
        }
        return arrstring;
    }

    static String[] splitList(String string, String string2) {
        if (string == null) {
            return new String[0];
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2);
        String[] arrstring = new String[stringTokenizer.countTokens()];
        for (int i = 0; i < arrstring.length; ++i) {
            arrstring[i] = stringTokenizer.nextToken().trim();
        }
        return arrstring;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static final Hashtable getList(Hashtable hashtable, Object object) {
        Hashtable hashtable2 = (Hashtable)hashtable.get(object);
        if (hashtable2 == null) {
            Hashtable hashtable3 = hashtable;
            synchronized (hashtable3) {
                hashtable2 = (Hashtable)hashtable.get(object);
                if (hashtable2 == null) {
                    hashtable2 = new Hashtable();
                    hashtable.put(object, hashtable2);
                }
            }
        }
        return hashtable2;
    }

    static final int[] compile_search(byte[] arrby) {
        int[] arrn = new int[]{0, 1, 0, 1, 0, 1};
        for (int i = 0; i < arrby.length; ++i) {
            int n;
            for (n = i + 1; n < arrby.length && arrby[i] != arrby[n]; ++n) {
            }
            if (n >= arrby.length) continue;
            if (n - i > arrn[1]) {
                arrn[4] = arrn[2];
                arrn[5] = arrn[3];
                arrn[2] = arrn[0];
                arrn[3] = arrn[1];
                arrn[0] = i;
                arrn[1] = n - i;
                continue;
            }
            if (n - i > arrn[3]) {
                arrn[4] = arrn[2];
                arrn[5] = arrn[3];
                arrn[2] = i;
                arrn[3] = n - i;
                continue;
            }
            if (n - i <= arrn[3]) continue;
            arrn[4] = i;
            arrn[5] = n - i;
        }
        arrn[1] = arrn[1] + arrn[0];
        arrn[3] = arrn[3] + arrn[2];
        arrn[5] = arrn[5] + arrn[4];
        return arrn;
    }

    static final int findStr(byte[] arrby, int[] arrn, byte[] arrby2, int n, int n2) {
        int n3 = arrn[0];
        int n4 = arrn[1];
        int n5 = n4 - n3;
        int n6 = arrn[2];
        int n7 = arrn[3];
        int n8 = n7 - n6;
        int n9 = arrn[4];
        int n10 = arrn[5];
        int n11 = n10 - n9;
        while (n + arrby.length <= n2) {
            if (arrby[n4] == arrby2[n + n4]) {
                if (arrby[n3] == arrby2[n + n3]) {
                    boolean bl = true;
                    for (int i = 0; i < arrby.length; ++i) {
                        if (arrby[i] == arrby2[n + i]) continue;
                        bl = false;
                        break;
                    }
                    if (bl) break;
                }
                n += n5;
                continue;
            }
            if (arrby[n7] == arrby2[n + n7]) {
                n += n8;
                continue;
            }
            if (arrby[n10] == arrby2[n + n10]) {
                n += n11;
                continue;
            }
            ++n;
        }
        if (n + arrby.length > n2) {
            return -1;
        }
        return n;
    }

    public static final String dequoteString(String string) {
        if (string.indexOf(92) == -1) {
            return string;
        }
        char[] arrc = string.toCharArray();
        int n = 0;
        for (int i = 0; i < arrc.length; ++i) {
            if (arrc[i] != '\\' || i + 1 >= arrc.length) continue;
            System.arraycopy(arrc, i + 1, arrc, i, arrc.length - i - 1);
            ++n;
        }
        return new String(arrc, 0, arrc.length - n);
    }

    public static final String quoteString(String string, String string2) {
        int n;
        char[] arrc = string2.toCharArray();
        for (n = 0; n < arrc.length && string.indexOf(arrc[n]) == -1; ++n) {
        }
        if (n == arrc.length) {
            return string;
        }
        int n2 = string.length();
        char[] arrc2 = new char[n2 * 2];
        string.getChars(0, n2, arrc2, 0);
        for (int i = 0; i < n2; ++i) {
            if (string2.indexOf(arrc2[i], 0) == -1) continue;
            if (n2 == arrc2.length) {
                arrc2 = Util.resizeArray(arrc2, n2 + string.length());
            }
            System.arraycopy(arrc2, i, arrc2, i + 1, n2 - i);
            ++n2;
            arrc2[i++] = 92;
        }
        return new String(arrc2, 0, n2);
    }

    public static final Vector parseHeader(String string) throws ParseException {
        return Util.parseHeader(string, true);
    }

    public static final Vector parseHeader(String string, boolean bl) throws ParseException {
        if (string == null) {
            return null;
        }
        Vector<HttpHeaderElement> vector = new Vector<HttpHeaderElement>();
        boolean bl2 = true;
        int n = -1;
        int n2 = 0;
        int n3 = string.length();
        int[] arrn = new int[1];
        while (true) {
            String string2;
            if (!bl2) {
                n = Util.skipSpace(string, n2);
                if (n == n3) break;
                if (string.charAt(n) != ',') {
                    throw new ParseException("Bad header format: '" + string + "'\nExpected \",\" at position " + n);
                }
            }
            bl2 = false;
            if ((n = Util.skipSpace(string, n + 1)) == n3) break;
            char c = string.charAt(n);
            if (c == ',') {
                n2 = n;
                continue;
            }
            if (c == '=' || c == ';' || c == '\"') {
                throw new ParseException("Bad header format: '" + string + "'\nEmpty element name at position " + n);
            }
            for (n2 = n + 1; n2 < n3 && !Character.isWhitespace(c = string.charAt(n2)) && c != '=' && c != ',' && c != ';'; ++n2) {
            }
            String string3 = string.substring(n, n2);
            n = Util.skipSpace(string, n2);
            if (n < n3 && string.charAt(n) == '=') {
                arrn[0] = n + 1;
                string2 = Util.parseValue(string, arrn, bl);
                n2 = arrn[0];
            } else {
                string2 = null;
                n2 = n;
            }
            NVPair[] arrnVPair = new NVPair[]{};
            while ((n = Util.skipSpace(string, n2)) != n3 && string.charAt(n) == ';') {
                String string4;
                if ((n = Util.skipSpace(string, n + 1)) == n3 || (c = string.charAt(n)) == ',') {
                    n2 = n;
                    break;
                }
                if (c == ';') {
                    n2 = n;
                    continue;
                }
                if (c == '=' || c == '\"') {
                    throw new ParseException("Bad header format: '" + string + "'\nEmpty parameter name at position " + n);
                }
                for (n2 = n + 1; n2 < n3 && !Character.isWhitespace(c = string.charAt(n2)) && c != '=' && c != ',' && c != ';'; ++n2) {
                }
                String string5 = string.substring(n, n2);
                n = Util.skipSpace(string, n2);
                if (n < n3 && string.charAt(n) == '=') {
                    arrn[0] = n + 1;
                    string4 = Util.parseValue(string, arrn, bl);
                    n2 = arrn[0];
                } else {
                    string4 = null;
                    n2 = n;
                }
                arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                arrnVPair[arrnVPair.length - 1] = new NVPair(string5, string4);
            }
            vector.addElement(new HttpHeaderElement(string3, string2, arrnVPair));
        }
        return vector;
    }

    private static String parseValue(char[] arrc, int[] arrn, String string, boolean bl) throws ParseException {
        String string2;
        int n;
        int n2 = n = arrn[0];
        int n3 = arrc.length;
        if ((n = Util.skipSpace(arrc, n)) < n3 && arrc[n] == '\"') {
            char[] arrc2 = null;
            int n4 = 0;
            int n5 = n;
            for (n2 = ++n; n2 < n3 && arrc[n2] != '\"'; ++n2) {
                if (arrc[n2] != '\\') continue;
                if (bl) {
                    if (arrc2 == null) {
                        arrc2 = new char[arrc.length];
                    }
                    System.arraycopy(arrc, n5, arrc2, n4, n2 - n5);
                    n4 += n2 - n5;
                    n5 = ++n2;
                    continue;
                }
                ++n2;
            }
            if (n2 == n3) {
                throw new ParseException("Bad header format: '" + string + "'\nClosing <\"> for quoted-string" + " starting at position " + (n - 1) + " not found");
            }
            if (arrc2 != null) {
                System.arraycopy(arrc, n5, arrc2, n4, n2 - n5);
                string2 = new String(arrc2, 0, n4 += n2 - n5);
            } else {
                string2 = new String(arrc, n, n2 - n);
            }
        } else {
            for (n2 = n; n2 < n3 && !Character.isWhitespace(arrc[n2]) && arrc[n2] != ',' && arrc[n2] != ';'; ++n2) {
            }
            string2 = new String(arrc, n, n2 - n);
        }
        arrn[0] = ++n2;
        return string2;
    }

    private static String parseValue(String string, int[] arrn, boolean bl) throws ParseException {
        String string2;
        int n;
        int n2 = n = arrn[0];
        int n3 = string.length();
        if ((n = Util.skipSpace(string, n)) < n3 && string.charAt(n) == '\"') {
            char c;
            char[] arrc = null;
            int n4 = 0;
            int n5 = n;
            for (n2 = ++n; n2 < n3 && (c = string.charAt(n2)) != '\"'; ++n2) {
                if (c != '\\') continue;
                if (bl) {
                    if (arrc == null) {
                        arrc = new char[n3];
                    }
                    string.getChars(n5, n2, arrc, n4);
                    n4 += n2 - n5;
                    n5 = ++n2;
                    continue;
                }
                ++n2;
            }
            if (n2 == n3) {
                throw new ParseException("Bad header format: '" + string + "'\nClosing <\"> for quoted-string" + " starting at position " + (n - 1) + " not found");
            }
            if (arrc != null) {
                string.getChars(n5, n2, arrc, n4);
                string2 = new String(arrc, 0, n4 += n2 - n5);
            } else {
                string2 = string.substring(n, n2);
            }
        } else {
            char c;
            for (n2 = n; n2 < n3 && !Character.isWhitespace(c = string.charAt(n2)) && c != ',' && c != ';'; ++n2) {
            }
            string2 = string.substring(n, n2);
        }
        arrn[0] = ++n2;
        return string2;
    }

    public static final boolean hasToken(String string, String string2) throws ParseException {
        if (string == null) {
            return false;
        }
        return Util.parseHeader(string).contains(new HttpHeaderElement(string2));
    }

    public static final HttpHeaderElement getElement(Vector vector, String string) {
        int n = vector.indexOf(new HttpHeaderElement(string));
        if (n == -1) {
            return null;
        }
        return (HttpHeaderElement)vector.elementAt(n);
    }

    public static final String getParameter(String string, String string2) throws ParseException {
        NVPair[] arrnVPair = ((HttpHeaderElement)Util.parseHeader(string2).firstElement()).getParams();
        for (int i = 0; i < arrnVPair.length; ++i) {
            if (!arrnVPair[i].getName().equalsIgnoreCase(string)) continue;
            return arrnVPair[i].getValue();
        }
        return null;
    }

    public static final String assembleHeader(Vector vector) {
        StringBuffer stringBuffer = new StringBuffer(200);
        int n = vector.size();
        for (int i = 0; i < n; ++i) {
            ((HttpHeaderElement)vector.elementAt(i)).appendTo(stringBuffer);
            stringBuffer.append(", ");
        }
        stringBuffer.setLength(stringBuffer.length() - 2);
        return stringBuffer.toString();
    }

    static final int skipSpace(String string, int n) {
        int n2 = string.length();
        while (n < n2 && Character.isWhitespace(string.charAt(n))) {
            ++n;
        }
        return n;
    }

    static final int skipSpace(char[] arrc, int n) {
        int n2 = arrc.length;
        while (n < n2 && Character.isWhitespace(arrc[n])) {
            ++n;
        }
        return n;
    }

    static final int findSpace(String string, int n) {
        int n2 = string.length();
        while (n < n2 && !Character.isWhitespace(string.charAt(n))) {
            ++n;
        }
        return n;
    }

    static final int findSpace(char[] arrc, int n) {
        int n2 = arrc.length;
        while (n < n2 && !Character.isWhitespace(arrc[n])) {
            ++n;
        }
        return n;
    }

    static final int skipToken(char[] arrc, int n) {
        int n2 = arrc.length;
        while (n < n2 && TokenChar.get(arrc[n])) {
            ++n;
        }
        return n;
    }

    static final boolean needsQuoting(String string) {
        int n;
        int n2 = string.length();
        for (n = 0; n < n2 && TokenChar.get(string.charAt(n)); ++n) {
        }
        return n < n2;
    }

    public static final String getValue(NVPair[] arrnVPair, String string) {
        int n = arrnVPair.length;
        for (int i = 0; i < n; ++i) {
            if (!arrnVPair[i].getName().equalsIgnoreCase(string)) continue;
            return arrnVPair[i].getValue();
        }
        return null;
    }

    public static final int getIndex(NVPair[] arrnVPair, String string) {
        int n = arrnVPair.length;
        for (int i = 0; i < n; ++i) {
            if (!arrnVPair[i].getName().equalsIgnoreCase(string)) continue;
            return i;
        }
        return -1;
    }

    public static final NVPair[] setValue(NVPair[] arrnVPair, String string, String string2) {
        int n = Util.getIndex(arrnVPair, string);
        if (n == -1) {
            n = arrnVPair.length;
            arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
        }
        arrnVPair[n] = new NVPair(string, string2);
        return arrnVPair;
    }

    public static final NVPair[] setValue(NVPair[] arrnVPair, String string, String string2, boolean bl) {
        int n = Util.getIndex(arrnVPair, string);
        if (n == -1) {
            n = arrnVPair.length;
            arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
        }
        arrnVPair[n] = new NVPair(string, string2, bl);
        return arrnVPair;
    }

    public static final void updateValue(NVPair[] arrnVPair, String string, String string2) {
        int n = Util.getIndex(arrnVPair, string);
        if (n != -1) {
            arrnVPair[n] = new NVPair(string, string2);
        }
    }

    public static final NVPair[] addValue(NVPair[] arrnVPair, String string, String string2) {
        int n = arrnVPair.length;
        arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
        arrnVPair[n] = new NVPair(string, string2);
        return arrnVPair;
    }

    public static final NVPair[] removeAllValues(NVPair[] arrnVPair, String string) {
        int n = arrnVPair.length;
        for (int i = 0; i < arrnVPair.length; ++i) {
            int n2 = i;
            while (i < arrnVPair.length && arrnVPair[i].getName().equalsIgnoreCase(string)) {
                ++i;
            }
            if (i - n2 <= 0) continue;
            System.arraycopy(arrnVPair, i, arrnVPair, n2, (n -= i - n2) - n2);
        }
        if (n < arrnVPair.length) {
            arrnVPair = Util.resizeArray(arrnVPair, n);
        }
        return arrnVPair;
    }

    public static final NVPair[] addToken(NVPair[] arrnVPair, String string, String string2) throws ParseException {
        int n = Util.getIndex(arrnVPair, string);
        if (n == -1) {
            return Util.addValue(arrnVPair, string, string2);
        }
        if (!Util.hasToken(arrnVPair[n].getValue(), string2)) {
            arrnVPair[n] = new NVPair(string, arrnVPair[n].getValue() + ", " + string2);
        }
        return arrnVPair;
    }

    public static final NVPair[] removeToken(NVPair[] arrnVPair, String string, String string2) throws ParseException {
        int n = Util.getIndex(arrnVPair, string);
        if (n == -1) {
            return arrnVPair;
        }
        Vector vector = Util.parseHeader(arrnVPair[n].getValue());
        if (!vector.removeElement(new HttpHeaderElement(string2))) {
            return arrnVPair;
        }
        if (vector.isEmpty()) {
            System.arraycopy(arrnVPair, n + 1, arrnVPair, n, arrnVPair.length - n - 1);
            arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length - 1);
        } else {
            arrnVPair[n] = new NVPair(string, Util.assembleHeader(vector));
        }
        return arrnVPair;
    }

    public static final boolean sameHttpURL(URL uRL, URL uRL2) {
        if (!uRL.getProtocol().equalsIgnoreCase(uRL2.getProtocol())) {
            return false;
        }
        if (!uRL.getHost().equalsIgnoreCase(uRL2.getHost())) {
            return false;
        }
        int n = uRL.getPort();
        int n2 = uRL2.getPort();
        if (n == -1) {
            n = URI.defaultPort(uRL.getProtocol());
        }
        if (n2 == -1) {
            n2 = URI.defaultPort(uRL.getProtocol());
        }
        if (n != n2) {
            return false;
        }
        try {
            return URI.unescape(uRL.getFile()).equals(URI.unescape(uRL2.getFile()));
        }
        catch (ParseException parseException) {
            return uRL.getFile().equals(uRL2.getFile());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final String httpDate(Date date) {
        if (http_format == null) {
            Class class_ = class$HTTPClient$Util == null ? (class$HTTPClient$Util = Util.class$("HTTPClient.Util")) : class$HTTPClient$Util;
            synchronized (class_) {
                if (http_format == null) {
                    http_format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                    http_format.setTimeZone(new SimpleTimeZone(0, "GMT"));
                }
            }
        }
        return http_format.format(date);
    }

    static final String escapeUnsafeChars(String string) {
        int n = string.length();
        char[] arrc = new char[3 * n];
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c >= '\u0080' || UnsafeChar.get(c)) {
                arrc[n2++] = 37;
                arrc[n2++] = hex_map[(c & 0xF0) >>> 4];
                arrc[n2++] = hex_map[c & 0xF];
                continue;
            }
            arrc[n2++] = c;
        }
        if (n2 > n) {
            return new String(arrc, 0, n2);
        }
        return string;
    }

    public static final String getPath(String string) {
        int n = string.length();
        int n2 = string.indexOf(35);
        if (n2 != -1) {
            n = n2;
        }
        if ((n2 = string.indexOf(63)) != -1 && n2 < n) {
            n = n2;
        }
        return string.substring(0, n);
    }

    public static final String getQuery(String string) {
        int n = string.length();
        int n2 = string.indexOf(35);
        if (n2 != -1) {
            n = n2;
        }
        if ((n2 = string.indexOf(63)) != -1 && n2 < n) {
            return string.substring(n2 + 1, n);
        }
        return null;
    }

    public static final String getFragment(String string) {
        int n = string.indexOf(35);
        if (n != -1) {
            return string.substring(n + 1);
        }
        return null;
    }

    static final void logLine(String string) {
        System.err.print(string + " (" + Thread.currentThread() + ")" + nl);
        System.err.flush();
    }

    static final void logLine() {
        System.err.println();
        System.err.flush();
    }

    static final void logMessage(String string) {
        System.err.print(string);
        System.err.flush();
    }

    static final void logStackTrace(Throwable throwable) {
        throwable.printStackTrace(System.err);
        System.err.flush();
    }

    public static void getBytes(String string, byte[] arrby, int n) {
        byte[] arrby2 = string.getBytes();
        System.arraycopy(arrby2, 0, arrby, n, arrby2.length);
    }

    public static void getBytes(String string, int n, byte[] arrby, int n2) {
        byte[] arrby2 = string.getBytes();
        System.arraycopy(arrby2, 0, arrby, n2, n);
    }

    public static Date parseDate(String string) throws IllegalArgumentException {
        try {
            return DateFormat.getDateTimeInstance().parse(string);
        }
        catch (java.text.ParseException parseException) {
            throw new IllegalArgumentException(parseException.getMessage());
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        int n;
        Separators = new BitSet(128);
        Separators.set(40);
        Separators.set(41);
        Separators.set(60);
        Separators.set(62);
        Separators.set(64);
        Separators.set(44);
        Separators.set(59);
        Separators.set(58);
        Separators.set(92);
        Separators.set(34);
        Separators.set(47);
        Separators.set(91);
        Separators.set(93);
        Separators.set(63);
        Separators.set(61);
        Separators.set(123);
        Separators.set(125);
        Separators.set(32);
        Separators.set(9);
        TokenChar = new BitSet(128);
        for (n = 32; n < 127; ++n) {
            TokenChar.set(n);
        }
        TokenChar.xor(Separators);
        UnsafeChar = new BitSet(128);
        for (n = 0; n < 32; ++n) {
            UnsafeChar.set(n);
        }
        UnsafeChar.set(32);
        UnsafeChar.set(60);
        UnsafeChar.set(62);
        UnsafeChar.set(34);
        UnsafeChar.set(123);
        UnsafeChar.set(125);
        UnsafeChar.set(124);
        UnsafeChar.set(92);
        UnsafeChar.set(94);
        UnsafeChar.set(126);
        UnsafeChar.set(91);
        UnsafeChar.set(93);
        UnsafeChar.set(96);
        UnsafeChar.set(127);
        hex_map = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        nl = System.getProperty("line.separator");
    }
}

