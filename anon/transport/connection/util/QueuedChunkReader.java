/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection.util;

import anon.transport.connection.ConnectionException;
import anon.transport.connection.IChunkReader;
import anon.util.ObjectQueue;
import java.io.IOException;
import java.util.Vector;

public class QueuedChunkReader
implements IChunkReader {
    private final ObjectQueue m_readingQueue;
    private volatile boolean m_isClosed;
    private final Vector m_waitingThreads;
    private int m_timeout;
    private boolean m_isTearDown = false;

    public QueuedChunkReader(ObjectQueue objectQueue, int n) {
        this.m_readingQueue = objectQueue;
        this.m_isClosed = false;
        this.m_waitingThreads = new Vector();
        this.m_timeout = n;
    }

    public QueuedChunkReader(ObjectQueue objectQueue) {
        this.m_readingQueue = objectQueue;
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

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public byte[] readChunk() throws ConnectionException {
        var1_1 = Thread.currentThread();
        try {
            try {
                this.m_waitingThreads.addElement(var1_1);
                if (this.m_isClosed) {
                    throw new ConnectionException("Reader already closed");
                }
                if (this.m_timeout > 0) {
                    var2_2 = (byte[])this.m_readingQueue.poll(this.m_timeout);
                    if (var2_2 == null) throw new ConnectionException("Timeout elapsed");
                    var3_5 = var2_2;
                    var5_6 = null;
                    var6_9 = this.m_waitingThreads.removeElement(var1_1);
                    if (this.m_isTearDown == false) return var3_5;
                    if (this.m_readingQueue.isEmpty() == false) return var3_5;
                    try {
                        this.close();
                        return var3_5;
                    }
                    catch (IOException var7_12) {
                        // empty catch block
                    }
                    return var3_5;
                }
                var2_3 = (byte[])this.m_readingQueue.take();
                var5_7 = null;
                var6_10 = this.m_waitingThreads.removeElement(var1_1);
                if (this.m_isTearDown == false) return var2_3;
                if (this.m_readingQueue.isEmpty() == false) return var2_3;
            }
            catch (InterruptedException var2_4) {
                throw new ConnectionException("Interrupted while reading. Probably closed Reader.");
            }
            ** try [egrp 2[TRYBLOCK] [5 : 142->149)] { 
lbl31:
            // 1 sources

            this.close();
            return var2_3;
lbl33:
            // 1 sources

            catch (IOException var7_13) {
                // empty catch block
            }
            return var2_3;
        }
        catch (Throwable var4_15) {
            var5_8 = null;
            var6_11 = this.m_waitingThreads.removeElement(var1_1);
            if (this.m_isTearDown == false) throw var4_15;
            if (this.m_readingQueue.isEmpty() == false) throw var4_15;
            try {}
            catch (IOException var7_14) {
                throw var4_15;
            }
            this.close();
            throw var4_15;
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
            this.m_readingQueue.close();
            this.m_isClosed = true;
        }
        object = this.m_waitingThreads.elements();
        while (object.hasMoreElements()) {
            ((Thread)object.nextElement()).interrupt();
        }
    }

    public void tearDown() throws IOException {
        this.m_isTearDown = true;
    }

    public int availableChunks() throws ConnectionException {
        return this.m_readingQueue.getSize();
    }
}

