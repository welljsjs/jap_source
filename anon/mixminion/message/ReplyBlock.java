/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.message.ExitInformation;
import anon.mixminion.message.Header;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.message.RoutingInformation;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.util.Base64;
import anon.util.ByteArrayUtil;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class ReplyBlock {
    static final int KEY_LEN = 16;
    static final long KEY_LIFETIME = 7776000L;
    private byte[] m_sharedSecret;
    private Vector m_path;
    private byte[] m_longterm_secret;
    private RoutingInformation m_myrouting;
    private byte[] m_headerbytes;
    private String m_myaddress;
    private long m_timetolive;

    public ReplyBlock(String string, Vector vector, byte[] arrby) {
        this.m_myaddress = string;
        this.m_path = vector;
        this.m_longterm_secret = arrby;
        this.m_myrouting = new RoutingInformation();
        this.m_headerbytes = null;
    }

    public ReplyBlock(RoutingInformation routingInformation, byte[] arrby, byte[] arrby2, long l) {
        this.m_myrouting = routingInformation;
        this.m_headerbytes = arrby;
        this.m_sharedSecret = arrby2;
        this.m_timetolive = l;
    }

    public void buildBlock() {
        byte by;
        System.out.println("Baue ReplyBlock an: " + this.m_myaddress);
        int n = this.m_path.size();
        byte[] arrby = null;
        do {
            arrby = MixMinionCryptoUtil.randomArray(20);
            arrby[0] = (byte)(arrby[0] & 0x7F);
        } while ((by = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, this.m_longterm_secret, "Validate".getBytes()))[19]) != 0);
        byte[] arrby2 = ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, this.m_longterm_secret, "Generate".getBytes())), 0, 16);
        byte[] arrby3 = MixMinionCryptoUtil.createPRNG(arrby2, 16 * (n + 1));
        this.m_sharedSecret = ByteArrayUtil.copy(arrby3, n * 16, 16);
        Vector<byte[]> vector = new Vector<byte[]>();
        for (int i = 1; i <= n; ++i) {
            vector.addElement(ByteArrayUtil.copy(arrby3, (n - i) * 16, 16));
        }
        ExitInformation exitInformation = new ExitInformation();
        String[] arrstring = new String[]{this.m_myaddress};
        exitInformation = MMRDescription.getExitInformation(arrstring, arrby);
        Header header = new Header(this.m_path, vector, exitInformation);
        this.m_headerbytes = header.getAsByteArray();
        this.m_myrouting.m_Type = (short)4;
        this.m_myrouting.m_Content = ((MMRDescription)this.m_path.elementAt((int)0)).getRoutingInformation().m_Content;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int n2 = calendar.get(6);
        int n3 = calendar.get(1);
        this.m_timetolive = (long)(((n3 - 1970 - 1) * 365 + n2) * 24 * 60 * 60) + 7776000L;
    }

    public byte[] getHeaderBytes() {
        return this.m_headerbytes;
    }

    public byte[] getSharedSecret() {
        return this.m_sharedSecret;
    }

    public byte[] getReplyBlockasBytes() {
        byte[] arrby = new byte[]{83, 85, 82, 66, 1, 0};
        return ByteArrayUtil.conc(arrby, ByteArrayUtil.inttobyte(this.m_timetolive, 4), this.m_headerbytes, ByteArrayUtil.inttobyte(this.m_myrouting.m_Content.length, 2), ByteArrayUtil.inttobyte(4L, 2), ByteArrayUtil.conc(this.m_sharedSecret, this.m_myrouting.m_Content));
    }

    public String getReplyBlockasString() {
        return "\n\n:-----BEGIN TYPE III REPLY BLOCK-----\nVERSION: 0.2\n" + Base64.encodeBytes(this.getReplyBlockasBytes()) + "\n:-----END TYPE III REPLY BLOCK-----";
    }

    public RoutingInformation getRouting() {
        return this.m_myrouting;
    }

    public static Vector parseReplyBlocks(String string, byte[] arrby) throws IOException {
        Vector<ReplyBlock> vector = new Vector<ReplyBlock>();
        string = string + "\n-----END OF PLAINTEXT MESSAGE-----";
        LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(string));
        String string2 = lineNumberReader.readLine();
        while (true) {
            if (!string2.endsWith("-----BEGIN TYPE III REPLY BLOCK-----")) {
                string2 = lineNumberReader.readLine();
                if (!string2.startsWith("-----END OF PLAINTEXT MESSAGE-----")) continue;
                return vector;
            }
            if (!lineNumberReader.readLine().startsWith(">")) {
                string2 = lineNumberReader.readLine();
                String string3 = "";
                while (!string2.trim().endsWith("-----END TYPE III REPLY BLOCK-----")) {
                    string3 = string3 + string2 + "\n";
                    string2 = lineNumberReader.readLine();
                }
                string3 = string3.substring(0, string3.length() - 1);
                byte[] arrby2 = Base64.decode(string3);
                byte[] arrby3 = new byte[4];
                for (int i = 0; i < 4; ++i) {
                    arrby3[i] = arrby2[6 + i];
                }
                long l = ReplyBlock.byteToInt(arrby3, 0);
                byte[] arrby4 = new byte[]{arrby2[2058], arrby2[2059]};
                int n = ReplyBlock.byteToInt(arrby4, 0);
                RoutingInformation routingInformation = new RoutingInformation();
                routingInformation.m_Type = (short)4;
                byte[] arrby5 = new byte[n];
                for (int i = 2078; i < 2078 + n; ++i) {
                    arrby5[i - 2078] = arrby2[i];
                }
                routingInformation.m_Content = arrby5;
                byte[] arrby6 = new byte[2048];
                for (int i = 0; i < 2048; ++i) {
                    arrby6[i] = arrby2[i + 10];
                }
                byte[] arrby7 = new byte[16];
                for (int i = 0; i < 16; ++i) {
                    arrby7[i] = arrby2[2062 + i];
                }
                vector.addElement(new ReplyBlock(routingInformation, arrby6, arrby7, l));
                continue;
            }
            string2 = lineNumberReader.readLine();
        }
    }

    public static String removeRepyBlocks(String string) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(string));
        String string2 = lineNumberReader.readLine();
        String string3 = "";
        boolean bl = false;
        while (string2 != null) {
            if (string2.trim().endsWith("-----BEGIN TYPE III REPLY BLOCK-----")) {
                bl = true;
            }
            if (!bl) {
                string3 = string3 + "\n" + string2;
            }
            if (string2.trim().endsWith("-----END TYPE III REPLY BLOCK-----")) {
                bl = false;
            }
            string2 = lineNumberReader.readLine();
        }
        return string3;
    }

    public boolean timetoliveIsOK() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int n = calendar.get(6);
        int n2 = calendar.get(1);
        long l = ((n2 - 1970 - 1) * 365 + n) * 24 * 60 * 60;
        return l < this.m_timetolive;
    }

    private static int byteToInt(byte[] arrby, int n) {
        int n2 = 0;
        for (int i = 0; i < arrby.length; ++i) {
            int n3 = (arrby.length - 1 - i) * 8;
            n2 += (arrby[i + n] & 0xFF) << n3;
        }
        return n2;
    }
}

