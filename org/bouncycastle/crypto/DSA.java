/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public interface DSA {
    public void init(boolean var1, CipherParameters var2);

    public BigInteger[] generateSignature(byte[] var1);

    public boolean verifySignature(byte[] var1, BigInteger var2, BigInteger var3);
}

