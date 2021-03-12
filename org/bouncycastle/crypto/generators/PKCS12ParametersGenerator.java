/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PKCS12ParametersGenerator
extends PBEParametersGenerator {
    public static final int KEY_MATERIAL = 1;
    public static final int IV_MATERIAL = 2;
    public static final int MAC_MATERIAL = 3;
    private Digest digest;
    private int u;
    private int v;

    public PKCS12ParametersGenerator(Digest digest) {
        this.digest = digest;
        if (!(digest instanceof ExtendedDigest)) {
            throw new IllegalArgumentException("Digest " + digest.getAlgorithmName() + " unsupported");
        }
        this.u = digest.getDigestSize();
        this.v = ((ExtendedDigest)digest).getByteLength();
    }

    private void adjust(byte[] arrby, int n, byte[] arrby2) {
        int n2 = (arrby2[arrby2.length - 1] & 0xFF) + (arrby[n + arrby2.length - 1] & 0xFF) + 1;
        arrby[n + arrby2.length - 1] = (byte)n2;
        n2 >>>= 8;
        for (int i = arrby2.length - 2; i >= 0; --i) {
            arrby[n + i] = (byte)(n2 += (arrby2[i] & 0xFF) + (arrby[n + i] & 0xFF));
            n2 >>>= 8;
        }
    }

    private byte[] generateDerivedKey(int n, int n2) {
        byte[] arrby;
        byte[] arrby2;
        byte[] arrby3 = new byte[this.v];
        byte[] arrby4 = new byte[n2];
        for (int i = 0; i != arrby3.length; ++i) {
            arrby3[i] = (byte)n;
        }
        if (this.salt != null && this.salt.length != 0) {
            arrby2 = new byte[this.v * ((this.salt.length + this.v - 1) / this.v)];
            for (int i = 0; i != arrby2.length; ++i) {
                arrby2[i] = this.salt[i % this.salt.length];
            }
        } else {
            arrby2 = new byte[]{};
        }
        if (this.password != null && this.password.length != 0) {
            arrby = new byte[this.v * ((this.password.length + this.v - 1) / this.v)];
            for (int i = 0; i != arrby.length; ++i) {
                arrby[i] = this.password[i % this.password.length];
            }
        } else {
            arrby = new byte[]{};
        }
        byte[] arrby5 = new byte[arrby2.length + arrby.length];
        System.arraycopy(arrby2, 0, arrby5, 0, arrby2.length);
        System.arraycopy(arrby, 0, arrby5, arrby2.length, arrby.length);
        byte[] arrby6 = new byte[this.v];
        int n3 = (n2 + this.u - 1) / this.u;
        byte[] arrby7 = new byte[this.u];
        for (int i = 1; i <= n3; ++i) {
            int n4;
            this.digest.update(arrby3, 0, arrby3.length);
            this.digest.update(arrby5, 0, arrby5.length);
            this.digest.doFinal(arrby7, 0);
            for (n4 = 1; n4 < this.iterationCount; ++n4) {
                this.digest.update(arrby7, 0, arrby7.length);
                this.digest.doFinal(arrby7, 0);
            }
            for (n4 = 0; n4 != arrby6.length; ++n4) {
                arrby6[n4] = arrby7[n4 % arrby7.length];
            }
            for (n4 = 0; n4 != arrby5.length / this.v; ++n4) {
                this.adjust(arrby5, n4 * this.v, arrby6);
            }
            if (i == n3) {
                System.arraycopy(arrby7, 0, arrby4, (i - 1) * this.u, arrby4.length - (i - 1) * this.u);
                continue;
            }
            System.arraycopy(arrby7, 0, arrby4, (i - 1) * this.u, arrby7.length);
        }
        return arrby4;
    }

    public CipherParameters generateDerivedParameters(int n) {
        byte[] arrby = this.generateDerivedKey(1, n /= 8);
        return new KeyParameter(arrby, 0, n);
    }

    public CipherParameters generateDerivedParameters(int n, int n2) {
        byte[] arrby = this.generateDerivedKey(1, n /= 8);
        byte[] arrby2 = this.generateDerivedKey(2, n2 /= 8);
        return new ParametersWithIV(new KeyParameter(arrby, 0, n), arrby2, 0, n2);
    }

    public CipherParameters generateDerivedMacParameters(int n) {
        byte[] arrby = this.generateDerivedKey(3, n /= 8);
        return new KeyParameter(arrby, 0, n);
    }
}

