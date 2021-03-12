/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractPublicKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureVerificationAlgorithm;
import anon.crypto.MyECDSASignature;
import anon.crypto.MyECParams;
import java.security.InvalidKeyException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class MyECPublicKey
extends AbstractPublicKey
implements IMyPublicKey {
    private X9ECPoint m_Q;
    private MyECParams m_params;

    public MyECPublicKey(ECPublicKeyParameters eCPublicKeyParameters) {
        this.m_Q = new X9ECPoint(eCPublicKeyParameters.getQ());
        this.m_params = new MyECParams(eCPublicKeyParameters.getParameters());
    }

    public MyECPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IllegalArgumentException {
        try {
            DERBitString dERBitString = subjectPublicKeyInfo.getPublicKeyData();
            DEROctetString dEROctetString = new DEROctetString(dERBitString.getBytes());
            this.m_params = new MyECParams(X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithmId().getParameters()));
            this.m_Q = new X9ECPoint(this.m_params.getECDomainParams().getCurve(), dEROctetString);
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("invalid info structure in ECDSA public key");
        }
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof ECPublicKeyParameters)) {
            return false;
        }
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)object;
        if (eCPublicKeyParameters.getQ().equals(this.m_Q.getPoint())) {
            return false;
        }
        return this.m_params.equals(eCPublicKeyParameters.getParameters());
    }

    public int hashCode() {
        return 0;
    }

    public SubjectPublicKeyInfo getAsSubjectPublicKeyInfo() {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, this.m_params.getX962Params().toASN1Object());
        return new SubjectPublicKeyInfo(algorithmIdentifier, this.m_Q.getPoint().getEncoded());
    }

    public int getKeyLength() {
        return this.m_params.getECDomainParams().getN().bitLength() - 1;
    }

    public ISignatureVerificationAlgorithm getSignatureAlgorithm() {
        try {
            MyECDSASignature myECDSASignature = new MyECDSASignature();
            myECDSASignature.initVerify(this);
            return myECDSASignature;
        }
        catch (InvalidKeyException invalidKeyException) {
            return null;
        }
    }

    public ECPublicKeyParameters getPublicParams() {
        return new ECPublicKeyParameters(this.m_Q.getPoint(), this.m_params.getECDomainParams());
    }

    public String getAlgorithm() {
        return "Elliptic Curve Cryptography";
    }

    public String getFormat() {
        return "X509";
    }

    public Element toXmlElement(Document document) {
        return null;
    }

    protected void setNamedCurveID(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.m_params.setNamedCurveID(aSN1ObjectIdentifier);
    }
}

