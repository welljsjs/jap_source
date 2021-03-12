/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.crypto.MyRSA;
import anon.crypto.MyRSAPublicKey;
import anon.mixminion.message.ExitInformation;
import anon.mixminion.message.ForwardInformation;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.message.RoutingInformation;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.util.ByteArrayUtil;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class Header {
    private final int HEADER_LEN = 2048;
    private final int PK_ENC_LEN = 256;
    private final int PK_OVERHEAD_LEN = 42;
    private final int PK_MAX_DATA_LEN = 214;
    private final int HASH_LEN = 20;
    private final int MIN_SH = 42;
    private byte[] VERSION_MAJOR = new byte[]{0, 3};
    private byte[] m_header;

    public Header(Vector vector, Vector vector2, ExitInformation exitInformation) {
        this.m_header = this.buildHeader(vector, vector2, exitInformation);
    }

    private byte[] buildHeader(Vector vector, Vector vector2, ExitInformation exitInformation) {
        byte[] arrby;
        int n;
        Object object;
        int n2;
        Vector<Object> vector3 = new Vector<Object>();
        Vector<MyRSAPublicKey> vector4 = new Vector<MyRSAPublicKey>();
        Vector<byte[]> vector5 = new Vector<byte[]>();
        Vector<byte[]> vector6 = new Vector<byte[]>();
        int n3 = vector.size();
        int[] arrn = new int[n3 + 1];
        vector4.addElement(null);
        vector3.addElement(null);
        vector5.addElement(null);
        vector6.addElement(null);
        for (n2 = 1; n2 <= n3; ++n2) {
            MMRDescription mMRDescription = (MMRDescription)vector.elementAt(n2 - 1);
            vector4.addElement(mMRDescription.getPacketKey());
            vector5.addElement(this.subKey((byte[])vector2.elementAt(n2 - 1), "RANDOM JUNK"));
            vector6.addElement(this.subKey((byte[])vector2.elementAt(n2 - 1), "HEADER SECRET KEY"));
            object = mMRDescription.getRoutingInformation();
            vector3.addElement(object);
        }
        n2 = 0;
        for (n = 1; n <= n3; ++n) {
            arrn[n] = n == n3 ? exitInformation.m_Content.length : ((RoutingInformation)vector3.elementAt((int)(n + 1))).m_Content.length;
            int n4 = n;
            arrn[n4] = arrn[n4] + 84;
            n2 += arrn[n];
        }
        n = 2048 - n2;
        if (n2 > 2048) {
            LogHolder.log(3, LogType.MISC, "[Calculating HEADERSIZE]: Subheaders don't fit into HEADER_LEN ");
        }
        object = new Vector<byte[]>();
        byte[] arrby2 = null;
        ((Vector)object).addElement("".getBytes());
        for (int i = 1; i <= n3; ++i) {
            arrby = (byte[])((Vector)object).elementAt(i - 1);
            byte[] arrby3 = ByteArrayUtil.conc(arrby, MixMinionCryptoUtil.createPRNG((byte[])vector5.elementAt(i), arrn[i]));
            arrby2 = MixMinionCryptoUtil.createPRNG((byte[])vector6.elementAt(i), 2048 + arrn[i]);
            int n5 = 1792 - arrby.length;
            ((Vector)object).addElement(MixMinionCryptoUtil.xor(arrby3, ByteArrayUtil.copy(arrby2, n5, arrby3.length)));
        }
        Vector<byte[]> vector7 = new Vector<byte[]>();
        vector7.setSize(n3 + 2);
        arrby = MixMinionCryptoUtil.randomArray(n);
        vector7.setElementAt(arrby, n3 + 1);
        for (int i = n3; i >= 1; --i) {
            ForwardInformation forwardInformation = i == n3 ? exitInformation : (ForwardInformation)vector3.elementAt(i + 1);
            byte[] arrby4 = this.makeSHS(this.VERSION_MAJOR, (byte[])vector2.elementAt(i - 1), new byte[20], ByteArrayUtil.inttobyte(forwardInformation.m_Content.length, 2), forwardInformation.m_Type, forwardInformation.m_Content);
            int n6 = arrby4.length;
            byte[] arrby5 = ByteArrayUtil.conc(arrby4, (byte[])vector7.elementAt(i + 1));
            byte[] arrby6 = ByteArrayUtil.copy(arrby5, 214, arrby5.length - 214);
            byte[] arrby7 = MixMinionCryptoUtil.Encrypt((byte[])vector6.elementAt(i), arrby6);
            byte[] arrby8 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby7, (byte[])((Vector)object).elementAt(i - 1)));
            byte[] arrby9 = this.makeSHS(this.VERSION_MAJOR, (byte[])vector2.elementAt(i - 1), arrby8, ByteArrayUtil.inttobyte(forwardInformation.m_Content.length, 2), forwardInformation.m_Type, forwardInformation.m_Content);
            int n7 = this.max(214 - n6, 0);
            byte[] arrby10 = ByteArrayUtil.conc(arrby9, ByteArrayUtil.copy(arrby5, 214 - n7, n7));
            byte[] arrby11 = this.pk_encrypt((MyRSAPublicKey)vector4.elementAt(i), arrby10);
            vector7.setElementAt(ByteArrayUtil.conc(arrby11, arrby7), i);
        }
        return (byte[])vector7.elementAt(1);
    }

    public byte[] getAsByteArray() {
        return this.m_header;
    }

    private byte[] subKey(byte[] arrby, String string) {
        return ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, string.getBytes())), 0, 16);
    }

    private byte[] makeFSHS(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4, short s) {
        return ByteArrayUtil.conc(arrby, arrby2, arrby3, arrby4, ByteArrayUtil.inttobyte(s, 2));
    }

    private byte[] makeSHS(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4, short s, byte[] arrby5) {
        return ByteArrayUtil.conc(this.makeFSHS(arrby, arrby2, arrby3, arrby4, s), arrby5);
    }

    private int max(int n, int n2) {
        if (n < n2) {
            n = n2;
        }
        return n;
    }

    private byte[] pk_encrypt(MyRSAPublicKey myRSAPublicKey, byte[] arrby) {
        byte[] arrby2 = "He who would make his own liberty secure, must guard even his enemy from oppression.".getBytes();
        SHA1Digest sHA1Digest = new SHA1Digest();
        sHA1Digest.update(arrby2, 0, arrby2.length);
        MyRSA myRSA = new MyRSA(sHA1Digest);
        try {
            myRSA.init(myRSAPublicKey);
            return myRSA.processBlockOAEP(arrby, 0, arrby.length);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.CRYPTO, exception);
            return null;
        }
    }
}

