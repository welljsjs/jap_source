/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class KeyParameter
implements CipherParameters {
    private byte[] key;

    public KeyParameter(byte[] arrby) {
        this(arrby, 0, arrby.length);
    }

    public KeyParameter(byte[] arrby, int n, int n2) {
        this.key = new byte[n2];
        System.arraycopy(arrby, n, this.key, 0, n2);
    }

    public byte[] getKey() {
        return this.key;
    }
}

