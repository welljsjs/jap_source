/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.ChannelClosedException;
import anon.client.MixPacket;
import anon.client.Multiplexer;
import java.io.IOException;

public abstract class AbstractChannel {
    private int m_channelId;
    protected Multiplexer m_parentMultiplexer;
    private volatile boolean m_channelOpen;
    private Object m_internalSynchronization;

    public AbstractChannel(int n, Multiplexer multiplexer) {
        this.m_channelId = n;
        this.m_parentMultiplexer = multiplexer;
        this.m_channelOpen = true;
        this.m_internalSynchronization = new Object();
    }

    public MixPacket createEmptyMixPacket() {
        return new MixPacket(this.m_channelId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPacket(MixPacket mixPacket) throws IOException {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (!this.m_channelOpen) {
                throw new ChannelClosedException("AbstractChannel: sendPacket(): The channel is already closed.");
            }
            this.m_parentMultiplexer.sendPacket(mixPacket);
        }
    }

    public boolean isClosed() {
        return !this.m_channelOpen;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteChannel() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (this.m_channelOpen) {
                this.m_parentMultiplexer.getChannelTable().removeChannel(this.m_channelId);
                this.m_channelOpen = false;
            }
        }
    }

    public void multiplexerClosed() {
    }

    public abstract void processReceivedPacket(MixPacket var1);
}

