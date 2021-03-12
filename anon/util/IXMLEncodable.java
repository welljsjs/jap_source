/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IXMLEncodable {
    public static final String XML_ATTR_VERSION = "version";
    public static final String XML_ATTR_ID = "id";
    public static final String XML_ATTR_LANGUAGE = "lang";
    public static final String FIELD_XML_ELEMENT_NAME = "XML_ELEMENT_NAME";
    public static final String FIELD_XML_ELEMENT_CONTAINER_NAME = "XML_ELEMENT_CONTAINER_NAME";

    public Element toXmlElement(Document var1);
}

