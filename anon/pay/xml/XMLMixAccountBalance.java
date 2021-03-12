/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPrivateKey;
import anon.crypto.XMLSignature;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLMixAccountBalance
implements IXMLEncodable {
    private int m_balance;
    private Timestamp m_lastUpdate;
    private Document m_docTheMixAccountBalance;
    public static String ms_strElemName = "MixAccountBalance";

    public XMLMixAccountBalance(int n, Timestamp timestamp) {
        this.m_balance = n;
        this.m_lastUpdate = timestamp;
        this.m_docTheMixAccountBalance = XMLUtil.createDocument();
        this.m_docTheMixAccountBalance.appendChild(this.internal_toXmlElement(this.m_docTheMixAccountBalance));
    }

    private Node internal_toXmlElement(Document document) {
        Element element = document.createElement(ms_strElemName);
        element.setAttribute("version", "1.0");
        Element element2 = document.createElement("Balance");
        XMLUtil.setValue((Node)element2, this.m_balance);
        element.appendChild(element2);
        element2 = document.createElement("LastUpdate");
        XMLUtil.setValue((Node)element2, this.m_lastUpdate.toString());
        element.appendChild(element2);
        return element;
    }

    public XMLMixAccountBalance(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheMixAccountBalance = document;
    }

    public XMLMixAccountBalance(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheMixAccountBalance = document;
    }

    public XMLMixAccountBalance(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheMixAccountBalance = XMLUtil.createDocument();
        this.m_docTheMixAccountBalance.appendChild(XMLUtil.importNode(this.m_docTheMixAccountBalance, element, true));
    }

    public XMLMixAccountBalance(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.setValues(element);
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("MixAccountBalance")) {
            throw new Exception("XMLMixAccountBalance: cannot parse, wrong xml format!");
        }
        if (!element.getAttribute("version").equals("1.0")) {
            throw new Exception("XMLMixAccountBalance: cannot parse, cert version is " + element.getAttribute("version") + " but 1.0 was expected.");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Balance");
        this.m_balance = XMLUtil.parseValue((Node)element2, -1000);
        if (this.m_balance == -1000) {
            throw new Exception("XMLMixAccountBalance: cannot parse the balance");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "LastUpdate");
        String string = XMLUtil.parseValue((Node)element2, "0");
        this.m_lastUpdate = Timestamp.valueOf(string);
    }

    public boolean sign(IMyPrivateKey iMyPrivateKey) {
        try {
            XMLSignature.sign((Node)this.m_docTheMixAccountBalance, iMyPrivateKey, 0);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public int getBalance() {
        return this.m_balance;
    }

    public Timestamp getLastUpdate() {
        return this.m_lastUpdate;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheMixAccountBalance.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

