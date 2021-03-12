/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.bsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.io.SocketInputStream;

public class RExecClient
extends SocketClient {
    public static final int DEFAULT_PORT = 512;
    private boolean __remoteVerificationEnabled;
    protected InputStream _errorStream_ = null;

    InputStream _createErrorStream() throws IOException {
        ServerSocket serverSocket = this._socketFactory_.createServerSocket(0, 1, this.getLocalAddress());
        this._output_.write(Integer.toString(serverSocket.getLocalPort()).getBytes());
        this._output_.write(0);
        this._output_.flush();
        Socket socket = serverSocket.accept();
        serverSocket.close();
        if (this.__remoteVerificationEnabled && !this.verifyRemote(socket)) {
            socket.close();
            throw new IOException("Security violation: unexpected connection attempt by " + socket.getInetAddress().getHostAddress());
        }
        return new SocketInputStream(socket, socket.getInputStream());
    }

    public RExecClient() {
        this.setDefaultPort(512);
    }

    public InputStream getInputStream() {
        return this._input_;
    }

    public OutputStream getOutputStream() {
        return this._output_;
    }

    public InputStream getErrorStream() {
        return this._errorStream_;
    }

    public void rexec(String string, String string2, String string3, boolean bl) throws IOException {
        if (bl) {
            this._errorStream_ = this._createErrorStream();
        } else {
            this._output_.write(0);
        }
        this._output_.write(string.getBytes());
        this._output_.write(0);
        this._output_.write(string2.getBytes());
        this._output_.write(0);
        this._output_.write(string3.getBytes());
        this._output_.write(0);
        this._output_.flush();
        int n = this._input_.read();
        if (n > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            while ((n = this._input_.read()) != -1 && n != 10) {
                stringBuffer.append((char)n);
            }
            throw new IOException(stringBuffer.toString());
        }
        if (n < 0) {
            throw new IOException("Server closed connection.");
        }
    }

    public void rexec(String string, String string2, String string3) throws IOException {
        this.rexec(string, string2, string3, false);
    }

    public void disconnect() throws IOException {
        if (this._errorStream_ != null) {
            this._errorStream_.close();
        }
        this._errorStream_ = null;
        super.disconnect();
    }

    public final void setRemoteVerificationEnabled(boolean bl) {
        this.__remoteVerificationEnabled = bl;
    }

    public final boolean isRemoteVerificationEnabled() {
        return this.__remoteVerificationEnabled;
    }
}

