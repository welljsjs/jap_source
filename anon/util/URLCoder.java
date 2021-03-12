/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import java.io.UnsupportedEncodingException;

public class URLCoder {
    static final String digits = "0123456789ABCDEF";

    public static String decode(String string) {
        if (string == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        byte[] arrby = new byte[string.length()];
        int n = 0;
        int n2 = 0;
        try {
            while (n2 < string.length()) {
                char c = string.charAt(n2);
                if (c == '+') {
                    stringBuffer.append(' ');
                } else if (c == '%') {
                    arrby[n] = (byte)Integer.parseInt(string.substring(n2 + 1, n2 + 3), 16);
                    ++n;
                    n2 += 2;
                } else {
                    stringBuffer.append(c);
                }
                if ((++n2 >= string.length() || string.charAt(n2) == '%') && n2 < string.length()) continue;
                stringBuffer.append(new String(arrby, 0, n, "UTF8"));
                n = 0;
            }
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            return null;
        }
        return stringBuffer.toString();
    }

    public static String encode(String string) throws UnsupportedEncodingException {
        if (string == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(string.length() + 16);
        int n = -1;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || " .-*_".indexOf(c) > -1) {
                if (n >= 0) {
                    URLCoder.convert(string.substring(n, i), stringBuffer);
                    n = -1;
                }
                if (c != ' ') {
                    stringBuffer.append(c);
                    continue;
                }
                stringBuffer.append('+');
                continue;
            }
            if (n >= 0) continue;
            n = i;
        }
        if (n >= 0) {
            URLCoder.convert(string.substring(n, string.length()), stringBuffer);
        }
        return stringBuffer.toString();
    }

    private static void convert(String string, StringBuffer stringBuffer) throws UnsupportedEncodingException {
        byte[] arrby = string.getBytes("UTF8");
        for (int i = 0; i < arrby.length; ++i) {
            stringBuffer.append('%');
            stringBuffer.append(digits.charAt((arrby[i] & 0xF0) >> 4));
            stringBuffer.append(digits.charAt(arrby[i] & 0xF));
        }
    }
}

