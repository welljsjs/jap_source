/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.transport.address.AddressParameter;
import anon.transport.address.IAddress;
import anon.util.JAPMessages;
import gui.JAPHtmlMultiLineLabel;
import jap.AbstractJAPConfModule;
import jap.JAPModel;
import jap.MessageSystem;
import jap.forward.JAPRoutingInfoServiceRegistrationTableModel;
import jap.forward.JAPRoutingMessage;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import logging.LogHolder;
import logging.LogType;

public class JAPConfForwardingState
extends AbstractJAPConfModule {
    private MessageSystem m_messageSystem;

    public JAPConfForwardingState() {
        super(null);
    }

    public String getHelpContext() {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recreateRootPanel() {
        Object object = this;
        synchronized (object) {
            if (this.m_messageSystem == null) {
                this.m_messageSystem = new MessageSystem();
            }
        }
        object = this.getRootPanel();
        JAPConfForwardingState jAPConfForwardingState = this;
        synchronized (jAPConfForwardingState) {
            ((Container)object).removeAll();
            this.m_messageSystem.sendMessage();
            JPanel jPanel = this.createForwardingStatePanel();
            GridBagLayout gridBagLayout = new GridBagLayout();
            ((Container)object).setLayout(gridBagLayout);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = 18;
            gridBagConstraints.fill = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagLayout.setConstraints(jPanel, gridBagConstraints);
            ((Container)object).add(jPanel);
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("confTreeForwardingStateLeaf");
    }

    private JPanel createForwardingServerStatePanel() {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setMinimumIntegerDigits(1);
        final NumberFormat numberFormat2 = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(1);
        final JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("");
        final JLabel jLabel = new JLabel();
        final JLabel jLabel2 = new JLabel();
        final JLabel jLabel3 = new JLabel();
        JLabel jLabel4 = new JLabel(JAPMessages.getString("settingsRoutingServerStatusStatisticsConnectionsLabel"));
        final JLabel jLabel5 = new JLabel();
        final JLabel jLabel6 = new JLabel();
        final JLabel jLabel7 = new JLabel();
        JLabel jLabel8 = new JLabel(JAPMessages.getString("settingsRoutingServerStatusInfoServiceRegistrationsLabel"));
        final JAPRoutingInfoServiceRegistrationTableModel jAPRoutingInfoServiceRegistrationTableModel = new JAPRoutingInfoServiceRegistrationTableModel();
        JTable jTable = new JTable(jAPRoutingInfoServiceRegistrationTableModel);
        jTable.getColumnModel().getColumn(1).setMaxWidth(125);
        jTable.getColumnModel().getColumn(1).setPreferredWidth(125);
        jTable.setEnabled(false);
        jTable.getTableHeader().setResizingAllowed(false);
        jTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(jScrollPane.getPreferredSize().width, 50));
        Observer observer = new Observer(){

            public void update(Observable observable, Object object) {
                try {
                    if (observable == JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener() && ((JAPRoutingMessage)object).getMessageCode() == 13) {
                        jLabel2.setText(JAPMessages.getString("settingsRoutingServerStatusStatisticsBandwidthLabelPart1") + " " + numberFormat.format((double)JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().getCurrentBandwidthUsage() / 1024.0) + " " + JAPMessages.getString("settingsRoutingServerStatusStatisticsBandwidthLabelPart2"));
                        jLabel3.setText(JAPMessages.getString("settingsRoutingServerStatusStatisticsForwardedBytesLabel") + " " + numberFormat2.format(JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().getTransferedBytes()));
                        jLabel5.setText(JAPMessages.getString("settingsRoutingServerStatusStatisticsCurrentConnectionsLabel") + " " + numberFormat2.format(JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().getCurrentlyForwardedConnections()));
                        jLabel6.setText(JAPMessages.getString("settingsRoutingServerStatusStatisticsAcceptedConnectionsLabel") + " " + numberFormat2.format(JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().getAcceptedConnections()));
                        jLabel7.setText(JAPMessages.getString("settingsRoutingServerStatusStatisticsRejectedConnectionsLabel") + " " + numberFormat2.format(JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().getRejectedConnections()));
                    }
                    if (observable == JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver() && ((JAPRoutingMessage)object).getMessageCode() == 14) {
                        int n = JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver().getCurrentState();
                        int n2 = JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver().getCurrentErrorCode();
                        if (n == 0) {
                            jAPHtmlMultiLineLabel.setText(JAPMessages.getString("settingsRoutingServerStatusLabelStateRegistrationDisabled"));
                        } else if (n == 1) {
                            jAPHtmlMultiLineLabel.setText(JAPMessages.getString("settingsRoutingServerStatusLabelStateRegistrationInitiated"));
                        } else if (n == 2) {
                            jAPHtmlMultiLineLabel.setText(JAPMessages.getString("settingsRoutingServerStatusLabelStateRegistrationFailed"));
                        } else if (n == 3) {
                            jAPHtmlMultiLineLabel.setText(JAPMessages.getString("settingsRoutingServerStatusLabelStateRegistrationSuccessful"));
                        }
                        if (n2 == 0) {
                            jLabel.setText(" ");
                        } else if (n2 == 1) {
                            jLabel.setText(JAPMessages.getString("settingsRoutingServerStatusRegistrationErrorLabelNoKnownInfoServices"));
                        } else if (n2 == 2) {
                            jLabel.setText(JAPMessages.getString("settingsRoutingServerStatusRegistrationErrorLabelConnectionFailed"));
                        } else if (n2 == 3) {
                            jLabel.setText(JAPMessages.getString("settingsRoutingServerStatusRegistrationErrorLabelVerificationFailed"));
                        } else if (n2 == 4) {
                            jLabel.setText(JAPMessages.getString("settingsRoutingServerStatusRegistrationErrorLabelUnknownReason"));
                        }
                    }
                    if (observable == JAPModel.getInstance().getRoutingSettings() && ((JAPRoutingMessage)object).getMessageCode() == 2) {
                        jAPRoutingInfoServiceRegistrationTableModel.updatePropagandaInstancesList((Vector)((JAPRoutingMessage)object).getMessageData());
                    }
                    if (observable == JAPConfForwardingState.this.m_messageSystem) {
                        JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().deleteObserver(this);
                        JAPModel.getInstance().getRoutingSettings().deleteObserver(this);
                        JAPConfForwardingState.this.m_messageSystem.deleteObserver(this);
                        jAPRoutingInfoServiceRegistrationTableModel.clearPropagandaInstancesTable();
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                }
            }
        };
        this.m_messageSystem.addObserver(observer);
        JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener().addObserver(observer);
        JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver().addObserver(observer);
        JAPModel.getInstance().getRoutingSettings().addObserver(observer);
        observer.update(JAPModel.getInstance().getRoutingSettings().getServerStatisticsListener(), new JAPRoutingMessage(13));
        observer.update(JAPModel.getInstance().getRoutingSettings().getRegistrationStatusObserver(), new JAPRoutingMessage(14));
        jAPRoutingInfoServiceRegistrationTableModel.updatePropagandaInstancesList(JAPModel.getInstance().getRoutingSettings().getRunningPropagandaInstances());
        JPanel jPanel = new JPanel();
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsRoutingServerStatusBorder"));
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(jAPHtmlMultiLineLabel, gridBagConstraints);
        jPanel.add(jAPHtmlMultiLineLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 5, 20, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel.add(jLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel8, gridBagConstraints);
        jPanel.add(jLabel8);
        gridBagConstraints.fill = 1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        gridBagLayout.setConstraints(jScrollPane, gridBagConstraints);
        jPanel.add(jScrollPane);
        JPanel jPanel2 = new JPanel();
        TitledBorder titledBorder2 = new TitledBorder(JAPMessages.getString("settingsRoutingServerStatusStatisticsBorder"));
        jPanel2.setBorder(titledBorder2);
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        jPanel2.setLayout(gridBagLayout2);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.anchor = 18;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 0.0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 4;
        gridBagConstraints2.insets = new Insets(5, 5, 10, 5);
        gridBagLayout2.setConstraints(jLabel2, gridBagConstraints2);
        jPanel2.add(jLabel2);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.insets = new Insets(0, 5, 10, 5);
        gridBagLayout2.setConstraints(jLabel3, gridBagConstraints2);
        jPanel2.add(jLabel3);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.insets = new Insets(0, 5, 5, 15);
        gridBagLayout2.setConstraints(jLabel4, gridBagConstraints2);
        jPanel2.add(jLabel4);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.insets = new Insets(0, 0, 5, 15);
        gridBagLayout2.setConstraints(jLabel5, gridBagConstraints2);
        jPanel2.add(jLabel5);
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.insets = new Insets(0, 0, 5, 15);
        gridBagLayout2.setConstraints(jLabel6, gridBagConstraints2);
        jPanel2.add(jLabel6);
        gridBagConstraints2.gridx = 3;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(0, 0, 5, 5);
        gridBagLayout2.setConstraints(jLabel7, gridBagConstraints2);
        jPanel2.add(jLabel7);
        JPanel jPanel3 = new JPanel();
        GridBagLayout gridBagLayout3 = new GridBagLayout();
        jPanel3.setLayout(gridBagLayout3);
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.anchor = 18;
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagLayout3.setConstraints(jPanel, gridBagConstraints3);
        jPanel3.add(jPanel);
        gridBagConstraints3.weighty = 0.0;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 1;
        gridBagLayout3.setConstraints(jPanel2, gridBagConstraints3);
        jPanel3.add(jPanel2);
        return jPanel3;
    }

    private JPanel createForwardingClientStatePanel() {
        JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsRoutingClientStatusClientRunningLabel"));
        JLabel jLabel2 = new JLabel(JAPMessages.getString("settingsRoutingClientStatusConnectedViaLabel"));
        final JLabel jLabel3 = new JLabel();
        Observer observer = new Observer(){

            public void update(Observable observable, Object object) {
                try {
                    if (observable == JAPModel.getInstance().getRoutingSettings() && ((JAPRoutingMessage)object).getMessageCode() == 1 && JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 1) {
                        IAddress iAddress = JAPModel.getInstance().getRoutingSettings().getForwarderAddress();
                        if (iAddress != null) {
                            AddressParameter[] arraddressParameter = iAddress.getAllParameters();
                            jLabel3.setText(JAPMessages.getString("settingsRoutingClientStatusForwarderInformationLabelPart1") + " " + arraddressParameter[0].getValue() + "    " + JAPMessages.getString("settingsRoutingClientStatusForwarderInformationLabelPart2") + " " + arraddressParameter[1].getValue());
                        } else {
                            jLabel3.setText(JAPMessages.getString("settingsRoutingClientStatusForwarderInformationLabelInvalid"));
                        }
                    }
                    if (observable == JAPConfForwardingState.this.m_messageSystem) {
                        JAPModel.getInstance().getRoutingSettings().deleteObserver(this);
                        JAPConfForwardingState.this.m_messageSystem.deleteObserver(this);
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                }
            }
        };
        this.m_messageSystem.addObserver(observer);
        JAPModel.getInstance().getRoutingSettings().addObserver(observer);
        observer.update(JAPModel.getInstance().getRoutingSettings(), new JAPRoutingMessage(1));
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsRoutingClientStatusBorder"));
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 10, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel.add(jLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 5, 2, 5);
        gridBagLayout.setConstraints(jLabel2, gridBagConstraints);
        jPanel.add(jLabel2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 15, 5, 5);
        gridBagLayout.setConstraints(jLabel3, gridBagConstraints);
        jPanel.add(jLabel3);
        return jPanel;
    }

    private JPanel createForwardingDisabledStatePanel() {
        JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsRoutingDisabledStatusNothingRunningLabel"));
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsRoutingDisabledStatusBorder"));
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel.add(jLabel);
        return jPanel;
    }

    private JPanel createForwardingStatePanel() {
        JPanel jPanel = new JPanel();
        final JPanel jPanel2 = this.createForwardingServerStatePanel();
        final JPanel jPanel3 = this.createForwardingClientStatePanel();
        final JPanel jPanel4 = this.createForwardingDisabledStatePanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagLayout.setConstraints(jPanel2, gridBagConstraints);
        jPanel.add(jPanel2);
        gridBagLayout.setConstraints(jPanel3, gridBagConstraints);
        jPanel.add(jPanel3);
        gridBagLayout.setConstraints(jPanel4, gridBagConstraints);
        jPanel.add(jPanel4);
        jPanel.setPreferredSize(jPanel.getPreferredSize());
        Observer observer = new Observer(){

            public void update(Observable observable, Object object) {
                try {
                    if (observable == JAPModel.getInstance().getRoutingSettings() && ((JAPRoutingMessage)object).getMessageCode() == 1) {
                        int n = JAPModel.getInstance().getRoutingSettings().getRoutingMode();
                        if (n == 1) {
                            jPanel2.setVisible(false);
                            jPanel4.setVisible(false);
                            jPanel3.setVisible(true);
                        }
                        if (n == 2) {
                            jPanel3.setVisible(false);
                            jPanel4.setVisible(false);
                            jPanel2.setVisible(true);
                        }
                        if (n == 0) {
                            jPanel2.setVisible(false);
                            jPanel3.setVisible(false);
                            jPanel4.setVisible(true);
                        }
                    }
                    if (observable == JAPConfForwardingState.this.m_messageSystem) {
                        JAPModel.getInstance().getRoutingSettings().deleteObserver(this);
                        JAPConfForwardingState.this.m_messageSystem.deleteObserver(this);
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                }
            }
        };
        this.m_messageSystem.addObserver(observer);
        JAPModel.getInstance().getRoutingSettings().addObserver(observer);
        observer.update(JAPModel.getInstance().getRoutingSettings(), new JAPRoutingMessage(1));
        return jPanel;
    }
}

