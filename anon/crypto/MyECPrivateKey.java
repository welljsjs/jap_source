/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractPrivateKey;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureCreationAlgorithm;
import anon.crypto.MyECDSASignature;
import anon.crypto.MyECParams;
import anon.crypto.MyECPublicKey;
import anon.util.XMLParseException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKeyStructure;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class MyECPrivateKey
extends AbstractPrivateKey
implements IMyPrivateKey {
    private BigInteger m_D;
    private MyECParams m_params;

    public MyECPrivateKey(ECPrivateKeyParameters eCPrivateKeyParameters, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.m_D = eCPrivateKeyParameters.getD();
        this.m_params = new MyECParams(eCPrivateKeyParameters.getParameters());
        this.m_params.setNamedCurveID(aSN1ObjectIdentifier);
    }

    public MyECPrivateKey(PrivateKeyInfo privateKeyInfo) throws InvalidKeyException {
        super(privateKeyInfo);
        try {
            AlgorithmIdentifier algorithmIdentifier = privateKeyInfo.getPrivateKeyAlgorithm();
            ECPrivateKeyStructure eCPrivateKeyStructure = new ECPrivateKeyStructure((ASN1Sequence)privateKeyInfo.getPrivateKey());
            this.m_D = eCPrivateKeyStructure.getKey();
            this.m_params = new MyECParams(X962Parameters.getInstance(algorithmIdentifier.getParameters()));
        }
        catch (Exception exception) {
            throw new InvalidKeyException("IOException while decoding private key");
        }
    }

    public MyECPrivateKey(Element element) throws InvalidKeyException, XMLParseException {
    }

    public IMyPublicKey createPublicKey() {
        ECPoint eCPoint = this.m_params.getECDomainParams().getG().multiply(this.m_D);
        MyECPublicKey myECPublicKey = new MyECPublicKey(new ECPublicKeyParameters(eCPoint, this.m_params.getECDomainParams()));
        myECPublicKey.setNamedCurveID(this.m_params.getCurveID());
        return myECPublicKey;
    }

    public PrivateKeyInfo getAsPrivateKeyInfo() {
        PrivateKeyInfo privateKeyInfo;
        ASN1Primitive aSN1Primitive = this.m_params.getX962Params().toASN1Primitive();
        try {
            privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, aSN1Primitive), new ECPrivateKeyStructure(this.m_D).toASN1Primitive());
        }
        catch (IOException iOException) {
            return null;
        }
        return privateKeyInfo;
    }

    public ISignatureCreationAlgorithm getSignatureAlgorithm() {
        try {
            MyECDSASignature myECDSASignature = new MyECDSASignature();
            myECDSASignature.initSign(this);
            return myECDSASignature;
        }
        catch (InvalidKeyException invalidKeyException) {
            return null;
        }
    }

    public String getAlgorithm() {
        return "Elliptic Curve Cryptography";
    }

    public String getFormat() {
        return "PKCS#8";
    }

    public Element toXmlElement(Document document) {
        return null;
    }

    public ECPrivateKeyParameters getPrivateParams() {
        return new ECPrivateKeyParameters(this.m_D, this.m_params.getECDomainParams());
    }
}

