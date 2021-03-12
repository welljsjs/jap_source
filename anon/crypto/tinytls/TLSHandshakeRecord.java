/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

import anon.crypto.tinytls.AbstractTLSRecord;

public final class TLSHandshakeRecord
extends AbstractTLSRecord {
    public static final int HEADER_LENGTH = 4;

    public TLSHandshakeRecord(byte[] arrby, int n) {
        this.m_Header = new byte[4];
        System.arraycopy(arrby, n, this.m_Header, 0, 4);
        this.m_Type = this.m_Header[0];
        this.m_dataLen = (this.m_Header[1] & 0xFF) << 16 | (this.m_Header[2] & 0xFF) << 8 | this.m_Header[3] & 0xFF;
        this.m_Data = new byte[this.m_dataLen];
        System.arraycopy(arrby, n + 4, this.m_Data, 0, this.m_dataLen);
    }

    public int getHeaderLength() {
        return 4;
    }
}

