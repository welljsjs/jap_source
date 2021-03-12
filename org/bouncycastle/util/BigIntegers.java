/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class BigIntegers {
    private static final int MAX_ITERATIONS = 1000;
    private static final BigInteger ZERO = BigInteger.valueOf(0L);

    public static byte[] asUnsignedByteArray(BigInteger bigInteger) {
        byte[] arrby = bigInteger.toByteArray();
        if (arrby[0] == 0) {
            byte[] arrby2 = new byte[arrby.length - 1];
            System.arraycopy(arrby, 1, arrby2, 0, arrby2.length);
            return arrby2;
        }
        return arrby;
    }

    public static byte[] asUnsignedByteArray(int n, BigInteger bigInteger) {
        byte[] arrby = bigInteger.toByteArray();
        if (arrby.length == n) {
            return arrby;
        }
        int n2 = arrby[0] == 0 ? 1 : 0;
        int n3 = arrby.length - n2;
        if (n3 > n) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        byte[] arrby2 = new byte[n];
        System.arraycopy(arrby, n2, arrby2, arrby2.length - n3, n3);
        return arrby2;
    }

    public static BigInteger createRandomInRange(BigInteger bigInteger, BigInteger bigInteger2, SecureRandom secureRandom) {
        int n = bigInteger.compareTo(bigInteger2);
        if (n >= 0) {
            if (n > 0) {
                throw new IllegalArgumentException("'min' may not be greater than 'max'");
            }
            return bigInteger;
        }
        if (bigInteger.bitLength() > bigInteger2.bitLength() / 2) {
            return BigIntegers.createRandomInRange(ZERO, bigInteger2.subtract(bigInteger), secureRandom).add(bigInteger);
        }
        for (int i = 0; i < 1000; ++i) {
            BigInteger bigInteger3 = new BigInteger(bigInteger2.bitLength(), secureRandom);
            if (bigInteger3.compareTo(bigInteger) < 0 || bigInteger3.compareTo(bigInteger2) > 0) continue;
            return bigInteger3;
        }
        return new BigInteger(bigInteger2.subtract(bigInteger).bitLength() - 1, secureRandom).add(bigInteger);
    }

    public static BigInteger fromUnsignedByteArray(byte[] arrby) {
        return new BigInteger(1, arrby);
    }

    public static BigInteger fromUnsignedByteArray(byte[] arrby, int n, int n2) {
        byte[] arrby2 = arrby;
        if (n != 0 || n2 != arrby.length) {
            arrby2 = new byte[n2];
            System.arraycopy(arrby, n, arrby2, 0, n2);
        }
        return new BigInteger(1, arrby2);
    }
}

