/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractPublicKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureVerificationAlgorithm;
import anon.crypto.MyRSASignature;
import anon.util.Base64;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MyRSAPublicKey
extends AbstractPublicKey
implements IMyPublicKey {
    private static final long serialVersionUID = 1L;
    private MyRSASignature m_algorithm = new MyRSASignature();
    private BigInteger m_n;
    private BigInteger m_e;
    private long m_hashValue = 0L;
    private int m_keyLength = 0;

    public MyRSAPublicKey(BigInteger bigInteger, BigInteger bigInteger2) {
        this.m_n = bigInteger;
        this.m_e = bigInteger2;
    }

    public MyRSAPublicKey(CipherParameters cipherParameters) throws Exception {
        RSAKeyParameters rSAKeyParameters = (RSAKeyParameters)cipherParameters;
        this.m_n = rSAKeyParameters.getModulus();
        this.m_e = rSAKeyParameters.getExponent();
    }

    public MyRSAPublicKey(RSAPublicKeyStructure rSAPublicKeyStructure) throws IllegalArgumentException {
        try {
            this.m_n = rSAPublicKeyStructure.getModulus();
            this.m_e = rSAPublicKeyStructure.getPublicExponent();
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("invalid info structure in RSA public key");
        }
    }

    public MyRSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IllegalArgumentException {
        try {
            RSAPublicKeyStructure rSAPublicKeyStructure = RSAPublicKeyStructure.getInstance(subjectPublicKeyInfo.getPublicKey());
            this.m_n = rSAPublicKeyStructure.getModulus();
            this.m_e = rSAPublicKeyStructure.getPublicExponent();
        }
        catch (IOException iOException) {
            throw new IllegalArgumentException("invalid info structure in RSA public key");
        }
    }

    public static MyRSAPublicKey getInstance(byte[] arrby) {
        try {
            return new MyRSAPublicKey(new RSAPublicKeyStructure((ASN1Sequence)new ASN1InputStream(new ByteArrayInputStream(arrby)).readObject()));
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public ISignatureVerificationAlgorithm getSignatureAlgorithm() {
        try {
            this.m_algorithm.initVerify(this);
        }
        catch (InvalidKeyException invalidKeyException) {
            // empty catch block
        }
        return this.m_algorithm;
    }

    public BigInteger getModulus() {
        return this.m_n;
    }

    public BigInteger getPublicExponent() {
        return this.m_e;
    }

    public String getAlgorithm() {
        return "RSA";
    }

    public String getFormat() {
        return "X.509";
    }

    public int getKeyLength() {
        return this.getModulus().bitLength();
    }

    public SubjectPublicKeyInfo getAsSubjectPublicKeyInfo() {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.1"));
        try {
            return new SubjectPublicKeyInfo(algorithmIdentifier, new RSAPublicKeyStructure(this.m_n, this.m_e).toASN1Primitive());
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public CipherParameters getParams() {
        return new RSAKeyParameters(false, this.m_n, this.m_e);
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement("RSAKeyValue");
        Element element2 = document.createElement("Modulus");
        element.appendChild(element2);
        byte[] arrby = this.m_n.toByteArray();
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(arrby));
        Element element3 = document.createElement("Exponent");
        element.appendChild(element3);
        arrby = this.m_e.toByteArray();
        XMLUtil.setValue((Node)element3, Base64.encodeBytes(arrby));
        return element;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof PublicKey)) {
            return false;
        }
        if (!(object instanceof MyRSAPublicKey)) {
            return false;
        }
        MyRSAPublicKey myRSAPublicKey = (MyRSAPublicKey)object;
        return myRSAPublicKey.getModulus().equals(this.m_n) && myRSAPublicKey.getPublicExponent().equals(this.m_e);
    }

    public int hashCode() {
        if (this.m_hashValue == 0L) {
            this.m_hashValue = this.m_n.longValue() + this.m_e.longValue();
        }
        return (int)this.m_hashValue;
    }

    public String toString() {
        String string = this.m_e == null ? "(not set)" : this.m_e.toString();
        String string2 = this.m_n == null ? "(not set)" : this.m_n.toString();
        return "e=" + string + " ; n=" + string2;
    }
}

