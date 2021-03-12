/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.xml.XMLPaymentOption;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLPaymentOptions
implements IXMLEncodable {
    private Vector m_currencies = new Vector();
    private Vector m_paymentOptions = new Vector();
    private String m_acceptedCreditCards;
    private String m_sortingLanguage = null;

    public XMLPaymentOptions(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLPaymentOptions() {
    }

    public XMLPaymentOptions(Element element) throws Exception {
        this.setValues(element);
    }

    public Element toXmlElement(Document document) {
        Element element;
        int n;
        Element element2 = document.createElement("PaymentOptions");
        element2.setAttribute("version", "1.0");
        for (n = 0; n < this.m_currencies.size(); ++n) {
            element = document.createElement("Currency");
            element.appendChild(document.createTextNode((String)this.m_currencies.elementAt(n)));
            element2.appendChild(element);
        }
        for (n = 0; n < this.m_paymentOptions.size(); ++n) {
            try {
                XMLPaymentOption xMLPaymentOption = (XMLPaymentOption)this.m_paymentOptions.elementAt(n);
                element = xMLPaymentOption.toXmlElement(document);
                element2.appendChild(element);
                continue;
            }
            catch (ClassCastException classCastException) {
                // empty catch block
            }
        }
        element = document.createElement("AcceptedCards");
        element.appendChild(document.createTextNode(this.m_acceptedCreditCards));
        element2.appendChild(element);
        return element2;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals("PaymentOptions")) {
            throw new Exception("XMLPaymentOptions wrong XML structure");
        }
        NodeList nodeList = element.getElementsByTagName("Currency");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            this.m_currencies.addElement(nodeList.item(i).getFirstChild().getNodeValue());
        }
        NodeList nodeList2 = element.getElementsByTagName("PaymentOption");
        for (int i = 0; i < nodeList2.getLength(); ++i) {
            XMLPaymentOption xMLPaymentOption = new XMLPaymentOption((Element)nodeList2.item(i));
            this.m_paymentOptions.addElement(xMLPaymentOption);
        }
        Node node = XMLUtil.getFirstChildByName(element, "AcceptedCards");
        this.m_acceptedCreditCards = XMLUtil.parseValue(node, "");
    }

    public XMLPaymentOptions(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
    }

    public void addOption(XMLPaymentOption xMLPaymentOption) {
        this.m_paymentOptions.addElement(xMLPaymentOption);
    }

    public void addCurrency(String string) {
        this.m_currencies.addElement(string);
    }

    public Vector getAllOptions() {
        return this.getAllOptionsSortedByRank("en");
    }

    public synchronized Vector getAllOptionsSortedByRank(String string) {
        this.setSortingLanguage(string);
        this.sortVector();
        Vector vector = (Vector)this.m_paymentOptions.clone();
        return vector;
    }

    public XMLPaymentOption getOption(String string) {
        for (int i = 0; i < this.m_paymentOptions.size(); ++i) {
            try {
                XMLPaymentOption xMLPaymentOption = (XMLPaymentOption)this.m_paymentOptions.elementAt(i);
                if (!xMLPaymentOption.getName().equalsIgnoreCase(string)) continue;
                return xMLPaymentOption;
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.PAY, "Could not get payment option with name: " + string);
            }
        }
        LogHolder.log(5, LogType.PAY, "Could not get payment option with name: " + string);
        return null;
    }

    public XMLPaymentOption getOption(String string, String string2) {
        for (int i = 0; i < this.m_paymentOptions.size(); ++i) {
            try {
                XMLPaymentOption xMLPaymentOption = (XMLPaymentOption)this.m_paymentOptions.elementAt(i);
                String string3 = xMLPaymentOption.getHeading(string2);
                if (!string3.equalsIgnoreCase(string)) continue;
                return xMLPaymentOption;
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.PAY, "Could not get payment option for heading: " + string + " in language " + string2);
            }
        }
        LogHolder.log(5, LogType.PAY, "Could not get payment option for heading: " + string + " in language " + string2);
        return null;
    }

    public Vector getCurrencies() {
        return (Vector)this.m_currencies.clone();
    }

    public void setAcceptedCreditCards(String string) {
        this.m_acceptedCreditCards = string;
    }

    public String getAcceptedCreditCards() {
        return this.m_acceptedCreditCards;
    }

    public int compare(Object object, Object object2) {
        XMLPaymentOption xMLPaymentOption;
        XMLPaymentOption xMLPaymentOption2;
        try {
            if (object == null || object2 == null) {
                throw new Exception("can not compare null objects");
            }
            xMLPaymentOption2 = (XMLPaymentOption)object;
            xMLPaymentOption = (XMLPaymentOption)object2;
        }
        catch (Exception exception) {
            throw new ClassCastException("could not compare payment options, incompatible objects?" + exception);
        }
        String string = this.m_sortingLanguage;
        if (string == null || xMLPaymentOption2.getRank(string) == null || xMLPaymentOption.getRank(string) == null) {
            string = "en";
        }
        Integer n = xMLPaymentOption2.getRank(string);
        Integer n2 = xMLPaymentOption.getRank(string);
        if (n == null || n2 == null) {
            return 0;
        }
        if (n < n2) {
            return -1;
        }
        if (n > n2) {
            return 1;
        }
        return 0;
    }

    public void setSortingLanguage(String string) {
        this.m_sortingLanguage = string;
    }

    private void sortVector() {
        Vector vector = (Vector)this.m_paymentOptions.clone();
        Vector<XMLPaymentOption> vector2 = new Vector<XMLPaymentOption>();
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            XMLPaymentOption xMLPaymentOption = (XMLPaymentOption)enumeration.nextElement();
            boolean bl = false;
            for (int i = 0; i < vector2.size(); ++i) {
                XMLPaymentOption xMLPaymentOption2 = (XMLPaymentOption)vector2.elementAt(i);
                if (this.compare(xMLPaymentOption, xMLPaymentOption2) >= 0) continue;
                vector2.insertElementAt(xMLPaymentOption, i);
                bl = true;
                break;
            }
            if (bl) continue;
            vector2.addElement(xMLPaymentOption);
        }
        this.m_paymentOptions = vector2;
    }
}

