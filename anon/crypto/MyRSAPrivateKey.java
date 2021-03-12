/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractPrivateKey;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureCreationAlgorithm;
import anon.crypto.MyRSAPublicKey;
import anon.crypto.MyRSASignature;
import anon.util.Base64;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MyRSAPrivateKey
extends AbstractPrivateKey
implements IMyPrivateKey {
    public static final String XML_ELEMENT_NAME = "RSAPrivateKey";
    private MyRSASignature m_algorithm = new MyRSASignature();
    private RSAPrivateCrtKeyParameters m_Params;

    public MyRSAPrivateKey(CipherParameters cipherParameters) throws Exception {
        this.m_Params = (RSAPrivateCrtKeyParameters)cipherParameters;
    }

    public MyRSAPrivateKey(PrivateKeyInfo privateKeyInfo) throws Exception {
        super(privateKeyInfo);
        ASN1Primitive aSN1Primitive = privateKeyInfo.getPrivateKey();
        RSAPrivateKeyStructure rSAPrivateKeyStructure = new RSAPrivateKeyStructure((ASN1Sequence)aSN1Primitive);
        this.m_Params = new RSAPrivateCrtKeyParameters(rSAPrivateKeyStructure.getModulus(), rSAPrivateKeyStructure.getPublicExponent(), rSAPrivateKeyStructure.getPrivateExponent(), rSAPrivateKeyStructure.getPrime1(), rSAPrivateKeyStructure.getPrime2(), rSAPrivateKeyStructure.getExponent1(), rSAPrivateKeyStructure.getExponent2(), rSAPrivateKeyStructure.getCoefficient());
    }

    public MyRSAPrivateKey(Element element) throws Exception {
        if (element == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException(XML_ELEMENT_NAME, "Element is null or has wrong name!");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Modulus");
        String string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "PublicExponent");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger2 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "PrivateExponent");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger3 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "P");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger4 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Q");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger5 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "dP");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger6 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "dQ");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger7 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "QInv");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger8 = new BigInteger(Base64.decode(string));
        this.m_Params = new RSAPrivateCrtKeyParameters(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
    }

    public MyRSAPrivateKey(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, BigInteger bigInteger6, BigInteger bigInteger7, BigInteger bigInteger8) throws Exception {
        this.m_Params = new RSAPrivateCrtKeyParameters(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
    }

    public ISignatureCreationAlgorithm getSignatureAlgorithm() {
        try {
            this.m_algorithm.initSign(this);
        }
        catch (InvalidKeyException invalidKeyException) {
            // empty catch block
        }
        return this.m_algorithm;
    }

    public IMyPublicKey createPublicKey() {
        return new MyRSAPublicKey(this.getModulus(), this.getPublicExponent());
    }

    public CipherParameters getParams() {
        return this.m_Params;
    }

    public BigInteger getModulus() {
        return this.m_Params.getModulus();
    }

    public BigInteger getPrivateExponent() {
        return this.m_Params.getExponent();
    }

    public BigInteger getP() {
        return this.m_Params.getP();
    }

    public BigInteger getQ() {
        return this.m_Params.getQ();
    }

    public BigInteger getDP() {
        return this.m_Params.getDP();
    }

    public BigInteger getDQ() {
        return this.m_Params.getDQ();
    }

    public BigInteger getQInv() {
        return this.m_Params.getQInv();
    }

    public BigInteger getPublicExponent() {
        return this.m_Params.getPublicExponent();
    }

    public String getAlgorithm() {
        return "RSA";
    }

    public String getFormat() {
        return "PKCS#8";
    }

    public PrivateKeyInfo getAsPrivateKeyInfo() {
        PrivateKeyInfo privateKeyInfo;
        try {
            privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.1")), new RSAPrivateKeyStructure(this.m_Params.getModulus(), this.m_Params.getPublicExponent(), this.m_Params.getExponent(), this.m_Params.getP(), this.m_Params.getQ(), this.m_Params.getDP(), this.m_Params.getDQ(), this.m_Params.getQInv()).toASN1Primitive());
        }
        catch (IOException iOException) {
            return null;
        }
        return privateKeyInfo;
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        Element element2 = document.createElement("Modulus");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getModulus().toByteArray()));
        element2 = document.createElement("PublicExponent");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getPublicExponent().toByteArray()));
        element2 = document.createElement("PrivateExponent");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getExponent().toByteArray()));
        element2 = document.createElement("P");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getP().toByteArray()));
        element2 = document.createElement("Q");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getQ().toByteArray()));
        element2 = document.createElement("dP");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getDP().toByteArray()));
        element2 = document.createElement("dQ");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getDQ().toByteArray()));
        element2 = document.createElement("QInv");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_Params.getQInv().toByteArray()));
        return element;
    }
}

