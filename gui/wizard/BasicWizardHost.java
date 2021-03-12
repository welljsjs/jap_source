/*
 * Decompiled with CFR 0.150.
 */
package gui.wizard;

import anon.util.JAPMessages;
import gui.dialog.JAPDialog;
import gui.wizard.Wizard;
import gui.wizard.WizardHost;
import gui.wizard.WizardPage;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class BasicWizardHost
implements WizardHost,
ActionListener {
    private JAPDialog m_Dialog;
    private JButton m_bttnOk;
    private JButton m_bttnCancel;
    private JButton m_bttnFinish;
    private JButton m_bttnBack;
    private JButton m_bttnNext;
    private JButton m_bttnHelp;
    private JPanel m_panelPages;
    private CardLayout m_cardlayoutPages;
    private Wizard m_Wizard;
    private static final String COMMAND_NEXT = "NEXT";
    private static final String COMMAND_BACK = "BACK";
    private static final String COMMAND_CANCEL = "CANCEL";
    private static final String COMMAND_FINISH = "FINISH";
    private static final String COMMAND_HELP = "HELP";

    public BasicWizardHost(JAPDialog jAPDialog, Wizard wizard) {
        this((Object)jAPDialog, wizard);
    }

    public BasicWizardHost(Component component, Wizard wizard) {
        this((Object)component, wizard);
    }

    private BasicWizardHost(Object object, Wizard wizard) {
        this.m_Wizard = wizard;
        this.m_Dialog = object instanceof JAPDialog ? new JAPDialog((JAPDialog)object, wizard.getWizardTitle(), true) : new JAPDialog((Component)object, wizard.getWizardTitle(), true);
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_Dialog.setDefaultCloseOperation(0);
        this.m_Dialog.getContentPane().setLayout(gridBagLayout);
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(gridBagLayout2);
        this.m_bttnBack = new JButton(JAPMessages.getString("updateM_bttnBack"));
        this.m_bttnBack.setActionCommand(COMMAND_BACK);
        this.m_bttnBack.addActionListener(this);
        this.m_bttnNext = new JButton(JAPMessages.getString("updateM_bttnNext"));
        this.m_bttnNext.setActionCommand(COMMAND_NEXT);
        this.m_bttnNext.addActionListener(this);
        this.m_bttnHelp = new JButton(JAPMessages.getString("updateM_bttnHelp"));
        this.m_bttnCancel = new JButton(JAPMessages.getString("updateM_bttnCancel"));
        this.m_bttnCancel.setActionCommand(COMMAND_CANCEL);
        this.m_bttnCancel.addActionListener(this);
        this.m_bttnFinish = new JButton(JAPMessages.getString("updateM_bttnFinish"));
        this.m_bttnFinish.setActionCommand(COMMAND_FINISH);
        this.m_bttnFinish.addActionListener(this);
        JSeparator jSeparator = new JSeparator();
        jSeparator.setVisible(true);
        this.m_cardlayoutPages = new CardLayout();
        this.m_panelPages = new JPanel(this.m_cardlayoutPages);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new Insets(10, 10, 10, 10);
        jPanel.add((Component)this.m_bttnHelp, gridBagConstraints2);
        JLabel jLabel = new JLabel("");
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.fill = 2;
        jPanel.add((Component)jLabel, gridBagConstraints2);
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.insets = new Insets(10, 10, 10, 20);
        jPanel.add((Component)this.m_bttnCancel, gridBagConstraints2);
        gridBagConstraints2.gridx = 3;
        gridBagConstraints2.insets = new Insets(10, 2, 10, 2);
        jPanel.add((Component)this.m_bttnBack, gridBagConstraints2);
        gridBagConstraints2.gridx = 4;
        jPanel.add((Component)this.m_bttnNext, gridBagConstraints2);
        gridBagConstraints2.gridx = 5;
        gridBagConstraints2.insets = new Insets(10, 20, 10, 10);
        jPanel.add((Component)this.m_bttnFinish, gridBagConstraints2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagLayout.setConstraints(this.m_panelPages, gridBagConstraints);
        this.m_Dialog.getContentPane().add(this.m_panelPages);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagLayout.setConstraints(jSeparator, gridBagConstraints);
        this.m_Dialog.getContentPane().add(jSeparator);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.fill = 2;
        gridBagLayout.setConstraints(jPanel, gridBagConstraints);
        this.m_Dialog.getContentPane().add(jPanel);
    }

    public void addWizardPage(int n, WizardPage wizardPage) {
        this.m_panelPages.add((Component)wizardPage.getPageComponent(), Integer.toString(n));
    }

    public void showWizardPage(int n) {
        if (n == 0) {
            this.m_cardlayoutPages.first(this.m_panelPages);
            this.m_Dialog.pack();
            this.m_Dialog.setVisible(true);
        } else {
            this.m_cardlayoutPages.show(this.m_panelPages, Integer.toString(n));
            this.m_Dialog.pack();
        }
    }

    public JAPDialog getDialogParent() {
        return this.m_Dialog;
    }

    public void setHelpEnabled(boolean bl) {
        this.m_bttnHelp.setEnabled(bl);
    }

    public void setNextEnabled(boolean bl) {
        this.m_bttnNext.setEnabled(bl);
    }

    public void setBackEnabled(boolean bl) {
        this.m_bttnBack.setEnabled(bl);
    }

    public void setCancelEnabled(boolean bl) {
        this.m_bttnCancel.setEnabled(bl);
    }

    public void setFinishEnabled(boolean bl) {
        this.m_bttnFinish.setEnabled(bl);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string = actionEvent.getActionCommand();
        if (string.equals(COMMAND_NEXT)) {
            this.m_Wizard.next();
        } else if (string.equals(COMMAND_BACK)) {
            this.m_Wizard.back();
        } else if (string.equals(COMMAND_CANCEL)) {
            this.doCancel();
        } else if (string.equals(COMMAND_FINISH)) {
            this.m_Wizard.finish();
        } else if (string.equals(COMMAND_HELP)) {
            // empty if block
        }
    }

    public void lockDialog() {
        this.m_Dialog.setEnabled(false);
    }

    public void unlockDialog() {
        this.m_Dialog.setEnabled(true);
    }

    public void doCancel() {
        this.m_Wizard.wizardCompleted();
        this.m_Dialog.dispose();
    }
}

