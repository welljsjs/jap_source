/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection.util;

import java.io.IOException;
import java.io.InputStream;

public class ClosedInputStream
extends InputStream {
    private final boolean m_multibleClose;

    public static InputStream getNotCloseable() {
        return Holder.neverCloseable;
    }

    public static InputStream getMultibleCloseable() {
        return Holder.multibleCloseable;
    }

    private ClosedInputStream() {
        this.m_multibleClose = false;
    }

    private ClosedInputStream(boolean bl) {
        this.m_multibleClose = bl;
    }

    public int read() throws IOException {
        throw new IOException("InputStream is closed");
    }

    public void close() throws IOException {
        if (this.m_multibleClose) {
            return;
        }
        throw new IOException("InputStream already closed");
    }

    private static class Holder {
        private static InputStream neverCloseable = new ClosedInputStream(false);
        private static InputStream multibleCloseable = new ClosedInputStream(true);

        private Holder() {
        }
    }
}

