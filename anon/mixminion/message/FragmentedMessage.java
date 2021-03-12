/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.fec.FECCode;
import anon.mixminion.fec.FECCodeFactory;
import anon.mixminion.message.ExitInformation;
import anon.mixminion.message.MessageImplementation;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.util.ByteArrayUtil;
import logging.LogHolder;
import logging.LogType;

public class FragmentedMessage
extends MessageImplementation {
    static final int KEY_LEN = 16;
    static final int OVERHEAD = 0;
    static final int FRAGMENT_HEADER_LEN = 47;
    String[] m_recipient;
    byte[] m_payload;

    public FragmentedMessage(String[] arrstring, byte[] arrby) {
        this.m_payload = arrby;
        this.m_recipient = arrstring;
    }

    public byte[][] buildPayload() {
        this.m_payload = MixMinionCryptoUtil.compressData(this.m_payload);
        ExitInformation exitInformation = MMRDescription.getExitInformation(this.m_recipient, null);
        exitInformation.m_Content = this.m_recipient[0].getBytes();
        byte[] arrby = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(256L, 2), ByteArrayUtil.inttobyte(exitInformation.m_Content.length, 2), exitInformation.m_Content);
        this.m_payload = ByteArrayUtil.conc(arrby, this.m_payload);
        LogHolder.log(7, LogType.MISC, "[Message] Fragmented, new Compressed Size = " + this.m_payload.length);
        if (this.m_payload.length + 22 <= 28672) {
            throw new RuntimeException("Fragmented Header nach Neukomprimierung mit Single-Laenge");
        }
        this.m_payload = this.whiten(this.m_payload);
        byte[][] arrby2 = this.divideIntoFragments(this.m_payload);
        byte[] arrby3 = MixMinionCryptoUtil.randomArray(20);
        byte[] arrby4 = ByteArrayUtil.inttobyte(this.m_payload.length, 4);
        byte[][] arrby5 = new byte[arrby2.length][28672];
        for (int i = 0; i < arrby2.length; ++i) {
            byte[] arrby6 = arrby2[i];
            byte[] arrby7 = new byte[3];
            arrby7 = ByteArrayUtil.inttobyte(0x800000 + i, 3);
            byte[] arrby8 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby3, arrby4, arrby6));
            arrby5[i] = ByteArrayUtil.conc(arrby7, arrby8, arrby3, arrby4, arrby6);
        }
        return arrby5;
    }

    byte[][] divideIntoFragments(byte[] arrby) {
        Object object;
        int n;
        int n2 = 28625;
        double d = 1.3333333333333333;
        double d2 = Math.ceil((double)arrby.length / (double)n2);
        double d3 = Math.log(d2) / Math.log(2.0);
        d3 = Math.ceil(d3);
        d3 = Math.pow(2.0, d3);
        int n3 = (int)Math.min(16.0, d3);
        int n4 = (int)Math.ceil(d2 / (double)n3);
        byte[] arrby2 = MixMinionCryptoUtil.randomArray(16);
        int n5 = arrby.length - n4 * n2 * n3;
        System.out.println(n5);
        n5 = Math.abs(n5);
        byte[] arrby3 = MixMinionCryptoUtil.createPRNG(arrby2, n5);
        arrby = ByteArrayUtil.conc(arrby, arrby3);
        byte[][] arrby4 = new byte[n4][n2];
        for (n = 1; n <= n4; ++n) {
            object = ByteArrayUtil.copy(arrby, (n - 1) * n2 * n3, n2 * n3);
            arrby4[n - 1] = object;
        }
        n = (int)Math.ceil(d * (double)n3);
        System.out.println("   N,num " + n + " " + n4);
        object = new byte[n4 * n][28672];
        for (int i = 0; i <= n4 - 1; ++i) {
            for (int j = 0; j <= n - 1; ++j) {
                object[i * n + j] = (byte)this.FRAGMENT(arrby4[i], n3, n, j, n2);
            }
        }
        return object;
    }

    byte[] FRAGMENT(byte[] arrby, int n, int n2, int n3, int n4) {
        int n5;
        int n6 = n4;
        byte[] arrby2 = arrby;
        byte[] arrby3 = new byte[n2 * n6];
        byte[][] arrarrby = new byte[n][];
        byte[][] arrarrby2 = new byte[n2][];
        int[] arrn = new int[arrarrby.length];
        int[] arrn2 = new int[arrarrby2.length];
        for (n5 = 0; n5 < arrarrby.length; ++n5) {
            arrarrby[n5] = arrby2;
            arrn[n5] = n5 * n6;
        }
        for (n5 = 0; n5 < arrarrby2.length; ++n5) {
            arrarrby2[n5] = arrby3;
            arrn2[n5] = n5 * n6;
        }
        int[] arrn3 = new int[n2];
        for (int i = 0; i < arrn3.length; ++i) {
            arrn3[i] = i;
        }
        FECCode fECCode = FECCodeFactory.getDefault().createFECCode(n, n2);
        fECCode.encode(arrarrby, arrn, arrarrby2, arrn2, arrn3, n6);
        return ByteArrayUtil.copy(arrarrby2[n3], arrn2[n3], n6);
    }

    private byte[] whiten(byte[] arrby) {
        byte[] arrby2 = new byte[]{87, 72, 73, 84, 69, 78};
        byte[] arrby3 = ByteArrayUtil.conc(arrby2, "WHITEN".getBytes());
        return MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(arrby3), arrby);
    }
}

