/*
 * Decompiled with CFR 0.150.
 */
package jap.pay.wizardnew;

import anon.pay.BIConnection;
import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLVolumePlan;
import anon.pay.xml.XMLVolumePlans;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.GUIUtils;
import gui.JapCouponField;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.JAPController;
import jap.JAPUtil;
import jap.gui.LinkRegistrator;
import jap.pay.AccountSettingsPanel;
import jap.pay.wizardnew.JpiSelectionPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import logging.LogHolder;
import logging.LogType;

public class VolumePlanSelectionPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable,
ActionListener,
DocumentListener {
    private static final String MSG_PRICE = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_price";
    private static final String MSG_HEADING = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_heading";
    private static final String MSG_VOLUME = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_volume";
    private static final String MSG_UNLIMITED = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_unlimited";
    private static final String MSG_ERROR_NO_PLAN_CHOSEN = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_errorNoPlanChosen";
    private static final String MSG_VALIDUNTIL = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_validuntil";
    private static final String MSG_CHOOSEAPLAN = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_chooseaplan";
    private static final String MSG_ENTER_COUPON = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_entercouponcode";
    private static final String MSG_PLAN_OR_COUPON = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_planorcoupon";
    private static final String MSG_INVALID_COUPON = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_invalidcoupon";
    private static final String MSG_COUPON_INCOMPLETE = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + "_couponincomplete";
    private static final String MSG_ONE_TIME_PRICES = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + ".oneTimePrices";
    private static final String MSG_TOTAL_PRICE = (class$jap$pay$wizardnew$VolumePlanSelectionPane == null ? (class$jap$pay$wizardnew$VolumePlanSelectionPane = VolumePlanSelectionPane.class$("jap.pay.wizardnew.VolumePlanSelectionPane")) : class$jap$pay$wizardnew$VolumePlanSelectionPane).getName() + ".totalPrice";
    private XMLVolumePlans m_allPlans;
    private XMLVolumePlan m_selectedPlan;
    private JapCouponField m_coupon1;
    private JapCouponField m_coupon2;
    private JapCouponField m_coupon3;
    private JapCouponField m_coupon4;
    private GridBagConstraints m_c = new GridBagConstraints();
    private Container m_rootPanel;
    private ButtonGroup m_rbGroup;
    private JRadioButton m_couponButton;
    private WorkerContentPane m_fetchPlansPane;
    private boolean m_bNewAccount;
    private boolean m_isCouponUsed;
    private boolean m_hasBeenShown = false;
    static /* synthetic */ Class class$jap$pay$wizardnew$VolumePlanSelectionPane;

    public VolumePlanSelectionPane(JAPDialog jAPDialog, WorkerContentPane workerContentPane, boolean bl) {
        super(jAPDialog, JAPMessages.getString(MSG_ONE_TIME_PRICES), new DialogContentPane.Layout(JAPMessages.getString(MSG_HEADING), -1), new DialogContentPaneOptions(2, (DialogContentPane)workerContentPane));
        this.setDefaultButtonOperation(266);
        this.m_fetchPlansPane = workerContentPane;
        this.m_bNewAccount = bl;
        this.m_rbGroup = new ButtonGroup();
        this.m_rootPanel = this.getContentPane();
        this.m_c = new GridBagConstraints();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        for (int i = 0; i < 10; ++i) {
            XMLVolumePlan xMLVolumePlan = new XMLVolumePlan("dummy", "Dummy        for sizing", 100, 2, "months", 2000000L, 2000000L, true, null, false, true, 0.0);
            this.addPlan(xMLVolumePlan);
        }
    }

    public XMLVolumePlan getSelectedVolumePlan() {
        return this.m_selectedPlan;
    }

    public String getEnteredCouponCode() {
        return this.getCouponString();
    }

    private void setCouponUsed(boolean bl) {
        this.m_isCouponUsed = bl;
    }

    public boolean isCouponUsed() {
        return this.m_isCouponUsed;
    }

    public boolean isCouponComplete() {
        return PayAccount.checkCouponCode(this.getCouponString()) != null;
    }

    public String getAmount() {
        int n = this.m_selectedPlan.getPrice();
        Integer n2 = new Integer(n);
        String string = n2.toString();
        return string;
    }

    public String getCurrency() {
        return new String("EUR");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof JRadioButton) {
            JRadioButton jRadioButton = (JRadioButton)actionEvent.getSource();
            String string = jRadioButton.getName();
            if (string.equals("coupon")) {
                this.m_selectedPlan = null;
                this.setCouponUsed(true);
            } else if (this.m_allPlans != null) {
                this.m_selectedPlan = this.m_allPlans.getVolumePlan(string);
                this.clearCouponFields();
                this.setCouponUsed(false);
            }
        }
    }

    public void insertUpdate(DocumentEvent documentEvent) {
        this.m_selectedPlan = null;
        this.m_couponButton.setSelected(true);
        this.setCouponUsed(true);
    }

    public void removeUpdate(DocumentEvent documentEvent) {
    }

    public void changedUpdate(DocumentEvent documentEvent) {
    }

    private void addPlan(XMLVolumePlan xMLVolumePlan) {
        JLabel jLabel;
        this.m_c.insets = new Insets(0, 5, 0, 5);
        ++this.m_c.gridy;
        String string = xMLVolumePlan.getDisplayName();
        if (string == null || string.equals("")) {
            string = xMLVolumePlan.getName();
        }
        String string2 = xMLVolumePlan.getName();
        this.m_c.gridx = 0;
        JRadioButton jRadioButton = new JRadioButton(string);
        jRadioButton.setName(string2);
        jRadioButton.addActionListener(this);
        this.m_rbGroup.add(jRadioButton);
        this.m_rootPanel.add((Component)jRadioButton, this.m_c);
        ++this.m_c.gridx;
        JLabel jLabel2 = new JLabel(JAPUtil.formatEuroCentValue(xMLVolumePlan.getPrice(), false));
        if (xMLVolumePlan.isMonthlyVolume()) {
            jLabel2.setText(JAPUtil.formatEuroCentValue(xMLVolumePlan.getPrice() / xMLVolumePlan.getDuration(), false) + " / Monat");
        }
        jLabel2.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        this.m_rootPanel.add((Component)jLabel2, this.m_c);
        ++this.m_c.gridx;
        JLabel jLabel3 = xMLVolumePlan.isDurationLimited() ? new JLabel(JAPUtil.getDuration(xMLVolumePlan.getDuration(), xMLVolumePlan.getDurationUnit())) : new JLabel(JAPMessages.getString(MSG_UNLIMITED));
        jLabel3.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        this.m_rootPanel.add((Component)jLabel3, this.m_c);
        ++this.m_c.gridx;
        if (xMLVolumePlan.isVolumeLimited()) {
            jLabel = new JLabel(Util.formatBytesValueWithUnit(xMLVolumePlan.getVolumeKbytes() * 1000L));
            if (xMLVolumePlan.isMonthlyVolume()) {
                jLabel.setText(JAPMessages.getString(AccountSettingsPanel.MSG_MONTHLY_VOLUME, jLabel.getText()));
            }
        } else {
            jLabel = new JLabel(JAPMessages.getString(MSG_UNLIMITED));
        }
        jLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        this.m_rootPanel.add((Component)jLabel, this.m_c);
        ++this.m_c.gridx;
        jLabel2 = new JLabel(JAPUtil.formatEuroCentValue(xMLVolumePlan.getPrice(), true));
        jLabel2.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        this.m_rootPanel.add((Component)jLabel2, this.m_c);
    }

    private void addCouponField() {
        ++this.m_c.gridy;
        this.m_c.insets = new Insets(10, 5, 0, 5);
        this.m_c.gridx = 0;
        this.m_c.gridwidth = 4;
        JPanel jPanel = new JPanel();
        this.m_couponButton = new JRadioButton("");
        this.m_couponButton.setName("coupon");
        this.m_couponButton.addActionListener(this);
        this.m_rbGroup.add(this.m_couponButton);
        jPanel.add(this.m_couponButton);
        jPanel.add(new JLabel(JAPMessages.getString(MSG_ENTER_COUPON)));
        this.m_rootPanel.add((Component)jPanel, this.m_c);
        ++this.m_c.gridy;
        this.m_c.gridx = 0;
        this.m_c.gridwidth = 4;
        if (this.m_allPlans == null || this.m_allPlans.getNrOfPlans() == 0) {
            this.m_couponButton.setSelected(true);
            this.m_couponButton.setVisible(false);
        }
        JPanel jPanel2 = new JPanel();
        this.m_coupon1 = new JapCouponField(true);
        this.m_coupon1.getDocument().addDocumentListener(this);
        jPanel2.add(this.m_coupon1);
        jPanel2.add(new JLabel(" - "));
        this.m_coupon2 = new JapCouponField(false);
        this.m_coupon1.setNextCouponField(this.m_coupon2);
        this.m_coupon2.getDocument().addDocumentListener(this);
        jPanel2.add(this.m_coupon2);
        jPanel2.add(new JLabel(" - "));
        this.m_coupon3 = new JapCouponField(false);
        this.m_coupon2.setNextCouponField(this.m_coupon3);
        this.m_coupon3.getDocument().addDocumentListener(this);
        jPanel2.add(this.m_coupon3);
        jPanel2.add(new JLabel(" - "));
        this.m_coupon4 = new JapCouponField(false);
        this.m_coupon3.setNextCouponField(this.m_coupon4);
        this.m_coupon4.getDocument().addDocumentListener(this);
        jPanel2.add(this.m_coupon4);
        PaymentInstanceDBEntry paymentInstanceDBEntry = this.getPI();
        URL uRL = null;
        if (paymentInstanceDBEntry != null) {
            uRL = paymentInstanceDBEntry.getWebshopURL();
        }
        if (uRL != null) {
            LinkRegistrator linkRegistrator = new LinkRegistrator(this.m_rootPanel, JAPController.getInstance().getView());
            linkRegistrator.addBrowserInstallationInfo(jPanel2, null, JAPMessages.getString(PayAccountsFile.MSG_DO_PREMIUM_PAYMENT), uRL.toString(), false, 1);
        }
        this.m_rootPanel.add((Component)jPanel2, this.m_c);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DialogContentPane.CheckError checkYesOK() {
        DialogContentPane.CheckError checkError = super.checkYesOK();
        if (!this.m_couponButton.isSelected() && this.isCouponUsed()) {
            return new DialogContentPane.CheckError(JAPMessages.getString(MSG_PLAN_OR_COUPON));
        }
        if (this.m_rbGroup.getSelection() == null && !this.isCouponUsed()) {
            return new DialogContentPane.CheckError(JAPMessages.getString(MSG_ERROR_NO_PLAN_CHOSEN));
        }
        if (this.isCouponUsed() && !this.isCouponComplete()) {
            return new DialogContentPane.CheckError(JAPMessages.getString(MSG_COUPON_INCOMPLETE));
        }
        if (this.isCouponUsed() && this.isCouponComplete() && this.m_bNewAccount) {
            PaymentInstanceDBEntry paymentInstanceDBEntry = this.getPI();
            DialogContentPane dialogContentPane = this.m_fetchPlansPane;
            while (!(dialogContentPane instanceof AccountSettingsPanel.AccountCreationPane)) {
                dialogContentPane = dialogContentPane.getPreviousContentPane();
            }
            AccountSettingsPanel.AccountCreationPane accountCreationPane = (AccountSettingsPanel.AccountCreationPane)dialogContentPane;
            PayAccount payAccount = (PayAccount)accountCreationPane.getValue();
            boolean bl = false;
            BIConnection bIConnection = null;
            try {
                try {
                    bIConnection = new BIConnection(paymentInstanceDBEntry);
                    bIConnection.connect();
                    bIConnection.authenticate(payAccount);
                    LogHolder.log(7, LogType.PAY, "Checking coupon code validity in VolumePlanSelectionPane");
                    bl = bIConnection.checkCouponCode(this.getEnteredCouponCode(), payAccount);
                }
                catch (Exception exception) {
                    if (!Thread.currentThread().isInterrupted()) {
                        LogHolder.log(2, LogType.NET, "Error while checking coupon validity: ", exception);
                        Thread.currentThread().interrupt();
                    }
                    Object var10_9 = null;
                    if (bIConnection != null) {
                        bIConnection.disconnect();
                    }
                }
                Object var10_8 = null;
                if (bIConnection != null) {
                    bIConnection.disconnect();
                }
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                if (bIConnection != null) {
                    bIConnection.disconnect();
                }
                throw throwable;
            }
            if (!PayAccountsFile.getInstance().isNewUserAllowed(this.getCouponString())) {
                return new DialogContentPane.CheckError(JAPMessages.getString(PayAccountsFile.MSG_ACTIVATING_COUPON_NOT_A_NEW_USER));
            }
            if (!bl) {
                return new DialogContentPane.CheckError(JAPMessages.getString(MSG_INVALID_COUPON));
            }
        }
        return checkError;
    }

    private PaymentInstanceDBEntry getPI() {
        DialogContentPane dialogContentPane = this.m_fetchPlansPane;
        while (!(dialogContentPane instanceof JpiSelectionPane)) {
            dialogContentPane = dialogContentPane.getPreviousContentPane();
        }
        JpiSelectionPane jpiSelectionPane = (JpiSelectionPane)dialogContentPane;
        PaymentInstanceDBEntry paymentInstanceDBEntry = jpiSelectionPane.getSelectedPaymentInstance();
        return paymentInstanceDBEntry;
    }

    public DialogContentPane.CheckError checkUpdate() {
        if (!this.m_hasBeenShown) {
            this.m_hasBeenShown = true;
            this.showVolumePlans();
        }
        return null;
    }

    public void showVolumePlans() {
        XMLVolumePlans xMLVolumePlans;
        WorkerContentPane workerContentPane = this.m_fetchPlansPane;
        Object object = workerContentPane.getValue();
        this.m_allPlans = xMLVolumePlans = (XMLVolumePlans)object;
        this.m_rootPanel.removeAll();
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        ++this.m_c.gridx;
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_PRICE) + " (Euro)");
        GUIUtils.setFontStyle(jLabel, 1);
        this.m_rootPanel.add((Component)jLabel, this.m_c);
        ++this.m_c.gridx;
        jLabel = new JLabel(JAPMessages.getString(MSG_VALIDUNTIL));
        GUIUtils.setFontStyle(jLabel, 1);
        this.m_rootPanel.add((Component)jLabel, this.m_c);
        ++this.m_c.gridx;
        jLabel = new JLabel(JAPMessages.getString(MSG_VOLUME));
        GUIUtils.setFontStyle(jLabel, 1);
        this.m_rootPanel.add((Component)jLabel, this.m_c);
        ++this.m_c.gridx;
        jLabel = new JLabel(JAPMessages.getString(MSG_TOTAL_PRICE));
        GUIUtils.setFontStyle(jLabel, 1);
        this.m_rootPanel.add((Component)jLabel, this.m_c);
        this.m_rbGroup = new ButtonGroup();
        ++this.m_c.gridy;
        if (this.m_allPlans != null) {
            for (int i = 0; i < this.m_allPlans.getNrOfPlans(); ++i) {
                this.addPlan(this.m_allPlans.getVolumePlan(i));
            }
        }
        this.addCouponField();
    }

    public void resetSelection() {
        this.m_selectedPlan = null;
        this.clearCouponFields();
    }

    private void clearCouponFields() {
        this.m_coupon1.setText("");
        this.m_coupon2.setText("");
        this.m_coupon3.setText("");
        this.m_coupon4.setText("");
    }

    private String getCouponString() {
        return this.m_coupon1.getText() + this.m_coupon2.getText() + this.m_coupon3.getText() + this.m_coupon4.getText();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

