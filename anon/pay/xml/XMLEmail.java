/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLEmail
implements IXMLEncodable {
    private String m_senderName;
    private String m_replyAddress;
    private String m_bodyText;
    private String m_receiverAddress;
    private String m_subject;
    private String m_senderIdentification;
    private Document m_docTheEmail;
    public static String ms_strElemName = "Email";

    public XMLEmail(String string, String string2, String string3, String string4) {
        this.m_senderName = string;
        this.m_replyAddress = string2;
        this.m_bodyText = string3;
        this.m_senderIdentification = string4;
        this.setDefaultValues();
        this.m_docTheEmail = XMLUtil.createDocument();
        this.m_docTheEmail.appendChild(this.internal_toXmlElement(this.m_docTheEmail));
    }

    public XMLEmail(String string, String string2, String string3, String string4, String string5, String string6) {
        this.m_senderName = string;
        this.m_replyAddress = string2;
        this.m_bodyText = string3;
        this.m_receiverAddress = string4;
        this.m_subject = string5;
        this.m_senderIdentification = string6;
        this.setDefaultValues();
        this.m_docTheEmail = XMLUtil.createDocument();
        this.m_docTheEmail.appendChild(this.internal_toXmlElement(this.m_docTheEmail));
    }

    private void setDefaultValues() {
        if (this.m_receiverAddress == null || this.m_receiverAddress.equals("")) {
            this.m_receiverAddress = "jap@inf.tu-dresden.de";
        }
        if (this.m_replyAddress == null || this.m_replyAddress.equals("")) {
            this.m_replyAddress = "no return";
        }
        if (this.m_senderName == null || this.m_senderName.equals("")) {
            this.m_senderName = "Unknown Sender";
        }
        if (this.m_subject == null || this.m_subject.equals("")) {
            this.m_subject = "AN.ON support request";
        }
        if (this.m_bodyText == null || this.m_bodyText.equals("")) {
            this.m_bodyText = "message is empty";
        }
    }

    private Node internal_toXmlElement(Document document) {
        Element element = document.createElement(ms_strElemName);
        Element element2 = document.createElement("SenderName");
        XMLUtil.setValue((Node)element2, this.m_senderName);
        element.appendChild(element2);
        element2 = document.createElement("ReplyAddress");
        XMLUtil.setValue((Node)element2, this.m_replyAddress);
        element.appendChild(element2);
        element2 = document.createElement("ReceiverAddress");
        XMLUtil.setValue((Node)element2, this.m_receiverAddress);
        element.appendChild(element2);
        element2 = document.createElement("Subject");
        XMLUtil.setValue((Node)element2, this.m_subject);
        element.appendChild(element2);
        element2 = document.createElement("BodyText");
        XMLUtil.setValue((Node)element2, this.m_bodyText);
        element.appendChild(element2);
        element2 = document.createElement("SenderIdentification");
        XMLUtil.setValue((Node)element2, this.m_senderIdentification);
        element.appendChild(element2);
        return element;
    }

    public XMLEmail(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheEmail = document;
    }

    public XMLEmail(char[] arrc) throws Exception {
        this(new String(arrc));
    }

    public XMLEmail(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheEmail = document;
    }

    public XMLEmail(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheEmail = XMLUtil.createDocument();
        this.m_docTheEmail.appendChild(XMLUtil.importNode(this.m_docTheEmail, element, true));
    }

    public XMLEmail(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.setValues(element);
        this.m_docTheEmail = document;
    }

    public String getSenderName() {
        return this.m_senderName;
    }

    public String getReplyAddress() {
        return this.m_replyAddress;
    }

    public String getReceiverAddress() {
        return this.m_receiverAddress;
    }

    public String getSubject() {
        return this.m_subject;
    }

    public String getBodyText() {
        return this.m_bodyText;
    }

    public String getSenderIdentification() {
        return this.m_senderIdentification;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("Email")) {
            throw new Exception("XMLEmail: cannot parse, wrong xml format!");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "SenderName");
        this.m_senderName = XMLUtil.parseValue((Node)element2, "");
        if (this.m_senderName.equals("")) {
            throw new Exception("XMLEmail: cannot parse the sender name");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "ReceiverAddress");
        this.m_receiverAddress = XMLUtil.parseValue((Node)element2, "");
        if (this.m_receiverAddress.equals("")) {
            throw new Exception("XMLEmail: cannot parse the receiver address");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "ReplyAddress");
        this.m_replyAddress = XMLUtil.parseValue((Node)element2, "");
        if (this.m_replyAddress.equals("")) {
            throw new Exception("XMLEmail: cannot parse the reply address");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Subject");
        this.m_subject = XMLUtil.parseValue((Node)element2, "");
        if (this.m_subject.equals("")) {
            throw new Exception("XMLEmail: cannot parse the Subject");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "BodyText");
        this.m_bodyText = XMLUtil.parseValue((Node)element2, "");
        if (this.m_bodyText.equals("")) {
            throw new Exception("XMLEmail: cannot parse the body text");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "SenderIdentification");
        this.m_senderIdentification = XMLUtil.parseValue((Node)element2, "");
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheEmail.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

