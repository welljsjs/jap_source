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

public class UnlimitedDataChannel
extends AbstractDataChannel {
    private Object m_internalSynchronization = new Object();
    private boolean m_channelOpened = false;

    public UnlimitedDataChannel(int n, Multiplexer multiplexer, AbstractDataChain abstractDataChain, MixCipherChain mixCipherChain) {
        super(n, multiplexer, abstractDataChain, mixCipherChain);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void organizeChannelClose() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (this.m_channelOpened) {
                DataChainSendOrderStructure dataChainSendOrderStructure = new DataChainSendOrderStructure(null);
                this.createAndSendMixPacket(dataChainSendOrderStructure, (short)1);
                Object object2 = dataChainSendOrderStructure.getSynchronizationObject();
                synchronized (object2) {
                    if (!dataChainSendOrderStructure.isProcessingDone()) {
                        try {
                            dataChainSendOrderStructure.getSynchronizationObject().wait();
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                    }
                }
            }
            this.deleteChannel();
            this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(2, null));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean processSendOrder(DataChainSendOrderStructure dataChainSendOrderStructure) {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (!this.m_channelOpened) {
                this.createAndSendMixPacket(dataChainSendOrderStructure, (short)8);
                this.m_channelOpened = true;
            } else {
                this.createAndSendMixPacket(dataChainSendOrderStructure, (short)0);
            }
        }
        return true;
    }

    public void multiplexerClosed() {
        LogHolder.log(3, LogType.NET, "UnlimitedDataChannel: multiplexerClosed(): Multiplexer closed while channel was still active.");
        this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(3, null));
        this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(2, null));
    }

    protected void handleReceivedPacket(MixPacket mixPacket) {
        if ((mixPacket.getChannelFlags() & 1) == 1) {
            this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(2, mixPacket.getPayloadData()));
            this.deleteChannel();
        } else {
            this.getChannelMessageQueue().addChannelMessage(new InternalChannelMessage(1, mixPacket.getPayloadData()));
        }
    }
}

