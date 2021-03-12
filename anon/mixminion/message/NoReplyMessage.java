/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.message.ExitInformation;
import anon.mixminion.message.Header;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.message.ReplyImplementation;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.mixminion.mmrdescription.MMRList;
import anon.util.ByteArrayUtil;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class NoReplyMessage
extends ReplyImplementation {
    private byte[][] m_message_parts;
    private int m_hops;
    private String[] m_recipient;
    private Vector m_start_server;
    private MMRList m_mmrlist;

    public NoReplyMessage(byte[][] arrby, int n, String[] arrstring, MMRList mMRList) {
        this.m_message_parts = arrby;
        this.m_hops = n;
        this.m_recipient = arrstring;
        this.m_start_server = new Vector();
        this.m_mmrlist = mMRList;
    }

    public Vector buildMessage() {
        Vector<byte[]> vector = new Vector<byte[]>();
        boolean bl = this.m_message_parts.length > 1;
        Vector vector2 = new Vector();
        int n = this.m_hops / 2;
        int n2 = this.m_hops - n;
        if (!bl) {
            Vector vector3 = this.m_mmrlist.getByRandomWithExit(this.m_hops);
            vector2.addElement(vector3);
        } else {
            vector2 = this.m_mmrlist.getByRandomWithFrag(this.m_hops, this.m_message_parts.length);
        }
        for (int i = 0; i < this.m_message_parts.length; ++i) {
            byte[] arrby;
            int n3;
            LogHolder.log(7, LogType.MISC, "[Message] make Header to Fragment_" + i);
            Vector vector4 = (Vector)vector2.elementAt(i);
            Vector vector5 = new Vector();
            Vector vector6 = new Vector();
            vector5 = MixMinionCryptoUtil.subVector(vector4, 0, n);
            vector6 = MixMinionCryptoUtil.subVector(vector4, n, n2);
            this.m_start_server.addElement((MMRDescription)vector5.elementAt(0));
            Vector<byte[]> vector7 = new Vector<byte[]>();
            Vector<byte[]> vector8 = new Vector<byte[]>();
            for (int j = 0; j < this.m_hops / 2; ++j) {
                vector7.addElement(MixMinionCryptoUtil.randomArray(16));
                vector8.addElement(MixMinionCryptoUtil.randomArray(16));
            }
            if (vector8.size() < n2) {
                vector8.addElement(MixMinionCryptoUtil.randomArray(16));
            }
            ExitInformation exitInformation = new ExitInformation();
            if (bl) {
                exitInformation.m_Type = (short)259;
                exitInformation.m_Content = new byte[0];
            } else {
                exitInformation = MMRDescription.getExitInformation(this.m_recipient, null);
            }
            ExitInformation exitInformation2 = new ExitInformation();
            exitInformation2.m_Type = (short)4;
            exitInformation2.m_Content = ((MMRDescription)vector6.elementAt((int)0)).getRoutingInformation().m_Content;
            Header header = new Header(vector5, vector7, exitInformation2);
            Header header2 = new Header(vector6, vector8, exitInformation);
            byte[] arrby2 = header.getAsByteArray();
            byte[] arrby3 = header2.getAsByteArray();
            byte[] arrby4 = this.m_message_parts[i];
            byte[] arrby5 = null;
            for (n3 = vector8.size() - 1; n3 >= 0; --n3) {
                arrby = (byte[])vector8.elementAt(n3);
                byte[] arrby6 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, "PAYLOAD ENCRYPT".getBytes()));
                arrby4 = MixMinionCryptoUtil.SPRP_Encrypt(arrby6, arrby4);
            }
            arrby3 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(MixMinionCryptoUtil.hash(arrby4), "HIDE HEADER".getBytes())), arrby3);
            arrby4 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(MixMinionCryptoUtil.hash(arrby3), "HIDE PAYLOAD".getBytes())), arrby4);
            for (n3 = vector7.size() - 1; n3 >= 0; --n3) {
                arrby = (byte[])vector7.elementAt(n3);
                arrby3 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, "HEADER ENCRYPT".getBytes())), arrby3);
                arrby4 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, "PAYLOAD ENCRYPT".getBytes())), arrby4);
            }
            arrby5 = ByteArrayUtil.conc(arrby2, arrby3, arrby4);
            LogHolder.log(7, LogType.MISC, "[Message] the Messagesize = " + arrby5.length + " Bytes");
            vector.addElement(arrby5);
        }
        return vector;
    }

    public Vector getStartServers() {
        return this.m_start_server;
    }
}

