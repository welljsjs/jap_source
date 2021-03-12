/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class ControlChannelCipher {
    GCMBlockCipher m_sentEngine = new GCMBlockCipher(new AESFastEngine());
    GCMBlockCipher m_recvEngine = new GCMBlockCipher(new AESFastEngine());
    long m_EncMsgCounter = 0L;
    long m_DecMsgCounter = 0L;
    byte[] m_sentKey;
    byte[] m_recvKey;

    public synchronized int setSentKey(byte[] arrby, int n, int n2) {
        try {
            this.m_sentKey = new byte[16];
            System.arraycopy(arrby, n, this.m_sentKey, 0, 16);
            this.m_EncMsgCounter = 0L;
            return 0;
        }
        catch (Exception exception) {
            return -1;
        }
    }

    public synchronized int setRecvKey(byte[] arrby, int n, int n2) {
        try {
            this.m_recvKey = new byte[16];
            System.arraycopy(arrby, n, this.m_recvKey, 0, 16);
            this.m_DecMsgCounter = 0L;
            return 0;
        }
        catch (Exception exception) {
            return -1;
        }
    }

    private byte[] createIV(long l) {
        byte[] arrby = new byte[12];
        for (int i = 0; i < 8; ++i) {
            arrby[i] = 0;
        }
        arrby[8] = (byte)(l >> 24 & 0xFFL);
        arrby[9] = (byte)(l >> 16 & 0xFFL);
        arrby[10] = (byte)(l >> 8 & 0xFFL);
        arrby[11] = (byte)(l & 0xFFL);
        return arrby;
    }

    public void encryptGCM1(byte[] arrby, int n, byte[] arrby2, int n2, int n3) throws Exception {
        byte[] arrby3 = this.createIV(this.m_EncMsgCounter);
        ++this.m_EncMsgCounter;
        this.m_sentEngine.init(true, new AEADParameters(new KeyParameter(this.m_sentKey), 128, arrby3, null));
        int n4 = this.m_sentEngine.processBytes(arrby, n, n3, arrby2, n2);
        this.m_sentEngine.doFinal(arrby2, n2 + n4);
    }

    public void decryptGCM2(byte[] arrby, int n, byte[] arrby2, int n2, int n3) throws Exception {
        byte[] arrby3 = this.createIV(this.m_DecMsgCounter);
        ++this.m_DecMsgCounter;
        this.m_recvEngine.init(false, new AEADParameters(new KeyParameter(this.m_recvKey), 128, arrby3, null));
        int n4 = this.m_recvEngine.processBytes(arrby, n, n3, arrby2, n2);
        this.m_recvEngine.doFinal(arrby2, n2 + n4);
    }

    public int getEncryptedOutputSize(int n) {
        return n + 16;
    }

    public int getDecryptedOutputSize(int n) {
        return n - 16;
    }
}

