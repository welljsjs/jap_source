/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

import anon.crypto.IMyPrivateKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyRSAPrivateKey;
import anon.crypto.tinytls.TLSException;
import anon.crypto.tinytls.TLSPlaintextRecord;
import anon.crypto.tinytls.ciphersuites.CipherSuite;
import anon.crypto.tinytls.ciphersuites.DHE_DSS_WITH_3DES_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_DSS_WITH_AES_128_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_DSS_WITH_DES_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_RSA_WITH_3DES_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_RSA_WITH_AES_128_CBC_SHA;
import anon.crypto.tinytls.ciphersuites.DHE_RSA_WITH_DES_CBC_SHA;
import anon.crypto.tinytls.keyexchange.DHE_DSS_Key_Exchange;
import anon.crypto.tinytls.keyexchange.DHE_RSA_Key_Exchange;
import anon.util.ByteArrayUtil;
import anon.util.SocketGuard;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class TinyTLSServerSocket
extends Socket {
    public static byte[] PROTOCOLVERSION = new byte[]{3, 1};
    private static int PROTOCOLVERSION_SHORT = 769;
    private Vector m_supportedciphersuites;
    private CipherSuite m_selectedciphersuite = null;
    private Thread m_threadCloseGuard = null;
    private Object SYNC_CLOSE = new Object();
    private Socket m_Socket;
    private TLSInputStream m_istream;
    private TLSOutputStream m_ostream;
    private boolean m_handshakecompleted;
    private byte[] m_clientrandom;
    private byte[] m_serverrandom;
    private JAPCertificate m_servercertificate;
    private IMyPrivateKey m_privatekey;
    private MyDSAPrivateKey m_DSSKey;
    private MyRSAPrivateKey m_RSAKey;
    private JAPCertificate m_DSSCertificate;
    private JAPCertificate m_RSACertificate;
    private byte[] m_handshakemessages;
    private boolean m_encrypt;

    public InetAddress getInetAddress() {
        return this.m_Socket.getInetAddress();
    }

    public TinyTLSServerSocket(Socket socket) throws IOException {
        this(socket, 0L);
    }

    public TinyTLSServerSocket(Socket socket, final long l) throws IOException {
        this.m_Socket = socket;
        if (l > 0L) {
            Runnable runnable = new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    Object object = TinyTLSServerSocket.this.SYNC_CLOSE;
                    synchronized (object) {
                        if (TinyTLSServerSocket.this.m_threadCloseGuard != null) {
                            try {
                                TinyTLSServerSocket.this.SYNC_CLOSE.wait(l);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            if (!SocketGuard.isClosed(TinyTLSServerSocket.this.m_Socket)) {
                                LogHolder.log(1, LogType.NET, "CloseGuard: Closing TLS socket after " + l + " milliseconds!");
                                try {
                                    TinyTLSServerSocket.this.close();
                                }
                                catch (IOException iOException) {
                                    LogHolder.log(1, LogType.NET, iOException);
                                }
                            }
                        }
                    }
                }
            };
            this.m_threadCloseGuard = new Thread(runnable);
            this.m_threadCloseGuard.start();
        }
        this.m_handshakecompleted = false;
        this.m_encrypt = false;
        this.m_supportedciphersuites = new Vector();
        this.m_istream = new TLSInputStream(socket.getInputStream());
        this.m_ostream = new TLSOutputStream(socket.getOutputStream());
        this.m_DSSCertificate = null;
        this.m_DSSKey = null;
        this.m_RSACertificate = null;
        this.m_RSAKey = null;
    }

    public void addCipherSuite(CipherSuite cipherSuite) {
        if (!this.m_supportedciphersuites.contains(cipherSuite)) {
            if (cipherSuite.getKeyExchangeAlgorithm() instanceof DHE_DSS_Key_Exchange && this.m_DSSKey != null && this.m_DSSCertificate != null || cipherSuite.getKeyExchangeAlgorithm() instanceof DHE_RSA_Key_Exchange && this.m_RSAKey != null && this.m_RSACertificate != null) {
                this.m_supportedciphersuites.addElement(cipherSuite);
            } else {
                LogHolder.log(7, LogType.MISC, "[CIPHERSUITE NOT ADDED] : Please check if you've set the Certificate and the Private Key");
            }
        }
    }

    public void startHandshake() throws IOException {
        if (this.m_supportedciphersuites.isEmpty()) {
            if (this.m_DSSKey != null && this.m_DSSCertificate != null) {
                this.addCipherSuite(new DHE_DSS_WITH_3DES_CBC_SHA());
                this.addCipherSuite(new DHE_DSS_WITH_AES_128_CBC_SHA());
                this.addCipherSuite(new DHE_DSS_WITH_DES_CBC_SHA());
            }
            if (this.m_RSAKey != null && this.m_RSACertificate != null) {
                this.addCipherSuite(new DHE_RSA_WITH_3DES_CBC_SHA());
                this.addCipherSuite(new DHE_RSA_WITH_AES_128_CBC_SHA());
                this.addCipherSuite(new DHE_RSA_WITH_DES_CBC_SHA());
            }
        }
        this.m_handshakemessages = new byte[0];
        try {
            this.m_istream.readClientHello();
            this.m_ostream.sendServerHandshakes();
            this.m_istream.readClientKeyExchange();
            this.m_istream.readClientFinished();
            this.m_ostream.sendChangeCipherSpec();
            this.m_ostream.sendServerFinished();
        }
        catch (TLSException tLSException) {
            if (tLSException.Alert()) {
                this.m_ostream.send(21, new byte[]{tLSException.getAlertLevel(), tLSException.getAlertDescription()}, 0, 2);
            }
            throw tLSException;
        }
        this.m_handshakecompleted = true;
    }

    public void setDSSParameters(JAPCertificate jAPCertificate, MyDSAPrivateKey myDSAPrivateKey) {
        this.m_DSSCertificate = jAPCertificate;
        this.m_DSSKey = myDSAPrivateKey;
    }

    public void setRSAParameters(JAPCertificate jAPCertificate, MyRSAPrivateKey myRSAPrivateKey) {
        this.m_RSACertificate = jAPCertificate;
        this.m_RSAKey = myRSAPrivateKey;
    }

    public InputStream getInputStream() {
        return this.m_istream;
    }

    public OutputStream getOutputStream() {
        return this.m_ostream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        IOException iOException;
        block19: {
            block18: {
                block17: {
                    iOException = null;
                    try {
                        if (this.m_ostream != null) {
                            this.m_ostream.send(21, new byte[]{1, 0}, 0, 2);
                        }
                    }
                    catch (IOException iOException2) {
                        iOException = iOException2;
                    }
                    try {
                        if (this.m_ostream != null) {
                            this.m_ostream.close();
                        }
                    }
                    catch (IOException iOException3) {
                        if (iOException != null) break block17;
                        iOException = iOException3;
                    }
                }
                try {
                    if (this.m_istream != null) {
                        this.m_istream.close();
                    }
                }
                catch (IOException iOException4) {
                    if (iOException != null) break block18;
                    iOException = iOException4;
                }
            }
            try {
                if (this.m_Socket != null) {
                    this.m_Socket.close();
                }
            }
            catch (IOException iOException5) {
                if (iOException != null) break block19;
                iOException = iOException5;
            }
        }
        if (this.m_threadCloseGuard != null) {
            Object object = this.SYNC_CLOSE;
            synchronized (object) {
                this.SYNC_CLOSE.notify();
                this.m_threadCloseGuard = null;
            }
        }
        if (iOException != null) {
            throw iOException;
        }
    }

    public boolean isClosed() {
        return SocketGuard.isClosed(this.m_Socket);
    }

    public void setSoTimeout(int n) throws SocketException {
        if (this.m_Socket != null) {
            this.m_Socket.setSoTimeout(n);
        }
    }

    static /* synthetic */ byte[] access$302(TinyTLSServerSocket tinyTLSServerSocket, byte[] arrby) {
        tinyTLSServerSocket.m_clientrandom = arrby;
        return arrby;
    }

    static /* synthetic */ byte[] access$1102(TinyTLSServerSocket tinyTLSServerSocket, byte[] arrby) {
        tinyTLSServerSocket.m_handshakemessages = arrby;
        return arrby;
    }

    static /* synthetic */ byte[] access$1302(TinyTLSServerSocket tinyTLSServerSocket, byte[] arrby) {
        tinyTLSServerSocket.m_serverrandom = arrby;
        return arrby;
    }

    class TLSOutputStream
    extends OutputStream {
        private DataOutputStream m_stream;
        private TLSPlaintextRecord m_aktTLSRecord = new TLSPlaintextRecord();

        public TLSOutputStream(OutputStream outputStream) {
            this.m_stream = new DataOutputStream(outputStream);
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

        public void flush() throws IOException {
            this.m_stream.flush();
        }

        private synchronized void send(int n, byte[] arrby, int n2, int n3) throws IOException {
            byte[] arrby2 = this.m_aktTLSRecord.getData();
            System.arraycopy(arrby, n2, arrby2, 0, n3);
            this.m_aktTLSRecord.setLength(n3);
            this.m_aktTLSRecord.setType(n);
            if (TinyTLSServerSocket.this.m_encrypt) {
                TinyTLSServerSocket.this.m_selectedciphersuite.encode(this.m_aktTLSRecord);
            }
            try {
                this.m_stream.write(this.m_aktTLSRecord.getHeader());
            }
            catch (SocketException socketException) {
                throw new TLSException(socketException.getMessage(), 2, 0);
            }
            this.m_stream.write(arrby2, 0, this.m_aktTLSRecord.getLength());
            this.m_stream.flush();
        }

        public void sendHandshake(int n, byte[] arrby) throws IOException {
            byte[] arrby2 = ByteArrayUtil.conc(new byte[]{(byte)n}, ByteArrayUtil.inttobyte(arrby.length, 3), arrby);
            this.send(22, arrby2, 0, arrby2.length);
            TinyTLSServerSocket.access$1102(TinyTLSServerSocket.this, ByteArrayUtil.conc(TinyTLSServerSocket.this.m_handshakemessages, arrby2));
        }

        public void sendServerHello() throws IOException {
            byte[] arrby = new byte[28];
            byte[] arrby2 = new byte[]{0};
            byte[] arrby3 = TinyTLSServerSocket.this.m_selectedciphersuite.getCipherSuiteCode();
            byte[] arrby4 = new byte[]{0};
            byte[] arrby5 = ByteArrayUtil.inttobyte(System.currentTimeMillis() / 1000L, 4);
            Random random = new Random(System.currentTimeMillis());
            random.nextBytes(arrby);
            TinyTLSServerSocket.access$1302(TinyTLSServerSocket.this, ByteArrayUtil.conc(arrby5, arrby));
            byte[] arrby6 = ByteArrayUtil.conc(PROTOCOLVERSION, TinyTLSServerSocket.this.m_serverrandom, arrby2, arrby3, arrby4);
            this.sendHandshake(2, arrby6);
        }

        public void sendServerCertificate() throws IOException {
            byte[] arrby = TinyTLSServerSocket.this.m_servercertificate.toByteArray();
            byte[] arrby2 = ByteArrayUtil.inttobyte(arrby.length, 3);
            byte[] arrby3 = ByteArrayUtil.conc(arrby2, arrby);
            arrby2 = ByteArrayUtil.inttobyte(arrby3.length, 3);
            arrby3 = ByteArrayUtil.conc(arrby2, arrby3);
            this.sendHandshake(11, arrby3);
        }

        public void sendServerKeyExchange() throws IOException {
            this.sendHandshake(12, TinyTLSServerSocket.this.m_selectedciphersuite.getKeyExchangeAlgorithm().generateServerKeyExchange(TinyTLSServerSocket.this.m_privatekey, TinyTLSServerSocket.this.m_clientrandom, TinyTLSServerSocket.this.m_serverrandom));
        }

        public void sendServerHelloDone() throws IOException {
            this.sendHandshake(14, new byte[0]);
        }

        public void sendServerHandshakes() throws IOException {
            this.sendServerHello();
            this.sendServerCertificate();
            this.sendServerKeyExchange();
            this.sendServerHelloDone();
        }

        public void sendChangeCipherSpec() throws IOException {
            TinyTLSServerSocket.this.m_encrypt = false;
            this.send(20, new byte[]{1}, 0, 1);
            TinyTLSServerSocket.this.m_encrypt = true;
        }

        public void sendServerFinished() throws IOException {
            this.sendHandshake(20, TinyTLSServerSocket.this.m_selectedciphersuite.getKeyExchangeAlgorithm().calculateServerFinished(TinyTLSServerSocket.this.m_handshakemessages));
        }
    }

    class TLSInputStream
    extends InputStream {
        private DataInputStream m_stream;
        private int m_aktPendOffset;
        private int m_aktPendLen;
        private TLSPlaintextRecord m_aktTLSRecord = new TLSPlaintextRecord();
        private int m_ReadRecordState;
        private static final int STATE_START = 0;
        private static final int STATE_VERSION = 1;
        private static final int STATE_LENGTH = 2;
        private static final int STATE_PAYLOAD = 3;

        public TLSInputStream(InputStream inputStream) {
            this.m_stream = new DataInputStream(inputStream);
            this.m_aktPendOffset = 0;
            this.m_aktPendLen = 0;
            this.m_ReadRecordState = 0;
        }

        private synchronized void readRecord() throws IOException {
            int n;
            if (this.m_ReadRecordState == 0) {
                try {
                    n = this.m_stream.readByte();
                }
                catch (InterruptedIOException interruptedIOException) {
                    interruptedIOException.bytesTransferred = 0;
                    throw interruptedIOException;
                }
                catch (SocketException socketException) {
                    throw new TLSException(socketException.getMessage(), 2, 0);
                }
                catch (EOFException eOFException) {
                    throw new TLSException("EOF", 2, 0);
                }
                if (n < 20 || n > 23) {
                    throw new TLSException("SSL content type protocol not supported: " + n, 2, 10);
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
                    throw new TLSException("Protocol version not supported" + n, 2, 70);
                }
                this.m_ReadRecordState = 2;
            }
            if (this.m_ReadRecordState == 2) {
                try {
                    n = this.m_stream.readShort();
                }
                catch (InterruptedIOException interruptedIOException) {
                    interruptedIOException.bytesTransferred = 0;
                    throw interruptedIOException;
                }
                if (n < 0) {
                    throw new TLSException("Wrong record len", 2, 70);
                }
                this.m_aktTLSRecord.setLength(n);
                this.m_ReadRecordState = 3;
                this.m_aktPendOffset = 0;
            }
            if (this.m_ReadRecordState == 3) {
                n = this.m_aktTLSRecord.getLength() - this.m_aktPendOffset;
                byte[] arrby = this.m_aktTLSRecord.getData();
                while (n > 0) {
                    try {
                        int n2 = this.m_stream.read(arrby, this.m_aktPendOffset, n);
                        if (n2 < 0) {
                            throw new TLSException("EOF", 2, 0);
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
                        case 21: {
                            this.handleAlert();
                        }
                        case 23: {
                            TinyTLSServerSocket.this.m_selectedciphersuite.decode(this.m_aktTLSRecord);
                            this.m_aktPendOffset = 0;
                            this.m_aktPendLen = this.m_aktTLSRecord.getLength();
                            continue block6;
                        }
                    }
                    throw new TLSException("Error while decoding application data", 2, 10);
                }
                catch (Throwable throwable) {
                    throw new TLSException("Exception by reading next TSL record: " + throwable.getMessage(), 2, 80);
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

        private void handleAlert() throws IOException {
            LogHolder.log(7, LogType.MISC, "[TLS] ALERT!");
            if (TinyTLSServerSocket.this.m_handshakecompleted) {
                TinyTLSServerSocket.this.m_selectedciphersuite.decode(this.m_aktTLSRecord);
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

        public void readClientHello() throws IOException {
            this.readRecord();
            byte[] arrby = this.m_aktTLSRecord.getData();
            if (this.m_aktTLSRecord.getType() == 22 && arrby[0] == 1) {
                if ((arrby[4] << 8 | arrby[5]) == PROTOCOLVERSION_SHORT) {
                    TinyTLSServerSocket.access$302(TinyTLSServerSocket.this, new byte[32]);
                    System.arraycopy(arrby, 6, TinyTLSServerSocket.this.m_clientrandom, 0, 32);
                    if (arrby[38] != 0) {
                        throw new TLSException("Client wants to reuse another session, but this is not supportet yet", 2, 40);
                    }
                    try {
                        int n;
                        int n2;
                        int n3 = (arrby[39] & 0xFF) << 8 | arrby[40] & 0xFF;
                        block2: for (n2 = 41; n3 + 41 > n2 && TinyTLSServerSocket.this.m_selectedciphersuite == null; n2 += 2) {
                            for (n = 0; n < TinyTLSServerSocket.this.m_supportedciphersuites.size(); ++n) {
                                CipherSuite cipherSuite = (CipherSuite)TinyTLSServerSocket.this.m_supportedciphersuites.elementAt(n);
                                byte[] arrby2 = cipherSuite.getCipherSuiteCode();
                                if (arrby[n2] != arrby2[0] || arrby[n2 + 1] != arrby2[1]) continue;
                                TinyTLSServerSocket.this.m_selectedciphersuite = cipherSuite;
                                if (cipherSuite.getKeyExchangeAlgorithm() instanceof DHE_DSS_Key_Exchange) {
                                    TinyTLSServerSocket.this.m_servercertificate = TinyTLSServerSocket.this.m_DSSCertificate;
                                    TinyTLSServerSocket.this.m_privatekey = TinyTLSServerSocket.this.m_DSSKey;
                                    continue block2;
                                }
                                if (cipherSuite.getKeyExchangeAlgorithm() instanceof DHE_RSA_Key_Exchange) {
                                    TinyTLSServerSocket.this.m_servercertificate = TinyTLSServerSocket.this.m_RSACertificate;
                                    TinyTLSServerSocket.this.m_privatekey = TinyTLSServerSocket.this.m_RSAKey;
                                    continue block2;
                                }
                                LogHolder.log(7, LogType.MISC, "[ERROR!!!] : KeyExchangeAlgorithm not supported yet.(should never happen)");
                                continue block2;
                            }
                        }
                        if (TinyTLSServerSocket.this.m_selectedciphersuite == null) {
                            throw new TLSException("no supported ciphersuite found", 2, 40);
                        }
                        n2 = n3 + 41;
                        n = arrby[n2];
                        if (n == 0) {
                            throw new TLSException("no compressionalgorithm defined. you need at least one (for example no_compression)", 2, 50);
                        }
                        while (n != 0) {
                            if (arrby[++n2] == 0) {
                                TinyTLSServerSocket.access$1102(TinyTLSServerSocket.this, ByteArrayUtil.conc(TinyTLSServerSocket.this.m_handshakemessages, arrby, this.m_aktTLSRecord.getLength()));
                                return;
                            }
                            --n;
                        }
                        throw new TLSException("no supportet compressionalgorithm found", 2, 40);
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                        throw new TLSException("client hello is not long enough", 2, 50);
                    }
                }
                throw new TLSException("this Protocol is not supported", 2, 70);
            }
            throw new TLSException("Client hello expected but another message was received", 2, 10);
        }

        public void readClientKeyExchange() throws IOException {
            this.readRecord();
            byte[] arrby = this.m_aktTLSRecord.getData();
            try {
                if (arrby[0] == 16) {
                    int n = (arrby[4] & 0xFF) << 8 | arrby[5];
                    byte[] arrby2 = ByteArrayUtil.copy(arrby, 6, this.m_aktTLSRecord.getLength() - 6);
                    arrby2 = ByteArrayUtil.conc(new byte[]{0}, arrby2);
                    BigInteger bigInteger = new BigInteger(arrby2);
                    TinyTLSServerSocket.this.m_selectedciphersuite.processClientKeyExchange(bigInteger);
                    TinyTLSServerSocket.access$1102(TinyTLSServerSocket.this, ByteArrayUtil.conc(TinyTLSServerSocket.this.m_handshakemessages, arrby, this.m_aktTLSRecord.getLength()));
                    return;
                }
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                throw new TLSException(arrayIndexOutOfBoundsException.getLocalizedMessage(), 2, 50);
            }
            throw new TLSException("Client Key Exchange expected, but another messagetype was recieved", 2, 10);
        }

        public void readClientFinished() throws IOException {
            this.readRecord();
            byte[] arrby = this.m_aktTLSRecord.getData();
            if (this.m_aktTLSRecord.getType() != 20 || this.m_aktTLSRecord.getLength() != 1 || arrby[0] != 1) {
                throw new TLSException("Change Cipher Spec expected", 2, 10);
            }
            TinyTLSServerSocket.this.m_encrypt = true;
            this.readRecord();
            TinyTLSServerSocket.this.m_selectedciphersuite.decode(this.m_aktTLSRecord);
            try {
                if (arrby[0] == 20) {
                    byte[] arrby2 = ByteArrayUtil.copy(arrby, 4, 12);
                    TinyTLSServerSocket.this.m_selectedciphersuite.getKeyExchangeAlgorithm().processClientFinished(arrby2, TinyTLSServerSocket.this.m_handshakemessages);
                    TinyTLSServerSocket.access$1102(TinyTLSServerSocket.this, ByteArrayUtil.conc(TinyTLSServerSocket.this.m_handshakemessages, arrby, this.m_aktTLSRecord.getLength()));
                    return;
                }
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                throw new TLSException(arrayIndexOutOfBoundsException.getLocalizedMessage(), 2, 50);
            }
            throw new TLSException("Client Finish message expected, but another message was recieved", 2, 10);
        }
    }
}

