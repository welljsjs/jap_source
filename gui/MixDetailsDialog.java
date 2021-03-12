/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.infoservice.DataRetentionInformation;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.infoservice.ServiceLocation;
import anon.infoservice.ServiceOperator;
import anon.platform.AbstractOS;
import anon.util.CountryMapper;
import anon.util.JAPMessages;
import gui.DataRetentionDialog;
import gui.GUIUtils;
import gui.MultiCertOverview;
import gui.dialog.JAPDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.LogHolder;
import logging.LogType;

public class MixDetailsDialog
extends JAPDialog {
    public static final String MSG_NOT_VERIFIED = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_notVerified";
    public static final String MSG_INVALID = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_invalid";
    public static final String MSG_VALID = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_valid";
    public static final String MSG_INDEPENDENT_CERTIFICATIONS = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_independentCertifications";
    public static final String MSG_MIX_X_OF_Y = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_mixXOfY";
    public static String MSG_MIX_NAME = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_mixName";
    public static String MSG_LOCATION = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_mixLocation";
    public static String MSG_HOMEPAGE = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_operatorHomepage";
    public static String MSG_E_MAIL = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_email";
    public static String MSG_CERTIFICATES = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_certificates";
    public static String MSG_BTN_DATA_RETENTION = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_btnDataRetention";
    private static String MSG_TITLE = (class$gui$MixDetailsDialog == null ? (class$gui$MixDetailsDialog = MixDetailsDialog.class$("gui.MixDetailsDialog")) : class$gui$MixDetailsDialog).getName() + "_title";
    private MixCascade m_mixCascade;
    private MixInfo m_mixInfo;
    private int m_mixPosition;
    private ActionListener m_buttonListener;
    private JButton m_btnHomepage;
    private JButton m_btnEMail;
    private JButton m_btnCertificates;
    private JButton m_btnDataRetention;
    static /* synthetic */ Class class$gui$MixDetailsDialog;

    public MixDetailsDialog(Component component, MixCascade mixCascade, int n, int n2) {
        super(component, JAPMessages.getString(MSG_TITLE));
        DataRetentionInformation dataRetentionInformation;
        this.m_mixCascade = mixCascade;
        this.m_mixPosition = n;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = (JPanel)this.getContentPane();
        JPanel jPanel2 = new JPanel(new FlowLayout());
        jPanel.setLayout(new GridBagLayout());
        if (this.m_mixCascade == null || mixCascade.getMixInfo(n) == null) {
            return;
        }
        this.m_mixInfo = n2 == 1 ? mixCascade.getMixInfo(mixCascade.getNumberOfMixes() - 1) : mixCascade.getMixInfo(n);
        ServiceOperator serviceOperator = this.m_mixInfo.getServiceOperator();
        ServiceLocation serviceLocation = this.m_mixInfo.getServiceLocation();
        if (serviceOperator == null || serviceLocation == null) {
            return;
        }
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_MIX_NAME) + ":");
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(15, 15, 10, 15);
        gridBagConstraints.anchor = 17;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(this.m_mixInfo.getName());
        gridBagConstraints.gridx = 1;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(JAPMessages.getString(MSG_LOCATION) + ":");
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 15, 10, 15);
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(GUIUtils.getCountryFromServiceLocation(serviceLocation));
        gridBagConstraints.gridy = 1;
        ++gridBagConstraints.gridx;
        jLabel.setIcon(GUIUtils.loadImageIcon("flags/" + serviceLocation.getCountryCode() + ".png"));
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(JAPMessages.getString("mixOperator"));
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.anchor = 17;
        jPanel.add((Component)jLabel, gridBagConstraints);
        String string = serviceOperator.getOrganization();
        jLabel = new JLabel();
        if (serviceOperator.getCountryCode() != null) {
            string = string + "  (" + new CountryMapper(serviceOperator.getCountryCode(), JAPMessages.getLocale()).toString() + ")";
            jLabel.setIcon(GUIUtils.loadImageIcon("flags/" + serviceOperator.getCountryCode() + ".png"));
        }
        jLabel.setText(string);
        gridBagConstraints.gridx = 1;
        jPanel.add((Component)jLabel, gridBagConstraints);
        this.m_buttonListener = new MyButtonListener();
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 2;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        if (this.m_mixInfo.getCertPath() != null) {
            this.m_btnCertificates = new JButton(JAPMessages.getString(MSG_CERTIFICATES));
            this.m_btnCertificates.addActionListener(this.m_buttonListener);
            if (!this.m_mixInfo.getCertPath().isVerified()) {
                this.m_btnCertificates.setIcon(GUIUtils.loadImageIcon("certs/not_trusted.png"));
                this.m_btnCertificates.setToolTipText(JAPMessages.getString(MSG_NOT_VERIFIED));
                this.m_btnCertificates.setForeground(Color.red);
            } else if (!this.m_mixInfo.getCertPath().isValid(new Date())) {
                this.m_btnCertificates.setIcon(GUIUtils.loadImageIcon("certs/invalid.png"));
                this.m_btnCertificates.setToolTipText(JAPMessages.getString(MSG_INVALID));
            } else if (this.m_mixInfo.getCertPath().countVerifiedAndValidPaths() > 1) {
                this.m_btnCertificates.setToolTipText(JAPMessages.getString(MSG_INDEPENDENT_CERTIFICATIONS, "" + this.m_mixInfo.getCertPath().countVerifiedAndValidPaths()));
                if (this.m_mixInfo.getCertPath().countVerifiedAndValidPaths() > 2) {
                    this.m_btnCertificates.setIcon(GUIUtils.loadImageIcon("certs/trusted_green.png"));
                } else {
                    this.m_btnCertificates.setIcon(GUIUtils.loadImageIcon("certs/trusted_blue.png"));
                }
            } else {
                this.m_btnCertificates.setToolTipText(JAPMessages.getString(MSG_VALID));
                this.m_btnCertificates.setIcon(GUIUtils.loadImageIcon("certs/trusted_black.png"));
            }
            jPanel2.add((Component)this.m_btnCertificates, gridBagConstraints);
        }
        if (serviceOperator.getEMail() != null) {
            this.m_btnEMail = new JButton(JAPMessages.getString(MSG_E_MAIL));
            this.m_btnEMail.setToolTipText(serviceOperator.getEMail());
            this.m_btnEMail.addActionListener(this.m_buttonListener);
            jPanel2.add((Component)this.m_btnEMail, gridBagConstraints);
        }
        if (serviceOperator.getUrl() != null) {
            this.m_btnHomepage = new JButton(JAPMessages.getString(MSG_HOMEPAGE));
            this.m_btnHomepage.setToolTipText(serviceOperator.getUrl());
            this.m_btnHomepage.addActionListener(this.m_buttonListener);
            jPanel2.add((Component)this.m_btnHomepage, gridBagConstraints);
        }
        if ((dataRetentionInformation = this.m_mixInfo.getDataRetentionInformation()) != null) {
            this.m_btnDataRetention = new JButton(JAPMessages.getString(MSG_BTN_DATA_RETENTION), GUIUtils.loadImageIcon("certs/invalid.png"));
            this.m_btnDataRetention.setToolTipText(JAPMessages.getString(DataRetentionDialog.MSG_DATA_RETENTION_MIX_EXPLAIN_SHORT));
            this.m_btnDataRetention.addActionListener(this.m_buttonListener);
            jPanel2.add((Component)this.m_btnDataRetention, gridBagConstraints);
        }
        this.pack();
        this.setResizable(false);
        jPanel.setVisible(true);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class MyButtonListener
    implements ActionListener {
        private MyButtonListener() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == MixDetailsDialog.this.m_btnHomepage) {
                String string = ((JButton)actionEvent.getSource()).getToolTipText();
                if (string == null) {
                    return;
                }
                AbstractOS abstractOS = AbstractOS.getInstance();
                try {
                    abstractOS.openURL(new URL(string));
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "Error opening URL in browser");
                }
            } else if (actionEvent.getSource() == MixDetailsDialog.this.m_btnDataRetention) {
                DataRetentionDialog.show(MixDetailsDialog.this.getContentPane(), MixDetailsDialog.this.m_mixCascade, MixDetailsDialog.this.m_mixPosition);
            } else if (actionEvent.getSource() == MixDetailsDialog.this.m_btnEMail) {
                String string = MixDetailsDialog.this.m_btnEMail.getToolTipText();
                if (string == null) {
                    return;
                }
                AbstractOS abstractOS = AbstractOS.getInstance();
                try {
                    abstractOS.openEMail(string);
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "Error creating E-Mail!");
                }
            } else if (actionEvent.getSource() == MixDetailsDialog.this.m_btnCertificates) {
                new MultiCertOverview(MixDetailsDialog.this.getContentPane(), MixDetailsDialog.this.m_mixInfo.getCertPath(), MixDetailsDialog.this.m_mixInfo.getName(), false);
            }
        }
    }
}

