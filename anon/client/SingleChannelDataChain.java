/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractDataChain;
import anon.client.AbstractDataChannel;
import anon.client.DataChainErrorListener;
import anon.client.DataChainInputStreamQueueEntry;
import anon.client.DataChainSendOrderStructure;
import anon.client.IDataChannelCreator;
import anon.client.IntegrityErrorListener;
import anon.client.InternalChannelMessage;
import anon.client.InternalChannelMessageQueue;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class SingleChannelDataChain
extends AbstractDataChain {
    private static final int FLAG_FLOW_CONTROL = 32768;
    private static final int CLOSE_CELL_CONNECTION_ERROR = 1;
    private static final short FLAG_INTEGRITY_ERROR = 16384;
    private int m_chainType;
    private boolean m_supportFlowControl;
    private boolean m_supportUpstreamFlowControl;
    private AbstractDataChannel m_associatedChannel;
    private boolean m_firstUpstreamPacket;
    private int m_downstreamSendMeCount;
    private int m_upstreamSendMeCount;
    private int m_downstreamSendMeLimit;
    private int m_upstreamSendMeLimit;
    private Object m_oSyncUpstreamFlowControl;
    private boolean m_bEnhancedChannelEncryption;
    private boolean m_bWithIntegrityCheck;

    public SingleChannelDataChain(IDataChannelCreator iDataChannelCreator, DataChainErrorListener dataChainErrorListener, IntegrityErrorListener integrityErrorListener, int n, boolean bl, boolean bl2, int n2, int n3, boolean bl3, boolean bl4) {
        super(iDataChannelCreator, dataChainErrorListener, integrityErrorListener);
        this.m_chainType = n;
        this.m_supportFlowControl = bl;
        this.m_supportUpstreamFlowControl = bl2;
        this.m_bEnhancedChannelEncryption = bl3;
        this.m_bWithIntegrityCheck = bl4;
        this.m_associatedChannel = this.createDataChannel();
        this.m_associatedChannel.getChannelMessageQueue().addObserver(this);
        this.m_firstUpstreamPacket = true;
        this.m_downstreamSendMeCount = 0;
        this.m_downstreamSendMeLimit = n3;
        this.m_upstreamSendMeCount = 0;
        this.m_upstreamSendMeLimit = n2;
        this.m_oSyncUpstreamFlowControl = new Object();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getOutputBlockSize() {
        int n = 0;
        AbstractDataChannel abstractDataChannel = this.m_associatedChannel;
        synchronized (abstractDataChannel) {
            n = this.m_associatedChannel.getNextPacketRecommandedOutputBlocksize();
        }
        return Math.max(0, n - 3);
    }

    public void createPacketPayload(DataChainSendOrderStructure dataChainSendOrderStructure) {
        if (dataChainSendOrderStructure.getOrderData() != null) {
            int n;
            int n2 = n = Math.min(dataChainSendOrderStructure.getOrderData().length, dataChainSendOrderStructure.getChannelCell().length - 3);
            if (this.m_supportFlowControl && dataChainSendOrderStructure.getAdditionalProtocolData() instanceof Boolean && ((Boolean)dataChainSendOrderStructure.getAdditionalProtocolData()).booleanValue()) {
                n2 |= 0x8000;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(n2);
                dataOutputStream.flush();
                if (this.m_firstUpstreamPacket) {
                    byteArrayOutputStream.write(this.m_chainType);
                    this.m_firstUpstreamPacket = false;
                } else {
                    byteArrayOutputStream.write(0);
                }
                byteArrayOutputStream.write(dataChainSendOrderStructure.getOrderData(), 0, n);
                byteArrayOutputStream.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            System.arraycopy(byteArrayOutputStream.toByteArray(), 0, dataChainSendOrderStructure.getChannelCell(), 0, byteArrayOutputStream.toByteArray().length);
            dataChainSendOrderStructure.setProcessedBytes(n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        Vector vector = this.getMessageQueuesNotificationsList();
        try {
            while (!Thread.interrupted()) {
                InternalChannelMessage internalChannelMessage = null;
                InternalChannelMessageQueue internalChannelMessageQueue = null;
                Object object = vector;
                synchronized (object) {
                    while (vector.size() == 0) {
                        vector.wait();
                    }
                    internalChannelMessageQueue = (InternalChannelMessageQueue)vector.firstElement();
                    internalChannelMessage = internalChannelMessageQueue.getFirstMessage();
                    internalChannelMessageQueue.removeFirstMessage();
                    vector.removeElementAt(0);
                }
                switch (internalChannelMessage.getMessageCode()) {
                    case 1: {
                        try {
                            Object object2;
                            ++this.m_downstreamSendMeCount;
                            object = internalChannelMessage.getMessageData();
                            ChainCell chainCell = new ChainCell((byte[])object);
                            if (this.m_supportFlowControl && this.m_downstreamSendMeCount >= this.m_downstreamSendMeLimit) {
                                LogHolder.log(7, LogType.NET, "FlowControl: Will sent sendme - and download packet counter is: " + this.m_downstreamSendMeCount);
                                object2 = new DataChainSendOrderStructure(new byte[0]);
                                ((DataChainSendOrderStructure)object2).setAdditionalProtocolData(new Boolean(true));
                                this.orderPacket((DataChainSendOrderStructure)object2);
                                this.m_downstreamSendMeCount = 0;
                            }
                            if (this.m_supportUpstreamFlowControl && chainCell.isFlowControlFlagSet()) {
                                object2 = this.m_oSyncUpstreamFlowControl;
                                synchronized (object2) {
                                    LogHolder.log(7, LogType.NET, "got sendme - and upstream packet counter is: " + this.m_upstreamSendMeCount);
                                    this.m_upstreamSendMeCount = Math.max(0, this.m_upstreamSendMeCount - this.m_upstreamSendMeLimit);
                                    this.m_oSyncUpstreamFlowControl.notifyAll();
                                }
                            }
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(1, chainCell.getPayloadData()));
                        }
                        catch (InvalidChainCellException invalidChainCellException) {
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException(invalidChainCellException.toString())));
                        }
                        break;
                    }
                    case 2: {
                        this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(2, null));
                        try {
                            if (internalChannelMessage.getMessageData() != null) {
                                object = new ChainCell(internalChannelMessage.getMessageData());
                                if (((ChainCell)object).getPayloadLength() == 0 && ((ChainCell)object).getPayloadType() == 1) {
                                    this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException("SingleChannelDataChain: run(): Last mix signaled connection error.")));
                                    this.propagateConnectionError();
                                }
                                if (((ChainCell)object).isIntegrityErrorFlagSet() && this.m_bWithIntegrityCheck) {
                                    this.propagateIntegrityError(-34);
                                }
                            }
                        }
                        catch (InvalidChainCellException invalidChainCellException) {
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException(invalidChainCellException.toString())));
                        }
                        internalChannelMessageQueue.deleteObserver(this);
                        Thread.currentThread().interrupt();
                        break;
                    }
                    case 3: {
                        this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException("SingleChannelDataChain: run(): Channel signaled an exception - closing chain.")));
                    }
                }
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void orderPacket(DataChainSendOrderStructure dataChainSendOrderStructure) {
        Object object;
        if (!(!this.m_supportUpstreamFlowControl || dataChainSendOrderStructure.getAdditionalProtocolData() instanceof Boolean && ((Boolean)dataChainSendOrderStructure.getAdditionalProtocolData()).booleanValue())) {
            object = this.m_oSyncUpstreamFlowControl;
            synchronized (object) {
                while (this.m_upstreamSendMeCount > 2 * this.m_upstreamSendMeLimit && !this.isClosed()) {
                    try {
                        this.m_oSyncUpstreamFlowControl.wait();
                    }
                    catch (Exception exception) {}
                }
                ++this.m_upstreamSendMeCount;
            }
        }
        object = this.m_associatedChannel;
        synchronized (object) {
            this.m_associatedChannel.processSendOrder(dataChainSendOrderStructure);
        }
    }

    protected void outputStreamClosed() throws IOException {
        this.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeDataChain() {
        Object object = this.m_associatedChannel;
        synchronized (object) {
            try {
                this.m_associatedChannel.organizeChannelClose();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        object = this.m_oSyncUpstreamFlowControl;
        synchronized (object) {
            this.m_oSyncUpstreamFlowControl.notifyAll();
        }
    }

    private class ChainCell {
        private static final int DATALENGTH_MASK = 1023;
        private byte[] m_payloadData;
        private int m_payloadLen;
        private int m_payloadType;
        private boolean m_flowControlFlagSet;
        private boolean m_integrityErrorFlagSet;

        public ChainCell(byte[] arrby) throws InvalidChainCellException {
            if (arrby.length < 3) {
                throw new InvalidChainCellException("SingleChannelDataChain: ChainCell: Constructor: Length of ChainCell must be at least 3 bytes.");
            }
            int n = (arrby[0] << 8 | arrby[1] & 0xFF) & 0xFFFF;
            this.m_payloadType = arrby[2] & 0xFF;
            this.m_flowControlFlagSet = false;
            this.m_integrityErrorFlagSet = false;
            int n2 = n & 0xFFFFFC00;
            if (SingleChannelDataChain.this.m_supportFlowControl && (n2 & 0x8000) == 32768) {
                this.m_flowControlFlagSet = true;
            }
            if ((n2 & 0x4000) == 16384) {
                this.m_integrityErrorFlagSet = true;
            }
            this.m_payloadLen = n & 0x3FF;
            int n3 = 3;
            if (n3 + this.m_payloadLen > arrby.length) {
                throw new InvalidChainCellException("SingleChannelDataChain: ChainCell: Constructor: ChainCell has invalid length-field.");
            }
            this.m_payloadData = new byte[this.m_payloadLen];
            System.arraycopy(arrby, n3, this.m_payloadData, 0, this.m_payloadLen);
        }

        public byte[] getPayloadData() {
            return this.m_payloadData;
        }

        public int getPayloadType() {
            return this.m_payloadType;
        }

        public int getPayloadLength() {
            return this.m_payloadLen;
        }

        public boolean isFlowControlFlagSet() {
            return this.m_flowControlFlagSet;
        }

        public boolean isIntegrityErrorFlagSet() {
            return this.m_integrityErrorFlagSet;
        }
    }

    private class InvalidChainCellException
    extends Exception {
        public InvalidChainCellException(String string) {
            super(string);
        }
    }
}

