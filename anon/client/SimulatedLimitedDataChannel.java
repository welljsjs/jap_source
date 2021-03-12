/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractDataChain;
import anon.client.AbstractDataChannel;
import anon.client.DataChainSendOrderStructure;
import anon.client.InternalChannelMessage;
import anon.client.MixPacket;
import anon.client.Multiplexer;
import anon.client.crypto.MixCipherChain;
import logging.LogHolder;
import logging.LogType;

public class SimulatedLimitedDataChannel
extends AbstractDataChannel
implements Runnable {
    private Object m_internalSynchronization = new Object();
    private boolean m_channelOpened = false;
    private int m_downstreamPackets;
    private long m_channelTimeout;
    private int m_receivedPackets;
    Thread m_timeoutSupervisionThread;
    Object m_timeoutSynchronization;
    private volatile boolean m_channelClosed;

    public SimulatedLimitedDataChannel(int n, Multiplexer multiplexer, AbstractDataChain abstractDataChain, MixCipherChain mixCipherChain, int n2, long l) {
        super(n, multiplexer, abstractDataChain, mixCipherChain);
        this.m_downstreamPackets = n2;
        this.m_channelTimeout = l;
        this.m_receivedPackets = 0;
        this.m_timeoutSupervisionThread = null;
        this.m_timeoutSynchronization = new Object();
        this.m_channelClosed = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void organizeChannelClose() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (!this.m_channelOpened) {
                this.deleteChannel();
                this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(2, null));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean processSendOrder(DataChainSendOrderStructure dataChainSendOrderStructure) {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (!this.m_channelOpened) {
                Object object2 = this.m_timeoutSynchronization;
                synchronized (object2) {
                    this.m_timeoutSupervisionThread = new Thread((Runnable)this, "SimulatedLimitedDataChannel: Channel-timeout supervisor thread");
                    this.m_timeoutSupervisionThread.setDaemon(true);
                    this.m_timeoutSupervisionThread.start();
                }
                this.createAndSendMixPacket(dataChainSendOrderStructure, (short)8);
                this.m_channelOpened = true;
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void multiplexerClosed() {
        Object object = this.m_timeoutSynchronization;
        synchronized (object) {
            if (!this.m_channelClosed) {
                LogHolder.log(3, LogType.NET, "SimulatedLimitedDataChannel: multiplexerClosed(): Multiplexer closed before channel has received all packets.");
                this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(3, null));
                this.m_channelClosed = true;
                this.m_timeoutSynchronization.notify();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleReceivedPacket(MixPacket mixPacket) {
        ++this.m_receivedPackets;
        Object object = this.m_timeoutSynchronization;
        synchronized (object) {
            if (!this.m_channelClosed) {
                if ((mixPacket.getChannelFlags() & 1) == 1) {
                    if (this.m_receivedPackets < this.m_downstreamPackets) {
                        LogHolder.log(1, LogType.NET, "SimulatedLimitedDataChannel: handleReceivedPacket(): Some packets are missing on channel.");
                        this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(3, null));
                    }
                    this.m_channelClosed = true;
                    this.m_timeoutSynchronization.notify();
                } else if (this.m_receivedPackets >= this.m_downstreamPackets) {
                    LogHolder.log(1, LogType.NET, "SimulatedLimitedDataChannel: handleReceivedPacket(): More packets on channel received than allowed.");
                    this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(3, null));
                    this.m_channelClosed = true;
                    this.m_timeoutSynchronization.notify();
                } else {
                    this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(1, mixPacket.getPayloadData()));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        Object object = this.m_timeoutSynchronization;
        synchronized (object) {
            try {
                this.m_timeoutSynchronization.wait(this.m_channelTimeout);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (!this.m_channelClosed) {
                LogHolder.log(1, LogType.NET, "SimulatedLimitedDataChannel: run(): Channel-timeout occured.");
                this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(3, null));
            }
            this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(2, null));
        }
        this.deleteChannel();
    }
}

