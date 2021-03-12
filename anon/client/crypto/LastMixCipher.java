/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.client.MixParameters;
import anon.client.crypto.DataChannelCipher;
import anon.client.crypto.IMixCipher;
import anon.util.Base64;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class LastMixCipher
implements IMixCipher {
    private boolean m_firstEncryptionPacket = true;
    private MixParameters m_mixParameters;
    private DataChannelCipher m_dataChannelCipher;
    private boolean m_bDebug = false;

    public LastMixCipher(MixParameters mixParameters, DataChannelCipher dataChannelCipher, boolean bl) {
        this.m_mixParameters = mixParameters;
        this.m_dataChannelCipher = dataChannelCipher;
        this.m_bDebug = bl;
    }

    public byte[] encrypt(byte[] arrby, int n, Vector vector) {
        int n2 = arrby[0] << 8 & 0x3F00 | arrby[1] & 0xFF;
        byte[] arrby2 = new byte[n2 + 3 + DataChannelCipher.getMACSize()];
        byte[] arrby3 = null;
        if (this.m_firstEncryptionPacket) {
            arrby3 = this.m_dataChannelCipher.getKeys();
            arrby3[0] = (byte)(arrby3[0] & 0x7F);
            this.m_dataChannelCipher.setEncryptionKeysAES(arrby3);
        }
        try {
            this.m_dataChannelCipher.encryptGCM1(arrby, 0, arrby2, 0, n2 + 3);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (!this.m_firstEncryptionPacket) {
            return arrby2;
        }
        byte[] arrby4 = new byte[Math.max(arrby2.length + arrby3.length + this.m_mixParameters.getMixCipher().getPaddingSize(), this.m_mixParameters.getMixCipher().getOutputBlockSize())];
        byte[] arrby5 = new byte[this.m_mixParameters.getMixCipher().getInputBlockSize()];
        int n3 = arrby5.length - arrby3.length;
        System.arraycopy(arrby3, 0, arrby5, 0, arrby3.length);
        System.arraycopy(arrby2, 0, arrby5, arrby3.length, Math.min(n3, arrby2.length));
        this.m_mixParameters.getMixCipher().encrypt(arrby5, 0, arrby4, 0);
        if (arrby2.length - n3 > 0) {
            System.arraycopy(arrby2, n3, arrby4, this.m_mixParameters.getMixCipher().getOutputBlockSize(), arrby2.length - n3);
        }
        this.m_firstEncryptionPacket = false;
        return arrby4;
    }

    public boolean decrypt(byte[] arrby) {
        try {
            if (this.m_bDebug) {
                LogHolder.log(7, LogType.CRYPTO, "LastMixDecryption - AN.ON Debug Mode Received Packet data: " + Base64.encode(arrby, false));
            }
            this.m_dataChannelCipher.decryptGCM2(arrby, 0, arrby, 0, arrby.length);
        }
        catch (Exception exception) {
            LogHolder.log(4, LogType.CRYPTO, "Decryption of MixPacket with integrity check failed: " + exception.getMessage());
            return false;
        }
        return true;
    }

    public int getNextPacketEncryptionOverhead() {
        int n = DataChannelCipher.getMACSize();
        if (this.m_firstEncryptionPacket) {
            n += this.m_dataChannelCipher.getKeys().length + this.m_mixParameters.getMixCipher().getPaddingSize();
        }
        return n;
    }
}

