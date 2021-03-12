/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.crypto.IVerifyable;
import anon.crypto.JAPCertificate;
import anon.crypto.MultiCertPath;
import anon.crypto.SignatureCreator;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDistributableCertifiedDatabaseEntry;
import anon.infoservice.ListenerInterface;
import anon.infoservice.ServiceSoftware;
import anon.pay.PayAccountsFile;
import anon.util.JAPMessages;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PaymentInstanceDBEntry
extends AbstractDistributableCertifiedDatabaseEntry
implements IVerifyable {
    public static final String XML_ELEMENT_NAME = "PaymentInstance";
    public static final String XML_ELEMENT_CONTAINER_NAME = "PaymentInstances";
    private static final String MSG_TEST_NAME = (class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PaymentInstanceDBEntry.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getName() + ".testInstanceAlternativeName";
    private static final String XML_ELEM_NAME = "Name";
    private static final String XML_ELEM_CERT = "Certificate";
    private static final String XML_ELEM_NET = "Network";
    private static final String XML_ELEM_WEBSHOP_URLS = "WebshopURLs";
    private static final String XML_ELEM_FREE_CODE_URLS = "FreeCodeURLs";
    private static final String XML_ELEM_WEBSHOP_URL = "URL";
    private static final String XML_ATTR_WEBSHOP_AFFILIATE_ARGUMENT = "affiliateArgument";
    private static final String XML_ATTR_WEBSHOP_ARGUMENT = "shopArgument";
    private static final String XML_ATTR_WEBSHOP_LANGUAGE_ARGUMENT = "language";
    private static final String XML_ATTR_WEBSHOP_TRANSACTION_ARGUMENT = "transactionArgument";
    private static final String XML_ATTR_WEBSHOP_RATE_ARGUMENT = "rateArgument";
    private String m_strPaymentInstanceId;
    private boolean m_bIsTest = false;
    private Element m_xmlDescription;
    private XMLSignature m_signature;
    private MultiCertPath m_certPath;
    private Hashtable m_hashWebshopURLs = new Hashtable();
    private Hashtable m_hashFreeCodeURLs = new Hashtable();
    private String m_affiliateArgument;
    private String m_argLanguage;
    private String m_shopArgument;
    private String m_argTransaction;
    private String m_argRate;
    private long m_creationTimeStamp;
    private long m_serialNumber;
    private Vector m_listenerInterfaces;
    private String m_name;
    private String m_strOrganisation;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;

    public PaymentInstanceDBEntry(Element element) throws XMLParseException {
        this(element, 0L);
    }

    public PaymentInstanceDBEntry(Element element, long l) throws XMLParseException {
        super(l == 0L ? System.currentTimeMillis() + 900000L : l);
        int n;
        XMLUtil.assertNotNull(element);
        this.m_xmlDescription = element;
        String string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_NAME), null);
        if (string == null) {
            throw new XMLParseException(XML_ELEM_NAME);
        }
        this.m_signature = SignatureVerifier.getInstance().getVerifiedXml(element, 4);
        if (this.m_signature != null) {
            this.m_certPath = this.m_signature.getMultiCertPath();
            if (this.m_certPath != null) {
                this.m_strOrganisation = this.m_certPath.getSubject().getOrganisation();
            }
        }
        this.m_strPaymentInstanceId = element.getAttribute("id");
        if (!this.checkId()) {
            throw new XMLParseException(element.getNodeName(), "Invalid Payment-Instance ID: " + this.m_strPaymentInstanceId);
        }
        this.m_name = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_NAME), "");
        this.checkName();
        this.m_creationTimeStamp = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "LastUpdate"), -1L);
        if (this.m_creationTimeStamp == -1L) {
            throw new XMLParseException("LastUpdate");
        }
        this.m_serialNumber = XMLUtil.parseAttribute((Node)element, "serial", this.m_creationTimeStamp);
        Node node = XMLUtil.getFirstChildByName(element, XML_ELEM_WEBSHOP_URLS);
        NodeList nodeList = XMLUtil.getElementsByTagName(node, XML_ELEM_WEBSHOP_URL);
        this.m_affiliateArgument = XMLUtil.parseAttribute(node, XML_ATTR_WEBSHOP_AFFILIATE_ARGUMENT, null);
        this.m_argLanguage = XMLUtil.parseAttribute(node, XML_ATTR_WEBSHOP_LANGUAGE_ARGUMENT, null);
        this.m_shopArgument = XMLUtil.parseAttribute(node, XML_ATTR_WEBSHOP_ARGUMENT, null);
        this.m_argRate = XMLUtil.parseAttribute(node, XML_ATTR_WEBSHOP_RATE_ARGUMENT, null);
        this.m_argTransaction = XMLUtil.parseAttribute(node, XML_ATTR_WEBSHOP_TRANSACTION_ARGUMENT, null);
        this.m_hashWebshopURLs = new Hashtable();
        for (n = 0; nodeList != null && n < nodeList.getLength(); ++n) {
            try {
                this.m_hashWebshopURLs.put(XMLUtil.parseAttribute(nodeList.item(n), "lang", "en"), new URL(XMLUtil.parseValue(nodeList.item(n), null)));
                continue;
            }
            catch (MalformedURLException malformedURLException) {
                LogHolder.log(5, LogType.PAY, malformedURLException);
            }
        }
        if (this.m_hashWebshopURLs.size() == 0) {
            this.m_hashWebshopURLs = null;
        }
        node = XMLUtil.getFirstChildByName(element, XML_ELEM_FREE_CODE_URLS);
        nodeList = XMLUtil.getElementsByTagName(node, XML_ELEM_WEBSHOP_URL);
        this.m_hashFreeCodeURLs = new Hashtable();
        for (n = 0; nodeList != null && n < nodeList.getLength(); ++n) {
            try {
                this.m_hashFreeCodeURLs.put(XMLUtil.parseAttribute(nodeList.item(n), "lang", "en"), new URL(XMLUtil.parseValue(nodeList.item(n), null)));
                continue;
            }
            catch (MalformedURLException malformedURLException) {
                LogHolder.log(5, LogType.PAY, malformedURLException);
            }
        }
        if (this.m_hashFreeCodeURLs.size() == 0) {
            this.m_hashFreeCodeURLs = null;
        }
        Node node2 = XMLUtil.getFirstChildByName(XMLUtil.getFirstChildByName(element, XML_ELEM_NET), "ListenerInterfaces");
        XMLUtil.assertNotNull(node2);
        NodeList nodeList2 = ((Element)node2).getElementsByTagName("ListenerInterface");
        if (nodeList2.getLength() == 0) {
            throw new XMLParseException("ListenerInterface");
        }
        this.m_listenerInterfaces = new Vector();
        for (int i = 0; i < nodeList2.getLength(); ++i) {
            this.m_listenerInterfaces.addElement(new ListenerInterface((Element)nodeList2.item(i)));
        }
    }

    public PaymentInstanceDBEntry(String string, String string2, JAPCertificate jAPCertificate, Enumeration enumeration, String string3, long l, long l2, Hashtable hashtable, Hashtable hashtable2, String string4, String string5, String string6, String string7, String string8) {
        super(System.currentTimeMillis() + 900000L);
        String string9;
        Element element;
        Object object;
        Object object2;
        this.m_shopArgument = string7;
        this.m_argLanguage = string8;
        this.m_affiliateArgument = string4;
        this.m_argTransaction = string5;
        this.m_argRate = string6;
        this.m_strPaymentInstanceId = string;
        this.m_creationTimeStamp = l;
        this.m_serialNumber = l2;
        this.m_name = string2;
        if (hashtable != null) {
            this.m_hashWebshopURLs = (Hashtable)hashtable.clone();
            if (this.m_hashWebshopURLs.size() == 0) {
                this.m_hashWebshopURLs = null;
            }
        }
        if (hashtable2 != null) {
            this.m_hashFreeCodeURLs = (Hashtable)hashtable2.clone();
            if (this.m_hashFreeCodeURLs.size() == 0) {
                this.m_hashFreeCodeURLs = null;
            }
        }
        Document document = XMLUtil.createDocument();
        Element element2 = document.createElement(XML_ELEMENT_NAME);
        document.appendChild(element2);
        XMLUtil.setAttribute(element2, "id", this.m_strPaymentInstanceId);
        XMLUtil.setAttribute(element2, "serial", this.m_serialNumber);
        Element element3 = document.createElement(XML_ELEM_NAME);
        XMLUtil.setValue((Node)element3, this.m_name);
        element2.appendChild(element3);
        ServiceSoftware serviceSoftware = new ServiceSoftware(string3);
        element2.appendChild(serviceSoftware.toXmlElement(document));
        Element element4 = document.createElement(XML_ELEM_NET);
        element2.appendChild(element4);
        Element element5 = document.createElement("ListenerInterfaces");
        element4.appendChild(element5);
        while (enumeration.hasMoreElements()) {
            object2 = (ListenerInterface)enumeration.nextElement();
            element5.appendChild(((ListenerInterface)object2).toXmlElement(document));
        }
        if (this.m_hashWebshopURLs != null) {
            object2 = document.createElement(XML_ELEM_WEBSHOP_URLS);
            object = this.m_hashWebshopURLs.keys();
            if (this.m_affiliateArgument != null) {
                XMLUtil.setAttribute((Element)object2, XML_ATTR_WEBSHOP_AFFILIATE_ARGUMENT, this.m_affiliateArgument);
            }
            if (this.m_shopArgument != null) {
                XMLUtil.setAttribute((Element)object2, XML_ATTR_WEBSHOP_ARGUMENT, this.m_shopArgument);
            }
            if (this.m_argLanguage != null) {
                XMLUtil.setAttribute((Element)object2, XML_ATTR_WEBSHOP_LANGUAGE_ARGUMENT, this.m_argLanguage);
            }
            if (this.m_argTransaction != null) {
                XMLUtil.setAttribute((Element)object2, XML_ATTR_WEBSHOP_TRANSACTION_ARGUMENT, this.m_argTransaction);
            }
            if (this.m_argRate != null) {
                XMLUtil.setAttribute((Element)object2, XML_ATTR_WEBSHOP_RATE_ARGUMENT, this.m_argRate);
            }
            while (object.hasMoreElements()) {
                element = document.createElement(XML_ELEM_WEBSHOP_URL);
                string9 = (String)object.nextElement();
                XMLUtil.setAttribute(element, "lang", string9);
                XMLUtil.setValue((Node)element, ((URL)this.m_hashWebshopURLs.get(string9)).toString());
                object2.appendChild(element);
            }
            element2.appendChild((Node)object2);
        }
        if (this.m_hashFreeCodeURLs != null) {
            object2 = document.createElement(XML_ELEM_FREE_CODE_URLS);
            object = this.m_hashFreeCodeURLs.keys();
            while (object.hasMoreElements()) {
                element = document.createElement(XML_ELEM_WEBSHOP_URL);
                string9 = (String)object.nextElement();
                XMLUtil.setAttribute(element, "lang", string9);
                XMLUtil.setValue((Node)element, ((URL)this.m_hashFreeCodeURLs.get(string9)).toString());
                object2.appendChild(element);
            }
            element2.appendChild((Node)object2);
        }
        object2 = document.createElement("LastUpdate");
        XMLUtil.setValue((Node)object2, this.m_creationTimeStamp);
        element2.appendChild((Node)object2);
        if (jAPCertificate != null) {
            object = document.createElement(XML_ELEM_CERT);
            element2.appendChild((Node)object);
            object.appendChild(jAPCertificate.toXmlElement(document));
            this.m_signature = SignatureCreator.getInstance().getSignedXml(4, element2);
            if (this.m_signature != null) {
                this.m_certPath = this.m_signature.getMultiCertPath();
            }
            if (this.m_certPath == null) {
                LogHolder.log(2, LogType.MISC, "Document could not be signed!");
            }
            this.m_strOrganisation = jAPCertificate.getSubject().getOrganisation();
        }
        this.m_xmlDescription = element2;
    }

    public URL getWebshopURL() {
        return this.getWebshopURL(0L, null);
    }

    public URL getFreeCodeURL() {
        URL uRL = null;
        if (this.m_hashFreeCodeURLs != null && this.m_hashFreeCodeURLs.size() > 0 && (uRL = (URL)this.m_hashFreeCodeURLs.get(JAPMessages.getLocale().getLanguage().toLowerCase())) == null) {
            uRL = (URL)this.m_hashFreeCodeURLs.get("en");
        }
        return uRL;
    }

    public URL getWebshopURL(long l, String string) {
        URL uRL = null;
        Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
        if (this.m_hashWebshopURLs != null && this.m_hashWebshopURLs.size() > 0) {
            String string2;
            uRL = (URL)this.m_hashWebshopURLs.get(JAPMessages.getLocale().getLanguage().toLowerCase());
            if (uRL == null) {
                uRL = (URL)this.m_hashWebshopURLs.get("en");
            }
            String string3 = PayAccountsFile.getInstance().getAffiliate(this.m_strPaymentInstanceId, false);
            if (this.m_affiliateArgument != null && string3 != null) {
                int n = string3.indexOf("_");
                if (this.m_shopArgument != null && n > 0 && string3.length() > n + 1) {
                    string2 = string3.substring(0, n);
                    string3 = string3.substring(n + 1, string3.length());
                    hashtable.put(this.m_shopArgument, string2);
                }
                hashtable.put(this.m_affiliateArgument, string3);
            }
            if (this.m_argLanguage != null) {
                hashtable.put(this.m_argLanguage, JAPMessages.getLocale().getLanguage());
            }
            if (this.m_argRate != null && string != null) {
                hashtable.put(this.m_argRate, string);
            }
            if (this.m_argTransaction != null && l > 0L) {
                hashtable.put(this.m_argTransaction, new Long(l));
            }
            if (uRL != null && hashtable.size() > 0) {
                String string4 = uRL.toString();
                Enumeration enumeration = hashtable.keys();
                while (enumeration.hasMoreElements()) {
                    string4 = string4.indexOf("?") > 0 ? string4 + "&" : string4 + "?";
                    string2 = (String)enumeration.nextElement();
                    string4 = string4 + string2 + "=" + hashtable.get(string2).toString();
                }
                try {
                    uRL = new URL(string4);
                }
                catch (MalformedURLException malformedURLException) {
                    LogHolder.log(1, LogType.PAY, malformedURLException);
                }
            }
        }
        return uRL;
    }

    public boolean isPersistanceDeletionAllowed() {
        return XMLUtil.getStorageMode() == 2;
    }

    public void deletePersistence() {
        if (this.isPersistanceDeletionAllowed()) {
            this.m_signature = null;
        }
    }

    public boolean isVerified() {
        if (this.m_certPath != null) {
            return this.m_certPath.isVerified();
        }
        return false;
    }

    public boolean isValid() {
        if (this.m_certPath != null) {
            return this.m_certPath.isValid(new Date());
        }
        return false;
    }

    public XMLSignature getSignature() {
        return this.m_signature;
    }

    public MultiCertPath getCertPath() {
        return this.m_certPath;
    }

    public String toString() {
        return this.getName();
    }

    public String getOrganisation() {
        return this.m_strOrganisation;
    }

    public String getId() {
        return this.m_strPaymentInstanceId;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PaymentInstanceDBEntry) || object == null) {
            return false;
        }
        PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)object;
        return paymentInstanceDBEntry.getId() == this.getId() || paymentInstanceDBEntry.getId().equals(this.getId());
    }

    public int hashCode() {
        if (this.m_strPaymentInstanceId == null) {
            return 0;
        }
        return this.m_strPaymentInstanceId.hashCode();
    }

    public String getName() {
        return this.m_name;
    }

    public Enumeration getListenerInterfaces() {
        Random random = new Random();
        Vector vector = (Vector)this.m_listenerInterfaces.clone();
        Vector vector2 = new Vector();
        while (vector.size() > 0) {
            int n = Math.abs(random.nextInt() % vector.size());
            vector2.addElement(vector.elementAt(n));
            vector.removeElementAt(n);
        }
        return vector2.elements();
    }

    public long getVersionNumber() {
        return this.m_serialNumber;
    }

    public long getLastUpdate() {
        return this.m_creationTimeStamp;
    }

    public String getPostFile() {
        return "/paymentinstance";
    }

    public Element getXmlStructure() {
        return this.m_xmlDescription;
    }

    public boolean isTest() {
        return this.m_bIsTest;
    }

    private void checkName() {
        if (this.m_name != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(this.m_name);
            while (stringTokenizer.hasMoreElements()) {
                if (!stringTokenizer.nextToken().toLowerCase().equals("test")) continue;
                this.m_bIsTest = true;
                String string = JAPMessages.getString(MSG_TEST_NAME);
                if (string.equals(MSG_TEST_NAME)) break;
                this.m_name = string;
                break;
            }
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

