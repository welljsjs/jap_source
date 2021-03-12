/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractDataChannel;

public class DataChainChannelListEntry {
    private AbstractDataChannel m_channel;
    private int m_processedDownstreamPackets;
    private Object m_internalSynchronization;
    private int m_processedUpstreamPackets;

    public DataChainChannelListEntry(AbstractDataChannel abstractDataChannel) {
        this.m_channel = abstractDataChannel;
        this.m_processedDownstreamPackets = 0;
        this.m_processedUpstreamPackets = 0;
        this.m_internalSynchronization = new Object();
    }

    public AbstractDataChannel getChannel() {
        return this.m_channel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getProcessedDownstreamPackets() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            return this.m_processedDownstreamPackets;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void incProcessedDownstreamPackets() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            ++this.m_processedDownstreamPackets;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getProcessedUpstreamPackets() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            return this.m_processedUpstreamPackets;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void incProcessedUpstreamPackets() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            ++this.m_processedUpstreamPackets;
        }
    }
}

