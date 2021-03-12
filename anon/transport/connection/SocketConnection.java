/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.address.IAddress;
import anon.transport.address.TcpIpAddress;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IStreamConnection;
import anon.transport.connection.util.ClosedInputStream;
import anon.transport.connection.util.ClosedOutputStream;
import anon.util.SocketGuard;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public final class SocketConnection
implements IStreamConnection {
    private Socket m_underlyingSocket;
    private OutputStream m_outputStream;
    private InputStream m_inputStream;
    private int m_internalState;
    private IAddress m_localAddress;
    private IAddress m_remoteAddress;

    public SocketConnection(Socket socket) {
        this.m_localAddress = new TcpIpAddress(socket.getLocalAddress(), socket.getLocalPort());
        this.m_remoteAddress = new TcpIpAddress(socket.getInetAddress(), socket.getPort());
        try {
            this.m_underlyingSocket = socket;
            this.m_inputStream = this.m_underlyingSocket.getInputStream();
            this.m_outputStream = this.m_underlyingSocket.getOutputStream();
            this.m_internalState = 1;
        }
        catch (IOException iOException) {
            this.setCLOSE();
        }
    }

    private void setCLOSE() {
        this.m_internalState = 2;
        this.m_underlyingSocket = null;
        this.m_inputStream = ClosedInputStream.getNotCloseable();
        this.m_outputStream = ClosedOutputStream.getNotCloseable();
    }

    public InputStream getInputStream() {
        return this.m_inputStream;
    }

    public OutputStream getOutputStream() {
        return this.m_outputStream;
    }

    public synchronized int getTimeout() throws ConnectionException {
        if (this.m_internalState == 2) {
            throw new ConnectionException("Connection is already closed");
        }
        try {
            return this.m_underlyingSocket.getSoTimeout();
        }
        catch (SocketException socketException) {
            throw new ConnectionException(socketException);
        }
    }

    public synchronized void setTimeout(int n) throws ConnectionException {
        if (this.m_internalState == 2) {
            throw new ConnectionException("Connection is already closed");
        }
        try {
            this.m_underlyingSocket.setSoTimeout(n);
        }
        catch (SocketException socketException) {
            throw new ConnectionException(socketException);
        }
    }

    public synchronized void close() throws IOException {
        if (this.m_internalState == 1) {
            SocketGuard.close(this.m_underlyingSocket);
            this.setCLOSE();
        }
    }

    public int getCurrentState() {
        return this.m_internalState;
    }

    public IAddress getLocalAddress() {
        return this.m_localAddress;
    }

    public IAddress getRemoteAddress() {
        return this.m_remoteAddress;
    }

    public Socket getUnderlyingSocket() {
        return this.m_underlyingSocket;
    }
}

