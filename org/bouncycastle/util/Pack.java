/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util;

public abstract class Pack {
    public static int bigEndianToInt(byte[] arrby, int n) {
        int n2 = arrby[n] << 24;
        n2 |= (arrby[++n] & 0xFF) << 16;
        n2 |= (arrby[++n] & 0xFF) << 8;
        return n2 |= arrby[++n] & 0xFF;
    }

    public static void bigEndianToInt(byte[] arrby, int n, int[] arrn) {
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = Pack.bigEndianToInt(arrby, n);
            n += 4;
        }
    }

    public static byte[] intToBigEndian(int n) {
        byte[] arrby = new byte[4];
        Pack.intToBigEndian(n, arrby, 0);
        return arrby;
    }

    public static void intToBigEndian(int n, byte[] arrby, int n2) {
        arrby[n2] = (byte)(n >>> 24);
        arrby[++n2] = (byte)(n >>> 16);
        arrby[++n2] = (byte)(n >>> 8);
        arrby[++n2] = (byte)n;
    }

    public static byte[] intToBigEndian(int[] arrn) {
        byte[] arrby = new byte[4 * arrn.length];
        Pack.intToBigEndian(arrn, arrby, 0);
        return arrby;
    }

    public static void intToBigEndian(int[] arrn, byte[] arrby, int n) {
        for (int i = 0; i < arrn.length; ++i) {
            Pack.intToBigEndian(arrn[i], arrby, n);
            n += 4;
        }
    }

    public static long bigEndianToLong(byte[] arrby, int n) {
        int n2 = Pack.bigEndianToInt(arrby, n);
        int n3 = Pack.bigEndianToInt(arrby, n + 4);
        return ((long)n2 & 0xFFFFFFFFL) << 32 | (long)n3 & 0xFFFFFFFFL;
    }

    public static void bigEndianToLong(byte[] arrby, int n, long[] arrl) {
        for (int i = 0; i < arrl.length; ++i) {
            arrl[i] = Pack.bigEndianToLong(arrby, n);
            n += 8;
        }
    }

    public static byte[] longToBigEndian(long l) {
        byte[] arrby = new byte[8];
        Pack.longToBigEndian(l, arrby, 0);
        return arrby;
    }

    public static void longToBigEndian(long l, byte[] arrby, int n) {
        Pack.intToBigEndian((int)(l >>> 32), arrby, n);
        Pack.intToBigEndian((int)(l & 0xFFFFFFFFL), arrby, n + 4);
    }

    public static byte[] longToBigEndian(long[] arrl) {
        byte[] arrby = new byte[8 * arrl.length];
        Pack.longToBigEndian(arrl, arrby, 0);
        return arrby;
    }

    public static void longToBigEndian(long[] arrl, byte[] arrby, int n) {
        for (int i = 0; i < arrl.length; ++i) {
            Pack.longToBigEndian(arrl[i], arrby, n);
            n += 8;
        }
    }

    public static int littleEndianToInt(byte[] arrby, int n) {
        int n2 = arrby[n] & 0xFF;
        n2 |= (arrby[++n] & 0xFF) << 8;
        n2 |= (arrby[++n] & 0xFF) << 16;
        return n2 |= arrby[++n] << 24;
    }

    public static void littleEndianToInt(byte[] arrby, int n, int[] arrn) {
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = Pack.littleEndianToInt(arrby, n);
            n += 4;
        }
    }

    public static void littleEndianToInt(byte[] arrby, int n, int[] arrn, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            arrn[n2 + i] = Pack.littleEndianToInt(arrby, n);
            n += 4;
        }
    }

    public static byte[] intToLittleEndian(int n) {
        byte[] arrby = new byte[4];
        Pack.intToLittleEndian(n, arrby, 0);
        return arrby;
    }

    public static void intToLittleEndian(int n, byte[] arrby, int n2) {
        arrby[n2] = (byte)n;
        arrby[++n2] = (byte)(n >>> 8);
        arrby[++n2] = (byte)(n >>> 16);
        arrby[++n2] = (byte)(n >>> 24);
    }

    public static byte[] intToLittleEndian(int[] arrn) {
        byte[] arrby = new byte[4 * arrn.length];
        Pack.intToLittleEndian(arrn, arrby, 0);
        return arrby;
    }

    public static void intToLittleEndian(int[] arrn, byte[] arrby, int n) {
        for (int i = 0; i < arrn.length; ++i) {
            Pack.intToLittleEndian(arrn[i], arrby, n);
            n += 4;
        }
    }

    public static long littleEndianToLong(byte[] arrby, int n) {
        int n2 = Pack.littleEndianToInt(arrby, n);
        int n3 = Pack.littleEndianToInt(arrby, n + 4);
        return ((long)n3 & 0xFFFFFFFFL) << 32 | (long)n2 & 0xFFFFFFFFL;
    }

    public static void littleEndianToLong(byte[] arrby, int n, long[] arrl) {
        for (int i = 0; i < arrl.length; ++i) {
            arrl[i] = Pack.littleEndianToLong(arrby, n);
            n += 8;
        }
    }

    public static byte[] longToLittleEndian(long l) {
        byte[] arrby = new byte[8];
        Pack.longToLittleEndian(l, arrby, 0);
        return arrby;
    }

    public static void longToLittleEndian(long l, byte[] arrby, int n) {
        Pack.intToLittleEndian((int)(l & 0xFFFFFFFFL), arrby, n);
        Pack.intToLittleEndian((int)(l >>> 32), arrby, n + 4);
    }

    public static byte[] longToLittleEndian(long[] arrl) {
        byte[] arrby = new byte[8 * arrl.length];
        Pack.longToLittleEndian(arrl, arrby, 0);
        return arrby;
    }

    public static void longToLittleEndian(long[] arrl, byte[] arrby, int n) {
        for (int i = 0; i < arrl.length; ++i) {
            Pack.longToLittleEndian(arrl[i], arrby, n);
            n += 8;
        }
    }
}

