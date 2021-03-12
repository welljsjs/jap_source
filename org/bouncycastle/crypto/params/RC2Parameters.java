/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.KeyParameter;

public class RC2Parameters
extends KeyParameter {
    private int bits;

    public RC2Parameters(byte[] arrby) {
        this(arrby, arrby.length > 128 ? 1024 : arrby.length * 8);
    }

    public RC2Parameters(byte[] arrby, int n) {
        super(arrby);
        this.bits = n;
    }

    public int getEffectiveKeyBits() {
        return this.bits;
    }
}

