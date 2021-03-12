/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ECEndomorphism;

public interface GLVEndomorphism
extends ECEndomorphism {
    public BigInteger[] decomposeScalar(BigInteger var1);
}

