/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.address.IAddress;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IChunkConnection;
import anon.transport.connection.IChunkReader;
import anon.transport.connection.IChunkWriter;
import anon.transport.connection.IStreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import logging.LogHolder;
import logging.LogType;

public class ChunkConnectionAdapter
implements IStreamConnection {
    private static final int StreamState_OPEN = 1;
    private static final int StreamState_EOF = 2;
    private static final int StreamState_CLOSE = 3;
    private static final byte DATA_PACKET = 0;
    private static final byte EOF_PACKET = -1;
    private IChunkConnection m_underliningConnection;
    private ChunkInputStream m_inputstream;
    private ChunkOutputStream m_outputstream;

    public ChunkConnectionAdapter(IChunkConnection iChunkConnection) {
        this.m_underliningConnection = iChunkConnection;
        this.m_inputstream = new ChunkInputStream(this.m_underliningConnection.getChunkReader());
        this.m_outputstream = new ChunkOutputStream(this.m_underliningConnection.getChunkWriter());
    }

    public InputStream getInputStream() {
        return this.m_inputstream;
    }

    public OutputStream getOutputStream() {
        return this.m_outputstream;
    }

    public int getCurrentState() {
        return this.m_underliningConnection.getCurrentState();
    }

    public IAddress getLocalAddress() {
        return this.m_underliningConnection.getLocalAddress();
    }

    public IAddress getRemoteAddress() {
        return this.m_underliningConnection.getRemoteAddress();
    }

    public int getTimeout() throws ConnectionException {
        return this.m_underliningConnection.getTimeout();
    }

    public void setTimeout(int n) throws ConnectionException {
        this.m_underliningConnection.setTimeout(n);
    }

    public void close() throws IOException {
        try {
            this.m_inputstream.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            this.m_outputstream.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.m_underliningConnection.close();
    }

    private static class ChunkOutputStream
    extends OutputStream {
        private static final int BUFFER_SIZE = 1000;
        private IChunkWriter m_writer;
        private byte[] m_buffer;
        private int m_writePos;
        private int m_state;

        public ChunkOutputStream(IChunkWriter iChunkWriter) {
            this.m_writer = iChunkWriter;
            this.m_buffer = new byte[1000];
            this.m_writePos = 0;
            this.m_state = 1;
        }

        public void write(int n) throws IOException {
            if (this.m_state == 3) {
                throw new IOException("Stream allready closed");
            }
            this.m_buffer[this.m_writePos++] = (byte)(n & 0xFF);
            if (this.m_writePos == this.m_buffer.length) {
                this.flush();
            }
        }

        public void flush() throws IOException {
            byte[] arrby = new byte[this.m_writePos + 1];
            arrby[0] = this.m_state == 2 ? -1 : 0;
            System.arraycopy(this.m_buffer, 0, arrby, 1, this.m_writePos);
            try {
                this.m_writer.writeChunk(arrby);
                LogHolder.log(7, LogType.FORWARDING, "Send a packet of " + this.m_writePos + " bytes");
            }
            catch (ConnectionException connectionException) {
                throw new IOException(connectionException.getMessage());
            }
            this.m_buffer = new byte[1000];
            this.m_writePos = 0;
            if (this.m_state == 2) {
                this.m_state = 3;
            }
        }

        public void close() throws IOException {
            if (this.m_state == 3) {
                throw new IOException("Stream already closed");
            }
            if (this.m_state == 2) {
                LogHolder.log(4, LogType.TRANSPORT, "Sync Warning. EOF State should be immediately transfert to CLOSE");
            }
            this.m_state = 2;
            this.flush();
            this.m_state = 3;
            this.m_writer.close();
        }
    }

    private static class ChunkInputStream
    extends InputStream {
        private IChunkReader m_reader;
        private byte[] m_buffer;
        private volatile int m_readPos;
        private int m_state;

        public ChunkInputStream(IChunkReader iChunkReader) {
            this.m_reader = iChunkReader;
            this.m_readPos = 0;
            this.m_buffer = new byte[0];
            this.m_state = 1;
        }

        public synchronized int read() throws IOException {
            if (this.m_state == 3) {
                throw new IOException("Stream is allready closed");
            }
            while (this.m_readPos == this.m_buffer.length) {
                if (this.m_state == 2) {
                    this.m_state = 3;
                    return -1;
                }
                LogHolder.log(7, LogType.TRANSPORT, "ChunkInputStream::read() -- We call updateBuffer() now - current m_readPos: " + this.m_readPos + " Current m_buffer.length: " + this.m_buffer.length);
                this.updateBuffer();
            }
            int n = this.m_buffer[this.m_readPos++] & 0xFF;
            return n;
        }

        private synchronized void updateBuffer() throws IOException {
            byte[] arrby;
            try {
                arrby = this.m_reader.readChunk();
            }
            catch (ConnectionException connectionException) {
                throw new IOException(connectionException.getMessage());
            }
            if (arrby == null) {
                throw new IOException("Wrong implementation of IChunkReader.readChunk().Should never return null.");
            }
            if (arrby.length == 0) {
                throw new IOException("Received Packet is to small");
            }
            if (arrby[0] == -1) {
                this.m_state = 2;
            }
            this.m_buffer = new byte[arrby.length - 1];
            System.arraycopy(arrby, 1, this.m_buffer, 0, this.m_buffer.length);
            LogHolder.log(7, LogType.TRANSPORT, "We readed " + this.m_buffer.length + " bytes");
            this.m_readPos = 0;
        }

        public synchronized int available() throws IOException {
            try {
                if (this.m_buffer.length == this.m_readPos && this.m_reader.availableChunks() > 0) {
                    this.updateBuffer();
                }
                return this.m_buffer.length - this.m_readPos;
            }
            catch (ConnectionException connectionException) {
                throw new IOException(connectionException.getMessage());
            }
        }

        public void close() throws IOException {
            this.m_state = 3;
            this.m_reader.close();
        }
    }
}

