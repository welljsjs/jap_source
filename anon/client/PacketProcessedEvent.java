/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

public class PacketProcessedEvent {
    public static final int CODE_DATA_PACKET_RECEIVED = 1;
    public static final int CODE_CONTROL_PACKET_RECEIVED = 2;
    public static final int CODE_DATA_PACKET_DISCARDED = 3;
    public static final int CODE_CONTROL_PACKET_DISCARDED = 4;
    public static final int CODE_DATA_PACKET_SENT = 5;
    public static final int CODE_CONTROL_PACKET_SENT = 6;
    private int m_code;

    public PacketProcessedEvent(int n) {
        this.m_code = n;
    }

    public int getCode() {
        return this.m_code;
    }
}

