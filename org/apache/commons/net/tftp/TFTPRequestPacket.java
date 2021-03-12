/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;

public abstract class TFTPRequestPacket
extends TFTPPacket {
    static final String[] _modeStrings = new String[]{"netascii", "octet"};
    static final byte[][] _modeBytes = new byte[][]{{110, 101, 116, 97, 115, 99, 105, 105, 0}, {111, 99, 116, 101, 116, 0}};
    int _mode;
    String _filename;

    TFTPRequestPacket(InetAddress inetAddress, int n, int n2, String string, int n3) {
        super(n2, inetAddress, n);
        this._filename = string;
        this._mode = n3;
    }

    TFTPRequestPacket(int n, DatagramPacket datagramPacket) throws TFTPPacketException {
        super(n, datagramPacket.getAddress(), datagramPacket.getPort());
        int n2;
        byte[] arrby = datagramPacket.getData();
        if (this.getType() != arrby[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = datagramPacket.getLength();
        for (n2 = 2; n2 < n3 && arrby[n2] != 0; ++n2) {
            stringBuffer.append((char)arrby[n2]);
        }
        this._filename = stringBuffer.toString();
        if (n2 >= n3) {
            throw new TFTPPacketException("Bad filename and mode format.");
        }
        stringBuffer.setLength(0);
        ++n2;
        while (n2 < n3 && arrby[n2] != 0) {
            stringBuffer.append((char)arrby[n2]);
            ++n2;
        }
        String string = stringBuffer.toString().toLowerCase();
        n3 = _modeStrings.length;
        for (n2 = 0; n2 < n3; ++n2) {
            if (!string.equals(_modeStrings[n2])) continue;
            this._mode = n2;
            break;
        }
        if (n2 >= n3) {
            throw new TFTPPacketException("Unrecognized TFTP transfer mode: " + string);
        }
    }

    final DatagramPacket _newDatagram(DatagramPacket datagramPacket, byte[] arrby) {
        int n = this._filename.length();
        int n2 = _modeBytes[this._mode].length;
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        System.arraycopy(this._filename.getBytes(), 0, arrby, 2, n);
        arrby[n + 2] = 0;
        System.arraycopy(_modeBytes[this._mode], 0, arrby, n + 3, n2);
        datagramPacket.setAddress(this._address);
        datagramPacket.setPort(this._port);
        datagramPacket.setData(arrby);
        datagramPacket.setLength(n + n2 + 3);
        return datagramPacket;
    }

    public final DatagramPacket newDatagram() {
        int n = this._filename.length();
        int n2 = _modeBytes[this._mode].length;
        byte[] arrby = new byte[n + n2 + 4];
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        System.arraycopy(this._filename.getBytes(), 0, arrby, 2, n);
        arrby[n + 2] = 0;
        System.arraycopy(_modeBytes[this._mode], 0, arrby, n + 3, n2);
        return new DatagramPacket(arrby, arrby.length, this._address, this._port);
    }

    public final int getMode() {
        return this._mode;
    }

    public final String getFilename() {
        return this._filename;
    }
}

