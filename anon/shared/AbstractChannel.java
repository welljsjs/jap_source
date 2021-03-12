/*
 * Decompiled with CFR 0.150.
 */
package anon.shared;

import anon.AnonChannel;
import anon.shared.ChannelInputStream;
import anon.shared.ChannelOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractChannel
implements AnonChannel {
    protected volatile boolean m_bIsClosedByPeer = false;
    protected volatile boolean m_bIsClosed = false;
    protected int m_id;
    private ChannelInputStream m_inputStream = new ChannelInputStream(this);
    private ChannelOutputStream m_outputStream = new ChannelOutputStream(this);

    public AbstractChannel(int n) {
        this();
        this.m_id = n;
    }

    public AbstractChannel() {
    }

    public void finalize() {
        this.close();
    }

    public int hashCode() {
        return this.m_id;
    }

    public InputStream getInputStream() {
        return this.m_inputStream;
    }

    public OutputStream getOutputStream() {
        return this.m_outputStream;
    }

    public boolean isClosed() {
        return this.m_bIsClosed;
    }

    public synchronized void close() {
        try {
            if (!this.m_bIsClosed) {
                this.m_outputStream.close();
                this.m_inputStream.close();
                if (!this.m_bIsClosedByPeer) {
                    this.close_impl();
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_bIsClosed = true;
    }

    protected abstract void close_impl();

    protected void recv(byte[] arrby, int n, int n2) throws IOException {
        this.m_inputStream.recv(arrby, n, n2);
    }

    protected abstract void send(byte[] var1, int var2) throws IOException;

    public void closedByPeer() {
        try {
            this.m_inputStream.closedByPeer();
            this.m_outputStream.closedByPeer();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_bIsClosedByPeer = true;
    }

    public abstract /* synthetic */ int getOutputBlockSize();
}

