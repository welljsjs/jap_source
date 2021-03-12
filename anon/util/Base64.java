/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import logging.LogHolder;
import logging.LogType;

public final class Base64 {
    public static final String BEGIN_TAG = "-----BEGIN ";
    public static final String END_TAG = "-----END ";
    public static final String TAG_END_SEQUENCE = "-----";
    public static final boolean ENCODE = true;
    public static final boolean DECODE = false;
    public static final boolean COMPRESS = true;
    public static final boolean DONT_COMPRESS = false;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final byte[] ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9};
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;

    private Base64() {
    }

    private static byte[] encode3to4(byte[] arrby, int n, int n2, byte[] arrby2, int n3) {
        int n4 = (n2 > 0 ? arrby[n] << 24 >>> 8 : 0) | (n2 > 1 ? arrby[n + 1] << 24 >>> 16 : 0) | (n2 > 2 ? arrby[n + 2] << 24 >>> 24 : 0);
        switch (n2) {
            case 3: {
                arrby2[n3] = ALPHABET[n4 >>> 18];
                arrby2[n3 + 1] = ALPHABET[n4 >>> 12 & 0x3F];
                arrby2[n3 + 2] = ALPHABET[n4 >>> 6 & 0x3F];
                arrby2[n3 + 3] = ALPHABET[n4 & 0x3F];
                return arrby2;
            }
            case 2: {
                arrby2[n3] = ALPHABET[n4 >>> 18];
                arrby2[n3 + 1] = ALPHABET[n4 >>> 12 & 0x3F];
                arrby2[n3 + 2] = ALPHABET[n4 >>> 6 & 0x3F];
                arrby2[n3 + 3] = 61;
                return arrby2;
            }
            case 1: {
                arrby2[n3] = ALPHABET[n4 >>> 18];
                arrby2[n3 + 1] = ALPHABET[n4 >>> 12 & 0x3F];
                arrby2[n3 + 2] = 61;
                arrby2[n3 + 3] = 61;
                return arrby2;
            }
        }
        return arrby2;
    }

    public static String createBeginTag(String string) {
        return BEGIN_TAG + string + TAG_END_SEQUENCE + "\n";
    }

    public static String createEndTag(String string) {
        return "\n-----END " + string + TAG_END_SEQUENCE + "\n";
    }

    public static String encodeBytes(byte[] arrby) {
        return Base64.encode(arrby, true);
    }

    public static String encode(byte[] arrby, boolean bl) {
        if (arrby == null) {
            return null;
        }
        return Base64.encode(arrby, 0, arrby.length, bl);
    }

    public static String encode(byte[] arrby, int n, int n2) {
        return Base64.encode(arrby, n, n2, true);
    }

    public static String encode(byte[] arrby, int n, int n2, boolean bl) {
        int n3 = n2 * 4 / 3;
        byte[] arrby2 = new byte[n3 + (n2 % 3 > 0 ? 4 : 0) + (bl ? n3 / 76 : 0)];
        int n4 = 0;
        int n5 = 0;
        int n6 = n2 - 2;
        int n7 = 0;
        while (n4 < n6) {
            Base64.encode3to4(arrby, n4 + n, 3, arrby2, n5);
            if (bl && (n7 += 4) == 76) {
                arrby2[n5 + 4] = 10;
                ++n5;
                n7 = 0;
            }
            n4 += 3;
            n5 += 4;
        }
        if (n4 < n2) {
            Base64.encode3to4(arrby, n4 + n, n2 - n4, arrby2, n5);
            n5 += 4;
        }
        return new String(arrby2, 0, n5);
    }

    public static String encodeString(String string) {
        return Base64.encodeString(string, true);
    }

    public static String encodeString(String string, boolean bl) {
        if (string == null) {
            return null;
        }
        return Base64.encode(string.getBytes(), bl);
    }

    private static int decode4to3(byte[] arrby, int n, byte[] arrby2, int n2) {
        if (arrby[n + 2] == 61) {
            int n3 = (DECODABET[arrby[n]] & 0xFF) << 18 | (DECODABET[arrby[n + 1]] & 0xFF) << 12;
            arrby2[n2] = (byte)(n3 >>> 16);
            return 1;
        }
        if (arrby[n + 3] == 61) {
            int n4 = (DECODABET[arrby[n]] & 0xFF) << 18 | (DECODABET[arrby[n + 1]] & 0xFF) << 12 | (DECODABET[arrby[n + 2]] & 0xFF) << 6;
            arrby2[n2] = (byte)(n4 >>> 16);
            arrby2[n2 + 1] = (byte)(n4 >>> 8);
            return 2;
        }
        try {
            int n5 = (DECODABET[arrby[n]] & 0xFF) << 18 | (DECODABET[arrby[n + 1]] & 0xFF) << 12 | (DECODABET[arrby[n + 2]] & 0xFF) << 6 | DECODABET[arrby[n + 3]] & 0xFF;
            arrby2[n2] = (byte)(n5 >> 16);
            arrby2[n2 + 1] = (byte)(n5 >> 8);
            arrby2[n2 + 2] = (byte)n5;
            return 3;
        }
        catch (Exception exception) {
            return -1;
        }
    }

    public static byte[] decode(String string) {
        if (string == null) {
            return null;
        }
        byte[] arrby = string.getBytes();
        return Base64.decode(arrby, 0, arrby.length);
    }

    public static String decodeToString(String string) {
        return new String(Base64.decode(string));
    }

    public static byte[] decode(byte[] arrby, int n, int n2) {
        int n3 = n2 * 3 / 4;
        byte[] arrby2 = new byte[n3];
        int n4 = 0;
        byte[] arrby3 = new byte[4];
        int n5 = 0;
        int n6 = 0;
        byte by = 0;
        byte by2 = 0;
        for (n6 = n; n6 < n + n2; ++n6) {
            by = (byte)(arrby[n6] & 0x7F);
            by2 = DECODABET[by];
            if (by2 >= -5) {
                if (by2 < -1) continue;
                arrby3[n5++] = by;
                if (n5 <= 3) continue;
                n4 += Base64.decode4to3(arrby3, 0, arrby2, n4);
                n5 = 0;
                if (by != 61) continue;
                break;
            }
            LogHolder.log(7, LogType.MISC, "Bad Base64 input character at " + n6 + ": " + arrby[n6] + "(decimal)");
            return null;
        }
        byte[] arrby4 = new byte[n4];
        System.arraycopy(arrby2, 0, arrby4, 0, n4);
        return arrby4;
    }
}

