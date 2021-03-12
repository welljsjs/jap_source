/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public interface SocketFactory {
    public Socket createSocket(String var1, int var2) throws UnknownHostException, IOException;

    public Socket createSocket(InetAddress var1, int var2) throws IOException;

    public Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws UnknownHostException, IOException;

    public Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException;

    public ServerSocket createServerSocket(int var1) throws IOException;

    public ServerSocket createServerSocket(int var1, int var2) throws IOException;

    public ServerSocket createServerSocket(int var1, int var2, InetAddress var3) throws IOException;
}

