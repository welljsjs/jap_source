/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import anon.util.JAPMessages;
import gui.TermsAndConditionsDialog;
import gui.dialog.DialogContentPane;
import gui.dialog.JAPDialog;
import jap.JAPController;
import jap.TermsAndConditionsOperatorTable;
import jap.TermsAndCondtionsTableController;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TermsAndConditionsInfoDialog
extends JAPDialog
implements TermsAndCondtionsTableController,
ActionListener {
    public static String MSG_DIALOG_TEXT = (class$jap$TermsAndConditionsInfoDialog == null ? (class$jap$TermsAndConditionsInfoDialog = TermsAndConditionsInfoDialog.class$("jap.TermsAndConditionsInfoDialog")) : class$jap$TermsAndConditionsInfoDialog).getName() + "_dialogText";
    public static String MSG_DIALOG_TITLE = (class$jap$TermsAndConditionsInfoDialog == null ? (class$jap$TermsAndConditionsInfoDialog = TermsAndConditionsInfoDialog.class$("jap.TermsAndConditionsInfoDialog")) : class$jap$TermsAndConditionsInfoDialog).getName() + "_dialogTitle";
    private TermsAndConditionsOperatorTable operatorTable = null;
    private JButton okButtton = null;
    private JButton cancelButton = null;
    static /* synthetic */ Class class$jap$TermsAndConditionsInfoDialog;

    public TermsAndConditionsInfoDialog(Component component, Vector vector, String string) {
        super(component, JAPMessages.getString(MSG_DIALOG_TITLE));
        this.setDefaultCloseOperation(1);
        Container container = this.getContentPane();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = 1;
        JTextArea jTextArea = new JTextArea(JAPMessages.getString(MSG_DIALOG_TEXT, string));
        jTextArea.setText(JAPMessages.getString(MSG_DIALOG_TEXT, string));
        jTextArea.setEditable(false);
        jTextArea.setBackground(container.getBackground());
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setSelectionColor(container.getBackground());
        jTextArea.setSelectedTextColor(jTextArea.getForeground());
        container.add((Component)jTextArea, gridBagConstraints);
        gridBagConstraints.gridwidth = 0;
        this.operatorTable = new TermsAndConditionsOperatorTable(vector);
        this.operatorTable.setController(this);
        JScrollPane jScrollPane = new JScrollPane(this.operatorTable);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.setPreferredSize(new Dimension(400, 120));
        this.okButtton = new JButton(JAPMessages.getString(DialogContentPane.MSG_OK));
        this.cancelButton = new JButton(JAPMessages.getString(DialogContentPane.MSG_CANCEL));
        this.okButtton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        JPanel jPanel = new JPanel();
        jPanel.add(this.okButtton);
        jPanel.add(this.cancelButton);
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        container.add((Component)jScrollPane, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        container.add((Component)jPanel, gridBagConstraints);
        this.okButtton.setEnabled(!this.operatorTable.areTermsRejected());
        this.pack();
    }

    public boolean areAllAccepted() {
        Vector vector = this.operatorTable.getOperators();
        for (int i = 0; i < vector.size(); ++i) {
            TermsAndConditions termsAndConditions = TermsAndConditions.getTermsAndConditions((ServiceOperator)vector.elementAt(i));
            if (termsAndConditions != null && termsAndConditions.isAccepted()) continue;
            return false;
        }
        return true;
    }

    public boolean handleOperatorAction(ServiceOperator serviceOperator, boolean bl) {
        TermsAndConditions termsAndConditions = TermsAndConditions.getTermsAndConditions(serviceOperator);
        TermsAndConditionsDialog termsAndConditionsDialog = new TermsAndConditionsDialog(JAPController.getInstance().getCurrentView(), bl, termsAndConditions);
        termsAndConditionsDialog.setVisible(true);
        TermsAndConditionsDialog.TermsAndConditonsDialogReturnValues termsAndConditonsDialogReturnValues = termsAndConditionsDialog.getReturnValues();
        return termsAndConditonsDialogReturnValues.isCancelled() ? bl : termsAndConditonsDialogReturnValues.isAccepted();
    }

    public void handleSelectLineAction(ServiceOperator serviceOperator) {
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.okButtton) {
            this.commitActions();
        }
        this.dispose();
    }

    public void commitActions() {
        Vector[] arrvector = new Vector[]{this.operatorTable.getTermsAccepted(), this.operatorTable.getTermsRejected()};
        TermsAndConditions termsAndConditions = null;
        boolean bl = false;
        for (int i = 0; i < arrvector.length; ++i) {
            boolean bl2 = bl = i == 0;
            if (arrvector[i] == null) continue;
            for (int j = 0; j < arrvector[i].size(); ++j) {
                termsAndConditions = (TermsAndConditions)arrvector[i].elementAt(j);
                if (termsAndConditions == null) continue;
                termsAndConditions.setAccepted(bl);
            }
        }
    }

    public void handleAcceptAction(ServiceOperator serviceOperator, boolean bl) {
        this.okButtton.setEnabled(!this.operatorTable.areTermsRejected());
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

