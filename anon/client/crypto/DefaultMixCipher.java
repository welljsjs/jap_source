/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.client.MixParameters;
import anon.client.crypto.IMixCipher;
import anon.client.crypto.SymCipher;
import anon.client.replay.ReplayTimestamp;
import java.util.Vector;

public class DefaultMixCipher
implements IMixCipher {
    private boolean m_firstEncryptionPacket = true;
    private MixParameters m_mixParameters;
    private SymCipher m_symCipher;

    public DefaultMixCipher(MixParameters mixParameters, SymCipher symCipher) {
        this.m_mixParameters = mixParameters;
        this.m_symCipher = symCipher;
    }

    public byte[] encrypt(byte[] arrby, int n, Vector vector) {
        byte[] arrby2;
        int n2 = 0;
        int n3 = 0;
        int n4 = arrby.length;
        byte[] arrby3 = null;
        byte[] arrby4 = null;
        if (this.m_firstEncryptionPacket) {
            ReplayTimestamp replayTimestamp;
            arrby3 = n > (n4 = n4 + this.m_symCipher.getKeys().length + this.m_mixParameters.getMixCipher().getPaddingSize()) ? new byte[n] : new byte[n4];
            arrby2 = this.m_symCipher.getKeys();
            arrby2[0] = (byte)(arrby2[0] & 0x7F);
            int n5 = (int)(System.currentTimeMillis() / 1000L - MixParameters.m_referenceTime);
            byte[] arrby5 = this.m_mixParameters.getCurrentReplayOffset(n5);
            if (arrby5 != null) {
                for (int i = 0; i < arrby5.length; ++i) {
                    arrby2[arrby2.length - arrby5.length + i] = arrby5[i];
                }
            }
            if ((replayTimestamp = this.m_mixParameters.getReplayTimestamp()) != null) {
                byte[] arrby6 = replayTimestamp.getCurrentTimestamp();
                System.arraycopy(arrby6, 0, arrby2, arrby2.length - arrby6.length, arrby6.length);
            }
            this.m_symCipher.setEncryptionKeysAES(arrby2);
            System.arraycopy(this.m_symCipher.getKeys(), 0, arrby3, 0, this.m_symCipher.getKeys().length);
            System.arraycopy(arrby, 0, arrby3, this.m_symCipher.getKeys().length, arrby.length);
            arrby4 = new byte[arrby3.length];
            this.m_mixParameters.getMixCipher().encrypt(arrby3, 0, arrby4, 0);
            n2 = this.m_mixParameters.getMixCipher().getInputBlockSize();
            n3 = this.m_mixParameters.getMixCipher().getOutputBlockSize();
            this.m_firstEncryptionPacket = false;
        } else {
            arrby3 = n > n4 ? new byte[n] : new byte[n4];
            System.arraycopy(arrby, 0, arrby3, 0, arrby.length);
            arrby4 = new byte[arrby3.length];
        }
        this.m_symCipher.encryptAES1(arrby3, n2, arrby4, n3, arrby3.length - n3);
        if (n4 < arrby4.length) {
            arrby2 = arrby4;
            arrby4 = new byte[n4];
            System.arraycopy(arrby2, 0, arrby4, 0, n4);
        }
        return arrby4;
    }

    public boolean decrypt(byte[] arrby) {
        this.m_symCipher.encryptAES2(arrby);
        return true;
    }

    public int getNextPacketEncryptionOverhead() {
        int n = 0;
        if (this.m_firstEncryptionPacket) {
            n = this.m_symCipher.getKeys().length + this.m_mixParameters.getMixCipher().getPaddingSize();
        }
        return n;
    }
}

