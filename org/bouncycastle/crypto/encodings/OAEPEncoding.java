/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class OAEPEncoding
implements AsymmetricBlockCipher {
    private byte[] defHash;
    private Digest mgf1Hash;
    private AsymmetricBlockCipher engine;
    private SecureRandom random;
    private boolean forEncryption;

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this(asymmetricBlockCipher, new SHA1Digest(), null);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest) {
        this(asymmetricBlockCipher, digest, null);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, byte[] arrby) {
        this(asymmetricBlockCipher, digest, digest, arrby);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, Digest digest2, byte[] arrby) {
        this.engine = asymmetricBlockCipher;
        this.mgf1Hash = digest2;
        this.defHash = new byte[digest.getDigestSize()];
        digest.reset();
        if (arrby != null) {
            digest.update(arrby, 0, arrby.length);
        }
        digest.doFinal(this.defHash, 0);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    public void init(boolean bl, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.random = parametersWithRandom.getRandom();
        } else {
            this.random = new SecureRandom();
        }
        this.engine.init(bl, cipherParameters);
        this.forEncryption = bl;
    }

    public int getInputBlockSize() {
        int n = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return n - 1 - 2 * this.defHash.length;
        }
        return n;
    }

    public int getOutputBlockSize() {
        int n = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return n;
        }
        return n - 1 - 2 * this.defHash.length;
    }

    public byte[] processBlock(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(arrby, n, n2);
        }
        return this.decodeBlock(arrby, n, n2);
    }

    public byte[] encodeBlock(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        int n3;
        byte[] arrby2 = new byte[this.getInputBlockSize() + 1 + 2 * this.defHash.length];
        System.arraycopy(arrby, n, arrby2, arrby2.length - n2, n2);
        arrby2[arrby2.length - n2 - 1] = 1;
        System.arraycopy(this.defHash, 0, arrby2, this.defHash.length, this.defHash.length);
        byte[] arrby3 = new byte[this.defHash.length];
        this.random.nextBytes(arrby3);
        byte[] arrby4 = this.maskGeneratorFunction1(arrby3, 0, arrby3.length, arrby2.length - this.defHash.length);
        for (n3 = this.defHash.length; n3 != arrby2.length; ++n3) {
            int n4 = n3;
            arrby2[n4] = (byte)(arrby2[n4] ^ arrby4[n3 - this.defHash.length]);
        }
        System.arraycopy(arrby3, 0, arrby2, 0, this.defHash.length);
        arrby4 = this.maskGeneratorFunction1(arrby2, this.defHash.length, arrby2.length - this.defHash.length, this.defHash.length);
        for (n3 = 0; n3 != this.defHash.length; ++n3) {
            int n5 = n3;
            arrby2[n5] = (byte)(arrby2[n5] ^ arrby4[n3]);
        }
        return this.engine.processBlock(arrby2, 0, arrby2.length);
    }

    public byte[] decodeBlock(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        int n3;
        int n4;
        byte[] arrby2;
        byte[] arrby3 = this.engine.processBlock(arrby, n, n2);
        if (arrby3.length < this.engine.getOutputBlockSize()) {
            arrby2 = new byte[this.engine.getOutputBlockSize()];
            System.arraycopy(arrby3, 0, arrby2, arrby2.length - arrby3.length, arrby3.length);
        } else {
            arrby2 = arrby3;
        }
        if (arrby2.length < 2 * this.defHash.length + 1) {
            throw new InvalidCipherTextException("data too short");
        }
        byte[] arrby4 = this.maskGeneratorFunction1(arrby2, this.defHash.length, arrby2.length - this.defHash.length, this.defHash.length);
        for (n4 = 0; n4 != this.defHash.length; ++n4) {
            int n5 = n4;
            arrby2[n5] = (byte)(arrby2[n5] ^ arrby4[n4]);
        }
        arrby4 = this.maskGeneratorFunction1(arrby2, 0, this.defHash.length, arrby2.length - this.defHash.length);
        for (n4 = this.defHash.length; n4 != arrby2.length; ++n4) {
            int n6 = n4;
            arrby2[n6] = (byte)(arrby2[n6] ^ arrby4[n4 - this.defHash.length]);
        }
        n4 = 0;
        for (n3 = 0; n3 != this.defHash.length; ++n3) {
            if (this.defHash[n3] == arrby2[this.defHash.length + n3]) continue;
            n4 = 1;
        }
        if (n4 != 0) {
            throw new InvalidCipherTextException("data hash wrong");
        }
        for (n3 = 2 * this.defHash.length; n3 != arrby2.length && arrby2[n3] == 0; ++n3) {
        }
        if (n3 >= arrby2.length - 1 || arrby2[n3] != 1) {
            throw new InvalidCipherTextException("data start wrong " + n3);
        }
        byte[] arrby5 = new byte[arrby2.length - ++n3];
        System.arraycopy(arrby2, n3, arrby5, 0, arrby5.length);
        return arrby5;
    }

    private void ItoOSP(int n, byte[] arrby) {
        arrby[0] = (byte)(n >>> 24);
        arrby[1] = (byte)(n >>> 16);
        arrby[2] = (byte)(n >>> 8);
        arrby[3] = (byte)(n >>> 0);
    }

    private byte[] maskGeneratorFunction1(byte[] arrby, int n, int n2, int n3) {
        int n4;
        byte[] arrby2 = new byte[n3];
        byte[] arrby3 = new byte[this.mgf1Hash.getDigestSize()];
        byte[] arrby4 = new byte[4];
        this.mgf1Hash.reset();
        for (n4 = 0; n4 < n3 / arrby3.length; ++n4) {
            this.ItoOSP(n4, arrby4);
            this.mgf1Hash.update(arrby, n, n2);
            this.mgf1Hash.update(arrby4, 0, arrby4.length);
            this.mgf1Hash.doFinal(arrby3, 0);
            System.arraycopy(arrby3, 0, arrby2, n4 * arrby3.length, arrby3.length);
        }
        if (n4 * arrby3.length < n3) {
            this.ItoOSP(n4, arrby4);
            this.mgf1Hash.update(arrby, n, n2);
            this.mgf1Hash.update(arrby4, 0, arrby4.length);
            this.mgf1Hash.doFinal(arrby3, 0);
            System.arraycopy(arrby3, 0, arrby2, n4 * arrby3.length, arrby2.length - n4 * arrby3.length);
        }
        return arrby2;
    }
}

