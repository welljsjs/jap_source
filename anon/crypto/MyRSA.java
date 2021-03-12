/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.MyRSAPrivateKey;
import anon.crypto.MyRSAPublicKey;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;

public class MyRSA {
    RSAEngine m_RSAEngine = new RSAEngine();
    OAEPEncoding m_OAEP;
    PKCS1Encoding m_PKCS1;

    public MyRSA() {
        this.m_OAEP = new OAEPEncoding(this.m_RSAEngine);
        this.m_PKCS1 = new PKCS1Encoding(this.m_RSAEngine);
    }

    public MyRSA(Digest digest) {
        this.m_OAEP = new OAEPEncoding(this.m_RSAEngine, digest);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(MyRSAPublicKey myRSAPublicKey) throws Exception {
        RSAEngine rSAEngine = this.m_RSAEngine;
        synchronized (rSAEngine) {
            this.m_RSAEngine.init(true, myRSAPublicKey.getParams());
            this.m_PKCS1.init(true, myRSAPublicKey.getParams());
            this.m_OAEP.init(true, myRSAPublicKey.getParams());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(MyRSAPrivateKey myRSAPrivateKey) throws Exception {
        RSAEngine rSAEngine = this.m_RSAEngine;
        synchronized (rSAEngine) {
            this.m_RSAEngine.init(false, myRSAPrivateKey.getParams());
            this.m_PKCS1.init(false, myRSAPrivateKey.getParams());
            this.m_OAEP.init(false, myRSAPrivateKey.getParams());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] processBlock(byte[] arrby, int n, int n2) throws Exception {
        RSAEngine rSAEngine = this.m_RSAEngine;
        synchronized (rSAEngine) {
            return this.m_RSAEngine.processBlock(arrby, n, n2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] processBlockOAEP(byte[] arrby, int n, int n2) throws Exception {
        RSAEngine rSAEngine = this.m_RSAEngine;
        synchronized (rSAEngine) {
            return this.m_OAEP.processBlock(arrby, n, n2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] processBlockPKCS1(byte[] arrby, int n, int n2) throws Exception {
        RSAEngine rSAEngine = this.m_RSAEngine;
        synchronized (rSAEngine) {
            return this.m_PKCS1.processBlock(arrby, n, n2);
        }
    }
}

