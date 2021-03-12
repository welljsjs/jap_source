/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection.util;

import java.io.IOException;
import java.io.OutputStream;

public class ClosedOutputStream
extends OutputStream {
    private final boolean m_multibleClose;

    public static OutputStream getNotCloseable() {
        return Holder.neverCloseable;
    }

    public static OutputStream getMultibleCloseable() {
        return Holder.multibleCloseable;
    }

    private ClosedOutputStream() {
        this.m_multibleClose = false;
    }

    private ClosedOutputStream(boolean bl) {
        this.m_multibleClose = bl;
    }

    public void write(int n) throws IOException {
        throw new IOException("OutputStream is closed");
    }

    public void close() throws IOException {
        if (this.m_multibleClose) {
            return;
        }
        throw new IOException("InputStream allready closed");
    }

    private static class Holder {
        private static OutputStream neverCloseable = new ClosedOutputStream(false);
        private static OutputStream multibleCloseable = new ClosedOutputStream(true);

        private Holder() {
        }
    }
}

