/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLGenericText
implements IXMLEncodable {
    public static final int TYPE_PLAINTEXT = 1;
    public static final int TYPE_XML = 2;
    private String m_text;
    private Document m_docTheText;
    public static final String XML_ELEMENT_NAME = "GenericText";

    public XMLGenericText() {
        this.m_text = "";
        this.m_docTheText = XMLUtil.createDocument();
        this.m_docTheText.appendChild(this.internal_toXmlElement(this.m_docTheText));
    }

    public XMLGenericText(String string) {
        this.m_text = string;
        this.m_docTheText = XMLUtil.createDocument();
        this.m_docTheText.appendChild(this.internal_toXmlElement(this.m_docTheText));
    }

    public String getText() {
        return this.m_text;
    }

    public String toString() {
        return this.getText();
    }

    public XMLGenericText(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheText = XMLUtil.createDocument();
        this.m_docTheText.appendChild(XMLUtil.importNode(this.m_docTheText, element, true));
    }

    public XMLGenericText(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
        this.m_docTheText = document;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheText.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setValue((Node)element, this.m_text);
        return element;
    }

    private void setValues(Element element) throws Exception {
        String string = element.getTagName();
        if (!string.equals(XML_ELEMENT_NAME)) {
            throw new Exception("XMLGenericText: cannot parse, wrong xml format!");
        }
        this.m_text = XMLUtil.parseValue((Node)element, "");
        if (this.m_text == null) {
            this.m_text = XMLUtil.toString(element.getFirstChild());
        }
    }

    public int hashCode() {
        int n = 1;
        n = 31 * n + (this.m_text == null ? 0 : this.m_text.hashCode());
        return n;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        XMLGenericText xMLGenericText = (XMLGenericText)object;
        return !(this.m_text == null ? xMLGenericText.m_text != null : !this.m_text.equals(xMLGenericText.m_text));
    }
}

