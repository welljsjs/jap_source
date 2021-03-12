/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.IMySignature;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyDSAPublicKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.signers.DSASigner;

public final class MyDSASignature
implements IMySignature {
    private static final AlgorithmIdentifier ms_identifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa_with_sha1);
    private DSASigner m_SignatureAlgorithm;
    private SHA1Digest m_Digest;

    public MyDSASignature() {
        try {
            this.m_SignatureAlgorithm = new DSASigner();
            this.m_Digest = new SHA1Digest();
        }
        catch (Exception exception) {
            this.m_SignatureAlgorithm = null;
        }
    }

    public synchronized void initVerify(IMyPublicKey iMyPublicKey) throws InvalidKeyException {
        try {
            MyDSAPublicKey myDSAPublicKey = (MyDSAPublicKey)iMyPublicKey;
            this.m_SignatureAlgorithm.init(false, myDSAPublicKey.getPublicParams());
            this.m_Digest.reset();
        }
        catch (Exception exception) {
            throw new InvalidKeyException("MyDSASignautre - initVerify - dont know how to hnalde the given key");
        }
    }

    public synchronized void initSign(IMyPrivateKey iMyPrivateKey) throws InvalidKeyException {
        try {
            MyDSAPrivateKey myDSAPrivateKey = (MyDSAPrivateKey)iMyPrivateKey;
            this.m_SignatureAlgorithm.init(true, myDSAPrivateKey.getPrivateParams());
        }
        catch (Exception exception) {
            throw new InvalidKeyException("MyDSASignautre - initVerify - dont know how to hnalde the given key");
        }
    }

    public synchronized boolean verify(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        try {
            this.m_Digest.reset();
            this.m_Digest.update(arrby, n, n2);
            byte[] arrby3 = new byte[this.m_Digest.getDigestSize()];
            this.m_Digest.doFinal(arrby3, 0);
            BigInteger[] arrbigInteger = MyDSASignature.derDecode(arrby2, n3, n4);
            return this.m_SignatureAlgorithm.verifySignature(arrby3, arrbigInteger[0], arrbigInteger[1]);
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.CRYPTO, "Signature algorithm does not match!");
            return false;
        }
    }

    public synchronized boolean verify(byte[] arrby, byte[] arrby2) {
        return this.verify(arrby, 0, arrby.length, arrby2, 0, arrby2.length);
    }

    public synchronized byte[] sign(byte[] arrby) {
        try {
            this.m_Digest.reset();
            this.m_Digest.update(arrby, 0, arrby.length);
            byte[] arrby2 = new byte[this.m_Digest.getDigestSize()];
            this.m_Digest.doFinal(arrby2, 0);
            BigInteger[] arrbigInteger = this.m_SignatureAlgorithm.generateSignature(arrby2);
            return MyDSASignature.derEncode(arrbigInteger[0], arrbigInteger[1]);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public AlgorithmIdentifier getIdentifier() {
        return ms_identifier;
    }

    public byte[] encodeForXMLSignature(byte[] arrby) {
        int n;
        int n2 = arrby[3];
        int n3 = arrby[3 + n2 + 2];
        byte[] arrby2 = new byte[40];
        for (n = 0; n < 40; ++n) {
            arrby2[n] = 0;
        }
        n = 0;
        if (n2 == 21) {
            n = 1;
            n2 = 20;
        }
        System.arraycopy(arrby, 4 + n, arrby2, 20 - n2, n2);
        n2 = (byte)(n2 + n);
        int n4 = 0;
        if (n3 == 21) {
            n4 = 1;
            n3 = 20;
        }
        System.arraycopy(arrby, 4 + n2 + 2 + n4, arrby2, 40 - n3, n3);
        return arrby2;
    }

    public byte[] decodeForXMLSignature(byte[] arrby) {
        try {
            int n = 46;
            if (arrby[0] < 0) {
                ++n;
            }
            if (arrby[20] < 0) {
                ++n;
            }
            byte[] arrby2 = new byte[n];
            arrby2[0] = 48;
            arrby2[1] = (byte)(n - 2);
            arrby2[2] = 2;
            if (arrby[0] < 0) {
                n = 5;
                arrby2[3] = 21;
                arrby2[4] = 0;
            } else {
                arrby2[3] = 20;
                n = 4;
            }
            System.arraycopy(arrby, 0, arrby2, n, 20);
            n += 20;
            arrby2[n++] = 2;
            if (arrby[20] < 0) {
                arrby2[n++] = 21;
                arrby2[n++] = 0;
            } else {
                arrby2[n++] = 20;
            }
            System.arraycopy(arrby, 20, arrby2, n, 20);
            return arrby2;
        }
        catch (Exception exception) {
            return null;
        }
    }

    public String getXMLSignatureAlgorithmReference() {
        return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    }

    static byte[] derEncode(BigInteger bigInteger, BigInteger bigInteger2) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(bigInteger));
        aSN1EncodableVector.add(new ASN1Integer(bigInteger2));
        dEROutputStream.writeObject(new DERSequence(aSN1EncodableVector));
        return byteArrayOutputStream.toByteArray();
    }

    static BigInteger[] derDecode(byte[] arrby, int n, int n2) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby, n, n2);
        ASN1InputStream aSN1InputStream = new ASN1InputStream(byteArrayInputStream);
        ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
        try {
            aSN1InputStream.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        BigInteger[] arrbigInteger = new BigInteger[]{((ASN1Integer)aSN1Sequence.getObjectAt(0)).getValue(), ((ASN1Integer)aSN1Sequence.getObjectAt(1)).getValue()};
        return arrbigInteger;
    }
}

