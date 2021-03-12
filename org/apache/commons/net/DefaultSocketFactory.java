/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.net.SocketFactory;

public class DefaultSocketFactory
implements SocketFactory {
    public Socket createSocket(String string, int n) throws UnknownHostException, IOException {
        return new Socket(string, n);
    }

    public Socket createSocket(InetAddress inetAddress, int n) throws IOException {
        return new Socket(inetAddress, n);
    }

    public Socket createSocket(String string, int n, InetAddress inetAddress, int n2) throws UnknownHostException, IOException {
        return new Socket(string, n, inetAddress, n2);
    }

    public Socket createSocket(InetAddress inetAddress, int n, InetAddress inetAddress2, int n2) throws IOException {
        return new Socket(inetAddress, n, inetAddress2, n2);
    }

    public ServerSocket createServerSocket(int n) throws IOException {
        return new ServerSocket(n);
    }

    public ServerSocket createServerSocket(int n, int n2) throws IOException {
        return new ServerSocket(n, n2);
    }

    public ServerSocket createServerSocket(int n, int n2, InetAddress inetAddress) throws IOException {
        return new ServerSocket(n, n2, inetAddress);
    }
}

