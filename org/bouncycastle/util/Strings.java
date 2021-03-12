/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.bouncycastle.util.StringList;

public final class Strings {
    private static String LINE_SEPARATOR;

    public static String fromUTF8ByteArray(byte[] arrby) {
        int n = 0;
        int n2 = 0;
        while (n < arrby.length) {
            ++n2;
            if ((arrby[n] & 0xF0) == 240) {
                ++n2;
                n += 4;
                continue;
            }
            if ((arrby[n] & 0xE0) == 224) {
                n += 3;
                continue;
            }
            if ((arrby[n] & 0xC0) == 192) {
                n += 2;
                continue;
            }
            ++n;
        }
        char[] arrc = new char[n2];
        n = 0;
        n2 = 0;
        while (n < arrby.length) {
            char c;
            if ((arrby[n] & 0xF0) == 240) {
                int n3 = (arrby[n] & 3) << 18 | (arrby[n + 1] & 0x3F) << 12 | (arrby[n + 2] & 0x3F) << 6 | arrby[n + 3] & 0x3F;
                int n4 = n3 - 65536;
                char c2 = (char)(0xD800 | n4 >> 10);
                char c3 = (char)(0xDC00 | n4 & 0x3FF);
                arrc[n2++] = c2;
                c = c3;
                n += 4;
            } else if ((arrby[n] & 0xE0) == 224) {
                c = (char)((arrby[n] & 0xF) << 12 | (arrby[n + 1] & 0x3F) << 6 | arrby[n + 2] & 0x3F);
                n += 3;
            } else if ((arrby[n] & 0xD0) == 208) {
                c = (char)((arrby[n] & 0x1F) << 6 | arrby[n + 1] & 0x3F);
                n += 2;
            } else if ((arrby[n] & 0xC0) == 192) {
                c = (char)((arrby[n] & 0x1F) << 6 | arrby[n + 1] & 0x3F);
                n += 2;
            } else {
                c = (char)(arrby[n] & 0xFF);
                ++n;
            }
            arrc[n2++] = c;
        }
        return new String(arrc);
    }

    public static byte[] toUTF8ByteArray(String string) {
        return Strings.toUTF8ByteArray(string.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] arrc) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Strings.toUTF8ByteArray(arrc, byteArrayOutputStream);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void toUTF8ByteArray(char[] arrc, OutputStream outputStream) throws IOException {
        char[] arrc2 = arrc;
        for (int i = 0; i < arrc2.length; ++i) {
            char c = arrc2[i];
            if (c < '\u0080') {
                outputStream.write(c);
                continue;
            }
            if (c < '\u0800') {
                outputStream.write(0xC0 | c >> 6);
                outputStream.write(0x80 | c & 0x3F);
                continue;
            }
            if (c >= '\ud800' && c <= '\udfff') {
                if (i + 1 >= arrc2.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                char c2 = c;
                char c3 = c = arrc2[++i];
                if (c2 > '\udbff') {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                int n = ((c2 & 0x3FF) << 10 | c3 & 0x3FF) + 65536;
                outputStream.write(0xF0 | n >> 18);
                outputStream.write(0x80 | n >> 12 & 0x3F);
                outputStream.write(0x80 | n >> 6 & 0x3F);
                outputStream.write(0x80 | n & 0x3F);
                continue;
            }
            outputStream.write(0xE0 | c >> 12);
            outputStream.write(0x80 | c >> 6 & 0x3F);
            outputStream.write(0x80 | c & 0x3F);
        }
    }

    public static String toUpperCase(String string) {
        boolean bl = false;
        char[] arrc = string.toCharArray();
        for (int i = 0; i != arrc.length; ++i) {
            char c = arrc[i];
            if ('a' > c || 'z' < c) continue;
            bl = true;
            arrc[i] = (char)(c - 97 + 65);
        }
        if (bl) {
            return new String(arrc);
        }
        return string;
    }

    public static String toLowerCase(String string) {
        boolean bl = false;
        char[] arrc = string.toCharArray();
        for (int i = 0; i != arrc.length; ++i) {
            char c = arrc[i];
            if ('A' > c || 'Z' < c) continue;
            bl = true;
            arrc[i] = (char)(c - 65 + 97);
        }
        if (bl) {
            return new String(arrc);
        }
        return string;
    }

    public static byte[] toByteArray(char[] arrc) {
        byte[] arrby = new byte[arrc.length];
        for (int i = 0; i != arrby.length; ++i) {
            arrby[i] = (byte)arrc[i];
        }
        return arrby;
    }

    public static byte[] toByteArray(String string) {
        byte[] arrby = new byte[string.length()];
        for (int i = 0; i != arrby.length; ++i) {
            char c = string.charAt(i);
            arrby[i] = (byte)c;
        }
        return arrby;
    }

    public static int toByteArray(String string, byte[] arrby, int n) {
        int n2 = string.length();
        for (int i = 0; i < n2; ++i) {
            char c = string.charAt(i);
            arrby[n + i] = (byte)c;
        }
        return n2;
    }

    public static String fromByteArray(byte[] arrby) {
        return new String(Strings.asCharArray(arrby));
    }

    public static char[] asCharArray(byte[] arrby) {
        char[] arrc = new char[arrby.length];
        for (int i = 0; i != arrc.length; ++i) {
            arrc[i] = (char)(arrby[i] & 0xFF);
        }
        return arrc;
    }

    public static String[] split(String string, char c) {
        Vector<String> vector = new Vector<String>();
        boolean bl = true;
        while (bl) {
            int n = string.indexOf(c);
            if (n > 0) {
                String string2 = string.substring(0, n);
                vector.addElement(string2);
                string = string.substring(n + 1);
                continue;
            }
            bl = false;
            vector.addElement(string);
        }
        String[] arrstring = new String[vector.size()];
        for (int i = 0; i != arrstring.length; ++i) {
            arrstring[i] = (String)vector.elementAt(i);
        }
        return arrstring;
    }

    public static String lineSeparator() {
        return LINE_SEPARATOR;
    }

    public static StringList newList() {
        return new StringListImpl();
    }

    static {
        try {
            LINE_SEPARATOR = System.getProperty("line.separator");
        }
        catch (Exception exception) {
            LINE_SEPARATOR = "\n";
        }
    }

    private static class StringListImpl
    implements StringList {
        private List list = new ArrayList();

        private StringListImpl() {
        }

        public boolean add(String string) {
            return this.list.add(string);
        }

        public String get(int n) {
            return (String)this.list.get(n);
        }

        public String set(int n, String string) {
            return this.list.set(n, string);
        }

        public void add(int n, String string) {
            this.list.add(n, string);
        }

        public int size() {
            return this.list.size();
        }

        public Iterator iterator() {
            return this.list.iterator();
        }

        public String[] toStringArray() {
            String[] arrstring = new String[this.size()];
            for (int i = 0; i != arrstring.length; ++i) {
                arrstring[i] = this.get(i);
            }
            return arrstring;
        }

        public String[] toStringArray(int n, int n2) {
            String[] arrstring = new String[n2 - n];
            for (int i = n; i != this.size() && i != n2; ++i) {
                arrstring[i - n] = this.get(i);
            }
            return arrstring;
        }
    }
}

