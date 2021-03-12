/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.client.crypto.IMixCipher;
import java.util.Vector;

public class MixCipherChain {
    private IMixCipher[] m_cipherChain;

    public MixCipherChain(IMixCipher[] arriMixCipher) {
        this.m_cipherChain = arriMixCipher;
    }

    public byte[] encryptPacket(byte[] arrby, int n, Vector vector) {
        byte[] arrby2 = arrby;
        for (int i = this.m_cipherChain.length - 1; i >= 0; --i) {
            arrby2 = this.m_cipherChain[i].encrypt(arrby2, n, vector);
        }
        return arrby2;
    }

    public int getNextPacketEncryptionOverhead() {
        int n = 0;
        for (int i = this.m_cipherChain.length - 1; i >= 0; --i) {
            n += this.m_cipherChain[i].getNextPacketEncryptionOverhead();
        }
        return n;
    }

    public boolean decryptPacket(byte[] arrby) {
        boolean bl = true;
        for (int i = 0; i < this.m_cipherChain.length; ++i) {
            bl = bl && this.m_cipherChain[i].decrypt(arrby);
        }
        return bl;
    }
}

