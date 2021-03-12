/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithIV
implements CipherParameters {
    private byte[] iv;
    private CipherParameters parameters;

    public ParametersWithIV(CipherParameters cipherParameters, byte[] arrby) {
        this(cipherParameters, arrby, 0, arrby.length);
    }

    public ParametersWithIV(CipherParameters cipherParameters, byte[] arrby, int n, int n2) {
        this.iv = new byte[n2];
        this.parameters = cipherParameters;
        System.arraycopy(arrby, n, this.iv, 0, n2);
    }

    public byte[] getIV() {
        return this.iv;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}

