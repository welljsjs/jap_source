/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;

public final class TFTPDataPacket
extends TFTPPacket {
    public static final int MAX_DATA_LENGTH = 512;
    public static final int MIN_DATA_LENGTH = 0;
    int _blockNumber;
    int _length;
    int _offset;
    byte[] _data;

    public TFTPDataPacket(InetAddress inetAddress, int n, int n2, byte[] arrby, int n3, int n4) {
        super(3, inetAddress, n);
        this._blockNumber = n2;
        this._data = arrby;
        this._offset = n3;
        this._length = n4 > 512 ? 512 : n4;
    }

    public TFTPDataPacket(InetAddress inetAddress, int n, int n2, byte[] arrby) {
        this(inetAddress, n, n2, arrby, 0, arrby.length);
    }

    TFTPDataPacket(DatagramPacket datagramPacket) throws TFTPPacketException {
        super(3, datagramPacket.getAddress(), datagramPacket.getPort());
        this._data = datagramPacket.getData();
        this._offset = 4;
        if (this.getType() != this._data[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }
        this._blockNumber = (this._data[2] & 0xFF) << 8 | this._data[3] & 0xFF;
        this._length = datagramPacket.getLength() - 4;
        if (this._length > 512) {
            this._length = 512;
        }
    }

    DatagramPacket _newDatagram(DatagramPacket datagramPacket, byte[] arrby) {
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        arrby[2] = (byte)((this._blockNumber & 0xFFFF) >> 8);
        arrby[3] = (byte)(this._blockNumber & 0xFF);
        if (arrby != this._data) {
            System.arraycopy(this._data, this._offset, arrby, 4, this._length);
        }
        datagramPacket.setAddress(this._address);
        datagramPacket.setPort(this._port);
        datagramPacket.setData(arrby);
        datagramPacket.setLength(this._length + 4);
        return datagramPacket;
    }

    public DatagramPacket newDatagram() {
        byte[] arrby = new byte[this._length + 4];
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        arrby[2] = (byte)((this._blockNumber & 0xFFFF) >> 8);
        arrby[3] = (byte)(this._blockNumber & 0xFF);
        System.arraycopy(this._data, this._offset, arrby, 4, this._length);
        return new DatagramPacket(arrby, this._length + 4, this._address, this._port);
    }

    public int getBlockNumber() {
        return this._blockNumber;
    }

    public void setBlockNumber(int n) {
        this._blockNumber = n;
    }

    public void setData(byte[] arrby, int n, int n2) {
        this._data = arrby;
        this._offset = n;
        this._length = n2;
        this._length = n2 > 512 ? 512 : n2;
    }

    public int getDataLength() {
        return this._length;
    }

    public int getDataOffset() {
        return this._offset;
    }

    public byte[] getData() {
        return this._data;
    }
}

