/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPAckPacket;
import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPReadRequestPacket;
import org.apache.commons.net.tftp.TFTPWriteRequestPacket;

public abstract class TFTPPacket {
    static final int MIN_PACKET_SIZE = 4;
    public static final int READ_REQUEST = 1;
    public static final int WRITE_REQUEST = 2;
    public static final int DATA = 3;
    public static final int ACKNOWLEDGEMENT = 4;
    public static final int ERROR = 5;
    public static final int SEGMENT_SIZE = 512;
    int _type;
    int _port;
    InetAddress _address;

    public static final TFTPPacket newTFTPPacket(DatagramPacket datagramPacket) throws TFTPPacketException {
        TFTPPacket tFTPPacket = null;
        if (datagramPacket.getLength() < 4) {
            throw new TFTPPacketException("Bad packet. Datagram data length is too short.");
        }
        byte[] arrby = datagramPacket.getData();
        switch (arrby[1]) {
            case 1: {
                tFTPPacket = new TFTPReadRequestPacket(datagramPacket);
                break;
            }
            case 2: {
                tFTPPacket = new TFTPWriteRequestPacket(datagramPacket);
                break;
            }
            case 3: {
                tFTPPacket = new TFTPDataPacket(datagramPacket);
                break;
            }
            case 4: {
                tFTPPacket = new TFTPAckPacket(datagramPacket);
                break;
            }
            case 5: {
                tFTPPacket = new TFTPErrorPacket(datagramPacket);
                break;
            }
            default: {
                throw new TFTPPacketException("Bad packet.  Invalid TFTP operator code.");
            }
        }
        return tFTPPacket;
    }

    TFTPPacket(int n, InetAddress inetAddress, int n2) {
        this._type = n;
        this._address = inetAddress;
        this._port = n2;
    }

    abstract DatagramPacket _newDatagram(DatagramPacket var1, byte[] var2);

    public abstract DatagramPacket newDatagram();

    public final int getType() {
        return this._type;
    }

    public final InetAddress getAddress() {
        return this._address;
    }

    public final int getPort() {
        return this._port;
    }

    public final void setPort(int n) {
        this._port = n;
    }

    public final void setAddress(InetAddress inetAddress) {
        this._address = inetAddress;
    }
}

