/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.xml.XMLVolumePlan;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLVolumePlans
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "VolumePlans";
    private Vector m_volumePlans = new Vector();
    private Document m_docTheVolumePlans;

    public XMLVolumePlans() {
        this.m_docTheVolumePlans = XMLUtil.createDocument();
    }

    public XMLVolumePlans(Vector vector) {
        this.m_volumePlans = vector;
        this.m_docTheVolumePlans = XMLUtil.createDocument();
        this.m_docTheVolumePlans.appendChild(this.internal_toXmlElement(this.m_docTheVolumePlans));
    }

    public XMLVolumePlans(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
        this.m_docTheVolumePlans = document;
    }

    public XMLVolumePlans(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheVolumePlans = XMLUtil.createDocument();
        this.m_docTheVolumePlans.appendChild(XMLUtil.importNode(this.m_docTheVolumePlans, element, true));
    }

    public XMLVolumePlans(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
        this.m_docTheVolumePlans = document;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheVolumePlans.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private void setValues(Element element) throws Exception {
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        NodeList nodeList = element.getElementsByTagName("VolumePlan");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            XMLVolumePlan xMLVolumePlan = new XMLVolumePlan((Element)nodeList.item(i));
            if (xMLVolumePlan.getFirstSupportedAnonlibVersion() != null && Util.convertVersionStringToNumber(xMLVolumePlan.getFirstSupportedAnonlibVersion()) > Util.convertVersionStringToNumber("00.20.001")) continue;
            this.insertByPrice(xMLVolumePlan);
        }
    }

    private void insertByPrice(XMLVolumePlan xMLVolumePlan) {
        XMLVolumePlan xMLVolumePlan2;
        int n;
        for (n = 0; !(n >= this.m_volumePlans.size() || !(xMLVolumePlan2 = (XMLVolumePlan)this.m_volumePlans.elementAt(n)).isMonthlyVolume() && xMLVolumePlan.isMonthlyVolume() || xMLVolumePlan2.getPrice() >= xMLVolumePlan.getPrice() && (xMLVolumePlan.isMonthlyVolume() && xMLVolumePlan2.isMonthlyVolume() || !xMLVolumePlan.isMonthlyVolume() && !xMLVolumePlan2.isMonthlyVolume()) && (xMLVolumePlan2.getPrice() > xMLVolumePlan.getPrice() || xMLVolumePlan2.getVolumeKbytes() > xMLVolumePlan.getVolumeKbytes())); ++n) {
        }
        this.m_volumePlans.insertElementAt(xMLVolumePlan, n);
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        Enumeration enumeration = this.m_volumePlans.elements();
        while (enumeration.hasMoreElements()) {
            XMLVolumePlan xMLVolumePlan = (XMLVolumePlan)enumeration.nextElement();
            Element element2 = xMLVolumePlan.toXmlElement(document);
            element.appendChild(element2);
        }
        return element;
    }

    public Vector getVolumePlans() {
        return this.m_volumePlans;
    }

    public XMLVolumePlan getVolumePlan(String string) {
        Enumeration enumeration = this.m_volumePlans.elements();
        while (enumeration.hasMoreElements()) {
            XMLVolumePlan xMLVolumePlan = (XMLVolumePlan)enumeration.nextElement();
            if (!xMLVolumePlan.getName().equalsIgnoreCase(string)) continue;
            return xMLVolumePlan;
        }
        return null;
    }

    public XMLVolumePlan getVolumePlan(int n) {
        return (XMLVolumePlan)this.m_volumePlans.elementAt(n);
    }

    public int getNrOfPlans() {
        return this.m_volumePlans.size();
    }

    public void addVolumePlan(XMLVolumePlan xMLVolumePlan) {
        this.insertByPrice(xMLVolumePlan);
        this.m_docTheVolumePlans = XMLUtil.createDocument();
        this.m_docTheVolumePlans.appendChild(this.internal_toXmlElement(this.m_docTheVolumePlans));
    }
}

