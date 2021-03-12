/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

import anon.crypto.tinytls.AbstractTLSRecord;
import anon.crypto.tinytls.TLSHandshakeRecord;
import anon.crypto.tinytls.TinyTLS;

public final class TLSPlaintextRecord
extends AbstractTLSRecord {
    public static final int CONTENTTYPE_HANDSHAKE = 22;
    public static final int HEADER_LENGTH = 5;
    public static final int MAX_PAYLOAD_SIZE = 16384;
    private int m_nextHandshakeRecordOffset;

    public TLSPlaintextRecord() {
        this.m_Header = new byte[5];
        this.m_Header[1] = TinyTLS.PROTOCOLVERSION[0];
        this.m_Header[2] = TinyTLS.PROTOCOLVERSION[1];
        this.m_Data = new byte[16384];
        this.m_dataLen = 0;
        this.m_Type = 0;
        this.m_nextHandshakeRecordOffset = 0;
    }

    public void clean() {
        this.m_dataLen = 0;
        this.m_Type = 0;
        this.m_nextHandshakeRecordOffset = 0;
    }

    public int getHeaderLength() {
        return 5;
    }

    public int getMaxPayloadSize() {
        return 16384;
    }

    public void setLength(int n) {
        this.m_dataLen = n;
        this.m_Header[3] = (byte)(n >> 8 & 0xFF);
        this.m_Header[4] = (byte)(n & 0xFF);
    }

    public boolean hasMoreHandshakeRecords() {
        return this.m_Type == 22 && this.m_nextHandshakeRecordOffset < this.m_dataLen;
    }

    public TLSHandshakeRecord getNextHandshakeRecord() {
        TLSHandshakeRecord tLSHandshakeRecord = new TLSHandshakeRecord(this.m_Data, this.m_nextHandshakeRecordOffset);
        this.m_nextHandshakeRecordOffset += tLSHandshakeRecord.getLength() + 4;
        return tLSHandshakeRecord;
    }
}

