/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

public class InternalChannelMessage {
    public static final int CODE_PACKET_RECEIVED = 1;
    public static final int CODE_CHANNEL_CLOSED = 2;
    public static final int CODE_CHANNEL_EXCEPTION = 3;
    private int m_messageCode;
    private byte[] m_messageData;

    public InternalChannelMessage(int n, byte[] arrby) {
        this.m_messageCode = n;
        this.m_messageData = arrby;
    }

    public int getMessageCode() {
        return this.m_messageCode;
    }

    public byte[] getMessageData() {
        return this.m_messageData;
    }
}

