/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class AEADParameters
implements CipherParameters {
    private byte[] associatedText;
    private byte[] nonce;
    private KeyParameter key;
    private int macSize;

    public AEADParameters(KeyParameter keyParameter, int n, byte[] arrby) {
        this(keyParameter, n, arrby, null);
    }

    public AEADParameters(KeyParameter keyParameter, int n, byte[] arrby, byte[] arrby2) {
        this.key = keyParameter;
        this.nonce = arrby;
        this.macSize = n;
        this.associatedText = arrby2;
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public int getMacSize() {
        return this.macSize;
    }

    public byte[] getAssociatedText() {
        return this.associatedText;
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}

