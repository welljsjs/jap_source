/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.field;

import java.math.BigInteger;

public interface FiniteField {
    public BigInteger getCharacteristic();

    public int getDimension();
}

