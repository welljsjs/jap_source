/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.transport.connection.IStreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import logging.LogHolder;
import logging.LogType;

public final class SocketHandler
extends Observable {
    private IStreamConnection m_underlyingConnection;
    private SocketInputStreamImplementation m_socketInputStream;
    private SocketOutputStreamImplementation m_socketOutputStream;
    private Object m_internalSynchronization;

    public SocketHandler(IStreamConnection iStreamConnection) throws IOException {
        this.m_underlyingConnection = iStreamConnection;
        if (this.m_underlyingConnection.getCurrentState() == 2) {
            throw new IOException("Connection allready closed");
        }
        this.m_internalSynchronization = new Object();
        this.m_socketInputStream = new SocketInputStreamImplementation(this.m_underlyingConnection.getInputStream());
        this.m_socketOutputStream = new SocketOutputStreamImplementation(this.m_underlyingConnection.getOutputStream());
    }

    public void closeSocket() {
        try {
            this.m_underlyingConnection.close();
        }
        catch (IOException iOException) {
            LogHolder.log(3, LogType.NET, iOException);
        }
    }

    public InputStream getInputStream() {
        return this.m_socketInputStream;
    }

    public OutputStream getOutputStream() {
        return this.m_socketOutputStream;
    }

    public IStreamConnection getUnderlyingIStreamConnection() {
        return this.m_underlyingConnection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleIOException(IOException iOException) {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.setChanged();
            this.notifyObservers(iOException);
        }
    }

    private void handleEndOfInputStream() {
        this.handleIOException(new IOException("SocketHandler: handleEndOfInputStream(): Unexpected end of input stream."));
    }

    private void handleInputStreamClose() {
    }

    private void handleOutputStreamClose() {
    }

    private class SocketOutputStreamImplementation
    extends OutputStream {
        private OutputStream m_underlyingStream;

        public SocketOutputStreamImplementation(OutputStream outputStream) {
            this.m_underlyingStream = outputStream;
        }

        public void write(int n) throws IOException {
            try {
                this.m_underlyingStream.write(n);
            }
            catch (IOException iOException) {
                SocketHandler.this.handleIOException(iOException);
                throw iOException;
            }
        }

        public void write(byte[] arrby, int n, int n2) throws IOException {
            try {
                this.m_underlyingStream.write(arrby, n, n2);
            }
            catch (IOException iOException) {
                SocketHandler.this.handleIOException(iOException);
                throw iOException;
            }
        }

        public void flush() throws IOException {
            try {
                this.m_underlyingStream.flush();
            }
            catch (IOException iOException) {
                SocketHandler.this.handleIOException(iOException);
                throw iOException;
            }
        }

        public void close() {
            SocketHandler.this.handleOutputStreamClose();
        }
    }

    private class SocketInputStreamImplementation
    extends InputStream {
        private InputStream m_underlyingStream;

        public SocketInputStreamImplementation(InputStream inputStream) {
            this.m_underlyingStream = inputStream;
        }

        public int read() throws IOException {
            int n = -1;
            try {
                n = this.m_underlyingStream.read();
            }
            catch (IOException iOException) {
                SocketHandler.this.handleIOException(iOException);
                throw iOException;
            }
            if (n == -1) {
                SocketHandler.this.handleEndOfInputStream();
            }
            return n;
        }

        public int read(byte[] arrby, int n, int n2) throws IOException {
            int n3 = -1;
            try {
                n3 = this.m_underlyingStream.read(arrby, n, n2);
            }
            catch (IOException iOException) {
                SocketHandler.this.handleIOException(iOException);
                throw iOException;
            }
            if (n3 == -1) {
                SocketHandler.this.handleEndOfInputStream();
            }
            return n3;
        }

        public int available() throws IOException {
            int n = 0;
            try {
                n = this.m_underlyingStream.available();
            }
            catch (IOException iOException) {
                SocketHandler.this.handleIOException(iOException);
                throw iOException;
            }
            return n;
        }

        public void close() {
            SocketHandler.this.handleInputStreamClose();
        }
    }
}

