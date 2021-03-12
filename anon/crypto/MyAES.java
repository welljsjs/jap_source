/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class MyAES {
    private AESFastEngine m_AES = new AESFastEngine();
    private byte[] m_arCounter = null;
    private byte[] m_arCounterOut = null;
    private int m_posCTR = 0;

    public synchronized void init(boolean bl, byte[] arrby) throws Exception {
        this.init(bl, arrby, 0, arrby.length);
    }

    public synchronized void init(boolean bl, byte[] arrby, int n, int n2) throws Exception {
        this.m_AES.init(bl, new KeyParameter(arrby, n, n2));
        this.m_arCounter = null;
        this.m_arCounterOut = null;
        this.m_posCTR = 0;
    }

    public synchronized void processBlockECB(byte[] arrby, byte[] arrby2) throws Exception {
        this.m_AES.processBlock(arrby, 0, arrby2, 0);
    }

    public synchronized byte[] processBlockECB(byte[] arrby) throws Exception {
        byte[] arrby2 = new byte[16];
        this.m_AES.processBlock(arrby, 0, arrby2, 0);
        return arrby2;
    }

    public void processBytesCTR(byte[] arrby, int n, byte[] arrby2, int n2, int n3) throws Exception {
        if (this.m_arCounterOut == null) {
            this.m_arCounterOut = new byte[16];
            this.m_posCTR = 0;
            this.m_arCounter = new byte[16];
        }
        while (n3 > 0) {
            if (this.m_posCTR == 0) {
                this.processBlockECB(this.m_arCounter, this.m_arCounterOut);
            }
            while (this.m_posCTR < this.m_arCounterOut.length) {
                arrby2[n2] = (byte)(this.m_arCounterOut[this.m_posCTR] ^ arrby[n]);
                ++n2;
                ++n;
                ++this.m_posCTR;
                if (--n3 != 0) continue;
                return;
            }
            this.m_posCTR = 0;
            int n4 = 1;
            for (int i = this.m_arCounter.length - 1; i >= 0; --i) {
                int n5 = (this.m_arCounter[i] & 0xFF) + n4;
                n4 = n5 > 255 ? 1 : 0;
                this.m_arCounter[i] = (byte)n5;
            }
        }
    }
}

