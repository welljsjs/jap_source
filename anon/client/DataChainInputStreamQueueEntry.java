/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import java.io.IOException;

public class DataChainInputStreamQueueEntry {
    public static final int TYPE_DATA_AVAILABLE = 1;
    public static final int TYPE_STREAM_END = 2;
    public static final int TYPE_IO_EXCEPTION = 3;
    private int m_type;
    private byte[] m_data;
    private int m_alreadyReadBytes;
    private IOException m_ioException;

    public DataChainInputStreamQueueEntry(int n, byte[] arrby) {
        this.m_type = n;
        this.m_data = arrby;
        this.m_alreadyReadBytes = 0;
        this.m_ioException = null;
    }

    public DataChainInputStreamQueueEntry(IOException iOException) {
        this.m_type = 3;
        this.m_data = null;
        this.m_alreadyReadBytes = 0;
        this.m_ioException = iOException;
    }

    public int getType() {
        return this.m_type;
    }

    public byte[] getData() {
        return this.m_data;
    }

    public int getDataLen() {
        if (this.m_data == null) {
            return 0;
        }
        return this.m_data.length;
    }

    public int getAlreadyReadBytes() {
        return this.m_alreadyReadBytes;
    }

    public void setAlreadyReadBytes(int n) {
        this.m_alreadyReadBytes = n;
    }

    public IOException getIOException() {
        return this.m_ioException;
    }
}

