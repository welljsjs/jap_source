/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.client.crypto.ASymMixCipherPlainRSA;

public class ASymMixCipherRSAOAEP
extends ASymMixCipherPlainRSA {
    public int encrypt(byte[] arrby, int n, byte[] arrby2, int n2) {
        byte[] arrby3 = null;
        try {
            arrby3 = this.m_RSA.processBlockOAEP(arrby, n, 86);
        }
        catch (Exception exception) {
            return -1;
        }
        if (arrby3.length == 128) {
            System.arraycopy(arrby3, 0, arrby2, n2, 128);
        } else if (arrby3.length == 129) {
            System.arraycopy(arrby3, 1, arrby2, n2, 128);
        } else {
            for (int i = 0; i < 128 - arrby3.length; ++i) {
                arrby2[n2 + i] = 0;
            }
            System.arraycopy(arrby3, 0, arrby2, n2 + 128 - arrby3.length, arrby3.length);
        }
        return 128;
    }

    public int decrypt(byte[] arrby, int n, byte[] arrby2, int n2) {
        byte[] arrby3 = null;
        try {
            arrby3 = this.m_RSA.processBlockOAEP(arrby, n, 128);
        }
        catch (Exception exception) {
            return -1;
        }
        System.arraycopy(arrby3, 0, arrby2, n2, arrby3.length);
        return arrby3.length;
    }

    public int getPaddingSize() {
        return 42;
    }

    public int getInputBlockSize() {
        return 86;
    }
}

