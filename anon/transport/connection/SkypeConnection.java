/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.address.IAddress;
import anon.transport.address.SkypeAddress;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IChunkConnection;
import anon.transport.connection.IChunkReader;
import anon.transport.connection.IChunkWriter;
import anon.transport.connection.UnsuportedCommandException;
import anon.transport.connection.util.QueuedChunkReader;
import anon.util.Base64;
import anon.util.ObjectQueue;
import com.skype.Application;
import com.skype.ApplicationListener;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.StreamListener;
import java.io.IOException;
import logging.LogHolder;
import logging.LogType;

public class SkypeConnection
implements IChunkConnection {
    public static final int IDLE_TIME_OUT = 480000;
    private final SkypeReader m_reader;
    private final SkypeWriter m_writer;
    private final SkypeAddress m_localAddress;
    private final SkypeAddress m_remoteAddress;
    private final Application m_application;
    private final Stream m_appStream;
    private int m_state;
    private ApplicationListener m_listner;

    public SkypeConnection(Stream stream) {
        if (stream == null) {
            throw new NullPointerException("No Application Stream provided");
        }
        this.m_appStream = stream;
        this.m_reader = new SkypeReader(this.m_appStream);
        this.m_writer = new SkypeWriter(this.m_appStream);
        this.m_application = this.m_appStream.getApplication();
        String string = this.m_application.getName();
        String string2 = this.m_appStream.getFriend().getId();
        String string3 = "<unresolved>";
        LogHolder.log(7, LogType.TRANSPORT, "Try to connect to " + string2 + " from " + string3);
        this.m_localAddress = new SkypeAddress(string3, string);
        this.m_remoteAddress = new SkypeAddress(string2, string);
        this.m_state = 1;
        this.m_listner = new ApplicationListener(){

            public void connected(Stream stream) throws SkypeException {
            }

            public void disconnected(Stream stream) throws SkypeException {
                if (stream.getId().equals(SkypeConnection.this.m_appStream.getId())) {
                    try {
                        SkypeConnection.this.close(false);
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
        };
        this.m_application.addApplicationListener(this.m_listner);
    }

    public IChunkReader getChunkReader() {
        return this.m_reader;
    }

    public IChunkWriter getChunkWriter() {
        return this.m_writer;
    }

    public int getCurrentState() {
        return this.m_state;
    }

    public IAddress getLocalAddress() {
        return this.m_localAddress;
    }

    public IAddress getRemoteAddress() {
        return this.m_remoteAddress;
    }

    public int getTimeout() throws ConnectionException {
        return 0;
    }

    public void setTimeout(int n) throws ConnectionException {
        throw new UnsuportedCommandException("Timeout could not be changed for Connection of Skype");
    }

    public void close(boolean bl) throws IOException {
        if (this.m_state != 2) {
            this.m_state = 2;
            this.m_application.removeApplicationListener(this.m_listner);
            this.m_reader.tearDown();
            this.m_writer.close();
            try {
                if (bl) {
                    this.m_appStream.disconnect();
                }
            }
            catch (SkypeException skypeException) {
                throw new IOException(skypeException.getMessage());
            }
        }
    }

    public void close() throws IOException {
        this.close(true);
    }

    private static class SkypeWriter
    implements IChunkWriter {
        private Stream m_appStream;
        private boolean m_isClosed;

        public SkypeWriter(Stream stream) {
            this.m_appStream = stream;
            this.m_isClosed = false;
        }

        public void writeChunk(byte[] arrby) throws ConnectionException {
            if (!this.m_isClosed) {
                String string = Base64.encode(arrby, false);
                try {
                    this.m_appStream.write(string);
                }
                catch (SkypeException skypeException) {
                    throw new ConnectionException(skypeException);
                }
            }
        }

        public void close() throws IOException {
            this.m_isClosed = true;
        }
    }

    private static class SkypeReader
    implements IChunkReader {
        public static final int MAX_MESSAGE_LENGTH = 65535;
        private ObjectQueue m_readBuffer;
        private Stream m_appStream;
        private StreamListener m_listner;
        private QueuedChunkReader m_baseReader;

        public SkypeReader(Stream stream) {
            this.m_appStream = stream;
            this.m_readBuffer = new ObjectQueue();
            this.m_baseReader = new QueuedChunkReader(this.m_readBuffer);
            this.m_listner = new StreamListener(){

                public void textReceived(String string) throws SkypeException {
                    byte[] arrby = Base64.decode(string);
                    if (arrby != null && arrby.length > 0) {
                        LogHolder.log(7, LogType.TRANSPORT, "Receveid text, push in the queue");
                        SkypeReader.this.m_readBuffer.push(arrby);
                    }
                }

                public void datagramReceived(String string) throws SkypeException {
                    LogHolder.log(4, LogType.TRANSPORT, "Received Datagram from Skype, but we only expect Streams.");
                }
            };
            this.m_appStream.addStreamListener(this.m_listner);
        }

        public int availableChunks() throws ConnectionException {
            return this.m_baseReader.availableChunks();
        }

        public byte[] readChunk() throws ConnectionException {
            byte[] arrby = this.m_baseReader.readChunk();
            return arrby;
        }

        public void close() throws IOException {
            this.m_appStream.removeStreamListener(this.m_listner);
            this.m_baseReader.close();
        }

        public void tearDown() throws IOException {
            this.m_appStream.removeStreamListener(this.m_listner);
            this.m_baseReader.tearDown();
        }
    }
}

