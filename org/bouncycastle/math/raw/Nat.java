/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat {
    private static final long M = 0xFFFFFFFFL;

    public static int add(int n, int[] arrn, int[] arrn2, int[] arrn3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[i] = (int)(l += ((long)arrn[i] & 0xFFFFFFFFL) + ((long)arrn2[i] & 0xFFFFFFFFL));
            l >>>= 32;
        }
        return (int)l;
    }

    public static int add33At(int n, int n2, int[] arrn, int n3) {
        long l = ((long)arrn[n3 + 0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + 0] = (int)l;
        l >>>= 32;
        arrn[n3 + 1] = (int)(l += ((long)arrn[n3 + 1] & 0xFFFFFFFFL) + 1L);
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3 + 2);
    }

    public static int add33At(int n, int n2, int[] arrn, int n3, int n4) {
        long l = ((long)arrn[n3 + n4] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + n4] = (int)l;
        l >>>= 32;
        arrn[n3 + n4 + 1] = (int)(l += ((long)arrn[n3 + n4 + 1] & 0xFFFFFFFFL) + 1L);
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3, n4 + 2);
    }

    public static int add33To(int n, int n2, int[] arrn) {
        long l = ((long)arrn[0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        arrn[0] = (int)l;
        l >>>= 32;
        arrn[1] = (int)(l += ((long)arrn[1] & 0xFFFFFFFFL) + 1L);
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, 2);
    }

    public static int add33To(int n, int n2, int[] arrn, int n3) {
        long l = ((long)arrn[n3 + 0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + 0] = (int)l;
        l >>>= 32;
        arrn[n3 + 1] = (int)(l += ((long)arrn[n3 + 1] & 0xFFFFFFFFL) + 1L);
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3, 2);
    }

    public static int addBothTo(int n, int[] arrn, int[] arrn2, int[] arrn3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[i] = (int)(l += ((long)arrn[i] & 0xFFFFFFFFL) + ((long)arrn2[i] & 0xFFFFFFFFL) + ((long)arrn3[i] & 0xFFFFFFFFL));
            l >>>= 32;
        }
        return (int)l;
    }

    public static int addBothTo(int n, int[] arrn, int n2, int[] arrn2, int n3, int[] arrn3, int n4) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[n4 + i] = (int)(l += ((long)arrn[n2 + i] & 0xFFFFFFFFL) + ((long)arrn2[n3 + i] & 0xFFFFFFFFL) + ((long)arrn3[n4 + i] & 0xFFFFFFFFL));
            l >>>= 32;
        }
        return (int)l;
    }

    public static int addDWordAt(int n, long l, int[] arrn, int n2) {
        long l2 = ((long)arrn[n2 + 0] & 0xFFFFFFFFL) + (l & 0xFFFFFFFFL);
        arrn[n2 + 0] = (int)l2;
        l2 >>>= 32;
        arrn[n2 + 1] = (int)(l2 += ((long)arrn[n2 + 1] & 0xFFFFFFFFL) + (l >>> 32));
        return (l2 >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n2 + 2);
    }

    public static int addDWordAt(int n, long l, int[] arrn, int n2, int n3) {
        long l2 = ((long)arrn[n2 + n3] & 0xFFFFFFFFL) + (l & 0xFFFFFFFFL);
        arrn[n2 + n3] = (int)l2;
        l2 >>>= 32;
        arrn[n2 + n3 + 1] = (int)(l2 += ((long)arrn[n2 + n3 + 1] & 0xFFFFFFFFL) + (l >>> 32));
        return (l2 >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n2, n3 + 2);
    }

    public static int addDWordTo(int n, long l, int[] arrn) {
        long l2 = ((long)arrn[0] & 0xFFFFFFFFL) + (l & 0xFFFFFFFFL);
        arrn[0] = (int)l2;
        l2 >>>= 32;
        arrn[1] = (int)(l2 += ((long)arrn[1] & 0xFFFFFFFFL) + (l >>> 32));
        return (l2 >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, 2);
    }

    public static int addDWordTo(int n, long l, int[] arrn, int n2) {
        long l2 = ((long)arrn[n2 + 0] & 0xFFFFFFFFL) + (l & 0xFFFFFFFFL);
        arrn[n2 + 0] = (int)l2;
        l2 >>>= 32;
        arrn[n2 + 1] = (int)(l2 += ((long)arrn[n2 + 1] & 0xFFFFFFFFL) + (l >>> 32));
        return (l2 >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n2, 2);
    }

    public static int addTo(int n, int[] arrn, int[] arrn2) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn2[i] = (int)(l += ((long)arrn[i] & 0xFFFFFFFFL) + ((long)arrn2[i] & 0xFFFFFFFFL));
            l >>>= 32;
        }
        return (int)l;
    }

    public static int addTo(int n, int[] arrn, int n2, int[] arrn2, int n3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn2[n3 + i] = (int)(l += ((long)arrn[n2 + i] & 0xFFFFFFFFL) + ((long)arrn2[n3 + i] & 0xFFFFFFFFL));
            l >>>= 32;
        }
        return (int)l;
    }

    public static int addWordAt(int n, int n2, int[] arrn, int n3) {
        long l = ((long)n2 & 0xFFFFFFFFL) + ((long)arrn[n3] & 0xFFFFFFFFL);
        arrn[n3] = (int)l;
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3 + 1);
    }

    public static int addWordAt(int n, int n2, int[] arrn, int n3, int n4) {
        long l = ((long)n2 & 0xFFFFFFFFL) + ((long)arrn[n3 + n4] & 0xFFFFFFFFL);
        arrn[n3 + n4] = (int)l;
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3, n4 + 1);
    }

    public static int addWordTo(int n, int n2, int[] arrn) {
        long l = ((long)n2 & 0xFFFFFFFFL) + ((long)arrn[0] & 0xFFFFFFFFL);
        arrn[0] = (int)l;
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, 1);
    }

    public static int addWordTo(int n, int n2, int[] arrn, int n3) {
        long l = ((long)n2 & 0xFFFFFFFFL) + ((long)arrn[n3] & 0xFFFFFFFFL);
        arrn[n3] = (int)l;
        return (l >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3, 1);
    }

    public static int[] copy(int n, int[] arrn) {
        int[] arrn2 = new int[n];
        System.arraycopy(arrn, 0, arrn2, 0, n);
        return arrn2;
    }

    public static void copy(int n, int[] arrn, int[] arrn2) {
        System.arraycopy(arrn, 0, arrn2, 0, n);
    }

    public static int[] create(int n) {
        return new int[n];
    }

    public static long[] create64(int n) {
        return new long[n];
    }

    public static int dec(int n, int[] arrn) {
        int n2 = 0;
        while (n2 < n) {
            int n3 = n2++;
            arrn[n3] = arrn[n3] - 1;
            if (arrn[n3] == -1) continue;
            return 0;
        }
        return -1;
    }

    public static int dec(int n, int[] arrn, int[] arrn2) {
        for (int i = 0; i < n; ++i) {
            int n2;
            arrn2[i] = n2 = arrn[i] - 1;
            if (n2 == -1) continue;
            while (i < n) {
                arrn2[i] = arrn[i];
                ++i;
            }
            return 0;
        }
        return -1;
    }

    public static int decAt(int n, int[] arrn, int n2) {
        int n3 = n2;
        while (n3 < n) {
            int n4 = n3++;
            arrn[n4] = arrn[n4] - 1;
            if (arrn[n4] == -1) continue;
            return 0;
        }
        return -1;
    }

    public static int decAt(int n, int[] arrn, int n2, int n3) {
        for (int i = n3; i < n; ++i) {
            int n4 = n2 + i;
            arrn[n4] = arrn[n4] - 1;
            if (arrn[n4] == -1) continue;
            return 0;
        }
        return -1;
    }

    public static boolean eq(int n, int[] arrn, int[] arrn2) {
        for (int i = n - 1; i >= 0; --i) {
            if (arrn[i] == arrn2[i]) continue;
            return false;
        }
        return true;
    }

    public static int[] fromBigInteger(int n, BigInteger bigInteger) {
        if (bigInteger.signum() < 0 || bigInteger.bitLength() > n) {
            throw new IllegalArgumentException();
        }
        int n2 = n + 31 >> 5;
        int[] arrn = Nat.create(n2);
        int n3 = 0;
        while (bigInteger.signum() != 0) {
            arrn[n3++] = bigInteger.intValue();
            bigInteger = bigInteger.shiftRight(32);
        }
        return arrn;
    }

    public static int getBit(int[] arrn, int n) {
        if (n == 0) {
            return arrn[0] & 1;
        }
        int n2 = n >> 5;
        if (n2 < 0 || n2 >= arrn.length) {
            return 0;
        }
        int n3 = n & 0x1F;
        return arrn[n2] >>> n3 & 1;
    }

    public static boolean gte(int n, int[] arrn, int[] arrn2) {
        for (int i = n - 1; i >= 0; --i) {
            int n2 = arrn[i] ^ Integer.MIN_VALUE;
            int n3 = arrn2[i] ^ Integer.MIN_VALUE;
            if (n2 < n3) {
                return false;
            }
            if (n2 <= n3) continue;
            return true;
        }
        return true;
    }

    public static int inc(int n, int[] arrn) {
        int n2 = 0;
        while (n2 < n) {
            int n3 = n2++;
            arrn[n3] = arrn[n3] + 1;
            if (arrn[n3] == 0) continue;
            return 0;
        }
        return 1;
    }

    public static int inc(int n, int[] arrn, int[] arrn2) {
        for (int i = 0; i < n; ++i) {
            int n2;
            arrn2[i] = n2 = arrn[i] + 1;
            if (n2 == 0) continue;
            while (i < n) {
                arrn2[i] = arrn[i];
                ++i;
            }
            return 0;
        }
        return 1;
    }

    public static int incAt(int n, int[] arrn, int n2) {
        int n3 = n2;
        while (n3 < n) {
            int n4 = n3++;
            arrn[n4] = arrn[n4] + 1;
            if (arrn[n4] == 0) continue;
            return 0;
        }
        return 1;
    }

    public static int incAt(int n, int[] arrn, int n2, int n3) {
        for (int i = n3; i < n; ++i) {
            int n4 = n2 + i;
            arrn[n4] = arrn[n4] + 1;
            if (arrn[n4] == 0) continue;
            return 0;
        }
        return 1;
    }

    public static boolean isOne(int n, int[] arrn) {
        if (arrn[0] != 1) {
            return false;
        }
        for (int i = 1; i < n; ++i) {
            if (arrn[i] == 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isZero(int n, int[] arrn) {
        for (int i = 0; i < n; ++i) {
            if (arrn[i] == 0) continue;
            return false;
        }
        return true;
    }

    public static void mul(int n, int[] arrn, int[] arrn2, int[] arrn3) {
        arrn3[n] = Nat.mulWord(n, arrn[0], arrn2, arrn3);
        for (int i = 1; i < n; ++i) {
            arrn3[i + n] = Nat.mulWordAddTo(n, arrn[i], arrn2, 0, arrn3, i);
        }
    }

    public static void mul(int n, int[] arrn, int n2, int[] arrn2, int n3, int[] arrn3, int n4) {
        arrn3[n4 + n] = Nat.mulWord(n, arrn[n2], arrn2, n3, arrn3, n4);
        for (int i = 1; i < n; ++i) {
            arrn3[n4 + i + n] = Nat.mulWordAddTo(n, arrn[n2 + i], arrn2, n3, arrn3, n4 + i);
        }
    }

    public static int mulAddTo(int n, int[] arrn, int[] arrn2, int[] arrn3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            long l2 = (long)Nat.mulWordAddTo(n, arrn[i], arrn2, 0, arrn3, i) & 0xFFFFFFFFL;
            arrn3[i + n] = (int)(l2 += l + ((long)arrn3[i + n] & 0xFFFFFFFFL));
            l = l2 >>> 32;
        }
        return (int)l;
    }

    public static int mulAddTo(int n, int[] arrn, int n2, int[] arrn2, int n3, int[] arrn3, int n4) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            long l2 = (long)Nat.mulWordAddTo(n, arrn[n2 + i], arrn2, n3, arrn3, n4) & 0xFFFFFFFFL;
            arrn3[n4 + n] = (int)(l2 += l + ((long)arrn3[n4 + n] & 0xFFFFFFFFL));
            l = l2 >>> 32;
            ++n4;
        }
        return (int)l;
    }

    public static int mul31BothAdd(int n, int n2, int[] arrn, int n3, int[] arrn2, int[] arrn3, int n4) {
        long l = 0L;
        long l2 = (long)n2 & 0xFFFFFFFFL;
        long l3 = (long)n3 & 0xFFFFFFFFL;
        int n5 = 0;
        do {
            arrn3[n4 + n5] = (int)(l += l2 * ((long)arrn[n5] & 0xFFFFFFFFL) + l3 * ((long)arrn2[n5] & 0xFFFFFFFFL) + ((long)arrn3[n4 + n5] & 0xFFFFFFFFL));
            l >>>= 32;
        } while (++n5 < n);
        return (int)l;
    }

    public static int mulWord(int n, int n2, int[] arrn, int[] arrn2) {
        long l = 0L;
        long l2 = (long)n2 & 0xFFFFFFFFL;
        int n3 = 0;
        do {
            arrn2[n3] = (int)(l += l2 * ((long)arrn[n3] & 0xFFFFFFFFL));
            l >>>= 32;
        } while (++n3 < n);
        return (int)l;
    }

    public static int mulWord(int n, int n2, int[] arrn, int n3, int[] arrn2, int n4) {
        long l = 0L;
        long l2 = (long)n2 & 0xFFFFFFFFL;
        int n5 = 0;
        do {
            arrn2[n4 + n5] = (int)(l += l2 * ((long)arrn[n3 + n5] & 0xFFFFFFFFL));
            l >>>= 32;
        } while (++n5 < n);
        return (int)l;
    }

    public static int mulWordAddTo(int n, int n2, int[] arrn, int n3, int[] arrn2, int n4) {
        long l = 0L;
        long l2 = (long)n2 & 0xFFFFFFFFL;
        int n5 = 0;
        do {
            arrn2[n4 + n5] = (int)(l += l2 * ((long)arrn[n3 + n5] & 0xFFFFFFFFL) + ((long)arrn2[n4 + n5] & 0xFFFFFFFFL));
            l >>>= 32;
        } while (++n5 < n);
        return (int)l;
    }

    public static int mulWordDwordAddAt(int n, int n2, long l, int[] arrn, int n3) {
        long l2 = 0L;
        long l3 = (long)n2 & 0xFFFFFFFFL;
        arrn[n3 + 0] = (int)(l2 += l3 * (l & 0xFFFFFFFFL) + ((long)arrn[n3 + 0] & 0xFFFFFFFFL));
        l2 >>>= 32;
        arrn[n3 + 1] = (int)(l2 += l3 * (l >>> 32) + ((long)arrn[n3 + 1] & 0xFFFFFFFFL));
        l2 >>>= 32;
        arrn[n3 + 2] = (int)(l2 += (long)arrn[n3 + 2] & 0xFFFFFFFFL);
        return (l2 >>>= 32) == 0L ? 0 : Nat.incAt(n, arrn, n3 + 3);
    }

    public static int shiftDownBit(int n, int[] arrn, int n2) {
        int n3 = n;
        while (--n3 >= 0) {
            int n4 = arrn[n3];
            arrn[n3] = n4 >>> 1 | n2 << 31;
            n2 = n4;
        }
        return n2 << 31;
    }

    public static int shiftDownBit(int n, int[] arrn, int n2, int n3) {
        int n4 = n;
        while (--n4 >= 0) {
            int n5 = arrn[n2 + n4];
            arrn[n2 + n4] = n5 >>> 1 | n3 << 31;
            n3 = n5;
        }
        return n3 << 31;
    }

    public static int shiftDownBit(int n, int[] arrn, int n2, int[] arrn2) {
        int n3 = n;
        while (--n3 >= 0) {
            int n4 = arrn[n3];
            arrn2[n3] = n4 >>> 1 | n2 << 31;
            n2 = n4;
        }
        return n2 << 31;
    }

    public static int shiftDownBit(int n, int[] arrn, int n2, int n3, int[] arrn2, int n4) {
        int n5 = n;
        while (--n5 >= 0) {
            int n6 = arrn[n2 + n5];
            arrn2[n4 + n5] = n6 >>> 1 | n3 << 31;
            n3 = n6;
        }
        return n3 << 31;
    }

    public static int shiftDownBits(int n, int[] arrn, int n2, int n3) {
        int n4 = n;
        while (--n4 >= 0) {
            int n5 = arrn[n4];
            arrn[n4] = n5 >>> n2 | n3 << -n2;
            n3 = n5;
        }
        return n3 << -n2;
    }

    public static int shiftDownBits(int n, int[] arrn, int n2, int n3, int n4) {
        int n5 = n;
        while (--n5 >= 0) {
            int n6 = arrn[n2 + n5];
            arrn[n2 + n5] = n6 >>> n3 | n4 << -n3;
            n4 = n6;
        }
        return n4 << -n3;
    }

    public static int shiftDownBits(int n, int[] arrn, int n2, int n3, int[] arrn2) {
        int n4 = n;
        while (--n4 >= 0) {
            int n5 = arrn[n4];
            arrn2[n4] = n5 >>> n2 | n3 << -n2;
            n3 = n5;
        }
        return n3 << -n2;
    }

    public static int shiftDownBits(int n, int[] arrn, int n2, int n3, int n4, int[] arrn2, int n5) {
        int n6 = n;
        while (--n6 >= 0) {
            int n7 = arrn[n2 + n6];
            arrn2[n5 + n6] = n7 >>> n3 | n4 << -n3;
            n4 = n7;
        }
        return n4 << -n3;
    }

    public static int shiftDownWord(int n, int[] arrn, int n2) {
        int n3 = n;
        while (--n3 >= 0) {
            int n4 = arrn[n3];
            arrn[n3] = n2;
            n2 = n4;
        }
        return n2;
    }

    public static int shiftUpBit(int n, int[] arrn, int n2) {
        for (int i = 0; i < n; ++i) {
            int n3 = arrn[i];
            arrn[i] = n3 << 1 | n2 >>> 31;
            n2 = n3;
        }
        return n2 >>> 31;
    }

    public static int shiftUpBit(int n, int[] arrn, int n2, int n3) {
        for (int i = 0; i < n; ++i) {
            int n4 = arrn[n2 + i];
            arrn[n2 + i] = n4 << 1 | n3 >>> 31;
            n3 = n4;
        }
        return n3 >>> 31;
    }

    public static int shiftUpBit(int n, int[] arrn, int n2, int[] arrn2) {
        for (int i = 0; i < n; ++i) {
            int n3 = arrn[i];
            arrn2[i] = n3 << 1 | n2 >>> 31;
            n2 = n3;
        }
        return n2 >>> 31;
    }

    public static int shiftUpBit(int n, int[] arrn, int n2, int n3, int[] arrn2, int n4) {
        for (int i = 0; i < n; ++i) {
            int n5 = arrn[n2 + i];
            arrn2[n4 + i] = n5 << 1 | n3 >>> 31;
            n3 = n5;
        }
        return n3 >>> 31;
    }

    public static long shiftUpBit64(int n, long[] arrl, int n2, long l, long[] arrl2, int n3) {
        for (int i = 0; i < n; ++i) {
            long l2 = arrl[n2 + i];
            arrl2[n3 + i] = l2 << 1 | l >>> 63;
            l = l2;
        }
        return l >>> 63;
    }

    public static int shiftUpBits(int n, int[] arrn, int n2, int n3) {
        for (int i = 0; i < n; ++i) {
            int n4 = arrn[i];
            arrn[i] = n4 << n2 | n3 >>> -n2;
            n3 = n4;
        }
        return n3 >>> -n2;
    }

    public static int shiftUpBits(int n, int[] arrn, int n2, int n3, int n4) {
        for (int i = 0; i < n; ++i) {
            int n5 = arrn[n2 + i];
            arrn[n2 + i] = n5 << n3 | n4 >>> -n3;
            n4 = n5;
        }
        return n4 >>> -n3;
    }

    public static long shiftUpBits64(int n, long[] arrl, int n2, int n3, long l) {
        for (int i = 0; i < n; ++i) {
            long l2 = arrl[n2 + i];
            arrl[n2 + i] = l2 << n3 | l >>> -n3;
            l = l2;
        }
        return l >>> -n3;
    }

    public static int shiftUpBits(int n, int[] arrn, int n2, int n3, int[] arrn2) {
        for (int i = 0; i < n; ++i) {
            int n4 = arrn[i];
            arrn2[i] = n4 << n2 | n3 >>> -n2;
            n3 = n4;
        }
        return n3 >>> -n2;
    }

    public static int shiftUpBits(int n, int[] arrn, int n2, int n3, int n4, int[] arrn2, int n5) {
        for (int i = 0; i < n; ++i) {
            int n6 = arrn[n2 + i];
            arrn2[n5 + i] = n6 << n3 | n4 >>> -n3;
            n4 = n6;
        }
        return n4 >>> -n3;
    }

    public static long shiftUpBits64(int n, long[] arrl, int n2, int n3, long l, long[] arrl2, int n4) {
        for (int i = 0; i < n; ++i) {
            long l2 = arrl[n2 + i];
            arrl2[n4 + i] = l2 << n3 | l >>> -n3;
            l = l2;
        }
        return l >>> -n3;
    }

    public static void square(int n, int[] arrn, int[] arrn2) {
        int n2 = n << 1;
        int n3 = 0;
        int n4 = n;
        int n5 = n2;
        do {
            long l = (long)arrn[--n4] & 0xFFFFFFFFL;
            long l2 = l * l;
            arrn2[--n5] = n3 << 31 | (int)(l2 >>> 33);
            arrn2[--n5] = (int)(l2 >>> 1);
            n3 = (int)l2;
        } while (n4 > 0);
        for (int i = 1; i < n; ++i) {
            n3 = Nat.squareWordAdd(arrn, i, arrn2);
            Nat.addWordAt(n2, n3, arrn2, i << 1);
        }
        Nat.shiftUpBit(n2, arrn2, arrn[0] << 31);
    }

    public static void square(int n, int[] arrn, int n2, int[] arrn2, int n3) {
        int n4 = n << 1;
        int n5 = 0;
        int n6 = n;
        int n7 = n4;
        do {
            long l = (long)arrn[n2 + --n6] & 0xFFFFFFFFL;
            long l2 = l * l;
            arrn2[n3 + --n7] = n5 << 31 | (int)(l2 >>> 33);
            arrn2[n3 + --n7] = (int)(l2 >>> 1);
            n5 = (int)l2;
        } while (n6 > 0);
        for (int i = 1; i < n; ++i) {
            n5 = Nat.squareWordAdd(arrn, n2, i, arrn2, n3);
            Nat.addWordAt(n4, n5, arrn2, n3, i << 1);
        }
        Nat.shiftUpBit(n4, arrn2, n3, arrn[n2] << 31);
    }

    public static int squareWordAdd(int[] arrn, int n, int[] arrn2) {
        long l = 0L;
        long l2 = (long)arrn[n] & 0xFFFFFFFFL;
        int n2 = 0;
        do {
            arrn2[n + n2] = (int)(l += l2 * ((long)arrn[n2] & 0xFFFFFFFFL) + ((long)arrn2[n + n2] & 0xFFFFFFFFL));
            l >>>= 32;
        } while (++n2 < n);
        return (int)l;
    }

    public static int squareWordAdd(int[] arrn, int n, int n2, int[] arrn2, int n3) {
        long l = 0L;
        long l2 = (long)arrn[n + n2] & 0xFFFFFFFFL;
        int n4 = 0;
        do {
            arrn2[n2 + n3] = (int)(l += l2 * ((long)arrn[n + n4] & 0xFFFFFFFFL) + ((long)arrn2[n2 + n3] & 0xFFFFFFFFL));
            l >>>= 32;
            ++n3;
        } while (++n4 < n2);
        return (int)l;
    }

    public static int sub(int n, int[] arrn, int[] arrn2, int[] arrn3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[i] = (int)(l += ((long)arrn[i] & 0xFFFFFFFFL) - ((long)arrn2[i] & 0xFFFFFFFFL));
            l >>= 32;
        }
        return (int)l;
    }

    public static int sub(int n, int[] arrn, int n2, int[] arrn2, int n3, int[] arrn3, int n4) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[n4 + i] = (int)(l += ((long)arrn[n2 + i] & 0xFFFFFFFFL) - ((long)arrn2[n3 + i] & 0xFFFFFFFFL));
            l >>= 32;
        }
        return (int)l;
    }

    public static int sub33At(int n, int n2, int[] arrn, int n3) {
        long l = ((long)arrn[n3 + 0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + 0] = (int)l;
        l >>= 32;
        arrn[n3 + 1] = (int)(l += ((long)arrn[n3 + 1] & 0xFFFFFFFFL) - 1L);
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n3 + 2);
    }

    public static int sub33At(int n, int n2, int[] arrn, int n3, int n4) {
        long l = ((long)arrn[n3 + n4] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + n4] = (int)l;
        l >>= 32;
        arrn[n3 + n4 + 1] = (int)(l += ((long)arrn[n3 + n4 + 1] & 0xFFFFFFFFL) - 1L);
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n3, n4 + 2);
    }

    public static int sub33From(int n, int n2, int[] arrn) {
        long l = ((long)arrn[0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[0] = (int)l;
        l >>= 32;
        arrn[1] = (int)(l += ((long)arrn[1] & 0xFFFFFFFFL) - 1L);
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, 2);
    }

    public static int sub33From(int n, int n2, int[] arrn, int n3) {
        long l = ((long)arrn[n3 + 0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + 0] = (int)l;
        l >>= 32;
        arrn[n3 + 1] = (int)(l += ((long)arrn[n3 + 1] & 0xFFFFFFFFL) - 1L);
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n3, 2);
    }

    public static int subBothFrom(int n, int[] arrn, int[] arrn2, int[] arrn3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[i] = (int)(l += ((long)arrn3[i] & 0xFFFFFFFFL) - ((long)arrn[i] & 0xFFFFFFFFL) - ((long)arrn2[i] & 0xFFFFFFFFL));
            l >>= 32;
        }
        return (int)l;
    }

    public static int subBothFrom(int n, int[] arrn, int n2, int[] arrn2, int n3, int[] arrn3, int n4) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn3[n4 + i] = (int)(l += ((long)arrn3[n4 + i] & 0xFFFFFFFFL) - ((long)arrn[n2 + i] & 0xFFFFFFFFL) - ((long)arrn2[n3 + i] & 0xFFFFFFFFL));
            l >>= 32;
        }
        return (int)l;
    }

    public static int subDWordAt(int n, long l, int[] arrn, int n2) {
        long l2 = ((long)arrn[n2 + 0] & 0xFFFFFFFFL) - (l & 0xFFFFFFFFL);
        arrn[n2 + 0] = (int)l2;
        l2 >>= 32;
        arrn[n2 + 1] = (int)(l2 += ((long)arrn[n2 + 1] & 0xFFFFFFFFL) - (l >>> 32));
        return (l2 >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n2 + 2);
    }

    public static int subDWordAt(int n, long l, int[] arrn, int n2, int n3) {
        long l2 = ((long)arrn[n2 + n3] & 0xFFFFFFFFL) - (l & 0xFFFFFFFFL);
        arrn[n2 + n3] = (int)l2;
        l2 >>= 32;
        arrn[n2 + n3 + 1] = (int)(l2 += ((long)arrn[n2 + n3 + 1] & 0xFFFFFFFFL) - (l >>> 32));
        return (l2 >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n2, n3 + 2);
    }

    public static int subDWordFrom(int n, long l, int[] arrn) {
        long l2 = ((long)arrn[0] & 0xFFFFFFFFL) - (l & 0xFFFFFFFFL);
        arrn[0] = (int)l2;
        l2 >>= 32;
        arrn[1] = (int)(l2 += ((long)arrn[1] & 0xFFFFFFFFL) - (l >>> 32));
        return (l2 >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, 2);
    }

    public static int subDWordFrom(int n, long l, int[] arrn, int n2) {
        long l2 = ((long)arrn[n2 + 0] & 0xFFFFFFFFL) - (l & 0xFFFFFFFFL);
        arrn[n2 + 0] = (int)l2;
        l2 >>= 32;
        arrn[n2 + 1] = (int)(l2 += ((long)arrn[n2 + 1] & 0xFFFFFFFFL) - (l >>> 32));
        return (l2 >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n2, 2);
    }

    public static int subFrom(int n, int[] arrn, int[] arrn2) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn2[i] = (int)(l += ((long)arrn2[i] & 0xFFFFFFFFL) - ((long)arrn[i] & 0xFFFFFFFFL));
            l >>= 32;
        }
        return (int)l;
    }

    public static int subFrom(int n, int[] arrn, int n2, int[] arrn2, int n3) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            arrn2[n3 + i] = (int)(l += ((long)arrn2[n3 + i] & 0xFFFFFFFFL) - ((long)arrn[n2 + i] & 0xFFFFFFFFL));
            l >>= 32;
        }
        return (int)l;
    }

    public static int subWordAt(int n, int n2, int[] arrn, int n3) {
        long l = ((long)arrn[n3] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[n3] = (int)l;
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n3 + 1);
    }

    public static int subWordAt(int n, int n2, int[] arrn, int n3, int n4) {
        long l = ((long)arrn[n3 + n4] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + n4] = (int)l;
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n3, n4 + 1);
    }

    public static int subWordFrom(int n, int n2, int[] arrn) {
        long l = ((long)arrn[0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[0] = (int)l;
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, 1);
    }

    public static int subWordFrom(int n, int n2, int[] arrn, int n3) {
        long l = ((long)arrn[n3 + 0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        arrn[n3 + 0] = (int)l;
        return (l >>= 32) == 0L ? 0 : Nat.decAt(n, arrn, n3, 1);
    }

    public static BigInteger toBigInteger(int n, int[] arrn) {
        byte[] arrby = new byte[n << 2];
        for (int i = 0; i < n; ++i) {
            int n2 = arrn[i];
            if (n2 == 0) continue;
            Pack.intToBigEndian(n2, arrby, n - 1 - i << 2);
        }
        return new BigInteger(1, arrby);
    }

    public static void zero(int n, int[] arrn) {
        for (int i = 0; i < n; ++i) {
            arrn[i] = 0;
        }
    }
}

