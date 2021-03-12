/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import HTTPClient.ForbiddenIOException;
import anon.AnonServerDescription;
import anon.crypto.ByteSignature;
import anon.crypto.IMyPrivateKey;
import anon.crypto.XMLSignature;
import anon.crypto.tinytls.TinyTLS;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.IProxyInterfaceGetter;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.pay.HttpClient;
import anon.pay.IBIConnectionListener;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLAccountCertificate;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLBalance;
import anon.pay.xml.XMLChallenge;
import anon.pay.xml.XMLErrorMessage;
import anon.pay.xml.XMLGenericStrings;
import anon.pay.xml.XMLGenericText;
import anon.pay.xml.XMLJapPublicKey;
import anon.pay.xml.XMLPassivePayment;
import anon.pay.xml.XMLPaymentOptions;
import anon.pay.xml.XMLPaymentSettings;
import anon.pay.xml.XMLResponse;
import anon.pay.xml.XMLTransCert;
import anon.pay.xml.XMLTransactionOverview;
import anon.pay.xml.XMLVolumePlans;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;
import anon.util.captcha.ZipBinaryImageCaptchaClient;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BIConnection
implements ICaptchaSender {
    public static final int TIMEOUT_DEFAULT = 40000;
    public static final int TIMEOUT_MAX = 100000;
    public static final int TIMEOUT_MIN = 1000;
    public static final String XML_ATTR_CONNECTION_TIMEOUT = "timeout";
    private static int ms_connectionTimeout = 40000;
    private PaymentInstanceDBEntry m_theBI;
    private Socket m_socket;
    private HttpClient m_httpClient;
    private Vector m_biConnectionListeners;
    private byte[] m_captchaSolution;
    private boolean m_bSendNewCaptcha;
    private boolean m_bFirstCaptcha = true;
    private static IMutableProxyInterface ms_proxyInterface = new IMutableProxyInterface.DummyMutableProxyInterface();

    public BIConnection(PaymentInstanceDBEntry paymentInstanceDBEntry) {
        if (paymentInstanceDBEntry == null) {
            throw new IllegalArgumentException("PI is null! No connection is possibble.");
        }
        this.m_theBI = paymentInstanceDBEntry;
        this.m_biConnectionListeners = new Vector();
    }

    public static void setConnectionTimeout(int n) {
        ms_connectionTimeout = n > 1000 ? (n > 100000 ? 100000 : n) : 1000;
    }

    public static int getConnectionTimeout() {
        return ms_connectionTimeout;
    }

    public static void setMutableProxyInterface(IMutableProxyInterface iMutableProxyInterface) {
        if (iMutableProxyInterface != null) {
            ms_proxyInterface = iMutableProxyInterface;
        }
    }

    public void connect() throws IOException {
        this.connect(ms_connectionTimeout);
    }

    public void connect(int n) throws IOException {
        IOException iOException = new IOException("No valid proxy available");
        boolean bl = false;
        for (int i = 0; i < 2 && !Thread.currentThread().isInterrupted(); ++i) {
            IProxyInterfaceGetter iProxyInterfaceGetter;
            if (i == 1) {
                bl = true;
            }
            if ((iProxyInterfaceGetter = ms_proxyInterface.getProxyInterface(bl)) == null) continue;
            ImmutableProxyInterface immutableProxyInterface = iProxyInterfaceGetter.getProxyInterface();
            if (bl && immutableProxyInterface == null) continue;
            try {
                this.connect_internal(immutableProxyInterface, n);
                return;
            }
            catch (IOException iOException2) {
                iOException = iOException2;
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException("Thread interrupted while connecting to payment instance.");
        }
        throw iOException;
    }

    private void connect_internal(ImmutableProxyInterface immutableProxyInterface, int n) throws IOException {
        boolean bl = false;
        TinyTLS tinyTLS = null;
        ListenerInterface listenerInterface = null;
        boolean bl2 = false;
        Enumeration enumeration = this.m_theBI.getListenerInterfaces();
        while (enumeration.hasMoreElements()) {
            listenerInterface = (ListenerInterface)enumeration.nextElement();
            LogHolder.log(7, LogType.PAY, "Trying to connect to Payment Instance at " + listenerInterface.getHost() + ":" + listenerInterface.getPort() + ".");
            try {
                if (immutableProxyInterface == null || !immutableProxyInterface.isValid()) {
                    tinyTLS = new TinyTLS(listenerInterface.getHost(), listenerInterface.getPort());
                } else {
                    LogHolder.log(6, LogType.PAY, "Using proxy at " + immutableProxyInterface.getHost() + ":" + immutableProxyInterface.getPort());
                    tinyTLS = new TinyTLS(listenerInterface.getHost(), listenerInterface.getPort(), immutableProxyInterface);
                }
                this.m_socket = tinyTLS;
                if (n < 1000 || n > 100000) {
                    tinyTLS.setSoTimeout(ms_connectionTimeout);
                } else {
                    tinyTLS.setSoTimeout(n);
                }
                if (this.m_theBI.getCertPath().getFirstVerifiedPath() == null) {
                    // empty if block
                }
                tinyTLS.setRootKey(this.m_theBI.getCertPath().getFirstVerifiedPath().getFirstCertificate().getPublicKey());
                tinyTLS.startHandshake();
                this.m_httpClient = new HttpClient(this.m_socket);
                bl2 = true;
                break;
            }
            catch (Exception exception) {
                if (this.m_httpClient != null) {
                    try {
                        this.m_httpClient.close();
                    }
                    catch (Exception exception2) {
                        LogHolder.log(3, LogType.NET, exception2);
                    }
                } else if (this.m_socket != null) {
                    try {
                        this.m_socket.close();
                    }
                    catch (IOException iOException) {
                        LogHolder.log(3, LogType.NET, iOException);
                    }
                }
                if (exception instanceof ForbiddenIOException) {
                    bl = true;
                }
                if (enumeration.hasMoreElements()) {
                    LogHolder.log(3, LogType.NET, "Could not connect to Payment Instance at " + listenerInterface.getHost() + ":" + listenerInterface.getPort() + ". Trying next interface...", exception);
                    continue;
                }
                LogHolder.log(2, LogType.NET, "Could not connect to Payment Instance at " + listenerInterface.getHost() + ":" + listenerInterface.getPort() + ". No more interfaces left.", exception);
            }
        }
        if (!bl2) {
            String string = "Could not connect to Payment Instance";
            if (bl) {
                throw new ForbiddenIOException(string);
            }
            throw new IOException(string);
        }
        LogHolder.log(6, LogType.PAY, "Connected to Payment Instance at " + listenerInterface.getHost() + ":" + listenerInterface.getPort() + ".", 1);
    }

    public void disconnect() {
        try {
            if (this.m_httpClient != null) {
                this.m_httpClient.close();
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.PAY, exception);
        }
    }

    public XMLTransCert charge(XMLGenericStrings xMLGenericStrings) throws Exception {
        this.m_httpClient.writeRequest("POST", "charge", XMLUtil.toString(xMLGenericStrings.toXmlElement(XMLUtil.createDocument())));
        Document document = this.m_httpClient.readAnswer();
        XMLTransCert xMLTransCert = new XMLTransCert(document);
        if (!XMLSignature.verifyFast((Node)document, this.m_theBI.getCertPath().getEndEntityKeys())) {
            throw new Exception("The BI's signature under the transfer certificate is invalid");
        }
        xMLTransCert.setReceivedDate(new Date());
        return xMLTransCert;
    }

    public XMLErrorMessage buyFlatrate(PayAccount payAccount) throws Exception {
        this.m_httpClient.writeRequest("POST", "buyflat", new Long(payAccount.getAccountNumber()).toString());
        Document document = this.m_httpClient.readAnswer();
        XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(document, payAccount, (AnonServerDescription)null);
        return xMLErrorMessage;
    }

    public XMLAccountInfo getAccountInfo(PayAccount payAccount) throws Exception {
        this.m_httpClient.writeRequest("GET", "balance", null);
        return this.getAccountInfo(this.m_httpClient.readAnswer(), payAccount);
    }

    private XMLAccountInfo getAccountInfo(Document document, PayAccount payAccount) throws Exception {
        if (document.getDocumentElement().getTagName().equals("ErrorMessage")) {
            XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(document, payAccount, (AnonServerDescription)null);
            if (xMLErrorMessage.getErrorCode() == 4) {
                // empty if block
            }
            throw xMLErrorMessage;
        }
        XMLAccountInfo xMLAccountInfo = new XMLAccountInfo(document);
        XMLBalance xMLBalance = xMLAccountInfo.getBalance();
        if (!XMLSignature.verifyFast((Node)XMLUtil.toXMLDocument(xMLBalance), this.m_theBI.getCertPath().getEndEntityKeys())) {
            throw new Exception("The BI's signature under the balance certificate is Invalid!");
        }
        return xMLAccountInfo;
    }

    protected XMLAccountInfo requestMonthlyOverusage(double d, PayAccount payAccount) throws Exception {
        this.m_httpClient.writeRequest("GET", "overusage/" + d + "/time/" + System.currentTimeMillis(), null);
        return this.getAccountInfo(this.m_httpClient.readAnswer(), payAccount);
    }

    public XMLPaymentOptions getPaymentOptions() throws Exception {
        this.m_httpClient.writeRequest("GET", "paymentoptions", null);
        Document document = this.m_httpClient.readAnswer();
        XMLPaymentOptions xMLPaymentOptions = new XMLPaymentOptions(document);
        return xMLPaymentOptions;
    }

    public XMLVolumePlans getVolumePlans() throws Exception {
        this.m_httpClient.writeRequest("GET", "volumeplans", null);
        Document document = this.m_httpClient.readAnswer();
        XMLVolumePlans xMLVolumePlans = new XMLVolumePlans(document);
        return xMLVolumePlans;
    }

    public XMLGenericText getTerms(String string) throws Exception {
        XMLGenericText xMLGenericText;
        this.m_httpClient.writeRequest("POST", "terms", string);
        Document document = this.m_httpClient.readAnswer();
        try {
            xMLGenericText = new XMLGenericText(document);
        }
        catch (Exception exception) {
            return null;
        }
        return xMLGenericText;
    }

    public XMLGenericText getCancellationPolicy(String string) throws Exception {
        XMLGenericText xMLGenericText;
        this.m_httpClient.writeRequest("POST", "cancellationpolicy", string);
        Document document = this.m_httpClient.readAnswer();
        try {
            xMLGenericText = new XMLGenericText(document);
        }
        catch (Exception exception) {
            return null;
        }
        return xMLGenericText;
    }

    public XMLPaymentSettings getPaymentSettings() throws Exception {
        this.m_httpClient.writeRequest("GET", "paymentsettings", null);
        Document document = this.m_httpClient.readAnswer();
        return new XMLPaymentSettings(document);
    }

    public void authenticate(PayAccount payAccount) throws Exception {
        XMLAccountCertificate xMLAccountCertificate = payAccount.getAccountCertificate();
        IMyPrivateKey iMyPrivateKey = payAccount.getPrivateKey();
        String string = XMLUtil.toString(XMLUtil.toXMLDocument(xMLAccountCertificate));
        this.m_httpClient.writeRequest("POST", "authenticate", string);
        Document document = this.m_httpClient.readAnswer();
        String string2 = document.getDocumentElement().getTagName();
        if (string2.equals("Challenge")) {
            XMLChallenge xMLChallenge = new XMLChallenge(document);
            if (xMLChallenge.getType() == null || !xMLChallenge.getType().equals("PaymentInstance") || xMLChallenge.getId() == null || !xMLChallenge.getId().equals(this.m_theBI.getId())) {
                throw new Exception("Challenge is invalid! Type: " + xMLChallenge.getType() + " ID: " + xMLChallenge.getId() + " (Expected: " + "PaymentInstance" + ", " + this.m_theBI.getId());
            }
            byte[] arrby = xMLChallenge.getChallengeForSigning();
            byte[] arrby2 = ByteSignature.sign(arrby, iMyPrivateKey);
            XMLResponse xMLResponse = new XMLResponse(arrby2, PayAccountsFile.getInstance().getAffiliate(this.m_theBI.getId(), false));
            String string3 = XMLUtil.toString(XMLUtil.toXMLDocument(xMLResponse));
            this.m_httpClient.writeRequest("POST", "response", string3);
            document = this.m_httpClient.readAnswer();
            XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(document, payAccount, (AnonServerDescription)null);
            if (xMLErrorMessage.getXmlErrorCode() >= 0 && xMLErrorMessage.getXmlErrorCode() != 0) {
                throw xMLErrorMessage;
            }
        } else if (string2.equals("ErrorMessage")) {
            throw new Exception("The BI sent an errormessage: " + new XMLErrorMessage(document, payAccount, (AnonServerDescription)null).getMessage());
        }
    }

    public XMLAccountCertificate registerNewAccount(XMLJapPublicKey xMLJapPublicKey, IMyPrivateKey iMyPrivateKey) throws Exception {
        Object object;
        Document document;
        byte[] arrby = null;
        this.m_bSendNewCaptcha = true;
        while (this.m_bSendNewCaptcha) {
            if (!this.m_bFirstCaptcha) {
                try {
                    this.disconnect();
                }
                catch (Exception exception) {
                    LogHolder.log(6, LogType.PAY, "Not connected to payment instance while trying to disconnect");
                }
                this.connect();
            }
            this.m_httpClient.writeRequest("POST", "register", XMLUtil.toString(XMLUtil.toXMLDocument(xMLJapPublicKey)));
            document = this.m_httpClient.readAnswer();
            try {
                object = new XMLChallenge(document.getDocumentElement());
                if (((XMLChallenge)object).getType() == null || !((XMLChallenge)object).getType().equals("PaymentInstance") || ((XMLChallenge)object).getId() == null || !((XMLChallenge)object).getId().equals(this.m_theBI.getId())) {
                    throw new Exception("Challenge is invalid! Type: " + ((XMLChallenge)object).getType() + " ID: " + ((XMLChallenge)object).getId() + " (Expected: " + "PaymentInstance" + ", " + this.m_theBI.getId());
                }
                arrby = ((XMLChallenge)object).getChallengeForSigning();
                this.m_bSendNewCaptcha = false;
                break;
            }
            catch (Exception exception) {
                LogHolder.log(4, LogType.PAY, "No challenge sent directly while registering account, trying capchta...", exception);
                object = new ZipBinaryImageCaptchaClient(document.getDocumentElement());
                this.m_bSendNewCaptcha = false;
                this.fireGotCaptcha((IImageEncodedCaptcha)object);
            }
        }
        if (this.m_captchaSolution != null) {
            object = new String(this.m_captchaSolution);
            int n = ((String)object).lastIndexOf(">");
            object = ((String)object).substring(0, n + 1);
            int n2 = ((String)object).indexOf(">") + 1;
            int n3 = ((String)object).lastIndexOf("<");
            object = ((String)object).substring(n2, n3);
            object = "<DontPanic>" + (String)object + "</DontPanic>";
            arrby = ((String)object).getBytes();
        } else if (arrby == null) {
            throw new Exception("CAPTCHA");
        }
        object = null;
        byte[] arrby2 = ByteSignature.sign(arrby, iMyPrivateKey);
        XMLResponse xMLResponse = new XMLResponse(arrby2, PayAccountsFile.getInstance().getAffiliate(this.m_theBI.getId(), false));
        String string = XMLUtil.toString(XMLUtil.toXMLDocument(xMLResponse));
        this.m_httpClient.writeRequest("POST", "response", string);
        document = this.m_httpClient.readAnswer();
        if (document.getDocumentElement().getTagName().equals("ErrorMessage")) {
            XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(document.getDocumentElement(), null, null);
            LogHolder.log(3, LogType.PAY, xMLErrorMessage.getMessage());
            throw xMLErrorMessage;
        }
        if (!XMLSignature.verifyFast((Node)document, this.m_theBI.getCertPath().getEndEntityKeys())) {
            throw new Exception("AccountCertificate: Wrong signature!");
        }
        object = new XMLAccountCertificate(document.getDocumentElement());
        if (!((XMLAccountCertificate)object).getPublicKey().equals(xMLJapPublicKey.getPublicKey())) {
            throw new Exception("The JPI is evil (sent a valid certificate, but with a wrong publickey)");
        }
        return object;
    }

    public XMLPaymentOptions fetchPaymentOptions() throws Exception {
        this.m_httpClient.writeRequest("GET", "paymentoptions", null);
        Document document = this.m_httpClient.readAnswer();
        XMLPaymentOptions xMLPaymentOptions = new XMLPaymentOptions(document.getDocumentElement());
        return xMLPaymentOptions;
    }

    public IXMLEncodable fetchPaymentData(String string, PayAccount payAccount) throws Exception {
        this.m_httpClient.writeRequest("POST", "paymentdata", string);
        Document document = this.m_httpClient.readAnswer();
        if (document == null) {
            return null;
        }
        IXMLEncodable iXMLEncodable = document.getDocumentElement().getTagName().equalsIgnoreCase("PassivePayment") ? new XMLPassivePayment(document.getDocumentElement()) : new XMLErrorMessage(document.getDocumentElement(), payAccount, (AnonServerDescription)null);
        return iXMLEncodable;
    }

    public XMLTransactionOverview fetchTransactionOverview(XMLTransactionOverview xMLTransactionOverview) throws Exception {
        String string = XMLUtil.toString(xMLTransactionOverview.toXmlElement(XMLUtil.createDocument()));
        this.m_httpClient.writeRequest("POST", "transactionoverview", string);
        Document document = this.m_httpClient.readAnswer();
        Element element = document.getDocumentElement();
        if (element.getTagName().equalsIgnoreCase("ErrorMessage")) {
            return null;
        }
        XMLTransactionOverview xMLTransactionOverview2 = new XMLTransactionOverview(document.getDocumentElement());
        return xMLTransactionOverview2;
    }

    public boolean sendPassivePayment(XMLPassivePayment xMLPassivePayment, PayAccount payAccount) {
        try {
            String string = XMLUtil.toString(xMLPassivePayment.toXmlElement(XMLUtil.createDocument()));
            payAccount.setStatusUnknown(true);
            this.m_httpClient.writeRequest("POST", "passivepayment", string);
            Document document = this.m_httpClient.readAnswer();
            XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(document.getDocumentElement(), payAccount, (AnonServerDescription)null);
            payAccount.setStatusUnknown(false);
            if (xMLErrorMessage.getXmlErrorCode() == 0) {
                if (xMLErrorMessage.getMessageObject() != null && xMLErrorMessage.getMessageObject() instanceof XMLAccountInfo) {
                    payAccount.setAccountInfo((XMLAccountInfo)xMLErrorMessage.getMessageObject());
                }
                return true;
            }
            return false;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "Could not send PassivePayment to payment instance: " + exception);
            return false;
        }
    }

    public boolean checkCouponCode(String string, PayAccount payAccount) {
        try {
            this.m_httpClient.writeRequest("POST", "coupon", string);
            Document document = this.m_httpClient.readAnswer();
            XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(document.getDocumentElement(), payAccount, (AnonServerDescription)null);
            if (xMLErrorMessage.getXmlErrorCode() == 0) {
                return true;
            }
            LogHolder.log(3, LogType.PAY, "User entered an invalid coupon, reply from jpi was: " + xMLErrorMessage.getMessage());
            return false;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "BIConnection.checkCouponCode: Could not check coupon validity due to: " + exception + " so I'll return false");
            return false;
        }
    }

    public void addConnectionListener(IBIConnectionListener iBIConnectionListener) {
        if (!this.m_biConnectionListeners.contains(iBIConnectionListener)) {
            this.m_biConnectionListeners.addElement(iBIConnectionListener);
        }
    }

    private void fireGotCaptcha(IImageEncodedCaptcha iImageEncodedCaptcha) {
        for (int i = 0; i < this.m_biConnectionListeners.size(); ++i) {
            ((IBIConnectionListener)this.m_biConnectionListeners.elementAt(i)).gotCaptcha(this, iImageEncodedCaptcha);
        }
    }

    public void setCaptchaSolution(byte[] arrby) {
        this.m_captchaSolution = arrby;
    }

    public void getNewCaptcha() {
        this.m_bSendNewCaptcha = true;
        this.m_bFirstCaptcha = false;
    }
}

