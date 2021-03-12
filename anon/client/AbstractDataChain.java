/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.AnonChannel;
import anon.TooMuchDataForPacketException;
import anon.client.AbstractDataChannel;
import anon.client.DataChainErrorListener;
import anon.client.DataChainInputStreamQueueEntry;
import anon.client.DataChainSendOrderStructure;
import anon.client.IDataChannelCreator;
import anon.client.IntegrityErrorListener;
import anon.client.InternalChannelMessageQueue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public abstract class AbstractDataChain
implements AnonChannel,
Observer,
Runnable {
    private DataChainInputStreamImplementation m_inputStream;
    private DataChainOutputStreamImplementation m_outputStream;
    private Vector m_messageQueuesNotifications;
    private IDataChannelCreator m_channelCreator;
    private DataChainErrorListener m_errorListener;
    private boolean m_chainClosed;
    private Thread m_downstreamThread;
    private IntegrityErrorListener m_integrityErrorListener;

    public AbstractDataChain(IDataChannelCreator iDataChannelCreator, DataChainErrorListener dataChainErrorListener, IntegrityErrorListener integrityErrorListener) {
        this.m_channelCreator = iDataChannelCreator;
        this.m_errorListener = dataChainErrorListener;
        this.m_integrityErrorListener = integrityErrorListener;
        this.m_inputStream = new DataChainInputStreamImplementation();
        this.m_outputStream = new DataChainOutputStreamImplementation();
        this.m_messageQueuesNotifications = new Vector();
        this.m_chainClosed = false;
        this.m_downstreamThread = new Thread((Runnable)this, "AbstractDataChain: Downstream-Organizer Thread");
        this.m_downstreamThread.setDaemon(true);
        this.m_downstreamThread.start();
    }

    public InputStream getInputStream() {
        return this.m_inputStream;
    }

    public OutputStream getOutputStream() {
        return this.m_outputStream;
    }

    public boolean isClosed() {
        return this.m_chainClosed;
    }

    public void close() {
        if (!this.m_chainClosed) {
            this.m_chainClosed = true;
            try {
                this.getOutputStream().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                this.getInputStream().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.closeDataChain();
            try {
                this.m_downstreamThread.join();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        if (observable instanceof InternalChannelMessageQueue) {
            Vector vector = this.m_messageQueuesNotifications;
            synchronized (vector) {
                this.m_messageQueuesNotifications.addElement(observable);
                this.m_messageQueuesNotifications.notify();
            }
        }
    }

    protected Vector getMessageQueuesNotificationsList() {
        return this.m_messageQueuesNotifications;
    }

    protected void addInputStreamQueueEntry(DataChainInputStreamQueueEntry dataChainInputStreamQueueEntry) {
        this.m_inputStream.addToQueue(dataChainInputStreamQueueEntry);
    }

    protected AbstractDataChannel createDataChannel() {
        return this.m_channelCreator.createDataChannel(this);
    }

    protected void interruptDownstreamThread() {
        this.m_downstreamThread.interrupt();
    }

    protected void propagateConnectionError() {
        this.m_errorListener.dataChainErrorSignaled(null);
    }

    protected void propagateIntegrityError(int n) {
        this.m_integrityErrorListener.integrityErrorSignaled(n);
    }

    public abstract int getOutputBlockSize();

    public abstract void createPacketPayload(DataChainSendOrderStructure var1);

    public abstract void run();

    protected abstract void orderPacket(DataChainSendOrderStructure var1);

    protected abstract void outputStreamClosed() throws IOException;

    protected abstract void closeDataChain();

    private class DataChainInputStreamImplementation
    extends InputStream {
        private boolean m_closed = false;
        private Vector m_queueEntries = new Vector();

        private DataChainInputStreamImplementation() {
        }

        public int read() throws IOException {
            byte[] arrby = new byte[1];
            int n = 0;
            while ((n = this.read(arrby)) == 0) {
            }
            int n2 = -1;
            if (n == 1) {
                n2 = new ByteArrayInputStream(arrby).read();
            }
            return n2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int read(byte[] arrby, int n, int n2) throws IOException {
            int n3 = 0;
            if (arrby.length < n) {
                n = arrby.length;
            }
            if (arrby.length < n + n2) {
                n2 = arrby.length - n;
            }
            if (n2 > 0) {
                Vector vector = this.m_queueEntries;
                synchronized (vector) {
                    if (this.m_closed) {
                        throw new IOException("Stream is closed.");
                    }
                    if (this.m_queueEntries.size() == 0) {
                        try {
                            this.m_queueEntries.wait();
                        }
                        catch (InterruptedException interruptedException) {
                            throw new InterruptedIOException("InterruptedException: " + interruptedException.toString());
                        }
                    }
                    if (this.m_queueEntries.size() > 0) {
                        DataChainInputStreamQueueEntry dataChainInputStreamQueueEntry = (DataChainInputStreamQueueEntry)this.m_queueEntries.firstElement();
                        switch (dataChainInputStreamQueueEntry.getType()) {
                            case 2: {
                                n3 = -1;
                                break;
                            }
                            case 1: {
                                while (this.m_queueEntries.size() > 0 && dataChainInputStreamQueueEntry.getType() == 1 && n3 < n2) {
                                    int n4 = Math.min(n2 - n3, dataChainInputStreamQueueEntry.getData().length - dataChainInputStreamQueueEntry.getAlreadyReadBytes());
                                    System.arraycopy(dataChainInputStreamQueueEntry.getData(), dataChainInputStreamQueueEntry.getAlreadyReadBytes(), arrby, n + n3, n4);
                                    n3 += n4;
                                    dataChainInputStreamQueueEntry.setAlreadyReadBytes(dataChainInputStreamQueueEntry.getAlreadyReadBytes() + n4);
                                    if (dataChainInputStreamQueueEntry.getAlreadyReadBytes() != dataChainInputStreamQueueEntry.getData().length) continue;
                                    this.m_queueEntries.removeElementAt(0);
                                    if (this.m_queueEntries.size() <= 0) continue;
                                    dataChainInputStreamQueueEntry = (DataChainInputStreamQueueEntry)this.m_queueEntries.firstElement();
                                }
                                break;
                            }
                            case 3: {
                                IOException iOException = dataChainInputStreamQueueEntry.getIOException();
                                this.m_queueEntries.removeElementAt(0);
                                if (iOException == null) break;
                                throw iOException;
                            }
                        }
                    }
                }
            }
            return n3;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int available() throws IOException {
            int n = 0;
            Vector vector = this.m_queueEntries;
            synchronized (vector) {
                if (this.m_closed) {
                    throw new IOException("Stream is closed.");
                }
                if (this.m_queueEntries.size() > 0) {
                    int n2 = 0;
                    DataChainInputStreamQueueEntry dataChainInputStreamQueueEntry = (DataChainInputStreamQueueEntry)this.m_queueEntries.elementAt(n2);
                    while (dataChainInputStreamQueueEntry != null) {
                        ++n2;
                        if (dataChainInputStreamQueueEntry.getType() == 1) {
                            n += dataChainInputStreamQueueEntry.getData().length - dataChainInputStreamQueueEntry.getAlreadyReadBytes();
                            if (n2 < this.m_queueEntries.size()) {
                                dataChainInputStreamQueueEntry = (DataChainInputStreamQueueEntry)this.m_queueEntries.elementAt(n2);
                                continue;
                            }
                            dataChainInputStreamQueueEntry = null;
                            continue;
                        }
                        dataChainInputStreamQueueEntry = null;
                    }
                }
            }
            return n;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() {
            if (!this.m_closed) {
                Vector vector = this.m_queueEntries;
                synchronized (vector) {
                    this.m_closed = true;
                    this.m_queueEntries.removeAllElements();
                    this.m_queueEntries.notifyAll();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addToQueue(DataChainInputStreamQueueEntry dataChainInputStreamQueueEntry) {
            if (dataChainInputStreamQueueEntry.getType() == 1 && dataChainInputStreamQueueEntry.getDataLen() == 0) {
                return;
            }
            Vector vector = this.m_queueEntries;
            synchronized (vector) {
                DataChainInputStreamQueueEntry dataChainInputStreamQueueEntry2;
                boolean bl = true;
                if (this.m_closed) {
                    bl = false;
                } else if (this.m_queueEntries.size() > 0 && (dataChainInputStreamQueueEntry2 = (DataChainInputStreamQueueEntry)this.m_queueEntries.lastElement()).getType() == 2) {
                    bl = false;
                }
                if (bl) {
                    this.m_queueEntries.addElement(dataChainInputStreamQueueEntry);
                    this.m_queueEntries.notify();
                }
            }
        }
    }

    private class DataChainOutputStreamImplementation
    extends OutputStream {
        private boolean m_closed = false;
        private Object m_internalStreamSynchronization = new Object();

        public void write(int n) throws IOException {
            byte[] arrby = new byte[]{(byte)n};
            this.write(arrby);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void write(byte[] arrby, int n, int n2) throws IOException {
            Object object = this.m_internalStreamSynchronization;
            synchronized (object) {
                if (this.m_closed) {
                    throw new IOException("Stream is closed.");
                }
                byte[] arrby2 = new byte[n2];
                System.arraycopy(arrby, n, arrby2, 0, n2);
                DataChainSendOrderStructure dataChainSendOrderStructure = new DataChainSendOrderStructure(arrby2);
                Object object2 = dataChainSendOrderStructure.getSynchronizationObject();
                synchronized (object2) {
                    AbstractDataChain.this.orderPacket(dataChainSendOrderStructure);
                    if (!dataChainSendOrderStructure.isProcessingDone()) {
                        try {
                            dataChainSendOrderStructure.getSynchronizationObject().wait();
                        }
                        catch (InterruptedException interruptedException) {
                            throw new InterruptedIOException("InterruptedException: " + interruptedException.toString());
                        }
                    }
                    if (dataChainSendOrderStructure.getThrownException() != null) {
                        throw dataChainSendOrderStructure.getThrownException();
                    }
                    if (dataChainSendOrderStructure.getProcessedBytes() < n2) {
                        throw new TooMuchDataForPacketException(dataChainSendOrderStructure.getProcessedBytes());
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            if (!this.m_closed) {
                Object object = this.m_internalStreamSynchronization;
                synchronized (object) {
                    this.m_closed = true;
                    AbstractDataChain.this.outputStreamClosed();
                }
            }
        }
    }
}

