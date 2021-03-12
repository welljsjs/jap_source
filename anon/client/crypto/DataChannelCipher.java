/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public final class DataChannelCipher {
    private GCMBlockCipher m_sendEngine;
    private GCMBlockCipher m_recvEngine;
    private long m_EncMsgCounter = 0L;
    private long m_DecMsgCounter = 0L;
    private AEADParameters m_sendKey;
    private AEADParameters m_recvKey;
    private byte[] m_sendIV;
    private byte[] m_recvIV;
    private boolean m_bSingleKey = false;
    private byte[] m_decryptHelperBuff = new byte[32];

    public DataChannelCipher() {
        this.m_sendEngine = new GCMBlockCipher(new AESFastEngine());
        this.m_recvEngine = new GCMBlockCipher(new AESFastEngine());
        this.m_sendIV = new byte[12];
        this.m_recvIV = new byte[12];
    }

    private synchronized int setSendKey(byte[] arrby, int n, int n2) {
        try {
            this.m_sendKey = new AEADParameters(new KeyParameter(arrby, n, n2), 128, this.m_sendIV, null);
            this.m_EncMsgCounter = 0L;
            return 0;
        }
        catch (Exception exception) {
            return -1;
        }
    }

    private synchronized int setRecvKey(byte[] arrby, int n, int n2) {
        try {
            this.m_recvKey = new AEADParameters(new KeyParameter(arrby, n, n2), 128, this.m_recvIV, null);
            this.m_DecMsgCounter = 0L;
            return 0;
        }
        catch (Exception exception) {
            return -1;
        }
    }

    public synchronized int setEncryptionKeysAES(byte[] arrby) {
        return this.setEncryptionKeysAES(arrby, 0, arrby.length);
    }

    public synchronized int setEncryptionKeysAES(byte[] arrby, int n, int n2) {
        try {
            int n3;
            int n4;
            if (n2 == 16) {
                this.m_bSingleKey = true;
                n4 = this.setSendKey(arrby, n, 16);
                n3 = this.setRecvKey(arrby, n, 16);
            } else {
                this.m_bSingleKey = false;
                n4 = this.setSendKey(arrby, n, 16);
                n3 = this.setRecvKey(arrby, n + 16, 16);
            }
            if (n4 == 0 && n3 == 0) {
                return 0;
            }
            return -1;
        }
        catch (Exception exception) {
            return -1;
        }
    }

    public byte[] getKeys() {
        try {
            if (this.m_sendKey == null || this.m_recvKey == null) {
                return null;
            }
            if (this.m_bSingleKey) {
                return this.m_sendKey.getKey().getKey();
            }
            byte[] arrby = new byte[32];
            System.arraycopy(this.m_sendKey.getKey().getKey(), 0, arrby, 0, 16);
            System.arraycopy(this.m_recvKey.getKey().getKey(), 0, arrby, 16, 16);
            return arrby;
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static void createIV(byte[] arrby, long l) {
        arrby[8] = (byte)(l >> 24 & 0xFFL);
        arrby[9] = (byte)(l >> 16 & 0xFFL);
        arrby[10] = (byte)(l >> 8 & 0xFFL);
        arrby[11] = (byte)(l & 0xFFL);
    }

    public void encryptGCM1(byte[] arrby, int n, byte[] arrby2, int n2, int n3) throws Exception {
        DataChannelCipher.createIV(this.m_sendIV, this.m_EncMsgCounter);
        ++this.m_EncMsgCounter;
        this.m_sendEngine.init(true, this.m_sendKey);
        int n4 = this.m_sendEngine.processBytes(arrby, n, n3, arrby2, n2);
        this.m_sendEngine.doFinal(arrby2, n2 + n4);
    }

    public void decryptGCM2(byte[] arrby, int n, byte[] arrby2, int n2, int n3) throws Exception {
        DataChannelCipher.createIV(this.m_recvIV, this.m_DecMsgCounter);
        ++this.m_DecMsgCounter;
        this.m_recvEngine.init(false, this.m_recvKey);
        this.m_recvEngine.processBytes(arrby, n, 32, this.m_decryptHelperBuff, 0);
        int n4 = this.m_decryptHelperBuff[0] << 8 & 0x3F00 | this.m_decryptHelperBuff[1] & 0xFF;
        this.m_recvEngine.reset();
        int n5 = this.m_recvEngine.processBytes(arrby, n, n4 + 19, arrby2, n2);
        this.m_recvEngine.doFinal(arrby2, n2 + n5);
    }

    public static int getMACSize() {
        return 16;
    }
}

