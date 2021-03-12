/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

public class X9IntegerConverter {
    public int getByteLength(ECCurve eCCurve) {
        return (eCCurve.getFieldSize() + 7) / 8;
    }

    public int getByteLength(ECFieldElement eCFieldElement) {
        return (eCFieldElement.getFieldSize() + 7) / 8;
    }

    public byte[] integerToBytes(BigInteger bigInteger, int n) {
        byte[] arrby = bigInteger.toByteArray();
        if (n < arrby.length) {
            byte[] arrby2 = new byte[n];
            System.arraycopy(arrby, arrby.length - arrby2.length, arrby2, 0, arrby2.length);
            return arrby2;
        }
        if (n > arrby.length) {
            byte[] arrby3 = new byte[n];
            System.arraycopy(arrby, 0, arrby3, arrby3.length - arrby.length, arrby.length);
            return arrby3;
        }
        return arrby;
    }
}

