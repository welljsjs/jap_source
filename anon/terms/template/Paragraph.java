/*
 * Decompiled with CFR 0.150.
 */
package anon.terms.template;

import anon.terms.TCComponent;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Paragraph
extends TCComponent
implements IXMLEncodable {
    public static String XML_ELEMENT_CONTAINER_NAME = "Section";
    public static String XML_ELEMENT_NAME = "Paragraph";
    private Vector elementNodes = null;
    private boolean hasElementNodes = false;

    public Paragraph() {
        this.content = new Vector();
        this.elementNodes = new Vector(3);
        this.hasElementNodes = false;
    }

    public Paragraph(double d) {
        this();
        this.setId(d);
    }

    public Paragraph(Node node) throws XMLParseException {
        this();
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
            throw new XMLParseException("Attribute id missing: " + XMLUtil.toString(node));
        }
        this.setContent(element.getChildNodes());
    }

    public void replaceElementNodes(NodeList nodeList) {
        Element element = null;
        Element element2 = null;
        NodeList nodeList2 = null;
        Element element3 = null;
        Object var6_6 = null;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            element2 = (Element)nodeList.item(i);
            for (int j = 0; j < this.elementNodes.size(); ++j) {
                int n;
                element = (Element)this.elementNodes.elementAt(j);
                if (element.getTagName().equals(element2.getTagName())) {
                    this.elementNodes.removeElementAt(j);
                    this.elementNodes.insertElementAt(element2, j);
                    n = this.contentNodes().indexOf(element);
                    this.contentNodes().removeElementAt(n);
                    this.contentNodes().insertElementAt(element2, n);
                    continue;
                }
                nodeList2 = element.getElementsByTagName(element2.getTagName());
                for (n = 0; n < nodeList2.getLength(); ++n) {
                    element3 = (Element)nodeList2.item(n);
                    try {
                        element3.getParentNode().replaceChild(XMLUtil.importNode(element3.getParentNode().getOwnerDocument(), element2, true), element3);
                        continue;
                    }
                    catch (XMLParseException xMLParseException) {
                        LogHolder.log(0, LogType.MISC, xMLParseException);
                    }
                }
            }
        }
    }

    public boolean hasElementNodes() {
        return this.hasElementNodes;
    }

    public void setContent(Object object) {
        Node node;
        NodeList nodeList = null;
        if (object != null) {
            if (!(object instanceof NodeList)) {
                if (object instanceof Node[]) {
                    nodeList = Paragraph.toNodeList((Node[])object);
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("<?xml version=\"1.0\"?><temp>");
                    stringBuffer.append(object);
                    stringBuffer.append("</temp>");
                    try {
                        node = XMLUtil.readXMLDocument(new StringReader(stringBuffer.toString()));
                        nodeList = node.getDocumentElement() != null ? node.getDocumentElement().getChildNodes() : null;
                    }
                    catch (IOException iOException) {
                        LogHolder.log(7, LogType.MISC, "Cannot set content, reason: " + iOException.getMessage());
                        return;
                    }
                    catch (XMLParseException xMLParseException) {
                        LogHolder.log(7, LogType.MISC, "Cannot set content, reason: " + xMLParseException.getMessage());
                        return;
                    }
                }
            } else {
                nodeList = (NodeList)object;
            }
        }
        this.elementNodes.removeAllElements();
        this.contentNodes().removeAllElements();
        this.hasElementNodes = false;
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                node = nodeList.item(i).cloneNode(true);
                if (node.getNodeType() == 1) {
                    this.elementNodes.addElement(node);
                    this.hasElementNodes = true;
                }
                this.contentNodes().addElement(node);
            }
        }
    }

    public Object getContent() {
        final Node[] arrnode = new Node[this.contentNodes().size()];
        for (int i = 0; i < arrnode.length; ++i) {
            arrnode[i] = (Node)this.contentNodes().elementAt(i);
        }
        return new NodeList(){

            public int getLength() {
                return arrnode.length;
            }

            public Node item(int n) {
                return arrnode[n];
            }
        };
    }

    public void setContentBold() {
        NodeList nodeList = (NodeList)this.getContent();
        Document document = XMLUtil.createDocument();
        Node node = null;
        this.elementNodes.removeAllElements();
        this.contentNodes().removeAllElements();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i).getNodeType() == 1 && ((Element)nodeList.item(i)).getTagName().toLowerCase().equals("b")) {
                node = nodeList.item(i);
            } else if (nodeList.item(i).getNodeType() != 3 || nodeList.item(i).getNodeValue() != null && !nodeList.item(i).getNodeValue().trim().equals("")) {
                node = document.createElement("b");
                try {
                    node.appendChild(XMLUtil.importNode(document, nodeList.item(i), true));
                }
                catch (XMLParseException xMLParseException) {
                    LogHolder.log(1, LogType.MISC, xMLParseException);
                }
            } else {
                node = null;
            }
            if (node == null) continue;
            this.contentNodes().addElement(node);
            this.elementNodes.addElement(node);
        }
        this.hasElementNodes = this.elementNodes.size() > 0;
    }

    private Vector contentNodes() {
        return (Vector)this.content;
    }

    public boolean hasContent() {
        return super.hasContent() && this.contentNodes().size() > 0;
    }

    public Element toXmlElement(Document document) {
        return this.toXmlElement(document, false);
    }

    public Element toXmlElement(Document document, boolean bl) {
        if (this.id < 0.0 || this.contentNodes().size() == 0 && !bl) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("id", "" + this.id);
        for (int i = 0; i < this.contentNodes().size(); ++i) {
            try {
                element.appendChild(XMLUtil.importNode(document, (Node)this.contentNodes().elementAt(i), true));
                continue;
            }
            catch (XMLParseException xMLParseException) {
                LogHolder.log(0, LogType.MISC, xMLParseException);
            }
        }
        return element;
    }

    public Object clone() {
        Paragraph paragraph = new Paragraph();
        paragraph.setId(this.id);
        paragraph.setContent(this.getContent());
        return paragraph;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String string = null;
        StringTokenizer stringTokenizer = null;
        for (int i = 0; i < this.contentNodes().size(); ++i) {
            string = XMLUtil.toString((Node)this.contentNodes().elementAt(i));
            stringTokenizer = new StringTokenizer(string, "\n");
            while (stringTokenizer.hasMoreTokens()) {
                stringBuffer.append(stringTokenizer.nextToken().trim());
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString().trim();
    }

    public static NodeList toNodeList(Node[] arrnode) {
        final Node[] arrnode2 = arrnode;
        return new NodeList(){

            public int getLength() {
                return arrnode2.length;
            }

            public Node item(int n) {
                return arrnode2[n];
            }
        };
    }
}

