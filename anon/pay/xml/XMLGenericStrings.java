/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLGenericStrings
implements IXMLEncodable {
    private Document m_doc;
    public static String ms_strElemName = "GenericStrings";
    Hashtable m_strings;

    public XMLGenericStrings(PaymentInstanceDBEntry paymentInstanceDBEntry) {
        String string;
        this.m_strings = new Hashtable();
        if (paymentInstanceDBEntry != null && (string = PayAccountsFile.getInstance().getAffiliate(paymentInstanceDBEntry.getId(), false)) != null) {
            this.m_strings.put("affiliate", string);
        }
        this.m_doc = XMLUtil.createDocument();
        this.m_doc.appendChild(this.internal_toXmlElement(this.m_doc));
    }

    public XMLGenericStrings(Element element) throws Exception {
        this.setValues(element);
        this.m_doc = XMLUtil.createDocument();
        this.m_doc.appendChild(XMLUtil.importNode(this.m_doc, element, true));
    }

    public XMLGenericStrings(String string, String string2) {
        this.m_strings = new Hashtable();
        this.m_strings.put(string, string2);
        this.m_doc = XMLUtil.createDocument();
        this.m_doc.appendChild(this.internal_toXmlElement(this.m_doc));
    }

    public XMLGenericStrings(Hashtable hashtable) {
        this.m_strings = hashtable;
        this.m_doc = XMLUtil.createDocument();
        this.m_doc.appendChild(this.internal_toXmlElement(this.m_doc));
    }

    public void addEntry(String string, String string2) {
        this.m_strings.put(string, string2);
        this.m_doc = XMLUtil.createDocument();
        this.m_doc.appendChild(this.internal_toXmlElement(this.m_doc));
    }

    public Hashtable getStrings() {
        return (Hashtable)this.m_strings.clone();
    }

    public String getValue(String string) {
        return (String)this.m_strings.get(string);
    }

    public XMLGenericStrings(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
        this.m_doc = document;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_doc.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement(ms_strElemName);
        Enumeration enumeration = this.m_strings.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            String string2 = (String)this.m_strings.get(string);
            Element element2 = document.createElement("Entry");
            element2.setAttribute("name", string);
            element2.appendChild(document.createTextNode(string2));
            element.appendChild(element2);
        }
        return element;
    }

    private void setValues(Element element) throws Exception {
        String string = element.getTagName();
        if (!string.equals(ms_strElemName)) {
            throw new Exception("XMLGenericStrings: cannot parse, wrong xml format!");
        }
        NodeList nodeList = element.getElementsByTagName("Entry");
        this.m_strings = new Hashtable();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            String string2 = XMLUtil.parseAttribute(node, "name", "");
            String string3 = XMLUtil.parseValue(node, "");
            this.m_strings.put(string2, string3);
        }
    }
}

