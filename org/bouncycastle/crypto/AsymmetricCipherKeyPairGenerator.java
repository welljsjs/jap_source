/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;

public interface AsymmetricCipherKeyPairGenerator {
    public void init(KeyGenerationParameters var1);

    public AsymmetricCipherKeyPair generateKeyPair();
}

