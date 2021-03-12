/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPublicKey;
import anon.crypto.MyDSAPublicKey;
import anon.crypto.MyRSAPublicKey;
import anon.util.Base64;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLJapPublicKey
implements IXMLEncodable {
    private IMyPublicKey m_publicKey;
    private static String ms_elemName = "JapPublicKey";

    public static String getXMLElementName() {
        return ms_elemName;
    }

    public XMLJapPublicKey(IMyPublicKey iMyPublicKey) {
        this.m_publicKey = iMyPublicKey;
    }

    public XMLJapPublicKey(byte[] arrby) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrby);
        this.setPubKey(document.getDocumentElement());
    }

    public XMLJapPublicKey(char[] arrc) throws Exception {
        this(new String(arrc));
    }

    public XMLJapPublicKey(String string) throws XMLParseException {
        Document document = XMLUtil.toXMLDocument(string);
        this.setPubKey(document.getDocumentElement());
    }

    public XMLJapPublicKey(Element element) throws XMLParseException {
        this.setPubKey(element);
    }

    public IMyPublicKey getPublicKey() {
        return this.m_publicKey;
    }

    private void setPubKey(Element element) throws XMLParseException {
        if (!element.getTagName().equals(ms_elemName)) {
            throw new XMLParseException("XMLJapPublicKey wrong xml structure. Tagname is" + element.getTagName());
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "RSAKeyValue");
        if (element2 != null) {
            Element element3 = (Element)XMLUtil.getFirstChildByName(element2, "Modulus");
            Element element4 = (Element)XMLUtil.getFirstChildByName(element2, "Exponent");
            BigInteger bigInteger = new BigInteger(Base64.decode(XMLUtil.parseValue((Node)element3, "")));
            BigInteger bigInteger2 = new BigInteger(Base64.decode(XMLUtil.parseValue((Node)element4, "")));
            this.m_publicKey = new MyRSAPublicKey(bigInteger, bigInteger2);
            return;
        }
        Element element5 = (Element)XMLUtil.getFirstChildByName(element, "DSAKeyValue");
        if (element5 != null) {
            Element element6 = (Element)XMLUtil.getFirstChildByName(element5, "P");
            BigInteger bigInteger = new BigInteger(Base64.decode(XMLUtil.parseValue((Node)element6, "")));
            element6 = (Element)XMLUtil.getFirstChildByName(element5, "Y");
            BigInteger bigInteger3 = new BigInteger(Base64.decode(XMLUtil.parseValue((Node)element6, "")));
            element6 = (Element)XMLUtil.getFirstChildByName(element5, "Q");
            BigInteger bigInteger4 = new BigInteger(Base64.decode(XMLUtil.parseValue((Node)element6, "")));
            element6 = (Element)XMLUtil.getFirstChildByName(element5, "G");
            BigInteger bigInteger5 = new BigInteger(Base64.decode(XMLUtil.parseValue((Node)element6, "")));
            DSAPublicKeyParameters dSAPublicKeyParameters = new DSAPublicKeyParameters(bigInteger3, new DSAParameters(bigInteger, bigInteger4, bigInteger5));
            this.m_publicKey = new MyDSAPublicKey(dSAPublicKeyParameters);
            return;
        }
        throw new XMLParseException("Wrong key format: Neither RSAKeyValue nor DSAKeyValue found!");
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(ms_elemName);
        element.setAttribute("version", "1.0");
        Element element2 = this.m_publicKey.toXmlElement(document);
        element.appendChild(element2);
        return element;
    }

    public boolean equals(XMLJapPublicKey xMLJapPublicKey) {
        if (xMLJapPublicKey == null) {
            return false;
        }
        IMyPublicKey iMyPublicKey = xMLJapPublicKey.getPublicKey();
        IMyPublicKey iMyPublicKey2 = this.getPublicKey();
        if (iMyPublicKey == null) {
            return iMyPublicKey2 == null;
        }
        if (iMyPublicKey instanceof MyRSAPublicKey && !(iMyPublicKey2 instanceof MyRSAPublicKey)) {
            return false;
        }
        if (iMyPublicKey instanceof MyDSAPublicKey && !(iMyPublicKey2 instanceof MyDSAPublicKey)) {
            return false;
        }
        return iMyPublicKey.equals(iMyPublicKey2);
    }
}

