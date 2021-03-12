/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractDataChain;
import anon.client.AbstractDataChannel;
import anon.client.DataChainChannelListEntry;
import anon.client.DataChainErrorListener;
import anon.client.DataChainInputStreamQueueEntry;
import anon.client.DataChainSendOrderStructure;
import anon.client.IDataChannelCreator;
import anon.client.IntegrityErrorListener;
import anon.client.InternalChannelMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class SequentialChannelDataChain
extends AbstractDataChain {
    private static final int CHAIN_ID_LENGTH = 8;
    private static final short FLAG_UNKNOWN_CHAIN_ID = 8192;
    private static final short FLAG_CONNECTION_ERROR = -32768;
    private static final short FLAG_NEW_CHAIN = 8192;
    private static final short FLAG_FAST_RESPONSE = -32768;
    private static final short FLAG_STREAM_CLOSED = 16384;
    private Vector m_associatedChannels = new Vector();
    private boolean m_firstDownstreamPacket = true;
    private volatile byte[] m_chainId;
    private int m_maximumOutputBlocksize;
    private Object m_sendSynchronization = new Object();
    private volatile boolean m_chainClosed = false;
    private long m_chainTimeout;

    public SequentialChannelDataChain(IDataChannelCreator iDataChannelCreator, DataChainErrorListener dataChainErrorListener, IntegrityErrorListener integrityErrorListener, long l) {
        super(iDataChannelCreator, dataChainErrorListener, integrityErrorListener);
        this.m_chainTimeout = l;
        AbstractDataChannel abstractDataChannel = this.createDataChannel();
        int n = abstractDataChannel.getNextPacketRecommandedOutputBlocksize();
        try {
            abstractDataChannel.organizeChannelClose();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.m_maximumOutputBlocksize = n - 2 + 1;
    }

    public int getOutputBlockSize() {
        return this.m_maximumOutputBlocksize;
    }

    public void createPacketPayload(DataChainSendOrderStructure dataChainSendOrderStructure) {
        if (dataChainSendOrderStructure.getOrderData() != null) {
            SendOrderProtocolData sendOrderProtocolData = (SendOrderProtocolData)dataChainSendOrderStructure.getAdditionalProtocolData();
            int n = 0;
            boolean bl = false;
            if (sendOrderProtocolData.getChannelEntry().getProcessedUpstreamPackets() == 0 && this.m_chainId != null) {
                n = Math.min(dataChainSendOrderStructure.getOrderData().length, dataChainSendOrderStructure.getChannelCell().length - 2 - 8);
                bl = true;
                LogHolder.log(7, LogType.NET, "SequentialChannelDataChain: createPacketPayload(): Resuming existent chain.");
            } else {
                n = Math.min(dataChainSendOrderStructure.getOrderData().length, dataChainSendOrderStructure.getChannelCell().length - 2);
            }
            int n2 = n;
            if (dataChainSendOrderStructure.getOrderData().length > n || sendOrderProtocolData.enforceFastResponse()) {
                n2 |= 0xFFFF8000;
            }
            if (sendOrderProtocolData.sendUpstreamClose()) {
                n2 |= 0x4000;
                LogHolder.log(7, LogType.NET, "SequentialChannelDataChain: createPacketPayload(): Sending STREAM_CLOSE.");
            }
            if (sendOrderProtocolData.getChannelEntry().getProcessedUpstreamPackets() == 0 && !bl) {
                n2 |= 0x2000;
                LogHolder.log(7, LogType.NET, "SequentialChannelDataChain: createPacketPayload(): Sending NEW_CHAIN.");
            }
            sendOrderProtocolData.getChannelEntry().incProcessedUpstreamPackets();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(n2);
                dataOutputStream.flush();
                if (bl) {
                    byteArrayOutputStream.write(this.m_chainId);
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
        Object object;
        Object object2;
        Object object3;
        try {
            object3 = null;
            while (!Thread.interrupted()) {
                if (object3 == null) {
                    object2 = this.m_associatedChannels;
                    synchronized (object2) {
                        if (this.m_associatedChannels.size() == 0 && !this.m_firstDownstreamPacket) {
                            object = new Thread(new Runnable(){

                                public void run() {
                                    DataChainSendOrderStructure dataChainSendOrderStructure = new DataChainSendOrderStructure(new byte[0]);
                                    SequentialChannelDataChain.this.orderPacketInternal(dataChainSendOrderStructure, false, true);
                                }
                            }, "SequentialChannelDataChain: Datachain keep-alive thread");
                            ((Thread)object).setDaemon(true);
                            ((Thread)object).start();
                        }
                        while (this.m_associatedChannels.size() == 0) {
                            this.m_associatedChannels.wait();
                        }
                        object3 = (DataChainChannelListEntry)this.m_associatedChannels.firstElement();
                    }
                }
                object2 = ((DataChainChannelListEntry)object3).getChannel().getChannelMessageQueue().waitForNextMessage();
                ((DataChainChannelListEntry)object3).getChannel().getChannelMessageQueue().removeFirstMessage();
                switch (((InternalChannelMessage)object2).getMessageCode()) {
                    case 1: {
                        object = null;
                        try {
                            object = new ChainCell(((InternalChannelMessage)object2).getMessageData());
                        }
                        catch (InvalidChainCellException invalidChainCellException) {
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException(invalidChainCellException.toString())));
                            Thread.currentThread().interrupt();
                        }
                        if (object == null) break;
                        if (((ChainCell)object).getReceivedChainId() != null) {
                            this.m_chainId = ((ChainCell)object).getReceivedChainId();
                        }
                        if (((ChainCell)object).getPayloadData().length > 0) {
                            LogHolder.log(7, LogType.NET, "SequentialChannelDataChain: run(): Data received.");
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(1, ((ChainCell)object).getPayloadData()));
                        }
                        if (((ChainCell)object).isUnknownChainIdFlagSet()) {
                            LogHolder.log(3, LogType.NET, "SequentialChannelDataChain: run(): Last mix signaled unknown chain ID.");
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException("SequentialChannelDataChain: run(): Last mix signaled unknown chain ID.")));
                        }
                        if (((ChainCell)object).isDownstreamClosedFlagSet()) {
                            LogHolder.log(7, LogType.NET, "SequentialChannelDataChain: run(): Received downstream-close flag.");
                            Thread.currentThread().interrupt();
                            break;
                        }
                        Object object4 = object3;
                        synchronized (object4) {
                            ((DataChainChannelListEntry)object3).incProcessedDownstreamPackets();
                            object3.notify();
                            break;
                        }
                    }
                    case 2: {
                        object = null;
                        try {
                            if (((InternalChannelMessage)object2).getMessageData() != null && ((ChainCell)(object = new ChainCell(((InternalChannelMessage)object2).getMessageData()))).getPayloadData().length == 0 && ((ChainCell)object).isConnectionErrorFlagSet()) {
                                LogHolder.log(3, LogType.NET, "SequentialChannelDataChain: run(): Last mix signaled a connection-error.");
                                this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException("SequentialChannelDataChain: run(): Last mix signaled a connection-error.")));
                                this.propagateConnectionError();
                            }
                        }
                        catch (InvalidChainCellException invalidChainCellException) {
                            this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException(invalidChainCellException.toString())));
                        }
                        if (((DataChainChannelListEntry)object3).getProcessedDownstreamPackets() == 0) {
                            LogHolder.log(3, LogType.NET, "SequentialChannelDataChain: run(): Last mix sent CHANNEL_CLOSE immediately without data-packets.");
                            Thread.currentThread().interrupt();
                            break;
                        }
                        Object object4 = this.m_associatedChannels;
                        synchronized (object4) {
                            this.m_associatedChannels.removeElementAt(0);
                        }
                        object3 = null;
                        break;
                    }
                    case 3: {
                        this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(new IOException("SingleChannelDataChain: run(): Channel signaled an exception - closing chain.")));
                        object = object3;
                        synchronized (object) {
                            object3.notify();
                        }
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        this.m_chainClosed = true;
        this.addInputStreamQueueEntry(new DataChainInputStreamQueueEntry(2, null));
        object3 = this.m_associatedChannels;
        synchronized (object3) {
            while (this.m_associatedChannels.size() > 0) {
                object = object2 = (DataChainChannelListEntry)this.m_associatedChannels.firstElement();
                synchronized (object) {
                    object2.notify();
                }
                this.m_associatedChannels.removeElementAt(0);
            }
        }
    }

    protected void orderPacket(DataChainSendOrderStructure dataChainSendOrderStructure) {
        this.orderPacketInternal(dataChainSendOrderStructure, false, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void orderPacketInternal(DataChainSendOrderStructure dataChainSendOrderStructure, boolean bl, boolean bl2) {
        Object object = this.m_sendSynchronization;
        synchronized (object) {
            if (!this.m_chainClosed) {
                DataChainChannelListEntry dataChainChannelListEntry = null;
                Vector vector = this.m_associatedChannels;
                synchronized (vector) {
                    if (this.m_associatedChannels.size() > 0) {
                        dataChainChannelListEntry = (DataChainChannelListEntry)this.m_associatedChannels.lastElement();
                    }
                }
                boolean bl3 = false;
                if (dataChainChannelListEntry != null) {
                    dataChainSendOrderStructure.setAdditionalProtocolData(new SendOrderProtocolData(dataChainChannelListEntry, bl, bl2));
                    bl3 = dataChainChannelListEntry.getChannel().processSendOrder(dataChainSendOrderStructure);
                }
                if (!bl3) {
                    DataChainChannelListEntry dataChainChannelListEntry2;
                    if (dataChainChannelListEntry != null) {
                        dataChainChannelListEntry2 = dataChainChannelListEntry;
                        synchronized (dataChainChannelListEntry2) {
                            if (dataChainChannelListEntry.getProcessedDownstreamPackets() == 0) {
                                try {
                                    dataChainChannelListEntry.wait();
                                }
                                catch (InterruptedException interruptedException) {
                                    dataChainSendOrderStructure.setThrownException(new InterruptedIOException("SequentialChannelDataChain: orderPacketInternal(): Waiting for available channel was interrupted: " + interruptedException.toString()));
                                    dataChainSendOrderStructure.processingDone();
                                    return;
                                }
                                if (dataChainChannelListEntry.getProcessedDownstreamPackets() == 0) {
                                    dataChainSendOrderStructure.setThrownException(new IOException("SequentialChannelDataChain: orderPacketInternal(): Chain already closed."));
                                    dataChainSendOrderStructure.processingDone();
                                }
                            }
                        }
                    }
                    dataChainChannelListEntry2 = new DataChainChannelListEntry(this.createDataChannel());
                    Vector vector2 = this.m_associatedChannels;
                    synchronized (vector2) {
                        this.m_associatedChannels.addElement(dataChainChannelListEntry2);
                        this.m_associatedChannels.notifyAll();
                    }
                    dataChainSendOrderStructure.setAdditionalProtocolData(new SendOrderProtocolData(dataChainChannelListEntry2, bl, bl2));
                    dataChainChannelListEntry2.getChannel().processSendOrder(dataChainSendOrderStructure);
                }
            } else {
                dataChainSendOrderStructure.setThrownException(new IOException("SequentialChannelDataChain: orderPacketInternal(): Chain already closed."));
                dataChainSendOrderStructure.processingDone();
            }
        }
    }

    protected void outputStreamClosed() throws IOException {
        this.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeDataChain() {
        Object object = this.m_sendSynchronization;
        synchronized (object) {
            if (!this.m_chainClosed) {
                this.orderPacketInternal(new DataChainSendOrderStructure(new byte[0]), true, false);
                this.m_chainClosed = true;
                this.interruptDownstreamThread();
            }
        }
    }

    private class SendOrderProtocolData {
        private DataChainChannelListEntry m_channelEntry;
        private boolean m_sendUpstreamClose;
        private boolean m_enforceFastResponse;

        public SendOrderProtocolData(DataChainChannelListEntry dataChainChannelListEntry, boolean bl, boolean bl2) {
            this.m_channelEntry = dataChainChannelListEntry;
            this.m_sendUpstreamClose = bl;
            this.m_enforceFastResponse = bl2;
        }

        public DataChainChannelListEntry getChannelEntry() {
            return this.m_channelEntry;
        }

        public boolean sendUpstreamClose() {
            return this.m_sendUpstreamClose;
        }

        public boolean enforceFastResponse() {
            return this.m_enforceFastResponse;
        }
    }

    private class ChainCell {
        private static final short DATALENGTH_MASK = 1023;
        private byte[] m_payloadData;
        private byte[] m_receivedChainId;
        private boolean m_unknownChainIdFlagSet;
        private boolean m_connectionErrorFlagSet;
        private boolean m_downstreamClosedFlagSet;

        public ChainCell(byte[] arrby) throws InvalidChainCellException {
            if (arrby.length < 2) {
                throw new InvalidChainCellException("SequentialChannelDataChain: ChainCell: Constructor: Length of chaincell must be at least 2 bytes.");
            }
            int n = 0;
            try {
                DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(arrby, 0, 2));
                n = dataInputStream.readShort();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            short s = (short)(n & 0xFFFFFC00);
            this.m_unknownChainIdFlagSet = (s & 0x2000) == 8192;
            this.m_connectionErrorFlagSet = (s & 0xFFFF8000) == -32768;
            this.m_downstreamClosedFlagSet = (s & 0x4000) == 16384;
            int n2 = 2;
            int n3 = n & 0x3FF;
            if (SequentialChannelDataChain.this.m_firstDownstreamPacket) {
                if (arrby.length < n2 + 8 + n3) {
                    throw new InvalidChainCellException("SequentialChannelDataChain: ChainCell: Constructor: First downstream chaincell must contain Chain-ID.");
                }
                this.m_receivedChainId = new byte[8];
                System.arraycopy(arrby, n2, this.m_receivedChainId, 0, 8);
                n2 += 8;
                SequentialChannelDataChain.this.m_firstDownstreamPacket = false;
            } else {
                if (n2 + n3 > arrby.length) {
                    throw new InvalidChainCellException("SequentialChannelDataChain: ChainCell: Constructor: Chaincell has invalid length-field.");
                }
                this.m_receivedChainId = null;
            }
            this.m_payloadData = new byte[n3];
            System.arraycopy(arrby, n2, this.m_payloadData, 0, n3);
        }

        public byte[] getPayloadData() {
            return this.m_payloadData;
        }

        public byte[] getReceivedChainId() {
            return this.m_receivedChainId;
        }

        public boolean isUnknownChainIdFlagSet() {
            return this.m_unknownChainIdFlagSet;
        }

        public boolean isDownstreamClosedFlagSet() {
            return this.m_downstreamClosedFlagSet;
        }

        public boolean isConnectionErrorFlagSet() {
            return this.m_connectionErrorFlagSet;
        }
    }

    private class InvalidChainCellException
    extends Exception {
        public InvalidChainCellException(String string) {
            super(string);
        }
    }
}

