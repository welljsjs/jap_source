/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.JAPMessages;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import logging.LogHolder;
import logging.LogType;

public class BISelectionDialog
extends JAPDialog
implements ActionListener,
ListSelectionListener {
    private JList m_biList;
    private JButton m_okButton;
    private JButton m_cancelButton;
    private JLabel m_biHost;
    private JLabel m_biPort;
    private PaymentInstanceDBEntry m_selectedBI;

    public BISelectionDialog(Component component) {
        super(component, JAPMessages.getString("biSelectionDialog"), true);
        this.setDefaultCloseOperation(2);
        this.jbInit();
        this.setSize(500, 400);
        this.setVisible(true);
    }

    private void jbInit() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        JPanel jPanel2 = new JPanel(new FlowLayout(0));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.m_biList = new JList();
        this.m_biList.addListSelectionListener(this);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        jPanel.add((Component)this.m_biList, gridBagConstraints);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridx = 3;
        jPanel.add((Component)new JLabel(JAPMessages.getString("infoAboutBI")), gridBagConstraints);
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(JAPMessages.getString("biInfoHost")), gridBagConstraints);
        this.m_biHost = new JLabel();
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_biHost, gridBagConstraints);
        --gridBagConstraints.gridx;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(JAPMessages.getString("biInfoPort")), gridBagConstraints);
        this.m_biPort = new JLabel();
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_biPort, gridBagConstraints);
        this.m_cancelButton = new JButton(JAPMessages.getString("bttnCancel"));
        this.m_cancelButton.addActionListener(this);
        jPanel2.add(this.m_cancelButton);
        this.m_okButton = new JButton(JAPMessages.getString("bttnOk"));
        this.m_okButton.addActionListener(this);
        jPanel2.add(this.m_okButton);
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = 14;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        this.getContentPane().add(jPanel);
        DefaultListModel<String> defaultListModel = new DefaultListModel<String>();
        defaultListModel.addElement(JAPMessages.getString("loadingBIInfo1"));
        defaultListModel.addElement(JAPMessages.getString("loadingBIInfo2"));
        this.m_biList.setModel(defaultListModel);
        this.m_biList.setEnabled(false);
        Runnable runnable = new Runnable(){

            public void run() {
                DefaultListModel<PaymentInstanceDBEntry> defaultListModel = new DefaultListModel<PaymentInstanceDBEntry>();
                try {
                    Vector vector = PayAccountsFile.getInstance().getPaymentInstances();
                    Enumeration enumeration = vector.elements();
                    while (enumeration.hasMoreElements()) {
                        defaultListModel.addElement((PaymentInstanceDBEntry)enumeration.nextElement());
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.PAY, exception.getMessage());
                }
                BISelectionDialog.this.m_biList.setEnabled(true);
                BISelectionDialog.this.m_biList.setModel(defaultListModel);
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    public PaymentInstanceDBEntry getSelectedBI() {
        return this.m_selectedBI;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.m_okButton) {
            this.m_selectedBI = (PaymentInstanceDBEntry)this.m_biList.getSelectedValue();
            this.dispose();
        } else if (actionEvent.getSource() == this.m_cancelButton) {
            this.dispose();
        }
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getSource() == this.m_biList) {
            // empty if block
        }
    }
}

