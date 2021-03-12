/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import HTTPClient.ForbiddenIOException;
import anon.client.TrustModel;
import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.DSAKeyPair;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.pay.BIConnection;
import anon.pay.IPaymentListener;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.Transaction;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLBalance;
import anon.pay.xml.XMLErrorMessage;
import anon.pay.xml.XMLGenericStrings;
import anon.pay.xml.XMLGenericText;
import anon.pay.xml.XMLPassivePayment;
import anon.pay.xml.XMLPaymentOptions;
import anon.pay.xml.XMLTransCert;
import anon.pay.xml.XMLVolumePlan;
import anon.pay.xml.XMLVolumePlans;
import anon.platform.AbstractOS;
import anon.util.BooleanVariable;
import anon.util.IReturnRunnable;
import anon.util.JAPMessages;
import anon.util.SingleStringPasswordReader;
import anon.util.Util;
import anon.util.XMLUtil;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;
import gui.GUIUtils;
import gui.dialog.CaptchaContentPane;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.PasswordContentPane;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.TermsAndConditionsPane;
import gui.dialog.WorkerContentPane;
import jap.AbstractJAPConfModule;
import jap.JAPConf;
import jap.JAPConfInfoService;
import jap.JAPController;
import jap.JAPControllerMessage;
import jap.JAPModel;
import jap.JAPUtil;
import jap.gui.LinkRegistrator;
import jap.pay.AccountCreator;
import jap.pay.ActivePaymentDetails;
import jap.pay.CoinstackProgressBarUI;
import jap.pay.IPaymentDialogPresentator;
import jap.pay.IReturnAccountRunnable;
import jap.pay.IReturnBooleanRunnable;
import jap.pay.PaymentMainPanel;
import jap.pay.TransactionOverviewDialog;
import jap.pay.wizardnew.JpiSelectionPane;
import jap.pay.wizardnew.MethodSelectionPane;
import jap.pay.wizardnew.PassivePaymentPane;
import jap.pay.wizardnew.PaymentInfoPane;
import jap.pay.wizardnew.VolumePlanSelectionPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AccountSettingsPanel
extends AbstractJAPConfModule
implements ListSelectionListener,
Observer,
IPaymentListener,
IPaymentDialogPresentator {
    protected static final String MSG_ACCOUNT_FLAT_VOLUME = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_flat_volume";
    protected static final String MSG_ACCOUNT_VALID = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_valid";
    protected static final String MSG_PAYMENT_INSTANCE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_paymentInstance";
    protected static final String IMG_COINS_DISABLED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_coins-disabled.gif";
    private static final String MSG_BUTTON_TRANSACTIONS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_transactions";
    private static final String MSG_BUTTON_DELETE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_delete";
    private static final String MSG_BTN_CREATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_btnCreate";
    private static final String MSG_BUTTON_EXPORT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_export";
    private static final String MSG_BUTTONRELOAD = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_buttonreload";
    private static final String MSG_TRANSACTION_OVERVIEW_DIALOG = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_transaction_overview_dialog";
    private static final String MSG_ACCOUNT_SPENT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_spent";
    private static final String MSG_ACCOUNT_DEPOSIT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_deposit";
    private static final String MSG_ACCOUNT_BALANCE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_balance";
    private static final String MSG_ACCOUNT_FLAT_ENDDATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_flat_enddate";
    private static final String MSG_ACCOUNT_NOFLAT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_noflat";
    private static final String MSG_ACCOUNT_DETAILS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_details";
    private static final String MSG_ACCOUNT_CREATION_DATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_creation_date";
    private static final String MSG_ACCOUNT_STATEMENT_DATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_statement_date";
    private static final String MSG_BUTTON_CHARGE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_charge";
    private static final String MSG_BUTTON_BUYFLAT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_buyflat";
    private static final String MSG_FLATTITLE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_flat_title";
    private static final String MSG_BUTTON_SELECT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_select";
    private static final String MSG_BUTTON_CHANGE_PASSWORD = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_button_change_password";
    private static final String MSG_ACCOUNT_INVALID = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_account_invalid";
    public static final String MSG_ACCOUNTCREATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accountcreate";
    private static final String MSG_CREATEERROR = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_createerror";
    private static final String MSG_DIRECT_CONNECTION_FORBIDDEN = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_directConnectionForbidden";
    private static final String MSG_ANON_CONNECTION_FORBIDDEN = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_anonConnectionForbidden";
    private static final String MSG_NO_ANONYMITY_POSSIBLY_BLOCKED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_noAnonymityPossiblyBlocked";
    private static final String MSG_ERROR_FORBIDDEN = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_errorForbidden";
    public static final String MSG_GETACCOUNTSTATEMENT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_getaccountstatement";
    private static final String MSG_GETACCOUNTSTATEMENTTITLE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_getaccountstatementtitle";
    private static final String MSG_ACCOUNTCREATEDESC = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accountcreatedesc";
    private static final String MSG_ACCPASSWORDTITLE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accpasswordtitle";
    private static final String MSG_EXPORTENCRYPT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_exportencrypt";
    private static final String MSG_ACCPASSWORD = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accpassword";
    private static final String MSG_OLDSTATEMENT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_oldstatement";
    private static final String MSG_EXPORTED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_exported";
    private static final String MSG_ENCRYPT_ACCOUNTS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_encryptAccounts";
    private static final String MSG_NOTEXPORTED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_notexported";
    private static final String MSG_CONNECTIONACTIVE_SELECT_QUESTION = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_connectionactive";
    private static final String MSG_CONNECTIONACTIVE_QUESTION = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_connectionActiveQuestion";
    public static final String MSG_FETCHINGOPTIONS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fetchingoptions";
    private static final String MSG_FETCHINGPLANS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fetchingplans";
    private static final String MSG_FETCHINGTERMS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fetchingterms";
    private static final String MSG_FETCHINGPOLICY = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fetchingpolicy";
    private static final String MSG_FETCHINGTAN = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fetchingtan";
    private static final String MSG_CHARGEWELCOME = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_chargewelcome";
    private static final String MSG_CHARGETITLE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_chargetitle";
    private static final String MSG_SENDINGPASSIVE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_sendingpassive";
    private static final String MSG_SENTPASSIVE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_sentpassive";
    private static final String MSG_NOTSENTPASSIVE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_notsentpassive";
    private static final String MSG_NEWCAPTCHA = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_newcaptcha";
    private static final String MSG_NEWCAPTCHAEASTEREGG = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_newcaptchaEasterEgg";
    private static final String MSG_SHOW_PAYMENT_CONFIRM_DIALOG = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_showPaymentConfirmDialog";
    private static final String MSG_TEST_PI_CONNECTION = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_testingPIConnection";
    private static final String MSG_CREATE_KEY_PAIR = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_creatingKeyPair";
    private static final String MSG_KEY_PAIR_CREATE_ERROR = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_keyPairCreateError";
    private static final String MSG_FETCHING_BIS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fetchingBIs";
    private static final String MSG_SAVE_CONFIG = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_savingConfig";
    private static final String MSG_CREATED_ACCOUNT_NOT_SAVED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_createdAccountNotSaved";
    private static final String MSG_ACCOUNT_IMPORT_FAILED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accountImportFailed";
    private static final String MSG_ACCOUNT_ALREADY_EXISTING = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accountAlreadyExisting";
    private static final String MSG_ALLOW_DIRECT_CONNECTION = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_allowDirectConnection";
    private static final String MSG_BI_CONNECTION_LOST = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_biConnectionLost";
    private static final String MSG_BUTTON_UNLOCK = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_unlockAccount";
    private static final String MSG_BUTTON_ACTIVATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_activateAccount";
    private static final String MSG_BUTTON_DEACTIVATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_deactivateAccount";
    private static final String MSG_ERROR_DELETING = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_errorDeletingAccount";
    private static final String MSG_ACCOUNT_DISABLED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_accountDisabled";
    private static final String MSG_GIVE_ACCOUNT_PASSWORD = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_giveAccountPassword";
    private static final String MSG_ACTIVATION_SUCCESSFUL = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_activationSuccessful";
    private static final String MSG_ACTIVATION_FAILED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_activationFailed";
    private static final String MSG_SHOW_AI_ERRORS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_showAIErrors";
    private static final String MSG_BALANCE_AUTO_UPDATE_ENABLED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_balanceAutoUpdateEnabled";
    private static final String MSG_NO_BACKUP = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_noBackup";
    private static final String MSG_TOOL_TIP_NO_BACKUP = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_toolTipNoBackup";
    private static final String MSG_TOOL_TIP_ACTIVATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_toolTipActivate";
    private static final String MSG_TOOL_TIP_EXPIRED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_toolTipExpired";
    private static final String MSG_PASSWORD_EXPORT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_passwordExport";
    private static final String MSG_ASK_IF_NOT_SAVED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_askIfNotSaved";
    private static final String MSG_NEW_CAPTCHA_HINT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_newCaptchaHint";
    private static final String MSG_BILLING_ERROR = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_billingError";
    public static final String MSG_BILLING_ERROR_EXPLAIN = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_billingErrorExplain";
    public static final String MSG_BILLING_ERROR_TOOLTIP = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_billingErrorClick";
    public static final String MSG_WRONG_TIME_TOO_EARLY = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".wrongClockTime";
    public static final String MSG_SHOW_TRANSACTION_DETAILS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_showTransactionDetails";
    public static final String MSG_NO_TRANSACTION = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_noTransaction";
    public static final String MSG_EXPIRED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_expired";
    public static final String MSG_NO_CREDIT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_noCredit";
    private static final String MSG_TERMS_AND_COND_DESC = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_termsAndConditionsDescription";
    private static final String MSG_TERMS_AND_COND = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_termsAndConditions";
    private static final String MSG_TERMS_AND_COND_HINT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_termsAndConditionsHint";
    private static final String MSG_EXPLAIN_PARTIAL_MONTHLY_VOLUME = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".explainPartialMonthlyVolume";
    public static final String MSG_MONTHLY_VOLUME = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".monthlyVolume";
    private static final String MSG_MONTHLY_VOLUME_USED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".monthlyCreditUsed";
    public static final String MSG_EXPLAIN_LAST_MONTH = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".monthlyLastMonthExplain";
    public static final String MSG_INFO_ABOUT_MONTHLY_RATE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".infoAboutMonthlyRate";
    private static final String MSG_THANK_YOU = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_thankYou";
    private static final String MSG_CHARGING_SUCCESSFUL = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_chargingSuccessful";
    private static final String MSG_LBL_IGNORE_ALL_ERRORS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + ".lblHidePopups";
    private static final String MSG_BACKUP_WARNING = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_backupwarning";
    private static final String MSG_ACTIVE_COMPLETE = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_activecomplete";
    private static final String MSG_COUPON_SENT = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_couponsent";
    private static final String MSG_COUPON_FAILED = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_couponfailed";
    private static final String MSG_COUPON = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_coupon";
    private static final String MSG_FILE_EXISTS = (class$jap$pay$AccountSettingsPanel == null ? (class$jap$pay$AccountSettingsPanel = AccountSettingsPanel.class$("jap.pay.AccountSettingsPanel")) : class$jap$pay$AccountSettingsPanel).getName() + "_fileExists";
    private Object SYNC_CREATE_ACCOUNT = new Object();
    private boolean m_bCreatingAccount = false;
    private static final Integer[] CONNECT_TIMEOUTS = new Integer[]{new Integer(10), new Integer(20), new Integer(30), new Integer(40), new Integer(50), new Integer(60), new Integer(80), new Integer(100)};
    private JButton m_btnCreateAccount;
    private JButton m_btnChargeAccount;
    private JButton m_btnDeleteAccount;
    private JButton m_btnExportAccount;
    private JButton m_btnImportAccount;
    private JButton m_btnTransactions;
    private JButton m_btnSelect;
    private JButton m_btnPassword;
    private JButton m_btnReload;
    private JButton m_btnActivate;
    private JComboBox m_comboAnonymousConnection;
    private JCheckBox m_cbHidePopups;
    private JCheckBox m_cbxShowAIErrors;
    private JCheckBox m_cbxBalanceAutoUpdateEnabled;
    private JCheckBox m_cbxAskIfNotSaved;
    private JLabel m_paymentInstance;
    private JLabel m_labelTermsAndConditions;
    private JLabel m_labelCreationDate;
    private JLabel m_labelStatementDate;
    private JLabel m_labelDeposit;
    private JLabel m_labelSpent;
    private JLabel m_labelValid;
    private JLabel m_labelVolume;
    private JLabel m_labelVolumeWarning;
    private JLabel m_labelVolumeMonthly;
    private JLabel m_lblInactiveMessage;
    private JLabel m_lblNoBackupMessage;
    private JProgressBar m_coinstack;
    private JList m_listAccounts;
    private JComboBox m_comboTimeout;
    private JPanel m_tabBasicSettings;
    private JPanel m_tabAdvancedSettings;
    private boolean m_bReady = true;
    private boolean m_bDoNotCloseDialog = false;
    private MyActionListener myActionListener;
    private JTabbedPane m_tabPane;
    private static final int DISCONNECT_CANCEL = -1;
    private static final int DISCONNECT_NOT_NEEDED = 0;
    private static final int DISCONNECT_DONE = 1;
    static /* synthetic */ Class class$jap$pay$AccountSettingsPanel;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;

    public AccountSettingsPanel() {
        super(null);
        this.updateAccountList();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                PayAccountsFile.getInstance().addPaymentListener(this);
                JAPController.getInstance().addObserver(this);
                return true;
            }
        }
        return false;
    }

    public void accountCertRequested(MixCascade mixCascade) {
    }

    public void accountError(XMLErrorMessage xMLErrorMessage, boolean bl) {
    }

    public void accountActivated(PayAccount payAccount) {
        this.updateAccountList();
    }

    public void accountRemoved(PayAccount payAccount) {
        this.updateAccountList();
    }

    public void accountAdded(PayAccount payAccount) {
        this.updateAccountList();
    }

    public void creditChanged(PayAccount payAccount) {
        if (payAccount != null && payAccount == this.getSelectedAccount()) {
            SwingUtilities.invokeLater(new Runnable(){

                public void run() {
                    AccountSettingsPanel.this.doShowDetails(AccountSettingsPanel.this.getSelectedAccount());
                    AccountSettingsPanel.this.enableDisableButtons();
                }
            });
        }
    }

    public void gotCaptcha(ICaptchaSender iCaptchaSender, IImageEncodedCaptcha iImageEncodedCaptcha) {
    }

    public void update(Observable observable, Object object) {
        if (observable instanceof JAPController && ((JAPControllerMessage)object).getMessageCode() == 3) {
            this.m_cbxAskIfNotSaved.setSelected(JAPController.getInstance().isAskSavePayment());
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("ngPaymentTabTitle");
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        if (JAPModel.getDefaultView() == 2) {
            jPanel.setBorder(new TitledBorder(JAPMessages.getString("ngPayment")));
        }
        this.myActionListener = new MyActionListener();
        this.m_tabPane = new JTabbedPane();
        this.m_tabBasicSettings = this.createBasicSettingsTab();
        this.m_tabPane.insertTab(JAPMessages.getString("ngPseudonymAccounts"), null, this.m_tabBasicSettings, null, 0);
        this.m_tabAdvancedSettings = this.createAdvancedSettingsTab();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = AbstractJAPConfModule.createTabbedRootPanelContraints();
        if (JAPModel.getDefaultView() != 2) {
            this.m_tabPane.insertTab(JAPMessages.getString("settingsInfoServiceConfigAdvancedSettingsTabTitle"), null, this.m_tabAdvancedSettings, null, 1);
            jPanel.add((Component)this.m_tabPane, gridBagConstraints);
        } else {
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.weighty = 0.0;
            jPanel.add((Component)this.m_tabBasicSettings, gridBagConstraints);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            jPanel.add((Component)new JLabel(), gridBagConstraints);
        }
        this.m_coinstack.setUI(new CoinstackProgressBarUI(GUIUtils.loadImageIcon("coinstack.gif", true), 0, 8));
    }

    private JPanel createBasicSettingsTab() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        this.m_listAccounts = new JList();
        this.m_listAccounts.setCellRenderer(new CustomRenderer());
        this.m_listAccounts.addListSelectionListener(this);
        this.m_listAccounts.getSelectionModel().setSelectionMode(0);
        this.m_listAccounts.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    AccountSettingsPanel.this.doSelectAccount(AccountSettingsPanel.this.getSelectedAccount());
                }
            }
        });
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.m_btnCreateAccount = new JButton(JAPMessages.getString(ActivePaymentDetails.MSG_PAYBUTTON));
        this.m_btnCreateAccount.addActionListener(this.myActionListener);
        jPanel2.add((Component)this.m_btnCreateAccount, gridBagConstraints);
        ++gridBagConstraints.gridx;
        this.m_btnTransactions = new JButton(JAPMessages.getString(MSG_BUTTON_TRANSACTIONS));
        this.m_btnTransactions.addActionListener(this.myActionListener);
        jPanel2.add((Component)this.m_btnTransactions, gridBagConstraints);
        ++gridBagConstraints.gridx;
        this.m_btnPassword = new JButton(JAPMessages.getString(MSG_BUTTON_CHANGE_PASSWORD));
        this.m_btnPassword.addActionListener(this.myActionListener);
        jPanel2.add((Component)this.m_btnPassword, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weighty = 1.0;
        this.m_btnImportAccount = new JButton(JAPMessages.getString("ngImportAccount"));
        this.m_btnImportAccount.addActionListener(this.myActionListener);
        jPanel2.add((Component)this.m_btnImportAccount, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        JScrollPane jScrollPane = new JScrollPane(this.m_listAccounts);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jPanel.add((Component)jScrollPane, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_CREATION_DATE)), gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 1.0;
        this.m_labelCreationDate = new JLabel();
        jPanel.add((Component)this.m_labelCreationDate, gridBagConstraints);
        this.m_labelStatementDate = new JLabel();
        --gridBagConstraints.gridx;
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_VALID)), gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 1.0;
        this.m_labelValid = new JLabel();
        jPanel.add((Component)this.m_labelValid, gridBagConstraints);
        ++gridBagConstraints.gridy;
        --gridBagConstraints.gridx;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_PAYMENT_INSTANCE) + ":"), gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 1.0;
        this.m_paymentInstance = new JLabel();
        jPanel.add((Component)this.m_paymentInstance, gridBagConstraints);
        ++gridBagConstraints.gridy;
        --gridBagConstraints.gridx;
        gridBagConstraints.gridwidth = 2;
        this.m_labelTermsAndConditions = new JLabel();
        this.m_labelTermsAndConditions.setToolTipText(JAPMessages.getString(MSG_TERMS_AND_COND_HINT));
        this.m_labelTermsAndConditions.setCursor(Cursor.getPredefinedCursor(12));
        this.m_labelTermsAndConditions.setForeground(Color.blue);
        this.m_labelTermsAndConditions.addMouseListener(this.myActionListener);
        jPanel.add((Component)this.m_labelTermsAndConditions, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        this.m_lblInactiveMessage = new JLabel();
        this.m_lblInactiveMessage.setCursor(Cursor.getPredefinedCursor(12));
        this.m_lblInactiveMessage.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                AccountSettingsPanel.this.m_btnActivate.doClick();
            }
        });
        jPanel.add((Component)this.m_lblInactiveMessage, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_lblNoBackupMessage = new JLabel();
        this.m_lblNoBackupMessage.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                AccountSettingsPanel.this.m_btnExportAccount.doClick();
            }
        });
        this.m_lblNoBackupMessage.setCursor(Cursor.getPredefinedCursor(12));
        jPanel.add((Component)this.m_lblNoBackupMessage, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        ++gridBagConstraints.gridy;
        JSeparator jSeparator = new JSeparator(0);
        jSeparator.setPreferredSize(new Dimension(520, 10));
        jPanel.add((Component)jSeparator, gridBagConstraints);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)this.createDetailsPanel(this.myActionListener), gridBagConstraints);
        this.enableDisableButtons();
        return jPanel;
    }

    private JPanel createAdvancedSettingsTab() {
        JPanel jPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        JPanel jPanel2 = new JPanel();
        jPanel2.add(new JLabel(JAPMessages.getString(MSG_ALLOW_DIRECT_CONNECTION) + ":"));
        String[] arrstring = new String[JAPModel.getMsgConnectionAnonymous().length];
        System.arraycopy(JAPModel.getMsgConnectionAnonymous(), 0, arrstring, 0, arrstring.length);
        for (int i = 0; i < arrstring.length; ++i) {
            arrstring[i] = JAPMessages.getString(arrstring[i]);
        }
        this.m_comboAnonymousConnection = new JComboBox<String>(arrstring);
        jPanel2.add(this.m_comboAnonymousConnection);
        jPanel.add((Component)jPanel2, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        ++gridBagConstraints.gridy;
        this.m_cbxBalanceAutoUpdateEnabled = new JCheckBox(JAPMessages.getString(MSG_BALANCE_AUTO_UPDATE_ENABLED));
        jPanel.add((Component)this.m_cbxBalanceAutoUpdateEnabled, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_cbHidePopups = new JCheckBox(JAPMessages.getString(MSG_LBL_IGNORE_ALL_ERRORS));
        jPanel.add((Component)this.m_cbHidePopups, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_cbxAskIfNotSaved = new JCheckBox(JAPMessages.getString(MSG_ASK_IF_NOT_SAVED));
        gridBagConstraints.insets = new Insets(10, 30, 0, 10);
        jPanel.add((Component)this.m_cbxAskIfNotSaved, gridBagConstraints);
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        ++gridBagConstraints.gridy;
        this.m_cbxShowAIErrors = new JCheckBox(JAPMessages.getString(MSG_SHOW_AI_ERRORS));
        this.m_cbxShowAIErrors.setVisible(false);
        jPanel.add((Component)this.m_cbxShowAIErrors, gridBagConstraints);
        gridBagConstraints.weightx = 0.0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = 17;
        jPanel.add((Component)new JLabel(JAPMessages.getString(JAPConfInfoService.MSG_CONNECT_TIMEOUT) + " (s):"), gridBagConstraints);
        this.m_comboTimeout = new JComboBox<Integer>(CONNECT_TIMEOUTS);
        gridBagConstraints.fill = 0;
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_comboTimeout, gridBagConstraints);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(), gridBagConstraints);
        this.m_cbHidePopups.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                AccountSettingsPanel.this.m_cbxAskIfNotSaved.setEnabled(itemEvent.getStateChange() != 1);
            }
        });
        this.updateValues(false);
        return jPanel;
    }

    private JPanel createDetailsPanel(ActionListener actionListener) {
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.gridwidth = 2;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_DETAILS)), gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridheight = 5;
        this.m_coinstack = new JProgressBar(0, 8);
        this.m_coinstack.setBorderPainted(false);
        jPanel.add((Component)this.m_coinstack, gridBagConstraints);
        gridBagConstraints.gridheight = 1;
        ++gridBagConstraints.gridx;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_FLAT_VOLUME) + ":"), gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.gridwidth = 1;
        this.m_labelVolume = new JLabel();
        this.m_labelVolume.addMouseListener(this.myActionListener);
        jPanel.add((Component)this.m_labelVolume, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.gridwidth = 1;
        this.m_labelVolumeMonthly = new JLabel();
        this.m_labelVolumeMonthly.addMouseListener(this.myActionListener);
        jPanel.add((Component)this.m_labelVolumeMonthly, gridBagConstraints);
        this.m_labelVolumeMonthly.setVisible(false);
        this.m_labelVolumeMonthly.setToolTipText(JAPMessages.getString(MSG_INFO_ABOUT_MONTHLY_RATE));
        ++gridBagConstraints.gridx;
        gridBagConstraints.gridwidth = 1;
        this.m_labelVolumeWarning = new JLabel();
        this.m_labelVolumeWarning.setCursor(Cursor.getPredefinedCursor(12));
        this.m_labelVolumeWarning.addMouseListener(this.myActionListener);
        jPanel.add((Component)this.m_labelVolumeWarning, gridBagConstraints);
        --gridBagConstraints.gridx;
        gridBagConstraints.gridwidth = 2;
        --gridBagConstraints.gridx;
        --gridBagConstraints.gridx;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_SPENT)), gridBagConstraints);
        ++gridBagConstraints.gridx;
        this.m_labelSpent = new JLabel();
        jPanel.add((Component)this.m_labelSpent, gridBagConstraints);
        --gridBagConstraints.gridx;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_DEPOSIT)), gridBagConstraints);
        ++gridBagConstraints.gridx;
        this.m_labelDeposit = new JLabel();
        jPanel.add((Component)this.m_labelDeposit, gridBagConstraints);
        --gridBagConstraints.gridx;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_ACCOUNT_STATEMENT_DATE) + ":"), gridBagConstraints);
        ++gridBagConstraints.gridx;
        this.m_labelStatementDate = new JLabel();
        jPanel.add((Component)this.m_labelStatementDate, gridBagConstraints);
        ++gridBagConstraints.gridy;
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.anchor = 18;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.weighty = 0.0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
        this.m_btnSelect = new JButton(JAPMessages.getString(MSG_BUTTON_ACTIVATE));
        this.m_btnSelect.addActionListener(actionListener);
        jPanel2.add((Component)this.m_btnSelect, gridBagConstraints2);
        ++gridBagConstraints2.gridx;
        this.m_btnReload = new JButton(JAPMessages.getString(MSG_BUTTONRELOAD));
        this.m_btnReload.addActionListener(actionListener);
        jPanel2.add((Component)this.m_btnReload, gridBagConstraints2);
        ++gridBagConstraints2.gridx;
        this.m_btnActivate = new JButton(JAPMessages.getString(MSG_BUTTON_UNLOCK));
        this.m_btnActivate.setVisible(false);
        this.m_btnActivate.addActionListener(actionListener);
        jPanel2.add((Component)this.m_btnActivate, gridBagConstraints2);
        ++gridBagConstraints2.gridx;
        this.m_btnDeleteAccount = new JButton(JAPMessages.getString(MSG_BUTTON_DELETE));
        this.m_btnDeleteAccount.addActionListener(actionListener);
        jPanel2.add((Component)this.m_btnDeleteAccount, gridBagConstraints2);
        ++gridBagConstraints2.gridx;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        this.m_btnExportAccount = new JButton(JAPMessages.getString(MSG_BUTTON_EXPORT));
        this.m_btnExportAccount.addActionListener(actionListener);
        jPanel2.add((Component)this.m_btnExportAccount, gridBagConstraints2);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 5;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        return jPanel;
    }

    private void updateAccountList() {
        Runnable runnable = new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                JList jList = AccountSettingsPanel.this.m_listAccounts;
                synchronized (jList) {
                    int n = -1;
                    DefaultListModel<PayAccount> defaultListModel = new DefaultListModel<PayAccount>();
                    Enumeration enumeration = PayAccountsFile.getInstance().getAccounts();
                    int n2 = AccountSettingsPanel.this.m_listAccounts.getSelectedIndex();
                    int n3 = 0;
                    while (enumeration.hasMoreElements()) {
                        PayAccount payAccount = (PayAccount)enumeration.nextElement();
                        if (PayAccountsFile.getInstance().getActiveAccount() == payAccount) {
                            n = n3;
                        }
                        defaultListModel.addElement(payAccount);
                        ++n3;
                    }
                    AccountSettingsPanel.this.m_listAccounts.setModel(defaultListModel);
                    AccountSettingsPanel.this.m_listAccounts.revalidate();
                    if (AccountSettingsPanel.this.m_listAccounts.getModel().getSize() > 0) {
                        if (n2 < 0) {
                            n2 = n >= 0 ? n : 0;
                        } else if (n2 >= AccountSettingsPanel.this.m_listAccounts.getModel().getSize()) {
                            n2 = AccountSettingsPanel.this.m_listAccounts.getModel().getSize() - 1;
                        }
                        AccountSettingsPanel.this.m_listAccounts.setSelectedIndex(n2);
                        AccountSettingsPanel.this.m_listAccounts.scrollRectToVisible(AccountSettingsPanel.this.m_listAccounts.getCellBounds(n2, n2));
                    }
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.GUI, exception);
            }
        }
    }

    private void enableDisableButtons() {
        boolean bl = this.getSelectedAccount() != null && this.getSelectedAccount().getPrivateKey() != null;
        this.m_btnActivate.setEnabled(this.getSelectedAccount() != null && this.getSelectedAccount().getPrivateKey() == null);
        this.m_btnTransactions.setEnabled(bl);
        this.m_btnExportAccount.setEnabled(bl);
        this.m_btnReload.setEnabled(bl);
        this.m_btnSelect.setEnabled(this.getSelectedAccount() != null && this.getSelectedAccount() != PayAccountsFile.getInstance().getActiveAccount());
        this.m_btnDeleteAccount.setEnabled(this.getSelectedAccount() != null);
    }

    private void doChangePassword() {
        JAPDialog jAPDialog = new JAPDialog(this.getRootPanel(), JAPMessages.getString(MSG_ACCPASSWORDTITLE), true);
        PasswordContentPane passwordContentPane = JAPController.getInstance().getPaymentPassword() != null ? new PasswordContentPane(jAPDialog, 3, JAPMessages.getString(MSG_ENCRYPT_ACCOUNTS)){

            public char[] getComparedPassword() {
                return JAPController.getInstance().getPaymentPassword().toCharArray();
            }
        } : new PasswordContentPane(jAPDialog, 1, JAPMessages.getString(MSG_ENCRYPT_ACCOUNTS));
        passwordContentPane.updateDialog();
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        if (passwordContentPane.getButtonValue() != 2 && passwordContentPane.getButtonValue() != -1) {
            String string = new String(passwordContentPane.getPassword());
            if (string.equals("")) {
                string = null;
            }
            JAPController.getInstance().setPaymentPassword(string);
        }
    }

    private void doShowTransactions() {
        Vector<PayAccount> vector = new Vector<PayAccount>();
        Enumeration enumeration = PayAccountsFile.getInstance().getAccounts();
        PayAccount payAccount = this.getSelectedAccount();
        PaymentInstanceDBEntry paymentInstanceDBEntry = payAccount.getBI();
        while (enumeration.hasMoreElements()) {
            PayAccount payAccount2 = (PayAccount)enumeration.nextElement();
            if (payAccount2.isTransactionExpired()) continue;
            PaymentInstanceDBEntry paymentInstanceDBEntry2 = payAccount2.getBI();
            if (paymentInstanceDBEntry2 != null && paymentInstanceDBEntry != null) {
                if (!paymentInstanceDBEntry2.getId().equalsIgnoreCase(paymentInstanceDBEntry.getId())) continue;
                vector.addElement(payAccount2);
                continue;
            }
            LogHolder.log(2, LogType.PAY, "JPI is null! Current account: " + paymentInstanceDBEntry2 + " " + "Active account: " + paymentInstanceDBEntry + " " + "Current account ID: " + payAccount2.getAccountNumber());
        }
        new TransactionOverviewDialog(this, JAPMessages.getString(MSG_TRANSACTION_OVERVIEW_DIALOG), true, vector);
    }

    private synchronized void doShowDetails(PayAccount payAccount) {
        if (payAccount == null) {
            this.m_coinstack.setValue(0);
            this.m_labelCreationDate.setText("");
            this.m_labelStatementDate.setText("");
            this.m_labelDeposit.setText("");
            this.m_labelVolumeMonthly.setText("");
            this.m_labelVolumeMonthly.setVisible(false);
            this.m_labelSpent.setText("");
            this.m_labelVolume.setText("");
            this.m_labelValid.setText("");
            this.m_paymentInstance.setText("");
            this.m_lblInactiveMessage.setText("");
            this.m_lblNoBackupMessage.setText("");
            this.m_lblNoBackupMessage.setIcon(null);
            this.m_labelTermsAndConditions.setText("");
            return;
        }
        if (payAccount.getPrivateKey() == null) {
            this.m_lblInactiveMessage.setIcon(GUIUtils.loadImageIcon("warning.gif"));
            this.m_lblInactiveMessage.setText(JAPMessages.getString(MSG_ACCOUNT_DISABLED));
            this.m_lblInactiveMessage.setToolTipText(JAPMessages.getString(MSG_TOOL_TIP_ACTIVATE));
        } else {
            this.m_lblInactiveMessage.setIcon(null);
            this.m_lblInactiveMessage.setText("");
            this.m_lblInactiveMessage.setToolTipText("");
        }
        if (!payAccount.isBackupDone()) {
            this.m_lblNoBackupMessage.setIcon(GUIUtils.loadImageIcon("warning.gif"));
            this.m_lblNoBackupMessage.setText(JAPMessages.getString(MSG_NO_BACKUP));
            this.m_lblNoBackupMessage.setToolTipText(JAPMessages.getString(MSG_TOOL_TIP_NO_BACKUP));
        } else {
            this.m_lblNoBackupMessage.setIcon(null);
            this.m_lblNoBackupMessage.setText("");
            this.m_lblNoBackupMessage.setToolTipText("");
        }
        XMLAccountInfo xMLAccountInfo = payAccount.getAccountInfo();
        PaymentInstanceDBEntry paymentInstanceDBEntry = payAccount.getBI();
        if (paymentInstanceDBEntry == null) {
            this.m_paymentInstance.setText("");
        } else {
            this.m_paymentInstance.setText(paymentInstanceDBEntry.getName());
        }
        this.m_labelCreationDate.setText(Util.formatTimestamp(payAccount.getCreationTime(), false));
        if (xMLAccountInfo != null) {
            XMLBalance xMLBalance = xMLAccountInfo.getBalance();
            Calendar calendar = payAccount.getTermsDate();
            String string = "";
            if (calendar != null) {
                string = "(" + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()) + ")";
            }
            this.m_labelTermsAndConditions.setText(JAPMessages.getString(MSG_TERMS_AND_COND, string));
            if (xMLBalance != null && xMLBalance.getStartDate() != null) {
                this.m_labelCreationDate.setText(Util.formatTimestamp(xMLBalance.getStartDate(), false));
            }
            if (xMLBalance == null || payAccount.isTransactionExpired()) {
                if (xMLBalance != null) {
                    this.m_labelStatementDate.setText(Util.formatTimestamp(xMLBalance.getTimestamp(), false));
                } else {
                    this.m_labelStatementDate.setText("");
                }
                this.m_labelDeposit.setText("");
                this.m_labelVolumeMonthly.setText("");
                this.m_labelVolumeMonthly.setVisible(false);
                this.m_labelVolumeWarning.setIcon(null);
                this.m_labelVolumeWarning.setText("");
                this.m_labelVolumeWarning.setToolTipText(null);
                this.m_labelSpent.setText("");
                this.m_coinstack.setValue(0);
                this.m_labelVolume.setText("");
                if (payAccount.isTransactionExpired() && payAccount.getTransaction() != null) {
                    this.m_labelValid.setText(JAPMessages.getString(MSG_EXPIRED));
                } else {
                    this.m_labelValid.setText("");
                }
            } else {
                long l = xMLBalance.getDeposit();
                boolean bl = true;
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                this.m_labelStatementDate.setText(Util.formatTimestamp(xMLBalance.getTimestamp(), false));
                if (xMLBalance.getVolumeBytesMonthly() > 0L) {
                    this.m_labelVolumeMonthly.setText("(" + JAPMessages.getString(MSG_MONTHLY_VOLUME, Util.formatBytesValueWithUnit(xMLBalance.getVolumeBytesMonthly(), 2)) + ")");
                    this.m_labelVolumeMonthly.setVisible(true);
                    if (payAccount.canDoMonthlyOverusage(timestamp)) {
                        this.m_labelVolumeMonthly.setForeground(Color.blue);
                        this.m_labelVolumeMonthly.setCursor(Cursor.getPredefinedCursor(12));
                        this.m_labelVolumeMonthly.setIcon(GUIUtils.loadImageIcon("info.png"));
                    } else if (xMLBalance != null && xMLBalance.getStartDate() != null && xMLBalance.getStartDate().after(timestamp)) {
                        if (payAccount.hasExpired(timestamp)) {
                            this.m_labelVolumeMonthly.setForeground(Color.blue);
                            this.m_labelVolumeMonthly.setCursor(Cursor.getPredefinedCursor(12));
                            this.m_labelVolumeMonthly.setIcon(GUIUtils.loadImageIcon("warning.gif"));
                        } else {
                            this.m_labelVolumeMonthly.setForeground(this.m_labelStatementDate.getForeground());
                            this.m_labelVolumeMonthly.setCursor(Cursor.getDefaultCursor());
                            this.m_labelVolumeMonthly.setIcon(null);
                        }
                    } else {
                        this.m_labelVolumeMonthly.setForeground(Color.blue);
                        this.m_labelVolumeMonthly.setCursor(Cursor.getPredefinedCursor(12));
                        this.m_labelVolumeMonthly.setIcon(GUIUtils.loadImageIcon("warning.gif"));
                    }
                } else {
                    this.m_labelVolumeMonthly.setVisible(false);
                }
                this.m_labelSpent.setText(Util.formatBytesValueWithUnit(payAccount.getCurrentSpent(), 2));
                Locale locale = JAPMessages.getLocale();
                String string2 = locale.getLanguage();
                Timestamp timestamp2 = xMLBalance.getFlatEnddate();
                Timestamp timestamp3 = new Timestamp(System.currentTimeMillis());
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
                gregorianCalendar.setTime(timestamp2);
                boolean bl2 = false;
                if (payAccount.getCurrentCredit() == 0L && (xMLBalance.getVolumeBytesMonthly() == 0L || gregorianCalendar.get(2) == gregorianCalendar2.get(2))) {
                    this.m_labelValid.setText("");
                } else if (timestamp2 != null && timestamp2.after(timestamp3)) {
                    this.m_labelValid.setText(Util.formatTimestamp(timestamp2, false, string2));
                } else {
                    bl2 = true;
                    this.m_labelValid.setText(Util.formatTimestamp(timestamp2, false, string2) + " (" + JAPMessages.getString(MSG_EXPIRED) + ")");
                }
                if (payAccount.isBlocked()) {
                    this.m_labelVolumeWarning.setIcon(GUIUtils.loadImageIcon("warning.gif"));
                    this.m_labelVolumeWarning.setText(JAPMessages.getString(PaymentMainPanel.MSG_ACCOUNT_BLOCKED));
                    this.m_labelVolumeWarning.setForeground(Color.red);
                    this.m_labelVolumeWarning.setToolTipText(JAPMessages.getString(PaymentMainPanel.MSG_ACCOUNT_BLOCKED_TOOLTIP));
                } else {
                    this.m_labelVolumeWarning.setIcon(null);
                    this.m_labelVolumeWarning.setText("");
                    this.m_labelVolumeWarning.getForeground();
                    this.m_labelVolumeWarning.setToolTipText(null);
                }
                if (payAccount.getCurrentCredit() > 0L) {
                    this.m_labelVolume.setText((bl2 ? "(" : "") + Util.formatBytesValueWithUnit(payAccount.getCurrentCredit(), 2) + (bl2 ? ")" : ""));
                    this.m_labelVolume.setForeground(this.m_labelValid.getForeground());
                    this.m_labelVolume.setToolTipText(null);
                    this.m_labelVolume.setCursor(Cursor.getDefaultCursor());
                } else if (!bl2 && !payAccount.isUsed()) {
                    bl = false;
                    if (payAccount.isTransactionExpired()) {
                        if (payAccount.getTransaction() == null) {
                            this.m_labelVolume.setText("");
                        } else {
                            this.m_labelVolume.setText(JAPMessages.getString(MSG_EXPIRED));
                        }
                        this.m_labelVolume.setToolTipText(null);
                        this.m_labelVolume.setForeground(this.m_labelValid.getForeground());
                        this.m_labelVolume.setCursor(Cursor.getDefaultCursor());
                    } else {
                        this.m_labelVolume.setText(JAPMessages.getString(MSG_NO_TRANSACTION));
                        this.m_labelVolume.setToolTipText(JAPMessages.getString(MSG_SHOW_TRANSACTION_DETAILS));
                        this.m_labelVolume.setForeground(Color.blue);
                        this.m_labelVolume.setCursor(Cursor.getPredefinedCursor(12));
                    }
                } else {
                    GregorianCalendar gregorianCalendar3 = new GregorianCalendar();
                    gregorianCalendar3.add(2, 1);
                    this.m_labelVolume.setText(JAPMessages.getString(MSG_NO_CREDIT));
                    this.m_labelVolume.setToolTipText(null);
                    this.m_labelVolume.setForeground(this.m_labelValid.getForeground());
                    this.m_labelVolume.setCursor(Cursor.getDefaultCursor());
                }
                if (l <= 0L && bl) {
                    this.m_labelDeposit.setText(JAPMessages.getString(MSG_COUPON));
                } else {
                    this.m_labelDeposit.setText(JAPUtil.formatEuroCentValue(l, true));
                }
                if (bl2) {
                    this.m_coinstack.setValue(0);
                } else {
                    l = 500000000L;
                    long l2 = payAccount.getCurrentCredit();
                    double d = (double)l2 / (double)l;
                    if (d > 0.87) {
                        this.m_coinstack.setValue(8);
                    } else if (d > 0.75) {
                        this.m_coinstack.setValue(7);
                    } else if (d > 0.63) {
                        this.m_coinstack.setValue(6);
                    } else if (d > 0.5) {
                        this.m_coinstack.setValue(5);
                    } else if (d > 0.38) {
                        this.m_coinstack.setValue(4);
                    } else if (d > 0.25) {
                        this.m_coinstack.setValue(3);
                    } else if (d > 0.13) {
                        this.m_coinstack.setValue(2);
                    } else if (d > 0.01) {
                        this.m_coinstack.setValue(1);
                    } else {
                        this.m_coinstack.setValue(0);
                    }
                }
            }
        } else {
            this.m_coinstack.setValue(0);
            this.m_labelCreationDate.setText("");
            this.m_labelStatementDate.setText("");
            this.m_labelVolumeMonthly.setText("");
            this.m_labelVolumeMonthly.setVisible(false);
            this.m_labelDeposit.setText("");
            this.m_labelSpent.setText("");
            this.m_labelValid.setText("");
            this.m_labelVolume.setText("");
            this.m_labelTermsAndConditions.setText("");
        }
    }

    private PayAccount getSelectedAccount() {
        try {
            return (PayAccount)this.m_listAccounts.getSelectedValue();
        }
        catch (Exception exception) {
            return null;
        }
    }

    public void backupAccount() {
        PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
        if (payAccount == null) {
            return;
        }
        if (!payAccount.isBackupDone()) {
            this.doExportAccount(payAccount);
        } else {
            Enumeration enumeration = PayAccountsFile.getInstance().getAccounts();
            while (enumeration.hasMoreElements()) {
                payAccount = (PayAccount)enumeration.nextElement();
                if (payAccount.isBackupDone()) continue;
                this.doExportAccount(payAccount);
                break;
            }
        }
    }

    public void showTermsAndConditions(final PayAccount payAccount) {
        JAPDialog jAPDialog = new JAPDialog(this.getRootPanel(), JAPMessages.getString(TermsAndConditionsPane.MSG_HEADING), true);
        jAPDialog.setDefaultCloseOperation(2);
        final WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHINGTERMS), new FetchTermsRunnable(jAPDialog, payAccount.getBI(), payAccount.getTerms()));
        TermsAndConditionsPane termsAndConditionsPane = new TermsAndConditionsPane(jAPDialog, workerContentPane, false, new TermsAndConditionsPane.TermsAndConditionsMessages()){

            public DialogContentPane.CheckError checkUpdate() {
                if (payAccount.getTerms() == null) {
                    payAccount.setTerms((XMLGenericText)workerContentPane.getValue());
                }
                return super.checkUpdate();
            }

            public boolean hideButtonCancel() {
                return true;
            }
        };
        workerContentPane.pack();
        jAPDialog.addWindowListener(new WindowAdapter(){

            public void windowClosed(WindowEvent windowEvent) {
                AccountSettingsPanel.this.updateAccountList();
            }
        });
        jAPDialog.setVisible(true);
    }

    private void doChargeAccount(final IReturnAccountRunnable iReturnAccountRunnable, final JAPDialog jAPDialog, DialogContentPane dialogContentPane, final IReturnBooleanRunnable iReturnBooleanRunnable, final Vector vector, boolean bl) {
        IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            private XMLVolumePlans m_volumePlans;

            public void run() {
                block3: {
                    BIConnection bIConnection = null;
                    try {
                        PaymentInstanceDBEntry paymentInstanceDBEntry = iReturnAccountRunnable.getAccount().getBI();
                        bIConnection = new BIConnection(paymentInstanceDBEntry);
                        bIConnection.connect();
                        bIConnection.authenticate(iReturnAccountRunnable.getAccount());
                        LogHolder.log(7, LogType.PAY, "Fetching volume plans");
                        this.m_volumePlans = bIConnection.getVolumePlans();
                        bIConnection.disconnect();
                    }
                    catch (Exception exception) {
                        if (bIConnection != null) {
                            bIConnection.disconnect();
                        }
                        if (Thread.currentThread().isInterrupted()) break block3;
                        LogHolder.log(2, LogType.NET, "Error fetching payment options: ", exception);
                        AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                        Thread.currentThread().interrupt();
                    }
                }
            }

            public Object getValue() {
                return this.m_volumePlans;
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHINGPLANS), dialogContentPane, iReturnRunnable){

            public boolean isSkippedAsNextContentPane() {
                return false;
            }

            public boolean isMoveForwardAllowed() {
                return jAPDialog.isVisible() && iReturnBooleanRunnable != null && !iReturnBooleanRunnable.isTrue();
            }
        };
        final VolumePlanSelectionPane volumePlanSelectionPane = new VolumePlanSelectionPane(jAPDialog, workerContentPane, bl);
        WorkerContentPane workerContentPane2 = new WorkerContentPane(jAPDialog, (String)null, (DialogContentPane)volumePlanSelectionPane, (Runnable)null){

            public Object getValue() {
                return iReturnAccountRunnable.getAccount().getTerms();
            }
        };
        TermsAndConditionsPane termsAndConditionsPane = new TermsAndConditionsPane(jAPDialog, workerContentPane2, true, new TermsAndConditionsPane.TermsAndConditionsMessages()){

            public boolean isSkippedAsNextContentPane() {
                return volumePlanSelectionPane.isCouponUsed() || this.isTermsAccepted();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }

            public boolean isMoveBackAllowed() {
                return false;
            }
        };
        IReturnRunnable iReturnRunnable2 = new IReturnRunnable(){
            private XMLPaymentOptions m_paymentOptions;

            public void run() {
                block3: {
                    BIConnection bIConnection = null;
                    try {
                        PaymentInstanceDBEntry paymentInstanceDBEntry = iReturnAccountRunnable.getAccount().getBI();
                        bIConnection = new BIConnection(paymentInstanceDBEntry);
                        bIConnection.connect();
                        bIConnection.authenticate(iReturnAccountRunnable.getAccount());
                        LogHolder.log(7, LogType.PAY, "Fetching payment options");
                        this.m_paymentOptions = bIConnection.getPaymentOptions();
                        bIConnection.disconnect();
                    }
                    catch (Exception exception) {
                        if (bIConnection != null) {
                            bIConnection.disconnect();
                        }
                        if (Thread.currentThread().isInterrupted()) break block3;
                        LogHolder.log(2, LogType.NET, "Error fetching payment options: " + exception.getMessage());
                        AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                        Thread.currentThread().interrupt();
                    }
                }
            }

            public Object getValue() {
                return this.m_paymentOptions;
            }
        };
        WorkerContentPane workerContentPane3 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHINGOPTIONS), (DialogContentPane)termsAndConditionsPane, (Runnable)iReturnRunnable2){

            public boolean isSkippedAsNextContentPane() {
                if (volumePlanSelectionPane.isCouponUsed()) {
                    LogHolder.log(7, LogType.PAY, "Coupon entered, skipping payment options pane");
                    return true;
                }
                return false;
            }
        };
        final MethodSelectionPane methodSelectionPane = new MethodSelectionPane(jAPDialog, workerContentPane3){

            public boolean isSkippedAsNextContentPane() {
                if (volumePlanSelectionPane.isCouponUsed()) {
                    LogHolder.log(7, LogType.PAY, "Coupon entered, skipping payment options pane");
                    return true;
                }
                return false;
            }
        };
        final IReturnRunnable iReturnRunnable3 = new IReturnRunnable(){
            private XMLTransCert m_transCert;

            public void run() {
                block6: {
                    if (this.m_transCert == null) {
                        try {
                            String string;
                            String string2;
                            String string3;
                            Object object;
                            LogHolder.log(7, LogType.PAY, "Fetching Transaction Certificate from Payment Instance");
                            if (volumePlanSelectionPane.isCouponUsed()) {
                                object = JAPMessages.getString(MSG_COUPON);
                                string3 = object;
                                string2 = object;
                                string = "0";
                            } else {
                                string3 = volumePlanSelectionPane.getSelectedVolumePlan().getName();
                                string2 = methodSelectionPane.getSelectedPaymentOption().getName();
                                int n = volumePlanSelectionPane.getSelectedVolumePlan().getPrice();
                                string = new Integer(n).toString();
                            }
                            object = iReturnAccountRunnable.getAccount();
                            XMLGenericStrings xMLGenericStrings = new XMLGenericStrings(((PayAccount)object).getBI());
                            xMLGenericStrings.addEntry("plan", string3);
                            xMLGenericStrings.addEntry("method", string2);
                            xMLGenericStrings.addEntry("amount", string);
                            String string4 = JAPMessages.getLocale().getLanguage();
                            xMLGenericStrings.addEntry("language", string4);
                            this.m_transCert = ((PayAccount)object).charge(xMLGenericStrings);
                            if (this.m_transCert != null) {
                                vector.addElement(this.m_transCert);
                            }
                        }
                        catch (Exception exception) {
                            if (Thread.currentThread().isInterrupted()) break block6;
                            LogHolder.log(2, LogType.NET, "Error fetching TransCert: ", exception);
                            AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            public Object getValue() {
                return this.m_transCert;
            }
        };
        final WorkerContentPane workerContentPane4 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHINGTAN), (DialogContentPane)methodSelectionPane, (Runnable)iReturnRunnable3){

            public boolean isSkippedAsNextContentPane() {
                return iReturnRunnable3.getValue() != null;
            }
        };
        WorkerContentPane workerContentPane5 = this.createUpdateAccountPane(iReturnAccountRunnable, jAPDialog, workerContentPane4, false, true);
        PaymentInfoPane paymentInfoPane = new PaymentInfoPane(jAPDialog, workerContentPane5){

            public boolean isSkippedAsNextContentPane() {
                if (volumePlanSelectionPane.isCouponUsed()) {
                    return true;
                }
                return methodSelectionPane.getSelectedPaymentOption() != null && methodSelectionPane.getSelectedPaymentOption().getType() != null && !methodSelectionPane.getSelectedPaymentOption().getType().equalsIgnoreCase("active");
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        final PassivePaymentPane passivePaymentPane = new PassivePaymentPane(jAPDialog, paymentInfoPane){

            public boolean isSkippedAsNextContentPane() {
                if (volumePlanSelectionPane.isCouponUsed()) {
                    return true;
                }
                return methodSelectionPane.getSelectedPaymentOption() != null && methodSelectionPane.getSelectedPaymentOption().getType().equalsIgnoreCase("active");
            }
        };
        IReturnRunnable iReturnRunnable4 = new IReturnRunnable(){
            private Boolean m_successful = new Boolean(true);

            public void run() {
                block7: {
                    BIConnection bIConnection = new BIConnection(iReturnAccountRunnable.getAccount().getBI());
                    XMLPassivePayment xMLPassivePayment = new XMLPassivePayment(iReturnAccountRunnable.getAccount().getPIID());
                    if (volumePlanSelectionPane.isCouponUsed()) {
                        xMLPassivePayment.addData("code", volumePlanSelectionPane.getEnteredCouponCode());
                        xMLPassivePayment.setPaymentName("Coupon");
                        long l = iReturnAccountRunnable.getAccount().getAccountNumber();
                        xMLPassivePayment.addData("accountnumber", new Long(l).toString());
                        XMLTransCert xMLTransCert = (XMLTransCert)workerContentPane4.getValue();
                        long l2 = xMLTransCert.getTransferNumber();
                        xMLPassivePayment.addData("transfernumber", new Long(l2).toString());
                    } else if (methodSelectionPane.getSelectedPaymentOption().getType().equalsIgnoreCase("passive")) {
                        xMLPassivePayment = passivePaymentPane.getEnteredInfo(iReturnAccountRunnable.getAccount());
                        XMLVolumePlan xMLVolumePlan = volumePlanSelectionPane.getSelectedVolumePlan();
                        String string = xMLVolumePlan.getName();
                        int n = xMLVolumePlan.getPrice();
                        xMLPassivePayment.setAmount(n);
                        xMLPassivePayment.addData("volumeplan", string);
                        long l = iReturnAccountRunnable.getAccount().getAccountNumber();
                        xMLPassivePayment.addData("accountnumber", new Long(l).toString());
                    }
                    try {
                        bIConnection.connect();
                        bIConnection.authenticate(iReturnAccountRunnable.getAccount());
                        if (!bIConnection.sendPassivePayment(xMLPassivePayment, iReturnAccountRunnable.getAccount())) {
                            this.m_successful = new Boolean(false);
                        }
                        bIConnection.disconnect();
                    }
                    catch (Exception exception) {
                        if (bIConnection != null) {
                            bIConnection.disconnect();
                        }
                        this.m_successful = new Boolean(false);
                        if (Thread.currentThread().isInterrupted()) break block7;
                        LogHolder.log(2, LogType.PAY, "Could not send PassivePayment to payment instance: " + exception.getMessage());
                        AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                        Thread.currentThread().interrupt();
                    }
                }
            }

            public Object getValue() {
                return this.m_successful;
            }
        };
        final WorkerContentPane workerContentPane6 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_SENDINGPASSIVE), passivePaymentPane, iReturnRunnable4){

            public boolean isSkippedAsNextContentPane() {
                if (volumePlanSelectionPane.isCouponUsed()) {
                    return false;
                }
                return methodSelectionPane.getSelectedPaymentOption() != null && methodSelectionPane.getSelectedPaymentOption().getType().equalsIgnoreCase("active");
            }
        };
        WorkerContentPane workerContentPane7 = this.createUpdateAccountPane(iReturnAccountRunnable, jAPDialog, workerContentPane6, true, false);
        DialogContentPaneOptions dialogContentPaneOptions = new DialogContentPaneOptions(workerContentPane7);
        SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(jAPDialog, JAPMessages.getString(MSG_SENTPASSIVE), null, dialogContentPaneOptions){

            public boolean isSkippedAsNextContentPane() {
                return false;
            }

            public DialogContentPane.CheckError checkUpdate() {
                boolean bl;
                String string;
                Vector<String> vector2 = new Vector<String>();
                String string2 = "<Font color='red'><b>";
                String string3 = "</b></Font>";
                String string4 = string2 + JAPMessages.getString(MSG_BACKUP_WARNING) + string3;
                String string5 = string2 + JAPMessages.getString(MSG_NOTSENTPASSIVE) + string3;
                String string6 = JAPMessages.getString(MSG_SENTPASSIVE);
                String string7 = JAPMessages.getString(MSG_COUPON_SENT);
                String string8 = string2 + JAPMessages.getString(MSG_COUPON_FAILED) + string3;
                String string9 = JAPMessages.getString(MSG_ACTIVE_COMPLETE);
                String string10 = JAPMessages.getLocale().getLanguage();
                String string11 = null;
                boolean bl2 = iReturnAccountRunnable.getAccount().isCharged();
                if (volumePlanSelectionPane.isCouponUsed()) {
                    string = "coupon";
                } else {
                    string = methodSelectionPane.getSelectedPaymentOption().getType();
                    string11 = methodSelectionPane.getSelectedPaymentOption().getPaymentDelay(string10);
                }
                if (string.equalsIgnoreCase("active")) {
                    if (bl2) {
                        vector2.addElement(JAPMessages.getString(MSG_CHARGING_SUCCESSFUL));
                    } else {
                        vector2.addElement(string9);
                        vector2.addElement(string11);
                    }
                    bl2 = true;
                    vector2.addElement(string4);
                } else if (string.equals("coupon")) {
                    bl = (Boolean)workerContentPane6.getValue();
                    if (bl) {
                        if (iReturnAccountRunnable.getAccount().isWaitingForTransaction()) {
                            string7 = string7 + " " + JAPMessages.getString(AccountCreator.MSG_WAITING_FOR_TRANSACTION);
                        }
                        vector2.addElement(string7);
                        vector2.addElement(string4);
                        bl2 = true;
                    } else {
                        vector2.addElement(string8);
                        vector.removeAllElements();
                    }
                } else {
                    bl = (Boolean)workerContentPane6.getValue();
                    if (bl) {
                        if (bl2) {
                            vector2.addElement(JAPMessages.getString(MSG_CHARGING_SUCCESSFUL));
                        } else {
                            vector2.addElement(string6);
                            vector2.addElement(string11);
                        }
                        bl2 = true;
                        vector2.addElement(string4);
                    } else {
                        vector2.addElement(string5);
                    }
                }
                vector2.addElement(JAPMessages.getString(MSG_THANK_YOU));
                String string12 = "";
                Object object = vector2.elements();
                while (object.hasMoreElements()) {
                    String string13 = (String)object.nextElement();
                    if (string13 == null) continue;
                    string12 = string12 + "<p>";
                    string12 = string12 + string13;
                    string12 = string12 + "</p><br>";
                }
                this.setText(string12);
                object = JAPController.getInstance().getCurrentMixCascade();
                if (bl2 && iReturnAccountRunnable.getAccount().isCharged() && !TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                    JAPController.getInstance().switchToNextMixCascade();
                }
                if (bl2 && ((MixCascade)object).isPayment() && ((MixCascade)object).getPIID().equals(iReturnAccountRunnable.getAccount().getPIID()) && JAPModel.isAutomaticallyReconnected()) {
                    PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
                    if (payAccount == null || !((MixCascade)object).getPIID().equals(payAccount.getPIID())) {
                        payAccount = PayAccountsFile.getInstance().getChargedAccount(iReturnAccountRunnable.getAccount().getPIID());
                        if (payAccount != null && payAccount.getAccountNumber() != iReturnAccountRunnable.getAccount().getAccountNumber()) {
                            payAccount = null;
                        }
                        if (payAccount != null) {
                            PayAccountsFile.getInstance().setActiveAccount(payAccount);
                        }
                    } else if (!payAccount.isCharged()) {
                        payAccount = PayAccountsFile.getInstance().getChargedAccount(payAccount.getPIID());
                    }
                    if (payAccount != null && !payAccount.isCharged()) {
                        payAccount = null;
                    }
                    if (payAccount != null && TrustModel.getCurrentTrustModel().isTrusted((MixCascade)object)) {
                        JAPController.getInstance().start();
                    }
                }
                return null;
            }

            public boolean hideButtonCancel() {
                return true;
            }

            public boolean hideButtonNo() {
                return true;
            }
        };
    }

    public void showPaymentDialog(String string) {
        this.doCreateAccount(string);
    }

    public void showOpenTransaction(PayAccount payAccount) {
        PayAccount payAccount2 = payAccount;
        if (payAccount2 != null && !payAccount2.isTransactionExpired() && payAccount2.getBI() != null) {
            try {
                Transaction transaction = payAccount2.getTransaction();
                URL uRL = payAccount2.getBI().getWebshopURL(transaction.getID(), null);
                if (!transaction.getPaymentMethod().toLowerCase().equals("Coupon".toLowerCase()) || uRL == null) {
                    TransactionOverviewDialog.showActivePaymentDialog(JAPConf.getInstance(), new Long(transaction.getID()).toString(), transaction.getAmountEuroCent(), payAccount2, transaction.getRateID(), transaction.getPaymentMethod(), false);
                } else {
                    JAPController.getInstance().allowDirectProxyDomain(uRL);
                    AbstractOS.getInstance().openURL(uRL);
                }
            }
            catch (Exception exception) {
                this.showPIerror(JAPConf.getInstance().getRootPane(), exception);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doCreateAccount(String string) {
        try {
            this.doCreateAccountInternal(string);
            Object var3_2 = null;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doCreateAccountInternal(final String string) {
        Object object = this.SYNC_CREATE_ACCOUNT;
        synchronized (object) {
            if (this.m_bCreatingAccount) {
                return;
            }
            this.m_bCreatingAccount = true;
        }
        boolean bl = PayAccountsFile.getInstance().isAIAccountErrorIgnored();
        PayAccountsFile.getInstance().setIgnoreAIAccountError(true);
        JAPController.getInstance().setAllowPaidServices(true);
        final JAPDialog jAPDialog = new JAPDialog(this.getRootPanel(), JAPMessages.getString(ActivePaymentDetails.MSG_PAYBUTTON), true);
        jAPDialog.setDefaultCloseOperation(0);
        LinkRegistrator linkRegistrator = new LinkRegistrator(jAPDialog.getRootPane(), JAPConf.getInstance().getMainView());
        DialogContentPane dialogContentPane = AccountCreator.createAccountPanes(jAPDialog, new DialogContentPane.Layout(), null, null, linkRegistrator, null, 2, new BooleanVariable(true));
        IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            private Vector allJpis;

            public void run() {
                PaymentInstanceDBEntry paymentInstanceDBEntry;
                if (string != null && (paymentInstanceDBEntry = PayAccountsFile.getInstance().getBI(string)) != null) {
                    this.allJpis = new Vector();
                    this.allJpis.addElement(paymentInstanceDBEntry);
                }
                if (this.allJpis == null || this.allJpis.size() == 0) {
                    this.allJpis = PayAccountsFile.getInstance().getPaymentInstances();
                }
                if (this.allJpis.size() == 0) {
                    JAPController.getInstance().updatePaymentInstances(false);
                    this.allJpis = PayAccountsFile.getInstance().getPaymentInstances();
                }
            }

            public Object getValue() {
                return this.allJpis;
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHING_BIS) + "...", JAPMessages.getString(MSG_FETCHING_BIS), dialogContentPane, iReturnRunnable){

            public boolean isMoveForwardAllowed() {
                return jAPDialog.isVisible();
            }
        };
        final JpiSelectionPane jpiSelectionPane = new JpiSelectionPane(jAPDialog, workerContentPane, string){
            private PaymentInstanceDBEntry m_pi;

            public boolean isSkippedAsNextContentPane() {
                return this.isSkippedAsContentPane();
            }

            public DialogContentPane.CheckError checkUpdate() {
                return super.checkUpdate();
            }

            public PaymentInstanceDBEntry getSelectedPaymentInstance() {
                if (this.m_pi != null) {
                    return this.m_pi;
                }
                return super.getSelectedPaymentInstance();
            }

            public boolean isSkippedAsContentPane() {
                PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountSettingsPanel.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(JAPController.getInstance().getCurrentMixCascade().getPIID());
                Vector vector = PayAccountsFile.getInstance().getPaymentInstances(paymentInstanceDBEntry == null || !paymentInstanceDBEntry.isTest());
                if (string != null) {
                    this.m_pi = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountSettingsPanel.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(string);
                    return true;
                }
                if (vector.size() <= 1) {
                    if (vector.size() == 1) {
                        this.m_pi = (PaymentInstanceDBEntry)vector.elementAt(0);
                    }
                    return true;
                }
                return false;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return this.isSkippedAsContentPane();
            }
        };
        Runnable runnable = new Runnable(){

            public void run() {
                block4: {
                    if (jpiSelectionPane.getSelectedPaymentInstance() == null) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    BIConnection bIConnection = null;
                    try {
                        bIConnection = new BIConnection(jpiSelectionPane.getSelectedPaymentInstance());
                        bIConnection.connect();
                        bIConnection.disconnect();
                    }
                    catch (Exception exception) {
                        if (bIConnection != null) {
                            bIConnection.disconnect();
                        }
                        if (Thread.currentThread().isInterrupted()) break block4;
                        AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        WorkerContentPane workerContentPane2 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_TEST_PI_CONNECTION) + "...", jpiSelectionPane, runnable);
        final WorkerContentPane workerContentPane3 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHINGTERMS), (DialogContentPane)jpiSelectionPane, (Runnable)new FetchTermsRunnable(jAPDialog, jpiSelectionPane)){

            public boolean isSkippedAsNextContentPane() {
                return this.getValue() != null;
            }
        };
        final IReturnRunnable iReturnRunnable2 = new IReturnRunnable(){
            private AsymmetricCryptoKeyPair m_keyPair;

            public void run() {
                AccountSettingsPanel.this.m_bDoNotCloseDialog = true;
                this.m_keyPair = PayAccountsFile.getInstance().createAccountKeyPair();
                if (this.m_keyPair == null) {
                    JAPDialog.showErrorDialog(jAPDialog, JAPMessages.getString(MSG_KEY_PAIR_CREATE_ERROR));
                    Thread.currentThread().interrupt();
                }
                AccountSettingsPanel.this.m_bDoNotCloseDialog = false;
            }

            public Object getValue() {
                return this.m_keyPair;
            }
        };
        final WorkerContentPane workerContentPane4 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_CREATE_KEY_PAIR) + "...", (DialogContentPane)workerContentPane3, (Runnable)iReturnRunnable2){

            public boolean hideButtonCancel() {
                return true;
            }
        };
        this.m_bReady = true;
        final IReturnAccountRunnable iReturnAccountRunnable = new IReturnAccountRunnable(){
            private PayAccount m_payAccount;
            private IOException m_connectionError;

            public void run() {
                AccountSettingsPanel.this.m_bReady = false;
                this.m_connectionError = null;
                try {
                    DSAKeyPair dSAKeyPair = (DSAKeyPair)workerContentPane4.getValue();
                    PaymentInstanceDBEntry paymentInstanceDBEntry = jpiSelectionPane.getSelectedPaymentInstance();
                    XMLGenericText xMLGenericText = (XMLGenericText)workerContentPane3.getValue();
                    this.m_payAccount = PayAccountsFile.getInstance().createAccount(paymentInstanceDBEntry, dSAKeyPair, xMLGenericText);
                    this.m_payAccount.fetchAccountInfo(true);
                }
                catch (InterruptedIOException interruptedIOException) {
                    LogHolder.log(4, LogType.GUI, interruptedIOException);
                    Thread.currentThread().interrupt();
                }
                catch (IOException iOException) {
                    this.m_connectionError = iOException;
                    AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), iOException);
                    Thread.currentThread().interrupt();
                }
                catch (Exception exception) {
                    if (!Thread.currentThread().isInterrupted() && exception.getMessage() != null && !exception.getMessage().equals("CAPTCHA")) {
                        AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                    } else {
                        LogHolder.log(4, LogType.GUI, exception);
                    }
                    Thread.currentThread().interrupt();
                }
            }

            public PayAccount getAccount() {
                Object object = this.getValue();
                if (object instanceof PayAccount) {
                    return (PayAccount)object;
                }
                return null;
            }

            public Object getValue() {
                if (this.m_connectionError != null) {
                    return this.m_connectionError;
                }
                return this.m_payAccount;
            }
        };
        AccountCreationPane accountCreationPane = new AccountCreationPane(jAPDialog, JAPMessages.getString(MSG_ACCOUNTCREATEDESC), workerContentPane4, (Runnable)iReturnAccountRunnable);
        final CaptchaContentPane captchaContentPane = new CaptchaContentPane(jAPDialog, accountCreationPane){

            public void gotCaptcha(ICaptchaSender iCaptchaSender, IImageEncodedCaptcha iImageEncodedCaptcha) {
                if (iReturnRunnable2.getValue() != null) {
                    super.gotCaptcha(iCaptchaSender, iImageEncodedCaptcha);
                }
            }

            public String getButtonNoText() {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                if (gregorianCalendar.get(5) == 27 && gregorianCalendar.get(2) == 8 || gregorianCalendar.get(5) == 4 && gregorianCalendar.get(2) == 10) {
                    return JAPMessages.getString(MSG_NEWCAPTCHAEASTEREGG);
                }
                return JAPMessages.getString(MSG_NEWCAPTCHA);
            }
        };
        PayAccountsFile.getInstance().addPaymentListener(captchaContentPane);
        captchaContentPane.addComponentListener(new ComponentAdapter(){

            public void componentShown(ComponentEvent componentEvent) {
                try {
                    if (iReturnAccountRunnable.getValue() instanceof IOException) {
                        captchaContentPane.printErrorStatusMessage(JAPMessages.getString(MSG_BI_CONNECTION_LOST));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                AccountSettingsPanel.this.m_bDoNotCloseDialog = false;
            }
        });
        PasswordContentPane passwordContentPane = new PasswordContentPane(jAPDialog, captchaContentPane, 1, JAPMessages.getString(MSG_ACCPASSWORD)){

            public DialogContentPane.CheckError checkYesOK() {
                DialogContentPane.CheckError checkError = super.checkYesOK();
                if (checkError == null) {
                    this.setButtonValue(0);
                    if (this.getPassword() != null) {
                        JAPController.getInstance().setPaymentPassword(new String(this.getPassword()));
                    } else {
                        JAPController.getInstance().setPaymentPassword("");
                    }
                }
                return checkError;
            }

            public boolean isSkippedAsNextContentPane() {
                return jAPDialog.isVisible();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return jAPDialog.isVisible();
            }
        };
        final ExportThread exportThread = new ExportThread(jAPDialog, iReturnAccountRunnable);
        WorkerContentPane workerContentPane5 = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_SAVE_CONFIG) + "...", (DialogContentPane)passwordContentPane, (Runnable)exportThread){

            public boolean isMoveBackAllowed() {
                return false;
            }

            public boolean hideButtonCancel() {
                return true;
            }
        };
        SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(jAPDialog, "<Font color=\"red\">" + JAPMessages.getString(MSG_CREATED_ACCOUNT_NOT_SAVED) + "</Font>", new DialogContentPane.Layout("", 0), new DialogContentPaneOptions(workerContentPane5)){

            public boolean isSkippedAsNextContentPane() {
                return exportThread.getValue() != null && (Boolean)exportThread.getValue() != false;
            }

            public boolean isMoveForwardAllowed() {
                return jAPDialog.isVisible();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }

            public boolean hideButtonCancel() {
                return true;
            }
        };
        final Vector vector = new Vector();
        jAPDialog.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                if (!AccountSettingsPanel.this.m_bDoNotCloseDialog) {
                    if (captchaContentPane.isVisible()) {
                        captchaContentPane.setButtonValue(-1);
                        captchaContentPane.checkCancel();
                    }
                    jAPDialog.dispose();
                }
            }

            public void windowClosed(WindowEvent windowEvent) {
                PayAccountsFile.getInstance().removePaymentListener(captchaContentPane);
                Object object = iReturnAccountRunnable.getValue();
                if (vector.size() == 0 && object != null && object instanceof PayAccount) {
                    ((PayAccount)object).unlock();
                    PayAccountsFile.getInstance().deleteAccount((PayAccount)object);
                }
                AccountSettingsPanel.this.updateAccountList();
                if (vector.size() != 0 && object != null && object instanceof PayAccount) {
                    AccountSettingsPanel.this.m_listAccounts.setSelectedValue(object, true);
                    PayAccount payAccount = AccountSettingsPanel.this.getSelectedAccount();
                    if (payAccount != null) {
                        payAccount.updated();
                    }
                }
            }
        });
        this.m_bDoNotCloseDialog = false;
        this.doChargeAccount(iReturnAccountRunnable, jAPDialog, simpleWizardContentPane, exportThread, vector, true);
        dialogContentPane.pack();
        captchaContentPane.setText(captchaContentPane.getText() + " " + JAPMessages.getString(MSG_NEW_CAPTCHA_HINT, JAPMessages.getString(MSG_NEWCAPTCHA)));
        jAPDialog.setLocationRelativeTo(jAPDialog.getOwner(), 0);
        jAPDialog.setVisible(true);
        PayAccountsFile.getInstance().setIgnoreAIAccountError(bl);
        linkRegistrator.unregisterAll();
        Object object2 = this.SYNC_CREATE_ACCOUNT;
        synchronized (object2) {
            this.m_bCreatingAccount = false;
        }
    }

    private void doSelectAccount(PayAccount payAccount) {
        int n = 0;
        if (payAccount == null || payAccount == PayAccountsFile.getInstance().getActiveAccount()) {
            return;
        }
        n = this.hasDisconnected(true, PayAccountsFile.getInstance().getActiveAccount());
        if (n == -1) {
            return;
        }
        PayAccountsFile payAccountsFile = PayAccountsFile.getInstance();
        try {
            payAccountsFile.setActiveAccount(payAccount);
        }
        catch (Exception exception) {
            JAPDialog.showErrorDialog((Component)GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString("Could not select account!"), (Throwable)exception);
        }
        if (n == 1) {
            this.reconnect();
        }
    }

    private WorkerContentPane createUpdateAccountPane(final IReturnAccountRunnable iReturnAccountRunnable, final JAPDialog jAPDialog, DialogContentPane dialogContentPane, boolean bl, final boolean bl2) {
        IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            private Boolean m_result = new Boolean(false);

            public Object getValue() {
                return this.m_result;
            }

            public void run() {
                block3: {
                    try {
                        if (iReturnAccountRunnable.getAccount().fetchAccountInfo(true) != null) {
                            this.m_result = new Boolean(true);
                        }
                    }
                    catch (Exception exception) {
                        if (Thread.currentThread().isInterrupted()) break block3;
                        AccountSettingsPanel.this.showPIerror(jAPDialog.getRootPane(), exception);
                        LogHolder.log(2, LogType.PAY, "Could not get account statement");
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_GETACCOUNTSTATEMENT), dialogContentPane, iReturnRunnable){

            public boolean isSkippedAsNextContentPane() {
                if (iReturnAccountRunnable.getAccount() == null) {
                    return true;
                }
                return bl2 && !iReturnAccountRunnable.getAccount().isUsed() && !iReturnAccountRunnable.getAccount().isTransactionExpired();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return this.isSkippedAsNextContentPane() || bl2;
            }
        };
        return workerContentPane;
    }

    public void updateAccountShown() {
        this.doGetStatement((PayAccount)this.m_listAccounts.getSelectedValue());
    }

    private void doGetStatement(PayAccount payAccount) {
        if (payAccount == null) {
            return;
        }
        JAPDialog jAPDialog = new JAPDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(MSG_GETACCOUNTSTATEMENTTITLE), true);
        WorkerContentPane workerContentPane = this.createUpdateAccountPane(new FixedReturnAccountRunnable(payAccount), jAPDialog, null, false, false);
        workerContentPane.pack();
        jAPDialog.setResizable(false);
        jAPDialog.setLocationRelativeTo(jAPDialog.getOwner(), 0);
        jAPDialog.setVisible(true);
    }

    private void doExportAccount(PayAccount payAccount) {
        if (payAccount == null) {
            return;
        }
        if (payAccount.getPrivateKey() != null) {
            JAPDialog jAPDialog = new JAPDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(MSG_ACCPASSWORDTITLE), true);
            PasswordContentPane passwordContentPane = JAPController.getInstance().getPaymentPassword() != null ? new PasswordContentPane(jAPDialog, 3, JAPMessages.getString(MSG_EXPORTENCRYPT, "" + payAccount.getAccountNumber())){

                public char[] getComparedPassword() {
                    return JAPController.getInstance().getPaymentPassword().toCharArray();
                }

                public String getOldPasswordLabel() {
                    return JAPMessages.getString(PasswordContentPane.MSG_ENTER_LBL);
                }

                public String getNewPasswordLabel() {
                    return JAPMessages.getString(MSG_PASSWORD_EXPORT);
                }
            } : new PasswordContentPane(jAPDialog, 1, JAPMessages.getString(MSG_EXPORTENCRYPT, "" + payAccount.getAccountNumber())){

                public String getNewPasswordLabel() {
                    return JAPMessages.getString(MSG_PASSWORD_EXPORT);
                }
            };
            passwordContentPane.updateDialog();
            jAPDialog.pack();
            jAPDialog.setVisible(true);
            if (passwordContentPane.getButtonValue() == 0 && this.exportAccount(payAccount, this.getRootPanel(), new String(passwordContentPane.getPassword()))) {
                payAccount.setBackupDone(System.currentTimeMillis());
                this.doShowDetails(payAccount);
                this.enableDisableButtons();
            }
        } else if (this.exportAccount(payAccount, this.getRootPanel(), null)) {
            payAccount.setBackupDone(System.currentTimeMillis());
            this.doShowDetails(payAccount);
            this.enableDisableButtons();
        }
    }

    private boolean exportAccount(PayAccount payAccount, Component component, String string) {
        int n;
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setSelectedFile(new File(payAccount.getAccountNumber() + ".acc"));
        MyFileFilter myFileFilter = new MyFileFilter();
        jFileChooser.setFileFilter(myFileFilter);
        while ((n = GUIUtils.showMonitoredFileChooser(jFileChooser, component, "__FILE_CHOOSER_SAVE")) == 0) {
            try {
                File file = jFileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".acc")) {
                    file = new File(file.getParent(), file.getName() + ".acc");
                }
                if (file.exists() && !JAPDialog.showYesNoDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(MSG_FILE_EXISTS))) continue;
                Document document = XMLUtil.createDocument();
                document.appendChild(payAccount.toXmlElement(document, string, false));
                String string2 = XMLUtil.toString(XMLUtil.formatHumanReadable(document));
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(string2.getBytes());
                fileOutputStream.close();
                JAPDialog.showMessageDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(MSG_EXPORTED));
                return true;
            }
            catch (Exception exception) {
                JAPDialog.showErrorDialog((Component)GUIUtils.getParentWindow(component), JAPMessages.getString(MSG_NOTEXPORTED) + ": " + exception);
                break;
            }
        }
        return false;
    }

    private void doImportAccount() {
        LogHolder.log(4, LogType.GUI, "Begin method import account...");
        PayAccount payAccount = null;
        Object object = null;
        JFileChooser jFileChooser = new JFileChooser();
        MyFileFilter myFileFilter = new MyFileFilter();
        jFileChooser.setFileFilter(myFileFilter);
        LogHolder.log(4, LogType.GUI, "Opening file chooser for importing account...");
        int n = GUIUtils.showMonitoredFileChooser(jFileChooser, this.getRootPanel(), "__FILE_CHOOSER_OPEN");
        LogHolder.log(4, LogType.GUI, "File chooser for importing account returned!");
        if (n == 0) {
            Object object2;
            Object object3;
            File file = jFileChooser.getSelectedFile();
            try {
                object3 = XMLUtil.readXMLDocument(file);
                XMLUtil.removeComments((Node)object3);
                object2 = object3.getDocumentElement();
                object = object2.getNodeName().equals("root") ? (Element)XMLUtil.getFirstChildByName((Node)object2, "Account") : object2;
            }
            catch (Exception exception) {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString(MSG_ACCOUNT_IMPORT_FAILED), (Throwable)exception);
            }
            try {
                if (object != null) {
                    XMLUtil.removeComments(object);
                    payAccount = new PayAccount((Element)object, null);
                    payAccount.setBackupDone(file.lastModified());
                    object3 = PayAccountsFile.getInstance();
                    ((PayAccountsFile)object3).addAccount(payAccount);
                    this.doActivateAccount(payAccount);
                    this.updateAccountList();
                    this.doGetStatement(payAccount);
                }
            }
            catch (Exception exception) {
                object2 = "";
                if (exception instanceof PayAccountsFile.AccountAlreadyExistingException) {
                    object2 = JAPMessages.getString(MSG_ACCOUNT_ALREADY_EXISTING);
                }
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString(MSG_ACCOUNT_IMPORT_FAILED) + (String)object2, (Throwable)exception);
            }
        }
    }

    private void doActivateAccount(PayAccount payAccount) {
        if (payAccount != null) {
            JAPDialog jAPDialog = new JAPDialog(this.getRootPanel(), JAPMessages.getString(MSG_ACCPASSWORDTITLE));
            jAPDialog.setDefaultCloseOperation(1);
            PasswordContentPane passwordContentPane = new PasswordContentPane(jAPDialog, 2, JAPMessages.getString(MSG_GIVE_ACCOUNT_PASSWORD));
            passwordContentPane.setDefaultButtonOperation(1);
            passwordContentPane.updateDialog();
            jAPDialog.pack();
            try {
                PayAccount payAccount2;
                Enumeration enumeration;
                payAccount.decryptPrivateKey(passwordContentPane);
                try {
                    enumeration = PayAccountsFile.getInstance().getAccounts();
                    while (enumeration.hasMoreElements()) {
                        payAccount2 = (PayAccount)enumeration.nextElement();
                        payAccount2.decryptPrivateKey(new SingleStringPasswordReader(passwordContentPane.getPassword()));
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                }
                if (PayAccountsFile.getInstance().getActiveAccount() == null) {
                    if (payAccount.getPrivateKey() != null) {
                        PayAccountsFile.getInstance().setActiveAccount(payAccount);
                    } else {
                        enumeration = PayAccountsFile.getInstance().getAccounts();
                        while (enumeration.hasMoreElements()) {
                            payAccount2 = (PayAccount)enumeration.nextElement();
                            if (payAccount2.getPrivateKey() == null) continue;
                            PayAccountsFile.getInstance().setActiveAccount(payAccount2);
                        }
                    }
                }
                this.doShowDetails(payAccount);
                this.enableDisableButtons();
                this.m_listAccounts.repaint();
                if (payAccount.getPrivateKey() != null) {
                    JAPDialog.showMessageDialog(this.getRootPanel(), JAPMessages.getString(MSG_ACTIVATION_SUCCESSFUL));
                }
            }
            catch (Exception exception) {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString(MSG_ACTIVATION_FAILED), (Throwable)exception);
            }
            jAPDialog.dispose();
        }
    }

    private int hasDisconnected(boolean bl, PayAccount payAccount) {
        if (!JAPController.getInstance().getAnonMode() || JAPController.getInstance().isAnonConnected() && (!JAPController.getInstance().getCurrentMixCascade().isPayment() || payAccount == null || payAccount != PayAccountsFile.getInstance().getActiveAccount())) {
            return 0;
        }
        if (!JAPController.getInstance().isAnonConnected() || 0 == JAPDialog.showConfirmDialog((Component)GUIUtils.getParentWindow(this.getRootPanel()), bl ? JAPMessages.getString(MSG_CONNECTIONACTIVE_SELECT_QUESTION) : JAPMessages.getString(MSG_CONNECTIONACTIVE_QUESTION), JAPMessages.getString(JAPDialog.MSG_TITLE_WARNING), new JAPDialog.Options(2){

            public String getYesOKText() {
                return JAPMessages.getString(JAPDialog.MSG_BTN_PROCEED);
            }
        }, 2, null)) {
            JAPDialog jAPDialog = new JAPDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(WorkerContentPane.MSG_PLEASE_WAIT) + "...");
            WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(WorkerContentPane.MSG_PLEASE_WAIT), new Runnable(){

                public void run() {
                    JAPController.getInstance().stopAnonModeWait();
                }
            });
            workerContentPane.updateDialog();
            jAPDialog.pack();
            jAPDialog.setResizable(false);
            jAPDialog.setVisible(true);
            return 1;
        }
        return -1;
    }

    private void doDeleteAccount(PayAccount payAccount) {
        String string;
        int n = 0;
        if (payAccount == null) {
            return;
        }
        PayAccountsFile payAccountsFile = PayAccountsFile.getInstance();
        Timestamp timestamp = new Timestamp(new Date().getTime());
        boolean bl = false;
        XMLBalance xMLBalance = payAccount.getBalance();
        if (payAccount.isLocked()) {
            JAPDialog.showErrorDialog((Component)GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(PayAccountsFile.MSG_ERROR_DELETION_ACCOUNT_BLOCKED));
            return;
        }
        if (xMLBalance == null && (bl = JAPDialog.showYesNoDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString("ngDeleteAccountStatement")))) {
            this.doGetStatement(payAccount);
        }
        if (!payAccount.hasExpired(timestamp)) {
            XMLAccountInfo xMLAccountInfo = payAccount.getAccountInfo();
            xMLBalance = xMLAccountInfo != null ? xMLAccountInfo.getBalance() : null;
            if (!bl && xMLAccountInfo != null && xMLAccountInfo.getLastBalanceUpdateLocalTime().before(new Timestamp(System.currentTimeMillis() - 86400000L)) && (bl = JAPDialog.showYesNoDialog(GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(MSG_OLDSTATEMENT)))) {
                this.doGetStatement(payAccount);
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(timestamp);
            gregorianCalendar.add(2, 1);
            string = payAccount.isCharged(timestamp) || payAccount.isCharged(new Timestamp(gregorianCalendar.getTime().getTime())) ? JAPMessages.getString("ngDeleteAccountCreditLeft") : JAPMessages.getString("ngReallyDeleteAccount");
        } else {
            string = JAPMessages.getString("ngReallyDeleteAccount");
        }
        if (JAPDialog.showYesNoDialog(GUIUtils.getParentWindow(this.getRootPanel()), string)) {
            n = this.hasDisconnected(false, payAccount);
            if (n == -1) {
                return;
            }
            try {
                this.m_listAccounts.clearSelection();
                payAccountsFile.deleteAccount(payAccount);
                this.updateAccountList();
                this.doShowDetails(this.getSelectedAccount());
                this.enableDisableButtons();
            }
            catch (Exception exception) {
                JAPDialog.showErrorDialog((Component)GUIUtils.getParentWindow(this.getRootPanel()), JAPMessages.getString(MSG_ERROR_DELETING), (Throwable)exception);
            }
        }
        if (n == 1) {
            this.reconnect();
        }
    }

    private void reconnect() {
        if (JAPController.getInstance().getAnonMode()) {
            return;
        }
        MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
        PayAccount payAccount = PayAccountsFile.getInstance().getActiveAccount();
        PaymentInstanceDBEntry paymentInstanceDBEntry = null;
        if (payAccount != null) {
            paymentInstanceDBEntry = payAccount.getBI();
        }
        if (JAPModel.isAutomaticallyReconnected() && (!mixCascade.isPayment() || JAPModel.getInstance().isCascadeAutoSwitched() || mixCascade.getPIID() != null && paymentInstanceDBEntry != null && paymentInstanceDBEntry.getId().equals(mixCascade.getPIID()) && payAccount.isCharged(new Timestamp(System.currentTimeMillis())))) {
            LogHolder.log(5, LogType.PAY, "re-establish connection");
            if (!JAPController.getInstance().getAnonMode()) {
                JAPController.getInstance().start();
            }
        }
    }

    public String getHelpContext() {
        return "payment";
    }

    protected void onRootPanelShown() {
    }

    protected boolean onOkPressed() {
        JAPModel.getInstance().setPaymentAnonymousConnectionSetting(this.m_comboAnonymousConnection.getSelectedIndex());
        PayAccountsFile.getInstance().setIgnoreAIAccountError(!this.m_cbxShowAIErrors.isSelected());
        JAPModel.getInstance().setHidePaymentPopups(this.m_cbHidePopups.isSelected());
        PayAccountsFile.getInstance().setBalanceAutoUpdateEnabled(this.m_cbxBalanceAutoUpdateEnabled.isSelected());
        JAPController.getInstance().setAskSavePayment(this.m_cbxAskIfNotSaved.isSelected());
        BIConnection.setConnectionTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        return true;
    }

    protected void onCancelPressed() {
    }

    protected void onResetToDefaultsPressed() {
        this.m_comboAnonymousConnection.setSelectedIndex(0);
        this.m_cbxShowAIErrors.setSelected(true);
        this.m_cbHidePopups.setSelected(false);
        this.m_cbxAskIfNotSaved.setSelected(true);
        this.m_cbxBalanceAutoUpdateEnabled.setSelected(true);
        this.setConnectionTimeout(40000);
    }

    protected void onUpdateValues() {
        this.m_comboAnonymousConnection.setSelectedIndex(JAPModel.getInstance().getPaymentAnonymousConnectionSetting());
        this.m_cbxAskIfNotSaved.setSelected(JAPController.getInstance().isAskSavePayment());
        this.m_cbxShowAIErrors.setSelected(!PayAccountsFile.getInstance().isAIAccountErrorIgnored());
        this.m_cbHidePopups.setSelected(JAPModel.getInstance().isPaymentPopupsHidden());
        this.m_cbxBalanceAutoUpdateEnabled.setSelected(PayAccountsFile.getInstance().isBalanceAutoUpdateEnabled());
        this.setConnectionTimeout(BIConnection.getConnectionTimeout());
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getSource() == this.m_listAccounts) {
            this.doShowDetails(this.getSelectedAccount());
            this.enableDisableButtons();
        }
    }

    public void showPIerror(Component component, Exception exception) {
        LogHolder.log(3, LogType.PAY, exception);
        if (exception instanceof XMLErrorMessage) {
            JAPDialog.showErrorDialog(component, PaymentMainPanel.translateBIError((XMLErrorMessage)exception));
        } else if (!JAPModel.getInstance().isAnonConnected() && JAPModel.getInstance().getPaymentAnonymousConnectionSetting() == 1) {
            int n = JAPDialog.showConfirmDialog(component, JAPMessages.getString(MSG_DIRECT_CONNECTION_FORBIDDEN), 0, 0);
            if (n == 0) {
                this.m_comboAnonymousConnection.setSelectedIndex(0);
                JAPModel.getInstance().setPaymentAnonymousConnectionSetting(0);
            }
        } else if (JAPModel.getInstance().isAnonConnected() && JAPModel.getInstance().getPaymentAnonymousConnectionSetting() == 2) {
            int n = JAPDialog.showConfirmDialog(component, JAPMessages.getString(MSG_ANON_CONNECTION_FORBIDDEN), 0, 0);
            if (n == 0) {
                this.m_comboAnonymousConnection.setSelectedIndex(0);
                JAPModel.getInstance().setPaymentAnonymousConnectionSetting(0);
            }
        } else if (!JAPModel.getInstance().isAnonConnected()) {
            JAPDialog.showErrorDialog(component, JAPMessages.getString(MSG_NO_ANONYMITY_POSSIBLY_BLOCKED));
        } else if (exception instanceof ForbiddenIOException) {
            JAPDialog.showErrorDialog(component, JAPMessages.getString(MSG_ERROR_FORBIDDEN));
        } else {
            JAPDialog.showErrorDialog(component, JAPMessages.getString(MSG_CREATEERROR));
        }
    }

    private void setConnectionTimeout(int n) {
        int n2 = n / 1000;
        if (n2 >= (Integer)this.m_comboTimeout.getItemAt(this.m_comboTimeout.getItemCount() - 1)) {
            this.m_comboTimeout.setSelectedIndex(this.m_comboTimeout.getItemCount() - 1);
            BIConnection.setConnectionTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        } else if (n2 <= (Integer)this.m_comboTimeout.getItemAt(0)) {
            this.m_comboTimeout.setSelectedIndex(0);
            BIConnection.setConnectionTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        } else {
            for (int i = 1; i < this.m_comboTimeout.getItemCount(); ++i) {
                if (n2 > (Integer)this.m_comboTimeout.getItemAt(i)) continue;
                this.m_comboTimeout.setSelectedIndex(i);
                break;
            }
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

    public class AccountCreationPane
    extends WorkerContentPane {
        public AccountCreationPane(JAPDialog jAPDialog, String string, WorkerContentPane workerContentPane, Runnable runnable) {
            super(jAPDialog, string, workerContentPane, runnable);
        }

        public boolean isReady() {
            return AccountSettingsPanel.this.m_bReady;
        }

        public boolean isSkippedAsPreviousContentPane() {
            return false;
        }
    }

    private static final class FixedReturnAccountRunnable
    implements IReturnAccountRunnable {
        private PayAccount m_account;

        public FixedReturnAccountRunnable(PayAccount payAccount) {
            this.m_account = payAccount;
        }

        public Object getValue() {
            return this.m_account;
        }

        public PayAccount getAccount() {
            return this.m_account;
        }

        public void run() {
        }
    }

    class CustomRenderer
    extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        CustomRenderer() {
        }

        public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
            JLabel jLabel;
            Component component = super.getListCellRendererComponent((JList<?>)jList, object, n, bl, bl2);
            if (component instanceof JComponent && object != null && object instanceof PayAccount) {
                jLabel = ((PayAccount)object).getPrivateKey() == null ? new JLabel(String.valueOf(((PayAccount)object).getAccountNumber()), GUIUtils.loadImageIcon(IMG_COINS_DISABLED, true), 2) : new JLabel(String.valueOf(((PayAccount)object).getAccountNumber()), GUIUtils.loadImageIcon("coins-full.gif", true), 2);
                if (bl) {
                    jLabel.setOpaque(true);
                    jLabel.setBackground(Color.lightGray);
                }
                Font font = jLabel.getFont();
                if (((PayAccount)object).equals(PayAccountsFile.getInstance().getActiveAccount())) {
                    jLabel.setFont(new Font(font.getName(), 1, font.getSize()));
                } else {
                    jLabel.setFont(new Font(font.getName(), 0, font.getSize()));
                }
            } else {
                jLabel = object != null ? new JLabel(object.toString()) : new JLabel();
            }
            return jLabel;
        }
    }

    private static class MyFileFilter
    extends FileFilter {
        public static final String ACCOUNT_EXTENSION = ".acc";
        private final String ACCOUNT_DESCRIPTION = "JAP Accountfile (*.acc)";
        private int filterType;

        private MyFileFilter() {
        }

        public int getFilterType() {
            return this.filterType;
        }

        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(ACCOUNT_EXTENSION);
        }

        public String getDescription() {
            return "JAP Accountfile (*.acc)";
        }
    }

    private class ExportThread
    implements IReturnBooleanRunnable {
        private JAPDialog m_dialog;
        private IReturnAccountRunnable doIt;
        private Boolean m_bAccountSaved = new Boolean(false);

        public ExportThread(JAPDialog jAPDialog, IReturnAccountRunnable iReturnAccountRunnable) {
            this.m_dialog = jAPDialog;
            this.doIt = iReturnAccountRunnable;
        }

        public void run() {
            block5: {
                AccountSettingsPanel.this.m_bDoNotCloseDialog = true;
                if (JAPController.getInstance().saveConfigFile()) {
                    JAPDialog.showErrorDialog(this.m_dialog, JAPMessages.getString(JAPController.MSG_ERROR_SAVING_CONFIG, JAPModel.getInstance().getConfigFile()));
                    try {
                        if (AccountSettingsPanel.this.exportAccount(this.doIt.getAccount(), this.m_dialog.getContentPane(), JAPController.getInstance().getPaymentPassword())) {
                            this.m_bAccountSaved = new Boolean(true);
                            break block5;
                        }
                        this.m_bAccountSaved = new Boolean(false);
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.MISC, exception);
                    }
                } else {
                    this.m_bAccountSaved = new Boolean(true);
                }
            }
            AccountSettingsPanel.this.m_bDoNotCloseDialog = false;
        }

        public Object getValue() {
            if (!this.m_dialog.isVisible()) {
                return new Boolean(true);
            }
            return this.m_bAccountSaved;
        }

        public boolean isTrue() {
            return (Boolean)this.getValue();
        }
    }

    private final class FetchTermsRunnable
    implements IReturnRunnable {
        private XMLGenericText m_termsAndConditions;
        private JAPDialog m_parentDialog;
        private JpiSelectionPane m_jpiPane;
        PaymentInstanceDBEntry m_jpi;

        public FetchTermsRunnable(JAPDialog jAPDialog, JpiSelectionPane jpiSelectionPane) {
            this.m_parentDialog = jAPDialog;
            this.m_jpiPane = jpiSelectionPane;
        }

        public FetchTermsRunnable(JAPDialog jAPDialog, PaymentInstanceDBEntry paymentInstanceDBEntry, XMLGenericText xMLGenericText) {
            this.m_parentDialog = jAPDialog;
            this.m_jpi = paymentInstanceDBEntry;
            this.m_termsAndConditions = xMLGenericText;
        }

        public void run() {
            block4: {
                BIConnection bIConnection = null;
                try {
                    if (this.m_termsAndConditions != null) {
                        return;
                    }
                    PaymentInstanceDBEntry paymentInstanceDBEntry = this.m_jpiPane != null ? this.m_jpiPane.getSelectedPaymentInstance() : this.m_jpi;
                    bIConnection = new BIConnection(paymentInstanceDBEntry);
                    bIConnection.connect();
                    LogHolder.log(7, LogType.PAY, "Fetching terms and conditions");
                    String string = JAPMessages.getLocale().getLanguage();
                    this.m_termsAndConditions = bIConnection.getTerms(string);
                    bIConnection.disconnect();
                }
                catch (Exception exception) {
                    if (bIConnection != null) {
                        bIConnection.disconnect();
                    }
                    if (Thread.currentThread().isInterrupted()) break block4;
                    LogHolder.log(2, LogType.NET, "Error fetching terms and conditions: ", exception);
                    AccountSettingsPanel.this.showPIerror(this.m_parentDialog.getRootPane(), exception);
                    Thread.currentThread().interrupt();
                }
            }
        }

        public Object getValue() {
            return this.m_termsAndConditions;
        }
    }

    private class MyActionListener
    extends MouseAdapter
    implements ActionListener {
        private boolean m_bButtonClicked = false;

        private MyActionListener() {
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            this.doAction(mouseEvent.getSource());
        }

        public void actionPerformed(ActionEvent actionEvent) {
            this.doAction(actionEvent.getSource());
        }

        public void doAction(final Object object) {
            Thread thread = new Thread(new Runnable(){

                public void run() {
                    PayAccount payAccount;
                    if (object == AccountSettingsPanel.this.m_btnCreateAccount) {
                        AccountSettingsPanel.this.doCreateAccount(null);
                    } else if (object == AccountSettingsPanel.this.m_btnDeleteAccount) {
                        AccountSettingsPanel.this.doDeleteAccount(AccountSettingsPanel.this.getSelectedAccount());
                    } else if (object == AccountSettingsPanel.this.m_btnImportAccount) {
                        AccountSettingsPanel.this.doImportAccount();
                    } else if (object == AccountSettingsPanel.this.m_btnExportAccount) {
                        AccountSettingsPanel.this.doExportAccount(AccountSettingsPanel.this.getSelectedAccount());
                    } else if (object == AccountSettingsPanel.this.m_btnTransactions) {
                        AccountSettingsPanel.this.doShowTransactions();
                    } else if (object == AccountSettingsPanel.this.m_btnSelect) {
                        if (AccountSettingsPanel.this.getSelectedAccount() != null && AccountSettingsPanel.this.getSelectedAccount().getPrivateKey() == null) {
                            AccountSettingsPanel.this.doActivateAccount(AccountSettingsPanel.this.getSelectedAccount());
                        }
                        if (AccountSettingsPanel.this.getSelectedAccount() != null && AccountSettingsPanel.this.getSelectedAccount().getPrivateKey() != null) {
                            AccountSettingsPanel.this.doSelectAccount(AccountSettingsPanel.this.getSelectedAccount());
                        }
                    } else if (object == AccountSettingsPanel.this.m_btnPassword) {
                        AccountSettingsPanel.this.doChangePassword();
                    } else if (object == AccountSettingsPanel.this.m_btnReload) {
                        AccountSettingsPanel.this.doGetStatement(AccountSettingsPanel.this.getSelectedAccount());
                    } else if (object == AccountSettingsPanel.this.m_btnActivate) {
                        AccountSettingsPanel.this.doActivateAccount(AccountSettingsPanel.this.getSelectedAccount());
                    } else if (object == AccountSettingsPanel.this.m_labelVolume) {
                        if (AccountSettingsPanel.this.m_labelVolume.getForeground() == Color.blue) {
                            AccountSettingsPanel.this.showOpenTransaction(AccountSettingsPanel.this.getSelectedAccount());
                        }
                    } else if (object == AccountSettingsPanel.this.m_labelVolumeMonthly) {
                        PayAccount payAccount2 = AccountSettingsPanel.this.getSelectedAccount();
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        if (payAccount2 != null && payAccount2.getVolumeBytesMonthly() > 0L) {
                            double d = payAccount2.getCurrentOverusageFactor(timestamp);
                            XMLBalance xMLBalance = payAccount2.getBalance();
                            if (payAccount2.canDoMonthlyOverusage(timestamp)) {
                                PaymentMainPanel.showMonthlyOverusageQuestion(payAccount2, AccountSettingsPanel.this.getRootPanel(), false, AccountSettingsPanel.this);
                            } else if (payAccount2.isCharged(timestamp) && xMLBalance != null) {
                                if (d > 1.0) {
                                    JAPDialog.showWarningDialog(AccountSettingsPanel.this.getRootPanel(), "<p>" + JAPMessages.getString(MSG_EXPLAIN_PARTIAL_MONTHLY_VOLUME, new String[]{Util.formatBytesValueWithUnit(xMLBalance.getVolumeBytesMonthly(), 2), Util.formatTimestamp(xMLBalance.getOverusageDate(), false), Util.formatBytesValueWithUnit(xMLBalance.getOverusageBytes(), 2), Util.formatTimestamp(XMLBalance.calculateEndOfCurrentMonthlyPeriod(xMLBalance.getFlatEnddate(), timestamp), false), NumberFormat.getInstance(JAPMessages.getLocale()).format((d - 1.0) * 100.0), Util.formatBytesValueWithUnit((long)((double)payAccount2.getVolumeBytesMonthly() / d), 2)}) + "</p><br>" + JAPMessages.getString(PaymentMainPanel.MSG_HINT_AUTO_BALANCE_UPDATES));
                                } else if (xMLBalance != null && xMLBalance.getStartDate() != null && xMLBalance.getStartDate().after(timestamp)) {
                                    if (payAccount2.hasExpired(timestamp)) {
                                        JAPDialog.showWarningDialog(AccountSettingsPanel.this.getRootPanel(), JAPMessages.getString(MSG_WRONG_TIME_TOO_EARLY, new String[]{"" + payAccount2.getAccountNumber(), Util.formatTimestamp(xMLBalance.getStartDate(), false), Util.formatTimestamp(new Date(), false)}));
                                    }
                                } else if (XMLBalance.isLastMonthOfRate(xMLBalance.getFlatEnddate(), timestamp, xMLBalance.getStartDate())) {
                                    String string = JAPMessages.getString(MSG_EXPLAIN_LAST_MONTH, new String[]{Util.formatBytesValueWithUnit(payAccount2.getCurrentCredit(), 2), Util.formatTimestamp(xMLBalance.getFlatEnddate(), false)});
                                    if (xMLBalance.getOverusageDate() != null) {
                                        string = "<p>" + string + "</p><br>" + JAPMessages.getString(PaymentMainPanel.MSG_HINT_AUTO_BALANCE_UPDATES);
                                    }
                                    JAPDialog.showWarningDialog(AccountSettingsPanel.this.getRootPanel(), string);
                                } else {
                                    String string = JAPMessages.getString(PaymentMainPanel.MSG_WEIRD_ACCONT_STATUS);
                                    JAPDialog.showWarningDialog(AccountSettingsPanel.this.getRootPanel(), string);
                                }
                            } else if (xMLBalance != null && xMLBalance.getStartDate() != null && xMLBalance.getStartDate().after(timestamp)) {
                                if (payAccount2.hasExpired(timestamp)) {
                                    JAPDialog.showWarningDialog(AccountSettingsPanel.this.getRootPanel(), JAPMessages.getString(MSG_WRONG_TIME_TOO_EARLY, new String[]{"" + payAccount2.getAccountNumber(), Util.formatTimestamp(xMLBalance.getStartDate(), false), Util.formatTimestamp(new Date(), false)}));
                                }
                            } else {
                                JAPDialog.showWarningDialog((Component)AccountSettingsPanel.this.getRootPanel(), JAPMessages.getString(PaymentMainPanel.MSG_MONTHLY_RATE_USED), JAPMessages.getString(PaymentMainPanel.MSG_MONTHLY_RATE_USED_TITLE));
                            }
                        }
                    } else if (object == AccountSettingsPanel.this.m_labelVolumeWarning) {
                        PayAccount payAccount3 = AccountSettingsPanel.this.getSelectedAccount();
                        if (payAccount3 != null) {
                            if (AccountSettingsPanel.this.m_labelVolumeWarning.getToolTipText() != null && AccountSettingsPanel.this.m_labelVolumeWarning.getToolTipText().equals(JAPMessages.getString(PaymentMainPanel.MSG_ACCOUNT_BLOCKED_TOOLTIP))) {
                                PaymentMainPanel.showAccountBlockedDialog(payAccount3, AccountSettingsPanel.this.getRootPanel());
                            } else {
                                String string = "";
                                if (payAccount3.getBI() != null) {
                                    string = payAccount3.getBI().getName();
                                }
                                JAPDialog.LinkedInformation linkedInformation = new JAPDialog.LinkedInformation("payment@jondos.de");
                                JAPDialog.showWarningDialog((Component)AccountSettingsPanel.this.getRootPanel(), JAPMessages.getString(MSG_BILLING_ERROR_EXPLAIN, new String[]{string, "" + Util.formatBytesValueWithUnit(payAccount3.getCurrentCreditCalculated() - payAccount3.getCurrentCreditFromBalance(), 2), "" + payAccount3.getAccountNumber()}), (JAPDialog.ILinkedInformation)linkedInformation);
                            }
                        }
                    } else if (object == AccountSettingsPanel.this.m_labelTermsAndConditions && (payAccount = AccountSettingsPanel.this.getSelectedAccount()) != null) {
                        AccountSettingsPanel.this.showTermsAndConditions(payAccount);
                    }
                    MyActionListener.this.m_bButtonClicked = false;
                }
            });
            if (!this.m_bButtonClicked) {
                this.m_bButtonClicked = true;
                thread.start();
            }
        }
    }
}

