/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.util;

import anon.crypto.tinytls.util.P_Hash;
import anon.util.ByteArrayUtil;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class PRF {
    private byte[] m_secret;
    private byte[] m_seed;
    private byte[] m_label;

    public PRF(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        this.m_secret = arrby;
        this.m_seed = arrby3;
        this.m_label = arrby2;
    }

    public byte[] calculate(int n) {
        byte[] arrby = new byte[n];
        int n2 = this.m_secret.length / 2;
        if (n2 * 2 < this.m_secret.length) {
            ++n2;
        }
        byte[] arrby2 = ByteArrayUtil.copy(this.m_secret, 0, n2);
        byte[] arrby3 = ByteArrayUtil.copy(this.m_secret, this.m_secret.length - n2, n2);
        P_Hash p_Hash = new P_Hash(arrby2, ByteArrayUtil.conc(this.m_label, this.m_seed), new MD5Digest());
        byte[] arrby4 = p_Hash.getHash(n);
        p_Hash = new P_Hash(arrby3, ByteArrayUtil.conc(this.m_label, this.m_seed), new SHA1Digest());
        byte[] arrby5 = p_Hash.getHash(n);
        for (int i = 0; i < n; ++i) {
            arrby[i] = (byte)((arrby4[i] ^ arrby5[i]) & 0xFF);
        }
        return arrby;
    }
}

