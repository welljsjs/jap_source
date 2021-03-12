/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class CBCBlockCipher
implements BlockCipher {
    private byte[] IV;
    private byte[] cbcV;
    private byte[] cbcNextV;
    private int blockSize;
    private BlockCipher cipher = null;
    private boolean encrypting;

    public CBCBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
        this.blockSize = blockCipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.cbcV = new byte[this.blockSize];
        this.cbcNextV = new byte[this.blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public void init(boolean bl, CipherParameters cipherParameters) throws IllegalArgumentException {
        boolean bl2 = this.encrypting;
        this.encrypting = bl;
        if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            byte[] arrby = parametersWithIV.getIV();
            if (arrby.length != this.blockSize) {
                throw new IllegalArgumentException("initialisation vector must be the same length as block size");
            }
            System.arraycopy(arrby, 0, this.IV, 0, arrby.length);
            this.reset();
            if (parametersWithIV.getParameters() != null) {
                this.cipher.init(bl, parametersWithIV.getParameters());
            } else if (bl2 != bl) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            }
        } else {
            this.reset();
            if (cipherParameters != null) {
                this.cipher.init(bl, cipherParameters);
            } else if (bl2 != bl) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            }
        }
    }

    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    public int processBlock(byte[] arrby, int n, byte[] arrby2, int n2) throws DataLengthException, IllegalStateException {
        return this.encrypting ? this.encryptBlock(arrby, n, arrby2, n2) : this.decryptBlock(arrby, n, arrby2, n2);
    }

    public void reset() {
        System.arraycopy(this.IV, 0, this.cbcV, 0, this.IV.length);
        Arrays.fill(this.cbcNextV, (byte)0);
        this.cipher.reset();
    }

    private int encryptBlock(byte[] arrby, int n, byte[] arrby2, int n2) throws DataLengthException, IllegalStateException {
        int n3;
        if (n + this.blockSize > arrby.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (n3 = 0; n3 < this.blockSize; ++n3) {
            int n4 = n3;
            this.cbcV[n4] = (byte)(this.cbcV[n4] ^ arrby[n + n3]);
        }
        n3 = this.cipher.processBlock(this.cbcV, 0, arrby2, n2);
        System.arraycopy(arrby2, n2, this.cbcV, 0, this.cbcV.length);
        return n3;
    }

    private int decryptBlock(byte[] arrby, int n, byte[] arrby2, int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > arrby.length) {
            throw new DataLengthException("input buffer too short");
        }
        System.arraycopy(arrby, n, this.cbcNextV, 0, this.blockSize);
        int n3 = this.cipher.processBlock(arrby, n, arrby2, n2);
        for (int i = 0; i < this.blockSize; ++i) {
            int n4 = n2 + i;
            arrby2[n4] = (byte)(arrby2[n4] ^ this.cbcV[i]);
        }
        byte[] arrby3 = this.cbcV;
        this.cbcV = this.cbcNextV;
        this.cbcNextV = arrby3;
        return n3;
    }
}

