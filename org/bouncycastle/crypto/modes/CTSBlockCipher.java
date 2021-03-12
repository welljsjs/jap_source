/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;

public class CTSBlockCipher
extends BufferedBlockCipher {
    private int blockSize;

    public CTSBlockCipher(BlockCipher blockCipher) {
        if (blockCipher instanceof StreamBlockCipher) {
            throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
        }
        this.cipher = blockCipher;
        this.blockSize = blockCipher.getBlockSize();
        this.buf = new byte[this.blockSize * 2];
        this.bufOff = 0;
    }

    public int getUpdateOutputSize(int n) {
        int n2 = n + this.bufOff;
        int n3 = n2 % this.buf.length;
        if (n3 == 0) {
            return n2 - this.buf.length;
        }
        return n2 - n3;
    }

    public int getOutputSize(int n) {
        return n + this.bufOff;
    }

    public int processByte(byte by, byte[] arrby, int n) throws DataLengthException, IllegalStateException {
        int n2 = 0;
        if (this.bufOff == this.buf.length) {
            n2 = this.cipher.processBlock(this.buf, 0, arrby, n);
            System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
            this.bufOff = this.blockSize;
        }
        this.buf[this.bufOff++] = by;
        return n2;
    }

    public int processBytes(byte[] arrby, int n, int n2, byte[] arrby2, int n3) throws DataLengthException, IllegalStateException {
        if (n2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int n4 = this.getBlockSize();
        int n5 = this.getUpdateOutputSize(n2);
        if (n5 > 0 && n3 + n5 > arrby2.length) {
            throw new DataLengthException("output buffer too short");
        }
        int n6 = 0;
        int n7 = this.buf.length - this.bufOff;
        if (n2 > n7) {
            System.arraycopy(arrby, n, this.buf, this.bufOff, n7);
            n6 += this.cipher.processBlock(this.buf, 0, arrby2, n3);
            System.arraycopy(this.buf, n4, this.buf, 0, n4);
            this.bufOff = n4;
            n2 -= n7;
            n += n7;
            while (n2 > n4) {
                System.arraycopy(arrby, n, this.buf, this.bufOff, n4);
                n6 += this.cipher.processBlock(this.buf, 0, arrby2, n3 + n6);
                System.arraycopy(this.buf, n4, this.buf, 0, n4);
                n2 -= n4;
                n += n4;
            }
        }
        System.arraycopy(arrby, n, this.buf, this.bufOff, n2);
        this.bufOff += n2;
        return n6;
    }

    public int doFinal(byte[] arrby, int n) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        if (this.bufOff + n > arrby.length) {
            throw new DataLengthException("output buffer to small in doFinal");
        }
        int n2 = this.cipher.getBlockSize();
        int n3 = this.bufOff - n2;
        byte[] arrby2 = new byte[n2];
        if (this.forEncryption) {
            if (this.bufOff < n2) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            this.cipher.processBlock(this.buf, 0, arrby2, 0);
            if (this.bufOff > n2) {
                int n4;
                for (n4 = this.bufOff; n4 != this.buf.length; ++n4) {
                    this.buf[n4] = arrby2[n4 - n2];
                }
                for (n4 = n2; n4 != this.bufOff; ++n4) {
                    int n5 = n4;
                    this.buf[n5] = (byte)(this.buf[n5] ^ arrby2[n4 - n2]);
                }
                if (this.cipher instanceof CBCBlockCipher) {
                    BlockCipher blockCipher = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
                    blockCipher.processBlock(this.buf, n2, arrby, n);
                } else {
                    this.cipher.processBlock(this.buf, n2, arrby, n);
                }
                System.arraycopy(arrby2, 0, arrby, n + n2, n3);
            } else {
                System.arraycopy(arrby2, 0, arrby, n, n2);
            }
        } else {
            if (this.bufOff < n2) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            byte[] arrby3 = new byte[n2];
            if (this.bufOff > n2) {
                if (this.cipher instanceof CBCBlockCipher) {
                    BlockCipher blockCipher = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
                    blockCipher.processBlock(this.buf, 0, arrby2, 0);
                } else {
                    this.cipher.processBlock(this.buf, 0, arrby2, 0);
                }
                for (int i = n2; i != this.bufOff; ++i) {
                    arrby3[i - n2] = (byte)(arrby2[i - n2] ^ this.buf[i]);
                }
                System.arraycopy(this.buf, n2, arrby2, 0, n3);
                this.cipher.processBlock(arrby2, 0, arrby, n);
                System.arraycopy(arrby3, 0, arrby, n + n2, n3);
            } else {
                this.cipher.processBlock(this.buf, 0, arrby2, 0);
                System.arraycopy(arrby2, 0, arrby, n, n2);
            }
        }
        int n6 = this.bufOff;
        this.reset();
        return n6;
    }
}

