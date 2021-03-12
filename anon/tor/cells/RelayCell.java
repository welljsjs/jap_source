/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.cells;

import anon.crypto.MyAES;
import anon.tor.cells.Cell;
import anon.util.ByteArrayUtil;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class RelayCell
extends Cell {
    public static final byte RELAY_BEGIN = 1;
    public static final byte RELAY_DATA = 2;
    public static final byte RELAY_END = 3;
    public static final byte RELAY_CONNECTED = 4;
    public static final byte RELAY_SENDME = 5;
    public static final byte RELAY_EXTEND = 6;
    public static final byte RELAY_EXTENDED = 7;
    public static final byte RELAY_TRUNCATE = 8;
    public static final byte RELAY_TRUNCATED = 9;
    public static final byte RELAY_DROP = 10;
    public static final byte RELAY_RESOLVE = 11;
    public static final byte RELAY_RESOLVED = 12;
    private byte m_relayCommand;
    private Integer m_streamID;
    private boolean m_digestGenerated;

    public RelayCell() {
        super(3);
        this.m_streamID = new Integer(0);
    }

    public RelayCell(int n) {
        super(3, n);
        this.m_streamID = new Integer(0);
        this.m_digestGenerated = false;
    }

    public RelayCell(int n, byte[] arrby, int n2) {
        super(3, n, arrby, n2);
        this.m_relayCommand = arrby[0];
        this.m_streamID = new Integer((arrby[3] & 0xFF) << 8 | arrby[4] & 0xFF);
        this.m_digestGenerated = false;
    }

    public RelayCell(int n, byte by, int n2, byte[] arrby) {
        super(3, n, RelayCell.createPayload(by, n2, arrby));
        this.m_relayCommand = by;
        this.m_streamID = new Integer(n2);
        this.m_digestGenerated = false;
    }

    public byte getRelayCommand() {
        return this.m_relayCommand;
    }

    public Integer getStreamID() {
        return this.m_streamID;
    }

    public void generateDigest(SHA1Digest sHA1Digest) {
        if (!this.m_digestGenerated) {
            sHA1Digest.update(this.m_payload, 0, this.m_payload.length);
            SHA1Digest sHA1Digest2 = new SHA1Digest(sHA1Digest);
            byte[] arrby = new byte[sHA1Digest2.getDigestSize()];
            sHA1Digest2.doFinal(arrby, 0);
            for (int i = 0; i < 4; ++i) {
                this.m_payload[i + 5] = arrby[i];
            }
            this.m_digestGenerated = true;
        }
    }

    public void checkDigest(SHA1Digest sHA1Digest) throws Exception {
        sHA1Digest.update(this.m_payload, 0, 5);
        sHA1Digest.update(new byte[4], 0, 4);
        sHA1Digest.update(this.m_payload, 9, this.m_payload.length - 9);
        SHA1Digest sHA1Digest2 = new SHA1Digest(sHA1Digest);
        byte[] arrby = new byte[sHA1Digest2.getDigestSize()];
        sHA1Digest2.doFinal(arrby, 0);
        for (int i = 0; i < 4; ++i) {
            if (this.m_payload[i + 5] == arrby[i]) continue;
            throw new Exception("Wrong Digest detected");
        }
        this.m_digestGenerated = true;
    }

    public void doCryptography(MyAES myAES) throws Exception {
        byte[] arrby = new byte[this.m_payload.length];
        myAES.processBytesCTR(this.m_payload, 0, arrby, 0, 509);
        this.m_payload = arrby;
        this.m_relayCommand = this.m_payload[0];
        this.m_streamID = new Integer((this.m_payload[3] & 0xFF) << 8 | this.m_payload[4] & 0xFF);
    }

    private static byte[] createPayload(byte by, int n, byte[] arrby) {
        byte[] arrby2 = new byte[]{by, 0, 0};
        arrby2 = ByteArrayUtil.conc(arrby2, ByteArrayUtil.inttobyte(n, 2), new byte[4]);
        if (arrby == null) {
            arrby = new byte[498];
        }
        arrby2 = arrby.length < 499 ? ByteArrayUtil.conc(arrby2, ByteArrayUtil.inttobyte(arrby.length, 2), arrby) : ByteArrayUtil.conc(arrby2, ByteArrayUtil.inttobyte(498L, 2), ByteArrayUtil.copy(arrby, 0, 498));
        return arrby2;
    }

    public byte[] getCellData() {
        if (this.m_digestGenerated) {
            return super.getCellData();
        }
        return null;
    }

    public byte[] getRelayPayload() {
        int n = this.m_payload[9] & 0xFF;
        n <<= 8;
        return ByteArrayUtil.copy(this.m_payload, 11, n |= this.m_payload[10] & 0xFF);
    }
}

