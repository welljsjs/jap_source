/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.GF2Polynomial;
import org.bouncycastle.math.field.GenericPolynomialExtensionField;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.field.PrimeField;

public abstract class FiniteFields {
    static final FiniteField GF_2 = new PrimeField(BigInteger.valueOf(2L));
    static final FiniteField GF_3 = new PrimeField(BigInteger.valueOf(3L));

    public static PolynomialExtensionField getBinaryExtensionField(int[] arrn) {
        if (arrn[0] != 0) {
            throw new IllegalArgumentException("Irreducible polynomials in GF(2) must have constant term");
        }
        for (int i = 1; i < arrn.length; ++i) {
            if (arrn[i] > arrn[i - 1]) continue;
            throw new IllegalArgumentException("Polynomial exponents must be montonically increasing");
        }
        return new GenericPolynomialExtensionField(GF_2, new GF2Polynomial(arrn));
    }

    public static FiniteField getPrimeField(BigInteger bigInteger) {
        int n = bigInteger.bitLength();
        if (bigInteger.signum() <= 0 || n < 2) {
            throw new IllegalArgumentException("'characteristic' must be >= 2");
        }
        if (n < 3) {
            switch (bigInteger.intValue()) {
                case 2: {
                    return GF_2;
                }
                case 3: {
                    return GF_3;
                }
            }
        }
        return new PrimeField(bigInteger);
    }
}

