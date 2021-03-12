/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.bsd;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.apache.commons.net.bsd.RExecClient;
import org.apache.commons.net.io.SocketInputStream;

public class RCommandClient
extends RExecClient {
    public static final int DEFAULT_PORT = 514;
    public static final int MIN_CLIENT_PORT = 512;
    public static final int MAX_CLIENT_PORT = 1023;

    InputStream _createErrorStream() throws IOException {
        int n = 1023;
        ServerSocket serverSocket = null;
        for (n = 1023; n >= 512; --n) {
            try {
                serverSocket = this._socketFactory_.createServerSocket(n, 1, this.getLocalAddress());
                break;
            }
            catch (SocketException socketException) {
                continue;
            }
        }
        if (n < 512) {
            throw new BindException("All ports in use.");
        }
        this._output_.write(Integer.toString(serverSocket.getLocalPort()).getBytes());
        this._output_.write(0);
        this._output_.flush();
        Socket socket = serverSocket.accept();
        serverSocket.close();
        if (this.isRemoteVerificationEnabled() && !this.verifyRemote(socket)) {
            socket.close();
            throw new IOException("Security violation: unexpected connection attempt by " + socket.getInetAddress().getHostAddress());
        }
        return new SocketInputStream(socket, socket.getInputStream());
    }

    public RCommandClient() {
        this.setDefaultPort(514);
    }

    public void connect(InetAddress inetAddress, int n, InetAddress inetAddress2) throws SocketException, BindException, IOException {
        int n2 = 1023;
        for (n2 = 1023; n2 >= 512; --n2) {
            try {
                this._socket_ = this._socketFactory_.createSocket(inetAddress, n, inetAddress2, n2);
                break;
            }
            catch (SocketException socketException) {
                continue;
            }
        }
        if (n2 < 512) {
            throw new BindException("All ports in use or insufficient permssion.");
        }
        this._connectAction_();
    }

    public void connect(InetAddress inetAddress, int n) throws SocketException, IOException {
        this.connect(inetAddress, n, InetAddress.getLocalHost());
    }

    public void connect(String string, int n) throws SocketException, IOException {
        this.connect(InetAddress.getByName(string), n, InetAddress.getLocalHost());
    }

    public void connect(String string, int n, InetAddress inetAddress) throws SocketException, IOException {
        this.connect(InetAddress.getByName(string), n, inetAddress);
    }

    public void connect(InetAddress inetAddress, int n, InetAddress inetAddress2, int n2) throws SocketException, IOException, IllegalArgumentException {
        if (n2 < 512 || n2 > 1023) {
            throw new IllegalArgumentException("Invalid port number " + n2);
        }
        super.connect(inetAddress, n, inetAddress2, n2);
    }

    public void connect(String string, int n, InetAddress inetAddress, int n2) throws SocketException, IOException, IllegalArgumentException {
        if (n2 < 512 || n2 > 1023) {
            throw new IllegalArgumentException("Invalid port number " + n2);
        }
        super.connect(string, n, inetAddress, n2);
    }

    public void rcommand(String string, String string2, String string3, boolean bl) throws IOException {
        this.rexec(string, string2, string3, bl);
    }

    public void rcommand(String string, String string2, String string3) throws IOException {
        this.rcommand(string, string2, string3, false);
    }
}

