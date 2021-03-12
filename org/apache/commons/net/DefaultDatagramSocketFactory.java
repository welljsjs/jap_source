/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import org.apache.commons.net.DatagramSocketFactory;

public class DefaultDatagramSocketFactory
implements DatagramSocketFactory {
    public DatagramSocket createDatagramSocket() throws SocketException {
        return new DatagramSocket();
    }

    public DatagramSocket createDatagramSocket(int n) throws SocketException {
        return new DatagramSocket(n);
    }

    public DatagramSocket createDatagramSocket(int n, InetAddress inetAddress) throws SocketException {
        return new DatagramSocket(n, inetAddress);
    }
}

