/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.AnonChannel;
import anon.TooMuchDataForPacketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TypeFilterDataChain
implements AnonChannel {
    private AnonChannel m_originChannel;
    private Object m_internalSynchronization;
    private boolean m_firstPacket;
    private OutputStream m_typeFilterOutputStream;

    public TypeFilterDataChain(AnonChannel anonChannel, int n) {
        this.m_originChannel = anonChannel;
        this.m_firstPacket = true;
        this.m_internalSynchronization = new Object();
        this.m_typeFilterOutputStream = new TypeFilterOutputStreamImplementation(anonChannel.getOutputStream(), n);
    }

    public InputStream getInputStream() {
        return this.m_originChannel.getInputStream();
    }

    public OutputStream getOutputStream() {
        return this.m_typeFilterOutputStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getOutputBlockSize() {
        int n = this.m_originChannel.getOutputBlockSize();
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (this.m_firstPacket && n > 0) {
                --n;
            }
        }
        return n;
    }

    public void close() {
        this.m_originChannel.close();
    }

    public boolean isClosed() {
        return this.m_originChannel.isClosed();
    }

    private class TypeFilterOutputStreamImplementation
    extends OutputStream {
        private int m_dataChainType;
        private OutputStream m_originOutputStream;

        public TypeFilterOutputStreamImplementation(OutputStream outputStream, int n) {
            this.m_originOutputStream = outputStream;
            this.m_dataChainType = n;
        }

        public void write(int n) throws IOException {
            byte[] arrby = new byte[]{(byte)n};
            this.write(arrby);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void write(byte[] arrby, int n, int n2) throws IOException {
            Object object = TypeFilterDataChain.this.m_internalSynchronization;
            synchronized (object) {
                byte[] arrby2 = arrby;
                if (TypeFilterDataChain.this.m_firstPacket) {
                    arrby2 = new byte[n2 + 1];
                    arrby2[0] = (byte)this.m_dataChainType;
                    System.arraycopy(arrby, n, arrby2, 1, n2);
                    n = 0;
                    n2 = arrby2.length;
                }
                try {
                    this.m_originOutputStream.write(arrby2, n, n2);
                    TypeFilterDataChain.this.m_firstPacket = false;
                }
                catch (TooMuchDataForPacketException tooMuchDataForPacketException) {
                    if (TypeFilterDataChain.this.m_firstPacket) {
                        if (tooMuchDataForPacketException.getBytesSent() > 0) {
                            TypeFilterDataChain.this.m_firstPacket = false;
                        }
                        throw new TooMuchDataForPacketException(Math.max(0, tooMuchDataForPacketException.getBytesSent() - 1));
                    }
                    throw tooMuchDataForPacketException;
                }
            }
        }

        public void flush() throws IOException {
            this.m_originOutputStream.flush();
        }

        public void close() throws IOException {
            this.m_originOutputStream.close();
        }
    }
}

