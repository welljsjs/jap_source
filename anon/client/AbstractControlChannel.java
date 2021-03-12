/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.IServiceContainer;
import anon.client.AbstractChannel;
import anon.client.BasicTrustModel;
import anon.client.ITrustModel;
import anon.client.MixPacket;
import anon.client.Multiplexer;
import java.io.IOException;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractControlChannel
extends AbstractChannel {
    private IServiceContainer m_serviceContainer;
    private boolean m_bSendingPacket = false;

    public AbstractControlChannel(int n, Multiplexer multiplexer, IServiceContainer iServiceContainer) {
        super(n, multiplexer);
        this.m_serviceContainer = iServiceContainer;
        if (this.m_serviceContainer == null) {
            this.m_serviceContainer = new IServiceContainer(){

                public void keepCurrentService(boolean bl) {
                }

                public boolean isServiceAutoSwitched() {
                    return false;
                }

                public boolean isReconnectedAutomatically() {
                    return false;
                }

                public ITrustModel getTrustModel() {
                    return new BasicTrustModel();
                }

                public void reset() {
                }
            };
        }
        multiplexer.getChannelTable().registerControlChannel(n, this);
    }

    public boolean isSending() {
        return this.m_bSendingPacket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int sendRawMessage(byte[] arrby) {
        try {
            int n;
            int n2 = arrby.length;
            this.m_bSendingPacket = true;
            do {
                MixPacket mixPacket = this.createEmptyMixPacket();
                n = Math.min(n2, mixPacket.getPayloadData().length);
                mixPacket.setChannelFlags((short)n);
                System.arraycopy(arrby, arrby.length - n2, mixPacket.getPayloadData(), 0, n);
                this.sendPacket(mixPacket);
            } while ((n2 -= n) > 0 && !Thread.currentThread().isInterrupted());
            int n3 = 0;
            Object var6_8 = null;
            this.m_bSendingPacket = false;
            return n3;
        }
        catch (IOException iOException) {
            try {
                int n = -1;
                Object var6_9 = null;
                this.m_bSendingPacket = false;
                return n;
            }
            catch (Throwable throwable) {
                Object var6_10 = null;
                this.m_bSendingPacket = false;
                throw throwable;
            }
        }
    }

    public void processReceivedPacket(MixPacket mixPacket) {
        short s = mixPacket.getChannelFlags();
        if (s > mixPacket.getPayloadData().length || s < 0) {
            LogHolder.log(3, LogType.NET, "AbstractControlChannel: processReceivedPacket(): Invalid packet length.");
        } else {
            byte[] arrby = new byte[s];
            System.arraycopy(mixPacket.getPayloadData(), 0, arrby, 0, s);
            this.processPacketData(arrby);
        }
    }

    protected final IServiceContainer getServiceContainer() {
        return this.m_serviceContainer;
    }

    protected abstract void processPacketData(byte[] var1);
}

