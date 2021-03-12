/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.EMail;
import anon.mixminion.FirstMMRConnection;
import anon.mixminion.Mixminion;
import anon.mixminion.message.Decoder;
import anon.mixminion.message.FragmentedMessage;
import anon.mixminion.message.Keyring;
import anon.mixminion.message.MessageImplementation;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.message.NoReplyMessage;
import anon.mixminion.message.ReplyBlock;
import anon.mixminion.message.ReplyImplementation;
import anon.mixminion.message.ReplyMessage;
import anon.mixminion.message.SingleBlockMessage;
import anon.mixminion.mmrdescription.InfoServiceMMRListFetcher;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.mixminion.mmrdescription.MMRList;
import anon.mixminion.mmrdescription.PlainMMRListFetcher;
import java.io.IOException;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class Message {
    private EMail m_email = null;
    private int m_hops = 0;
    private String m_address;
    private String m_decoded = null;
    private String m_keyringpassword;
    private int m_rbs;
    int MAX_FRAGMENTS_PER_CHUNK = 16;
    double EXP_FACTOR = 1.3333333333333333;

    public Message(EMail eMail, int n, String string, String string2, int n2) {
        this.m_email = eMail;
        this.m_hops = n;
        this.m_address = string;
        this.m_keyringpassword = string2;
        this.m_rbs = n2;
    }

    public boolean send() {
        return this.encodeMessage();
    }

    boolean encodeMessage() {
        MessageImplementation messageImplementation;
        Object object;
        Vector vector;
        MMRList mMRList;
        String string = null;
        if (this.m_email.getType().equals("ENC")) {
            Decoder decoder = new Decoder(this.m_email.getPayload(), this.m_keyringpassword);
            Vector<String> vector2 = new Vector<String>();
            try {
                string = decoder.decode();
            }
            catch (IOException iOException) {
                System.out.println("Decodier-Exception...");
            }
            vector2.addElement(string);
            this.m_decoded = (String)vector2.elementAt(0);
            return false;
        }
        byte[][] arrby = null;
        boolean bl = true;
        Vector vector3 = null;
        if (this.m_email.getType().equals("RPL")) {
            vector3 = this.m_email.getReplyBlocks();
        }
        if ((mMRList = null) == null && !(mMRList = new MMRList(new InfoServiceMMRListFetcher())).updateList()) {
            mMRList = new MMRList(new PlainMMRListFetcher());
            if (!mMRList.updateList()) {
                return false;
            }
            System.out.println("Groesse: " + mMRList.size());
        }
        for (int i = 0; i < this.m_rbs; ++i) {
            vector = mMRList.getByRandomWithExit(this.m_hops);
            object = new Keyring(this.m_keyringpassword).getNewSecret();
            ReplyBlock replyBlock = new ReplyBlock(this.m_address, vector, (byte[])object);
            replyBlock.buildBlock();
            this.m_email.addRBtoPayload(replyBlock.getReplyBlockasString());
        }
        byte[] arrby2 = MixMinionCryptoUtil.compressData(this.m_email.getPayload().getBytes());
        LogHolder.log(7, LogType.MISC, "[Message] Compressed Size = " + arrby2.length);
        if (arrby2.length + 22 <= 28672) {
            messageImplementation = new SingleBlockMessage(arrby2);
        } else {
            System.out.println("fragmente!");
            messageImplementation = new FragmentedMessage(this.m_email.getReceiver(), this.m_email.getPayload().getBytes());
        }
        arrby = ((MessageImplementation)messageImplementation).buildPayload();
        if (arrby.length == 0) {
            LogHolder.log(3, LogType.MISC, "[Message] Compression failure--> 0 packets ");
            return false;
        }
        ReplyImplementation replyImplementation = this.m_email.getType().equals("RPL") ? new ReplyMessage(arrby, this.m_hops, vector3, mMRList) : new NoReplyMessage(arrby, this.m_hops, this.m_email.getReceiver(), mMRList);
        vector = replyImplementation.buildMessage();
        object = replyImplementation.getStartServers();
        for (int i = 0; i < vector.size(); ++i) {
            bl = bl && this.sendToMixMinionServer((byte[])vector.elementAt(i), (MMRDescription)((Vector)object).elementAt(i));
        }
        return bl;
    }

    private boolean sendToMixMinionServer(byte[] arrby, MMRDescription mMRDescription) {
        boolean bl = false;
        try {
            Mixminion mixminion = Mixminion.getInstance();
            FirstMMRConnection firstMMRConnection = new FirstMMRConnection(mMRDescription, mixminion);
            System.out.println("   connecting...");
            firstMMRConnection.connect();
            System.out.println("   sending...");
            bl = firstMMRConnection.sendMessage(arrby);
            System.out.println("   Value of SendingMethod = " + bl);
            System.out.println("   close connection");
            firstMMRConnection.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return bl;
    }

    private int ceilDiv(double d, double d2) {
        return (int)Math.ceil(d / d2);
    }

    public String getDecoded() {
        return this.m_decoded;
    }
}

