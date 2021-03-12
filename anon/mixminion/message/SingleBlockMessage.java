/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.message.MessageImplementation;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.util.ByteArrayUtil;

public class SingleBlockMessage
extends MessageImplementation {
    static final int SINGLETON_HEADER_LEN = 22;
    private byte[] m_payload;

    public SingleBlockMessage(byte[] arrby) {
        this.m_payload = arrby;
    }

    public byte[][] buildPayload() {
        int n = 28672 - this.m_payload.length - 22;
        byte[] arrby = MixMinionCryptoUtil.randomArray(n);
        byte[] arrby2 = ByteArrayUtil.inttobyte(this.m_payload.length, 2);
        byte[] arrby3 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(this.m_payload, arrby));
        byte[][] arrby4 = new byte[1][28672];
        arrby4[0] = ByteArrayUtil.conc(arrby2, arrby3, this.m_payload, arrby);
        return arrby4;
    }
}

