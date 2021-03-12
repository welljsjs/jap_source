/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.raw;

import java.util.Random;
import org.bouncycastle.math.raw.Nat;

public abstract class Mod {
    public static int inverse32(int n) {
        int n2 = n;
        n2 *= 2 - n * n2;
        n2 *= 2 - n * n2;
        n2 *= 2 - n * n2;
        n2 *= 2 - n * n2;
        return n2;
    }

    public static void invert(int[] arrn, int[] arrn2, int[] arrn3) {
        int n = arrn.length;
        if (Nat.isZero(n, arrn2)) {
            throw new IllegalArgumentException("'x' cannot be 0");
        }
        if (Nat.isOne(n, arrn2)) {
            System.arraycopy(arrn2, 0, arrn3, 0, n);
            return;
        }
        int[] arrn4 = Nat.copy(n, arrn2);
        int[] arrn5 = Nat.create(n);
        arrn5[0] = 1;
        int n2 = 0;
        if ((arrn4[0] & 1) == 0) {
            n2 = Mod.inversionStep(arrn, arrn4, n, arrn5, n2);
        }
        if (Nat.isOne(n, arrn4)) {
            Mod.inversionResult(arrn, n2, arrn5, arrn3);
            return;
        }
        int[] arrn6 = Nat.copy(n, arrn);
        int[] arrn7 = Nat.create(n);
        int n3 = 0;
        int n4 = n;
        while (true) {
            if (arrn4[n4 - 1] == 0 && arrn6[n4 - 1] == 0) {
                --n4;
                continue;
            }
            if (Nat.gte(n4, arrn4, arrn6)) {
                Nat.subFrom(n4, arrn6, arrn4);
                n2 += Nat.subFrom(n, arrn7, arrn5) - n3;
                n2 = Mod.inversionStep(arrn, arrn4, n4, arrn5, n2);
                if (!Nat.isOne(n4, arrn4)) continue;
                Mod.inversionResult(arrn, n2, arrn5, arrn3);
                return;
            }
            Nat.subFrom(n4, arrn4, arrn6);
            n3 += Nat.subFrom(n, arrn5, arrn7) - n2;
            n3 = Mod.inversionStep(arrn, arrn6, n4, arrn7, n3);
            if (Nat.isOne(n4, arrn6)) break;
        }
        Mod.inversionResult(arrn, n3, arrn7, arrn3);
    }

    public static int[] random(int[] arrn) {
        int n = arrn.length;
        Random random = new Random();
        int[] arrn2 = Nat.create(n);
        int n2 = arrn[n - 1];
        n2 |= n2 >>> 1;
        n2 |= n2 >>> 2;
        n2 |= n2 >>> 4;
        n2 |= n2 >>> 8;
        n2 |= n2 >>> 16;
        do {
            for (int i = 0; i != n; ++i) {
                arrn2[i] = random.nextInt();
            }
            int n3 = n - 1;
            arrn2[n3] = arrn2[n3] & n2;
        } while (Nat.gte(n, arrn2, arrn));
        return arrn2;
    }

    public static void add(int[] arrn, int[] arrn2, int[] arrn3, int[] arrn4) {
        int n = arrn.length;
        int n2 = Nat.add(n, arrn2, arrn3, arrn4);
        if (n2 != 0) {
            Nat.subFrom(n, arrn, arrn4);
        }
    }

    public static void subtract(int[] arrn, int[] arrn2, int[] arrn3, int[] arrn4) {
        int n = arrn.length;
        int n2 = Nat.sub(n, arrn2, arrn3, arrn4);
        if (n2 != 0) {
            Nat.addTo(n, arrn, arrn4);
        }
    }

    private static void inversionResult(int[] arrn, int n, int[] arrn2, int[] arrn3) {
        if (n < 0) {
            Nat.add(arrn.length, arrn2, arrn, arrn3);
        } else {
            System.arraycopy(arrn2, 0, arrn3, 0, arrn.length);
        }
    }

    private static int inversionStep(int[] arrn, int[] arrn2, int n, int[] arrn3, int n2) {
        int n3 = arrn.length;
        int n4 = 0;
        while (arrn2[0] == 0) {
            Nat.shiftDownWord(n, arrn2, 0);
            n4 += 32;
        }
        int n5 = Mod.getTrailingZeroes(arrn2[0]);
        if (n5 > 0) {
            Nat.shiftDownBits(n, arrn2, n5, 0);
            n4 += n5;
        }
        for (n5 = 0; n5 < n4; ++n5) {
            if ((arrn3[0] & 1) != 0) {
                n2 = n2 < 0 ? (n2 += Nat.addTo(n3, arrn, arrn3)) : (n2 += Nat.subFrom(n3, arrn, arrn3));
            }
            Nat.shiftDownBit(n3, arrn3, n2);
        }
        return n2;
    }

    private static int getTrailingZeroes(int n) {
        int n2 = 0;
        while ((n & 1) == 0) {
            n >>>= 1;
            ++n2;
        }
        return n2;
    }
}

