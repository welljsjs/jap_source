/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.Base64;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLResponse
implements IXMLEncodable {
    private byte[] m_arbResponse;
    private String m_strAffiliate;

    public XMLResponse(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLResponse(byte[] arrby, String string) throws Exception {
        this.m_arbResponse = arrby;
        this.m_strAffiliate = string;
    }

    public String getAffiliate() {
        return this.m_strAffiliate;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("Response")) {
            throw new Exception("XMLResponse wrong xml structure");
        }
        String string = XMLUtil.parseValue((Node)element, "");
        this.m_arbResponse = Base64.decode(string);
        this.m_strAffiliate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "Affiliate"), null);
    }

    public byte[] getResponse() {
        return this.m_arbResponse;
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement("Response");
        XMLUtil.setValue((Node)element, Base64.encodeBytes(this.m_arbResponse));
        if (this.m_strAffiliate != null) {
            XMLUtil.createChildElementWithValue(element, "Affiliate", this.m_strAffiliate);
        }
        return element;
    }
}

