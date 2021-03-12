/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.tftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.net.io.FromNetASCIIOutputStream;
import org.apache.commons.net.io.ToNetASCIIInputStream;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPAckPacket;
import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPReadRequestPacket;
import org.apache.commons.net.tftp.TFTPWriteRequestPacket;

public class TFTPClient
extends TFTP {
    public static final int DEFAULT_MAX_TIMEOUTS = 5;
    private int __maxTimeouts = 5;

    public void setMaxTimeouts(int n) {
        this.__maxTimeouts = n < 1 ? 1 : n;
    }

    public int getMaxTimeouts() {
        return this.__maxTimeouts;
    }

    public int receiveFile(String string, int n, OutputStream outputStream, InetAddress inetAddress, int n2) throws IOException {
        TFTPPacket tFTPPacket = null;
        TFTPAckPacket tFTPAckPacket = new TFTPAckPacket(inetAddress, n2, 0);
        this.beginBufferedOps();
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 1;
        if (n == 0) {
            outputStream = new FromNetASCIIOutputStream(outputStream);
        }
        TFTPPacket tFTPPacket2 = new TFTPReadRequestPacket(inetAddress, n2, string, n);
        block10: do {
            block17: {
                TFTPErrorPacket tFTPErrorPacket;
                this.bufferedSend(tFTPPacket2);
                block11: while (true) {
                    int n8 = 0;
                    while (n8 < this.__maxTimeouts) {
                        try {
                            tFTPPacket = this.bufferedReceive();
                            break;
                        }
                        catch (SocketException socketException) {
                            if (++n8 < this.__maxTimeouts) continue;
                            this.endBufferedOps();
                            throw new IOException("Connection timed out.");
                        }
                        catch (InterruptedIOException interruptedIOException) {
                            if (++n8 < this.__maxTimeouts) continue;
                            this.endBufferedOps();
                            throw new IOException("Connection timed out.");
                        }
                        catch (TFTPPacketException tFTPPacketException) {
                            this.endBufferedOps();
                            throw new IOException("Bad packet: " + tFTPPacketException.getMessage());
                        }
                    }
                    if (n5 == 0) {
                        n4 = tFTPPacket.getPort();
                        tFTPAckPacket.setPort(n4);
                        if (!inetAddress.equals(tFTPPacket.getAddress())) {
                            inetAddress = tFTPPacket.getAddress();
                            tFTPAckPacket.setAddress(inetAddress);
                            tFTPPacket2.setAddress(inetAddress);
                        }
                    }
                    if (!inetAddress.equals(tFTPPacket.getAddress()) || tFTPPacket.getPort() != n4) break;
                    switch (tFTPPacket.getType()) {
                        case 5: {
                            tFTPErrorPacket = (TFTPErrorPacket)tFTPPacket;
                            this.endBufferedOps();
                            throw new IOException("Error code " + tFTPErrorPacket.getError() + " received: " + tFTPErrorPacket.getMessage());
                        }
                        case 3: {
                            TFTPDataPacket tFTPDataPacket = (TFTPDataPacket)tFTPPacket;
                            n6 = tFTPDataPacket.getDataLength();
                            n5 = tFTPDataPacket.getBlockNumber();
                            if (n5 == n7) {
                                try {
                                    outputStream.write(tFTPDataPacket.getData(), tFTPDataPacket.getDataOffset(), n6);
                                }
                                catch (IOException iOException) {
                                    tFTPErrorPacket = new TFTPErrorPacket(inetAddress, n4, 3, "File write failed.");
                                    this.bufferedSend(tFTPErrorPacket);
                                    this.endBufferedOps();
                                    throw iOException;
                                }
                                ++n7;
                                break block17;
                            }
                            this.discardPackets();
                            if (n5 != n7 - 1) continue block11;
                            continue block10;
                        }
                        default: {
                            this.endBufferedOps();
                            throw new IOException("Received unexpected packet type.");
                        }
                    }
                    break;
                }
                tFTPErrorPacket = new TFTPErrorPacket(tFTPPacket.getAddress(), tFTPPacket.getPort(), 5, "Unexpected host or port.");
                this.bufferedSend(tFTPErrorPacket);
                continue;
            }
            tFTPAckPacket.setBlockNumber(n5);
            tFTPPacket2 = tFTPAckPacket;
            n3 += n6;
        } while (n6 == 512);
        this.bufferedSend(tFTPPacket2);
        this.endBufferedOps();
        return n3;
    }

    public int receiveFile(String string, int n, OutputStream outputStream, String string2, int n2) throws UnknownHostException, IOException {
        return this.receiveFile(string, n, outputStream, InetAddress.getByName(string2), n2);
    }

    public int receiveFile(String string, int n, OutputStream outputStream, InetAddress inetAddress) throws IOException {
        return this.receiveFile(string, n, outputStream, inetAddress, 69);
    }

    public int receiveFile(String string, int n, OutputStream outputStream, String string2) throws UnknownHostException, IOException {
        return this.receiveFile(string, n, outputStream, InetAddress.getByName(string2), 69);
    }

    public void sendFile(String string, int n, InputStream inputStream, InetAddress inetAddress, int n2) throws IOException {
        TFTPPacket tFTPPacket = null;
        TFTPDataPacket tFTPDataPacket = new TFTPDataPacket(inetAddress, n2, 0, this._sendBuffer, 4, 0);
        this.beginBufferedOps();
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        boolean bl = false;
        if (n == 0) {
            inputStream = new ToNetASCIIInputStream(inputStream);
        }
        TFTPPacket tFTPPacket2 = new TFTPWriteRequestPacket(inetAddress, n2, string, n);
        block8: do {
            block17: {
                TFTPErrorPacket tFTPErrorPacket;
                this.bufferedSend(tFTPPacket2);
                block9: while (true) {
                    int n8 = 0;
                    while (n8 < this.__maxTimeouts) {
                        try {
                            tFTPPacket = this.bufferedReceive();
                            break;
                        }
                        catch (SocketException socketException) {
                            if (++n8 < this.__maxTimeouts) continue;
                            this.endBufferedOps();
                            throw new IOException("Connection timed out.");
                        }
                        catch (InterruptedIOException interruptedIOException) {
                            if (++n8 < this.__maxTimeouts) continue;
                            this.endBufferedOps();
                            throw new IOException("Connection timed out.");
                        }
                        catch (TFTPPacketException tFTPPacketException) {
                            this.endBufferedOps();
                            throw new IOException("Bad packet: " + tFTPPacketException.getMessage());
                        }
                    }
                    if (n5 == 0) {
                        n4 = tFTPPacket.getPort();
                        tFTPDataPacket.setPort(n4);
                        if (!inetAddress.equals(tFTPPacket.getAddress())) {
                            inetAddress = tFTPPacket.getAddress();
                            tFTPDataPacket.setAddress(inetAddress);
                            tFTPPacket2.setAddress(inetAddress);
                        }
                    }
                    if (!inetAddress.equals(tFTPPacket.getAddress()) || tFTPPacket.getPort() != n4) break;
                    switch (tFTPPacket.getType()) {
                        case 5: {
                            tFTPErrorPacket = (TFTPErrorPacket)tFTPPacket;
                            this.endBufferedOps();
                            throw new IOException("Error code " + tFTPErrorPacket.getError() + " received: " + tFTPErrorPacket.getMessage());
                        }
                        case 4: {
                            TFTPAckPacket tFTPAckPacket = (TFTPAckPacket)tFTPPacket;
                            n5 = tFTPAckPacket.getBlockNumber();
                            if (n5 == n7) {
                                ++n7;
                                if (bl) {
                                    break block8;
                                }
                                break block17;
                            }
                            this.discardPackets();
                            if (n5 != n7 - 1) continue block9;
                            continue block8;
                        }
                        default: {
                            this.endBufferedOps();
                            throw new IOException("Received unexpected packet type.");
                        }
                    }
                    break;
                }
                tFTPErrorPacket = new TFTPErrorPacket(tFTPPacket.getAddress(), tFTPPacket.getPort(), 5, "Unexpected host or port.");
                this.bufferedSend(tFTPErrorPacket);
                continue;
            }
            int n9 = 4;
            for (n6 = 512; n6 > 0 && (n3 = inputStream.read(this._sendBuffer, n9, n6)) > 0; n6 -= n3) {
                n9 += n3;
            }
            tFTPDataPacket.setBlockNumber(n7);
            tFTPDataPacket.setData(this._sendBuffer, 4, n9 - 4);
            tFTPPacket2 = tFTPDataPacket;
        } while (n6 == 0 || bl);
        this.endBufferedOps();
    }

    public void sendFile(String string, int n, InputStream inputStream, String string2, int n2) throws UnknownHostException, IOException {
        this.sendFile(string, n, inputStream, InetAddress.getByName(string2), n2);
    }

    public void sendFile(String string, int n, InputStream inputStream, InetAddress inetAddress) throws IOException {
        this.sendFile(string, n, inputStream, inetAddress, 69);
    }

    public void sendFile(String string, int n, InputStream inputStream, String string2) throws UnknownHostException, IOException {
        this.sendFile(string, n, inputStream, InetAddress.getByName(string2), 69);
    }
}

