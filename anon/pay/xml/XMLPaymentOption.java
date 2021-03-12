/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLPaymentOption
implements IXMLEncodable {
    public static final int MAX_CLICKS_UNLIMITED = Integer.MAX_VALUE;
    public static final String OPTION_ACTIVE = "active";
    public static final String OPTION_PASSIVE = "passive";
    public static final String OPTION_MIXED = "mixed";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_PHONE = "phone";
    private static final String MSG_OPTION_BANK_TRANSFER = (class$anon$pay$xml$XMLPaymentOption == null ? (class$anon$pay$xml$XMLPaymentOption = XMLPaymentOption.class$("anon.pay.xml.XMLPaymentOption")) : class$anon$pay$xml$XMLPaymentOption).getName() + ".optionBankTransfer";
    private static final String XML_ATTR_MAXCLICKS = "maxclicks";
    private static final String EXCEPTION_WRONG_XML_STRUCTURE = "XMLPaymentOption wrong XML structure";
    private static Vector m_languages = new Vector();
    private String m_name;
    private String m_type;
    private int m_markup;
    private boolean m_generic;
    private Vector m_headings = new Vector();
    private Vector m_detailedInfos = new Vector();
    private Hashtable m_ranks = new Hashtable();
    private Vector m_paymentDelays = new Vector();
    private Vector m_extraInfos = new Vector();
    private Vector m_inputFields = new Vector();
    private String m_imageLink;
    private String m_minJapVersion;
    static /* synthetic */ Class class$anon$pay$xml$XMLPaymentOption;

    public XMLPaymentOption(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLPaymentOption() {
    }

    public XMLPaymentOption(String string, String string2, boolean bl) {
        this.m_name = string;
        this.m_type = string2;
        this.m_generic = bl;
    }

    public XMLPaymentOption(String string, String string2, boolean bl, String string3) {
        this.m_name = string;
        this.m_type = string2;
        this.m_generic = bl;
        this.m_minJapVersion = string3;
    }

    public XMLPaymentOption(String string, String string2, boolean bl, String string3, int n) {
        this.m_name = string;
        this.m_type = string2;
        this.m_generic = bl;
        this.m_minJapVersion = string3;
        this.m_markup = n;
    }

    public XMLPaymentOption(String string, String string2) {
        this.m_name = string;
        this.m_type = string2;
        this.m_generic = true;
    }

    public void addHeading(String string, String string2) {
        this.m_headings.addElement(new String[]{string, string2});
        XMLPaymentOption.addLanguage(string2);
    }

    public void addDetailedInfo(String string, String string2) {
        this.m_detailedInfos.addElement(new String[]{string, string2});
        XMLPaymentOption.addLanguage(string2);
    }

    public void addRank(int n, String string) {
        this.m_ranks.put(string, new Integer(n));
    }

    public void addPaymentDelay(String string, String string2) {
        this.m_paymentDelays.addElement(new String[]{string, string2});
        XMLPaymentOption.addLanguage(string2);
    }

    public void addExtraInfo(String string, String string2, String string3) {
        this.m_extraInfos.addElement(new String[]{string, string2, string3});
        XMLPaymentOption.addLanguage(string3);
    }

    public void addInputField(String string, String string2, String string3) {
        this.m_inputFields.addElement(new String[]{string, string2, string3});
        XMLPaymentOption.addLanguage(string3);
    }

    public void setImageLink(String string) {
        this.m_imageLink = string;
    }

    public XMLPaymentOption(Element element) throws Exception {
        this.setValues(element);
    }

    public XMLPaymentOption(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
    }

    public Element toXmlElement(Document document) {
        int n;
        String[] arrstring;
        Object object;
        int n2;
        Element element = document.createElement("PaymentOption");
        element.setAttribute("name", this.m_name);
        element.setAttribute("type", this.m_type);
        element.setAttribute("generic", String.valueOf(this.m_generic));
        element.setAttribute("japversion", this.m_minJapVersion);
        Element element2 = document.createElement("Markup");
        XMLUtil.setValue((Node)element2, this.m_markup);
        element.appendChild(element2);
        for (n2 = 0; n2 < this.m_headings.size(); ++n2) {
            object = (String[])this.m_headings.elementAt(n2);
            element2 = document.createElement("Heading");
            element2.setAttribute("lang", object[1]);
            element2.appendChild(document.createTextNode(object[0]));
            element.appendChild(element2);
        }
        for (n2 = 0; n2 < this.m_detailedInfos.size(); ++n2) {
            object = (String[])this.m_detailedInfos.elementAt(n2);
            element2 = document.createElement("DetailedInfo");
            element2.setAttribute("lang", object[1]);
            element2.appendChild(document.createTextNode(object[0]));
            element.appendChild(element2);
        }
        Enumeration enumeration = this.m_ranks.keys();
        while (enumeration.hasMoreElements()) {
            object = (String)enumeration.nextElement();
            arrstring = (String[])this.m_ranks.get(object);
            element2 = document.createElement("Rank");
            element2.setAttribute("lang", (String)object);
            element2.appendChild(document.createTextNode(arrstring.toString()));
            element.appendChild(element2);
        }
        for (n = 0; n < this.m_paymentDelays.size(); ++n) {
            arrstring = (String[])this.m_paymentDelays.elementAt(n);
            element2 = document.createElement("PaymentDelay");
            element2.setAttribute("lang", arrstring[1]);
            element2.appendChild(document.createTextNode(arrstring[0]));
            element.appendChild(element2);
        }
        for (n = 0; n < this.m_extraInfos.size(); ++n) {
            arrstring = (String[])this.m_extraInfos.elementAt(n);
            element2 = document.createElement("ExtraInfo");
            element2.setAttribute("type", arrstring[1]);
            if (arrstring[2] != null) {
                element2.setAttribute("lang", arrstring[2]);
            }
            element2.appendChild(document.createTextNode(arrstring[0]));
            element.appendChild(element2);
        }
        if (this.m_imageLink != null) {
            element2 = document.createElement("ImageLink");
            element2.appendChild(document.createTextNode(this.m_imageLink));
            element.appendChild(element2);
        }
        for (n = 0; n < this.m_inputFields.size(); ++n) {
            arrstring = (String[])this.m_inputFields.elementAt(n);
            element2 = document.createElement("input");
            element2.setAttribute("ref", arrstring[0]);
            Element element3 = document.createElement("label");
            element2.appendChild(element3);
            if (arrstring[2] != null) {
                element3.setAttribute("lang", arrstring[2]);
            }
            element3.appendChild(document.createTextNode(arrstring[1]));
            element.appendChild(element2);
        }
        return element;
    }

    protected void setValues(Element element) throws Exception {
        String string;
        String string2;
        String string3;
        String string4;
        String string5;
        if (!element.getTagName().equals("PaymentOption")) {
            throw new Exception(EXCEPTION_WRONG_XML_STRUCTURE);
        }
        this.m_type = element.getAttribute("type");
        this.m_name = element.getAttribute("name");
        this.m_generic = XMLUtil.parseAttribute((Node)element, "generic", true);
        this.m_minJapVersion = XMLUtil.parseAttribute((Node)element, "japversion", "00.00.000");
        Node node = XMLUtil.getFirstChildByName(element, "Markup");
        this.m_markup = XMLUtil.parseValue(node, 0);
        NodeList nodeList = element.getElementsByTagName("Heading");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String string6 = XMLUtil.parseValue(nodeList.item(i), null);
            string5 = ((Element)nodeList.item(i)).getAttribute("lang");
            if (string5 == null || string6 == null) {
                throw new Exception(EXCEPTION_WRONG_XML_STRUCTURE);
            }
            this.m_headings.addElement(new String[]{string6, string5});
        }
        NodeList nodeList2 = element.getElementsByTagName("DetailedInfo");
        for (int i = 0; i < nodeList2.getLength(); ++i) {
            string5 = XMLUtil.parseValue(nodeList2.item(i), null);
            string4 = ((Element)nodeList2.item(i)).getAttribute("lang");
            if (string4 == null || string5 == null) {
                throw new Exception(EXCEPTION_WRONG_XML_STRUCTURE);
            }
            this.m_detailedInfos.addElement(new String[]{string5, string4});
        }
        NodeList nodeList3 = element.getElementsByTagName("Rank");
        for (int i = 0; i < nodeList3.getLength(); ++i) {
            string4 = ((Element)nodeList3.item(i)).getAttribute("lang");
            int n = XMLUtil.parseValue(nodeList3.item(i), Integer.MAX_VALUE);
            this.m_ranks.put(string4, new Integer(n));
        }
        NodeList nodeList4 = element.getElementsByTagName("PaymentDelay");
        for (int i = 0; i < nodeList4.getLength(); ++i) {
            String string7 = XMLUtil.parseValue(nodeList4.item(i), "");
            string3 = ((Element)nodeList4.item(i)).getAttribute("lang");
            if (string3 == null || string7 == null) {
                throw new Exception(EXCEPTION_WRONG_XML_STRUCTURE);
            }
            this.m_paymentDelays.addElement(new String[]{string7, string3});
        }
        NodeList nodeList5 = element.getElementsByTagName("ExtraInfo");
        for (int i = 0; i < nodeList5.getLength(); ++i) {
            string3 = XMLUtil.parseValue(nodeList5.item(i), null);
            string2 = ((Element)nodeList5.item(i)).getAttribute("lang");
            string = ((Element)nodeList5.item(i)).getAttribute("type");
            if (string2 == null || string3 == null || string == null) {
                throw new Exception(EXCEPTION_WRONG_XML_STRUCTURE);
            }
            this.m_extraInfos.addElement(new String[]{string3, string, string2});
        }
        NodeList nodeList6 = element.getElementsByTagName("input");
        for (int i = 0; i < nodeList6.getLength(); ++i) {
            string2 = XMLUtil.parseValue(nodeList6.item(i).getFirstChild(), null);
            string = ((Element)nodeList6.item(i).getFirstChild()).getAttribute("lang");
            String string8 = ((Element)nodeList6.item(i)).getAttribute("ref");
            if (string == null || string2 == null || string8 == null) {
                throw new Exception(EXCEPTION_WRONG_XML_STRUCTURE);
            }
            this.m_inputFields.addElement(new String[]{string8, string2, string});
        }
        try {
            String string9 = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "ImageLink").getFirstChild(), "0");
            if (!string9.equals("0")) {
                this.m_imageLink = string9;
            }
        }
        catch (Exception exception) {
            this.m_imageLink = null;
        }
    }

    public void setType(String string) {
        this.m_type = string;
    }

    public String getHeading(String string) {
        for (int i = 0; i < this.m_headings.size(); ++i) {
            String[] arrstring = (String[])this.m_headings.elementAt(i);
            if (!arrstring[1].equalsIgnoreCase(string)) continue;
            return arrstring[0];
        }
        if (string.equals("en")) {
            return null;
        }
        return this.getHeading("en");
    }

    public String getDetailedInfo(String string) {
        for (int i = 0; i < this.m_detailedInfos.size(); ++i) {
            String[] arrstring = (String[])this.m_detailedInfos.elementAt(i);
            if (!arrstring[1].equalsIgnoreCase(string)) continue;
            return arrstring[0];
        }
        if (string.equals("en")) {
            return null;
        }
        return this.getDetailedInfo("en");
    }

    public Integer getRank(String string) {
        Integer n = (Integer)this.m_ranks.get(string);
        if (n == null && string.equalsIgnoreCase("en")) {
            n = new Integer(Integer.MAX_VALUE);
        }
        return n;
    }

    public String getPaymentDelay(String string) {
        for (int i = 0; i < this.m_paymentDelays.size(); ++i) {
            String[] arrstring = (String[])this.m_paymentDelays.elementAt(i);
            if (!arrstring[1].equalsIgnoreCase(string)) continue;
            return arrstring[0];
        }
        if (!string.equalsIgnoreCase("en")) {
            return this.getPaymentDelay("en");
        }
        return null;
    }

    public String getExtraInfo(String string) {
        for (int i = 0; i < this.m_extraInfos.size(); ++i) {
            String[] arrstring = (String[])this.m_extraInfos.elementAt(i);
            if (!arrstring[2].equalsIgnoreCase(string)) continue;
            return arrstring[0];
        }
        if (string.equals("en")) {
            return null;
        }
        return this.getExtraInfo("en");
    }

    public Vector getExtraInfos() {
        return (Vector)this.m_extraInfos.clone();
    }

    public Vector getLocalizedExtraInfoText(String string) {
        Vector vector = this.getExtraInfos();
        Vector vector2 = new Vector();
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            String[] arrstring = (String[])enumeration.nextElement();
            if (!arrstring[2].equals(string)) continue;
            vector2.addElement(arrstring[0]);
        }
        if (!(vector2.size() >= 1 || string != null && string.equals("en"))) {
            vector2 = this.getLocalizedExtraInfoText("en");
        }
        return vector2;
    }

    public String getType() {
        return this.m_type;
    }

    public String getName() {
        return this.m_name;
    }

    public String getExtraInfoType(String string) {
        for (int i = 0; i < this.m_extraInfos.size(); ++i) {
            String[] arrstring = (String[])this.m_extraInfos.elementAt(i);
            if (!arrstring[2].equalsIgnoreCase(string)) continue;
            return arrstring[1];
        }
        if (string == null || string.equals("en")) {
            return "unknown";
        }
        return this.getExtraInfoType("en");
    }

    public Vector getInputFields() {
        return (Vector)this.m_inputFields.clone();
    }

    public Vector getLanguages() {
        return (Vector)m_languages.clone();
    }

    public boolean isGeneric() {
        return this.m_generic;
    }

    public int getMarkup() {
        return this.m_markup;
    }

    public String getMinJapVersion() {
        return this.m_minJapVersion;
    }

    public boolean isNewer(XMLPaymentOption xMLPaymentOption) {
        if (this.m_minJapVersion == null) {
            return false;
        }
        if (xMLPaymentOption.getMinJapVersion() == null) {
            return true;
        }
        return Util.convertVersionStringToNumber(this.m_minJapVersion) > Util.convertVersionStringToNumber(xMLPaymentOption.getMinJapVersion());
    }

    public boolean worksWithJapVersion(String string) {
        return this.m_minJapVersion == null || Util.convertVersionStringToNumber(this.m_minJapVersion) <= Util.convertVersionStringToNumber(string);
    }

    private static void addLanguage(String string) {
        if (!m_languages.contains(string)) {
            m_languages.addElement(string);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

