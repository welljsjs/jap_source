/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLBankAccount
implements IXMLEncodable {
    private String m_type;
    private String m_details;
    private String m_operatorCert = "none";
    private Document m_docTheBankAccount;
    public static String ms_strElemName = "BankAccount";

    public XMLBankAccount(String string, String string2) {
        this.m_type = string;
        this.m_details = string2;
        this.m_docTheBankAccount = XMLUtil.createDocument();
        this.m_docTheBankAccount.appendChild(this.internal_toXmlElement(this.m_docTheBankAccount));
    }

    public XMLBankAccount(String string, String string2, String string3) {
        this.m_type = string;
        this.m_details = string2;
        this.m_operatorCert = string3;
        this.m_docTheBankAccount = XMLUtil.createDocument();
        this.m_docTheBankAccount.appendChild(this.internal_toXmlElement(this.m_docTheBankAccount));
    }

    private Node internal_toXmlElement(Document document) {
        Element element = document.createElement(ms_strElemName);
        element.setAttribute("version", "1.0");
        Element element2 = document.createElement("Type");
        XMLUtil.setValue((Node)element2, this.m_type);
        element.appendChild(element2);
        element2 = document.createElement("Details");
        XMLUtil.setValue((Node)element2, this.m_details);
        element.appendChild(element2);
        if (!this.m_operatorCert.equals("none")) {
            element2 = document.createElement("OperatorCert");
            XMLUtil.setValue((Node)element2, this.m_operatorCert);
            element.appendChild(element2);
        }
        return element;
    }

    public XMLBankAccount(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheBankAccount = document;
    }

    public XMLBankAccount(char[] arrc) throws Exception {
        this(new String(arrc));
    }

    public XMLBankAccount(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheBankAccount = document;
    }

    public XMLBankAccount(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheBankAccount = XMLUtil.createDocument();
        this.m_docTheBankAccount.appendChild(XMLUtil.importNode(this.m_docTheBankAccount, element, true));
    }

    public XMLBankAccount(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.setValues(element);
        this.m_docTheBankAccount = document;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("BankAccount")) {
            throw new Exception("XMLBankAccount: cannot parse, wrong xml format!");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Type");
        this.m_type = XMLUtil.parseValue((Node)element2, "error");
        if (this.m_type.equals("error")) {
            throw new Exception("XMLBankAccount: cannot parse the account type");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Details");
        this.m_details = XMLUtil.parseValue((Node)element2, "error");
        if (this.m_details.equals("error")) {
            throw new Exception("XMLBankAccount: cannot parse the account details");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "OperatorCert");
        this.m_operatorCert = XMLUtil.parseValue((Node)element2, "none");
        if (this.m_operatorCert.equals("error")) {
            LogHolder.log(3, LogType.PAY, "XMLBankAccount: no operator cert set");
        }
    }

    public String getType() {
        return this.m_type;
    }

    public String getDetails() {
        return this.m_details;
    }

    public String getOperatorCert() {
        return this.m_operatorCert;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheBankAccount.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

