/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyDSASignature;
import anon.crypto.MyRSAPrivateKey;
import anon.crypto.MyRSASignature;
import anon.crypto.tinytls.TLSException;
import anon.crypto.tinytls.TLSHandshakeRecord;
import anon.crypto.tinytls.TLSPlaintextRecord;
import anon.crypto.tinytls.ciphersuites.CipherSuite;
import anon.crypto.tinytls.ciphersuites.DHE_DSS_WITH_3DES_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_DSS_WITH_AES_128_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_DSS_WITH_DES_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_RSA_WITH_3DES_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_RSA_WITH_AES_128_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_RSA_WITH_DES_CBC_SHA;
import anon.crypto.tinytls.util.hash;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.shared.ProxyConnection;
import anon.util.ByteArrayUtil;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.util.Random;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class TinyTLS
extends Socket {
    public static byte[] PROTOCOLVERSION = new byte[]{3, 1};
    private static int PROTOCOLVERSION_SHORT = 769;
    private Vector m_supportedciphersuites;
    private CipherSuite m_selectedciphersuite = null;
    private TLSInputStream m_istream;
    private TLSOutputStream m_ostream;
    private boolean m_handshakecompleted;
    private boolean m_serverhellodone;
    private boolean m_certificaterequested;
    private JAPCertificate m_servercertificate;
    private IMyPublicKey m_trustedRoot;
    private boolean m_checkTrustedRoot;
    private byte[] m_clientrandom;
    private byte[] m_serverrandom;
    private byte[] m_handshakemessages;
    private byte[] m_clientcertificatetypes;
    private IMyPrivateKey m_clientprivatekey;
    private JAPCertificate[] m_clientcertificates;
    private boolean m_certificateverify;
    private boolean m_encrypt;
    private ProxyConnection m_ProxyConnection;

    public TinyTLS(String string, int n) throws UnknownHostException, IOException, Exception {
        this(string, n, null);
    }

    public TinyTLS(String string, int n, ImmutableProxyInterface immutableProxyInterface) throws UnknownHostException, IOException, Exception {
        this.m_ProxyConnection = new ProxyConnection(HTTPConnectionFactory.getInstance().createHTTPConnection(new ListenerInterface(string, n), immutableProxyInterface).Connect());
        this.m_handshakecompleted = false;
        this.m_serverhellodone = false;
        this.m_encrypt = false;
        this.m_certificaterequested = false;
        this.m_certificateverify = false;
        this.m_supportedciphersuites = new Vector();
        this.m_istream = new TLSInputStream(this.m_ProxyConnection.getInputStream());
        this.m_ostream = new TLSOutputStream(this.m_ProxyConnection.getOutputStream());
        this.m_trustedRoot = null;
        this.m_checkTrustedRoot = true;
        this.m_clientcertificatetypes = null;
        this.m_clientcertificates = null;
        this.m_clientprivatekey = null;
    }

    public void addCipherSuite(CipherSuite cipherSuite) {
        if (!this.m_supportedciphersuites.contains(cipherSuite)) {
            this.m_supportedciphersuites.addElement(cipherSuite);
            LogHolder.log(7, LogType.MISC, "[CIPHERSUITE_ADDED] : " + cipherSuite.toString());
        }
    }

    public void startHandshake() throws IOException {
        if (this.m_supportedciphersuites.isEmpty()) {
            LogHolder.log(7, LogType.MISC, "[NO_CIPHERSUITE_DEFINED] : using predefined");
            this.addCipherSuite(new DHE_RSA_WITH_AES_128_CBC_SHA());
            this.addCipherSuite(new DHE_DSS_WITH_AES_128_CBC_SHA());
            this.addCipherSuite(new DHE_RSA_WITH_3DES_CBC_SHA());
            this.addCipherSuite(new DHE_DSS_WITH_3DES_CBC_SHA());
            this.addCipherSuite(new DHE_RSA_WITH_DES_CBC_SHA());
            this.addCipherSuite(new DHE_DSS_WITH_DES_CBC_SHA());
        }
        if (!this.m_checkTrustedRoot) {
            LogHolder.log(7, LogType.MISC, "[CHECK_TRUSTED_ROOT_DEACTIVATED] : all certificates are accepted");
        } else if (this.m_trustedRoot == null) {
            LogHolder.log(7, LogType.MISC, "[TRUSTED_CERTIFICATES_NOT_SET] : cannot verify Certificates");
            throw new TLSException("Please set Trusted Root");
        }
        this.m_handshakemessages = new byte[0];
        this.m_ostream.sendClientHello();
        this.m_istream.readServerHandshakes();
        this.m_ostream.sendClientCertificate();
        this.m_ostream.sendClientKeyExchange();
        this.m_ostream.sendCertificateVerify();
        this.m_ostream.sendChangeCipherSpec();
        this.m_ostream.sendClientFinished();
        this.m_istream.readServerFinished();
        this.m_handshakecompleted = true;
    }

    public void setRootKey(IMyPublicKey iMyPublicKey) {
        this.m_trustedRoot = iMyPublicKey;
    }

    public void checkRootCertificate(boolean bl) {
        this.m_checkTrustedRoot = bl;
    }

    public InputStream getInputStream() {
        return this.m_istream;
    }

    public OutputStream getOutputStream() {
        return this.m_ostream;
    }

    public void setSoTimeout(int n) throws SocketException {
        this.m_ProxyConnection.setSoTimeout(n);
    }

    public void setClientCertificate(JAPCertificate jAPCertificate, IMyPrivateKey iMyPrivateKey) throws IOException {
        this.setClientCertificate(new JAPCertificate[]{jAPCertificate}, iMyPrivateKey);
    }

    public void setClientCertificate(JAPCertificate[] arrjAPCertificate, IMyPrivateKey iMyPrivateKey) throws IOException {
        if (arrjAPCertificate != null) {
            JAPCertificate jAPCertificate = arrjAPCertificate[0];
            LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE] " + jAPCertificate.getIssuer().toString());
            LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE] " + jAPCertificate.getSubject().toString());
            for (int i = 1; i < arrjAPCertificate.length; ++i) {
                JAPCertificate jAPCertificate2 = arrjAPCertificate[i];
                if (!jAPCertificate.verify(jAPCertificate2.getPublicKey())) {
                    throw new IOException("TLS Server Certs could not be verified!");
                }
                jAPCertificate = jAPCertificate2;
                LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE] " + jAPCertificate.getIssuer().toString());
                LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE] " + jAPCertificate.getSubject().toString());
            }
        }
        this.m_clientcertificates = arrjAPCertificate;
        this.m_clientprivatekey = iMyPrivateKey;
    }

    public void close() {
        try {
            if (this.m_ostream != null) {
                this.m_ostream.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            if (this.m_istream != null) {
                this.m_istream.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.m_ProxyConnection.close();
    }

    public Socket getSocket() {
        return this.m_ProxyConnection.getSocket();
    }

    static /* synthetic */ byte[] access$202(TinyTLS tinyTLS, byte[] arrby) {
        tinyTLS.m_serverrandom = arrby;
        return arrby;
    }

    static /* synthetic */ byte[] access$902(TinyTLS tinyTLS, byte[] arrby) {
        tinyTLS.m_clientcertificatetypes = arrby;
        return arrby;
    }

    static /* synthetic */ byte[] access$1202(TinyTLS tinyTLS, byte[] arrby) {
        tinyTLS.m_handshakemessages = arrby;
        return arrby;
    }

    static /* synthetic */ byte[] access$702(TinyTLS tinyTLS, byte[] arrby) {
        tinyTLS.m_clientrandom = arrby;
        return arrby;
    }

    class TLSOutputStream
    extends OutputStream {
        private OutputStream m_stream;
        private TLSPlaintextRecord m_aktTLSRecord = new TLSPlaintextRecord();

        public TLSOutputStream(OutputStream outputStream) {
            this.m_stream = outputStream;
        }

        public void write(byte[] arrby) throws IOException {
            this.send(23, arrby, 0, arrby.length);
        }

        public void write(byte[] arrby, int n, int n2) throws IOException {
            this.send(23, arrby, n, n2);
        }

        public void write(int n) throws IOException {
            this.write(new byte[]{(byte)n});
        }

        public void close() throws IOException {
            this.sendCloseNotify();
            this.m_stream.close();
        }

        public void flush() throws IOException {
            this.m_stream.flush();
        }

        private synchronized void send(int n, byte[] arrby, int n2, int n3) throws IOException {
            byte[] arrby2 = this.m_aktTLSRecord.getData();
            System.arraycopy(arrby, n2, arrby2, 0, n3);
            this.m_aktTLSRecord.setLength(n3);
            this.m_aktTLSRecord.setType(n);
            if (TinyTLS.this.m_encrypt) {
                TinyTLS.this.m_selectedciphersuite.encode(this.m_aktTLSRecord);
            }
            this.m_stream.write(this.m_aktTLSRecord.getHeader());
            this.m_stream.write(arrby2, 0, this.m_aktTLSRecord.getLength());
            this.m_stream.flush();
        }

        public void sendHandshake(int n, byte[] arrby) throws IOException {
            byte[] arrby2 = ByteArrayUtil.conc(new byte[]{(byte)n}, ByteArrayUtil.inttobyte(arrby.length, 3), arrby);
            this.send(22, arrby2, 0, arrby2.length);
            TinyTLS.access$1202(TinyTLS.this, ByteArrayUtil.conc(TinyTLS.this.m_handshakemessages, arrby2));
        }

        public void sendClientHello() throws IOException {
            Object object;
            byte[] arrby = new byte[28];
            byte[] arrby2 = new byte[]{0};
            byte[] arrby3 = new byte[TinyTLS.this.m_supportedciphersuites.size() * 2];
            int n = 0;
            for (int i = 0; i < TinyTLS.this.m_supportedciphersuites.size(); ++i) {
                object = (CipherSuite)TinyTLS.this.m_supportedciphersuites.elementAt(i);
                arrby3[n] = ((CipherSuite)object).getCipherSuiteCode()[0];
                arrby3[++n] = ((CipherSuite)object).getCipherSuiteCode()[1];
                ++n;
            }
            byte[] arrby4 = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(TinyTLS.this.m_supportedciphersuites.size() * 2, 2), arrby3);
            object = new byte[]{1, 0};
            byte[] arrby5 = ByteArrayUtil.inttobyte(System.currentTimeMillis() / 1000L, 4);
            Random random = new Random(System.currentTimeMillis());
            random.nextBytes(arrby);
            byte[] arrby6 = ByteArrayUtil.conc(PROTOCOLVERSION, arrby5, arrby, arrby2, arrby4, (byte[])object);
            this.sendHandshake(1, arrby6);
            TinyTLS.access$702(TinyTLS.this, ByteArrayUtil.conc(arrby5, arrby));
            LogHolder.log(7, LogType.MISC, "[CLIENT_HELLO]");
        }

        public void sendClientCertificate() throws IOException {
            LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE]");
            if (TinyTLS.this.m_certificaterequested) {
                if (TinyTLS.this.m_clientcertificatetypes != null && TinyTLS.this.m_clientcertificates != null) {
                    block5: for (int i = 0; i < TinyTLS.this.m_clientcertificatetypes.length; ++i) {
                        switch (TinyTLS.this.m_clientcertificatetypes[i]) {
                            case 1: {
                                byte[] arrby = new byte[]{};
                                for (int j = 0; j < TinyTLS.this.m_clientcertificates.length; ++j) {
                                    byte[] arrby2 = TinyTLS.this.m_clientcertificates[j].toByteArray(false);
                                    arrby = ByteArrayUtil.conc(arrby, ByteArrayUtil.inttobyte(arrby2.length, 3), arrby2);
                                }
                                arrby = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby.length, 3), arrby);
                                this.sendHandshake(11, arrby);
                                TinyTLS.this.m_certificateverify = true;
                                return;
                            }
                            case 2: {
                                byte[] arrby = new byte[]{};
                                for (int j = 0; j < TinyTLS.this.m_clientcertificates.length; ++j) {
                                    byte[] arrby3 = TinyTLS.this.m_clientcertificates[j].toByteArray(false);
                                    arrby = ByteArrayUtil.conc(arrby, ByteArrayUtil.inttobyte(arrby3.length, 3), arrby3);
                                }
                                arrby = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby.length, 3), arrby);
                                this.sendHandshake(11, arrby);
                                TinyTLS.this.m_certificateverify = true;
                                return;
                            }
                            case 3: {
                                continue block5;
                            }
                        }
                    }
                } else {
                    this.sendHandshake(11, new byte[]{0, 0, 0});
                }
            }
        }

        public void sendClientKeyExchange() throws IOException {
            byte[] arrby = TinyTLS.this.m_selectedciphersuite.calculateClientKeyExchange();
            this.sendHandshake(16, ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby.length, 2), arrby));
            LogHolder.log(7, LogType.MISC, "[CLIENT_KEY_EXCHANGE]");
        }

        public void sendCertificateVerify() throws IOException {
            if (TinyTLS.this.m_certificateverify) {
                if (TinyTLS.this.m_clientprivatekey instanceof MyRSAPrivateKey) {
                    byte[] arrby = ByteArrayUtil.conc(hash.md5(TinyTLS.this.m_handshakemessages), hash.sha(TinyTLS.this.m_handshakemessages));
                    MyRSASignature myRSASignature = new MyRSASignature();
                    try {
                        myRSASignature.initSign(TinyTLS.this.m_clientprivatekey);
                    }
                    catch (InvalidKeyException invalidKeyException) {
                        throw new TLSException("cannot encrypt signature", 2, 80);
                    }
                    byte[] arrby2 = myRSASignature.signPlain(arrby);
                    arrby2 = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby2.length, 2), arrby2);
                    this.sendHandshake(15, arrby2);
                    LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE_VERIFY_RSA]");
                } else if (TinyTLS.this.m_clientprivatekey instanceof MyDSAPrivateKey) {
                    MyDSASignature myDSASignature = new MyDSASignature();
                    try {
                        myDSASignature.initSign(TinyTLS.this.m_clientprivatekey);
                    }
                    catch (InvalidKeyException invalidKeyException) {
                        // empty catch block
                    }
                    byte[] arrby = myDSASignature.sign(TinyTLS.this.m_handshakemessages);
                    arrby = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby.length, 2), arrby);
                    this.sendHandshake(15, arrby);
                    LogHolder.log(7, LogType.MISC, "[CLIENT_CERTIFICATE_VERIFY_DSA]");
                }
            }
        }

        public void sendChangeCipherSpec() throws IOException {
            this.send(20, new byte[]{1}, 0, 1);
            TinyTLS.this.m_encrypt = true;
            LogHolder.log(7, LogType.MISC, "[CLIENT_CHANGE_CIPHER_SPEC]");
        }

        public void sendCloseNotify() throws IOException {
            this.send(21, new byte[]{1, 0}, 0, 2);
            LogHolder.log(7, LogType.MISC, "[CLIENT_CLOSE_NOTIFY]");
        }

        public void sendClientFinished() throws IOException {
            this.sendHandshake(20, TinyTLS.this.m_selectedciphersuite.getKeyExchangeAlgorithm().calculateClientFinished(TinyTLS.this.m_handshakemessages));
            LogHolder.log(7, LogType.MISC, "[CLIENT_FINISHED]");
        }
    }

    class TLSInputStream
    extends InputStream
    implements ITLSConstants {
        private DataInputStream m_stream;
        private int m_aktPendOffset;
        private int m_aktPendLen;
        private TLSPlaintextRecord m_aktTLSRecord = new TLSPlaintextRecord();
        private int m_ReadRecordState;

        public TLSInputStream(InputStream inputStream) {
            this.m_stream = new DataInputStream(inputStream);
            this.m_aktPendOffset = 0;
            this.m_aktPendLen = 0;
            this.m_ReadRecordState = 0;
        }

        private synchronized void readRecord() throws IOException {
            int n;
            if (this.m_ReadRecordState == 0) {
                this.m_aktTLSRecord.clean();
                try {
                    n = this.m_stream.readByte();
                }
                catch (InterruptedIOException interruptedIOException) {
                    interruptedIOException.bytesTransferred = 0;
                    throw interruptedIOException;
                }
                if (n < 20 || n > 23) {
                    throw new TLSException("SSL Content typeProtocoll not supported: " + n);
                }
                this.m_aktTLSRecord.setType(n);
                this.m_ReadRecordState = 1;
            }
            if (this.m_ReadRecordState == 1) {
                try {
                    n = this.m_stream.readShort();
                }
                catch (InterruptedIOException interruptedIOException) {
                    interruptedIOException.bytesTransferred = 0;
                    throw interruptedIOException;
                }
                if (n != PROTOCOLVERSION_SHORT) {
                    throw new TLSException("Protocol version not supported: " + n);
                }
                this.m_ReadRecordState = 2;
            }
            if (this.m_ReadRecordState == 2) {
                n = 0;
                try {
                    n = this.m_stream.readShort();
                }
                catch (InterruptedIOException interruptedIOException) {
                    interruptedIOException.bytesTransferred = 0;
                    throw interruptedIOException;
                }
                if (n > 16384) {
                    throw new TLSException("Given size of TLSPlaintex record payload exceeds TLSPlaintextRecord.MAX_PAYLOAD_SIZE!");
                }
                this.m_aktTLSRecord.setLength(n);
                this.m_ReadRecordState = 3;
                this.m_aktPendOffset = 0;
            }
            if (this.m_ReadRecordState == 3) {
                n = this.m_aktTLSRecord.getLength() - this.m_aktPendOffset;
                while (n > 0) {
                    try {
                        byte[] arrby = this.m_aktTLSRecord.getData();
                        int n2 = this.m_stream.read(arrby, this.m_aktPendOffset, n);
                        if (n2 < 0) {
                            throw new EOFException();
                        }
                        n -= n2;
                        this.m_aktPendOffset += n2;
                    }
                    catch (InterruptedIOException interruptedIOException) {
                        this.m_aktPendOffset += interruptedIOException.bytesTransferred;
                        interruptedIOException.bytesTransferred = 0;
                        throw interruptedIOException;
                    }
                }
                this.m_ReadRecordState = 0;
                this.m_aktPendOffset = 0;
            }
        }

        public int read() throws IOException {
            byte[] arrby = new byte[1];
            if (this.read(arrby, 0, 1) < 1) {
                return -1;
            }
            return arrby[0] & 0xFF;
        }

        public int read(byte[] arrby) throws IOException {
            return this.read(arrby, 0, arrby.length);
        }

        public int read(byte[] arrby, int n, int n2) throws IOException {
            block6: while (this.m_aktPendLen < 1) {
                this.readRecord();
                try {
                    switch (this.m_aktTLSRecord.getType()) {
                        case 23: {
                            TinyTLS.this.m_selectedciphersuite.decode(this.m_aktTLSRecord);
                            this.m_aktPendOffset = 0;
                            this.m_aktPendLen = this.m_aktTLSRecord.getLength();
                            continue block6;
                        }
                        case 21: {
                            this.handleAlert();
                            continue block6;
                        }
                    }
                    throw new IOException("Error while decoding application data");
                }
                catch (Throwable throwable) {
                    throw new IOException("Exception by reading next TSL record: " + throwable.getMessage());
                }
            }
            int n3 = Math.min(this.m_aktPendLen, n2);
            System.arraycopy(this.m_aktTLSRecord.getData(), this.m_aktPendOffset, arrby, n, n3);
            this.m_aktPendOffset += n3;
            this.m_aktPendLen -= n3;
            return n3;
        }

        public int available() {
            return this.m_aktPendLen;
        }

        private void gotServerHello(TLSHandshakeRecord tLSHandshakeRecord) throws IOException {
            byte[] arrby;
            int n = 0;
            byte[] arrby2 = tLSHandshakeRecord.getData();
            LogHolder.log(7, LogType.MISC, "[SERVER_HELLO] SSLVERSION :" + arrby2[n] + "." + arrby2[n + 1]);
            if (arrby2[n] != PROTOCOLVERSION[0] || arrby2[n + 1] != PROTOCOLVERSION[1]) {
                throw new TLSException("Server replies with wrong protocoll");
            }
            TinyTLS.access$202(TinyTLS.this, ByteArrayUtil.copy(arrby2, n + 2, 32));
            byte[] arrby3 = new byte[]{};
            byte by = arrby2[n + 34];
            if (by > 0) {
                arrby3 = ByteArrayUtil.copy(arrby2, n + 35, by);
            }
            LogHolder.log(7, LogType.MISC, "[SERVER_HELLO] Laenge der SessionID : " + by);
            byte[] arrby4 = ByteArrayUtil.copy(arrby2, n + 35 + by, 2);
            LogHolder.log(7, LogType.MISC, "[SERVER_HELLO] Ciphersuite : " + arrby4[0] + " " + arrby4[1]);
            byte[] arrby5 = ByteArrayUtil.copy(arrby2, n + 37 + by, 1);
            LogHolder.log(7, LogType.MISC, "[SERVER_HELLO] Kompression : " + arrby5[0]);
            CipherSuite cipherSuite = null;
            for (int i = 0; i < TinyTLS.this.m_supportedciphersuites.size() && ((arrby = (cipherSuite = (CipherSuite)TinyTLS.this.m_supportedciphersuites.elementAt(i)).getCipherSuiteCode())[0] != arrby4[0] || arrby[1] != arrby4[1]); ++i) {
                cipherSuite = null;
            }
            if (cipherSuite == null) {
                throw new TLSException("Unsupported Ciphersuite selected");
            }
            TinyTLS.this.m_selectedciphersuite = cipherSuite;
            TinyTLS.this.m_supportedciphersuites = null;
        }

        private void gotCertificate(TLSHandshakeRecord tLSHandshakeRecord) throws IOException {
            byte[] arrby = tLSHandshakeRecord.getData();
            int n = 0;
            int n2 = tLSHandshakeRecord.getLength();
            Vector<JAPCertificate> vector = new Vector<JAPCertificate>();
            byte[] arrby2 = ByteArrayUtil.copy(arrby, n, 3);
            int n3 = (arrby2[0] & 0xFF) << 16 | (arrby2[1] & 0xFF) << 8 | arrby2[2] & 0xFF;
            int n4 = n + 3;
            arrby2 = ByteArrayUtil.copy(arrby, n4, 3);
            int n5 = (arrby2[0] & 0xFF) << 16 | (arrby2[1] & 0xFF) << 8 | arrby2[2] & 0xFF;
            arrby2 = ByteArrayUtil.copy(arrby, n4 += 3, n5);
            n4 += n5;
            JAPCertificate jAPCertificate = JAPCertificate.getInstance(arrby2);
            LogHolder.log(7, LogType.MISC, "[SERVER_CERTIFICATE] " + jAPCertificate.getIssuer().toString());
            LogHolder.log(7, LogType.MISC, "[SERVER_CERTIFICATE] " + jAPCertificate.getSubject().toString());
            TinyTLS.this.m_servercertificate = jAPCertificate;
            TinyTLS.this.m_selectedciphersuite.setServerCertificate(jAPCertificate);
            while (n4 - n < n3) {
                arrby2 = ByteArrayUtil.copy(arrby, n4, 3);
                n5 = (arrby2[0] & 0xFF) << 16 | (arrby2[1] & 0xFF) << 8 | arrby2[2] & 0xFF;
                arrby2 = ByteArrayUtil.copy(arrby, n4 += 3, n5);
                n4 += n5;
                jAPCertificate = JAPCertificate.getInstance(arrby2);
                LogHolder.log(7, LogType.MISC, "[NEXT_CERTIFICATE] " + jAPCertificate.getIssuer().toString());
                LogHolder.log(7, LogType.MISC, "[NEXT_CERTIFICATE] " + jAPCertificate.getSubject().toString());
                vector.addElement(jAPCertificate);
            }
            JAPCertificate jAPCertificate2 = TinyTLS.this.m_servercertificate;
            for (int i = 0; i < vector.size(); ++i) {
                JAPCertificate jAPCertificate3 = (JAPCertificate)vector.elementAt(i);
                if (!jAPCertificate2.verify(jAPCertificate3.getPublicKey())) {
                    throw new IOException("TLS Server Certs could not be verified!");
                }
                jAPCertificate2 = jAPCertificate3;
            }
            if (TinyTLS.this.m_checkTrustedRoot && !jAPCertificate2.verify(TinyTLS.this.m_trustedRoot)) {
                throw new IOException("TLS Server Cert could not be verified to be trusted!");
            }
        }

        private void gotServerKeyExchange(TLSHandshakeRecord tLSHandshakeRecord) throws IOException {
            byte[] arrby = tLSHandshakeRecord.getData();
            int n = 0;
            int n2 = tLSHandshakeRecord.getLength();
            TinyTLS.this.m_selectedciphersuite.getKeyExchangeAlgorithm().processServerKeyExchange(arrby, n, n2, TinyTLS.this.m_clientrandom, TinyTLS.this.m_serverrandom, TinyTLS.this.m_servercertificate);
        }

        private void gotCertificateRequest(TLSHandshakeRecord tLSHandshakeRecord) {
            byte[] arrby = tLSHandshakeRecord.getData();
            int n = 0;
            int n2 = tLSHandshakeRecord.getLength();
            TinyTLS.this.m_certificaterequested = true;
            LogHolder.log(7, LogType.MISC, "[SERVER_CERTIFICATE_REQUEST]");
            byte by = arrby[n];
            if (by > 0) {
                TinyTLS.access$902(TinyTLS.this, ByteArrayUtil.copy(arrby, n + 1, by));
            }
        }

        private void gotServerHelloDone() {
            TinyTLS.this.m_serverhellodone = true;
            LogHolder.log(7, LogType.MISC, "[SERVER_HELLO_DONE]");
        }

        private void handleAlert() throws IOException {
            LogHolder.log(7, LogType.MISC, "[TLS] ALERT!");
            if (TinyTLS.this.m_handshakecompleted) {
                TinyTLS.this.m_selectedciphersuite.decode(this.m_aktTLSRecord);
            }
            byte[] arrby = this.m_aktTLSRecord.getData();
            block0 : switch (arrby[0]) {
                case 1: {
                    switch (arrby[1]) {
                        case 0: {
                            LogHolder.log(7, LogType.MISC, "[RECIEVED-ALERT] TYPE=WARNING ; MESSAGE=CLOSE NOTIFY");
                            break block0;
                        }
                    }
                    throw new TLSException("TLSAlert detected!! Level : Warning - Description :" + arrby[1]);
                }
                case 2: {
                    throw new TLSException("TLSAlert detected!! Level : Fatal - Description :" + arrby[1]);
                }
                default: {
                    throw new TLSException("Unknown TLSAlert detected!!");
                }
            }
        }

        protected void readServerHandshakes() throws IOException {
            block11: while (!TinyTLS.this.m_serverhellodone) {
                if (!this.m_aktTLSRecord.hasMoreHandshakeRecords()) {
                    this.readRecord();
                    switch (this.m_aktTLSRecord.getType()) {
                        case 21: {
                            this.handleAlert();
                            break;
                        }
                        case 22: {
                            break;
                        }
                        default: {
                            throw new TLSException("Error while shaking hands");
                        }
                    }
                }
                TLSHandshakeRecord tLSHandshakeRecord = this.m_aktTLSRecord.getNextHandshakeRecord();
                byte[] arrby = tLSHandshakeRecord.getData();
                int n = tLSHandshakeRecord.getType();
                int n2 = tLSHandshakeRecord.getLength();
                TinyTLS.access$1202(TinyTLS.this, ByteArrayUtil.conc(TinyTLS.this.m_handshakemessages, tLSHandshakeRecord.getHeader(), 4));
                TinyTLS.access$1202(TinyTLS.this, ByteArrayUtil.conc(TinyTLS.this.m_handshakemessages, arrby, n2));
                switch (n) {
                    case 2: {
                        this.gotServerHello(tLSHandshakeRecord);
                        continue block11;
                    }
                    case 11: {
                        this.gotCertificate(tLSHandshakeRecord);
                        continue block11;
                    }
                    case 12: {
                        this.gotServerKeyExchange(tLSHandshakeRecord);
                        continue block11;
                    }
                    case 13: {
                        this.gotCertificateRequest(tLSHandshakeRecord);
                        continue block11;
                    }
                    case 14: {
                        this.gotServerHelloDone();
                        continue block11;
                    }
                }
                throw new TLSException("Unexpected Handshake type: " + n);
            }
        }

        protected void readServerFinished() throws IOException {
            this.readRecord();
            switch (this.m_aktTLSRecord.getType()) {
                case 20: {
                    if (this.m_aktTLSRecord.getLength() != 1 || this.m_aktTLSRecord.getData()[0] != 1) break;
                    LogHolder.log(7, LogType.MISC, "[SERVER_CHANGE_CIPHER_SPEC]");
                    break;
                }
                case 21: {
                    this.handleAlert();
                    break;
                }
                default: {
                    throw new TLSException("Error while shaking hands");
                }
            }
            this.readRecord();
            switch (this.m_aktTLSRecord.getType()) {
                case 22: {
                    LogHolder.log(7, LogType.MISC, "[SERVER_FINISHED]");
                    TinyTLS.this.m_selectedciphersuite.processServerFinished(this.m_aktTLSRecord, TinyTLS.this.m_handshakemessages);
                    break;
                }
                case 21: {
                    this.handleAlert();
                    break;
                }
                default: {
                    throw new TLSException("Error while shaking hands");
                }
            }
        }
    }

    private static interface ITLSConstants {
        public static final int STATE_START = 0;
        public static final int STATE_VERSION = 1;
        public static final int STATE_LENGTH = 2;
        public static final int STATE_PAYLOAD = 3;
    }
}

