/*
 * Decompiled with CFR 0.150.
 */
package jap.pay.wizardnew;

import anon.pay.PayAccount;
import anon.pay.xml.XMLPassivePayment;
import anon.pay.xml.XMLPaymentOption;
import anon.pay.xml.XMLPaymentOptions;
import anon.pay.xml.XMLTransCert;
import anon.util.Base64;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPJIntField;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.pay.wizardnew.MethodSelectionPane;
import jap.pay.wizardnew.VolumePlanSelectionPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PassivePaymentPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable {
    private static final String MSG_ENTER = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_enter";
    private static final String MSG_ERRALLFIELDS = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_errallfields";
    private static final String MSG_CARDCOMPANY = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_cardcompany";
    private static final String MSG_CARDOWNER = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_cardowner";
    private static final String MSG_CARDVALIDITY = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_cardvalidity";
    private static final String MSG_CARDNUMBER = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_cardnumber";
    private static final String MSG_CARDCHECKNUMBER = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_cardchecknumber";
    public static final String IMG_CREDITCARDSECURITY = (class$jap$pay$wizardnew$PassivePaymentPane == null ? (class$jap$pay$wizardnew$PassivePaymentPane = PassivePaymentPane.class$("jap.pay.wizardnew.PassivePaymentPane")) : class$jap$pay$wizardnew$PassivePaymentPane).getName() + "_creditcardsecurity.gif";
    public static final String[] creditCardDataKeys = new String[]{"creditcardtype", "number", "owner", "valid", "checknumber"};
    private Container m_rootPanel;
    private GridBagConstraints m_c;
    private String m_language;
    private Vector m_inputFields;
    private XMLPaymentOption m_selectedOption;
    private XMLPaymentOptions m_paymentOptions;
    private JComboBox m_cbCompany;
    private JComboBox m_cbMonth;
    private JComboBox m_cbYear;
    private JTextField m_tfCardOwner;
    private JAPJIntField m_tfCardNumber1;
    private JAPJIntField m_tfCardNumber2;
    private JAPJIntField m_tfCardNumber3;
    private JAPJIntField m_tfCardNumber4;
    private JAPJIntField m_tfCardCheckNumber;
    static /* synthetic */ Class class$jap$pay$wizardnew$PassivePaymentPane;

    public PassivePaymentPane(JAPDialog jAPDialog, DialogContentPane dialogContentPane) {
        super(jAPDialog, "Dummy Text<br>Dummy Text<br>DummyText", new DialogContentPane.Layout(JAPMessages.getString(MSG_ENTER), -1), new DialogContentPaneOptions(2, dialogContentPane));
        this.setDefaultButtonOperation(266);
        this.m_language = JAPMessages.getLocale().getLanguage();
        this.m_rootPanel = this.getContentPane();
        this.m_c = new GridBagConstraints();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
    }

    public void showGenericForm() {
        this.m_rootPanel.removeAll();
        this.m_rootPanel = this.getContentPane();
        this.m_c = new GridBagConstraints();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        this.m_c.gridwidth = 1;
        this.setText(this.m_selectedOption.getDetailedInfo(this.m_language));
        JTextField jTextField = null;
        JComboBox<String> jComboBox = null;
        this.m_inputFields = new Vector();
        Vector vector = this.m_selectedOption.getInputFields();
        for (int i = 0; i < vector.size(); ++i) {
            String[] arrstring = (String[])vector.elementAt(i);
            if (!arrstring[2].equalsIgnoreCase(this.m_language)) continue;
            JLabel jLabel = new JLabel("<html>" + arrstring[1] + "</html>");
            if (arrstring[0].equalsIgnoreCase("creditcardtype")) {
                String string = this.m_paymentOptions.getAcceptedCreditCards();
                StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
                jComboBox = new JComboBox<String>();
                jComboBox.setName(arrstring[0]);
                while (stringTokenizer.hasMoreTokens()) {
                    jComboBox.addItem(stringTokenizer.nextToken());
                }
                this.m_inputFields.addElement(jComboBox);
                this.m_rootPanel.add((Component)jLabel, this.m_c);
                ++this.m_c.gridx;
                this.m_rootPanel.add(jComboBox, this.m_c);
            } else {
                jTextField = new JTextField(15);
                jTextField.setName(arrstring[0]);
                this.m_inputFields.addElement(jTextField);
                this.m_rootPanel.add((Component)jLabel, this.m_c);
                ++this.m_c.gridx;
                this.m_rootPanel.add((Component)jTextField, this.m_c);
            }
            ++this.m_c.gridy;
            this.m_c.gridx = 0;
        }
    }

    public XMLPassivePayment getEnteredInfo(PayAccount payAccount) {
        if (this.m_selectedOption.isGeneric()) {
            return this.getEnteredGenericInfo(payAccount);
        }
        if (this.m_selectedOption.getName().equalsIgnoreCase("creditcard")) {
            return this.getEnteredCreditCardInfo(payAccount);
        }
        return null;
    }

    private XMLPassivePayment getEnteredCreditCardInfo(PayAccount payAccount) {
        Object object;
        DialogContentPane dialogContentPane;
        XMLTransCert xMLTransCert = null;
        DialogContentPane dialogContentPane2 = this.getPreviousContentPane();
        while (xMLTransCert == null) {
            if (dialogContentPane2 instanceof WorkerContentPane) {
                dialogContentPane = (WorkerContentPane)dialogContentPane2;
                object = ((WorkerContentPane)dialogContentPane).getValue();
                if (object instanceof XMLTransCert) {
                    xMLTransCert = (XMLTransCert)object;
                    continue;
                }
                dialogContentPane2 = dialogContentPane2.getPreviousContentPane();
                continue;
            }
            dialogContentPane2 = dialogContentPane2.getPreviousContentPane();
        }
        dialogContentPane2 = this.getPreviousContentPane();
        while (!(dialogContentPane2 instanceof VolumePlanSelectionPane)) {
            dialogContentPane2 = dialogContentPane2.getPreviousContentPane();
        }
        dialogContentPane = (VolumePlanSelectionPane)dialogContentPane2;
        object = ((VolumePlanSelectionPane)dialogContentPane).getAmount();
        String string = ((VolumePlanSelectionPane)dialogContentPane).getCurrency();
        XMLPassivePayment xMLPassivePayment = new XMLPassivePayment(payAccount.getPIID());
        xMLPassivePayment.setTransferNumber(xMLTransCert.getTransferNumber());
        xMLPassivePayment.setAmount(Long.parseLong((String)object));
        xMLPassivePayment.setCurrency(string);
        xMLPassivePayment.setPaymentName(this.m_selectedOption.getName());
        xMLPassivePayment.addData("creditcardtype", (String)this.m_cbCompany.getSelectedItem());
        xMLPassivePayment.addData("number", this.m_tfCardNumber1.getText() + this.m_tfCardNumber2.getText() + this.m_tfCardNumber3.getText() + this.m_tfCardNumber4.getText());
        xMLPassivePayment.addData("owner", Base64.encode(this.m_tfCardOwner.getText().getBytes(), false));
        xMLPassivePayment.addData("valid", (String)this.m_cbMonth.getSelectedItem() + "/" + (String)this.m_cbYear.getSelectedItem());
        xMLPassivePayment.addData("checknumber", this.m_tfCardCheckNumber.getText());
        return xMLPassivePayment;
    }

    public XMLPassivePayment getEnteredGenericInfo(PayAccount payAccount) {
        Object object;
        DialogContentPane dialogContentPane;
        XMLTransCert xMLTransCert = null;
        DialogContentPane dialogContentPane2 = this.getPreviousContentPane();
        while (xMLTransCert == null) {
            if (dialogContentPane2 instanceof WorkerContentPane) {
                dialogContentPane = (WorkerContentPane)dialogContentPane2;
                object = ((WorkerContentPane)dialogContentPane).getValue();
                if (object instanceof XMLTransCert) {
                    xMLTransCert = (XMLTransCert)object;
                    continue;
                }
                dialogContentPane2 = dialogContentPane2.getPreviousContentPane();
                continue;
            }
            dialogContentPane2 = dialogContentPane2.getPreviousContentPane();
        }
        dialogContentPane2 = this.getPreviousContentPane();
        while (!(dialogContentPane2 instanceof VolumePlanSelectionPane)) {
            dialogContentPane2 = dialogContentPane2.getPreviousContentPane();
        }
        dialogContentPane = (VolumePlanSelectionPane)dialogContentPane2;
        object = ((VolumePlanSelectionPane)dialogContentPane).getAmount();
        String string = ((VolumePlanSelectionPane)dialogContentPane).getCurrency();
        XMLPassivePayment xMLPassivePayment = new XMLPassivePayment(payAccount.getPIID());
        xMLPassivePayment.setTransferNumber(xMLTransCert.getTransferNumber());
        xMLPassivePayment.setAmount(Long.parseLong((String)object));
        xMLPassivePayment.setCurrency(string);
        xMLPassivePayment.setPaymentName(this.m_selectedOption.getName());
        Enumeration enumeration = this.m_inputFields.elements();
        while (enumeration.hasMoreElements()) {
            Component component = (Component)enumeration.nextElement();
            if (component instanceof JTextField) {
                JTextField jTextField = (JTextField)component;
                String string2 = jTextField.getName();
                String string3 = jTextField.getText();
                xMLPassivePayment.addData(string2, string3);
                continue;
            }
            if (!(component instanceof JComboBox)) continue;
            xMLPassivePayment.addData(((JComboBox)component).getName(), (String)((JComboBox)component).getSelectedItem());
        }
        return xMLPassivePayment;
    }

    public DialogContentPane.CheckError checkYesOK() {
        if (this.m_selectedOption.getType().equals("passive")) {
            if (this.m_selectedOption.isGeneric()) {
                Enumeration enumeration = this.m_inputFields.elements();
                while (enumeration.hasMoreElements()) {
                    JTextField jTextField;
                    Component component = (Component)enumeration.nextElement();
                    if (!(component instanceof JTextField) || (jTextField = (JTextField)component).getText() != null && !jTextField.getText().trim().equals("")) continue;
                    return new DialogContentPane.CheckError(JAPMessages.getString(MSG_ERRALLFIELDS));
                }
            } else {
                boolean bl = true;
                if (this.m_tfCardCheckNumber.getText() == null || this.m_tfCardCheckNumber.getText().trim().equals("")) {
                    bl = false;
                }
                if (this.m_tfCardNumber1.getText() == null || this.m_tfCardNumber1.getText().trim().equals("")) {
                    bl = false;
                }
                if (this.m_tfCardNumber2.getText() == null || this.m_tfCardNumber2.getText().trim().equals("")) {
                    bl = false;
                }
                if (this.m_tfCardNumber3.getText() == null || this.m_tfCardNumber3.getText().trim().equals("")) {
                    bl = false;
                }
                if (this.m_tfCardNumber4.getText() == null || this.m_tfCardNumber4.getText().trim().equals("")) {
                    bl = false;
                }
                if (this.m_tfCardOwner.getText() == null || this.m_tfCardOwner.getText().trim().equals("")) {
                    bl = false;
                }
                if (!bl) {
                    return new DialogContentPane.CheckError(JAPMessages.getString(MSG_ERRALLFIELDS));
                }
                return null;
            }
        }
        return null;
    }

    public DialogContentPane.CheckError checkUpdate() {
        this.showForm();
        return null;
    }

    public void showForm() {
        DialogContentPane dialogContentPane = this.getPreviousContentPane();
        while (!(dialogContentPane instanceof MethodSelectionPane)) {
            dialogContentPane = dialogContentPane.getPreviousContentPane();
        }
        MethodSelectionPane methodSelectionPane = (MethodSelectionPane)dialogContentPane;
        this.m_selectedOption = methodSelectionPane.getSelectedPaymentOption();
        this.m_paymentOptions = null;
        while (this.m_paymentOptions == null) {
            if (dialogContentPane instanceof WorkerContentPane) {
                WorkerContentPane workerContentPane = (WorkerContentPane)dialogContentPane;
                Object object = workerContentPane.getValue();
                if (object instanceof XMLPaymentOptions) {
                    this.m_paymentOptions = (XMLPaymentOptions)object;
                    continue;
                }
                dialogContentPane = dialogContentPane.getPreviousContentPane();
                continue;
            }
            dialogContentPane = dialogContentPane.getPreviousContentPane();
        }
        if (this.m_selectedOption.isGeneric()) {
            this.showGenericForm();
        } else if (this.m_selectedOption.getName().equalsIgnoreCase("creditcard")) {
            this.showCreditCardForm();
        }
    }

    private void showCreditCardForm() {
        int n;
        this.m_rootPanel.removeAll();
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
        this.m_c.gridwidth = 1;
        String string = this.m_paymentOptions.getAcceptedCreditCards();
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        this.m_cbCompany = new JComboBox();
        while (stringTokenizer.hasMoreTokens()) {
            this.m_cbCompany.addItem(stringTokenizer.nextToken());
        }
        this.m_rootPanel.add((Component)this.m_cbCompany, this.m_c);
        JAPJIntField.IntFieldUnlimitedZerosBounds intFieldUnlimitedZerosBounds = new JAPJIntField.IntFieldUnlimitedZerosBounds(9999);
        ++this.m_c.gridy;
        this.m_c.gridwidth = 1;
        this.m_c.fill = 2;
        this.m_rootPanel.add((Component)new JLabel(JAPMessages.getString(MSG_CARDNUMBER)), this.m_c);
        JPanel jPanel = new JPanel(new GridBagLayout());
        this.m_c.gridwidth = 4;
        ++this.m_c.gridx;
        this.m_c.weightx = 1.0;
        this.m_c.insets = new Insets(0, 5, 0, 5);
        this.m_rootPanel.add((Component)jPanel, this.m_c);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        this.m_tfCardNumber1 = new JAPJIntField(intFieldUnlimitedZerosBounds, true);
        jPanel.add((Component)this.m_tfCardNumber1, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.m_tfCardNumber2 = new JAPJIntField(intFieldUnlimitedZerosBounds, true);
        jPanel.add((Component)this.m_tfCardNumber2, gridBagConstraints);
        ++gridBagConstraints.gridx;
        this.m_tfCardNumber3 = new JAPJIntField(intFieldUnlimitedZerosBounds, true);
        jPanel.add((Component)this.m_tfCardNumber3, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        this.m_tfCardNumber4 = new JAPJIntField(intFieldUnlimitedZerosBounds, true);
        jPanel.add((Component)this.m_tfCardNumber4, gridBagConstraints);
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.weightx = 0.0;
        this.m_c.gridx = 0;
        ++this.m_c.gridy;
        this.m_c.gridwidth = 1;
        this.m_rootPanel.add((Component)new JLabel(JAPMessages.getString(MSG_CARDOWNER)), this.m_c);
        ++this.m_c.gridx;
        this.m_c.gridwidth = 4;
        this.m_c.fill = 2;
        this.m_tfCardOwner = new JTextField();
        this.m_rootPanel.add((Component)this.m_tfCardOwner, this.m_c);
        this.m_c.weightx = 0.0;
        this.m_c.gridx = 0;
        ++this.m_c.gridy;
        this.m_c.gridwidth = 1;
        this.m_rootPanel.add((Component)new JLabel(JAPMessages.getString(MSG_CARDVALIDITY)), this.m_c);
        ++this.m_c.gridx;
        this.m_cbMonth = new JComboBox();
        for (n = 1; n < 13; ++n) {
            this.m_cbMonth.addItem(String.valueOf(n));
        }
        this.m_rootPanel.add((Component)this.m_cbMonth, this.m_c);
        this.m_c.gridx = 2;
        n = new GregorianCalendar().get(1);
        this.m_cbYear = new JComboBox();
        for (int i = 0; i < 21; ++i) {
            this.m_cbYear.addItem(String.valueOf(n + i));
        }
        this.m_rootPanel.add((Component)this.m_cbYear, this.m_c);
        ++this.m_c.gridx;
        this.m_c.gridwidth = 2;
        this.m_c.gridheight = 2;
        this.m_rootPanel.add((Component)new JLabel(GUIUtils.loadImageIcon(IMG_CREDITCARDSECURITY, true)), this.m_c);
        this.m_c.gridx = 0;
        ++this.m_c.gridy;
        this.m_c.gridwidth = 1;
        this.m_c.gridheight = 1;
        this.m_c.fill = 2;
        this.m_rootPanel.add((Component)new JLabel(JAPMessages.getString(MSG_CARDCHECKNUMBER)), this.m_c);
        ++this.m_c.gridx;
        this.m_c.gridwidth = 1;
        this.m_tfCardCheckNumber = new JAPJIntField(intFieldUnlimitedZerosBounds, true);
        this.m_rootPanel.add((Component)this.m_tfCardCheckNumber, this.m_c);
        this.setText(this.m_selectedOption.getDetailedInfo(this.m_language));
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

