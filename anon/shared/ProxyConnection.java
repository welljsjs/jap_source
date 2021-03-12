/*
 * Decompiled with CFR 0.150.
 */
package anon.shared;

import anon.transport.address.IAddress;
import anon.transport.address.TcpIpAddress;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IStreamConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public final class ProxyConnection
implements IStreamConnection {
    private Socket m_ioSocket;
    private InputStream m_In;
    private OutputStream m_Out;
    private int m_State;

    public ProxyConnection(Socket socket) throws Exception {
        this.m_ioSocket = socket;
        this.m_State = 1;
        try {
            this.m_ioSocket.setSoTimeout(0);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.m_In = this.m_ioSocket.getInputStream();
            this.m_Out = this.m_ioSocket.getOutputStream();
        }
        catch (Exception exception) {
            this.close();
            throw exception;
        }
    }

    public Socket getSocket() {
        return this.m_ioSocket;
    }

    public InputStream getInputStream() {
        return this.m_In;
    }

    public OutputStream getOutputStream() {
        return this.m_Out;
    }

    public void setSoTimeout(int n) throws SocketException {
        this.m_ioSocket.setSoTimeout(n);
    }

    public void close() {
        try {
            this.m_In.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.m_Out.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.m_ioSocket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_State = 2;
    }

    public int getCurrentState() {
        return this.m_State;
    }

    public int getTimeout() throws ConnectionException {
        try {
            return this.m_ioSocket.getSoTimeout();
        }
        catch (SocketException socketException) {
            throw new ConnectionException(socketException);
        }
    }

    public void setTimeout(int n) throws ConnectionException {
        try {
            this.m_ioSocket.setSoTimeout(n);
        }
        catch (SocketException socketException) {
            throw new ConnectionException(socketException);
        }
    }

    public IAddress getLocalAddress() {
        return new TcpIpAddress(this.m_ioSocket.getLocalAddress(), this.m_ioSocket.getLocalPort());
    }

    public IAddress getRemoteAddress() {
        return new TcpIpAddress(this.m_ioSocket.getInetAddress(), this.m_ioSocket.getPort());
    }
}

