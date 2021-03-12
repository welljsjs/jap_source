/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLAiLoginConfirmation
implements IXMLEncodable {
    private int m_code = -1;
    private String m_message = null;
    public static final String XML_ELEMENT_NAME = "LoginConfirmation";

    public XMLAiLoginConfirmation(Element element) throws XMLParseException {
        this.setValues(element);
    }

    private void setValues(Element element) throws XMLParseException {
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        this.m_code = XMLUtil.parseAttribute((Node)element, "code", -1);
        if (this.m_code == -1) {
            throw new XMLParseException("No or invalid confirmation code for login confirmation specified");
        }
        this.m_message = XMLUtil.parseValue((Node)element, null);
        if (this.m_message == null) {
            throw new XMLParseException("No login confirmation message specified");
        }
    }

    public Element toXmlElement(Document document) {
        return null;
    }

    public int getCode() {
        return this.m_code;
    }

    public String getMessage() {
        return this.m_message;
    }
}

