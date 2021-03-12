/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion;

import anon.crypto.JAPCertificate;
import anon.crypto.PKCS12;
import anon.crypto.RSAKeyPair;
import anon.crypto.Validity;
import anon.crypto.X509DistinguishedName;
import anon.crypto.tinytls.TinyTLS;
import anon.mixminion.FirstMMRConnectionThread;
import anon.mixminion.Mixminion;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.util.ByteArrayUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Random;
import logging.LogHolder;
import logging.LogType;

public class FirstMMRConnection {
    private static String OP_NAME = "JAPClient";
    private TinyTLS m_tinyTLS;
    private MMRDescription m_description;
    private InputStream m_istream;
    private OutputStream m_ostream;
    private String m_protocol = "MMTP 0.3";
    private boolean m_bIsClosed = true;
    private long m_inittimeout = 30000L;
    private Mixminion m_Mixminion;
    private int m_blocksize = 1024;

    public FirstMMRConnection(MMRDescription mMRDescription, Mixminion mixminion) {
        this.m_description = mMRDescription;
        this.m_Mixminion = mixminion;
    }

    public MMRDescription getMMRDescription() {
        return this.m_description;
    }

    public boolean isClosed() {
        return this.m_bIsClosed;
    }

    private boolean sending(byte[] arrby, String string) throws IOException {
        try {
            String string2 = string + "\r\n";
            byte[] arrby2 = new byte[6];
            arrby2 = string2.getBytes();
            int n = 32768;
            if (arrby.length != n) {
                return false;
            }
            byte[] arrby3 = arrby;
            byte[] arrby4 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby3, string.getBytes()));
            this.m_ostream.write(arrby2);
            for (int i = 0; i < arrby3.length; i += this.m_blocksize) {
                this.m_ostream.write(arrby3, i, this.m_blocksize);
            }
            this.m_ostream.write(arrby4);
            this.m_ostream.flush();
            LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Send a packet");
            if (string.equals("SEND")) {
                return this.receive(arrby3, "RECEIVED");
            }
            if (string.equals("JUNK")) {
                return this.receive(arrby3, "RECEIVED JUNK");
            }
            return false;
        }
        catch (InterruptedIOException interruptedIOException) {
            return false;
        }
    }

    public boolean send(byte[] arrby) throws IOException {
        try {
            this.connect();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        String string = "SEND";
        boolean bl = this.sending(arrby, string);
        this.close();
        return bl;
    }

    public boolean sendMessage(byte[] arrby) throws IOException {
        String string = "SEND";
        return this.sending(arrby, string);
    }

    public boolean sendJunk() throws IOException {
        String string = "JUNK";
        int n = 32768;
        byte[] arrby = new byte[n];
        new Random().nextBytes(arrby);
        return this.sending(arrby, string);
    }

    private boolean receive(byte[] arrby, String string) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(this.m_istream);
            byte[] arrby2 = new byte[10];
            bufferedInputStream.read(arrby2, 0, 10);
            String string2 = new String(arrby2, 0, 8);
            byte[] arrby3 = new byte[20];
            bufferedInputStream.read(arrby3, 0, 20);
            if (string2.equals("RECEIVED")) {
                byte[] arrby4 = new byte[20];
                arrby4 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, string.getBytes()));
                if (ByteArrayUtil.equal(arrby3, arrby4)) {
                    LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Packet Transmission succeeded. Valid checksum.");
                    return true;
                }
                LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Packet Transmission failed. Invalid checksum.");
                System.out.println("Hash nicht korrekt!");
                return false;
            }
            if (string2.equals("REJECTED")) {
                byte[] arrby5 = new byte[20];
                arrby5 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, "REJECTED".getBytes()));
                if (ByteArrayUtil.equal(arrby3, arrby5)) {
                    LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Packet Transmission rejected. Valid checksum.");
                    return false;
                }
                LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Packet Transmission rejected. Invalid checksum.");
                return false;
            }
            LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Packet Transmission failed. Invalid server answer.");
            return false;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return false;
        }
    }

    private void createClientCert() {
        try {
            RSAKeyPair rSAKeyPair = RSAKeyPair.getInstance(new BigInteger(new byte[]{1, 0, 1}), new SecureRandom(), 1024, 100);
            JAPCertificate jAPCertificate = JAPCertificate.getInstance(new X509DistinguishedName("CN=" + OP_NAME), rSAKeyPair, new Validity(Calendar.getInstance(), 1));
            RSAKeyPair rSAKeyPair2 = RSAKeyPair.getInstance(new BigInteger(new byte[]{1, 0, 1}), new SecureRandom(), 1024, 100);
            PKCS12 pKCS12 = new PKCS12(new X509DistinguishedName("CN=" + OP_NAME + " <identity>"), rSAKeyPair2, new Validity(Calendar.getInstance(), 1));
            JAPCertificate jAPCertificate2 = jAPCertificate.sign(pKCS12);
            JAPCertificate jAPCertificate3 = JAPCertificate.getInstance(pKCS12.getX509Certificate());
            this.m_tinyTLS.setClientCertificate(new JAPCertificate[]{jAPCertificate2, jAPCertificate3}, rSAKeyPair.getPrivate());
        }
        catch (Exception exception) {
            LogHolder.log(7, LogType.TOR, "Error while creating Certificates. Certificates are not used.");
        }
    }

    public void connect() throws Exception {
        FirstMMRConnectionThread firstMMRConnectionThread = new FirstMMRConnectionThread(this.m_description.getAddress(), this.m_description.getPort(), this.m_inittimeout, this.m_Mixminion.getProxy().getProxyInterface(false).getProxyInterface());
        this.m_tinyTLS = firstMMRConnectionThread.getConnection();
        this.m_tinyTLS.checkRootCertificate(false);
        this.createClientCert();
        this.m_tinyTLS.startHandshake();
        this.m_ostream = this.m_tinyTLS.getOutputStream();
        this.m_istream = this.m_tinyTLS.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(this.m_istream);
        this.m_tinyTLS.setSoTimeout(30000);
        this.m_ostream.write(this.m_protocol.concat("\r\n").getBytes());
        byte[] arrby = new byte[10];
        bufferedInputStream.read(arrby, 0, 10);
        String string = new String(arrby, 0, 8);
        if (string.equals(this.m_protocol)) {
            LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Protocol supported: " + this.m_protocol);
            this.m_bIsClosed = false;
        } else {
            LogHolder.log(7, LogType.MISC, "MMRConnection " + this.m_description.getName() + " - Protocol not supported: " + this.m_protocol);
            this.close();
            this.m_bIsClosed = true;
        }
    }

    public void close() {
        try {
            if (!this.m_bIsClosed) {
                this.m_bIsClosed = true;
                this.m_tinyTLS.close();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

