/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.AnonServerDescription;
import anon.AnonServiceEventAdapter;
import anon.client.BasicTrustModel;
import anon.client.TrustModel;
import anon.crypto.AbstractX509AlternativeName;
import anon.crypto.CertPath;
import anon.crypto.JAPCertificate;
import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.error.ServiceSignatureException;
import anon.error.TrustException;
import anon.infoservice.BlacklistedCascadeIDEntry;
import anon.infoservice.DataRetentionInformation;
import anon.infoservice.Database;
import anon.infoservice.DatabaseMessage;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.infoservice.PerformanceEntry;
import anon.infoservice.PerformanceInfo;
import anon.infoservice.PreviouslyKnownCascadeIDEntry;
import anon.infoservice.ServiceLocation;
import anon.infoservice.ServiceOperator;
import anon.infoservice.ServiceSoftware;
import anon.infoservice.StatusInfo;
import anon.platform.AbstractOS;
import anon.util.CountryMapper;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.CertDetailsDialog;
import gui.DataRetentionDialog;
import gui.GUIUtils;
import gui.JAPJIntField;
import gui.MapBox;
import gui.MixDetailsDialog;
import gui.MultiCertOverview;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.IJAPConfSavePoint;
import jap.JAPController;
import jap.JAPControllerMessage;
import jap.JAPModel;
import jap.JAPNewView;
import jap.OperatorsCellRenderer;
import jap.ServerListPanel;
import jap.forward.JAPRoutingMessage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import logging.LogHolder;
import logging.LogType;

class JAPConfAnon
extends AbstractJAPConfModule
implements MouseListener,
ActionListener,
ListSelectionListener,
ItemListener,
KeyListener,
Observer {
    private static final String MSG_X_OF_Y_CERTS_TRUSTED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_certXofYtrusted";
    private static final String MSG_REALLY_DELETE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_reallyDelete";
    private static final String MSG_MIX_VERSION = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_mixVersion";
    private static final String MSG_MIX_ID = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_mixID";
    private static final String MSG_BUTTONEDITSHOW = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_buttoneditshow";
    private static final String MSG_PAYCASCADE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_paycascade";
    private static final String MSG_MIX_POSITION = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_mixPosition";
    private static final String MSG_OF_THE_SERVICE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_ofTheService";
    private static final String MSG_MIX_FIRST = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_mixFirst";
    private static final String MSG_MIX_SINGLE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_singleMix";
    private static final String MSG_MIX_MIDDLE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_mixMiddle";
    private static final String MSG_MIX_LAST = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_mixLast";
    private static final String MSG_SHOW_ON_MAP = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_showOnMap";
    private static final String MSG_EXPLAIN_MIX_TT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_explainMixTT";
    private static final String MSG_FIRST_MIX_TEXT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_firstMixText";
    private static final String MSG_SINGLE_MIX_TEXT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_singleMixText";
    private static final String MSG_MIDDLE_MIX_TEXT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_middleMixText";
    private static final String MSG_LAST_MIX_TEXT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_lastMixText";
    private static final String MSG_NOT_TRUSTWORTHY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_notTrustworthy";
    private static final String MSG_EXPLAIN_NOT_TRUSTWORTHY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_explainNotTrustworthy";
    private static final String MSG_EXPLAIN_NOT_ALLOWED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".explainNotAllowed";
    private static final String MSG_BTN_CONNECT_NEVERTHELESS = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".btnConnectNevertheless";
    private static final String MSG_EXPLAIN_NO_SELECTION_ANTI_CENSORSHIP = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".explainNotSelectionAntiCensorship";
    private static final String MSG_INVALID_CERTIFICATION = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".invalidCertification";
    private static final String MSG_EXPLAIN_BLACKLISTED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".explainBlacklisted";
    private static final String MSG_EXPLAIN_PI_UNAVAILABLE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_explainPiUnavailable";
    private static final String MSG_EXPLAIN_NO_CASCADES = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_explainNoCascades";
    private static final String MSG_EXPLAIN_CURRENT_CASCADE_NOT_TRUSTED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_explainCurrentCascadeNotTrusted";
    private static final String MSG_WHAT_IS_THIS = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_whatIsThis";
    public static final String MSG_FILTER = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_filter";
    private static final String MSG_FILTER_CANCEL = "cancelButton";
    private static final String MSG_EDIT_FILTER = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_editFilter";
    private static final String MSG_ANON_LEVEL = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_anonLevel";
    private static final String MSG_SUPPORTS_SOCKS = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_socksSupported";
    private static final String MSG_FILTER_PAYMENT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_payment";
    private static final String MSG_FILTER_CASCADES = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_cascades";
    private static final String MSG_FILTER_INTERNATIONALITY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_internationality";
    private static final String MSG_FILTER_OPERATORS = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_operators";
    private static final String MSG_FILTER_SPEED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_speed";
    private static final String MSG_FILTER_LATENCY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_latency";
    private static final String MSG_FILTER_ALL = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_all";
    private static final String MSG_FILTER_PAYMENT_ONLY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_paymentOnly";
    private static final String MSG_FILTER_PREMIUM_PRIVATE_ONLY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_premiumPrivateOnly";
    private static final String MSG_FILTER_BUSINESS_ONLY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_businessOnly";
    private static final String MSG_FILTER_NO_PAYMENT_ONLY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_noPaymentOnly";
    private static final String MSG_FILTER_PAYMENT_PREFERRED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".paymentPreferred";
    private static final String MSG_FILTER_AT_LEAST_3_MIXES = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_atLeast3Mixes";
    private static final String MSG_FILTER_AT_LEAST_2_MIXES = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_atLeast2Mixes";
    private static final String MSG_FILTER_AT_LEAST_2_COUNTRIES = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_atLeast2Countries";
    private static final String MSG_FILTER_AT_LEAST_3_COUNTRIES = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_atLeast3Countries";
    private static final String MSG_FILTER_AT_LEAST = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_atLeast";
    private static final String MSG_FILTER_AT_MOST = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_atMost";
    private static final String MSG_FILTER_SELECT_ALL_OPERATORS = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_selectAllOperators";
    private static final String MSG_FILTER_OTHER = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_filterOther";
    private static final String MSG_FILTER_SOCKS_ONLY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_filterSOCKSOnly";
    private static final String MSG_FILTER_NO_DATA_RETENTION = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_filterNoDataRetention";
    private static final String MSG_WARNING_USER_DEFINED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + ".warningUserDefined";
    private static final String MSG_CONNECTED = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_connected";
    private static final String MSG_LBL_AVAILABILITY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityLbl";
    private static final String MSG_USER_LIMIT = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityUserLimit";
    private static final String MSG_UNSTABLE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityUnstable";
    private static final String MSG_HARDLY_REACHABLE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityHardlyReachable";
    private static final String MSG_UNREACHABLE = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityUnreachable";
    private static final String MSG_BAD_AVAILABILITY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityBad";
    private static final String MSG_GOOD_AVAILABILITY = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = JAPConfAnon.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_availabilityGood";
    private static final int FILTER_SPEED_MAJOR_TICK = 100;
    private static final int FILTER_SPEED_MAX = 400;
    private static final int FILTER_SPEED_STEPS = 5;
    private static final int FILTER_LATENCY_STEPS = 5;
    private static final int FILTER_LATENCY_MAJOR_TICK = 1000;
    private static final int FILTER_LATENCY_MAX = 5000;
    private boolean m_bUpdateServerPanel = true;
    private JComboBox m_cmbCascadeFilter;
    private JList m_listMixCascade;
    private JTable m_tableMixCascade;
    private JTable m_listOperators;
    private ServerListPanel m_serverList;
    private JPanel pRoot;
    private JPanel m_cascadesPanel;
    private ServerPanel m_serverPanel;
    private JPanel m_serverInfoPanel;
    private ManualPanel m_manualPanel;
    private FilterPanel m_filterPanel;
    private JLabel m_lblSpeed;
    private JLabel m_lblDelay;
    private JLabel m_anonLevelLabel;
    private JLabel m_numOfUsersLabel;
    private JLabel m_lblVDS;
    private JLabel m_lblSocks;
    private GridBagLayout m_rootPanelLayout;
    private GridBagConstraints m_rootPanelConstraints;
    private JLabel m_lblMix;
    private JLabel m_lblMixOfService;
    private JPanel m_nrPanel;
    private JLabel m_nrLabel;
    private JPanel m_pnlMixInfoButtons;
    private JLabel m_operatorLabel;
    private JButton m_btnEmail;
    private JButton m_btnHomepage;
    private JButton m_btnMap;
    private JButton m_btnDataRetention;
    private JButton m_moveMixLeft;
    private JButton m_moveMixRight;
    private JLabel m_locationLabel;
    private JLabel m_lblAvailability;
    private boolean m_blacklist;
    private boolean m_bNetworkBlocked;
    private boolean m_unknownPI;
    private JButton m_btnViewCert;
    private JButton m_manualCascadeButton;
    private JButton m_reloadCascadesButton;
    private JButton m_selectCascadeButton;
    private JButton m_editCascadeButton;
    private JButton m_deleteCascadeButton;
    private JButton m_cancelCascadeButton;
    private JButton m_showEditPanelButton;
    private JButton m_showEditFilterButton;
    private JPopupMenu m_opPopupMenu;
    private JTextField m_manHostField;
    private JTextField m_manPortField;
    private JSlider m_filterSpeedSlider;
    private JSlider m_filterLatencySlider;
    private JRadioButton m_filterAllCountries;
    private JRadioButton m_filterAtLeast2Countries;
    private JRadioButton m_filterAtLeast3Countries;
    private JRadioButton m_filterAllMixes;
    private JRadioButton m_filterAtLeast2Mixes;
    private JRadioButton m_filterAtLeast3Mixes;
    private JTextField m_filterNameField;
    private JCheckBox m_cbxSocks5;
    private JCheckBox m_cbxDataRetention;
    private JCheckBox m_cbxFreeOfCharge;
    private ButtonGroup m_filterCascadeGroup;
    private ButtonGroup m_filterInternationalGroup;
    private boolean mb_backSpacePressed;
    private boolean mb_manualCascadeNew;
    private String m_oldCascadeHost;
    private String m_oldCascadePort;
    private boolean m_bMixInfoShown = false;
    private boolean m_mapShown = false;
    private MultiCertPath m_serverCertPaths;
    private MixInfo m_serverInfo;
    private MixCascade m_cascadeInfo;
    private Vector m_locationCoordinates;
    private TrustModel m_previousTrustModel;
    private TrustModel m_trustModelCopy;
    private static boolean m_bIsShowingServiceUntrustedBox = false;
    private static final Object SYNC_SERVICE_UNTRUSTED_BOX = new Object();
    static /* synthetic */ Class class$jap$JAPConfAnon;
    static /* synthetic */ Class class$anon$client$TrustModel$SocksAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$DataRetentionAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$NumberOfMixesAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$InternationalAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$SpeedAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$DelayAttribute;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$PreviouslyKnownCascadeIDEntry;
    static /* synthetic */ Class class$anon$client$TrustModel$OperatorBlacklistAttribute;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;
    static /* synthetic */ Class class$anon$infoservice$BlacklistedCascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;
    static /* synthetic */ Class class$anon$infoservice$MixInfo;
    static /* synthetic */ Class class$anon$infoservice$PerformanceInfo;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Object;

    protected JAPConfAnon(IJAPConfSavePoint iJAPConfSavePoint) {
        super(null);
        JAPController.getInstance().addEventListener(new LocalAnonServiceEventListener());
    }

    public void recreateRootPanel() {
        this.m_lblMix = new JLabel();
        this.m_lblMixOfService = new JLabel();
        this.m_lblMix.setForeground(Color.blue);
        this.m_lblMix.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (JAPConfAnon.this.m_bMixInfoShown || JAPConfAnon.this.m_lblMix.getText().trim().length() == 0) {
                    return;
                }
                JAPConfAnon.this.m_bMixInfoShown = true;
                if (JAPConfAnon.this.m_serverList.getSelectedIndex() == 0) {
                    if (JAPConfAnon.this.m_serverList.getNumberOfMixes() == 1) {
                        JAPDialog.showMessageDialog((Component)JAPConfAnon.this.getRootPanel(), JAPMessages.getString(MSG_SINGLE_MIX_TEXT), JAPMessages.getString(MSG_MIX_SINGLE));
                    } else {
                        JAPDialog.showMessageDialog((Component)JAPConfAnon.this.getRootPanel(), JAPMessages.getString(MSG_FIRST_MIX_TEXT), JAPMessages.getString(MSG_MIX_FIRST));
                    }
                } else if (JAPConfAnon.this.m_serverList.getSelectedIndex() == JAPConfAnon.this.m_serverList.getNumberOfMixes() - 1) {
                    JAPDialog.showMessageDialog((Component)JAPConfAnon.this.getRootPanel(), JAPMessages.getString(MSG_LAST_MIX_TEXT), JAPMessages.getString(MSG_MIX_LAST));
                } else {
                    JAPDialog.showMessageDialog((Component)JAPConfAnon.this.getRootPanel(), JAPMessages.getString(MSG_MIDDLE_MIX_TEXT), JAPMessages.getString(MSG_MIX_MIDDLE));
                }
                JAPConfAnon.this.m_bMixInfoShown = false;
            }
        });
        this.drawCompleteDialog();
    }

    private synchronized void drawServerPanel(int n, String string, boolean bl, int n2) {
        if (this.m_filterPanel != null && this.m_filterPanel.isVisible() || this.m_manualPanel != null && this.m_manualPanel.isVisible()) {
            this.hideEditFilter();
        }
        if (this.m_serverPanel == null) {
            this.m_serverPanel = new ServerPanel(this);
            this.m_rootPanelConstraints.gridx = 0;
            this.m_rootPanelConstraints.gridy = 2;
            this.m_rootPanelConstraints.weightx = 1.0;
            this.m_rootPanelConstraints.weighty = 0.0;
            this.m_rootPanelConstraints.anchor = 18;
            this.m_rootPanelConstraints.fill = 1;
            this.pRoot.add((Component)this.m_serverPanel, this.m_rootPanelConstraints);
        } else if (!this.m_serverPanel.isVisible()) {
            this.m_serverPanel.setVisible(true);
        }
        this.m_serverPanel.setCascadeName(string);
        this.m_serverPanel.updateServerList(n, bl, n2);
        this.m_serverPanel.validate();
        this.pRoot.validate();
    }

    private synchronized void drawServerInfoPanel() {
        if (this.m_manualPanel != null) {
            this.m_manualPanel.setVisible(false);
        }
        if (this.m_filterPanel != null) {
            this.hideEditFilter();
        }
        if (this.m_serverInfoPanel == null) {
            this.m_serverInfoPanel = new ServerInfoPanel(this);
            this.m_rootPanelConstraints.anchor = 18;
            this.m_rootPanelConstraints.gridx = 0;
            this.m_rootPanelConstraints.gridy = 3;
            this.m_rootPanelConstraints.weightx = 1.0;
            this.m_rootPanelConstraints.weighty = 0.0;
            this.m_rootPanelConstraints.fill = 1;
            this.pRoot.add((Component)this.m_serverInfoPanel, this.m_rootPanelConstraints);
        } else if (!this.m_serverInfoPanel.isVisible()) {
            this.m_serverInfoPanel.setVisible(true);
            this.m_serverInfoPanel.validate();
        }
    }

    private synchronized void drawManualPanel(String string, String string2) {
        if (this.m_serverPanel != null) {
            this.m_serverPanel.setVisible(false);
            this.m_serverPanel.validate();
            this.m_serverInfoPanel.setVisible(false);
            this.m_serverInfoPanel.validate();
        }
        if (this.m_filterPanel != null) {
            this.m_filterPanel.setVisible(false);
        }
        if (this.m_manualPanel == null) {
            this.m_manualPanel = new ManualPanel(this);
            this.m_rootPanelConstraints.gridx = 0;
            this.m_rootPanelConstraints.gridy = 2;
            this.m_rootPanelConstraints.weightx = 0.0;
            this.m_rootPanelConstraints.weighty = 1.0;
            this.m_rootPanelConstraints.anchor = 18;
            this.m_rootPanelConstraints.fill = 1;
            this.pRoot.add((Component)this.m_manualPanel, this.m_rootPanelConstraints);
        }
        this.m_manualPanel.setHostName(string);
        this.m_manualPanel.setPort(string2);
        this.m_manualPanel.setVisible(true);
        this.m_manualPanel.validate();
        this.pRoot.validate();
    }

    private synchronized void drawFilterPanel() {
        if (this.m_serverPanel != null) {
            this.m_serverPanel.setVisible(false);
            this.m_serverInfoPanel.setVisible(false);
            this.m_serverPanel.validate();
            this.m_serverInfoPanel.validate();
        }
        if (this.m_manualPanel != null && this.m_manualPanel.isVisible()) {
            this.m_manualPanel.setVisible(false);
        }
        this.m_trustModelCopy = new TrustModel(TrustModel.getCurrentTrustModel());
        if (this.m_filterPanel == null) {
            this.m_filterPanel = new FilterPanel(this);
            this.m_rootPanelConstraints.anchor = 14;
            this.m_rootPanelConstraints.gridx = 0;
            this.m_rootPanelConstraints.gridy = 3;
            this.m_rootPanelConstraints.weightx = 1.0;
            this.m_rootPanelConstraints.weighty = 0.5;
            this.m_rootPanelConstraints.fill = 1;
            this.pRoot.add((Component)this.m_filterPanel, this.m_rootPanelConstraints);
        }
        if (this.m_trustModelCopy != null) {
            boolean bl;
            this.m_filterNameField.setText(this.m_trustModelCopy.getName());
            boolean bl2 = bl = this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$SocksAttribute == null ? (class$anon$client$TrustModel$SocksAttribute = JAPConfAnon.class$("anon.client.TrustModel$SocksAttribute")) : class$anon$client$TrustModel$SocksAttribute).getTrustCondition() == 2;
            if (bl != this.m_cbxSocks5.isSelected()) {
                this.m_cbxSocks5.setSelected(bl);
            }
            boolean bl3 = bl = this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$DataRetentionAttribute == null ? (class$anon$client$TrustModel$DataRetentionAttribute = JAPConfAnon.class$("anon.client.TrustModel$DataRetentionAttribute")) : class$anon$client$TrustModel$DataRetentionAttribute).getTrustCondition() == 1;
            if (bl != this.m_cbxDataRetention.isSelected()) {
                this.m_cbxDataRetention.setSelected(bl);
            }
            boolean bl4 = bl = this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute == null ? (class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute = JAPConfAnon.class$("anon.client.TrustModel$ForcePremiumIfChargedAccountAttribute")) : class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute).getTrustCondition() == 2;
            if (bl != this.m_cbxFreeOfCharge.isSelected()) {
                this.m_cbxFreeOfCharge.setSelected(bl);
            }
            int n = this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$NumberOfMixesAttribute == null ? (class$anon$client$TrustModel$NumberOfMixesAttribute = JAPConfAnon.class$("anon.client.TrustModel$NumberOfMixesAttribute")) : class$anon$client$TrustModel$NumberOfMixesAttribute).getTrustCondition();
            Integer n2 = (Integer)this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$NumberOfMixesAttribute == null ? (class$anon$client$TrustModel$NumberOfMixesAttribute = JAPConfAnon.class$("anon.client.TrustModel$NumberOfMixesAttribute")) : class$anon$client$TrustModel$NumberOfMixesAttribute).getConditionValue();
            this.m_filterPanel.selectRadioButton(this.m_filterCascadeGroup, String.valueOf(n));
            if (n2 != null) {
                if (n == 3 && n2 == 0) {
                    this.m_filterCascadeGroup.setSelected(this.m_filterAllMixes.getModel(), true);
                } else if (n == 3 && n2 == 2) {
                    this.m_filterCascadeGroup.setSelected(this.m_filterAtLeast2Mixes.getModel(), true);
                } else if (n == 3 && n2 == 3) {
                    this.m_filterCascadeGroup.setSelected(this.m_filterAtLeast3Mixes.getModel(), true);
                }
            }
            n = this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$InternationalAttribute == null ? (class$anon$client$TrustModel$InternationalAttribute = JAPConfAnon.class$("anon.client.TrustModel$InternationalAttribute")) : class$anon$client$TrustModel$InternationalAttribute).getTrustCondition();
            n2 = (Integer)this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$InternationalAttribute == null ? (class$anon$client$TrustModel$InternationalAttribute = JAPConfAnon.class$("anon.client.TrustModel$InternationalAttribute")) : class$anon$client$TrustModel$InternationalAttribute).getConditionValue();
            this.m_filterPanel.selectRadioButton(this.m_filterInternationalGroup, String.valueOf(n));
            if (n2 != null) {
                if (n == 3 && n2 == 0) {
                    this.m_filterInternationalGroup.setSelected(this.m_filterAllCountries.getModel(), true);
                } else if (n == 3 && n2 == 2) {
                    this.m_filterAllMixes.setEnabled(false);
                    this.m_filterInternationalGroup.setSelected(this.m_filterAtLeast2Countries.getModel(), true);
                } else if (n == 3 && n2 == 3) {
                    this.m_filterAtLeast2Mixes.setEnabled(false);
                    this.m_filterInternationalGroup.setSelected(this.m_filterAtLeast3Countries.getModel(), true);
                }
            }
            this.m_filterSpeedSlider.setValue((Integer)this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$SpeedAttribute == null ? (class$anon$client$TrustModel$SpeedAttribute = JAPConfAnon.class$("anon.client.TrustModel$SpeedAttribute")) : class$anon$client$TrustModel$SpeedAttribute).getConditionValue() / 100);
            int n3 = (Integer)this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$DelayAttribute == null ? (class$anon$client$TrustModel$DelayAttribute = JAPConfAnon.class$("anon.client.TrustModel$DelayAttribute")) : class$anon$client$TrustModel$DelayAttribute).getConditionValue();
            this.m_filterLatencySlider.setValue(this.convertDelayValue(n3, false));
            ((OperatorsTableModel)this.m_listOperators.getModel()).update();
        }
        this.m_filterPanel.setVisible(true);
        this.m_filterPanel.validate();
        this.pRoot.validate();
    }

    private synchronized void drawCascadesPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        if (this.m_cascadesPanel != null) {
            this.m_cascadesPanel.removeAll();
        } else {
            this.m_cascadesPanel = new JPanel();
        }
        this.m_cascadesPanel.setLayout(gridBagLayout);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = 12;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weighty = 0.2;
        this.m_cmbCascadeFilter = new JComboBox(TrustModel.getTrustModels());
        final JLabel jLabel = new JLabel();
        jLabel.setOpaque(true);
        this.m_cmbCascadeFilter.setRenderer(new ListCellRenderer(){

            public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
                if (bl) {
                    jLabel.setBackground(jList.getSelectionBackground());
                    jLabel.setForeground(jList.getSelectionForeground());
                } else {
                    jLabel.setBackground(jList.getBackground());
                    jLabel.setForeground(jList.getForeground());
                }
                if (TrustModel.getCurrentTrustModel() == (TrustModel)object) {
                    jLabel.setFont(new Font(jList.getFont().getName(), 1, jList.getFont().getSize()));
                } else {
                    jLabel.setFont(new Font(jList.getFont().getName(), 0, jList.getFont().getSize()));
                }
                if (object == null) {
                    jLabel.setText("");
                } else {
                    jLabel.setText(object.toString());
                }
                return jLabel;
            }
        });
        this.m_cmbCascadeFilter.setSelectedItem(TrustModel.getCurrentTrustModel());
        this.m_cmbCascadeFilter.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (TrustModel.getCurrentTrustModel() == null || !TrustModel.getCurrentTrustModel().equals(JAPConfAnon.this.m_cmbCascadeFilter.getSelectedItem())) {
                    TrustModel.setCurrentTrustModel((TrustModel)JAPConfAnon.this.m_cmbCascadeFilter.getSelectedItem());
                    JAPConfAnon.this.updateValues(false);
                    if (JAPConfAnon.this.m_filterPanel != null && JAPConfAnon.this.m_filterPanel.isVisible()) {
                        JAPConfAnon.this.hideEditFilter();
                    }
                }
            }
        });
        this.m_listMixCascade = new JList();
        this.m_tableMixCascade = new JTable();
        this.m_tableMixCascade.setModel(new MixCascadeTableModel());
        this.m_tableMixCascade.setTableHeader(null);
        this.m_tableMixCascade.setIntercellSpacing(new Dimension(0, 0));
        this.m_tableMixCascade.setShowGrid(false);
        this.m_tableMixCascade.setSelectionMode(0);
        this.m_tableMixCascade.getColumnModel().getColumn(0).setMaxWidth(1);
        this.m_tableMixCascade.getColumnModel().getColumn(0).setPreferredWidth(1);
        this.m_tableMixCascade.getColumnModel().getColumn(1).setCellRenderer(new MixCascadeCellRenderer());
        this.m_tableMixCascade.addMouseListener(this);
        this.m_tableMixCascade.getSelectionModel().addListSelectionListener(this);
        this.m_listMixCascade.setFixedCellWidth(30);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        JScrollPane jScrollPane = new JScrollPane(this.m_listMixCascade);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        Dimension dimension = jScrollPane.getPreferredSize();
        jScrollPane = new JScrollPane(this.m_tableMixCascade);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.setPreferredSize(dimension);
        this.m_cascadesPanel.add((Component)jScrollPane, gridBagConstraints);
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 3;
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.gridheight = 1;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.insets = new Insets(0, 0, 0, 10);
        this.m_reloadCascadesButton = new JButton(JAPMessages.getString("reloadCascades"));
        this.m_reloadCascadesButton.setIcon(GUIUtils.loadImageIcon("reload.gif", true, false));
        this.m_reloadCascadesButton.setDisabledIcon(GUIUtils.loadImageIcon("reloaddisabled_anim.gif", true, false));
        this.m_reloadCascadesButton.setPressedIcon(GUIUtils.loadImageIcon("reloadrollover.gif", true, false));
        this.m_reloadCascadesButton.addActionListener(this);
        jPanel.add((Component)this.m_reloadCascadesButton, gridBagConstraints2);
        this.m_selectCascadeButton = new JButton(JAPMessages.getString("selectCascade"));
        this.m_showEditFilterButton = new JButton(JAPMessages.getString(MSG_EDIT_FILTER));
        this.m_showEditFilterButton.addActionListener(this);
        gridBagConstraints2.gridx = 1;
        jPanel.add((Component)this.m_showEditFilterButton, gridBagConstraints2);
        this.m_manualCascadeButton = new JButton(JAPMessages.getString("manualCascade"));
        this.m_manualCascadeButton.addActionListener(this);
        gridBagConstraints2.gridx = 2;
        jPanel.add((Component)this.m_manualCascadeButton, gridBagConstraints2);
        this.m_showEditPanelButton = new JButton(JAPMessages.getString(MSG_BUTTONEDITSHOW));
        this.m_showEditPanelButton.addActionListener(this);
        gridBagConstraints2.gridx = 3;
        jPanel.add((Component)this.m_showEditPanelButton, gridBagConstraints2);
        this.m_deleteCascadeButton = new JButton(JAPMessages.getString("manualServiceDelete"));
        this.m_deleteCascadeButton.addActionListener(this);
        gridBagConstraints2.gridx = 4;
        gridBagConstraints2.weightx = 1.0;
        jPanel.add((Component)this.m_deleteCascadeButton, gridBagConstraints2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        this.m_cascadesPanel.add((Component)jPanel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 20, 0, 5);
        JLabel jLabel2 = new JLabel(JAPMessages.getString(MSG_ANON_LEVEL) + ":");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = 0;
        this.m_cascadesPanel.add((Component)jLabel2, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        this.m_anonLevelLabel = new JLabel("");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)this.m_anonLevelLabel, gridBagConstraints);
        this.m_lblVDS = new JLabel();
        this.m_lblVDS.setIcon(GUIUtils.loadImageIcon("certs/invalid.png", true));
        this.m_lblVDS.setToolTipText(JAPMessages.getString(DataRetentionDialog.MSG_DATA_RETENTION_EXPLAIN_SHORT));
        this.m_lblVDS.setForeground(Color.red);
        this.m_lblVDS.setCursor(Cursor.getPredefinedCursor(12));
        this.m_lblVDS.addMouseListener(this);
        ++gridBagConstraints.gridx;
        this.m_cascadesPanel.add((Component)this.m_lblVDS, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 20, 0, 5);
        jLabel2 = new JLabel(JAPMessages.getString("numOfUsersOnCascade") + ":");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = 0;
        this.m_cascadesPanel.add((Component)jLabel2, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        this.m_numOfUsersLabel = new JLabel("");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)this.m_numOfUsersLabel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 20, 0, 5);
        jLabel2 = new JLabel(JAPMessages.getString(MSG_FILTER_SPEED) + ":");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)jLabel2, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        this.m_lblSpeed = new JLabel("");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)this.m_lblSpeed, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 20, 0, 5);
        jLabel2 = new JLabel(JAPMessages.getString(MSG_FILTER_LATENCY) + ":");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)jLabel2, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        this.m_lblDelay = new JLabel("");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)this.m_lblDelay, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 20, 0, 5);
        jLabel2 = new JLabel(JAPMessages.getString(MSG_LBL_AVAILABILITY) + ":");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = 2;
        this.m_cascadesPanel.add((Component)jLabel2, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        this.m_lblAvailability = new JLabel("");
        this.m_lblAvailability.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (JAPConfAnon.this.m_lblAvailability.getCursor() != Cursor.getDefaultCursor() && JAPConfAnon.this.m_lblAvailability.getForeground() == Color.red) {
                    TrustModel trustModel = TrustModel.getCurrentTrustModel();
                    JAPConfAnon.showServiceUntrustedBox(JAPConfAnon.this.m_cascadeInfo, JAPConfAnon.this.m_lblAvailability, trustModel);
                }
            }
        });
        this.m_cascadesPanel.add((Component)this.m_lblAvailability, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 20, 0, 5);
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 4;
        this.m_lblSocks = new JLabel(" ");
        this.m_cascadesPanel.add((Component)this.m_lblSocks, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridy = 7;
        this.m_cascadesPanel.add((Component)new JLabel("                                               "), gridBagConstraints);
        this.m_rootPanelConstraints.gridx = 0;
        this.m_rootPanelConstraints.gridy = 0;
        this.m_rootPanelConstraints.insets = new Insets(10, 10, 0, 10);
        this.m_rootPanelConstraints.anchor = 18;
        this.m_rootPanelConstraints.fill = 1;
        this.m_rootPanelConstraints.weightx = 1.0;
        this.m_rootPanelConstraints.weighty = 1.0;
        this.pRoot.add((Component)this.m_cascadesPanel, this.m_rootPanelConstraints);
        this.m_rootPanelConstraints.weightx = 1.0;
        this.m_rootPanelConstraints.weighty = 0.0;
        JSeparator jSeparator = new JSeparator();
        this.m_rootPanelConstraints.gridy = 1;
        this.m_rootPanelConstraints.fill = 2;
        this.pRoot.add((Component)jSeparator, this.m_rootPanelConstraints);
    }

    private synchronized void drawCompleteDialog() {
        this.m_rootPanelLayout = new GridBagLayout();
        this.m_rootPanelConstraints = new GridBagConstraints();
        this.m_cascadesPanel = null;
        this.m_serverPanel = null;
        this.m_serverInfoPanel = null;
        this.m_manualPanel = null;
        this.pRoot = this.getRootPanel();
        this.pRoot.removeAll();
        this.pRoot.setLayout(this.m_rootPanelLayout);
        if (JAPModel.getDefaultView() == 2) {
            this.pRoot.setBorder(new TitledBorder(JAPMessages.getString("confAnonTab")));
        }
        this.m_rootPanelConstraints.anchor = 18;
        this.drawManualPanel("", "");
        this.drawCascadesPanel();
        this.drawFilterPanel();
        this.drawServerPanel(3, "", false, 0);
        this.drawServerInfoPanel();
        this.hideEditFilter();
    }

    private void setAvailabilityLabel(MixCascade mixCascade, PerformanceEntry performanceEntry) {
        StringBuffer stringBuffer = new StringBuffer();
        PerformanceEntry.StabilityAttributes stabilityAttributes = performanceEntry.getStabilityAttributes();
        MixCascade mixCascade2 = JAPController.getInstance().getConnectedCascade();
        TrustModel trustModel = TrustModel.getCurrentTrustModel();
        if (mixCascade2 != null && mixCascade2.equals(mixCascade)) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(this.m_anonLevelLabel.getForeground());
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_CONNECTED));
            this.m_lblAvailability.setToolTipText(null);
        } else if (!trustModel.isTrusted(mixCascade, stringBuffer)) {
            this.m_lblAvailability.setForeground(Color.red);
            this.m_lblAvailability.setCursor(Cursor.getPredefinedCursor(12));
            this.m_lblAvailability.setText(stringBuffer.toString());
            if (TrustModel.isBlacklisted(mixCascade)) {
                this.m_lblAvailability.setToolTipText(JAPMessages.getString(MSG_EXPLAIN_BLACKLISTED, TrustModel.getCurrentTrustModel().getName()));
                this.m_blacklist = true;
                this.m_unknownPI = false;
                this.m_bNetworkBlocked = false;
            } else if (TrustModel.isNoPaymentInstanceFound(mixCascade)) {
                this.m_lblAvailability.setToolTipText(JAPMessages.getString(MSG_EXPLAIN_PI_UNAVAILABLE, TrustModel.getCurrentTrustModel().getName()));
                this.m_blacklist = false;
                this.m_unknownPI = true;
                this.m_bNetworkBlocked = false;
            } else if (TrustModel.areListenerInterfacesBlocked(mixCascade)) {
                this.m_lblAvailability.setToolTipText(JAPMessages.getString(JAPController.MSG_CASCADE_UNREACHABLE, mixCascade));
                this.m_blacklist = false;
                this.m_unknownPI = false;
                this.m_bNetworkBlocked = true;
            }
        } else if (stabilityAttributes.getBoundUnknown() + stabilityAttributes.getBoundErrors() > 75) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(Color.red);
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_UNREACHABLE));
            this.m_lblAvailability.setToolTipText(null);
        } else if (InfoServiceTempLayer.isUserLimitReached(mixCascade)) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(Color.red);
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_USER_LIMIT));
            this.m_lblAvailability.setToolTipText(null);
        } else if (stabilityAttributes.getBoundUnknown() + stabilityAttributes.getBoundErrors() > 25) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(Color.red);
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_HARDLY_REACHABLE));
            this.m_lblAvailability.setToolTipText(null);
        } else if (stabilityAttributes.getBoundResets() > 5 || stabilityAttributes.getBoundErrors() > 10) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(Color.red);
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_UNSTABLE));
            this.m_lblAvailability.setToolTipText(null);
        } else if (stabilityAttributes.getBoundUnknown() + stabilityAttributes.getBoundErrors() > 5) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(this.m_anonLevelLabel.getForeground());
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_BAD_AVAILABILITY));
            this.m_lblAvailability.setToolTipText(null);
        } else if (stabilityAttributes.getValueSize() == 0) {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(this.m_anonLevelLabel.getForeground());
            this.m_lblAvailability.setToolTipText(null);
            this.m_lblAvailability.setText(JAPMessages.getString(JAPNewView.MSG_UNKNOWN_PERFORMANCE));
        } else {
            this.m_lblAvailability.setCursor(Cursor.getDefaultCursor());
            this.m_lblAvailability.setForeground(this.m_anonLevelLabel.getForeground());
            this.m_lblAvailability.setToolTipText(null);
            this.m_lblAvailability.setText(JAPMessages.getString(MSG_GOOD_AVAILABILITY));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void showServiceUntrustedBox(MixCascade mixCascade, Component component, TrustModel trustModel) {
        Object object;
        Object object2 = SYNC_SERVICE_UNTRUSTED_BOX;
        synchronized (object2) {
            if (m_bIsShowingServiceUntrustedBox) {
                return;
            }
            m_bIsShowingServiceUntrustedBox = true;
        }
        object2 = null;
        try {
            trustModel.checkTrust(mixCascade);
        }
        catch (TrustException trustException) {
        }
        catch (ServiceSignatureException serviceSignatureException) {
            object2 = serviceSignatureException.getMessage();
        }
        if (object2 != null) {
            JAPDialog.showWarningDialog(component, JAPMessages.getString(MSG_INVALID_CERTIFICATION, "\"" + mixCascade.getName() + "\""));
            object = SYNC_SERVICE_UNTRUSTED_BOX;
            synchronized (object) {
                m_bIsShowingServiceUntrustedBox = false;
            }
            return;
        }
        if (TrustModel.isBlacklisted(mixCascade)) {
            JAPDialog.showMessageDialog(component, JAPMessages.getString(MSG_EXPLAIN_BLACKLISTED, "\"" + mixCascade.getName() + "\""));
        } else if (TrustModel.isNoPaymentInstanceFound(mixCascade)) {
            JAPDialog.showMessageDialog(component, JAPMessages.getString(MSG_EXPLAIN_PI_UNAVAILABLE, "\"" + mixCascade.getName() + "\""));
        } else {
            object = trustModel.readUntrustedAttributeText(mixCascade);
            if (((Vector)object).size() == 0 && TrustModel.areListenerInterfacesBlocked(mixCascade)) {
                JAPDialog.showMessageDialog(component, JAPMessages.getString(JAPController.MSG_CASCADE_UNREACHABLE, "\"" + mixCascade.getName() + "\""));
            } else {
                int n;
                String string = "<b><font color=\"red\"><ul>";
                for (n = 0; n < ((Vector)object).size(); ++n) {
                    string = string + "<li>" + ((Vector)object).elementAt(n) + "</li>";
                }
                string = string + "</ul></font></b>";
                n = JAPDialog.showConfirmDialog(component, JAPMessages.getString(MSG_EXPLAIN_NOT_ALLOWED, mixCascade.getName()) + string, new JAPDialog.Options(1){

                    public String getYesOKText() {
                        return JAPMessages.getString(MSG_BTN_CONNECT_NEVERTHELESS);
                    }

                    public String getNoText() {
                        return JAPMessages.getString(MSG_EDIT_FILTER);
                    }
                }, 2, null);
                if (n == 0) {
                    if (!mixCascade.equals(JAPController.getInstance().getConnectedCascade())) {
                        TrustModel.allowAttributeWhitelist(mixCascade);
                    }
                    JAPController.getInstance().setCurrentMixCascade(mixCascade);
                    JAPController.getInstance().start();
                } else if (n == 1) {
                    JAPController.getInstance().showConfigDialog("ANON_TAB", Boolean.TRUE);
                }
            }
        }
        object = SYNC_SERVICE_UNTRUSTED_BOX;
        synchronized (object) {
            m_bIsShowingServiceUntrustedBox = false;
        }
    }

    public synchronized void itemStateChanged(ItemEvent itemEvent) {
        Object object;
        MixCascade mixCascade;
        if (this.m_serverList == null || this.m_serverPanel == null || this.m_serverInfoPanel == null) {
            return;
        }
        int n = this.m_serverList.getSelectedIndex();
        this.m_cascadeInfo = mixCascade = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
        String string = null;
        if (mixCascade != null) {
            if (this.m_serverList.getNumberOfMixes() == 1) {
                n = mixCascade.getNumberOfMixes() - 1;
            }
            string = mixCascade.getMixId(n);
        }
        if (string != null) {
            String string2 = GUIUtils.trim(InfoServiceTempLayer.getMixVersion(mixCascade, string));
            string2 = string2 != null ? ", " + JAPMessages.getString(MSG_MIX_VERSION) + "=" + string2 : "";
            this.m_nrLabel.setToolTipText(JAPMessages.getString(MSG_MIX_ID) + "=" + string + string2);
            object = GUIUtils.trim(InfoServiceTempLayer.getName(mixCascade, string), 80);
            if (object == null) {
                this.m_nrLabel.setText("N/A");
            } else {
                this.m_nrLabel.setText((String)object);
            }
        } else {
            this.m_nrLabel.setToolTipText("");
        }
        if (this.m_serverList.areMixButtonsEnabled()) {
            this.m_lblMix.setText(JAPMessages.getString(MixDetailsDialog.MSG_MIX_X_OF_Y, new Object[]{new Integer(Math.min(n, this.m_serverList.getNumberOfMixes() - 1) + 1), new Integer(this.m_serverList.getNumberOfMixes())}));
            if (mixCascade != null) {
                this.m_lblMixOfService.setText(JAPMessages.getString(MSG_OF_THE_SERVICE, "\"" + mixCascade.getName() + "\""));
            } else {
                this.m_lblMixOfService.setText("");
            }
            this.m_moveMixLeft.setVisible(true);
            this.m_moveMixRight.setVisible(true);
            this.m_lblMix.setCursor(Cursor.getPredefinedCursor(12));
            this.m_lblMix.setToolTipText(JAPMessages.getString(MSG_EXPLAIN_MIX_TT));
        } else {
            this.m_lblMix.setCursor(Cursor.getPredefinedCursor(0));
            this.m_lblMix.setToolTipText(null);
            this.m_lblMix.setText(" ");
            this.m_lblMixOfService.setText("");
            this.m_moveMixLeft.setVisible(false);
            this.m_moveMixRight.setVisible(false);
        }
        if (mixCascade != null) {
            for (int i = 0; i < this.m_serverList.getNumberOfMixes() && i < mixCascade.getNumberOfMixes(); ++i) {
                object = mixCascade.getMixId(i);
                object = this.m_serverList.getNumberOfMixes() == 1 ? mixCascade.getMixId(mixCascade.getNumberOfMixes() - 1) : mixCascade.getMixId(i);
                ServiceLocation serviceLocation = InfoServiceTempLayer.getServiceLocation(mixCascade, (String)object);
                ServiceOperator serviceOperator = InfoServiceTempLayer.getServiceOperator(mixCascade, (String)object);
                this.m_serverList.update(i, serviceOperator, serviceLocation);
            }
        }
        this.m_operatorLabel.setText(GUIUtils.trim(InfoServiceTempLayer.getOperator(mixCascade, string)));
        this.m_operatorLabel.setToolTipText(this.m_operatorLabel.getText());
        ServiceOperator serviceOperator = InfoServiceTempLayer.getServiceOperator(mixCascade, string);
        if (serviceOperator != null && serviceOperator.getCountryCode() != null) {
            this.m_operatorLabel.setIcon(GUIUtils.loadImageIcon("flags/" + serviceOperator.getCountryCode() + ".png"));
        } else {
            this.m_operatorLabel.setIcon(null);
        }
        this.m_btnHomepage.setToolTipText(InfoServiceTempLayer.getUrl(mixCascade, string));
        if (JAPConfAnon.getUrlFromLabel(this.m_btnHomepage) != null) {
            this.m_btnHomepage.setEnabled(true);
        } else {
            this.m_btnHomepage.setEnabled(false);
        }
        this.m_btnEmail.setToolTipText(GUIUtils.trim(InfoServiceTempLayer.getEMail(mixCascade, string)));
        if (JAPConfAnon.getEMailFromLabel(this.m_btnEmail) != null) {
            this.m_btnEmail.setEnabled(true);
        } else {
            this.m_btnEmail.setEnabled(false);
        }
        this.m_locationCoordinates = InfoServiceTempLayer.getCoordinates(mixCascade, string);
        this.m_locationLabel.setText(GUIUtils.trim(InfoServiceTempLayer.getLocation(mixCascade, string)));
        this.m_btnMap.setToolTipText(GUIUtils.trim(InfoServiceTempLayer.getLocation(mixCascade, string)));
        if (this.m_locationCoordinates != null) {
            this.m_btnMap.setEnabled(true);
        } else {
            this.m_btnMap.setEnabled(false);
        }
        object = InfoServiceTempLayer.getServiceLocation(mixCascade, string);
        if (object != null) {
            this.m_locationLabel.setIcon(GUIUtils.loadImageIcon("flags/" + ((ServiceLocation)object).getCountryCode() + ".png"));
        } else {
            this.m_locationLabel.setIcon(null);
        }
        this.m_locationLabel.setToolTipText(InfoServiceTempLayer.getLocation(mixCascade, string));
        this.m_serverInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
        this.m_serverCertPaths = InfoServiceTempLayer.getMixCertPath(mixCascade, string);
        if (this.m_serverCertPaths != null && this.m_serverInfo != null) {
            boolean bl = this.isServerCertVerified();
            boolean bl2 = this.m_serverCertPaths.isValid(new Date());
            if (!bl) {
                this.m_btnViewCert.setIcon(GUIUtils.loadImageIcon("certs/not_trusted.png"));
                this.m_btnViewCert.setForeground(Color.red);
                this.m_btnViewCert.setToolTipText(JAPMessages.getString(MixDetailsDialog.MSG_NOT_VERIFIED));
            } else if (!bl2) {
                this.m_btnViewCert.setIcon(GUIUtils.loadImageIcon("certs/invalid.png"));
                this.m_btnViewCert.setForeground(this.m_btnEmail.getForeground());
                this.m_btnViewCert.setToolTipText(JAPMessages.getString(MixDetailsDialog.MSG_INVALID));
            } else {
                this.m_btnViewCert.setForeground(this.m_btnEmail.getForeground());
                if (this.m_serverCertPaths.countVerifiedAndValidPaths() > 2) {
                    this.m_btnViewCert.setIcon(GUIUtils.loadImageIcon("certs/trusted_green.png"));
                    this.m_btnViewCert.setToolTipText(JAPMessages.getString(MixDetailsDialog.MSG_INDEPENDENT_CERTIFICATIONS, "" + this.m_serverCertPaths.countVerifiedAndValidPaths()));
                } else if (this.m_serverCertPaths.countVerifiedAndValidPaths() > 1) {
                    this.m_btnViewCert.setIcon(GUIUtils.loadImageIcon("certs/trusted_blue.png"));
                    this.m_btnViewCert.setToolTipText(JAPMessages.getString(MixDetailsDialog.MSG_INDEPENDENT_CERTIFICATIONS, "" + this.m_serverCertPaths.countVerifiedAndValidPaths()));
                } else {
                    this.m_btnViewCert.setToolTipText(JAPMessages.getString(MixDetailsDialog.MSG_VALID));
                    this.m_btnViewCert.setIcon(GUIUtils.loadImageIcon("certs/trusted_black.png"));
                }
            }
            this.m_btnViewCert.setEnabled(true);
        } else {
            this.m_btnViewCert.setToolTipText("N/A");
            this.m_btnViewCert.setIcon(null);
            this.m_btnViewCert.setEnabled(false);
        }
        DataRetentionInformation dataRetentionInformation = null;
        if (this.m_serverInfo != null) {
            dataRetentionInformation = this.m_serverInfo.getDataRetentionInformation();
        }
        if (dataRetentionInformation == null) {
            this.m_btnDataRetention.setVisible(false);
            this.m_btnDataRetention.setToolTipText(null);
        } else {
            this.m_btnDataRetention.setVisible(true);
            this.m_btnDataRetention.setToolTipText(JAPMessages.getString(DataRetentionDialog.MSG_DATA_RETENTION_MIX_EXPLAIN_SHORT));
        }
        this.pRoot.validate();
    }

    public String getTabTitle() {
        return JAPMessages.getString("confAnonTab");
    }

    public void onResetToDefaultsPressed() {
        this.hideEditFilter();
        this.m_filterAllMixes.setEnabled(true);
        this.m_filterAtLeast2Mixes.setEnabled(true);
        TrustModel.restoreDefault();
    }

    protected void onCancelPressed() {
        if (this.m_filterPanel != null && this.m_filterPanel.isVisible() && this.m_previousTrustModel != TrustModel.getCustomFilter()) {
            this.m_cmbCascadeFilter.setSelectedItem(this.m_previousTrustModel);
        }
        this.hideEditFilter();
    }

    public boolean onOkPressed() {
        if (this.m_filterPanel != null && this.m_filterPanel.isVisible()) {
            this.applyFilter();
        }
        this.hideEditFilter();
        return true;
    }

    protected void onUpdateValues() {
        ((MixCascadeTableModel)this.m_tableMixCascade.getModel()).update();
    }

    private void fetchCascades(final boolean bl, boolean bl2) {
        this.m_reloadCascadesButton.setEnabled(false);
        Runnable runnable = new Runnable(){

            public void run() {
                if (JAPController.getInstance().fetchMixCascades(bl, false)) {
                    JAPController.getInstance().updatePerformanceInfo(bl);
                }
                JAPConfAnon.this.updateValues(false);
                LogHolder.log(7, LogType.GUI, "Enabling reload button");
                JAPConfAnon.this.m_reloadCascadesButton.setEnabled(true);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof JMenuItem && actionEvent.getActionCommand() != null && actionEvent.getActionCommand().equals(MSG_FILTER_SELECT_ALL_OPERATORS)) {
            ((OperatorsTableModel)this.m_listOperators.getModel()).reset();
            this.m_listOperators.updateUI();
        }
        if (actionEvent.getSource() == this.m_cancelCascadeButton) {
            if (this.mb_manualCascadeNew) {
                this.m_manualPanel.setVisible(false);
                this.m_serverPanel.setVisible(true);
                this.m_serverInfoPanel.setVisible(true);
                this.updateValues(false);
            } else {
                this.m_manHostField.setText(this.m_oldCascadeHost);
                this.m_manPortField.setText(this.m_oldCascadePort);
                this.m_cancelCascadeButton.setEnabled(false);
            }
        } else if (actionEvent.getSource() == this.m_reloadCascadesButton) {
            this.fetchCascades(true, false);
        } else if (actionEvent.getSource() == this.m_selectCascadeButton) {
            MixCascade mixCascade = null;
            try {
                mixCascade = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
            }
            catch (Exception exception) {
                mixCascade = null;
            }
            if (mixCascade != null) {
                TrustModel trustModel = TrustModel.getCurrentTrustModel();
                if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                    JAPDialog.showMessageDialog(this.m_selectCascadeButton, JAPMessages.getString(MSG_EXPLAIN_NO_SELECTION_ANTI_CENSORSHIP));
                } else if (!trustModel.isTrusted(mixCascade) && JAPController.getInstance().getAnonMode()) {
                    JAPConfAnon.showServiceUntrustedBox(mixCascade, this.m_selectCascadeButton, trustModel);
                } else {
                    JAPController.getInstance().setCurrentMixCascade(mixCascade);
                    this.m_selectCascadeButton.setEnabled(false);
                    this.m_tableMixCascade.repaint();
                }
            }
        } else if (actionEvent.getSource() == this.m_manualCascadeButton) {
            this.drawManualPanel(null, null);
            this.mb_manualCascadeNew = true;
            this.m_deleteCascadeButton.setEnabled(false);
            this.m_cancelCascadeButton.setEnabled(true);
        } else if (actionEvent.getSource() == this.m_editCascadeButton) {
            if (this.mb_manualCascadeNew) {
                this.enterManualCascade();
            } else {
                this.editManualCascade();
            }
        } else if (actionEvent.getSource() == this.m_deleteCascadeButton) {
            this.deleteManualCascade();
        } else if (actionEvent.getSource() == this.m_showEditPanelButton) {
            MixCascade mixCascade = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
            this.drawManualPanel(mixCascade.getListenerInterface(0).getHost(), String.valueOf(mixCascade.getListenerInterface(0).getPort()));
            this.mb_manualCascadeNew = false;
            this.m_deleteCascadeButton.setEnabled(!JAPController.getInstance().getCurrentMixCascade().equals(mixCascade));
            this.m_cancelCascadeButton.setEnabled(false);
            this.m_oldCascadeHost = this.m_manHostField.getText();
            this.m_oldCascadePort = this.m_manPortField.getText();
        } else if (actionEvent.getSource() == this.m_showEditFilterButton) {
            if (this.m_filterPanel == null || !this.m_filterPanel.isVisible()) {
                this.showFilter();
            }
        } else if (actionEvent.getSource() == this.m_filterAllCountries) {
            this.m_filterAllMixes.setEnabled(true);
            this.m_filterAtLeast2Mixes.setEnabled(true);
        } else if (actionEvent.getSource() == this.m_filterAtLeast2Countries) {
            this.m_filterAllMixes.setEnabled(false);
            this.m_filterAtLeast2Mixes.setEnabled(true);
        } else if (actionEvent.getSource() == this.m_filterAtLeast3Countries) {
            this.m_filterAllMixes.setEnabled(false);
            this.m_filterAtLeast2Mixes.setEnabled(false);
        }
    }

    public void showFilter() {
        if (this.m_filterPanel == null || !this.m_filterPanel.isVisible()) {
            this.m_previousTrustModel = (TrustModel)this.m_cmbCascadeFilter.getSelectedItem();
            this.m_cmbCascadeFilter.setSelectedItem(TrustModel.getCustomFilter());
            this.drawFilterPanel();
        }
    }

    private void hideEditFilter() {
        this.m_filterPanel.setVisible(false);
        this.m_manualPanel.setVisible(false);
        boolean bl = true;
        if (this.m_serverPanel != null) {
            this.m_serverPanel.setVisible(true);
            bl = false;
        }
        if (this.m_serverInfoPanel != null) {
            this.m_serverInfoPanel.setVisible(true);
            bl = false;
        }
        if (bl) {
            this.updateValues(false);
        }
    }

    private boolean isServerCertVerified() {
        if (this.m_serverInfo != null) {
            return this.m_serverInfo.getCertPath().isVerified();
        }
        return false;
    }

    private void editManualCascade() {
        boolean bl = true;
        try {
            MixCascade mixCascade = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
            final MixCascade mixCascade2 = new MixCascade(this.m_manHostField.getText(), Integer.parseInt(this.m_manPortField.getText()));
            Vector vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
            for (int i = 0; i < vector.size(); ++i) {
                MixCascade mixCascade3 = (MixCascade)vector.elementAt(i);
                if (!mixCascade3.getListenerInterface(0).getHost().equalsIgnoreCase(mixCascade2.getListenerInterface(0).getHost()) || mixCascade3.getListenerInterface(0).getPort() != mixCascade2.getListenerInterface(0).getPort() || !mixCascade3.isUserDefined()) continue;
                bl = false;
            }
            if (bl) {
                Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).update(new PreviouslyKnownCascadeIDEntry(mixCascade2));
                Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).update(mixCascade2);
                Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).remove(new PreviouslyKnownCascadeIDEntry(mixCascade));
                Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).remove(mixCascade);
                if (JAPController.getInstance().getCurrentMixCascade().equals(mixCascade) && !JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                    JAPController.getInstance().setCurrentMixCascade(mixCascade2);
                }
                new Thread(new Runnable(){

                    public void run() {
                        JAPConfAnon.this.updateValues(true);
                        SwingUtilities.invokeLater(new Runnable(){

                            public void run() {
                                JAPConfAnon.this.setSelectedCascade(mixCascade2);
                            }
                        });
                    }
                }).start();
            } else {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("cascadeExistsDesc"));
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Cannot edit cascade");
            JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("errorCreateCascadeDesc"), (Throwable)exception);
        }
    }

    private void deleteManualCascade() {
        try {
            MixCascade mixCascade = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
            if (JAPController.getInstance().getCurrentMixCascade().equals(mixCascade)) {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("activeCascadeDelete"));
            } else if (JAPDialog.showYesNoDialog(this.getRootPanel(), JAPMessages.getString(MSG_REALLY_DELETE))) {
                Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).remove(mixCascade);
                if (this.m_tableMixCascade.getRowCount() >= 0) {
                    this.m_tableMixCascade.getSelectionModel().setSelectionInterval(0, 0);
                }
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Cannot delete cascade");
        }
    }

    private void enterManualCascade() {
        try {
            final MixCascade mixCascade = new MixCascade(this.m_manHostField.getText(), Integer.parseInt(this.m_manPortField.getText()));
            Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).update(new PreviouslyKnownCascadeIDEntry(mixCascade));
            Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).update(mixCascade);
            ((MixCascadeTableModel)this.m_tableMixCascade.getModel()).addElement(mixCascade);
            if (!JAPController.getInstance().isAnonConnected() && !JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                JAPController.getInstance().setCurrentMixCascade(mixCascade);
            }
            this.setSelectedCascade(mixCascade);
            new Thread(new Runnable(){

                public void run() {
                    JAPConfAnon.this.updateValues(true);
                    SwingUtilities.invokeLater(new Runnable(){

                        public void run() {
                            JAPConfAnon.this.setSelectedCascade(mixCascade);
                        }
                    });
                }
            }).start();
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Cannot create cascade");
        }
    }

    private void applyFilter() {
        if (!this.m_trustModelCopy.isEditable() || !TrustModel.getCurrentTrustModel().isEditable()) {
            return;
        }
        try {
            int n = 0;
            int n2 = this.m_cbxSocks5.isSelected() ? 2 : 0;
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$SocksAttribute == null ? (class$anon$client$TrustModel$SocksAttribute = JAPConfAnon.class$("anon.client.TrustModel$SocksAttribute")) : class$anon$client$TrustModel$SocksAttribute, n2);
            n2 = this.m_cbxDataRetention.isSelected() ? 1 : 0;
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$DataRetentionAttribute == null ? (class$anon$client$TrustModel$DataRetentionAttribute = JAPConfAnon.class$("anon.client.TrustModel$DataRetentionAttribute")) : class$anon$client$TrustModel$DataRetentionAttribute, n2);
            n2 = this.m_cbxFreeOfCharge.isSelected() ? 2 : 0;
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute == null ? (class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute = JAPConfAnon.class$("anon.client.TrustModel$ForcePremiumIfChargedAccountAttribute")) : class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute, n2);
            if (this.m_filterAtLeast2Mixes.isSelected()) {
                n = 2;
            } else if (this.m_filterAtLeast3Mixes.isSelected()) {
                n = 3;
            }
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$NumberOfMixesAttribute == null ? (class$anon$client$TrustModel$NumberOfMixesAttribute = JAPConfAnon.class$("anon.client.TrustModel$NumberOfMixesAttribute")) : class$anon$client$TrustModel$NumberOfMixesAttribute, 3, n);
            n = 0;
            if (this.m_filterAtLeast2Countries.isSelected()) {
                n = 2;
            } else if (this.m_filterAtLeast3Countries.isSelected()) {
                n = 3;
            }
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$InternationalAttribute == null ? (class$anon$client$TrustModel$InternationalAttribute = JAPConfAnon.class$("anon.client.TrustModel$InternationalAttribute")) : class$anon$client$TrustModel$InternationalAttribute, 3, n);
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$OperatorBlacklistAttribute == null ? (class$anon$client$TrustModel$OperatorBlacklistAttribute = JAPConfAnon.class$("anon.client.TrustModel$OperatorBlacklistAttribute")) : class$anon$client$TrustModel$OperatorBlacklistAttribute, 6, ((OperatorsTableModel)this.m_listOperators.getModel()).getBlacklist());
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$SpeedAttribute == null ? (class$anon$client$TrustModel$SpeedAttribute = JAPConfAnon.class$("anon.client.TrustModel$SpeedAttribute")) : class$anon$client$TrustModel$SpeedAttribute, 3, this.m_filterSpeedSlider.getValue() * 100);
            this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$DelayAttribute == null ? (class$anon$client$TrustModel$DelayAttribute = JAPConfAnon.class$("anon.client.TrustModel$DelayAttribute")) : class$anon$client$TrustModel$DelayAttribute, 5, this.convertDelayValue(this.m_filterLatencySlider.getValue(), true));
            if (this.m_filterNameField.getText().length() > 0) {
                this.m_trustModelCopy.setName(this.m_filterNameField.getText());
            }
            TrustModel.getCurrentTrustModel().clone(this.m_trustModelCopy);
            if (!TrustModel.getCurrentTrustModel().hasTrustedCascades() && !JAPController.getInstance().getCurrentMixCascade().isShownAsTrusted()) {
                JAPDialog.showWarningDialog(this.m_filterPanel, JAPMessages.getString(MSG_EXPLAIN_NO_CASCADES));
            } else if (JAPController.getInstance().isAnonConnected() && !TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade()) && (JAPModel.getInstance().isCascadeAutoSwitched() || JAPDialog.showYesNoDialog(this.m_filterPanel, JAPMessages.getString(MSG_EXPLAIN_CURRENT_CASCADE_NOT_TRUSTED)))) {
                JAPController.getInstance().switchToNextMixCascade(true);
            }
        }
        catch (NumberFormatException numberFormatException) {
            LogHolder.log(3, LogType.GUI, "Error parsing trust condition from filter settings");
        }
        this.updateValues(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == this.m_btnHomepage) {
            String string = JAPConfAnon.getUrlFromLabel((JButton)mouseEvent.getSource());
            if (string == null) {
                return;
            }
            AbstractOS abstractOS = AbstractOS.getInstance();
            try {
                abstractOS.openURL(new URL(string));
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, "Error opening URL in browser");
            }
        } else if (mouseEvent.getSource() == this.m_btnDataRetention) {
            DataRetentionDialog.show(this.getRootPanel().getParent(), this.m_cascadeInfo, this.m_serverList.getSelectedIndex());
        } else if (mouseEvent.getSource() == this.m_btnEmail) {
            AbstractOS.getInstance().openEMail(JAPConfAnon.getEMailFromLabel(this.m_btnEmail));
        } else if (mouseEvent.getSource() == this.m_listOperators) {
            if (mouseEvent.getClickCount() == 2) {
                ServiceOperator serviceOperator = null;
                Object object = this.m_listOperators.getModel();
                synchronized (object) {
                    serviceOperator = (ServiceOperator)this.m_listOperators.getValueAt(this.m_listOperators.rowAtPoint(mouseEvent.getPoint()), 1);
                }
                if (serviceOperator != null && serviceOperator.getCertificate() != null) {
                    object = new CertDetailsDialog(this.getRootPanel().getParent(), serviceOperator.getCertificate(), true, JAPMessages.getLocale());
                    ((JAPDialog)object).pack();
                    ((JAPDialog)object).setVisible(true);
                }
            }
        } else if (mouseEvent.getSource() == this.m_tableMixCascade) {
            if (mouseEvent.getClickCount() == 2) {
                MixCascade mixCascade = null;
                Object object = this.m_tableMixCascade.getModel();
                synchronized (object) {
                    mixCascade = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.rowAtPoint(mouseEvent.getPoint()), 1);
                }
                if (mixCascade != null) {
                    object = TrustModel.getCurrentTrustModel();
                    if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                        JAPDialog.showMessageDialog(this.m_tableMixCascade, JAPMessages.getString(MSG_EXPLAIN_NO_SELECTION_ANTI_CENSORSHIP));
                    } else if (!((BasicTrustModel)object).isTrusted(mixCascade) && JAPController.getInstance().getAnonMode()) {
                        JAPConfAnon.showServiceUntrustedBox(mixCascade, this.m_tableMixCascade, (TrustModel)object);
                    } else {
                        if (!((BasicTrustModel)object).isTrusted(mixCascade)) {
                            TrustModel.allowAttributeWhitelist(mixCascade);
                        }
                        JAPController.getInstance().setCurrentMixCascade(mixCascade);
                        TrustModel.cleanAttributeWhitelist(null);
                        this.m_deleteCascadeButton.setEnabled(false);
                        this.m_showEditPanelButton.setEnabled(false);
                        this.m_selectCascadeButton.setEnabled(false);
                        this.m_tableMixCascade.repaint();
                    }
                }
            }
        } else if (mouseEvent.getSource() == this.m_btnViewCert) {
            if (this.m_serverCertPaths != null && this.m_serverInfo != null) {
                MultiCertOverview multiCertOverview = new MultiCertOverview(this.getRootPanel().getParent(), this.m_serverCertPaths, this.m_serverInfo.getName(), false);
            }
        } else if (mouseEvent.getSource() == this.m_btnMap) {
            if (this.m_locationCoordinates != null && !this.m_mapShown) {
                new Thread(new Runnable(){

                    public void run() {
                        JAPConfAnon.this.m_mapShown = true;
                        JAPConfAnon.this.getRootPanel().setCursor(Cursor.getPredefinedCursor(3));
                        new MapBox(JAPConfAnon.this.getRootPanel(), (String)JAPConfAnon.this.m_locationCoordinates.elementAt(0), (String)JAPConfAnon.this.m_locationCoordinates.elementAt(1), 8).setVisible(true);
                        JAPConfAnon.this.getRootPanel().setCursor(Cursor.getDefaultCursor());
                        JAPConfAnon.this.m_mapShown = false;
                    }
                }).start();
            }
        } else if (mouseEvent.getSource() == this.m_moveMixLeft) {
            this.m_serverList.moveToPrevious();
        } else if (mouseEvent.getSource() == this.m_moveMixRight) {
            this.m_serverList.moveToNext();
        } else if (mouseEvent.getSource() == this.m_lblVDS) {
            DataRetentionDialog.show(this.getRootPanel().getParent(), this.m_cascadeInfo);
        }
    }

    private int convertDelayValue(int n, boolean bl) {
        if (bl && n == this.m_filterLatencySlider.getMinimum()) {
            return Integer.MAX_VALUE;
        }
        if (!bl && n == Integer.MAX_VALUE) {
            return this.m_filterLatencySlider.getMinimum();
        }
        n = bl ? (5 - n) * 1000 : (n < 1000 ? 1000 : (n > 5000 ? 5000 : 5 - n / 1000));
        return n;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        this.maybeShowPopup(mouseEvent);
        if (mouseEvent.getSource() == this.m_moveMixRight || mouseEvent.getSource() == this.m_moveMixLeft) {
            ((JButton)mouseEvent.getSource()).setBorder(BorderFactory.createLoweredBevelBorder());
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        this.maybeShowPopup(mouseEvent);
        if (mouseEvent.getSource() == this.m_moveMixRight || mouseEvent.getSource() == this.m_moveMixLeft) {
            ((JButton)mouseEvent.getSource()).setBorder(BorderFactory.createRaisedBevelBorder());
        }
    }

    private void maybeShowPopup(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger() && mouseEvent.getSource() == this.m_listOperators) {
            this.m_opPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public String getHelpContext() {
        return "services_anon";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                JAPController.getInstance().addObserver(this);
                JAPModel.getInstance().getRoutingSettings().addObserver(this);
                SignatureVerifier.getInstance().getVerificationCertificateStore().addObserver(this);
                Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).addObserver(this);
                Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPConfAnon.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).addObserver(this);
                Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).addObserver(this);
                Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfAnon.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).addObserver(this);
                Database.getInstance(class$anon$infoservice$MixInfo == null ? (class$anon$infoservice$MixInfo = JAPConfAnon.class$("anon.infoservice.MixInfo")) : class$anon$infoservice$MixInfo).addObserver(this);
                Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = JAPConfAnon.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).addObserver(this);
                this.m_cmbCascadeFilter.setSelectedItem(TrustModel.getCurrentTrustModel());
                TrustModel.addModelObserver(this);
            }
            return true;
        }
        return false;
    }

    protected void onRootPanelShown() {
        if (this.m_tableMixCascade.getRowCount() > 0 && this.m_tableMixCascade.getSelectedRow() < 0) {
            this.m_tableMixCascade.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    public void setSelectedCascade(MixCascade mixCascade) {
        ((MixCascadeTableModel)this.m_tableMixCascade.getModel()).setSelectedCascade(mixCascade);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        boolean bl;
        Object object = ((MixCascadeTableModel)this.m_tableMixCascade.getModel()).SYNC_UPDATE_SERVER_PANEL;
        synchronized (object) {
            bl = this.m_bUpdateServerPanel;
        }
        if (listSelectionEvent == null || !listSelectionEvent.getValueIsAdjusting()) {
            object = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
            if (object != null && this.m_serverList != null) {
                int n = this.m_serverList.getSelectedIndex();
                if (object == null) {
                    this.m_deleteCascadeButton.setEnabled(false);
                    this.m_showEditPanelButton.setEnabled(false);
                    this.m_selectCascadeButton.setEnabled(false);
                    return;
                }
                String string = ((MixCascade)object).getId();
                if (!this.m_bUpdateServerPanel) {
                    if (((MixCascade)object).getNumberOfMixes() <= 1) {
                        this.drawServerPanel(3, "", false, 0);
                    } else if (!((MixCascade)object).isUserDefined() && ((MixCascade)object).getNumberOfOperatorsShown() <= 1) {
                        this.drawServerPanel(1, ((MixCascade)object).getName(), true, n);
                    } else {
                        this.drawServerPanel(((MixCascade)object).getNumberOfMixes(), ((MixCascade)object).getName(), true, n);
                    }
                }
                PerformanceEntry performanceEntry = InfoServiceTempLayer.getPerformanceEntry(string);
                DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance(JAPMessages.getLocale());
                decimalFormat.applyPattern("#,####0.00");
                if (performanceEntry != null) {
                    boolean bl2;
                    try {
                        TrustModel.getCurrentTrustModel().getAttribute(class$anon$client$TrustModel$SpeedAttribute == null ? (class$anon$client$TrustModel$SpeedAttribute = JAPConfAnon.class$("anon.client.TrustModel$SpeedAttribute")) : class$anon$client$TrustModel$SpeedAttribute).checkTrust((MixCascade)object);
                        bl2 = true;
                    }
                    catch (TrustException trustException) {
                        bl2 = false;
                    }
                    catch (ServiceSignatureException serviceSignatureException) {
                        bl2 = false;
                    }
                    int n2 = performanceEntry.getBound(0).getBound();
                    int n3 = performanceEntry.getBestBound(0);
                    if (n3 < n2) {
                        n3 = n2;
                    }
                    if (n2 < 0 || n2 == Integer.MAX_VALUE) {
                        this.m_lblSpeed.setText(JAPMessages.getString(JAPNewView.MSG_UNKNOWN_PERFORMANCE));
                    } else if (n2 == 0) {
                        this.m_lblSpeed.setText("< " + Util.formatKbitPerSecValueWithUnit(PerformanceEntry.BOUNDARIES[0][1], 0));
                    } else if (PerformanceEntry.BOUNDARIES[0][PerformanceEntry.BOUNDARIES[0].length - 1] == n3) {
                        if (System.getProperty("java.version").compareTo("1.4") >= 0) {
                            this.m_lblSpeed.setText("\u2265 " + Util.formatKbitPerSecValueWithUnit(n2, 0));
                        } else {
                            this.m_lblSpeed.setText("> " + Util.formatKbitPerSecValueWithUnit(n2, 0));
                        }
                    } else if (n3 == n2 || n3 == Integer.MAX_VALUE) {
                        this.m_lblSpeed.setText(Util.formatKbitPerSecValueWithUnit(n2, 0));
                    } else {
                        this.m_lblSpeed.setText(Util.formatKbitPerSecValueWithoutUnit(n2, 0) + "-" + Util.formatKbitPerSecValueWithUnit(n3, 0));
                    }
                    if (bl2) {
                        this.m_lblSpeed.setForeground(this.m_anonLevelLabel.getForeground());
                    } else {
                        this.m_lblSpeed.setForeground(Color.red);
                    }
                    try {
                        TrustModel.getCurrentTrustModel().getAttribute(class$anon$client$TrustModel$DelayAttribute == null ? (class$anon$client$TrustModel$DelayAttribute = JAPConfAnon.class$("anon.client.TrustModel$DelayAttribute")) : class$anon$client$TrustModel$DelayAttribute).checkTrust((MixCascade)object);
                        bl2 = true;
                    }
                    catch (TrustException trustException) {
                        bl2 = false;
                    }
                    catch (ServiceSignatureException serviceSignatureException) {
                        bl2 = false;
                    }
                    n2 = performanceEntry.getBound(1).getBound();
                    n3 = performanceEntry.getBestBound(1);
                    if (n3 > n2) {
                        n3 = n2;
                    }
                    if (n2 <= 0) {
                        this.m_lblDelay.setText(JAPMessages.getString(JAPNewView.MSG_UNKNOWN_PERFORMANCE));
                    } else if (n2 == Integer.MAX_VALUE) {
                        this.m_lblDelay.setText("> " + PerformanceEntry.BOUNDARIES[1][PerformanceEntry.BOUNDARIES[1].length - 2] + " ms");
                    } else if (PerformanceEntry.BOUNDARIES[1][0] == n3) {
                        if (System.getProperty("java.version").compareTo("1.4") >= 0) {
                            this.m_lblDelay.setText("\u2264 " + n2 + " ms");
                        } else {
                            this.m_lblDelay.setText("< " + n2 + " ms");
                        }
                    } else if (n3 == n2 || n3 == 0) {
                        this.m_lblDelay.setText(n2 + " ms");
                    } else {
                        this.m_lblDelay.setText(n2 + "-" + n3 + " ms");
                    }
                    if (bl2) {
                        this.m_lblDelay.setForeground(this.m_anonLevelLabel.getForeground());
                    } else {
                        this.m_lblDelay.setForeground(Color.red);
                    }
                } else {
                    this.m_lblSpeed.setText(JAPMessages.getString(JAPNewView.MSG_UNKNOWN_PERFORMANCE));
                    this.m_lblDelay.setText(JAPMessages.getString(JAPNewView.MSG_UNKNOWN_PERFORMANCE));
                    this.m_lblSpeed.setForeground(this.m_anonLevelLabel.getForeground());
                    this.m_lblDelay.setForeground(this.m_anonLevelLabel.getForeground());
                }
                this.m_anonLevelLabel.setText(((MixCascade)object).getDistribution() + "," + InfoServiceTempLayer.getAnonLevel(string) + " / " + 6 + "," + 6);
                this.m_numOfUsersLabel.setText(InfoServiceTempLayer.getNumOfUsers((MixCascade)object));
                this.m_lblVDS.setVisible(((MixCascade)object).getDataRetentionInformation() != null);
                this.setAvailabilityLabel((MixCascade)object, performanceEntry);
                if (((MixCascade)object).isSocks5Supported()) {
                    this.m_lblSocks.setText(JAPMessages.getString(MSG_SUPPORTS_SOCKS));
                    this.m_lblSocks.setIcon(GUIUtils.loadImageIcon("socks_icon.gif", true));
                } else {
                    this.m_lblSocks.setText(" ");
                    this.m_lblSocks.setIcon(null);
                }
                if (((MixCascade)object).isUserDefined()) {
                    this.m_deleteCascadeButton.setEnabled(!JAPController.getInstance().getCurrentMixCascade().equals(object));
                    this.m_showEditPanelButton.setEnabled(true);
                } else {
                    this.m_deleteCascadeButton.setEnabled(false);
                    this.m_showEditPanelButton.setEnabled(false);
                }
                MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
                if (mixCascade != null && mixCascade.equals(object)) {
                    this.m_selectCascadeButton.setEnabled(false);
                } else {
                    this.m_selectCascadeButton.setEnabled(true);
                }
            }
            if (!bl) {
                this.drawServerInfoPanel();
            }
            this.itemStateChanged(null);
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
        char c;
        if (!(keyEvent.getSource() != this.m_manPortField || (c = keyEvent.getKeyChar()) >= '0' && c <= '9' || this.mb_backSpacePressed)) {
            keyEvent.consume();
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getSource() == this.m_manHostField || keyEvent.getSource() == this.m_manPortField) {
            this.m_editCascadeButton.setVisible(true);
            this.m_cancelCascadeButton.setEnabled(true);
        }
        if (keyEvent.getSource() == this.m_manPortField) {
            this.mb_backSpacePressed = keyEvent.getKeyCode() == 8;
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public void update(Observable var1_1, Object var2_2) {
        try {
            var3_3 = false;
            var4_5 = null;
            var5_6 = this.m_tableMixCascade.getSelectedRow();
            if (var5_6 >= 0) {
                try {
                    var4_5 = (MixCascade)this.m_tableMixCascade.getValueAt(this.m_tableMixCascade.getSelectedRow(), 1);
                }
                catch (Exception var6_7) {
                    // empty catch block
                }
            }
            var6_8 = this.m_serverList.getSelectedIndex();
            if (var1_1 == JAPModel.getInstance().getRoutingSettings()) {
                if (((JAPRoutingMessage)var2_2).getMessageCode() == 16 && (var7_9 = this.m_selectCascadeButton) != null) {
                    var7_9.setEnabled(JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder() == false);
                }
            } else if (var2_2 != null && var2_2 instanceof DatabaseMessage) {
                var7_10 = (DatabaseMessage)var2_2;
                if (var7_10.getMessageData() instanceof MixCascade) {
                    if (var7_10.getMessageCode() == 1 || var7_10.getMessageCode() == 3 || var7_10.getMessageCode() == 4) {
                        var3_3 = true;
                    } else if (var7_10.getMessageCode() == 2 && var4_5 != null && var4_5.equals((MixCascade)var7_10.getMessageData())) {
                        var3_3 = true;
                    }
                    if (var7_10.getMessageCode() == 4) {
                        Database.getInstance(JAPConfAnon.class$anon$infoservice$MixInfo == null ? (JAPConfAnon.class$anon$infoservice$MixInfo = JAPConfAnon.class$("anon.infoservice.MixInfo")) : JAPConfAnon.class$anon$infoservice$MixInfo).removeAll();
                    } else if (var7_10.getMessageCode() == 3) {
                        try {
                            var8_12 = (MixCascade)((DatabaseMessage)var2_2).getMessageData();
                            if (JAPController.getInstance().getCurrentMixCascade().equals(var8_12)) ** GOTO lbl94
                            for (var9_17 = 0; var9_17 < var8_12.getNumberOfMixes(); ++var9_17) {
                                Database.getInstance(JAPConfAnon.class$anon$infoservice$MixInfo == null ? JAPConfAnon.class$("anon.infoservice.MixInfo") : JAPConfAnon.class$anon$infoservice$MixInfo).remove(var8_12.getMixId(var9_17));
                            }
                        }
                        catch (Exception var8_13) {
                            LogHolder.log(2, LogType.MISC, var8_13);
                        }
                    } else if (var7_10.getMessageCode() == 1 || var7_10.getMessageCode() == 2) {
                        try {
                            var8_14 = (MixCascade)((DatabaseMessage)var2_2).getMessageData();
                            for (var11_19 = 0; var11_19 < var8_14.getNumberOfMixes(); ++var11_19) {
                                var10_20 = var8_14.getMixId(var11_19);
                                var9_18 = var8_14.getMixInfo(var11_19);
                                if (var9_18 == null || var9_18.getVersionNumber() <= 0L) {
                                    var9_18 = (MixInfo)Database.getInstance(JAPConfAnon.class$anon$infoservice$MixInfo == null ? JAPConfAnon.class$("anon.infoservice.MixInfo") : JAPConfAnon.class$anon$infoservice$MixInfo).getEntryById(var10_20);
                                }
                                if (JAPModel.isInfoServiceDisabled() || var8_14.isUserDefined() || var9_18 != null && !var9_18.isFromCascade()) continue;
                                var12_21 = InfoServiceHolder.getInstance().getMixInfo(var10_20);
                                if (var12_21 == null) {
                                    LogHolder.log(5, LogType.GUI, "Did not get Mix info from InfoService for Mix " + var10_20 + "!");
                                    continue;
                                }
                                Database.getInstance(JAPConfAnon.class$anon$infoservice$MixInfo == null ? JAPConfAnon.class$("anon.infoservice.MixInfo") : JAPConfAnon.class$anon$infoservice$MixInfo).update(var12_21);
                            }
                        }
                        catch (Exception var8_15) {
                            LogHolder.log(2, LogType.MISC, var8_15);
                        }
                    }
                } else if (var7_10.getMessageData() instanceof BlacklistedCascadeIDEntry) {
                    var8_16 = (BlacklistedCascadeIDEntry)var7_10.getMessageData();
                    if (var4_5 != null && var4_5.getId().equals(var8_16.getCascadeId())) {
                        this.setAvailabilityLabel(var4_5, InfoServiceTempLayer.access$3100(var8_16.getCascadeId()));
                    }
                } else if (var7_10.getMessageData() instanceof ServiceOperator) {
                    if (this.m_listOperators != null) {
                        ((OperatorsTableModel)this.m_listOperators.getModel()).update();
                    }
                } else if (var7_10.getMessageData() instanceof StatusInfo) {
                    if (var4_5 != null && var4_5.getId().equals(((StatusInfo)var7_10.getMessageData()).getId())) {
                        var3_3 = true;
                    }
                } else if (var7_10.getMessageData() instanceof MixInfo) {
                    if (var4_5 != null && var6_8 >= 0 && var4_5.getNumberOfMixes() > 0 && var4_5.getMixId(var6_8).equals(((MixInfo)var7_10.getMessageData()).getId())) {
                        var3_3 = true;
                    }
                } else if (var7_10.getMessageData() instanceof PerformanceInfo) {
                    var3_3 = true;
                }
            } else if (var1_1 == JAPController.getInstance() && var2_2 != null) {
                if (((JAPControllerMessage)var2_2).getMessageCode() == 2) {
                    var3_3 = true;
                }
            } else if (var1_1 == SignatureVerifier.getInstance().getVerificationCertificateStore()) {
                if (var2_2 == null || var2_2 instanceof Integer && (Integer)var2_2 == 1) {
                    var3_3 = true;
                }
            } else if (var1_1 == TrustModel.getObservable()) {
                var7_11 = new DefaultComboBoxModel<E>(TrustModel.getTrustModels());
                this.m_cmbCascadeFilter.setModel(var7_11);
                this.m_cmbCascadeFilter.setSelectedItem(TrustModel.getCurrentTrustModel());
                if (var2_2 == TrustModel.NOTIFY_TRUST_MODEL_ADDED && TrustModel.getCurrentTrustModel().isEditable() && 5L == TrustModel.getCurrentTrustModel().getId()) {
                    this.drawFilterPanel();
                }
                var3_3 = true;
            }
lbl94:
            // 17 sources

            if (var3_3 == false) return;
            this.updateValues(false);
            return;
        }
        catch (Exception var3_4) {
            LogHolder.log(0, LogType.GUI, var3_4);
        }
    }

    private static String getEMailFromLabel(JButton jButton) {
        String string = jButton.getToolTipText();
        if (AbstractX509AlternativeName.isValidEMail(string)) {
            return string;
        }
        return null;
    }

    private static String getUrlFromLabel(JButton jButton) {
        try {
            return new URL(jButton.getToolTipText()).toString();
        }
        catch (Exception exception) {
            return null;
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

    private class LocalAnonServiceEventListener
    extends AnonServiceEventAdapter {
        private LocalAnonServiceEventListener() {
        }

        public void disconnected() {
            JAPConfAnon.this.updateValues(false);
        }

        public void connectionEstablished(AnonServerDescription anonServerDescription) {
            JAPConfAnon.this.updateValues(false);
        }
    }

    private class OperatorsTableModel
    extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private Vector m_vecOperators = new Vector();
        private Vector m_vecBlacklist = new Vector();
        private String[] columnNames = new String[]{"B", "Operator"};
        private Class[] columnClasses = new Class[]{class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAPConfAnon.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$java$lang$Object == null ? (class$java$lang$Object = JAPConfAnon.class$("java.lang.Object")) : class$java$lang$Object};

        private OperatorsTableModel() {
        }

        public int getRowCount() {
            return this.m_vecOperators.size();
        }

        public int getColumnCount() {
            return this.columnNames.length;
        }

        public boolean isCellEditable(int n, int n2) {
            return n2 == 0;
        }

        public synchronized void update() {
            if (JAPConfAnon.this.m_trustModelCopy != null) {
                this.m_vecBlacklist = (Vector)((Vector)JAPConfAnon.this.m_trustModelCopy.getAttribute(class$anon$client$TrustModel$OperatorBlacklistAttribute == null ? (class$anon$client$TrustModel$OperatorBlacklistAttribute = JAPConfAnon.class$("anon.client.TrustModel$OperatorBlacklistAttribute")) : class$anon$client$TrustModel$OperatorBlacklistAttribute).getConditionValue()).clone();
            }
            this.m_vecOperators = Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfAnon.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getSortedEntryList(new Util.Comparable(){

                public int compare(Object object, Object object2) {
                    boolean bl;
                    if (object == null || object2 == null || ((ServiceOperator)object).getOrganization() == null || ((ServiceOperator)object2).getOrganization() == null) {
                        return 0;
                    }
                    boolean bl2 = OperatorsTableModel.this.m_vecBlacklist.contains(object);
                    if (bl2 == (bl = OperatorsTableModel.this.m_vecBlacklist.contains(object2))) {
                        return ((ServiceOperator)object).getOrganization().compareTo(((ServiceOperator)object2).getOrganization());
                    }
                    if (bl2 && !bl) {
                        return -1;
                    }
                    return 1;
                }
            });
            this.fireTableDataChanged();
        }

        public synchronized void reset() {
            JAPConfAnon.this.m_trustModelCopy.setAttribute(class$anon$client$TrustModel$OperatorBlacklistAttribute == null ? (class$anon$client$TrustModel$OperatorBlacklistAttribute = JAPConfAnon.class$("anon.client.TrustModel$OperatorBlacklistAttribute")) : class$anon$client$TrustModel$OperatorBlacklistAttribute, 6, new Vector());
            this.update();
        }

        public Class getColumnClass(int n) {
            return this.columnClasses[n];
        }

        public String getColumnName(int n) {
            return this.columnNames[n];
        }

        public Object getValueAt(int n, int n2) {
            try {
                if (n2 == 0) {
                    return new Boolean(!this.m_vecBlacklist.contains(this.m_vecOperators.elementAt(n)));
                }
                if (n2 == 1) {
                    return this.m_vecOperators.elementAt(n);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return null;
        }

        public void setValueAt(Object object, int n, int n2) {
            if (n2 == 0) {
                try {
                    Object e = this.m_vecOperators.elementAt(n);
                    if (object == Boolean.FALSE) {
                        if (!this.m_vecBlacklist.contains(e)) {
                            this.m_vecBlacklist.addElement(e);
                        }
                    } else {
                        this.m_vecBlacklist.removeElement(e);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }

        public Vector getBlacklist() {
            return this.m_vecBlacklist;
        }
    }

    private class MixCascadeTableModel
    extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        public final Object SYNC_UPDATE_SERVER_PANEL = new Object();
        private Vector m_vecCascades;
        private String[] columnNames = new String[]{"B", "Cascade"};
        private Class[] columnClasses = new Class[]{class$java$lang$Boolean == null ? (class$java$lang$Boolean = JAPConfAnon.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$java$lang$Object == null ? (class$java$lang$Object = JAPConfAnon.class$("java.lang.Object")) : class$java$lang$Object};

        private MixCascadeTableModel() {
            this.update();
        }

        public synchronized void addElement(MixCascade mixCascade) {
            this.m_vecCascades.addElement(mixCascade);
            this.fireTableDataChanged();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized void update() {
            int n = JAPConfAnon.this.m_tableMixCascade.getSelectedRow();
            MixCascade mixCascade = null;
            if (n >= 0) {
                mixCascade = (MixCascade)this.getValueAt(n, 1);
            }
            final TrustModel trustModel = TrustModel.getCurrentTrustModel();
            final boolean bl = trustModel.hasFreeCascades();
            this.m_vecCascades = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getSortedEntryList(new Util.Comparable(){

                public int compare(Object object, Object object2) {
                    boolean bl3;
                    if (object == null && object2 == null) {
                        return 0;
                    }
                    if (object == null) {
                        return 1;
                    }
                    MixCascade mixCascade = (MixCascade)object;
                    MixCascade mixCascade2 = (MixCascade)object2;
                    boolean bl2 = trustModel.isTrusted(mixCascade);
                    if (bl2 == (bl3 = trustModel.isTrusted(mixCascade2))) {
                        if (bl) {
                            if (mixCascade.isPayment() && !mixCascade2.isPayment()) {
                                return 1;
                            }
                            if (!mixCascade.isPayment() && mixCascade2.isPayment()) {
                                return -1;
                            }
                        }
                        return object.toString().compareTo(object2.toString());
                    }
                    if (bl2 && !bl3) {
                        return -1;
                    }
                    return 1;
                }
            });
            MixCascade mixCascade2 = JAPController.getInstance().getCurrentMixCascade();
            if (!this.m_vecCascades.contains(mixCascade2)) {
                this.m_vecCascades.addElement(mixCascade2);
            }
            Object object = this.SYNC_UPDATE_SERVER_PANEL;
            synchronized (object) {
                JAPConfAnon.this.m_bUpdateServerPanel = JAPConfAnon.this.m_manualPanel != null && JAPConfAnon.this.m_manualPanel.isVisible() || JAPConfAnon.this.m_filterPanel != null && JAPConfAnon.this.m_filterPanel.isVisible();
                this.fireTableDataChanged();
                int n2 = -1;
                if (mixCascade != null) {
                    n2 = this.m_vecCascades.indexOf(mixCascade);
                }
                if ((mixCascade == null || n2 < 0) && JAPConfAnon.this.m_tableMixCascade.getRowCount() > 0) {
                    n2 = 0;
                }
                if (JAPConfAnon.this.m_tableMixCascade.getSelectedRow() != n2) {
                    JAPConfAnon.this.m_tableMixCascade.setRowSelectionInterval(n2, n2);
                }
                JAPConfAnon.this.m_bUpdateServerPanel = false;
            }
        }

        public int getColumnCount() {
            return this.columnNames.length;
        }

        public int getRowCount() {
            return this.m_vecCascades.size();
        }

        public synchronized void setSelectedCascade(MixCascade mixCascade) {
            if (mixCascade == null) {
                return;
            }
            int n = this.m_vecCascades.indexOf(mixCascade);
            if (n >= 0) {
                JAPConfAnon.this.m_tableMixCascade.setRowSelectionInterval(n, n);
                JAPConfAnon.this.m_tableMixCascade.scrollRectToVisible(JAPConfAnon.this.m_tableMixCascade.getCellRect(n, n, true));
            }
        }

        public Object getValueAt(int n, int n2) {
            MixCascade mixCascade;
            try {
                mixCascade = (MixCascade)this.m_vecCascades.elementAt(n);
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                return null;
            }
            if (mixCascade == null) {
                return null;
            }
            if (n2 == 0) {
                if (Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).getEntryById(mixCascade.getMixIDsAsString()) == null) {
                    return new Boolean(true);
                }
                return new Boolean(false);
            }
            return mixCascade;
        }

        public Class getColumnClass(int n) {
            return this.columnClasses[n];
        }

        public String getColumnName(int n) {
            return this.columnNames[n];
        }

        public boolean isCellEditable(int n, int n2) {
            return n2 == 0;
        }

        public void setValueAt(Object object, int n, int n2) {
            MixCascade mixCascade = (MixCascade)this.m_vecCascades.elementAt(n);
            if (Boolean.FALSE.equals(object)) {
                Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).update(new BlacklistedCascadeIDEntry(mixCascade));
            } else {
                Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = JAPConfAnon.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).remove(mixCascade.getMixIDsAsString());
            }
            this.fireTableCellUpdated(n, 1);
        }
    }

    class MixCascadeCellRenderer
    extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        public void setValue(Object object) {
            if (object == null) {
                this.setText("");
                return;
            }
            if (object instanceof MixCascade) {
                ImageIcon imageIcon;
                MixCascade mixCascade = (MixCascade)object;
                this.setToolTipText(JAPMessages.getString("cascadeReachableBy") + ": " + InfoServiceTempLayer.getHosts(mixCascade) + " - " + JAPMessages.getString("cascadePorts") + ": " + InfoServiceTempLayer.getPorts(mixCascade));
                if (mixCascade.isUserDefined()) {
                    if (TrustModel.getCurrentTrustModel().isTrusted(mixCascade)) {
                        imageIcon = GUIUtils.loadImageIcon("servermanuell.gif", true);
                        this.setForeground(Color.black);
                    } else {
                        imageIcon = GUIUtils.loadImageIcon("cdisabled.gif", true);
                        this.setForeground(Color.gray);
                    }
                } else if (mixCascade.isPayment()) {
                    if (TrustModel.getCurrentTrustModel().isTrusted(mixCascade)) {
                        imageIcon = GUIUtils.loadImageIcon("serverwithpayment.gif", true);
                        this.setForeground(Color.black);
                    } else {
                        imageIcon = GUIUtils.loadImageIcon("cdisabled.gif", true);
                        this.setForeground(Color.gray);
                    }
                } else if (TrustModel.getCurrentTrustModel().isTrusted(mixCascade)) {
                    imageIcon = GUIUtils.loadImageIcon("serverfrominternet.gif", true);
                    this.setForeground(Color.black);
                } else {
                    imageIcon = GUIUtils.loadImageIcon("cdisabled.gif", true);
                    this.setForeground(Color.gray);
                }
                if (mixCascade.isSocks5Supported()) {
                    imageIcon = GUIUtils.combine(imageIcon, GUIUtils.loadImageIcon("socks_icon.gif", true));
                }
                this.setIcon(imageIcon);
                if (mixCascade.equals(JAPController.getInstance().getCurrentMixCascade())) {
                    GUIUtils.setFontStyle(this, 1);
                } else {
                    GUIUtils.setFontStyle(this, 0);
                }
            }
            this.setText(object.toString());
        }
    }

    private class FilterPanel
    extends JPanel {
        private static final long serialVersionUID = 1L;

        public FilterPanel(JAPConfAnon jAPConfAnon2) {
            int n;
            GridBagLayout gridBagLayout = new GridBagLayout();
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(gridBagLayout);
            JAPConfAnon.this.m_opPopupMenu = new JPopupMenu();
            JMenuItem jMenuItem = new JMenuItem(JAPMessages.getString(MSG_FILTER_SELECT_ALL_OPERATORS));
            jMenuItem.addActionListener(jAPConfAnon2);
            jMenuItem.setActionCommand(MSG_FILTER_SELECT_ALL_OPERATORS);
            JAPConfAnon.this.m_opPopupMenu.add(jMenuItem);
            JLabel jLabel = new JLabel(JAPMessages.getString(MSG_FILTER) + ":");
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new Insets(0, 0, 5, 5);
            gridBagConstraints.anchor = 17;
            JAPConfAnon.this.m_filterNameField = new JTextField();
            ++gridBagConstraints.gridx;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.weightx = 0.0;
            TitledBorder titledBorder = new TitledBorder(JAPMessages.getString(MSG_FILTER_CASCADES));
            JPanel jPanel = new JPanel(new GridLayout(0, 1));
            jPanel.setBorder(titledBorder);
            JAPConfAnon.this.m_filterAllMixes = new JRadioButton(JAPMessages.getString(MSG_FILTER_ALL));
            JAPConfAnon.this.m_filterAllMixes.setActionCommand(String.valueOf(0));
            JAPConfAnon.this.m_filterAllMixes.setSelected(true);
            JAPConfAnon.this.m_filterAllMixes.addActionListener(jAPConfAnon2);
            jPanel.add((Component)JAPConfAnon.this.m_filterAllMixes, gridBagConstraints);
            JAPConfAnon.this.m_filterAtLeast2Mixes = new JRadioButton(JAPMessages.getString(MSG_FILTER_AT_LEAST_2_MIXES));
            JAPConfAnon.this.m_filterAtLeast2Mixes.setActionCommand(String.valueOf(2));
            JAPConfAnon.this.m_filterAtLeast2Mixes.addActionListener(jAPConfAnon2);
            jPanel.add((Component)JAPConfAnon.this.m_filterAtLeast2Mixes, gridBagConstraints);
            JAPConfAnon.this.m_filterAtLeast3Mixes = new JRadioButton(JAPMessages.getString(MSG_FILTER_AT_LEAST_3_MIXES));
            JAPConfAnon.this.m_filterAtLeast3Mixes.setActionCommand(String.valueOf(1));
            JAPConfAnon.this.m_filterAtLeast3Mixes.addActionListener(jAPConfAnon2);
            jPanel.add((Component)JAPConfAnon.this.m_filterAtLeast3Mixes, gridBagConstraints);
            JAPConfAnon.this.m_filterCascadeGroup = new ButtonGroup();
            JAPConfAnon.this.m_filterCascadeGroup.add(JAPConfAnon.this.m_filterAllMixes);
            JAPConfAnon.this.m_filterCascadeGroup.add(JAPConfAnon.this.m_filterAtLeast2Mixes);
            JAPConfAnon.this.m_filterCascadeGroup.add(JAPConfAnon.this.m_filterAtLeast3Mixes);
            gridBagConstraints.anchor = 18;
            gridBagConstraints.fill = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridx = 0;
            ++gridBagConstraints.gridy;
            gridBagConstraints.weightx = 0.4;
            this.add((Component)jPanel, gridBagConstraints);
            titledBorder = new TitledBorder(JAPMessages.getString(MSG_FILTER_INTERNATIONALITY));
            jPanel = new JPanel(new GridLayout(0, 1));
            jPanel.setBorder(titledBorder);
            JAPConfAnon.this.m_filterAllCountries = new JRadioButton(JAPMessages.getString(MSG_FILTER_ALL));
            JAPConfAnon.this.m_filterAllCountries.setActionCommand(String.valueOf(0));
            JAPConfAnon.this.m_filterAllCountries.setSelected(true);
            JAPConfAnon.this.m_filterAllCountries.addActionListener(jAPConfAnon2);
            jPanel.add((Component)JAPConfAnon.this.m_filterAllCountries, gridBagConstraints);
            JAPConfAnon.this.m_filterAtLeast2Countries = new JRadioButton(JAPMessages.getString(MSG_FILTER_AT_LEAST_2_COUNTRIES));
            JAPConfAnon.this.m_filterAtLeast2Countries.setActionCommand(String.valueOf(3));
            JAPConfAnon.this.m_filterAtLeast2Countries.addActionListener(jAPConfAnon2);
            jPanel.add(JAPConfAnon.this.m_filterAtLeast2Countries);
            JAPConfAnon.this.m_filterAtLeast3Countries = new JRadioButton(JAPMessages.getString(MSG_FILTER_AT_LEAST_3_COUNTRIES));
            JAPConfAnon.this.m_filterAtLeast3Countries.setActionCommand(String.valueOf(3));
            JAPConfAnon.this.m_filterAtLeast3Countries.addActionListener(jAPConfAnon2);
            jPanel.add(JAPConfAnon.this.m_filterAtLeast3Countries);
            JAPConfAnon.this.m_filterInternationalGroup = new ButtonGroup();
            JAPConfAnon.this.m_filterInternationalGroup.add(JAPConfAnon.this.m_filterAllCountries);
            JAPConfAnon.this.m_filterInternationalGroup.add(JAPConfAnon.this.m_filterAtLeast2Countries);
            JAPConfAnon.this.m_filterInternationalGroup.add(JAPConfAnon.this.m_filterAtLeast3Countries);
            gridBagConstraints.gridx += 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.weightx = 0.15;
            this.add((Component)jPanel, gridBagConstraints);
            jPanel = new JPanel(new GridBagLayout());
            jPanel.setEnabled(false);
            jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_FILTER_SPEED)));
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.anchor = 18;
            gridBagConstraints2.insets = new Insets(0, 5, 5, 0);
            gridBagConstraints2.weightx = 1.0;
            jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_FILTER_AT_LEAST)), gridBagConstraints2);
            JAPConfAnon.this.m_filterSpeedSlider = new JSlider(1);
            JAPConfAnon.this.m_filterSpeedSlider.setMinimum(0);
            JAPConfAnon.this.m_filterSpeedSlider.setMaximum(4);
            JAPConfAnon.this.m_filterSpeedSlider.setValue(0);
            JAPConfAnon.this.m_filterSpeedSlider.setMajorTickSpacing(1);
            JAPConfAnon.this.m_filterSpeedSlider.setPaintLabels(true);
            JAPConfAnon.this.m_filterSpeedSlider.setPaintTicks(true);
            JAPConfAnon.this.m_filterSpeedSlider.setInverted(true);
            JAPConfAnon.this.m_filterSpeedSlider.setSnapToTicks(true);
            Hashtable<Integer, JLabel> hashtable = new Hashtable<Integer, JLabel>(5);
            JLabel jLabel2 = null;
            for (n = 0; n < 5; ++n) {
                jLabel2 = n == 0 ? new JLabel(JAPMessages.getString(MSG_FILTER_ALL)) : new JLabel(n * 100 + " kbit/s");
                hashtable.put(new Integer(n), jLabel2);
            }
            JAPConfAnon.this.m_filterSpeedSlider.setLabelTable(hashtable);
            ++gridBagConstraints2.gridy;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.fill = 3;
            jPanel.add((Component)JAPConfAnon.this.m_filterSpeedSlider, gridBagConstraints2);
            ++gridBagConstraints.gridx;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.weightx = 0.175;
            this.add((Component)jPanel, gridBagConstraints);
            jPanel = new JPanel(new GridBagLayout());
            jPanel.setEnabled(false);
            jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_FILTER_LATENCY)));
            gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.anchor = 18;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(0, 5, 5, 0);
            jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_FILTER_AT_MOST)), gridBagConstraints2);
            JAPConfAnon.this.m_filterLatencySlider = new JSlider(1);
            JAPConfAnon.this.m_filterLatencySlider.setMinimum(0);
            JAPConfAnon.this.m_filterLatencySlider.setMaximum(4);
            jLabel2 = null;
            hashtable = new Hashtable(5);
            for (n = 0; n < 5; ++n) {
                jLabel2 = n == 0 ? new JLabel(JAPMessages.getString(MSG_FILTER_ALL)) : new JLabel(5000 - n * 1000 + " ms");
                hashtable.put(new Integer(n), jLabel2);
            }
            JAPConfAnon.this.m_filterLatencySlider.setLabelTable(hashtable);
            JAPConfAnon.this.m_filterLatencySlider.setMajorTickSpacing(1);
            JAPConfAnon.this.m_filterLatencySlider.setMinorTickSpacing(1);
            JAPConfAnon.this.m_filterLatencySlider.setValue(0);
            JAPConfAnon.this.m_filterLatencySlider.setPaintLabels(true);
            JAPConfAnon.this.m_filterLatencySlider.setPaintTicks(true);
            JAPConfAnon.this.m_filterLatencySlider.setInverted(true);
            JAPConfAnon.this.m_filterLatencySlider.setSnapToTicks(true);
            ++gridBagConstraints2.gridy;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.fill = 3;
            jPanel.add((Component)JAPConfAnon.this.m_filterLatencySlider, gridBagConstraints2);
            ++gridBagConstraints.gridx;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.weightx = 0.275;
            this.add((Component)jPanel, gridBagConstraints);
            jPanel = new JPanel(new GridLayout());
            jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_FILTER_OPERATORS)));
            JAPConfAnon.this.m_listOperators = new JTable();
            JAPConfAnon.this.m_listOperators.setModel(new OperatorsTableModel());
            JAPConfAnon.this.m_listOperators.setTableHeader(null);
            JAPConfAnon.this.m_listOperators.setIntercellSpacing(new Dimension(0, 0));
            JAPConfAnon.this.m_listOperators.setShowGrid(false);
            JAPConfAnon.this.m_listOperators.setSelectionMode(0);
            JAPConfAnon.this.m_listOperators.addMouseListener(jAPConfAnon2);
            JAPConfAnon.this.m_listOperators.getColumnModel().getColumn(0).setMaxWidth(1);
            JAPConfAnon.this.m_listOperators.getColumnModel().getColumn(0).setPreferredWidth(1);
            JAPConfAnon.this.m_listOperators.getColumnModel().getColumn(1).setCellRenderer(new OperatorsCellRenderer());
            JScrollPane jScrollPane = new JScrollPane(JAPConfAnon.this.m_listOperators);
            jScrollPane.setHorizontalScrollBarPolicy(31);
            jScrollPane.setPreferredSize(new Dimension(130, 30));
            jPanel.add(jScrollPane);
            gridBagConstraints.gridx = 0;
            ++gridBagConstraints.gridy;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.weightx = 0.4;
            gridBagConstraints.weighty = 0.7;
            this.add((Component)jPanel, gridBagConstraints);
            titledBorder = new TitledBorder(JAPMessages.getString(MSG_FILTER_OTHER));
            jPanel = new JPanel(new GridLayout(0, 1));
            jPanel.setBorder(titledBorder);
            JAPConfAnon.this.m_cbxSocks5 = new JCheckBox(JAPMessages.getString(MSG_FILTER_SOCKS_ONLY), false);
            JAPConfAnon.this.m_cbxDataRetention = new JCheckBox(JAPMessages.getString(MSG_FILTER_NO_DATA_RETENTION), false);
            if (JAPModel.getInstance().getContext().equals("jondonym")) {
                JAPConfAnon.this.m_cbxFreeOfCharge = new JCheckBox(JAPMessages.getString(MSG_FILTER_PAYMENT_PREFERRED), false);
            } else {
                JAPConfAnon.this.m_cbxFreeOfCharge = new JCheckBox(JAPMessages.getString(MSG_FILTER_BUSINESS_ONLY), false);
            }
            jPanel.add(JAPConfAnon.this.m_cbxSocks5);
            jPanel.add(JAPConfAnon.this.m_cbxDataRetention);
            jPanel.add(JAPConfAnon.this.m_cbxFreeOfCharge);
            gridBagConstraints.gridx += 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.weightx = 0.15;
            this.add((Component)jPanel, gridBagConstraints);
        }

        private void selectRadioButton(ButtonGroup buttonGroup, String string) {
            Enumeration<AbstractButton> enumeration = buttonGroup.getElements();
            while (enumeration.hasMoreElements()) {
                AbstractButton abstractButton = enumeration.nextElement();
                if (!string.equals(abstractButton.getActionCommand())) continue;
                buttonGroup.setSelected(abstractButton.getModel(), true);
                break;
            }
        }
    }

    private class ServerInfoPanel
    extends JPanel {
        private static final long serialVersionUID = 1L;

        public ServerInfoPanel(JAPConfAnon jAPConfAnon2) {
            GridBagLayout gridBagLayout = new GridBagLayout();
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            this.setLayout(gridBagLayout);
            JPanel jPanel = new JPanel(new GridBagLayout());
            gridBagConstraints.insets = new Insets(5, 10, 5, 5);
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = 0;
            gridBagConstraints.anchor = 18;
            gridBagConstraints.insets = new Insets(5, 20, 5, 0);
            this.add((Component)jPanel, gridBagConstraints);
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.fill = 0;
            gridBagConstraints2.anchor = 17;
            gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
            JAPConfAnon.this.m_moveMixLeft = new JButton(GUIUtils.loadImageIcon("arrowLeft.png", true));
            JAPConfAnon.this.m_moveMixLeft.setBorder(BorderFactory.createRaisedBevelBorder());
            JAPConfAnon.this.m_moveMixLeft.addMouseListener(jAPConfAnon2);
            jPanel.add((Component)JAPConfAnon.this.m_moveMixLeft, gridBagConstraints2);
            ++gridBagConstraints2.gridx;
            gridBagConstraints2.weightx = 0.0;
            gridBagConstraints2.insets = new Insets(5, 5, 5, 0);
            jPanel.add((Component)JAPConfAnon.this.m_lblMix, gridBagConstraints2);
            JAPConfAnon.this.m_moveMixRight = new JButton(GUIUtils.loadImageIcon("arrowRight.png", true));
            JAPConfAnon.this.m_moveMixRight.setBorder(BorderFactory.createRaisedBevelBorder());
            JAPConfAnon.this.m_moveMixRight.addMouseListener(jAPConfAnon2);
            ++gridBagConstraints2.gridx;
            gridBagConstraints2.weightx = 1.0;
            jPanel.add((Component)JAPConfAnon.this.m_moveMixRight, gridBagConstraints2);
            ++gridBagConstraints2.gridx;
            gridBagConstraints2.weightx = 0.0;
            jPanel.add((Component)JAPConfAnon.this.m_lblMixOfService, gridBagConstraints2);
            JLabel jLabel = new JLabel(JAPMessages.getString(MixDetailsDialog.MSG_MIX_NAME) + ":");
            ++gridBagConstraints.gridy;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(5, 30, 5, 5);
            this.add((Component)jLabel, gridBagConstraints);
            JAPConfAnon.this.m_nrPanel = new JPanel(new GridBagLayout());
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.insets = new Insets(5, 30, 5, 0);
            this.add((Component)JAPConfAnon.this.m_nrPanel, gridBagConstraints);
            gridBagConstraints.gridx = 3;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = 2;
            gridBagConstraints.anchor = 17;
            this.add((Component)new JLabel(), gridBagConstraints);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            JAPConfAnon.this.m_nrLabel = new JLabel();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 0.0;
            gridBagConstraints3.insets = new Insets(0, 0, 0, 5);
            JAPConfAnon.this.m_nrPanel.add((Component)JAPConfAnon.this.m_nrLabel, gridBagConstraints3);
            jLabel = new JLabel(JAPMessages.getString(MixDetailsDialog.MSG_LOCATION) + ":");
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridx = 0;
            ++gridBagConstraints.gridy;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(5, 30, 5, 5);
            this.add((Component)jLabel, gridBagConstraints);
            JAPConfAnon.this.m_locationLabel = new JLabel();
            JAPConfAnon.this.m_locationLabel.addMouseListener(jAPConfAnon2);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new Insets(5, 30, 5, 0);
            this.add((Component)JAPConfAnon.this.m_locationLabel, gridBagConstraints);
            jLabel = new JLabel(JAPMessages.getString("mixOperator"));
            ++gridBagConstraints.gridy;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.insets = new Insets(5, 30, 5, 5);
            this.add((Component)jLabel, gridBagConstraints);
            JAPConfAnon.this.m_operatorLabel = new JLabel();
            JAPConfAnon.this.m_operatorLabel.addMouseListener(jAPConfAnon2);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(5, 30, 5, 0);
            gridBagConstraints.gridwidth = 2;
            this.add((Component)JAPConfAnon.this.m_operatorLabel, gridBagConstraints);
            JAPConfAnon.this.m_pnlMixInfoButtons = new JPanel(new GridBagLayout());
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.anchor = 17;
            gridBagConstraints4.fill = 2;
            JAPConfAnon.this.m_btnViewCert = new JButton(JAPMessages.getString(MixDetailsDialog.MSG_CERTIFICATES));
            JAPConfAnon.this.m_btnViewCert.addMouseListener(jAPConfAnon2);
            gridBagConstraints4.insets = new Insets(5, 15, 5, 0);
            JAPConfAnon.this.m_pnlMixInfoButtons.add((Component)JAPConfAnon.this.m_btnViewCert, gridBagConstraints4);
            JAPConfAnon.this.m_btnEmail = new JButton(JAPMessages.getString(MixDetailsDialog.MSG_E_MAIL));
            JAPConfAnon.this.m_btnEmail.addMouseListener(jAPConfAnon2);
            ++gridBagConstraints4.gridx;
            gridBagConstraints4.insets = new Insets(5, 5, 5, 0);
            JAPConfAnon.this.m_pnlMixInfoButtons.add((Component)JAPConfAnon.this.m_btnEmail, gridBagConstraints4);
            JAPConfAnon.this.m_btnHomepage = new JButton(JAPMessages.getString(MixDetailsDialog.MSG_HOMEPAGE));
            JAPConfAnon.this.m_btnHomepage.addMouseListener(jAPConfAnon2);
            ++gridBagConstraints4.gridx;
            JAPConfAnon.this.m_pnlMixInfoButtons.add((Component)JAPConfAnon.this.m_btnHomepage, gridBagConstraints4);
            JAPConfAnon.this.m_btnMap = new JButton(JAPMessages.getString(MSG_SHOW_ON_MAP));
            JAPConfAnon.this.m_btnMap.addMouseListener(jAPConfAnon2);
            ++gridBagConstraints4.gridx;
            JAPConfAnon.this.m_pnlMixInfoButtons.add((Component)JAPConfAnon.this.m_btnMap, gridBagConstraints4);
            JAPConfAnon.this.m_btnDataRetention = new JButton(JAPMessages.getString(MixDetailsDialog.MSG_BTN_DATA_RETENTION), GUIUtils.loadImageIcon("certs/invalid.png", true));
            JAPConfAnon.this.m_btnDataRetention.addMouseListener(jAPConfAnon2);
            ++gridBagConstraints4.gridx;
            JAPConfAnon.this.m_pnlMixInfoButtons.add((Component)JAPConfAnon.this.m_btnDataRetention, gridBagConstraints4);
            ++gridBagConstraints4.gridx;
            gridBagConstraints4.weightx = 1.0;
            jLabel = new JLabel("");
            JAPConfAnon.this.m_pnlMixInfoButtons.add((Component)jLabel, gridBagConstraints4);
            gridBagConstraints.gridx = 0;
            ++gridBagConstraints.gridy;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = 17;
            gridBagConstraints.fill = 0;
            this.add((Component)JAPConfAnon.this.m_pnlMixInfoButtons, gridBagConstraints);
        }
    }

    private class ServerPanel
    extends JPanel {
        private static final long serialVersionUID = 1L;
        private JLabel m_lblCascadeName;
        private JAPConfAnon m_listener;
        GridBagConstraints m_constraints;

        public ServerPanel(JAPConfAnon jAPConfAnon2) {
            this.m_listener = jAPConfAnon2;
            GridBagLayout gridBagLayout = new GridBagLayout();
            this.m_constraints = new GridBagConstraints();
            this.setLayout(gridBagLayout);
            this.m_constraints.gridx = 0;
            this.m_constraints.gridy = 0;
            this.m_constraints.anchor = 18;
            this.m_constraints.fill = 2;
            this.m_constraints.weightx = 1.0;
            this.m_constraints.weighty = 0.0;
            this.m_constraints.insets = new Insets(5, 10, 5, 5);
            this.m_constraints.gridy = 1;
            this.m_lblCascadeName = new JLabel();
            this.add((Component)new JLabel(), this.m_constraints);
            this.m_constraints.gridy = 2;
            this.m_constraints.insets = new Insets(2, 20, 2, 2);
        }

        public void setCascadeName(String string) {
            GUIUtils.trim(string);
            if (string == null || string.length() < 1) {
                string = " ";
            }
            this.m_lblCascadeName.setText(string);
        }

        public void updateServerList(int n, boolean bl, int n2) {
            if (JAPConfAnon.this.m_serverList != null && JAPConfAnon.this.m_serverList.areMixButtonsEnabled() == bl && JAPConfAnon.this.m_serverList.getNumberOfMixes() == n) {
                JAPConfAnon.this.m_serverList.setSelectedIndex(n2);
            } else {
                if (JAPConfAnon.this.m_serverList != null) {
                    this.remove(JAPConfAnon.this.m_serverList);
                    JAPConfAnon.this.m_serverList.removeItemListener(this.m_listener);
                    JAPConfAnon.this.m_serverList.setVisible(false);
                }
                JAPConfAnon.this.m_serverList = new ServerListPanel(n, bl, n2);
                JAPConfAnon.this.m_serverList.addItemListener(this.m_listener);
            }
            this.add((Component)JAPConfAnon.this.m_serverList, this.m_constraints);
        }
    }

    private class ManualPanel
    extends JPanel {
        private static final long serialVersionUID = 1L;

        public ManualPanel(JAPConfAnon jAPConfAnon2) {
            GridBagLayout gridBagLayout = new GridBagLayout();
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints.anchor = 18;
            this.setLayout(gridBagLayout);
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.fill = 0;
            JLabel jLabel = new JLabel(JAPMessages.getString("manualServiceAddHost"));
            gridBagConstraints.gridy = 1;
            this.add((Component)jLabel, gridBagConstraints);
            jLabel = new JLabel(JAPMessages.getString("manualServiceAddPort"));
            gridBagConstraints.gridy = 2;
            this.add((Component)jLabel, gridBagConstraints);
            JAPConfAnon.this.m_manHostField = new JTextField();
            gridBagConstraints.fill = 2;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 3;
            this.add((Component)JAPConfAnon.this.m_manHostField, gridBagConstraints);
            JAPConfAnon.this.m_manPortField = new JAPJIntField(65535);
            JAPConfAnon.this.m_manPortField.setMinimumSize(JAPConfAnon.this.m_manPortField.getPreferredSize());
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = 0;
            this.add((Component)JAPConfAnon.this.m_manPortField, gridBagConstraints);
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.fill = 0;
            gridBagConstraints.anchor = 12;
            JAPConfAnon.this.m_editCascadeButton = new JButton(JAPMessages.getString("okButton"));
            JAPConfAnon.this.m_editCascadeButton.addActionListener(jAPConfAnon2);
            gridBagConstraints.gridx = 1;
            this.add((Component)JAPConfAnon.this.m_editCascadeButton, gridBagConstraints);
            JAPConfAnon.this.m_cancelCascadeButton = new JButton(JAPMessages.getString(JAPConfAnon.MSG_FILTER_CANCEL));
            JAPConfAnon.this.m_cancelCascadeButton.addActionListener(jAPConfAnon2);
            gridBagConstraints.gridx = 2;
            this.add((Component)JAPConfAnon.this.m_cancelCascadeButton, gridBagConstraints);
            JAPConfAnon.this.m_manHostField.addKeyListener(jAPConfAnon2);
            JAPConfAnon.this.m_manPortField.addKeyListener(jAPConfAnon2);
        }

        public void setHostName(String string) {
            JAPConfAnon.this.m_manHostField.setText(string);
        }

        public void setPort(String string) {
            JAPConfAnon.this.m_manPortField.setText(string);
        }
    }

    final class TempCascade {
        private String m_id;
        private String m_ports;
        private String m_hosts;
        private int m_maxUsers;

        public TempCascade(String string, String string2, String string3, int n) {
            this.m_id = string;
            this.m_hosts = string2;
            this.m_ports = string3;
            this.m_maxUsers = n;
        }

        public int getMaxUsers() {
            return this.m_maxUsers;
        }

        public String getId() {
            return this.m_id;
        }

        public String getPorts() {
            return this.m_ports;
        }

        public String getHosts() {
            return this.m_hosts;
        }
    }

    private static final class InfoServiceTempLayer {
        private InfoServiceTempLayer() {
        }

        public static String getAnonLevel(String string) {
            StatusInfo statusInfo = InfoServiceTempLayer.getStatusInfo(string);
            if (statusInfo != null && statusInfo.getAnonLevel() >= 0) {
                return "" + statusInfo.getAnonLevel();
            }
            return "?";
        }

        public static String getNumOfUsers(MixCascade mixCascade) {
            StatusInfo statusInfo;
            if (mixCascade != null && (statusInfo = InfoServiceTempLayer.getStatusInfo(mixCascade.getId())) != null) {
                int n = 0;
                n = mixCascade.getMaxUsers();
                return "" + statusInfo.getNrOfActiveUsers() + (n != 0 ? " / " + n : "");
            }
            return "N/A";
        }

        public static boolean isUserLimitReached(MixCascade mixCascade) {
            int n;
            StatusInfo statusInfo;
            return mixCascade != null && (statusInfo = InfoServiceTempLayer.getStatusInfo(mixCascade.getId())) != null && (n = mixCascade.getMaxUsers()) > 0 && n - statusInfo.getNrOfActiveUsers() <= 3;
        }

        public static String getHosts(MixCascade mixCascade) {
            if (mixCascade == null || mixCascade.getHostsAsString() == null) {
                return "N/A";
            }
            return mixCascade.getHostsAsString();
        }

        public static String getPorts(MixCascade mixCascade) {
            if (mixCascade == null || mixCascade.getPortsAsString() == null) {
                return "N/A";
            }
            return mixCascade.getPortsAsString();
        }

        public static String getMixVersion(MixCascade mixCascade, String string) {
            ServiceSoftware serviceSoftware;
            MixInfo mixInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
            if (mixInfo != null && (serviceSoftware = mixInfo.getServiceSoftware()) != null) {
                return serviceSoftware.getVersion();
            }
            return null;
        }

        public static MultiCertPath getMixCertPath(MixCascade mixCascade, String string) {
            MixInfo mixInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
            MultiCertPath multiCertPath = null;
            if (mixInfo != null) {
                multiCertPath = mixInfo.getCertPath();
            }
            return multiCertPath;
        }

        public static String getEMail(MixCascade mixCascade, String string) {
            ServiceOperator serviceOperator;
            String string2 = null;
            MixInfo mixInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
            if (mixInfo != null && (serviceOperator = mixInfo.getServiceOperator()) != null) {
                string2 = serviceOperator.getEMail();
            }
            if (string2 == null || !AbstractX509AlternativeName.isValidEMail(string2)) {
                return "N/A";
            }
            return string2;
        }

        public static String getOperator(MixCascade mixCascade, String string) {
            ServiceOperator serviceOperator = InfoServiceTempLayer.getServiceOperator(mixCascade, string);
            String string2 = null;
            String string3 = null;
            if (serviceOperator != null) {
                string2 = serviceOperator.getOrganization();
            }
            if (string2 == null || string2.trim().length() == 0) {
                return "N/A";
            }
            string3 = serviceOperator.getCountryCode();
            if (string3 != null && string3.trim().length() > 0) {
                string2 = string2 + "  (";
                try {
                    string2 = string2 + new CountryMapper(string3, JAPMessages.getLocale()).toString();
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    string2 = string2 + string3.trim();
                }
                string2 = string2 + ")";
            }
            return string2;
        }

        public static String getUrl(MixCascade mixCascade, String string) {
            ServiceOperator serviceOperator = InfoServiceTempLayer.getServiceOperator(mixCascade, string);
            String string2 = null;
            if (serviceOperator != null) {
                string2 = serviceOperator.getUrl();
            }
            try {
                if (string2 != null && string2.toLowerCase().startsWith("https")) {
                    new URL("http" + string2.substring(5, string2.length()));
                } else {
                    new URL(string2);
                }
            }
            catch (MalformedURLException malformedURLException) {
                string2 = null;
            }
            if (string2 == null) {
                return "N/A";
            }
            return string2;
        }

        public static String getName(MixCascade mixCascade, String string) {
            MixInfo mixInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
            if (mixInfo == null) {
                return null;
            }
            String string2 = mixInfo.getName();
            if (string2 == null || string2.trim().length() == 0) {
                string2 = null;
            }
            return string2;
        }

        public static String getLocation(MixCascade mixCascade, String string) {
            ServiceLocation serviceLocation = InfoServiceTempLayer.getServiceLocation(mixCascade, string);
            if (serviceLocation != null) {
                return GUIUtils.getCountryFromServiceLocation(serviceLocation);
            }
            return "N/A";
        }

        public static boolean isPay(String string) {
            MixCascade mixCascade = InfoServiceTempLayer.getMixCascade(string);
            if (mixCascade != null) {
                return mixCascade.isPayment();
            }
            return false;
        }

        public static Vector getCoordinates(MixCascade mixCascade, String string) {
            ServiceLocation serviceLocation = InfoServiceTempLayer.getServiceLocation(mixCascade, string);
            if (serviceLocation == null || serviceLocation.getLatitude() == null || serviceLocation.getLongitude() == null) {
                return null;
            }
            try {
                Double.valueOf(serviceLocation.getLatitude());
                Double.valueOf(serviceLocation.getLongitude());
            }
            catch (NumberFormatException numberFormatException) {
                return null;
            }
            Vector<String> vector = new Vector<String>();
            vector.addElement(serviceLocation.getLatitude());
            vector.addElement(serviceLocation.getLongitude());
            return vector;
        }

        private static StatusInfo getStatusInfo(String string) {
            return (StatusInfo)Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPConfAnon.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).getEntryById(string);
        }

        private static MixCascade getMixCascade(String string) {
            return (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(string);
        }

        private static ServiceLocation getServiceLocation(MixCascade mixCascade, String string) {
            JAPCertificate jAPCertificate;
            CertPath certPath;
            MixInfo mixInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
            if (mixInfo != null) {
                return mixInfo.getServiceLocation();
            }
            MixCascade mixCascade2 = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(string);
            if (mixCascade2 != null && mixCascade2.getCertPath() != null && (certPath = mixCascade2.getCertPath().getPath()) != null && (jAPCertificate = certPath.getSecondCertificate()) != null) {
                return new ServiceLocation(null, jAPCertificate);
            }
            return null;
        }

        private static ServiceOperator getServiceOperator(MixCascade mixCascade, String string) {
            JAPCertificate jAPCertificate;
            CertPath certPath;
            MixInfo mixInfo = InfoServiceTempLayer.getMixInfo(mixCascade, string);
            if (mixInfo != null) {
                return mixInfo.getServiceOperator();
            }
            MixCascade mixCascade2 = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfAnon.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(string);
            if (mixCascade2 != null && mixCascade2.getCertPath() != null && (certPath = mixCascade2.getCertPath().getPath()) != null && (jAPCertificate = certPath.getSecondCertificate()) != null) {
                return (ServiceOperator)Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfAnon.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryById(jAPCertificate.getId());
            }
            return null;
        }

        private static MixInfo getMixInfo(MixCascade mixCascade, String string) {
            MixInfo mixInfo;
            MixInfo mixInfo2 = null;
            if (mixCascade == null || string == null) {
                return null;
            }
            mixInfo2 = mixCascade.getMixInfo(string);
            if ((mixInfo2 == null || mixInfo2.getVersionNumber() <= 0L) && (mixInfo = (MixInfo)Database.getInstance(class$anon$infoservice$MixInfo == null ? (class$anon$infoservice$MixInfo = JAPConfAnon.class$("anon.infoservice.MixInfo")) : class$anon$infoservice$MixInfo).getEntryById(string)) != null) {
                mixInfo2 = mixInfo;
            }
            if ((mixInfo2 == null || mixInfo2.getCertPath() == null) && mixCascade.getCertPath() != null) {
                mixInfo2 = new MixInfo(mixCascade.getCertPath());
            }
            return mixInfo2;
        }

        private static PerformanceEntry getPerformanceEntry(String string) {
            return PerformanceInfo.getLowestCommonBoundEntry(string);
        }
    }
}

