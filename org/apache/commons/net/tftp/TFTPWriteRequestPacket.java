/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPRequestPacket;

public final class TFTPWriteRequestPacket
extends TFTPRequestPacket {
    public TFTPWriteRequestPacket(InetAddress inetAddress, int n, String string, int n2) {
        super(inetAddress, n, 2, string, n2);
    }

    TFTPWriteRequestPacket(DatagramPacket datagramPacket) throws TFTPPacketException {
        super(2, datagramPacket);
    }
}

