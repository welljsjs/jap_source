/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.XMLSignature;
import anon.pay.xml.XMLPriceCertificate;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLPriceCertificateList
implements IXMLEncodable {
    private Vector m_thePriceCerts;
    private Document m_docThePriceCerts;
    private static final String ms_strElemName = "PriceCertificateList";

    public XMLPriceCertificateList(Vector vector) {
        this.m_thePriceCerts = vector;
        this.m_docThePriceCerts = XMLUtil.createDocument();
        this.m_docThePriceCerts = this.internal_toXmlElement(this.m_docThePriceCerts);
    }

    private Document internal_toXmlElement(Document document) {
        Element element = document.createElement(ms_strElemName);
        element.setAttribute("version", "1.0");
        document.appendChild(element);
        Enumeration enumeration = this.m_thePriceCerts.elements();
        while (enumeration.hasMoreElements()) {
            XMLPriceCertificate xMLPriceCertificate = (XMLPriceCertificate)enumeration.nextElement();
            try {
                Element element2 = xMLPriceCertificate.toXmlElement(document);
                element.appendChild(element2);
            }
            catch (DOMException dOMException) {
                LogHolder.log(7, LogType.PAY, dOMException.getMessage());
            }
        }
        return document;
    }

    public XMLPriceCertificateList(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.m_thePriceCerts = new Vector();
        this.setValues(document.getDocumentElement());
        this.m_docThePriceCerts = document;
    }

    public XMLPriceCertificateList(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.m_thePriceCerts = new Vector();
        this.setValues(document.getDocumentElement());
        this.m_docThePriceCerts = document;
    }

    public XMLPriceCertificateList(Element element) throws Exception {
        this.m_thePriceCerts = new Vector();
        this.setValues(element);
        this.m_docThePriceCerts = XMLUtil.createDocument();
        this.m_docThePriceCerts.appendChild(XMLUtil.importNode(this.m_docThePriceCerts, element, true));
    }

    public XMLPriceCertificateList(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.m_thePriceCerts = new Vector();
        this.setValues(element);
        this.m_docThePriceCerts = document;
    }

    public Vector getPriceCerts() {
        return this.m_thePriceCerts;
    }

    public static String getXMLElementName() {
        return ms_strElemName;
    }

    public Vector getPriceCertHashes() {
        Vector<String> vector = new Vector<String>();
        Enumeration enumeration = this.m_thePriceCerts.elements();
        while (enumeration.hasMoreElements()) {
            XMLPriceCertificate xMLPriceCertificate = (XMLPriceCertificate)enumeration.nextElement();
            String string = XMLSignature.getHashValueOfElement(xMLPriceCertificate.getDocument());
            vector.addElement(string);
        }
        return vector;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals(ms_strElemName)) {
            throw new Exception("XMLPriceCertificateList: cannot parse, wrong xml format!");
        }
        if (!element.getAttribute("version").equals("1.0")) {
            throw new Exception("XMLPriceCertificate: cannot parse, cert version is " + element.getAttribute("version") + " but 1.0 was expected.");
        }
        NodeList nodeList = element.getElementsByTagName("PriceCertificate");
        if (nodeList == null) {
            throw new Exception("XMLPriceCertificate: cannot parse price certificates");
        }
        int n = 0;
        while (nodeList.item(n) != null) {
            Element element2 = (Element)nodeList.item(n);
            XMLPriceCertificate xMLPriceCertificate = new XMLPriceCertificate(element2);
            this.m_thePriceCerts.addElement(xMLPriceCertificate);
            ++n;
        }
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docThePriceCerts.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

