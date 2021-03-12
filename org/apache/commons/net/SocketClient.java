/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import org.apache.commons.net.DefaultSocketFactory;
import org.apache.commons.net.SocketFactory;

public abstract class SocketClient {
    public static final String NETASCII_EOL = "\r\n";
    private static final SocketFactory __DEFAULT_SOCKET_FACTORY = new DefaultSocketFactory();
    protected int _timeout_ = 0;
    protected Socket _socket_ = null;
    protected boolean _isConnected_ = false;
    protected int _defaultPort_ = 0;
    protected InputStream _input_ = null;
    protected OutputStream _output_ = null;
    protected SocketFactory _socketFactory_ = __DEFAULT_SOCKET_FACTORY;

    protected void _connectAction_() throws IOException {
        this._socket_.setSoTimeout(this._timeout_);
        this._input_ = this._socket_.getInputStream();
        this._output_ = this._socket_.getOutputStream();
        this._isConnected_ = true;
    }

    public void connect(InetAddress inetAddress, int n) throws SocketException, IOException {
        this._socket_ = this._socketFactory_.createSocket(inetAddress, n);
        this._connectAction_();
    }

    public void connect(String string, int n) throws SocketException, IOException {
        this._socket_ = this._socketFactory_.createSocket(string, n);
        this._connectAction_();
    }

    public void connect(InetAddress inetAddress, int n, InetAddress inetAddress2, int n2) throws SocketException, IOException {
        this._socket_ = this._socketFactory_.createSocket(inetAddress, n, inetAddress2, n2);
        this._connectAction_();
    }

    public void connect(String string, int n, InetAddress inetAddress, int n2) throws SocketException, IOException {
        this._socket_ = this._socketFactory_.createSocket(string, n, inetAddress, n2);
        this._connectAction_();
    }

    public void connect(InetAddress inetAddress) throws SocketException, IOException {
        this.connect(inetAddress, this._defaultPort_);
    }

    public void connect(String string) throws SocketException, IOException {
        this.connect(string, this._defaultPort_);
    }

    public void disconnect() throws IOException {
        this._socket_.close();
        this._input_.close();
        this._output_.close();
        this._socket_ = null;
        this._input_ = null;
        this._output_ = null;
        this._isConnected_ = false;
    }

    public boolean isConnected() {
        return this._isConnected_;
    }

    public void setDefaultPort(int n) {
        this._defaultPort_ = n;
    }

    public int getDefaultPort() {
        return this._defaultPort_;
    }

    public void setDefaultTimeout(int n) {
        this._timeout_ = n;
    }

    public int getDefaultTimeout() {
        return this._timeout_;
    }

    public void setSoTimeout(int n) throws SocketException {
        this._socket_.setSoTimeout(n);
    }

    public int getSoTimeout() throws SocketException {
        return this._socket_.getSoTimeout();
    }

    public void setTcpNoDelay(boolean bl) throws SocketException {
        this._socket_.setTcpNoDelay(bl);
    }

    public boolean getTcpNoDelay() throws SocketException {
        return this._socket_.getTcpNoDelay();
    }

    public void setSoLinger(boolean bl, int n) throws SocketException {
        this._socket_.setSoLinger(bl, n);
    }

    public int getSoLinger() throws SocketException {
        return this._socket_.getSoLinger();
    }

    public int getLocalPort() {
        return this._socket_.getLocalPort();
    }

    public InetAddress getLocalAddress() {
        return this._socket_.getLocalAddress();
    }

    public int getRemotePort() {
        return this._socket_.getPort();
    }

    public InetAddress getRemoteAddress() {
        return this._socket_.getInetAddress();
    }

    public boolean verifyRemote(Socket socket) {
        InetAddress inetAddress = socket.getInetAddress();
        InetAddress inetAddress2 = this.getRemoteAddress();
        return inetAddress.equals(inetAddress2);
    }

    public void setSocketFactory(SocketFactory socketFactory) {
        this._socketFactory_ = socketFactory == null ? __DEFAULT_SOCKET_FACTORY : socketFactory;
    }
}

