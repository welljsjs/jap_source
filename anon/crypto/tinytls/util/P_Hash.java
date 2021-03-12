/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.util;

import anon.util.ByteArrayUtil;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class P_Hash {
    private byte[] m_secret;
    private byte[] m_seed;
    private Digest m_digest;

    public P_Hash(byte[] arrby, byte[] arrby2, Digest digest) {
        this.m_secret = arrby;
        this.m_seed = arrby2;
        this.m_digest = digest;
    }

    public byte[] getHash(int n) {
        byte[] arrby = null;
        boolean bl = false;
        HMac hMac = new HMac(this.m_digest);
        hMac.reset();
        hMac.init(new KeyParameter(this.m_secret));
        hMac.update(this.m_seed, 0, this.m_seed.length);
        byte[] arrby2 = new byte[hMac.getMacSize()];
        hMac.doFinal(arrby2, 0);
        do {
            hMac.reset();
            hMac.init(new KeyParameter(this.m_secret));
            hMac.update(ByteArrayUtil.conc(arrby2, this.m_seed), 0, arrby2.length + this.m_seed.length);
            byte[] arrby3 = new byte[hMac.getMacSize()];
            hMac.doFinal(arrby3, 0);
            arrby = arrby == null ? arrby3 : ByteArrayUtil.conc(arrby, arrby3);
            hMac.reset();
            hMac.init(new KeyParameter(this.m_secret));
            hMac.update(arrby2, 0, arrby2.length);
            arrby2 = new byte[hMac.getMacSize()];
            hMac.doFinal(arrby2, 0);
        } while (arrby.length < n);
        return ByteArrayUtil.copy(arrby, 0, n);
    }
}

