/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.message.ExitInformation;
import anon.mixminion.message.Header;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.message.ReplyBlock;
import anon.mixminion.message.ReplyImplementation;
import anon.mixminion.message.RoutingInformation;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.mixminion.mmrdescription.MMRList;
import anon.util.ByteArrayUtil;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class ReplyMessage
extends ReplyImplementation {
    private MMRList m_mmrlist;
    private int m_hops;
    private byte[][] m_message_parts;
    private Vector m_replyblocks;
    private Vector m_start_server;

    public ReplyMessage(byte[][] arrby, int n, Vector vector, MMRList mMRList) {
        this.m_mmrlist = mMRList;
        this.m_hops = n;
        this.m_message_parts = arrby;
        this.m_replyblocks = vector;
        this.m_start_server = new Vector();
    }

    public Vector buildMessage() {
        boolean bl;
        Vector<byte[]> vector = new Vector<byte[]>();
        boolean bl2 = bl = this.m_message_parts.length > 1;
        if (bl) {
            System.out.println("Reply und Fragmente; Decodierung wird noch nicht moeglich sein...");
        }
        Vector vector2 = new Vector();
        if (!bl) {
            Vector vector3 = this.m_mmrlist.getByRandomWithExit(this.m_hops);
            vector2.addElement(vector3);
        } else {
            vector2 = this.m_mmrlist.getByRandomWithFrag(this.m_hops, this.m_message_parts.length);
        }
        if (this.m_replyblocks.size() < this.m_message_parts.length) {
            return null;
        }
        for (int i = 0; i < this.m_message_parts.length; ++i) {
            LogHolder.log(7, LogType.MISC, "[Message] make Header to Fragment_" + i);
            byte[] arrby = null;
            byte[] arrby2 = ((ReplyBlock)this.m_replyblocks.elementAt(i)).getHeaderBytes();
            Vector<byte[]> vector4 = new Vector<byte[]>();
            Vector vector5 = (Vector)vector2.elementAt(i);
            this.m_start_server.addElement((MMRDescription)vector5.elementAt(0));
            for (int j = 0; j < this.m_hops; ++j) {
                vector4.addElement(MixMinionCryptoUtil.randomArray(16));
            }
            ExitInformation exitInformation = new ExitInformation();
            RoutingInformation routingInformation = ((ReplyBlock)this.m_replyblocks.elementAt(i)).getRouting();
            exitInformation.m_Type = routingInformation.m_Type;
            exitInformation.m_Content = routingInformation.m_Content;
            Header header = new Header(vector5, vector4, exitInformation);
            arrby = header.getAsByteArray();
            byte[] arrby3 = this.m_message_parts[i];
            byte[] arrby4 = null;
            byte[] arrby5 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(((ReplyBlock)this.m_replyblocks.elementAt(i)).getSharedSecret(), "PAYLOAD ENCRYPT".getBytes()));
            arrby3 = MixMinionCryptoUtil.SPRP_Decrypt(arrby5, arrby3);
            arrby2 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(MixMinionCryptoUtil.hash(arrby3), "HIDE HEADER".getBytes())), arrby2);
            arrby3 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(MixMinionCryptoUtil.hash(arrby2), "HIDE PAYLOAD".getBytes())), arrby3);
            for (int j = vector4.size() - 1; j >= 0; --j) {
                byte[] arrby6 = (byte[])vector4.elementAt(j);
                arrby2 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby6, "HEADER ENCRYPT".getBytes())), arrby2);
                arrby3 = MixMinionCryptoUtil.SPRP_Encrypt(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby6, "PAYLOAD ENCRYPT".getBytes())), arrby3);
            }
            arrby4 = ByteArrayUtil.conc(arrby, arrby2, arrby3);
            LogHolder.log(7, LogType.MISC, "[Message] the Messagesize = " + arrby4.length + " Bytes");
            vector.addElement(arrby4);
        }
        return vector;
    }

    public Vector getStartServers() {
        return this.m_start_server;
    }
}

