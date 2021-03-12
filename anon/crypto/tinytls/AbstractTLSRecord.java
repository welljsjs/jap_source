/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

public abstract class AbstractTLSRecord {
    protected int m_Type;
    protected int m_dataLen;
    protected byte[] m_Data;
    protected byte[] m_Header;

    public byte[] getHeader() {
        return this.m_Header;
    }

    public byte[] getData() {
        return this.m_Data;
    }

    public void setType(int n) {
        this.m_Type = n;
        this.m_Header[0] = (byte)(n & 0xFF);
    }

    public int getType() {
        return this.m_Type;
    }

    public int getLength() {
        return this.m_dataLen;
    }

    public abstract int getHeaderLength();
}

