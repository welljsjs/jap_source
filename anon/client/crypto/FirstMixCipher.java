/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.client.ISendCallbackHandler;
import anon.client.MixPacket;
import anon.client.crypto.IMixCipher;
import anon.client.crypto.SymCipher;
import java.util.Vector;

public class FirstMixCipher
implements IMixCipher {
    private boolean m_firstEncryptionPacket = true;
    private SymCipher m_mixCipher;
    private SymCipher m_channelSymCipher;

    public FirstMixCipher(SymCipher symCipher, SymCipher symCipher2) {
        this.m_mixCipher = symCipher;
        this.m_channelSymCipher = symCipher2;
    }

    public byte[] encrypt(byte[] arrby, int n, Vector vector) {
        int n2 = 0;
        int n3 = arrby.length;
        byte[] arrby2 = null;
        byte[] arrby3 = null;
        if (this.m_firstEncryptionPacket) {
            arrby2 = n > (n3 += this.m_channelSymCipher.getKeys().length) ? new byte[n] : new byte[n3];
            System.arraycopy(this.m_channelSymCipher.getKeys(), 0, arrby2, 0, this.m_channelSymCipher.getKeys().length);
            System.arraycopy(arrby, 0, arrby2, this.m_channelSymCipher.getKeys().length, arrby.length);
            arrby3 = new byte[arrby2.length];
            System.arraycopy(arrby2, 0, arrby3, 0, this.m_channelSymCipher.getKeys().length);
            n2 = this.m_channelSymCipher.getKeys().length;
            vector.addElement(new MixEncryptionHandler(this.m_mixCipher, n2));
            this.m_firstEncryptionPacket = false;
        } else {
            arrby2 = n > n3 ? new byte[n] : new byte[n3];
            System.arraycopy(arrby, 0, arrby2, 0, arrby.length);
            arrby3 = new byte[arrby2.length];
        }
        this.m_channelSymCipher.encryptAES1(arrby2, n2, arrby3, n2, arrby2.length - n2);
        if (n3 < arrby3.length) {
            byte[] arrby4 = arrby3;
            arrby3 = new byte[n3];
            System.arraycopy(arrby4, 0, arrby3, 0, n3);
        }
        return arrby3;
    }

    public boolean decrypt(byte[] arrby) {
        this.m_channelSymCipher.encryptAES2(arrby);
        return true;
    }

    public int getNextPacketEncryptionOverhead() {
        int n = 0;
        if (this.m_firstEncryptionPacket) {
            n = this.m_channelSymCipher.getKeys().length;
        }
        return n;
    }

    private class MixEncryptionHandler
    implements ISendCallbackHandler {
        private SymCipher m_mixStreamCipher;
        private int m_bytesToEncrypt;

        public MixEncryptionHandler(SymCipher symCipher, int n) {
            this.m_mixStreamCipher = symCipher;
            this.m_bytesToEncrypt = n;
        }

        public void finalizePacket(MixPacket mixPacket) {
            this.m_mixStreamCipher.encryptAES1(mixPacket.getPayloadData(), 0, mixPacket.getPayloadData(), 0, this.m_bytesToEncrypt);
        }
    }
}

