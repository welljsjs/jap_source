/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractPrivateKey;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureCreationAlgorithm;
import anon.crypto.MyDSAParams;
import anon.crypto.MyDSAPublicKey;
import anon.crypto.MyDSASignature;
import anon.util.Base64;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MyDSAPrivateKey
extends AbstractPrivateKey
implements DSAPrivateKey,
IMyPrivateKey {
    public static final String XML_ELEMENT_NAME = "DSAPrivateKey";
    private BigInteger m_X;
    private MyDSAParams m_params;

    public MyDSAPrivateKey(PrivateKeyInfo privateKeyInfo) throws InvalidKeyException {
        super(privateKeyInfo);
        try {
            AlgorithmIdentifier algorithmIdentifier = privateKeyInfo.getPrivateKeyAlgorithm();
            ASN1Integer aSN1Integer = (ASN1Integer)privateKeyInfo.parsePrivateKey();
            this.m_X = aSN1Integer.getValue();
            this.m_params = new MyDSAParams(DSAParameter.getInstance(algorithmIdentifier.getParameters()));
        }
        catch (Exception exception) {
            throw new InvalidKeyException("IOException while decoding private key");
        }
    }

    public MyDSAPrivateKey(Element element) throws InvalidKeyException, XMLParseException {
        if (element == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException(XML_ELEMENT_NAME, "Element is null or has wrong name!");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "G");
        String string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "P");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger2 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Q");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        BigInteger bigInteger3 = new BigInteger(Base64.decode(string));
        element2 = (Element)XMLUtil.getFirstChildByName(element, "X");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_X = new BigInteger(Base64.decode(string));
        this.m_params = new MyDSAParams(new DSAPrivateKeyParameters(this.m_X, new DSAParameters(bigInteger2, bigInteger3, bigInteger)).getParameters());
    }

    public MyDSAPrivateKey(DSAPrivateKeyParameters dSAPrivateKeyParameters) {
        this.m_X = dSAPrivateKeyParameters.getX();
        this.m_params = new MyDSAParams(dSAPrivateKeyParameters.getParameters());
    }

    public IMyPublicKey createPublicKey() {
        BigInteger bigInteger = this.getParams().getG().modPow(this.getX(), this.getParams().getP());
        MyDSAPublicKey myDSAPublicKey = new MyDSAPublicKey(new DSAPublicKeyParameters(bigInteger, (DSAParameters)this.m_params));
        return myDSAPublicKey;
    }

    public String getAlgorithm() {
        return "DSA";
    }

    public String getFormat() {
        return "PKCS#8";
    }

    public BigInteger getX() {
        return this.m_X;
    }

    public PrivateKeyInfo getAsPrivateKeyInfo() {
        PrivateKeyInfo privateKeyInfo;
        ASN1Primitive aSN1Primitive = new DSAParameter(this.m_params.getP(), this.m_params.getQ(), this.m_params.getG()).toASN1Primitive();
        try {
            privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, aSN1Primitive), new ASN1Integer(this.getX()));
        }
        catch (IOException iOException) {
            return null;
        }
        return privateKeyInfo;
    }

    public ISignatureCreationAlgorithm getSignatureAlgorithm() {
        try {
            MyDSASignature myDSASignature = new MyDSASignature();
            myDSASignature.initSign(this);
            return myDSASignature;
        }
        catch (InvalidKeyException invalidKeyException) {
            return null;
        }
    }

    public DSAParams getParams() {
        return this.m_params;
    }

    public MyDSAParams getMyDSAParams() {
        return this.m_params;
    }

    public DSAPrivateKeyParameters getPrivateParams() {
        return new DSAPrivateKeyParameters(this.m_X, (DSAParameters)this.m_params);
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        Element element2 = document.createElement("G");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_params.getG().toByteArray()));
        element2 = document.createElement("P");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_params.getP().toByteArray()));
        element2 = document.createElement("Q");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_params.getQ().toByteArray()));
        element2 = document.createElement("X");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_X.toByteArray()));
        return element;
    }
}

