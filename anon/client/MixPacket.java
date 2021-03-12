/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.crypto.SymCipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class MixPacket {
    private static final int PACKET_SIZE = 998;
    private static final int NON_DATA_LENGTH = 6;
    public static final short FLAG_CHANNEL_DATA = 0;
    public static final short FLAG_CHANNEL_CLOSE = 1;
    public static final short FLAG_CHANNEL_OPEN = 8;
    public static final short FLAG_CHANNEL_DUMMY = 16;
    public static final short FLAG_DEBUG = 32;
    private static final SecureRandom ms_secureRandom = new SecureRandom();
    private int m_channelId;
    private short m_channelFlags;
    private byte[] m_payloadData;
    private Vector m_sendCallbackHandlers = new Vector();

    public static int getPacketSize() {
        return 998;
    }

    public static int getPayloadSize() {
        return 992;
    }

    public MixPacket(InputStream inputStream, SymCipher symCipher) throws IOException {
        byte[] arrby = new byte[998];
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        try {
            dataInputStream.readFully(arrby);
            LogHolder.log(7, LogType.TRANSPORT, "PacketReceived: " + System.currentTimeMillis());
        }
        catch (EOFException eOFException) {
            LogHolder.log(4, LogType.NET, Thread.currentThread().getName() + ": received a truncated packet from a mix: ", eOFException);
            throw eOFException;
        }
        catch (IOException iOException) {
            try {
                Class<?> class_ = Class.forName("java.net.SocketTimeoutException");
                if (!class_.isAssignableFrom(iOException.getClass())) {
                    throw iOException;
                }
                dataInputStream.readFully(arrby);
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw iOException;
            }
        }
        if (symCipher != null) {
            symCipher.encryptAES1(arrby, 0, arrby, 0, 16);
        }
        DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream(arrby, 0, 6));
        this.m_channelId = dataInputStream2.readInt();
        this.m_channelFlags = dataInputStream2.readShort();
        this.m_payloadData = new byte[arrby.length - 6];
        System.arraycopy(arrby, 6, this.m_payloadData, 0, arrby.length - 6);
    }

    public MixPacket(int n) {
        this.m_channelId = n;
        this.m_channelFlags = 0;
        this.m_payloadData = new byte[992];
        ms_secureRandom.nextBytes(this.m_payloadData);
    }

    public int getChannelId() {
        return this.m_channelId;
    }

    public short getChannelFlags() {
        return this.m_channelFlags;
    }

    public void setChannelFlags(short s) {
        this.m_channelFlags = s;
    }

    public byte[] getPayloadData() {
        return this.m_payloadData;
    }

    public byte[] getRawPacket() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(this.m_channelId);
            dataOutputStream.writeShort(this.m_channelFlags);
            dataOutputStream.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        byte[] arrby = new byte[998];
        byte[] arrby2 = byteArrayOutputStream.toByteArray();
        System.arraycopy(arrby2, 0, arrby, 0, arrby2.length);
        System.arraycopy(this.m_payloadData, 0, arrby, arrby2.length, this.m_payloadData.length);
        return arrby;
    }

    public Vector getSendCallbackHandlers() {
        return this.m_sendCallbackHandlers;
    }
}

