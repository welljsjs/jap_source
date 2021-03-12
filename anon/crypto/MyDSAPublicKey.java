/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractPublicKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureVerificationAlgorithm;
import anon.crypto.MyDSAParams;
import anon.crypto.MyDSASignature;
import anon.util.Base64;
import anon.util.XMLUtil;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MyDSAPublicKey
extends AbstractPublicKey
implements DSAPublicKey,
IMyPublicKey {
    private BigInteger m_Y;
    private MyDSAParams m_params;
    private long m_hashValue = 0L;

    public MyDSAPublicKey(DSAPublicKeyParameters dSAPublicKeyParameters) {
        this.m_Y = dSAPublicKeyParameters.getY();
        this.m_params = new MyDSAParams(dSAPublicKeyParameters.getParameters());
    }

    public MyDSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IllegalArgumentException {
        try {
            DSAParameter dSAParameter = DSAParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            ASN1Integer aSN1Integer = null;
            aSN1Integer = (ASN1Integer)subjectPublicKeyInfo.parsePublicKey();
            this.m_Y = aSN1Integer.getValue();
            this.m_params = new MyDSAParams(dSAParameter);
        }
        catch (IOException iOException) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
    }

    public ISignatureVerificationAlgorithm getSignatureAlgorithm() {
        try {
            MyDSASignature myDSASignature = new MyDSASignature();
            myDSASignature.initVerify(this);
            return myDSASignature;
        }
        catch (InvalidKeyException invalidKeyException) {
            return null;
        }
    }

    public BigInteger getY() {
        return this.m_Y;
    }

    public DSAParams getParams() {
        return this.m_params;
    }

    public DSAPublicKeyParameters getPublicParams() {
        return new DSAPublicKeyParameters(this.m_Y, (DSAParameters)this.m_params);
    }

    public MyDSAParams getMyDASParams() {
        return this.m_params;
    }

    public String getAlgorithm() {
        return "DSA";
    }

    public String getFormat() {
        return "X.509";
    }

    public SubjectPublicKeyInfo getAsSubjectPublicKeyInfo() {
        ASN1Primitive aSN1Primitive = new DSAParameter(this.m_params.getP(), this.m_params.getQ(), this.m_params.getG()).toASN1Primitive();
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, aSN1Primitive);
        try {
            return new SubjectPublicKeyInfo(algorithmIdentifier, new ASN1Integer(this.getY()));
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement("DSAKeyValue");
        Element element2 = null;
        element2 = document.createElement("Y");
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Y.toByteArray()));
        element.appendChild(element2);
        element2 = document.createElement("P");
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_params.getP().toByteArray()));
        element.appendChild(element2);
        element2 = document.createElement("Q");
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_params.getQ().toByteArray()));
        element.appendChild(element2);
        element2 = document.createElement("G");
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_params.getG().toByteArray()));
        element.appendChild(element2);
        return element;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof DSAPublicKey)) {
            return false;
        }
        DSAPublicKey dSAPublicKey = (DSAPublicKey)object;
        return dSAPublicKey.getY().equals(this.m_Y) && dSAPublicKey.getParams().equals(this.m_params);
    }

    public int hashCode() {
        if (this.m_hashValue == 0L) {
            this.m_hashValue = this.m_Y.longValue() + this.m_params.getG().longValue() + this.m_params.getP().longValue() + this.m_params.getQ().longValue();
        }
        return (int)this.m_hashValue;
    }

    public int getKeyLength() {
        int n = this.m_Y.toByteArray().length * 8;
        return n - n % 64;
    }

    public int getParameterLength() {
        return (this.m_params.getG().toByteArray().length + this.m_params.getP().toByteArray().length + this.m_params.getQ().toByteArray().length) * 8;
    }
}

