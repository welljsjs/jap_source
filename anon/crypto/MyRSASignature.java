/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.IMySignature;
import anon.crypto.MyRSAPrivateKey;
import anon.crypto.MyRSAPublicKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;

public final class MyRSASignature
implements IMySignature {
    private static final AlgorithmIdentifier ms_identifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
    private PKCS1Encoding m_SignatureAlgorithm = new PKCS1Encoding(new RSAEngine());
    private SHA1Digest m_Digest = new SHA1Digest();
    private static final AlgorithmIdentifier ms_AlgID = new AlgorithmIdentifier(X509ObjectIdentifiers.id_SHA1, null);

    public synchronized void initVerify(IMyPublicKey iMyPublicKey) throws InvalidKeyException {
        this.m_SignatureAlgorithm.init(false, ((MyRSAPublicKey)iMyPublicKey).getParams());
    }

    public synchronized void initSign(IMyPrivateKey iMyPrivateKey) throws InvalidKeyException {
        this.m_SignatureAlgorithm.init(true, ((MyRSAPrivateKey)iMyPrivateKey).getParams());
    }

    public synchronized boolean verify(byte[] arrby, byte[] arrby2) {
        return this.verify(arrby, 0, arrby.length, arrby2, 0, arrby2.length);
    }

    public synchronized boolean verify(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        try {
            this.m_Digest.reset();
            this.m_Digest.update(arrby, n, n2);
            byte[] arrby3 = new byte[this.m_Digest.getDigestSize()];
            this.m_Digest.doFinal(arrby3, 0);
            byte[] arrby4 = this.m_SignatureAlgorithm.processBlock(arrby2, n3, n4);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby4);
            ASN1InputStream aSN1InputStream = new ASN1InputStream(byteArrayInputStream);
            DigestInfo digestInfo = new DigestInfo((ASN1Sequence)aSN1InputStream.readObject());
            byteArrayInputStream.close();
            aSN1InputStream.close();
            if (!digestInfo.getAlgorithmId().getAlgorithm().equals(ms_AlgID.getAlgorithm())) {
                return false;
            }
            ASN1Encodable aSN1Encodable = digestInfo.getAlgorithmId().getParameters();
            if (aSN1Encodable != null && !(aSN1Encodable instanceof ASN1Null)) {
                return false;
            }
            byte[] arrby5 = digestInfo.getDigest();
            if (arrby3.length != arrby5.length) {
                return false;
            }
            for (int i = 0; i < arrby3.length; ++i) {
                if (arrby5[i] == arrby3[i]) continue;
                return false;
            }
            return true;
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.CRYPTO, "Signature algorithm does not match!");
            return false;
        }
    }

    public synchronized boolean verifyPlain(byte[] arrby, byte[] arrby2) {
        try {
            byte[] arrby3 = this.m_SignatureAlgorithm.processBlock(arrby2, 0, arrby2.length);
            if (arrby.length != arrby3.length) {
                return false;
            }
            for (int i = 0; i < arrby.length; ++i) {
                if (arrby3[i] == arrby[i]) continue;
                return false;
            }
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public synchronized byte[] sign(byte[] arrby) {
        try {
            byte[] arrby2 = new byte[this.m_Digest.getDigestSize()];
            this.m_Digest.reset();
            this.m_Digest.update(arrby, 0, arrby.length);
            this.m_Digest.doFinal(arrby2, 0);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
            DigestInfo digestInfo = new DigestInfo(ms_AlgID, arrby2);
            dEROutputStream.writeObject(digestInfo);
            byte[] arrby3 = byteArrayOutputStream.toByteArray();
            return this.m_SignatureAlgorithm.processBlock(arrby3, 0, arrby3.length);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public synchronized byte[] signPlain(byte[] arrby) {
        try {
            return this.m_SignatureAlgorithm.processBlock(arrby, 0, arrby.length);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public AlgorithmIdentifier getIdentifier() {
        return ms_identifier;
    }

    public byte[] encodeForXMLSignature(byte[] arrby) {
        return arrby;
    }

    public byte[] decodeForXMLSignature(byte[] arrby) {
        return arrby;
    }

    public String getXMLSignatureAlgorithmReference() {
        return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    }
}

