/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLExternalChargeRequest
implements IXMLEncodable {
    private Vector m_chargeRequest = new Vector();
    private String m_password;

    public XMLExternalChargeRequest() {
    }

    public XMLExternalChargeRequest(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLExternalChargeRequest(char[] arrc) throws Exception {
        this(new String(arrc));
    }

    public XMLExternalChargeRequest(byte[] arrby) throws Exception {
        this.setValues(XMLUtil.toXMLDocument(arrby).getDocumentElement());
    }

    public XMLExternalChargeRequest(Document document) {
        try {
            this.setValues(document.getDocumentElement());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setPassword(String string) {
        this.m_password = string;
    }

    public String getPassword() {
        return this.m_password;
    }

    public void addCharge(String string, String string2, String string3) {
        this.m_chargeRequest.addElement(new String[]{string, string2, string3});
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement("ExternalChargeRequest");
        Element element2 = document.createElement("Password");
        element2.appendChild(document.createTextNode(this.m_password));
        element.appendChild(element2);
        for (int i = 0; i < this.m_chargeRequest.size(); ++i) {
            String[] arrstring = (String[])this.m_chargeRequest.elementAt(i);
            Element element3 = document.createElement("Charge");
            element2 = document.createElement("TransferNumber");
            element2.appendChild(document.createTextNode(arrstring[0]));
            element3.appendChild(element2);
            element2 = document.createElement("Currency");
            element2.appendChild(document.createTextNode(arrstring[1]));
            element3.appendChild(element2);
            element2 = document.createElement("Amount");
            element2.appendChild(document.createTextNode(arrstring[2]));
            element3.appendChild(element2);
            element.appendChild(element3);
        }
        return element;
    }

    protected void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("ExternalChargeRequest")) {
            throw new Exception("ExternalChargeRequest wrong XML structure");
        }
        Node node = XMLUtil.getFirstChildByName(element, "Password");
        if (node != null) {
            this.m_password = XMLUtil.parseValue(node, "");
        }
        NodeList nodeList = element.getElementsByTagName("Charge");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String string = "";
            String string2 = "";
            String string3 = "";
            Node node2 = XMLUtil.getFirstChildByName(nodeList.item(i), "TransferNumber");
            if (node2 != null) {
                string = XMLUtil.parseValue(node2, "");
            }
            if ((node2 = XMLUtil.getFirstChildByName(nodeList.item(i), "Currency")) != null) {
                string2 = XMLUtil.parseValue(node2, "");
            }
            if ((node2 = XMLUtil.getFirstChildByName(nodeList.item(i), "Amount")) != null) {
                string3 = XMLUtil.parseValue(node2, "");
            }
            this.addCharge(string, string2, string3);
        }
    }

    public Enumeration getChargeLines() {
        return this.m_chargeRequest.elements();
    }
}

