/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.modes.gcm.Tables1kGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.Tables8kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMBlockCipher
implements AEADBlockCipher {
    private static final int BLOCK_SIZE = 16;
    private BlockCipher cipher;
    private GCMMultiplier multiplier;
    private GCMExponentiator exp;
    private boolean forEncryption;
    private int macSize;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private byte[] H;
    private byte[] J0;
    private byte[] bufBlock;
    private byte[] macBlock;
    private byte[] S;
    private byte[] S_at;
    private byte[] S_atPre;
    private byte[] counter;
    private int bufOff;
    private long totalLength;
    private byte[] atBlock;
    private int atBlockPos;
    private long atLength;
    private long atLengthPre;

    public GCMBlockCipher(BlockCipher blockCipher) {
        this(blockCipher, null);
    }

    public GCMBlockCipher(BlockCipher blockCipher, GCMMultiplier gCMMultiplier) {
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
        if (gCMMultiplier == null) {
            gCMMultiplier = new Tables8kGCMMultiplier();
        }
        this.cipher = blockCipher;
        this.multiplier = gCMMultiplier;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCM";
    }

    public void init(boolean bl, CipherParameters cipherParameters) throws IllegalArgumentException {
        KeyParameter keyParameter;
        CipherParameters cipherParameters2;
        this.forEncryption = bl;
        this.macBlock = null;
        if (cipherParameters instanceof AEADParameters) {
            cipherParameters2 = (AEADParameters)cipherParameters;
            this.nonce = ((AEADParameters)cipherParameters2).getNonce();
            this.initialAssociatedText = ((AEADParameters)cipherParameters2).getAssociatedText();
            int n = ((AEADParameters)cipherParameters2).getMacSize();
            if (n < 32 || n > 128 || n % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + n);
            }
            this.macSize = n / 8;
            keyParameter = ((AEADParameters)cipherParameters2).getKey();
        } else if (cipherParameters instanceof ParametersWithIV) {
            cipherParameters2 = (ParametersWithIV)cipherParameters;
            this.nonce = ((ParametersWithIV)cipherParameters2).getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters2).getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM");
        }
        int n = bl ? 16 : 16 + this.macSize;
        this.bufBlock = new byte[n];
        if (this.nonce == null || this.nonce.length < 1) {
            throw new IllegalArgumentException("IV must be at least 1 byte");
        }
        if (keyParameter != null) {
            this.cipher.init(true, keyParameter);
            this.H = new byte[16];
            this.cipher.processBlock(this.H, 0, this.H, 0);
            this.multiplier.init(this.H);
            this.exp = null;
        } else if (this.H == null) {
            throw new IllegalArgumentException("Key must be specified in initial init");
        }
        this.J0 = new byte[16];
        if (this.nonce.length == 12) {
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
        } else {
            this.gHASH(this.J0, this.nonce, this.nonce.length);
            byte[] arrby = new byte[16];
            Pack.longToBigEndian((long)this.nonce.length * 8L, arrby, 8);
            this.gHASHBlock(this.J0, arrby);
        }
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    public byte[] getMac() {
        return Arrays.clone(this.macBlock);
    }

    public int getOutputSize(int n) {
        int n2 = n + this.bufOff;
        if (this.forEncryption) {
            return n2 + this.macSize;
        }
        return n2 < this.macSize ? 0 : n2 - this.macSize;
    }

    public int getUpdateOutputSize(int n) {
        int n2 = n + this.bufOff;
        if (!this.forEncryption) {
            if (n2 < this.macSize) {
                return 0;
            }
            n2 -= this.macSize;
        }
        return n2 - n2 % 16;
    }

    public void processAADByte(byte by) {
        this.atBlock[this.atBlockPos] = by;
        if (++this.atBlockPos == 16) {
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atBlockPos = 0;
            this.atLength += 16L;
        }
    }

    public void processAADBytes(byte[] arrby, int n, int n2) {
        for (int i = 0; i < n2; ++i) {
            this.atBlock[this.atBlockPos] = arrby[n + i];
            if (++this.atBlockPos != 16) continue;
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atBlockPos = 0;
            this.atLength += 16L;
        }
    }

    private void initCipher() {
        if (this.atLength > 0L) {
            System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
            this.atLengthPre = this.atLength;
        }
        if (this.atBlockPos > 0) {
            this.gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
            this.atLengthPre += (long)this.atBlockPos;
        }
        if (this.atLengthPre > 0L) {
            System.arraycopy(this.S_atPre, 0, this.S, 0, 16);
        }
    }

    public int processByte(byte by, byte[] arrby, int n) throws DataLengthException {
        this.bufBlock[this.bufOff] = by;
        if (++this.bufOff == this.bufBlock.length) {
            this.outputBlock(arrby, n);
            return 16;
        }
        return 0;
    }

    public int processBytes(byte[] arrby, int n, int n2, byte[] arrby2, int n3) throws DataLengthException {
        if (arrby.length < n + n2) {
            throw new DataLengthException("Input buffer too short");
        }
        int n4 = 0;
        for (int i = 0; i < n2; ++i) {
            this.bufBlock[this.bufOff] = arrby[n + i];
            if (++this.bufOff != this.bufBlock.length) continue;
            this.outputBlock(arrby2, n3 + n4);
            n4 += 16;
        }
        return n4;
    }

    private void outputBlock(byte[] arrby, int n) {
        if (arrby.length < n + 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        this.gCTRBlock(this.bufBlock, arrby, n);
        if (this.forEncryption) {
            this.bufOff = 0;
        } else {
            System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
            this.bufOff = this.macSize;
        }
    }

    public int doFinal(byte[] arrby, int n) throws IllegalStateException, InvalidCipherTextException {
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        int n2 = this.bufOff;
        if (this.forEncryption) {
            if (arrby.length < n + n2 + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
        } else {
            if (n2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            if (arrby.length < n + (n2 -= this.macSize)) {
                throw new OutputLengthException("Output buffer too short");
            }
        }
        if (n2 > 0) {
            this.gCTRPartial(this.bufBlock, 0, n2, arrby, n);
        }
        this.atLength += (long)this.atBlockPos;
        if (this.atLength > this.atLengthPre) {
            if (this.atBlockPos > 0) {
                this.gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos);
            }
            if (this.atLengthPre > 0L) {
                GCMUtil.xor(this.S_at, this.S_atPre);
            }
            long l = this.totalLength * 8L + 127L >>> 7;
            byte[] arrby2 = new byte[16];
            if (this.exp == null) {
                this.exp = new Tables1kGCMExponentiator();
                this.exp.init(this.H);
            }
            this.exp.exponentiateX(l, arrby2);
            GCMUtil.multiply(this.S_at, arrby2);
            GCMUtil.xor(this.S, this.S_at);
        }
        byte[] arrby3 = new byte[16];
        Pack.longToBigEndian(this.atLength * 8L, arrby3, 0);
        Pack.longToBigEndian(this.totalLength * 8L, arrby3, 8);
        this.gHASHBlock(this.S, arrby3);
        byte[] arrby4 = new byte[16];
        this.cipher.processBlock(this.J0, 0, arrby4, 0);
        GCMUtil.xor(arrby4, this.S);
        int n3 = n2;
        this.macBlock = new byte[this.macSize];
        System.arraycopy(arrby4, 0, this.macBlock, 0, this.macSize);
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, arrby, n + this.bufOff, this.macSize);
            n3 += this.macSize;
        } else {
            byte[] arrby5 = new byte[this.macSize];
            System.arraycopy(this.bufBlock, n2, arrby5, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.macBlock, arrby5)) {
                throw new InvalidCipherTextException("mac check in GCM failed");
            }
        }
        this.reset(false);
        return n3;
    }

    public void reset() {
        this.reset(true);
    }

    private void reset(boolean bl) {
        this.cipher.reset();
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.bufBlock != null) {
            Arrays.fill(this.bufBlock, (byte)0);
        }
        if (bl) {
            this.macBlock = null;
        }
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void gCTRBlock(byte[] arrby, byte[] arrby2, int n) {
        byte[] arrby3 = this.getNextCounterBlock();
        GCMUtil.xor(arrby3, arrby);
        System.arraycopy(arrby3, 0, arrby2, n, 16);
        this.gHASHBlock(this.S, this.forEncryption ? arrby3 : arrby);
        this.totalLength += 16L;
    }

    private void gCTRPartial(byte[] arrby, int n, int n2, byte[] arrby2, int n3) {
        byte[] arrby3 = this.getNextCounterBlock();
        GCMUtil.xor(arrby3, arrby, n, n2);
        System.arraycopy(arrby3, 0, arrby2, n3, n2);
        this.gHASHPartial(this.S, this.forEncryption ? arrby3 : arrby, 0, n2);
        this.totalLength += (long)n2;
    }

    private void gHASH(byte[] arrby, byte[] arrby2, int n) {
        for (int i = 0; i < n; i += 16) {
            int n2 = Math.min(n - i, 16);
            this.gHASHPartial(arrby, arrby2, i, n2);
        }
    }

    private void gHASHBlock(byte[] arrby, byte[] arrby2) {
        GCMUtil.xor(arrby, arrby2);
        this.multiplier.multiplyH(arrby);
    }

    private void gHASHPartial(byte[] arrby, byte[] arrby2, int n, int n2) {
        GCMUtil.xor(arrby, arrby2, n, n2);
        this.multiplier.multiplyH(arrby);
    }

    private byte[] getNextCounterBlock() {
        for (int i = 15; i >= 12; --i) {
            byte by;
            this.counter[i] = by = (byte)(this.counter[i] + 1 & 0xFF);
            if (by != 0) break;
        }
        byte[] arrby = new byte[16];
        this.cipher.processBlock(this.counter, 0, arrby, 0);
        return arrby;
    }
}

