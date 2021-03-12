/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class PKCS1Encoding
implements AsymmetricBlockCipher {
    public static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.strict";
    public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.not_strict";
    private static final int HEADER_LENGTH = 10;
    private SecureRandom random;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private boolean useStrictLength;
    private int pLen = -1;
    private byte[] fallback = null;

    public PKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this.engine = asymmetricBlockCipher;
        this.useStrictLength = this.useStrict();
    }

    public PKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher, int n) {
        this.engine = asymmetricBlockCipher;
        this.useStrictLength = this.useStrict();
        this.pLen = n;
    }

    public PKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher, byte[] arrby) {
        this.engine = asymmetricBlockCipher;
        this.useStrictLength = this.useStrict();
        this.fallback = arrby;
        this.pLen = arrby.length;
    }

    private boolean useStrict() {
        String string = System.getProperty(STRICT_LENGTH_ENABLED_PROPERTY);
        String string2 = System.getProperty(NOT_STRICT_LENGTH_ENABLED_PROPERTY);
        if (string2 != null) {
            return !string2.equals("true");
        }
        return string == null || string.equals("true");
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    public void init(boolean bl, CipherParameters cipherParameters) {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.random = parametersWithRandom.getRandom();
            asymmetricKeyParameter = (AsymmetricKeyParameter)parametersWithRandom.getParameters();
        } else {
            this.random = new SecureRandom();
            asymmetricKeyParameter = (AsymmetricKeyParameter)cipherParameters;
        }
        this.engine.init(bl, cipherParameters);
        this.forPrivateKey = asymmetricKeyParameter.isPrivate();
        this.forEncryption = bl;
    }

    public int getInputBlockSize() {
        int n = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return n - 10;
        }
        return n;
    }

    public int getOutputBlockSize() {
        int n = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return n;
        }
        return n - 10;
    }

    public byte[] processBlock(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(arrby, n, n2);
        }
        return this.decodeBlock(arrby, n, n2);
    }

    private byte[] encodeBlock(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        if (n2 > this.getInputBlockSize()) {
            throw new IllegalArgumentException("input data too large");
        }
        byte[] arrby2 = new byte[this.engine.getInputBlockSize()];
        if (this.forPrivateKey) {
            arrby2[0] = 1;
            for (int i = 1; i != arrby2.length - n2 - 1; ++i) {
                arrby2[i] = -1;
            }
        } else {
            this.random.nextBytes(arrby2);
            arrby2[0] = 2;
            for (int i = 1; i != arrby2.length - n2 - 1; ++i) {
                while (arrby2[i] == 0) {
                    arrby2[i] = (byte)this.random.nextInt();
                }
            }
        }
        arrby2[arrby2.length - n2 - 1] = 0;
        System.arraycopy(arrby, n, arrby2, arrby2.length - n2, n2);
        return this.engine.processBlock(arrby2, 0, arrby2.length);
    }

    private static int checkPkcs1Encoding(byte[] arrby, int n) {
        int n2 = 0;
        n2 |= arrby[0] ^ 2;
        int n3 = arrby.length - (n + 1);
        for (int i = 1; i < n3; ++i) {
            int n4 = arrby[i];
            n4 |= n4 >> 1;
            n4 |= n4 >> 2;
            n4 |= n4 >> 4;
            n2 |= (n4 & 1) - 1;
        }
        n2 |= arrby[arrby.length - (n + 1)];
        n2 |= n2 >> 1;
        n2 |= n2 >> 2;
        n2 |= n2 >> 4;
        return ~((n2 & 1) - 1);
    }

    private byte[] decodeBlockOrRandom(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        if (!this.forPrivateKey) {
            throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing");
        }
        byte[] arrby2 = this.engine.processBlock(arrby, n, n2);
        byte[] arrby3 = null;
        if (this.fallback == null) {
            arrby3 = new byte[this.pLen];
            this.random.nextBytes(arrby3);
        } else {
            arrby3 = this.fallback;
        }
        if (arrby2.length < this.getOutputBlockSize()) {
            throw new InvalidCipherTextException("block truncated");
        }
        if (this.useStrictLength && arrby2.length != this.engine.getOutputBlockSize()) {
            throw new InvalidCipherTextException("block incorrect size");
        }
        int n3 = PKCS1Encoding.checkPkcs1Encoding(arrby2, this.pLen);
        byte[] arrby4 = new byte[this.pLen];
        for (int i = 0; i < this.pLen; ++i) {
            arrby4[i] = (byte)(arrby2[i + (arrby2.length - this.pLen)] & ~n3 | arrby3[i] & n3);
        }
        return arrby4;
    }

    private byte[] decodeBlock(byte[] arrby, int n, int n2) throws InvalidCipherTextException {
        byte by;
        int n3;
        if (this.pLen != -1) {
            return this.decodeBlockOrRandom(arrby, n, n2);
        }
        byte[] arrby2 = this.engine.processBlock(arrby, n, n2);
        if (arrby2.length < this.getOutputBlockSize()) {
            throw new InvalidCipherTextException("block truncated");
        }
        byte by2 = arrby2[0];
        if (this.forPrivateKey ? by2 != 2 : by2 != 1) {
            throw new InvalidCipherTextException("unknown block type");
        }
        if (this.useStrictLength && arrby2.length != this.engine.getOutputBlockSize()) {
            throw new InvalidCipherTextException("block incorrect size");
        }
        for (n3 = 1; n3 != arrby2.length && (by = arrby2[n3]) != 0; ++n3) {
            if (by2 != 1 || by == -1) continue;
            throw new InvalidCipherTextException("block padding incorrect");
        }
        if (++n3 > arrby2.length || n3 < 10) {
            throw new InvalidCipherTextException("no data in block");
        }
        byte[] arrby3 = new byte[arrby2.length - n3];
        System.arraycopy(arrby2, n3, arrby3, 0, arrby3.length);
        return arrby3;
    }
}

