/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.AnonServerDescription;
import anon.IServiceContainer;
import anon.client.Multiplexer;
import anon.client.PacketCounter;
import anon.client.XmlControlChannel;
import anon.crypto.ByteSignature;
import anon.error.AnonServiceException;
import anon.error.ProtocolViolationException;
import anon.error.ServiceInterruptedException;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.infoservice.MixPosition;
import anon.pay.IAIEventListener;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.NotRecoverableXMLError;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLAiLoginConfirmation;
import anon.pay.xml.XMLChallenge;
import anon.pay.xml.XMLEasyCC;
import anon.pay.xml.XMLErrorMessage;
import anon.pay.xml.XMLPayRequest;
import anon.pay.xml.XMLPriceCertificate;
import anon.pay.xml.XMLResponse;
import anon.util.XMLUtil;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AIControlChannel
extends XmlControlChannel {
    public static final long MAX_PREPAID_INTERVAL = 4000000L;
    public static final long MIN_PREPAID_INTERVAL = 5000L;
    public static final long AI_LOGIN_TIMEOUT = 120000L;
    private static final long MULTIPLE_LOGIN_BLOCK_TIME = 30000L;
    private static final int FIRST_MIX = 0;
    private static final String PREPAID_AMOUNT_IN_PAY_REQ_MIXVERSION = "00.08.42";
    public static final boolean REVERT_PRE_PREPAID = true;
    public static final Hashtable HASH_PREPAID_ON_SERVICES = new Hashtable();
    private int m_aiLogin_timeout = 30000;
    private XMLErrorMessage m_lastErrorMessage;
    private static long m_totalBytes = 0L;
    private boolean m_bPrepaidReceived = false;
    private long m_prepaidBytes = 0L;
    private boolean m_bMultiplexerClosed = false;
    private Vector m_aiListeners = new Vector();
    private PacketCounter m_packetCounter;
    private EmptyAccountPacketObserver m_packetCountEmptyObserver;
    private boolean m_bEmptyMessageSent = false;
    private MixCascade m_connectedCascade;
    private PayAccount m_payAccount;
    private XMLEasyCC m_initialCC;
    private final Vector m_aiLoginSyncObject = new Vector(1);
    private boolean m_prepaidAmountInPayRequest = false;
    private boolean m_bLastCCSignaled = false;

    public AIControlChannel(Multiplexer multiplexer, PacketCounter packetCounter, IServiceContainer iServiceContainer, MixCascade mixCascade) {
        super(2, multiplexer, iServiceContainer, true);
        this.m_packetCounter = packetCounter;
        this.m_connectedCascade = mixCascade;
        if (this.m_packetCounter == null) {
            throw new NullPointerException();
        }
        this.m_packetCountEmptyObserver = new EmptyAccountPacketObserver(this.m_connectedCascade.getConcatenatedPriceCertHashes());
        this.m_packetCounter.addObserver(this.m_packetCountEmptyObserver);
        String string = this.m_connectedCascade.getMixInfo(0).getServiceSoftware().getVersion();
        if (string != null) {
            // empty if block
        }
        LogHolder.log(6, LogType.PAY, "Mix " + this.m_connectedCascade.getMixInfo(0).getName() + (this.m_prepaidAmountInPayRequest ? " supports " : " does not support ") + "improved prepaid bytes negotiation.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addAIListener(IAIEventListener iAIEventListener) {
        Vector vector = this.m_aiListeners;
        synchronized (vector) {
            if (!this.m_aiListeners.contains(iAIEventListener)) {
                this.m_aiListeners.addElement(iAIEventListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processXmlMessage(Document document) {
        block34: {
            Element element = document.getDocumentElement();
            String string = element.getTagName();
            if (!(string.equals("ErrorMessage") || this.m_payAccount != null && this.m_payAccount.getCurrentCredit() > 0L)) {
                element = new XMLErrorMessage(10, this.m_payAccount, (AnonServerDescription)this.m_connectedCascade).toXmlElement(document);
                string = "ErrorMessage";
            }
            try {
                if (string.equals(XMLPayRequest.XML_ELEMENT_NAME)) {
                    XMLPayRequest xMLPayRequest = new XMLPayRequest(element);
                    this.processPayRequest(xMLPayRequest);
                    break block34;
                }
                if (string.equals("LoginConfirmation")) {
                    XMLAiLoginConfirmation xMLAiLoginConfirmation = new XMLAiLoginConfirmation(element);
                    Vector vector = this.m_aiLoginSyncObject;
                    synchronized (vector) {
                        if (xMLAiLoginConfirmation.getCode() == 0) {
                            this.m_aiLoginSyncObject.addElement(new Object());
                        }
                        this.m_aiLoginSyncObject.notifyAll();
                        break block34;
                    }
                }
                if (string.equals("ErrorMessage")) {
                    Object object;
                    XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(element, this.m_payAccount, (AnonServerDescription)this.m_connectedCascade);
                    LogHolder.log(2, LogType.PAY, "For account " + this.m_payAccount.getAccountNumber() + ", processing AI ErrorMessage " + xMLErrorMessage.getXmlErrorCode() + ": " + xMLErrorMessage.getMessage());
                    if (xMLErrorMessage.getXmlErrorCode() == 18 && xMLErrorMessage.getService() != null && xMLErrorMessage.getService() instanceof MixCascade) {
                        object = ((MixCascade)xMLErrorMessage.getService()).getListenerInterfaces();
                        for (int i = 0; object != null && i < ((Vector)object).size(); ++i) {
                            ((ListenerInterface)((Vector)object).elementAt(i)).blockInterface(30000L);
                        }
                    }
                    if (xMLErrorMessage.getXmlErrorCode() == 10 && !this.checkAccountChanged()) {
                        object = PayAccountsFile.getInstance();
                        synchronized (object) {
                            this.updateBalance(this.m_payAccount, false);
                            PayAccount payAccount = PayAccountsFile.getInstance().getChargedAccount(this.m_connectedCascade.getPIID(), this.m_payAccount);
                            if (payAccount != null) {
                                PayAccountsFile.getInstance().setActiveAccount(payAccount);
                            } else {
                                if (!PayAccountsFile.getInstance().isAIAccountErrorIgnored()) {
                                    xMLErrorMessage = new NotRecoverableXMLError(xMLErrorMessage);
                                }
                                this.getServiceContainer().keepCurrentService(false);
                            }
                        }
                    } else if (xMLErrorMessage.getXmlErrorCode() == 21 && !this.checkAccountChanged()) {
                        this.m_payAccount.setBlocked(true);
                        object = PayAccountsFile.getInstance();
                        synchronized (object) {
                            PayAccount payAccount = PayAccountsFile.getInstance().getChargedAccount(this.m_connectedCascade.getPIID());
                            if (payAccount != null) {
                                PayAccountsFile.getInstance().setActiveAccount(payAccount);
                            } else {
                                if (!PayAccountsFile.getInstance().isAIAccountErrorIgnored()) {
                                    xMLErrorMessage = new NotRecoverableXMLError(xMLErrorMessage);
                                }
                                this.getServiceContainer().keepCurrentService(false);
                            }
                        }
                    } else {
                        this.getServiceContainer().keepCurrentService(false);
                    }
                    this.m_lastErrorMessage = xMLErrorMessage;
                    if (xMLErrorMessage instanceof NotRecoverableXMLError) {
                        xMLErrorMessage = ((NotRecoverableXMLError)xMLErrorMessage).getSource();
                    }
                    PayAccountsFile.getInstance().signalAccountError(xMLErrorMessage);
                } else if (string.equals("Challenge")) {
                    this.processChallenge(new XMLChallenge(element));
                } else if (string.equals("CC")) {
                    this.processInitialCC(new XMLEasyCC(element));
                } else {
                    LogHolder.log(4, LogType.PAY, "Received unknown payment control channel message '" + string + "'");
                }
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.PAY, exception);
                this.getServiceContainer().keepCurrentService(false);
                PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(6, exception.getClass().getName() + ": " + exception.getMessage(), this.m_payAccount, this.m_connectedCascade));
            }
        }
    }

    private synchronized void handlePrepaidBytesReceived(int n, PayAccount payAccount) {
        if (payAccount == null) {
            throw new NullPointerException("Active Account must not be null!");
        }
        if (n > 0 && !this.m_bPrepaidReceived) {
            this.m_prepaidBytes = n;
            PreviousPrepdaidBytes previousPrepdaidBytes = this.getPreviousPrepaidBytes();
            if (previousPrepdaidBytes != null && !previousPrepdaidBytes.m_dateTimeout.before(new Date())) {
                previousPrepdaidBytes.m_prepaidBytes = Math.max(0L, previousPrepdaidBytes.m_prepaidBytes - this.m_prepaidBytes);
            }
            this.m_bPrepaidReceived = true;
            payAccount.updateCurrentBytes(n * -1);
        }
    }

    private synchronized void processChallenge(XMLChallenge xMLChallenge) throws Exception {
        byte[] arrby = xMLChallenge.getChallengeForSigning();
        LogHolder.log(5, LogType.PAY, "Received " + xMLChallenge.getPrepaidBytes() + " prepaid bytes.");
        if (xMLChallenge.getType() != null && !xMLChallenge.getType().equals("Mix")) {
            throw new Exception("Challenge has wrong type: " + xMLChallenge.getType());
        }
        if (this.m_payAccount == null) {
            throw new Exception("Received Challenge from AI but ActiveAccount not set!");
        }
        if (!this.m_prepaidAmountInPayRequest) {
            this.handlePrepaidBytesReceived(xMLChallenge.getPrepaidBytes(), this.m_payAccount);
        }
        byte[] arrby2 = ByteSignature.sign(arrby, this.m_payAccount.getPrivateKey());
        XMLResponse xMLResponse = new XMLResponse(arrby2, null);
        this.sendXmlMessage(XMLUtil.toXMLDocument(xMLResponse));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean checkAccountChanged() {
        if (this.m_payAccount == null || this.m_payAccount != PayAccountsFile.getInstance().getActiveAccount()) {
            Vector vector = this.m_aiListeners;
            synchronized (vector) {
                LogHolder.log(4, LogType.PAY, "Active account has changed while connected to paid service!");
                for (int i = 0; i < this.m_aiListeners.size(); ++i) {
                    ((IAIEventListener)this.m_aiListeners.elementAt(i)).accountChanged(this.m_payAccount, this.m_connectedCascade);
                }
            }
            return true;
        }
        return false;
    }

    private synchronized void processPayRequest(XMLPayRequest xMLPayRequest) {
        if (this.checkAccountChanged()) {
            return;
        }
        if (xMLPayRequest.isInitialCCRequest()) {
            if (this.m_prepaidAmountInPayRequest) {
                this.handlePrepaidBytesReceived(xMLPayRequest.getPrepaidBytes(), this.m_payAccount);
            }
            this.processInitialCC(xMLPayRequest.getCC());
            return;
        }
        if (xMLPayRequest.isAccountRequest()) {
            try {
                this.sendAccountCert();
            }
            catch (AnonServiceException anonServiceException) {
                LogHolder.log(1, LogType.PAY, "Could not send account certificate!", anonServiceException);
            }
        }
        try {
            this.processCcToSign(xMLPayRequest.getCC());
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.PAY, exception);
        }
    }

    private void updateBalance(final PayAccount payAccount, final boolean bl) {
        if (payAccount == null) {
            return;
        }
        Runnable runnable = new Runnable(){

            public void run() {
                try {
                    if (bl) {
                        payAccount.fetchAccountInfo(false, 2000);
                    } else {
                        payAccount.fetchAccountInfo(false);
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(7, LogType.PAY, exception);
                }
            }
        };
        if (bl) {
            LogHolder.log(7, LogType.PAY, "Fetching new Balance from BI.");
            runnable.run();
        } else {
            LogHolder.log(7, LogType.PAY, "Fetching new Balance from BI asynchronously.");
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void processCcToSign(XMLEasyCC xMLEasyCC) throws Exception {
        long l;
        if (xMLEasyCC == null || xMLEasyCC.getConcatenatedPriceCertHashes() == null || !xMLEasyCC.getConcatenatedPriceCertHashes().equals(this.m_connectedCascade.getConcatenatedPriceCertHashes())) {
            return;
        }
        if (this.checkAccountChanged()) {
            throw new Exception("Active account has changed!");
        }
        if (this.m_payAccount == null || this.m_payAccount.getAccountNumber() != xMLEasyCC.getAccountNumber()) {
            throw new Exception("Received CC with wrong accountnumber");
        }
        this.m_payAccount.updateCurrentBytes(this.m_packetCounter);
        XMLEasyCC xMLEasyCC2 = this.m_payAccount.getAccountInfo().getCC(xMLEasyCC.getConcatenatedPriceCertHashes());
        long l2 = 0L;
        if (xMLEasyCC2 != null) {
            if (this.m_initialCC == null) {
                LogHolder.log(4, LogType.PAY, "No initial CC available! The Mix might have lost its CC.");
                if (this.m_prepaidBytes > 0L) {
                    this.m_payAccount.updateCurrentBytes(this.m_prepaidBytes);
                }
            } else {
                l2 = xMLEasyCC2.getTransferredBytes();
                LogHolder.log(7, LogType.PAY, "Transferred bytes of last CC: " + l2);
            }
        }
        m_totalBytes = l = this.m_payAccount.getCurrentBytes();
        long l3 = xMLEasyCC.getTransferredBytes() - l;
        long l4 = l3 - this.m_connectedCascade.getPrepaidInterval();
        if (l4 > 0L) {
            LogHolder.log(4, LogType.PAY, "Illegal number of prepaid bytes for signing. Difference/Spent/CC/PrevCC: " + l4 + "/" + l + "/" + xMLEasyCC.getTransferredBytes() + "/" + l2);
            if (l < 0L) {
                LogHolder.log(4, LogType.PAY, "The mix might have lost a CC. Resetting transferred bytes to zero for now...");
                this.m_payAccount.updateCurrentBytes(l * -1L);
                l = this.m_payAccount.getCurrentBytes();
            } else if (xMLEasyCC.getTransferredBytes() < l2) {
                LogHolder.log(4, LogType.PAY, "Requested less than confirmed before! Maybe a CC did get lost!");
            }
        }
        xMLEasyCC.setTransferredBytes(l + this.m_connectedCascade.getPrepaidInterval());
        if (xMLEasyCC.getTransferredBytes() > l2) {
            if (!this.m_bLastCCSignaled) {
                long l5 = xMLEasyCC.getTransferredBytes();
                long l6 = xMLEasyCC.getTransferredBytes();
                if (xMLEasyCC2 != null) {
                    l6 -= xMLEasyCC2.getTransferredBytes();
                }
                if (this.m_payAccount.getCurrentCredit() < l6) {
                    long l7 = this.m_payAccount.getCurrentCredit();
                    if (xMLEasyCC2 != null) {
                        l7 += xMLEasyCC2.getTransferredBytes();
                    }
                    xMLEasyCC.setTransferredBytes(l7);
                    xMLEasyCC.setLastCC(true);
                    this.m_bLastCCSignaled = true;
                    LogHolder.log(4, LogType.PAY, "For account " + this.m_payAccount.getAccountNumber() + ", " + l6 + " credits were requested. Should provide " + l5 + ", but can only provide " + l7 + ". Missing credits: " + (l5 - l7));
                }
            }
            xMLEasyCC.setPriceCerts(this.m_connectedCascade.getPriceCertificateHashes());
            xMLEasyCC.setPIID(this.m_payAccount.getAccountCertificate().getPIID());
            xMLEasyCC.setCascadeID(this.m_connectedCascade.getId());
            xMLEasyCC.sign(this.m_payAccount.getPrivateKey());
            if (this.m_payAccount.addCostConfirmation(xMLEasyCC, true) <= 0L) {
                LogHolder.log(4, LogType.PAY, "Added old cost confirmation!");
            }
        } else if (xMLEasyCC2 != null && this.m_initialCC != null) {
            xMLEasyCC = xMLEasyCC2;
        } else {
            LogHolder.log(0, LogType.PAY, "Creating zero CC!!");
            xMLEasyCC.setTransferredBytes(0L);
            xMLEasyCC.setPriceCerts(this.m_connectedCascade.getPriceCertificateHashes());
            xMLEasyCC.setPIID(this.m_payAccount.getAccountCertificate().getPIID());
            xMLEasyCC.setCascadeID(this.m_connectedCascade.getId());
            xMLEasyCC.sign(this.m_payAccount.getPrivateKey());
            this.m_payAccount.addCostConfirmation(xMLEasyCC, true);
        }
        if (this.m_initialCC == null) {
            LogHolder.log(5, LogType.PAY, "Seems to be the first connection to service. Setting initial CC to current CC...");
            this.m_initialCC = xMLEasyCC;
        }
        if (!this.m_payAccount.isCharged(new Timestamp(System.currentTimeMillis()))) {
            Vector vector = this.m_aiLoginSyncObject;
            synchronized (vector) {
                if (!this.m_bMultiplexerClosed) {
                    this.m_packetCountEmptyObserver.setEmpty();
                }
            }
        }
        this.sendXmlMessage(XMLUtil.toXMLDocument(xMLEasyCC));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendAccountCert() throws AnonServiceException {
        Object object;
        String string = null;
        Vector vector = this.m_connectedCascade.getPriceCertificates();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (this.m_payAccount != null) {
            throw new ProtocolViolationException(this.m_connectedCascade, "Account certificate already received!");
        }
        PayAccountsFile payAccountsFile = PayAccountsFile.getInstance();
        synchronized (payAccountsFile) {
            this.m_payAccount = PayAccountsFile.getInstance().getActiveAccount();
            if (this.m_payAccount == null || !this.m_payAccount.isCharged(timestamp) || this.m_payAccount.getBI() == null || !this.m_payAccount.getBI().getId().equals(this.m_connectedCascade.getPIID())) {
                Vector vector2;
                object = null;
                PayAccount payAccount = null;
                if (this.m_payAccount != null && this.m_payAccount.getCurrentSpent() == 0L) {
                    payAccount = PayAccountsFile.getInstance().getActiveAccount();
                }
                if ((vector2 = PayAccountsFile.getInstance().getAccounts(this.m_connectedCascade.getPIID())).size() > 0) {
                    for (int i = 0; i < vector2.size() && !((PayAccount)(object = (PayAccount)vector2.elementAt(i))).isCharged(timestamp); ++i) {
                        if (!(payAccount != null || this.m_payAccount != null && this.m_payAccount.getCurrentSpent() != 0L || ((PayAccount)object).hasExpired())) {
                            payAccount = object;
                        }
                        object = null;
                    }
                    if (object != null) {
                        PayAccountsFile.getInstance().setActiveAccount((PayAccount)object);
                    } else if (payAccount != null) {
                        PayAccountsFile.getInstance().setActiveAccount(payAccount);
                    }
                    PayAccount payAccount2 = PayAccountsFile.getInstance().getActiveAccount();
                    if (!(payAccount2 != null && payAccount2.isCharged(timestamp) && payAccount2.isSamePaymentInstance(this.m_connectedCascade.getPIID()) || vector2.size() <= 0)) {
                        LogHolder.log(4, LogType.PAY, "No charged account is available for connecting. Trying to update balances...");
                        for (int i = 0; i < vector2.size(); ++i) {
                            object = (PayAccount)vector2.elementAt(i);
                            if (((PayAccount)object).canDoMonthlyOverusage(timestamp)) {
                                PayAccountsFile.getInstance().setActiveAccount((PayAccount)object);
                                break;
                            }
                            if (((PayAccount)object).getBalance() != null && !((PayAccount)object).shouldUpdateAccountInfo()) continue;
                            this.updateBalance((PayAccount)object, false);
                            if (!((PayAccount)object).isCharged(timestamp)) continue;
                            PayAccountsFile.getInstance().setActiveAccount((PayAccount)object);
                            break;
                        }
                    }
                }
            }
            this.m_payAccount = PayAccountsFile.getInstance().getActiveAccount();
        }
        PayAccountsFile.getInstance().signalAccountRequest(this.m_connectedCascade);
        if (this.m_payAccount == null || this.m_payAccount.getCurrentCredit() <= 0L) {
            this.getServiceContainer().keepCurrentService(false);
            throw new XMLErrorMessage(10, "No active account is available! Cannot connect to paid cascade.", this.m_payAccount, this.m_connectedCascade);
        }
        if (vector.size() != this.m_connectedCascade.getNumberOfMixes()) {
            string = "Not all Mixes in cascade " + this.m_connectedCascade.getId() + " have price certs! " + "PriceCerts/MixIDs:" + vector.size() + "/" + this.m_connectedCascade.getNumberOfMixes();
        } else {
            for (int i = 0; i < this.m_connectedCascade.getNumberOfMixes(); ++i) {
                XMLPriceCertificate xMLPriceCertificate = (XMLPriceCertificate)vector.elementAt(i);
                String string2 = this.m_connectedCascade.getMixId(i);
                object = this.m_payAccount.getBI();
                if (!xMLPriceCertificate.verify((PaymentInstanceDBEntry)object)) {
                    string = "Price certificate of cascade " + this.m_connectedCascade.getId() + " for mix " + string2 + " cannot be verified for payment instance " + object + " (" + this.m_payAccount.getPIID() + ")" + "!";
                    break;
                }
                if (xMLPriceCertificate.getSubjectKeyIdentifier().equals(string2)) continue;
                string = "SKI in price certificate of cascade " + this.m_connectedCascade.getId() + " differs from Mix ID! SKI:" + xMLPriceCertificate.getSubjectKeyIdentifier() + " MixID: " + string2;
                break;
            }
        }
        if (string != null) {
            LogHolder.log(3, LogType.PAY, string);
            this.getServiceContainer().keepCurrentService(false);
            PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(17, string, this.m_payAccount, this.m_connectedCascade));
            throw new AnonServiceException(this.m_connectedCascade, string);
        }
        this.m_payAccount.resetCurrentBytes();
        this.sendXmlMessage(XMLUtil.toXMLDocument(this.m_payAccount.getAccountCertificate()));
        AnonServiceException anonServiceException = null;
        object = this.m_aiLoginSyncObject;
        synchronized (object) {
            LogHolder.log(6, LogType.PAY, "Performing new synchronous AI login");
            try {
                this.m_aiLoginSyncObject.wait(this.m_aiLogin_timeout);
            }
            catch (InterruptedException interruptedException) {
                anonServiceException = new ServiceInterruptedException(this.m_connectedCascade);
            }
            if (this.m_aiLoginSyncObject.size() <= 0) {
                anonServiceException = this.m_lastErrorMessage != null ? this.m_lastErrorMessage : new AnonServiceException(this.m_connectedCascade, "No login confirmation found!");
            }
            this.m_aiLoginSyncObject.removeAllElements();
        }
        if (anonServiceException != null) {
            throw anonServiceException;
        }
    }

    public static long getBytes() {
        return m_totalBytes;
    }

    private PreviousPrepdaidBytes getPreviousPrepaidBytes() {
        return (PreviousPrepdaidBytes)HASH_PREPAID_ON_SERVICES.get(this.m_connectedCascade.getConcatenatedPriceCertHashes() + this.m_payAccount.getAccountNumber());
    }

    private synchronized void processInitialCC(XMLEasyCC xMLEasyCC) {
        if (this.checkAccountChanged()) {
            return;
        }
        String string = "AI has sent an INVALID last cost confirmation.";
        if (xMLEasyCC.verify(this.m_payAccount.getPublicKey())) {
            try {
                long l;
                if (xMLEasyCC.getNrOfPriceCerts() != this.m_connectedCascade.getNrOfPriceCerts()) {
                    LogHolder.log(2, LogType.PAY, "number of price certificates in cost confirmation does not match number of price certs in cascade");
                    this.getServiceContainer().keepCurrentService(false);
                    PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(17, "AI sent CC will illegal number of price certs" + xMLEasyCC.getNrOfPriceCerts(), this.m_payAccount, this.m_connectedCascade));
                    return;
                }
                Hashtable hashtable = xMLEasyCC.getPriceCertHashes();
                Enumeration enumeration = this.m_connectedCascade.getPriceCertificateHashes().keys();
                Hashtable hashtable2 = this.m_connectedCascade.getPriceCertificateHashes();
                int n = 0;
                while (enumeration.hasMoreElements()) {
                    MixPosition mixPosition = (MixPosition)enumeration.nextElement();
                    String string2 = (String)hashtable2.get(mixPosition);
                    String string3 = (String)hashtable.get(mixPosition);
                    if (string3 == null || !string2.equals(string3)) {
                        String string4 = "AI sent CC with illegal price cert hash for mix " + (mixPosition.getPosition() + 1) + " (" + (n + 1) + ")" + "!";
                        if (string3 == null) {
                            string4 = string4 + " Price certificate for this Mix was not found in CC!";
                        }
                        LogHolder.log(4, LogType.PAY, string4);
                        this.getServiceContainer().keepCurrentService(false);
                        PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(17, string4, this.m_payAccount, this.m_connectedCascade));
                        return;
                    }
                    hashtable.remove(mixPosition);
                    ++n;
                }
                if (this.m_connectedCascade.getConcatenatedPriceCertHashes() == null || xMLEasyCC.getConcatenatedPriceCertHashes() == null || !this.m_connectedCascade.getConcatenatedPriceCertHashes().equals(xMLEasyCC.getConcatenatedPriceCertHashes())) {
                    PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(17, "Price certificate hashes for a CC for service " + this.m_connectedCascade.getName() + " cannot be verified!", this.m_payAccount, this.m_connectedCascade));
                    return;
                }
                LogHolder.log(7, LogType.PAY, "AI has sent a valid last cost confirmation. Adding it to account.");
                if (this.m_initialCC == null) {
                    this.m_payAccount.updateCurrentBytes(xMLEasyCC.getTransferredBytes());
                    this.m_initialCC = xMLEasyCC;
                } else {
                    l = xMLEasyCC.getTransferredBytes() - this.m_initialCC.getTransferredBytes();
                    LogHolder.log(4, LogType.PAY, "Updated initial CostConfirmation! Difference: " + l);
                    this.m_payAccount.updateCurrentBytes(l);
                }
                l = xMLEasyCC.getTransferredBytes();
                long l2 = this.m_payAccount.addCostConfirmation(xMLEasyCC, true);
                if (l2 < 0L) {
                    this.m_payAccount.updateCurrentBytes(l2);
                    LogHolder.log(4, LogType.PAY, "Received old cost confirmation!");
                } else if (l2 > 0L) {
                    LogHolder.log(4, LogType.PAY, "Restored lost cost confirmation!");
                }
                long l3 = this.m_payAccount.getCurrentBytes();
                long l4 = this.m_connectedCascade.getPrepaidInterval() - (l - l3);
                long l5 = xMLEasyCC.getTransferredBytes();
                XMLEasyCC xMLEasyCC2 = new XMLEasyCC(xMLEasyCC);
                if (l4 > 0L) {
                    PreviousPrepdaidBytes previousPrepdaidBytes = this.getPreviousPrepaidBytes();
                    if (previousPrepdaidBytes != null) {
                        if (previousPrepdaidBytes.m_dateTimeout.before(new Date())) {
                            HASH_PREPAID_ON_SERVICES.remove(this.m_connectedCascade.getConcatenatedPriceCertHashes() + this.m_payAccount.getAccountNumber());
                            previousPrepdaidBytes = null;
                        } else if (previousPrepdaidBytes.m_prepaidBytes + this.m_prepaidBytes + l4 > 4000000L) {
                            PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(6, "The service " + this.m_connectedCascade.getName() + " requests too many prepaid bytes. From previous logins, we " + "have:" + previousPrepdaidBytes.m_prepaidBytes + " Now additionally: " + l4, this.m_payAccount, this.m_connectedCascade));
                            return;
                        }
                    }
                    if (this.m_payAccount.getCurrentCredit() < l4) {
                        long l6 = l4;
                        l4 = this.m_payAccount.getCurrentCredit();
                        xMLEasyCC2.setLastCC(true);
                        this.m_bLastCCSignaled = true;
                        LogHolder.log(4, LogType.PAY, "For account " + this.m_payAccount.getAccountNumber() + ", " + l6 + " credits were requested. Should provide " + (l + l6) + ", but can only provide " + (l + l4) + ". Missing credits: " + (l6 - l4));
                    }
                    xMLEasyCC2.setTransferredBytes(l + l4);
                } else {
                    xMLEasyCC2.setTransferredBytes(l);
                }
                this.m_prepaidBytes += l4;
                xMLEasyCC2.sign(this.m_payAccount.getPrivateKey());
                if (l4 > 0L && this.m_payAccount.addCostConfirmation(xMLEasyCC2, true) <= 0L) {
                    LogHolder.log(4, LogType.PAY, "Sending old cost confirmation! Diff (ShouldBe)/Old/New:" + l4 + "/" + l5 + "/" + xMLEasyCC2.getTransferredBytes());
                }
                this.sendXmlMessage(XMLUtil.toXMLDocument(xMLEasyCC2));
                return;
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.PAY, string, exception);
            }
        } else {
            LogHolder.log(3, LogType.PAY, string);
        }
        this.getServiceContainer().keepCurrentService(false);
        PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(3, string, this.m_payAccount, this.m_connectedCascade));
    }

    protected void finalize() throws Throwable {
        this.m_packetCounter.deleteObserver(this.m_packetCountEmptyObserver);
        super.finalize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void multiplexerClosed() {
        Object object = this.m_aiLoginSyncObject;
        synchronized (object) {
            this.m_bMultiplexerClosed = true;
            this.m_packetCounter.deleteObserver(this.m_packetCountEmptyObserver);
            this.m_aiLoginSyncObject.notifyAll();
        }
        if (this.m_payAccount == null) {
            return;
        }
        object = this.m_payAccount.getAccountInfo();
        if (object == null) {
            return;
        }
        XMLEasyCC xMLEasyCC = ((XMLAccountInfo)object).getCC(this.m_connectedCascade.getConcatenatedPriceCertHashes());
        if (xMLEasyCC == null) {
            return;
        }
        long l = xMLEasyCC.getTransferredBytes() - this.m_payAccount.getCurrentBytes();
        if (l > 0L) {
            PreviousPrepdaidBytes previousPrepdaidBytes = this.getPreviousPrepaidBytes();
            if (previousPrepdaidBytes != null) {
                if (previousPrepdaidBytes.m_dateTimeout.before(new Date())) {
                    previousPrepdaidBytes.m_prepaidBytes = 0L;
                }
            } else {
                previousPrepdaidBytes = new PreviousPrepdaidBytes();
            }
            previousPrepdaidBytes.m_prepaidBytes += l;
            previousPrepdaidBytes.m_dateTimeout = new Date(System.currentTimeMillis() + 300000L);
            HASH_PREPAID_ON_SERVICES.put(this.m_connectedCascade.getConcatenatedPriceCertHashes() + this.m_payAccount.getAccountNumber(), previousPrepdaidBytes);
            int n = 5;
            if (previousPrepdaidBytes.m_prepaidBytes > this.m_connectedCascade.getPrepaidInterval()) {
                n = 4;
            }
            LogHolder.log(n, LogType.MISC, "Stored " + previousPrepdaidBytes.m_prepaidBytes + " prepaid bytes for account " + this.m_payAccount.getAccountNumber() + " with current bytes " + this.m_payAccount.getCurrentBytes() + " while transferred " + xMLEasyCC.getTransferredBytes() + " and reported prepaid " + this.m_prepaidBytes + " on service " + this.m_connectedCascade.getName() + " (" + this.m_connectedCascade.getConcatenatedPriceCertHashes() + ")" + " with prepaid interval " + this.m_connectedCascade.getPrepaidInterval());
        } else {
            LogHolder.log(1, LogType.MISC, "prepaid:" + this.m_prepaidBytes + " : " + l);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAILoginTimeout(int n) {
        Vector vector = this.m_aiLoginSyncObject;
        synchronized (vector) {
            this.m_aiLogin_timeout = n;
        }
    }

    public boolean isPrepaidAmountInPayRequest() {
        return this.m_prepaidAmountInPayRequest;
    }

    public void setPrepaidAmountInPayRequest(boolean bl) {
        this.m_prepaidAmountInPayRequest = bl;
    }

    private class PreviousPrepdaidBytes {
        private Date m_dateTimeout;
        private long m_prepaidBytes;

        private PreviousPrepdaidBytes() {
        }
    }

    private final class EmptyAccountPacketObserver
    implements Observer {
        private String m_concatenatedPCHashes;
        private boolean m_bEmpty = false;

        private EmptyAccountPacketObserver(String string) {
            this.m_concatenatedPCHashes = string;
        }

        public void setEmpty() {
            this.m_bEmpty = true;
        }

        public void update(Observable observable, Object object) {
            new Thread(new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    AIControlChannel aIControlChannel = AIControlChannel.this;
                    synchronized (aIControlChannel) {
                        Vector vector = AIControlChannel.this.m_aiLoginSyncObject;
                        synchronized (vector) {
                            if (!AIControlChannel.this.m_bEmptyMessageSent) {
                                try {
                                    AIControlChannel.this.m_payAccount.updateCurrentBytes(AIControlChannel.this.m_packetCounter);
                                    XMLEasyCC xMLEasyCC = AIControlChannel.this.m_payAccount.getAccountInfo().getCC(EmptyAccountPacketObserver.this.m_concatenatedPCHashes);
                                    long l = xMLEasyCC.getTransferredBytes() - AIControlChannel.this.m_payAccount.getCurrentBytes();
                                    long l2 = AIControlChannel.this.m_payAccount.getCurrentCreditCalculatedAlsoNegative();
                                    if (l2 < 0L) {
                                        l += l2;
                                    }
                                    if (EmptyAccountPacketObserver.this.m_bEmpty || l < 1000000L) {
                                        // empty if block
                                    }
                                    if (!EmptyAccountPacketObserver.this.m_bEmpty || l > 0L) {
                                        return;
                                    }
                                }
                                catch (Exception exception) {
                                    LogHolder.log(2, LogType.PAY, exception);
                                }
                            }
                            PayAccountsFile.getInstance().signalAccountError(new XMLErrorMessage(10, AIControlChannel.this.m_payAccount, (AnonServerDescription)AIControlChannel.this.m_connectedCascade));
                            Vector vector2 = AIControlChannel.this.m_aiListeners;
                            synchronized (vector2) {
                                for (int i = 0; i < AIControlChannel.this.m_aiListeners.size(); ++i) {
                                    ((IAIEventListener)AIControlChannel.this.m_aiListeners.elementAt(i)).accountEmpty(AIControlChannel.this.m_payAccount, AIControlChannel.this.m_connectedCascade);
                                }
                                AIControlChannel.this.m_bEmptyMessageSent = true;
                            }
                        }
                    }
                }
            }).start();
        }
    }
}

