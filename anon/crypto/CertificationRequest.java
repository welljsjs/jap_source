/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.ByteSignature;
import anon.crypto.CertificationRequestInfo;
import anon.crypto.IMyPublicKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

final class CertificationRequest
extends DERSequence {
    private CertificationRequestInfo m_certificationRequestInfo;
    private DERBitString m_signature;

    public CertificationRequest(CertificationRequestInfo certificationRequestInfo, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair) {
        super(CertificationRequest.createRequest(certificationRequestInfo, asymmetricCryptoKeyPair.getPrivate().getSignatureAlgorithm().getIdentifier(), new DERBitString(ByteSignature.sign(CertificationRequest.DERtoBytes(certificationRequestInfo), asymmetricCryptoKeyPair))));
        this.m_certificationRequestInfo = certificationRequestInfo;
        this.m_signature = new DERBitString(ByteSignature.sign(CertificationRequest.DERtoBytes(certificationRequestInfo), asymmetricCryptoKeyPair));
    }

    CertificationRequest(ASN1Sequence aSN1Sequence) {
        super(CertificationRequest.createRequest((ASN1Sequence)aSN1Sequence.getObjectAt(0), AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1)), (DERBitString)aSN1Sequence.getObjectAt(2)));
        this.m_certificationRequestInfo = new CertificationRequestInfo((ASN1Sequence)aSN1Sequence.getObjectAt(0));
        this.m_signature = (DERBitString)aSN1Sequence.getObjectAt(2);
    }

    public IMyPublicKey getPublicKey() {
        return this.m_certificationRequestInfo.getPublicKey();
    }

    public CertificationRequestInfo getCertificationRequestInfo() {
        return this.m_certificationRequestInfo;
    }

    public boolean verify() {
        return ByteSignature.verify(CertificationRequest.DERtoBytes(this.m_certificationRequestInfo), this.m_signature.getBytes(), this.getPublicKey());
    }

    private static ASN1EncodableVector createRequest(ASN1Sequence aSN1Sequence, AlgorithmIdentifier algorithmIdentifier, DERBitString dERBitString) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(aSN1Sequence);
        aSN1EncodableVector.add(algorithmIdentifier);
        aSN1EncodableVector.add(dERBitString);
        return aSN1EncodableVector;
    }

    private static byte[] DERtoBytes(ASN1Encodable aSN1Encodable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            new DEROutputStream(byteArrayOutputStream).writeObject(aSN1Encodable);
        }
        catch (IOException iOException) {
            throw new RuntimeException("Could not write DER data to bytes.");
        }
        return byteArrayOutputStream.toByteArray();
    }
}

