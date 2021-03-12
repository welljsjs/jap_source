/*
 * Decompiled with CFR 0.150.
 */
package anon;

import java.io.IOException;

public class TooMuchDataForPacketException
extends IOException {
    private int m_bytesSent;

    public TooMuchDataForPacketException(int n) {
        super("ToMuchDataForPacketException: Supplied data doesn't fit in one single packet.");
        this.m_bytesSent = n;
    }

    public int getBytesSent() {
        return this.m_bytesSent;
    }
}

