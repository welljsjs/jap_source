/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.Transaction;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLTransactionOverview
implements IXMLEncodable {
    public static final Object XML_ELEMENT_NAME = "TransactionOverview";
    private Vector m_transactions = new Vector();
    private String m_language;
    public static final String KEY_ACCOUNTNUMBER = "accountnumber";
    public static final String KEY_TAN = "tan";
    public static final String KEY_DATE = "date";
    public static final String KEY_CREATIONDATE = "created_on";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_VOLUMEPLAN = "volumeplan";
    public static final String KEY_PAYMENTMETHOD = "paymentmethod";
    public static final String KEY_USED = "used";

    public XMLTransactionOverview(String string) {
        this.m_language = string;
    }

    public XMLTransactionOverview(char[] arrc) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrc);
        this.setValues(document.getDocumentElement());
    }

    public XMLTransactionOverview(byte[] arrby) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrby);
        this.setValues(document.getDocumentElement());
    }

    public XMLTransactionOverview(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
    }

    public XMLTransactionOverview(Element element) throws Exception {
        this.setValues(element);
    }

    public int size() {
        return this.m_transactions.size();
    }

    private void setValues(Element element) throws Exception {
        this.m_transactions = new Vector();
        if (!element.getTagName().equals(XML_ELEMENT_NAME) || !element.getAttribute("version").equals("1.1")) {
            throw new Exception("TransactionOverview wrong format or wrong version number");
        }
        this.m_language = element.getAttribute("language");
        NodeList nodeList = element.getElementsByTagName("TransferNumber");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            Element element2 = (Element)nodeList.item(i);
            String string = element2.getFirstChild().getNodeValue();
            string = XMLUtil.parseValue((Node)element2, "");
            hashtable.put(KEY_TAN, string);
            String string2 = element2.getAttribute(KEY_USED) != null ? element2.getAttribute(KEY_USED) : "false";
            hashtable.put(KEY_USED, string2);
            String string3 = element2.getAttribute(KEY_CREATIONDATE) != null ? element2.getAttribute(KEY_CREATIONDATE) : "0";
            hashtable.put(KEY_CREATIONDATE, string3);
            String string4 = element2.getAttribute(KEY_DATE) != null ? element2.getAttribute(KEY_DATE) : "0";
            hashtable.put(KEY_DATE, string4);
            String string5 = element2.getAttribute(KEY_AMOUNT) != null ? element2.getAttribute(KEY_AMOUNT) : "0";
            hashtable.put(KEY_AMOUNT, string5);
            String string6 = element2.getAttribute(KEY_ACCOUNTNUMBER) != null ? element2.getAttribute(KEY_ACCOUNTNUMBER) : "";
            hashtable.put(KEY_ACCOUNTNUMBER, string6);
            String string7 = element2.getAttribute(KEY_VOLUMEPLAN) != null ? element2.getAttribute(KEY_VOLUMEPLAN) : "";
            hashtable.put(KEY_VOLUMEPLAN, string7);
            String string8 = element2.getAttribute(KEY_PAYMENTMETHOD) != null ? element2.getAttribute(KEY_PAYMENTMETHOD) : "";
            hashtable.put(KEY_PAYMENTMETHOD, string8);
            this.m_transactions.addElement(hashtable);
        }
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement("TransactionOverview");
        element.setAttribute("version", "1.1");
        element.setAttribute("language", this.m_language);
        Enumeration enumeration = this.m_transactions.elements();
        while (enumeration.hasMoreElements()) {
            Hashtable hashtable = (Hashtable)enumeration.nextElement();
            Element element2 = document.createElement("TransferNumber");
            String string = (String)hashtable.get(KEY_CREATIONDATE);
            string = string == null ? "" : string;
            element2.setAttribute(KEY_CREATIONDATE, string);
            String string2 = (String)hashtable.get(KEY_ACCOUNTNUMBER);
            string2 = string2 == null ? "" : string2;
            element2.setAttribute(KEY_ACCOUNTNUMBER, string2);
            String string3 = (String)hashtable.get(KEY_DATE);
            string3 = string3 == null ? "" : string3;
            element2.setAttribute(KEY_DATE, string3);
            String string4 = (String)hashtable.get(KEY_AMOUNT);
            string4 = string4 == null ? "" : string4;
            element2.setAttribute(KEY_AMOUNT, string4);
            String string5 = (String)hashtable.get(KEY_VOLUMEPLAN);
            string5 = string5 == null ? "" : string5;
            element2.setAttribute(KEY_VOLUMEPLAN, string5);
            String string6 = (String)hashtable.get(KEY_PAYMENTMETHOD);
            string6 = string6 == null ? "" : string6;
            element2.setAttribute(KEY_PAYMENTMETHOD, string6);
            String string7 = (String)hashtable.get(KEY_USED);
            string7 = string7 == null ? "" : string7;
            element2.setAttribute(KEY_USED, string7);
            String string8 = (String)hashtable.get(KEY_TAN);
            element2.appendChild(document.createTextNode(string8));
            element.appendChild(element2);
        }
        return element;
    }

    public Vector getTans() {
        return this.m_transactions;
    }

    public String getLanguage() {
        return this.m_language;
    }

    public boolean isUsed(long l) {
        boolean bl = false;
        Hashtable hashtable = this.getDataForTransaction(l);
        if (hashtable != null) {
            String string = (String)hashtable.get(KEY_USED);
            bl = Boolean.valueOf(string);
        }
        return bl;
    }

    public Hashtable getDataForTransaction(long l) {
        Hashtable hashtable = null;
        Enumeration enumeration = this.m_transactions.elements();
        while (enumeration.hasMoreElements()) {
            Hashtable hashtable2 = (Hashtable)enumeration.nextElement();
            String string = (String)hashtable2.get(KEY_TAN);
            try {
                long l2 = Long.parseLong(string);
                if (l2 != l) continue;
                hashtable = hashtable2;
                break;
            }
            catch (NumberFormatException numberFormatException) {
                LogHolder.log(3, LogType.PAY, numberFormatException);
            }
        }
        return hashtable;
    }

    public void setTransactionData(long l, long l2, boolean bl, long l3, long l4, long l5, String string, String string2) {
        String string3 = l5 == 0L ? new String("") : new Long(l5).toString();
        String string4 = l4 == 0L ? new String("") : new Long(l4).toString();
        String string5 = l2 == 0L ? new String("") : new Long(l2).toString();
        String string6 = l3 == 0L ? new String("") : new Long(l3).toString();
        if (string == null) {
            string = new String("");
        }
        if (string2 == null) {
            string2 = new String("");
        }
        Hashtable hashtable = this.getDataForTransaction(l);
        hashtable.put(KEY_USED, new Boolean(bl).toString());
        hashtable.put(KEY_DATE, string6);
        hashtable.put(KEY_CREATIONDATE, string5);
        hashtable.put(KEY_ACCOUNTNUMBER, string3);
        hashtable.put(KEY_AMOUNT, string4);
        hashtable.put(KEY_VOLUMEPLAN, string);
        hashtable.put(KEY_PAYMENTMETHOD, string2);
    }

    public void addTan(Transaction transaction) {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put(KEY_TAN, new Long(transaction.getID()).toString());
        hashtable.put(KEY_USED, new Boolean(transaction.isUsed()).toString());
        if (transaction.getUsedTime() != null) {
            hashtable.put(KEY_DATE, new Long(transaction.getUsedTime().getTime()).toString());
        }
        hashtable.put(KEY_CREATIONDATE, new Long(transaction.getCreationTime().getTime()).toString());
        hashtable.put(KEY_ACCOUNTNUMBER, new Long(transaction.getAccountNumber()).toString());
        hashtable.put(KEY_AMOUNT, new Integer(transaction.getAmountEuroCent()).toString());
        hashtable.put(KEY_VOLUMEPLAN, transaction.getRateName() == null ? "" : transaction.getRateName());
        hashtable.put(KEY_PAYMENTMETHOD, transaction.getPaymentMethod());
        this.m_transactions.addElement(hashtable);
    }
}

