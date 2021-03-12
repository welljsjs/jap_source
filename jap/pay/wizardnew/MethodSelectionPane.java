/*
 * Decompiled with CFR 0.150.
 */
package jap.pay.wizardnew;

import anon.pay.xml.XMLPaymentOption;
import anon.pay.xml.XMLPaymentOptions;
import anon.pay.xml.XMLVolumePlan;
import anon.util.JAPMessages;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.JAPUtil;
import jap.pay.wizardnew.VolumePlanSelectionPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class MethodSelectionPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable,
ActionListener {
    private static final int SHOW_MARKUP_IF_ABOVE = 5;
    private static final String MSG_PRICE = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_price";
    private static final String MSG_SELECTOPTION = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_selectoption";
    private static final String MSG_ERRSELECT = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_errselect";
    private static final String MSG_NOTSUPPORTED = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_notsupported";
    private static final String MSG_SELECTED_PLAN = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_selectedplan";
    private static final String MSG_MARKUP = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_markup";
    private static final String MSG_MARKUP_CAPTION = (class$jap$pay$wizardnew$MethodSelectionPane == null ? (class$jap$pay$wizardnew$MethodSelectionPane = MethodSelectionPane.class$("jap.pay.wizardnew.MethodSelectionPane")) : class$jap$pay$wizardnew$MethodSelectionPane).getName() + "_markupcaption";
    private ButtonGroup m_rbGroup;
    private XMLPaymentOptions m_paymentOptions;
    private GridBagConstraints m_c = new GridBagConstraints();
    private XMLPaymentOption m_selectedPaymentOption;
    private Container m_rootPanel;
    XMLPaymentOptions m_options;
    static /* synthetic */ Class class$jap$pay$wizardnew$MethodSelectionPane;

    public MethodSelectionPane(JAPDialog jAPDialog, WorkerContentPane workerContentPane) {
        super(jAPDialog, "", new DialogContentPane.Layout(JAPMessages.getString(MSG_SELECTOPTION), -1), new DialogContentPaneOptions(2, (DialogContentPane)workerContentPane));
        this.setDefaultButtonOperation(266);
        this.m_rootPanel = this.getContentPane();
        this.m_c = new GridBagConstraints();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_rbGroup = new ButtonGroup();
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        for (int i = 0; i < 6; ++i) {
            this.addOption("Dummy", "0");
        }
    }

    private void addOption(String string, String string2) {
        this.m_c.insets = new Insets(0, 5, 0, 5);
        ++this.m_c.gridy;
        JRadioButton jRadioButton = new JRadioButton(new JAPHtmlMultiLineLabel(string).getHTMLDocumentText() + string2);
        jRadioButton.setName(string);
        jRadioButton.addActionListener(this);
        this.m_rbGroup.add(jRadioButton);
        this.m_rootPanel.add((Component)jRadioButton, this.m_c);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof JRadioButton) {
            String string = ((JRadioButton)actionEvent.getSource()).getName();
            this.m_selectedPaymentOption = this.m_paymentOptions.getOption(string, JAPMessages.getLocale().getLanguage());
        }
    }

    public XMLPaymentOption getSelectedPaymentOption() {
        return this.m_selectedPaymentOption;
    }

    public void showPaymentOptions() {
        WorkerContentPane workerContentPane = (WorkerContentPane)this.getPreviousContentPane();
        Object object = workerContentPane.getValue();
        XMLPaymentOptions xMLPaymentOptions = (XMLPaymentOptions)object;
        if (this.m_options != null && this.m_selectedPaymentOption != null && this.m_options == xMLPaymentOptions) {
            return;
        }
        this.m_selectedPaymentOption = null;
        this.m_options = xMLPaymentOptions;
        this.m_rootPanel.removeAll();
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        this.m_paymentOptions = xMLPaymentOptions;
        String string = JAPMessages.getLocale().getLanguage();
        Vector vector = this.m_paymentOptions.getAllOptionsSortedByRank(string);
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            XMLPaymentOption xMLPaymentOption = (XMLPaymentOption)enumeration.nextElement();
            if (!xMLPaymentOption.worksWithJapVersion("00.20.001")) continue;
            String string2 = JAPMessages.getLocale().getLanguage();
            int n = xMLPaymentOption.getMarkup();
            String string3 = "";
            this.addOption(xMLPaymentOption.getHeading(string2), string3);
        }
        DialogContentPane dialogContentPane = workerContentPane;
        while (true) {
            if (dialogContentPane instanceof VolumePlanSelectionPane) break;
            dialogContentPane = dialogContentPane.getPreviousContentPane();
        }
        Object object2 = (VolumePlanSelectionPane)dialogContentPane;
        XMLVolumePlan xMLVolumePlan = ((VolumePlanSelectionPane)object2).getSelectedVolumePlan();
        object2 = xMLVolumePlan.getDisplayName();
        String string4 = JAPUtil.formatEuroCentValue(xMLVolumePlan.getPrice(), true);
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_SELECTED_PLAN));
        JLabel jLabel2 = new JLabel((String)object2 + " (" + string4 + ")");
        this.m_c.insets = new Insets(30, 5, 0, 5);
        ++this.m_c.gridy;
        this.m_rootPanel.add((Component)jLabel, this.m_c);
        this.m_c.insets = new Insets(5, 5, 0, 5);
        ++this.m_c.gridy;
        this.m_rootPanel.add((Component)jLabel2, this.m_c);
    }

    public DialogContentPane.CheckError checkYesOK() {
        boolean bl = false;
        if (this.m_selectedPaymentOption == null) {
            return new DialogContentPane.CheckError(JAPMessages.getString(MSG_ERRSELECT));
        }
        if (!this.m_selectedPaymentOption.isGeneric()) {
            StringTokenizer stringTokenizer = new StringTokenizer("CreditCard", ",");
            while (stringTokenizer.hasMoreTokens()) {
                String string = stringTokenizer.nextToken();
                if (!this.m_selectedPaymentOption.getName().equalsIgnoreCase(string)) continue;
                bl = true;
            }
            if (!bl) {
                return new DialogContentPane.CheckError(JAPMessages.getString(MSG_NOTSUPPORTED));
            }
        }
        return null;
    }

    public DialogContentPane.CheckError checkUpdate() {
        this.showPaymentOptions();
        return null;
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

