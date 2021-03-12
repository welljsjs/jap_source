/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.XMLSignature;
import anon.pay.xml.XMLJapPublicKey;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLAccountCertificate
implements IXMLEncodable {
    private IMyPublicKey m_publicKey;
    private Timestamp m_creationTime;
    private long m_accountNumber;
    private String m_biID;
    private Document m_docTheAccountCert;

    public XMLAccountCertificate(IMyPublicKey iMyPublicKey, long l, Timestamp timestamp, String string) {
        this.m_publicKey = iMyPublicKey;
        this.m_accountNumber = l;
        this.m_creationTime = timestamp;
        this.m_biID = string;
        this.m_docTheAccountCert = XMLUtil.createDocument();
        this.m_docTheAccountCert.appendChild(this.internal_toXmlElement(this.m_docTheAccountCert));
    }

    public XMLAccountCertificate(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
        this.m_docTheAccountCert = document;
    }

    public XMLAccountCertificate(byte[] arrby) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrby);
        this.setValues(document.getDocumentElement());
        this.m_docTheAccountCert = document;
    }

    public XMLAccountCertificate(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheAccountCert = XMLUtil.createDocument();
        this.m_docTheAccountCert.appendChild(XMLUtil.importNode(this.m_docTheAccountCert, element, true));
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("AccountCertificate")) {
            throw new Exception("XMLAccountCertificate: cannot parse, wrong xml format!");
        }
        if (!element.getAttribute("version").equals("1.0")) {
            throw new Exception("XMLAccountCertificate: cannot parse, cert version is " + element.getAttribute("version") + " but 1.0 was expected.");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "AccountNumber");
        this.m_accountNumber = XMLUtil.parseValue((Node)element2, 0L);
        if (this.m_accountNumber == 0L) {
            throw new Exception("XMLAccountCertificate: cannot parse accountnumber");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "BiID");
        this.m_biID = XMLUtil.parseValue((Node)element2, "");
        if (this.m_biID.equals("")) {
            throw new Exception("XMLAccountCertificate: cannot parse BiID");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "CreationTime");
        String string = XMLUtil.parseValue((Node)element2, "0");
        this.m_creationTime = Timestamp.valueOf(string);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "JapPublicKey");
        if (element2 == null) {
            throw new Exception("XMLAccountCertificate: cannot parse public key");
        }
        this.m_publicKey = new XMLJapPublicKey(element2).getPublicKey();
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement("AccountCertificate");
        element.setAttribute("version", "1.0");
        Element element2 = document.createElement("AccountNumber");
        XMLUtil.setValue((Node)element2, Long.toString(this.m_accountNumber));
        element.appendChild(element2);
        element2 = document.createElement("BiID");
        XMLUtil.setValue((Node)element2, this.m_biID);
        element.appendChild(element2);
        element2 = document.createElement("CreationTime");
        XMLUtil.setValue((Node)element2, this.m_creationTime.toString());
        element.appendChild(element2);
        element2 = document.createElement("JapPublicKey");
        element.appendChild(element2);
        element2.setAttribute("version", "1.0");
        element2.appendChild(this.m_publicKey.toXmlElement(document));
        return element;
    }

    public long getAccountNumber() {
        return this.m_accountNumber;
    }

    public Timestamp getCreationTime() {
        return this.m_creationTime;
    }

    public IMyPublicKey getPublicKey() {
        return this.m_publicKey;
    }

    public boolean sign(IMyPrivateKey iMyPrivateKey) {
        try {
            XMLSignature.sign((Node)this.m_docTheAccountCert, iMyPrivateKey, 0);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheAccountCert.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public String getPIID() {
        return this.m_biID;
    }
}

