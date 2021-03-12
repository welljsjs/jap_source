/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.fec.FECCode;
import anon.mixminion.fec.FECCodeFactory;
import anon.util.ByteArrayUtil;

public class FragmentContainer {
    private byte[] m_id = null;
    private int FRAGSIZE = 28625;
    private byte[][] m_fragments;
    private boolean m_readytoreassemble = false;
    private int m_counter;
    private int[] m_indizes;
    private int m_numberoffrags;
    private boolean[] m_add;

    public FragmentContainer(byte[] arrby, int n) {
        this.m_id = arrby;
        this.m_numberoffrags = n;
        this.m_fragments = new byte[n][this.FRAGSIZE];
        this.m_counter = n - 1;
        this.m_indizes = new int[n];
        double d = 1.3333333333333333;
        double d2 = Math.log(this.m_numberoffrags) / Math.log(2.0);
        d2 = Math.ceil(d2);
        d2 = Math.pow(2.0, d2);
        int n2 = (int)Math.min(16.0, d2);
        int n3 = (int)Math.ceil(d * (double)n2);
        this.m_add = new boolean[n3];
    }

    public boolean addFragment(byte[] arrby, int n) {
        if (this.m_readytoreassemble) {
            return true;
        }
        if (!this.m_add[n]) {
            this.m_add[n] = true;
            this.m_indizes[this.m_counter] = n;
            this.m_fragments[this.m_counter] = arrby;
            --this.m_counter;
        }
        if (this.m_counter == -1) {
            this.m_readytoreassemble = true;
            return true;
        }
        return false;
    }

    public byte[] getID() {
        return this.m_id;
    }

    public byte[] reassembleMessage() {
        byte[] arrby = null;
        if (this.m_readytoreassemble) {
            arrby = new byte[]{};
            double d = 1.3333333333333333;
            double d2 = Math.log(this.m_numberoffrags) / Math.log(2.0);
            d2 = Math.ceil(d2);
            d2 = Math.pow(2.0, d2);
            int n = (int)Math.min(16.0, d2);
            int n2 = (int)Math.ceil(d * (double)n);
            FECCode fECCode = FECCodeFactory.getDefault().createFECCode(n, n2);
            int[] arrn = new int[this.m_numberoffrags];
            fECCode.decode(this.m_fragments, arrn, this.m_indizes, 28625, false);
            for (int i = 0; i < n; ++i) {
                arrby = ByteArrayUtil.conc(arrby, this.m_fragments[i]);
            }
            return arrby;
        }
        return arrby;
    }
}

