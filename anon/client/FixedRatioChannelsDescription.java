/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

public class FixedRatioChannelsDescription {
    private int m_downstreamPackets;
    private long m_channelTimeout;
    private long m_chainTimeout;

    public FixedRatioChannelsDescription(int n, long l, long l2) {
        this.m_downstreamPackets = n;
        this.m_channelTimeout = l;
        this.m_chainTimeout = l2;
    }

    public int getChannelDownstreamPackets() {
        return this.m_downstreamPackets;
    }

    public long getChannelTimeout() {
        return this.m_channelTimeout;
    }

    public long getChainTimeout() {
        return this.m_chainTimeout;
    }
}

