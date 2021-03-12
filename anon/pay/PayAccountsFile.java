/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.DSAKeyPair;
import anon.crypto.DSAKeyPool;
import anon.error.AccountEmptyException;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.pay.BIConnection;
import anon.pay.IAccountListener;
import anon.pay.IBIConnectionListener;
import anon.pay.IMessageListener;
import anon.pay.IPaymentListener;
import anon.pay.PayAccount;
import anon.pay.PayMessage;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLAccountCertificate;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLBalance;
import anon.pay.xml.XMLErrorMessage;
import anon.pay.xml.XMLGenericText;
import anon.pay.xml.XMLJapPublicKey;
import anon.pay.xml.XMLPassivePayment;
import anon.util.IMiscPasswordReader;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PayAccountsFile
extends Observable
implements IXMLEncodable,
IBIConnectionListener,
IMessageListener {
    public static final String XML_ELEMENT_NAME = "PayAccounts";
    public static final String METHOD_COUPON = "Coupon";
    public static final String XML_ELEMENT_AFFILIATE = "Affiliate";
    public static final String XML_ELEMENT_AFFILIATES = "Affiliates";
    public static final Integer CHANGED_AUTO_UPDATE = new Integer(0);
    private static final String XML_ATTR_IGNORE_AI_ERRORS = "ignoreAIErrorMessages";
    private static final String XML_ATTR_ENABLE_BALANCE_AUTO_UPDATE = "autoUpdateBalance";
    private static final String XML_ATTR_IS_NEW_USER = "isNewUser";
    public static final String MSG_DO_PREMIUM_PAYMENT = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".doPremiumPayment";
    public static final String MSG_DO_PREMIUM_PAYMENT_ALTERNATIVE = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".doPremiumPaymentAlternative";
    public static final String MSG_GET_FREE_CODE = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".getFreeCode";
    public static final String MSG_PAY_CREATE_ACCOUNT = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".payCreateAccount";
    public static final String MSG_NO_PAYMENT_INSTANCE = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".noPaymentInstance";
    public static final String MSG_CREATING_KEY_PAIR = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".creatingKeyPair";
    public static final String MSG_CREATING_KEY_PAIR_ERROR = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".creatingKeyPairError";
    public static final String MSG_UPDATING_ACCOUNT_DATA = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".updatingAccountData";
    public static final String MSG_UPDATING_ACCOUNT_DATA_ERROR = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".updatingAccountDataError";
    public static final String MSG_CREATING_ACCOUNT = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".creatingAccount";
    public static final String MSG_CREATING_ACCOUNT_ERROR = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".creatingAccountError";
    public static final String MSG_CREATING_ACCOUNT_ERROR_UNREACHABLE = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".creatingAccountErrorUnreachable";
    public static final String MSG_CREATING_ACCOUNT_ERROR_FORBIDDEN = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".creatingAccountErrorForbidden";
    public static final String MSG_SAVING_CONFIG_FILE = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".savingConfigFile";
    public static final String MSG_SAVING_CONFIG_FILE_ERROR = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".savingConfigFileError";
    public static final String MSG_VERIFYING_COUPON = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".verifyingCoupon";
    public static final String MSG_ACTIVATING_COUPON = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".activatingCoupon";
    public static final String MSG_ACTIVATING_COUPON_ERROR = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".activatingCouponError";
    public static final String MSG_ACTIVATING_COUPON_NOT_ACCEPTED = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".activatingCouponNotAccepted";
    public static final String MSG_ACTIVATING_COUPON_NOT_A_NEW_USER = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".activatingCouponNotANewUser";
    public static final String MSG_ERROR_ALLOW_NON_ANONYMOUS_OR_CONNECT = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".errorAllowNonAnonymousOrConnect";
    public static final String MSG_ERROR_ALLOW_NON_ANONYMOUS_OR_CONNECT_INFOSERVICE = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".errorAllowNonAnonymousOrConnectInfoService";
    public static final String MSG_ERROR_DELETION_ACCOUNT_BLOCKED = (class$anon$pay$PayAccountsFile == null ? (class$anon$pay$PayAccountsFile = PayAccountsFile.class$("anon.pay.PayAccountsFile")) : class$anon$pay$PayAccountsFile).getName() + ".errorDeletionAccountBlocked";
    private static boolean m_bIsInitialized = false;
    private boolean m_bIgnoreAIAccountErrorMessages = false;
    private boolean m_bEnableBalanceAutoUpdate = true;
    private Hashtable m_hashAffiliate = new Hashtable();
    private Vector m_Accounts = new Vector();
    private boolean m_bIsNewUser = true;
    private PayAccount m_ActiveAccount = null;
    private static PayAccountsFile ms_AccountsFile = null;
    private Vector m_paymentListeners = new Vector();
    private Vector m_messageListeners = new Vector();
    private MyAccountListener m_MyAccountListener = new MyAccountListener();
    private DSAKeyPool m_keyPool;
    private static int ms_keyPoolSize = 2;
    private IAffiliateOptOut m_affiliateOptOut = new IAffiliateOptOut(){

        public boolean isAffiliateAllowed() {
            return true;
        }
    };
    private boolean m_bGotPremiumAccount = false;
    static /* synthetic */ Class class$anon$pay$PayAccountsFile;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;

    private PayAccountsFile() {
        this.m_keyPool = new DSAKeyPool(ms_keyPoolSize);
        this.m_keyPool.start();
    }

    public static PayAccountsFile getInstance() {
        if (ms_AccountsFile == null) {
            ms_AccountsFile = new PayAccountsFile();
        }
        return ms_AccountsFile;
    }

    public AsymmetricCryptoKeyPair createAccountKeyPair() {
        return this.m_keyPool.popKeyPair();
    }

    public AsymmetricCryptoKeyPair createAccountKeyPair(int n) {
        return DSAKeyPair.getInstance(new SecureRandom(), n, 60);
    }

    public void setIgnoreAIAccountError(boolean bl) {
        this.m_bIgnoreAIAccountErrorMessages = bl;
    }

    public boolean isNewUser() {
        return this.m_bIsNewUser;
    }

    public boolean isBalanceAutoUpdateEnabled() {
        return this.m_bEnableBalanceAutoUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setBalanceAutoUpdateEnabled(boolean bl) {
        PayAccountsFile payAccountsFile = this;
        synchronized (payAccountsFile) {
            if (this.m_bEnableBalanceAutoUpdate != bl) {
                this.m_bEnableBalanceAutoUpdate = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_AUTO_UPDATE);
        }
    }

    public boolean isAIAccountErrorIgnored() {
        return this.m_bIgnoreAIAccountErrorMessages;
    }

    public synchronized boolean importAccounts(Element element, IMiscPasswordReader iMiscPasswordReader) throws XMLParseException, Exception {
        return this.importAccounts(element, iMiscPasswordReader, false);
    }

    public synchronized boolean importAccounts(Element element, IMiscPasswordReader iMiscPasswordReader, boolean bl) throws XMLParseException, Exception {
        boolean bl2 = false;
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Accounts");
        Node node = XMLUtil.getFirstChildByName(element2, "Account");
        while (node != null) {
            PayMessage payMessage;
            XMLBalance xMLBalance;
            PayAccount payAccount = new PayAccount((Element)node, iMiscPasswordReader);
            if (bl && payAccount.getPrivateKey() == null) continue;
            try {
                this.addAccount(payAccount);
                bl2 = true;
            }
            catch (AccountAlreadyExistingException accountAlreadyExistingException) {
                LogHolder.log(4, LogType.PAY, "Account " + payAccount.getAccountNumber() + " already existed in our configuration and was not added.");
            }
            if (payAccount.getAccountInfo() != null && (xMLBalance = payAccount.getAccountInfo().getBalance()) != null && (payMessage = xMLBalance.getMessage()) != null && !payMessage.getShortMessage().equals("")) {
                PayAccountsFile.getInstance().messageReceived(payMessage);
            }
            while ((node = XMLUtil.getNextSibling(node)) != null && !(node instanceof Element)) {
            }
        }
        return bl2;
    }

    public static synchronized boolean init(Element element, IMiscPasswordReader iMiscPasswordReader, boolean bl, int n, IAffiliateOptOut iAffiliateOptOut) {
        if (m_bIsInitialized) {
            return false;
        }
        if (iAffiliateOptOut != null) {
            PayAccountsFile.getInstance().m_affiliateOptOut = iAffiliateOptOut;
        }
        if (n >= 0) {
            ms_keyPoolSize = n;
        }
        if (element != null && element.getNodeName().equals(XML_ELEMENT_NAME)) {
            PayAccountsFile.getInstance().m_bIgnoreAIAccountErrorMessages = bl ? false : false;
            PayAccountsFile.getInstance().m_bEnableBalanceAutoUpdate = XMLUtil.parseAttribute((Node)element, XML_ATTR_ENABLE_BALANCE_AUTO_UPDATE, true);
            PayAccountsFile.getInstance().m_bIsNewUser = XMLUtil.parseAttribute((Node)element, XML_ATTR_IS_NEW_USER, PayAccountsFile.getInstance().m_bIsNewUser);
            Element element2 = (Element)XMLUtil.getFirstChildByName(element, "ActiveAccountNumber");
            NodeList nodeList = XMLUtil.getElementsByTagName(XMLUtil.getFirstChildByName(element, XML_ELEMENT_AFFILIATES), XML_ELEMENT_AFFILIATE);
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    String string = XMLUtil.parseValue(nodeList.item(i), null);
                    String string2 = XMLUtil.parseAttribute(nodeList.item(i), "piid", null);
                    if (string == null || string.length() <= 0 || string2 == null || string2.length() <= 0 || string.endsWith("_null")) continue;
                    PayAccountsFile.getInstance().m_hashAffiliate.put(string2, string);
                    PayAccountsFile.getInstance().m_bIsNewUser = false;
                }
            }
            long l = Long.parseLong(XMLUtil.parseValue((Node)element2, "0"));
            try {
                PayAccountsFile.getInstance().importAccounts(element, iMiscPasswordReader);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.PAY, exception);
                return false;
            }
            if (l > 0L) {
                Enumeration enumeration = PayAccountsFile.getInstance().m_Accounts.elements();
                while (enumeration.hasMoreElements()) {
                    PayAccount payAccount = (PayAccount)enumeration.nextElement();
                    if (payAccount.getAccountNumber() != l) continue;
                    try {
                        PayAccountsFile.getInstance().setActiveAccount(payAccount);
                    }
                    catch (Exception exception) {}
                    break;
                }
            }
            PayAccountsFile.getInstance().checkAffiliateToDelete();
        }
        PayAccountsFile.getInstance();
        m_bIsInitialized = true;
        return true;
    }

    private synchronized void checkAffiliateToDelete() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        Hashtable hashtable2 = (Hashtable)this.m_hashAffiliate.clone();
        Enumeration<Object> enumeration = this.m_Accounts.elements();
        while (enumeration.hasMoreElements()) {
            PayAccount payAccount = (PayAccount)enumeration.nextElement();
            hashtable.put(payAccount.getPIID(), payAccount.getPIID());
        }
        enumeration = this.m_hashAffiliate.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            if (hashtable.containsKey(string)) continue;
            hashtable2.remove(string);
        }
        this.m_hashAffiliate = hashtable2;
    }

    public Element toXmlElement(Document document) {
        return this.toXmlElement(document, null, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document, String string, boolean bl) {
        try {
            Element element;
            Element element2 = document.createElement(XML_ELEMENT_NAME);
            element2.setAttribute("version", "1.0");
            XMLUtil.setAttribute(element2, XML_ATTR_IGNORE_AI_ERRORS, this.m_bIgnoreAIAccountErrorMessages);
            XMLUtil.setAttribute(element2, XML_ATTR_ENABLE_BALANCE_AUTO_UPDATE, this.m_bEnableBalanceAutoUpdate);
            XMLUtil.setAttribute(element2, XML_ATTR_IS_NEW_USER, this.m_bIsNewUser);
            Element element3 = document.createElement("ActiveAccountNumber");
            XMLUtil.setValue((Node)element3, this.getActiveAccountNumber());
            element2.appendChild(element3);
            if (this.m_hashAffiliate.size() > 0) {
                element3 = document.createElement(XML_ELEMENT_AFFILIATES);
                Enumeration enumeration = this.m_hashAffiliate.keys();
                while (enumeration.hasMoreElements()) {
                    String string2 = (String)enumeration.nextElement();
                    if (this.m_hashAffiliate.get(string2) == null || this.m_hashAffiliate.get(string2).toString().endsWith("_null")) continue;
                    element = document.createElement(XML_ELEMENT_AFFILIATE);
                    XMLUtil.setValue((Node)element, (String)this.m_hashAffiliate.get(string2));
                    XMLUtil.setAttribute(element, "piid", string2);
                    element3.appendChild(element);
                }
                element2.appendChild(element3);
            }
            element3 = document.createElement("Accounts");
            element2.appendChild(element3);
            PayAccountsFile payAccountsFile = this;
            synchronized (payAccountsFile) {
                for (int i = 0; i < this.m_Accounts.size(); ++i) {
                    PayAccount payAccount = (PayAccount)this.m_Accounts.elementAt(i);
                    element = payAccount.toXmlElement(document, string, bl);
                    element3.appendChild(element);
                }
            }
            return element2;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "Exception while creating PayAccountsFile XML: " + exception);
            return null;
        }
    }

    public boolean hasActiveAccount() {
        return this.m_ActiveAccount != null;
    }

    public PayAccount getActiveAccount() {
        return this.m_ActiveAccount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void setActiveAccount(PayAccount payAccount) {
        PayAccount payAccount2 = null;
        if (payAccount != null) {
            payAccount2 = this.getAccount(payAccount.getAccountNumber(), payAccount.getPIID());
        }
        if (payAccount2 != null && payAccount2.getPrivateKey() != null) {
            Vector vector = this.m_paymentListeners;
            synchronized (vector) {
                this.m_ActiveAccount = payAccount2;
                Enumeration enumeration = this.m_paymentListeners.elements();
                while (enumeration.hasMoreElements()) {
                    ((IPaymentListener)enumeration.nextElement()).accountActivated(this.m_ActiveAccount);
                }
            }
        }
        if (payAccount2 == null) {
            this.m_ActiveAccount = null;
            Vector vector = this.m_paymentListeners;
            synchronized (vector) {
                Enumeration enumeration = this.m_paymentListeners.elements();
                while (enumeration.hasMoreElements()) {
                    ((IPaymentListener)enumeration.nextElement()).accountActivated(this.m_ActiveAccount);
                }
            }
        }
    }

    public long getActiveAccountNumber() {
        PayAccount payAccount = this.m_ActiveAccount;
        if (payAccount != null) {
            return payAccount.getAccountNumber();
        }
        return -1L;
    }

    public synchronized PayAccount getAccount(long l, String string) {
        PayAccount payAccount = null;
        Enumeration enumeration = this.m_Accounts.elements();
        while (enumeration.hasMoreElements() && ((payAccount = (PayAccount)enumeration.nextElement()).getAccountNumber() != l || string != payAccount.getPIID() && (string == null || payAccount.getPIID() == null || !string.equals(payAccount.getPIID())))) {
            payAccount = null;
        }
        return payAccount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void deleteAccount(PayAccount payAccount) {
        if (payAccount == null || payAccount.isLocked() || payAccount.isStatusUnknown()) {
            return;
        }
        PayAccount payAccount2 = null;
        Object object = this;
        synchronized (object) {
            payAccount2 = this.getAccount(payAccount.getAccountNumber(), payAccount.getPIID());
            if (payAccount2 != null) {
                PayMessage payMessage;
                for (int i = 0; i < this.m_Accounts.size(); ++i) {
                    payAccount2 = (PayAccount)this.m_Accounts.elementAt(i);
                    if (payAccount2.getAccountNumber() != payAccount.getAccountNumber()) continue;
                    this.m_Accounts.removeElementAt(i);
                    break;
                }
                if (payAccount2.getBalance() != null && (payMessage = payAccount2.getBalance().getMessage()) != null && !payMessage.getShortMessage().equals("")) {
                    this.fireMessageRemoved(payMessage);
                }
                if (this.getActiveAccount() == payAccount2) {
                    if (this.m_Accounts.size() > 0) {
                        this.setActiveAccount((PayAccount)this.m_Accounts.elementAt(0));
                    } else {
                        this.setActiveAccount(null);
                    }
                }
            }
            this.checkAffiliateToDelete();
        }
        if (payAccount2 != null) {
            object = this.m_paymentListeners;
            synchronized (object) {
                Enumeration enumeration = this.m_paymentListeners.elements();
                while (enumeration.hasMoreElements()) {
                    ((IPaymentListener)enumeration.nextElement()).accountRemoved(payAccount2);
                }
            }
        }
    }

    public String getAffiliate(String string, boolean bl) {
        if (string == null) {
            return null;
        }
        if (!this.m_affiliateOptOut.isAffiliateAllowed() && !bl) {
            return null;
        }
        return (String)this.m_hashAffiliate.get(string);
    }

    public Enumeration getAccounts() {
        return ((Vector)this.m_Accounts.clone()).elements();
    }

    public static void fireKnownMessages() {
        Enumeration enumeration = PayAccountsFile.getInstance().getAccounts();
        while (enumeration.hasMoreElements()) {
            XMLBalance xMLBalance;
            PayMessage payMessage;
            PayAccount payAccount = (PayAccount)enumeration.nextElement();
            XMLAccountInfo xMLAccountInfo = payAccount.getAccountInfo();
            if (xMLAccountInfo == null || (payMessage = (xMLBalance = payAccount.getAccountInfo().getBalance()).getMessage()) == null || payMessage.getShortMessage().equals("")) continue;
            ms_AccountsFile.fireMessageReceived(payMessage);
        }
    }

    public synchronized PayAccount getAlternativeChargedAccount(String string) {
        return this.getChargedAccount(string, this.getActiveAccount());
    }

    public PayAccount getAccountWaitingForTransaction(String string) {
        Vector vector = this.getAccounts(string);
        PayAccount payAccount = null;
        for (int i = 0; i < vector.size() && !(payAccount = (PayAccount)vector.elementAt(i)).isWaitingForTransaction(); ++i) {
            payAccount = null;
        }
        return payAccount;
    }

    public PayAccount getChargedAccount(String string) {
        return this.getChargedAccount(string, null);
    }

    public PayAccount getChargedAccount(String string, PayAccount payAccount) {
        Vector vector = this.getAccounts(string);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        PayAccount payAccount2 = null;
        if (payAccount != null && !vector.contains(payAccount)) {
            payAccount = null;
        }
        for (int i = 0; i < vector.size() && ((payAccount2 = (PayAccount)vector.elementAt(i)).isBlocked() || !payAccount2.isCharged(timestamp) || payAccount != null && payAccount == payAccount2); ++i) {
            payAccount2 = null;
        }
        return payAccount2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getAccounts(String string) {
        Vector vector = new Vector();
        Vector vector2 = this.m_Accounts;
        synchronized (vector2) {
            Enumeration enumeration = this.m_Accounts.elements();
            PayAccount payAccount = this.getActiveAccount();
            this.addAccount(vector, string, payAccount);
            while (enumeration.hasMoreElements()) {
                PayAccount payAccount2 = (PayAccount)enumeration.nextElement();
                if (payAccount2 == payAccount) continue;
                this.addAccount(vector, string, payAccount2);
            }
        }
        return vector;
    }

    private void addAccount(Vector vector, String string, PayAccount payAccount) {
        String string2;
        if (payAccount == null || vector == null) {
            return;
        }
        PaymentInstanceDBEntry paymentInstanceDBEntry = payAccount.getBI();
        if (paymentInstanceDBEntry == null) {
            LogHolder.log(3, LogType.PAY, "Payment instance for account nr. " + payAccount.getAccountNumber() + " not found!");
            string2 = payAccount.getPIID();
        } else {
            string2 = paymentInstanceDBEntry.getId();
        }
        if (string2 == null) {
            LogHolder.log(1, LogType.PAY, "Payment instance for account nr. " + payAccount.getAccountNumber() + " is null!");
            return;
        }
        if (string == null || string2.equals(string)) {
            vector.addElement(payAccount);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void addAccount(PayAccount payAccount) throws AccountAlreadyExistingException {
        boolean bl = false;
        Enumeration enumeration = this.m_Accounts.elements();
        while (enumeration.hasMoreElements()) {
            PayAccount payAccount2 = (PayAccount)enumeration.nextElement();
            if (payAccount2.getAccountNumber() != payAccount.getAccountNumber() || payAccount2.getPrivateKey() == null && payAccount.getPrivateKey() != null) continue;
            if (payAccount2.getPIID() == payAccount.getPIID()) {
                throw new AccountAlreadyExistingException();
            }
            if (payAccount2.getPIID() == null || payAccount.getPIID() == null || !payAccount2.getPIID().equals(payAccount.getPIID())) continue;
            throw new AccountAlreadyExistingException();
        }
        payAccount.addAccountListener(this.m_MyAccountListener);
        payAccount.addMessageListener(this);
        this.m_Accounts.addElement(payAccount);
        if (payAccount.isUsed()) {
            this.m_bIsNewUser = false;
        }
        if (this.m_ActiveAccount == null && payAccount.getPrivateKey() != null) {
            this.m_ActiveAccount = payAccount;
            bl = true;
        }
        if (this.getAffiliate(payAccount.getPIID(), true) == null && payAccount.getAffiliate() != null) {
            this.m_hashAffiliate.put(payAccount.getPIID(), payAccount.getAffiliate());
        }
        Vector vector = this.m_paymentListeners;
        synchronized (vector) {
            IPaymentListener iPaymentListener;
            Enumeration enumeration2 = this.m_paymentListeners.elements();
            while (enumeration2.hasMoreElements()) {
                iPaymentListener = (IPaymentListener)enumeration2.nextElement();
                iPaymentListener.accountAdded(payAccount);
                if (!bl) continue;
                iPaymentListener.accountActivated(payAccount);
            }
            enumeration2 = null;
            iPaymentListener = null;
        }
    }

    public int getNumAccounts() {
        return this.m_Accounts.size();
    }

    public synchronized PayAccount getAccountAt(int n) {
        return (PayAccount)this.m_Accounts.elementAt(n);
    }

    public boolean isInitialized() {
        return m_bIsInitialized;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPaymentListener(IPaymentListener iPaymentListener) {
        Vector vector = this.m_paymentListeners;
        synchronized (vector) {
            if (iPaymentListener != null) {
                this.m_paymentListeners.addElement(iPaymentListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePaymentListener(IPaymentListener iPaymentListener) {
        Vector vector = this.m_paymentListeners;
        synchronized (vector) {
            if (this.m_paymentListeners.contains(iPaymentListener)) {
                this.m_paymentListeners.removeElement(iPaymentListener);
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

    public PayAccount createAccount(PaymentInstanceDBEntry paymentInstanceDBEntry, XMLGenericText xMLGenericText) throws Exception {
        AsymmetricCryptoKeyPair asymmetricCryptoKeyPair = this.createAccountKeyPair();
        if (asymmetricCryptoKeyPair == null) {
            return null;
        }
        return this.createAccount(paymentInstanceDBEntry, asymmetricCryptoKeyPair, xMLGenericText);
    }

    public static boolean isNewUserLetter(char c) {
        return c == 'X' || c == 'Y';
    }

    public static boolean isPromotionLetter(char c) {
        return c == 'P';
    }

    public boolean isNewUserAllowed(String string) {
        if (string == null) {
            return true;
        }
        return !PayAccountsFile.isNewUserLetter(string.charAt(0)) || this.isNewUser();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean activateCouponCode(String string, PayAccount payAccount, boolean bl) throws Exception {
        boolean bl2 = true;
        if (payAccount == null) {
            throw new NullPointerException("No account given!");
        }
        if ((string = PayAccount.checkCouponCode(string)) == null) {
            return false;
        }
        BIConnection bIConnection = null;
        if (bl) {
            bl2 = false;
            bIConnection = new BIConnection(payAccount.getBI());
            try {
                bIConnection.connect();
                bIConnection.authenticate(payAccount);
                bl2 = bIConnection.checkCouponCode(string, payAccount);
                Object var7_6 = null;
                bIConnection.disconnect();
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                bIConnection.disconnect();
                throw throwable;
            }
            if (!this.isNewUserAllowed(string)) {
                return false;
            }
            return bl2;
        }
        XMLPassivePayment xMLPassivePayment = new XMLPassivePayment(payAccount.getPIID());
        xMLPassivePayment.addData("code", string);
        xMLPassivePayment.setPaymentName(METHOD_COUPON);
        long l = payAccount.getAccountNumber();
        xMLPassivePayment.addData("accountnumber", new Long(l).toString());
        bIConnection = null;
        try {
            bIConnection = new BIConnection(payAccount.getBI());
            bIConnection.connect();
            bIConnection.authenticate(payAccount);
            if (!bIConnection.sendPassivePayment(xMLPassivePayment, payAccount)) {
                bl2 = false;
            }
            Object var10_11 = null;
            if (bIConnection != null) {
                bIConnection.disconnect();
            }
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
            if (bIConnection != null) {
                bIConnection.disconnect();
            }
            throw throwable;
        }
        if (!bl2) {
            throw new Exception("Coupon code was not accepted when charging at the payment instance!");
        }
        return true;
    }

    public PayAccount createAccount(PaymentInstanceDBEntry paymentInstanceDBEntry, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, XMLGenericText xMLGenericText) throws Exception {
        return this.createAccount(paymentInstanceDBEntry, asymmetricCryptoKeyPair, xMLGenericText, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PayAccount createAccount(PaymentInstanceDBEntry paymentInstanceDBEntry, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, XMLGenericText xMLGenericText, boolean bl) throws Exception {
        XMLAccountCertificate xMLAccountCertificate;
        XMLJapPublicKey xMLJapPublicKey = new XMLJapPublicKey(asymmetricCryptoKeyPair.getPublic());
        LogHolder.log(7, LogType.PAY, "Attempting to create account at PI " + paymentInstanceDBEntry.getName());
        BIConnection bIConnection = new BIConnection(paymentInstanceDBEntry);
        bIConnection.addConnectionListener(this);
        try {
            bIConnection.connect();
            xMLAccountCertificate = bIConnection.registerNewAccount(xMLJapPublicKey, asymmetricCryptoKeyPair.getPrivate());
            Object var9_8 = null;
            bIConnection.disconnect();
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            bIConnection.disconnect();
            throw throwable;
        }
        PayAccount payAccount = new PayAccount(xMLAccountCertificate, asymmetricCryptoKeyPair.getPrivate(), paymentInstanceDBEntry, xMLGenericText, bl);
        this.addAccount(payAccount);
        return payAccount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void signalAccountRequest(MixCascade mixCascade) throws AccountEmptyException {
        Vector vector = this.m_paymentListeners;
        synchronized (vector) {
            Enumeration enumeration = this.m_paymentListeners.elements();
            while (enumeration.hasMoreElements()) {
                ((IPaymentListener)enumeration.nextElement()).accountCertRequested(mixCascade);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void signalAccountError(XMLErrorMessage xMLErrorMessage) {
        Object object = this.m_paymentListeners;
        synchronized (object) {
            IPaymentListener iPaymentListener = null;
            Enumeration enumeration = this.m_paymentListeners.elements();
            while (enumeration.hasMoreElements()) {
                iPaymentListener = (IPaymentListener)enumeration.nextElement();
                iPaymentListener.accountError(xMLErrorMessage, this.m_bIgnoreAIAccountErrorMessages);
            }
        }
        if (xMLErrorMessage.getXmlErrorCode() == 21 && (object = this.getAccount(xMLErrorMessage.getAccountNumber(), xMLErrorMessage.getPIID())) != null) {
            ((PayAccount)object).setBlocked(true);
            this.m_MyAccountListener.accountChanged((PayAccount)object);
        }
    }

    public Vector getPaymentInstances(boolean bl) {
        Vector vector = Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PayAccountsFile.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryList();
        Vector vector2 = new Vector();
        for (int i = 0; i < vector.size(); ++i) {
            if (bl == ((PaymentInstanceDBEntry)vector.elementAt(i)).isTest()) continue;
            vector2.addElement(vector.elementAt(i));
        }
        return vector2;
    }

    public Vector getPaymentInstances() {
        return Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PayAccountsFile.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryList();
    }

    public PaymentInstanceDBEntry getBI(String string) {
        PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PayAccountsFile.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(string);
        return paymentInstanceDBEntry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gotCaptcha(ICaptchaSender iCaptchaSender, IImageEncodedCaptcha iImageEncodedCaptcha) {
        Vector vector = this.m_paymentListeners;
        synchronized (vector) {
            Enumeration enumeration = this.m_paymentListeners.elements();
            while (enumeration.hasMoreElements()) {
                ((IPaymentListener)enumeration.nextElement()).gotCaptcha(iCaptchaSender, iImageEncodedCaptcha);
            }
        }
    }

    public void messageReceived(PayMessage payMessage) {
        this.fireMessageReceived(payMessage);
    }

    public void messageRemoved(PayMessage payMessage) {
        this.fireMessageRemoved(payMessage);
    }

    protected void finalize() {
        if (this.m_keyPool != null) {
            this.m_keyPool.stop();
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

    private class MyAccountListener
    implements IAccountListener {
        private MyAccountListener() {
        }

        public void accountChanged(PayAccount payAccount) {
            Enumeration enumeration = ((Vector)PayAccountsFile.this.m_paymentListeners.clone()).elements();
            if (payAccount != null) {
                String string = payAccount.getAffiliate();
                if (string != null && PayAccountsFile.this.getAffiliate(payAccount.getPIID(), true) == null) {
                    PayAccountsFile.this.m_hashAffiliate.put(payAccount.getPIID(), string);
                }
                while (enumeration.hasMoreElements()) {
                    ((IPaymentListener)enumeration.nextElement()).creditChanged(payAccount);
                }
                if (payAccount.isUsed()) {
                    PayAccountsFile.this.m_bIsNewUser = false;
                }
            }
        }
    }

    public static class AccountAlreadyExistingException
    extends Exception {
    }

    public static interface IAffiliateOptOut {
        public boolean isAffiliateAllowed();
    }
}

