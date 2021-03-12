/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;

public final class TFTPAckPacket
extends TFTPPacket {
    int _blockNumber;

    public TFTPAckPacket(InetAddress inetAddress, int n, int n2) {
        super(4, inetAddress, n);
        this._blockNumber = n2;
    }

    TFTPAckPacket(DatagramPacket datagramPacket) throws TFTPPacketException {
        super(4, datagramPacket.getAddress(), datagramPacket.getPort());
        byte[] arrby = datagramPacket.getData();
        if (this.getType() != arrby[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }
        this._blockNumber = (arrby[2] & 0xFF) << 8 | arrby[3] & 0xFF;
    }

    DatagramPacket _newDatagram(DatagramPacket datagramPacket, byte[] arrby) {
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        arrby[2] = (byte)((this._blockNumber & 0xFFFF) >> 8);
        arrby[3] = (byte)(this._blockNumber & 0xFF);
        datagramPacket.setAddress(this._address);
        datagramPacket.setPort(this._port);
        datagramPacket.setData(arrby);
        datagramPacket.setLength(4);
        return datagramPacket;
    }

    public DatagramPacket newDatagram() {
        byte[] arrby = new byte[]{0, (byte)this._type, (byte)((this._blockNumber & 0xFFFF) >> 8), (byte)(this._blockNumber & 0xFF)};
        return new DatagramPacket(arrby, arrby.length, this._address, this._port);
    }

    public int getBlockNumber() {
        return this._blockNumber;
    }

    public void setBlockNumber(int n) {
        this._blockNumber = n;
    }
}

