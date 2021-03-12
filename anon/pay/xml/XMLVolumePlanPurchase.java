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

public class XMLVolumePlanPurchase
implements IXMLEncodable {
    private Document m_docTheVolumePlanPurchase;
    private long m_accountNumber;
    private String m_planName;

    public XMLVolumePlanPurchase(long l, String string) {
        this.m_accountNumber = l;
        this.m_planName = string;
        this.m_docTheVolumePlanPurchase = XMLUtil.createDocument();
        this.m_docTheVolumePlanPurchase.appendChild(this.internal_toXmlElement(this.m_docTheVolumePlanPurchase));
    }

    public XMLVolumePlanPurchase(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheVolumePlanPurchase = document;
    }

    public String getPlanName() {
        return this.m_planName;
    }

    public long getAccountNumber() {
        return this.m_accountNumber;
    }

    public Element internal_toXmlElement(Document document) {
        Element element = document.createElement("VolumePlanPurchase");
        Element element2 = document.createElement("AccountNumber");
        XMLUtil.setValue((Node)element2, this.m_accountNumber);
        element.appendChild(element2);
        element2 = document.createElement("VolumePlanName");
        XMLUtil.setValue((Node)element2, this.m_planName);
        element.appendChild(element2);
        return element;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheVolumePlanPurchase.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    protected void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("VolumePlanPurchase")) {
            throw new Exception("XMLVolumePlan: wrong XML structure");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "AccountNumber");
        long l = XMLUtil.parseValue((Node)element2, 0);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "VolumePlanName");
        String string = XMLUtil.parseValue((Node)element2, (String)null);
    }
}

