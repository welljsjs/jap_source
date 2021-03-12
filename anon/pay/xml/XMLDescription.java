/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLDescription
implements IXMLEncodable {
    private String m_strDescription;

    public XMLDescription(byte[] arrby) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrby);
        this.setValues(document);
    }

    public XMLDescription(char[] arrc) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrc);
        this.setValues(document);
    }

    public XMLDescription(String string) throws Exception {
        this.m_strDescription = string;
    }

    private void setValues(Document document) throws Exception {
        Element element = document.getDocumentElement();
        if (!element.getTagName().equals("Description")) {
            throw new Exception("XMLDescription wrong xml structure");
        }
        CharacterData characterData = (CharacterData)element.getFirstChild();
        this.m_strDescription = characterData.getData();
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement("Description");
        XMLUtil.setValue((Node)element, this.m_strDescription);
        return element;
    }

    public String getDescription() {
        return this.m_strDescription;
    }
}

