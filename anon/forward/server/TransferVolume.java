/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

public class TransferVolume {
    private int m_transferedBytes;
    private long m_timeStamp;

    public TransferVolume(int n) {
        this.m_transferedBytes = n;
        this.m_timeStamp = System.currentTimeMillis();
    }

    public int getVolume() {
        return this.m_transferedBytes;
    }

    public long getTimeStamp() {
        return this.m_timeStamp;
    }
}

