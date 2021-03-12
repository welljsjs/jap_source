/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.PayAccount;
import anon.pay.xml.XMLBalance;
import anon.pay.xml.XMLEasyCC;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLAccountInfo
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "AccountInfo";
    public static final String XML_ELEMENT_NAME_COST_CONFIRMATIONS = "CostConfirmations";
    private XMLBalance m_balance = null;
    private PayAccount m_callbackAccount;
    private Hashtable m_costConfirmations = new Hashtable();
    private Hashtable m_pastMonthlyVolumeBytes = new Hashtable();
    private Timestamp m_tLastBalanceUpdate = new Timestamp(0L);

    public XMLAccountInfo(XMLBalance xMLBalance) {
        this.m_balance = xMLBalance;
        if (this.m_balance != null) {
            this.m_tLastBalanceUpdate = new Timestamp(System.currentTimeMillis());
        }
    }

    public XMLAccountInfo(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLAccountInfo() {
    }

    public XMLAccountInfo(Element element) throws Exception {
        this.setValues(element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        IXMLEncodable iXMLEncodable;
        Enumeration enumeration;
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("version", "1.1");
        XMLUtil.setAttribute(element, "lastBalanceUpdate", this.m_tLastBalanceUpdate.toString());
        Element element2 = this.m_balance.toXmlElement(document);
        element.appendChild(element2);
        Element element3 = document.createElement(XML_ELEMENT_NAME_COST_CONFIRMATIONS);
        element.appendChild(element3);
        Hashtable hashtable = this.m_costConfirmations;
        synchronized (hashtable) {
            enumeration = this.m_costConfirmations.elements();
            while (enumeration.hasMoreElements()) {
                iXMLEncodable = (XMLEasyCC)enumeration.nextElement();
                element2 = ((XMLEasyCC)iXMLEncodable).toXmlElement(document);
                element3.appendChild(element2);
            }
        }
        element3 = document.createElement("PastMonthsVolumeBytesContainer");
        element.appendChild(element3);
        hashtable = this.m_pastMonthlyVolumeBytes;
        synchronized (hashtable) {
            enumeration = this.m_pastMonthlyVolumeBytes.elements();
            while (enumeration.hasMoreElements()) {
                iXMLEncodable = (PastMonthsVolumeBytes)enumeration.nextElement();
                element2 = ((PastMonthsVolumeBytes)iXMLEncodable).toXmlElement(document);
                element3.appendChild(element2);
            }
        }
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long addCC(XMLEasyCC xMLEasyCC, boolean bl) {
        long l = 0L;
        Hashtable hashtable = this.m_costConfirmations;
        synchronized (hashtable) {
            XMLEasyCC xMLEasyCC2 = (XMLEasyCC)this.m_costConfirmations.get(xMLEasyCC.getConcatenatedPriceCertHashes());
            if (xMLEasyCC2 != null) {
                l = xMLEasyCC2.getTransferredBytes();
            }
            if (bl || xMLEasyCC.getTransferredBytes() >= l) {
                this.m_costConfirmations.put(xMLEasyCC.getConcatenatedPriceCertHashes(), xMLEasyCC);
            }
        }
        return xMLEasyCC.getTransferredBytes() - l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPastMonthlyVolumeBytes(XMLAccountInfo xMLAccountInfo) throws Exception {
        Hashtable hashtable = this.m_pastMonthlyVolumeBytes;
        synchronized (hashtable) {
            Enumeration enumeration = xMLAccountInfo.m_pastMonthlyVolumeBytes.elements();
            while (enumeration.hasMoreElements()) {
                PastMonthsVolumeBytes pastMonthsVolumeBytes = (PastMonthsVolumeBytes)enumeration.nextElement();
                this.m_pastMonthlyVolumeBytes.put(pastMonthsVolumeBytes.getConcatenatedPriceCertHashes(), pastMonthsVolumeBytes);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPastMonthlyVolumeBytes(PastMonthsVolumeBytes pastMonthsVolumeBytes) {
        Timestamp timestamp = new Timestamp(0L);
        Hashtable hashtable = this.m_pastMonthlyVolumeBytes;
        synchronized (hashtable) {
            PastMonthsVolumeBytes pastMonthsVolumeBytes2 = (PastMonthsVolumeBytes)this.m_pastMonthlyVolumeBytes.get(pastMonthsVolumeBytes.getConcatenatedPriceCertHashes());
            if (pastMonthsVolumeBytes2 != null) {
                timestamp = pastMonthsVolumeBytes2.getUpdatedOn();
            }
            if (pastMonthsVolumeBytes.getUpdatedOn() != null && pastMonthsVolumeBytes.getUpdatedOn().getTime() >= timestamp.getTime()) {
                this.m_pastMonthlyVolumeBytes.put(pastMonthsVolumeBytes.getConcatenatedPriceCertHashes(), pastMonthsVolumeBytes);
            }
        }
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals(XML_ELEMENT_NAME)) {
            LogHolder.log(2, LogType.PAY, "invalid XML structure: " + XMLUtil.toString(element));
            throw new Exception("XMLAccountInfo wrong XML structure");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Balance");
        this.m_balance = new XMLBalance(element2);
        this.m_tLastBalanceUpdate = Timestamp.valueOf(XMLUtil.parseAttribute((Node)element, "lastBalanceUpdate", this.m_balance.getTimestamp().toString()));
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEMENT_NAME_COST_CONFIRMATIONS);
        Element element4 = (Element)XMLUtil.getFirstChildByName(element3, "CC");
        XMLEasyCC xMLEasyCC = null;
        while (element4 != null) {
            xMLEasyCC = new XMLEasyCC(element4);
            this.m_costConfirmations.put(xMLEasyCC.getConcatenatedPriceCertHashes(), xMLEasyCC);
            element4 = (Element)XMLUtil.getNextSiblingByName(element4, "CC");
        }
        Element element5 = (Element)XMLUtil.getFirstChildByName(element, "PastMonthsVolumeBytesContainer");
        Element element6 = (Element)XMLUtil.getFirstChildByName(element5, "PastMonthsVolumeBytes");
        PastMonthsVolumeBytes pastMonthsVolumeBytes = null;
        while (element6 != null) {
            pastMonthsVolumeBytes = new PastMonthsVolumeBytes(element6);
            this.m_pastMonthlyVolumeBytes.put(pastMonthsVolumeBytes.getConcatenatedPriceCertHashes(), pastMonthsVolumeBytes);
            element6 = (Element)XMLUtil.getNextSiblingByName(element6, "PastMonthsVolumeBytes");
        }
    }

    public XMLBalance getBalance() {
        return this.m_balance;
    }

    public XMLEasyCC getCC(String string) {
        return (XMLEasyCC)this.m_costConfirmations.get(string);
    }

    public void setAccountCallback(PayAccount payAccount) {
        this.m_callbackAccount = payAccount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkMonthlyBytesUpdatedOn() {
        if (this.m_balance != null && this.m_balance.getVolumeBytesMonthly() > 0L) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Hashtable hashtable = this.m_costConfirmations;
            synchronized (hashtable) {
                Hashtable hashtable2 = this.m_pastMonthlyVolumeBytes;
                synchronized (hashtable2) {
                    if (!(XMLBalance.isSameMonthlyPeriod(this.m_balance.getMonthlyBytesUpdatedOn(), timestamp, this.m_balance.getStartDate(), true) || XMLBalance.isSameMonthlyPeriod(this.m_tLastBalanceUpdate, timestamp, this.m_balance.getStartDate(), true) || XMLBalance.isSameMonthlyPeriod(this.m_balance.getStartDate(), timestamp, this.m_balance.getStartDate(), true))) {
                        Enumeration enumeration = this.getCCs();
                        while (enumeration.hasMoreElements()) {
                            XMLEasyCC xMLEasyCC = (XMLEasyCC)enumeration.nextElement();
                            PastMonthsVolumeBytes pastMonthsVolumeBytes = (PastMonthsVolumeBytes)this.m_pastMonthlyVolumeBytes.get(xMLEasyCC.getPriceCertHashes());
                            if (pastMonthsVolumeBytes != null && XMLBalance.isSameMonthlyPeriod(pastMonthsVolumeBytes.getUpdatedOn(), timestamp, this.m_balance.getStartDate(), true)) continue;
                            pastMonthsVolumeBytes = new PastMonthsVolumeBytes(xMLEasyCC.getConcatenatedPriceCertHashes(), xMLEasyCC.getTransferredBytes(), timestamp);
                            this.m_pastMonthlyVolumeBytes.put(pastMonthsVolumeBytes.getConcatenatedPriceCertHashes(), pastMonthsVolumeBytes);
                        }
                        PayAccount payAccount = this.m_callbackAccount;
                        if (payAccount != null) {
                            payAccount.fireChangeEvent();
                        }
                    }
                }
            }
        }
    }

    public Enumeration getCCs() {
        return ((Hashtable)this.m_costConfirmations.clone()).elements();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getAllCCsTransferredBytes() {
        long l = 0L;
        Hashtable hashtable = this.m_costConfirmations;
        synchronized (hashtable) {
            Hashtable hashtable2 = this.m_pastMonthlyVolumeBytes;
            synchronized (hashtable2) {
                Enumeration enumeration = this.m_costConfirmations.elements();
                while (enumeration.hasMoreElements()) {
                    XMLEasyCC xMLEasyCC = (XMLEasyCC)enumeration.nextElement();
                    PastMonthsVolumeBytes pastMonthsVolumeBytes = (PastMonthsVolumeBytes)this.m_pastMonthlyVolumeBytes.get(xMLEasyCC.getConcatenatedPriceCertHashes());
                    long l2 = pastMonthsVolumeBytes == null ? 0L : pastMonthsVolumeBytes.getBytes();
                    l += xMLEasyCC.getTransferredBytes() - l2;
                }
            }
        }
        return l;
    }

    public void setBalance(XMLBalance xMLBalance) {
        this.m_balance = xMLBalance;
        if (this.m_balance != null) {
            this.m_tLastBalanceUpdate = new Timestamp(System.currentTimeMillis());
        }
    }

    public Timestamp getLastBalanceUpdateLocalTime() {
        return this.m_tLastBalanceUpdate;
    }

    public XMLAccountInfo(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
        this.m_tLastBalanceUpdate = new Timestamp(System.currentTimeMillis());
    }

    public static class PastMonthsVolumeBytes
    implements IXMLEncodable {
        public static final String XML_ELEMENT_NAME_PMB = "PastMonthsVolumeBytes";
        private long m_lBytes = 0L;
        private String m_strPriceCertHashes = null;
        private Timestamp m_tUpdatedOn = null;

        public PastMonthsVolumeBytes(String string, long l, Timestamp timestamp) {
            if (l < 0L) {
                throw new IllegalArgumentException("Past Bytes may not be < 0!");
            }
            if (string == null) {
                throw new IllegalArgumentException("Price cert hashed may not be null!");
            }
            this.m_strPriceCertHashes = string;
            this.m_lBytes = l;
            this.m_tUpdatedOn = timestamp;
        }

        public PastMonthsVolumeBytes(Element element) throws XMLParseException {
            XMLUtil.assertNodeName(element, XML_ELEMENT_NAME_PMB);
            this.m_strPriceCertHashes = XMLUtil.parseAttribute((Node)element, "priceCertHashes", null);
            if (this.m_strPriceCertHashes == null) {
                throw new XMLParseException("priceCertHashes", "##__null__##");
            }
            this.m_tUpdatedOn = new Timestamp(XMLUtil.parseAttribute((Node)element, "updatedOn", 0L));
            this.m_lBytes = XMLUtil.parseValue((Node)element, 0L);
        }

        public long getBytes() {
            return this.m_lBytes;
        }

        public void setUpdatedOn(Timestamp timestamp) {
            this.m_tUpdatedOn = timestamp;
        }

        public String getConcatenatedPriceCertHashes() {
            return this.m_strPriceCertHashes;
        }

        public Timestamp getUpdatedOn() {
            return this.m_tUpdatedOn;
        }

        public Element toXmlElement(Document document) {
            Element element = document.createElement(XML_ELEMENT_NAME_PMB);
            XMLUtil.setAttribute(element, "priceCertHashes", this.m_strPriceCertHashes);
            XMLUtil.setAttribute(element, "updatedOn", this.m_tUpdatedOn.getTime());
            XMLUtil.setValue((Node)element, this.m_lBytes);
            return element;
        }
    }
}

