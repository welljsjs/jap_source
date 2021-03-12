/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import anon.client.TrustModel;
import anon.error.AccountEmptyException;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.pay.AIControlChannel;
import anon.pay.IPaymentListener;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLBalance;
import anon.pay.xml.XMLErrorMessage;
import anon.util.IReturnRunnable;
import anon.util.JAPMessages;
import anon.util.JobQueue;
import anon.util.Util;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;
import gui.FlippingPanel;
import gui.GUIUtils;
import gui.JAPProgressBar;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.WorkerContentPane;
import jap.JAPConstants;
import jap.JAPController;
import jap.JAPModel;
import jap.JAPNewView;
import jap.pay.AccountSettingsPanel;
import jap.pay.ActivePaymentDetails;
import jap.pay.IPaymentDialogPresentator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import logging.LogHolder;
import logging.LogType;

public class PaymentMainPanel
extends FlippingPanel {
    public static final long WARNING_AMOUNT = 125000000L;
    public static final long WARNING_TIME = 604800000L;
    public static final long FULL_AMOUNT = 500000000L;
    private static final String MSG_TITLE = AccountSettingsPanel.MSG_ACCOUNT_FLAT_VOLUME;
    private static final String MSG_LASTUPDATE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_lastupdate";
    private static final String MSG_PAYMENTNOTACTIVE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_paymentnotactive";
    private static final String MSG_NEARLYEMPTY_CREATE_ACCOUNT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_nearlyEmptyCreateAccount";
    private static final String MSG_NEARLYEXPIRED_CREATE_ACCOUNT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_nearlyExpiredCreateAccount";
    private static final String MSG_SESSIONSPENT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_sessionspent";
    private static final String MSG_TOTALSPENT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_totalspent";
    private static final String MSG_NO_ACTIVE_ACCOUNT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_noActiveAccount";
    private static final String MSG_ENABLE_AUTO_SWITCH = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_enableAutoSwitch";
    private static final String MSG_WITH_COSTS = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_withCosts";
    private static final String MSG_CREATE_ACCOUNT_TITLE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_createAccountTitle";
    private static final String MSG_CHOOSE_FREE_SERVICES_ONLY = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_chooseFreeServicesOnly";
    private static final String MSG_EXPERIMENTAL = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_experimental";
    private static final String MSG_TITLE_FLAT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_title_flat";
    private static final String MSG_VALID_UNTIL = AccountSettingsPanel.MSG_ACCOUNT_VALID;
    private static final String MSG_EURO_BALANCE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_euro_balance";
    private static final String MSG_NO_FLATRATE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_no_flatrate";
    private static final String MSG_WANNA_CHARGE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_wannaCharge";
    private static final String MSG_TT_CREATE_ACCOUNT = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_ttCreateAccount";
    private static final String MSG_FREE_OF_CHARGE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_freeOfCharge";
    private static final String MSG_OPEN_TRANSACTION = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_openTransaction";
    private static final String MSG_CREATE_ACCOUNT_QUESTION = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_createAccountQuestion";
    private static final String MSG_MAYBE_LATER = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + "_maybeLater";
    public static final String MSG_MONTHLY_RATE_USED = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".monthlyRateUsed";
    public static final String MSG_MONTHLY_RATE_USED_TITLE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".monthlyRateUsedTitle";
    public static final String MSG_MONTHLY_RATE_ALMOST_USED = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".monthlyRateAlmostUsed";
    public static final String MSG_MONTHLY_RATE_OVERUSAGE_EXPLAIN = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".monthlyRateOverusage";
    public static final String MSG_LBL_MONTHLY_AVAILABLE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".lblMonthlyAvailable";
    public static final String MSG_BTN_ADDITIONAL_RATE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".btnAdditionalRate";
    public static final String MSG_ALTERNATIVE_ADDITIONAL_RATE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".alternativeBuyAdditionalRate";
    private static final String MSG_TEST_PREMIUM_BETA = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".testPremiumBeta";
    public static final String MSG_ACCOUNT_BLOCKED_TOOLTIP = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".accountBlockedTooltip";
    public static final String MSG_ACCOUNT_BLOCKED = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".accountBlocked";
    public static final String MSG_HINT_AUTO_BALANCE_UPDATES = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".hintAutoBalanceUpdates";
    public static final String MSG_WEIRD_ACCONT_STATUS = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".weirdAccountStatus";
    private static final String MSG_TITLE_USE_MONTHLY_VOLUME_PREMATURELY = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".titleUseMonthlyVolumePrematurely";
    private static final String MSG_CBX_ACCEPT_OVERUSAGE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".cbxAcceptOverusage";
    private static final String MSG_HINT_ACCEPT_OVERUSAGE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".hintAcceptOverusage";
    private static final String MSG_WAIT_REQUESTING_DATA_VOLUME = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".waitRequestingDataVolume";
    private static final String MSG_SUCCESS_OVERUSAGE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".successOverusage";
    private static final String MSG_FAILED_OVERUSAGE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".failedOverusage";
    private static final String MSG_FAILED_OVERUSAGE_NO_CONNECTION = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".failedOverusageNoConnection";
    private static final String MSG_FAILED_OVERUSAGE_NOT_SYNCHRONIZED = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".failedOverusageNotSynchronized";
    private static final String MSG_FAILED_OVERUSAGE_DOUBLE = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".failedOverusageDouble";
    private static final String MSG_BLOCKED_REASONS = (class$jap$pay$PaymentMainPanel == null ? (class$jap$pay$PaymentMainPanel = PaymentMainPanel.class$("jap.pay.PaymentMainPanel")) : class$jap$pay$PaymentMainPanel).getName() + ".blockedReasons";
    private static final String[] MSG_PAYMENT_ERRORS = new String[]{"_xmlSuccess", "_xmlErrorInternal", "_xmlErrorWrongFormat", "_xmlErrorWrongData", "_xmlErrorKeyNotFound", "_xmlErrorBadSignature", "_xmlErrorBadRequest", "_xmlErrorNoAccountCert", "_xmlErrorNoBalance", "_xmlErrorNoConfirmation", "_accountempty", "_xmlErrorCascadeLength", "_xmlErrorDatabase", "_xmlErrorInsufficientBalance", "_xmlErrorNoFlatrateOffered", "_xmlErrorInvalidCode", "_xmlErrorInvalidCC", "_xmlErrorInvalidPriceCerts", "_xmlErrorMultipleLogin", "_xmlErrorNoRecordFound", "_xmlErrorPartialSuccess", "_xmlErrorAccountBlocked"};
    private ImageIcon[] m_accountIcons;
    private JAPProgressBar m_BalanceProgressBar;
    private JAPProgressBar m_BalanceSmallProgressBar;
    private JButton m_btnCreateAccount;
    private JLabel m_BalanceText;
    private JLabel m_BalanceTextSmall;
    private JobQueue m_queueUpdate;
    private JLabel m_dateLabel;
    private JAPNewView m_view;
    private MyPaymentListener m_MyPaymentListener = new MyPaymentListener();
    private boolean m_notifiedEmpty = false;
    private boolean m_bShowingError = false;
    private Object SYNC_SHOW_ERROR = new Object();
    private JLabel m_labelTotalSpent;
    private JLabel m_labelSessionSpent;
    private JLabel m_labelTitle;
    private JLabel m_labelTitleSmall;
    private JLabel m_labelTotalSpentHeader;
    private JLabel m_labelSessionSpentHeader;
    private JLabel m_labelValidUntilHeader;
    private JLabel m_labelValidUntil;
    private JLabel m_labelMonthlyAvailableHeader;
    private JLabel m_labelMonthlyAvailable;
    private JButton m_btnDetails;
    private long m_spentThisSession;
    static /* synthetic */ Class class$jap$pay$PaymentMainPanel;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    public PaymentMainPanel(JAPNewView jAPNewView, final JLabel jLabel) {
        super(jAPNewView, true);
        this.m_view = jAPNewView;
        this.m_queueUpdate = new JobQueue("Payment Panel Update");
        this.loadIcons();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_labelTitle = new JLabel(JAPMessages.getString(MSG_TITLE) + ":");
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)this.m_labelTitle, gridBagConstraints);
        JPanel jPanel2 = new JPanel();
        Dimension dimension = new Dimension(this.m_labelTitle.getFontMetrics(this.m_labelTitle.getFont()).charWidth('9') * 6, 1);
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.m_BalanceText = new JLabel(" ");
        this.m_BalanceText.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridx = 2;
        jPanel.add((Component)this.m_BalanceText, gridBagConstraints);
        JLabel jLabel2 = new JLabel("", 4){

            public Dimension getPreferredSize() {
                return jLabel.getPreferredSize();
            }
        };
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        jPanel.add((Component)jLabel2, gridBagConstraints);
        this.m_BalanceProgressBar = new JAPProgressBar();
        this.m_BalanceProgressBar.setMinimum(0);
        this.m_BalanceProgressBar.setMaximum(6);
        this.m_BalanceProgressBar.setBorderPainted(false);
        gridBagConstraints.gridx = 4;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        this.m_btnDetails = new JButton(JAPMessages.getString(ActivePaymentDetails.MSG_PAYBUTTON));
        this.m_btnDetails.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                String string = JAPController.getInstance().getCurrentMixCascade().getPIID();
                if (string == null || string.trim().length() == 0) {
                    PaymentMainPanel.this.m_view.showPaymentDialog(null);
                } else {
                    PaymentMainPanel.this.m_view.showPaymentDialog(string);
                }
            }
        });
        jPanel.add(this.m_btnDetails);
        this.m_labelValidUntilHeader = new JLabel(JAPMessages.getString(MSG_VALID_UNTIL));
        gridBagConstraints.insets = new Insets(10, 20, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelValidUntilHeader, gridBagConstraints);
        jPanel2 = new JPanel();
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.m_labelValidUntil = new JLabel(" ");
        this.m_labelValidUntil.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelValidUntil, gridBagConstraints);
        this.m_labelSessionSpentHeader = new JLabel(JAPMessages.getString(MSG_SESSIONSPENT));
        this.m_labelSessionSpentHeader.setVisible(false);
        gridBagConstraints.insets = new Insets(10, 20, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelSessionSpentHeader, gridBagConstraints);
        jPanel2 = new JPanel();
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.m_labelSessionSpent = new JLabel(" ");
        this.m_labelSessionSpent.setVisible(false);
        this.m_labelSessionSpent.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelSessionSpent, gridBagConstraints);
        this.m_labelTotalSpentHeader = new JLabel(JAPMessages.getString(MSG_TOTALSPENT));
        gridBagConstraints.insets = new Insets(10, 20, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelTotalSpentHeader, gridBagConstraints);
        jPanel2 = new JPanel();
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.m_labelTotalSpent = new JLabel(" ");
        this.m_labelTotalSpent.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelTotalSpent, gridBagConstraints);
        this.m_labelMonthlyAvailableHeader = new JLabel(JAPMessages.getString(MSG_LBL_MONTHLY_AVAILABLE));
        gridBagConstraints.insets = new Insets(10, 20, 0, 0);
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelMonthlyAvailableHeader, gridBagConstraints);
        jPanel2 = new JPanel();
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.m_labelMonthlyAvailable = new JLabel(" ");
        this.m_labelMonthlyAvailable.setHorizontalAlignment(4);
        gridBagConstraints.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_labelMonthlyAvailable, gridBagConstraints);
        this.m_dateLabel = new JLabel(JAPMessages.getString(MSG_LASTUPDATE));
        this.m_dateLabel.setVisible(false);
        gridBagConstraints.insets = new Insets(10, 20, 0, 0);
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_dateLabel, gridBagConstraints);
        jPanel2 = new JPanel();
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.setFullPanel(jPanel);
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        this.m_labelTitleSmall = new JLabel(JAPMessages.getString(MSG_TITLE) + ":");
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        jPanel3.add((Component)this.m_labelTitleSmall, gridBagConstraints);
        jPanel2 = new JPanel();
        dimension = new Dimension(jLabel2.getFontMetrics(jLabel2.getFont()).charWidth('9') * 6, 1);
        jPanel2.setPreferredSize(dimension);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add((Component)jPanel2, gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridx = 2;
        this.m_BalanceTextSmall = new JLabel(" ");
        this.m_BalanceTextSmall.setHorizontalAlignment(4);
        this.m_btnCreateAccount = new JButton(JAPMessages.getString(AccountSettingsPanel.MSG_ACCOUNTCREATE) + "...");
        this.m_btnCreateAccount.setToolTipText(JAPMessages.getString(MSG_TT_CREATE_ACCOUNT));
        jPanel3.add((Component)this.m_BalanceTextSmall, gridBagConstraints);
        this.m_btnCreateAccount.setVisible(false);
        this.m_BalanceSmallProgressBar = new JAPProgressBar();
        this.m_BalanceSmallProgressBar.setMinimum(0);
        this.m_BalanceSmallProgressBar.setMaximum(6);
        this.m_BalanceSmallProgressBar.setBorderPainted(false);
        jLabel2 = new JLabel("", 4){

            public Dimension getPreferredSize() {
                Dimension dimension = new Dimension(jLabel.getPreferredSize().width + (((PaymentMainPanel)PaymentMainPanel.this).m_BalanceSmallProgressBar.getPreferredSize().width - ((PaymentMainPanel)PaymentMainPanel.this).m_btnDetails.getPreferredSize().width), jLabel.getPreferredSize().height);
                return dimension;
            }
        };
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridx = 3;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        jPanel3.add((Component)jLabel2, gridBagConstraints);
        gridBagConstraints.gridx = 4;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        jPanel3.add((Component)this.m_btnDetails, gridBagConstraints);
        this.setSmallPanel(jPanel3);
        MouseAdapter mouseAdapter = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() == PaymentMainPanel.this.m_btnCreateAccount) {
                    String string = JAPController.getInstance().getCurrentMixCascade().getPIID();
                    if (string == null || string.trim().length() == 0) {
                        PaymentMainPanel.this.m_view.showPaymentDialog(null);
                    } else {
                        PaymentMainPanel.this.m_view.showPaymentDialog(string);
                    }
                    return;
                }
                String string = ((JLabel)mouseEvent.getSource()).getToolTipText();
                if (string != null && string.equals(JAPMessages.getString(AccountSettingsPanel.MSG_BILLING_ERROR_TOOLTIP))) {
                    PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
                    if (payAccount != null) {
                        XMLBalance xMLBalance = payAccount.getBalance();
                        if (xMLBalance != null && xMLBalance.getStartDate().after(new Timestamp(System.currentTimeMillis()))) {
                            JAPDialog.showWarningDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(AccountSettingsPanel.MSG_WRONG_TIME_TOO_EARLY, new String[]{"" + payAccount.getAccountNumber(), Util.formatTimestamp(xMLBalance.getStartDate(), false), Util.formatTimestamp(new Date(), false)}));
                        } else {
                            String string2 = "";
                            if (payAccount.getBI() != null) {
                                string2 = payAccount.getBI().getName();
                            }
                            JAPDialog.LinkedInformation linkedInformation = new JAPDialog.LinkedInformation("payment@jondos.de");
                            JAPDialog.showWarningDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(AccountSettingsPanel.MSG_BILLING_ERROR_EXPLAIN, new String[]{string2, "" + Util.formatBytesValueWithUnit(payAccount.getCurrentCreditCalculated() - payAccount.getCurrentCreditFromBalance(), 2), "" + payAccount.getAccountNumber()}), (JAPDialog.ILinkedInformation)linkedInformation);
                        }
                    }
                } else if (string != null && string.equals(JAPMessages.getString(MSG_ACCOUNT_BLOCKED_TOOLTIP))) {
                    PaymentMainPanel.showAccountBlockedDialog(PayAccountsFile.getInstance().getActiveAccount(), JAPController.getInstance().getCurrentView());
                } else if (((JLabel)mouseEvent.getSource()).getCursor() != Cursor.getDefaultCursor()) {
                    if (((JLabel)mouseEvent.getSource()).getName() != null && ((JLabel)mouseEvent.getSource()).getName().equals("Transaction")) {
                        PaymentMainPanel.this.m_view.showConfigDialog("PAYMENT_TAB", PayAccountsFile.getInstance().getActiveAccount());
                    } else {
                        PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
                        if (payAccount != null && payAccount.canDoMonthlyOverusage(new Timestamp(System.currentTimeMillis()))) {
                            PaymentMainPanel.showMonthlyOverusageQuestion(payAccount, JAPController.getInstance().getCurrentView(), true, PaymentMainPanel.this.m_view);
                        } else {
                            String string3 = JAPController.getInstance().getCurrentMixCascade().getPIID();
                            if (payAccount != null) {
                                PaymentMainPanel.this.m_view.showPaymentDialog(payAccount.getPIID());
                            } else if (string3 == null || string3.trim().length() == 0) {
                                PaymentMainPanel.this.m_view.showPaymentDialog(null);
                            } else {
                                PaymentMainPanel.this.m_view.showPaymentDialog(string3);
                            }
                        }
                    }
                }
            }
        };
        this.m_BalanceTextSmall.addMouseListener(mouseAdapter);
        this.m_BalanceText.addMouseListener(mouseAdapter);
        this.m_btnCreateAccount.addMouseListener(mouseAdapter);
        PayAccountsFile.getInstance().addPaymentListener(this.m_MyPaymentListener);
        this.updateDisplay(PayAccountsFile.getInstance().getActiveAccount(), false);
    }

    public static void showAccountBlockedDialog(PayAccount payAccount, Component component) {
        XMLErrorMessage xMLErrorMessage = new XMLErrorMessage(21, payAccount);
        JAPDialog.showErrorDialog(component, PaymentMainPanel.translateBIError(xMLErrorMessage), (JAPDialog.ILinkedInformation)PaymentMainPanel.translateBIErrorAdapter(xMLErrorMessage, false));
    }

    public static JAPDialog.LinkedInformationAdapter translateBIErrorAdapter(XMLErrorMessage xMLErrorMessage, final boolean bl) {
        if (xMLErrorMessage.getXmlErrorCode() == 21) {
            return new JAPDialog.AbstractLinkedURLAdapter("premium"){

                public boolean isOnTop() {
                    return bl;
                }

                public URL getUrl() {
                    try {
                        return new URL("mailto:payment@jondos.de");
                    }
                    catch (MalformedURLException malformedURLException) {
                        LogHolder.log(3, LogType.GUI, malformedURLException);
                        return null;
                    }
                }
            };
        }
        return new JAPDialog.LinkedInformationAdapter(){

            public boolean isOnTop() {
                return bl;
            }
        };
    }

    public static String translateBIError(XMLErrorMessage xMLErrorMessage) {
        String string = "";
        if (xMLErrorMessage.getXmlErrorCode() >= 0 && xMLErrorMessage.getXmlErrorCode() < MSG_PAYMENT_ERRORS.length) {
            String string2 = "";
            if (xMLErrorMessage.getXmlErrorCode() == 21) {
                string2 = JAPMessages.getString(MSG_BLOCKED_REASONS) + "<br/><br/>";
            }
            string = string + JAPMessages.getString(MSG_PAYMENT_ERRORS[xMLErrorMessage.getXmlErrorCode()], new String[]{"" + xMLErrorMessage.getAccountNumber(), string2});
        } else {
            string = string + xMLErrorMessage.getMessage();
        }
        return string;
    }

    public boolean isShowingError() {
        return this.m_bShowingError;
    }

    public void stopUpdateQueue() {
        this.m_queueUpdate.stop();
    }

    private void updateDisplay(final PayAccount payAccount, final boolean bl) {
        if (PayAccountsFile.getInstance().getActiveAccount() != payAccount) {
            return;
        }
        JobQueue.Job job = new JobQueue.Job(true){

            public void runJob() {
                if (payAccount == null) {
                    PaymentMainPanel.this.m_labelValidUntil.setText("");
                    PaymentMainPanel.this.m_BalanceText.setVisible(false);
                    PaymentMainPanel.this.m_btnCreateAccount.setVisible(true);
                    PaymentMainPanel.this.m_BalanceText.setIcon(null);
                    PaymentMainPanel.this.m_BalanceText.setText(JAPMessages.getString(AccountSettingsPanel.MSG_ACCOUNTCREATE) + "...");
                    PaymentMainPanel.this.m_BalanceText.setForeground(PaymentMainPanel.this.m_labelValidUntil.getForeground());
                    PaymentMainPanel.this.m_BalanceProgressBar.setValue(0);
                    PaymentMainPanel.this.m_BalanceProgressBar.setEnabled(false);
                    PaymentMainPanel.this.m_spentThisSession = 0L;
                    PaymentMainPanel.this.m_labelSessionSpent.setText("");
                    PaymentMainPanel.this.m_labelTotalSpent.setText("");
                    PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(false);
                    PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(false);
                } else {
                    PaymentMainPanel.this.m_btnCreateAccount.setVisible(false);
                    PaymentMainPanel.this.m_BalanceText.setVisible(true);
                    final XMLBalance xMLBalance = payAccount.getBalance();
                    final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    GregorianCalendar gregorianCalendar = null;
                    GregorianCalendar gregorianCalendar2 = null;
                    Timestamp timestamp2 = null;
                    if (xMLBalance != null) {
                        timestamp2 = xMLBalance.getFlatEnddate();
                        gregorianCalendar = new GregorianCalendar();
                        gregorianCalendar2 = new GregorianCalendar();
                        gregorianCalendar.setTime(timestamp2);
                    }
                    if (xMLBalance != null && payAccount.isCharged(timestamp)) {
                        PaymentMainPanel.this.m_BalanceProgressBar.setEnabled(false);
                        PaymentMainPanel.this.m_BalanceText.setText(Util.formatBytesValueWithUnit(payAccount.getCurrentCredit(), 2));
                        PaymentMainPanel.this.m_BalanceText.setForeground(PaymentMainPanel.this.m_labelValidUntil.getForeground());
                        PaymentMainPanel.this.m_BalanceText.setIcon(null);
                        if (payAccount.hasExpired(timestamp) && xMLBalance.getStartDate().after(timestamp)) {
                            PaymentMainPanel.this.m_BalanceText.setIcon(GUIUtils.loadImageIcon("warning.gif"));
                            PaymentMainPanel.this.m_BalanceText.setCursor(Cursor.getPredefinedCursor(12));
                            PaymentMainPanel.this.m_BalanceText.setToolTipText(JAPMessages.getString(AccountSettingsPanel.MSG_BILLING_ERROR_TOOLTIP));
                        } else if (payAccount.isBlocked()) {
                            PaymentMainPanel.this.m_BalanceText.setIcon(GUIUtils.loadImageIcon("warning.gif"));
                            PaymentMainPanel.this.m_BalanceText.setCursor(Cursor.getPredefinedCursor(12));
                            PaymentMainPanel.this.m_BalanceText.setToolTipText(JAPMessages.getString(MSG_ACCOUNT_BLOCKED_TOOLTIP));
                        } else {
                            PaymentMainPanel.this.m_BalanceText.setIcon(null);
                            PaymentMainPanel.this.m_BalanceText.setCursor(Cursor.getDefaultCursor());
                            PaymentMainPanel.this.m_BalanceText.setToolTipText(null);
                        }
                        PaymentMainPanel.this.m_labelValidUntil.setText(Util.formatTimestamp(xMLBalance.getFlatEnddate(), false));
                        long l = 500000000L;
                        long l2 = payAccount.getCurrentCredit();
                        double d = (double)l2 / (double)l;
                        if (d > 0.86) {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(6);
                        } else if (d > 0.71) {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(5);
                        } else if (d > 0.57) {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(4);
                        } else if (d > 0.43) {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(3);
                        } else if (d > 0.29) {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(2);
                        } else if ((double)l2 > 0.14) {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(1);
                        } else {
                            PaymentMainPanel.this.m_BalanceProgressBar.setValue(0);
                        }
                        PaymentMainPanel.this.m_BalanceProgressBar.setEnabled(true);
                        if (xMLBalance.getVolumeBytesMonthly() > 0L) {
                            PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(true);
                            PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(true);
                            PaymentMainPanel.this.m_labelMonthlyAvailable.setText(Util.formatBytesValueWithUnit(xMLBalance.getVolumeBytesMonthly(), 2));
                        } else {
                            PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(false);
                            PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(false);
                        }
                    } else {
                        PaymentMainPanel.this.m_BalanceText.setIcon(null);
                        PaymentMainPanel.this.m_BalanceProgressBar.setValue(0);
                        PaymentMainPanel.this.m_BalanceProgressBar.setEnabled(false);
                        if (xMLBalance == null) {
                            PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(false);
                            PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(false);
                            PaymentMainPanel.this.m_labelValidUntil.setText("");
                            PaymentMainPanel.this.m_BalanceText.setCursor(Cursor.getDefaultCursor());
                            PaymentMainPanel.this.m_BalanceText.setToolTipText(null);
                            PaymentMainPanel.this.m_BalanceText.setIcon(null);
                            PaymentMainPanel.this.m_BalanceText.setText(Util.formatBytesValueWithUnit(0L, 2));
                            PaymentMainPanel.this.m_BalanceText.setForeground(PaymentMainPanel.this.m_labelValidUntil.getForeground());
                        } else {
                            String string = Util.formatTimestamp(timestamp2, false);
                            boolean bl2 = false;
                            PaymentMainPanel.this.m_BalanceText.setCursor(Cursor.getPredefinedCursor(12));
                            PaymentMainPanel.this.m_BalanceText.setForeground(Color.blue);
                            PaymentMainPanel.this.m_BalanceText.setIcon(GUIUtils.loadImageIcon("info.png"));
                            PaymentMainPanel.this.m_BalanceText.setToolTipText(JAPMessages.getString(MSG_TT_CREATE_ACCOUNT));
                            if (payAccount.getCurrentCredit() == 0L && (xMLBalance.getVolumeBytesMonthly() == 0L || gregorianCalendar.get(2) == gregorianCalendar2.get(2))) {
                                PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(false);
                                PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(false);
                                PaymentMainPanel.this.m_labelValidUntil.setText("");
                            } else if (timestamp2 != null && timestamp2.after(timestamp)) {
                                if (xMLBalance.getVolumeBytesMonthly() > 0L) {
                                    PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(true);
                                    PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(true);
                                    PaymentMainPanel.this.m_labelMonthlyAvailable.setText(Util.formatBytesValueWithUnit(xMLBalance.getVolumeBytesMonthly(), 2));
                                } else {
                                    PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(false);
                                    PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(false);
                                }
                                PaymentMainPanel.this.m_labelValidUntil.setText(string);
                            } else {
                                PaymentMainPanel.this.m_labelMonthlyAvailableHeader.setVisible(false);
                                PaymentMainPanel.this.m_labelMonthlyAvailable.setVisible(false);
                                bl2 = true;
                                PaymentMainPanel.this.m_labelValidUntil.setText(JAPMessages.getString(AccountSettingsPanel.MSG_EXPIRED));
                            }
                            if (payAccount.getCurrentBytes() > 0L && bl2) {
                                PaymentMainPanel.this.m_BalanceText.setText(JAPMessages.getString(AccountSettingsPanel.MSG_EXPIRED));
                            } else if (!bl2 && !payAccount.isUsed()) {
                                if (!payAccount.isTransactionExpired()) {
                                    PaymentMainPanel.this.m_BalanceText.setText(JAPMessages.getString(AccountSettingsPanel.MSG_NO_TRANSACTION));
                                    PaymentMainPanel.this.m_BalanceText.setToolTipText(JAPMessages.getString(AccountSettingsPanel.MSG_SHOW_TRANSACTION_DETAILS));
                                    PaymentMainPanel.this.m_BalanceText.setName("Transaction");
                                } else {
                                    PaymentMainPanel.this.m_BalanceText.setVisible(false);
                                    PaymentMainPanel.this.m_btnCreateAccount.setVisible(true);
                                    PaymentMainPanel.this.m_BalanceText.setText("");
                                    PaymentMainPanel.this.m_BalanceText.setToolTipText(null);
                                }
                            } else {
                                GregorianCalendar gregorianCalendar3 = new GregorianCalendar();
                                gregorianCalendar3.add(2, 1);
                                PaymentMainPanel.this.m_BalanceText.setText(JAPMessages.getString(AccountSettingsPanel.MSG_NO_CREDIT));
                            }
                        }
                    }
                    PaymentMainPanel.this.m_spentThisSession = AIControlChannel.getBytes();
                    PaymentMainPanel.this.m_labelSessionSpent.setText(Util.formatBytesValueWithUnit(PaymentMainPanel.this.m_spentThisSession, 2));
                    PaymentMainPanel.this.m_labelTotalSpent.setText(Util.formatBytesValueWithUnit(payAccount.getCurrentSpent(), 2));
                    if (JAPController.getInstance().isConfigAssistantShown() && payAccount.getCurrentCredit() + payAccount.getCurrentSpent() <= 125000000L) {
                        PaymentMainPanel.this.m_notifiedEmpty = true;
                    }
                    if (!JAPModel.getInstance().isPaymentPopupsHidden()) {
                        if (bl && payAccount.getCurrentCredit() <= 125000000L && !PaymentMainPanel.this.m_notifiedEmpty && payAccount.isCharged(timestamp) && PayAccountsFile.getInstance().getAlternativeChargedAccount(payAccount.getPIID()) == null) {
                            PaymentMainPanel.this.m_notifiedEmpty = true;
                            new Thread(new Runnable(){

                                public void run() {
                                    if (xMLBalance.getVolumeBytesMonthly() > 0L && xMLBalance.canDoMonthlyOverusage(timestamp)) {
                                        PaymentMainPanel.showMonthlyOverusageQuestion(payAccount, JAPController.getInstance().getCurrentView(), true, PaymentMainPanel.this.m_view);
                                    } else if (JAPDialog.showYesNoDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_NEARLYEMPTY_CREATE_ACCOUNT))) {
                                        PaymentMainPanel.this.m_view.showPaymentDialog(JAPController.getInstance().getCurrentMixCascade().getPIID());
                                    }
                                }
                            }).start();
                        }
                        Timestamp timestamp3 = new Timestamp(System.currentTimeMillis() + 604800000L);
                        if (bl && !PaymentMainPanel.this.m_notifiedEmpty && payAccount.isCharged(timestamp) && !payAccount.isCharged(timestamp3) && PayAccountsFile.getInstance().getAlternativeChargedAccount(JAPController.getInstance().getCurrentMixCascade().getPIID()) == null && (xMLBalance.getVolumeBytesMonthly() == 0L || gregorianCalendar.get(2) == gregorianCalendar2.get(2))) {
                            PaymentMainPanel.this.m_notifiedEmpty = true;
                            new Thread(new Runnable(){

                                public void run() {
                                    if (JAPDialog.showYesNoDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_NEARLYEXPIRED_CREATE_ACCOUNT))) {
                                        PaymentMainPanel.this.m_view.showPaymentDialog(JAPController.getInstance().getCurrentMixCascade().getPIID());
                                    }
                                }
                            }).start();
                        }
                    }
                }
                PaymentMainPanel.this.m_BalanceTextSmall.setVisible(PaymentMainPanel.this.m_BalanceText.isVisible());
                PaymentMainPanel.this.m_BalanceTextSmall.setText(PaymentMainPanel.this.m_BalanceText.getText());
                PaymentMainPanel.this.m_BalanceTextSmall.setIcon(PaymentMainPanel.this.m_BalanceText.getIcon());
                PaymentMainPanel.this.m_BalanceTextSmall.setForeground(PaymentMainPanel.this.m_BalanceText.getForeground());
                PaymentMainPanel.this.m_BalanceTextSmall.setToolTipText(PaymentMainPanel.this.m_BalanceText.getToolTipText());
                PaymentMainPanel.this.m_BalanceTextSmall.setCursor(PaymentMainPanel.this.m_BalanceText.getCursor());
                PaymentMainPanel.this.m_BalanceTextSmall.setIcon(PaymentMainPanel.this.m_BalanceText.getIcon());
                PaymentMainPanel.this.m_BalanceTextSmall.setName(PaymentMainPanel.this.m_BalanceText.getName());
                PaymentMainPanel.this.m_BalanceSmallProgressBar.setValue(PaymentMainPanel.this.m_BalanceProgressBar.getValue());
                PaymentMainPanel.this.m_BalanceSmallProgressBar.setEnabled(PaymentMainPanel.this.m_BalanceProgressBar.isEnabled());
            }
        };
        this.m_queueUpdate.addJob(job);
    }

    public static void showMonthlyOverusageQuestion(PayAccount payAccount, Component component, boolean bl, IPaymentDialogPresentator iPaymentDialogPresentator) {
        String string = "";
        if (payAccount.getCurrentCredit() <= 0L) {
            string = string + JAPMessages.getString(MSG_MONTHLY_RATE_USED);
        } else if (bl) {
            string = string + JAPMessages.getString(MSG_MONTHLY_RATE_ALMOST_USED);
        }
        PaymentMainPanel.showMonthlyOverusageQuestion(payAccount, component, string, JAPMessages.getString(MSG_BTN_ADDITIONAL_RATE), iPaymentDialogPresentator);
    }

    public static int showMonthlyOverusageQuestion(final PayAccount payAccount, Component component, String string, final String string2, IPaymentDialogPresentator iPaymentDialogPresentator) {
        XMLBalance xMLBalance = payAccount.getBalance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (xMLBalance == null) {
            return 1;
        }
        XMLBalance.MonthlyOverusage monthlyOverusage = xMLBalance.calculateMonthlyOverusage(timestamp);
        if (string.trim().length() > 0) {
            string = "<p><b>" + string + "</b></p><br/>";
        }
        string = string + "<p>" + JAPMessages.getString(MSG_MONTHLY_RATE_OVERUSAGE_EXPLAIN, new Object[]{Util.formatTimestamp(monthlyOverusage.m_tEndOfCurrentPeriod, false), NumberFormat.getInstance(JAPMessages.getLocale()).format(monthlyOverusage.m_dFactor), Util.formatBytesValueWithUnit(monthlyOverusage.m_lAdditionalTraffic, 3), Util.formatBytesValueWithUnit((long)((double)payAccount.getVolumeBytesMonthly() / monthlyOverusage.m_dFactor), 2)}) + "</p>";
        if (iPaymentDialogPresentator != null) {
            string = string + "<br/>" + JAPMessages.getString(MSG_ALTERNATIVE_ADDITIONAL_RATE);
        }
        JAPDialog jAPDialog = new JAPDialog(component, JAPMessages.getString(MSG_TITLE_USE_MONTHLY_VOLUME_PREMATURELY));
        final JCheckBox jCheckBox = new JCheckBox(JAPMessages.getString(MSG_CBX_ACCEPT_OVERUSAGE, new String[]{Util.formatBytesValueWithUnit(monthlyOverusage.m_lAdditionalTraffic, 3), NumberFormat.getInstance(JAPMessages.getLocale()).format((monthlyOverusage.m_dFactor - 1.0) * 100.0)}));
        SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(jAPDialog, string, new DialogContentPaneOptions(1)){

            public DialogContentPane.CheckError checkYesOK() {
                DialogContentPane.CheckError checkError = null;
                if (!jCheckBox.isSelected()) {
                    checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_HINT_ACCEPT_OVERUSAGE));
                }
                return checkError;
            }

            public boolean hideButtonNo() {
                return false;
            }

            public String getButtonNoText() {
                return string2;
            }

            public String getButtonYesOKText() {
                return JAPMessages.getString(DialogContentPane.MSG_OK);
            }
        };
        simpleWizardContentPane.setDefaultButtonOperation(49160);
        simpleWizardContentPane.getContentPane().add(jCheckBox);
        final IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            private Object info = null;

            public void run() {
                try {
                    this.info = payAccount.requestMonthlyOverusage();
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.PAY, exception);
                    this.info = exception;
                }
            }

            public Object getValue() {
                return this.info;
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_WAIT_REQUESTING_DATA_VOLUME), (DialogContentPane)simpleWizardContentPane, (Runnable)iReturnRunnable){

            public boolean isMoveBackAllowed() {
                return iReturnRunnable.getValue() == null || !(iReturnRunnable.getValue() instanceof XMLAccountInfo);
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        final SimpleWizardContentPane simpleWizardContentPane2 = new SimpleWizardContentPane(jAPDialog, "<p>" + JAPMessages.getString(MSG_SUCCESS_OVERUSAGE, new String[]{Util.formatBytesValueWithUnit(monthlyOverusage.m_lAdditionalTraffic, 2), Util.formatTimestamp(monthlyOverusage.m_tEndOfCurrentPeriod, false), NumberFormat.getInstance(JAPMessages.getLocale()).format(monthlyOverusage.m_dFactor)}) + "</p><br>" + JAPMessages.getString(MSG_HINT_AUTO_BALANCE_UPDATES), new DialogContentPane.Layout(JAPMessages.getString(JAPDialog.MSG_TITLE_INFO)), new DialogContentPaneOptions(-1, (DialogContentPane)workerContentPane)){

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }

            public boolean isSkippedAsNextContentPane() {
                return iReturnRunnable.getValue() == null || !(iReturnRunnable.getValue() instanceof XMLAccountInfo);
            }
        };
        SimpleWizardContentPane simpleWizardContentPane3 = new SimpleWizardContentPane(jAPDialog, "Text", new DialogContentPane.Layout(JAPMessages.getString(JAPDialog.MSG_TITLE_ERROR)), new DialogContentPaneOptions(simpleWizardContentPane2)){

            public boolean hideButtonYesOK() {
                return true;
            }

            public boolean hideButtonCancel() {
                return false;
            }

            public boolean isSkippedAsNextContentPane() {
                return !simpleWizardContentPane2.isSkippedAsNextContentPane();
            }

            public DialogContentPane.CheckError checkUpdate() {
                String string = JAPMessages.getString(MSG_FAILED_OVERUSAGE) + " ";
                string = iReturnRunnable.getValue() == null || !(iReturnRunnable.getValue() instanceof XMLErrorMessage) ? string + JAPMessages.getString(MSG_FAILED_OVERUSAGE_NO_CONNECTION) : (((XMLErrorMessage)iReturnRunnable.getValue()).getXmlErrorCode() == 20 ? string + JAPMessages.getString(MSG_FAILED_OVERUSAGE_DOUBLE) : (((XMLErrorMessage)iReturnRunnable.getValue()).getXmlErrorCode() == 22 ? string + JAPMessages.getString(MSG_FAILED_OVERUSAGE_NOT_SYNCHRONIZED) : string + JAPMessages.getString(MSG_FAILED_OVERUSAGE_NO_CONNECTION)));
                this.setText("<font color=\"red\">" + string + "</font>");
                return null;
            }
        };
        simpleWizardContentPane.pack();
        jAPDialog.setResizable(false);
        jAPDialog.setVisible(true);
        if (simpleWizardContentPane.getButtonValue() == 1) {
            if (iPaymentDialogPresentator != null) {
                iPaymentDialogPresentator.showPaymentDialog(payAccount.getPIID());
            }
            return 0;
        }
        if (iReturnRunnable.getValue() != null && iReturnRunnable.getValue() instanceof XMLAccountInfo) {
            return 0;
        }
        return 2;
    }

    private void allowShowingError() {
        new Thread(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Object object = PaymentMainPanel.this.SYNC_SHOW_ERROR;
                synchronized (object) {
                    PaymentMainPanel.this.m_bShowingError = false;
                }
            }
        }.start();
    }

    private void loadIcons() {
        this.m_accountIcons = new ImageIcon[JAPConstants.ACCOUNTICONFNARRAY.length];
        for (int i = 0; i < JAPConstants.ACCOUNTICONFNARRAY.length; ++i) {
            this.m_accountIcons[i] = GUIUtils.loadImageIcon(JAPConstants.ACCOUNTICONFNARRAY[i], false);
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

    static {
        for (int i = 0; i < MSG_PAYMENT_ERRORS.length; ++i) {
            PaymentMainPanel.MSG_PAYMENT_ERRORS[i] = (class$jap$pay$PaymentMainPanel == null ? PaymentMainPanel.class$("jap.pay.PaymentMainPanel") : class$jap$pay$PaymentMainPanel).getName() + MSG_PAYMENT_ERRORS[i];
        }
    }

    private class MyPaymentListener
    implements IPaymentListener {
        private MyPaymentListener() {
        }

        public void accountActivated(PayAccount payAccount) {
            PaymentMainPanel.this.updateDisplay(payAccount, true);
        }

        public void accountAdded(PayAccount payAccount) {
        }

        public void accountRemoved(PayAccount payAccount) {
        }

        public void creditChanged(PayAccount payAccount) {
            PaymentMainPanel.this.updateDisplay(payAccount, true);
        }

        private String formatCascadeName(MixCascade mixCascade) {
            String string = mixCascade == null || mixCascade.getName() == null ? "" : ", <b>" + mixCascade.getName() + "</b>,";
            return string;
        }

        private String formatOrganisation(String string) {
            string = "JonDos GmbH";
            String string2 = string == null ? "" : "<b>" + string + "</b>";
            return string2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void accountCertRequested(final MixCascade mixCascade) throws AccountEmptyException {
            if (PayAccountsFile.getInstance().isAIAccountErrorIgnored()) {
                return;
            }
            if (JAPModel.getInstance().isPaymentPopupsHidden()) {
                return;
            }
            PayAccountsFile payAccountsFile = PayAccountsFile.getInstance();
            AccountEmptyException accountEmptyException = null;
            final PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PaymentMainPanel.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(mixCascade.getPIID());
            final boolean bl = paymentInstanceDBEntry != null && paymentInstanceDBEntry.getFreeCodeURL() != null && PayAccountsFile.getInstance().isNewUser();
            String string = null;
            final JAPDialog.AbstractLinkedURLAdapter abstractLinkedURLAdapter = new JAPDialog.AbstractLinkedURLAdapter("premium"){

                public URL getUrl() {
                    if (bl) {
                        return paymentInstanceDBEntry.getFreeCodeURL();
                    }
                    return null;
                }

                public String getMessage() {
                    if (bl) {
                        return JAPMessages.getString(PayAccountsFile.MSG_GET_FREE_CODE);
                    }
                    return super.getMessage();
                }

                public void clicked(boolean bl2) {
                    JAPController.getInstance().allowDirectProxyDomain(this.getUrl());
                    super.clicked(bl2);
                }

                public boolean isOnTop() {
                    return true;
                }
            };
            Runnable runnable = null;
            if (paymentInstanceDBEntry != null) {
                string = paymentInstanceDBEntry.getOrganisation();
            }
            String string2 = string;
            final MixCascade mixCascade2 = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = PaymentMainPanel.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(mixCascade.getId());
            Object object = PaymentMainPanel.this.SYNC_SHOW_ERROR;
            synchronized (object) {
                if (PaymentMainPanel.this.m_bShowingError) {
                    return;
                }
                PaymentMainPanel.this.m_bShowingError = true;
            }
            object = payAccountsFile.getActiveAccount();
            if (payAccountsFile.getNumAccounts() == 0 || object != null && !((PayAccount)object).getPIID().equals(mixCascade.getPIID())) {
                accountEmptyException = new AccountEmptyException(mixCascade);
                PaymentInstanceDBEntry paymentInstanceDBEntry2 = mixCascade.getPaymentInstance();
                if (paymentInstanceDBEntry2 != null && paymentInstanceDBEntry2.isTest() || !JAPModel.getInstance().isCascadeAutoSwitched()) {
                    JAPController.getInstance().stop();
                } else if (TrustModel.getCurrentTrustModel().isPaymentForced() || !TrustModel.getCurrentTrustModel().hasFreeCascades()) {
                    if (JAPModel.getInstance().isConfigAssistantAutomaticallyShown()) {
                        accountEmptyException = null;
                        JAPController.getInstance().showInstallationAssistant(4);
                    } else {
                        JAPController.getInstance().stop();
                    }
                }
                runnable = new Runnable(){

                    public void run() {
                        int n;
                        JAPDialog.Options options = new JAPDialog.Options(2){

                            public String getCancelText() {
                                return JAPMessages.getString(MSG_MAYBE_LATER);
                            }
                        };
                        PaymentInstanceDBEntry paymentInstanceDBEntry = mixCascade.getPaymentInstance();
                        String string = MSG_CREATE_ACCOUNT_QUESTION;
                        if (paymentInstanceDBEntry != null && paymentInstanceDBEntry.isTest()) {
                            string = MSG_TEST_PREMIUM_BETA;
                        }
                        if ((n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), "" + JAPMessages.getString(MSG_WITH_COSTS, MyPaymentListener.this.formatCascadeName(mixCascade2)) + " " + JAPMessages.getString(string), JAPMessages.getString(MSG_CREATE_ACCOUNT_TITLE), options, 3, (JAPDialog.ILinkedInformation)abstractLinkedURLAdapter)) == 0) {
                            PaymentMainPanel.this.m_view.showPaymentDialog(mixCascade.getPIID());
                        } else if (JAPModel.getInstance().isCascadeAutoSwitched()) {
                            JAPController.getInstance().setAllowPaidServices(false);
                            if (!TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                                JAPController.getInstance().switchToNextMixCascade();
                            }
                            if (TrustModel.getCurrentTrustModel().hasFreeCascades()) {
                                JAPController.getInstance().start();
                            }
                        }
                        PaymentMainPanel.this.allowShowingError();
                    }
                };
            } else {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                if (object == null) {
                    accountEmptyException = new AccountEmptyException(mixCascade);
                    PaymentInstanceDBEntry paymentInstanceDBEntry3 = mixCascade.getPaymentInstance();
                    if (paymentInstanceDBEntry3 != null && paymentInstanceDBEntry3.isTest() || !JAPModel.getInstance().isCascadeAutoSwitched()) {
                        JAPController.getInstance().stop();
                    } else if (TrustModel.getCurrentTrustModel().isPaymentForced() || !TrustModel.getCurrentTrustModel().hasFreeCascades()) {
                        if (JAPModel.getInstance().isConfigAssistantAutomaticallyShown()) {
                            accountEmptyException = null;
                            JAPController.getInstance().showInstallationAssistant(4);
                        } else {
                            JAPController.getInstance().stop();
                        }
                    }
                    runnable = new Runnable(){

                        public void run() {
                            JAPDialog.showErrorDialog(JAPController.getInstance().getCurrentView(), "" + JAPMessages.getString(MSG_NO_ACTIVE_ACCOUNT), (JAPDialog.ILinkedInformation)abstractLinkedURLAdapter);
                            PaymentMainPanel.this.m_view.showConfigDialog("PAYMENT_TAB", null);
                            PaymentMainPanel.this.allowShowingError();
                        }
                    };
                } else if (!((PayAccount)object).isCharged(timestamp)) {
                    accountEmptyException = new AccountEmptyException(mixCascade, (PayAccount)object);
                    PaymentInstanceDBEntry paymentInstanceDBEntry4 = mixCascade.getPaymentInstance();
                    if (paymentInstanceDBEntry4 != null && paymentInstanceDBEntry4.isTest() || !JAPModel.getInstance().isCascadeAutoSwitched()) {
                        JAPController.getInstance().stop();
                    } else if (TrustModel.getCurrentTrustModel().isPaymentForced() || !TrustModel.getCurrentTrustModel().hasFreeCascades()) {
                        if (JAPModel.getInstance().isConfigAssistantAutomaticallyShown()) {
                            accountEmptyException = null;
                            JAPController.getInstance().showInstallationAssistant(4);
                        } else {
                            JAPController.getInstance().stop();
                        }
                    }
                    runnable = new Runnable((PayAccount)object, timestamp, mixCascade2, abstractLinkedURLAdapter, mixCascade){
                        private final /* synthetic */ PayAccount val$account;
                        private final /* synthetic */ Timestamp val$tNow;
                        private final /* synthetic */ MixCascade val$cascade;
                        private final /* synthetic */ JAPDialog.LinkedHelpContext val$helpAdapter;
                        private final /* synthetic */ MixCascade val$a_connectedCascade;
                        {
                            this.val$account = payAccount;
                            this.val$tNow = timestamp;
                            this.val$cascade = mixCascade;
                            this.val$helpAdapter = linkedHelpContext;
                            this.val$a_connectedCascade = mixCascade2;
                        }

                        public void run() {
                            if (PayAccount.canDoMonthlyOverusage(this.val$account, this.val$tNow)) {
                                PaymentMainPanel.showMonthlyOverusageQuestion(this.val$account, JAPController.getInstance().getCurrentView(), true, PaymentMainPanel.this.m_view);
                            } else {
                                Object object;
                                String string = "" + JAPMessages.getString(MSG_WITH_COSTS, MyPaymentListener.this.formatCascadeName(this.val$cascade)) + " ";
                                boolean bl = false;
                                String string2 = null;
                                if (this.val$account.isWaitingForTransaction()) {
                                    string = string + JAPMessages.getString(MSG_OPEN_TRANSACTION);
                                    bl = true;
                                } else {
                                    PaymentInstanceDBEntry paymentInstanceDBEntry;
                                    object = MSG_CREATE_ACCOUNT_QUESTION;
                                    if (this.val$cascade != null && (paymentInstanceDBEntry = this.val$cascade.getPaymentInstance()) != null && paymentInstanceDBEntry.isTest()) {
                                        object = MSG_TEST_PREMIUM_BETA;
                                    }
                                    string = string + JAPMessages.getString((String)object);
                                    string2 = JAPMessages.getString(MSG_CREATE_ACCOUNT_TITLE);
                                }
                                object = new JAPDialog.Options(2){

                                    public String getCancelText() {
                                        return JAPMessages.getString(MSG_MAYBE_LATER);
                                    }
                                };
                                int n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), string, string2, (JAPDialog.Options)object, 3, (JAPDialog.ILinkedInformation)this.val$helpAdapter);
                                if (n == 0) {
                                    if (bl) {
                                        PaymentMainPanel.this.m_view.showConfigDialog("PAYMENT_TAB", this.val$account);
                                    } else {
                                        PaymentMainPanel.this.m_view.showPaymentDialog(this.val$a_connectedCascade.getPIID());
                                    }
                                } else if (JAPModel.getInstance().isCascadeAutoSwitched()) {
                                    JAPController.getInstance().setAllowPaidServices(false);
                                    if (!TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                                        JAPController.getInstance().switchToNextMixCascade();
                                    }
                                    if (TrustModel.getCurrentTrustModel().hasFreeCascades()) {
                                        JAPController.getInstance().start();
                                    }
                                }
                            }
                            PaymentMainPanel.this.allowShowingError();
                        }
                    };
                }
            }
            if (runnable != null && accountEmptyException != null) {
                if (JAPDialog.isConsoleOnly()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            } else {
                PaymentMainPanel.this.allowShowingError();
            }
            if (accountEmptyException != null) {
                throw accountEmptyException;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void accountError(XMLErrorMessage xMLErrorMessage, boolean bl) {
            boolean bl2;
            String string = null;
            if (xMLErrorMessage.getXmlErrorCode() <= 0 || xMLErrorMessage.getXmlErrorCode() < 0) {
                return;
            }
            LogHolder.log(3, LogType.PAY, xMLErrorMessage);
            if (JAPModel.isAutomaticallyReconnected() || PayAccountsFile.getInstance().isAIAccountErrorIgnored()) {
                return;
            }
            if (JAPModel.getInstance().isPaymentPopupsHidden()) {
                return;
            }
            MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
            PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PaymentMainPanel.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(mixCascade.getPIID());
            boolean bl3 = bl2 = paymentInstanceDBEntry != null && paymentInstanceDBEntry.getFreeCodeURL() != null && PayAccountsFile.getInstance().isNewUser();
            if (paymentInstanceDBEntry != null) {
                string = paymentInstanceDBEntry.getOrganisation();
            }
            String string2 = string;
            Object object = PaymentMainPanel.this.SYNC_SHOW_ERROR;
            synchronized (object) {
                if (PaymentMainPanel.this.m_bShowingError || bl || !mixCascade.equals(xMLErrorMessage.getService())) {
                    return;
                }
                PaymentMainPanel.this.m_bShowingError = true;
            }
            object = xMLErrorMessage;
            new Thread(new Runnable((XMLErrorMessage)object, bl2, paymentInstanceDBEntry, mixCascade){
                private final /* synthetic */ XMLErrorMessage val$msg;
                private final /* synthetic */ boolean val$bFreeCodeAllowed;
                private final /* synthetic */ PaymentInstanceDBEntry val$piEntry;
                private final /* synthetic */ MixCascade val$cascade;
                {
                    this.val$msg = xMLErrorMessage;
                    this.val$bFreeCodeAllowed = bl;
                    this.val$piEntry = paymentInstanceDBEntry;
                    this.val$cascade = mixCascade;
                }

                public void run() {
                    String string = PaymentMainPanel.translateBIError(this.val$msg);
                    JAPDialog.AbstractLinkedURLAdapter abstractLinkedURLAdapter = new JAPDialog.AbstractLinkedURLAdapter("premium"){

                        public URL getUrl() {
                            if (val$msg.getXmlErrorCode() == 21) {
                                try {
                                    return new URL("mailto:payment@jondos.de");
                                }
                                catch (MalformedURLException malformedURLException) {
                                    LogHolder.log(3, LogType.GUI, malformedURLException);
                                }
                            } else if (val$bFreeCodeAllowed) {
                                return val$piEntry.getFreeCodeURL();
                            }
                            return null;
                        }

                        public String getMessage() {
                            if (val$msg.getXmlErrorCode() != 21 && val$bFreeCodeAllowed) {
                                return JAPMessages.getString(PayAccountsFile.MSG_GET_FREE_CODE);
                            }
                            return super.getMessage();
                        }

                        public void clicked(boolean bl) {
                            JAPController.getInstance().allowDirectProxyDomain(this.getUrl());
                            super.clicked(bl);
                        }

                        public boolean isOnTop() {
                            return true;
                        }
                    };
                    if (this.val$msg.getXmlErrorCode() == 10) {
                        Object object;
                        String string2 = MSG_CREATE_ACCOUNT_QUESTION;
                        if (this.val$cascade != null && (object = this.val$cascade.getPaymentInstance()) != null && ((PaymentInstanceDBEntry)object).isTest()) {
                            string2 = MSG_TEST_PREMIUM_BETA;
                        }
                        string = string + "<br><br>" + JAPMessages.getString(MSG_WITH_COSTS, MyPaymentListener.this.formatCascadeName(this.val$cascade)) + " " + JAPMessages.getString(string2);
                        PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
                        if (PayAccount.canDoMonthlyOverusage(payAccount, new Timestamp(System.currentTimeMillis()))) {
                            PaymentMainPanel.showMonthlyOverusageQuestion(payAccount, JAPController.getInstance().getCurrentView(), true, PaymentMainPanel.this.m_view);
                        } else {
                            object = new JAPDialog.Options(2){

                                public String getCancelText() {
                                    return JAPMessages.getString(MSG_MAYBE_LATER);
                                }
                            };
                            int n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), string, JAPMessages.getString(MSG_CREATE_ACCOUNT_TITLE), (JAPDialog.Options)object, 3, (JAPDialog.ILinkedInformation)abstractLinkedURLAdapter);
                            if (n == 0) {
                                new Thread(new Runnable(){

                                    public void run() {
                                        if (val$cascade.isPayment()) {
                                            PaymentMainPanel.this.m_view.showPaymentDialog(val$cascade.getPIID());
                                        } else {
                                            PaymentMainPanel.this.m_view.showConfigDialog("PAYMENT_TAB", new Boolean(true));
                                        }
                                    }
                                }).start();
                            } else if (JAPModel.getInstance().isCascadeAutoSwitched()) {
                                JAPController.getInstance().setAllowPaidServices(false);
                                if (!TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                                    JAPController.getInstance().switchToNextMixCascade();
                                }
                                if (TrustModel.getCurrentTrustModel().hasFreeCascades()) {
                                    JAPController.getInstance().start();
                                }
                            }
                        }
                    } else if (!JAPModel.getInstance().isCascadeAutoSwitched() || !JAPModel.isAutomaticallyReconnected()) {
                        string = string + "<br><br>" + JAPMessages.getString(MSG_ENABLE_AUTO_SWITCH);
                        if (0 == JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), string, JAPMessages.getString(JAPDialog.MSG_TITLE_WARNING), 0, 2, (JAPDialog.ILinkedInformation)abstractLinkedURLAdapter)) {
                            JAPModel.getInstance().setCascadeAutoSwitch(true);
                            JAPModel.getInstance().setAutoReConnect(true);
                            JAPController.getInstance().switchToNextMixCascade();
                            JAPController.getInstance().start();
                        }
                    } else {
                        JAPDialog.showErrorDialog(JAPController.getInstance().getCurrentView(), string, (JAPDialog.ILinkedInformation)abstractLinkedURLAdapter);
                    }
                    PaymentMainPanel.this.allowShowingError();
                }
            }).start();
        }

        public void gotCaptcha(ICaptchaSender iCaptchaSender, IImageEncodedCaptcha iImageEncodedCaptcha) {
        }
    }
}

