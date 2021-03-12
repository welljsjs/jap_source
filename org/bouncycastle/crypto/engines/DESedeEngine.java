/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class DESedeEngine
extends DESEngine {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey1 = null;
    private int[] workingKey2 = null;
    private int[] workingKey3 = null;
    private boolean forEncryption;

    public void init(boolean bl, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DESede init - " + cipherParameters.getClass().getName());
        }
        byte[] arrby = ((KeyParameter)cipherParameters).getKey();
        if (arrby.length != 24 && arrby.length != 16) {
            throw new IllegalArgumentException("key size must be 16 or 24 bytes.");
        }
        this.forEncryption = bl;
        byte[] arrby2 = new byte[8];
        System.arraycopy(arrby, 0, arrby2, 0, arrby2.length);
        this.workingKey1 = this.generateWorkingKey(bl, arrby2);
        byte[] arrby3 = new byte[8];
        System.arraycopy(arrby, 8, arrby3, 0, arrby3.length);
        this.workingKey2 = this.generateWorkingKey(!bl, arrby3);
        if (arrby.length == 24) {
            byte[] arrby4 = new byte[8];
            System.arraycopy(arrby, 16, arrby4, 0, arrby4.length);
            this.workingKey3 = this.generateWorkingKey(bl, arrby4);
        } else {
            this.workingKey3 = this.workingKey1;
        }
    }

    public String getAlgorithmName() {
        return "DESede";
    }

    public int getBlockSize() {
        return 8;
    }

    public int processBlock(byte[] arrby, int n, byte[] arrby2, int n2) {
        if (this.workingKey1 == null) {
            throw new IllegalStateException("DESede engine not initialised");
        }
        if (n + 8 > arrby.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 8 > arrby2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        byte[] arrby3 = new byte[8];
        if (this.forEncryption) {
            this.desFunc(this.workingKey1, arrby, n, arrby3, 0);
            this.desFunc(this.workingKey2, arrby3, 0, arrby3, 0);
            this.desFunc(this.workingKey3, arrby3, 0, arrby2, n2);
        } else {
            this.desFunc(this.workingKey3, arrby, n, arrby3, 0);
            this.desFunc(this.workingKey2, arrby3, 0, arrby3, 0);
            this.desFunc(this.workingKey1, arrby3, 0, arrby2, n2);
        }
        return 8;
    }

    public void reset() {
    }
}

