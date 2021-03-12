/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.crypto.IASymMixCipher;
import anon.client.replay.ReplayTimestamp;

public class MixParameters {
    private String m_mixId;
    private IASymMixCipher m_mixCipher;
    private ReplayTimestamp m_replayTimestamp;
    private int m_replayOffset;
    private Object m_internalSynchronization;
    public static long m_referenceTime;

    public MixParameters(String string, IASymMixCipher iASymMixCipher) {
        this.m_mixId = string;
        this.m_mixCipher = iASymMixCipher;
        this.m_replayTimestamp = null;
        this.m_internalSynchronization = new Object();
        this.m_replayOffset = 0;
    }

    public String getMixId() {
        return this.m_mixId;
    }

    public IASymMixCipher getMixCipher() {
        return this.m_mixCipher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ReplayTimestamp getReplayTimestamp() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            return this.m_replayTimestamp;
        }
    }

    public byte[] getReplayOffset() {
        byte[] arrby = new byte[3];
        this.m_replayOffset &= 0xFFFFFF;
        arrby[0] = (byte)(this.m_replayOffset >> 16);
        arrby[1] = (byte)(this.m_replayOffset >> 8 & 0xFF);
        arrby[2] = (byte)(this.m_replayOffset & 0xFF);
        return arrby;
    }

    public byte[] getCurrentReplayOffset(int n) {
        if (this.m_replayOffset == 0) {
            return null;
        }
        byte[] arrby = new byte[3];
        int n2 = this.m_replayOffset + n & 0xFFFFFF;
        arrby[0] = (byte)(n2 >> 16);
        arrby[1] = (byte)(n2 >> 8 & 0xFF);
        arrby[2] = (byte)(n2 & 0xFF);
        return arrby;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReplayTimestamp(ReplayTimestamp replayTimestamp) {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.m_replayTimestamp = replayTimestamp;
        }
    }

    public void setReplayOffset(int n) {
        this.m_replayOffset = n & 0xFFFFFF;
    }
}

