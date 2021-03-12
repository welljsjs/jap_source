/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class AsymmetricKeyParameter
implements CipherParameters {
    boolean privateKey;

    public AsymmetricKeyParameter(boolean bl) {
        this.privateKey = bl;
    }

    public boolean isPrivate() {
        return this.privateKey;
    }
}

