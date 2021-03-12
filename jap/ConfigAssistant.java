/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.AbstractAutoSwitchedMixCascadeContainer;
import anon.client.AnonClient;
import anon.client.TrustModel;
import anon.error.ServiceSignatureException;
import anon.error.ServiceUnreachableException;
import anon.error.TrustException;
import anon.infoservice.BlacklistedCascadeIDEntry;
import anon.infoservice.Database;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.infoservice.MixCascadeExitAddresses;
import anon.infoservice.MixInfo;
import anon.infoservice.ProxyInterface;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.platform.AbstractOS;
import anon.proxy.HTTPProxyCallback;
import anon.util.BooleanVariable;
import anon.util.IProgressCapsule;
import anon.util.IReturnRunnable;
import anon.util.IXMLEncodable;
import anon.util.JAPMessages;
import anon.util.LanguageMapper;
import anon.util.SocketGuard;
import anon.util.StringVariable;
import anon.util.Util;
import gui.ClipboardCopier;
import gui.GUIUtils;
import gui.JAPHelpContext;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.WorkerContentPane;
import gui.help.JAPHelp;
import jap.IJAPMainView;
import jap.JAPConstants;
import jap.JAPController;
import jap.JAPModel;
import jap.gui.LinkRegistrator;
import jap.pay.AccountCreator;
import jap.pay.PaymentInstancePanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Observable;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import logging.LogHolder;
import logging.LogType;

public class ConfigAssistant
extends JAPDialog {
    private static final String BROWSER_JONDOFOX = "JonDoFox";
    private static final String MSG_WELCOME_ONE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".welcomeOne";
    private static final String MSG_WELCOME_TWO = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".welcomeTwo";
    private static final String MSG_TITLE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_title";
    private static final String MSG_SUGGEST_PREMIUM_HEAD = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".suggestPremiumHead";
    private static final String MSG_SUGGEST_PREMIUM = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".suggestPremium";
    private static final String MSG_EXAMPLE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".example";
    private static final String MSG_FINISHED_ANONTEST = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_menuFinishAnontest";
    private static final String MSG_FINISHED_TESTED = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".finishedTested";
    private static final String MSG_FINISHED_TESTED_PROXY = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".finishedTestedProxy";
    private static final String MSG_FINISHED_TESTED_PREMIUM = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".finishedTestedPremium";
    private static final String MSG_BTN_RESTART = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".btnRestart";
    private static final String MSG_BROWSER_CONF = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_browserConf";
    private static final String MSG_MAKE_SELECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_makeSelection";
    private static final String MSG_ERROR_IP_CHECK_NOT_REQUESTED = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorIPCheckNotRequested";
    private static final String MSG_FAILED_TEST_CONNECT = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".failedTestConnect";
    private static final String MSG_FAILED_TEST_DOWN_OR_BLOCKED = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".failedTestDownOrBlocked";
    private static final String MSG_FAILED_TEST_MISCONFIGURED_BROWSER = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".failedTestMisconfiguredBrowser";
    private static final String MSG_FAILED_TEST_MISCONFIGURED_BROWSER_OR_NOT_USED = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".failedTestMisconfiguredBrowserOrNotUsed";
    private static final String MSG_BROWSER_RECOMMEND_JONDOFOX = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".browserRecommendJonDoFox";
    private static final String MSG_DOWNLOAD_JONDOFOX = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".downloadJonDoFox";
    private static final String[] MSG_TRANSLATE = new String[]{"Unknown", "NoInfoServiceData", "NoSwitchServiceNotTrusted", "InfoServiceUpdateFailed", "NoInternetConnection", "WrongProxySettings", "InfoserviceBlocked", "TimedOut", "Interrupted", "PaymentForcedButNoAccount", "BrokenService", "NotConnected", "MixServicesBlocked", "InfoserviceUnreachable", "InfoserviceAnonymousOnly", "SwitchFilter", "AntiCensorshipNotConnected", "NoConnectionRecommendProxy", "ServiceUnreachable", "PremiumServicesOnly"};
    private static final String MSG_EXPLAIN_FIREWALL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_explainFirewall";
    private static final String MSG_EXPLAIN_NO_CONNECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_explainNoConnection";
    private static final String MSG_EXPLAIN_BAD_CONNECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_explainBadConnection";
    private static final String MSG_EXPLAIN_NO_SERVICE_AVAILABLE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_explainNoServiceAvailable";
    private static final String MSG_HINT = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".hint";
    private static final String MSG_TRANSLATE_INFOSERVICE_TOO_FEW = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorInfoServiceTooFew";
    private static final String MSG_TRANSLATE_ALLOW_AUTO_CHANGE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorAllowAutoChange";
    private static final String MSG_TRANSLATE_CHANGE_SERVICE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorChangeService";
    private static final String MSG_TRANSLATE_ANTI_CENSORSHIP = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorAntiCensorship";
    private static final String MSG_TRANSLATE_REMOVE_FIREWALL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorRemoveFirewall";
    private static final String MSG_TRANSLATE_TRY_PREMIUM = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorTryPremium";
    private static final String MSG_TRANSLATE_TIMED_OUT_CHANGE_FILTER = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorTimedOutChangeFilter";
    private static final String MSG_TRANSLATE_INFOSERVICE_OUT_OF_ORDER = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorInfoserviceOutOfOrder";
    private static final String MSG_TRANSLATE_INFOSERVICE_HEAD = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorInfoserviceHead";
    private static final String MSG_CHECK_AUTO_OPEN = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".checkAutoOpen";
    private static final String MSG_ERROR_CODE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".errorCode";
    private static final String MSG_BROWSER_ANONYMITY_TEST = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".browserAnonymityTest";
    private static final String MSG_BROWSER_RESULT = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".browserResult";
    private static final String MSG_BROWSER_RED = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".browserRed";
    private static final String MSG_BROWSER_ANONYMOUS = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".browserAnonymous";
    private static final String MSG_BROWSER_NO_CONNECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".browserNoConnection";
    private static final String MSG_NO_CLUE_SUPPORT = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".noClueSupport";
    private static final String MSG_NO_CLUE_SUPPORT_EXPLAIN = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".noClueSupportExplain";
    private static final String MSG_NO_CLUE_SUPPORT_FORUM_URL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".noClueSupportForumURL";
    private static final String MSG_NO_CLUE_SUPPORT_E_MAIL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".noClueSupportEMail";
    private static final String MSG_NO_CLUE_SUPPORT_E_MAIL_LABEL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".noClueSupportEMailLabel";
    private static final String MSG_NO_CLUE_SUPPORT_FORUM_LABEL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".noClueSupportForumLabel";
    private static final String MSG_PREMIUM_ADVANTAGES = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".advantagesPremiumServices";
    private static final String MSG_TEST_SHOWS_YOUR_IP = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".yourIPAddress";
    private static final String MSG_URL = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".url";
    private static final String MSG_REALLY_CLOSE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_reallyClose";
    private static final String MSG_ERROR_NO_CONNECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_errorNoConnection";
    private static final String MSG_SELECT_VIEW = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_selectView";
    private static final String MSG_SET_NEW_VIEW = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_setNewView";
    private static final String MSG_SET_NEW_LANGUAGE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_setNewLanguage";
    private static final String MSG_EXPLAIN_RESTART = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_explainRestart";
    private static final String MSG_CREATING_CONNECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".creatingConnection";
    private static final String MSG_STATUS_FETCH_FROM_INFOSERVICE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusFetchFromInfoService";
    private static final String MSG_STATUS_CONNECTING_INTERNATIONAL_THREE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusConnectingInternationalThree";
    private static final String MSG_STATUS_CONNECTING_MIX_THREE = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusConnectingMixThree";
    private static final String MSG_STATUS_CONNECTING_INTERNATIONAL_TWO = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusConnectingInternationalTwo";
    private static final String MSG_STATUS_CONNECTING_MIX_TWO = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusConnectingMixTwo";
    private static final String MSG_STATUS_CONNECTING_MIX_ONE_ONLY = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusConnectingMixOneOnly";
    private static final String MSG_STATUS_CONNECTING_ALL_SERVICES = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusConnectingAllServices";
    private static final String MSG_STATUS_TESTING_CONNECTIVITY = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusTestingConnectivity";
    private static final String MSG_STATUS_TESTING_ANONYMOUS_CONNECTION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".statusTestingAnonymousConnection";
    private static final String MSG_PANE_BEFORE_START = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".paneBeforeStart";
    private static final String MSG_HEAD_BEFORE_START = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".headBeforeStart";
    private static final String MSG_PAY_INFORMATION = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".payInformation";
    private static final String[] PROXIES = new String[]{"HTTP(S)", "SSL/FTP"};
    private static final String IMG_HELP_BUTTON = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_en_help.gif";
    private static final String IMG_SERVICES = (class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + "_services{0}.gif";
    public static final int COMPONENT_STARTUP = 0;
    public static final int COMPONENT_INFOSERVICE_TEST = 1;
    public static final int COMPONENT_ANONYMITY_TEST = 2;
    public static final int COMPONENT_DEFAULT = 3;
    public static final int COMPONENT_CONNECTION_TEST = 4;
    private JTextPane[] m_lblHostnames = new JTextPane[PROXIES.length];
    private JTextPane[] m_lblPorts = new JTextPane[PROXIES.length];
    private JTextPane m_txtHostNameSystemProxy;
    private JTextPane m_txtPortSystemProxy;
    private ClipboardCopier m_textCopier = new ClipboardCopier(false);
    private ButtonGroup m_groupBrowserAnonymity;
    private JRadioButton m_radioBrowserIPUncovered;
    private JRadioButton m_radioBrowserRed;
    private JRadioButton m_radioBrowserAnonymous;
    private JRadioButton m_radioBrowserNoConnection;
    private JRadioButton m_radioBrowserNoClue;
    private JRadioButton m_radioSimpleView;
    private JRadioButton m_radioAdvancedView;
    private ButtonGroup m_groupView;
    private JComboBox comboLang;
    private final BooleanVariable SYNC_STARTED = new BooleanVariable(false);
    private LinkRegistrator m_registrator;
    static /* synthetic */ Class class$jap$ConfigAssistant;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;
    static /* synthetic */ Class class$java$awt$Component;

    public ConfigAssistant(Component component, IJAPMainView iJAPMainView, int n) {
        super(component, JAPMessages.getString(MSG_TITLE) + " - " + JAPModel.getInstance().getProgramName(), false);
        this.m_registrator = new LinkRegistrator(this.getRootPane(), iJAPMainView);
        this.init(n);
    }

    public BooleanVariable getSyncStarted() {
        return this.SYNC_STARTED;
    }

    private void init(final int n) {
        BooleanVariable booleanVariable;
        final Locale locale = JAPMessages.getLocale();
        ConfigAssistant configAssistant = this;
        final Insets insets = new Insets(0, 0, 0, 5);
        ImageIcon imageIcon = GUIUtils.loadImageIcon("install.gif");
        DialogContentPane.Layout layout = new DialogContentPane.Layout(imageIcon);
        final RunConnectionCreator runConnectionCreator = new RunConnectionCreator(n);
        SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(this, JAPMessages.getString(MSG_WELCOME_TWO), layout, new DialogContentPaneOptions("index")){

            private boolean isSkipped() {
                return n == 3 || n == 4;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return this.isSkipped();
            }

            public boolean isSkippedAsNextContentPane() {
                return this.isSkipped();
            }

            public String getButtonCancelText() {
                return JAPMessages.getString(DialogContentPane.MSG_CANCEL);
            }

            public DialogContentPane.CheckError checkUpdate() {
                JAPController.getInstance().blockDirectProxy(true);
                JAPMessages.init(((LanguageMapper)ConfigAssistant.this.comboLang.getSelectedItem()).getLocale(), "JAPMessages");
                this.setText(JAPMessages.getString(MSG_WELCOME_TWO));
                ConfigAssistant.this.m_radioSimpleView.setText(JAPMessages.getString("ngSettingsViewSimplified"));
                ConfigAssistant.this.m_radioAdvancedView.setText(JAPMessages.getString("ngSettingsViewNormal"));
                runConnectionCreator.reset();
                return super.checkUpdate();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public DialogContentPane.CheckError checkYesOK() {
                DialogContentPane.CheckError checkError = super.checkYesOK();
                if (ConfigAssistant.this.m_groupView.getSelection() == null) {
                    return new DialogContentPane.CheckError(JAPMessages.getString(MSG_MAKE_SELECTION));
                }
                if (checkError == null) {
                    BooleanVariable booleanVariable = ConfigAssistant.this.SYNC_STARTED;
                    synchronized (booleanVariable) {
                        ConfigAssistant.this.SYNC_STARTED.set(true);
                    }
                }
                return checkError;
            }
        };
        simpleWizardContentPane.setDefaultButtonOperation(65800);
        JComponent jComponent = simpleWizardContentPane.getContentPane();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        jComponent.setLayout(new GridBagLayout());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        JLabel jLabel = new JLabel("line");
        jLabel.setForeground(jComponent.getBackground());
        jComponent.add((Component)jLabel, gridBagConstraints);
        ++gridBagConstraints.gridy;
        jLabel = new JLabel(JAPMessages.getString("settingsLanguage"));
        jComponent.add((Component)jLabel, gridBagConstraints);
        this.comboLang = new JComboBox();
        String[] arrstring = JAPConstants.getSupportedLanguages();
        for (int i = 0; i < arrstring.length; ++i) {
            this.comboLang.addItem(new LanguageMapper(arrstring[i], new Locale(arrstring[i], "")));
        }
        this.comboLang.setSelectedItem(new LanguageMapper(JAPMessages.getLocale().getLanguage()));
        ++gridBagConstraints.gridx;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        jComponent.add((Component)this.comboLang, gridBagConstraints);
        gridBagConstraints.gridwidth = 2;
        this.m_radioSimpleView = new JRadioButton(JAPMessages.getString("ngSettingsViewSimplified"));
        this.m_radioAdvancedView = new JRadioButton(JAPMessages.getString("ngSettingsViewNormal"));
        if (JAPModel.getDefaultView() == 1) {
            this.m_radioAdvancedView.setSelected(true);
        } else {
            this.m_radioSimpleView.setSelected(true);
        }
        this.m_groupView = new ButtonGroup();
        this.m_groupView.add(this.m_radioSimpleView);
        this.m_groupView.add(this.m_radioAdvancedView);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        jComponent.add((Component)this.m_radioSimpleView, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        jComponent.add((Component)this.m_radioAdvancedView, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 1.0;
        jComponent.add((Component)new JLabel(), gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        final SimpleWizardContentPane simpleWizardContentPane2 = new SimpleWizardContentPane(this, JAPMessages.getString(MSG_SET_NEW_LANGUAGE) + "<br><br>" + JAPMessages.getString(MSG_SET_NEW_VIEW, "ngSettingsViewNormal") + "<br><br>" + JAPMessages.getString(MSG_EXPLAIN_RESTART), layout, simpleWizardContentPane){

            public String getButtonYesOKText() {
                return JAPMessages.getString(MSG_BTN_RESTART);
            }

            public String getButtonCancelText() {
                return JAPMessages.getString(DialogContentPane.MSG_CANCEL);
            }

            public DialogContentPane.CheckError checkUpdate() {
                String string = "";
                JAPMessages.init(((LanguageMapper)ConfigAssistant.this.comboLang.getSelectedItem()).getLocale(), "JAPMessages");
                if (!((LanguageMapper)ConfigAssistant.this.comboLang.getSelectedItem()).getLocale().equals(locale)) {
                    string = JAPMessages.getString(MSG_SET_NEW_LANGUAGE);
                }
                if (ConfigAssistant.this.m_radioSimpleView.isSelected() && JAPModel.getDefaultView() == 1 || ConfigAssistant.this.m_radioAdvancedView.isSelected() && JAPModel.getDefaultView() == 2) {
                    String string2 = ConfigAssistant.this.m_radioSimpleView.isSelected() ? JAPMessages.getString("ngSettingsViewSimplified") : JAPMessages.getString("ngSettingsViewNormal");
                    string = string + " " + JAPMessages.getString(MSG_SET_NEW_VIEW, string2);
                }
                this.setText(string + "<br><br>" + JAPMessages.getString(MSG_EXPLAIN_RESTART));
                return super.checkUpdate();
            }

            public boolean isSkippedAsNextContentPane() {
                if (n == 3 || n == 4) {
                    return true;
                }
                return ((LanguageMapper)ConfigAssistant.this.comboLang.getSelectedItem()).getLocale().equals(locale) && (ConfigAssistant.this.m_radioSimpleView.isSelected() && JAPModel.getDefaultView() == 2 || ConfigAssistant.this.m_radioAdvancedView.isSelected() && JAPModel.getDefaultView() == 1);
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        simpleWizardContentPane2.addComponentListener(new ComponentAdapter(){

            public void componentShown(ComponentEvent componentEvent) {
                JAPMessages.init(locale, "JAPMessages");
            }
        });
        simpleWizardContentPane2.setDefaultButtonOperation(73984);
        DialogContentPane dialogContentPane = AccountCreator.createAccountPanes(this, layout, simpleWizardContentPane2, "<b>" + JAPMessages.getString(MSG_HEAD_BEFORE_START) + "</b><br/><br/>" + JAPMessages.getString(MSG_PANE_BEFORE_START), this.m_registrator, runConnectionCreator, 0, this.SYNC_STARTED);
        DialogContentPane dialogContentPane2 = n == 1 ? null : dialogContentPane;
        final DialogContentPane dialogContentPane3 = dialogContentPane2;
        WorkerContentPane workerContentPane = new WorkerContentPane(this, JAPMessages.getString(MSG_STATUS_TESTING_ANONYMOUS_CONNECTION), layout, dialogContentPane3, runConnectionCreator, runConnectionCreator){

            public boolean hideButtonCancel() {
                return false;
            }

            public DialogContentPane.CheckError checkUpdate() {
                JAPController.getInstance().blockDirectProxy(true);
                return super.checkUpdate();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return dialogContentPane3 != null;
            }

            public boolean isMoveForwardAllowed() {
                return simpleWizardContentPane2.isSkippedAsNextContentPane();
            }
        };
        if (n != 1 && n != 4) {
            workerContentPane.setDefaultButtonOperation(776);
        } else {
            workerContentPane.setDefaultButtonOperation(33034);
        }
        MouseAdapter mouseAdapter = new MouseAdapter(){
            final Object SYNC = new Object();
            final 1ImagePanel pnlBrowserAnonymous;
            1ImagePanel lblBrowserIPUncovered;
            1ImagePanel lblBrowserRed;
            Object popBrowserIPUncovered;
            Object popBrowserRed;
            Object popBrowserAnonymous;
            {
                class 1ImagePanel
                extends JPanel {
                    private Image img;
                    private Dimension m_size;
                    private static final int FONT_HEIGHT = 100;

                    public 1ImagePanel(Image image) {
                        this.img = image;
                        this.m_size = new Dimension(image.getWidth(null), image.getHeight(null));
                        this.setPreferredSize(this.m_size);
                        this.setMinimumSize(this.m_size);
                        this.setMaximumSize(this.m_size);
                        this.setSize(this.m_size);
                        this.setLayout(null);
                    }

                    public void paintComponent(Graphics graphics) {
                        super.paintComponent(graphics);
                        Graphics2D graphics2D = (Graphics2D)graphics;
                        graphics2D.setPaint(Color.black);
                        graphics2D.drawImage(this.img, 0, 0, null);
                        Font font = new Font("Sans-Serif", 1, 100);
                        graphics2D.setFont(font);
                        graphics2D.drawString(JAPMessages.getString(MSG_EXAMPLE), 25, this.m_size.height / 2 + 50);
                    }
                }
                this.pnlBrowserAnonymous = new 1ImagePanel(GUIUtils.loadImageIcon("AnonymityTestGood.png").getImage());
                this.lblBrowserIPUncovered = new 1ImagePanel(GUIUtils.loadImageIcon("AnonymityTestUncovered_02.gif").getImage());
                this.lblBrowserRed = new 1ImagePanel(GUIUtils.loadImageIcon("AnonymityTestBad.png").getImage());
            }

            private Object showPopup(JRadioButton jRadioButton, Component component) {
                Point point = jRadioButton.getLocationOnScreen();
                point = new Point(point.x + jRadioButton.getWidth(), point.y - component.getPreferredSize().height / 2);
                Object object = null;
                try {
                    Class<?> class_ = Class.forName("javax.swing.PopupFactory");
                    Object object2 = class_.getMethod("getSharedInstance", null).invoke(class_, null);
                    object = class_.getMethod("getPopup", class$java$awt$Component == null ? (class$java$awt$Component = ConfigAssistant.class$("java.awt.Component")) : class$java$awt$Component, class$java$awt$Component == null ? (class$java$awt$Component = ConfigAssistant.class$("java.awt.Component")) : class$java$awt$Component, Integer.TYPE, Integer.TYPE).invoke(object2, jRadioButton, component, new Integer(point.x), new Integer(point.y));
                    Class<?> class_2 = Class.forName("javax.swing.Popup");
                    class_2.getMethod("show", null).invoke(object, null);
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.GUI, exception);
                }
                return object;
            }

            private void hide(Object object) {
                try {
                    Class<?> class_ = Class.forName("javax.swing.Popup");
                    class_.getMethod("hide", null).invoke(object, null);
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.GUI, exception);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseEntered(MouseEvent mouseEvent) {
                Object object = this.SYNC;
                synchronized (object) {
                    if (mouseEvent.getSource() == ConfigAssistant.this.m_radioBrowserIPUncovered && this.popBrowserIPUncovered == null) {
                        this.popBrowserIPUncovered = this.showPopup(ConfigAssistant.this.m_radioBrowserIPUncovered, this.lblBrowserIPUncovered);
                    } else if (mouseEvent.getSource() == ConfigAssistant.this.m_radioBrowserRed && this.popBrowserRed == null) {
                        this.popBrowserRed = this.showPopup(ConfigAssistant.this.m_radioBrowserRed, this.lblBrowserRed);
                    } else if (mouseEvent.getSource() == ConfigAssistant.this.m_radioBrowserAnonymous && this.popBrowserAnonymous == null) {
                        this.popBrowserAnonymous = this.showPopup(ConfigAssistant.this.m_radioBrowserAnonymous, this.pnlBrowserAnonymous);
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseExited(MouseEvent mouseEvent) {
                Object object = this.SYNC;
                synchronized (object) {
                    if (mouseEvent.getSource() == ConfigAssistant.this.m_radioBrowserIPUncovered && this.popBrowserIPUncovered != null) {
                        this.hide(this.popBrowserIPUncovered);
                        this.popBrowserIPUncovered = null;
                    } else if (mouseEvent.getSource() == ConfigAssistant.this.m_radioBrowserRed && this.popBrowserRed != null) {
                        this.hide(this.popBrowserRed);
                        this.popBrowserRed = null;
                    } else if (mouseEvent.getSource() == ConfigAssistant.this.m_radioBrowserAnonymous && this.popBrowserAnonymous != null) {
                        this.hide(this.popBrowserAnonymous);
                        this.popBrowserAnonymous = null;
                    }
                }
            }
        };
        DialogContentPane.Layout layout2 = new DialogContentPane.Layout(GUIUtils.loadImageIcon("jap.ConfigAssistant_brokenCable.jpg"));
        final StringVariable stringVariable = new StringVariable("index");
        JAPHelpContext.IHelpContext iHelpContext = new JAPHelpContext.IHelpContext(){

            public String getHelpContext() {
                return stringVariable.get();
            }

            public Component getHelpExtractionDisplayContext() {
                return ConfigAssistant.this.getParentComponent();
            }
        };
        SimpleWizardContentPane simpleWizardContentPane3 = new SimpleWizardContentPane(this, JAPMessages.getString(MSG_ERROR_NO_CONNECTION), layout2, new DialogContentPaneOptions(iHelpContext, (DialogContentPane)workerContentPane)){

            public boolean isSkippedAsNextContentPane() {
                return runConnectionCreator.getErrorCode() == 0;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }

            public boolean hideButtonCancel() {
                return false;
            }

            public DialogContentPane.CheckError checkUpdate() {
                ProxyInterface proxyInterface;
                JAPController.getInstance().blockDirectProxy(false);
                runConnectionCreator.reset();
                RunConnectionCreator.TranslatedErrorCode translatedErrorCode = runConnectionCreator.translateErrorCode();
                stringVariable.set(translatedErrorCode.getHelpContext());
                this.setText(translatedErrorCode.getMessage());
                JComponent jComponent = this.getContentPane();
                jComponent.removeAll();
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                jComponent.setLayout(new GridBagLayout());
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = -1;
                if (translatedErrorCode.getConfigurationPage() != null) {
                    gridBagConstraints.anchor = 17;
                    gridBagConstraints.insets = insets;
                    ConfigAssistant.this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString("settingsDialog") + "...", translatedErrorCode.getConfigurationPage(), false, 2);
                }
                if (translatedErrorCode.getAdditionalHelpContext() != null && translatedErrorCode.getAdditionalHelpContextText() != null) {
                    gridBagConstraints.anchor = 17;
                    gridBagConstraints.insets = insets;
                    ConfigAssistant.this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, translatedErrorCode.getAdditionalHelpContextText() + "...", translatedErrorCode.getAdditionalHelpContext(), false, 0);
                }
                if (translatedErrorCode.getURL() != null) {
                    gridBagConstraints.anchor = 17;
                    gridBagConstraints.insets = insets;
                    ConfigAssistant.this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString(MSG_URL) + "...", translatedErrorCode.getURL(), false, 1);
                }
                if ((proxyInterface = AbstractOS.getInstance().getProxyInterface(null)) != null && runConnectionCreator.getErrorCode() == 17) {
                    gridBagConstraints.gridx = 0;
                    ++gridBagConstraints.gridy;
                    jComponent.add((Component)new JLabel(" "), gridBagConstraints);
                    ConfigAssistant.this.m_txtHostNameSystemProxy = GUIUtils.createSelectableAndResizeableLabel(jComponent);
                    ConfigAssistant.this.m_txtPortSystemProxy = GUIUtils.createSelectableAndResizeableLabel(jComponent);
                    ConfigAssistant.this.addListenerInterfaceLabels(this.getContentPane(), gridBagConstraints, insets, ConfigAssistant.this.m_txtHostNameSystemProxy, ConfigAssistant.this.m_txtPortSystemProxy, 0, proxyInterface.getProtocol() == 3 ? "SOCKS" : "HTTP");
                    ConfigAssistant.this.m_txtHostNameSystemProxy.setText(proxyInterface.getHost());
                    ConfigAssistant.this.m_txtPortSystemProxy.setText("" + proxyInterface.getPort());
                }
                return null;
            }
        };
        simpleWizardContentPane3.setDefaultButtonOperation(65920);
        Object object = null;
        if (n != 1 && n != 4) {
            booleanVariable = new BooleanVariable(true);
            SimpleWizardContentPane simpleWizardContentPane4 = new SimpleWizardContentPane(this, JAPMessages.getString(MSG_BROWSER_ANONYMITY_TEST), layout, new DialogContentPaneOptions("security_test", (DialogContentPane)simpleWizardContentPane3)){

                public DialogContentPane.CheckError checkUpdate() {
                    runConnectionCreator.reset();
                    JAPController.getInstance().blockDirectProxy(false);
                    JAPController.getInstance().forceAnonymityTestRedirect(true);
                    booleanVariable.set(true);
                    return null;
                }

                public boolean isSkippedAsPreviousContentPane() {
                    return !booleanVariable.isTrue();
                }

                public DialogContentPane.CheckError checkNo() {
                    JAPController.getInstance().forceAnonymityTestRedirect(false);
                    return null;
                }

                public DialogContentPane.CheckError checkYesOK() {
                    DialogContentPane.CheckError checkError = null;
                    if (ConfigAssistant.this.m_groupBrowserAnonymity.getSelection() == null) {
                        checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_MAKE_SELECTION));
                    } else if (ConfigAssistant.this.m_radioBrowserAnonymous.isSelected() && JAPModel.getInstance().isAnonymizedHttpHeaders() && !JAPController.getInstance().hasAnonymityTestRedirected()) {
                        checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_ERROR_IP_CHECK_NOT_REQUESTED));
                    }
                    if (checkError == null) {
                        JAPController.getInstance().forceAnonymityTestRedirect(false);
                    }
                    return checkError;
                }
            };
            simpleWizardContentPane4.setDefaultButtonOperation(65800);
            jComponent = simpleWizardContentPane4.getContentPane();
            jComponent.setLayout(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = -1;
            gridBagConstraints.anchor = 17;
            this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString(MSG_FINISHED_ANONTEST), JAPMessages.getString(HTTPProxyCallback.MSG_URL_ANONYMITY_TEST), false, 1);
            ++gridBagConstraints.gridy;
            jComponent.add((Component)new JLabel(" "), gridBagConstraints);
            ++gridBagConstraints.gridy;
            jComponent.add((Component)new JLabel(JAPMessages.getString(MSG_BROWSER_RESULT)), gridBagConstraints);
            ++gridBagConstraints.gridy;
            this.m_radioBrowserIPUncovered = new JRadioButton(JAPMessages.getString(MSG_TEST_SHOWS_YOUR_IP));
            this.m_radioBrowserIPUncovered.addMouseListener(mouseAdapter);
            this.m_radioBrowserRed = new JRadioButton(JAPMessages.getString(MSG_BROWSER_RED));
            this.m_radioBrowserRed.addMouseListener(mouseAdapter);
            ++gridBagConstraints.gridy;
            jComponent.add((Component)this.m_radioBrowserRed, gridBagConstraints);
            this.m_radioBrowserAnonymous = new JRadioButton(JAPMessages.getString(MSG_BROWSER_ANONYMOUS));
            this.m_radioBrowserAnonymous.addMouseListener(mouseAdapter);
            ++gridBagConstraints.gridy;
            jComponent.add((Component)this.m_radioBrowserAnonymous, gridBagConstraints);
            this.m_radioBrowserNoConnection = new JRadioButton(JAPMessages.getString(MSG_BROWSER_NO_CONNECTION));
            this.m_radioBrowserNoConnection.addMouseListener(mouseAdapter);
            ++gridBagConstraints.gridy;
            jComponent.add((Component)this.m_radioBrowserNoConnection, gridBagConstraints);
            this.m_radioBrowserNoClue = new JRadioButton(JAPMessages.getString(MSG_NO_CLUE_SUPPORT));
            this.m_radioBrowserNoClue.addMouseListener(mouseAdapter);
            ++gridBagConstraints.gridy;
            jComponent.add((Component)this.m_radioBrowserNoClue, gridBagConstraints);
            this.m_groupBrowserAnonymity = new ButtonGroup();
            this.m_groupBrowserAnonymity.add(this.m_radioBrowserIPUncovered);
            this.m_groupBrowserAnonymity.add(this.m_radioBrowserRed);
            this.m_groupBrowserAnonymity.add(this.m_radioBrowserAnonymous);
            this.m_groupBrowserAnonymity.add(this.m_radioBrowserNoConnection);
            this.m_groupBrowserAnonymity.add(this.m_radioBrowserNoClue);
            final WorkerContentPane workerContentPane2 = new WorkerContentPane(this, JAPMessages.getString(MSG_STATUS_TESTING_ANONYMOUS_CONNECTION), layout, simpleWizardContentPane4, new WorkerContentPane.IResettableReturnRunnable(){
                Integer value;

                public void run() {
                    try {
                        this.value = null;
                        runConnectionCreator.checkAnonymousConnection(true);
                        this.value = new Integer(11);
                    }
                    catch (InterruptedException interruptedException) {
                        this.value = new Integer(8);
                    }
                    catch (RunConnectionCreator.TimeoutException timeoutException) {
                        this.value = new Integer(10);
                    }
                    catch (RunConnectionCreator.BrokenServiceException brokenServiceException) {
                        this.value = new Integer(10);
                    }
                    catch (RunConnectionCreator.ConnectedException connectedException) {
                        this.value = null;
                    }
                }

                public void reset() {
                    this.value = null;
                }

                public Object getValue() {
                    return this.value;
                }
            }, runConnectionCreator){

                public boolean isSkippedAsNextContentPane() {
                    return !ConfigAssistant.this.m_radioBrowserNoConnection.isSelected();
                }
            };
            workerContentPane2.setDefaultButtonOperation(776);
            final WorkerContentPane workerContentPane3 = new WorkerContentPane(this, JAPMessages.getString(MSG_STATUS_TESTING_CONNECTIVITY), layout, workerContentPane2, new WorkerContentPane.IResettableReturnRunnable(){
                Object value;

                public void run() {
                    try {
                        this.value = workerContentPane2.getValue();
                        int n = runConnectionCreator.checkInternetConnection(true);
                        if (n != 0) {
                            this.value = new Integer(n);
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        this.value = new Integer(8);
                    }
                    catch (RunConnectionCreator.TimeoutException timeoutException) {
                        this.value = new Integer(4);
                    }
                }

                public void reset() {
                    this.value = null;
                }

                public Object getValue() {
                    return this.value;
                }
            }, runConnectionCreator){

                public boolean isSkippedAsNextContentPane() {
                    return !ConfigAssistant.this.m_radioBrowserNoConnection.isSelected() || workerContentPane2.getValue() == null;
                }
            };
            workerContentPane3.setDefaultButtonOperation(776);
            final String string = "<b>" + JAPMessages.getString(MSG_FAILED_TEST_MISCONFIGURED_BROWSER_OR_NOT_USED) + "</b>" + "<br/><br/>" + JAPMessages.getString(MSG_BROWSER_CONF) + " <i>" + JAPMessages.getString(MSG_BROWSER_RECOMMEND_JONDOFOX) + "</i>";
            final StringVariable stringVariable2 = new StringVariable();
            JAPHelpContext.IHelpContext iHelpContext2 = new JAPHelpContext.IHelpContext(){

                public String getHelpContext() {
                    return stringVariable2.get();
                }

                public Component getHelpExtractionDisplayContext() {
                    return ConfigAssistant.this.getParentComponent();
                }
            };
            final BooleanVariable booleanVariable2 = new BooleanVariable(false);
            SimpleWizardContentPane simpleWizardContentPane5 = new SimpleWizardContentPane(this, string, layout, new DialogContentPaneOptions(iHelpContext2, (DialogContentPane)workerContentPane3)){

                public boolean hideButtonYesOK() {
                    return booleanVariable2 == null || booleanVariable2.isTrue();
                }

                public DialogContentPane.CheckError checkUpdate() {
                    String string2;
                    Object object = workerContentPane3.getValue();
                    DialogContentPane.CheckError checkError = super.checkUpdate();
                    boolean bl = false;
                    for (int i = 0; i < ConfigAssistant.this.m_lblPorts.length; ++i) {
                        if (ConfigAssistant.this.m_lblPorts[i] == null) continue;
                        ConfigAssistant.this.m_lblPorts[i].setText("" + JAPModel.getHttpListenerPortNumber());
                    }
                    stringVariable2.set(null);
                    if (ConfigAssistant.this.m_radioBrowserNoConnection.isSelected()) {
                        if (object == null) {
                            booleanVariable2.set(true);
                            if (JAPModel.getInstance().isAnonymizedHttpHeaders() && JAPController.getInstance().hasAnonymityTestRedirected()) {
                                checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_FAILED_TEST_DOWN_OR_BLOCKED));
                                string2 = null;
                            } else {
                                string2 = string;
                                bl = true;
                                stringVariable2.set("browser");
                            }
                        } else {
                            booleanVariable2.set(false);
                            booleanVariable.set(false);
                            RunConnectionCreator.TranslatedErrorCode translatedErrorCode = runConnectionCreator.translateErrorCode((Integer)object);
                            string2 = translatedErrorCode.getMessage();
                            string2 = string2 + "<br/><br/>" + JAPMessages.getString(MSG_FAILED_TEST_CONNECT);
                            stringVariable2.set(translatedErrorCode.getHelpContext());
                        }
                    } else {
                        booleanVariable2.set(true);
                        string2 = "<b>" + JAPMessages.getString(MSG_FAILED_TEST_MISCONFIGURED_BROWSER) + "</b>";
                        string2 = string2 + "<br/><br/>" + JAPMessages.getString(MSG_BROWSER_CONF) + " <i>" + JAPMessages.getString(MSG_BROWSER_RECOMMEND_JONDOFOX) + "</i>";
                        bl = true;
                        stringVariable2.set("jondofox");
                    }
                    this.setText("");
                    if (bl) {
                        this.getContentPane().setVisible(true);
                    } else {
                        this.getContentPane().setVisible(false);
                    }
                    if (string2 != null) {
                        this.setText(string2);
                    }
                    workerContentPane3.reset();
                    workerContentPane2.reset();
                    return checkError;
                }

                public boolean isSkippedAsNextContentPane() {
                    return ConfigAssistant.this.m_radioBrowserAnonymous.isSelected() || ConfigAssistant.this.m_radioBrowserNoClue.isSelected();
                }

                public boolean isSkippedAsPreviousContentPane() {
                    return true;
                }
            };
            simpleWizardContentPane5.setDefaultButtonOperation(65920);
            jComponent = simpleWizardContentPane5.getContentPane();
            jComponent.setLayout(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = 17;
            gridBagConstraints.insets = insets;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = 2;
            for (int i = 0; i < PROXIES.length; ++i) {
                ++gridBagConstraints.gridy;
                gridBagConstraints.gridx = 5;
                this.addProxyInfo(jComponent, gridBagConstraints, PROXIES[i]);
                ++gridBagConstraints.gridy;
                gridBagConstraints.gridx = 5;
                this.m_lblHostnames[i] = GUIUtils.createSelectableAndResizeableLabel(jComponent);
                this.m_lblHostnames[i].setText("localhost");
                this.m_textCopier.register(this.m_lblHostnames[i]);
                this.m_lblHostnames[i].setBackground(Color.white);
                jComponent.add((Component)this.m_lblHostnames[i], gridBagConstraints);
                ++gridBagConstraints.gridx;
                jComponent.add((Component)new JLabel(":"), gridBagConstraints);
                ++gridBagConstraints.gridx;
                this.m_lblPorts[i] = GUIUtils.createSelectableAndResizeableLabel(jComponent);
                this.m_lblPorts[i].setText("65535");
                this.m_textCopier.register(this.m_lblPorts[i]);
                this.m_lblPorts[i].setBackground(Color.white);
                jComponent.add((Component)this.m_lblPorts[i], gridBagConstraints);
                ++gridBagConstraints.gridy;
            }
            gridBagConstraints.gridy = 0;
            this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, BROWSER_JONDOFOX, JAPMessages.getString(MSG_DOWNLOAD_JONDOFOX), true, 1);
            SimpleWizardContentPane simpleWizardContentPane6 = new SimpleWizardContentPane(this, JAPMessages.getString(MSG_NO_CLUE_SUPPORT_EXPLAIN), layout, new DialogContentPaneOptions("trouble", (DialogContentPane)simpleWizardContentPane5)){

                public boolean isSkippedAsNextContentPane() {
                    return !ConfigAssistant.this.m_radioBrowserNoClue.isSelected();
                }

                public boolean isSkippedAsPreviousContentPane() {
                    return true;
                }

                public boolean hideButtonYesOK() {
                    return true;
                }
            };
            simpleWizardContentPane6.setDefaultButtonOperation(65920);
            jComponent = simpleWizardContentPane6.getContentPane();
            jComponent.setLayout(new GridBagLayout());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = -1;
            gridBagConstraints.anchor = 17;
            this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString(JAPHelp.MSG_HELP_BUTTON), "trouble", false, 0);
            this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString(MSG_NO_CLUE_SUPPORT_FORUM_LABEL), JAPMessages.getString(MSG_NO_CLUE_SUPPORT_FORUM_URL), false, 1);
            this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString(MSG_NO_CLUE_SUPPORT_E_MAIL_LABEL), JAPMessages.getString(MSG_NO_CLUE_SUPPORT_E_MAIL), false, 3);
            if (JAPModel.getDefaultView() != 2) {
                this.m_registrator.addBrowserInstallationInfo(jComponent, gridBagConstraints, JAPMessages.getString("settingsDialog") + "...", "CONF_DEBUG", false, 2);
            }
            final String string2 = "<b><font color=\"green\">" + JAPMessages.getString(MSG_FINISHED_TESTED) + "</font></b><br/><br/>";
            JAPHelpContext.IHelpContext iHelpContext3 = new JAPHelpContext.IHelpContext(){

                public String getHelpContext() {
                    if (JAPController.getInstance().getCurrentMixCascade().isPayment()) {
                        return "otherApplications";
                    }
                    return "payment";
                }

                public Component getHelpExtractionDisplayContext() {
                    return null;
                }
            };
            object = new SimpleWizardContentPane(this, string2, layout, new DialogContentPaneOptions(iHelpContext3, (DialogContentPane)simpleWizardContentPane6)){

                public DialogContentPane.CheckError checkUpdate() {
                    this.getContentPane().removeAll();
                    GridBagConstraints gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.gridy = 0;
                    if (JAPController.getInstance().getCurrentMixCascade().isPayment()) {
                        this.setText(string2 + JAPMessages.getString(MSG_FINISHED_TESTED_PROXY, "'<i>" + JAPMessages.getString("ngActivity") + "</i>'"));
                        ConfigAssistant.this.m_registrator.addBrowserInstallationInfo(this.getContentPane(), gridBagConstraints, JAPMessages.getString(JAPHelp.MSG_HELP_BUTTON), "otherApplications", false, 0);
                    } else {
                        this.setText(string2 + JAPMessages.getString(MSG_FINISHED_TESTED_PREMIUM));
                        Vector vector = AccountCreator.getCurrentPaymentInstances();
                        ConfigAssistant.this.m_registrator.addBrowserInstallationInfo(this.getContentPane(), gridBagConstraints, JAPMessages.getString(MSG_PREMIUM_ADVANTAGES) + "...", "premium", false, 0);
                        ++gridBagConstraints.gridy;
                        for (int i = 0; i < vector.size(); ++i) {
                            PaymentInstancePanel paymentInstancePanel = new PaymentInstancePanel((PaymentInstanceDBEntry)vector.elementAt(i), ConfigAssistant.this.m_registrator, true);
                            if (vector.size() < 2) {
                                paymentInstancePanel.setHeadlineVisible(false);
                            }
                            this.getContentPane().add((Component)paymentInstancePanel, gridBagConstraints);
                            ++gridBagConstraints.gridy;
                        }
                    }
                    return null;
                }

                public boolean isSkippedAsPreviousContentPane() {
                    return true;
                }

                public boolean isSkippedAsNextContentPane() {
                    return !ConfigAssistant.this.m_radioBrowserAnonymous.isSelected();
                }

                public boolean hideButtonCancel() {
                    return true;
                }
            };
            ((DialogContentPane)object).getContentPane().setLayout(new GridBagLayout());
        }
        booleanVariable = object;
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter((DialogContentPane)((Object)booleanVariable), configAssistant, runConnectionCreator, simpleWizardContentPane2, locale){
            private final /* synthetic */ DialogContentPane val$paneFinish;
            private final /* synthetic */ JAPDialog val$thisDialog;
            private final /* synthetic */ RunConnectionCreator val$runConnectionCreator;
            private final /* synthetic */ DialogContentPane val$paneRestart;
            private final /* synthetic */ Locale val$locale;
            {
                this.val$paneFinish = dialogContentPane;
                this.val$thisDialog = jAPDialog;
                this.val$runConnectionCreator = runConnectionCreator;
                this.val$paneRestart = dialogContentPane2;
                this.val$locale = locale;
            }

            public void windowClosing(WindowEvent windowEvent) {
                boolean bl = true;
                if (this.val$paneFinish != null && !this.val$paneFinish.isVisible() && !JAPModel.getInstance().isAnonymityPopupsHidden()) {
                    JAPDialog.LinkedCheckBox linkedCheckBox = null;
                    boolean bl2 = bl = JAPDialog.showConfirmDialog(this.val$thisDialog, JAPMessages.getString(MSG_REALLY_CLOSE), 2, 3, linkedCheckBox) == 0;
                    if (linkedCheckBox != null) {
                        JAPModel.getInstance().setShowConfigAssistantAutomatically(!linkedCheckBox.getState());
                    }
                }
                if (bl) {
                    ConfigAssistant.this.dispose();
                }
            }

            public void windowClosed(WindowEvent windowEvent) {
                this.val$runConnectionCreator.reset();
                JAPController.getInstance().forceAnonymityTestRedirect(false);
                if (this.val$paneRestart.getButtonValue() == 0) {
                    JAPMessages.setLocale(((LanguageMapper)ConfigAssistant.this.comboLang.getSelectedItem()).getLocale());
                    if (ConfigAssistant.this.m_radioSimpleView.isSelected()) {
                        JAPModel.getInstance().setDefaultView(2);
                    } else if (ConfigAssistant.this.m_radioAdvancedView.isSelected()) {
                        JAPModel.getInstance().setDefaultView(1);
                    }
                    JAPController.getInstance().setShowConfigAssistant(true);
                    JAPController.goodBye(false);
                } else {
                    JAPController.getInstance().forceAnonymityTestRedirect(false);
                    JAPController.getInstance().blockDirectProxy(false);
                    JAPMessages.init(this.val$locale, "JAPMessages");
                    JAPController.getInstance().setShowConfigAssistant(false);
                    ConfigAssistant.this.m_textCopier.unregisterAll();
                    ConfigAssistant.this.m_registrator.unregisterAll();
                }
            }
        });
        simpleWizardContentPane.pack();
        if (n == 1 || n == 2 || n == 4) {
            workerContentPane.updateDialog();
        }
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner(), 10);
    }

    private void addListenerInterfaceLabels(JComponent jComponent, GridBagConstraints gridBagConstraints, Insets insets, JTextPane jTextPane, JTextPane jTextPane2, int n, String string) {
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = insets;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = n;
        this.addProxyInfo(jComponent, gridBagConstraints, string);
        gridBagConstraints.gridx = n;
        ++gridBagConstraints.gridy;
        jTextPane.setText("localhost");
        this.m_textCopier.register(jTextPane);
        jTextPane.setBackground(Color.white);
        jComponent.add((Component)jTextPane, gridBagConstraints);
        ++gridBagConstraints.gridx;
        jComponent.add((Component)new JLabel(":"), gridBagConstraints);
        ++gridBagConstraints.gridx;
        jTextPane2.setText("65535");
        this.m_textCopier.register(jTextPane2);
        jTextPane2.setBackground(Color.white);
        jComponent.add((Component)jTextPane2, gridBagConstraints);
        ++gridBagConstraints.gridy;
    }

    private ImageIcon loadServicesIcon() {
        ImageIcon imageIcon = GUIUtils.loadImageIcon(MessageFormat.format(IMG_SERVICES, "_" + JAPMessages.getLocale().getLanguage()));
        if (imageIcon == null) {
            imageIcon = GUIUtils.loadImageIcon(MessageFormat.format(IMG_SERVICES, ""));
        }
        return imageIcon;
    }

    private void addProxyInfo(JComponent jComponent, GridBagConstraints gridBagConstraints, String string) {
        string = string == null ? "" : string + " ";
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = 13;
        JLabel jLabel = new JLabel(string + "Hostname");
        jComponent.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(":");
        ++gridBagConstraints.gridx;
        jComponent.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(string + "Port");
        ++gridBagConstraints.gridx;
        jComponent.add((Component)jLabel, gridBagConstraints);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private static class RunConnectionCreator
    extends Observable
    implements Runnable,
    IProgressCapsule,
    WorkerContentPane.IResettable {
        public static final int ERR_SUCCESS = 0;
        public static final int ERR_NO_INFOSERVICE_DATA = 1;
        public static final int ERR_NO_SWITCH_SERVICE_NOT_TRUSTED = 2;
        public static final int ERR_INFO_SERVICE_UPDATE_FAILED = 3;
        public static final int ERR_NO_INTERNET_CONNECTION = 4;
        public static final int ERR_WRONG_PROXY_SETTINGS = 5;
        public static final int ERR_INFO_SERVICE_BLOCKED = 6;
        public static final int ERR_TIMED_OUT = 7;
        public static final int ERR_INTERRUPTED = 8;
        public static final int ERR_PAYMENT_FORCED_BUT_NO_ACCOUNT = 9;
        public static final int ERR_BROKEN_SERVICE = 10;
        public static final int ERR_NOT_CONNECTED = 11;
        public static final int ERR_MIX_SERVICES_BLOCKED = 12;
        public static final int ERR_INFO_SERVICE_UNREACHABLE = 13;
        public static final int ERR_INFO_SERVICE_ANONYMOUS_ONLY = 14;
        public static final int ERR_SWITCH_FILTER = 15;
        public static final int ERR_ANTI_CENSORSHIP_NOT_CONNECTED = 16;
        public static final int ERR_NO_INTERNET_CONNECTION_RECOMMEND_PROXY = 17;
        public static final int ERR_SERVICE_UNREACHABLE = 18;
        public static final int ERR_PREMIUM_SERVICES_ONLY = 19;
        private static final int MAX_SECONDS = 180;
        private static int max_seconds_dynamic = 180;
        private int m_localTimeout = -1;
        private long m_startTime = 0L;
        private int m_status = -1;
        private int m_startComponent;
        private boolean m_backedUp = false;
        private boolean m_bBackupIgnoreAIErrors;
        private boolean m_bBackupAutoReconnect;
        private boolean m_bBackupAutoSwitch;
        private int m_errorCode = 0;
        private String m_strMessage;
        private Vector m_vecTempCascades;

        public RunConnectionCreator(int n) {
            this.m_startComponent = n;
        }

        public TranslatedErrorCode translateErrorCode(int n) {
            String string;
            boolean bl;
            String string2 = null;
            String string3 = null;
            String string4 = null;
            String string5 = null;
            String string6 = "<br/><br/>";
            Object var9_7 = null;
            boolean bl2 = bl = TrustModel.getCurrentTrustModel().isFreeServicesForced() || PayAccountsFile.getInstance().getChargedAccount(null) == null && TrustModel.getCurrentTrustModel().hasFreeCascades();
            if (n < 0) {
                n = 0;
            }
            if (n > 19) {
                n = 0;
            }
            String string7 = "<b>" + (this.m_startComponent != 1 ? JAPMessages.getString(MSG_TRANSLATE_INFOSERVICE_HEAD) : JAPMessages.getString((class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".error" + MSG_TRANSLATE[n] + "Desc")) + "</b>" + (this.m_startComponent != 1 ? "<br/>(" + JAPMessages.getString((class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".error" + MSG_TRANSLATE[n] + "Desc") + ")" : "") + string6 + JAPMessages.getString((class$jap$ConfigAssistant == null ? (class$jap$ConfigAssistant = ConfigAssistant.class$("jap.ConfigAssistant")) : class$jap$ConfigAssistant).getName() + ".error" + MSG_TRANSLATE[n], new String[]{"", "'<i>" + JAPController.getInstance().getCurrentMixCascade().getName() + "</i>'"});
            if (n == 1 || n == 3 || n == 13) {
                string = "infoservice";
                string7 = string7 + " " + JAPMessages.getString(MSG_TRANSLATE_INFOSERVICE_OUT_OF_ORDER);
                if (!InfoServiceHolder.getInstance().isChangeInfoServices() || InfoServiceHolder.getInstance().getNumberOfAskedInfoServices() <= 1) {
                    string2 = "CONF_INFOSERVICE_SETTINGS";
                    string7 = string7 + string6 + JAPMessages.getString(MSG_HINT) + ": " + JAPMessages.getString(MSG_TRANSLATE_INFOSERVICE_TOO_FEW, "3");
                } else {
                    string2 = "CONF_INFOSERVICE";
                }
                if (JAPModel.getDefaultView() == 2) {
                    string2 = null;
                }
            } else if (n == 14) {
                string = "infoservice";
                if (JAPModel.getDefaultView() != 2) {
                    string2 = "CONF_INFOSERVICE_SETTINGS";
                }
            } else if (n == 2 || n == 10 || n == 18) {
                string7 = string7 + " " + JAPMessages.getString(MSG_TRANSLATE_CHANGE_SERVICE);
                if (!JAPModel.getInstance().isCascadeAutoSwitched()) {
                    string2 = "CONF_SERVICE_SETTINGS";
                    string = "services_general";
                    string7 = string7 + string6 + JAPMessages.getString(MSG_HINT) + ": " + JAPMessages.getString(MSG_TRANSLATE_ALLOW_AUTO_CHANGE);
                } else {
                    string = "services_anon";
                    string2 = "CONF_SERVICE";
                }
            } else if (n == 4 || n == 5 || n == 6 || n == 12 || n == 16 || n == 17) {
                string = "net";
                string2 = "CONF_NETWORK";
                if (n == 6 || n == 12) {
                    string7 = string7 + string6 + JAPMessages.getString(MSG_TRANSLATE_ANTI_CENSORSHIP);
                    string5 = "http://www.google.com/search?q=proxy+list";
                }
            } else if (n == 15) {
                string = "services_anon";
                string2 = "CONF_FILTER";
            } else if (n == 9 || n == 19) {
                string = "services_anon";
                string2 = "CONF_FILTER";
                string3 = "premium";
                string4 = JAPMessages.getString(MSG_PREMIUM_ADVANTAGES);
            } else if (n == 7) {
                if (bl) {
                    string = "services_anon";
                    string2 = "CONF_FILTER";
                    string7 = string7 + string6 + JAPMessages.getString(MSG_TRANSLATE_TRY_PREMIUM);
                    string = "premium";
                } else if (BlacklistedCascadeIDEntry.hasActiveElements() || TrustModel.getCurrentTrustModel().countTrustedCascades() < TrustModel.TRUST_MODEL_ALL_SERVICES.countTrustedCascades()) {
                    string7 = string7 + string6 + JAPMessages.getString(MSG_TRANSLATE_TIMED_OUT_CHANGE_FILTER);
                    string = "services_anon";
                    string2 = "CONF_FILTER";
                } else {
                    string = "trouble";
                }
            } else {
                string = "trouble";
                if (JAPModel.getDefaultView() != 2) {
                    string2 = "CONF_DEBUG";
                }
            }
            if (n == 10 || n == 13) {
                string7 = string7 + " " + JAPMessages.getString(MSG_TRANSLATE_REMOVE_FIREWALL);
            }
            return new TranslatedErrorCode(string7, string, string2, string5, string3, string4, var9_7);
        }

        public TranslatedErrorCode translateErrorCode() {
            return this.translateErrorCode(this.getErrorCode());
        }

        private boolean checkInternetConnection(String string, ImmutableProxyInterface immutableProxyInterface, boolean bl, int n, int n2) throws TimeoutException, InterruptedException {
            ListenerInterface listenerInterface = new ListenerInterface(string, 80);
            while (this.waitUntilSecondsConnected(n, n2) && (!bl || immutableProxyInterface != null && immutableProxyInterface.isValid())) {
                LogHolder.log(5, LogType.GUI, "Try HTTP connection to host: " + string);
                try {
                    if (Util.doHttpGetRequest(listenerInterface, immutableProxyInterface, "/").getStatusCode() == 200) {
                        return true;
                    }
                }
                catch (InterruptedIOException interruptedIOException) {
                    throw new InterruptedException();
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.NET, exception);
                }
                try {
                    this.checkTime(false, false, null);
                }
                catch (ConnectedException connectedException) {
                }
                catch (BrokenServiceException brokenServiceException) {
                    // empty catch block
                }
                if (immutableProxyInterface == null || !immutableProxyInterface.isValid() || bl) break;
                immutableProxyInterface = null;
            }
            return false;
        }

        private int checkInternetConnection() throws InterruptedException, TimeoutException {
            return this.checkInternetConnection(false, this.getCurrentSeconds(), 30);
        }

        private int checkInternetConnection(boolean bl) throws InterruptedException, TimeoutException {
            return this.checkInternetConnection(bl, this.getCurrentSeconds(), 30);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean checkProxy(final ImmutableProxyInterface immutableProxyInterface, int n, int n2) throws InterruptedException, TimeoutException {
            if (immutableProxyInterface != null && immutableProxyInterface.isValid()) {
                if (immutableProxyInterface.getHost() == null && immutableProxyInterface.getPort() == JAPModel.getHttpListenerPortNumber() || immutableProxyInterface.getHost().equals("localhost") && immutableProxyInterface.getPort() == JAPModel.getHttpListenerPortNumber() || immutableProxyInterface.getHost().equals("127.0.0.1") && immutableProxyInterface.getPort() == JAPModel.getHttpListenerPortNumber()) {
                    return false;
                }
                IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                    Socket socket = null;

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void run() {
                        try {
                            this.socket = new Socket(immutableProxyInterface.getHost(), immutableProxyInterface.getPort());
                        }
                        catch (Exception exception) {
                            try {
                                SocketGuard.close(this.socket);
                            }
                            catch (Exception exception2) {
                                LogHolder.log(3, LogType.GUI, exception2);
                            }
                            this.socket = null;
                            LogHolder.log(4, LogType.GUI, exception);
                            return;
                        }
                        finally {
                            try {
                                SocketGuard.close(this.socket);
                            }
                            catch (Exception exception) {
                                LogHolder.log(3, LogType.GUI, exception);
                            }
                        }
                    }

                    public Object getValue() {
                        return this.socket;
                    }
                };
                Thread thread = new Thread(iReturnRunnable);
                thread.start();
                try {
                    int n3 = this.getCurrentSeconds();
                    while (thread.isAlive() && this.waitUntilSecondsConnected(n, n2) && this.waitUntilSecondsConnected(n3, 10)) {
                        this.checkTime(500L, false, thread);
                    }
                }
                catch (InterruptedException interruptedException) {
                    throw interruptedException;
                }
                catch (TimeoutException timeoutException) {
                    throw timeoutException;
                }
                catch (Exception exception) {
                }
                finally {
                    Util.interrupt(thread);
                }
                if (iReturnRunnable.getValue() == null) {
                    return false;
                }
            }
            return true;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private int checkInternetConnection(boolean bl, final int n, final int n2) throws InterruptedException, TimeoutException {
            if (bl) {
                this.m_localTimeout = n2;
            }
            IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                Integer returnCode;
                String[] strURLs = new String[]{"www.w3.org", "www.mozilla.org", "www.wikipedia.org", "www.google.com", "www.baidu.com"};

                private boolean checkHTTPConnection(ImmutableProxyInterface immutableProxyInterface, int n3, int n22) throws TimeoutException, InterruptedException {
                    LogHolder.log(5, LogType.GUI, "Check HTTP connection...", new Exception());
                    if (RunConnectionCreator.this.checkProxy(immutableProxyInterface, n3, n22)) {
                        for (int i = 0; i < this.strURLs.length; ++i) {
                            if (!RunConnectionCreator.this.checkInternetConnection(this.strURLs[i], immutableProxyInterface, true, n, n22)) continue;
                            return true;
                        }
                    }
                    return false;
                }

                private boolean checkHTTPConnection() throws InterruptedException, TimeoutException {
                    for (int i = 0; i < this.strURLs.length; ++i) {
                        if (!RunConnectionCreator.this.checkInternetConnection(this.strURLs[i], null, false, n, n2)) continue;
                        return true;
                    }
                    return false;
                }

                public void run() {
                    ImmutableProxyInterface immutableProxyInterface = JAPModel.getInstance().getMutableProxyInterface().getProxyInterface(false).getProxyInterface();
                    LogHolder.log(5, LogType.GUI, "Starting Internet connection test with proxy: " + immutableProxyInterface);
                    try {
                        Object object;
                        this.returnCode = null;
                        if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1) {
                            LogHolder.log(5, LogType.GUI, "Force anonymous...");
                            if (immutableProxyInterface != null && RunConnectionCreator.this.checkProxy(immutableProxyInterface, n, n2) && this.checkHTTPConnection(immutableProxyInterface, n, n2)) {
                                this.returnCode = new Integer(0);
                            } else if (this.checkHTTPConnection()) {
                                this.returnCode = immutableProxyInterface != null ? new Integer(5) : new Integer(0);
                            }
                            LogHolder.log(5, LogType.GUI, "Force anonymous, return code: " + this.returnCode);
                        } else {
                            LogHolder.log(5, LogType.GUI, "Checking proxy...");
                            object = null;
                            if (immutableProxyInterface != null && RunConnectionCreator.this.checkProxy(immutableProxyInterface, n, n2)) {
                                LogHolder.log(5, LogType.GUI, "Get external address...");
                                object = InfoServiceHolder.getInstance().getMyIP(InfoServiceDBEntry.PROXY_FORCE_DEFAULT);
                            }
                            if (object != null) {
                                this.returnCode = new Integer(0);
                            } else {
                                LogHolder.log(5, LogType.GUI, "Get external address...");
                                object = InfoServiceHolder.getInstance().getMyIP(InfoServiceDBEntry.PROXY_FORCE_DIRECT);
                                if (immutableProxyInterface != null && object != null) {
                                    this.returnCode = new Integer(5);
                                } else if (object != null) {
                                    this.returnCode = new Integer(0);
                                }
                            }
                            LogHolder.log(5, LogType.GUI, "Got external address return code: " + this.returnCode);
                            if (this.returnCode == null) {
                                if (this.checkHTTPConnection(immutableProxyInterface, n, n2)) {
                                    this.returnCode = JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() != 2 && InfoServiceHolder.getInstance().getMyIP(InfoServiceDBEntry.PROXY_FORCE_ANONYMOUS) != null ? new Integer(13) : new Integer(6);
                                } else if (this.checkHTTPConnection()) {
                                    this.returnCode = immutableProxyInterface != null ? new Integer(5) : new Integer(6);
                                }
                            }
                        }
                        if (this.returnCode == null) {
                            object = AbstractOS.getInstance().getProxyInterface(null);
                            ProxyInterface proxyInterface = JAPModel.getInstance().getProxyInterface();
                            if (object != null && (proxyInterface == null || !Util.equals(((ProxyInterface)object).getListenerInterface(), proxyInterface.getListenerInterface())) && this.checkHTTPConnection((ImmutableProxyInterface)object, n, n2)) {
                                if (JAPModel.getInstance().getProxyInterface() == null) {
                                    JAPModel.getInstance().setProxyListener((ProxyInterface)object);
                                    this.returnCode = new Integer(0);
                                } else {
                                    this.returnCode = new Integer(17);
                                }
                            }
                        }
                        if (this.returnCode == null && immutableProxyInterface == null && (object = JAPModel.getInstance().getProxyInterface()) != null) {
                            object = (ProxyInterface)((ProxyInterface)object).clone();
                            ((ProxyInterface)object).setUseInterface(true);
                            if (((ProxyInterface)object).isValid() && this.checkHTTPConnection((ImmutableProxyInterface)object, n, n2)) {
                                JAPModel.getInstance().setProxyListener((ProxyInterface)object);
                                this.returnCode = new Integer(0);
                            }
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            new InterruptedException().printStackTrace();
                            this.returnCode = new Integer(8);
                        }
                        if (this.returnCode == null) {
                            this.returnCode = new Integer(4);
                        }
                    }
                    catch (TimeoutException timeoutException) {
                        this.returnCode = new Integer(7);
                    }
                    catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                        this.returnCode = new Integer(8);
                    }
                }

                public Object getValue() {
                    return this.returnCode;
                }
            };
            Thread thread = new Thread(iReturnRunnable);
            try {
                thread.start();
                while (thread.isAlive() && this.waitUntilSecondsConnected(n, n2)) {
                    this.m_strMessage = MSG_STATUS_TESTING_CONNECTIVITY;
                    try {
                        this.checkTime(false, false, thread);
                    }
                    catch (BrokenServiceException brokenServiceException) {
                        LogHolder.log(3, LogType.GUI, brokenServiceException);
                    }
                    catch (ConnectedException connectedException) {
                        LogHolder.log(3, LogType.GUI, connectedException);
                    }
                }
                if (iReturnRunnable.getValue() == null) {
                    int n3 = 4;
                    return n3;
                }
            }
            finally {
                Util.interrupt(thread);
                if (bl) {
                    this.m_localTimeout = -1;
                }
            }
            if (iReturnRunnable.getValue() instanceof InetAddress) {
                return 0;
            }
            return (Integer)iReturnRunnable.getValue();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void checkAnonymousConnection(boolean bl) throws InterruptedException, ConnectedException, BrokenServiceException, TimeoutException {
            if (bl) {
                this.m_localTimeout = 25;
            }
            try {
                this.checkAnonymousConnection();
            }
            finally {
                if (bl) {
                    this.m_localTimeout = -1;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        private void checkAnonymousConnection() throws InterruptedException, ConnectedException, BrokenServiceException, TimeoutException {
            MixCascade mixCascade = JAPController.getInstance().getConnectedCascade();
            if (mixCascade == null) {
                return;
            }
            IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                Object value;

                public void run() {
                    MixCascade mixCascade = JAPController.getInstance().getConnectedCascade();
                    this.value = InfoServiceHolder.getInstance().getMyIP(InfoServiceDBEntry.PROXY_FORCE_ANONYMOUS);
                    try {
                        if (this.value == null && RunConnectionCreator.this.checkInternetConnection("www.w3.org", JAPModel.getInstance().getAnonymityProxy(), true, RunConnectionCreator.this.getCurrentSeconds(), 30)) {
                            this.value = new Boolean(true);
                        } else if (this.value != null) {
                            MixCascade mixCascade2 = JAPController.getInstance().getConnectedCascade();
                            if (this.value != null && mixCascade != null && mixCascade2 != null && mixCascade.equals(mixCascade2)) {
                                MixCascadeExitAddresses.addInetAddress(mixCascade.getId(), (InetAddress)this.value, mixCascade.getDistribution(), null);
                            }
                        }
                    }
                    catch (TimeoutException timeoutException) {
                        this.value = timeoutException;
                    }
                    catch (InterruptedException interruptedException) {
                        this.value = interruptedException;
                    }
                }

                public Object getValue() {
                    return this.value;
                }
            };
            Thread thread = new Thread(iReturnRunnable);
            try {
                thread.start();
                int n = this.getCurrentSeconds();
                while (thread.isAlive() && this.waitUntilSecondsConnected(n, 30)) {
                    this.m_strMessage = MSG_STATUS_TESTING_ANONYMOUS_CONNECTION;
                    if (JAPController.getInstance().isAnonConnected()) {
                        Thread.sleep(400L);
                        if (System.currentTimeMillis() - this.m_startTime > (long)max_seconds_dynamic * 1000L) {
                            throw new TimeoutException();
                        }
                        RunConnectionCreator runConnectionCreator = this;
                        synchronized (runConnectionCreator) {
                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException();
                            }
                            this.setChanged();
                            this.notifyObservers(this);
                            continue;
                        }
                    }
                    break;
                }
            }
            finally {
                Util.interrupt(thread);
            }
            if (JAPController.getInstance().isAnonConnected()) {
                if (iReturnRunnable.getValue() != null) {
                    if (iReturnRunnable.getValue() instanceof TimeoutException) {
                        throw (TimeoutException)iReturnRunnable.getValue();
                    }
                    if (iReturnRunnable.getValue() instanceof InterruptedException) {
                        throw (InterruptedException)iReturnRunnable.getValue();
                    }
                    throw new ConnectedException();
                }
                if (mixCascade == JAPController.getInstance().getConnectedCascade()) {
                    Database database = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade);
                    synchronized (database) {
                        for (int i = 0; i < mixCascade.getNumberOfListenerInterfaces(); ++i) {
                            mixCascade.getListenerInterface(i).blockInterface(30000L);
                        }
                    }
                }
                if (this.checkInternetConnection() == 0 && JAPController.getInstance().getConnectedCascade() == mixCascade && JAPController.getInstance().switchToNextMixCascade(this.m_bBackupAutoSwitch) == mixCascade) {
                    throw new BrokenServiceException(mixCascade);
                }
            }
        }

        private void checkTime(boolean bl, Thread thread) throws TimeoutException, InterruptedException, ConnectedException, BrokenServiceException {
            this.checkTime(bl, true, thread);
        }

        private void checkTime(boolean bl, boolean bl2, Thread thread) throws TimeoutException, InterruptedException, ConnectedException, BrokenServiceException {
            if (bl && this.m_bBackupAutoSwitch) {
                JAPModel.getInstance().setCascadeAutoSwitch(true);
            }
            this.checkTime(400L, bl2, thread);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void checkTime(long l, boolean bl, Thread thread) throws TimeoutException, InterruptedException, ConnectedException, BrokenServiceException {
            if (l < 0L) {
                return;
            }
            if (this.m_startComponent != 1) {
                if (l > 0L) {
                    AnonClient.setBlockOnHttpConnectionError(true);
                }
                JAPModel.getInstance().setAutoReConnect(true);
                if (!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder() && bl && !JAPController.getInstance().getAnonMode()) {
                    JAPController.getInstance().start();
                }
            }
            long l2 = System.currentTimeMillis();
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            if (System.currentTimeMillis() - this.m_startTime > (long)max_seconds_dynamic * 1000L) {
                throw new TimeoutException();
            }
            if (this.m_startComponent != 1 && bl) {
                this.checkAnonymousConnection();
            }
            if ((l2 = l - (System.currentTimeMillis() - l2)) > 0L) {
                if (thread == null) {
                    Thread.sleep(l2);
                } else if (thread.isAlive()) {
                    Thread.yield();
                    thread.join(l2);
                }
            }
            RunConnectionCreator runConnectionCreator = this;
            synchronized (runConnectionCreator) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                this.setChanged();
                this.notifyObservers(this);
            }
        }

        private boolean hasChanceToConnect() {
            if (TrustModel.getCurrentTrustModel().isPaymentForced() && !AccountCreator.checkValidAccount()) {
                return false;
            }
            MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
            return TrustModel.getCurrentTrustModel().isTrusted(mixCascade) && mixCascade != AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE || TrustModel.getCurrentTrustModel().hasTrustedCascades() && (this.m_bBackupAutoSwitch || JAPController.getInstance().switchToNextMixCascade(this.m_bBackupAutoSwitch) != mixCascade);
        }

        private boolean waitUntilSecondsConnected(int n, int n2) {
            if (Thread.currentThread().isInterrupted()) {
                return false;
            }
            if (n2 == Integer.MAX_VALUE) {
                return true;
            }
            return System.currentTimeMillis() - this.m_startTime < (long)(n + n2) * 1000L;
        }

        public String getMessage() {
            String string = this.m_strMessage;
            if (string != null) {
                return JAPMessages.getString(string);
            }
            return null;
        }

        public int getMaximum() {
            if (this.m_localTimeout > 0) {
                return this.m_localTimeout;
            }
            return max_seconds_dynamic;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void reset() {
            RunConnectionCreator runConnectionCreator = this;
            synchronized (runConnectionCreator) {
                this.m_status = -1;
                this.m_startTime = System.currentTimeMillis();
                max_seconds_dynamic = 180;
                AnonClient.setBlockOnHttpConnectionError(false);
                TrustModel.getCurrentTrustModel().unblockInterfacesFromDatabase();
                ListenerInterface.unblockInterfacesFromDatabase(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = ConfigAssistant.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry);
                if (this.m_backedUp) {
                    PayAccountsFile.getInstance().setIgnoreAIAccountError(this.m_bBackupIgnoreAIErrors);
                    JAPModel.getInstance().setAutoReConnect(this.m_bBackupAutoReconnect);
                    JAPModel.getInstance().setCascadeAutoSwitch(this.m_bBackupAutoSwitch);
                    TrustModel.setCurrentTrustModel(TrustModel.getTrustModelDefault());
                    this.m_backedUp = false;
                    if (this.m_vecTempCascades != null) {
                        for (int i = 0; i < this.m_vecTempCascades.size(); ++i) {
                            Database.getInstance(class$anon$infoservice$MixCascade == null ? ConfigAssistant.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).removeThis((MixCascade)this.m_vecTempCascades.elementAt(i));
                        }
                        this.m_vecTempCascades.removeAllElements();
                    }
                }
                if (Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getNumberOfEntries() == 0 && !JAPController.getInstance().isAnonConnected()) {
                    JAPController.getInstance().setCurrentMixCascade(AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE);
                }
            }
        }

        public int getMinimum() {
            return 0;
        }

        public int getValue() {
            return (int)((System.currentTimeMillis() - this.m_startTime) / 1000L);
        }

        public int getStatus() {
            return this.m_status;
        }

        public int getErrorCode() {
            return this.m_errorCode;
        }

        private int getCurrentSeconds() {
            return this.getValue();
        }

        private void setErrorCode(int n) {
            if (this.m_errorCode == 0) {
                this.m_errorCode = n;
            }
        }

        private MixCascade createFailoverService(String string, final String string2, Vector vector, final int n) throws Exception {
            MixCascade mixCascade = new MixCascade(string, string2, vector){
                MixInfo m_DefaultInfo;
                {
                    this.m_DefaultInfo = new MixInfo(string2, null, null, 0L){

                        public boolean isValid() {
                            return true;
                        }

                        public boolean isVerified() {
                            return true;
                        }
                    };
                }

                public int getNumberOfOperators() {
                    return n;
                }

                public int getNumberOfCountries() {
                    return 1;
                }

                public boolean isValid() {
                    return true;
                }

                public boolean isVerified() {
                    return true;
                }

                public MixInfo getMixInfo(int n2) {
                    return this.m_DefaultInfo;
                }

                public int getNumberOfMixes() {
                    return n;
                }
            };
            mixCascade.setUserDefined(false, null);
            return mixCascade;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void run() {
            block76: {
                block75: {
                    block74: {
                        block73: {
                            Class class_;
                            boolean bl;
                            block77: {
                                block71: {
                                    block72: {
                                        this.m_startTime = System.currentTimeMillis();
                                        max_seconds_dynamic = 180;
                                        this.m_backedUp = false;
                                        this.m_status = 1;
                                        this.m_errorCode = 0;
                                        this.m_localTimeout = this.m_startComponent == 1 ? 60 : -1;
                                        try {
                                            try {
                                                int n;
                                                int n2;
                                                IXMLEncodable iXMLEncodable;
                                                Object object;
                                                this.m_backedUp = true;
                                                this.m_bBackupIgnoreAIErrors = PayAccountsFile.getInstance().isAIAccountErrorIgnored();
                                                this.m_bBackupAutoReconnect = JAPModel.isAutomaticallyReconnected();
                                                this.m_bBackupAutoSwitch = JAPModel.getInstance().isCascadeAutoSwitched();
                                                PayAccountsFile.getInstance().setIgnoreAIAccountError(true);
                                                if (!this.m_bBackupAutoSwitch) {
                                                    object = JAPController.getInstance().getCurrentMixCascade();
                                                    if (!this.m_bBackupAutoSwitch && JAPController.getInstance().switchToNextMixCascade(this.m_bBackupAutoSwitch) == object && JAPController.getInstance().switchToNextMixCascade() == object) {
                                                        iXMLEncodable = TrustModel.getCurrentTrustModel();
                                                        if (!iXMLEncodable.isTrusted((MixCascade)object)) {
                                                            n2 = 0;
                                                            try {
                                                                iXMLEncodable.checkTrust((MixCascade)object);
                                                            }
                                                            catch (TrustException trustException) {
                                                                if (trustException instanceof ServiceUnreachableException) {
                                                                    n2 = 1;
                                                                }
                                                            }
                                                            catch (ServiceSignatureException serviceSignatureException) {
                                                                // empty catch block
                                                            }
                                                            if (n2 != 0) {
                                                                this.setErrorCode(18);
                                                            } else {
                                                                this.setErrorCode(2);
                                                            }
                                                        } else if (JAPController.getInstance().getCurrentMixCascade().isPayment() && !AccountCreator.checkValidAccount()) {
                                                            this.setErrorCode(9);
                                                        }
                                                    }
                                                }
                                                this.checkTime(0L, true, null);
                                                if (this.m_errorCode != 0 || (this.m_errorCode = this.checkInternetConnection()) != 0) {
                                                    this.m_status = 3;
                                                    Object var7_13 = null;
                                                    if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                                                        this.m_status = 2;
                                                    }
                                                    if (Thread.currentThread().isInterrupted()) return;
                                                    if (this.m_errorCode != 0) return;
                                                    if (this.m_startComponent == 1) return;
                                                    bl = true;
                                                    if (class$anon$infoservice$MixCascade != null) break block71;
                                                    break block72;
                                                }
                                                if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                                                    this.setErrorCode(16);
                                                    throw new TimeoutException();
                                                }
                                                if (this.m_startComponent == 1 || Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getNumberOfEntries() == 0) {
                                                    object = new Thread(){

                                                        public void run() {
                                                            if (!JAPController.getInstance().fetchMixCascades(false, false, true)) {
                                                                if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1) {
                                                                    RunConnectionCreator.this.setErrorCode(14);
                                                                } else {
                                                                    RunConnectionCreator.this.setErrorCode(3);
                                                                }
                                                            }
                                                        }
                                                    };
                                                    ((Thread)object).start();
                                                    n = this.getCurrentSeconds();
                                                    while (((Thread)object).isAlive() && this.waitUntilSecondsConnected(n, this.m_startComponent == 1 ? Integer.MAX_VALUE : 90)) {
                                                        this.m_strMessage = MSG_STATUS_FETCH_FROM_INFOSERVICE;
                                                        this.checkTime(false, this.m_startComponent != 1, (Thread)object);
                                                    }
                                                    if (this.m_startComponent == 1) {
                                                        if (((Thread)object).isAlive()) throw new TimeoutException();
                                                        if (Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getNumberOfEntries() != 0) throw new ConnectedException();
                                                        this.setErrorCode(1);
                                                        break block73;
                                                    }
                                                }
                                                if (Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getNumberOfEntries() == 0) {
                                                    this.setErrorCode(1);
                                                    if (this.m_startComponent == 1) {
                                                        this.m_status = 3;
                                                        break block74;
                                                    }
                                                    object = new Vector<ListenerInterface>();
                                                    this.m_vecTempCascades = new Vector();
                                                    try {
                                                        ((Vector)object).addElement(new ListenerInterface("141.76.45.33", 22));
                                                        ((Vector)object).addElement(new ListenerInterface("141.76.45.33", 80));
                                                        ((Vector)object).addElement(new ListenerInterface("141.76.45.33", 443));
                                                        ((Vector)object).addElement(new ListenerInterface("141.76.45.33", 6544));
                                                        this.m_vecTempCascades.addElement(this.createFailoverService("Dresden", "F30905FCD73B6B30CB5FEFD3250FD66EF4B32591", (Vector)object, 1));
                                                        ((Vector)object).removeAllElements();
                                                        ((Vector)object).addElement(new ListenerInterface("188.40.112.222", 80));
                                                        ((Vector)object).addElement(new ListenerInterface("188.40.112.222", 443));
                                                        this.m_vecTempCascades.addElement(this.createFailoverService("Free Beer", "4BC9EE5CC9DB3ABE6759715E5E7D5495FD21FED1", (Vector)object, 2));
                                                        ((Vector)object).removeAllElements();
                                                        ((Vector)object).addElement(new ListenerInterface("78.47.222.170", 80));
                                                        ((Vector)object).addElement(new ListenerInterface("78.47.222.170", 443));
                                                        this.m_vecTempCascades.addElement(this.createFailoverService("Vosskuhle", "1324D6A8EF5B854DE18D89080FDD605EBADC4B30", (Vector)object, 2));
                                                        ((Vector)object).removeAllElements();
                                                        ((Vector)object).addElement(new ListenerInterface("91.184.37.67", 80));
                                                        ((Vector)object).addElement(new ListenerInterface("91.184.37.67", 443));
                                                        this.m_vecTempCascades.addElement(this.createFailoverService("SpeedPartner", "938402DCA648975BD6A9C90B94E73A415D4E4757", (Vector)object, 2));
                                                        ((Vector)object).removeAllElements();
                                                        ((Vector)object).addElement(new ListenerInterface("212.117.177.7", 80));
                                                        ((Vector)object).addElement(new ListenerInterface("212.117.177.7", 443));
                                                        this.m_vecTempCascades.addElement(this.createFailoverService("Speedy1", "C359B6B7BDE86F2F7CBAE4E1AB89ECACB0B95657", (Vector)object, 2));
                                                        ((Vector)object).removeAllElements();
                                                        ((Vector)object).addElement(new ListenerInterface("80.237.152.53", 6547));
                                                        this.m_vecTempCascades.addElement(this.createFailoverService("HE", "145C6DB983E5FF548CD1986BC60531DCBF9C8E21", (Vector)object, 2));
                                                        for (int i = 0; i < this.m_vecTempCascades.size(); ++i) {
                                                            Database.getInstance(class$anon$infoservice$MixCascade == null ? ConfigAssistant.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).update((MixCascade)this.m_vecTempCascades.elementAt(i));
                                                        }
                                                    }
                                                    catch (Exception exception) {
                                                        LogHolder.log(2, LogType.GUI, exception);
                                                    }
                                                }
                                                this.m_strMessage = MSG_CREATING_CONNECTION;
                                                if (!TrustModel.getCurrentTrustModel().hasFreeCascades() && TrustModel.getCurrentTrustModel().hasPremiumCascades() && !AccountCreator.checkValidAccount()) {
                                                    this.setErrorCode(19);
                                                    this.m_status = 3;
                                                    break block75;
                                                }
                                                if (this.m_bBackupAutoSwitch && TrustModel.getCurrentTrustModel().isPaymentForced() && PayAccountsFile.getInstance().getChargedAccount(null) == null) {
                                                    this.setErrorCode(9);
                                                    this.m_status = 3;
                                                    break block76;
                                                }
                                                n = this.getCurrentSeconds();
                                                while (this.waitUntilSecondsConnected(n, Integer.MAX_VALUE) && this.hasChanceToConnect()) {
                                                    this.checkTime(true, null);
                                                }
                                                object = JAPController.getInstance().getCurrentMixCascade();
                                                iXMLEncodable = null;
                                                if (!this.m_bBackupAutoSwitch && !TrustModel.getCurrentTrustModel().isTrusted((MixCascade)object) && (iXMLEncodable = JAPController.getInstance().switchToNextMixCascade(this.m_bBackupAutoSwitch)) == object || !TrustModel.getCurrentTrustModel().hasTrustedCascades()) {
                                                    TrustModel.getCurrentTrustModel().unblockInterfacesFromDatabase();
                                                    n2 = TrustModel.getCurrentTrustModel().countTrustedCascades();
                                                    TrustModel trustModel = null;
                                                    if (TrustModel.TRUST_MODEL_ALL_SERVICES.countTrustedCascades() > 0) {
                                                        trustModel = TrustModel.TRUST_MODEL_ALL_SERVICES;
                                                    }
                                                    if (!this.m_bBackupAutoSwitch && iXMLEncodable != null) {
                                                        if (trustModel != null) {
                                                            if (n2 == 0) {
                                                                if (this.getErrorCode() == 2) {
                                                                    this.m_errorCode = 15;
                                                                }
                                                                this.setErrorCode(15);
                                                            } else {
                                                                this.setErrorCode(2);
                                                            }
                                                        } else {
                                                            if (this.getErrorCode() == 2) {
                                                                this.m_errorCode = 1;
                                                            }
                                                            this.setErrorCode(1);
                                                        }
                                                    } else if (n2 <= 2) {
                                                        if (BlacklistedCascadeIDEntry.hasActiveElements()) {
                                                            this.setErrorCode(15);
                                                        } else if (trustModel != null) {
                                                            if (n2 < trustModel.countTrustedCascades()) {
                                                                this.setErrorCode(15);
                                                            }
                                                        } else {
                                                            if (this.getErrorCode() == 2) {
                                                                this.m_errorCode = 1;
                                                            }
                                                            this.setErrorCode(1);
                                                        }
                                                    } else {
                                                        this.setErrorCode(12);
                                                        if (this.m_errorCode == 6) {
                                                            this.m_errorCode = 12;
                                                        }
                                                    }
                                                }
                                                if ((n2 = this.checkInternetConnection()) != 0) {
                                                    this.m_errorCode = n2;
                                                }
                                                if (!Thread.currentThread().isInterrupted()) throw new TimeoutException();
                                                throw new InterruptedException();
                                            }
                                            catch (InterruptedException interruptedException) {
                                                this.m_status = 2;
                                                this.setErrorCode(8);
                                                Thread.currentThread().interrupt();
                                                Object var7_18 = null;
                                                if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                                                    this.m_status = 2;
                                                }
                                                if (Thread.currentThread().isInterrupted()) return;
                                                if (this.m_errorCode != 0) return;
                                                if (this.m_startComponent == 1) return;
                                                boolean bl2 = true;
                                                Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                                                while (enumeration.hasMoreElements()) {
                                                    MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                                                    if (mixCascade.isDefaultVerified()) continue;
                                                    bl2 = false;
                                                    break;
                                                }
                                                if (!bl2) return;
                                                Thread thread = new Thread(){

                                                    public void run() {
                                                        JAPController.getInstance().fetchMixCascades(false, false, true);
                                                    }
                                                };
                                                thread.start();
                                                return;
                                            }
                                            catch (TimeoutException timeoutException) {
                                                this.m_status = 3;
                                                this.setErrorCode(7);
                                                LogHolder.log(4, LogType.GUI, "Timed out during connection attempt.", timeoutException);
                                                Object var7_19 = null;
                                                if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                                                    this.m_status = 2;
                                                }
                                                if (Thread.currentThread().isInterrupted()) return;
                                                if (this.m_errorCode != 0) return;
                                                if (this.m_startComponent == 1) return;
                                                boolean bl3 = true;
                                                Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                                                while (enumeration.hasMoreElements()) {
                                                    MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                                                    if (mixCascade.isDefaultVerified()) continue;
                                                    bl3 = false;
                                                    break;
                                                }
                                                if (!bl3) return;
                                                Thread thread = new /* invalid duplicate definition of identical inner class */;
                                                thread.start();
                                                return;
                                            }
                                            catch (ConnectedException connectedException) {
                                                this.m_errorCode = 0;
                                                this.m_status = 0;
                                                LogHolder.log(5, LogType.GUI, "Connected!", connectedException);
                                                Object var7_20 = null;
                                                if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                                                    this.m_status = 2;
                                                }
                                                if (Thread.currentThread().isInterrupted()) return;
                                                if (this.m_errorCode != 0) return;
                                                if (this.m_startComponent == 1) return;
                                                boolean bl4 = true;
                                                Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                                                while (enumeration.hasMoreElements()) {
                                                    MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                                                    if (mixCascade.isDefaultVerified()) continue;
                                                    bl4 = false;
                                                    break;
                                                }
                                                if (!bl4) return;
                                                Thread thread = new /* invalid duplicate definition of identical inner class */;
                                                thread.start();
                                                return;
                                            }
                                            catch (BrokenServiceException brokenServiceException) {
                                                this.m_status = 3;
                                                this.setErrorCode(10);
                                                LogHolder.log(4, LogType.GUI, "Current service seems broken.", brokenServiceException);
                                                Object var7_21 = null;
                                                if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                                                    this.m_status = 2;
                                                }
                                                if (Thread.currentThread().isInterrupted()) return;
                                                if (this.m_errorCode != 0) return;
                                                if (this.m_startComponent == 1) return;
                                                boolean bl5 = true;
                                                Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                                                while (enumeration.hasMoreElements()) {
                                                    MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                                                    if (mixCascade.isDefaultVerified()) continue;
                                                    bl5 = false;
                                                    break;
                                                }
                                                if (!bl5) return;
                                                Thread thread = new /* invalid duplicate definition of identical inner class */;
                                                thread.start();
                                                return;
                                            }
                                        }
                                        catch (Throwable throwable) {
                                            Object var7_22 = null;
                                            if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                                                this.m_status = 2;
                                            }
                                            if (Thread.currentThread().isInterrupted()) throw throwable;
                                            if (this.m_errorCode != 0) throw throwable;
                                            if (this.m_startComponent == 1) throw throwable;
                                            boolean bl6 = true;
                                            Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                                            while (enumeration.hasMoreElements()) {
                                                MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                                                if (mixCascade.isDefaultVerified()) continue;
                                                bl6 = false;
                                                break;
                                            }
                                            if (!bl6) throw throwable;
                                            Thread thread = new /* invalid duplicate definition of identical inner class */;
                                            thread.start();
                                            throw throwable;
                                        }
                                    }
                                    class_ = class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade");
                                    break block77;
                                }
                                class_ = class$anon$infoservice$MixCascade;
                            }
                            Enumeration enumeration = Database.getInstance(class_).getEntrySnapshotAsEnumeration();
                            while (enumeration.hasMoreElements()) {
                                MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                                if (mixCascade.isDefaultVerified()) continue;
                                bl = false;
                                break;
                            }
                            if (!bl) return;
                            Thread thread = new /* invalid duplicate definition of identical inner class */;
                            thread.start();
                            return;
                        }
                        Object var7_14 = null;
                        if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                            this.m_status = 2;
                        }
                        if (Thread.currentThread().isInterrupted()) return;
                        if (this.m_errorCode != 0) return;
                        if (this.m_startComponent == 1) return;
                        boolean bl = true;
                        Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                        while (enumeration.hasMoreElements()) {
                            MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                            if (mixCascade.isDefaultVerified()) continue;
                            bl = false;
                            break;
                        }
                        if (!bl) return;
                        Thread thread = new /* invalid duplicate definition of identical inner class */;
                        thread.start();
                        return;
                    }
                    Object var7_15 = null;
                    if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                        this.m_status = 2;
                    }
                    if (Thread.currentThread().isInterrupted()) return;
                    if (this.m_errorCode != 0) return;
                    if (this.m_startComponent == 1) return;
                    boolean bl = true;
                    Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                    while (enumeration.hasMoreElements()) {
                        MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                        if (mixCascade.isDefaultVerified()) continue;
                        bl = false;
                        break;
                    }
                    if (!bl) return;
                    Thread thread = new /* invalid duplicate definition of identical inner class */;
                    thread.start();
                    return;
                }
                Object var7_16 = null;
                if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                    this.m_status = 2;
                }
                if (Thread.currentThread().isInterrupted()) return;
                if (this.m_errorCode != 0) return;
                if (this.m_startComponent == 1) return;
                boolean bl = true;
                Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                while (enumeration.hasMoreElements()) {
                    MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                    if (mixCascade.isDefaultVerified()) continue;
                    bl = false;
                    break;
                }
                if (!bl) return;
                Thread thread = new /* invalid duplicate definition of identical inner class */;
                thread.start();
                return;
            }
            Object var7_17 = null;
            if (Thread.currentThread().isInterrupted() || this.m_errorCode == 8) {
                this.m_status = 2;
            }
            if (Thread.currentThread().isInterrupted()) return;
            if (this.m_errorCode != 0) return;
            if (this.m_startComponent == 1) return;
            boolean bl = true;
            Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ConfigAssistant.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
            while (enumeration.hasMoreElements()) {
                MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                if (mixCascade.isDefaultVerified()) continue;
                bl = false;
                break;
            }
            if (!bl) return;
            Thread thread = new /* invalid duplicate definition of identical inner class */;
            thread.start();
        }

        private static class TranslatedErrorCode {
            private String m_strMessage;
            private String m_strHelpContext;
            private String m_strConfigurationPage;
            private String m_strUrl;
            private Object m_confArg;
            private String m_strAdditionalHelpContext;
            private String m_strAdditionalHelpContextText;

            public TranslatedErrorCode(String string, String string2, String string3, String string4, String string5, String string6, Object object) {
                this.m_strMessage = string;
                this.m_strHelpContext = string2;
                this.m_strConfigurationPage = string3;
                this.m_strUrl = string4;
                this.m_confArg = object;
                this.m_strAdditionalHelpContext = string5;
                this.m_strAdditionalHelpContextText = string6;
            }

            public final String getURL() {
                return this.m_strUrl;
            }

            public final String getMessage() {
                return this.m_strMessage;
            }

            public final String getHelpContext() {
                return this.m_strHelpContext;
            }

            public final String getConfigurationPage() {
                return this.m_strConfigurationPage;
            }

            public final Object getConfigurationArg() {
                return this.m_confArg;
            }

            public final String getAdditionalHelpContext() {
                return this.m_strAdditionalHelpContext;
            }

            public final String getAdditionalHelpContextText() {
                return this.m_strAdditionalHelpContextText;
            }
        }

        private static class BrokenServiceException
        extends Exception {
            public BrokenServiceException(MixCascade mixCascade) {
                super(mixCascade.getName());
            }
        }

        private static class ConnectedException
        extends Exception {
            private ConnectedException() {
            }
        }

        private static class TimeoutException
        extends Exception {
            private TimeoutException() {
            }
        }
    }
}

