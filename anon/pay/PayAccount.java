/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.client.PacketCounter;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyRSAPrivateKey;
import anon.crypto.XMLEncryption;
import anon.pay.BIConnection;
import anon.pay.IAccountListener;
import anon.pay.IMessageListener;
import anon.pay.PayAccountsFile;
import anon.pay.PayMessage;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.Transaction;
import anon.pay.xml.XMLAccountCertificate;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLBalance;
import anon.pay.xml.XMLEasyCC;
import anon.pay.xml.XMLGenericStrings;
import anon.pay.xml.XMLGenericText;
import anon.pay.xml.XMLTransCert;
import anon.util.Base64;
import anon.util.IMiscPasswordReader;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import anon.util.ZLibTools;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PayAccount
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "Account";
    private static final String XML_ATTR_ACTIVE = "active";
    private static final String XML_BACKUP_DONE = "backupDone";
    public static final long ACCOUNT_MIN_UPDATE_INTERVAL_MS = 900000L;
    public static final long ACCOUNT_MAX_UPDATE_INTERVAL_MS = 3600000L;
    private final Object SYNC_BYTES = new Object();
    private static final long NEW_ACCOUNT_EXPIRATION_TIME = 1209600000L;
    private static final String VERSION = "1.1";
    private static final long TIMEOUT_BLOCKED = 900000L;
    private Vector m_transCerts;
    private XMLAccountCertificate m_accountCertificate;
    private XMLAccountInfo m_accountInfo;
    private XMLGenericText m_terms;
    private IMyPrivateKey m_privateKey;
    private Document m_encryptedPrivateKey;
    private long m_currentBytes;
    private boolean m_bLocked = false;
    private Vector m_accountListeners = new Vector();
    private Vector m_messageListeners = new Vector();
    private long m_lBackupDone = 0L;
    private long m_lastAccountInfoUpdate = 0L;
    private boolean m_bAccountInfoUpdateRunning = false;
    private Object SYNC_ACCOUNT_INFO_UPDATE = new Object();
    private long m_tBlocked = 0L;
    private boolean m_bStatusUnknown = false;
    private Calendar m_termsDate;
    public static final int MAX_KBYTES_COUNTING_AS_EMPTY = 5000;
    private long m_mySpent;
    private PaymentInstanceDBEntry m_theBI;
    private String m_strBiID;

    public boolean isWaitingForTransaction() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return !this.isBlocked() && !this.isCharged(timestamp) && !this.isUsed() && !this.hasExpired(timestamp) && !this.isTransactionExpired();
    }

    public boolean isTransactionExpired() {
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo == null) {
            return this.hasExpired();
        }
        if (this.getCurrentCredit() > 0L || this.getCurrentSpent() > 0L || xMLAccountInfo.getAllCCsTransferredBytes() > 0L) {
            return false;
        }
        if (this.getTransaction() != null) {
            if (this.getTransaction().isUsed()) {
                return false;
            }
            return this.getTransaction().hasExpired();
        }
        return true;
    }

    public PayAccount(Element element, IMiscPasswordReader iMiscPasswordReader) throws Exception {
        this.setValues(element, iMiscPasswordReader);
    }

    public PayAccount(XMLAccountCertificate xMLAccountCertificate, IMyPrivateKey iMyPrivateKey, PaymentInstanceDBEntry paymentInstanceDBEntry, XMLGenericText xMLGenericText, boolean bl) {
        this.m_accountCertificate = xMLAccountCertificate;
        this.m_privateKey = iMyPrivateKey;
        this.m_transCerts = new Vector();
        this.m_theBI = paymentInstanceDBEntry;
        this.m_strBiID = paymentInstanceDBEntry.getId();
        this.m_bLocked = bl;
        this.setTerms(xMLGenericText);
    }

    private void setValues(Element element, IMiscPasswordReader iMiscPasswordReader) throws Exception {
        Object object;
        Element element2;
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        boolean bl = XMLUtil.parseAttribute((Node)element, XML_ATTR_ACTIVE, true);
        boolean bl2 = XMLUtil.parseAttribute((Node)element, XML_BACKUP_DONE, false);
        this.m_lBackupDone = bl2 ? System.currentTimeMillis() : XMLUtil.parseAttribute((Node)element, XML_BACKUP_DONE, 0L);
        this.m_transCerts = new Vector();
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "TransferCertificates");
        Element element4 = (Element)XMLUtil.getFirstChildByName(element3, "TransferCertificate");
        while (element4 != null) {
            this.m_transCerts.addElement(new XMLTransCert(element4));
            element4 = (Element)XMLUtil.getNextSiblingByName(element4, "TransferCertificate");
        }
        Element element5 = (Element)XMLUtil.getFirstChildByName(element, "AccountCertificate");
        this.m_accountCertificate = new XMLAccountCertificate(element5);
        Element element6 = (Element)XMLUtil.getFirstChildByName(element, "AccountInfo");
        if (element6 != null) {
            this.m_accountInfo = new XMLAccountInfo(element6);
            this.m_accountInfo.setAccountCallback(this);
            if (this.m_accountInfo.getBalance() != null) {
                this.setBlocked(this.m_accountInfo.getBalance().isBlocked(), false);
            }
        }
        if ((element2 = (Element)XMLUtil.getFirstChildByName(element, "GenericText")) != null && XMLUtil.getStorageMode() == 0) {
            object = new XMLGenericText(element2);
            try {
                byte[] arrby = Base64.decode(((XMLGenericText)object).getText());
                object = new XMLGenericText(new String(ZLibTools.decompress(arrby)));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.setTerms((XMLGenericText)object);
        }
        object = (Element)XMLUtil.getFirstChildByName(element5, "BiID");
        this.m_strBiID = XMLUtil.parseValue((Node)object, "-1");
        this.m_theBI = null;
        this.decryptPrivateKey(element, iMiscPasswordReader, !bl);
    }

    public Element toXmlElement(Document document) {
        return this.toXmlElement(document, null, false);
    }

    public Element toXmlElement(Document document, String string, boolean bl) {
        try {
            Object object;
            Object object2;
            if (string != null && string.trim().equals("")) {
                return this.toXmlElement(document, null, bl);
            }
            Element element = document.createElement(XML_ELEMENT_NAME);
            element.setAttribute("version", VERSION);
            Element element2 = this.m_accountCertificate.toXmlElement(document);
            element.appendChild(element2);
            XMLUtil.setAttribute(element, XML_BACKUP_DONE, this.m_lBackupDone);
            if (this.m_encryptedPrivateKey != null) {
                if (!bl) {
                    XMLUtil.setAttribute(element, XML_ATTR_ACTIVE, false);
                }
                element2 = (Element)XMLUtil.importNode(document, this.m_encryptedPrivateKey.getDocumentElement(), true);
                element.appendChild(element2);
            } else {
                XMLUtil.setAttribute(element, XML_ATTR_ACTIVE, true);
                element2 = this.m_privateKey.toXmlElement(document);
                element.appendChild(element2);
                if (string != null) {
                    try {
                        XMLEncryption.encryptElement(element2, string);
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.PAY, "Could not encrypt account key: " + exception);
                    }
                }
            }
            Element element3 = document.createElement("TransferCertificates");
            element.appendChild(element3);
            if (this.m_transCerts != null) {
                object2 = this.m_transCerts.elements();
                while (object2.hasMoreElements()) {
                    object = (XMLTransCert)object2.nextElement();
                    element3.appendChild(((XMLTransCert)object).toXmlElement(document));
                }
            }
            if (this.m_accountInfo != null) {
                element2 = this.m_accountInfo.toXmlElement(document);
                element.appendChild(element2);
            }
            if (this.m_terms != null) {
                object2 = this.m_terms.getText();
                object = Base64.encode(ZLibTools.compress(((String)object2).getBytes()), true);
                XMLGenericText xMLGenericText = new XMLGenericText((String)object);
                element2 = xMLGenericText.toXmlElement(document);
                element.appendChild(element2);
            }
            return element;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "Exception while creating PayAccount XML: " + exception);
            return null;
        }
    }

    public void addTransCert(XMLTransCert xMLTransCert) throws Exception {
        this.m_transCerts.addElement(xMLTransCert);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setAccountInfo(XMLAccountInfo xMLAccountInfo) throws Exception {
        boolean bl = false;
        if (xMLAccountInfo != null) {
            if (xMLAccountInfo.getBalance() != null && xMLAccountInfo.getBalance().getAccountNumber() != this.getAccountNumber()) {
                throw new IllegalArgumentException("Wrong account number! Expected: " + this.getAccountNumber() + " Value: " + xMLAccountInfo.getBalance().getAccountNumber());
            }
            this.setStatusUnknown(false);
        }
        if (this.m_accountInfo == null) {
            this.m_accountInfo = xMLAccountInfo;
            bl = true;
        } else {
            XMLAccountInfo xMLAccountInfo2 = this.m_accountInfo;
            synchronized (xMLAccountInfo2) {
                String string;
                XMLBalance xMLBalance = xMLAccountInfo.getBalance();
                XMLBalance xMLBalance2 = this.m_accountInfo.getBalance();
                PayMessage payMessage = xMLBalance2.getMessage();
                PayMessage payMessage2 = xMLBalance.getMessage();
                if (xMLBalance.getTimestamp().after(xMLBalance2.getTimestamp())) {
                    this.m_accountInfo.setBalance(xMLBalance);
                    this.setBlocked(this.m_accountInfo.getBalance().isBlocked(), false);
                    bl = true;
                    if (payMessage2 != null && !payMessage2.getShortMessage().equals("")) {
                        if (payMessage == null) {
                            this.fireMessageReceived(payMessage2);
                        } else if (!payMessage2.equals(payMessage)) {
                            this.fireMessageRemoved(payMessage);
                            this.fireMessageReceived(payMessage2);
                        }
                    } else if (payMessage != null && !payMessage.getShortMessage().equals("")) {
                        this.fireMessageRemoved(payMessage);
                    }
                }
                xMLBalance2.setMessage(payMessage2);
                Enumeration[] arrenumeration = new Enumeration[]{this.m_accountInfo.getCCs(), xMLAccountInfo.getCCs()};
                Hashtable<String, String> hashtable = new Hashtable<String, String>();
                for (int i = 0; i < arrenumeration.length; ++i) {
                    while (arrenumeration[i].hasMoreElements()) {
                        string = ((XMLEasyCC)arrenumeration[i].nextElement()).getConcatenatedPriceCertHashes();
                        hashtable.put(string, string);
                    }
                }
                Enumeration enumeration = hashtable.keys();
                while (enumeration.hasMoreElements()) {
                    string = (String)enumeration.nextElement();
                    XMLEasyCC xMLEasyCC = this.m_accountInfo.getCC(string);
                    XMLEasyCC xMLEasyCC2 = xMLAccountInfo.getCC(string);
                    if (xMLEasyCC == null && xMLEasyCC2 == null) {
                        throw new NullPointerException("no CC available for " + string + " This must NEVER happen!");
                    }
                    if (xMLEasyCC2 == null || xMLEasyCC != null && xMLEasyCC2.getTransferredBytes() <= xMLEasyCC.getTransferredBytes()) continue;
                    if (xMLEasyCC2.verify(this.m_accountCertificate.getPublicKey())) {
                        this.addCostConfirmation(xMLEasyCC2, false, false);
                        bl = true;
                        continue;
                    }
                    throw new Exception("The BI is trying to betray you with faked CostConfirmations");
                }
                this.m_accountInfo.addPastMonthlyVolumeBytes(xMLAccountInfo);
            }
        }
        if (bl) {
            this.fireChangeEvent();
        }
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof PayAccount)) {
            return false;
        }
        PayAccount payAccount = (PayAccount)object;
        return payAccount.getAccountNumber() == this.getAccountNumber() && (payAccount.m_strBiID == this.m_strBiID || payAccount.m_strBiID != null && this.m_strBiID != null && payAccount.m_strBiID.equals(this.m_strBiID));
    }

    public int hashCode() {
        return (this.getAccountNumber() + this.m_strBiID).hashCode();
    }

    public long getAccountNumber() {
        return this.m_accountCertificate.getAccountNumber();
    }

    public boolean hasExpired() {
        return this.hasExpired(new Timestamp(System.currentTimeMillis()));
    }

    public boolean hasExpired(Timestamp timestamp) {
        XMLBalance xMLBalance = this.getBalance();
        Calendar calendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        if (xMLBalance != null) {
            if (xMLBalance.getStartDate() != null) {
                calendar.setTime(xMLBalance.getStartDate());
                gregorianCalendar.setTime(timestamp);
                calendar = XMLBalance.calculateEndDate(calendar, -2, 5);
                calendar.add(13, 1);
            }
            if ((this.getCurrentCredit() > 0L || this.getCurrentSpent() > 0L) && xMLBalance.getFlatEnddate() != null && xMLBalance.getFlatEnddate().before(timestamp) || this.getVolumeBytesMonthly() > 0L && calendar.after(gregorianCalendar)) {
                return true;
            }
        } else if (this.getCreationTime().before(new Date(System.currentTimeMillis() - 1209600000L))) {
            return true;
        }
        return false;
    }

    public boolean isCharged() {
        return this.isCharged(new Timestamp(System.currentTimeMillis()));
    }

    public boolean isCharged(Timestamp timestamp) {
        XMLBalance xMLBalance = this.getBalance();
        if (xMLBalance == null) {
            return false;
        }
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        if (xMLBalance.getFlatEnddate().before(timestamp)) {
            return false;
        }
        if (xMLBalance.getVolumeBytesMonthly() == 0L || xMLBalance.getStartDate() == null || XMLBalance.isSameMonthlyPeriod(timestamp, timestamp2, xMLBalance.getStartDate(), false)) {
            return this.getCurrentCredit() > 0L;
        }
        return !XMLBalance.isLastMonthOfRate(xMLBalance.getFlatEnddate(), timestamp, xMLBalance.getStartDate()) || xMLBalance.getLastMonthRemainingTraffic() > 0L;
    }

    public boolean isSamePaymentInstance(String string) {
        if (string == null || this.m_strBiID == null) {
            return false;
        }
        return string.equals(this.m_strBiID);
    }

    public boolean isBackupDone() {
        return this.m_lBackupDone > 0L;
    }

    public long getBackupTime() {
        return this.m_lBackupDone;
    }

    public void setBackupDone(long l) {
        this.m_lBackupDone = l;
    }

    protected void setStatusUnknown(boolean bl) {
        this.m_bStatusUnknown = bl;
    }

    public boolean isStatusUnknown() {
        return this.m_bStatusUnknown;
    }

    public boolean hasAccountInfo() {
        return this.m_accountInfo != null;
    }

    public XMLAccountCertificate getAccountCertificate() {
        return this.m_accountCertificate;
    }

    public Timestamp getCreationTime() {
        return this.m_accountCertificate.getCreationTime();
    }

    public Timestamp getBalanceValidTime() {
        if (this.m_accountInfo != null) {
            return this.m_accountInfo.getBalance().getValidTime();
        }
        return this.m_accountCertificate.getCreationTime();
    }

    public IMyPrivateKey getPrivateKey() {
        return this.m_privateKey;
    }

    public IMyPublicKey getPublicKey() {
        return this.m_accountCertificate.getPublicKey();
    }

    public long getDeposit() {
        if (this.m_accountInfo != null) {
            return this.m_accountInfo.getBalance().getDeposit();
        }
        return 0L;
    }

    public Transaction getTransaction() {
        if (this.m_accountInfo != null) {
            return this.m_accountInfo.getBalance().getTransaction();
        }
        return null;
    }

    public long getCurrentCreditCalculated() {
        if (this.m_accountInfo == null) {
            return Long.MIN_VALUE;
        }
        long l = this.getCurrentCreditCalculatedAlsoNegative();
        if (l < 0L) {
            l = 0L;
        }
        return l;
    }

    public boolean isUsed() {
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo == null) {
            return false;
        }
        XMLBalance xMLBalance = xMLAccountInfo.getBalance();
        if (xMLBalance == null) {
            return false;
        }
        if (xMLBalance.getTransaction() != null) {
            return xMLBalance.getTransaction().isUsed();
        }
        return xMLBalance.getSpent() > 0L || xMLBalance.getVolumeBytesLeft() > 0L || xMLAccountInfo.getAllCCsTransferredBytes() > 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getCurrentCreditCalculatedAlsoNegative() {
        long l;
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo == null) {
            return Long.MIN_VALUE;
        }
        XMLBalance xMLBalance = xMLAccountInfo.getBalance();
        if (xMLBalance == null) {
            return 0L;
        }
        XMLAccountInfo xMLAccountInfo2 = xMLAccountInfo;
        synchronized (xMLAccountInfo2) {
            if (xMLBalance.getVolumeBytesMonthly() <= 0L) {
                l = xMLBalance.getSpent() + xMLBalance.getVolumeBytesLeft() - xMLAccountInfo.getAllCCsTransferredBytes();
            } else {
                long l2 = xMLBalance.getVolumeBytesMonthly();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                if (xMLBalance.getFlatEnddate().getTime() < timestamp.getTime()) {
                    return 0L;
                }
                if (XMLBalance.isLastMonthOfRate(xMLBalance.getFlatEnddate(), timestamp, xMLBalance.getStartDate())) {
                    l2 = xMLBalance.getLastMonthRemainingTraffic();
                } else if (xMLBalance.getOverusageBytes() > 0L && XMLBalance.isSameMonthlyPeriod(xMLBalance.getOverusageDate(), new Timestamp(System.currentTimeMillis()), xMLBalance.getStartDate(), true)) {
                    l2 += xMLBalance.getOverusageBytes();
                }
                l = l2 - xMLAccountInfo.getAllCCsTransferredBytes();
                long l3 = xMLAccountInfo.getBalance().getSpent() + xMLAccountInfo.getBalance().getVolumeBytesLeft() - l;
                if (l3 < 0L) {
                    l += l3;
                }
            }
        }
        return l;
    }

    public long getCurrentCreditFromBalance() {
        return this.getBalance().getVolumeBytesLeft();
    }

    public String toString() {
        return "" + this.getAccountNumber();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unlock() {
        Vector vector = this.m_accountListeners;
        synchronized (vector) {
            if (this.m_bLocked) {
                this.fireChangeEvent();
                this.m_bLocked = false;
            }
        }
    }

    public boolean isLocked() {
        return this.m_bLocked;
    }

    public boolean isBlocked() {
        return System.currentTimeMillis() - this.m_tBlocked < 900000L;
    }

    public void setBlocked(boolean bl) {
        this.setBlocked(bl, true);
    }

    private void setBlocked(boolean bl, boolean bl2) {
        boolean bl3 = false;
        if (this.isBlocked() != bl) {
            bl3 = true;
        }
        this.m_tBlocked = bl ? System.currentTimeMillis() : 0L;
        if (bl3 && bl2) {
            this.fireChangeEvent();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getCurrentCredit() {
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            XMLAccountInfo xMLAccountInfo2 = xMLAccountInfo;
            synchronized (xMLAccountInfo2) {
                long l = this.getCurrentCreditCalculated();
                if (l < 0L || (xMLAccountInfo.getBalance().getVolumeBytesMonthly() == 0L || xMLAccountInfo.getBalance().getVolumeBytesMonthly() > 0L && XMLBalance.isSameMonthlyPeriod(xMLAccountInfo.getBalance().getMonthlyBytesUpdatedOn(), timestamp, xMLAccountInfo.getBalance().getStartDate(), true)) && l > this.getBalance().getVolumeBytesLeft()) {
                    return this.getBalance().getVolumeBytesLeft();
                }
                return l;
            }
        }
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getCurrentSpent() {
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo != null) {
            XMLAccountInfo xMLAccountInfo2 = xMLAccountInfo;
            synchronized (xMLAccountInfo2) {
                return xMLAccountInfo.getBalance().getSpent() + xMLAccountInfo.getBalance().getVolumeBytesLeft() - this.getCurrentCredit();
            }
        }
        return 0L;
    }

    public XMLAccountInfo getAccountInfo() {
        return this.m_accountInfo;
    }

    public XMLGenericText getTerms() {
        return this.m_terms;
    }

    public void setTerms(XMLGenericText xMLGenericText) {
        this.m_termsDate = null;
        if (xMLGenericText == null) {
            this.m_terms = null;
        }
        String string = null;
        if (xMLGenericText != null) {
            string = xMLGenericText.getText();
        }
        if (string == null || string.trim().equals("")) {
            this.m_terms = null;
        } else {
            this.m_terms = xMLGenericText;
            int n = string.indexOf("<title>");
            int n2 = string.indexOf("</title>");
            if (n >= 0 && n2 >= 0) {
                try {
                    String string2 = string.substring(n + "<title>".length(), n2);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(string2));
                    this.m_termsDate = calendar;
                }
                catch (ParseException parseException) {
                    LogHolder.log(4, LogType.PAY, parseException);
                    this.m_terms = null;
                }
            } else {
                LogHolder.log(4, LogType.PAY, "No valid title tag was found!");
                this.m_terms = null;
            }
        }
    }

    public Calendar getTermsDate() {
        return this.m_termsDate;
    }

    public Vector getTransCerts() {
        return this.m_transCerts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long updateCurrentBytes(PacketCounter packetCounter) throws Exception {
        long l;
        if (PayAccountsFile.getInstance().getActiveAccount() != this) {
            throw new Exception("Error: Inactive account called to count used bytes!");
        }
        Object object = this.SYNC_BYTES;
        synchronized (object) {
            l = packetCounter.getAndResetBytesForPayment();
            if (l > 0L) {
                this.m_currentBytes += l;
            }
        }
        if (l > 0L) {
            this.fireChangeEvent();
        } else if (l < 0L) {
            throw new Exception("Negative payment bytes added! Bytes: " + l);
        }
        return l;
    }

    public void resetCurrentBytes() {
        this.m_currentBytes = 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void updateCurrentBytes(long l) {
        Object object = this.SYNC_BYTES;
        synchronized (object) {
            this.m_currentBytes += l;
        }
    }

    public long getCurrentBytes() {
        return this.m_currentBytes;
    }

    public long addCostConfirmation(XMLEasyCC xMLEasyCC, boolean bl) throws Exception {
        return this.addCostConfirmation(xMLEasyCC, bl, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long addCostConfirmation(XMLEasyCC xMLEasyCC, boolean bl, boolean bl2) throws Exception {
        long l;
        Object object = this.SYNC_BYTES;
        synchronized (object) {
            if (this.m_accountInfo == null) {
                this.m_accountInfo = new XMLAccountInfo();
                this.m_accountInfo.setAccountCallback(this);
            }
            if ((l = this.m_accountInfo.addCC(xMLEasyCC, bl)) > 0L) {
                this.m_mySpent += l;
            }
        }
        if (bl2) {
            this.fireChangeEvent();
        }
        return l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addAccountListener(IAccountListener iAccountListener) {
        Vector vector = this.m_accountListeners;
        synchronized (vector) {
            if (iAccountListener != null) {
                this.m_accountListeners.addElement(iAccountListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addMessageListener(IMessageListener iMessageListener) {
        Vector vector = this.m_messageListeners;
        synchronized (vector) {
            if (iMessageListener != null) {
                this.m_messageListeners.addElement(iMessageListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeMessageListener(IMessageListener iMessageListener) {
        Vector vector = this.m_messageListeners;
        synchronized (vector) {
            this.m_messageListeners.removeElement(iMessageListener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireChangeEvent() {
        Vector vector = this.m_accountListeners;
        synchronized (vector) {
            Enumeration enumeration = this.m_accountListeners.elements();
            while (enumeration.hasMoreElements()) {
                ((IAccountListener)enumeration.nextElement()).accountChanged(this);
            }
        }
    }

    private void fireMessageReceived(PayMessage payMessage) {
        Enumeration enumeration = ((Vector)this.m_messageListeners.clone()).elements();
        while (enumeration.hasMoreElements()) {
            ((IMessageListener)enumeration.nextElement()).messageReceived(payMessage);
        }
    }

    private void fireMessageRemoved(PayMessage payMessage) {
        Enumeration enumeration = ((Vector)this.m_messageListeners.clone()).elements();
        while (enumeration.hasMoreElements()) {
            ((IMessageListener)enumeration.nextElement()).messageRemoved(payMessage);
        }
    }

    public String getAffiliate() {
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo == null) {
            return null;
        }
        return xMLAccountInfo.getBalance().getAffiliate();
    }

    public XMLBalance getBalance() {
        XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
        if (xMLAccountInfo == null) {
            return null;
        }
        return xMLAccountInfo.getBalance();
    }

    public XMLAccountInfo fetchAccountInfo(boolean bl) throws SecurityException, Exception {
        return this.fetchAccountInfo(bl, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XMLAccountInfo fetchAccountInfo(boolean bl, int n) throws SecurityException, Exception {
        if (!(bl || PayAccountsFile.getInstance().isBalanceAutoUpdateEnabled() && !this.m_bAccountInfoUpdateRunning)) {
            return null;
        }
        if (this.getPrivateKey() == null) {
            throw new SecurityException("Account is encrypted and not usable!");
        }
        XMLAccountInfo xMLAccountInfo = null;
        this.m_theBI = this.getBI();
        if (this.m_theBI == null) {
            return null;
        }
        BIConnection bIConnection = null;
        Object object = this.SYNC_ACCOUNT_INFO_UPDATE;
        synchronized (object) {
            this.m_bAccountInfoUpdateRunning = true;
            try {
                bIConnection = new BIConnection(this.m_theBI);
                if (n > 0) {
                    bIConnection.connect(n);
                } else {
                    bIConnection.connect();
                }
                bIConnection.authenticate(this);
                xMLAccountInfo = bIConnection.getAccountInfo(this);
                Object var7_6 = null;
                if (bIConnection != null) {
                    bIConnection.disconnect();
                }
                this.m_bAccountInfoUpdateRunning = false;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                if (bIConnection != null) {
                    bIConnection.disconnect();
                }
                this.m_bAccountInfoUpdateRunning = false;
                throw throwable;
            }
            if (xMLAccountInfo != null) {
                this.m_lastAccountInfoUpdate = System.currentTimeMillis();
                this.setAccountInfo(xMLAccountInfo);
            }
        }
        if (xMLAccountInfo != null && xMLAccountInfo.getBalance().getAffiliate() != null) {
            this.fireChangeEvent();
        }
        return xMLAccountInfo;
    }

    public static boolean canDoMonthlyOverusage(PayAccount payAccount, Timestamp timestamp) {
        XMLBalance xMLBalance;
        return payAccount != null && (xMLBalance = payAccount.getBalance()) != null && xMLBalance.canDoMonthlyOverusage(timestamp);
    }

    public long getVolumeBytesMonthly() {
        XMLBalance xMLBalance = this.getBalance();
        if (xMLBalance != null) {
            return xMLBalance.getVolumeBytesMonthly();
        }
        return 0L;
    }

    public double getCurrentOverusageFactor(Timestamp timestamp) {
        XMLBalance xMLBalance = this.getBalance();
        if (xMLBalance != null && xMLBalance.isCurrentlyInOverusage(timestamp)) {
            return xMLBalance.getOverusageFactor();
        }
        return 0.0;
    }

    public boolean canDoMonthlyOverusage(Timestamp timestamp) {
        return PayAccount.canDoMonthlyOverusage(this, timestamp);
    }

    public boolean isAccountInfoUpdated() {
        return this.m_lastAccountInfoUpdate > 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean shouldUpdateAccountInfo() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (this.hasExpired(timestamp) || this.getCurrentSpent() > 0L && this.getCurrentCredit() == 0L && !this.canDoMonthlyOverusage(timestamp)) {
            return false;
        }
        if (this.getCurrentSpent() < 0L && !this.m_bAccountInfoUpdateRunning) {
            return true;
        }
        if (!PayAccountsFile.getInstance().isBalanceAutoUpdateEnabled() || this.m_bAccountInfoUpdateRunning) {
            return false;
        }
        long l = PayAccountsFile.getInstance().getActiveAccount() == this ? 900000L : 3600000L;
        if (this.m_lastAccountInfoUpdate == 0L) {
            if (this.m_accountInfo == null) {
                return false;
            }
            XMLAccountInfo xMLAccountInfo = this.m_accountInfo;
            synchronized (xMLAccountInfo) {
                if (this.m_accountInfo.getBalance() == null || this.m_accountInfo.getBalance().getTimestamp() == null || this.m_accountInfo.getBalance().getTimestamp().getTime() > System.currentTimeMillis() || this.m_accountInfo.getBalance().getTimestamp().getTime() < System.currentTimeMillis() - l) {
                    return true;
                }
            }
        } else if (this.m_lastAccountInfoUpdate < System.currentTimeMillis() - l) {
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XMLTransCert charge(XMLGenericStrings xMLGenericStrings) throws SecurityException, Exception {
        if (this.getPrivateKey() == null) {
            throw new SecurityException("Account is encrypted and not usable!");
        }
        BIConnection bIConnection = null;
        XMLTransCert xMLTransCert = null;
        bIConnection = new BIConnection(this.m_theBI);
        try {
            bIConnection.connect();
            bIConnection.authenticate(this);
            xMLTransCert = bIConnection.charge(xMLGenericStrings);
            Object var5_4 = null;
            bIConnection.disconnect();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            bIConnection.disconnect();
            throw throwable;
        }
        this.m_transCerts.addElement(xMLTransCert);
        return xMLTransCert;
    }

    public void updated() {
        this.fireChangeEvent();
    }

    public String getPIID() {
        return this.m_strBiID;
    }

    public PaymentInstanceDBEntry getBI() {
        if (this.m_theBI == null) {
            this.m_theBI = PayAccountsFile.getInstance().getBI(this.m_strBiID);
        }
        return this.m_theBI;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XMLAccountInfo requestMonthlyOverusage() throws Exception {
        XMLAccountInfo xMLAccountInfo;
        PaymentInstanceDBEntry paymentInstanceDBEntry = this.getBI();
        if (paymentInstanceDBEntry == null) {
            return null;
        }
        BIConnection bIConnection = new BIConnection(paymentInstanceDBEntry);
        Object object = this.SYNC_ACCOUNT_INFO_UPDATE;
        synchronized (object) {
            this.m_bAccountInfoUpdateRunning = true;
            try {
                bIConnection.connect();
                bIConnection.authenticate(this);
                xMLAccountInfo = bIConnection.requestMonthlyOverusage(this.getBalance().getOverusageFactorGeneral(), this);
                Object var6_5 = null;
                bIConnection.disconnect();
                this.m_bAccountInfoUpdateRunning = false;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                bIConnection.disconnect();
                this.m_bAccountInfoUpdateRunning = false;
                throw throwable;
            }
            if (xMLAccountInfo != null) {
                this.m_lastAccountInfoUpdate = System.currentTimeMillis();
                this.setAccountInfo(xMLAccountInfo);
            }
        }
        if (xMLAccountInfo != null) {
            this.fireChangeEvent();
        }
        return xMLAccountInfo;
    }

    public static String checkCouponCode(String string) {
        if (string == null) {
            return null;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        string = "";
        while (stringTokenizer.hasMoreTokens()) {
            string = string + stringTokenizer.nextToken();
        }
        if (string.length() != 16) {
            return null;
        }
        string = string.toUpperCase();
        for (int i = 0; i < string.length(); ++i) {
            if (i == 0 && (PayAccountsFile.isNewUserLetter(string.charAt(i)) || PayAccountsFile.isPromotionLetter(string.charAt(i))) || string.charAt(i) >= '0' && string.charAt(i) <= '9' || string.charAt(i) >= 'A' && string.charAt(i) <= 'F') continue;
            return null;
        }
        return string;
    }

    public void decryptPrivateKey(IMiscPasswordReader iMiscPasswordReader) throws Exception {
        if (this.m_encryptedPrivateKey != null) {
            this.decryptPrivateKey(this.m_encryptedPrivateKey, iMiscPasswordReader, false);
        }
    }

    private void decryptPrivateKey(Node node, final IMiscPasswordReader iMiscPasswordReader, boolean bl) throws Exception {
        Object object;
        if (this.m_privateKey != null || node == null) {
            return;
        }
        Element element = (Element)XMLUtil.getFirstChildByName(node, "EncryptedData");
        if (element != null) {
            try {
                if (bl) {
                    this.deactivate(element);
                    return;
                }
                object = iMiscPasswordReader != null ? new IMiscPasswordReader(){

                    public String readPassword(Object object) {
                        return iMiscPasswordReader.readPassword(new String("" + PayAccount.this.m_accountCertificate.getAccountNumber()));
                    }
                } : iMiscPasswordReader;
                LogHolder.log(7, LogType.PAY, "Decrypting account " + this.m_accountCertificate.getAccountNumber());
                XMLEncryption.decryptElement(element, (IMiscPasswordReader)object);
            }
            catch (Exception exception) {
                this.deactivate(element);
                return;
            }
        }
        object = (Element)XMLUtil.getFirstChildByName(node, "RSAPrivateKey");
        Element element2 = (Element)XMLUtil.getFirstChildByName(node, "DSAPrivateKey");
        if (object != null) {
            if (bl) {
                this.deactivate((Element)object);
                return;
            }
            this.m_privateKey = new MyRSAPrivateKey((Element)object);
        } else if (element2 != null) {
            if (bl) {
                this.deactivate(element2);
                return;
            }
            this.m_privateKey = new MyDSAPrivateKey(element2);
        } else {
            throw new XMLParseException("No RSA and no DSA private key found");
        }
        this.m_encryptedPrivateKey = null;
    }

    private void deactivate(Element element) throws Exception {
        this.m_privateKey = null;
        this.m_encryptedPrivateKey = XMLUtil.createDocument();
        this.m_encryptedPrivateKey.appendChild(XMLUtil.importNode(this.m_encryptedPrivateKey, element, true));
    }
}

