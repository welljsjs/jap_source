/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPrivateKey;
import anon.crypto.XMLSignature;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.sql.Timestamp;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLTransCert
implements IXMLEncodable {
    private Timestamp m_validTime;
    private Date m_receivedDate;
    private Date m_usedDate;
    private long m_accountNumber;
    private long m_transferNumber;
    private long m_deposit;
    private Document m_docTheTransCert;
    public static final String XML_ELEMENT_NAME_TRANSFER_CERTIFICATES = "TransferCertificates";
    public static final String XML_ELEMENT_NAME_TRANSFER_CERTIFICATE = "TransferCertificate";

    public XMLTransCert(long l, long l2, long l3, Timestamp timestamp) {
        this.m_accountNumber = l;
        this.m_transferNumber = l2;
        this.m_deposit = l3;
        this.m_validTime = timestamp;
        this.m_docTheTransCert = XMLUtil.createDocument();
        this.m_docTheTransCert.appendChild(this.internal_toXmlElement(this.m_docTheTransCert));
    }

    public XMLTransCert(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        Element element = document.getDocumentElement();
        this.setValues(element);
        this.m_docTheTransCert = document;
    }

    public XMLTransCert(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheTransCert = XMLUtil.createDocument();
        this.m_docTheTransCert.appendChild(XMLUtil.importNode(this.m_docTheTransCert, element, true));
    }

    public XMLTransCert(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.setValues(element);
        this.m_docTheTransCert = document;
    }

    public void setReceivedDate(Date date) {
        this.m_receivedDate = date;
        this.m_docTheTransCert = XMLUtil.createDocument();
        this.m_docTheTransCert.appendChild(this.internal_toXmlElement(this.m_docTheTransCert));
    }

    public void setUsedDate(Date date) {
        this.m_usedDate = date;
        this.m_docTheTransCert = XMLUtil.createDocument();
        this.m_docTheTransCert.appendChild(this.internal_toXmlElement(this.m_docTheTransCert));
    }

    public Date getReceivedDate() {
        return this.m_receivedDate;
    }

    public Date getUsedDate() {
        return this.m_usedDate;
    }

    public long getAccountNumber() {
        return this.m_accountNumber;
    }

    public long getTransferNumber() {
        return this.m_transferNumber;
    }

    public Timestamp getValidTime() {
        return this.m_validTime;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals(XML_ELEMENT_NAME_TRANSFER_CERTIFICATE)) {
            throw new Exception("XMLTransCert wrong xml structure: " + XMLUtil.toString(element));
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "AccountNumber");
        String string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_accountNumber = Long.parseLong(string);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "TransferNumber");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_transferNumber = Long.parseLong(string);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "ValidTime");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_validTime = Timestamp.valueOf(string);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "ReceivedDate");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        if (string != null) {
            this.m_receivedDate = new Date(Long.parseLong(string));
        }
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME_TRANSFER_CERTIFICATE);
        element.setAttribute("version", "1.2");
        Element element2 = document.createElement("AccountNumber");
        XMLUtil.setValue((Node)element2, Long.toString(this.m_accountNumber));
        element.appendChild(element2);
        element2 = document.createElement("TransferNumber");
        XMLUtil.setValue((Node)element2, Long.toString(this.m_transferNumber));
        element.appendChild(element2);
        element2 = document.createElement("Deposit");
        XMLUtil.setValue((Node)element2, Long.toString(this.m_deposit));
        element.appendChild(element2);
        element2 = document.createElement("ValidTime");
        XMLUtil.setValue((Node)element2, this.m_validTime.toString());
        element.appendChild(element2);
        element2 = document.createElement("ReceivedDate");
        if (this.m_receivedDate != null) {
            XMLUtil.setValue((Node)element2, this.m_receivedDate.getTime());
            element.appendChild(element2);
        }
        return element;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheTransCert.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public boolean sign(IMyPrivateKey iMyPrivateKey) {
        try {
            XMLSignature.sign((Node)this.m_docTheTransCert, iMyPrivateKey, 0);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }
}

