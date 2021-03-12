/*
 * Decompiled with CFR 0.150.
 */
package anon.shared;

import anon.shared.AbstractChannel;
import java.io.IOException;
import java.io.OutputStream;

final class ChannelOutputStream
extends OutputStream {
    boolean m_bIsClosed = false;
    AbstractChannel m_channel = null;

    protected ChannelOutputStream(AbstractChannel abstractChannel) {
        this.m_channel = abstractChannel;
    }

    public void write(int n) throws IOException {
        if (this.m_bIsClosed) {
            throw new IOException("Channel closed by peer");
        }
        byte[] arrby = new byte[]{(byte)n};
        this.m_channel.send(arrby, 1);
    }

    public void write(byte[] arrby, int n, int n2) throws IOException {
        if (this.m_bIsClosed) {
            throw new IOException("Channel closed by peer");
        }
        this.m_channel.send(arrby, (short)n2);
    }

    public void close() {
        this.m_bIsClosed = true;
    }

    void closedByPeer() {
        this.m_bIsClosed = true;
    }
}

