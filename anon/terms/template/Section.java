/*
 * Decompiled with CFR 0.150.
 */
package anon.terms.template;

import anon.terms.TCComponent;
import anon.terms.TCComposite;
import anon.terms.template.Paragraph;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Section
extends TCComposite
implements IXMLEncodable {
    public static String XML_ELEMENT_CONTAINER_NAME = "Sections";
    public static String XML_ELEMENT_NAME = "Section";
    public static String XML_ATTR_NAME = "name";

    public Section() {
    }

    public Section(double d, Object object) {
        super(d, object);
    }

    public Section(Node node) throws XMLParseException {
        Element element = null;
        if (node.getNodeType() == 9) {
            element = ((Document)node).getDocumentElement();
        } else if (node.getNodeType() == 1) {
            element = (Element)node;
        } else {
            throw new XMLParseException("Invalid node type.");
        }
        if (!element.getTagName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException("Invalid Tag name: " + element.getTagName());
        }
        this.id = XMLUtil.parseAttribute((Node)element, "id", -1.0);
        if (this.id < 0.0) {
            throw new XMLParseException("Attribute id of " + XMLUtil.parseAttribute((Node)element, XML_ATTR_NAME, "") + " missing");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, Paragraph.XML_ELEMENT_NAME);
        this.setContent(XMLUtil.parseAttribute((Node)element, XML_ATTR_NAME, null));
        while (element2 != null) {
            this.addTCComponent(new Paragraph(element2));
            element2 = (Element)XMLUtil.getNextSiblingByName(element2, Paragraph.XML_ELEMENT_NAME);
        }
    }

    public void replaceElementNodes(NodeList nodeList) {
        TCComponent[] arrtCComponent = this.getTCComponents();
        Paragraph paragraph = null;
        for (int i = 0; i < arrtCComponent.length; ++i) {
            paragraph = (Paragraph)arrtCComponent[i];
            if (!paragraph.hasElementNodes()) continue;
            paragraph.replaceElementNodes(nodeList);
        }
    }

    public Element toXmlElement(Document document) {
        return this.toXmlElement(document, false);
    }

    public Element toXmlElement(Document document, boolean bl) {
        if (this.getId() < 0.0 || !this.hasContent() && !bl) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        if (this.getContent() != null) {
            element.setAttribute(XML_ATTR_NAME, this.getContent().toString());
        }
        element.setAttribute("id", "" + this.getId());
        TCComponent[] arrtCComponent = this.getTCComponents();
        Paragraph paragraph = null;
        Element element2 = null;
        for (int i = 0; i < arrtCComponent.length; ++i) {
            paragraph = (Paragraph)arrtCComponent[i];
            element2 = paragraph.toXmlElement(document, bl);
            if (element2 == null) continue;
            element.appendChild(element2);
        }
        return element;
    }
}

