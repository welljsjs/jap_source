/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLTransferRequest
implements IXMLEncodable {
    private int m_requested;
    private String m_operatorCert;
    private Document m_docTheRequest;
    private static String ms_strElemName = "TransferRequest";

    public XMLTransferRequest(int n) {
        this.m_requested = n;
        this.m_docTheRequest = XMLUtil.createDocument();
        this.m_docTheRequest.appendChild(this.internal_toXmlElement(this.m_docTheRequest));
    }

    public XMLTransferRequest(int n, String string) {
        this.m_requested = n;
        this.m_operatorCert = string;
        this.m_docTheRequest = XMLUtil.createDocument();
        this.m_docTheRequest.appendChild(this.internal_toXmlElement(this.m_docTheRequest));
    }

    public XMLTransferRequest(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheRequest = document;
    }

    public XMLTransferRequest(char[] arrc) throws Exception {
        this(new String(arrc));
    }

    public XMLTransferRequest(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheRequest = document;
    }

    public XMLTransferRequest(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheRequest = XMLUtil.createDocument();
        this.m_docTheRequest.appendChild(XMLUtil.importNode(this.m_docTheRequest, element, true));
    }

    public XMLTransferRequest(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.setValues(element);
        this.m_docTheRequest = document;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("TransferRequest")) {
            throw new Exception("XMLTransferRequest: cannot parse, wrong xml format!");
        }
        if (!element.getAttribute("version").equals("1.0")) {
            throw new Exception("XMLTransferRequest: cannot parse, cert version is " + element.getAttribute("version") + " but 1.0 was expected.");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Requested");
        this.m_requested = XMLUtil.parseValue((Node)element2, 0);
        if (this.m_requested == 0) {
            throw new Exception("XMLTransferRequest: cannot parse requested");
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Operator");
        this.m_operatorCert = XMLUtil.parseValue((Node)element2, "none");
        if (this.m_operatorCert.equals("none")) {
            throw new Exception("no operator cert set in XMLTransferRequest");
        }
    }

    public int getRequested() {
        return this.m_requested;
    }

    public String getOperatorCert() {
        return this.m_operatorCert;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheRequest.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private Node internal_toXmlElement(Document document) {
        Element element = document.createElement(ms_strElemName);
        element.setAttribute("version", "1.0");
        Element element2 = document.createElement("Requested");
        XMLUtil.setValue((Node)element2, this.m_requested);
        element.appendChild(element2);
        element2 = document.createElement("Operator");
        XMLUtil.setValue((Node)element2, this.m_operatorCert);
        element.appendChild(element2);
        return element;
    }
}

