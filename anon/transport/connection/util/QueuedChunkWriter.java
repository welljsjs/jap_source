/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection.util;

import anon.transport.connection.ConnectionException;
import anon.transport.connection.IChunkWriter;
import anon.util.ObjectQueue;
import java.io.IOException;
import java.util.Vector;

public class QueuedChunkWriter
implements IChunkWriter {
    private final ObjectQueue m_writingQueue;
    private volatile boolean m_isClosed;
    private final Vector m_waitingThreads;
    private int m_timeout;

    public QueuedChunkWriter(ObjectQueue objectQueue, int n) {
        this.m_writingQueue = objectQueue;
        this.m_isClosed = false;
        this.m_waitingThreads = new Vector();
        this.m_timeout = n;
    }

    public QueuedChunkWriter(ObjectQueue objectQueue) {
        this.m_writingQueue = objectQueue;
        this.m_isClosed = false;
        this.m_waitingThreads = new Vector();
        this.m_timeout = 0;
    }

    public int getTimeout() {
        return this.m_timeout;
    }

    public void setTimeout(int n) {
        this.m_timeout = n;
    }

    public void writeChunk(byte[] arrby) throws ConnectionException {
        Thread thread = Thread.currentThread();
        try {
            try {
                this.m_waitingThreads.addElement(thread);
                if (this.m_isClosed) {
                    throw new ConnectionException("Writer already closed");
                }
                this.m_writingQueue.push(arrby);
            }
            catch (Exception exception) {
                throw new ConnectionException("Interrupted while writing. Probably closed Writer.");
            }
            Object var5_3 = null;
            boolean bl = this.m_waitingThreads.removeElement(thread);
        }
        catch (Throwable throwable) {
            Object var5_4 = null;
            boolean bl = this.m_waitingThreads.removeElement(thread);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        Object object = this;
        synchronized (object) {
            if (this.m_isClosed) {
                return;
            }
            this.m_isClosed = true;
        }
        object = this.m_waitingThreads.elements();
        while (object.hasMoreElements()) {
            ((Thread)object.nextElement()).interrupt();
        }
    }
}

