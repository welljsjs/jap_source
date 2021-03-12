/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Transaction
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "Transaction";
    private static final String XML_ELEM_ACCOUNT_NUMBER = "AccountNumber";
    private static final String XML_ELEM_AMOUNT_EURO_CENT = "AmountEuroCent";
    private static final String XML_ELEM_CREATION_TIME = "CreationTime";
    private static final String XML_ELEM_USED_TIME = "UsedTime";
    private static final String XML_ELEM_PAYMENT_METHOD = "PaymentMethod";
    private static final String XML_ELEM_RATE = "Rate";
    private static final String XML_ATTR_DONE = "done";
    public static final long T_EXPIRE = 1209600000L;
    private long m_lTransactionID;
    private String m_strPaymentMethod;
    private long m_lAccountNr;
    private int m_iAmountInEuroCent;
    private String m_rate;
    private String m_rateID;
    private boolean m_bDone;
    private Timestamp m_tCreation;
    private Timestamp m_tExpiration;
    private Timestamp m_tUsed;

    public Transaction(long l, long l2, int n, String string, String string2, Timestamp timestamp, Timestamp timestamp2, String string3, boolean bl) {
        this.m_lTransactionID = l;
        this.m_strPaymentMethod = string3;
        this.m_tUsed = timestamp2;
        this.m_lAccountNr = l2;
        this.m_iAmountInEuroCent = n;
        this.m_rate = string;
        this.m_rateID = string2;
        this.m_bDone = bl;
        this.m_tCreation = timestamp;
        this.m_tExpiration = new Timestamp(this.m_tCreation.getTime() + 1209600000L);
    }

    public Transaction(Element element) throws XMLParseException {
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        this.m_lTransactionID = XMLUtil.parseAttribute((Node)element, "id", 0L);
        if (this.m_lTransactionID <= 0L) {
            throw new XMLParseException(XML_ELEMENT_NAME);
        }
        this.m_bDone = XMLUtil.parseAttribute((Node)element, XML_ATTR_DONE, false);
        this.m_lAccountNr = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_ACCOUNT_NUMBER), 0L);
        if (this.m_lAccountNr <= 0L) {
            throw new XMLParseException(XML_ELEM_ACCOUNT_NUMBER);
        }
        this.m_iAmountInEuroCent = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_AMOUNT_EURO_CENT), 0);
        String string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_CREATION_TIME), null);
        if (string != null) {
            this.m_tCreation = Timestamp.valueOf(string);
        }
        if (this.m_tCreation == null) {
            throw new XMLParseException(XML_ELEM_CREATION_TIME);
        }
        this.m_tExpiration = new Timestamp(this.m_tCreation.getTime() + 1209600000L);
        string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_USED_TIME), null);
        if (string != null) {
            this.m_tUsed = Timestamp.valueOf(string);
        }
        this.m_strPaymentMethod = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_PAYMENT_METHOD), null);
        this.m_rate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_RATE), null);
        this.m_rateID = XMLUtil.parseAttribute(XMLUtil.getFirstChildByName(element, XML_ELEM_RATE), "id", null);
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element, "id", this.m_lTransactionID);
        XMLUtil.setAttribute(element, XML_ATTR_DONE, this.m_bDone);
        Element element2 = document.createElement(XML_ELEM_ACCOUNT_NUMBER);
        XMLUtil.setValue((Node)element2, this.m_lAccountNr);
        element.appendChild(element2);
        element2 = document.createElement(XML_ELEM_AMOUNT_EURO_CENT);
        XMLUtil.setValue((Node)element2, this.m_iAmountInEuroCent);
        element.appendChild(element2);
        element2 = document.createElement(XML_ELEM_CREATION_TIME);
        XMLUtil.setValue((Node)element2, this.m_tCreation.toString());
        element.appendChild(element2);
        if (this.m_tUsed != null) {
            element2 = document.createElement(XML_ELEM_USED_TIME);
            XMLUtil.setValue((Node)element2, this.m_tUsed.toString());
            element.appendChild(element2);
        }
        element2 = document.createElement(XML_ELEM_PAYMENT_METHOD);
        XMLUtil.setValue((Node)element2, this.m_strPaymentMethod);
        element.appendChild(element2);
        element2 = document.createElement(XML_ELEM_RATE);
        XMLUtil.setValue((Node)element2, this.m_rate);
        XMLUtil.setAttribute(element2, "id", this.m_rateID);
        element.appendChild(element2);
        return element;
    }

    public String getPaymentMethod() {
        return this.m_strPaymentMethod;
    }

    public long getID() {
        return this.m_lTransactionID;
    }

    public long getAccountNumber() {
        return this.m_lAccountNr;
    }

    public int getAmountEuroCent() {
        return this.m_iAmountInEuroCent;
    }

    public String getRateName() {
        return this.m_rate;
    }

    public String getRateID() {
        return this.m_rateID;
    }

    public boolean isUsed() {
        return this.m_bDone;
    }

    public Timestamp getUsedTime() {
        return this.m_tUsed;
    }

    public Timestamp getCreationTime() {
        return this.m_tCreation;
    }

    public boolean hasExpired() {
        return this.hasExpired(new Timestamp(System.currentTimeMillis()));
    }

    public boolean hasExpired(Timestamp timestamp) {
        return !this.m_bDone && this.m_tExpiration.before(timestamp);
    }
}

