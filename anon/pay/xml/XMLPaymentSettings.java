/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLPaymentSettings
implements IXMLEncodable {
    private Hashtable m_paymentSettings = new Hashtable();
    private Document m_docTheSettings = null;

    public XMLPaymentSettings(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
    }

    public XMLPaymentSettings() {
    }

    public XMLPaymentSettings(Hashtable hashtable) {
        this.m_paymentSettings = hashtable;
        this.m_docTheSettings = XMLUtil.createDocument();
        this.internal_toXmlElement(this.m_docTheSettings);
    }

    public XMLPaymentSettings(Element element) throws Exception {
        this.setValues(element);
    }

    public XMLPaymentSettings(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
    }

    public void addSetting(String string, String string2) {
        this.m_paymentSettings.put(string, string2);
    }

    public String getSettingValue(String string) {
        return (String)this.m_paymentSettings.get(string);
    }

    public Calendar getEndDate() {
        Calendar calendar = Calendar.getInstance();
        String string = this.getSettingValue("FlatrateDurationUnit");
        if (string.equalsIgnoreCase("day") || string.equalsIgnoreCase("days")) {
            int n = calendar.get(6);
            int n2 = Integer.parseInt(this.getSettingValue("FlatrateDuration"));
            calendar.set(6, (n + n2) % calendar.getMaximum(6));
        } else if (string.equalsIgnoreCase("week") || string.equalsIgnoreCase("weeks")) {
            int n = calendar.get(3);
            int n3 = Integer.parseInt(this.getSettingValue("FlatrateDuration"));
            calendar.set(3, (n + n3) % calendar.getMaximum(3));
        } else if (string.equalsIgnoreCase("month") || string.equalsIgnoreCase("months")) {
            int n = calendar.get(2);
            int n4 = Integer.parseInt(this.getSettingValue("FlatrateDuration"));
            calendar.set(2, (n + n4) % calendar.getMaximum(2));
        } else if (string.equalsIgnoreCase("year") || string.equalsIgnoreCase("years")) {
            int n = calendar.get(1);
            int n5 = Integer.parseInt(this.getSettingValue("FlatrateDuration"));
            calendar.set(1, n + n5);
        }
        return calendar;
    }

    public Enumeration getSettingNames() {
        return this.m_paymentSettings.keys();
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement("PaymentSettings");
        element.setAttribute("version", "1.0");
        document.appendChild(element);
        Enumeration enumeration = this.m_paymentSettings.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            String string2 = (String)this.m_paymentSettings.get(string);
            Element element2 = document.createElement("Setting");
            XMLUtil.setAttribute(element2, "name", string);
            XMLUtil.setValue((Node)element2, string2);
            element.appendChild(element2);
        }
        return element;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("PaymentSettings") || !element.getAttribute("version").equals("1.0")) {
            throw new Exception("wrong XML format");
        }
        NodeList nodeList = element.getElementsByTagName("Setting");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element element2 = (Element)nodeList.item(i);
            String string = XMLUtil.parseValue((Node)element2, (String)null);
            String string2 = XMLUtil.parseAttribute((Node)element2, "name", null);
            this.m_paymentSettings.put(string2, string);
        }
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheSettings.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

