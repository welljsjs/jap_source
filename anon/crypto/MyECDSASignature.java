/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.IMySignature;
import anon.crypto.MyDSASignature;
import anon.crypto.MyECPrivateKey;
import anon.crypto.MyECPublicKey;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.signers.ECDSASigner;

public final class MyECDSASignature
implements IMySignature {
    private static final AlgorithmIdentifier ms_identifier = new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA1);
    SHA1Digest m_digest = new SHA1Digest();
    ECDSASigner m_signatureAlgorithm = new ECDSASigner();
    private Key m_initKey;

    public byte[] encodeForXMLSignature(byte[] arrby) {
        int n = ((MyECPrivateKey)this.m_initKey).getPrivateParams().getParameters().getN().toByteArray().length;
        byte by = arrby[3];
        byte by2 = arrby[3 + by + 2];
        byte[] arrby2 = new byte[2 * n];
        for (int i = 0; i < 2 * n; ++i) {
            arrby2[i] = 0;
        }
        System.arraycopy(arrby, 4, arrby2, n - by, by);
        System.arraycopy(arrby, 4 + by + 2, arrby2, 2 * n - by2, by2);
        return arrby2;
    }

    public byte[] decodeForXMLSignature(byte[] arrby) {
        byte[] arrby2;
        int n = ((MyECPublicKey)this.m_initKey).getPublicParams().getParameters().getN().toByteArray().length;
        if (arrby.length != 2 * n) {
            return null;
        }
        byte[] arrby3 = new byte[n];
        byte[] arrby4 = new byte[n];
        System.arraycopy(arrby, 0, arrby3, 0, n);
        System.arraycopy(arrby, n, arrby4, 0, n);
        try {
            arrby2 = MyDSASignature.derEncode(new BigInteger(arrby3), new BigInteger(arrby4));
        }
        catch (IOException iOException) {
            arrby2 = null;
        }
        return arrby2;
    }

    public String getXMLSignatureAlgorithmReference() {
        return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    }

    public synchronized void initSign(IMyPrivateKey iMyPrivateKey) throws InvalidKeyException {
        try {
            MyECPrivateKey myECPrivateKey = (MyECPrivateKey)iMyPrivateKey;
            this.m_signatureAlgorithm.init(true, myECPrivateKey.getPrivateParams());
            this.m_initKey = iMyPrivateKey;
        }
        catch (Exception exception) {
            throw new InvalidKeyException("MyECDSASignautre - initVerify - dont know how to handle the given key");
        }
    }

    public synchronized void initVerify(IMyPublicKey iMyPublicKey) throws InvalidKeyException {
        try {
            MyECPublicKey myECPublicKey = (MyECPublicKey)iMyPublicKey;
            this.m_signatureAlgorithm.init(false, myECPublicKey.getPublicParams());
            this.m_digest.reset();
            this.m_initKey = iMyPublicKey;
        }
        catch (Exception exception) {
            throw new InvalidKeyException("MyECDSASignautre - initVerify - dont know how to handle the given key");
        }
    }

    public synchronized byte[] sign(byte[] arrby) {
        try {
            this.m_digest.reset();
            this.m_digest.update(arrby, 0, arrby.length);
            byte[] arrby2 = new byte[this.m_digest.getDigestSize()];
            this.m_digest.doFinal(arrby2, 0);
            BigInteger[] arrbigInteger = this.m_signatureAlgorithm.generateSignature(arrby2);
            return MyDSASignature.derEncode(arrbigInteger[0], arrbigInteger[1]);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public synchronized boolean verify(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        try {
            this.m_digest.reset();
            this.m_digest.update(arrby, n, n2);
            byte[] arrby3 = new byte[this.m_digest.getDigestSize()];
            this.m_digest.doFinal(arrby3, 0);
            BigInteger[] arrbigInteger = MyDSASignature.derDecode(arrby2, n3, n4);
            return this.m_signatureAlgorithm.verifySignature(arrby3, arrbigInteger[0], arrbigInteger[1]);
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.CRYPTO, "Signature algorithm does not match!");
            return false;
        }
    }

    public synchronized boolean verify(byte[] arrby, byte[] arrby2) {
        return this.verify(arrby, 0, arrby.length, arrby2, 0, arrby2.length);
    }

    public AlgorithmIdentifier getIdentifier() {
        return ms_identifier;
    }
}

