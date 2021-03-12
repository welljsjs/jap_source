/*
 * Decompiled with CFR 0.150.
 */
package anon.terms.template;

import anon.infoservice.OperatorAddress;
import anon.infoservice.ServiceOperator;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Preamble {
    public static String XML_ELEMENT_NAME = "Preamble";
    public static String XML_ELEMENT_LEADING_TEXT = "LeadingText";
    public static String XML_ELEMENT_TRAILING_TEXT = "TrailingText";
    private String leadingText = null;
    private ServiceOperator operator = null;
    private OperatorAddress operatorAddress = null;
    private String trailingText = null;

    public Preamble() {
    }

    public Preamble(Node node) throws XMLParseException {
        Element element = null;
        if (node.getNodeType() == 9) {
            element = ((Document)node).getDocumentElement();
        } else if (node.getNodeType() == 1) {
            element = (Element)node;
        } else {
            throw new XMLParseException("Invalid node type");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEMENT_LEADING_TEXT);
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEMENT_TRAILING_TEXT);
        this.leadingText = element2 != null ? XMLUtil.parseValue((Node)element2, (String)null) : null;
        this.trailingText = element3 != null ? XMLUtil.parseValue((Node)element3, (String)null) : null;
    }

    public String getLeadingText() {
        return this.leadingText;
    }

    public void setLeadingText(String string) {
        this.leadingText = string;
    }

    public ServiceOperator getOperator() {
        return this.operator;
    }

    public void setOperator(ServiceOperator serviceOperator) {
        this.operator = serviceOperator;
    }

    public OperatorAddress getOperatorAddress() {
        return this.operatorAddress;
    }

    public void setOperatorAddress(OperatorAddress operatorAddress) {
        this.operatorAddress = operatorAddress;
    }

    public String getTrailingText() {
        return this.trailingText;
    }

    public void setTrailingText(String string) {
        this.trailingText = string;
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        Element element2 = document.createElement(XML_ELEMENT_LEADING_TEXT);
        Element element3 = document.createElement(XML_ELEMENT_TRAILING_TEXT);
        Element element4 = this.operator != null ? this.operator.toXMLElement(document, this.operatorAddress, false) : document.createElement("Operator");
        XMLUtil.setValue((Node)element2, this.leadingText != null ? this.leadingText : "");
        XMLUtil.setValue((Node)element3, this.trailingText != null ? this.trailingText : "");
        element.appendChild(element2);
        element.appendChild(element4);
        element.appendChild(element3);
        return element;
    }
}

