/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.AnonServerDescription;
import anon.AnonServiceEventAdapter;
import anon.AnonServiceEventListener;
import anon.client.AbstractAutoSwitchedMixCascadeContainer;
import anon.client.AnonClient;
import anon.client.TrustModel;
import anon.crypto.ExpiredSignatureException;
import anon.crypto.JAPCertificate;
import anon.crypto.SignatureVerifier;
import anon.crypto.Util;
import anon.error.AnonServiceException;
import anon.error.INotRecoverableException;
import anon.error.ParseServiceException;
import anon.error.ServiceInterruptedException;
import anon.error.ServiceSignatureException;
import anon.error.ServiceUnreachableException;
import anon.error.TrustException;
import anon.error.UnknownProtocolVersionException;
import anon.forward.server.ForwardServerManager;
import anon.infoservice.BlacklistedCascadeIDEntry;
import anon.infoservice.CascadeIDEntry;
import anon.infoservice.Database;
import anon.infoservice.DatabaseMessage;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.IDistributable;
import anon.infoservice.IDistributor;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.InfoServiceHolderMessage;
import anon.infoservice.JAPMinVersion;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.MixCascade;
import anon.infoservice.MixCascadeExitAddresses;
import anon.infoservice.MixInfo;
import anon.infoservice.PreviouslyKnownCascadeIDEntry;
import anon.infoservice.ProxyInterface;
import anon.infoservice.ServiceOperator;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.infoservice.update.AccountUpdater;
import anon.infoservice.update.InfoServiceUpdater;
import anon.infoservice.update.JavaVersionUpdater;
import anon.infoservice.update.MessageUpdater;
import anon.infoservice.update.MinVersionUpdater;
import anon.infoservice.update.PaymentInstanceUpdater;
import anon.infoservice.update.PerformanceInfoUpdater;
import anon.infoservice.update.ServiceExitAddressUpdater;
import anon.mixminion.mmrdescription.MMRList;
import anon.pay.BIConnection;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLErrorMessage;
import anon.platform.AbstractOS;
import anon.platform.MacOS;
import anon.proxy.AnonProxy;
import anon.proxy.BrowserIdentification;
import anon.proxy.DirectProxy;
import anon.proxy.HTTPConnectionEvent;
import anon.proxy.HTTPProxyCallback;
import anon.proxy.HttpConnectionListenerAdapter;
import anon.proxy.IProxyListener;
import anon.proxy.JonDoFoxHeader;
import anon.proxy.JonDonymXHeaders;
import anon.terms.TermsAndConditionConfirmation;
import anon.terms.TermsAndConditions;
import anon.terms.TermsAndConditionsResponseHandler;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.transport.address.IAddress;
import anon.util.AbstractMemorizingPasswordReader;
import anon.util.Base64;
import anon.util.ClassUtil;
import anon.util.IMiscPasswordReader;
import anon.util.IPasswordReader;
import anon.util.IReturnRunnable;
import anon.util.IXMLEncodable;
import anon.util.IntegerVariable;
import anon.util.JAPMessages;
import anon.util.JobQueue;
import anon.util.RecursiveFileTool;
import anon.util.ResourceLoader;
import anon.util.SocketGuard;
import anon.util.StoredPasswordReader;
import anon.util.Updater;
import anon.util.XMLUtil;
import gui.GUIUtils;
import gui.dialog.DialogContentPane;
import gui.dialog.JAPDialog;
import gui.dialog.PasswordContentPane;
import gui.help.JAPHelp;
import jap.AbstractJAPMainView;
import jap.ConfigAssistant;
import jap.IJAPMainView;
import jap.ISplashResponse;
import jap.JAPAbout;
import jap.JAPAboutNew;
import jap.JAPConf;
import jap.JAPConfAnon;
import jap.JAPConfAnonGeneral;
import jap.JAPConfCert;
import jap.JAPConfUpdate;
import jap.JAPConstants;
import jap.JAPControllerMessage;
import jap.JAPDebug;
import jap.JAPFeedback;
import jap.JAPFirewallPasswdDlg;
import jap.JAPModel;
import jap.JAPNewView;
import jap.JAPObserver;
import jap.JAPSplash;
import jap.JAPViewIconified;
import jap.MixCascadeUpdater;
import jap.SoftwareUpdater;
import jap.TermsAndConditionsInfoDialog;
import jap.forward.JAPRoutingEstablishForwardedConnectionDialog;
import jap.forward.JAPRoutingMessage;
import jap.forward.JAPRoutingSettings;
import jap.pay.AccountCreator;
import jap.pay.PaymentMainPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import update.JAPWelcomeWizardPage;

public final class JAPController
extends Observable
implements IProxyListener,
Observer,
AnonServiceEventListener,
TermsAndConditionConfirmation {
    public static final String MSG_ERROR_SAVING_CONFIG = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_errorSavingConfig";
    public static final String MSG_NO_WRITING = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_noWriting";
    public static final String MSG_NO_WRITING_PORTABLE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_noWritingPortable";
    private static final String MSG_DIALOG_ACCOUNT_PASSWORD = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_dialog_account_password";
    private static final String MSG_ACCOUNT_PASSWORD = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_account_password";
    private static final String MSG_ENCRYPTACCOUNT = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_encryptaccount";
    private static final String MSG_ENCRYPTACCOUNTTITLE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_encryptaccounttitle";
    private static final String MSG_ACCPASSWORDTITLE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_accpasswordtitle";
    private static final String MSG_ACCPASSWORD = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_accpassword";
    private static final String MSG_ACCPASSWORDENTERTITLE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_accpasswordentertitle";
    private static final String MSG_ACCPASSWORDENTER = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_accpasswordenter";
    private static final String MSG_LOSEACCOUNTDATA = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_loseaccountdata";
    private static final String MSG_REPEAT_ENTER_ACCOUNT_PASSWORD = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_repeatEnterAccountPassword";
    private static final String MSG_DISABLE_GOODBYE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_disableGoodByMessage";
    private static final String MSG_NEW_OPTIONAL_VERSION = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_newOptionalVersion";
    private static final String MSG_CASCADE_NOT_TRUSTED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_cascadeNotTrusted";
    public static final String MSG_CASCADE_UNREACHABLE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".cascadeUnreachable";
    private static final String MSG_NOTHING_ANONYMIZED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".nothingAnonymized";
    private static final String MSG_FINISH_NEVERTHELESS = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".finishNevertheless";
    private static final String MSG_ALLOWUNPROTECTED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_allowunprotected";
    private static final String MSG_ANONYMITY_MODE_OFF = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".anonymityModeOff";
    private static final String MSG_ANONYMITY_MODE_NOT_YET_CONNECTED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".anonymityModeOffNotYetConnected";
    private static final String MSG_ALLOWUNPROTECTED_ALL = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_allowunprotectedAll";
    private static final String MSG_EXPLAIN_ALLOWUNPROTECTED_ALL = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_allowunprotectedAllExplain";
    private static final String MSG_BTN_BLOCK_REQUEST = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".btnBlockRequest";
    private static final String MSG_I_DO_NOT_KNOW = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".iDoNotKnow";
    public static final String MSG_IS_NOT_ALLOWED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_isNotAllowed";
    public static final String MSG_IS_NOT_ALLOWED_FOR_ANONYMOUS = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_isNotAllowedForAnonymous";
    public static final String MSG_ASK_SWITCH = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_askForSwitchOnError";
    public static final String MSG_ASK_RECONNECT = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_askForReconnectOnError";
    public static final String MSG_ASK_AUTO_CONNECT = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_reallyAutoConnect";
    public static final String MSG_FINISHING = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_finishing";
    public static final String MSG_SAVING_CONFIG = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_savingConfig";
    public static final String MSG_CLOSING_DIALOGS = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_closingDialogs";
    public static final String MSG_FINISHING_IS_UPDATES = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_finishISUpdates";
    public static final String MSG_FINISHING_ANON = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_finishAnon";
    public static final String MSG_WAITING_IS = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_waitingIS";
    public static final String MSG_WAITING_ANON = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_waitingAnon";
    public static final String MSG_STOPPING_PROXY = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_stoppingProxy";
    public static final String MSG_STOPPING_LISTENER = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_stoppingListener";
    public static final String MSG_RESTARTING = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_restarting";
    public static final String MSG_FINISH_FORWARDING_SERVER = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_finishForwardingServer";
    public static final String MSG_VERSION_RELEASE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_versionRelease";
    public static final String MSG_VERSION_DEVELOPER = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_versionDeveloper";
    public static final String MSG_ASK_WHICH_VERSION = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_askWhichVersion";
    private static final String MSG_CASCADE_NOT_PARSABLE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_cascadeNotParsable";
    public static final String MSG_PAYMENT_DAMAGED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_paymentDamaged";
    public static final String MSG_ACCOUNT_NOT_SAVED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_accountNotSaved";
    public static final String MSG_UPDATING_HELP = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_updatingHelp";
    public static final String MSG_FORWARDER_REGISTRATION_ERROR_HEADER = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_forwardErrorHead";
    public static final String MSG_FORWARDER_REGISTRATION_ERROR_FOOTER = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_forwardErrorFoot";
    public static final String MSG_FORWARDER_REG_ERROR_SHORT = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_forwardErrorShort";
    public static final String MSG_READ_NEW_HELP = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_readNewHelp";
    public static final String MSG_WARNING_IS_CERTS_EXPIRED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_warningISCertsExpired";
    public static final String MSG_WARNING_IS_CERTS_INVALID = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_warningISCertsInvalid";
    public static final String MSG_WARNING_INSUFFICIENT_BALANCE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_warningInsufficientBalance";
    public static final String MSG_WARNING_SHORT_BALANCE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_warningShortBalance";
    public static final String MSG_WARNING_SHORT_BALANCE_CONTINUE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_warningShortBalanceContinue";
    private static final String MSG_CONFIRM_IGNORE_WARNING_SHORT_BALANCE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".confirmIgnoreWarningShortBalance";
    private static final String MSG_WARNING_BROWSER_NOT_OPTIMIZED = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_browserNotOptimized";
    private static final String MSG_WARNING_CHECK_JONDOFOX = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".waringCheckJonDoFox";
    private static final String MSG_INTEGRITY_ERROR = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_integrityErrorMessage";
    private static final String MSG_BLACKLIST_CASCADE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_blacklistCascadeLabel";
    private static final String MSG_DIRECT_PROXY_CONFIRM_ONCE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".directProxyConfirmOnce";
    private static final String MSG_DIRECT_PROXY_CONFIRM_EVERY_PAGE = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + ".directProxyConfirmEveryPage";
    public static final String MSG_E_MAIL_JONDOS = (class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController).getName() + "_emailJonDos";
    private static final String XML_ELEM_LOOK_AND_FEEL = "LookAndFeel";
    private static final String XML_ELEM_LOOK_AND_FEELS = "LookAndFeels";
    private static final String XML_ATTR_LOOK_AND_FEEL = "current";
    private static final String XML_ALLOW_NON_ANONYMOUS_CONNECTION = "AllowDirectConnection";
    private static final String XML_ALLOW_NON_ANONYMOUS_UPDATE = "AllowDirectUpdate";
    private static final String XML_ATTR_AUTO_CHOOSE_CASCADES = "AutoSwitchCascades";
    private static final String XML_ATTR_HIDE_ANONYMITY_POPUPS = "hideAnonymityPopups";
    private static final String XML_ATTR_HIDE_PAYMENT_POPUPS = "hidePaymentPopups";
    private static final String XML_ATTR_HIDE_INFOSERVICE_POPUPS = "hideInfoServicePopups";
    private static final String XML_ATTR_SEND_SYSTEM_STATISTICS = "sendInfoServiceStatistics";
    private static final String XML_ATTR_AUTO_CHOOSE_CASCADES_ON_STARTUP = "autoSwitchCascadesOnStartup";
    private static final String XML_ATTR_SHOW_CONFIG_ASSISTANT = "showConfigAssistant";
    private static final String XML_ATTR_STARTUPS = "startups";
    private static final String XML_ATTR_LOGIN_TIMEOUT = "loginTimeout";
    private static final String XML_ATTR_INFOSERVICE_CONNECT_TIMEOUT = "infoServiceConnectionTimeout";
    private static final String XML_ATTR_ASK_SAVE_PAYMENT = "askIfNotSaved";
    private static final String XML_ATTR_SHOW_SPLASH_SCREEN = "ShowSplashScreen";
    private static final String XML_ATTR_PORTABLE_BROWSER_PATH = "portableBrowserPath";
    private static final String XML_ATTR_WARN_ON_INSECURE_BRWOSER = "warnInsecureBrowser";
    private static final String XML_ATTR_HAS_ANONYMIZED = "hasAnonymized";
    private static final String XML_SHOW_CONFIG_ASSISTANT = "ShowConfigAssistant";
    private static final String XML_ATTR_ENABLE_CLOSE_BUTTON = "enableCloseButton";
    private static final String XML_ATTR_HELP_PATH = "helpPath";
    private final String CLASS_PATH = ClassUtil.getClassPath().trim();
    private final Object PROXY_SYNC = new Object();
    private String[] m_commandLineArgs = new String[0];
    boolean m_firstPortableFFStart = false;
    private boolean m_bShutdown = false;
    private boolean m_bExternalShutdown = false;
    private Vector m_programExitListeners = new Vector();
    private boolean m_bShowConfigAssistant = false;
    private boolean m_bAssistantClicked = false;
    private long m_lAllowPaidServices = 0L;
    private boolean m_bShowHelpAdvise = false;
    private JobQueue m_anonJobQueue;
    private boolean m_bConnecting = false;
    private JobQueue queueFetchAccountInfo;
    private boolean m_bHideUpdateDialogs = false;
    private boolean m_bConnectionUnused = true;
    private long m_lStartupCounter = 0L;
    private static final long L_NON_ANONYMOUS_WARNING_LIMIT = 4L;
    private AutoSwitchedMixCascadeContainer m_mixContainer = null;
    private ServerSocket m_socketHTTPListener = null;
    private ServerSocket m_socketHTTPListenerTwo = null;
    private boolean m_bIsVirtualBoxListener = false;
    private DirectProxy m_proxyDirect = null;
    private AnonProxy m_proxyAnon = null;
    private Updater.ObservableInfo m_observableInfo;
    private AccountUpdater m_AccountUpdater;
    private AccountUpdater m_AccountUpdaterInternal;
    private InfoServiceUpdater m_InfoServiceUpdater;
    private ServiceExitAddressUpdater m_updaterExitAddress;
    private PaymentInstanceUpdater m_paymentInstanceUpdater;
    private MixCascadeUpdater m_MixCascadeUpdater;
    private MinVersionUpdater m_minVersionUpdater;
    private JavaVersionUpdater m_javaVersionUpdater;
    private MessageUpdater m_messageUpdater;
    private PerformanceInfoUpdater m_perfInfoUpdater;
    private Object LOCK_VERSION_UPDATE = new Object();
    private boolean m_bShowingVersionUpdate = false;
    private boolean m_bAskAutoConnect = false;
    private boolean isRunningHTTPListener = false;
    private boolean mbActCntMessageNotRemind = false;
    private boolean mbActCntMessageNeverRemind = false;
    private boolean mbDoNotAbuseReminder = false;
    private boolean m_bForwarderNotExplain = false;
    private boolean m_bExpiredISCertificatesShown = false;
    private final Object SYNC_EXPIRED_IS_CERTS = new Object();
    private static final Object SYNC_NON_ANONYMOUS_WARNING = new Object();
    private static boolean ms_bShowingAnonymousWarning = false;
    private final Object SYNC_DISCONNECTED_ERROR = new Object();
    private boolean m_bDisconnectedErrorShown = false;
    private boolean m_bAskSavePayment;
    private boolean m_bPresentationMode = false;
    private boolean m_bPortableJava = false;
    private boolean m_bPortable = false;
    private boolean m_bMultiple = false;
    private long m_nrOfBytesWWW = 0L;
    private long m_nrOfBytesOther = 0L;
    private boolean m_bAllowTorMixminion = false;
    private IJAPMainView m_View = null;
    private boolean m_bMainView = true;
    private Object SYNC_VIEW = new Object();
    private static JAPController m_Controller = null;
    private static JAPModel m_Model = null;
    private static JAPFeedback m_feedback = null;
    private RunnableShowConfigAssistant m_thRunnableShowConfigAssistant;
    private Vector observerVector = new Vector();
    private Vector m_anonServiceListener;
    private IPasswordReader m_passwordReader;
    private Object m_finishSync = new Object();
    private ISplashResponse m_finishSplash;
    private IRestarter m_restarter = new IRestarter(){

        public boolean hideWarnings() {
            return false;
        }

        public boolean isConfigFileSaved() {
            return true;
        }

        public void exec(String[] arrstring) throws IOException {
            if (arrstring != null) {
                Runtime.getRuntime().exec(arrstring);
            }
        }
    };
    private ConfigAssistantHttpListener m_httpListenerConfigAssistant;
    private final JonDoFoxHeader m_httpListenerJonDoFox = new JonDoFoxHeader(0);
    private final BrowserIdentification m_httpBrowserIdentification = new BrowserIdentification(-1);
    private final JonDonymXHeaders m_httpListenerXHeaders = new JonDonymXHeaders(-2);
    private boolean m_bInitialized = false;
    private int m_iStatusPanelMsgIdForwarderServerStatus;
    private boolean m_bPreloadCalled = false;
    private boolean m_bBlockDirectProxyTemp = false;
    private boolean m_bBlockDirectProxyPermanent = false;
    static /* synthetic */ Class class$jap$JAPController;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$PerformanceInfo;
    static /* synthetic */ Class class$anon$infoservice$CascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$PreviouslyKnownCascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;
    static /* synthetic */ Class class$anon$infoservice$JAPMinVersion;
    static /* synthetic */ Class class$anon$infoservice$MixCascadeExitAddresses;
    static /* synthetic */ Class class$anon$infoservice$BlacklistedCascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;
    static /* synthetic */ Class class$anon$infoservice$DeletedMessageIDDBEntry;
    static /* synthetic */ Class class$anon$infoservice$ClickedMessageIDDBEntry;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;
    static /* synthetic */ Class class$anon$terms$template$TermsAndConditionsTemplate;
    static /* synthetic */ Class class$java$net$InetAddress;
    static /* synthetic */ Class class$anon$infoservice$JAPVersionInfo;
    static /* synthetic */ Class class$anon$client$TrustModel$SpeedAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$DelayAttribute;

    private JAPController() {
        m_Model = JAPModel.getInstance();
    }

    public synchronized void initialize(boolean bl, boolean bl2) {
        if (this.m_bInitialized) {
            return;
        }
        this.m_bAllowTorMixminion = bl2;
        this.m_bPresentationMode = bl;
        Database.registerDistributor(new IDistributor(){

            public void addJob(IDistributable iDistributable) {
            }
        });
        InfoServiceDBEntry.setJVMNetworkErrorHandling(new Runnable(){

            public void run() {
                JAPController.goodBye(false);
            }
        }, 60000L);
        Updater.ObservableInfo observableInfo = new Updater.ObservableInfo(JAPModel.getInstance()){

            public Integer getUpdateChanged() {
                return JAPModel.CHANGED_INFOSERVICE_AUTO_UPDATE;
            }

            public boolean isUpdateDisabled() {
                return JAPModel.isInfoServiceDisabled();
            }

            public void notifyAdditionalObserversOnUpdate(Class class_) {
                if (class_ == null) {
                    throw new NullPointerException("No class given!");
                }
                if (class_ == (class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade)) {
                    JAPController.getInstance().notifyJAPObservers();
                } else if (class_ == (class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = JAPController.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo)) {
                    JAPController.getInstance().notifyJAPObservers();
                }
            }
        };
        this.m_observableInfo = new Updater.ObservableInfoContainer(observableInfo){

            public boolean updateImmediately() {
                return true;
            }
        };
        m_feedback = new JAPFeedback(this.m_observableInfo);
        this.m_InfoServiceUpdater = new InfoServiceUpdater(observableInfo);
        this.m_updaterExitAddress = new ServiceExitAddressUpdater(this.m_observableInfo);
        this.m_perfInfoUpdater = new PerformanceInfoUpdater(this.m_observableInfo);
        this.m_paymentInstanceUpdater = new PaymentInstanceUpdater(this.m_observableInfo);
        this.m_MixCascadeUpdater = new MixCascadeUpdater(observableInfo);
        this.m_minVersionUpdater = new MinVersionUpdater(observableInfo);
        this.m_javaVersionUpdater = new JavaVersionUpdater(observableInfo);
        this.m_messageUpdater = new MessageUpdater(observableInfo);
        this.m_anonJobQueue = new JobQueue("Anon mode job queue");
        m_Model.setAnonConnectionChecker(new AnonConnectionChecker());
        InfoServiceDBEntry.setMutableProxyInterface(m_Model.getInfoServiceProxyInterface());
        BIConnection.setMutableProxyInterface(m_Model.getPaymentProxyInterface());
        this.queueFetchAccountInfo = new JobQueue("FetchAccountInfoJobQueue");
        this.m_anonServiceListener = new Vector();
        try {
            this.m_mixContainer = new AutoSwitchedMixCascadeContainer();
            this.m_mixContainer.addObserver(this);
            Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPController.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry).update(new CascadeIDEntry(this.m_mixContainer.getCurrentCascade()));
            Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = JAPController.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).update(new PreviouslyKnownCascadeIDEntry(this.m_mixContainer.getCurrentCascade()));
        }
        catch (Exception exception) {
            LogHolder.log(0, LogType.NET, exception);
            System.exit(-1);
        }
        try {
            InfoServiceDBEntry[] arrinfoServiceDBEntry = JAPController.createDefaultInfoServices();
            for (int i = 0; i < arrinfoServiceDBEntry.length; ++i) {
                Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? JAPController.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).update(arrinfoServiceDBEntry[i]);
            }
            InfoServiceHolder.getInstance().setPreferredInfoService(arrinfoServiceDBEntry[0]);
        }
        catch (Exception exception) {
            LogHolder.log(0, LogType.NET, "JAPController: Constructor - default info service.", exception);
        }
        this.setInfoServiceDisabled(false);
        JAPController.addDefaultCertificates();
        SignatureVerifier.getInstance().setCheckSignatures(true);
        HTTPConnectionFactory.getInstance().setTimeout(30);
        this.m_passwordReader = new JAPFirewallPasswdDlg();
        JAPModel.getInstance().getRoutingSettings().addObserver(this);
        JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().addObserver(this);
        JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver().addObserver(this);
        m_Model.addObserver(this);
        Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = JAPController.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).addObserver(this);
        InfoServiceHolder.getInstance().addObserver(this);
        this.m_iStatusPanelMsgIdForwarderServerStatus = -1;
    }

    public static JAPController getInstance() {
        if (m_Controller == null) {
            m_Controller = new JAPController();
        }
        return m_Controller;
    }

    public IRestarter getRestarter() {
        return this.m_restarter;
    }

    public void setRestarter(IRestarter iRestarter) {
        if (iRestarter != null) {
            this.m_restarter = iRestarter;
        }
    }

    public void addProgramExitListener(ProgramExitListener programExitListener) {
        if (programExitListener != null && !this.m_programExitListeners.contains(programExitListener)) {
            this.m_programExitListeners.addElement(programExitListener);
        }
    }

    public void setPortableJava(boolean bl) {
        this.m_bPortableJava = bl;
    }

    public boolean hasPortableJava() {
        return this.m_bPortableJava;
    }

    public void hideUpdateDialogs() {
        this.m_bHideUpdateDialogs = true;
    }

    public boolean isHideUpdateDialogs() {
        return this.m_bHideUpdateDialogs;
    }

    public void setPortableMode(boolean bl) {
        this.m_bPortable = bl;
    }

    public boolean isPortableMode() {
        return this.m_bPortable;
    }

    public void setLauncher(String string) {
        m_Model.setLauncher(string);
    }

    public String getLauncher() {
        return m_Model.getLauncher();
    }

    public void setAllowMultipleInstances(boolean bl) {
        this.m_bMultiple = bl;
    }

    public boolean isMultipleInstancesAllowed() {
        return this.m_bMultiple;
    }

    public void initCommandLineArgs(String[] arrstring) {
        if (arrstring != null) {
            this.m_commandLineArgs = arrstring;
        }
    }

    public String[] getCommandlineArgs() {
        return this.m_commandLineArgs;
    }

    public IPasswordReader getPasswordReader() {
        return this.m_passwordReader;
    }

    public void initialRun(String string, int n) {
        LogHolder.log(6, LogType.MISC, "Initial run of JAP...");
        Database.getInstance(class$anon$infoservice$JAPMinVersion == null ? (class$anon$infoservice$JAPMinVersion = JAPController.class$("anon.infoservice.JAPMinVersion")) : class$anon$infoservice$JAPMinVersion).addObserver(this);
        Thread thread = new Thread(new Runnable(){

            public void run() {
                m_feedback.start(false);
                if (JAPModel.isInfoServiceDisabled()) {
                    JAPController.this.m_InfoServiceUpdater.start(false);
                    JAPController.this.m_perfInfoUpdater.start(false);
                    JAPController.this.m_paymentInstanceUpdater.start(false);
                    JAPController.this.m_MixCascadeUpdater.start(false);
                    JAPController.this.m_minVersionUpdater.start(false);
                    JAPController.this.m_javaVersionUpdater.start(false);
                    JAPController.this.m_messageUpdater.start(false);
                    JAPController.this.m_updaterExitAddress.start(false);
                } else {
                    Vector<AbstractDatabaseUpdater> vector = new Vector<AbstractDatabaseUpdater>();
                    vector.addElement(JAPController.this.m_updaterExitAddress);
                    vector.addElement(JAPController.this.m_perfInfoUpdater);
                    vector.addElement(JAPController.this.m_MixCascadeUpdater);
                    vector.addElement(JAPController.this.m_minVersionUpdater);
                    vector.addElement(JAPController.this.m_javaVersionUpdater);
                    vector.addElement(JAPController.this.m_messageUpdater);
                    if (!JAPController.this.m_InfoServiceUpdater.isFirstUpdateDone() || !JAPController.this.m_MixCascadeUpdater.isFirstUpdateDone()) {
                        JAPController.this.m_InfoServiceUpdater.updateAsync(vector);
                    }
                    if (!JAPController.this.m_paymentInstanceUpdater.isFirstUpdateDone()) {
                        JAPController.this.m_paymentInstanceUpdater.updateAsync(null);
                    }
                }
                if (!PayAccountsFile.getInstance().isBalanceAutoUpdateEnabled()) {
                    JAPController.this.m_AccountUpdater.start(false);
                    JAPController.this.m_AccountUpdaterInternal.update();
                } else {
                    JAPController.this.m_AccountUpdaterInternal.start(true);
                    JAPController.this.m_AccountUpdater.updateAsync(null);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        if (!this.startHTTPListener(string, n)) {
            Object[] arrobject = new Object[]{new Integer(n <= 0 ? JAPModel.getHttpListenerPortNumber() : n)};
            JAPDialog.showErrorDialog(this.getCurrentView(), JAPMessages.getString("errorListenerPort", arrobject) + "<br><br>" + JAPMessages.getString(JAPConf.MSG_READ_PANEL_HELP, new Object[]{JAPMessages.getString("confButton"), JAPMessages.getString("confListenerTab")}), (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("portlistener"){

                public boolean isOnTop() {
                    return true;
                }
            });
            this.stop();
            this.m_View.disableSetAnonMode();
            this.notifyJAPObservers();
        } else if (!SignatureVerifier.getInstance().isCheckSignatures()) {
            this.stop();
            if (!JAPModel.getInstance().isAnonymityPopupsHidden()) {
                JAPDialog.showWarningDialog(this.getCurrentView(), JAPMessages.getString(JAPConfCert.MSG_NO_CHECK_WARNING), (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("cert"){

                    public boolean isOnTop() {
                        return true;
                    }
                });
            }
        } else {
            if (JAPController.getInstance().isConfigAssistantShown() && !JAPDialog.isConsoleOnly() && !JAPModel.isSmallDisplay()) {
                this.showInstallationAssistant(0);
            }
            if (this.m_bAskAutoConnect && !JAPModel.getInstance().isAnonymityPopupsHidden()) {
                if (JAPDialog.showYesNoDialog(this.getCurrentView(), JAPMessages.getString(MSG_ASK_AUTO_CONNECT), (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("services_general"))) {
                    JAPModel.getInstance().setAutoConnect(true);
                } else {
                    JAPModel.getInstance().setAutoConnect(false);
                }
            }
            if (JAPModel.isAutoConnect()) {
                this.start();
            } else {
                m_Controller.stop();
            }
        }
        if (this.m_bShowHelpAdvise) {
            JAPDialog.showMessageDialog(this.getCurrentView(), JAPMessages.getString(MSG_READ_NEW_HELP, "00.20.001"));
            JAPHelp.getInstance().setContext("index", this.getCurrentView());
            JAPHelp.getInstance().setVisible(true);
        }
        this.m_thRunnableShowConfigAssistant = new RunnableShowConfigAssistant();
        this.m_thRunnableShowConfigAssistant.start();
    }

    public boolean isAskSavePayment() {
        return this.m_bAskSavePayment;
    }

    public void forceAnonymityTestRedirect(boolean bl) {
        this.m_httpListenerConfigAssistant.forceRedirect(bl);
    }

    public boolean hasAnonymityTestRedirected() {
        return this.m_httpListenerConfigAssistant.hasRedirected();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAskSavePayment(boolean bl) {
        JAPController jAPController = this;
        synchronized (jAPController) {
            if (this.m_bAskSavePayment != bl) {
                this.m_bAskSavePayment = bl;
                this.setChanged();
                this.notifyObservers(new JAPControllerMessage(3));
            }
        }
    }

    public boolean isShuttingDown() {
        return this.m_bShutdown;
    }

    public synchronized void loadConfigFile(String string, ISplashResponse iSplashResponse) {
        boolean bl = false;
        try {
            bl = this.lookForConfigFile(string, this.m_bPreloadCalled);
        }
        catch (FileNotFoundException fileNotFoundException) {
            // empty catch block
        }
        if (string != null) {
            JAPModel.getInstance().setConfigFile(string);
        } else if (!bl) {
            JAPModel.getInstance().setConfigFile(AbstractOS.getInstance().getConfigPath("JonDo", true) + "jap.conf");
        }
        Document document = null;
        if (bl) {
            try {
                document = XMLUtil.readXMLDocument(new File(JAPModel.getInstance().getConfigFile()));
            }
            catch (Exception exception) {
                LogHolder.log(5, LogType.MISC, "Error while loading the configuration file!");
            }
        }
        if (document == null) {
            document = XMLUtil.createDocument();
        }
        try {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            Object object5;
            Element element;
            Object object6;
            Element element2;
            block119: {
                String string2;
                Object object7;
                Object object8;
                Serializable serializable;
                Object object9;
                Object object10;
                String string3;
                Object object11;
                Object object12;
                Object object13;
                Element element3;
                element2 = document.getDocumentElement();
                XMLUtil.removeComments(element2);
                String string4 = XMLUtil.parseAttribute((Node)element2, "Locale", JAPMessages.getLocale().getLanguage());
                if (!JAPMessages.init(new Locale(string4, ""), "JAPMessages")) {
                    GUIUtils.exitWithNoMessagesError("MixConfigMessages");
                }
                if ((element3 = (Element)XMLUtil.getFirstChildByName(element2, "Debug")) != null) {
                    try {
                        object6 = (Element)XMLUtil.getFirstChildByName(element3, "Level");
                        JAPDebug.getInstance().setLogLevel(XMLUtil.parseValue((Node)object6, JAPDebug.getInstance().getLogLevel()));
                        object13 = (Element)XMLUtil.getFirstChildByName(element3, "Detail");
                        LogHolder.setDetailLevel(XMLUtil.parseValue((Node)object13, LogHolder.getDetailLevel()));
                        Element element4 = (Element)XMLUtil.getFirstChildByName(element3, "Type");
                        if (element4 != null) {
                            int n = LogType.NUL;
                            object12 = LogType.getAvailableLogTypes();
                            for (int i = 0; i < ((int[])object12).length; ++i) {
                                if (!XMLUtil.parseAttribute((Node)element4, LogType.getLogTypeName(object12[i]), true)) continue;
                                n |= object12[i];
                            }
                            JAPDebug.getInstance().setLogType(n);
                        }
                        if ((object11 = XMLUtil.getFirstChildByName(element3, "Output")) != null) {
                            new Thread(new Runnable((Node)object11){
                                private final /* synthetic */ Node val$elemOutput;
                                {
                                    this.val$elemOutput = node;
                                }

                                public void run() {
                                    JAPDebug.showConsole(XMLUtil.parseAttribute(this.val$elemOutput, "showWindow", false), JAPController.this.getViewWindow());
                                }
                            }).start();
                            object12 = XMLUtil.getLastChildByName((Node)object11, "File");
                            JAPDebug.setLogToFile(XMLUtil.parseValue((Node)object12, null));
                        }
                    }
                    catch (Exception exception) {
                        LogHolder.log(6, LogType.MISC, " Error loading Debug Settings.");
                    }
                }
                object6 = XMLUtil.parseAttribute((Node)element2, "version", null);
                m_Model.setDLLupdate(XMLUtil.parseAttribute((Node)element2, "dllVersionUpdate", null));
                m_Model.setDllWarningVersion(XMLUtil.parseAttribute((Node)element2, "dllWarningVersion", 0));
                m_Model.setMacOSXLibraryUpdateAtStartupNeeded(XMLUtil.parseAttribute((Node)element2, "macOSXLibNeedsUpdate", false));
                if (XMLUtil.parseAttribute((Node)element2, XML_ALLOW_NON_ANONYMOUS_UPDATE, true)) {
                    JAPModel.getInstance().setUpdateAnonymousConnectionSetting(XMLUtil.parseAttribute((Node)element2, XML_ALLOW_NON_ANONYMOUS_UPDATE, 0));
                } else {
                    JAPModel.getInstance().setUpdateAnonymousConnectionSetting(1);
                }
                JAPModel.getInstance().setDiableAllHeaderProcessing(XMLUtil.parseAttribute((Node)element2, "disableAllHeaderProcessing", false));
                JAPModel.getInstance().setAnonymizedHttpHeaders(XMLUtil.parseAttribute((Node)element2, "httpHeaderAnonymization", false));
                JAPModel.getInstance().setANONDebugMode(XMLUtil.parseAttribute((Node)element2, "enableANONDebugMode", false));
                JAPModel.getInstance().setShowConfigAssistantAutomatically(XMLUtil.parseValue(XMLUtil.getFirstChildByName(element2, XML_SHOW_CONFIG_ASSISTANT), true));
                JAPModel.getInstance().setReminderForOptionalUpdate(XMLUtil.parseAttribute((Node)element2, "remindOptionalUpdate", true));
                JAPModel.getInstance().setReminderForJavaUpdate(XMLUtil.parseAttribute((Node)element2, "remindJavaUpdate", !this.isPortableMode()));
                if (!this.isConfigAssistantShown()) {
                    this.setShowConfigAssistant(XMLUtil.parseAttribute((Node)element2, XML_ATTR_SHOW_CONFIG_ASSISTANT, false));
                }
                if (this.m_lStartupCounter < 4L) {
                    this.m_lStartupCounter = XMLUtil.parseAttribute((Node)element2, XML_ATTR_STARTUPS, 0);
                    ++this.m_lStartupCounter;
                }
                AnonClient.setLoginTimeout(XMLUtil.parseAttribute((Node)element2, XML_ATTR_LOGIN_TIMEOUT, 30000));
                InfoServiceDBEntry.setConnectionTimeout(XMLUtil.parseAttribute((Node)element2, XML_ATTR_INFOSERVICE_CONNECT_TIMEOUT, 15000));
                JAPModel.getInstance().setHideAnonymityPopups(XMLUtil.parseAttribute((Node)element2, XML_ATTR_HIDE_ANONYMITY_POPUPS, false));
                JAPModel.getInstance().setHidePaymentPopups(XMLUtil.parseAttribute((Node)element2, XML_ATTR_HIDE_PAYMENT_POPUPS, false));
                JAPModel.getInstance().setHideInfoServicePopups(XMLUtil.parseAttribute((Node)element2, XML_ATTR_HIDE_INFOSERVICE_POPUPS, false));
                InfoServiceDBEntry.setUseInfoServiceStatistics(XMLUtil.parseAttribute((Node)element2, XML_ATTR_SEND_SYSTEM_STATISTICS, true));
                JAPModel.getInstance().setCascadeAutoSwitch(XMLUtil.parseAttribute((Node)element2, XML_ATTR_AUTO_CHOOSE_CASCADES, true));
                JAPModel.getInstance().setAutoChooseCascadeOnStartup(XMLUtil.parseAttribute((Node)element2, XML_ATTR_AUTO_CHOOSE_CASCADES_ON_STARTUP, false));
                JAPModel.getInstance().setAskForAnyNonAnonymousRequest(XMLUtil.parseAttribute((Node)element2, "askForUnprotectedSurfing", true));
                JAPModel.getInstance().setNonAnonymousAllowed(XMLUtil.parseAttribute((Node)element2, "allowUnprotectedSurfing", JAPModel.getInstance().isNonAnonymousAllowed()));
                JAPModel.getInstance().initHelpPath(XMLUtil.restoreFilteredXMLChars(XMLUtil.parseAttribute((Node)element2, XML_ATTR_HELP_PATH, null)));
                if (!JAPDialog.isConsoleOnly()) {
                    object13 = iSplashResponse.getText();
                    iSplashResponse.setText(JAPMessages.getString(MSG_UPDATING_HELP));
                    try {
                        JAPModel.getInstance().getHelpURL();
                        if (!JAPModel.getInstance().isHelpPathDefined() && AbstractOS.getInstance().isHelpAutoInstalled() && !JAPDialog.isConsoleOnly()) {
                            JAPModel.getInstance().setHelpPath(new File(AbstractOS.getInstance().getDefaultHelpPath("JonDo")));
                        }
                    }
                    catch (Throwable throwable) {
                        LogHolder.log(2, LogType.MISC, "Error while installing help");
                        LogHolder.log(2, LogType.MISC, throwable);
                    }
                    iSplashResponse.setText((String)object13);
                }
                m_Model.setHttpListenerPortNumber(XMLUtil.parseAttribute((Node)element2, "portNumber", JAPModel.getHttpListenerPortNumber()));
                JAPModel.getInstance().setHttpListenerIsLocal(XMLUtil.parseAttribute((Node)element2, "listenerIsLocal", true));
                this.m_httpListenerConfigAssistant = new ConfigAssistantHttpListener(-14, XMLUtil.parseAttribute((Node)element2, XML_ATTR_HAS_ANONYMIZED, false));
                try {
                    this.mbActCntMessageNeverRemind = XMLUtil.parseAttribute((Node)element2, "neverRemindActiveContent", false);
                    this.mbDoNotAbuseReminder = XMLUtil.parseAttribute((Node)element2, "doNotAbuseReminder", false);
                    if (this.mbActCntMessageNeverRemind && this.mbDoNotAbuseReminder) {
                        this.mbActCntMessageNotRemind = true;
                    }
                    m_Model.setNeverRemindGoodbye(XMLUtil.parseAttribute((Node)element2, "neverRemindGoodBye", false));
                    this.m_bForwarderNotExplain = XMLUtil.parseAttribute((Node)element2, "neverExplainForward", false);
                }
                catch (Exception exception) {
                    LogHolder.log(6, LogType.MISC, "Error loading reminder message ins setAnonMode.");
                }
                boolean bl2 = XMLUtil.parseAttribute((Node)element2, "infoServiceDisabled", JAPModel.isInfoServiceDisabled());
                this.setInfoServiceDisabled(bl2);
                int n = XMLUtil.parseAttribute((Node)element2, "infoserviceTimeout", 10);
                try {
                    if (n >= 1 && n <= 60) {
                        HTTPConnectionFactory.getInstance().setTimeout(n);
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(6, LogType.MISC, "Error loading InfoService timeout.");
                }
                object11 = null;
                try {
                    object12 = XMLUtil.parseAttribute((Node)element2, "proxyType", "HTTP/TCP");
                    if (((String)object12).equalsIgnoreCase("HTTP")) {
                        object12 = "HTTP/TCP";
                    } else if (((String)object12).equalsIgnoreCase("SOCKS")) {
                        object12 = "socks";
                    }
                    JAPModel.getInstance().setUseProxyAuthentication(XMLUtil.parseAttribute((Node)element2, "proxyAuthorization", false));
                    String string5 = XMLUtil.parseAttribute((Node)element2, "proxyAuthPassword", null);
                    object11 = new ProxyInterface(XMLUtil.parseAttribute((Node)element2, "proxyHostName", null), XMLUtil.parseAttribute((Node)element2, "proxyPortNumber", -1), (String)object12, XMLUtil.parseAttribute((Node)element2, "proxyAuthUserID", null), string5 != null ? new StoredPasswordReader(string5.toCharArray()) : this.getPasswordReader(), JAPModel.getInstance().isProxyAuthenticationUsed(), XMLUtil.parseAttribute((Node)element2, "proxyMode", false));
                }
                catch (Exception exception) {
                    LogHolder.log(5, LogType.NET, "Could not load proxy settings!", exception);
                }
                this.changeProxyInterface((ProxyInterface)object11, XMLUtil.parseAttribute((Node)element2, "proxyAuthorization", false), this.getCurrentView());
                this.setDummyTraffic(XMLUtil.parseAttribute((Node)element2, "DummytrafficInterval", 20000));
                JAPModel.getInstance().setAutoConnect(XMLUtil.parseAttribute((Node)element2, "autoconnect", true));
                this.setInterfaceBlockTimeout(XMLUtil.parseAttribute((Node)element2, "InterfaceBlockTimeout", 180000L));
                m_Model.setAutoReConnect(XMLUtil.parseAttribute((Node)element2, "autoReconnect", true));
                m_Model.setMinimizeOnStartup(XMLUtil.parseAttribute((Node)element2, "minimizedStartup", false));
                try {
                    object12 = (Element)XMLUtil.getFirstChildByName(element2, SignatureVerifier.getXmlSettingsRootNodeName());
                    if (object12 == null) {
                        throw new Exception("No SignatureVerification node found. Using default settings for signature verification.");
                    }
                    Hashtable<Integer, Integer> hashtable = new Hashtable<Integer, Integer>();
                    hashtable.put(new Integer(7), new Integer(7));
                    SignatureVerifier.getInstance().loadSettingsFromXml((Element)object12, hashtable);
                }
                catch (Exception exception) {
                    LogHolder.log(4, LogType.MISC, exception);
                }
                Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = JAPController.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "ExitAddressesList"));
                Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPController.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "BlacklistedCascades"));
                BlacklistedCascadeIDEntry.putNewCascadesInBlacklist(false);
                boolean bl3 = true;
                Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).randomize();
                Node node = XMLUtil.getFirstChildByName(element2, "MixCascades");
                MixCascade mixCascade = null;
                if (node != null) {
                    string3 = null;
                    for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                        if (!node2.getNodeName().equals("MixCascade")) continue;
                        try {
                            mixCascade = new MixCascade((Element)node2, Long.MAX_VALUE);
                            string3 = mixCascade.getContext();
                            if (m_Model.getContext().startsWith("jondonym") && string3.equals("jondonym.premium") || string3.equals(m_Model.getContext())) {
                                try {
                                    if (!mixCascade.isPayment()) {
                                        bl3 = true;
                                    }
                                    Database.getInstance(class$anon$infoservice$MixCascade == null ? JAPController.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).update(mixCascade);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? JAPController.class$("anon.infoservice.CascadeIDEntry") : class$anon$infoservice$CascadeIDEntry).update(new CascadeIDEntry(mixCascade));
                                continue;
                            }
                            LogHolder.log(5, LogType.MISC, "No service context match " + string3 + "." + mixCascade.getName());
                            continue;
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }
                TrustModel.setFreeAllowed(bl3);
                boolean bl4 = XMLUtil.parseAttribute(XMLUtil.getFirstChildByName(element2, "BlacklistedCascades"), "autoBlacklistNewCascades", false);
                BlacklistedCascadeIDEntry.putNewCascadesInBlacklist(bl4);
                Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPController.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "KnownCascades"));
                Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = JAPController.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "PreviouslyKnownCascades"));
                TrustModel.fromXmlElement((Element)XMLUtil.getFirstChildByName(element2, "TrustModels"));
                Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPController.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "MixCascadeStatusList"));
                Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = JAPController.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "PerformanceInfoList"), true);
                Database.getInstance(class$anon$infoservice$DeletedMessageIDDBEntry == null ? (class$anon$infoservice$DeletedMessageIDDBEntry = JAPController.class$("anon.infoservice.DeletedMessageIDDBEntry")) : class$anon$infoservice$DeletedMessageIDDBEntry).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "DeletedMessageIDEntries"));
                Database.getInstance(class$anon$infoservice$ClickedMessageIDDBEntry == null ? (class$anon$infoservice$ClickedMessageIDDBEntry = JAPController.class$("anon.infoservice.ClickedMessageIDDBEntry")) : class$anon$infoservice$ClickedMessageIDDBEntry).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, "ClickedMessageIDDBEntries"));
                if (!JAPModel.isSmallDisplay() && !JAPDialog.isConsoleOnly()) {
                    JAPModel.getInstance().updateSystemLookAndFeels();
                }
                if ((string3 = XMLUtil.parseAttribute((Node)element2, XML_ELEM_LOOK_AND_FEEL, null)) == null) {
                    object10 = XMLUtil.getFirstChildByName(element2, XML_ELEM_LOOK_AND_FEELS);
                    string3 = XMLUtil.parseAttribute((Node)object10, XML_ATTR_LOOK_AND_FEEL, "unknown");
                    if (object10 != null) {
                        object9 = ((Element)object10).getElementsByTagName(XML_ELEM_LOOK_AND_FEEL);
                        for (int i = 0; i < object9.getLength(); ++i) {
                            try {
                                serializable = new File(XMLUtil.parseValue(object9.item(i), null));
                                try {
                                    if (!JAPModel.isSmallDisplay() && !JAPDialog.isConsoleOnly() && GUIUtils.registerLookAndFeelClasses(serializable).size() <= 0) continue;
                                    JAPModel.getInstance().addLookAndFeelFile((File)serializable);
                                }
                                catch (IllegalAccessException illegalAccessException) {
                                    JAPModel.getInstance().addLookAndFeelFile((File)serializable);
                                }
                                continue;
                            }
                            catch (Exception exception) {
                                LogHolder.log(3, LogType.MISC, "Error while parsing Look&Feels!");
                            }
                        }
                    }
                }
                if (!JAPModel.isSmallDisplay() && !JAPDialog.isConsoleOnly()) {
                    object10 = UIManager.getInstalledLookAndFeels();
                    for (n = 0; n < ((UIManager.LookAndFeelInfo[])object10).length; ++n) {
                        if (!object10[n].getName().equals(string3) && !object10[n].getClassName().equals(string3)) continue;
                        try {
                            UIManager.setLookAndFeel(object10[n].getClassName());
                        }
                        catch (Throwable throwable) {
                            try {
                                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                            }
                            catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                            }
                            catch (IllegalAccessException illegalAccessException) {
                            }
                            catch (InstantiationException instantiationException) {
                            }
                            catch (ClassNotFoundException classNotFoundException) {
                                // empty catch block
                            }
                            LogHolder.log(4, LogType.GUI, "Exception while setting look-and-feel '" + object10[n].getClassName() + "'");
                        }
                        break;
                    }
                    JAPModel.getInstance().setLookAndFeel(UIManager.getLookAndFeel().getClass().getName());
                }
                object10 = (Element)XMLUtil.getFirstChildByName(element2, "GUI");
                JAPModel.getInstance().setFontSize(XMLUtil.parseAttribute((Node)object10, "fontSize", JAPModel.getInstance().getFontSize()));
                JAPDialog.setOptimizedFormat(XMLUtil.parseAttribute((Node)object10, "dialogOptFormat", JAPDialog.getOptimizedFormat()));
                JAPModel.getInstance().setFontSize(XMLUtil.parseAttribute((Node)element2, "fontSize", JAPModel.getInstance().getFontSize()));
                JAPDialog.setOptimizedFormat(XMLUtil.parseAttribute((Node)element2, "dialogOptFormat", JAPDialog.getOptimizedFormat()));
                serializable = new Point(0, 0);
                Node node3 = XMLUtil.getFirstChildByName((Node)object10, "ConfigWindow");
                Node node4 = XMLUtil.getFirstChildByName(node3, "Size");
                if (!JAPDialog.isConsoleOnly()) {
                    object8 = new Dimension();
                    object7 = this.parseWindowSize(node3, (Dimension)object8, false, false);
                    JAPModel.getInstance().setSaveConfigWindowSize(object7 != null);
                    if (object7 == null) {
                        object7 = this.parseWindowSize(node3, (Dimension)object8, false, true);
                    }
                    if (object7 != object8) {
                        JAPModel.getInstance().setConfigSize((Dimension)object7);
                    }
                }
                object9 = this.parseWindowLocation(node3, (Point)serializable, false);
                JAPModel.getInstance().setSaveConfigWindowPosition(object9 != null);
                if (object9 != serializable) {
                    JAPModel.getInstance().setConfigWindowLocation((Point)object9);
                }
                node3 = XMLUtil.getFirstChildByName((Node)object10, "IconifiedWindow");
                JAPModel.getInstance().setMiniViewOnTop(XMLUtil.parseAttribute(node3, "alwaysOnTop", true));
                node4 = XMLUtil.getFirstChildByName(node3, "Size");
                if (!JAPDialog.isConsoleOnly()) {
                    JAPModel.getInstance().setIconifiedSize(new Dimension(XMLUtil.parseAttribute(node4, "width", 0), XMLUtil.parseAttribute(node4, "height", 0)));
                }
                object9 = this.parseWindowLocation(node3, (Point)serializable, true);
                JAPModel.getInstance().setSaveIconifiedWindowPosition(object9 != null);
                if (object9 != serializable) {
                    JAPModel.getInstance().setIconifiedWindowLocation((Point)object9);
                }
                node3 = XMLUtil.getFirstChildByName((Node)object10, "HelpWindow");
                object9 = this.parseWindowLocation(node3, (Point)serializable, false);
                JAPModel.getInstance().setSaveHelpWindowPosition(object9 != null);
                if (object9 != serializable) {
                    JAPModel.getInstance().setHelpWindowLocation((Point)object9);
                }
                if (!JAPDialog.isConsoleOnly()) {
                    object8 = new Dimension();
                    object7 = this.parseWindowSize(node3, (Dimension)object8, false, false);
                    JAPModel.getInstance().setSaveHelpWindowSize(object7 != null);
                    if (object7 != object8) {
                        JAPModel.getInstance().setHelpWindowSize((Dimension)object7);
                    }
                }
                object8 = (Element)XMLUtil.getFirstChildByName((Node)object10, "MainWindow");
                object9 = this.parseWindowLocation((Node)object8, (Point)serializable, true);
                JAPModel.getInstance().setSaveMainWindowPosition(object9 != null);
                if (object9 != serializable) {
                    JAPModel.getInstance().setMainWindowLocation((Point)object9);
                }
                object7 = (Element)XMLUtil.getFirstChildByName((Node)object8, "MoveToSystray");
                bl2 = XMLUtil.parseValue((Node)object7, false);
                this.setMoveToSystrayOnStartup(bl2);
                JAPModel.getInstance().setShowCloseButton(XMLUtil.parseAttribute((Node)object8, XML_ATTR_ENABLE_CLOSE_BUTTON, false));
                object7 = (Element)XMLUtil.getFirstChildByName((Node)object8, "StartPortableFirefox");
                JAPModel.getInstance().setStartPortableFirefox(XMLUtil.parseValue((Node)object7, true));
                object7 = (Element)XMLUtil.getFirstChildByName((Node)object8, "DefaultView");
                if (this.isConfigAssistantShown()) {
                    string2 = XMLUtil.parseValue((Node)object7, "Simplified");
                } else {
                    string2 = XMLUtil.parseValue((Node)object7, "Normal");
                    if (object7 == null) {
                        this.m_bShowHelpAdvise = true;
                    }
                }
                if (string2.equals("Simplified")) {
                    this.setDefaultView(2);
                } else {
                    this.setDefaultView(1);
                }
                try {
                    element = (Element)XMLUtil.getFirstChildByName(element2, InfoServiceHolder.getXmlSettingsRootNodeName());
                    if (XMLUtil.parseAttribute((Node)element, XML_ALLOW_NON_ANONYMOUS_CONNECTION, true)) {
                        JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(XMLUtil.parseAttribute((Node)element, XML_ALLOW_NON_ANONYMOUS_CONNECTION, 0));
                    } else {
                        JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(1);
                    }
                    if (element == null) {
                        throw new Exception("No InfoServiceManagement node found. Using default settings for infoservice management in InfoServiceHolder.");
                    }
                    InfoServiceHolder.getInstance().loadSettingsFromXml(element, true);
                }
                catch (Exception exception) {
                    LogHolder.log(4, LogType.MISC, exception);
                }
                try {
                    element = (Element)XMLUtil.getFirstChildByName(element2, "Payment");
                    if (XMLUtil.parseAttribute((Node)element, XML_ALLOW_NON_ANONYMOUS_CONNECTION, true)) {
                        JAPModel.getInstance().setPaymentAnonymousConnectionSetting(XMLUtil.parseAttribute((Node)element, XML_ALLOW_NON_ANONYMOUS_CONNECTION, 0));
                    } else {
                        JAPModel.getInstance().setPaymentAnonymousConnectionSetting(1);
                    }
                    this.m_bAskSavePayment = XMLUtil.parseAttribute((Node)element, XML_ATTR_ASK_SAVE_PAYMENT, true);
                    BIConnection.setConnectionTimeout(XMLUtil.parseAttribute((Node)element, "timeout", 40000));
                    object5 = (Element)XMLUtil.getFirstChildByName(element, "PayAccounts");
                    Node node5 = XMLUtil.getFirstChildByName(element, "PaymentInstances");
                    if (node5 != null) {
                        for (object4 = node5.getFirstChild(); object4 != null; object4 = object4.getNextSibling()) {
                            if (!object4.getNodeName().equals("PaymentInstance")) continue;
                            try {
                                PaymentInstanceDBEntry paymentInstanceDBEntry = new PaymentInstanceDBEntry((Element)object4, Long.MAX_VALUE);
                                if (!paymentInstanceDBEntry.isVerified()) continue;
                                Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? JAPController.class$("anon.pay.PaymentInstanceDBEntry") : class$anon$pay$PaymentInstanceDBEntry).update(paymentInstanceDBEntry);
                                continue;
                            }
                            catch (Exception exception) {
                                LogHolder.log(3, LogType.MISC, exception);
                            }
                        }
                    }
                    object3 = null;
                    if (JAPDialog.isConsoleOnly()) {
                        object4 = new IMiscPasswordReader(){

                            public String readPassword(Object object) {
                                return null;
                            }
                        };
                    } else {
                        JAPDialog.LinkedInformationAdapter linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                            public boolean isOnTop() {
                                return true;
                            }
                        };
                        object2 = iSplashResponse instanceof Component ? (Component)((Object)iSplashResponse) : new Frame();
                        object = new JAPDialog((Component)object2, "JAP: " + JAPMessages.getString(MSG_ACCPASSWORDENTERTITLE), true);
                        ((JAPDialog)object).setResizable(false);
                        ((JAPDialog)object).setAlwaysOnTop(true);
                        object3 = object;
                        ((JAPDialog)object).setDefaultCloseOperation(1);
                        PasswordContentPane passwordContentPane = new PasswordContentPane((JAPDialog)object, 2, JAPMessages.getString(MSG_ACCPASSWORDENTER, new Long(Long.MAX_VALUE)));
                        passwordContentPane.updateDialog();
                        ((JAPDialog)object).pack();
                        object4 = new AbstractMemorizingPasswordReader((JAPDialog)object, iSplashResponse, linkedInformationAdapter){
                            private PasswordContentPane panePassword;
                            private final /* synthetic */ JAPDialog val$dialog;
                            private final /* synthetic */ ISplashResponse val$a_splash;
                            private final /* synthetic */ JAPDialog.LinkedInformationAdapter val$onTopAdapter;
                            {
                                this.val$dialog = jAPDialog;
                                this.val$a_splash = iSplashResponse;
                                this.val$onTopAdapter = linkedInformationAdapter;
                            }

                            protected void initPasswordDialog(Object object) {
                                this.panePassword = new PasswordContentPane(this.val$dialog, 2, JAPMessages.getString(MSG_ACCPASSWORDENTER, object));
                                this.panePassword.setDefaultButtonOperation(1);
                            }

                            protected String readPassword() {
                                return this.panePassword.readPassword(null);
                            }

                            protected boolean askForCancel() {
                                return JAPDialog.showYesNoDialog((Component)((Object)this.val$a_splash), JAPMessages.getString(MSG_LOSEACCOUNTDATA), (JAPDialog.ILinkedInformation)this.val$onTopAdapter);
                            }
                        };
                    }
                    boolean bl5 = PayAccountsFile.init((Element)object5, (IMiscPasswordReader)object4, true, 1, new PayAccountsFile.IAffiliateOptOut(){

                        public boolean isAffiliateAllowed() {
                            return JAPModel.getInstance().getPaymentAnonymousConnectionSetting() != 1;
                        }
                    });
                    this.m_AccountUpdater = new AccountUpdater(false);
                    this.m_AccountUpdaterInternal = new AccountUpdater(true);
                    if (object3 != null) {
                        ((JAPDialog)object3).dispose();
                    }
                    if (object4 instanceof AbstractMemorizingPasswordReader && ((AbstractMemorizingPasswordReader)object4).countCmpletedObjects() > 0) {
                        this.setPaymentPassword(new String(((AbstractMemorizingPasswordReader)object4).getPassword()));
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(1, LogType.PAY, "Error loading Payment configuration.", exception);
                    if (JAPDialog.isConsoleOnly()) {
                        LogHolder.log(1, LogType.PAY, "Exiting...");
                        System.exit(1);
                    }
                    if (JAPDialog.showConfirmDialog((Component)new Frame(), JAPMessages.getString(MSG_PAYMENT_DAMAGED), 0, 0, (JAPDialog.ILinkedInformation)new JAPDialog.LinkedInformationAdapter(){

                        public boolean isOnTop() {
                            return true;
                        }
                    }) == 0) break block119;
                    System.exit(1);
                }
            }
            PayAccountsFile.init(null, null, true, 1, new PayAccountsFile.IAffiliateOptOut(){

                public boolean isAffiliateAllowed() {
                    return JAPModel.getInstance().getPaymentAnonymousConnectionSetting() != 1;
                }
            });
            PayAccountsFile.getInstance();
            try {
                element = (Element)XMLUtil.getFirstChildByName(element2, "TORSettings");
                if (!this.isTorMixminionAllowed()) {
                    JAPModel.getInstance().setTorActivated(false);
                } else {
                    JAPModel.getInstance().setTorActivated(XMLUtil.parseAttribute((Node)element, "activated", false));
                }
                object5 = (Element)XMLUtil.getFirstChildByName(element, "MaxConnectionsPerRoute");
                JAPController.setTorMaxConnectionsPerRoute(XMLUtil.parseValue((Node)object5, JAPModel.getTorMaxConnectionsPerRoute()));
                object5 = (Element)XMLUtil.getFirstChildByName(element, "RouteLen");
                int n = XMLUtil.parseAttribute((Node)object5, "min", JAPModel.getTorMinRouteLen());
                int n2 = XMLUtil.parseAttribute((Node)object5, "max", JAPModel.getTorMaxRouteLen());
                JAPController.setTorRouteLen(n, n2);
                object5 = (Element)XMLUtil.getFirstChildByName(element, "PreCreateAnonRoutes");
                JAPController.setPreCreateAnonRoutes(XMLUtil.parseValue((Node)object5, JAPModel.isPreCreateAnonRoutesEnabled()));
                object5 = (Element)XMLUtil.getFirstChildByName(element, "DirectoryServer");
                JAPController.setTorUseNoneDefaultDirServer(XMLUtil.parseAttribute((Node)object5, "useNoneDefault", JAPModel.isTorNoneDefaultDirServerEnabled()));
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, "Error loading Tor configuration.", exception);
            }
            try {
                element = (Element)XMLUtil.getFirstChildByName(element2, "MixMinion");
                if (!this.isTorMixminionAllowed()) {
                    JAPModel.getInstance().setMixMinionActivated(false);
                } else {
                    JAPModel.getInstance().setMixMinionActivated(XMLUtil.parseAttribute((Node)element, "activated", false));
                }
                object5 = (Element)XMLUtil.getFirstChildByName(element, "RouteLen");
                int n = XMLUtil.parseValue((Node)object5, JAPModel.getMixminionRouteLen());
                JAPModel.getInstance().setMixminionRouteLen(n);
                Element element5 = (Element)XMLUtil.getFirstChildByName(element, "MixminionREPLYMail");
                object4 = XMLUtil.parseAttribute((Node)element5, "MixminionSender", "");
                if ((object6 == null || ((String)object6).compareTo("00.20.001") < 0) && ((String)object4).equals("none")) {
                    object4 = "";
                }
                JAPModel.getInstance().setMixminionMyEMail((String)object4);
                object3 = (Element)XMLUtil.getFirstChildByName(element, "MixminionPasswordHash");
                String string6 = XMLUtil.parseValue((Node)object3, (String)null);
                if (string6 != null) {
                    JAPModel.getInstance().setMixinionPasswordHash(Base64.decode(string6));
                }
                object2 = (Element)XMLUtil.getFirstChildByName(element, "MixminionKeyring");
                object = XMLUtil.parseValue((Node)object2, "");
                JAPModel.getInstance().setMixminionKeyring((String)object);
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, "Error loading Mixminion configuration.", exception);
            }
            element = (Element)XMLUtil.getFirstChildByName(element2, "JapForwardingSettings");
            if (element != null) {
                JAPModel.getInstance().getRoutingSettings().loadSettingsFromXml(element);
            } else {
                LogHolder.log(4, LogType.MISC, "No JapForwardingSettings node found. Using default settings for forwarding.");
            }
            if (JAPModel.getInstance().isCascadeAutoSwitched() && JAPModel.getInstance().isCascadeAutoChosenOnStartup()) {
                this.m_mixContainer.getNextRandomCascade();
            } else {
                object5 = (Element)XMLUtil.getFirstChildByName(element2, "MixCascade");
                try {
                    if (!this.m_mixContainer.setCurrentCascade(new MixCascade((Element)object5, Long.MAX_VALUE))) {
                        this.m_mixContainer.getNextCascade();
                    } else if (this.m_mixContainer.getCurrentCascade().getNumberOfOperators() <= 1 && JAPModel.getInstance().isCascadeAutoSwitched()) {
                        this.m_mixContainer.getNextRandomCascade();
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.DB, exception);
                    this.m_mixContainer.getNextCascade();
                }
            }
            object5 = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_mixContainer.getCurrentCascade().getId());
            if (object5 != null) {
                this.m_mixContainer.setCurrentCascade((MixCascade)object5);
            }
            Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).update(this.m_mixContainer.getCurrentCascade());
            Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPController.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry).update(new CascadeIDEntry(this.m_mixContainer.getCurrentCascade()));
            TermsAndConditions.loadTermsAndConditionsFromXMLElement((Element)XMLUtil.getFirstChildByName(element2, "TermsAndConditionsList"));
            Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? (class$anon$terms$template$TermsAndConditionsTemplate = JAPController.class$("anon.terms.template.TermsAndConditionsTemplate")) : class$anon$terms$template$TermsAndConditionsTemplate).loadFromXml((Element)XMLUtil.getFirstChildByName(element2, TermsAndConditionsTemplate.XML_ELEMENT_CONTAINER_NAME));
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Error loading configuration! ", exception);
        }
        this.notifyJAPObservers();
    }

    public void uninstall(String string) throws IOException {
        try {
            while (this.lookForConfigFile(string, false)) {
                if (JAPModel.getInstance().getConfigFile() == null) {
                    LogHolder.log(1, LogType.MISC, "Config file found, but path was not set in model!");
                    break;
                }
                File file = new File(JAPModel.getInstance().getConfigFile());
                if (file.exists()) {
                    Document document;
                    try {
                        document = XMLUtil.readXMLDocument(file);
                        if (document == null) {
                            throw new IOException("Error while loading the configuration file!");
                        }
                    }
                    catch (Exception exception) {
                        throw new IOException(exception.getMessage());
                    }
                    Element element = document.getDocumentElement();
                    JAPModel.getInstance().initHelpPath(XMLUtil.restoreFilteredXMLChars(XMLUtil.parseAttribute((Node)element, XML_ATTR_HELP_PATH, null)));
                    JAPModel.getInstance().resetHelpPath();
                    try {
                        file.delete();
                        continue;
                    }
                    catch (SecurityException securityException) {
                        throw new IOException(securityException.getMessage());
                    }
                }
                LogHolder.log(1, LogType.MISC, "Config file found but does not exist!");
                break;
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            // empty catch block
        }
        String string2 = AbstractOS.getInstance().getAppdataDefaultDirectory("JonDo", false);
        if (string2 != null) {
            File file = new File(string2);
            File file2 = ClassUtil.getClassDirectory(class$jap$JAPController == null ? (class$jap$JAPController = JAPController.class$("jap.JAPController")) : class$jap$JAPController);
            if (file.exists() && file.isDirectory() && file.getPath().indexOf("JonDo") >= 0 && (file2 == null || !file2.equals(file))) {
                RecursiveFileTool.deleteRecursion(file);
            } else {
                LogHolder.log(1, LogType.MISC, "There was a problem while deleting the app data directory: " + file);
            }
        }
    }

    public void preLoadConfigFile(String string) throws FileNotFoundException {
        this.m_bPreloadCalled = true;
        if (this.lookForConfigFile(string, false)) {
            String string2 = "";
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(m_Model.getConfigFile()));
                while ((string2 = bufferedReader.readLine()) != null && string2.indexOf("<JAP") < 0) {
                }
                if (string2 == null) {
                    LogHolder.log(2, LogType.MISC, "Unable to pre-load config file " + m_Model.getConfigFile() + ".");
                    return;
                }
                int n = string2.indexOf("?>");
                if (n >= 0) {
                    string2 = string2.substring(n + 2, string2.length());
                }
                if (string2.indexOf("</JAP>") < 0) {
                    n = string2.indexOf(">");
                    if (n <= 0 || string2.length() < n + 1) {
                        LogHolder.log(2, LogType.MISC, "Unable to pre-load config file " + m_Model.getConfigFile() + ". Invalid XML structure.");
                        return;
                    }
                    string2 = string2.substring(0, n + 1);
                    string2 = string2 + "</JAP>";
                }
                Document document = XMLUtil.toXMLDocument(string2);
                m_Model.setShowSplashScreen(XMLUtil.parseAttribute((Node)document, XML_ATTR_SHOW_SPLASH_SCREEN, true));
                m_Model.setPortableBrowserpath(XMLUtil.parseAttribute((Node)document, XML_ATTR_PORTABLE_BROWSER_PATH, null));
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, "Unable to pre-load config file " + m_Model.getConfigFile() + ".", exception);
            }
        }
    }

    private boolean lookForConfigFile(String string, boolean bl) throws FileNotFoundException {
        boolean bl2 = false;
        if (string != null && !(bl2 = this.loadConfigFileCommandLine(string))) {
            this.setShowConfigAssistant(true);
            this.m_lStartupCounter = 4L;
            throw new FileNotFoundException("Could not initialise with specified config file: " + string);
        }
        if (!bl2) {
            bl2 = this.loadConfigFileOSdependent(bl);
        }
        if (!bl2) {
            bl2 = this.loadConfigFileHome(bl);
        }
        if (!bl2) {
            bl2 = this.loadConfigFileCurrentDir(bl);
        }
        if (!bl2) {
            m_Model.setConfigFile(AbstractOS.getInstance().getConfigPath("JonDo", true) + "jap.conf");
            this.setShowConfigAssistant(true);
            this.m_lStartupCounter = 4L;
        }
        return bl2;
    }

    private boolean loadConfigFileCommandLine(String string) {
        LogHolder.log(6, LogType.MISC, "Trying to load configuration from: " + string);
        try {
            FileInputStream fileInputStream = new FileInputStream(string);
            JAPModel.getInstance().setConfigFile(string);
            try {
                fileInputStream.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Configuration file \"" + string + "\" not found.");
            return false;
        }
    }

    private Dimension parseWindowSize(Node node, Dimension dimension, boolean bl, boolean bl2) {
        Element element = (Element)XMLUtil.getFirstChildByName(node, "Size");
        Dimension dimension2 = new Dimension();
        boolean bl3 = XMLUtil.parseAttribute((Node)element, "save", bl);
        dimension2.width = XMLUtil.parseAttribute((Node)element, "width", 0);
        dimension2.height = XMLUtil.parseAttribute((Node)element, "height", 0);
        if (dimension2.width <= 0 || dimension2.height <= 0 || !bl3 && !bl2) {
            if (bl3) {
                return dimension;
            }
            return null;
        }
        return dimension2;
    }

    private Point parseWindowLocation(Node node, Point point, boolean bl) {
        Element element = (Element)XMLUtil.getFirstChildByName(node, "Location");
        boolean bl2 = XMLUtil.parseAttribute((Node)element, "save", bl);
        if (element == null || element.getAttribute("x") == null || element.getAttribute("x").trim().length() == 0 || element.getAttribute("y") == null || element.getAttribute("y").trim().length() == 0 || !bl2) {
            if (bl2) {
                return point;
            }
            return null;
        }
        Point point2 = new Point();
        point2.x = XMLUtil.parseAttribute((Node)element, "x", 0);
        point2.y = XMLUtil.parseAttribute((Node)element, "y", 0);
        return point2;
    }

    private boolean loadConfigFileOSdependent(boolean bl) {
        String string = AbstractOS.getInstance().getConfigPath("JonDo", true) + "jap.conf";
        LogHolder.log(6, LogType.MISC, "Trying to load configuration from: " + string);
        try {
            FileInputStream fileInputStream = new FileInputStream(string);
            JAPModel.getInstance().setConfigFile(string);
            try {
                fileInputStream.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(bl ? 7 : 3, LogType.MISC, "JAPController: loadConfigFileOSdependent: Configuration file \"" + string + "\" not found.");
            return false;
        }
    }

    private boolean loadConfigFileHome(boolean bl) {
        String string = System.getProperty("user.home", "") + File.separator + "jap.conf";
        LogHolder.log(6, LogType.MISC, "JAPController: loadConfigFile: Trying to load configuration from: " + string);
        try {
            FileInputStream fileInputStream = new FileInputStream(string);
            JAPModel.getInstance().setConfigFile(string);
            try {
                fileInputStream.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(bl ? 7 : 3, LogType.MISC, "JAPController: loadConfigFile: Configuration file \"" + string + "\" not found.");
            return false;
        }
    }

    private boolean loadConfigFileCurrentDir(boolean bl) {
        String string = "jap.conf";
        LogHolder.log(6, LogType.MISC, "JAPController: loadConfigFile: Trying to load configuration from: " + string);
        try {
            FileInputStream fileInputStream = new FileInputStream(string);
            JAPModel.getInstance().setConfigFile(string);
            try {
                fileInputStream.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(bl ? 7 : 3, LogType.MISC, "JAPController: loadConfigFile: Configuration file \"" + string + "\" not found.");
            return false;
        }
    }

    private void restartJAP() {
        MacOS macOS = AbstractOS.getInstance() instanceof MacOS ? (MacOS)AbstractOS.getInstance() : null;
        String[] arrstring = new String[4 + this.m_commandLineArgs.length];
        if (this.m_commandLineArgs.length > 0) {
            System.arraycopy(this.m_commandLineArgs, 0, arrstring, 4, this.m_commandLineArgs.length);
        }
        if (m_Model.getLauncher() != null) {
            arrstring[0] = m_Model.getLauncher();
            arrstring[1] = "";
            arrstring[2] = "";
            arrstring[3] = "";
            try {
                this.m_restarter.exec(arrstring);
                return;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        arrstring[2] = this.CLASS_PATH;
        arrstring[3] = macOS != null ? "JAPMacintosh" : "JAP";
        String string = System.getProperty("java.vendor");
        String string2 = null;
        String string3 = null;
        String string4 = "";
        if (string.toLowerCase().indexOf("microsoft") != -1) {
            string4 = System.getProperty("com.ms.sysdir") + File.separator;
            string2 = "jview";
            string3 = "/cp";
        } else {
            string4 = AbstractOS.getInstance().getProperty("java.home") + File.separator + "bin" + File.separator;
            string2 = "javaw";
            string3 = "-cp";
        }
        boolean bl = macOS != null ? macOS.isBundle() : false;
        try {
            arrstring[0] = string4 + string2;
            arrstring[1] = string3;
            if (!bl) {
                this.m_restarter.exec(arrstring);
            } else {
                String[] arrstring2 = new String[]{"open", "-n", macOS.getBundlePath()};
                this.m_restarter.exec(arrstring2);
            }
        }
        catch (Exception exception) {
            string2 = "java";
            string3 = "-cp";
            arrstring[0] = string4 + string2;
            arrstring[1] = string3;
            try {
                this.m_restarter.exec(arrstring);
            }
            catch (Exception exception2) {
                LogHolder.log(2, LogType.ALL, "Error auto-restart JAP: " + exception);
            }
        }
    }

    public synchronized void changeProxyInterface(ProxyInterface proxyInterface, boolean bl, Component component) {
        if (!anon.util.Util.equals(m_Model.getProxyInterface(), proxyInterface)) {
            boolean bl2 = false;
            if (m_Model.getProxyInterface() == null) {
                if (proxyInterface.isValid()) {
                    bl2 = true;
                }
            } else if (proxyInterface == null) {
                if (m_Model.getProxyInterface().isValid()) {
                    bl2 = true;
                }
            } else if (m_Model.getProxyInterface().isValid() || proxyInterface.isValid()) {
                bl2 = true;
            }
            m_Model.setProxyListener(proxyInterface);
            this.applyProxySettingsToInfoService(bl);
            if (bl2) {
                this.applyProxySettingsToAnonService(component);
            }
            this.notifyJAPObservers();
        }
    }

    public boolean saveConfigFile() {
        boolean bl = false;
        LogHolder.log(6, LogType.MISC, "Try saving configuration.");
        if (this.m_httpBrowserIdentification.isJonDoFoxDetected()) {
            JAPModel.getInstance().setNeverRemindGoodbye(true);
        }
        try {
            Document document = this.getConfigurationAsXmlString();
            if (document == null) {
                LogHolder.log(3, LogType.MISC, "Could not transform the configuration to a string.");
                bl = true;
            } else {
                String string = JAPModel.getInstance().getConfigFile();
                LogHolder.log(6, LogType.MISC, "Configuration created, now saving to " + string + "...");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                XMLUtil.write(document, byteArrayOutputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(string);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.close();
                LogHolder.log(6, LogType.MISC, "Configuration was save to file: " + string);
            }
        }
        catch (Throwable throwable) {
            LogHolder.log(3, LogType.MISC, throwable);
            bl = true;
        }
        return bl;
    }

    private void addWindowLocationToConf(Element element, Point point) {
        if (element != null) {
            Element element2 = element.getOwnerDocument().createElement("Location");
            element.appendChild(element2);
            XMLUtil.setAttribute(element2, "save", point != null);
            if (point != null) {
                XMLUtil.setAttribute(element2, "x", Integer.toString(point.x));
                XMLUtil.setAttribute(element2, "y", Integer.toString(point.y));
            }
        }
    }

    private void addWindowSizeToConf(Element element, Dimension dimension, boolean bl) {
        if (element != null) {
            Element element2 = element.getOwnerDocument().createElement("Size");
            element.appendChild(element2);
            XMLUtil.setAttribute(element2, "save", dimension != null && bl);
            if (dimension != null) {
                XMLUtil.setAttribute(element2, "width", Integer.toString(dimension.width));
                XMLUtil.setAttribute(element2, "height", Integer.toString(dimension.height));
            }
        }
    }

    private Document getConfigurationAsXmlString() {
        try {
            Element element;
            Element element2;
            Element element3;
            IXMLEncodable iXMLEncodable;
            Document document = XMLUtil.createDocument();
            Element element4 = document.createElement("JAP");
            document.appendChild(element4);
            XMLUtil.setAttribute(element4, "version", "00.20.001");
            if (m_Model.getDllUpdatePath() != null) {
                XMLUtil.setAttribute(element4, "dllVersionUpdate", m_Model.getDllUpdatePath());
            }
            XMLUtil.setAttribute(element4, "macOSXLibNeedsUpdate", m_Model.isMacOSXLibraryUpdateAtStartupNeeded());
            XMLUtil.setAttribute(element4, "dllWarningVersion", m_Model.getDLLWarningVersion());
            if (this.m_bExternalShutdown) {
                XMLUtil.setAttribute(element4, "externalShutdownDetected", true);
            }
            XMLUtil.setAttribute(element4, "httpHeaderAnonymization", JAPModel.getInstance().isAnonymizedHttpHeaders());
            XMLUtil.setAttribute(element4, "disableAllHeaderProcessing", JAPModel.getInstance().isHeaderProcessingDisabled());
            XMLUtil.setAttribute(element4, "enableANONDebugMode", JAPModel.getInstance().isANONDebugMode());
            Element element5 = document.createElement(XML_SHOW_CONFIG_ASSISTANT);
            XMLUtil.setValue((Node)element5, JAPModel.getInstance().isConfigAssistantAutomaticallyShown());
            element4.appendChild(element5);
            XMLUtil.setAttribute(element4, XML_ATTR_HIDE_ANONYMITY_POPUPS, JAPModel.getInstance().isAnonymityPopupsHidden());
            XMLUtil.setAttribute(element4, XML_ATTR_HIDE_PAYMENT_POPUPS, JAPModel.getInstance().isPaymentPopupsHidden());
            XMLUtil.setAttribute(element4, XML_ATTR_HIDE_INFOSERVICE_POPUPS, JAPModel.getInstance().isInfoServicePopupsHidden());
            XMLUtil.setAttribute(element4, XML_ATTR_SEND_SYSTEM_STATISTICS, InfoServiceDBEntry.isInfoServiceStatisticsUsed());
            XMLUtil.setAttribute(element4, XML_ALLOW_NON_ANONYMOUS_UPDATE, JAPModel.getInstance().getUpdateAnonymousConnectionSetting());
            XMLUtil.setAttribute(element4, "remindOptionalUpdate", JAPModel.getInstance().isReminderForOptionalUpdateActivated());
            XMLUtil.setAttribute(element4, "remindJavaUpdate", JAPModel.getInstance().isReminderForJavaUpdateActivated());
            XMLUtil.setAttribute(element4, XML_ATTR_AUTO_CHOOSE_CASCADES, JAPModel.getInstance().isCascadeAutoSwitched());
            XMLUtil.setAttribute(element4, XML_ATTR_AUTO_CHOOSE_CASCADES_ON_STARTUP, JAPModel.getInstance().isCascadeAutoChosenOnStartup());
            XMLUtil.setAttribute(element4, "askForUnprotectedSurfing", JAPModel.getInstance().isAskForAnyNonAnonymousRequest());
            XMLUtil.setAttribute(element4, "allowUnprotectedSurfing", JAPModel.getInstance().isNonAnonymousAllowed());
            XMLUtil.setAttribute(element4, XML_ATTR_SHOW_CONFIG_ASSISTANT, this.isConfigAssistantShown());
            XMLUtil.setAttribute(element4, XML_ATTR_STARTUPS, Math.min(this.m_lStartupCounter, 4L));
            XMLUtil.setAttribute(element4, XML_ATTR_SHOW_SPLASH_SCREEN, m_Model.getShowSplashScreen());
            if (!(m_Model.getPortableBrowserpath() == null || AbstractOS.getInstance().getDefaultBrowserPath() != null && AbstractOS.toAbsolutePath(AbstractOS.getInstance().getDefaultBrowserPath()).equals(AbstractOS.toAbsolutePath(m_Model.getPortableBrowserpath())))) {
                XMLUtil.setAttribute(element4, XML_ATTR_PORTABLE_BROWSER_PATH, m_Model.getPortableBrowserpath());
            }
            XMLUtil.setAttribute(element4, XML_ATTR_LOGIN_TIMEOUT, AnonClient.getLoginTimeout());
            XMLUtil.setAttribute(element4, XML_ATTR_INFOSERVICE_CONNECT_TIMEOUT, InfoServiceDBEntry.getConnectionTimeout());
            if (JAPModel.getInstance().isHelpPathDefined() && JAPModel.getInstance().isHelpPathChangeable()) {
                XMLUtil.setAttribute(element4, XML_ATTR_HELP_PATH, XMLUtil.filterXMLChars(JAPModel.getInstance().getHelpPath()));
            }
            try {
                iXMLEncodable = PayAccountsFile.getInstance();
                if (iXMLEncodable != null) {
                    Element element6 = document.createElement("Payment");
                    XMLUtil.setAttribute(element6, XML_ALLOW_NON_ANONYMOUS_CONNECTION, JAPModel.getInstance().getPaymentAnonymousConnectionSetting());
                    XMLUtil.setAttribute(element6, "timeout", BIConnection.getConnectionTimeout());
                    XMLUtil.setAttribute(element6, XML_ATTR_ASK_SAVE_PAYMENT, this.m_bAskSavePayment);
                    element4.appendChild(element6);
                    element6.appendChild(Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = JAPController.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).toXmlElement(document, "PaymentInstances"));
                    element6.appendChild(((PayAccountsFile)iXMLEncodable).toXmlElement(document, this.getPaymentPassword(), false));
                }
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, "Error saving payment configuration", exception);
                return null;
            }
            XMLUtil.setAttribute(element4, "portNumber", JAPModel.getHttpListenerPortNumber());
            XMLUtil.setAttribute(element4, "listenerIsLocal", JAPModel.isHttpListenerLocal());
            XMLUtil.setAttribute(element4, XML_ATTR_HAS_ANONYMIZED, this.m_httpListenerConfigAssistant.hasVisitedWebsite());
            iXMLEncodable = m_Model.getProxyInterface();
            boolean bl = iXMLEncodable != null && ((ProxyInterface)iXMLEncodable).isValid();
            XMLUtil.setAttribute(element4, "proxyMode", bl);
            if (iXMLEncodable != null) {
                XMLUtil.setAttribute(element4, "proxyType", m_Model.getProxyInterface().getProtocolAsString().toUpperCase());
                XMLUtil.setAttribute(element4, "proxyHostName", m_Model.getProxyInterface().getHost());
                XMLUtil.setAttribute(element4, "proxyPortNumber", m_Model.getProxyInterface().getPort());
                XMLUtil.setAttribute(element4, "proxyAuthorization", m_Model.getProxyInterface().isAuthenticationUsed());
                XMLUtil.setAttribute(element4, "proxyAuthUserID", m_Model.getProxyInterface().getAuthenticationUserID());
                if (m_Model.getProxyInterface().isAuthenticationPasswordSaveable()) {
                    XMLUtil.setAttribute(element4, "proxyAuthPassword", m_Model.getProxyInterface().getAuthenticationPassword());
                }
            }
            XMLUtil.setAttribute(element4, "infoServiceDisabled", JAPModel.isInfoServiceDisabled());
            XMLUtil.setAttribute(element4, "infoserviceTimeout", HTTPConnectionFactory.getInstance().getTimeout());
            XMLUtil.setAttribute(element4, "DummytrafficInterval", JAPModel.getDummyTraffic());
            XMLUtil.setAttribute(element4, "InterfaceBlockTimeout", JAPModel.getInterfaceBlockTimeout());
            XMLUtil.setAttribute(element4, "autoconnect", JAPModel.isAutoConnect());
            XMLUtil.setAttribute(element4, "autoReconnect", JAPModel.isAutomaticallyReconnected());
            XMLUtil.setAttribute(element4, "minimizedStartup", JAPModel.getMinimizeOnStartup());
            XMLUtil.setAttribute(element4, "neverRemindActiveContent", this.mbActCntMessageNeverRemind);
            XMLUtil.setAttribute(element4, "neverExplainForward", this.m_bForwarderNotExplain);
            XMLUtil.setAttribute(element4, "doNotAbuseReminder", this.mbDoNotAbuseReminder);
            XMLUtil.setAttribute(element4, "neverRemindGoodBye", JAPModel.getInstance().isNeverRemindGoodbye());
            XMLUtil.setAttribute(element4, "Locale", JAPMessages.getLocale().getLanguage());
            Element element7 = document.createElement(XML_ELEM_LOOK_AND_FEELS);
            XMLUtil.setAttribute(element7, XML_ATTR_LOOK_AND_FEEL, JAPModel.getInstance().getLookAndFeel());
            element4.appendChild(element7);
            Vector vector = JAPModel.getInstance().getLookAndFeelFiles();
            for (int i = 0; i < vector.size(); ++i) {
                Element element8 = document.createElement(XML_ELEM_LOOK_AND_FEEL);
                XMLUtil.setValue((Node)element8, ((File)vector.elementAt(i)).getAbsolutePath());
                element7.appendChild(element8);
            }
            element4.appendChild(TrustModel.toXmlElement(document, "TrustModels"));
            Element element9 = document.createElement("ExitAddressesList");
            element4.appendChild(element9);
            Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = JAPController.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses).getEntrySnapshotAsEnumeration();
            while (enumeration.hasMoreElements()) {
                element9.appendChild(((MixCascadeExitAddresses)enumeration.nextElement()).toXmlElement(document));
            }
            Element element10 = document.createElement("MixCascades");
            element4.appendChild(element10);
            enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
            while (enumeration.hasMoreElements()) {
                element10.appendChild(((MixCascade)enumeration.nextElement()).toXmlElement(document));
            }
            element4.appendChild(Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPController.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry).toXmlElement(document));
            element4.appendChild(Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPController.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).toXmlElement(document));
            element4.appendChild(Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = JAPController.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).toXmlElement(document));
            Element element11 = Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPController.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).toXmlElement(document);
            XMLUtil.setAttribute(element11, "autoBlacklistNewCascades", BlacklistedCascadeIDEntry.areNewCascadesInBlacklist());
            element4.appendChild(element11);
            MixCascade mixCascade = this.getCurrentMixCascade();
            if (mixCascade != null) {
                element3 = mixCascade.toXmlElement(document);
                element4.appendChild(element3);
            }
            element4.appendChild(Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = JAPController.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).toXmlElement(document));
            element4.appendChild(Database.getInstance(class$anon$infoservice$DeletedMessageIDDBEntry == null ? (class$anon$infoservice$DeletedMessageIDDBEntry = JAPController.class$("anon.infoservice.DeletedMessageIDDBEntry")) : class$anon$infoservice$DeletedMessageIDDBEntry).toXmlElement(document));
            element4.appendChild(Database.getInstance(class$anon$infoservice$ClickedMessageIDDBEntry == null ? (class$anon$infoservice$ClickedMessageIDDBEntry = JAPController.class$("anon.infoservice.ClickedMessageIDDBEntry")) : class$anon$infoservice$ClickedMessageIDDBEntry).toXmlElement(document));
            element3 = document.createElement("GUI");
            element4.appendChild(element3);
            XMLUtil.setAttribute(element3, "fontSize", JAPModel.getInstance().getFontSize());
            XMLUtil.setAttribute(element3, "dialogOptFormat", JAPDialog.getOptimizedFormat());
            if (this.m_View instanceof AbstractJAPMainView) {
                ((AbstractJAPMainView)this.m_View).saveWindowPositions();
            }
            Element element12 = document.createElement("ConfigWindow");
            this.addWindowLocationToConf(element12, JAPModel.getInstance().getConfigWindowLocation());
            this.addWindowSizeToConf(element12, JAPModel.getInstance().getConfigSize(), JAPModel.getInstance().isConfigWindowSizeSaved());
            element3.appendChild(element12);
            element12 = document.createElement("IconifiedWindow");
            XMLUtil.setAttribute(element12, "alwaysOnTop", JAPModel.getInstance().isMiniViewOnTop());
            if (JAPModel.getInstance().getIconifiedSize() != null) {
                Element element13 = document.createElement("Size");
                XMLUtil.setAttribute(element13, "width", JAPModel.getInstance().getIconifiedSize().width);
                XMLUtil.setAttribute(element13, "height", JAPModel.getInstance().getIconifiedSize().height);
                element12.appendChild(element13);
            }
            this.addWindowLocationToConf(element12, JAPModel.getInstance().getIconifiedWindowLocation());
            element3.appendChild(element12);
            Element element14 = document.createElement("MainWindow");
            element3.appendChild(element14);
            this.addWindowLocationToConf(element14, JAPModel.getMainWindowLocation());
            if (JAPModel.getMoveToSystrayOnStartup()) {
                element2 = document.createElement("MoveToSystray");
                XMLUtil.setValue((Node)element2, true);
                element14.appendChild(element2);
            }
            if (!JAPModel.getInstance().getStartPortableFirefox()) {
                element2 = document.createElement("StartPortableFirefox");
                XMLUtil.setValue((Node)element2, false);
                element14.appendChild(element2);
            }
            if (JAPModel.getDefaultView() == 2) {
                element2 = document.createElement("DefaultView");
                XMLUtil.setValue((Node)element2, "Simplified");
                element14.appendChild(element2);
            } else {
                element2 = document.createElement("DefaultView");
                XMLUtil.setValue((Node)element2, "Normal");
                element14.appendChild(element2);
            }
            XMLUtil.setAttribute(element14, XML_ATTR_ENABLE_CLOSE_BUTTON, JAPModel.getInstance().isCloseButtonShown());
            element2 = document.createElement("Debug");
            element4.appendChild(element2);
            Element element15 = document.createElement("Level");
            Text text = document.createTextNode(Integer.toString(JAPDebug.getInstance().getLogLevel()));
            element15.appendChild(text);
            element2.appendChild(element15);
            element15 = document.createElement("Detail");
            XMLUtil.setValue((Node)element15, LogHolder.getDetailLevel());
            element2.appendChild(element15);
            element15 = document.createElement("Type");
            int n = JAPDebug.getInstance().getLogType();
            int[] arrn = LogType.getAvailableLogTypes();
            for (int i = 1; i < arrn.length; ++i) {
                XMLUtil.setAttribute(element15, LogType.getLogTypeName(arrn[i]), (n & arrn[i]) != 0);
            }
            element2.appendChild(element15);
            if (JAPDebug.isShowConsole() || JAPDebug.isLogToFile()) {
                element15 = document.createElement("Output");
                element2.appendChild(element15);
                if (JAPDebug.isShowConsole()) {
                    XMLUtil.setAttribute(element15, "showWindow", true);
                }
                if (JAPDebug.isLogToFile()) {
                    Element element16 = document.createElement("File");
                    element15.appendChild(element16);
                    XMLUtil.setValue((Node)element16, JAPDebug.getLogFilename());
                }
            }
            element4.appendChild(SignatureVerifier.getInstance().toXmlElement(document));
            Element element17 = InfoServiceHolder.getInstance().toXmlElement(document);
            XMLUtil.setAttribute(element17, XML_ALLOW_NON_ANONYMOUS_CONNECTION, JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting());
            element4.appendChild(element17);
            Element element18 = document.createElement("TORSettings");
            XMLUtil.setAttribute(element18, "activated", JAPModel.getInstance().isTorActivated());
            Element element19 = document.createElement("MaxConnectionsPerRoute");
            XMLUtil.setValue((Node)element19, JAPModel.getTorMaxConnectionsPerRoute());
            element18.appendChild(element19);
            element19 = document.createElement("RouteLen");
            XMLUtil.setAttribute(element19, "min", JAPModel.getTorMinRouteLen());
            XMLUtil.setAttribute(element19, "max", JAPModel.getTorMaxRouteLen());
            element18.appendChild(element19);
            element19 = document.createElement("PreCreateAnonRoutes");
            XMLUtil.setValue((Node)element19, JAPModel.isPreCreateAnonRoutesEnabled());
            element18.appendChild(element19);
            element19 = document.createElement("DirectoryServer");
            XMLUtil.setAttribute(element19, "useNoneDefault", JAPModel.isTorNoneDefaultDirServerEnabled());
            element18.appendChild(element19);
            element4.appendChild(element18);
            try {
                element = document.createElement("MixMinion");
                XMLUtil.setAttribute(element, "activated", JAPModel.getInstance().isMixMinionActivated());
                Element element20 = document.createElement("RouteLen");
                XMLUtil.setValue((Node)element20, JAPModel.getMixminionRouteLen());
                Element element21 = document.createElement("MixminionREPLYMail");
                XMLUtil.setAttribute(element21, "MixminionSender", JAPModel.getMixminionMyEMail());
                Element element22 = document.createElement("MixminionPasswordHash");
                XMLUtil.setValue((Node)element22, Base64.encodeBytes(JAPModel.getMixMinionPasswordHash()));
                Element element23 = document.createElement("MixminionKeyring");
                XMLUtil.setValue((Node)element23, JAPModel.getMixminionKeyring());
                element.appendChild(element20);
                element.appendChild(element21);
                element.appendChild(element22);
                element.appendChild(element23);
                element4.appendChild(element);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, "Error in savin Mixminion settings -- ignoring...", exception);
            }
            try {
                element = document.createElement("Dialog");
                XMLUtil.setValue(element, JAPModel.getInstance().getDialogVersion());
                element4.appendChild(element);
            }
            catch (Exception exception) {
                // empty catch block
            }
            element4.appendChild(JAPModel.getInstance().getRoutingSettings().toXmlElement(document));
            element4.appendChild(TermsAndConditions.getAllTermsAndConditionsAsXMLElement(document));
            element4.appendChild(Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? (class$anon$terms$template$TermsAndConditionsTemplate = JAPController.class$("anon.terms.template.TermsAndConditionsTemplate")) : class$anon$terms$template$TermsAndConditionsTemplate).toXmlElement(document));
            return document;
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.MISC, throwable);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMinimizeOnStartup(boolean bl) {
        JAPController jAPController = this;
        synchronized (jAPController) {
            m_Model.setMinimizeOnStartup(bl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMoveToSystrayOnStartup(boolean bl) {
        JAPController jAPController = this;
        synchronized (jAPController) {
            m_Model.setMoveToSystrayOnStartup(bl);
        }
    }

    public boolean isTorMixminionAllowed() {
        return this.m_bAllowTorMixminion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDefaultView(int n) {
        JAPController jAPController = this;
        synchronized (jAPController) {
            m_Model.setDefaultView(n);
        }
    }

    public MixCascade switchTrustFilter(TrustModel trustModel) {
        TrustModel.setCurrentTrustModel(trustModel);
        if (!trustModel.isTrusted(this.getCurrentMixCascade())) {
            return this.switchToNextMixCascade(true);
        }
        return this.getCurrentMixCascade();
    }

    public MixCascade switchToNextMixCascade() {
        return this.switchToNextMixCascade(false);
    }

    public MixCascade switchToNextMixCascade(boolean bl) {
        if (bl) {
            this.m_mixContainer.getNextRandomCascade();
        } else {
            this.m_mixContainer.getNextCascade();
        }
        return this.m_mixContainer.getCurrentCascade();
    }

    public void setCurrentMixCascade(MixCascade mixCascade) {
        this.m_mixContainer.setCurrentCascade(mixCascade);
    }

    public InetAddress getListenerInetAddress() {
        return this.m_socketHTTPListener == null ? null : this.m_socketHTTPListener.getInetAddress();
    }

    public InetAddress getVirtualBoxInetAddress() {
        if (!this.m_bIsVirtualBoxListener) {
            return null;
        }
        return this.m_socketHTTPListenerTwo == null ? null : this.m_socketHTTPListenerTwo.getInetAddress();
    }

    public int getListenerPort() {
        return this.m_socketHTTPListener == null ? -1 : this.m_socketHTTPListener.getLocalPort();
    }

    public String getCurrentPIID() {
        String string = JAPController.getInstance().getCurrentMixCascade().getPIID();
        if (string == null) {
            Vector vector = Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = JAPController.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryList();
            for (int i = 0; i < vector.size(); ++i) {
                if (((PaymentInstanceDBEntry)vector.elementAt(i)).isTest()) continue;
                string = ((PaymentInstanceDBEntry)vector.elementAt(i)).getId();
                break;
            }
        }
        return string;
    }

    public MixCascade getCurrentMixCascade() {
        if (this.m_mixContainer == null) {
            return null;
        }
        return this.m_mixContainer.getCurrentCascade();
    }

    public void applyProxySettingsToInfoService(boolean bl) {
        if (m_Model.getProxyInterface() != null && m_Model.getProxyInterface().isValid()) {
            HTTPConnectionFactory.getInstance().setNewProxySettings(m_Model.getProxyInterface(), bl);
        } else {
            HTTPConnectionFactory.getInstance().setNewProxySettings(null, false);
        }
    }

    private void applyProxySettingsToAnonService(Component component) {
        if (this.getAnonMode()) {
            int n = 0;
            if (this.isAnonConnected()) {
                JAPDialog.Options options = new JAPDialog.Options(0){

                    public String getYesOKText() {
                        return JAPMessages.getString("reconnect");
                    }

                    public String getNoText() {
                        return JAPMessages.getString("later");
                    }
                };
                n = !JAPModel.getInstance().isAnonymityPopupsHidden() ? JAPDialog.showConfirmDialog(component, JAPMessages.getString("reconnectAfterProxyChangeMsg"), JAPMessages.getString("reconnectAfterProxyChangeTitle"), options, 2, null, null) : 0;
            }
            if (n == 0) {
                new Thread(){

                    public void run() {
                        JAPController.this.stopAnonModeWait();
                        JAPController.this.start();
                    }
                }.start();
            }
        }
    }

    public static String getFirewallAuthPasswd_() {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInfoServiceDisabled(boolean bl) {
        m_Model.setInfoServiceDisabled(bl);
        JAPController jAPController = this;
        synchronized (jAPController) {
            this.setChanged();
            this.notifyObservers(new JAPControllerMessage(1));
        }
    }

    public static void setPreCreateAnonRoutes(boolean bl) {
        m_Model.setPreCreateAnonRoutes(bl);
    }

    public static void setTorUseNoneDefaultDirServer(boolean bl) {
        m_Model.setTorUseNoneDefaultDirServer(bl);
    }

    public boolean isConnecting() {
        return this.m_bConnecting;
    }

    protected void showRequestAutoReconnectDialog(final String string) {
        if (this.m_View.isShowingPaymentError()) {
            return;
        }
        new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Object object = JAPController.this.SYNC_DISCONNECTED_ERROR;
                synchronized (object) {
                    if (JAPController.this.m_bDisconnectedErrorShown) {
                        return;
                    }
                    JAPController.this.m_bDisconnectedErrorShown = true;
                }
                if (!JAPModel.getInstance().isAnonymityPopupsHidden() && JAPDialog.showConfirmDialog(JAPController.this.getCurrentView(), string + "<br><br>" + JAPMessages.getString(MSG_ASK_RECONNECT), JAPMessages.getString("errorConnectingFirstMixTitle"), 0, 0, (JAPDialog.ILinkedInformation)new JAPDialog.LinkedInformationAdapter(){

                    public boolean isOnTop() {
                        return true;
                    }
                }) == 0) {
                    JAPModel.getInstance().setAutoReConnect(true);
                    JAPController.getInstance().start();
                }
                object = JAPController.this.SYNC_DISCONNECTED_ERROR;
                synchronized (object) {
                    JAPController.this.m_bDisconnectedErrorShown = false;
                }
            }
        }).start();
    }

    public boolean getAnonMode() {
        return this.m_proxyAnon != null;
    }

    public boolean isConfigAssistantShown() {
        return this.m_bShowConfigAssistant;
    }

    public void setAllowPaidServices(boolean bl) {
        if (!bl) {
            this.m_mixContainer.reset();
        }
        this.m_lAllowPaidServices = bl ? 0L : System.currentTimeMillis() + 60000L;
    }

    public void setShowConfigAssistant(boolean bl) {
        this.m_bShowConfigAssistant = bl;
    }

    public boolean isOperatorOfConnectedMix(ServiceOperator serviceOperator) {
        MixCascade mixCascade = this.getConnectedCascade();
        if (mixCascade != null) {
            MixInfo mixInfo = null;
            for (int i = 0; i < mixCascade.getNumberOfMixes(); ++i) {
                mixInfo = mixCascade.getMixInfo(i);
                if (!mixInfo.getServiceOperator().equals(serviceOperator)) continue;
                return false;
            }
        }
        return true;
    }

    public MixCascade getConnectedCascade() {
        AnonProxy anonProxy = this.m_proxyAnon;
        if (anonProxy == null) {
            return null;
        }
        MixCascade mixCascade = this.getCurrentMixCascade();
        MixCascade mixCascade2 = anonProxy.getMixCascade();
        if (anonProxy != null && anonProxy.isConnected() && mixCascade != null && mixCascade2 != null && mixCascade2.equals(mixCascade)) {
            return mixCascade;
        }
        return null;
    }

    public boolean isAnonConnected() {
        return this.getConnectedCascade() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopAnonModeWait() {
        while (m_Controller.getAnonMode() || m_Controller.isAnonConnected()) {
            Object object = JAPController.m_Controller.m_finishSync;
            synchronized (object) {
                m_Controller.stop();
                LogHolder.log(5, LogType.MISC, "Waiting for finish of AN.ON connection...");
                try {
                    JAPController.m_Controller.m_finishSync.wait(4000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                if (!m_Controller.getAnonMode() && !m_Controller.isAnonConnected()) {
                    this.m_anonJobQueue.removeAllJobs();
                    try {
                        JAPController.m_Controller.m_finishSync.wait(1000L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    break;
                }
            }
        }
    }

    public void blockDirectProxy(boolean bl) {
        this.m_bBlockDirectProxyTemp = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAnonMode(boolean bl) {
        if (!this.m_bShutdown || !bl) {
            Object object = JAPController.m_Controller.m_finishSync;
            synchronized (object) {
                this.m_anonJobQueue.addJob(new SetAnonModeAsync(bl));
            }
        }
    }

    public void stop() {
        this.setAnonMode(false);
    }

    public void start() {
        if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
            IAddress iAddress = JAPModel.getInstance().getRoutingSettings().getUserProvidetForwarder();
            if (iAddress != null) {
                new JAPRoutingEstablishForwardedConnectionDialog(this.getCurrentView(), iAddress);
            } else {
                new JAPRoutingEstablishForwardedConnectionDialog(this.getCurrentView());
            }
            this.notifyJAPObservers();
        } else {
            this.setAnonMode(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDummyTraffic(int n) {
        if (n == Integer.MAX_VALUE) {
            return;
        }
        m_Model.setDummyTraffic(n);
        ForwardServerManager.getInstance().setDummyTrafficInterval(n);
        Object object = this.PROXY_SYNC;
        synchronized (object) {
            if (this.m_proxyAnon != null) {
                this.m_proxyAnon.setDummyTraffic(n);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInterfaceBlockTimeout(long l) {
        m_Model.setInterfaceBlockTimeout(l);
        Object object = this.PROXY_SYNC;
        synchronized (object) {
            if (this.m_proxyAnon != null) {
                this.m_proxyAnon.setInterfaceBlockTimeout(l);
            }
        }
    }

    public static void setTorMaxConnectionsPerRoute(int n) {
        m_Model.setTorMaxConnectionsPerRoute(n);
    }

    public static void setTorRouteLen(int n, int n2) {
        m_Model.setTorMaxRouteLen(n2);
        m_Model.setTorMinRouteLen(n);
    }

    public static void setMixminionPassword(String string) {
        m_Model.setMixMinionPassword(string);
    }

    public static void setMixminionPasswordHash(byte[] arrby) {
        m_Model.setMixinionPasswordHash(arrby);
    }

    public static void resetMixminionPassword() {
        m_Model.resetMixMinionKeyringandPw();
    }

    public static void setMixminionKeyring(String string) {
        m_Model.setMixminionKeyring(string);
    }

    public static void setMixminionMessages(Vector vector) {
        m_Model.setMixminionMessages(vector);
    }

    public static void setMixminionMMRList(MMRList mMRList) {
        m_Model.setMixminionMMRList(mMRList);
    }

    public static void setMixminionFragments(Vector vector) {
        m_Model.setMixminionFragments(vector);
    }

    private void intern_startListener(int n, String string) {
        block16: {
            LogHolder.log(7, LogType.MISC, "JAPModel:startListener on port: " + n);
            try {
                InetAddress inetAddress;
                block17: {
                    if (!JAPModel.isHttpListenerLocal()) {
                        LogHolder.log(5, LogType.NET, "Try binding Listener on all hosts.");
                        this.m_socketHTTPListener = new ServerSocket(n);
                        break block16;
                    }
                    inetAddress = InetAddress.getByName("127.0.0.1");
                    LogHolder.log(5, LogType.NET, "Try binding Listener on host: " + inetAddress);
                    try {
                        this.m_socketHTTPListener = new ServerSocket(n, 50, inetAddress);
                        LogHolder.log(1, LogType.NET, "Listener was successfully bound to: " + inetAddress.getHostAddress() + ":" + n);
                    }
                    catch (IOException iOException) {
                        if (string != null) break block17;
                        throw iOException;
                    }
                }
                if (string != null) {
                    inetAddress = InetAddress.getAllByName(string)[0];
                    if (this.m_socketHTTPListener == null) {
                        LogHolder.log(5, LogType.NET, "Try binding Listener on host: " + inetAddress);
                        this.m_socketHTTPListener = new ServerSocket(n, 50, inetAddress);
                        LogHolder.log(1, LogType.NET, "Listener was successfully bound to: " + inetAddress.getHostAddress() + ":" + n);
                    } else {
                        try {
                            if (((Boolean)(class$java$net$InetAddress == null ? (class$java$net$InetAddress = JAPController.class$("java.net.InetAddress")) : class$java$net$InetAddress).getMethod("isLoopbackAddress", null).invoke(inetAddress, null)).booleanValue()) {
                                LogHolder.log(4, LogType.NET, "Host is explicitly set, but it is a loopback address!");
                                inetAddress = InetAddress.getByName(null);
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(5, LogType.NET, exception);
                        }
                        if (!this.m_socketHTTPListener.getInetAddress().equals(inetAddress)) {
                            LogHolder.log(5, LogType.NET, "Try binding Listener on host: " + inetAddress);
                            try {
                                this.m_socketHTTPListenerTwo = new ServerSocket(n, 50, inetAddress);
                                LogHolder.log(1, LogType.NET, "Listener was successfully bound to: " + inetAddress.getHostAddress() + ":" + n);
                            }
                            catch (IOException iOException) {
                                LogHolder.log(1, LogType.NET, "Could not bind listener to " + inetAddress.getHostAddress() + ":" + n, iOException);
                            }
                        }
                    }
                }
                if (this.m_socketHTTPListenerTwo == null) {
                    this.m_socketHTTPListenerTwo = SocketGuard.createVirtualBoxServerSocket(n, this.m_socketHTTPListener.getInetAddress());
                    if (this.m_socketHTTPListenerTwo != null) {
                        this.m_bIsVirtualBoxListener = true;
                    }
                }
            }
            catch (Exception exception) {
                LogHolder.log(1, LogType.NET, exception);
                this.m_socketHTTPListener = null;
            }
        }
    }

    public synchronized boolean startHTTPListener(String string, int n) {
        if (!this.isRunningHTTPListener) {
            LogHolder.log(7, LogType.NET, "Start HTTP Listener");
            this.intern_startListener(n <= 0 ? JAPModel.getHttpListenerPortNumber() : n, string);
            if (this.m_socketHTTPListener != null && this.m_proxyDirect == null) {
                DirectProxy.AllowProxyConnectionCallback allowProxyConnectionCallback = null;
                if (!JAPModel.isSmallDisplay()) {
                    allowProxyConnectionCallback = new DirectProxy.AllowProxyConnectionCallback(){

                        public boolean isNonAnonymousAccessForbidden() {
                            return JAPController.this.m_bBlockDirectProxyPermanent || !JAPModel.getInstance().isNonAnonymousAllowed();
                        }

                        public URL getHTMLHelpPath() {
                            return JAPHelp.getInstance().getContextURL("services_general");
                        }

                        public boolean isAskedForAnyNonAnonymousRequest() {
                            return JAPModel.getInstance().isAskForAnyNonAnonymousRequest();
                        }

                        public String getApplicationName() {
                            return JAPModel.getInstance().getProgramName();
                        }

                        public boolean isConnecting() {
                            AnonProxy anonProxy = JAPController.this.m_proxyAnon;
                            if (anonProxy == null) {
                                return false;
                            }
                            return anonProxy.isConnecting() || anonProxy.isConnected() || anonProxy.isConnecting();
                        }

                        public String getAllowNonAnonymousSettingName() {
                            if (this.isNonAnonymousAccessForbidden() && !JAPController.this.m_bBlockDirectProxyPermanent) {
                                return JAPConfAnonGeneral.MSG_DENY_NON_ANONYMOUS_SURFING;
                            }
                            return null;
                        }

                        public DirectProxy.AllowProxyConnectionCallback.Answer callback(DirectProxy.RequestInfo requestInfo) {
                            JAPDialog.LinkedInformationAdapter linkedInformationAdapter;
                            String string = "";
                            String string2 = this.getApplicationName();
                            if (JAPController.this.m_View == null) {
                                return new DirectProxy.AllowProxyConnectionCallback.Answer(false, false, false);
                            }
                            if (this.isNonAnonymousAccessForbidden()) {
                                return new DirectProxy.AllowProxyConnectionCallback.Answer(false, true, false);
                            }
                            if (JAPController.this.m_bBlockDirectProxyTemp) {
                                return new DirectProxy.AllowProxyConnectionCallback.Answer(false, false, true);
                            }
                            JAPDialog.LinkedCheckBox linkedCheckBox = null;
                            if (JAPModel.getInstance().isAskForAnyNonAnonymousRequest()) {
                                linkedCheckBox = new JAPDialog.LinkedCheckBox(JAPMessages.getString(MSG_DIRECT_PROXY_CONFIRM_ONCE), false){

                                    public boolean isOnTop() {
                                        return true;
                                    }
                                };
                                linkedInformationAdapter = linkedCheckBox;
                            } else {
                                linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                                    public boolean isOnTop() {
                                        return true;
                                    }
                                };
                            }
                            string = "<font color=\"red\"><b>" + (this.isConnecting() ? JAPMessages.getString(MSG_ANONYMITY_MODE_NOT_YET_CONNECTED) : JAPMessages.getString(MSG_ANONYMITY_MODE_OFF)) + "</b></font> ";
                            String string3 = requestInfo.getHost() + (requestInfo.getPort() != 80 ? ":" + requestInfo.getPort() : "");
                            if (JAPModel.getInstance().isAskForAnyNonAnonymousRequest()) {
                                string = string + JAPMessages.getString(MSG_ALLOWUNPROTECTED, "<b>" + string3 + "</b>");
                                string2 = string2 + ": " + string3;
                            } else {
                                string = string + JAPMessages.getString(MSG_ALLOWUNPROTECTED_ALL);
                            }
                            final IntegerVariable integerVariable = new IntegerVariable(20);
                            final IntegerVariable integerVariable2 = new IntegerVariable(Integer.MIN_VALUE);
                            final JAPDialog.Options options = new JAPDialog.Options(1){

                                public String getYesOKText() {
                                    return JAPMessages.getString(DialogContentPane.MSG_CONTINUE);
                                }

                                public String getNoText() {
                                    return JAPMessages.getString(MSG_BTN_BLOCK_REQUEST);
                                }

                                public String getCancelText() {
                                    return JAPMessages.getString(MSG_I_DO_NOT_KNOW) + " (" + integerVariable.intValue() + ")";
                                }
                            };
                            final Thread thread = new Thread(){

                                public void run() {
                                    while (integerVariable.intValue() > 0 && !Thread.currentThread().isInterrupted()) {
                                        try {
                                            options.update();
                                            Thread.sleep(1000L);
                                            integerVariable.set(integerVariable.get() - 1);
                                        }
                                        catch (InterruptedException interruptedException) {
                                            break;
                                        }
                                    }
                                }
                            };
                            thread.start();
                            final String string4 = string;
                            final String string5 = string2;
                            final JAPDialog.LinkedInformationAdapter linkedInformationAdapter2 = linkedInformationAdapter;
                            Thread thread2 = new Thread(){

                                public void run() {
                                    integerVariable2.set(JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), string4, string5, options, 2, (JAPDialog.ILinkedInformation)linkedInformationAdapter2));
                                    if (thread.isAlive()) {
                                        thread.interrupt();
                                    }
                                }
                            };
                            thread2.start();
                            try {
                                thread.join();
                            }
                            catch (InterruptedException interruptedException) {
                                LogHolder.log(5, LogType.GUI, interruptedException);
                            }
                            while (thread2.isAlive()) {
                                thread2.interrupt();
                                try {
                                    thread2.join(200L);
                                }
                                catch (InterruptedException interruptedException) {
                                    LogHolder.log(3, LogType.GUI, interruptedException);
                                }
                                integerVariable2.set(Integer.MIN_VALUE);
                            }
                            if (integerVariable2.get() == Integer.MIN_VALUE || integerVariable2.get() == 2) {
                                return new DirectProxy.AllowProxyConnectionCallback.Answer(false, true, true);
                            }
                            if (linkedCheckBox != null) {
                                if (JAPModel.getInstance().isAskForAnyNonAnonymousRequest()) {
                                    return new DirectProxy.AllowProxyConnectionCallback.Answer(integerVariable2.get() == 0, linkedCheckBox.getState(), false);
                                }
                                return new DirectProxy.AllowProxyConnectionCallback.Answer(integerVariable2.get() == 0, !linkedCheckBox.getState(), false);
                            }
                            return new DirectProxy.AllowProxyConnectionCallback.Answer(integerVariable2.get() == 0, true, false);
                        }
                    };
                }
                this.m_proxyDirect = new DirectProxy(this.m_socketHTTPListener, this.m_socketHTTPListenerTwo, JAPModel.getInstance().getMutableProxyInterface(), allowProxyConnectionCallback);
                if (!JAPModel.isSmallDisplay()) {
                    JAPModel.getInstance().addObserver(allowProxyConnectionCallback);
                    this.m_proxyDirect.start();
                }
            }
            this.isRunningHTTPListener = true;
        }
        return this.m_socketHTTPListener != null;
    }

    public ConfigAssistant showInstallationAssistant(int n) {
        if (this.m_bAssistantClicked) {
            return null;
        }
        this.m_bAssistantClicked = true;
        final ConfigAssistant configAssistant = new ConfigAssistant((Component)this.getViewWindow(), this.m_View, n);
        configAssistant.addWindowListener(new WindowAdapter(){

            public void windowClosed(WindowEvent windowEvent) {
                configAssistant.removeWindowListener(this);
                JAPController.this.m_bAssistantClicked = false;
                JAPController.this.getViewWindow().setVisible(true);
            }
        });
        new Thread(new Runnable(){

            public void run() {
                configAssistant.setVisible(true);
            }
        }).start();
        return configAssistant;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int showNonAnonymousWarning(final String string, boolean bl) {
        int n = Integer.MIN_VALUE;
        if (!(JAPModel.getInstance().isAnonymityPopupsHidden() || JAPController.getInstance().m_httpListenerConfigAssistant.hasVisitedWebsite() || JAPController.getInstance().m_restarter.hideWarnings() || JAPController.getInstance().m_lStartupCounter < 4L)) {
            Object object = SYNC_NON_ANONYMOUS_WARNING;
            synchronized (object) {
                if (ms_bShowingAnonymousWarning) {
                    return 0;
                }
                ms_bShowingAnonymousWarning = true;
            }
            n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_NOTHING_ANONYMIZED), JAPMessages.getString(JAPDialog.MSG_TITLE_WARNING), new JAPDialog.Options(bl ? 1 : 0){

                public String getNoText() {
                    return JAPMessages.getString(JAPNewView.MSG_BTN_ASSISTANT);
                }

                public String getYesOKText() {
                    return string;
                }
            }, 2);
            object = SYNC_NON_ANONYMOUS_WARNING;
            synchronized (object) {
                ms_bShowingAnonymousWarning = false;
            }
        }
        if (n == 1) {
            if (JAPController.getInstance().m_View != null) {
                JAPController.getInstance().m_View.setVisible(true);
            }
            JAPController.getInstance().showInstallationAssistant(2);
        }
        return n;
    }

    public static void goodBye(final boolean bl) {
        Thread thread = new Thread(new Runnable(){

            public void run() {
                Object object;
                Object object2;
                int n = Integer.MIN_VALUE;
                if (!bl && JAPController.getInstance().m_lStartupCounter < 4L) {
                    JAPController.getInstance().m_lStartupCounter--;
                }
                if (JAPController.getInstance().m_httpBrowserIdentification.isJonDoFoxDetected()) {
                    JAPModel.getInstance().setNeverRemindGoodbye(true);
                }
                if (bl) {
                    n = JAPController.showNonAnonymousWarning(JAPMessages.getString(MSG_FINISH_NEVERTHELESS), true);
                }
                if (n == Integer.MIN_VALUE) {
                    if (!(JAPModel.getInstance().isAnonymityPopupsHidden() || JAPModel.getInstance().isNeverRemindGoodbye() || !bl || JAPController.getInstance().isPortableMode() || JAPController.getInstance().m_restarter.hideWarnings())) {
                        JAPDialog.LinkedCheckBox linkedCheckBox = new JAPDialog.LinkedCheckBox(false){

                            public boolean isOnTop() {
                                return true;
                            }
                        };
                        n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_DISABLE_GOODBYE), 2, 1, (JAPDialog.ILinkedInformation)linkedCheckBox);
                        if (n == 0) {
                            JAPModel.getInstance().setNeverRemindGoodbye(linkedCheckBox.getState());
                        }
                    } else {
                        n = 0;
                    }
                }
                if (n == 0 && !JAPModel.getInstance().isPaymentPopupsHidden() && JAPController.getInstance().getViewWindow() != null && JAPController.getInstance().m_bAskSavePayment && bl && !JAPController.getInstance().m_restarter.hideWarnings()) {
                    object2 = PayAccountsFile.getInstance().getAccounts();
                    while (object2.hasMoreElements()) {
                        object = (PayAccount)object2.nextElement();
                        if (((PayAccount)object).isBackupDone()) continue;
                        JAPDialog.LinkedCheckBox linkedCheckBox = new JAPDialog.LinkedCheckBox(false, "payment"){

                            public boolean isOnTop() {
                                return true;
                            }
                        };
                        n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_ACCOUNT_NOT_SAVED), new JAPDialog.Options(0){

                            public String getNoText() {
                                return JAPMessages.getString("exportAccountFile");
                            }

                            public String getYesOKText() {
                                return JAPMessages.getString(MSG_FINISH_NEVERTHELESS);
                            }
                        }, 1, (JAPDialog.ILinkedInformation)linkedCheckBox);
                        if (1 == n) {
                            JAPController.getInstance().setAskSavePayment(!linkedCheckBox.getState());
                            new Thread(new Runnable(){

                                public void run() {
                                    JAPController.getInstance().m_View.showConfigDialog("PAYMENT_TAB", Boolean.FALSE);
                                }
                            }).start();
                        } else if (Integer.MIN_VALUE == n) {
                            n = 2;
                        }
                        JAPController.getInstance().setAskSavePayment(!linkedCheckBox.getState());
                        break;
                    }
                }
                if (n == 0 || n == Integer.MIN_VALUE || JAPDialog.isConsoleOnly()) {
                    Object object3;
                    int n2;
                    if (JAPController.getInstance().getViewWindow() != null) {
                        JAPController.getInstance().getViewWindow().setEnabled(false);
                        object2 = JAPController.getInstance().m_View.getViewIconified();
                        if (object2 != null) {
                            ((Component)object2).setEnabled(false);
                        }
                    }
                    JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_SAVING_CONFIG));
                    if (JAPController.getInstance().m_finishSplash instanceof JAPSplash) {
                        if (JAPController.getInstance().getViewWindow() instanceof AbstractJAPMainView && JAPController.getInstance().getViewWindow().isVisible()) {
                            GUIUtils.centerOnWindow((JAPSplash)JAPController.getInstance().m_finishSplash, (AbstractJAPMainView)m_Controller.m_View);
                        } else {
                            ((JAPSplash)JAPController.getInstance().m_finishSplash).centerOnScreen();
                        }
                        ((JAPSplash)JAPController.getInstance().m_finishSplash).setVisible(true);
                    }
                    object2 = JAPController.getInstance().getViewWindow();
                    if (JAPController.getInstance().m_finishSplash instanceof JAPSplash) {
                        object2 = (JAPSplash)JAPController.getInstance().m_finishSplash;
                    }
                    object = (Vector)JAPController.getInstance().m_programExitListeners.clone();
                    for (n2 = 0; n2 < ((Vector)object).size(); ++n2) {
                        ((ProgramExitListener)((Vector)object).elementAt(n2)).programExiting();
                    }
                    anon.util.Util.interrupt(JAPController.getInstance().m_thRunnableShowConfigAssistant);
                    n2 = 1;
                    while (m_Controller.m_restarter.isConfigFileSaved() && m_Controller.saveConfigFile() && bl && !JAPController.getInstance().m_restarter.hideWarnings() && n2 == 1) {
                        object3 = JAPMessages.getString(MSG_ERROR_SAVING_CONFIG, JAPModel.getInstance().getConfigFile());
                        object3 = (String)object3 + " " + JAPMessages.getString(MSG_NO_WRITING);
                        if (JAPController.getInstance().isPortableMode()) {
                            object3 = (String)object3 + "<br><br><b>" + JAPMessages.getString(MSG_NO_WRITING_PORTABLE) + "</b>";
                        }
                        if ((n2 = JAPDialog.showConfirmDialog((Component)object2, (String)object3, new JAPDialog.Options(1){

                            public String getYesOKText() {
                                return JAPMessages.getString(DialogContentPane.MSG_OK);
                            }

                            public String getNoText() {
                                return JAPMessages.getString(JAPDialog.MSG_BTN_RETRY);
                            }
                        }, 0)) == 0) break;
                        if (n2 != 2) continue;
                        if (JAPController.getInstance().getViewWindow() != null) {
                            JAPController.getInstance().getViewWindow().setEnabled(true);
                            JAPViewIconified jAPViewIconified = JAPController.getInstance().m_View.getViewIconified();
                            if (jAPViewIconified != null) {
                                jAPViewIconified.setEnabled(true);
                            }
                        }
                        if (JAPController.getInstance().m_finishSplash instanceof JAPSplash) {
                            ((JAPSplash)JAPController.getInstance().m_finishSplash).setVisible(false);
                        }
                        return;
                    }
                    JAPModel.getInstance().setAutoReConnect(false);
                    JAPModel.getInstance().setCascadeAutoSwitch(false);
                    JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_CLOSING_DIALOGS));
                    JAPDialog.setConsoleOnly(true);
                    if (!bl) {
                        GUIUtils.setLoadImages(false);
                    }
                    m_Controller.m_bShutdown = true;
                    JAPModel.getInstance().setInfoServiceDisabled(true);
                    object3 = new Thread(new Runnable(){

                        public void run() {
                            LogHolder.log(5, LogType.MISC, "Stopping InfoService auto-update threads...");
                            JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_FINISHING_IS_UPDATES));
                            m_feedback.stop();
                            m_Controller.m_AccountUpdater.stop();
                            m_Controller.m_AccountUpdaterInternal.stop();
                            m_Controller.m_MixCascadeUpdater.stop();
                            m_Controller.m_InfoServiceUpdater.stop();
                            m_Controller.m_updaterExitAddress.stop();
                            m_Controller.m_paymentInstanceUpdater.stop();
                            m_Controller.m_minVersionUpdater.stop();
                            m_Controller.m_javaVersionUpdater.stop();
                            m_Controller.m_messageUpdater.stop();
                            m_Controller.m_perfInfoUpdater.stop();
                        }
                    }, "Finish IS threads");
                    ((Thread)object3).start();
                    m_Controller.m_bBlockDirectProxyPermanent = true;
                    Thread thread = new Thread(new Runnable(){

                        public void run() {
                            try {
                                JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_FINISHING_ANON));
                                m_Controller.stop();
                                m_Controller.stopAnonModeWait();
                                LogHolder.log(5, LogType.MISC, "Finishing all AN.ON jobs...");
                                m_Controller.m_anonJobQueue.stop();
                                m_Controller.queueFetchAccountInfo.stop();
                            }
                            catch (Throwable throwable) {
                                LogHolder.log(0, LogType.MISC, throwable);
                            }
                        }
                    }, "Finish anon thread");
                    thread.start();
                    if (JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2) {
                        JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_FINISH_FORWARDING_SERVER));
                        JAPController.getInstance().enableForwardingServer(false);
                    }
                    while (((Thread)object3).isAlive() || thread.isAlive()) {
                        try {
                            if (((Thread)object3).isAlive()) {
                                JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_WAITING_IS));
                            }
                            if (thread.isAlive()) {
                                JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_WAITING_ANON));
                            }
                            ((Thread)object3).join();
                            thread.join();
                        }
                        catch (InterruptedException interruptedException) {}
                    }
                    try {
                        LogHolder.log(5, LogType.NET, "Shutting down direct proxy...");
                        JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_STOPPING_PROXY));
                        DirectProxy directProxy = m_Controller.m_proxyDirect;
                        if (directProxy != null) {
                            directProxy.stop();
                        }
                        LogHolder.log(5, LogType.NET, "Shutting down direct proxy - Done!");
                    }
                    catch (Exception exception) {
                        LogHolder.log(7, LogType.NET, "Shutting down direct proxy - exception", exception);
                    }
                    try {
                        JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_STOPPING_LISTENER));
                        m_Controller.m_socketHTTPListener.close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        m_Controller.m_socketHTTPListenerTwo.close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_FINISHING));
                    LogHolder.log(5, LogType.NET, "Interrupting all network communication threads...");
                    ((Hashtable)System.getProperties()).put("socksProxyPort", "0");
                    ((Hashtable)System.getProperties()).put("socksProxyHost", "localhost");
                    JAPController.getInstance().switchViewWindow(true);
                    if (JAPController.getInstance().getViewWindow() != null) {
                        JAPController.getInstance().getViewWindow().dispose();
                    }
                    if (JAPController.getInstance().m_finishSplash instanceof JAPSplash) {
                        ((JAPSplash)m_Controller.m_finishSplash).dispose();
                    }
                    LogHolder.log(6, LogType.GUI, "View has been disposed. Finishing...");
                    if (!bl) {
                        JAPController.getInstance().m_finishSplash.setText(JAPMessages.getString(MSG_RESTARTING));
                        LogHolder.log(6, LogType.ALL, "Try to restart JAP...");
                        m_Controller.restartJAP();
                    }
                    System.exit(0);
                }
            }
        });
        if (!JAPDialog.isConsoleOnly() && SwingUtilities.isEventDispatchThread()) {
            thread.start();
        } else {
            thread.run();
        }
    }

    public static void aboutJAP() {
        try {
            if (JAPController.getInstance().m_bPresentationMode) {
                new JAPAbout(JAPController.getInstance().getViewWindow());
            } else {
                new JAPAboutNew(JAPController.getInstance().getViewWindow()).setVisible(true);
            }
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.GUI, throwable);
        }
    }

    public boolean updatePaymentInstances(boolean bl) {
        if (bl && this.m_paymentInstanceUpdater.isFirstUpdateDone()) {
            return true;
        }
        return this.m_paymentInstanceUpdater.update();
    }

    public boolean updateInfoServices(boolean bl) {
        if (bl && this.m_InfoServiceUpdater.isFirstUpdateDone()) {
            return true;
        }
        return this.m_InfoServiceUpdater.update();
    }

    public boolean updatePerformanceInfo(boolean bl) {
        if (bl && this.m_perfInfoUpdater.isFirstUpdateDone()) {
            return true;
        }
        return this.m_perfInfoUpdater.update();
    }

    public boolean fetchMixCascades(boolean bl, boolean bl2) {
        return this.fetchMixCascades(bl, bl2, false);
    }

    public boolean fetchMixCascades(boolean bl, boolean bl2, boolean bl3) {
        if (bl2 && this.m_MixCascadeUpdater.isFirstUpdateDone()) {
            return true;
        }
        LogHolder.log(6, LogType.MISC, "Trying to fetch mixcascades from infoservice.");
        while (!this.m_MixCascadeUpdater.update() && !this.m_bExpiredISCertificatesShown) {
            LogHolder.log(3, LogType.NET, "No connection to infoservices.");
            if (!(JAPModel.getInstance().isInfoServicePopupsHidden() || JAPModel.isSmallDisplay() || !bl && Database.getInstance(class$anon$infoservice$MixCascade == null ? JAPController.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getNumberOfEntries() != 0)) {
                int n;
                if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1 && !this.isAnonConnected()) {
                    n = JAPDialog.showConfirmDialog(this.getCurrentView(), JAPMessages.getString(MSG_IS_NOT_ALLOWED), 0, 0);
                    if (n == 0) {
                        JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                        this.updateInfoServices(false);
                        continue;
                    }
                } else if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 2 && this.isAnonConnected() && !bl3) {
                    n = JAPDialog.showConfirmDialog(this.getCurrentView(), JAPMessages.getString(MSG_IS_NOT_ALLOWED_FOR_ANONYMOUS), 0, 0);
                    if (n == 0) {
                        JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                        this.updateInfoServices(false);
                        continue;
                    }
                } else {
                    LogHolder.log(2, LogType.NET, JAPMessages.getString("errorConnectingInfoService"));
                    if (this.getCurrentView() != null && !bl3 && !JAPController.m_Controller.m_bShutdown) {
                        if (JAPModel.getInstance().isConfigAssistantAutomaticallyShown()) {
                            this.showInstallationAssistant(1);
                        } else {
                            JAPDialog.showErrorDialog(this.getCurrentView(), JAPMessages.getString("errorConnectingInfoService"));
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    private int versionCheck(String string, boolean bl) {
        int n;
        Object object;
        JAPDialog.LinkedInformationAdapter linkedInformationAdapter;
        String string2;
        String string3;
        boolean bl2 = false;
        String string4 = bl ? "mandatory" : "optional";
        LogHolder.log(5, LogType.MISC, "Checking if new " + string4 + " version of JAP is available...");
        JAPVersionInfo jAPVersionInfo = null;
        Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPController.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).update(InfoServiceHolder.getInstance().getJAPVersionInfo(1));
        Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPController.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).update(InfoServiceHolder.getInstance().getJAPVersionInfo(2));
        jAPVersionInfo = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPController.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById("/japRelease.jnlp");
        JAPVersionInfo jAPVersionInfo2 = JAPVersionInfo.getRecommendedUpdate("00.20.001", true);
        if (jAPVersionInfo == null) {
            LogHolder.log(3, LogType.MISC, "Could not get the current JAP version from InfoService.");
            return 1;
        }
        if (jAPVersionInfo2 != null && !jAPVersionInfo.equals(jAPVersionInfo2)) {
            bl2 = true;
            string3 = jAPVersionInfo2.getJapVersion();
        } else {
            string3 = jAPVersionInfo.getJapVersion();
        }
        if (string3.compareTo("00.20.001") <= 0) {
            return 0;
        }
        if (!(bl || bl2 || !this.isConfigAssistantShown() && JAPModel.getInstance().isReminderForOptionalUpdateActivated())) {
            return 0;
        }
        String string5 = "";
        if (bl) {
            string2 = AbstractOS.getInstance().isJavaWebstart() ? JAPMessages.getString("webstartUpdate") : (JAPController.getInstance().isHideUpdateDialogs() ? JAPMessages.getString(JAPConfUpdate.MSG_DO_EXTERNAL_UPDATE, string3 + string5) : JAPMessages.getString("newVersionAvailable", string3 + string5));
            linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                public boolean isOnTop() {
                    return true;
                }
            };
        } else if (AbstractOS.getInstance().isJavaWebstart()) {
            string2 = JAPMessages.getString("webstartUpdate");
            linkedInformationAdapter = new JAPDialog.LinkedCheckBox(false);
        } else if (JAPController.getInstance().isHideUpdateDialogs()) {
            string2 = JAPMessages.getString(JAPConfUpdate.MSG_DO_EXTERNAL_UPDATE, string3 + string5);
            linkedInformationAdapter = new JAPDialog.LinkedCheckBox(false);
        } else {
            string2 = JAPMessages.getString(MSG_NEW_OPTIONAL_VERSION, string3 + string5);
            try {
                object = jAPVersionInfo.getId().equals("/japRelease.jnlp") || jAPVersionInfo2 != null && jAPVersionInfo2.equals("/japRelease.jnlp") ? new URL(JAPMessages.getString(JAPWelcomeWizardPage.MSG_CHANGELOG_URL) + "#" + jAPVersionInfo.getJapVersion()) : new URL(JAPMessages.getString(JAPWelcomeWizardPage.MSG_CHANGELOG_URL_BETA) + "#" + jAPVersionInfo.getJapVersion() + "-beta");
                linkedInformationAdapter = new JAPDialog.LinkedURLCheckBox(false, (URL)object, JAPMessages.getString(JAPWelcomeWizardPage.MSG_CHANGELOG));
            }
            catch (MalformedURLException malformedURLException) {
                LogHolder.log(1, LogType.GUI, malformedURLException);
                linkedInformationAdapter = new JAPDialog.LinkedCheckBox(false);
            }
        }
        if (AbstractOS.getInstance().isJavaWebstart() || JAPController.getInstance().isHideUpdateDialogs()) {
            object = new JAPDialog.Options(-1);
            n = 1;
        } else {
            n = 3;
            if (bl2) {
                string2 = string2 + "<br><br>" + JAPMessages.getString(MSG_ASK_WHICH_VERSION);
                object = new JAPDialog.Options(0){

                    public String getYesOKText() {
                        return JAPMessages.getString(MSG_VERSION_DEVELOPER);
                    }

                    public String getNoText() {
                        return JAPMessages.getString(MSG_VERSION_RELEASE);
                    }
                };
            } else {
                object = new JAPDialog.Options(2){

                    public int getDefaultButton() {
                        return 2;
                    }
                };
            }
        }
        int n2 = JAPDialog.showConfirmDialog(this.getCurrentView(), string2, JAPMessages.getString("newVersionAvailableTitle"), (JAPDialog.Options)object, n, (JAPDialog.ILinkedInformation)linkedInformationAdapter);
        if (linkedInformationAdapter instanceof JAPDialog.LinkedCheckBox) {
            JAPModel.getInstance().setReminderForOptionalUpdate(!((JAPDialog.LinkedCheckBox)linkedInformationAdapter).getState());
        }
        if (AbstractOS.getInstance().isJavaWebstart() || JAPController.getInstance().isHideUpdateDialogs()) {
            return 0;
        }
        if (n2 == 0 || n2 == 1) {
            if (n2 == 1) {
                jAPVersionInfo = jAPVersionInfo2;
            }
            SoftwareUpdater.show(jAPVersionInfo, this.getCurrentView());
            return 0;
        }
        return 0;
    }

    public IJAPMainView getView() {
        return this.m_View;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setView(IJAPMainView iJAPMainView, ISplashResponse iSplashResponse) {
        Object object = this.SYNC_VIEW;
        synchronized (object) {
            this.m_View = iJAPMainView;
            this.m_finishSplash = iSplashResponse;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void switchViewWindow(boolean bl) {
        Object object = this.SYNC_VIEW;
        synchronized (object) {
            this.m_bMainView = bl;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Component getCurrentView() {
        Object object = this.SYNC_VIEW;
        synchronized (object) {
            if (this.m_finishSplash != null && this.m_finishSplash instanceof Component && ((Component)((Object)this.m_finishSplash)).isVisible()) {
                return (Component)((Object)this.m_finishSplash);
            }
            Window window = this.getViewWindow();
            if (window instanceof AbstractJAPMainView) {
                return ((AbstractJAPMainView)window).getCurrentView();
            }
            return window;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Window getViewWindow() {
        Object object = this.SYNC_VIEW;
        synchronized (object) {
            if (this.m_View instanceof Window) {
                if (this.m_bMainView) {
                    return (Window)((Object)this.m_View);
                }
                return this.m_View.getViewIconified();
            }
            return null;
        }
    }

    public void showConfigDialog(String string, Object object) {
        if (this.m_View != null && this.m_View instanceof AbstractJAPMainView) {
            ((AbstractJAPMainView)this.m_View).showConfigDialog(string, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showNotRecoverableExceptionDialog(final AnonServiceException anonServiceException) {
        if (!(anonServiceException instanceof INotRecoverableException)) {
            return;
        }
        if (JAPModel.getInstance().isAnonymityPopupsHidden()) {
            return;
        }
        Object object = this.SYNC_DISCONNECTED_ERROR;
        synchronized (object) {
            if (this.m_bDisconnectedErrorShown) {
                return;
            }
            this.m_bDisconnectedErrorShown = true;
        }
        new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                String string;
                JAPDialog.LinkedInformationAdapter linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                    public boolean isOnTop() {
                        return true;
                    }
                };
                boolean bl = false;
                if (anonServiceException instanceof UnknownProtocolVersionException) {
                    string = JAPMessages.getString("errorMixProtocolNotSupported");
                } else if (anonServiceException instanceof ServiceSignatureException) {
                    string = ((ServiceSignatureException)anonServiceException).getMixIndex() == 0 ? JAPMessages.getString("errorMixFirstMixSigCheckFailed") : JAPMessages.getString("errorMixOtherMixSigCheckFailed");
                } else if (anonServiceException instanceof TrustException) {
                    if (anonServiceException instanceof ServiceUnreachableException) {
                        string = JAPMessages.getString(MSG_CASCADE_UNREACHABLE, anonServiceException.getService());
                    } else {
                        bl = true;
                        string = JAPMessages.getString(MSG_CASCADE_NOT_TRUSTED);
                    }
                } else if (anonServiceException instanceof ParseServiceException) {
                    string = JAPMessages.getString(MSG_CASCADE_NOT_PARSABLE);
                } else if (anonServiceException instanceof XMLErrorMessage) {
                    string = PaymentMainPanel.translateBIError((XMLErrorMessage)anonServiceException);
                    linkedInformationAdapter = PaymentMainPanel.translateBIErrorAdapter((XMLErrorMessage)anonServiceException, linkedInformationAdapter.isOnTop());
                } else {
                    string = JAPMessages.getString("statusCannotConnect");
                }
                string = string + "<br><br>" + JAPMessages.getString(MSG_ASK_SWITCH);
                if (bl) {
                    JAPConfAnon.showServiceUntrustedBox((MixCascade)anonServiceException.getService(), JAPController.this.getCurrentView(), TrustModel.getCurrentTrustModel());
                } else if (JAPDialog.showConfirmDialog(JAPController.this.getCurrentView(), string, 0, 0, (JAPDialog.ILinkedInformation)linkedInformationAdapter) == 0) {
                    JAPModel.getInstance().setAutoReConnect(true);
                    JAPModel.getInstance().setCascadeAutoSwitch(true);
                    JAPController.getInstance().start();
                }
                Object object = JAPController.this.SYNC_DISCONNECTED_ERROR;
                synchronized (object) {
                    JAPController.this.m_bDisconnectedErrorShown = false;
                }
            }
        }).start();
    }

    public final void showConfigDialog() {
        if (this.m_View != null && this.m_View instanceof AbstractJAPMainView) {
            ((AbstractJAPMainView)this.m_View).showConfigDialog();
        }
    }

    public void removeEventListener(AnonServiceEventListener anonServiceEventListener) {
        this.m_anonServiceListener.removeElement(anonServiceEventListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEventListener(AnonServiceEventListener anonServiceEventListener) {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                if (!anonServiceEventListener.equals(enumeration.nextElement())) continue;
                return;
            }
            this.m_anonServiceListener.addElement(anonServiceEventListener);
        }
    }

    public void addJAPObserver(JAPObserver jAPObserver) {
        this.observerVector.addElement(jAPObserver);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void notifyJAPObservers() {
        LogHolder.log(7, LogType.MISC, "JAPModel:notifyJAPObservers()");
        Vector vector = this.observerVector;
        synchronized (vector) {
            try {
                Enumeration enumeration = this.observerVector.elements();
                int n = 0;
                while (enumeration.hasMoreElements()) {
                    JAPObserver jAPObserver = (JAPObserver)enumeration.nextElement();
                    LogHolder.log(7, LogType.MISC, "JAPModel:notifyJAPObservers: " + n);
                    jAPObserver.updateValues(false);
                    ++n;
                }
            }
            catch (Throwable throwable) {
                LogHolder.log(0, LogType.MISC, "JAPModel:notifyJAPObservers - critical exception: " + throwable.getMessage());
            }
        }
        LogHolder.log(7, LogType.MISC, "JAPModel:notifyJAPObservers()-ended");
    }

    public synchronized void channelsChanged(int n) {
        Enumeration enumeration = this.observerVector.elements();
        while (enumeration.hasMoreElements()) {
            JAPObserver jAPObserver = (JAPObserver)enumeration.nextElement();
            jAPObserver.channelsChanged(n);
        }
    }

    public synchronized void transferedBytes(long l, int n) {
        long l2;
        if (n == 1) {
            this.m_nrOfBytesWWW += l;
            l2 = this.m_nrOfBytesWWW;
        } else if (n == 0) {
            this.m_nrOfBytesOther += l;
            l2 = this.m_nrOfBytesOther;
        } else {
            return;
        }
        Enumeration enumeration = this.observerVector.elements();
        while (enumeration.hasMoreElements()) {
            JAPObserver jAPObserver = (JAPObserver)enumeration.nextElement();
            jAPObserver.transferedBytes(l2, n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        block22: {
            try {
                if (observable == JAPModel.getInstance().getRoutingSettings()) {
                    if (((JAPRoutingMessage)object).getMessageCode() == 1) {
                        this.notifyJAPObservers();
                    }
                    if (((JAPRoutingMessage)object).getMessageCode() == 16) {
                        this.notifyJAPObservers();
                    }
                    break block22;
                }
                if (observable == this.m_mixContainer) {
                    this.setChanged();
                    this.notifyObservers(new JAPControllerMessage(2));
                    this.notifyJAPObservers();
                    break block22;
                }
                if (observable == JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver()) {
                    if (((JAPRoutingMessage)object).getMessageCode() == 14) {
                        this.notifyJAPObservers();
                    }
                    break block22;
                }
                if (observable == InfoServiceHolder.getInstance()) {
                    final InfoServiceHolderMessage infoServiceHolderMessage = (InfoServiceHolderMessage)object;
                    if (JAPModel.getInstance().isInfoServicePopupsHidden()) {
                        return;
                    }
                    Object object2 = this.SYNC_EXPIRED_IS_CERTS;
                    synchronized (object2) {
                        if (!this.m_bExpiredISCertificatesShown && infoServiceHolderMessage != null && infoServiceHolderMessage.getMessageData() != null) {
                            this.m_bExpiredISCertificatesShown = true;
                            new Thread(new Runnable(){

                                public void run() {
                                    JAPDialog.LinkedHelpContext linkedHelpContext = new JAPDialog.LinkedHelpContext("certificates");
                                    if (infoServiceHolderMessage.getMessageData() instanceof ExpiredSignatureException) {
                                        JAPDialog.showWarningDialog(JAPController.this.getCurrentView(), JAPMessages.getString(MSG_WARNING_IS_CERTS_EXPIRED), (JAPDialog.ILinkedInformation)linkedHelpContext);
                                    } else if (infoServiceHolderMessage.getMessageData() instanceof SignatureException) {
                                        JAPDialog.showWarningDialog(JAPController.this.getCurrentView(), JAPMessages.getString(MSG_WARNING_IS_CERTS_INVALID), (JAPDialog.ILinkedInformation)linkedHelpContext);
                                    }
                                    JAPController.this.m_bExpiredISCertificatesShown = false;
                                }
                            }).start();
                        }
                        break block22;
                    }
                }
                if (observable == Database.getInstance(class$anon$infoservice$JAPMinVersion == null ? (class$anon$infoservice$JAPMinVersion = JAPController.class$("anon.infoservice.JAPMinVersion")) : class$anon$infoservice$JAPMinVersion) && object != null && ((DatabaseMessage)object).getMessageData() instanceof JAPMinVersion) {
                    if (SoftwareUpdater.isShown()) {
                        return;
                    }
                    JAPMinVersion jAPMinVersion = (JAPMinVersion)((DatabaseMessage)object).getMessageData();
                    final String string = jAPMinVersion.getJapSoftware().getVersion().trim();
                    final boolean bl = string.compareTo("00.20.001") > 0;
                    new Thread(new Runnable(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        public void run() {
                            Object object = JAPController.this.LOCK_VERSION_UPDATE;
                            synchronized (object) {
                                if (JAPController.this.m_bShowingVersionUpdate) {
                                    return;
                                }
                                JAPController.this.m_bShowingVersionUpdate = true;
                            }
                            try {
                                JAPController.this.versionCheck(string, bl);
                            }
                            catch (Throwable throwable) {
                                LogHolder.log(2, LogType.MISC, throwable);
                            }
                            object = JAPController.this.LOCK_VERSION_UPDATE;
                            synchronized (object) {
                                JAPController.this.m_bShowingVersionUpdate = false;
                            }
                        }
                    }).start();
                    break block22;
                }
                if (observable == m_Model && object != null) {
                    if (!object.equals(JAPModel.CHANGED_ANONYMIZED_HTTP_HEADERS)) break block22;
                    Object object3 = this.PROXY_SYNC;
                    synchronized (object3) {
                        if (this.m_proxyAnon != null) {
                            this.m_proxyAnon.setHTTPHeaderProcessingEnabled(JAPModel.getInstance().isAnonymizedHttpHeaders(), true);
                            this.m_proxyAnon.setHTTPDecompressionEnabled(JAPModel.getInstance().isAnonymizedHttpHeaders());
                        }
                        break block22;
                    }
                }
                if (!(observable != Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = JAPController.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo) || object == null || object.equals(new Integer(5)) || !this.m_bConnectionUnused || this.getCurrentMixCascade() == AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE || !JAPModel.getInstance().isCascadeAutoSwitched() || TrustModel.getCurrentTrustModel().getAttribute(class$anon$client$TrustModel$SpeedAttribute == null ? (class$anon$client$TrustModel$SpeedAttribute = JAPController.class$("anon.client.TrustModel$SpeedAttribute")) : class$anon$client$TrustModel$SpeedAttribute).isTrusted(this.getCurrentMixCascade()) && TrustModel.getCurrentTrustModel().getAttribute(class$anon$client$TrustModel$DelayAttribute == null ? (class$anon$client$TrustModel$DelayAttribute = JAPController.class$("anon.client.TrustModel$DelayAttribute")) : class$anon$client$TrustModel$DelayAttribute).isTrusted(this.getCurrentMixCascade()))) {
                    this.switchToNextMixCascade();
                    LogHolder.log(4, LogType.NET, "Automatically switched service " + this.getCurrentMixCascade() + " due to bad performance!");
                }
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, exception);
            }
        }
    }

    public synchronized boolean enableForwardingServer(boolean bl) {
        Object object;
        if (!this.m_bForwarderNotExplain && bl) {
            object = new JAPDialog.LinkedCheckBox(false, "forwarding_server"){

                public boolean isOnTop() {
                    return true;
                }
            };
            if (JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString("forwardingExplainMessage"), JAPMessages.getString("forwardingExplainMessageTitle"), new JAPDialog.Options(2), 1, (JAPDialog.ILinkedInformation)object) != 0) {
                JAPModel.getInstance().getRoutingSettings().setRoutingMode(0);
                return false;
            }
            this.m_bForwarderNotExplain = ((JAPDialog.LinkedCheckBox)object).getState();
        }
        if (this.m_iStatusPanelMsgIdForwarderServerStatus != -1) {
            this.m_View.removeStatusMsg(this.m_iStatusPanelMsgIdForwarderServerStatus);
            this.m_iStatusPanelMsgIdForwarderServerStatus = -1;
        }
        if (JAPModel.getInstance().getRoutingSettings().getRoutingMode() != 1) {
            if (bl) {
                if (JAPModel.getInstance().getRoutingSettings().setRoutingMode(2)) {
                    object = new Thread(new Runnable(){

                        public void run() {
                            try {
                                String string = "<font color='red'>" + JAPMessages.getString(MSG_FORWARDER_REGISTRATION_ERROR_HEADER) + "</font><br><br>";
                                String string2 = "<br><br>" + JAPMessages.getString(MSG_FORWARDER_REGISTRATION_ERROR_FOOTER);
                                String string3 = null;
                                int n = JAPController.this.m_View.addStatusMsg(JAPMessages.getString("controllerStatusMsgRoutingStartServer"), 1, false);
                                int n2 = JAPModel.getInstance().getRoutingSettings().startPropaganda(true);
                                JAPController.this.m_View.removeStatusMsg(n);
                                switch (n2) {
                                    case 1: {
                                        string3 = string + JAPMessages.getString("settingsRoutingServerRegistrationEmptyListError") + string2;
                                        break;
                                    }
                                    case 2: {
                                        string3 = string + JAPMessages.getString("settingsRoutingServerRegistrationUnknownError") + string2;
                                        break;
                                    }
                                    case 3: {
                                        string3 = string + JAPMessages.getString("settingsRoutingServerRegistrationInfoservicesError") + string2;
                                        break;
                                    }
                                    case 4: {
                                        StringBuffer stringBuffer = new StringBuffer().append(string);
                                        StringBuffer stringBuffer2 = new StringBuffer().append("<b>");
                                        JAPModel.getInstance().getRoutingSettings();
                                        string3 = stringBuffer.append(JAPMessages.getString("settingsRoutingServerRegistrationVerificationError", stringBuffer2.append(JAPRoutingSettings.getServerPort()).append("</b>").toString())).append(string2).toString();
                                        break;
                                    }
                                    case 0: {
                                        JAPController.this.m_iStatusPanelMsgIdForwarderServerStatus = JAPController.this.m_View.addStatusMsg(JAPMessages.getString("controllerStatusMsgRoutingStartServerSuccess"), 1, true);
                                    }
                                }
                                if (string3 != null) {
                                    JAPDialog.showErrorDialog(JAPController.this.getCurrentView(), string3, (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("forwarding_server"));
                                }
                            }
                            catch (Exception exception) {
                                LogHolder.log(2, LogType.MISC, exception);
                            }
                        }
                    });
                    ((Thread)object).setDaemon(true);
                    ((Thread)object).start();
                } else {
                    this.m_iStatusPanelMsgIdForwarderServerStatus = this.m_View.addStatusMsg(JAPMessages.getString("controllerStatusMsgRoutingStartServerError"), 0, true);
                    JAPDialog.showErrorDialog(this.getCurrentView(), JAPMessages.getString("settingsRoutingStartServerError"));
                }
            } else {
                JAPModel.getInstance().getRoutingSettings().setRoutingMode(0);
                this.m_iStatusPanelMsgIdForwarderServerStatus = this.m_View.addStatusMsg(JAPMessages.getString("controllerStatusMsgRoutingServerStopped"), 1, true);
            }
        }
        return true;
    }

    public static InfoServiceDBEntry[] createDefaultInfoServices() throws Exception {
        return anon.util.Util.createDefaultInfoServices(JAPConstants.DEFAULT_INFOSERVICE_NAMES, JAPConstants.DEFAULT_INFOSERVICE_HOSTNAMES, JAPConstants.DEFAULT_INFOSERVICE_PORT_NUMBERS);
    }

    private static void addDefaultCertificates(String string, String[] arrstring, int n) {
        Util.addDefaultCertificates(string, arrstring, n, ".dev");
    }

    public static void addDefaultCertificates() {
        JAPController.addDefaultCertificates("acceptedMixCAs/", JAPConstants.MIX_ROOT_CERTS, 1);
        JAPController.addDefaultCertificates("acceptedMixOperators/", null, 2);
        JAPController.addDefaultCertificates("acceptedInfoServices/", JAPConstants.INFOSERVICE_CERTS, 3);
        JAPController.addDefaultCertificates("acceptedInfoServiceCAs/", JAPConstants.INFOSERVICE_ROOT_CERTS, 5);
        JAPController.addDefaultCertificates("acceptedTaCTemplates/", JAPConstants.TERMS_CERTS, 9);
        JAPController.addDefaultCertificates("acceptedPaymentCAs/", JAPConstants.PAYMENT_ROOT_CERTS, 8);
        JAPController.addDefaultCertificates("acceptedPIs/", JAPConstants.PI_CERTS, 7);
        JAPCertificate jAPCertificate = JAPCertificate.getInstance(ResourceLoader.loadResource("certificates/japupdatemessages.cer"));
        if (jAPCertificate != null) {
            SignatureVerifier.getInstance().getVerificationCertificateStore().addCertificateWithoutVerification(jAPCertificate, 4, true, true);
        } else {
            LogHolder.log(3, LogType.MISC, "Error loading default update messages certificate.");
        }
    }

    public boolean allowDirectProxyDomain(URL uRL) {
        boolean bl = false;
        DirectProxy directProxy = this.m_proxyDirect;
        if (JAPModel.getInstance().isNonAnonymousAllowed() && JAPModel.getInstance().getPaymentAnonymousConnectionSetting() != 1 && directProxy != null) {
            bl = directProxy.allowDomain(uRL);
        }
        if (!bl) {
            LogHolder.log(3, LogType.GUI, "Could not allow direct proxy access to web page: " + (uRL == null ? "*URL not given*" : uRL.toString()));
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connecting(AnonServerDescription anonServerDescription, boolean bl) {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).connecting(anonServerDescription, bl);
            }
        }
    }

    public void currentServiceChanged(AnonServerDescription anonServerDescription) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connectionEstablished(AnonServerDescription anonServerDescription) {
        if (!JAPModel.isInfoServiceDisabled()) {
            m_feedback.updateAsync(null);
        }
        Object object = this.m_anonServiceListener;
        synchronized (object) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).connectionEstablished(anonServerDescription);
            }
        }
        this.transferedBytes(0L, 1);
        this.transferedBytes(0L, 0);
        if (!this.isMultipleInstancesAllowed() && this.isPortableMode() && m_Model.getStartPortableFirefox() && !this.m_firstPortableFFStart) {
            LogHolder.log(7, LogType.MISC, "First browser start");
            this.m_firstPortableFFStart = true;
            AbstractOS.getInstance().openURL(null);
        }
        if (this.m_thRunnableShowConfigAssistant != null) {
            object = this.m_thRunnableShowConfigAssistant.SYNC;
            synchronized (object) {
                this.m_thRunnableShowConfigAssistant.SYNC.notify();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dataChainErrorSignaled(AnonServiceException anonServiceException) {
        this.connectionError(anonServiceException);
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).dataChainErrorSignaled(anonServiceException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void integrityErrorSignaled(AnonServiceException anonServiceException) {
        int n;
        String string = anonServiceException.getErrorCode() == -33 ? "Possible attack: Integrity check failed in downstream packet!" : "Possible attack: Integrity check failed in upstream packet!";
        LogHolder.log(0, LogType.NET, string);
        MixCascade mixCascade = (MixCascade)anonServiceException.getService();
        if (JAPModel.getInstance().isAnonymityPopupsHidden()) {
            n = 0;
        } else {
            JAPDialog.LinkedCheckBox linkedCheckBox = new JAPDialog.LinkedCheckBox(JAPMessages.getString(MSG_BLACKLIST_CASCADE), false){

                public boolean isOnTop() {
                    return true;
                }
            };
            n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_INTEGRITY_ERROR), 0, 0, (JAPDialog.ILinkedInformation)linkedCheckBox);
            if (linkedCheckBox.getState()) {
                Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPController.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).update(new BlacklistedCascadeIDEntry(mixCascade));
            }
        }
        if (n == 0 && this.getAnonMode() && mixCascade.equals(this.switchToNextMixCascade())) {
            this.stop();
        }
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).integrityErrorSignaled(anonServiceException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnected() {
        Object object = this.m_finishSync;
        synchronized (object) {
            if (this.m_proxyAnon != null) {
                // empty if block
            }
            this.m_nrOfBytesWWW = 0L;
            this.m_nrOfBytesOther = 0L;
            this.transferedBytes(0L, 1);
            this.transferedBytes(0L, 0);
            Vector vector = this.m_anonServiceListener;
            synchronized (vector) {
                Enumeration enumeration = this.m_anonServiceListener.elements();
                while (enumeration.hasMoreElements()) {
                    ((AnonServiceEventListener)enumeration.nextElement()).disconnected();
                }
            }
            this.m_finishSync.notifyAll();
        }
        if (this.m_thRunnableShowConfigAssistant != null) {
            object = this.m_thRunnableShowConfigAssistant.SYNC;
            synchronized (object) {
                this.m_thRunnableShowConfigAssistant.SYNC.notify();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connectionError(AnonServiceException anonServiceException) {
        if (!JAPModel.isAutomaticallyReconnected() || anonServiceException instanceof INotRecoverableException) {
            this.stop();
            this.showNotRecoverableExceptionDialog(anonServiceException);
        }
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).connectionError(anonServiceException);
            }
        }
    }

    public String getPaymentPassword() {
        return JAPModel.getInstance().getPaymentPassword();
    }

    public void setPaymentPassword(String string) {
        JAPModel.getInstance().setPaymentPassword(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void packetMixed(long l) {
        this.m_bConnectionUnused = l == 0L;
        JobQueue.Job job = new JobQueue.Job(true){

            public void runJob() {
                PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
                MixCascade mixCascade = JAPController.this.getCurrentMixCascade();
                if (payAccount == null || !mixCascade.isPayment()) {
                    return;
                }
            }
        };
        this.queueFetchAccountInfo.addJob(job);
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).packetMixed(l);
            }
        }
    }

    public boolean confirmTermsAndConditions(Vector vector, Vector vector2) {
        TermsAndConditionsInfoDialog termsAndConditionsInfoDialog = new TermsAndConditionsInfoDialog((Component)JAPController.getInstance().getViewWindow(), vector, this.getCurrentMixCascade() != null ? this.getCurrentMixCascade().getName() : "");
        termsAndConditionsInfoDialog.setVisible(true);
        TermsAndConditionsResponseHandler.get().notifyAboutChanges();
        return termsAndConditionsInfoDialog.areAllAccepted();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class RunnableShowConfigAssistant
    extends Thread {
        public final Object SYNC = new Object();
        private static final long MINUTES_WAIT = 120000L;
        private boolean bShown = false;
        private long lLastStartup = Long.MAX_VALUE;

        private RunnableShowConfigAssistant() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Object object = null;
            while (!Thread.currentThread().isInterrupted()) {
                Object object2;
                if (object != null) {
                    object2 = ((ConfigAssistant)object).getSyncStarted();
                    synchronized (object2) {
                        if (((JAPDialog)object).isVisible() || ((JAPDialog)object).isDisplayable()) {
                            if (!(((ConfigAssistant)object).getSyncStarted().isTrue() || JAPController.this.getAnonMode() && !JAPController.this.isAnonConnected())) {
                                ((JAPDialog)object).dispose();
                                object = null;
                            }
                        } else {
                            object = null;
                        }
                    }
                }
                if (JAPController.this.isConfigAssistantShown() || JAPController.this.m_bShutdown) {
                    this.bShown = true;
                } else if (!JAPController.this.getAnonMode() || JAPController.this.isAnonConnected()) {
                    this.lLastStartup = Long.MAX_VALUE;
                    this.bShown = false;
                } else if (this.lLastStartup == Long.MAX_VALUE) {
                    this.lLastStartup = System.currentTimeMillis();
                } else if (!this.bShown && this.lLastStartup + 120000L < System.currentTimeMillis() && JAPModel.getInstance().isConfigAssistantAutomaticallyShown() && !JAPModel.getInstance().isAnonymityPopupsHidden()) {
                    this.bShown = true;
                    LogHolder.log(4, LogType.GUI, "Could not get a connection. Opening installation assistant...");
                    object2 = JAPController.this.showInstallationAssistant(4);
                    if (object2 != null) {
                        object = object2;
                    }
                }
                object2 = this.SYNC;
                synchronized (object2) {
                    try {
                        this.SYNC.wait(1000L);
                    }
                    catch (InterruptedException interruptedException) {
                        return;
                    }
                }
            }
        }
    }

    private static class WarnNoJonDoFoxHttpListener
    extends HttpConnectionListenerAdapter {
        private final Object SYNC = new Object();
        private static boolean ms_bWarned;
        private static boolean ms_bShowWarning;

        public WarnNoJonDoFoxHttpListener(int n, boolean bl) {
            super(n);
            ms_bWarned = !bl;
            ms_bShowWarning = bl;
        }

        public boolean isWarningShownOnInsecureBrowser() {
            return ms_bShowWarning;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void responseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
            boolean bl = false;
            Object object = this.SYNC;
            synchronized (object) {
                if (ms_bWarned) {
                    return;
                }
                if (hTTPConnectionEvent != null && hTTPConnectionEvent.getAnonRequest().isBrowserWarningShown() && HTTPProxyCallback.redirect(hTTPConnectionEvent.getConnectionHeader(), 0)) {
                    ms_bWarned = true;
                    bl = true;
                }
            }
            if (bl) {
                new Thread(new Runnable(){

                    public void run() {
                        JAPDialog.LinkedCheckBox linkedCheckBox = new JAPDialog.LinkedCheckBox(false, "jondofox"){

                            public boolean isOnTop() {
                                return true;
                            }
                        };
                        JAPDialog.showWarningDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_WARNING_BROWSER_NOT_OPTIMIZED) + "<br/><br/>" + JAPMessages.getString(MSG_WARNING_CHECK_JONDOFOX), (JAPDialog.ILinkedInformation)linkedCheckBox);
                        if (linkedCheckBox.getState()) {
                            ms_bShowWarning = false;
                        }
                    }
                }).start();
            }
        }

        public void requestHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
            if (!ms_bWarned && HTTPProxyCallback.isAnonymityTestDomain(hTTPConnectionEvent.getConnectionHeader())) {
                ms_bWarned = true;
            }
        }
    }

    private static class ConfigAssistantHttpListener
    extends HttpConnectionListenerAdapter {
        private boolean m_bForceRedirect = false;
        private boolean m_bRedirected = false;
        private boolean m_bHasVisitedWebsite = false;

        public ConfigAssistantHttpListener(int n, boolean bl) {
            super(n);
            this.m_bHasVisitedWebsite = bl;
        }

        public boolean hasVisitedWebsite() {
            return this.m_bHasVisitedWebsite;
        }

        public synchronized void forceRedirect(boolean bl) {
            this.m_bForceRedirect = bl;
            this.m_bRedirected = false;
        }

        public boolean hasRedirected() {
            return this.m_bRedirected;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void responseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
            Object object;
            if (this.m_bForceRedirect && hTTPConnectionEvent != null && hTTPConnectionEvent.getAnonRequest().isBrowserWarningShown() && HTTPProxyCallback.redirect(hTTPConnectionEvent.getConnectionHeader(), 0)) {
                object = this;
                synchronized (object) {
                    this.m_bForceRedirect = false;
                    this.m_bRedirected = true;
                }
            }
            if (this.m_bHasVisitedWebsite || hTTPConnectionEvent.getConnectionHeader() == null) {
                return;
            }
            object = hTTPConnectionEvent.getConnectionHeader().getResponseHeader("Content-Type");
            if (object != null && ((String[])object).length > 0 && hTTPConnectionEvent.getConnectionHeader().parseStatus() == 200 && hTTPConnectionEvent.getConnectionHeader().getRequestLine().startsWith("GET")) {
                for (int i = 0; i < ((String[])object).length; ++i) {
                    if (object[i].toLowerCase().indexOf("text/html") < 0) continue;
                    this.m_bHasVisitedWebsite = true;
                    break;
                }
            }
        }

        public boolean isBlockable() {
            return false;
        }

        public void requestHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
            if (this.m_bForceRedirect) {
                hTTPConnectionEvent.getAnonRequest().showBrowserWarning(true);
            }
            if (this.m_bHasVisitedWebsite && !this.m_bForceRedirect || hTTPConnectionEvent.getConnectionHeader() == null) {
                return;
            }
            if (HTTPProxyCallback.isAnonymityTestDomain(hTTPConnectionEvent.getConnectionHeader())) {
                this.m_bForceRedirect = false;
                this.m_bRedirected = true;
                this.m_bHasVisitedWebsite = true;
                return;
            }
            if (!this.m_bHasVisitedWebsite && HTTPProxyCallback.isJonDosDomain(hTTPConnectionEvent.getConnectionHeader())) {
                this.m_bHasVisitedWebsite = true;
                return;
            }
        }
    }

    private class WarnSmallBalanceOnDownloadListener
    extends HttpConnectionListenerAdapter {
        public WarnSmallBalanceOnDownloadListener(int n) {
            super(n);
        }

        public void responseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
            if (JAPDialog.isConsoleOnly() || hTTPConnectionEvent == null || hTTPConnectionEvent.getConnectionHeader() == null) {
                return;
            }
            String[] arrstring = hTTPConnectionEvent.getConnectionHeader().getResponseHeader("Content-Length");
            long l = 0L;
            if (arrstring == null || arrstring.length == 0) {
                return;
            }
            try {
                l = Long.parseLong(arrstring[0]);
            }
            catch (NumberFormatException numberFormatException) {
                LogHolder.log(4, LogType.FILTER, numberFormatException);
                return;
            }
            final PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
            if (payAccount == null) {
                return;
            }
            if (l > 5000000L && JAPController.this.isAnonConnected() && JAPController.this.getCurrentMixCascade().isPayment()) {
                final JAPDialog.LinkedInformationAdapter linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                    public boolean isOnTop() {
                        return true;
                    }
                };
                final JAPDialog.Options options = new JAPDialog.Options(2){

                    public String getYesOKText() {
                        return JAPMessages.getString(MSG_WARNING_SHORT_BALANCE_CONTINUE);
                    }
                };
                int n = 0;
                final long l2 = l;
                if ((double)l * 1.1 > (double)(payAccount.getCurrentCredit() + JAPController.this.getCurrentMixCascade().getPrepaidInterval())) {
                    LogHolder.log(4, LogType.PAY, "Insufficient balance for downloading file!");
                    IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                        private Integer m_retVal = null;

                        public void run() {
                            String string = JAPMessages.getString(MSG_WARNING_INSUFFICIENT_BALANCE, new String[]{anon.util.Util.formatBytesValueWithUnit(l2), anon.util.Util.formatBytesValueWithUnit(payAccount.getCurrentCredit())});
                            if (payAccount.canDoMonthlyOverusage(new Timestamp(System.currentTimeMillis()))) {
                                this.m_retVal = new Integer(PaymentMainPanel.showMonthlyOverusageQuestion(payAccount, JAPController.this.getCurrentView(), string, JAPMessages.getString(MSG_WARNING_SHORT_BALANCE_CONTINUE), null));
                            } else {
                                string = string + " " + JAPMessages.getString(MSG_CONFIRM_IGNORE_WARNING_SHORT_BALANCE);
                                this.m_retVal = new Integer(JAPDialog.showConfirmDialog(JAPController.this.getCurrentView(), string, options, 2, (JAPDialog.ILinkedInformation)linkedInformationAdapter));
                            }
                        }

                        public Object getValue() {
                            return this.m_retVal;
                        }
                    };
                    Thread thread = new Thread(iReturnRunnable);
                    thread.start();
                    try {
                        thread.join(60000L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    if (iReturnRunnable.getValue() == null) {
                        while (thread.isAlive()) {
                            thread.interrupt();
                            try {
                                thread.join(200L);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            Thread.yield();
                        }
                        n = 2;
                    } else {
                        n = (Integer)iReturnRunnable.getValue();
                    }
                } else if ((double)l * 1.3 > (double)payAccount.getCurrentCredit()) {
                    LogHolder.log(4, LogType.PAY, "Balance might be insufficient balance for downloading file.");
                    IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                        private Integer m_retVal = null;

                        public void run() {
                            String string = JAPMessages.getString(MSG_WARNING_SHORT_BALANCE, new String[]{anon.util.Util.formatBytesValueWithUnit(l2), anon.util.Util.formatBytesValueWithUnit(payAccount.getCurrentCredit())});
                            if (payAccount.canDoMonthlyOverusage(new Timestamp(System.currentTimeMillis()))) {
                                this.m_retVal = new Integer(PaymentMainPanel.showMonthlyOverusageQuestion(payAccount, JAPController.this.getCurrentView(), string, JAPMessages.getString(MSG_WARNING_SHORT_BALANCE_CONTINUE), null));
                            } else {
                                string = string + " " + JAPMessages.getString(MSG_CONFIRM_IGNORE_WARNING_SHORT_BALANCE);
                                this.m_retVal = new Integer(JAPDialog.showConfirmDialog(JAPController.this.getCurrentView(), string, options, 2, (JAPDialog.ILinkedInformation)linkedInformationAdapter));
                            }
                        }

                        public Object getValue() {
                            return this.m_retVal;
                        }
                    };
                    Thread thread = new Thread(iReturnRunnable);
                    thread.start();
                    try {
                        thread.join(60000L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    if (iReturnRunnable.getValue() == null) {
                        while (thread.isAlive()) {
                            thread.interrupt();
                            try {
                                thread.join(200L);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            Thread.yield();
                        }
                        n = 0;
                    } else {
                        n = (Integer)iReturnRunnable.getValue();
                    }
                }
                if (n != 0) {
                    hTTPConnectionEvent.getConnectionHeader().replaceResponseHeader("Connection", "close");
                    hTTPConnectionEvent.getConnectionHeader().replaceResponseHeader("Content-Length", "0");
                    if (hTTPConnectionEvent.getConnectionHeader().getResponseLine() != null && hTTPConnectionEvent.getConnectionHeader().getResponseLine().indexOf("HTTP/1.1") >= 0) {
                        hTTPConnectionEvent.getConnectionHeader().replaceResponseLine("HTTP/1.1 204 No Content");
                    } else {
                        hTTPConnectionEvent.getConnectionHeader().replaceResponseLine("HTTP/1.0 204 No Content");
                    }
                }
            }
        }
    }

    private class AutoSwitchedMixCascadeContainer
    extends AbstractAutoSwitchedMixCascadeContainer {
        public AutoSwitchedMixCascadeContainer(boolean bl) {
            super(bl, JAPController.getInstance().getCurrentMixCascade(), null);
        }

        public AutoSwitchedMixCascadeContainer() {
            this(false);
        }

        public boolean hasUserAllowedPaidServices(String string) {
            if (JAPController.this.isConfigAssistantShown() && JAPController.this.m_bAssistantClicked && !AccountCreator.checkValidAccount(string)) {
                return false;
            }
            return !(JAPController.this.m_lAllowPaidServices >= System.currentTimeMillis() && TrustModel.getCurrentTrustModel().hasFreeCascades() || JAPController.this.m_View != null && JAPController.this.m_View.isShowingPaymentError());
        }

        public boolean isServiceAutoSwitched() {
            return JAPModel.getInstance().isCascadeAutoSwitched();
        }

        public boolean isReconnectedAutomatically() {
            return JAPModel.isAutomaticallyReconnected();
        }
    }

    private final class SetAnonModeAsync
    extends JobQueue.Job {
        private boolean m_startServer;

        public SetAnonModeAsync(boolean bl) {
            this.m_startServer = bl;
        }

        public boolean isInterrupting() {
            return !this.m_startServer;
        }

        public boolean equals(Object object) {
            if (!(object instanceof SetAnonModeAsync) || object == null) {
                return false;
            }
            return ((SetAnonModeAsync)object).isStartServerJob() == this.isStartServerJob();
        }

        public int hashCode() {
            if (this.isStartServerJob()) {
                return 1;
            }
            return 0;
        }

        public String getAddedJobLogMessage() {
            return "Added a job for changing the anonymity mode to '" + new Boolean(this.isStartServerJob()).toString() + "' to the job queue.";
        }

        public boolean isStartServerJob() {
            return this.m_startServer;
        }

        public void runJob() {
            if (!Thread.currentThread().isInterrupted()) {
                if (JAPController.this.getAnonMode() && this.m_startServer) {
                    return;
                }
                try {
                    if (this.m_startServer) {
                        JAPController.this.m_bConnecting = true;
                    }
                    this.setServerMode(this.m_startServer);
                }
                catch (Throwable throwable) {
                    LogHolder.log(2, LogType.NET, "Error while setting server mode to " + this.m_startServer + "!", throwable);
                }
                JAPController.this.m_bConnecting = false;
                LogHolder.log(7, LogType.MISC, "Job for changing the anonymity mode to '" + new Boolean(this.m_startServer).toString() + "' was executed.");
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private synchronized void setServerMode(boolean bl) {
            int n = 0;
            if (!bl) {
                try {
                    if (JAPController.this.m_proxyAnon != null) {
                        n = JAPController.this.m_View.addStatusMsg(JAPMessages.getString("setAnonModeSplashDisconnect"), 1, false);
                    }
                    JAPController.this.m_proxyAnon.stop();
                }
                catch (NullPointerException nullPointerException) {
                    // empty catch block
                }
                if (n != 0) {
                    JAPController.this.m_View.removeStatusMsg(n);
                }
            }
            Object object = JAPController.this.PROXY_SYNC;
            synchronized (object) {
                boolean bl2 = true;
                LogHolder.log(7, LogType.MISC, "setAnonMode(" + bl + ")");
                if (bl && (JAPController.this.m_proxyAnon == null || !JAPController.this.m_proxyAnon.getMixCascade().equals(JAPController.this.getCurrentMixCascade()))) {
                    Object object2;
                    boolean bl3 = JAPController.this.m_proxyAnon != null;
                    AnonServiceException anonServiceException = null;
                    n = JAPController.this.m_View.addStatusMsg(JAPMessages.getString("setAnonModeSplashConnect"), 1, false);
                    boolean bl4 = false;
                    if (JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 1) {
                        bl4 = true;
                        JAPController.this.m_proxyAnon = JAPModel.getInstance().getRoutingSettings().getAnonProxyInstance(JAPController.this.m_proxyDirect);
                    } else if (!bl3) {
                        JAPController.this.m_proxyAnon = new AnonProxy(JAPController.this.m_proxyDirect, JAPModel.getInstance().getMutableProxyInterface(), (TermsAndConditionConfirmation)JAPController.getInstance());
                    }
                    if (!JAPModel.getInstance().isHeaderProcessingDisabled()) {
                        JAPController.this.m_proxyAnon.setHTTPHeaderProcessingEnabled(JAPModel.getInstance().isAnonymizedHttpHeaders(), true);
                        JAPController.this.m_proxyAnon.addHTTPConnectionListener(JAPController.this.m_httpListenerConfigAssistant);
                        JAPController.this.m_proxyAnon.addHTTPConnectionListener(JAPController.this.m_httpListenerXHeaders);
                        JAPController.this.m_proxyAnon.setHTTPDecompressionEnabled(JAPModel.getInstance().isAnonymizedHttpHeaders());
                    } else {
                        JAPController.this.m_proxyAnon.setHTTPHeaderProcessingEnabled(false, false);
                        JAPController.this.m_proxyAnon.setHTTPDecompressionEnabled(false);
                    }
                    if (!JAPModel.isInfoServiceDisabled()) {
                        m_feedback.updateAsync(null);
                    }
                    if (JAPModel.getInstance().isANONDebugMode()) {
                        // empty if block
                    }
                    JAPController.this.m_proxyAnon.addEventListener(JAPController.getInstance());
                    if (!bl3) {
                        if (!JAPModel.getInstance().isTorActivated() || !bl4) {
                            // empty if block
                        }
                        JAPController.this.m_proxyAnon.setTorParams(null);
                        if (!JAPModel.getInstance().isMixMinionActivated() || !bl4) {
                            // empty if block
                        }
                        JAPController.this.m_proxyAnon.setMixminionParams(null);
                        JAPController.this.m_proxyAnon.setProxyListener(m_Controller);
                        JAPController.this.m_proxyAnon.setDummyTraffic(JAPModel.getDummyTraffic());
                        JAPController.this.m_proxyAnon.setInterfaceBlockTimeout(JAPModel.getInterfaceBlockTimeout());
                        LogHolder.log(7, LogType.NET, "Try to start AN.ON service...");
                    }
                    try {
                        JAPController.this.m_proxyAnon.start(JAPController.this.m_mixContainer);
                    }
                    catch (AnonServiceException anonServiceException2) {
                        anonServiceException = anonServiceException2;
                    }
                    JAPDialog.LinkedInformationAdapter linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                        public boolean isOnTop() {
                            return true;
                        }
                    };
                    if (anonServiceException instanceof INotRecoverableException) {
                        bl2 = false;
                        JAPController.this.m_proxyDirect.reset();
                        JAPController.this.m_proxyAnon.stop();
                        JAPController.this.m_proxyAnon = null;
                        JAPController.this.showNotRecoverableExceptionDialog(anonServiceException);
                    } else if (anonServiceException == null || !(anonServiceException instanceof ServiceInterruptedException) && JAPController.this.m_mixContainer.isReconnectedAutomatically()) {
                        object2 = JAPController.this.m_proxyAnon;
                        AnonServiceEventAdapter anonServiceEventAdapter = new AnonServiceEventAdapter(){
                            boolean bWaitingForConnection = true;

                            public synchronized void connectionEstablished(AnonServerDescription anonServerDescription) {
                                if (this.bWaitingForConnection) {
                                    JAPController.getInstance().removeEventListener(this);
                                    this.bWaitingForConnection = false;
                                }
                            }
                        };
                        if (anonServiceException == null) {
                            LogHolder.log(7, LogType.NET, "AN.ON service started successfully");
                            anonServiceEventAdapter.connectionEstablished(((AnonProxy)object2).getMixCascade());
                        } else {
                            JAPController.getInstance().addEventListener(anonServiceEventAdapter);
                            LogHolder.log(6, LogType.NET, "AN.ON service not connected. Trying reconnect...");
                        }
                        if (!JAPModel.isInfoServiceDisabled()) {
                            m_feedback.updateAsync(null);
                        }
                    } else {
                        bl2 = false;
                        JAPController.this.m_proxyDirect.reset();
                        JAPController.this.m_proxyAnon.stop();
                        JAPController.this.m_proxyAnon = null;
                        if (!JAPModel.isSmallDisplay() && !(anonServiceException instanceof ServiceInterruptedException)) {
                            LogHolder.log(3, LogType.NET, "Error starting AN.ON service!", anonServiceException);
                            JAPController.this.showRequestAutoReconnectDialog(JAPMessages.getString("errorConnectingFirstMix"));
                        }
                    }
                    linkedInformationAdapter = null;
                    JAPController.this.notifyJAPObservers();
                    JAPController.this.m_View.removeStatusMsg(n);
                    if (bl2 && !JAPModel.isInfoServiceDisabled()) {
                        object2 = null;
                        try {
                            object2 = JAPController.this.m_proxyAnon.getMixCascade();
                        }
                        catch (NullPointerException nullPointerException) {
                            // empty catch block
                        }
                        if (!(JAPModel.isInfoServiceDisabled() || object2 == null || ((MixCascade)object2).isUserDefined() || !((MixCascade)object2).isFromCascade() && Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(((MixCascade)object2).getId()) != null)) {
                            Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPController.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).update(InfoServiceHolder.getInstance().getMixCascadeInfo(((MixCascade)object2).getId()));
                        }
                    }
                } else if (JAPController.this.m_proxyAnon != null && !bl) {
                    JAPController.this.m_proxyDirect.reset();
                    AnonProxy anonProxy = JAPController.this.m_proxyAnon;
                    if (anonProxy != null) {
                        n = JAPController.this.m_View.addStatusMsg(JAPMessages.getString("setAnonModeSplashDisconnect"), 1, false);
                        anonProxy.stop();
                        JAPController.this.m_View.removeStatusMsg(n);
                    }
                    Object object3 = JAPController.this.m_finishSync;
                    synchronized (object3) {
                        JAPController.this.m_proxyAnon = null;
                        JAPController.this.m_finishSync.notifyAll();
                    }
                    JAPModel.getInstance().getRoutingSettings().anonConnectionClosed();
                    JAPController.this.notifyJAPObservers();
                    if (JAPController.this.m_thRunnableShowConfigAssistant != null) {
                        object3 = ((JAPController)JAPController.this).m_thRunnableShowConfigAssistant.SYNC;
                        synchronized (object3) {
                            ((JAPController)JAPController.this).m_thRunnableShowConfigAssistant.SYNC.notify();
                        }
                    }
                }
            }
        }
    }

    public class AnonConnectionChecker {
        public boolean checkAnonConnected() {
            return JAPController.this.isAnonConnected();
        }
    }

    public static interface IRestarter {
        public void exec(String[] var1) throws IOException;

        public boolean isConfigFileSaved();

        public boolean hideWarnings();
    }

    public static interface ProgramExitListener {
        public void programExiting();
    }
}

