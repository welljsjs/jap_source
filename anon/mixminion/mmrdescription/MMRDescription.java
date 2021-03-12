/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.mmrdescription;

import anon.crypto.MyRSAPublicKey;
import anon.mixminion.message.ExitInformation;
import anon.mixminion.message.RoutingInformation;
import anon.util.Base64;
import anon.util.ByteArrayUtil;
import java.io.LineNumberReader;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import logging.LogHolder;
import logging.LogType;

public class MMRDescription {
    private String m_address;
    private String m_name;
    private int m_port;
    private MyRSAPublicKey m_IdentityKey;
    private MyRSAPublicKey m_PacketKey;
    private byte[] m_digest;
    private byte[] m_keydigest;
    private boolean m_isExitNode;
    private boolean m_allowsFragmened;
    private String m_software;
    private SimpleDateFormat m_published;
    private static String m_time;

    public MMRDescription(String string, String string2, int n, byte[] arrby, byte[] arrby2, boolean bl, boolean bl2, String string3, SimpleDateFormat simpleDateFormat) {
        this.m_address = string;
        this.m_name = string2;
        this.m_port = n;
        this.m_digest = arrby;
        this.m_keydigest = arrby2;
        this.m_isExitNode = bl;
        this.m_allowsFragmened = bl2;
        this.m_software = string3;
        this.m_published = simpleDateFormat;
    }

    public boolean setIdentityKey(byte[] arrby) {
        this.m_IdentityKey = MyRSAPublicKey.getInstance(arrby);
        return this.m_IdentityKey != null;
    }

    public MyRSAPublicKey getIdentityKey() {
        return this.m_IdentityKey;
    }

    public SimpleDateFormat getPublished() {
        return this.m_published;
    }

    public boolean setPacketKey(byte[] arrby) {
        this.m_PacketKey = MyRSAPublicKey.getInstance(arrby);
        return this.m_PacketKey != null;
    }

    public MyRSAPublicKey getPacketKey() {
        return this.m_PacketKey;
    }

    public byte[] getDigest() {
        return this.m_digest;
    }

    public byte[] getKeyDigest() {
        return this.m_keydigest;
    }

    public boolean isExitNode() {
        return this.m_isExitNode;
    }

    public boolean allowsFragmented() {
        return this.m_allowsFragmened;
    }

    public String getAddress() {
        return this.m_address;
    }

    public String getName() {
        return this.m_name;
    }

    public int getPort() {
        return this.m_port;
    }

    public RoutingInformation getRoutingInformation() {
        RoutingInformation routingInformation = new RoutingInformation();
        routingInformation.m_Type = (short)3;
        routingInformation.m_Content = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(this.m_port, 2), this.m_keydigest, this.m_address.getBytes());
        return routingInformation;
    }

    public String getSoftwareVersion() {
        return this.m_software;
    }

    public static ExitInformation getExitInformation(String[] arrstring, byte[] arrby) {
        Object object;
        ExitInformation exitInformation = new ExitInformation();
        byte[] arrby2 = null;
        if (arrby == null) {
            object = new SecureRandom();
            arrby2 = new byte[20];
            ((SecureRandom)object).nextBytes(arrby2);
            arrby2[0] = (byte)(arrby2[0] & 0x7F);
        } else {
            arrby2 = arrby;
        }
        if (arrstring.length < 1) {
            exitInformation.m_Type = 0;
            exitInformation.m_Content = arrby2;
            LogHolder.log(3, LogType.MISC, "[Building ExitInformation]: no Recipients; Packet will be dropped! ");
            return exitInformation;
        }
        exitInformation.m_Type = (short)256;
        object = arrstring[0];
        arrby2 = ByteArrayUtil.conc(arrby2, ((String)object).getBytes());
        exitInformation.m_Content = arrby2;
        return exitInformation;
    }

    public static MMRDescription parse(LineNumberReader lineNumberReader) {
        try {
            String string;
            lineNumberReader.readLine();
            String string2 = lineNumberReader.readLine().substring(10);
            byte[] arrby = Base64.decode(lineNumberReader.readLine().substring(10));
            byte[] arrby2 = Base64.decode(lineNumberReader.readLine().substring(8));
            byte[] arrby3 = Base64.decode(lineNumberReader.readLine().substring(11));
            m_time = string = lineNumberReader.readLine().substring(11, 21);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.parse(string);
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            byte[] arrby4 = Base64.decode(lineNumberReader.readLine().substring(12));
            lineNumberReader.readLine();
            String string3 = lineNumberReader.readLine().substring(10);
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            String string4 = lineNumberReader.readLine().substring(10);
            String string5 = lineNumberReader.readLine().substring(6);
            if (string5.startsWith("gest")) {
                return null;
            }
            byte[] arrby5 = Base64.decode(lineNumberReader.readLine().substring(12));
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            lineNumberReader.readLine();
            String string6 = "";
            boolean bl = false;
            boolean bl2 = false;
            boolean bl3 = false;
            while (!(string6 = lineNumberReader.readLine()).startsWith("[Testing]")) {
                if (string6.startsWith("[Delivery/SMTP]")) {
                    bl = true;
                }
                if (string6.startsWith("[Delivery/MBOX]")) {
                    bl2 = true;
                }
                if (!string6.startsWith("[Delivery/Fragmented")) continue;
                bl3 = true;
            }
            MMRDescription mMRDescription = new MMRDescription(string4, string2, Integer.parseInt(string5), arrby2, arrby5, bl, bl3, string3, simpleDateFormat);
            if (!mMRDescription.setIdentityKey(arrby) || !mMRDescription.setPacketKey(arrby4)) {
                return null;
            }
            return mMRDescription;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "MMRRouter: " + this.m_name + " Exitnode:" + this.m_isExitNode + " FRAGS: " + this.allowsFragmented() + "Published: " + m_time;
    }
}

