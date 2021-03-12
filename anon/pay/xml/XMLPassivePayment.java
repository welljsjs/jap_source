/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.PayAccountsFile;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLPassivePayment
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "PassivePayment";
    private static final String XML_DOCUMENT_VERSION = "1.0";
    private static final String VERSION = "version";
    private static final String TRANSFER_NUM = "TransferNumber";
    private static final String AMOUNT = "Amount";
    private static final String CURRENCY = "Currency";
    private static final String CHARGED = "Charged";
    private static final String PAYMENT_DATA = "PaymentData";
    private static final String REF = "ref";
    private static final String PAYMENT_NAME = "PaymentName";
    private static final String ERRORCODE = "ErrorCode";
    private static final String ERRORMSG = "ErrorMessage";
    private static final String IP = "IPAdress";
    private Hashtable m_paymentData = new Hashtable();
    private long m_transferNumber;
    private String m_currency;
    private long m_centAmount;
    private String m_paymentName;
    private boolean m_charged;
    private String m_sErrorCode = "";
    private String m_sErrorMessage = "";
    private String m_sIP = "";
    private String m_strAffiliate;
    public static final String KEY_COUPONCODE = "code";
    public static final String KEY_ACCOUNTNUMBER = "accountnumber";
    public static final String KEY_TRANSFERNUMBER = "transfernumber";
    public static final String KEY_VOLUMEPLAN = "volumeplan";
    public static final String KEY_MERCHANT_ID = "merchant_id";
    public static final String KEY_TRANSACTION_ID = "transaction_id";
    public static final String KEY_ERRORCODE = "errorcode";
    public static final String KEY_ERRORMESSAGE = "errormessage";
    public static final String KEY_IPADRESS = "IPAdress";

    public XMLPassivePayment(String string) {
        this.m_strAffiliate = PayAccountsFile.getInstance().getAffiliate(string, false);
    }

    public XMLPassivePayment(Element element) throws XMLParseException {
        this.setValues(element);
    }

    private void setValues(Element element) throws XMLParseException {
        if (!element.getTagName().equals(XML_ELEMENT_NAME) || !element.getAttribute(VERSION).equals(XML_DOCUMENT_VERSION)) {
            throw new XMLParseException("PassivePayment wrong format or wrong version number");
        }
        this.m_paymentData = new Hashtable();
        NodeList nodeList = element.getElementsByTagName(PAYMENT_DATA);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String string = XMLUtil.parseAttribute(nodeList.item(i), REF, null);
            String string2 = XMLUtil.parseValue(nodeList.item(i), null);
            this.m_paymentData.put(string, string2);
        }
        this.m_transferNumber = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, TRANSFER_NUM), 0L);
        this.m_centAmount = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, AMOUNT), 0L);
        this.m_currency = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, CURRENCY), null);
        this.m_paymentName = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, PAYMENT_NAME), null);
        this.m_charged = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, CHARGED), false);
        this.m_sErrorCode = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, ERRORCODE), "0");
        this.m_sErrorMessage = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, ERRORMSG), "");
        this.m_strAffiliate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "Affiliate"), null);
    }

    public String getAffiliate() {
        return this.m_strAffiliate;
    }

    public void setIP(String string) {
        this.m_sIP = string;
    }

    public String getIP() {
        return this.m_sIP;
    }

    public void setErrorMessage(String string) {
        this.m_sErrorMessage = string;
    }

    public String getErrorMessage() {
        return this.m_sErrorMessage;
    }

    public void setErrorCode(String string) {
        this.m_sErrorCode = string;
    }

    public String getErrorCode() {
        return this.m_sErrorCode;
    }

    public void setPaymentName(String string) {
        this.m_paymentName = string;
    }

    public String getPaymentName() {
        return this.m_paymentName;
    }

    public void setAmount(long l) {
        this.m_centAmount = l;
    }

    public void setCurrency(String string) {
        this.m_currency = string;
    }

    public void setCharged(boolean bl) {
        this.m_charged = bl;
    }

    public void setTransferNumber(long l) {
        this.m_transferNumber = l;
    }

    public void addData(String string, String string2) {
        this.m_paymentData.put(string, string2);
    }

    public long getAmount() {
        return this.m_centAmount;
    }

    public long getTransferNumber() {
        return this.m_transferNumber;
    }

    public String getCurrency() {
        return this.m_currency;
    }

    public boolean isCharged() {
        return this.m_charged;
    }

    public Enumeration getReferences() {
        return this.m_paymentData.keys();
    }

    public String getPaymentData(String string) {
        return (String)this.m_paymentData.get(string);
    }

    public String getAllPaymentData() {
        String string = "";
        Enumeration enumeration = this.m_paymentData.keys();
        while (enumeration.hasMoreElements()) {
            String string2 = (String)enumeration.nextElement();
            string = string + string2 + " = " + (String)this.m_paymentData.get(string2);
            if (!enumeration.hasMoreElements()) continue;
            string = string + "\n";
        }
        return string;
    }

    public Enumeration getPaymentDataKeys() {
        return this.m_paymentData.keys();
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute(VERSION, XML_DOCUMENT_VERSION);
        Element element2 = document.createElement(TRANSFER_NUM);
        XMLUtil.setValue((Node)element2, this.m_transferNumber);
        element.appendChild(element2);
        element2 = document.createElement(PAYMENT_NAME);
        XMLUtil.setValue((Node)element2, this.m_paymentName);
        element.appendChild(element2);
        element2 = document.createElement(AMOUNT);
        XMLUtil.setValue((Node)element2, String.valueOf(this.m_centAmount));
        element.appendChild(element2);
        element2 = document.createElement(CURRENCY);
        XMLUtil.setValue((Node)element2, this.m_currency);
        element.appendChild(element2);
        element2 = document.createElement(CHARGED);
        XMLUtil.setValue((Node)element2, this.m_charged);
        element.appendChild(element2);
        element2 = document.createElement(ERRORCODE);
        XMLUtil.setValue((Node)element2, this.m_sErrorCode);
        element.appendChild(element2);
        element2 = document.createElement(ERRORMSG);
        XMLUtil.setValue((Node)element2, this.m_sErrorMessage);
        element.appendChild(element2);
        element2 = document.createElement("IPAdress");
        XMLUtil.setValue((Node)element2, this.m_sIP);
        element.appendChild(element2);
        if (this.m_strAffiliate != null) {
            element2 = document.createElement("Affiliate");
            XMLUtil.setValue((Node)element2, this.m_strAffiliate);
            element.appendChild(element2);
        }
        Enumeration enumeration = this.m_paymentData.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            element2 = document.createElement(PAYMENT_DATA);
            XMLUtil.setAttribute(element2, REF, string);
            XMLUtil.setValue((Node)element2, (String)this.m_paymentData.get(string));
            element.appendChild(element2);
        }
        return element;
    }
}

