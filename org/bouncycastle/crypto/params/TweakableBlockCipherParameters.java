/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class TweakableBlockCipherParameters
implements CipherParameters {
    private final byte[] tweak;
    private final KeyParameter key;

    public TweakableBlockCipherParameters(KeyParameter keyParameter, byte[] arrby) {
        this.key = keyParameter;
        this.tweak = Arrays.clone(arrby);
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public byte[] getTweak() {
        return this.tweak;
    }
}

