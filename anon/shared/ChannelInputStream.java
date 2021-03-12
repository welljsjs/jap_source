/*
 * Decompiled with CFR 0.150.
 */
package anon.shared;

import anon.shared.AbstractChannel;
import anon.shared.IOQueue;
import java.io.IOException;
import java.io.InputStream;

final class ChannelInputStream
extends InputStream {
    private IOQueue m_Queue = new IOQueue();
    private boolean m_bIsClosed = false;

    ChannelInputStream(AbstractChannel abstractChannel) {
    }

    protected void recv(byte[] arrby, int n, int n2) throws IOException {
        try {
            this.m_Queue.write(arrby, n, n2);
        }
        catch (IOException iOException) {
            throw iOException;
        }
        catch (Exception exception) {
            throw new IOException(exception.getMessage());
        }
    }

    public synchronized int available() throws IOException {
        return this.m_Queue.available();
    }

    public int read() throws IOException {
        return this.m_Queue.read();
    }

    public int read(byte[] arrby) throws IOException {
        return this.m_Queue.read(arrby, 0, arrby.length);
    }

    public int read(byte[] arrby, int n, int n2) throws IOException {
        return this.m_Queue.read(arrby, n, n2);
    }

    protected void closedByPeer() {
        try {
            this.m_Queue.closeWrite();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public synchronized void close() throws IOException {
        if (!this.m_bIsClosed) {
            try {
                this.m_Queue.closeWrite();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.m_Queue.closeRead();
            this.m_Queue = null;
            this.m_bIsClosed = true;
        }
    }
}

