/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import anon.pay.xml.XMLPassivePayment;
import anon.util.JAPMessages;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.JAPDialog;
import jap.JAPUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.LogHolder;
import logging.LogType;

public class PassivePaymentDetails
extends JAPDialog
implements ActionListener {
    private static final String MSG_HEADING = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_heading";
    private static final String MSG_TITLE = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_title";
    private static final String MSG_CLOSEBUTTON = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_closebutton";
    private static final String MSG_UNKNOWN_PAYMENT = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_unknownpayment";
    private static final String MSG_NOT_SHOWN = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_notshown";
    private static final String MSG_PAID_BY = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_paidby";
    private static final String MSG_CREDITCARDWORD = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_creditcardword";
    private static final String MSG_CREDITCARDTYPE = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_creditcardtype";
    private static final String MSG_NUMBER = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_number";
    private static final String MSG_OWNER = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_owner";
    private static final String MSG_VALID = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_valid";
    private static final String MSG_CHECKNUMBER = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_checknumber";
    private static final String MSG_AMOUNT = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_amount";
    private static final String MSG_TRANSFERNUMBER = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_transfernumber";
    private static final String MSG_ACCOUNTNUMBER = (class$jap$pay$PassivePaymentDetails == null ? (class$jap$pay$PassivePaymentDetails = PassivePaymentDetails.class$("jap.pay.PassivePaymentDetails")) : class$jap$pay$PassivePaymentDetails).getName() + "_accountnumber";
    private GridBagConstraints m_c;
    private JButton m_closeButton;
    static /* synthetic */ Class class$jap$pay$PassivePaymentDetails;

    public PassivePaymentDetails(JAPDialog jAPDialog, XMLPassivePayment xMLPassivePayment, long l, long l2) {
        super(jAPDialog, JAPMessages.getString(MSG_TITLE));
        try {
            this.setDefaultCloseOperation(2);
            this.buildDialog(xMLPassivePayment, l, l2);
            this.setResizable(false);
            this.pack();
            this.setVisible(true);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "Could not create PassivePaymentDetails: ", exception);
        }
    }

    private void buildDialog(XMLPassivePayment xMLPassivePayment, long l, long l2) {
        this.m_c = new GridBagConstraints();
        this.m_c.anchor = 11;
        this.m_c.insets = new Insets(10, 30, 10, 30);
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weighty = 0.0;
        this.m_c.weightx = 0.0;
        this.getContentPane().setLayout(new GridBagLayout());
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("<h3>" + JAPMessages.getString(MSG_HEADING) + "</h3");
        this.getContentPane().add((Component)jAPHtmlMultiLineLabel, this.m_c);
        ++this.m_c.gridy;
        JPanel jPanel = this.buildTransactionDetailsPanel(l2, l, xMLPassivePayment.getAmount());
        this.getContentPane().add((Component)jPanel, this.m_c);
        ++this.m_c.gridy;
        JPanel jPanel2 = this.buildPaymentDetailsPanel(xMLPassivePayment);
        this.getContentPane().add((Component)jPanel2, this.m_c);
        ++this.m_c.gridy;
        this.m_closeButton = new JButton(JAPMessages.getString(MSG_CLOSEBUTTON));
        this.m_closeButton.addActionListener(this);
        ++this.m_c.gridy;
        this.getContentPane().add((Component)this.m_closeButton, this.m_c);
    }

    private JPanel buildTransactionDetailsPanel(long l, long l2, long l3) {
        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel, 1);
        jPanel.setLayout(boxLayout);
        String string = new Long(l).toString();
        String string2 = JAPMessages.getString(MSG_ACCOUNTNUMBER);
        JLabel jLabel = new JLabel(string2 + ": " + string);
        jPanel.add(jLabel);
        String string3 = new Long(l2).toString();
        String string4 = JAPMessages.getString(MSG_TRANSFERNUMBER);
        JLabel jLabel2 = new JLabel(string4 + ": " + string3);
        jPanel.add((Component)jLabel2, this.m_c);
        String string5 = JAPUtil.formatEuroCentValue(l3, true);
        String string6 = JAPMessages.getString(MSG_AMOUNT);
        JLabel jLabel3 = new JLabel(string6 + ": " + string5);
        jPanel.add((Component)jLabel3, this.m_c);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        return jPanel;
    }

    private JPanel buildPaymentDetailsPanel(XMLPassivePayment xMLPassivePayment) {
        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel, 1);
        jPanel.setLayout(boxLayout);
        String string = xMLPassivePayment.getPaymentName();
        if (string.equalsIgnoreCase("CreditCard")) {
            JLabel jLabel = new JLabel(JAPMessages.getString(MSG_NOT_SHOWN));
            jPanel.add(jLabel);
        } else if (!string.equalsIgnoreCase("Paysafecard")) {
            JLabel jLabel = new JLabel(JAPMessages.getString(MSG_UNKNOWN_PAYMENT));
            jPanel.add(jLabel);
        }
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        return jPanel;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.m_closeButton) {
            this.setVisible(false);
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
}

