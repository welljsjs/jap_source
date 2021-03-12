/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLCloseAck
implements IXMLEncodable {
    public Element toXmlElement(Document document) {
        Element element = document.createElement("CloseAck");
        return element;
    }
}

