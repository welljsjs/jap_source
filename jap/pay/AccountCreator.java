/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import HTTPClient.ForbiddenIOException;
import anon.client.TrustModel;
import anon.crypto.AsymmetricCryptoKeyPair;
import anon.infoservice.Database;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.BooleanVariable;
import anon.util.IProgressCapsule;
import anon.util.IReturnRunnable;
import anon.util.JAPMessages;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.WorkerContentPane;
import jap.JAPController;
import jap.JAPModel;
import jap.gui.LinkRegistrator;
import jap.pay.PaymentInstancePanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.LogHolder;
import logging.LogType;

public class AccountCreator {
    private static final String MSG_COUPON_INCOMPLETE = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".couponIncomplete";
    private static final String MSG_COUPON_NULL = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".couponNull";
    private static final String MSG_ACCOUNT_OVERVIEW = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".accountOverview";
    private static final String MSG_LOOKING_FOR_PAYMENT_INSTANCES = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".paneLookingForPIs";
    private static final String MSG_PAYMENT_SELECTION = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".paymentSelection";
    private static final String MSG_ALLOW_NON_ANONYMOUS_INFOSERVICE = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".allowNonAnonymousInfoService";
    public static final String MSG_WAITING_FOR_TRANSACTION = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".waitingForTransaction";
    private static final String MSG_TRANSACTION_FINISHED = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".finishedTransaction";
    private static final String MSG_TRANSACTION_FINISHED_TEST = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".finishedTransactionTest";
    private static final String MSG_COUPON_REDEEMED = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".redeemedCoupon";
    private static final String MSG_WAITING_FOR_PAYMENT = (class$jap$pay$AccountCreator == null ? (class$jap$pay$AccountCreator = AccountCreator.class$("jap.pay.AccountCreator")) : class$jap$pay$AccountCreator).getName() + ".waitingForPayment";
    public static final int PAYMENT_OPTIONAL = 0;
    public static final int PAYMENT_RECOMMENDED = 1;
    public static final int PAYMENT_FORCED = 2;
    static /* synthetic */ Class class$jap$pay$AccountCreator;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;
    static /* synthetic */ Class class$jap$pay$AccountCreator$RunAccountCreator;

    private AccountCreator() {
    }

    public static Vector getCurrentPaymentInstances() {
        PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountCreator.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(JAPController.getInstance().getCurrentMixCascade().getPIID());
        Vector vector = PayAccountsFile.getInstance().getPaymentInstances(paymentInstanceDBEntry == null || !paymentInstanceDBEntry.isTest());
        return vector;
    }

    private static boolean isPaymentRecommended() {
        return true;
    }

    public static boolean checkValidAccount() {
        return AccountCreator.checkValidAccount(null);
    }

    public static boolean checkValidAccount(String string) {
        if (string != null) {
            return PayAccountsFile.getInstance().getChargedAccount(string) != null;
        }
        Vector vector = AccountCreator.getCurrentPaymentInstances();
        boolean bl = false;
        String string2 = JAPController.getInstance().getCurrentMixCascade().getPIID();
        if (string2 != null && string2.trim().length() == 0) {
            string2 = null;
        }
        for (int i = 0; i < vector.size(); ++i) {
            PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)vector.elementAt(i);
            if ((string2 != null || paymentInstanceDBEntry.isTest()) && (string2 == null || !string2.equals(paymentInstanceDBEntry.getId())) || PayAccountsFile.getInstance().getChargedAccount(paymentInstanceDBEntry.getId()) == null) continue;
            bl = true;
            break;
        }
        return bl;
    }

    public static DialogContentPane createAccountPanes(JAPDialog jAPDialog, DialogContentPane.Layout layout, DialogContentPane dialogContentPane, final String string, final LinkRegistrator linkRegistrator, final IProgressCapsule iProgressCapsule, final int n, final BooleanVariable booleanVariable) {
        Object object;
        final Hashtable hashtable = new Hashtable();
        final Vector vector = new Vector();
        final RunAccountCreator runAccountCreator = new RunAccountCreator(hashtable, vector);
        Runnable runnable = new Runnable(){

            public void run() {
                Thread thread = new Thread(new Runnable(){

                    public void run() {
                        JAPController.getInstance().updatePaymentInstances(false);
                    }
                });
                thread.start();
                try {
                    thread.join(25000L);
                }
                catch (InterruptedException interruptedException) {
                    LogHolder.log(3, LogType.GUI, interruptedException);
                }
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_LOOKING_FOR_PAYMENT_INSTANCES), layout, dialogContentPane, runnable){

            public boolean isSkippedAsPreviousContentPane() {
                return n != 2 || this.isSkipped();
            }

            private boolean isSkipped() {
                return n == 0 && !AccountCreator.isPaymentRecommended() || Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountCreator.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getNumberOfEntries() > 0;
            }

            public boolean isSkippedAsNextContentPane() {
                return this.isSkipped();
            }
        };
        workerContentPane.setDefaultButtonOperation(8 | (n == 2 ? 32768 : 32) | 0x100);
        final IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            private String strError;

            public void run() {
                this.strError = null;
                if (vector.size() > 0) {
                    return;
                }
                AsymmetricCryptoKeyPair asymmetricCryptoKeyPair = PayAccountsFile.getInstance().createAccountKeyPair();
                if (asymmetricCryptoKeyPair != null) {
                    vector.addElement(asymmetricCryptoKeyPair);
                } else {
                    this.strError = JAPMessages.getString(PayAccountsFile.MSG_CREATING_KEY_PAIR_ERROR);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public Object getValue() {
                Vector vector2 = vector;
                synchronized (vector2) {
                    if (vector.size() > 0) {
                        return vector.elementAt(0);
                    }
                    return this.strError;
                }
            }
        };
        SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(jAPDialog, JAPMessages.getString(PayAccountsFile.MSG_NO_PAYMENT_INSTANCE), layout, workerContentPane){
            private JCheckBox m_jbInfoService;

            public DialogContentPane.CheckError checkUpdate() {
                JAPController.getInstance().forceAnonymityTestRedirect(false);
                String string = "<b><font color=\"red\">" + JAPMessages.getString(PayAccountsFile.MSG_NO_PAYMENT_INSTANCE) + "</font></b>";
                this.getContentPane().removeAll();
                if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1 && !JAPController.getInstance().isAnonConnected()) {
                    string = string + "<br/><br/>" + JAPMessages.getString(PayAccountsFile.MSG_ERROR_ALLOW_NON_ANONYMOUS_OR_CONNECT_INFOSERVICE);
                    this.m_jbInfoService = new JCheckBox(JAPMessages.getString(MSG_ALLOW_NON_ANONYMOUS_INFOSERVICE));
                    this.getContentPane().add(this.m_jbInfoService);
                } else {
                    this.m_jbInfoService = null;
                }
                this.setText(string);
                return null;
            }

            public DialogContentPane.CheckError checkNo() {
                return this.checkYesOK();
            }

            public DialogContentPane.CheckError checkYesOK() {
                if (this.m_jbInfoService != null && this.m_jbInfoService.isSelected()) {
                    JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                }
                return null;
            }

            private boolean isSkipped() {
                return n != 2 || Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountCreator.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getNumberOfEntries() > 0;
            }

            public boolean isSkippedAsNextContentPane() {
                return this.isSkipped();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return this.isSkipped();
            }

            public boolean isMoveBackAllowed() {
                return n != 2;
            }
        };
        simpleWizardContentPane.setDefaultButtonOperation(0x80 | (n == 2 ? 32768 : 65536) | 0x100);
        final SimpleWizardContentPane simpleWizardContentPane2 = new SimpleWizardContentPane(jAPDialog, (string != null ? string + " " : "") + JAPMessages.getString(MSG_PAYMENT_SELECTION), layout, new DialogContentPaneOptions("premium", (DialogContentPane)simpleWizardContentPane)){

            public boolean isSkippedAsNextContentPane() {
                if (n == 2) {
                    Vector vector = AccountCreator.getPaymentInstances(n);
                    for (int i = 0; i < vector.size(); ++i) {
                        if (((PaymentInstanceDBEntry)vector.elementAt(i)).getWebshopURL() != null) continue;
                        return true;
                    }
                } else if (n == 1 && iProgressCapsule.getStatus() == 0) {
                    return true;
                }
                return false;
            }

            public boolean isSkippedAsPreviousContentPane() {
                boolean bl = false;
                Enumeration enumeration = hashtable.elements();
                while (enumeration.hasMoreElements()) {
                    if (((PaymentInstancePanel)enumeration.nextElement()).getCode() == null) continue;
                    bl = true;
                    break;
                }
                return this.isSkippedAsNextContentPane() || n == 1 && !bl || n == 2 && Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountCreator.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getNumberOfEntries() == 0;
            }

            public DialogContentPane.CheckError checkUpdate() {
                return AccountCreator.showContents(this, iProgressCapsule, n, hashtable, string, linkRegistrator, false);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public DialogContentPane.CheckError checkYesOK() {
                DialogContentPane.CheckError checkError = null;
                boolean bl = false;
                if (hashtable.size() == 0) {
                    return null;
                }
                Enumeration enumeration = hashtable.elements();
                while (enumeration.hasMoreElements()) {
                    PaymentInstancePanel paymentInstancePanel = (PaymentInstancePanel)enumeration.nextElement();
                    if (bl) {
                        paymentInstancePanel.clearCode();
                        continue;
                    }
                    if (!paymentInstancePanel.isComplete()) {
                        checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_COUPON_INCOMPLETE));
                    } else if (n == 2 && paymentInstancePanel.getCode() == null) {
                        checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_COUPON_NULL));
                    }
                    if (paymentInstancePanel.getCode() == null) continue;
                    bl = true;
                }
                if (checkError == null) {
                    BooleanVariable booleanVariable2 = booleanVariable;
                    synchronized (booleanVariable2) {
                        booleanVariable.set(true);
                    }
                }
                this.setDefaultButtonOperation((n == 1 && !bl ? 128 : 8) | (n == 2 ? 32768 : 65536) | 0x100);
                return checkError;
            }
        };
        simpleWizardContentPane2.setDefaultButtonOperation(8 | (n == 2 ? 32768 : 65536) | 0x100);
        AccountCreator.showContents(simpleWizardContentPane2, iProgressCapsule, n, hashtable, string, linkRegistrator, true);
        WorkerContentPane workerContentPane2 = new WorkerContentPane(jAPDialog, JAPMessages.getString(PayAccountsFile.MSG_CREATING_KEY_PAIR), layout, simpleWizardContentPane2, iReturnRunnable){

            public DialogContentPane.CheckError checkUpdate() {
                JAPController.getInstance().blockDirectProxy(true);
                return super.checkUpdate();
            }

            public boolean isSkippedAsNextContentPane() {
                if (simpleWizardContentPane2.isSkippedAsNextContentPane()) {
                    return true;
                }
                if (vector.size() > 0) {
                    return true;
                }
                Enumeration enumeration = hashtable.elements();
                while (enumeration.hasMoreElements()) {
                    PaymentInstancePanel paymentInstancePanel = (PaymentInstancePanel)enumeration.nextElement();
                    if (!paymentInstancePanel.isComplete() || paymentInstancePanel.getCode() == null) continue;
                    return false;
                }
                return true;
            }

            public boolean hideButtonCancel() {
                return true;
            }
        };
        workerContentPane2.setDefaultButtonOperation(8 | (n == 2 ? 32768 : 512) | 0x100);
        final WorkerContentPane workerContentPane3 = new WorkerContentPane(jAPDialog, JAPMessages.getString(PayAccountsFile.MSG_VERIFYING_COUPON), layout, workerContentPane2, runAccountCreator, runAccountCreator){

            public boolean isSkippedAsNextContentPane() {
                if (simpleWizardContentPane2.isSkippedAsNextContentPane()) {
                    return true;
                }
                if (iReturnRunnable.getValue() == null) {
                    return true;
                }
                Enumeration enumeration = hashtable.elements();
                while (enumeration.hasMoreElements()) {
                    PaymentInstancePanel paymentInstancePanel = (PaymentInstancePanel)enumeration.nextElement();
                    if (!paymentInstancePanel.isComplete() || paymentInstancePanel.getCode() == null) continue;
                    return false;
                }
                return true;
            }

            public DialogContentPane.CheckError checkUpdate() {
                if (iReturnRunnable.getValue() != null && iReturnRunnable.getValue() instanceof String) {
                    return new DialogContentPane.CheckError((String)iReturnRunnable.getValue());
                }
                JAPController.getInstance().blockDirectProxy(true);
                return super.checkUpdate();
            }

            public Object getValue() {
                return runAccountCreator.getError();
            }

            public boolean isMoveBackAllowed() {
                return n != 2;
            }
        };
        workerContentPane3.setDefaultButtonOperation(8 | (n == 2 ? 32768 : 512) | 0x100);
        SimpleWizardContentPane simpleWizardContentPane3 = new SimpleWizardContentPane(jAPDialog, "Test", layout, new DialogContentPaneOptions("premium", (DialogContentPane)workerContentPane3)){

            public boolean hideButtonCancel() {
                return !this.hasNextContentPane();
            }

            public DialogContentPane.CheckError checkUpdate() {
                if (workerContentPane3.getValue() != null && workerContentPane3.getValue() instanceof String) {
                    String string = workerContentPane3.getValue().toString();
                    runAccountCreator.reset();
                    return new DialogContentPane.CheckError(string);
                }
                String string = "<font color =\"green\"><b>" + (runAccountCreator.getCreatedAccount().isWaitingForTransaction() ? JAPMessages.getString(MSG_WAITING_FOR_PAYMENT) : JAPMessages.getString(MSG_COUPON_REDEEMED)) + "</b></font><br/><br/>";
                PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = AccountCreator.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(runAccountCreator.getCreatedAccount().getPIID());
                string = runAccountCreator.getCreatedAccount().isWaitingForTransaction() ? string + JAPMessages.getString(MSG_WAITING_FOR_TRANSACTION) : (paymentInstanceDBEntry == null || !paymentInstanceDBEntry.isTest() ? string + JAPMessages.getString(MSG_TRANSACTION_FINISHED) : string + JAPMessages.getString(MSG_TRANSACTION_FINISHED_TEST));
                this.setText(string);
                runAccountCreator.reset();
                return null;
            }

            public boolean isSkippedAsNextContentPane() {
                if (simpleWizardContentPane2.isSkippedAsNextContentPane()) {
                    return true;
                }
                return n != 2 && runAccountCreator.getCreatedAccount() == null && workerContentPane3.getValue() == null;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        simpleWizardContentPane3.setDefaultButtonOperation((n == 2 ? 8192 : (n == 1 ? 128 : 8)) | (n == 2 ? 32768 : 65536) | 0x100);
        JPanel jPanel = new JPanel();
        simpleWizardContentPane3.getContentPane().add(jPanel);
        if (n != 2) {
            jPanel.setLayout(new GridBagLayout());
            object = new GridBagConstraints();
            ((GridBagConstraints)object).gridx = 0;
            ((GridBagConstraints)object).gridy = -1;
            ((GridBagConstraints)object).anchor = 17;
            linkRegistrator.addBrowserInstallationInfo(jPanel, (GridBagConstraints)object, JAPMessages.getString(MSG_ACCOUNT_OVERVIEW) + "...", "CONF_PAYMENT", false, 2);
        }
        object = new SimpleWizardContentPane(jAPDialog, "Test", layout, new DialogContentPaneOptions("payment", (DialogContentPane)simpleWizardContentPane3)){

            public boolean isMoveForwardAllowed() {
                return this.getNextContentPane() != null && (n != 2 || simpleWizardContentPane2.isSkippedAsNextContentPane());
            }

            public boolean isSkippedAsNextContentPane() {
                return n != 2 || simpleWizardContentPane2.isSkippedAsNextContentPane();
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        ((DialogContentPane)object).setDefaultButton(n == 1 ? 128 : 8 | (n == 2 ? 32768 : 65536) | 0x100);
        jAPDialog.addWindowListener(new WindowAdapter(){

            public void windowClosed(WindowEvent windowEvent) {
                runAccountCreator.deleteUnusedAccounts();
            }
        });
        return object;
    }

    private static DialogContentPane.CheckError showContents(DialogContentPane dialogContentPane, IProgressCapsule iProgressCapsule, int n, Hashtable hashtable, String string, LinkRegistrator linkRegistrator, boolean bl) {
        int n2;
        PaymentInstanceDBEntry paymentInstanceDBEntry;
        Vector<PaymentInstanceDBEntry> vector;
        JAPController.getInstance().blockDirectProxy(false);
        if (iProgressCapsule != null) {
            iProgressCapsule.reset();
        }
        dialogContentPane.getContentPane().removeAll();
        JPanel jPanel = new JPanel();
        dialogContentPane.getContentPane().add(jPanel);
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = -1;
        gridBagConstraints.anchor = 17;
        if (bl) {
            vector = new Vector<PaymentInstanceDBEntry>();
            paymentInstanceDBEntry = new PaymentInstanceDBEntry("ID", "Dummy", null, new Vector().elements(), "0.0", 0L, 0L, null, null, null, null, null, null, null);
            vector.addElement(paymentInstanceDBEntry);
            paymentInstanceDBEntry = new PaymentInstanceDBEntry("ID2", "Dummy2", null, new Vector().elements(), "0.0", 0L, 0L, null, null, null, null, null, null, null);
            vector.addElement(paymentInstanceDBEntry);
        } else {
            vector = AccountCreator.getPaymentInstances(n);
        }
        hashtable.clear();
        for (int i = 0; i < vector.size(); ++i) {
            paymentInstanceDBEntry = (PaymentInstanceDBEntry)vector.elementAt(i);
            if (hashtable.containsKey(paymentInstanceDBEntry.getId())) continue;
            hashtable.put(paymentInstanceDBEntry.getId(), new PaymentInstancePanel(paymentInstanceDBEntry, linkRegistrator));
        }
        Enumeration enumeration = hashtable.elements();
        String string2 = string;
        if (string2 == null) {
            string2 = "";
        }
        if (hashtable.size() == 0) {
            dialogContentPane.setText(string2);
            ++gridBagConstraints.gridy;
            jPanel.add((Component)new JLabel(" "), gridBagConstraints);
        } else {
            dialogContentPane.setText(string2 + " " + JAPMessages.getString(MSG_PAYMENT_SELECTION));
        }
        boolean bl2 = false;
        for (n2 = 0; n2 < vector.size(); ++n2) {
            if (PayAccountsFile.getInstance().getChargedAccount(((PaymentInstanceDBEntry)vector.elementAt(n2)).getId()) == null) continue;
            bl2 = true;
            break;
        }
        while (enumeration.hasMoreElements()) {
            PaymentInstancePanel paymentInstancePanel = (PaymentInstancePanel)enumeration.nextElement();
            paymentInstancePanel.setHeadlineVisible(hashtable.size() > 1);
            ++gridBagConstraints.gridy;
            jPanel.add((Component)paymentInstancePanel, gridBagConstraints);
            if (!enumeration.hasMoreElements()) continue;
            ++gridBagConstraints.gridy;
            jPanel.add((Component)new JLabel(" "), gridBagConstraints);
        }
        if (bl) {
            for (n2 = 0; n2 < vector.size(); ++n2) {
                hashtable.remove(((PaymentInstanceDBEntry)vector.elementAt(n2)).getId());
            }
        }
        if (n == 1 && hashtable.size() == 0) {
            return new DialogContentPane.CheckError(JAPMessages.getString(PayAccountsFile.MSG_NO_PAYMENT_INSTANCE));
        }
        return null;
    }

    private static Vector getPaymentInstances(int n) {
        if (n == 0 && !AccountCreator.isPaymentRecommended()) {
            return new Vector();
        }
        return AccountCreator.getCurrentPaymentInstances();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private static class RunAccountCreator
    extends Observable
    implements Runnable,
    IProgressCapsule {
        private static final String MSG_ERROR_TIMED_OUT = (class$jap$pay$AccountCreator$RunAccountCreator == null ? (class$jap$pay$AccountCreator$RunAccountCreator = AccountCreator.class$("jap.pay.AccountCreator$RunAccountCreator")) : class$jap$pay$AccountCreator$RunAccountCreator).getName() + ".errorTimedOut";
        private static final String MSG_ERROR_INTERRUPTED = (class$jap$pay$AccountCreator$RunAccountCreator == null ? (class$jap$pay$AccountCreator$RunAccountCreator = AccountCreator.class$("jap.pay.AccountCreator$RunAccountCreator")) : class$jap$pay$AccountCreator$RunAccountCreator).getName() + ".errorInterrupted";
        private static final int MAX_SECONDS = 120;
        private Hashtable m_hashPayAccounts = new Hashtable();
        private String m_strError;
        private Vector m_vecKeys;
        private Hashtable m_hashPIs;
        private PayAccount m_createdAccount;
        private long m_startTime;
        private int m_status;
        private String m_strMessage;

        public RunAccountCreator(Hashtable hashtable, Vector vector) {
            this.m_vecKeys = vector;
            this.m_hashPIs = hashtable;
            this.m_status = -1;
        }

        protected void finalize() {
            this.deleteUnusedAccounts();
        }

        public void reset() {
            this.m_strError = null;
            this.m_status = -1;
            this.m_createdAccount = null;
        }

        public PayAccount getCreatedAccount() {
            return this.m_createdAccount;
        }

        public void deleteUnusedAccounts() {
            if (this.m_hashPayAccounts == null) {
                return;
            }
            Enumeration enumeration = this.m_hashPayAccounts.elements();
            while (enumeration.hasMoreElements()) {
                PayAccount payAccount = (PayAccount)enumeration.nextElement();
                payAccount.unlock();
                if (payAccount.getTransaction() != null) continue;
                PayAccountsFile.getInstance().deleteAccount(payAccount);
            }
        }

        public int getMaximum() {
            return 120;
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

        public String getMessage() {
            String string = this.m_strMessage;
            if (string != null) {
                return JAPMessages.getString(string) + "...";
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void showMessage(String string) {
            this.m_strMessage = string;
            RunAccountCreator runAccountCreator = this;
            synchronized (runAccountCreator) {
                this.setChanged();
                this.notifyObservers(this);
            }
        }

        public String getError() {
            String string = this.m_strError;
            if (string != null) {
                string = JAPMessages.getString(string);
            }
            return string;
        }

        private void checkDirectContact() throws DirectContactException {
            if (!JAPController.getInstance().isAnonConnected() && JAPModel.getInstance().getPaymentAnonymousConnectionSetting() == 1) {
                throw new DirectContactException();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void runInternal() throws DirectContactException {
            Enumeration enumeration = this.m_hashPIs.elements();
            PaymentInstancePanel paymentInstancePanel = null;
            this.m_createdAccount = null;
            this.m_status = 1;
            this.showMessage(PayAccountsFile.MSG_VERIFYING_COUPON);
            while (enumeration.hasMoreElements() && (!(paymentInstancePanel = (PaymentInstancePanel)enumeration.nextElement()).isComplete() || paymentInstancePanel.getCode() == null)) {
                paymentInstancePanel = null;
            }
            if (paymentInstancePanel == null) {
                return;
            }
            PayAccount payAccount = (PayAccount)this.m_hashPayAccounts.get(paymentInstancePanel.getPaymentInstance().getId());
            if (payAccount != null && payAccount.isCharged(new Timestamp(System.currentTimeMillis()))) {
                this.m_hashPayAccounts.remove(paymentInstancePanel.getPaymentInstance().getId());
                payAccount = null;
            }
            if (payAccount != null) {
                this.checkDirectContact();
                this.showMessage(PayAccountsFile.MSG_UPDATING_ACCOUNT_DATA);
                try {
                    payAccount.fetchAccountInfo(true);
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                    this.checkDirectContact();
                    this.m_strError = PayAccountsFile.MSG_UPDATING_ACCOUNT_DATA_ERROR;
                    return;
                }
                if (payAccount.isCharged(new Timestamp(System.currentTimeMillis()))) {
                    this.m_hashPayAccounts.remove(paymentInstancePanel.getPaymentInstance().getId());
                    payAccount = null;
                }
            }
            if (payAccount == null) {
                AsymmetricCryptoKeyPair asymmetricCryptoKeyPair = null;
                Vector vector = this.m_vecKeys;
                synchronized (vector) {
                    if (this.m_vecKeys.size() == 0) {
                        this.m_strError = PayAccountsFile.MSG_CREATING_KEY_PAIR_ERROR;
                    } else {
                        asymmetricCryptoKeyPair = (AsymmetricCryptoKeyPair)this.m_vecKeys.elementAt(0);
                    }
                }
                if (asymmetricCryptoKeyPair != null) {
                    this.checkDirectContact();
                    this.showMessage(PayAccountsFile.MSG_CREATING_ACCOUNT);
                    try {
                        try {
                            payAccount = PayAccountsFile.getInstance().createAccount(paymentInstancePanel.getPaymentInstance(), asymmetricCryptoKeyPair, null, true);
                        }
                        catch (ForbiddenIOException forbiddenIOException) {
                            this.m_strError = PayAccountsFile.MSG_CREATING_ACCOUNT_ERROR_FORBIDDEN;
                            throw forbiddenIOException;
                        }
                        catch (IOException iOException) {
                            this.m_strError = PayAccountsFile.MSG_CREATING_ACCOUNT_ERROR_UNREACHABLE;
                            throw iOException;
                        }
                        catch (Exception exception) {
                            this.m_strError = PayAccountsFile.MSG_CREATING_ACCOUNT_ERROR;
                            throw exception;
                        }
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.GUI, exception);
                        this.checkDirectContact();
                        payAccount = null;
                    }
                    if (payAccount != null) {
                        this.m_vecKeys.removeAllElements();
                        this.showMessage(PayAccountsFile.MSG_SAVING_CONFIG_FILE);
                        if (JAPController.getInstance().saveConfigFile()) {
                            this.m_strError = PayAccountsFile.MSG_SAVING_CONFIG_FILE_ERROR;
                            payAccount = null;
                        } else {
                            this.m_hashPayAccounts.put(paymentInstancePanel.getPaymentInstance().getId(), payAccount);
                        }
                    }
                }
            }
            if (payAccount != null) {
                try {
                    this.checkDirectContact();
                    this.showMessage(PayAccountsFile.MSG_ACTIVATING_COUPON);
                    if (PayAccountsFile.getInstance().activateCouponCode(paymentInstancePanel.getCode(), payAccount, true)) {
                        this.checkDirectContact();
                        if (PayAccountsFile.getInstance().activateCouponCode(paymentInstancePanel.getCode(), payAccount, false)) {
                            payAccount.unlock();
                            paymentInstancePanel.clearCode();
                            this.m_hashPayAccounts.remove(payAccount.getBI().getId());
                            this.showMessage(PayAccountsFile.MSG_SAVING_CONFIG_FILE);
                            JAPController.getInstance().saveConfigFile();
                            JAPController.getInstance().setAllowPaidServices(true);
                            if (payAccount.isCharged() && !TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                                JAPController.getInstance().switchToNextMixCascade();
                            }
                            this.m_createdAccount = payAccount;
                            this.m_status = 0;
                        } else {
                            this.checkDirectContact();
                            this.m_strError = PayAccountsFile.MSG_ACTIVATING_COUPON_ERROR;
                        }
                    } else {
                        this.checkDirectContact();
                        this.m_strError = !PayAccountsFile.getInstance().isNewUserAllowed(paymentInstancePanel.getCode()) ? PayAccountsFile.MSG_ACTIVATING_COUPON_NOT_A_NEW_USER : PayAccountsFile.MSG_ACTIVATING_COUPON_NOT_ACCEPTED;
                        this.m_status = 3;
                    }
                }
                catch (DirectContactException directContactException) {
                    this.m_status = 3;
                    throw directContactException;
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                    this.m_status = 3;
                    this.checkDirectContact();
                    this.m_strError = PayAccountsFile.MSG_ACTIVATING_COUPON_ERROR;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            this.m_strError = null;
            this.m_startTime = System.currentTimeMillis();
            this.m_status = -1;
            boolean bl = false;
            Thread thread = new Thread(){

                public void run() {
                    try {
                        RunAccountCreator.this.runInternal();
                    }
                    catch (DirectContactException directContactException) {
                        RunAccountCreator.this.m_strError = PayAccountsFile.MSG_ERROR_ALLOW_NON_ANONYMOUS_OR_CONNECT;
                        RunAccountCreator.this.m_status = 3;
                    }
                }
            };
            thread.start();
            try {
                do {
                    Thread.sleep(250L);
                    RunAccountCreator runAccountCreator = this;
                    synchronized (runAccountCreator) {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        this.setChanged();
                        this.notifyObservers(this);
                    }
                } while (this.getValue() < this.getMaximum() && thread.isAlive());
            }
            catch (InterruptedException interruptedException) {
                bl = true;
                this.m_strError = MSG_ERROR_INTERRUPTED;
                this.m_status = 2;
            }
            while (thread.isAlive()) {
                if (!bl) {
                    this.m_strError = MSG_ERROR_TIMED_OUT;
                    this.m_status = 3;
                }
                thread.interrupt();
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException interruptedException) {}
            }
            if (this.m_strError == null) {
                this.m_status = 0;
            }
        }

        private static class DirectContactException
        extends Exception {
            private DirectContactException() {
            }
        }
    }
}

