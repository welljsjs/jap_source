/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPRequestPacket;

public final class TFTPReadRequestPacket
extends TFTPRequestPacket {
    public TFTPReadRequestPacket(InetAddress inetAddress, int n, String string, int n2) {
        super(inetAddress, n, 1, string, n2);
    }

    TFTPReadRequestPacket(DatagramPacket datagramPacket) throws TFTPPacketException {
        super(1, datagramPacket);
    }
}

