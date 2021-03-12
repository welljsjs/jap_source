/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

public abstract class GCMUtil {
    private static final int E1 = -520093696;
    private static final long E1L = -2233785415175766016L;
    private static final int[] LOOKUP = GCMUtil.generateLookup();

    private static int[] generateLookup() {
        int[] arrn = new int[256];
        for (int i = 0; i < 256; ++i) {
            int n = 0;
            for (int j = 7; j >= 0; --j) {
                if ((i & 1 << j) == 0) continue;
                n ^= -520093696 >>> 7 - j;
            }
            arrn[i] = n;
        }
        return arrn;
    }

    public static byte[] oneAsBytes() {
        byte[] arrby = new byte[16];
        arrby[0] = -128;
        return arrby;
    }

    public static int[] oneAsInts() {
        int[] arrn = new int[4];
        arrn[0] = Integer.MIN_VALUE;
        return arrn;
    }

    public static long[] oneAsLongs() {
        long[] arrl = new long[2];
        arrl[0] = Long.MIN_VALUE;
        return arrl;
    }

    public static byte[] asBytes(int[] arrn) {
        byte[] arrby = new byte[16];
        Pack.intToBigEndian(arrn, arrby, 0);
        return arrby;
    }

    public static void asBytes(int[] arrn, byte[] arrby) {
        Pack.intToBigEndian(arrn, arrby, 0);
    }

    public static byte[] asBytes(long[] arrl) {
        byte[] arrby = new byte[16];
        Pack.longToBigEndian(arrl, arrby, 0);
        return arrby;
    }

    public static void asBytes(long[] arrl, byte[] arrby) {
        Pack.longToBigEndian(arrl, arrby, 0);
    }

    public static int[] asInts(byte[] arrby) {
        int[] arrn = new int[4];
        Pack.bigEndianToInt(arrby, 0, arrn);
        return arrn;
    }

    public static void asInts(byte[] arrby, int[] arrn) {
        Pack.bigEndianToInt(arrby, 0, arrn);
    }

    public static long[] asLongs(byte[] arrby) {
        long[] arrl = new long[2];
        Pack.bigEndianToLong(arrby, 0, arrl);
        return arrl;
    }

    public static void asLongs(byte[] arrby, long[] arrl) {
        Pack.bigEndianToLong(arrby, 0, arrl);
    }

    public static void multiply(byte[] arrby, byte[] arrby2) {
        int[] arrn = GCMUtil.asInts(arrby);
        int[] arrn2 = GCMUtil.asInts(arrby2);
        GCMUtil.multiply(arrn, arrn2);
        GCMUtil.asBytes(arrn, arrby);
    }

    public static void multiply(int[] arrn, int[] arrn2) {
        int n = arrn[0];
        int n2 = arrn[1];
        int n3 = arrn[2];
        int n4 = arrn[3];
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        for (int i = 0; i < 4; ++i) {
            int n9 = arrn2[i];
            for (int j = 0; j < 32; ++j) {
                int n10 = n9 >> 31;
                n9 <<= 1;
                n5 ^= n & n10;
                n6 ^= n2 & n10;
                n7 ^= n3 & n10;
                n8 ^= n4 & n10;
                int n11 = n4 << 31 >> 8;
                n4 = n4 >>> 1 | n3 << 31;
                n3 = n3 >>> 1 | n2 << 31;
                n2 = n2 >>> 1 | n << 31;
                n = n >>> 1 ^ n11 & 0xE1000000;
            }
        }
        arrn[0] = n5;
        arrn[1] = n6;
        arrn[2] = n7;
        arrn[3] = n8;
    }

    public static void multiply(long[] arrl, long[] arrl2) {
        long l = arrl[0];
        long l2 = arrl[1];
        long l3 = 0L;
        long l4 = 0L;
        for (int i = 0; i < 2; ++i) {
            long l5 = arrl2[i];
            for (int j = 0; j < 64; ++j) {
                long l6 = l5 >> 63;
                l5 <<= 1;
                l3 ^= l & l6;
                l4 ^= l2 & l6;
                long l7 = l2 << 63 >> 8;
                l2 = l2 >>> 1 | l << 63;
                l = l >>> 1 ^ l7 & 0xE100000000000000L;
            }
        }
        arrl[0] = l3;
        arrl[1] = l4;
    }

    public static void multiplyP(int[] arrn) {
        int n = GCMUtil.shiftRight(arrn) >> 8;
        arrn[0] = arrn[0] ^ n & 0xE1000000;
    }

    public static void multiplyP(int[] arrn, int[] arrn2) {
        int n = GCMUtil.shiftRight(arrn, arrn2) >> 8;
        arrn2[0] = arrn2[0] ^ n & 0xE1000000;
    }

    public static void multiplyP8(int[] arrn) {
        int n = GCMUtil.shiftRightN(arrn, 8);
        arrn[0] = arrn[0] ^ LOOKUP[n >>> 24];
    }

    public static void multiplyP8(int[] arrn, int[] arrn2) {
        int n = GCMUtil.shiftRightN(arrn, 8, arrn2);
        arrn2[0] = arrn2[0] ^ LOOKUP[n >>> 24];
    }

    static int shiftRight(int[] arrn) {
        int n = arrn[0];
        arrn[0] = n >>> 1;
        int n2 = n << 31;
        n = arrn[1];
        arrn[1] = n >>> 1 | n2;
        n2 = n << 31;
        n = arrn[2];
        arrn[2] = n >>> 1 | n2;
        n2 = n << 31;
        n = arrn[3];
        arrn[3] = n >>> 1 | n2;
        return n << 31;
    }

    static int shiftRight(int[] arrn, int[] arrn2) {
        int n = arrn[0];
        arrn2[0] = n >>> 1;
        int n2 = n << 31;
        n = arrn[1];
        arrn2[1] = n >>> 1 | n2;
        n2 = n << 31;
        n = arrn[2];
        arrn2[2] = n >>> 1 | n2;
        n2 = n << 31;
        n = arrn[3];
        arrn2[3] = n >>> 1 | n2;
        return n << 31;
    }

    static long shiftRight(long[] arrl) {
        long l = arrl[0];
        arrl[0] = l >>> 1;
        long l2 = l << 63;
        l = arrl[1];
        arrl[1] = l >>> 1 | l2;
        return l << 63;
    }

    static long shiftRight(long[] arrl, long[] arrl2) {
        long l = arrl[0];
        arrl2[0] = l >>> 1;
        long l2 = l << 63;
        l = arrl[1];
        arrl2[1] = l >>> 1 | l2;
        return l << 63;
    }

    static int shiftRightN(int[] arrn, int n) {
        int n2 = arrn[0];
        int n3 = 32 - n;
        arrn[0] = n2 >>> n;
        int n4 = n2 << n3;
        n2 = arrn[1];
        arrn[1] = n2 >>> n | n4;
        n4 = n2 << n3;
        n2 = arrn[2];
        arrn[2] = n2 >>> n | n4;
        n4 = n2 << n3;
        n2 = arrn[3];
        arrn[3] = n2 >>> n | n4;
        return n2 << n3;
    }

    static int shiftRightN(int[] arrn, int n, int[] arrn2) {
        int n2 = arrn[0];
        int n3 = 32 - n;
        arrn2[0] = n2 >>> n;
        int n4 = n2 << n3;
        n2 = arrn[1];
        arrn2[1] = n2 >>> n | n4;
        n4 = n2 << n3;
        n2 = arrn[2];
        arrn2[2] = n2 >>> n | n4;
        n4 = n2 << n3;
        n2 = arrn[3];
        arrn2[3] = n2 >>> n | n4;
        return n2 << n3;
    }

    public static void xor(byte[] arrby, byte[] arrby2) {
        int n = 0;
        do {
            int n2 = n;
            arrby[n2] = (byte)(arrby[n2] ^ arrby2[n]);
            int n3 = ++n;
            arrby[n3] = (byte)(arrby[n3] ^ arrby2[n]);
            int n4 = ++n;
            arrby[n4] = (byte)(arrby[n4] ^ arrby2[n]);
            int n5 = ++n;
            arrby[n5] = (byte)(arrby[n5] ^ arrby2[n]);
        } while (++n < 16);
    }

    public static void xor(byte[] arrby, byte[] arrby2, int n, int n2) {
        while (--n2 >= 0) {
            int n3 = n2;
            arrby[n3] = (byte)(arrby[n3] ^ arrby2[n + n2]);
        }
    }

    public static void xor(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        int n = 0;
        do {
            arrby3[n] = (byte)(arrby[n] ^ arrby2[n]);
            arrby3[++n] = (byte)(arrby[n] ^ arrby2[n]);
            arrby3[++n] = (byte)(arrby[n] ^ arrby2[n]);
            arrby3[++n] = (byte)(arrby[n] ^ arrby2[n]);
        } while (++n < 16);
    }

    public static void xor(int[] arrn, int[] arrn2) {
        arrn[0] = arrn[0] ^ arrn2[0];
        arrn[1] = arrn[1] ^ arrn2[1];
        arrn[2] = arrn[2] ^ arrn2[2];
        arrn[3] = arrn[3] ^ arrn2[3];
    }

    public static void xor(int[] arrn, int[] arrn2, int[] arrn3) {
        arrn3[0] = arrn[0] ^ arrn2[0];
        arrn3[1] = arrn[1] ^ arrn2[1];
        arrn3[2] = arrn[2] ^ arrn2[2];
        arrn3[3] = arrn[3] ^ arrn2[3];
    }

    public static void xor(long[] arrl, long[] arrl2) {
        arrl[0] = arrl[0] ^ arrl2[0];
        arrl[1] = arrl[1] ^ arrl2[1];
    }

    public static void xor(long[] arrl, long[] arrl2, long[] arrl3) {
        arrl3[0] = arrl[0] ^ arrl2[0];
        arrl3[1] = arrl[1] ^ arrl2[1];
    }
}

