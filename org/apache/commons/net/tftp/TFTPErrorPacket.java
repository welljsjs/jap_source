/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;

public final class TFTPErrorPacket
extends TFTPPacket {
    public static final int UNDEFINED = 0;
    public static final int FILE_NOT_FOUND = 1;
    public static final int ACCESS_VIOLATION = 2;
    public static final int OUT_OF_SPACE = 3;
    public static final int ILLEGAL_OPERATION = 4;
    public static final int UNKNOWN_TID = 5;
    public static final int FILE_EXISTS = 6;
    public static final int NO_SUCH_USER = 7;
    int _error;
    String _message;

    public TFTPErrorPacket(InetAddress inetAddress, int n, int n2, String string) {
        super(5, inetAddress, n);
        this._error = n2;
        this._message = string;
    }

    TFTPErrorPacket(DatagramPacket datagramPacket) throws TFTPPacketException {
        super(5, datagramPacket.getAddress(), datagramPacket.getPort());
        byte[] arrby = datagramPacket.getData();
        int n = datagramPacket.getLength();
        if (this.getType() != arrby[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }
        this._error = (arrby[2] & 0xFF) << 8 | arrby[3] & 0xFF;
        if (n < 5) {
            throw new TFTPPacketException("Bad error packet. No message.");
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 4; i < n && arrby[i] != 0; ++i) {
            stringBuffer.append((char)arrby[i]);
        }
        this._message = stringBuffer.toString();
    }

    DatagramPacket _newDatagram(DatagramPacket datagramPacket, byte[] arrby) {
        int n = this._message.length();
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        arrby[2] = (byte)((this._error & 0xFFFF) >> 8);
        arrby[3] = (byte)(this._error & 0xFF);
        System.arraycopy(this._message.getBytes(), 0, arrby, 4, n);
        arrby[n + 4] = 0;
        datagramPacket.setAddress(this._address);
        datagramPacket.setPort(this._port);
        datagramPacket.setData(arrby);
        datagramPacket.setLength(n + 4);
        return datagramPacket;
    }

    public DatagramPacket newDatagram() {
        int n = this._message.length();
        byte[] arrby = new byte[n + 5];
        arrby[0] = 0;
        arrby[1] = (byte)this._type;
        arrby[2] = (byte)((this._error & 0xFFFF) >> 8);
        arrby[3] = (byte)(this._error & 0xFF);
        System.arraycopy(this._message.getBytes(), 0, arrby, 4, n);
        arrby[n + 4] = 0;
        return new DatagramPacket(arrby, arrby.length, this._address, this._port);
    }

    public int getError() {
        return this._error;
    }

    public String getMessage() {
        return this._message;
    }
}

