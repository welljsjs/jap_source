/*
 * Decompiled with CFR 0.150.
 */
package jap.pay.wizardnew;

import anon.infoservice.ListenerInterface;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.JAPMessages;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class JpiSelectionPane
extends DialogContentPane
implements ActionListener,
DialogContentPane.IWizardSuitable {
    private static final String MSG_CHOOSEAJPI = (class$jap$pay$wizardnew$JpiSelectionPane == null ? (class$jap$pay$wizardnew$JpiSelectionPane = JpiSelectionPane.class$("jap.pay.wizardnew.JpiSelectionPane")) : class$jap$pay$wizardnew$JpiSelectionPane).getName() + "_chooseajpi";
    private static final String MSG_CHOOSEAJPI_TITLE = (class$jap$pay$wizardnew$JpiSelectionPane == null ? (class$jap$pay$wizardnew$JpiSelectionPane = JpiSelectionPane.class$("jap.pay.wizardnew.JpiSelectionPane")) : class$jap$pay$wizardnew$JpiSelectionPane).getName() + "_titleChooseajpi";
    private static final String MSG_HAVE_TO_CHOOSE = (class$jap$pay$wizardnew$JpiSelectionPane == null ? (class$jap$pay$wizardnew$JpiSelectionPane = JpiSelectionPane.class$("jap.pay.wizardnew.JpiSelectionPane")) : class$jap$pay$wizardnew$JpiSelectionPane).getName() + "_havetochoose";
    private WorkerContentPane m_fetchJPIPane;
    private PaymentInstanceDBEntry m_selectedJpi;
    private Vector m_allJpis;
    private Hashtable m_Jpis;
    private ButtonGroup m_rbGroup;
    private GridBagConstraints m_c = new GridBagConstraints();
    private Container m_rootPanel;
    static /* synthetic */ Class class$jap$pay$wizardnew$JpiSelectionPane;

    public JpiSelectionPane(JAPDialog jAPDialog, WorkerContentPane workerContentPane, String string) {
        super(jAPDialog, JAPMessages.getString(MSG_CHOOSEAJPI), new DialogContentPane.Layout(JAPMessages.getString(MSG_CHOOSEAJPI_TITLE), -1), new DialogContentPaneOptions(2, (DialogContentPane)workerContentPane));
        this.setDefaultButtonOperation(266);
        this.m_fetchJPIPane = workerContentPane;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof JRadioButton) {
            PaymentInstanceDBEntry paymentInstanceDBEntry;
            JRadioButton jRadioButton = (JRadioButton)actionEvent.getSource();
            String string = jRadioButton.getName();
            this.m_selectedJpi = paymentInstanceDBEntry = (PaymentInstanceDBEntry)this.m_Jpis.get(string);
        }
    }

    private void addJpi(PaymentInstanceDBEntry paymentInstanceDBEntry) {
        this.m_c.insets = new Insets(0, 5, 0, 5);
        ++this.m_c.gridy;
        String string = paymentInstanceDBEntry.getId();
        String string2 = paymentInstanceDBEntry.getName();
        ListenerInterface listenerInterface = (ListenerInterface)paymentInstanceDBEntry.getListenerInterfaces().nextElement();
        String string3 = listenerInterface.getHost() + " : " + listenerInterface.getPort();
        this.m_c.gridx = 0;
        JRadioButton jRadioButton = new JRadioButton(string2 + " , " + string3);
        jRadioButton.setName(string);
        jRadioButton.addActionListener(this);
        this.m_rbGroup.add(jRadioButton);
        this.m_rootPanel.add((Component)jRadioButton, this.m_c);
    }

    public DialogContentPane.CheckError checkYesOK() {
        DialogContentPane.CheckError checkError = super.checkYesOK();
        if (checkError == null && this.m_rbGroup.getSelection() == null) {
            checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_HAVE_TO_CHOOSE));
        }
        return checkError;
    }

    public DialogContentPane.CheckError checkUpdate() {
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
        this.m_allJpis = (Vector)this.m_fetchJPIPane.getValue();
        this.showPaymentInstances();
        this.m_rootPanel.setVisible(true);
        this.resetSelection();
        return null;
    }

    public void showPaymentInstances() {
        this.m_Jpis = new Hashtable();
        Enumeration enumeration = this.m_allJpis.elements();
        while (enumeration.hasMoreElements()) {
            PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)enumeration.nextElement();
            String string = paymentInstanceDBEntry.getId();
            this.m_Jpis.put(string, paymentInstanceDBEntry);
        }
        this.m_rootPanel.removeAll();
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        ++this.m_c.gridy;
        enumeration = this.m_allJpis.elements();
        while (enumeration.hasMoreElements()) {
            this.addJpi((PaymentInstanceDBEntry)enumeration.nextElement());
        }
    }

    public void resetSelection() {
        this.m_selectedJpi = null;
    }

    public PaymentInstanceDBEntry getSelectedPaymentInstance() {
        this.m_allJpis = (Vector)this.m_fetchJPIPane.getValue();
        if (this.m_allJpis == null || this.m_allJpis.size() == 0) {
            this.m_selectedJpi = null;
        } else if (this.m_allJpis.size() == 1) {
            return (PaymentInstanceDBEntry)this.m_allJpis.elementAt(0);
        }
        return this.m_selectedJpi;
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

