/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractChannel;
import anon.client.AbstractDataChain;
import anon.client.DataChainSendOrderStructure;
import anon.client.InternalChannelMessageQueue;
import anon.client.MixPacket;
import anon.client.Multiplexer;
import anon.client.crypto.MixCipherChain;
import java.io.IOException;
import java.security.SecureRandom;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractDataChannel
extends AbstractChannel {
    private MixCipherChain m_mixCipherChain;
    private AbstractDataChain m_parentDataChain;
    private InternalChannelMessageQueue m_channelMessageQueue;
    private boolean m_bWithIntegrityCheck;
    private boolean m_bDebug = false;
    private static final SecureRandom ms_secureRandom = new SecureRandom();

    public AbstractDataChannel(int n, Multiplexer multiplexer, AbstractDataChain abstractDataChain, MixCipherChain mixCipherChain) {
        super(n, multiplexer);
        this.m_parentDataChain = abstractDataChain;
        this.m_mixCipherChain = mixCipherChain;
        this.m_channelMessageQueue = new InternalChannelMessageQueue();
        this.m_bWithIntegrityCheck = multiplexer.isProtocolWithIntegrityCheck();
        this.m_bDebug = multiplexer.isDebug();
    }

    public InternalChannelMessageQueue getChannelMessageQueue() {
        return this.m_channelMessageQueue;
    }

    public AbstractDataChain getDataChain() {
        return this.m_parentDataChain;
    }

    public void processReceivedPacket(MixPacket mixPacket) {
        if (!this.m_mixCipherChain.decryptPacket(mixPacket.getPayloadData())) {
            this.m_parentDataChain.closeDataChain();
            this.m_parentDataChain.propagateIntegrityError(-33);
        } else if ((mixPacket.getChannelFlags() & 0x10) == 16) {
            LogHolder.log(6, LogType.NET, "AbstractDataChannel: processReceivedPacket(): Catched an unexpected dummy-paket on channel '" + Integer.toString(mixPacket.getChannelId()) + "'.");
        } else {
            this.handleReceivedPacket(mixPacket);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNextPacketRecommandedOutputBlocksize() {
        int n = 0;
        MixCipherChain mixCipherChain = this.m_mixCipherChain;
        synchronized (mixCipherChain) {
            n = MixPacket.getPayloadSize() - this.m_mixCipherChain.getNextPacketEncryptionOverhead();
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void createAndSendMixPacket(DataChainSendOrderStructure dataChainSendOrderStructure, short s) {
        MixPacket mixPacket = this.createEmptyMixPacket();
        if (this.m_bDebug) {
            s = (short)(s | 0x20);
        }
        mixPacket.setChannelFlags(s);
        MixCipherChain mixCipherChain = this.m_mixCipherChain;
        synchronized (mixCipherChain) {
            byte[] arrby = new byte[mixPacket.getPayloadData().length - this.m_mixCipherChain.getNextPacketEncryptionOverhead()];
            ms_secureRandom.nextBytes(arrby);
            dataChainSendOrderStructure.setChannelCell(arrby);
            if (dataChainSendOrderStructure.getOrderData() != null) {
                this.m_parentDataChain.createPacketPayload(dataChainSendOrderStructure);
            } else if (this.m_bWithIntegrityCheck) {
                dataChainSendOrderStructure.getChannelCell()[0] = 0;
                dataChainSendOrderStructure.getChannelCell()[1] = 0;
            }
            byte[] arrby2 = this.m_mixCipherChain.encryptPacket(dataChainSendOrderStructure.getChannelCell(), mixPacket.getPayloadData().length, mixPacket.getSendCallbackHandlers());
            System.arraycopy(arrby2, 0, mixPacket.getPayloadData(), 0, arrby2.length);
            try {
                this.sendPacket(mixPacket);
            }
            catch (IOException iOException) {
                dataChainSendOrderStructure.setThrownException(iOException);
            }
        }
        dataChainSendOrderStructure.processingDone();
    }

    public abstract boolean processSendOrder(DataChainSendOrderStructure var1);

    public abstract void organizeChannelClose() throws IOException;

    protected abstract void handleReceivedPacket(MixPacket var1);
}

