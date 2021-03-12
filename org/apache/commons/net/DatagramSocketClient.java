/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import org.apache.commons.net.DatagramSocketFactory;
import org.apache.commons.net.DefaultDatagramSocketFactory;

public abstract class DatagramSocketClient {
    private static final DatagramSocketFactory __DEFAULT_SOCKET_FACTORY = new DefaultDatagramSocketFactory();
    protected int _timeout_ = 0;
    protected DatagramSocket _socket_ = null;
    protected boolean _isOpen_ = false;
    protected DatagramSocketFactory _socketFactory_ = __DEFAULT_SOCKET_FACTORY;

    public void open() throws SocketException {
        this._socket_ = this._socketFactory_.createDatagramSocket();
        this._socket_.setSoTimeout(this._timeout_);
        this._isOpen_ = true;
    }

    public void open(int n) throws SocketException {
        this._socket_ = this._socketFactory_.createDatagramSocket(n);
        this._socket_.setSoTimeout(this._timeout_);
        this._isOpen_ = true;
    }

    public void open(int n, InetAddress inetAddress) throws SocketException {
        this._socket_ = this._socketFactory_.createDatagramSocket(n, inetAddress);
        this._socket_.setSoTimeout(this._timeout_);
        this._isOpen_ = true;
    }

    public void close() {
        this._socket_.close();
        this._socket_ = null;
        this._isOpen_ = false;
    }

    public boolean isOpen() {
        return this._isOpen_;
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

    public int getLocalPort() {
        return this._socket_.getLocalPort();
    }

    public InetAddress getLocalAddress() {
        return this._socket_.getLocalAddress();
    }

    public void setDatagramSocketFactory(DatagramSocketFactory datagramSocketFactory) {
        this._socketFactory_ = datagramSocketFactory == null ? __DEFAULT_SOCKET_FACTORY : datagramSocketFactory;
    }
}

