/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLMixInfo
implements IXMLEncodable {
    private String m_mixCert;
    private int m_balance;
    private Timestamp m_updateTime;
    private int m_operatorId;
    private int m_id;
    private Document m_docTheMixInfo;

    public XMLMixInfo(String string, int n, Timestamp timestamp, int n2, int n3) {
        this.m_mixCert = string;
        this.m_updateTime = timestamp;
        this.m_balance = n;
        this.m_updateTime = timestamp;
        this.m_operatorId = n2;
        this.m_id = n3;
        this.m_docTheMixInfo = XMLUtil.createDocument();
    }

    public XMLMixInfo(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheMixInfo = document;
    }

    public XMLMixInfo(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheMixInfo = document;
    }

    public XMLMixInfo(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheMixInfo = XMLUtil.createDocument();
        this.m_docTheMixInfo.appendChild(XMLUtil.importNode(this.m_docTheMixInfo, element, true));
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("MixInfo")) {
            throw new Exception("XMLMixInfo: cannot parse, wrong xml format!");
        }
        if (!element.getAttribute("version").equals("1.0")) {
            throw new Exception("XMLMixInfo: cannot parse, cert version is " + element.getAttribute("version") + " but 1.0 was expected.");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "MixCert");
        this.m_mixCert = XMLUtil.parseValue((Node)element2, "error");
        if (this.m_mixCert.equals("error")) {
            throw new Exception("XMLMixInfo: cannot parse the MixCertificate");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Balance");
        this.m_balance = XMLUtil.parseValue((Node)element2, -1000);
        if (this.m_balance == -1000) {
            throw new Exception("XMLMixInfo: cannot parse balance");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "updateTime");
        String string = XMLUtil.parseValue((Node)element2, "0");
        this.m_updateTime = Timestamp.valueOf(string);
        element2 = (Element)XMLUtil.getLastChildByName(element, "operatorId");
        this.m_operatorId = XMLUtil.parseValue((Node)element2, -1);
        if (this.m_operatorId == -1) {
            throw new Exception("XMLMixInfo: cannot parse operator id");
        }
        element2 = (Element)XMLUtil.getLastChildByName(element, "id");
        this.m_id = XMLUtil.parseValue((Node)element2, -1);
        if (this.m_id == -1) {
            throw new Exception("XMLMixInfo: cannot parse id");
        }
    }

    public Timestamp getUpdateTime() {
        return this.m_updateTime;
    }

    public int getBalance() {
        return this.m_balance;
    }

    public String getMixCert() {
        return this.m_mixCert;
    }

    public int getOperatorId() {
        return this.m_operatorId;
    }

    public int getId() {
        return this.m_id;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheMixInfo.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

