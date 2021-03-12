/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util;

import java.math.BigInteger;

public final class Arrays {
    private Arrays() {
    }

    public static boolean areEqual(boolean[] arrbl, boolean[] arrbl2) {
        if (arrbl == arrbl2) {
            return true;
        }
        if (arrbl == null || arrbl2 == null) {
            return false;
        }
        if (arrbl.length != arrbl2.length) {
            return false;
        }
        for (int i = 0; i != arrbl.length; ++i) {
            if (arrbl[i] == arrbl2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean areEqual(char[] arrc, char[] arrc2) {
        if (arrc == arrc2) {
            return true;
        }
        if (arrc == null || arrc2 == null) {
            return false;
        }
        if (arrc.length != arrc2.length) {
            return false;
        }
        for (int i = 0; i != arrc.length; ++i) {
            if (arrc[i] == arrc2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean areEqual(byte[] arrby, byte[] arrby2) {
        if (arrby == arrby2) {
            return true;
        }
        if (arrby == null || arrby2 == null) {
            return false;
        }
        if (arrby.length != arrby2.length) {
            return false;
        }
        for (int i = 0; i != arrby.length; ++i) {
            if (arrby[i] == arrby2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean constantTimeAreEqual(byte[] arrby, byte[] arrby2) {
        if (arrby == arrby2) {
            return true;
        }
        if (arrby == null || arrby2 == null) {
            return false;
        }
        if (arrby.length != arrby2.length) {
            return false;
        }
        int n = 0;
        for (int i = 0; i != arrby.length; ++i) {
            n |= arrby[i] ^ arrby2[i];
        }
        return n == 0;
    }

    public static boolean areEqual(int[] arrn, int[] arrn2) {
        if (arrn == arrn2) {
            return true;
        }
        if (arrn == null || arrn2 == null) {
            return false;
        }
        if (arrn.length != arrn2.length) {
            return false;
        }
        for (int i = 0; i != arrn.length; ++i) {
            if (arrn[i] == arrn2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean areEqual(long[] arrl, long[] arrl2) {
        if (arrl == arrl2) {
            return true;
        }
        if (arrl == null || arrl2 == null) {
            return false;
        }
        if (arrl.length != arrl2.length) {
            return false;
        }
        for (int i = 0; i != arrl.length; ++i) {
            if (arrl[i] == arrl2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean areEqual(Object[] arrobject, Object[] arrobject2) {
        if (arrobject == arrobject2) {
            return true;
        }
        if (arrobject == null || arrobject2 == null) {
            return false;
        }
        if (arrobject.length != arrobject2.length) {
            return false;
        }
        for (int i = 0; i != arrobject.length; ++i) {
            Object object = arrobject[i];
            Object object2 = arrobject2[i];
            if (!(object == null ? object2 != null : !object.equals(object2))) continue;
            return false;
        }
        return true;
    }

    public static boolean contains(short[] arrs, short s) {
        for (int i = 0; i < arrs.length; ++i) {
            if (arrs[i] != s) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(int[] arrn, int n) {
        for (int i = 0; i < arrn.length; ++i) {
            if (arrn[i] != n) continue;
            return true;
        }
        return false;
    }

    public static void fill(byte[] arrby, byte by) {
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = by;
        }
    }

    public static void fill(char[] arrc, char c) {
        for (int i = 0; i < arrc.length; ++i) {
            arrc[i] = c;
        }
    }

    public static void fill(long[] arrl, long l) {
        for (int i = 0; i < arrl.length; ++i) {
            arrl[i] = l;
        }
    }

    public static void fill(short[] arrs, short s) {
        for (int i = 0; i < arrs.length; ++i) {
            arrs[i] = s;
        }
    }

    public static void fill(int[] arrn, int n) {
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = n;
        }
    }

    public static int hashCode(byte[] arrby) {
        if (arrby == null) {
            return 0;
        }
        int n = arrby.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 *= 257;
            n2 ^= arrby[n];
        }
        return n2;
    }

    public static int hashCode(byte[] arrby, int n, int n2) {
        if (arrby == null) {
            return 0;
        }
        int n3 = n2;
        int n4 = n3 + 1;
        while (--n3 >= 0) {
            n4 *= 257;
            n4 ^= arrby[n + n3];
        }
        return n4;
    }

    public static int hashCode(char[] arrc) {
        if (arrc == null) {
            return 0;
        }
        int n = arrc.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 *= 257;
            n2 ^= arrc[n];
        }
        return n2;
    }

    public static int hashCode(int[][] arrn) {
        int n = 0;
        for (int i = 0; i != arrn.length; ++i) {
            n = n * 257 + Arrays.hashCode(arrn[i]);
        }
        return n;
    }

    public static int hashCode(int[] arrn) {
        if (arrn == null) {
            return 0;
        }
        int n = arrn.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 *= 257;
            n2 ^= arrn[n];
        }
        return n2;
    }

    public static int hashCode(int[] arrn, int n, int n2) {
        if (arrn == null) {
            return 0;
        }
        int n3 = n2;
        int n4 = n3 + 1;
        while (--n3 >= 0) {
            n4 *= 257;
            n4 ^= arrn[n + n3];
        }
        return n4;
    }

    public static int hashCode(short[][][] arrs) {
        int n = 0;
        for (int i = 0; i != arrs.length; ++i) {
            n = n * 257 + Arrays.hashCode(arrs[i]);
        }
        return n;
    }

    public static int hashCode(short[][] arrs) {
        int n = 0;
        for (int i = 0; i != arrs.length; ++i) {
            n = n * 257 + Arrays.hashCode(arrs[i]);
        }
        return n;
    }

    public static int hashCode(short[] arrs) {
        if (arrs == null) {
            return 0;
        }
        int n = arrs.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 *= 257;
            n2 ^= arrs[n] & 0xFF;
        }
        return n2;
    }

    public static int hashCode(Object[] arrobject) {
        if (arrobject == null) {
            return 0;
        }
        int n = arrobject.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 *= 257;
            n2 ^= arrobject[n].hashCode();
        }
        return n2;
    }

    public static int hashCode(long[] arrl, int n, int n2) {
        if (arrl == null) {
            return 0;
        }
        int n3 = n2;
        int n4 = n3 + 1;
        while (--n3 >= 0) {
            long l = arrl[n + n3];
            n4 *= 257;
            n4 ^= (int)l;
            n4 *= 257;
            n4 ^= (int)(l >>> 32);
        }
        return n4;
    }

    public static byte[] clone(byte[] arrby) {
        if (arrby == null) {
            return null;
        }
        byte[] arrby2 = new byte[arrby.length];
        System.arraycopy(arrby, 0, arrby2, 0, arrby.length);
        return arrby2;
    }

    public static char[] clone(char[] arrc) {
        if (arrc == null) {
            return null;
        }
        char[] arrc2 = new char[arrc.length];
        System.arraycopy(arrc, 0, arrc2, 0, arrc.length);
        return arrc2;
    }

    public static byte[] clone(byte[] arrby, byte[] arrby2) {
        if (arrby == null) {
            return null;
        }
        if (arrby2 == null || arrby2.length != arrby.length) {
            return Arrays.clone(arrby);
        }
        System.arraycopy(arrby, 0, arrby2, 0, arrby2.length);
        return arrby2;
    }

    public static byte[][] clone(byte[][] arrby) {
        if (arrby == null) {
            return null;
        }
        byte[][] arrarrby = new byte[arrby.length][];
        for (int i = 0; i != arrarrby.length; ++i) {
            arrarrby[i] = Arrays.clone(arrby[i]);
        }
        return arrarrby;
    }

    public static byte[][][] clone(byte[][][] arrby) {
        if (arrby == null) {
            return null;
        }
        byte[][][] arrarrby = new byte[arrby.length][][];
        for (int i = 0; i != arrarrby.length; ++i) {
            arrarrby[i] = Arrays.clone(arrby[i]);
        }
        return arrarrby;
    }

    public static int[] clone(int[] arrn) {
        if (arrn == null) {
            return null;
        }
        int[] arrn2 = new int[arrn.length];
        System.arraycopy(arrn, 0, arrn2, 0, arrn.length);
        return arrn2;
    }

    public static long[] clone(long[] arrl) {
        if (arrl == null) {
            return null;
        }
        long[] arrl2 = new long[arrl.length];
        System.arraycopy(arrl, 0, arrl2, 0, arrl.length);
        return arrl2;
    }

    public static long[] clone(long[] arrl, long[] arrl2) {
        if (arrl == null) {
            return null;
        }
        if (arrl2 == null || arrl2.length != arrl.length) {
            return Arrays.clone(arrl);
        }
        System.arraycopy(arrl, 0, arrl2, 0, arrl2.length);
        return arrl2;
    }

    public static short[] clone(short[] arrs) {
        if (arrs == null) {
            return null;
        }
        short[] arrs2 = new short[arrs.length];
        System.arraycopy(arrs, 0, arrs2, 0, arrs.length);
        return arrs2;
    }

    public static BigInteger[] clone(BigInteger[] arrbigInteger) {
        if (arrbigInteger == null) {
            return null;
        }
        BigInteger[] arrbigInteger2 = new BigInteger[arrbigInteger.length];
        System.arraycopy(arrbigInteger, 0, arrbigInteger2, 0, arrbigInteger.length);
        return arrbigInteger2;
    }

    public static byte[] copyOf(byte[] arrby, int n) {
        byte[] arrby2 = new byte[n];
        if (n < arrby.length) {
            System.arraycopy(arrby, 0, arrby2, 0, n);
        } else {
            System.arraycopy(arrby, 0, arrby2, 0, arrby.length);
        }
        return arrby2;
    }

    public static char[] copyOf(char[] arrc, int n) {
        char[] arrc2 = new char[n];
        if (n < arrc.length) {
            System.arraycopy(arrc, 0, arrc2, 0, n);
        } else {
            System.arraycopy(arrc, 0, arrc2, 0, arrc.length);
        }
        return arrc2;
    }

    public static int[] copyOf(int[] arrn, int n) {
        int[] arrn2 = new int[n];
        if (n < arrn.length) {
            System.arraycopy(arrn, 0, arrn2, 0, n);
        } else {
            System.arraycopy(arrn, 0, arrn2, 0, arrn.length);
        }
        return arrn2;
    }

    public static long[] copyOf(long[] arrl, int n) {
        long[] arrl2 = new long[n];
        if (n < arrl.length) {
            System.arraycopy(arrl, 0, arrl2, 0, n);
        } else {
            System.arraycopy(arrl, 0, arrl2, 0, arrl.length);
        }
        return arrl2;
    }

    public static BigInteger[] copyOf(BigInteger[] arrbigInteger, int n) {
        BigInteger[] arrbigInteger2 = new BigInteger[n];
        if (n < arrbigInteger.length) {
            System.arraycopy(arrbigInteger, 0, arrbigInteger2, 0, n);
        } else {
            System.arraycopy(arrbigInteger, 0, arrbigInteger2, 0, arrbigInteger.length);
        }
        return arrbigInteger2;
    }

    public static byte[] copyOfRange(byte[] arrby, int n, int n2) {
        int n3 = Arrays.getLength(n, n2);
        byte[] arrby2 = new byte[n3];
        if (arrby.length - n < n3) {
            System.arraycopy(arrby, n, arrby2, 0, arrby.length - n);
        } else {
            System.arraycopy(arrby, n, arrby2, 0, n3);
        }
        return arrby2;
    }

    public static int[] copyOfRange(int[] arrn, int n, int n2) {
        int n3 = Arrays.getLength(n, n2);
        int[] arrn2 = new int[n3];
        if (arrn.length - n < n3) {
            System.arraycopy(arrn, n, arrn2, 0, arrn.length - n);
        } else {
            System.arraycopy(arrn, n, arrn2, 0, n3);
        }
        return arrn2;
    }

    public static long[] copyOfRange(long[] arrl, int n, int n2) {
        int n3 = Arrays.getLength(n, n2);
        long[] arrl2 = new long[n3];
        if (arrl.length - n < n3) {
            System.arraycopy(arrl, n, arrl2, 0, arrl.length - n);
        } else {
            System.arraycopy(arrl, n, arrl2, 0, n3);
        }
        return arrl2;
    }

    public static BigInteger[] copyOfRange(BigInteger[] arrbigInteger, int n, int n2) {
        int n3 = Arrays.getLength(n, n2);
        BigInteger[] arrbigInteger2 = new BigInteger[n3];
        if (arrbigInteger.length - n < n3) {
            System.arraycopy(arrbigInteger, n, arrbigInteger2, 0, arrbigInteger.length - n);
        } else {
            System.arraycopy(arrbigInteger, n, arrbigInteger2, 0, n3);
        }
        return arrbigInteger2;
    }

    private static int getLength(int n, int n2) {
        int n3 = n2 - n;
        if (n3 < 0) {
            StringBuffer stringBuffer = new StringBuffer(n);
            stringBuffer.append(" > ").append(n2);
            throw new IllegalArgumentException(stringBuffer.toString());
        }
        return n3;
    }

    public static byte[] append(byte[] arrby, byte by) {
        if (arrby == null) {
            return new byte[]{by};
        }
        int n = arrby.length;
        byte[] arrby2 = new byte[n + 1];
        System.arraycopy(arrby, 0, arrby2, 0, n);
        arrby2[n] = by;
        return arrby2;
    }

    public static short[] append(short[] arrs, short s) {
        if (arrs == null) {
            return new short[]{s};
        }
        int n = arrs.length;
        short[] arrs2 = new short[n + 1];
        System.arraycopy(arrs, 0, arrs2, 0, n);
        arrs2[n] = s;
        return arrs2;
    }

    public static int[] append(int[] arrn, int n) {
        if (arrn == null) {
            return new int[]{n};
        }
        int n2 = arrn.length;
        int[] arrn2 = new int[n2 + 1];
        System.arraycopy(arrn, 0, arrn2, 0, n2);
        arrn2[n2] = n;
        return arrn2;
    }

    public static byte[] concatenate(byte[] arrby, byte[] arrby2) {
        if (arrby != null && arrby2 != null) {
            byte[] arrby3 = new byte[arrby.length + arrby2.length];
            System.arraycopy(arrby, 0, arrby3, 0, arrby.length);
            System.arraycopy(arrby2, 0, arrby3, arrby.length, arrby2.length);
            return arrby3;
        }
        if (arrby2 != null) {
            return Arrays.clone(arrby2);
        }
        return Arrays.clone(arrby);
    }

    public static byte[] concatenate(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        if (arrby != null && arrby2 != null && arrby3 != null) {
            byte[] arrby4 = new byte[arrby.length + arrby2.length + arrby3.length];
            System.arraycopy(arrby, 0, arrby4, 0, arrby.length);
            System.arraycopy(arrby2, 0, arrby4, arrby.length, arrby2.length);
            System.arraycopy(arrby3, 0, arrby4, arrby.length + arrby2.length, arrby3.length);
            return arrby4;
        }
        if (arrby2 == null) {
            return Arrays.concatenate(arrby, arrby3);
        }
        return Arrays.concatenate(arrby, arrby2);
    }

    public static byte[] concatenate(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4) {
        if (arrby != null && arrby2 != null && arrby3 != null && arrby4 != null) {
            byte[] arrby5 = new byte[arrby.length + arrby2.length + arrby3.length + arrby4.length];
            System.arraycopy(arrby, 0, arrby5, 0, arrby.length);
            System.arraycopy(arrby2, 0, arrby5, arrby.length, arrby2.length);
            System.arraycopy(arrby3, 0, arrby5, arrby.length + arrby2.length, arrby3.length);
            System.arraycopy(arrby4, 0, arrby5, arrby.length + arrby2.length + arrby3.length, arrby4.length);
            return arrby5;
        }
        if (arrby4 == null) {
            return Arrays.concatenate(arrby, arrby2, arrby3);
        }
        if (arrby3 == null) {
            return Arrays.concatenate(arrby, arrby2, arrby4);
        }
        if (arrby2 == null) {
            return Arrays.concatenate(arrby, arrby3, arrby4);
        }
        return Arrays.concatenate(arrby2, arrby3, arrby4);
    }

    public static int[] concatenate(int[] arrn, int[] arrn2) {
        if (arrn == null) {
            return Arrays.clone(arrn2);
        }
        if (arrn2 == null) {
            return Arrays.clone(arrn);
        }
        int[] arrn3 = new int[arrn.length + arrn2.length];
        System.arraycopy(arrn, 0, arrn3, 0, arrn.length);
        System.arraycopy(arrn2, 0, arrn3, arrn.length, arrn2.length);
        return arrn3;
    }

    public static byte[] prepend(byte[] arrby, byte by) {
        if (arrby == null) {
            return new byte[]{by};
        }
        int n = arrby.length;
        byte[] arrby2 = new byte[n + 1];
        System.arraycopy(arrby, 0, arrby2, 1, n);
        arrby2[0] = by;
        return arrby2;
    }

    public static short[] prepend(short[] arrs, short s) {
        if (arrs == null) {
            return new short[]{s};
        }
        int n = arrs.length;
        short[] arrs2 = new short[n + 1];
        System.arraycopy(arrs, 0, arrs2, 1, n);
        arrs2[0] = s;
        return arrs2;
    }

    public static int[] prepend(int[] arrn, int n) {
        if (arrn == null) {
            return new int[]{n};
        }
        int n2 = arrn.length;
        int[] arrn2 = new int[n2 + 1];
        System.arraycopy(arrn, 0, arrn2, 1, n2);
        arrn2[0] = n;
        return arrn2;
    }

    public static byte[] reverse(byte[] arrby) {
        if (arrby == null) {
            return null;
        }
        int n = 0;
        int n2 = arrby.length;
        byte[] arrby2 = new byte[n2];
        while (--n2 >= 0) {
            arrby2[n2] = arrby[n++];
        }
        return arrby2;
    }

    public static class Iterator
    implements java.util.Iterator {
        private final Object[] dataArray;
        private int position = 0;

        public Iterator(Object[] arrobject) {
            this.dataArray = arrobject;
        }

        public boolean hasNext() {
            return this.position < this.dataArray.length;
        }

        public Object next() {
            return this.dataArray[this.position++];
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot remove element from an Array.");
        }
    }
}

