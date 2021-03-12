/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.ciphersuites;

import anon.crypto.JAPCertificate;
import anon.crypto.MyRandom;
import anon.crypto.tinytls.TLSException;
import anon.crypto.tinytls.TLSPlaintextRecord;
import anon.crypto.tinytls.keyexchange.Key_Exchange;
import anon.util.ByteArrayUtil;
import java.math.BigInteger;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public abstract class CipherSuite {
    private byte[] m_ciphersuitecode;
    protected String m_ciphersuitename = "Name not set";
    private Key_Exchange m_keyexchangealgorithm = null;
    private JAPCertificate m_servercertificate = null;
    protected CBCBlockCipher m_decryptcipher;
    protected CBCBlockCipher m_encryptcipher;
    private HMac m_hmacInput = new HMac(new SHA1Digest());
    private HMac m_hmacOutput = new HMac(new SHA1Digest());
    private MyRandom m_Random;
    protected long m_writesequenznumber;
    protected long m_readsequenznumber;
    protected byte[] m_clientwritekey = null;
    protected byte[] m_clientmacsecret = null;
    protected byte[] m_clientwriteIV = null;
    protected byte[] m_serverwritekey = null;
    protected byte[] m_servermacsecret = null;
    protected byte[] m_serverwriteIV = null;

    public CipherSuite(byte[] arrby) throws TLSException {
        if (arrby.length != 2) {
            throw new TLSException("wrong CipherSuiteCode ");
        }
        this.m_ciphersuitecode = arrby;
        this.m_writesequenznumber = 0L;
        this.m_readsequenznumber = 0L;
        this.m_Random = new MyRandom();
    }

    protected void setKeyExchangeAlgorithm(Key_Exchange key_Exchange) {
        this.m_keyexchangealgorithm = key_Exchange;
    }

    public Key_Exchange getKeyExchangeAlgorithm() {
        return this.m_keyexchangealgorithm;
    }

    public void setServerCertificate(JAPCertificate jAPCertificate) {
        this.m_servercertificate = jAPCertificate;
    }

    public byte[] getCipherSuiteCode() {
        return this.m_ciphersuitecode;
    }

    public void processClientKeyExchange(BigInteger bigInteger) {
        this.m_keyexchangealgorithm.processClientKeyExchange(bigInteger);
        this.calculateKeys(this.m_keyexchangealgorithm.calculateKeys(), false);
        this.m_hmacInput.init(new KeyParameter(this.m_servermacsecret));
        this.m_hmacOutput.init(new KeyParameter(this.m_clientmacsecret));
    }

    public byte[] calculateClientKeyExchange() throws TLSException {
        byte[] arrby = this.m_keyexchangealgorithm.calculateClientKeyExchange();
        this.calculateKeys(this.m_keyexchangealgorithm.calculateKeys(), true);
        this.m_hmacInput.init(new KeyParameter(this.m_servermacsecret));
        this.m_hmacOutput.init(new KeyParameter(this.m_clientmacsecret));
        return arrby;
    }

    public void processServerFinished(TLSPlaintextRecord tLSPlaintextRecord, byte[] arrby) throws TLSException {
        this.decode(tLSPlaintextRecord);
        this.m_keyexchangealgorithm.processServerFinished(tLSPlaintextRecord.getData(), tLSPlaintextRecord.getLength(), arrby);
    }

    public void encode(TLSPlaintextRecord tLSPlaintextRecord) {
        int n;
        int n2 = tLSPlaintextRecord.getLength();
        byte[] arrby = tLSPlaintextRecord.getData();
        byte[] arrby2 = tLSPlaintextRecord.getHeader();
        this.m_hmacOutput.reset();
        this.m_hmacOutput.update(ByteArrayUtil.inttobyte(this.m_writesequenznumber, 8), 0, 8);
        ++this.m_writesequenznumber;
        this.m_hmacOutput.update(arrby2, 0, arrby2.length);
        this.m_hmacOutput.update(arrby, 0, n2);
        this.m_hmacOutput.doFinal(arrby, n2);
        int n3 = this.m_Random.nextInt(240);
        n3 += this.m_encryptcipher.getBlockSize() - ((n2 += this.m_hmacOutput.getMacSize()) + 1 + n3) % this.m_encryptcipher.getBlockSize();
        for (n = 0; n < n3 + 1; ++n) {
            arrby[n2++] = (byte)n3;
        }
        for (n = 0; n < n2; n += this.m_encryptcipher.getBlockSize()) {
            this.m_encryptcipher.processBlock(arrby, n, arrby, n);
        }
        tLSPlaintextRecord.setLength(n2);
    }

    public void decode(TLSPlaintextRecord tLSPlaintextRecord) throws TLSException {
        int n;
        int n2 = tLSPlaintextRecord.getLength();
        byte[] arrby = tLSPlaintextRecord.getData();
        if (n2 % this.m_decryptcipher.getBlockSize() != 0 || n2 < this.m_hmacInput.getMacSize()) {
            throw new TLSException("wrong payload len!");
        }
        for (n = 0; n < n2; n += this.m_decryptcipher.getBlockSize()) {
            this.m_decryptcipher.processBlock(arrby, n, arrby, n);
        }
        n = n2 - this.m_hmacInput.getMacSize() - 1;
        byte by = arrby[n2 - 1];
        int n3 = by & 0xFF;
        if (n3 > n2 - 2) {
            throw new TLSException("wrong Padding len detected", 2, 51);
        }
        for (int i = n2 - 1; i > n2 - n3 - 2; --i) {
            if (arrby[i] == by) continue;
            throw new TLSException("wrong Padding detected", 2, 51);
        }
        tLSPlaintextRecord.setLength(n -= n3);
        this.m_hmacInput.reset();
        this.m_hmacInput.update(ByteArrayUtil.inttobyte(this.m_readsequenznumber, 8), 0, 8);
        ++this.m_readsequenznumber;
        byte[] arrby2 = tLSPlaintextRecord.getHeader();
        this.m_hmacInput.update(arrby2, 0, arrby2.length);
        this.m_hmacInput.update(arrby, 0, n);
        byte[] arrby3 = new byte[this.m_hmacInput.getMacSize()];
        this.m_hmacInput.doFinal(arrby3, 0);
        if (!ByteArrayUtil.equal(arrby, n, arrby3, 0, arrby3.length)) {
            throw new TLSException("Wrong MAC detected!!!", 2, 20);
        }
    }

    protected abstract void calculateKeys(byte[] var1, boolean var2);

    public String toString() {
        return this.m_ciphersuitename;
    }
}

