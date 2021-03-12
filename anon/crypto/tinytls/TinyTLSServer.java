/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

import anon.crypto.IMyPrivateKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyRSAPrivateKey;
import anon.crypto.tinytls.TinyTLSServerSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TinyTLSServer
extends ServerSocket {
    private JAPCertificate m_Certificate = null;
    private IMyPrivateKey m_PrivateKey = null;
    private MyDSAPrivateKey m_DSSKey = null;
    private MyRSAPrivateKey m_RSAKey = null;
    private JAPCertificate m_DSSCertificate = null;
    private JAPCertificate m_RSACertificate = null;
    private TinyTLSServerSocket tls = null;

    public TinyTLSServer(int n) throws IOException {
        super(n);
    }

    public TinyTLSServer(int n, int n2, InetAddress inetAddress) throws IOException {
        super(n, n2, inetAddress);
    }

    public void setDSSParameters(JAPCertificate jAPCertificate, MyDSAPrivateKey myDSAPrivateKey) {
        this.m_DSSCertificate = jAPCertificate;
        this.m_DSSKey = myDSAPrivateKey;
    }

    public void setRSAParameters(JAPCertificate jAPCertificate, MyRSAPrivateKey myRSAPrivateKey) {
        this.m_RSACertificate = jAPCertificate;
        this.m_RSAKey = myRSAPrivateKey;
    }

    public Socket accept() throws IOException {
        return this.accept(0L);
    }

    public Socket accept(long l) throws IOException {
        Socket socket = super.accept();
        this.tls = new TinyTLSServerSocket(socket, l);
        this.tls.setDSSParameters(this.m_DSSCertificate, this.m_DSSKey);
        this.tls.setRSAParameters(this.m_RSACertificate, this.m_RSAKey);
        return this.tls;
    }
}

