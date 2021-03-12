/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.AnonServerDescription;
import anon.client.AbstractAutoSwitchedMixCascadeContainer;
import anon.client.TrustModel;
import anon.crypto.MultiCertPath;
import anon.error.AnonServiceException;
import anon.error.ServiceInterruptedException;
import anon.error.TrustException;
import anon.infoservice.CascadeIDEntry;
import anon.infoservice.ClickedMessageIDDBEntry;
import anon.infoservice.Database;
import anon.infoservice.DatabaseMessage;
import anon.infoservice.DeletedMessageIDDBEntry;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.JavaVersionDBEntry;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MessageDBEntry;
import anon.infoservice.MixCascade;
import anon.infoservice.MixCascadeExitAddresses;
import anon.infoservice.MixInfo;
import anon.infoservice.NewCascadeIDEntry;
import anon.infoservice.PerformanceEntry;
import anon.infoservice.PerformanceInfo;
import anon.infoservice.StatusInfo;
import anon.pay.IMessageListener;
import anon.pay.PayAccountsFile;
import anon.pay.PayMessage;
import anon.platform.AbstractOS;
import anon.platform.WindowsOS;
import anon.util.AbstractMessage;
import anon.util.CountryMapper;
import anon.util.JAPMessages;
import anon.util.JobQueue;
import anon.util.Util;
import gui.DataRetentionDialog;
import gui.FlippingPanel;
import gui.GUIUtils;
import gui.JAPDll;
import gui.JAPHelpContext;
import gui.JAPProgressBar;
import gui.MixDetailsDialog;
import gui.dialog.DialogContentPane;
import gui.dialog.JAPDialog;
import gui.help.JAPHelp;
import jap.AbstractJAPMainView;
import jap.IJAPMainView;
import jap.JAPConf;
import jap.JAPConfNetwork;
import jap.JAPConfUpdate;
import jap.JAPController;
import jap.JAPMixCascadeComboBox;
import jap.JAPModel;
import jap.JAPObserver;
import jap.JAPUtil;
import jap.JAPViewIconified;
import jap.SoftwareUpdater;
import jap.StatusPanel;
import jap.SystrayPopupMenu;
import jap.forward.JAPRoutingMessage;
import jap.forward.JAPRoutingServerStatisticsListener;
import jap.forward.JAPRoutingSettings;
import jap.pay.AccountCreator;
import jap.pay.IPaymentDialogPresentator;
import jap.pay.PaymentMainPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import logging.LogHolder;
import logging.LogType;

public final class JAPNewView
extends AbstractJAPMainView
implements IJAPMainView,
ActionListener,
JAPObserver,
Observer,
IMessageListener,
IPaymentDialogPresentator {
    private static final long serialVersionUID = 1L;
    public static final String MSG_UPDATE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_update";
    public static final String MSG_NO_REAL_PAYMENT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_noRealPayment";
    public static final String MSG_UNKNOWN_PERFORMANCE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_unknownPerformance";
    public static final String MSG_USERS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_users";
    public static final String MSG_SERVICE_NAME = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_ngAnonymisierungsdienst";
    private static final String MSG_BROWSER = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".btnBrowser";
    private static final String MSG_BROWSER_MNEMONIC = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".btnBrowserMnemonic";
    private static final String MSG_ANONYMETER_TOOL_TIP = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_anonymeterToolTip";
    private static final String MSG_ERROR_DISCONNECTED = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_errorDisconnected";
    private static final String MSG_ERROR_PROXY = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_errorProxy";
    private static final String MSG_TITLE_OLD_JAVA = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_titleOldJava";
    private static final String MSG_OLD_JAVA = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_oldJava";
    private static final String MSG_OLD_JAVA_HINT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_oldJavaHint";
    private static final String MSG_LBL_NEW_SERVICES_FOUND = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_newServicesFound";
    private static final String MSG_TOOLTIP_NEW_SERVICES_FOUND = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_tooltipNewServicesFound";
    private static final String MSG_NEW_SERVICES_FOUND_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_newServicesFoundExplanation";
    private static final String MSG_NO_COSTS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_noCosts";
    private static final String MSG_WITH_COSTS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_withCosts";
    public static final String MSG_BTN_ASSISTANT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_btnAssistant";
    private static final String MSG_MN_ASSISTANT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_mnAssistant";
    private static final String MSG_MN_DETAILS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_mnDetails";
    private static final String MSG_IS_DISABLED_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_isDisabledExplain";
    private static final String MSG_IS_DEACTIVATED = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_isDisabled";
    private static final String MSG_IS_TOOLTIP = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_isDisabledTooltip";
    private static final String MSG_IS_TRUST_PARANOID = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_trustParanoid";
    private static final String MSG_IS_TRUST_SUSPICIOUS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_trustSuspicious";
    private static final String MSG_IS_TRUST_HIGH = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_trustHigh";
    private static final String MSG_IS_TRUST_ALL = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_trustAll";
    private static final String MSG_IS_EDIT_TRUST = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_editTrust";
    private static final String MSG_TRUST_FILTER = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_trustFilter";
    private static final String MSG_CONNECTED = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_connected";
    private static final String MSG_DELETE_MESSAGE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_deleteMessage";
    private static final String MSG_HIDE_MESSAGE_SHORT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_hideMessageShort";
    private static final String MSG_DELETE_MESSAGE_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_deleteMessageExplain";
    private static final String MSG_DELETE_MESSAGE_SHORT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_deleteMessageShort";
    private static final String MSG_VIEW_MESSAGE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_viewMessage";
    private static final String MSG_ANTI_CENSORSHIP = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_antiCensorship";
    private static final String MSG_DATA_RETENTION_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_dataRetentionExplain";
    private static final String MSG_OBSERVABLE_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_observableExplain";
    private static final String MSG_OBSERVABLE_TITLE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_observableTitle";
    private static final String MSG_MITM_WARNING_TITLE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".mitmWarningTitle";
    private static final String MSG_MITM_WARNING = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".mitmWarning";
    private static final String MSG_DISTRIBUTION = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_lblDistribution";
    private static final String MSG_USER_ACTIVITY = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_lblUserActivity";
    private static final String MSG_JAVA_FORCED_TITLE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_javaForcedTitle";
    private static final String MSG_JAVA_FORCED_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_javaForcedExplain";
    private static final String MSG_JAVA_FORCED_OS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_javaForcedOS";
    private static final String MSG_JAVA_FORCED_QUESTION = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_javaForcedQuestion";
    private static final String MSG_IP_ADDRESS = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".ipAddress";
    private static final String MSG_IP_ADDRESS_ENTRY = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".ipAddressEntry";
    private static final String MSG_LBL_HELP_OTHER_PEOPLE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".lblHelpOtherPeople";
    private static final String MSG_LBL_ENCRYPTED_DATA = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_lblEncryptedData";
    private static final String MSG_LBL_HTTP_DATA = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_lblHTTPData";
    private static final String MSG_LBL_OTHER_DATA = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_lblOtherData";
    private static final String IMG_ICONIFY = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_iconify.gif";
    private static final String IMG_ABOUT = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_about.gif";
    private static final String MSG_OPEN_FIREFOX = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_openFirefox";
    private static final String MSG_BTN_HIDE = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".btnHide";
    private static final String MSG_BTN_HIDE_EXPLAIN = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + ".btnHideExplain";
    private JobQueue m_blinkJobs;
    private JobQueue m_transferedBytesJobs;
    private JobQueue m_channelsChangedJobs;
    private JobQueue m_packetMixedJobs;
    private static final String HLP_ANONYMETER = (class$jap$JAPNewView == null ? (class$jap$JAPNewView = JAPNewView.class$("jap.JAPNewView")) : class$jap$JAPNewView).getName() + "_anonymometer";
    private static final String IMG_METER = "anonym-o-meter/JAP.NewView_m{0}.anim.gif";
    private static final String IMG_METER_NO_MEASURE = "anonym-o-meter/JAP.no.measure.anim{0}.gif";
    private static final String IMG_METER_DEACTIVATED = "anonym-o-meter/JAP.deactivated.anim{0}.gif";
    private static final String IMG_METER_CONNECTING = "anonym-o-meter/JAP.connecting.anim.gif";
    private final JLabel DEFAULT_LABEL = new JLabel();
    private JPanel m_pnlVersion;
    private JButton m_bttnHelp;
    private JButton m_bttnQuit;
    private JButton m_bttnIconify;
    private JButton m_bttnConf;
    private JButton m_btnAssistant;
    private JButton m_btnAbout;
    private JLabel m_buttonDeleteMessage;
    private JAPConf m_dlgConfig;
    private Object LOCK_CONFIG = new Object();
    private boolean m_bConfigActive = false;
    private JAPViewIconified m_ViewIconified;
    private Object SYNC_ICONIFIED_VIEW = new Object();
    private boolean m_bIsIconified;
    private boolean m_bWithPayment = false;
    private JAPMixCascadeComboBox m_comboAnonServices;
    private JLabel m_labelAnonService;
    private JLabel m_labelAnonymity;
    private JLabel m_labelAnonymitySmall;
    private JLabel m_labelAnonymityOnOff;
    private JLabel m_labelAnonMeter;
    private JLabel m_labelAnonymityLow;
    private JLabel m_labelAnonymityHigh;
    private JLabel m_labelSpeed;
    private JLabel m_labelDelay;
    private JLabel m_labelSpeedLabel;
    private JLabel m_labelDelayLabel;
    private JLabel m_labelOperatorCountries;
    private JLabel m_lblUsers;
    private JLabel m_lblUsersLabel;
    private JLabel m_lblIP;
    private JLabel m_lblIPValue;
    private JLabel m_lblIPFlag;
    private JLabel m_lblIPEntry;
    private JLabel m_lblIPValueEntry;
    private JLabel m_lblIPFlagEntry;
    private JLabel[] m_labelOperatorFlags;
    private MixMouseAdapter m_adapterExitMix;
    private MixMouseAdapter m_adapterEntryMix;
    private MixMouseAdapter[] m_adapterOperator;
    private LawListener m_lawListener;
    private JLabel[] m_lawFlags;
    private JLabel m_labelOwnTraffic;
    private JLabel m_labelOwnTrafficSmall;
    private JLabel m_labelOwnActivity;
    private JLabel m_labelForwarderActivity;
    private JLabel m_labelOwnActivitySmall;
    private JLabel m_labelForwarderActivitySmall;
    private JLabel m_labelOwnTrafficBytes;
    private JLabel m_labelOwnTrafficUnit;
    private JLabel m_labelOwnTrafficBytesSmall;
    private JLabel m_labelOwnTrafficUnitSmall;
    private JLabel m_labelOwnTrafficWWW;
    private JLabel m_labelOwnTrafficOther;
    private JLabel m_labelOwnTrafficBytesWWW;
    private JLabel m_labelOwnTrafficUnitWWW;
    private JLabel m_labelOwnTrafficBytesOther;
    private JLabel m_labelOwnTrafficUnitOther;
    private JLabel m_labelForwarding;
    private JLabel m_labelForwardingSmall;
    private JLabel m_labelForwardedTrafficBytes;
    private JLabel m_labelForwardedTrafficBytesUnit;
    private JLabel m_labelForwarderCurrentConnections;
    private JLabel m_labelForwarderAcceptedConnections;
    private JLabel m_labelForwarderRejectedConnections;
    private JLabel m_labelForwardedTraffic;
    private JLabel m_labelForwarderUsedBandwidth;
    private JLabel m_labelForwarderCurrentConnectionsLabel;
    private JLabel m_labelForwarderAcceptedConnectionsLabel;
    private JLabel m_labelForwarderRejectedConnectionsLabel;
    private JLabel m_labelForwarderUsedBandwidthLabel;
    private JLabel m_labelForwarderConnections;
    private JLabel m_labelForwardingErrorSmall;
    private JLabel m_labelForwardingError;
    private JAPProgressBar m_progressOwnTrafficActivity;
    private JAPProgressBar m_progressOwnTrafficActivitySmall;
    private JAPProgressBar m_progressAnonLevel;
    private JAPProgressBar m_progressDistribution;
    private JButton m_bttnAnonDetails;
    private JButton m_bttnReload;
    private JButton m_firefox;
    private JCheckBox m_cbAnonymityOn;
    private JRadioButton m_rbAnonOff;
    private JRadioButton m_rbAnonOn;
    private final Object SYNC_SELECTION = new Object();
    private JCheckBox m_cbForwarding;
    private JCheckBox m_cbForwardingSmall;
    private FlippingPanel m_flippingpanelAnon;
    private FlippingPanel m_flippingpanelOwnTraffic;
    private JPanel m_flippingpanelForward;
    private StatusPanel m_StatusPanel;
    private JPanel m_panelAnonService;
    private boolean m_bIgnoreAnonComboEvents = false;
    private PaymentMainPanel m_flippingPanelPayment;
    private final Object m_connectionEstablishedSync = new Object();
    private boolean m_bShowConnecting = false;
    private JAPProgressBar m_progForwarderActivity;
    private JAPProgressBar m_progForwarderActivitySmall;
    private int m_ForwardingID = -1;
    private int m_updateAvailableID = -1;
    private Hashtable m_messageIDs = new Hashtable();
    private int m_enableInfoServiceID = -1;
    private int m_newServicesID = -1;
    private final Object SYNC_STATUS_ENABLE_IS = new Object();
    private final Object SYNC_STATUS_UPDATE_AVAILABLE = new Object();
    private final Object SYNC_NEW_SERVICES = new Object();
    private ActionListener m_listenerUpdate;
    private ActionListener m_listenerEnableIS;
    private ActionListener m_listenerNewServices;
    private volatile long m_lTrafficWWW;
    private volatile long m_lTrafficOther;
    private final Object SYNC_ACTION = new Object();
    private boolean m_bActionPerformed = false;
    private ComponentMovedAdapter m_mainMovedAdapter;
    private ComponentMovedAdapter m_configMovedAdapter;
    private ComponentMovedAdapter m_helpMovedAdapter;
    private ComponentMovedAdapter m_miniMovedAdapter;
    private boolean m_bTrustChanged = false;
    private boolean m_bIsSimpleView;
    private final Object SYNC_MSG_INSECURE = new Object();
    private int m_msgIDInsecure;
    private int m_msgForwardServer = -1;
    private int m_msgForwardServerStatus = 0;
    private MouseListener m_mouseForwardError;
    private final Object SYNC_FORWARD_MSG = new Object();
    private int m_currentChannels = 0;
    private Hashtable m_messagesShown = new Hashtable();
    static /* synthetic */ Class class$jap$JAPNewView;
    static /* synthetic */ Class class$anon$infoservice$JAPVersionInfo;
    static /* synthetic */ Class class$jap$JAPConfAnon;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$javax$swing$event$PopupMenuListener;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;
    static /* synthetic */ Class class$anon$infoservice$MixCascadeExitAddresses;
    static /* synthetic */ Class class$anon$infoservice$JavaVersionDBEntry;
    static /* synthetic */ Class class$anon$infoservice$NewCascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$CascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$BlacklistedCascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$MessageDBEntry;
    static /* synthetic */ Class class$anon$infoservice$DeletedMessageIDDBEntry;
    static /* synthetic */ Class class$anon$infoservice$ClickedMessageIDDBEntry;
    static /* synthetic */ Class class$anon$client$TrustModel$SpeedAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$DelayAttribute;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public JAPNewView(String string, JAPController jAPController) {
        super(string, jAPController);
        this.m_bIsSimpleView = JAPModel.getDefaultView() == 2;
        this.m_Controller = JAPController.getInstance();
        this.m_dlgConfig = null;
        this.m_bIsIconified = false;
        this.m_blinkJobs = new JobQueue("Blink job queue");
        this.m_transferedBytesJobs = new JobQueue("Transfered bytes update job queue");
        this.m_packetMixedJobs = new JobQueue("packet mixed update job queue");
        this.m_channelsChangedJobs = new JobQueue("channels changed job queue");
        this.m_lTrafficWWW = 0L;
        this.m_lTrafficOther = 0L;
    }

    public void create(boolean bl) {
        this.m_bWithPayment = bl;
        LogHolder.log(6, LogType.GUI, "Initializing view...");
        this.init();
        this.setTitle(Double.toString(Math.random()));
        JAPDll.setWindowIcon(this.getTitle());
        this.setTitle(this.m_Title);
        LogHolder.log(6, LogType.GUI, "View initialized!");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init() {
        Object object;
        new SystrayPopupMenu(new SystrayPopupMenu.MainWindowListener(){

            public void onShowMainWindow() {
            }

            public void onShowSettings(String string, Object object) {
                JAPNewView.this.showConfigDialog(string, object);
            }

            public void onShowHelp() {
            }
        });
        MouseAdapter mouseAdapter = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent == null) {
                    return;
                }
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                if (SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isPopupTrigger()) {
                    SystrayPopupMenu systrayPopupMenu = new SystrayPopupMenu(new SystrayPopupMenu.MainWindowListener(){

                        public void onShowMainWindow() {
                        }

                        public void onShowSettings(String string, Object object) {
                            JAPNewView.this.showConfigDialog(string, object);
                        }

                        public void onShowHelp() {
                        }
                    });
                    systrayPopupMenu.show((Component)mouseEvent.getSource(), GUIUtils.getEventLocation(JAPNewView.this, mouseEvent));
                } else if (mouseEvent.getClickCount() == 2) {
                    JAPNewView.this.showIconifiedView(false);
                }
            }
        };
        this.addMouseListener(mouseAdapter);
        this.m_listenerUpdate = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JavaVersionDBEntry javaVersionDBEntry;
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                boolean bl = false;
                JAPVersionInfo jAPVersionInfo = JAPVersionInfo.getRecommendedUpdate("00.20.001", true);
                if (jAPVersionInfo == null) {
                    jAPVersionInfo = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPNewView.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById("/japRelease.jnlp");
                }
                if (JAPController.getInstance().hasPortableJava() || (javaVersionDBEntry = JavaVersionDBEntry.getNewJavaVersion()) == null || !javaVersionDBEntry.isUpdateForced() && !JAPModel.getInstance().isReminderForJavaUpdateActivated()) {
                    javaVersionDBEntry = null;
                }
                if (jAPVersionInfo != null && jAPVersionInfo.getJapVersion().compareTo("00.20.001") > 0) {
                    if (AbstractOS.getInstance().isJavaWebstart()) {
                        JAPDialog.showMessageDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString("webstartUpdate"), JAPMessages.getString("newVersionAvailableTitle"));
                    } else if (JAPController.getInstance().isHideUpdateDialogs()) {
                        JAPDialog.showMessageDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(JAPConfUpdate.MSG_DO_EXTERNAL_UPDATE, jAPVersionInfo.getJapVersion()), JAPMessages.getString("newVersionAvailableTitle"));
                    } else {
                        SoftwareUpdater.show(jAPVersionInfo, null);
                    }
                }
            }
        };
        this.m_listenerEnableIS = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                if (JAPModel.isInfoServiceDisabled()) {
                    String string = "";
                    if (JAPMessages.getLocale().getLanguage() == "de") {
                        string = "_de";
                    }
                    if (JAPDialog.showConfirmDialog((Component)JAPNewView.this, JAPMessages.getString(MSG_IS_DISABLED_EXPLAIN), 0, 2, (Icon)GUIUtils.loadImageIcon(MessageFormat.format(JAPNewView.IMG_METER_NO_MEASURE, string), true, true)) == 0) {
                        JAPModel.getInstance().setInfoServiceDisabled(false);
                    }
                }
                if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1 && !JAPController.getInstance().isAnonConnected()) {
                    if (JAPDialog.showConfirmDialog((Component)JAPNewView.this, JAPMessages.getString(JAPController.MSG_IS_NOT_ALLOWED), 0, 2) == 0) {
                        JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                    }
                } else if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 2 && JAPController.getInstance().isAnonConnected() && JAPDialog.showConfirmDialog((Component)JAPNewView.this, JAPMessages.getString(JAPController.MSG_IS_NOT_ALLOWED_FOR_ANONYMOUS), 0, 2) == 0) {
                    JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                }
            }
        };
        this.m_listenerNewServices = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                JAPDialog.showMessageDialog(JAPNewView.this, JAPMessages.getString(MSG_NEW_SERVICES_FOUND_EXPLAIN, JAPMessages.getString("availableCascades")));
                JAPNewView.this.m_comboAnonServices.showPopup();
            }
        };
        this.m_flippingpanelOwnTraffic = new FlippingPanel(this);
        this.m_flippingpanelForward = new FlippingPanel(this);
        String string = JAPModel.getInstance().getProgramName().equals("JonDo") ? "JonDo.ico.gif" : "icon16.gif";
        ImageIcon imageIcon = GUIUtils.loadImageIcon(string, true, false);
        if (imageIcon != null) {
            this.setIconImage(imageIcon.getImage());
        }
        JPanel jPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        jPanel.setLayout(gridBagLayout);
        gridBagConstraints.anchor = 11;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(5, 10, 5, 10);
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        this.m_buttonDeleteMessage = new JLabel(JAPMessages.getString(MSG_HIDE_MESSAGE_SHORT));
        this.m_buttonDeleteMessage.setCursor(Cursor.getPredefinedCursor(12));
        this.m_buttonDeleteMessage.setToolTipText(JAPMessages.getString(MSG_DELETE_MESSAGE));
        this.m_StatusPanel = new StatusPanel(this.m_buttonDeleteMessage);
        this.m_StatusPanel.addMouseListener(mouseAdapter);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 2;
        jPanel2.add((Component)this.m_StatusPanel, gridBagConstraints2);
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.anchor = 13;
        jPanel2.add((Component)this.m_buttonDeleteMessage, gridBagConstraints2);
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridwidth = 2;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = 17;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        ++gridBagConstraints.gridy;
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        this.m_panelAnonService = new JPanel(gridBagLayout2);
        this.m_labelAnonService = new JLabel(JAPMessages.getString("availableCascades"));
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 0;
        this.m_comboAnonServices = new JAPMixCascadeComboBox();
        if (AbstractOS.getInstance() instanceof WindowsOS) {
            this.addComponentListener(new ComponentAdapter(){

                public void componentMoved(ComponentEvent componentEvent) {
                    JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                }
            });
        }
        gridBagConstraints3.gridwidth = 3;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.weightx = 1.0;
        this.m_panelAnonService.add((Component)this.m_comboAnonServices, gridBagConstraints3);
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.gridwidth = 1;
        this.m_bttnReload = new JButton(GUIUtils.loadImageIcon("reload.gif", true, false));
        this.m_bttnReload.setOpaque(false);
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel != null && UIManager.getCrossPlatformLookAndFeelClassName().equals(lookAndFeel.getClass().getName())) {
            this.m_bttnReload.setBackground(this.m_panelAnonService.getBackground());
        }
        this.m_bttnReload.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                JAPNewView.this.fetchMixCascadesAsync(true);
            }
        });
        this.m_bttnReload.setRolloverEnabled(true);
        this.m_bttnReload.setToolTipText(JAPMessages.getString("ngCascadeReloadTooltip"));
        ImageIcon imageIcon2 = GUIUtils.loadImageIcon("reloadrollover.gif", true, false);
        this.m_bttnReload.setRolloverIcon(imageIcon2);
        this.m_bttnReload.setSelectedIcon(imageIcon2);
        this.m_bttnReload.setRolloverSelectedIcon(imageIcon2);
        this.m_bttnReload.setPressedIcon(imageIcon2);
        ImageIcon imageIcon3 = GUIUtils.loadImageIcon("reloaddisabled_anim.gif", true, false);
        this.m_bttnReload.setDisabledIcon(imageIcon3);
        this.m_bttnReload.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.m_bttnReload.setFocusPainted(false);
        this.m_bttnReload.setBorderPainted(true);
        this.m_bttnReload.setContentAreaFilled(false);
        gridBagConstraints3.gridx = 4;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 0;
        this.m_panelAnonService.add((Component)this.m_bttnReload, gridBagConstraints3);
        this.m_bttnAnonDetails = new JButton(JAPMessages.getString("confButton") + "...");
        this.m_bttnAnonDetails.setToolTipText(JAPMessages.getString("confButton") + "...");
        this.m_bttnAnonDetails.setMnemonic(JAPMessages.getString("confButtonMn").charAt(0));
        this.m_bttnAnonDetails.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPNewView.this.showConfigDialog("ANON_TAB", JAPController.getInstance().getCurrentMixCascade());
            }
        });
        gridBagConstraints3.gridx = 5;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 0;
        this.m_panelAnonService.add((Component)this.m_bttnAnonDetails, gridBagConstraints3);
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints3.insets = new Insets(5, 20, 0, 0);
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridwidth = 2;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = 17;
        jPanel.add((Component)this.m_panelAnonService, gridBagConstraints);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        this.m_flippingpanelAnon = new FlippingPanel(this);
        JPanel jPanel3 = new JPanel();
        gridBagLayout2 = new GridBagLayout();
        gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.anchor = 18;
        jPanel3.setLayout(gridBagLayout2);
        this.m_labelAnonymity = new JLabel(JAPMessages.getString("ngCascadeInfo"));
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.gridwidth = 4;
        jPanel3.add((Component)this.m_labelAnonymity, gridBagConstraints3);
        gridBagConstraints3.gridwidth = 1;
        this.m_lblUsersLabel = new JLabel(JAPMessages.getString(MSG_USERS) + ":");
        ++gridBagConstraints3.gridy;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.insets = new Insets(5, 15, 0, 10);
        jPanel3.add((Component)this.m_lblUsersLabel, gridBagConstraints3);
        this.m_labelOperatorCountries = new JLabel(JAPMessages.getString("ngOperatorCountries"));
        Insets insets = gridBagConstraints3.insets;
        gridBagConstraints3.insets = new Insets(5, gridBagConstraints3.insets.left, 2, gridBagConstraints3.insets.right);
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)this.m_labelOperatorCountries, gridBagConstraints3);
        gridBagConstraints3.insets = insets;
        this.m_labelSpeedLabel = new JLabel(JAPMessages.getString((class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPNewView.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_speed") + ":");
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)this.m_labelSpeedLabel, gridBagConstraints3);
        this.m_labelDelayLabel = new JLabel(JAPMessages.getString((class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPNewView.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_latency") + ":");
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)this.m_labelDelayLabel, gridBagConstraints3);
        this.m_lblIP = new JLabel(JAPMessages.getString(MSG_IP_ADDRESS) + ":");
        gridBagConstraints3.insets = new Insets(gridBagConstraints3.insets.top, gridBagConstraints3.insets.left, 2, gridBagConstraints3.insets.right);
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)this.m_lblIP, gridBagConstraints3);
        this.m_lblUsers = new JLabel("9999 / 9999", 2);
        gridBagConstraints3.insets = new Insets(5, 0, 0, 10);
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridwidth = 4;
        jPanel3.add((Component)this.m_lblUsers, gridBagConstraints3);
        this.m_labelOperatorFlags = new JLabel[3];
        this.m_adapterOperator = new MixMouseAdapter[3];
        this.m_lawFlags = new JLabel[3];
        this.m_lawListener = new LawListener();
        gridBagConstraints3.gridwidth = 1;
        gridBagConstraints3.fill = 0;
        insets = gridBagConstraints3.insets;
        ++gridBagConstraints3.gridy;
        for (int i = 0; i < this.m_labelOperatorFlags.length; ++i) {
            gridBagConstraints3.insets = new Insets(5, i == 0 ? 0 : 2, 0, 5);
            gridBagConstraints3.gridx = i + 1;
            this.m_labelOperatorFlags[i] = new JLabel("");
            this.m_labelOperatorFlags[i].setBorder(BorderFactory.createEmptyBorder());
            jPanel3.add((Component)this.m_labelOperatorFlags[i], gridBagConstraints3);
            this.m_adapterOperator[i] = new MixMouseAdapter(this.m_labelOperatorFlags[i]);
            this.m_labelOperatorFlags[i].addMouseListener(this.m_adapterOperator[i]);
            this.m_labelOperatorFlags[i].setCursor(Cursor.getPredefinedCursor(12));
            ++gridBagConstraints3.gridx;
            this.m_lawFlags[i] = new JLabel(GUIUtils.loadImageIcon("certs/invalid.png", true));
            this.m_lawFlags[i].setCursor(Cursor.getPredefinedCursor(12));
            this.m_lawFlags[i].setToolTipText(JAPMessages.getString(DataRetentionDialog.MSG_DATA_RETENTION_EXPLAIN_SHORT));
            this.m_lawFlags[i].addMouseListener(this.m_lawListener);
            gridBagConstraints3.insets = new Insets(3, 2, 0, 5);
            jPanel3.add((Component)this.m_lawFlags[i], gridBagConstraints3);
            if (i >= this.m_labelOperatorFlags.length - 1) continue;
            this.m_lawFlags[i].setVisible(false);
        }
        gridBagConstraints3.insets = insets;
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridwidth = 4;
        this.m_labelSpeed = new JLabel("1500 - 1500 kbit/s", 2);
        gridBagConstraints3.weightx = 0.0;
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)this.m_labelSpeed, gridBagConstraints3);
        this.m_labelDelay = new JLabel("8000 - 8000 ms", 2);
        gridBagConstraints3.weightx = 0.0;
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)this.m_labelDelay, gridBagConstraints3);
        JPanel jPanel4 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.insets = new Insets(0, 0, 2, 0);
        this.m_lblIPValue = new JLabel("111.111.111.111");
        jPanel4.add((Component)this.m_lblIPValue, gridBagConstraints4);
        this.m_lblIPFlag = new JLabel(GUIUtils.loadImageIcon("flags/de.png"));
        ++gridBagConstraints4.gridx;
        gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
        jPanel4.add((Component)this.m_lblIPFlag, gridBagConstraints4);
        this.m_adapterExitMix = new MixMouseAdapter(this.m_lblIPFlag);
        this.m_lblIPFlag.addMouseListener(this.m_adapterExitMix);
        this.m_lblIPFlag.setCursor(Cursor.getPredefinedCursor(12));
        this.m_lblIPFlag.setBorder(BorderFactory.createEmptyBorder());
        gridBagConstraints3.weightx = 0.0;
        ++gridBagConstraints3.gridy;
        jPanel3.add((Component)jPanel4, gridBagConstraints3);
        gridBagConstraints3.gridwidth = 1;
        gridBagConstraints3.fill = 2;
        this.m_labelAnonMeter = new JLabel(this.getMeterImage(null, null));
        this.m_labelAnonMeter.setToolTipText(JAPMessages.getString(MSG_ANONYMETER_TOOL_TIP));
        this.m_labelAnonMeter.setCursor(Cursor.getPredefinedCursor(12));
        this.m_labelAnonMeter.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                JAPHelp.getInstance().setContext(JAPHelpContext.createHelpContext(HLP_ANONYMETER, JAPNewView.this));
                JAPHelp.getInstance().setVisible(true);
            }
        });
        gridBagConstraints3.gridx = 5;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.gridheight = 5;
        gridBagConstraints3.anchor = 13;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.fill = 0;
        gridBagConstraints3.insets = new Insets(0, 10, 0, 10);
        jPanel3.add((Component)this.m_labelAnonMeter, gridBagConstraints3);
        GridBagLayout gridBagLayout3 = new GridBagLayout();
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        JPanel jPanel5 = new JPanel(gridBagLayout3);
        jPanel5.setBorder(LineBorder.createBlackLineBorder());
        this.m_labelAnonymityOnOff = new JLabel(JAPMessages.getString("ngAnonymitaet"));
        gridBagConstraints5.anchor = 18;
        gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
        jPanel5.add((Component)this.m_labelAnonymityOnOff, gridBagConstraints5);
        this.m_rbAnonOn = new JRadioButton(JAPMessages.getString("ngAnonOn"));
        this.m_rbAnonOn.addActionListener(this);
        this.m_rbAnonOff = new JRadioButton(JAPMessages.getString("ngAnonOff"));
        this.m_rbAnonOff.addActionListener(this);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.m_rbAnonOn);
        buttonGroup.add(this.m_rbAnonOff);
        this.m_rbAnonOff.setSelected(true);
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.insets = new Insets(0, 7, 0, 0);
        jPanel5.add((Component)this.m_rbAnonOn, gridBagConstraints5);
        gridBagConstraints5.gridy = 2;
        jPanel5.add((Component)this.m_rbAnonOff, gridBagConstraints5);
        ++gridBagConstraints3.gridx;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.insets = new Insets(0, 10, 0, 0);
        jPanel3.add((Component)jPanel5, gridBagConstraints3);
        this.m_flippingpanelAnon.setFullPanel(jPanel3);
        gridBagLayout2 = new GridBagLayout();
        gridBagConstraints3 = new GridBagConstraints();
        jPanel3 = new JPanel(gridBagLayout2);
        this.m_labelAnonymitySmall = new JLabel(JAPMessages.getString("ngAnonymitaet") + ":");
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        jPanel3.add((Component)this.m_labelAnonymitySmall, gridBagConstraints3);
        this.m_cbAnonymityOn = new JCheckBox(JAPMessages.getString("ngAnonOn"));
        this.m_cbAnonymityOn.setBorder(null);
        this.m_cbAnonymityOn.addActionListener(this);
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.insets = new Insets(0, 10, 0, 0);
        jPanel3.add((Component)this.m_cbAnonymityOn, gridBagConstraints3);
        JPanel jPanel6 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        this.m_labelAnonymityLow = new JLabel(JAPMessages.getString(MSG_DISTRIBUTION), 4);
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.fill = 2;
        gridBagConstraints6.insets = new Insets(0, 0, 0, 5);
        gridBagConstraints6.weightx = 0.0;
        jPanel6.add((Component)this.m_labelAnonymityLow, gridBagConstraints6);
        this.m_progressDistribution = new JAPProgressBar();
        this.m_progressDistribution.setMinimum(0);
        this.m_progressDistribution.setMaximum(6);
        this.m_progressDistribution.setBorderPainted(false);
        ++gridBagConstraints6.gridx;
        gridBagConstraints6.weightx = 1.0;
        jPanel6.add((Component)this.m_progressDistribution, gridBagConstraints6);
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.weightx = 0.75;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.insets = new Insets(0, 20, 0, 0);
        ++gridBagConstraints3.gridx;
        jPanel3.add((Component)jPanel6, gridBagConstraints3);
        JPanel jPanel7 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.gridx = 0;
        gridBagConstraints7.gridy = 0;
        gridBagConstraints7.fill = 2;
        gridBagConstraints7.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints7.weightx = 0.0;
        this.m_labelAnonymityHigh = new JLabel(JAPMessages.getString(MSG_USER_ACTIVITY));
        jPanel7.add((Component)this.m_labelAnonymityHigh, gridBagConstraints7);
        this.m_progressAnonLevel = new JAPProgressBar();
        this.m_progressAnonLevel.setMinimum(0);
        this.m_progressAnonLevel.setMaximum(6);
        this.m_progressAnonLevel.setBorderPainted(false);
        ++gridBagConstraints7.gridx;
        gridBagConstraints7.weightx = 1.0;
        jPanel7.add((Component)this.m_progressAnonLevel, gridBagConstraints7);
        ++gridBagConstraints3.gridx;
        gridBagConstraints3.weightx = 0.75;
        gridBagConstraints3.anchor = 13;
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        jPanel3.add((Component)jPanel7, gridBagConstraints3);
        this.m_flippingpanelAnon.setSmallPanel(jPanel3);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = 18;
        ++gridBagConstraints.gridy;
        this.m_flippingpanelAnon.setFlipped(true);
        if (this.m_bIsSimpleView) {
            jPanel.add((Component)this.m_flippingpanelAnon.getFullPanel(), gridBagConstraints);
        } else {
            jPanel.add((Component)this.m_flippingpanelAnon, gridBagConstraints);
        }
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        this.m_labelOwnActivity = new JLabel(JAPMessages.getString("ngActivity") + ":", 4);
        if (this.m_bWithPayment) {
            this.m_flippingPanelPayment = new PaymentMainPanel(this, this.m_labelOwnActivity);
            gridBagConstraints.fill = 2;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = 18;
            ++gridBagConstraints.gridy;
            this.m_flippingPanelPayment.setFlipped(false);
            insets = gridBagConstraints.insets;
            gridBagConstraints.insets = new Insets(0, insets.left, 0, insets.right);
            if (this.m_bIsSimpleView) {
                jPanel.add((Component)this.m_flippingPanelPayment.getSmallPanel(), gridBagConstraints);
            } else {
                jPanel.add((Component)this.m_flippingPanelPayment, gridBagConstraints);
            }
            gridBagConstraints.insets = insets;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridx = 0;
            ++gridBagConstraints.gridy;
            gridBagConstraints.fill = 2;
            gridBagConstraints.weightx = 1.0;
            jPanel.add((Component)new JSeparator(), gridBagConstraints);
        }
        gridBagLayout2 = new GridBagLayout();
        gridBagConstraints3 = new GridBagConstraints();
        jPanel3 = new JPanel(gridBagLayout2);
        this.m_labelOwnTraffic = new JLabel(JAPMessages.getString(MSG_LBL_ENCRYPTED_DATA) + ":");
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 2;
        jPanel3.add((Component)this.m_labelOwnTraffic, gridBagConstraints3);
        JPanel jPanel8 = new JPanel();
        Dimension dimension = new Dimension(this.m_labelAnonService.getFontMetrics(this.m_labelAnonService.getFont()).charWidth('9') * 6, 1);
        jPanel8.setPreferredSize(dimension);
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.fill = 0;
        gridBagConstraints3.weightx = 1.0;
        jPanel3.add((Component)jPanel8, gridBagConstraints3);
        this.m_labelOwnTrafficBytes = new JLabel("0");
        this.m_labelOwnTrafficBytes.setHorizontalAlignment(4);
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.gridx = 2;
        jPanel3.add((Component)this.m_labelOwnTrafficBytes, gridBagConstraints3);
        this.m_labelOwnTrafficUnit = new JLabel(JAPMessages.getString("Byte"));
        gridBagConstraints3.gridx = 3;
        jPanel3.add((Component)this.m_labelOwnTrafficUnit, gridBagConstraints3);
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.gridx = 4;
        gridBagConstraints3.insets = new Insets(0, 10, 0, 0);
        jPanel3.add((Component)this.m_labelOwnActivity, gridBagConstraints3);
        this.m_progressOwnTrafficActivity = new JAPProgressBar();
        this.m_progressOwnTrafficActivity.setMinimum(0);
        this.m_progressOwnTrafficActivity.setMaximum(6);
        this.m_progressOwnTrafficActivity.setBorderPainted(false);
        gridBagConstraints3.gridx = 5;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 0;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        jPanel3.add((Component)this.m_progressOwnTrafficActivity, gridBagConstraints3);
        this.m_labelOwnTrafficWWW = new JLabel(JAPMessages.getString(MSG_LBL_HTTP_DATA) + ":");
        gridBagConstraints3.insets = new Insets(10, 20, 0, 0);
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.weightx = 0.0;
        jPanel3.add((Component)this.m_labelOwnTrafficWWW, gridBagConstraints3);
        jPanel8 = new JPanel();
        jPanel8.setPreferredSize(dimension);
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.fill = 0;
        jPanel3.add((Component)jPanel8, gridBagConstraints3);
        this.m_labelOwnTrafficBytesWWW = new JLabel("0");
        this.m_labelOwnTrafficBytesWWW.setHorizontalAlignment(4);
        gridBagConstraints3.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.weightx = 0.0;
        jPanel3.add((Component)this.m_labelOwnTrafficBytesWWW, gridBagConstraints3);
        this.m_labelOwnTrafficUnitWWW = new JLabel(JAPMessages.getString("Byte"));
        gridBagConstraints3.gridx = 3;
        jPanel3.add((Component)this.m_labelOwnTrafficUnitWWW, gridBagConstraints3);
        this.m_labelOwnTrafficOther = new JLabel(JAPMessages.getString(MSG_LBL_OTHER_DATA) + ":");
        gridBagConstraints3.insets = new Insets(7, 20, 0, 0);
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 2;
        jPanel3.add((Component)this.m_labelOwnTrafficOther, gridBagConstraints3);
        jPanel8 = new JPanel();
        jPanel8.setPreferredSize(dimension);
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.fill = 0;
        jPanel3.add((Component)jPanel8, gridBagConstraints3);
        this.m_labelOwnTrafficBytesOther = new JLabel("0");
        this.m_labelOwnTrafficBytesOther.setHorizontalAlignment(4);
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.insets = new Insets(7, 5, 0, 0);
        gridBagConstraints3.gridx = 2;
        jPanel3.add((Component)this.m_labelOwnTrafficBytesOther, gridBagConstraints3);
        this.m_labelOwnTrafficUnitOther = new JLabel(JAPMessages.getString("Byte"));
        gridBagConstraints3.gridx = 3;
        jPanel3.add((Component)this.m_labelOwnTrafficUnitOther, gridBagConstraints3);
        this.m_flippingpanelOwnTraffic.setFullPanel(jPanel3);
        gridBagLayout2 = new GridBagLayout();
        gridBagConstraints3 = new GridBagConstraints();
        jPanel3 = new JPanel(gridBagLayout2);
        this.m_labelOwnTrafficSmall = new JLabel(JAPMessages.getString(MSG_LBL_ENCRYPTED_DATA) + ":");
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 0;
        gridBagConstraints3.anchor = 17;
        jPanel3.add((Component)this.m_labelOwnTrafficSmall, gridBagConstraints3);
        this.m_labelOwnTrafficBytesSmall = new JLabel("0");
        this.m_labelOwnTrafficBytesSmall.setHorizontalAlignment(4);
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.gridx = 1;
        jPanel3.add((Component)this.m_labelOwnTrafficBytesSmall, gridBagConstraints3);
        this.m_labelOwnTrafficUnitSmall = new JLabel(JAPMessages.getString("Byte"));
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.fill = 0;
        jPanel3.add((Component)this.m_labelOwnTrafficUnitSmall, gridBagConstraints3);
        this.m_labelOwnActivitySmall = new JLabel(JAPMessages.getString("ngActivity") + ":", 4);
        gridBagConstraints3.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints3.gridx = 3;
        jPanel3.add((Component)this.m_labelOwnActivitySmall, gridBagConstraints3);
        this.m_progressOwnTrafficActivitySmall = new JAPProgressBar();
        this.m_progressOwnTrafficActivitySmall.setMinimum(0);
        this.m_progressOwnTrafficActivitySmall.setMaximum(6);
        this.m_progressOwnTrafficActivitySmall.setBorderPainted(false);
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.fill = 0;
        gridBagConstraints3.gridx = 4;
        jPanel3.add((Component)this.m_progressOwnTrafficActivitySmall, gridBagConstraints3);
        this.m_flippingpanelOwnTraffic.setSmallPanel(jPanel3);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = 18;
        ++gridBagConstraints.gridy;
        if (this.m_bIsSimpleView) {
            jPanel.add((Component)this.m_flippingpanelOwnTraffic.getSmallPanel(), gridBagConstraints);
        } else {
            jPanel.add((Component)this.m_flippingpanelOwnTraffic, gridBagConstraints);
        }
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = 18;
        ++gridBagConstraints.gridy;
        this.m_flippingpanelForward = this.buildForwarderPanel();
        jPanel.add((Component)this.m_flippingpanelForward, gridBagConstraints);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        gridBagLayout2 = new GridBagLayout();
        gridBagConstraints3 = new GridBagConstraints();
        JPanel jPanel9 = new JPanel(gridBagLayout2);
        this.m_bttnHelp = new JButton(JAPMessages.getString(JAPHelp.MSG_HELP_BUTTON));
        this.m_bttnHelp.setToolTipText(JAPMessages.getString(JAPHelp.MSG_HELP_BUTTON));
        this.m_btnAbout = new JButton();
        this.m_btnAbout.setToolTipText(JAPMessages.getString("aboutBox"));
        this.m_bttnQuit = new JButton();
        this.update(JAPModel.getInstance(), JAPModel.CHANGED_SHOW_CLOSED_BUTTON);
        this.m_btnAssistant = new JButton(JAPMessages.getString(MSG_BTN_ASSISTANT) + "...");
        this.m_btnAssistant.setToolTipText(JAPMessages.getString(MSG_BTN_ASSISTANT) + "...");
        this.m_bttnConf = new JButton(JAPMessages.getString("confButton"));
        this.m_bttnConf.setToolTipText(JAPMessages.getString("confButton"));
        this.m_bttnConf.setVisible(false);
        this.m_bttnIconify = new JButton();
        this.m_bttnIconify.setToolTipText(JAPMessages.getString("iconifyWindow"));
        gridBagConstraints3.fill = 3;
        jPanel9.add((Component)this.m_bttnIconify, gridBagConstraints3);
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        jPanel9.add((Component)this.m_btnAbout, gridBagConstraints3);
        if (this.m_Controller.isPortableMode()) {
            this.m_firefox = new JButton(JAPMessages.getString(MSG_BROWSER));
            this.m_firefox.setToolTipText(JAPMessages.getString(MSG_OPEN_FIREFOX));
            this.m_firefox.setMnemonic(JAPMessages.getString(MSG_BROWSER_MNEMONIC).charAt(0));
            this.m_firefox.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent actionEvent) {
                    JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                    LogHolder.log(5, LogType.GUI, "Opening web browser...");
                    AbstractOS.getInstance().openURL(null);
                }
            });
            ++gridBagConstraints3.gridx;
            jPanel9.add((Component)this.m_firefox, gridBagConstraints3);
        }
        ++gridBagConstraints3.gridx;
        jPanel9.add((Component)this.m_bttnHelp, gridBagConstraints3);
        ++gridBagConstraints3.gridx;
        jPanel9.add((Component)this.m_btnAssistant, gridBagConstraints3);
        ++gridBagConstraints3.gridx;
        jPanel9.add((Component)this.m_bttnConf, gridBagConstraints3);
        ++gridBagConstraints3.gridx;
        gridBagConstraints3.fill = 2;
        jPanel9.add((Component)new JLabel(), gridBagConstraints3);
        ++gridBagConstraints3.gridx;
        jPanel9.add((Component)this.m_bttnQuit, gridBagConstraints3);
        this.m_bttnIconify.addActionListener(this);
        this.m_bttnConf.addActionListener(this);
        this.m_btnAbout.addActionListener(this);
        this.m_bttnHelp.addActionListener(this);
        this.m_bttnQuit.addActionListener(this);
        this.m_btnAssistant.addActionListener(this);
        JAPUtil.setMnemonic(this.m_bttnIconify, JAPMessages.getString("iconifyButtonMn"));
        JAPUtil.setMnemonic(this.m_bttnConf, JAPMessages.getString("confButtonMn"));
        JAPUtil.setMnemonic(this.m_bttnHelp, JAPMessages.getString("helpButtonMn"));
        JAPUtil.setMnemonic(this.m_btnAssistant, JAPMessages.getString(MSG_MN_ASSISTANT));
        ++gridBagConstraints.gridy;
        jPanel.add((Component)jPanel9, gridBagConstraints);
        this.getContentPane().setBackground(jPanel9.getBackground());
        this.getContentPane().add((Component)jPanel, "Center");
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                if (JAPNewView.this.isEnabled()) {
                    if (JAPDll.getDllVersion() == null) {
                        if (JAPModel.getInstance().isCloseButtonShown()) {
                            JAPController.goodBye(true);
                        } else {
                            JAPNewView.this.showIconifiedView(false);
                        }
                    } else {
                        JAPNewView.this.setState(1);
                    }
                }
            }

            public void windowDeiconified(WindowEvent windowEvent) {
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                JAPNewView.this.m_bIsIconified = false;
                JAPNewView.this.updateValues(false);
            }

            public void windowIconified(WindowEvent windowEvent) {
                JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                JAPNewView.this.hideWindowInTaskbar();
                JAPNewView.this.m_bIsIconified = true;
                JAPNewView.this.updateValues(false);
                JAPController.showNonAnonymousWarning(JAPMessages.getString(DialogContentPane.MSG_IGNORE), false);
            }
        });
        this.updateFonts();
        this.setOptimalSize();
        PopupMenuListener popupMenuListener = new PopupMenuListener(){

            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPNewView.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(JAPController.getInstance().getCurrentMixCascade().getId());
                if (mixCascade == null) {
                    JAPNewView.this.m_comboAnonServices.setMixCascade(JAPController.getInstance().getCurrentMixCascade());
                } else {
                    JAPNewView.this.m_comboAnonServices.setMixCascade(mixCascade);
                }
                if (JAPNewView.this.m_comboAnonServices.getSelectedItem() == null) {
                    JAPNewView.this.m_comboAnonServices.setSelectedItem(mixCascade);
                }
                JAPNewView.this.m_comboAnonServices.validate();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                JAPNewView.this.m_comboAnonServices.validate();
            }

            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
                this.popupMenuWillBecomeVisible(popupMenuEvent);
            }
        };
        try {
            object = this.m_comboAnonServices.getClass().getMethod("addPopupMenuListener", class$javax$swing$event$PopupMenuListener == null ? (class$javax$swing$event$PopupMenuListener = JAPNewView.class$("javax.swing.event.PopupMenuListener")) : class$javax$swing$event$PopupMenuListener);
            ((Method)object).invoke(this.m_comboAnonServices, popupMenuListener);
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.GUI, "Cannot update cascade popup! Please update you Java!", exception);
        }
        this.m_comboAnonServices.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                final MixCascade mixCascade = (MixCascade)JAPNewView.this.m_comboAnonServices.getSelectedItem();
                if (JAPNewView.this.m_bIgnoreAnonComboEvents) {
                    return;
                }
                if (itemEvent.getStateChange() == 1) {
                    SwingUtilities.invokeLater(new Runnable(){

                        public void run() {
                            (this).JAPNewView.this.m_Controller.setCurrentMixCascade(mixCascade);
                        }
                    });
                }
            }
        });
        PayAccountsFile.getInstance().addMessageListener(this);
        PayAccountsFile.fireKnownMessages();
        this.updateValues(true);
        GUIUtils.centerOnScreen(this);
        GUIUtils.restoreLocation(this, JAPModel.getMainWindowLocation());
        Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPNewView.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).addObserver(this);
        Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = JAPNewView.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses).addObserver(this);
        Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPNewView.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).addObserver(this);
        Database.getInstance(class$anon$infoservice$JavaVersionDBEntry == null ? (class$anon$infoservice$JavaVersionDBEntry = JAPNewView.class$("anon.infoservice.JavaVersionDBEntry")) : class$anon$infoservice$JavaVersionDBEntry).addObserver(this);
        Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPNewView.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).addObserver(this);
        Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).addObserver(this);
        Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPNewView.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry).addObserver(this);
        Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPNewView.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).addObserver(this);
        Database.getInstance(class$anon$infoservice$MessageDBEntry == null ? (class$anon$infoservice$MessageDBEntry = JAPNewView.class$("anon.infoservice.MessageDBEntry")) : class$anon$infoservice$MessageDBEntry).addObserver(this);
        TrustModel.addModelObserver(this);
        JAPModel.getInstance().addObserver(this);
        JAPModel.getInstance().getRoutingSettings().addObserver(this);
        JAPHelp.init(this, JAPModel.getInstance());
        if (JAPHelp.getHelpDialog() != null) {
            JAPHelp.getHelpDialog().setLocationRelativeTo(JAPHelp.getHelpDialog().getOwner(), 0);
            JAPHelp.getHelpDialog().resetAutomaticLocation(JAPModel.getInstance().isHelpWindowLocationSaved());
            JAPHelp.getHelpDialog().restoreLocation(JAPModel.getInstance().getHelpWindowLocation());
            JAPHelp.getHelpDialog().restoreSize(JAPModel.getInstance().getHelpWindowSize());
        }
        this.m_mainMovedAdapter = new ComponentMovedAdapter();
        this.m_helpMovedAdapter = new ComponentMovedAdapter();
        this.m_configMovedAdapter = new ComponentMovedAdapter();
        this.addComponentListener(this.m_mainMovedAdapter);
        if (JAPHelp.getHelpDialog() != null) {
            JAPHelp.getHelpDialog().addComponentListener(this.m_helpMovedAdapter);
        }
        object = this.LOCK_CONFIG;
        synchronized (object) {
            if (this.m_dlgConfig == null) {
                this.m_dlgConfig = new JAPConf(this, this.m_bWithPayment);
                this.m_dlgConfig.addComponentListener(this.m_configMovedAdapter);
            }
        }
        if (!JAPModel.isInfoServiceDisabled()) {
            this.fetchMixCascadesAsync(false);
        }
    }

    private JPanel buildForwarderPanel() {
        FlippingPanel flippingPanel = new FlippingPanel(this);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = 17;
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        this.m_labelForwarding = new JLabel(JAPMessages.getString(MSG_LBL_HELP_OTHER_PEOPLE));
        gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints2.anchor = 17;
        jPanel2.add((Component)this.m_labelForwarding, gridBagConstraints2);
        this.m_cbForwarding = new JCheckBox(JAPMessages.getString("ngForwardingOn"));
        this.m_cbForwarding.setBorder(null);
        ActionListener actionListener = new ActionListener(){

            public void actionPerformed(final ActionEvent actionEvent) {
                new Thread(){

                    public void run() {
                        if (!(this).JAPNewView.this.m_Controller.enableForwardingServer(((JCheckBox)actionEvent.getSource()).isSelected())) {
                            try {
                                SwingUtilities.invokeAndWait(new Runnable(){

                                    public void run() {
                                        ((JCheckBox)actionEvent.getSource()).setSelected(false);
                                    }
                                });
                            }
                            catch (Exception exception) {
                                LogHolder.log(3, LogType.GUI, exception);
                            }
                        }
                    }
                }.start();
            }
        };
        this.m_cbForwarding.addActionListener(actionListener);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
        jPanel2.add((Component)this.m_cbForwarding, gridBagConstraints2);
        this.m_labelForwardingError = new JLabel();
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.insets = new Insets(0, 15, 0, 0);
        jPanel2.add((Component)this.m_labelForwardingError, gridBagConstraints2);
        this.m_labelForwarderActivity = new JLabel(JAPMessages.getString("ngActivity") + ":");
        gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints2.gridx = 3;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        jPanel2.add((Component)this.m_labelForwarderActivity, gridBagConstraints2);
        this.m_progForwarderActivity = new JAPProgressBar();
        this.m_progForwarderActivity.setMinimum(0);
        this.m_progForwarderActivity.setMaximum(6);
        this.m_progForwarderActivity.setBorderPainted(false);
        gridBagConstraints2.gridx = 4;
        jPanel2.add((Component)this.m_progForwarderActivity, gridBagConstraints2);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 4;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.m_labelForwarderConnections = new JLabel(JAPMessages.getString("ngForwardedConnections"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(10, 5, 0, 0);
        jPanel.add((Component)this.m_labelForwarderConnections, gridBagConstraints);
        JPanel jPanel3 = new JPanel();
        Dimension dimension = new Dimension(this.m_labelForwarderConnections.getFontMetrics(this.m_labelForwarderConnections.getFont()).charWidth('9') * 6, 1);
        jPanel3.setPreferredSize(dimension);
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        jPanel.add((Component)jPanel3, gridBagConstraints);
        this.m_labelForwarderCurrentConnections = new JLabel("0");
        this.m_labelForwarderCurrentConnections.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridx = 2;
        jPanel.add((Component)this.m_labelForwarderCurrentConnections, gridBagConstraints);
        this.m_labelForwarderCurrentConnectionsLabel = new JLabel(JAPMessages.getString("ngForwardedCurrentConnections"));
        gridBagConstraints.gridx = 3;
        jPanel.add((Component)this.m_labelForwarderCurrentConnectionsLabel, gridBagConstraints);
        this.m_labelForwarderAcceptedConnections = new JLabel("0");
        this.m_labelForwarderAcceptedConnections.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(7, 5, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel.add((Component)this.m_labelForwarderAcceptedConnections, gridBagConstraints);
        this.m_labelForwarderAcceptedConnectionsLabel = new JLabel(JAPMessages.getString("ngForwardedAcceptedConnections"));
        gridBagConstraints.gridx = 3;
        jPanel.add((Component)this.m_labelForwarderAcceptedConnectionsLabel, gridBagConstraints);
        this.m_labelForwarderRejectedConnections = new JLabel("0");
        this.m_labelForwarderRejectedConnections.setHorizontalAlignment(4);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jPanel.add((Component)this.m_labelForwarderRejectedConnections, gridBagConstraints);
        this.m_labelForwarderRejectedConnectionsLabel = new JLabel(JAPMessages.getString("ngForwardedRejectedConnections"));
        gridBagConstraints.gridx = 3;
        jPanel.add((Component)this.m_labelForwarderRejectedConnectionsLabel, gridBagConstraints);
        this.m_labelForwardedTraffic = new JLabel(JAPMessages.getString("ngForwardedTraffic"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        jPanel.add((Component)this.m_labelForwardedTraffic, gridBagConstraints);
        this.m_labelForwardedTrafficBytes = new JLabel("0");
        this.m_labelForwardedTrafficBytes.setHorizontalAlignment(4);
        gridBagConstraints.gridx = 2;
        jPanel.add((Component)this.m_labelForwardedTrafficBytes, gridBagConstraints);
        this.m_labelForwardedTrafficBytesUnit = new JLabel(JAPMessages.getString("Byte"));
        gridBagConstraints.gridx = 3;
        jPanel.add((Component)this.m_labelForwardedTrafficBytesUnit, gridBagConstraints);
        this.m_labelForwarderUsedBandwidthLabel = new JLabel(JAPMessages.getString("ngForwardedUsedBandwidth"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        jPanel.add((Component)this.m_labelForwarderUsedBandwidthLabel, gridBagConstraints);
        this.m_labelForwarderUsedBandwidth = new JLabel("0");
        this.m_labelForwarderUsedBandwidth.setHorizontalAlignment(4);
        gridBagConstraints.gridx = 2;
        jPanel.add((Component)this.m_labelForwarderUsedBandwidth, gridBagConstraints);
        JLabel jLabel = new JLabel("Byte/s");
        gridBagConstraints.gridx = 3;
        jPanel.add((Component)jLabel, gridBagConstraints);
        flippingPanel.setFullPanel(jPanel);
        gridBagConstraints = new GridBagConstraints();
        jPanel = new JPanel(new GridBagLayout());
        this.m_labelForwardingSmall = new JLabel(JAPMessages.getString(MSG_LBL_HELP_OTHER_PEOPLE));
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = 17;
        jPanel.add((Component)this.m_labelForwardingSmall, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        this.m_cbForwardingSmall = new JCheckBox(JAPMessages.getString("ngForwardingOn"));
        this.m_cbForwardingSmall.setBorder(null);
        this.m_cbForwardingSmall.addActionListener(actionListener);
        jPanel.add((Component)this.m_cbForwardingSmall, gridBagConstraints);
        this.m_labelForwardingErrorSmall = new JLabel();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(0, 15, 0, 0);
        jPanel.add((Component)this.m_labelForwardingErrorSmall, gridBagConstraints);
        this.m_labelForwarderActivitySmall = new JLabel(JAPMessages.getString("ngActivity") + ":");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        jPanel.add((Component)this.m_labelForwarderActivitySmall, gridBagConstraints);
        this.m_progForwarderActivitySmall = new JAPProgressBar();
        this.m_progForwarderActivitySmall.setMinimum(0);
        this.m_progForwarderActivitySmall.setMaximum(6);
        this.m_progForwarderActivitySmall.setBorderPainted(false);
        gridBagConstraints.gridx = 4;
        jPanel.add((Component)this.m_progForwarderActivitySmall, gridBagConstraints);
        flippingPanel.setSmallPanel(jPanel);
        Observer observer = new Observer(){

            public void update(Observable observable, Object object) {
                try {
                    if (observable instanceof JAPRoutingServerStatisticsListener) {
                        JAPRoutingServerStatisticsListener jAPRoutingServerStatisticsListener = (JAPRoutingServerStatisticsListener)observable;
                        long l = jAPRoutingServerStatisticsListener.getTransferedBytes();
                        JAPNewView.this.m_labelForwardedTrafficBytes.setText(Util.formatBytesValueWithoutUnit(l));
                        JAPNewView.this.m_labelForwardedTrafficBytesUnit.setText(Util.formatBytesValueOnlyUnit(l));
                        JAPNewView.this.m_labelForwarderAcceptedConnections.setText(Integer.toString(jAPRoutingServerStatisticsListener.getAcceptedConnections()));
                        JAPNewView.this.m_labelForwarderRejectedConnections.setText(Integer.toString(jAPRoutingServerStatisticsListener.getRejectedConnections()));
                        JAPNewView.this.m_labelForwarderCurrentConnections.setText(Integer.toString(jAPRoutingServerStatisticsListener.getCurrentlyForwardedConnections()));
                        JAPNewView.this.m_labelForwarderUsedBandwidth.setText(Integer.toString(jAPRoutingServerStatisticsListener.getCurrentBandwidthUsage()));
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        };
        JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().addObserver(observer);
        if (!this.m_bIsSimpleView) {
            return flippingPanel;
        }
        return jPanel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disableSetAnonMode() {
        Object object = this.SYNC_ICONIFIED_VIEW;
        synchronized (object) {
            this.m_ViewIconified.disableSetAnonMode();
        }
        this.m_rbAnonOn.setEnabled(false);
        this.m_rbAnonOff.setEnabled(false);
    }

    private Icon getMeterImage(MixCascade mixCascade, StatusInfo statusInfo) {
        boolean bl = this.m_Controller.getAnonMode();
        boolean bl2 = this.m_Controller.isAnonConnected();
        boolean bl3 = this.m_Controller.isConnecting() || this.m_bShowConnecting;
        String string = "";
        if (JAPMessages.getLocale().getLanguage() == "de") {
            string = "_de";
        }
        if (bl && bl2 && mixCascade != null) {
            if (mixCascade.getDistribution() > 0) {
                return GUIUtils.loadImageIcon(MessageFormat.format(IMG_METER, mixCascade.getDistribution() + "" + Math.max(0, statusInfo.getAnonLevel())), true, true);
            }
            return GUIUtils.loadImageIcon(MessageFormat.format(IMG_METER_NO_MEASURE, string), true, true);
        }
        if (bl && !bl2 && bl3 && mixCascade != null) {
            return GUIUtils.loadImageIcon(IMG_METER_CONNECTING, true, true);
        }
        if (mixCascade == null) {
            GUIUtils.loadImageIcon(IMG_METER_CONNECTING, true, true);
        }
        return GUIUtils.loadImageIcon(MessageFormat.format(IMG_METER_DEACTIVATED, string), true, true);
    }

    private void blink(final long l) {
        this.m_blinkJobs.addJob(new JobQueue.Job(true){

            public void runJob() {
                if (l > 0L && JAPNewView.this.m_ViewIconified != null) {
                    JAPNewView.this.m_ViewIconified.blink();
                }
                if (JAPNewView.this.isVisible()) {
                    Runnable runnable = new Runnable(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        public void run() {
                            JAPProgressBar jAPProgressBar = JAPNewView.this.m_progressOwnTrafficActivity;
                            synchronized (jAPProgressBar) {
                                if (JAPNewView.this.m_currentChannels == 0) {
                                    return;
                                }
                                if ((this).JAPNewView.this.m_Controller.isAnonConnected()) {
                                    JAPNewView.this.m_progressOwnTrafficActivity.setValue(Math.min(JAPNewView.this.m_currentChannels, JAPNewView.this.m_progressOwnTrafficActivity.getMaximum()) - 1);
                                    JAPNewView.this.m_progressOwnTrafficActivitySmall.setValue(Math.min(JAPNewView.this.m_currentChannels, JAPNewView.this.m_progressOwnTrafficActivity.getMaximum()) - 1);
                                    try {
                                        JAPNewView.this.m_progressOwnTrafficActivity.wait(250L);
                                    }
                                    catch (InterruptedException interruptedException) {
                                        // empty catch block
                                    }
                                }
                            }
                        }
                    };
                    try {
                        SwingUtilities.invokeAndWait(runnable);
                    }
                    catch (InvocationTargetException invocationTargetException) {
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    runnable = null;
                    if (JAPNewView.this.m_Controller.isAnonConnected()) {
                        try {
                            Thread.sleep(250L);
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                    }
                    runnable = new Runnable(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        public void run() {
                            JAPProgressBar jAPProgressBar = JAPNewView.this.m_progressOwnTrafficActivity;
                            synchronized (jAPProgressBar) {
                                if (!(this).JAPNewView.this.m_Controller.isAnonConnected()) {
                                    JAPNewView.this.m_currentChannels = 0;
                                }
                                JAPNewView.this.m_progressOwnTrafficActivity.setValue(JAPNewView.this.m_currentChannels);
                                JAPNewView.this.m_progressOwnTrafficActivitySmall.setValue(JAPNewView.this.m_currentChannels);
                            }
                        }
                    };
                    try {
                        SwingUtilities.invokeAndWait(runnable);
                    }
                    catch (InvocationTargetException invocationTargetException) {
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    runnable = null;
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        Object object2;
        Runnable runnable = null;
        if (observable == Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPNewView.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo)) {
            object2 = ((DatabaseMessage)object).getMessageData();
            if (object2 instanceof StatusInfo && ((StatusInfo)object2).getId().equals(JAPController.getInstance().getCurrentMixCascade().getId())) {
                this.updateValues(false);
            }
        } else if (observable == Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = JAPNewView.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses)) {
            object2 = ((DatabaseMessage)object).getMessageData();
            if (object2 instanceof MixCascadeExitAddresses && ((MixCascadeExitAddresses)object2).getId().equals(JAPController.getInstance().getCurrentMixCascade().getId())) {
                this.updateValues(false);
            }
        } else if (observable == Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPNewView.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo)) {
            this.updateValues(false);
        } else if (observable == Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPNewView.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry)) {
            object2 = (DatabaseMessage)object;
            if (object2 == null) {
                return;
            }
            if (((AbstractMessage)object2).getMessageCode() != 5) {
                this.m_bTrustChanged = true;
                this.updateValues(false);
            }
        } else if (observable == Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPNewView.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade)) {
            object2 = (DatabaseMessage)object;
            if (((AbstractMessage)object2).getMessageData() == null || !(((AbstractMessage)object2).getMessageData() instanceof MixCascade)) {
                return;
            }
            MixCascade mixCascade = (MixCascade)((AbstractMessage)object2).getMessageData();
            if (((AbstractMessage)object2).getMessageCode() != 2 && ((AbstractMessage)object2).getMessageCode() != 5 && mixCascade.isUserDefined()) {
                this.m_bTrustChanged = true;
            }
            if (((AbstractMessage)object2).getMessageCode() == 1 || ((AbstractMessage)object2).getMessageCode() == 2) {
                MixCascade mixCascade2 = JAPController.getInstance().getCurrentMixCascade();
                if (mixCascade2.equals(mixCascade) && TrustModel.getCurrentTrustModel().isTrusted(mixCascade2) != TrustModel.getCurrentTrustModel().isTrusted(mixCascade)) {
                    JAPController.getInstance().setCurrentMixCascade(mixCascade);
                    this.m_bTrustChanged = true;
                }
                Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPNewView.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry).update(new CascadeIDEntry(mixCascade));
                if (Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).getEntryById(mixCascade.getMixIDsAsString()) != null) {
                    this.m_bTrustChanged = true;
                    if (!JAPController.getInstance().getCurrentMixCascade().isPayment()) {
                        Object object3 = this.SYNC_NEW_SERVICES;
                        synchronized (object3) {
                            if (this.m_newServicesID < 0) {
                                this.m_newServicesID = this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_LBL_NEW_SERVICES_FOUND), 1, false, this.m_listenerNewServices);
                            }
                        }
                    }
                }
            } else if ((((AbstractMessage)object2).getMessageCode() == 3 || ((AbstractMessage)object2).getMessageCode() == 4) && Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).getEntryById(mixCascade.getMixIDsAsString()) != null) {
                this.m_bTrustChanged = true;
                Enumeration enumeration = Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).getEntrySnapshotAsEnumeration();
                boolean bl = true;
                while (enumeration.hasMoreElements()) {
                    NewCascadeIDEntry newCascadeIDEntry = (NewCascadeIDEntry)enumeration.nextElement();
                    if (Database.getInstance(class$anon$infoservice$MixCascade == null ? JAPNewView.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryById(newCascadeIDEntry.getCascadeId()) == null || newCascadeIDEntry.getCascadeId().equals(mixCascade.getId())) continue;
                    bl = false;
                    break;
                }
                if (bl) {
                    Object object4 = this.SYNC_NEW_SERVICES;
                    synchronized (object4) {
                        if (this.m_newServicesID >= 0) {
                            this.m_StatusPanel.removeStatusMsg(this.m_newServicesID);
                            this.m_newServicesID = -1;
                        }
                    }
                }
            }
            this.updateValues(false);
        } else if (observable == Database.getInstance(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = JAPNewView.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry)) {
            object2 = (DatabaseMessage)object;
            if (((AbstractMessage)object2).getMessageData() == null) {
                return;
            }
            if (((AbstractMessage)object2).getMessageCode() == 1) {
                Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).update(new NewCascadeIDEntry((CascadeIDEntry)((AbstractMessage)object2).getMessageData()));
            }
        } else if (observable == Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry)) {
            Object object5;
            object2 = (DatabaseMessage)object;
            if (((AbstractMessage)object2).getMessageData() == null) {
                return;
            }
            boolean bl = false;
            if (((AbstractMessage)object2).getMessageCode() == 1 || ((AbstractMessage)object2).getMessageCode() == 2) {
                if (!JAPController.getInstance().getCurrentMixCascade().isPayment()) {
                    object5 = this.SYNC_NEW_SERVICES;
                    synchronized (object5) {
                        if (this.m_newServicesID < 0) {
                            this.m_newServicesID = this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_LBL_NEW_SERVICES_FOUND), 1, false, this.m_listenerNewServices);
                        }
                    }
                }
            } else if (((AbstractMessage)object2).getMessageCode() == 3) {
                object5 = Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).getEntrySnapshotAsEnumeration();
                bl = true;
                while (object5.hasMoreElements()) {
                    if (Database.getInstance(class$anon$infoservice$MixCascade == null ? JAPNewView.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryById(((NewCascadeIDEntry)object5.nextElement()).getCascadeId()) == null) continue;
                    bl = false;
                    break;
                }
            } else if (((AbstractMessage)object2).getMessageCode() == 4) {
                bl = true;
            }
            if (bl) {
                object5 = this.SYNC_NEW_SERVICES;
                synchronized (object5) {
                    if (this.m_newServicesID >= 0) {
                        this.m_StatusPanel.removeStatusMsg(this.m_newServicesID);
                        this.m_newServicesID = -1;
                    }
                }
            }
        } else if (observable instanceof TrustModel.InnerObservable || observable == TrustModel.getObservable()) {
            object2 = JAPController.getInstance().getCurrentMixCascade();
            this.tryShowInsecureMessage((MixCascade)object2);
            this.m_bTrustChanged = true;
            this.updateValues(false);
        } else if (object != null && (object.equals(JAPModel.CHANGED_INFOSERVICE_AUTO_UPDATE) || object.equals(JAPModel.CHANGED_ALLOW_INFOSERVICE_DIRECT_CONNECTION))) {
            runnable = new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    if (!JAPController.getInstance().isShuttingDown() && (JAPModel.isInfoServiceDisabled() || JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1 && !JAPController.getInstance().isAnonConnected())) {
                        Object object = JAPNewView.this.SYNC_STATUS_ENABLE_IS;
                        synchronized (object) {
                            if (JAPNewView.this.m_enableInfoServiceID < 0) {
                                JAPNewView.this.m_enableInfoServiceID = JAPNewView.this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_IS_DEACTIVATED), 2, false, JAPNewView.this.m_listenerEnableIS);
                            }
                        }
                    }
                    Object object = JAPNewView.this.SYNC_STATUS_ENABLE_IS;
                    synchronized (object) {
                        if (JAPNewView.this.m_enableInfoServiceID >= 0) {
                            JAPNewView.this.m_StatusPanel.removeStatusMsg(JAPNewView.this.m_enableInfoServiceID);
                            JAPNewView.this.m_enableInfoServiceID = -1;
                        }
                    }
                }
            };
        } else if (object != null && object.equals(JAPModel.CHANGED_SHOW_CLOSED_BUTTON)) {
            if (JAPModel.getInstance().isCloseButtonShown() || JAPDll.getDllVersion() == null) {
                this.m_bttnQuit.setText(JAPMessages.getString("quitButton"));
                this.m_bttnQuit.setToolTipText(JAPMessages.getString("quitButton"));
                JAPUtil.setMnemonic(this.m_bttnQuit, JAPMessages.getString("quitButtonMn"));
                this.m_bttnQuit.setVisible(true);
            } else {
                this.m_bttnQuit.setText(JAPMessages.getString(MSG_BTN_HIDE));
                this.m_bttnQuit.setToolTipText(JAPMessages.getString(MSG_BTN_HIDE_EXPLAIN));
                JAPUtil.setMnemonic(this.m_bttnQuit, JAPMessages.getString("MnBtnHide"));
                this.m_bttnQuit.setVisible(false);
            }
        } else {
            if (observable == Database.getInstance(class$anon$infoservice$MessageDBEntry == null ? (class$anon$infoservice$MessageDBEntry = JAPNewView.class$("anon.infoservice.MessageDBEntry")) : class$anon$infoservice$MessageDBEntry)) {
                object2 = (DatabaseMessage)object;
                if (((AbstractMessage)object2).getMessageData() == null) {
                    return;
                }
                if (((AbstractMessage)object2).getMessageCode() == 5) {
                    return;
                }
                final MessageDBEntry messageDBEntry = (MessageDBEntry)((AbstractMessage)object2).getMessageData();
                if (messageDBEntry.isForFreeCascadesOnly() && AccountCreator.checkValidAccount()) {
                    return;
                }
                Hashtable hashtable = this.m_messageIDs;
                synchronized (hashtable) {
                    if (messageDBEntry != null && (((AbstractMessage)object2).getMessageCode() == 1 || ((AbstractMessage)object2).getMessageCode() == 2)) {
                        if (messageDBEntry.isDummy()) {
                            MessageDBEntry messageDBEntry2 = (MessageDBEntry)this.m_messageIDs.remove(messageDBEntry.getId());
                            if (messageDBEntry2 != null) {
                                this.m_StatusPanel.removeStatusMsg(messageDBEntry2.getExternalIdentifier());
                            }
                            return;
                        }
                        final StatusPanel.ButtonListener buttonListener = new StatusPanel.ButtonListener(){

                            /*
                             * WARNING - Removed try catching itself - possible behaviour change.
                             */
                            public void actionPerformed(ActionEvent actionEvent) {
                                int n = JAPDialog.showConfirmDialog((Component)JAPNewView.this, actionEvent != null ? JAPMessages.getString(MSG_DELETE_MESSAGE_EXPLAIN) : (messageDBEntry.getPopupText(JAPMessages.getLocale()) != null ? messageDBEntry.getPopupText(JAPMessages.getLocale()) : messageDBEntry.getText(JAPMessages.getLocale())), JAPMessages.getString(JAPDialog.MSG_TITLE_INFO), new JAPDialog.Options(2){

                                    public String getCancelText() {
                                        return JAPMessages.getString(DialogContentPane.MSG_OK);
                                    }

                                    public String getYesOKText() {
                                        return JAPMessages.getString(MSG_DELETE_MESSAGE);
                                    }
                                }, 1, (JAPDialog.ILinkedInformation)new JAPDialog.AbstractLinkedURLAdapter(){

                                    public boolean isOnTop() {
                                        return true;
                                    }

                                    public URL getUrl() {
                                        return messageDBEntry.getURL(JAPMessages.getLocale());
                                    }

                                    public String getMessage() {
                                        return JAPMessages.getString(MSG_VIEW_MESSAGE);
                                    }
                                });
                                if (n == 0) {
                                    Hashtable hashtable = JAPNewView.this.m_messageIDs;
                                    synchronized (hashtable) {
                                        JAPNewView.this.m_StatusPanel.removeStatusMsg(messageDBEntry.getExternalIdentifier());
                                        JAPNewView.this.m_messageIDs.remove(messageDBEntry.getId());
                                        Database.getInstance(class$anon$infoservice$DeletedMessageIDDBEntry == null ? (class$anon$infoservice$DeletedMessageIDDBEntry = JAPNewView.class$("anon.infoservice.DeletedMessageIDDBEntry")) : class$anon$infoservice$DeletedMessageIDDBEntry).update(new DeletedMessageIDDBEntry(messageDBEntry));
                                    }
                                }
                            }

                            public boolean isButtonShown() {
                                ClickedMessageIDDBEntry clickedMessageIDDBEntry = (ClickedMessageIDDBEntry)Database.getInstance(class$anon$infoservice$ClickedMessageIDDBEntry == null ? (class$anon$infoservice$ClickedMessageIDDBEntry = JAPNewView.class$("anon.infoservice.ClickedMessageIDDBEntry")) : class$anon$infoservice$ClickedMessageIDDBEntry).getEntryById(messageDBEntry.getId());
                                return clickedMessageIDDBEntry != null && clickedMessageIDDBEntry.getVersionNumber() >= messageDBEntry.getVersionNumber();
                            }
                        };
                        DeletedMessageIDDBEntry deletedMessageIDDBEntry = (DeletedMessageIDDBEntry)Database.getInstance(class$anon$infoservice$DeletedMessageIDDBEntry == null ? (class$anon$infoservice$DeletedMessageIDDBEntry = JAPNewView.class$("anon.infoservice.DeletedMessageIDDBEntry")) : class$anon$infoservice$DeletedMessageIDDBEntry).getEntryById(messageDBEntry.getId());
                        if (!(deletedMessageIDDBEntry != null && deletedMessageIDDBEntry.getVersionNumber() >= messageDBEntry.getVersionNumber() || this.m_messageIDs.get(messageDBEntry.getId()) != null || messageDBEntry.isDummy())) {
                            int n = this.m_StatusPanel.addStatusMsg(messageDBEntry.getText(JAPMessages.getLocale()), 1, false, new ActionListener(){

                                public void actionPerformed(ActionEvent actionEvent) {
                                    Database.getInstance(class$anon$infoservice$ClickedMessageIDDBEntry == null ? (class$anon$infoservice$ClickedMessageIDDBEntry = JAPNewView.class$("anon.infoservice.ClickedMessageIDDBEntry")) : class$anon$infoservice$ClickedMessageIDDBEntry).update(new ClickedMessageIDDBEntry(messageDBEntry));
                                    AbstractOS.getInstance().openURL(messageDBEntry.getURL(JAPMessages.getLocale()));
                                }
                            }, buttonListener);
                            messageDBEntry.setExternalIdentifier(n);
                            this.m_messageIDs.put(messageDBEntry.getId(), messageDBEntry);
                        }
                        if (messageDBEntry.isPopupShown() && !buttonListener.isButtonShown()) {
                            new Thread(new Runnable(){

                                public void run() {
                                    Database.getInstance(class$anon$infoservice$ClickedMessageIDDBEntry == null ? (class$anon$infoservice$ClickedMessageIDDBEntry = JAPNewView.class$("anon.infoservice.ClickedMessageIDDBEntry")) : class$anon$infoservice$ClickedMessageIDDBEntry).update(new ClickedMessageIDDBEntry(messageDBEntry));
                                    buttonListener.actionPerformed(null);
                                }
                            }).start();
                        }
                    } else {
                        if (messageDBEntry != null && ((AbstractMessage)object2).getMessageCode() == 3) {
                            MessageDBEntry messageDBEntry3 = (MessageDBEntry)this.m_messageIDs.remove(messageDBEntry.getId());
                            if (messageDBEntry3 != null) {
                                this.m_StatusPanel.removeStatusMsg(messageDBEntry3.getExternalIdentifier());
                            }
                            return;
                        }
                        if (((AbstractMessage)object2).getMessageCode() == 4) {
                            Enumeration enumeration = this.m_messageIDs.elements();
                            while (enumeration.hasMoreElements()) {
                                this.m_StatusPanel.removeStatusMsg(((MessageDBEntry)enumeration.nextElement()).getExternalIdentifier());
                            }
                            this.m_StatusPanel.removeAll();
                        }
                    }
                }
            }
            if (observable == Database.getInstance(class$anon$infoservice$JavaVersionDBEntry == null ? (class$anon$infoservice$JavaVersionDBEntry = JAPNewView.class$("anon.infoservice.JavaVersionDBEntry")) : class$anon$infoservice$JavaVersionDBEntry)) {
                JavaVersionDBEntry javaVersionDBEntry;
                if (JAPController.getInstance().hasPortableJava()) {
                    return;
                }
                object2 = (DatabaseMessage)object;
                if (((AbstractMessage)object2).getMessageData() == null) {
                    return;
                }
                if ((((AbstractMessage)object2).getMessageCode() == 1 || ((AbstractMessage)object2).getMessageCode() == 2) && (javaVersionDBEntry = (JavaVersionDBEntry)((AbstractMessage)object2).getMessageData()) != null && (javaVersionDBEntry.isJavaTooOld() || javaVersionDBEntry.isJavaNoMoreSupported())) {
                    if (javaVersionDBEntry.isUpdateForced() || JAPModel.getInstance().isReminderForJavaUpdateActivated()) {
                        Object object6 = this.SYNC_STATUS_UPDATE_AVAILABLE;
                        synchronized (object6) {
                            if (this.m_updateAvailableID < 0) {
                                this.m_updateAvailableID = this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_UPDATE), 1, false, this.m_listenerUpdate);
                            }
                        }
                    }
                    if ((javaVersionDBEntry.isUpdateForced() || javaVersionDBEntry.isJavaNoMoreSupported() || JAPModel.getInstance().isReminderForJavaUpdateActivated()) && !JAPController.getInstance().isConfigAssistantShown()) {
                        new Runnable(){

                            /*
                             * WARNING - Removed try catching itself - possible behaviour change.
                             */
                            public void run() {
                                int n = 3;
                                int n2 = 0;
                                String string = JAPMessages.getString(MSG_TITLE_OLD_JAVA);
                                String string2 = "";
                                if (javaVersionDBEntry.isUpdateForced() || javaVersionDBEntry.isJavaNoMoreSupported()) {
                                    n = 2;
                                    string = JAPMessages.getString(MSG_JAVA_FORCED_TITLE);
                                    string2 = "<p><b>" + JAPMessages.getString(MSG_JAVA_FORCED_EXPLAIN, JavaVersionDBEntry.CURRENT_JAVA_VERSION) + "</b></p>";
                                    if (javaVersionDBEntry.isJavaNoMoreSupported() && !javaVersionDBEntry.isJavaTooOld()) {
                                        n2 = -1;
                                        string2 = string2 + "<br><p><b>" + JAPMessages.getString(MSG_JAVA_FORCED_OS, "\u2265 " + javaVersionDBEntry.getLastSupportedJREVersion()) + "</b></p>";
                                    } else {
                                        string2 = string2 + "<br><p>" + JAPMessages.getString(MSG_JAVA_FORCED_QUESTION) + "</p>";
                                    }
                                } else {
                                    string2 = JAPMessages.getString(MSG_OLD_JAVA_HINT, new Object[]{javaVersionDBEntry.getJREVersion()});
                                }
                                JAPDialog.LinkedCheckBox linkedCheckBox = null;
                                if (!javaVersionDBEntry.isUpdateForced() && !javaVersionDBEntry.isJavaNoMoreSupported()) {
                                    linkedCheckBox = new JAPDialog.LinkedCheckBox(false);
                                }
                                if (JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), string2, string, n2, n, linkedCheckBox) == 0 && n2 != -1) {
                                    JAPNewView.this.showJavaUpdateDialog(javaVersionDBEntry);
                                }
                                if (linkedCheckBox != null && linkedCheckBox.getState()) {
                                    JAPModel.getInstance().setReminderForJavaUpdate(false);
                                    Object object = JAPNewView.this.SYNC_STATUS_UPDATE_AVAILABLE;
                                    synchronized (object) {
                                        if (JAPVersionInfo.getRecommendedUpdate("00.20.001", true) == null && JAPNewView.this.m_updateAvailableID >= 0) {
                                            JAPNewView.this.m_StatusPanel.removeStatusMsg(JAPNewView.this.m_updateAvailableID);
                                            JAPNewView.this.m_updateAvailableID = -1;
                                        }
                                    }
                                }
                            }
                        }.run();
                    }
                }
            } else if (observable == JAPModel.getInstance().getRoutingSettings()) {
                object2 = (JAPRoutingMessage)object;
                JAPRoutingSettings jAPRoutingSettings = JAPModel.getInstance().getRoutingSettings();
                synchronized (jAPRoutingSettings) {
                    if (object2 != null && ((AbstractMessage)object2).getMessageCode() == 16) {
                        if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder() && this.m_ForwardingID < 0) {
                            this.m_ForwardingID = this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_ANTI_CENSORSHIP), 2, false, new ActionListener(){

                                public void actionPerformed(ActionEvent actionEvent) {
                                    JAPDialog.showMessageDialog((Component)JAPNewView.this, JAPMessages.getString(JAPConfNetwork.MSG_SLOW_ANTI_CENSORSHIP), (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("forwarding_client"));
                                }
                            });
                        } else if (!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder() && this.m_ForwardingID >= 0) {
                            this.m_StatusPanel.removeStatusMsg(this.m_ForwardingID);
                            this.m_ForwardingID = -1;
                        }
                    }
                }
            }
        }
        if (runnable != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                try {
                    SwingUtilities.invokeAndWait(runnable);
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.GUI, exception);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showIconifiedView(boolean bl) {
        this.m_comboAnonServices.closeCascadePopupMenu();
        Object object = this.SYNC_ICONIFIED_VIEW;
        synchronized (object) {
            if (this.m_ViewIconified != null) {
                this.m_ViewIconified.setVisible(true);
                this.setVisible(false);
                this.m_ViewIconified.toFront();
                if (!bl) {
                    new Thread(){

                        public void run() {
                            JAPController.showNonAnonymousWarning(JAPMessages.getString(DialogContentPane.MSG_IGNORE), false);
                        }
                    }.start();
                }
            }
        }
    }

    public void currentServiceChanged(AnonServerDescription anonServerDescription) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connectionEstablished(AnonServerDescription anonServerDescription) {
        Object object = this.SYNC_MSG_INSECURE;
        synchronized (object) {
            this.removeStatusMsg(this.m_msgIDInsecure);
        }
        if (anonServerDescription != null && anonServerDescription instanceof MixCascade) {
            object = (MixCascade)anonServerDescription;
            Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = JAPNewView.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).remove(((MixCascade)object).getMixIDsAsString());
            this.tryShowInsecureMessage((MixCascade)object);
        }
        new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Object object = JAPNewView.this.m_connectionEstablishedSync;
                synchronized (object) {
                    JAPNewView.this.m_connectionEstablishedSync.notifyAll();
                }
            }
        }).start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryShowInsecureMessage(final MixCascade mixCascade) {
        if (AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE == mixCascade) {
            return;
        }
        if (mixCascade.getNumberOfOperators() <= 1 || mixCascade.getDataRetentionInformation() != null) {
            boolean bl = false;
            Object object = this.SYNC_MSG_INSECURE;
            synchronized (object) {
                this.removeStatusMsg(this.m_msgIDInsecure);
                this.m_msgIDInsecure = this.m_StatusPanel.addStatusMsg(bl ? JAPMessages.getString(MSG_MITM_WARNING_TITLE) : JAPMessages.getString(MSG_OBSERVABLE_TITLE), 2, false, new ActionListener(){

                    public void actionPerformed(ActionEvent actionEvent) {
                        TrustModel trustModel = TrustModel.getCurrentTrustModel();
                        if (mixCascade.getDataRetentionInformation() != null) {
                            JAPDialog.showWarningDialog(JAPNewView.this, JAPMessages.getString(MSG_DATA_RETENTION_EXPLAIN, new String[]{"<b>" + mixCascade.getName() + "</b>", "<i>" + JAPMessages.getString("confButton") + "</i>"}));
                        } else if (mixCascade.getNumberOfOperators() <= 1) {
                            JAPDialog.showWarningDialog(JAPNewView.this, JAPMessages.getString(MSG_OBSERVABLE_EXPLAIN, "<b>" + mixCascade.getName() + "</b>"));
                        }
                        JAPNewView.this.doClickOnCascadeChooser();
                    }
                });
            }
        }
    }

    public void dataChainErrorSignaled(AnonServiceException anonServiceException) {
        this.addStatusMsg(JAPMessages.getString(MSG_ERROR_PROXY), 0, true);
    }

    public void integrityErrorSignaled(AnonServiceException anonServiceException) {
    }

    public void dispose() {
        this.m_blinkJobs.stop();
        this.m_transferedBytesJobs.stop();
        this.m_channelsChangedJobs.stop();
        this.m_packetMixedJobs.stop();
        this.m_flippingPanelPayment.stopUpdateQueue();
        super.dispose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnected() {
        Object object = this.SYNC_MSG_INSECURE;
        synchronized (object) {
            this.removeStatusMsg(this.m_msgIDInsecure);
        }
        new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Object object = JAPNewView.this.m_connectionEstablishedSync;
                synchronized (object) {
                    JAPNewView.this.m_connectionEstablishedSync.notifyAll();
                }
            }
        }).start();
        this.updateValues(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connecting(AnonServerDescription anonServerDescription, boolean bl) {
        Object object = this.SYNC_MSG_INSECURE;
        synchronized (object) {
            this.removeStatusMsg(this.m_msgIDInsecure);
        }
        this.showConnecting(false, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connectionError(AnonServiceException anonServiceException) {
        Object object = this.SYNC_MSG_INSECURE;
        synchronized (object) {
            this.removeStatusMsg(this.m_msgIDInsecure);
        }
        this.showConnecting(true, anonServiceException instanceof ServiceInterruptedException);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void actionPerformed(final ActionEvent actionEvent) {
        JAPNewView jAPNewView = this;
        this.m_comboAnonServices.closeCascadePopupMenu();
        Object object = this.SYNC_ACTION;
        synchronized (object) {
            if (this.m_bActionPerformed) {
                return;
            }
            this.m_bActionPerformed = true;
        }
        object = new Thread(new Runnable(){

            public void run() {
                Runnable runnable = null;
                Object object = actionEvent.getSource();
                if (object == JAPNewView.this.m_bttnQuit) {
                    if (JAPDll.getDllVersion() == null || JAPModel.getInstance().isCloseButtonShown()) {
                        JAPController.goodBye(true);
                    } else {
                        JAPNewView.this.setState(1);
                    }
                } else if (object == JAPNewView.this.m_bttnIconify) {
                    JAPNewView.this.showIconifiedView(false);
                } else if (object == JAPNewView.this.m_bttnConf) {
                    JAPNewView.this.showConfigDialog();
                } else if (object == JAPNewView.this.m_btnAbout) {
                    JAPNewView.this.m_comboAnonServices.closeCascadePopupMenu();
                    JAPController.aboutJAP();
                } else if (object == JAPNewView.this.m_btnAssistant) {
                    JAPController.getInstance().showInstallationAssistant(3);
                } else if (object == JAPNewView.this.m_bttnHelp) {
                    JAPNewView.this.showHelpWindow();
                } else if (object == JAPNewView.this.m_rbAnonOn || object == JAPNewView.this.m_rbAnonOff) {
                    JAPNewView.this.m_bActionPerformed = false;
                    runnable = new Runnable(){

                        public void run() {
                            if (JAPNewView.this.m_rbAnonOn.isSelected()) {
                                (this).JAPNewView.this.m_Controller.start();
                            } else {
                                (this).JAPNewView.this.m_Controller.stop();
                            }
                        }
                    };
                } else if (object == JAPNewView.this.m_cbAnonymityOn) {
                    JAPNewView.this.m_bActionPerformed = false;
                    runnable = new Runnable(){

                        public void run() {
                            if (JAPNewView.this.m_cbAnonymityOn.isSelected()) {
                                if (!(this).JAPNewView.this.m_Controller.isConnecting() && !(this).JAPNewView.this.m_Controller.getAnonMode()) {
                                    (this).JAPNewView.this.m_Controller.start();
                                }
                            } else if ((this).JAPNewView.this.m_Controller.getAnonMode()) {
                                (this).JAPNewView.this.m_Controller.stop();
                            }
                        }
                    };
                } else {
                    LogHolder.log(7, LogType.GUI, "Event ?????: " + actionEvent.getSource());
                }
                if (runnable != null) {
                    try {
                        SwingUtilities.invokeAndWait(runnable);
                    }
                    catch (Exception exception) {
                        LogHolder.log(3, LogType.GUI, exception);
                    }
                }
                JAPNewView.this.m_bActionPerformed = false;
            }
        });
        ((Thread)object).start();
    }

    private void showConnecting(final boolean bl, final boolean bl2) {
        Thread thread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                boolean bl3 = false;
                Object object = JAPNewView.this.m_connectionEstablishedSync;
                synchronized (object) {
                    if (!bl && !bl2 || JAPModel.isAutomaticallyReconnected()) {
                        if (JAPNewView.this.m_Controller.getAnonMode() && !JAPNewView.this.m_Controller.isAnonConnected()) {
                            if (JAPNewView.this.m_bShowConnecting) {
                                return;
                            }
                            JAPNewView.this.m_bShowConnecting = true;
                            JAPNewView.this.updateValues(true);
                            int n = JAPNewView.this.addStatusMsg(JAPMessages.getString("setAnonModeSplashConnect"), 1, false);
                            try {
                                JAPNewView.this.m_connectionEstablishedSync.wait();
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            JAPNewView.this.removeStatusMsg(n);
                            JAPNewView.this.m_bShowConnecting = false;
                        }
                        JAPNewView.this.updateValues(false);
                    } else {
                        JAPNewView.this.updateValues(false);
                        bl3 = true;
                    }
                    JAPNewView.this.m_connectionEstablishedSync.notifyAll();
                }
                if (bl3 && !bl2) {
                    JAPController.getInstance().showRequestAutoReconnectDialog(JAPMessages.getString(MSG_ERROR_DISCONNECTED));
                }
            }
        }, "Wait for connecting");
        thread.setDaemon(true);
        thread.start();
    }

    public boolean isShowingPaymentError() {
        return this.m_flippingPanelPayment.isShowingError();
    }

    private void showHelpWindow() {
        this.m_comboAnonServices.closeCascadePopupMenu();
        JAPHelp jAPHelp = JAPHelp.getInstance();
        jAPHelp.setContext(JAPHelpContext.createHelpContext("index", this));
        jAPHelp.loadCurrentContext();
    }

    public void setVisible(boolean bl) {
        boolean bl2 = true;
        if (bl && !this.isVisible()) {
            boolean bl3 = bl2 = !JAPDll.showWindowFromTaskbar();
        }
        if (bl2) {
            super.setVisible(bl);
        }
    }

    public void showPaymentDialog(String string) {
        if (string == null) {
            this.showConfigDialog("PAYMENT_TAB", new Boolean(true));
        } else {
            this.showConfigDialog("PAYMENT_TAB", string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveWindowPositions() {
        JAPModel.getInstance().setMainWindowLocation(this.getLocation());
        Object object = this.SYNC_ICONIFIED_VIEW;
        synchronized (object) {
            if (this.getViewIconified() != null && this.m_miniMovedAdapter != null) {
                JAPModel.getInstance().setIconifiedWindowLocation(this.getViewIconified().getLocation());
            }
        }
        if (this.m_dlgConfig != null) {
            JAPModel.getInstance().setConfigWindowLocation(this.m_dlgConfig.getLocation());
        }
        if (JAPHelp.getHelpDialog() != null) {
            JAPModel.getInstance().setHelpWindowLocation(JAPHelp.getHelpDialog().getLocation());
        }
        if (JAPHelp.getHelpDialog() != null) {
            JAPModel.getInstance().setHelpWindowSize(JAPHelp.getHelpDialog().getSize());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showConfigDialog(String string, Object object) {
        this.m_comboAnonServices.closeCascadePopupMenu();
        if (this.m_bConfigActive) {
            return;
        }
        this.m_bConfigActive = true;
        Object object2 = this.LOCK_CONFIG;
        synchronized (object2) {
            if (!this.m_bConfigActive) {
                return;
            }
            if (this.m_dlgConfig == null) {
                Cursor cursor = this.getCursor();
                this.setCursor(Cursor.getPredefinedCursor(3));
                this.m_dlgConfig = new JAPConf(this, this.m_bWithPayment);
                this.m_dlgConfig.addComponentListener(this.m_configMovedAdapter);
                this.setCursor(cursor);
            }
            if (this.m_dlgConfig != null) {
                this.m_dlgConfig.selectCard(string, object);
                new Thread(new Runnable(){

                    public void run() {
                        JAPNewView.this.m_dlgConfig.setVisible(true);
                    }
                }).start();
            }
            this.m_bConfigActive = false;
        }
    }

    public Component getCurrentView() {
        if (this.m_dlgConfig != null && this.m_dlgConfig.isVisible()) {
            return this.m_dlgConfig.getContentPane();
        }
        return this.getContentPane();
    }

    private void setOptimalSize() {
        try {
            if (!JAPModel.isSmallDisplay()) {
                this.pack();
                this.setResizable(true);
            }
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.GUI, "Hm.. Error by Pack - Has To be fixed!!");
        }
    }

    public void doClickOnCascadeChooser() {
        this.m_comboAnonServices.showPopup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setSelection(boolean bl) {
        Object object = this.SYNC_SELECTION;
        synchronized (object) {
            this.m_cbAnonymityOn.removeActionListener(this);
            this.m_rbAnonOn.removeActionListener(this);
            this.m_rbAnonOff.removeActionListener(this);
            this.m_rbAnonOn.setSelected(bl);
            this.m_rbAnonOff.setSelected(!bl);
            this.m_cbAnonymityOn.setSelected(bl);
            this.m_cbAnonymityOn.addActionListener(this);
            this.m_rbAnonOn.addActionListener(this);
            this.m_rbAnonOff.addActionListener(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUpdateValues() {
        JavaVersionDBEntry javaVersionDBEntry;
        boolean bl;
        Object object = this.SYNC_ICONIFIED_VIEW;
        synchronized (object) {
            if (this.m_ViewIconified != null) {
                this.m_ViewIconified.updateValues(false);
            }
        }
        boolean bl2 = bl = JAPVersionInfo.getRecommendedUpdate("00.20.001", true) != null;
        if (!JAPController.getInstance().hasPortableJava() && (javaVersionDBEntry = JavaVersionDBEntry.getNewJavaVersion()) != null && (javaVersionDBEntry.isUpdateForced() || JAPModel.getInstance().isReminderForJavaUpdateActivated())) {
            bl = true;
        }
        Object object2 = this.SYNC_STATUS_UPDATE_AVAILABLE;
        synchronized (object2) {
            if (bl) {
                if (this.m_updateAvailableID < 0) {
                    this.m_updateAvailableID = this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_UPDATE), 1, false, this.m_listenerUpdate);
                }
            } else if (this.m_updateAvailableID >= 0) {
                this.m_StatusPanel.removeStatusMsg(this.m_updateAvailableID);
                this.m_updateAvailableID = -1;
            }
        }
        if (!JAPController.getInstance().isShuttingDown() && (JAPModel.isInfoServiceDisabled() || JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1 && !JAPController.getInstance().isAnonConnected())) {
            object2 = this.SYNC_STATUS_ENABLE_IS;
            synchronized (object2) {
                if (this.m_enableInfoServiceID < 0) {
                    this.m_enableInfoServiceID = this.m_StatusPanel.addStatusMsg(JAPMessages.getString(MSG_IS_DEACTIVATED), 2, false, this.m_listenerEnableIS);
                }
            }
        }
        object2 = this.SYNC_STATUS_ENABLE_IS;
        synchronized (object2) {
            if (this.m_enableInfoServiceID >= 0) {
                this.m_StatusPanel.removeStatusMsg(this.m_enableInfoServiceID);
                this.m_enableInfoServiceID = -1;
            }
        }
        object2 = this.m_Controller.getCurrentMixCascade();
        Hashtable hashtable = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPNewView.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryHash();
        if (object2 != null && !hashtable.containsKey(((MixCascade)object2).getId())) {
            hashtable.put(((MixCascade)object2).getId(), object2);
        }
        if (object2 != null) {
            object2 = (MixCascade)hashtable.get(((MixCascade)object2).getId());
        }
        if (!this.m_comboAnonServices.isPopupVisible()) {
            this.m_bIgnoreAnonComboEvents = true;
            if (object2 == null) {
                this.m_comboAnonServices.setMixCascade(this.m_Controller.getCurrentMixCascade());
            } else if (this.m_bTrustChanged || !JAPNewView.equals((MixCascade)object2, this.m_comboAnonServices.getMixCascade())) {
                this.m_bTrustChanged = false;
                this.m_comboAnonServices.setMixCascade((MixCascade)object2);
            }
            this.m_comboAnonServices.setToolTipText(((MixCascade)object2).getName());
            if (this.m_comboAnonServices.getSelectedItem() == null) {
                this.m_comboAnonServices.setSelectedItem(object2);
            }
            this.m_comboAnonServices.validate();
            this.m_bIgnoreAnonComboEvents = false;
        }
        LogHolder.log(7, LogType.GUI, "Start updateValues");
        try {
            String string;
            Object object3;
            ImageIcon imageIcon;
            Object object4;
            int n;
            this.setSelection(this.m_Controller.getAnonMode());
            StatusInfo statusInfo = ((MixCascade)object2).getCurrentStatus();
            if (!GUIUtils.isLoadingImagesStopped()) {
                this.m_labelAnonMeter.setIcon(this.getMeterImage((MixCascade)object2, statusInfo));
            }
            Color color = Color.blue;
            if (statusInfo.getAnonLevel() > 3) {
                color = Color.green;
            }
            this.m_progressAnonLevel.setFilledBarColor(color);
            if (statusInfo.getAnonLevel() < 0) {
                this.m_progressAnonLevel.setValue(0);
            } else {
                this.m_progressAnonLevel.setValue(statusInfo.getAnonLevel());
            }
            color = Color.blue;
            if (((MixCascade)object2).getDistribution() > 3) {
                color = Color.green;
            }
            this.m_progressDistribution.setFilledBarColor(color);
            this.m_progressDistribution.setValue(((MixCascade)object2).getDistribution());
            String string2 = "JonDo";
            string2 = string2 + "\n" + GUIUtils.trim(((MixCascade)object2).getName(), 25);
            if (object2 == AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) {
                this.m_labelAnonymity.setText(JAPMessages.getString("ngCascadeInfo"));
            } else {
                this.m_labelAnonymity.setText(((MixCascade)object2).getName() + ":");
            }
            if (statusInfo.getNrOfActiveUsers() > -1) {
                this.m_lblUsers.setText(Integer.toString(statusInfo.getNrOfActiveUsers()) + (((MixCascade)object2).getMaxUsers() > 0 ? " / " + ((MixCascade)object2).getMaxUsers() : ""));
            } else {
                this.m_lblUsers.setText("");
            }
            if (this.m_Controller.isAnonConnected() && statusInfo.getNrOfActiveUsers() > -1) {
                string2 = string2 + "\n" + JAPMessages.getString(SystrayPopupMenu.MSG_ANONYMITY_ASCII) + ": ";
                string2 = string2 + ((MixCascade)object2).getDistribution() + "," + statusInfo.getAnonLevel() + " / 6,6";
                if (!this.isChangingTitle()) {
                    if (this.m_bIsIconified) {
                        this.setTitle(JAPModel.getInstance().getProgramName() + " (" + ((MixCascade)object2).getDistribution() + "," + statusInfo.getAnonLevel() + " / 6,6" + ")");
                    } else {
                        this.setTitle(this.m_Title);
                    }
                }
            }
            this.m_lawListener.setCascadeInfo((MixCascade)object2);
            if (((MixCascade)object2).getNumberOfMixes() <= 0) {
                this.m_lblIPFlag.setIcon(null);
            }
            for (n = 0; n < ((MixCascade)object2).getNumberOfMixes() && n < this.m_labelOperatorFlags.length; ++n) {
                Object object5;
                object4 = ((MixCascade)object2).getMixInfo(n);
                Color color2 = this.m_panelAnonService.getBackground();
                ImageIcon imageIcon2 = null;
                imageIcon = null;
                if (object4 != null && ((MixInfo)object4).getCertPath() != null && ((MixInfo)object4).getCertPath().getIssuer() != null) {
                    MultiCertPath multiCertPath = ((MixInfo)object4).getCertPath();
                    object3 = new CountryMapper(multiCertPath.getIssuer().getCountryCode(), JAPMessages.getLocale()).toString();
                    string = new CountryMapper(multiCertPath.getSubject().getCountryCode(), JAPMessages.getLocale()).toString();
                    if (n < ((MixCascade)object2).getNumberOfOperatorsShown()) {
                        imageIcon = GUIUtils.loadImageIcon("flags/" + multiCertPath.getIssuer().getCountryCode() + ".png");
                    }
                    imageIcon2 = GUIUtils.loadImageIcon("flags/" + multiCertPath.getSubject().getCountryCode() + ".png");
                    this.m_adapterOperator[n].setMixInfo((MixCascade)object2, n, ((MixCascade)object2).getNumberOfOperatorsShown());
                    this.m_adapterExitMix.setMixInfo((MixCascade)object2, n, ((MixCascade)object2).getNumberOfMixes());
                    object5 = "";
                    if (multiCertPath.isVerified()) {
                        if (!multiCertPath.isValid(new Date())) {
                            color2 = Color.yellow;
                            object5 = ", " + JAPMessages.getString(MixDetailsDialog.MSG_INVALID);
                        } else if (multiCertPath.countVerifiedAndValidPaths() > 2) {
                            color2 = Color.green;
                            object5 = ", " + JAPMessages.getString(MixDetailsDialog.MSG_INDEPENDENT_CERTIFICATIONS, "" + multiCertPath.countVerifiedAndValidPaths());
                        } else if (multiCertPath.countVerifiedAndValidPaths() > 1) {
                            color2 = new Color(100, 215, 255);
                            object5 = ", " + JAPMessages.getString(MixDetailsDialog.MSG_INDEPENDENT_CERTIFICATIONS, "" + multiCertPath.countVerifiedAndValidPaths());
                        }
                    } else {
                        color2 = Color.red;
                        object5 = ", " + JAPMessages.getString(MixDetailsDialog.MSG_NOT_VERIFIED);
                    }
                    this.m_labelOperatorFlags[n].setToolTipText((String)object3 + (String)object5);
                    this.m_lblIPFlag.setToolTipText(string + (String)object5);
                }
                if (n == ((MixCascade)object2).getNumberOfMixes() - 1) {
                    this.m_lblIPFlag.setIcon(imageIcon2);
                }
                this.m_labelOperatorFlags[n].setIcon(imageIcon);
                object5 = this.m_labelOperatorFlags[n];
                synchronized (object5) {
                    this.m_labelOperatorFlags[n].setBorder(BorderFactory.createLineBorder(color2, 2));
                    this.m_lblIPFlag.setBorder(BorderFactory.createLineBorder(color2, 2));
                    continue;
                }
            }
            for (n = ((MixCascade)object2).getNumberOfOperatorsShown(); n < this.m_labelOperatorFlags.length; ++n) {
                this.m_labelOperatorFlags[n].setIcon(null);
                this.m_labelOperatorFlags[n].setBorder(BorderFactory.createLineBorder(this.m_panelAnonService.getBackground(), 2));
            }
            for (n = 0; n < this.m_lawFlags.length; ++n) {
                if (n == ((MixCascade)object2).getNumberOfOperatorsShown() - 1 && ((MixCascade)object2).getDataRetentionInformation() != null) {
                    this.m_lawFlags[n].setVisible(true);
                    continue;
                }
                this.m_lawFlags[n].setVisible(false);
            }
            MixCascadeExitAddresses mixCascadeExitAddresses = (MixCascadeExitAddresses)Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = JAPNewView.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses).getEntryById(((MixCascade)object2).getId());
            if (mixCascadeExitAddresses == null) {
                this.m_lblIPValue.setText("");
            } else {
                object4 = mixCascadeExitAddresses.createExitAddressAsString();
                if (object4 == null) {
                    this.m_lblIPValue.setText("");
                } else {
                    this.m_lblIPValue.setText((String)object4);
                }
            }
            object4 = PerformanceInfo.getLowestCommonBoundEntry(((MixCascade)object2).getId());
            int n2 = 0;
            int n3 = 0;
            if (object4 != null) {
                boolean bl3;
                try {
                    TrustModel.getCurrentTrustModel().getAttribute(class$anon$client$TrustModel$SpeedAttribute == null ? (class$anon$client$TrustModel$SpeedAttribute = JAPNewView.class$("anon.client.TrustModel$SpeedAttribute")) : class$anon$client$TrustModel$SpeedAttribute).checkTrust((MixCascade)object2);
                    bl3 = true;
                }
                catch (TrustException trustException) {
                    bl3 = false;
                }
                n2 = ((PerformanceEntry)object4).getBound(0).getBound();
                n3 = ((PerformanceEntry)object4).getBestBound(0);
                if (n3 < n2) {
                    n3 = n2;
                }
                if (n2 < 0 || n2 == Integer.MAX_VALUE) {
                    this.m_labelSpeed.setText(JAPMessages.getString(MSG_UNKNOWN_PERFORMANCE));
                } else if (n2 == 0) {
                    this.m_labelSpeed.setText("< " + Util.formatKbitPerSecValueWithUnit(PerformanceEntry.BOUNDARIES[0][1], 0));
                } else if (PerformanceEntry.BOUNDARIES[0][PerformanceEntry.BOUNDARIES[0].length - 1] == n3) {
                    if (System.getProperty("java.version").compareTo("1.4") >= 0) {
                        this.m_labelSpeed.setText("\u2265 " + Util.formatKbitPerSecValueWithUnit(n2, 0));
                    } else {
                        this.m_labelSpeed.setText("> " + Util.formatKbitPerSecValueWithUnit(n2, 0));
                    }
                } else if (n3 == n2 || n3 == Integer.MAX_VALUE) {
                    this.m_labelSpeed.setText(Util.formatKbitPerSecValueWithUnit(n2, 0));
                } else {
                    this.m_labelSpeed.setText(Util.formatKbitPerSecValueWithoutUnit(n2, 0) + "-" + Util.formatKbitPerSecValueWithUnit(n3, 0));
                }
                if (bl3) {
                    this.m_labelSpeed.setForeground(this.m_lblUsers.getForeground());
                } else {
                    this.m_labelSpeed.setForeground(Color.red);
                }
                try {
                    TrustModel.getCurrentTrustModel().getAttribute(class$anon$client$TrustModel$DelayAttribute == null ? (class$anon$client$TrustModel$DelayAttribute = JAPNewView.class$("anon.client.TrustModel$DelayAttribute")) : class$anon$client$TrustModel$DelayAttribute).checkTrust((MixCascade)object2);
                    bl3 = true;
                }
                catch (TrustException trustException) {
                    bl3 = false;
                }
                n2 = ((PerformanceEntry)object4).getBound(1).getBound();
                n3 = ((PerformanceEntry)object4).getBestBound(1);
                if (n3 > n2) {
                    n3 = n2;
                }
                if (n2 <= 0) {
                    this.m_labelDelay.setText(JAPMessages.getString(MSG_UNKNOWN_PERFORMANCE));
                } else if (n2 == Integer.MAX_VALUE) {
                    this.m_labelDelay.setText("> " + PerformanceEntry.BOUNDARIES[1][PerformanceEntry.BOUNDARIES[1].length - 2] + " ms");
                } else if (PerformanceEntry.BOUNDARIES[1][0] == n3) {
                    if (System.getProperty("java.version").compareTo("1.4") >= 0) {
                        this.m_labelDelay.setText("\u2264 " + n2 + " ms");
                    } else {
                        this.m_labelDelay.setText("< " + n2 + " ms");
                    }
                } else if (n3 == n2 || n3 == 0) {
                    this.m_labelDelay.setText(n2 + " ms");
                } else {
                    this.m_labelDelay.setText(n2 + "-" + n3 + " ms");
                }
                if (bl3) {
                    this.m_labelDelay.setForeground(this.m_lblUsers.getForeground());
                } else {
                    this.m_labelDelay.setForeground(Color.red);
                }
            } else {
                this.m_labelSpeed.setText(JAPMessages.getString(MSG_UNKNOWN_PERFORMANCE));
                this.m_labelDelay.setText(JAPMessages.getString(MSG_UNKNOWN_PERFORMANCE));
                this.m_labelSpeed.setForeground(this.m_lblUsers.getForeground());
                this.m_labelDelay.setForeground(this.m_lblUsers.getForeground());
            }
            JAPDll.setSystrayTooltip(string2);
            LogHolder.log(7, LogType.GUI, "Finished updateValues");
            boolean bl4 = JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2;
            this.m_cbForwarding.setSelected(bl4);
            this.m_cbForwardingSmall.setSelected(bl4);
            imageIcon = null;
            string = null;
            object3 = this.SYNC_FORWARD_MSG;
            synchronized (object3) {
                if (bl4) {
                    int n4 = JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver().getCurrentState();
                    int n5 = JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver().getCurrentErrorCode();
                    if (n4 != this.m_msgForwardServerStatus) {
                        this.removeStatusMsg(this.m_msgForwardServer);
                        this.m_msgForwardServerStatus = 3;
                        if (this.m_mouseForwardError != null) {
                            this.m_labelForwardingErrorSmall.removeMouseListener(this.m_mouseForwardError);
                            this.m_labelForwardingError.removeMouseListener(this.m_mouseForwardError);
                            this.m_labelForwardingErrorSmall.setCursor(Cursor.getDefaultCursor());
                            this.m_labelForwardingError.setCursor(Cursor.getDefaultCursor());
                        }
                    }
                    if (n4 == 2) {
                        String string3 = "<font color='red'>" + JAPMessages.getString(JAPController.MSG_FORWARDER_REGISTRATION_ERROR_HEADER) + "</font><br><br>";
                        String string4 = "<br><br>" + JAPMessages.getString(JAPController.MSG_FORWARDER_REGISTRATION_ERROR_FOOTER);
                        if (!GUIUtils.isLoadingImagesStopped()) {
                            imageIcon = GUIUtils.loadImageIcon("warning.gif", true);
                        }
                        if (n5 == 1) {
                            string = string3 + JAPMessages.getString("settingsRoutingServerRegistrationEmptyListError") + string4;
                        } else if (n5 == 2) {
                            string = string3 + JAPMessages.getString("settingsRoutingServerRegistrationInfoservicesError") + string4;
                        } else if (n5 == 3) {
                            StringBuffer stringBuffer = new StringBuffer().append(string3);
                            StringBuffer stringBuffer2 = new StringBuffer().append("<b>");
                            JAPModel.getInstance().getRoutingSettings();
                            string = stringBuffer.append(JAPMessages.getString("settingsRoutingServerRegistrationVerificationError", stringBuffer2.append(JAPRoutingSettings.getServerPort()).append("</b>").toString())).append(string4).toString();
                        } else if (n5 == 4) {
                            string = string3 + JAPMessages.getString("settingsRoutingServerRegistrationUnknownError") + string4;
                        }
                        if (string != null) {
                            string = JAPMessages.getString(string);
                            if (this.m_msgForwardServerStatus == 3) {
                                final String string5 = string;
                                this.m_msgForwardServer = this.addStatusMsg(JAPMessages.getString(JAPController.MSG_FORWARDER_REG_ERROR_SHORT), 2, false, new ActionListener(){

                                    public void actionPerformed(ActionEvent actionEvent) {
                                        JAPDialog.showErrorDialog(JAPNewView.this.getCurrentView(), string5, (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("forwarding_server"));
                                    }
                                });
                                this.m_mouseForwardError = new MouseAdapter(){

                                    public void mouseClicked(MouseEvent mouseEvent) {
                                        JAPDialog.showErrorDialog(JAPNewView.this.getCurrentView(), string5, (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("forwarding_server"));
                                    }
                                };
                                this.m_labelForwardingErrorSmall.addMouseListener(this.m_mouseForwardError);
                                this.m_labelForwardingError.addMouseListener(this.m_mouseForwardError);
                                this.m_labelForwardingErrorSmall.setCursor(Cursor.getPredefinedCursor(12));
                                this.m_labelForwardingError.setCursor(Cursor.getPredefinedCursor(12));
                            }
                        }
                    }
                } else {
                    this.removeStatusMsg(this.m_msgForwardServer);
                    if (this.m_mouseForwardError != null) {
                        this.m_labelForwardingErrorSmall.removeMouseListener(this.m_mouseForwardError);
                        this.m_labelForwardingError.removeMouseListener(this.m_mouseForwardError);
                        this.m_labelForwardingErrorSmall.setCursor(Cursor.getDefaultCursor());
                        this.m_labelForwardingError.setCursor(Cursor.getDefaultCursor());
                    }
                    this.m_msgForwardServerStatus = 3;
                }
                if (!GUIUtils.isLoadingImagesStopped()) {
                    this.m_labelForwardingError.setIcon(imageIcon);
                    this.m_labelForwardingErrorSmall.setIcon(imageIcon);
                }
                this.m_labelForwardingError.setToolTipText("<html>" + string + "</html>");
                this.m_labelForwardingErrorSmall.setToolTipText("<html>" + string + "</html>");
                this.m_cbForwarding.setEnabled(!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder());
                this.m_cbForwardingSmall.setEnabled(!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder());
                this.m_comboAnonServices.setEnabled(!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder());
            }
            this.validate();
        }
        catch (Throwable throwable) {
            LogHolder.log(0, LogType.GUI, throwable);
        }
    }

    public JAPViewIconified getViewIconified() {
        return this.m_ViewIconified;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerViewIconified(JAPViewIconified jAPViewIconified) {
        Object object = this.SYNC_ICONIFIED_VIEW;
        synchronized (object) {
            if (this.m_ViewIconified != null) {
                this.m_ViewIconified.removeComponentListener(this.m_miniMovedAdapter);
            }
            this.m_ViewIconified = jAPViewIconified;
            this.m_miniMovedAdapter = new ComponentMovedAdapter();
            this.m_ViewIconified.addComponentListener(this.m_miniMovedAdapter);
        }
    }

    public void channelsChanged(final int n) {
        this.m_channelsChangedJobs.addJob(new JobQueue.Job(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void runJob() {
                JAPProgressBar jAPProgressBar = JAPNewView.this.m_progressOwnTrafficActivity;
                synchronized (jAPProgressBar) {
                    JAPNewView.this.m_currentChannels = n;
                    int n2 = Math.min(n, JAPNewView.this.m_progressOwnTrafficActivity.getMaximum());
                    JAPNewView.this.m_progressOwnTrafficActivity.setValue(n2);
                    JAPNewView.this.m_progressOwnTrafficActivitySmall.setValue(n2);
                    JAPNewView.this.m_progressOwnTrafficActivity.notify();
                }
            }
        });
    }

    public void packetMixed(final long l) {
        this.m_packetMixedJobs.addJob(new JobQueue.Job(true){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void runJob() {
                Object object = JAPNewView.this.SYNC_ICONIFIED_VIEW;
                synchronized (object) {
                    if (JAPNewView.this.m_ViewIconified != null) {
                        JAPNewView.this.m_ViewIconified.packetMixed(l);
                    }
                }
                object = new Runnable(){

                    public void run() {
                        String string = Util.formatBytesValueOnlyUnit(l, 1);
                        JAPNewView.this.m_labelOwnTrafficUnit.setText(string);
                        JAPNewView.this.m_labelOwnTrafficUnit.revalidate();
                        JAPNewView.this.m_labelOwnTrafficUnitSmall.setText(string);
                        JAPNewView.this.m_labelOwnTrafficUnitSmall.revalidate();
                        String string2 = Util.formatBytesValueWithoutUnit(l, 1);
                        JAPNewView.this.m_labelOwnTrafficBytes.setText(string2);
                        JAPNewView.this.m_labelOwnTrafficBytes.revalidate();
                        JAPNewView.this.m_labelOwnTrafficBytesSmall.setText(string2);
                        JAPNewView.this.m_labelOwnTrafficBytesSmall.revalidate();
                    }
                };
                try {
                    SwingUtilities.invokeAndWait((Runnable)object);
                }
                catch (InvocationTargetException invocationTargetException) {
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                object = null;
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        });
    }

    public void transferedBytes(long l, int n) {
        if (n == 1) {
            this.m_lTrafficWWW = l;
        } else if (n == 0) {
            this.m_lTrafficOther = l;
        }
        this.blink(l);
        this.m_transferedBytesJobs.addJob(new JobQueue.Job(){

            public void runJob() {
                Runnable runnable = new Runnable(){

                    public void run() {
                        String string = Util.formatBytesValueOnlyUnit(JAPNewView.this.m_lTrafficWWW, 1);
                        JAPNewView.this.m_labelOwnTrafficUnitWWW.setText(string);
                        JAPNewView.this.m_labelOwnTrafficUnitWWW.revalidate();
                        String string2 = Util.formatBytesValueWithoutUnit(JAPNewView.this.m_lTrafficWWW, 1);
                        JAPNewView.this.m_labelOwnTrafficBytesWWW.setText(string2);
                        JAPNewView.this.m_labelOwnTrafficBytesWWW.revalidate();
                        string = Util.formatBytesValueOnlyUnit(JAPNewView.this.m_lTrafficOther, 1);
                        JAPNewView.this.m_labelOwnTrafficUnitOther.setText(string);
                        JAPNewView.this.m_labelOwnTrafficUnitOther.revalidate();
                        string2 = Util.formatBytesValueWithoutUnit(JAPNewView.this.m_lTrafficOther, 1);
                        JAPNewView.this.m_labelOwnTrafficBytesOther.setText(string2);
                        JAPNewView.this.m_labelOwnTrafficBytesOther.revalidate();
                        JAPDll.onTraffic();
                    }
                };
                try {
                    SwingUtilities.invokeAndWait(runnable);
                }
                catch (InvocationTargetException invocationTargetException) {
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                runnable = null;
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        });
    }

    public Dimension getPreferredSize() {
        Dimension dimension = super.getPreferredSize();
        return dimension;
    }

    public int addStatusMsg(String string, int n, boolean bl) {
        return this.m_StatusPanel.addStatusMsg(string, n, bl);
    }

    public int addStatusMsg(String string, int n, boolean bl, ActionListener actionListener) {
        return this.m_StatusPanel.addStatusMsg(string, n, bl, actionListener);
    }

    public void removeStatusMsg(int n) {
        this.m_StatusPanel.removeStatusMsg(n);
    }

    private void showJavaUpdateDialog(JavaVersionDBEntry javaVersionDBEntry) {
        this.m_comboAnonServices.closeCascadePopupMenu();
        Object[] arrobject = new Object[]{JavaVersionDBEntry.CURRENT_JAVA_VERSION, JavaVersionDBEntry.CURRENT_JAVA_VENDOR, javaVersionDBEntry.getJREVersion(), javaVersionDBEntry.getVendorLongName(), javaVersionDBEntry.getVendor()};
        JAPDialog.showMessageDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_OLD_JAVA, arrobject) + (javaVersionDBEntry.getJREVersionName() == null ? "" : "<br>" + javaVersionDBEntry.getJREVersionName()), JAPMessages.getString(MSG_TITLE_OLD_JAVA), GUIUtils.createURLLink(javaVersionDBEntry.getDownloadURL(), null, "updateJava"));
    }

    private synchronized void fetchMixCascadesAsync(final boolean bl) {
        this.m_bttnReload.setEnabled(false);
        Runnable runnable = new Runnable(){

            public void run() {
                if (bl) {
                    ListenerInterface.unblockInterfacesFromDatabase(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPNewView.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry);
                }
                LogHolder.log(4, LogType.GUI, "Fetching InfoServices...");
                JAPNewView.this.m_Controller.updateInfoServices(!bl);
                LogHolder.log(4, LogType.GUI, "Fetching Payment instances...");
                JAPNewView.this.m_Controller.updatePaymentInstances(!bl);
                LogHolder.log(4, LogType.GUI, "Fetching Performance Infos...");
                JAPNewView.this.m_Controller.updatePerformanceInfo(!bl);
                LogHolder.log(4, LogType.GUI, "Fetching Mix cascades...");
                JAPNewView.this.m_Controller.fetchMixCascades(bl, !bl);
                LogHolder.log(4, LogType.GUI, "Fetching finished. Re-enable update button.");
                SwingUtilities.invokeLater(new Runnable(){

                    public void run() {
                        JAPNewView.this.m_bttnReload.setEnabled(true);
                    }
                });
            }
        };
        Thread thread = new Thread(runnable, "DoFetchMixCascades");
        thread.setDaemon(true);
        thread.start();
    }

    private void updateFonts() {
        this.m_bttnIconify.setIcon(GUIUtils.loadImageIcon(IMG_ICONIFY, true));
        this.m_btnAbout.setText("\u00a9");
    }

    private static boolean equals(MixCascade mixCascade, MixCascade mixCascade2) {
        return !(mixCascade == null && mixCascade2 != null || mixCascade != null && mixCascade2 == null) && (mixCascade == null || mixCascade.equals(mixCascade2) && mixCascade.isPayment() == mixCascade2.isPayment() && mixCascade.getName().equals(mixCascade2.getName()));
    }

    public void messageReceived(PayMessage payMessage) {
        ActionListener actionListener;
        boolean bl;
        final URL uRL = payMessage.getMessageLink();
        String string = payMessage.getMessageText();
        String string2 = payMessage.getShortMessage();
        int n = 0;
        boolean bl2 = uRL != null;
        boolean bl3 = bl = string != null && !string.equals("");
        if (!bl2 && !bl) {
            actionListener = null;
        } else if (bl2 && !bl) {
            actionListener = new ActionListener(){

                public void actionPerformed(ActionEvent actionEvent) {
                    AbstractOS.getInstance().openURL(uRL);
                }
            };
        } else {
            final String string3 = string2;
            final String string4 = string;
            final JAPNewView jAPNewView = this;
            if (bl2) {
                final JAPDialog.LinkedInformationAdapter linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                    public void clicked(boolean bl) {
                        AbstractOS.getInstance().openURL(uRL);
                    }

                    public String getMessage() {
                        String string = uRL.toString();
                        string = Util.replaceAll(string, "mailto:", "Email:");
                        string = Util.replaceAll(string, "http://", "Link:");
                        return string;
                    }

                    public int getType() {
                        return 2;
                    }
                };
                final JAPDialog.Options options = new JAPDialog.Options(2){

                    public String getYesOKText() {
                        return JAPMessages.getString("bttnOk");
                    }

                    public String getCancelText() {
                        return JAPMessages.getString("bttnCancel");
                    }
                };
                actionListener = new ActionListener(){

                    public void actionPerformed(ActionEvent actionEvent) {
                        int n = JAPDialog.showConfirmDialog((Component)jAPNewView, string4, string3, options, 1, (JAPDialog.ILinkedInformation)linkedInformationAdapter);
                        if (n == 0) {
                            AbstractOS.getInstance().openURL(uRL);
                        }
                    }
                };
            } else {
                actionListener = new ActionListener(){

                    public void actionPerformed(ActionEvent actionEvent) {
                        JAPDialog.showMessageDialog((Component)jAPNewView, string4, string3);
                    }
                };
            }
        }
        n = this.m_StatusPanel.addStatusMsg(string2, 1, false, actionListener);
        this.m_messagesShown.put(string2, new Integer(n));
    }

    public void messageRemoved(PayMessage payMessage) {
        String string = payMessage.getShortMessage();
        Integer n = (Integer)this.m_messagesShown.get(string);
        if (n == null) {
            LogHolder.log(7, LogType.PAY, "Tried to remove a message, but failed, since no id exists, message is: " + payMessage);
        } else {
            this.m_StatusPanel.removeStatusMsg(n);
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

    private final class MixMouseAdapter
    extends MouseAdapter {
        private MixCascade m_mixInfo;
        private int m_mixPosition;
        private JLabel m_registeredLabel;
        private LineBorder m_borderOriginal;
        private int m_totalMixes;

        public MixMouseAdapter(JLabel jLabel) {
            this.m_registeredLabel = jLabel;
        }

        public synchronized void mouseClicked(MouseEvent mouseEvent) {
            MixDetailsDialog mixDetailsDialog = new MixDetailsDialog(JAPNewView.this, this.m_mixInfo, this.m_mixPosition, this.m_totalMixes);
            mixDetailsDialog.pack();
            mixDetailsDialog.setVisible(true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void mouseEntered(MouseEvent mouseEvent) {
            JLabel jLabel = this.m_registeredLabel;
            synchronized (jLabel) {
                if (this.m_borderOriginal == null) {
                    Border border = this.m_registeredLabel.getBorder();
                    if (border != null && border instanceof LineBorder) {
                        this.m_borderOriginal = (LineBorder)border;
                        this.m_registeredLabel.setBorder(new LineBorder(this.m_borderOriginal.getLineColor().darker(), this.m_borderOriginal.getThickness()));
                    }
                } else {
                    this.m_borderOriginal = null;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void mouseExited(MouseEvent mouseEvent) {
            JLabel jLabel = this.m_registeredLabel;
            synchronized (jLabel) {
                if (this.m_borderOriginal != null) {
                    this.m_registeredLabel.setBorder(this.m_borderOriginal);
                    this.m_borderOriginal = null;
                }
            }
        }

        public synchronized void setMixInfo(MixCascade mixCascade, int n, int n2) {
            this.m_mixInfo = mixCascade;
            this.m_mixPosition = n;
            this.m_totalMixes = n2;
        }
    }

    private final class LawListener
    extends MouseAdapter {
        private MixCascade m_cascade;

        private LawListener() {
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            DataRetentionDialog.show(JAPNewView.this, this.m_cascade);
        }

        public void setCascadeInfo(MixCascade mixCascade) {
            this.m_cascade = mixCascade;
        }
    }

    private final class ComponentMovedAdapter
    extends ComponentAdapter {
        private boolean m_bMoved = false;

        private ComponentMovedAdapter() {
        }

        public void componentMoved(ComponentEvent componentEvent) {
            this.m_bMoved = true;
        }

        public boolean hasMoved() {
            return this.m_bMoved;
        }
    }
}

