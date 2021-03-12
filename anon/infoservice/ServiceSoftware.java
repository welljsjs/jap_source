/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ServiceSoftware
implements IXMLEncodable {
    private String m_strVersion;

    public static String getXmlElementName() {
        return "Software";
    }

    public ServiceSoftware(Node node) throws XMLParseException {
        this.m_strVersion = XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "Version"), null);
        if (this.m_strVersion == null) {
            throw new XMLParseException("Version");
        }
    }

    public ServiceSoftware(String string) {
        this.m_strVersion = string;
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(ServiceSoftware.getXmlElementName());
        Element element2 = document.createElement("Version");
        XMLUtil.setValue((Node)element2, this.m_strVersion);
        element.appendChild(element2);
        return element;
    }

    public String getVersion() {
        return this.m_strVersion;
    }
}

