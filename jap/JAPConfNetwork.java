/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.ListenerInterface;
import anon.infoservice.ProxyInterface;
import anon.util.JAPMessages;
import anon.util.StoredPasswordReader;
import gui.JAPHtmlMultiLineLabel;
import gui.JAPJIntField;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.JAPConf;
import jap.JAPConfNetworkSavePoint;
import jap.JAPController;
import jap.JAPModel;
import jap.forward.JAPRoutingForwardingModeSelector;
import jap.forward.JAPRoutingMessage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import logging.LogHolder;
import logging.LogType;

public class JAPConfNetwork
extends AbstractJAPConfModule {
    private static final String MSG_LISTENER_CHANGED = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + "_listenerChanged";
    private static final String MSG_ACCESS_TO_JAP = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + "_accessToJAP";
    public static final String MSG_TITLE_ANTI_CENSORSHIP = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + ".titleAntiCensorship";
    public static final String MSG_SLOW_ANTI_CENSORSHIP = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + "_slowAntiCensorship";
    private static final String MSG_SLOW_ANTI_CENSORSHIP_NOT_NEEDED = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + ".slowAntiCensorshipNotNeeded";
    private static final String MSG_SLOW_ANTI_CENSORSHIP_Q = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + "_slowAntiCensorshipQuestion";
    private static final String MSG_Q_REALLY_LISTEN_ON_ALL = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + ".qReallyListenOnAll";
    private static final String MSG_CONFIRM_DANGERS = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + ".confirmDangers";
    private static final String MSG_AUTH_PASSWORD = (class$jap$JAPConfNetwork == null ? (class$jap$JAPConfNetwork = JAPConfNetwork.class$("jap.JAPConfNetwork")) : class$jap$JAPConfNetwork).getName() + "_password";
    private JAPJIntField m_tfListenerPortNumber;
    private JCheckBox m_cbListenerIsLocal;
    private JLabel m_labelPortnumber1;
    private TitledBorder m_borderSettingsListener;
    private JCheckBox m_cbProxy;
    private JCheckBox m_settingsForwardingClientConfigNeedForwarderBox;
    private JComboBox m_comboForwardingType;
    private JAPJIntField m_tfProxyPortNumber;
    private JTextField m_tfProxyHost;
    private JComboBox m_comboProxyType;
    private JCheckBox m_cbProxyAuthentication;
    private JTextField m_tfProxyAuthenticationUserID;
    private JPasswordField m_tfProxyAuthenticationPassword;
    private JLabel m_labelProxyHost;
    private JLabel m_labelProxyPort;
    private JLabel m_labelProxyType;
    private JLabel m_labelProxyAuthUserID;
    static /* synthetic */ Class class$jap$JAPConfNetwork;

    public JAPConfNetwork() {
        super(new JAPConfNetworkSavePoint());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                Observer observer = new Observer(){

                    public void update(Observable observable, Object object) {
                        try {
                            if (observable == JAPModel.getInstance().getRoutingSettings()) {
                                if (((JAPRoutingMessage)object).getMessageCode() == 16) {
                                    if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                                        JAPConfNetwork.this.m_settingsForwardingClientConfigNeedForwarderBox.setSelected(true);
                                    } else {
                                        JAPConfNetwork.this.m_settingsForwardingClientConfigNeedForwarderBox.setSelected(false);
                                    }
                                }
                            } else if (observable == JAPModel.getInstance() && object.equals(JAPModel.CHANGED_PROXY)) {
                                JAPConfNetwork.this.updateValues(false);
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().addObserver(observer);
                JAPModel.getInstance().addObserver(observer);
                observer.update(JAPModel.getInstance().getRoutingSettings(), new JAPRoutingMessage(16));
                return true;
            }
        }
        return false;
    }

    public void onResetToDefaultsPressed() {
        this.m_tfListenerPortNumber.setInt(4001);
        this.m_cbListenerIsLocal.setSelected(true);
        this.m_comboForwardingType.setSelectedIndex(0);
        this.m_cbProxy.setSelected(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        JAPConfNetwork jAPConfNetwork = this;
        synchronized (jAPConfNetwork) {
            jPanel.removeAll();
            JPanel jPanel2 = this.createForwardingClientConfigPanel();
            GridBagLayout gridBagLayout = new GridBagLayout();
            jPanel.setLayout(gridBagLayout);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = 18;
            gridBagConstraints.fill = 1;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.0;
            gridBagLayout.setConstraints(jPanel2, gridBagConstraints);
            jPanel.add(jPanel2);
            gridBagConstraints.fill = 2;
            gridBagConstraints.weighty = 0.0;
            ++gridBagConstraints.gridy;
            jPanel.add((Component)this.buildPortPanel(), gridBagConstraints);
            ++gridBagConstraints.gridy;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.fill = 1;
            jPanel.add((Component)this.buildProxyPanel(), gridBagConstraints);
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("ngTreeNetwork");
    }

    private JPanel createForwardingClientConfigPanel() {
        JPanel jPanel = new JPanel();
        this.m_settingsForwardingClientConfigNeedForwarderBox = new JCheckBox(JAPMessages.getString("settingsForwardingClientConfigNeedForwarderBox"));
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsForwardingClientConfigBorder"));
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(this.m_settingsForwardingClientConfigNeedForwarderBox, gridBagConstraints);
        jPanel.add(this.m_settingsForwardingClientConfigNeedForwarderBox);
        this.m_comboForwardingType = new JComboBox(JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector().getForwardingModes());
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)this.m_comboForwardingType, gridBagConstraints);
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(0, 20, 5, 5);
        return jPanel;
    }

    private void showForwardingClientConfirmServerShutdownDialog(Component component) {
        final JAPDialog jAPDialog = new JAPDialog(component, JAPMessages.getString("settingsForwardingClientConfigConfirmServerShutdownDialogTitle"));
        jAPDialog.setDefaultCloseOperation(0);
        JPanel jPanel = new JPanel();
        jAPDialog.getContentPane().add(jPanel);
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel(JAPMessages.getString("settingsForwardingClientConfigConfirmServerShutdownLabel"));
        JButton jButton = new JButton(JAPMessages.getString("settingsForwardingClientConfigConfirmServerShutdownShutdownButton"));
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPModel.getInstance().getRoutingSettings().setRoutingMode(0);
                JAPModel.getInstance().getRoutingSettings().setConnectViaForwarder(true);
                jAPDialog.dispose();
            }
        });
        JButton jButton2 = new JButton(JAPMessages.getString("cancelButton"));
        jButton2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                jAPDialog.dispose();
            }
        });
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 11;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 5, 20, 5);
        gridBagLayout.setConstraints(jAPHtmlMultiLineLabel, gridBagConstraints);
        jPanel.add(jAPHtmlMultiLineLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 5, 15, 5);
        gridBagLayout.setConstraints(jButton, gridBagConstraints);
        jPanel.add(jButton);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 5, 15, 5);
        gridBagLayout.setConstraints(jButton2, gridBagConstraints);
        jPanel.add(jButton2);
        jAPDialog.pack();
        jAPDialog.setVisible(true);
    }

    protected boolean onOkPressed() {
        int n;
        String string = null;
        try {
            n = Integer.parseInt(this.m_tfListenerPortNumber.getText().trim());
        }
        catch (Exception exception) {
            n = -1;
        }
        if (!ListenerInterface.isValidPort(n)) {
            JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("errorListenerPortWrong"));
            return false;
        }
        if (this.m_cbProxy.isSelected()) {
            string = this.m_tfProxyHost.getText().trim();
            if (string == null || string.equals("")) {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("errorFirewallHostNotNull"));
                JAPConf.getInstance().selectCard("NETWORK_TAB", null);
                return false;
            }
            try {
                n = Integer.parseInt(this.m_tfProxyPortNumber.getText().trim());
            }
            catch (Exception exception) {
                n = -1;
            }
            if (!ListenerInterface.isValidPort(n)) {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("errorFirewallServicePortWrong"));
                JAPConf.getInstance().selectCard("NETWORK_TAB", null);
                return false;
            }
            if (this.m_cbProxyAuthentication.isSelected() && ((string = this.m_tfProxyAuthenticationUserID.getText().trim()) == null || string.equals(""))) {
                JAPDialog.showErrorDialog((Component)this.getRootPanel(), JAPMessages.getString("errorFirewallAuthUserIDNotNull"));
                JAPConf.getInstance().selectCard("NETWORK_TAB", null);
                return false;
            }
        }
        if (this.m_settingsForwardingClientConfigNeedForwarderBox.isSelected()) {
            if (!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                if (JAPController.getInstance().isAnonConnected()) {
                    this.m_settingsForwardingClientConfigNeedForwarderBox.setSelected(false);
                    JAPDialog.showMessageDialog((Component)this.getRootPanel(), JAPMessages.getString(MSG_SLOW_ANTI_CENSORSHIP) + "<br><br>" + JAPMessages.getString(MSG_SLOW_ANTI_CENSORSHIP_NOT_NEEDED), JAPMessages.getString(MSG_TITLE_ANTI_CENSORSHIP));
                    return false;
                }
                if (JAPDialog.showYesNoDialog((Component)this.getRootPanel(), JAPMessages.getString(MSG_SLOW_ANTI_CENSORSHIP) + "<br><br>" + JAPMessages.getString(MSG_SLOW_ANTI_CENSORSHIP_Q), JAPMessages.getString(MSG_TITLE_ANTI_CENSORSHIP))) {
                    if (JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2) {
                        this.showForwardingClientConfirmServerShutdownDialog(this.getRootPanel());
                        if (!JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
                            this.m_settingsForwardingClientConfigNeedForwarderBox.setSelected(false);
                            return false;
                        }
                    } else {
                        JAPModel.getInstance().getRoutingSettings().setConnectViaForwarder(true);
                    }
                    JAPController.getInstance().stopAnonModeWait();
                    if (!JAPController.getInstance().isAnonConnected() && JAPModel.isAutoConnect()) {
                        JAPController.getInstance().start();
                    }
                } else {
                    this.m_settingsForwardingClientConfigNeedForwarderBox.setSelected(false);
                }
            }
        } else if (JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder()) {
            JAPModel.getInstance().getRoutingSettings().setConnectViaForwarder(false);
            JAPController.getInstance().stopAnonModeWait();
            if (!JAPController.getInstance().isAnonConnected() && JAPModel.isAutoConnect()) {
                JAPController.getInstance().start();
            }
        }
        if (this.m_comboForwardingType.getSelectedItem() != JAPModel.getInstance().getRoutingSettings().getTransportMode()) {
            JAPModel.getInstance().getRoutingSettings().setTransportMode((JAPRoutingForwardingModeSelector.TransportMode)this.m_comboForwardingType.getSelectedItem());
        }
        if (JAPModel.getHttpListenerPortNumber() != this.m_tfListenerPortNumber.getInt()) {
            JAPConf.getInstance().addNeedRestart(new JAPConf.AbstractRestartNeedingConfigChange(){

                public String getName() {
                    return JAPMessages.getString("confListenerTab");
                }

                public String getMessage() {
                    return JAPMessages.getString(MSG_LISTENER_CHANGED);
                }

                public void doChange() {
                    JAPModel.getInstance().setHttpListenerPortNumber(JAPConfNetwork.this.m_tfListenerPortNumber.getInt());
                }
            });
        }
        if (JAPModel.isHttpListenerLocal() != this.m_cbListenerIsLocal.isSelected()) {
            JAPConf.getInstance().addNeedRestart(new JAPConf.AbstractRestartNeedingConfigChange(){

                public String getName() {
                    return JAPMessages.getString(JAPMessages.getString(MSG_ACCESS_TO_JAP));
                }

                public void doChange() {
                    JAPModel.getInstance().setHttpListenerIsLocal(JAPConfNetwork.this.m_cbListenerIsLocal.isSelected());
                }
            });
        }
        int n2 = -1;
        try {
            n2 = Integer.parseInt(this.m_tfProxyPortNumber.getText().trim());
        }
        catch (Exception exception) {
            // empty catch block
        }
        int n3 = 1;
        if (this.m_comboProxyType.getSelectedIndex() == 1) {
            n3 = 3;
        }
        char[] arrc = this.m_tfProxyAuthenticationPassword.getPassword();
        ProxyInterface proxyInterface = new ProxyInterface(this.m_tfProxyHost.getText().trim(), n2, n3, this.m_tfProxyAuthenticationUserID.getText().trim(), arrc.length > 0 ? new StoredPasswordReader(arrc) : JAPController.getInstance().getPasswordReader(), this.m_cbProxyAuthentication.isSelected(), this.m_cbProxy.isSelected());
        JAPController.getInstance().changeProxyInterface(proxyInterface, this.m_cbProxyAuthentication.isSelected(), this.getRootPanel());
        return true;
    }

    protected void onUpdateValues() {
        this.m_comboForwardingType.setSelectedItem(JAPModel.getInstance().getRoutingSettings().getTransportMode());
        this.m_tfListenerPortNumber.setInt(JAPModel.getHttpListenerPortNumber());
        this.m_cbListenerIsLocal.setSelected(JAPModel.isHttpListenerLocal());
        ProxyInterface proxyInterface = JAPModel.getInstance().getProxyInterface();
        boolean bl = proxyInterface != null && proxyInterface.isValid();
        this.m_cbProxy.setSelected(bl);
        this.m_tfProxyHost.setEnabled(bl);
        this.m_tfProxyPortNumber.setEnabled(bl);
        this.m_comboProxyType.setEnabled(bl);
        this.m_tfProxyAuthenticationUserID.setEnabled(bl);
        this.m_tfProxyAuthenticationPassword.setEnabled(bl);
        this.m_labelProxyHost.setEnabled(bl);
        this.m_labelProxyPort.setEnabled(bl);
        this.m_labelProxyType.setEnabled(bl);
        if (proxyInterface == null || proxyInterface.getProtocol() == 1) {
            this.m_comboProxyType.setSelectedIndex(0);
        } else {
            this.m_comboProxyType.setSelectedIndex(1);
        }
        this.m_cbProxyAuthentication.setEnabled(bl);
        if (proxyInterface != null) {
            this.m_tfProxyHost.setText(proxyInterface.getHost());
            this.m_tfProxyPortNumber.setText(String.valueOf(proxyInterface.getPort()));
            this.m_tfProxyAuthenticationUserID.setText(proxyInterface.getAuthenticationUserID());
            if (proxyInterface.isAuthenticationPasswordSaveable()) {
                this.m_tfProxyAuthenticationPassword.setText(proxyInterface.getAuthenticationPassword());
            } else {
                this.m_tfProxyAuthenticationPassword.setText("");
            }
            this.m_cbProxyAuthentication.setSelected(proxyInterface.isAuthenticationUsed());
        }
        this.m_labelProxyAuthUserID.setEnabled(this.m_cbProxyAuthentication.isSelected() & bl);
        this.m_tfProxyAuthenticationUserID.setEnabled(this.m_labelProxyAuthUserID.isEnabled());
        this.m_tfProxyAuthenticationPassword.setEnabled(this.m_labelProxyAuthUserID.isEnabled());
        if (this.m_tfProxyPortNumber.getText().trim().equalsIgnoreCase("-1")) {
            this.m_tfProxyPortNumber.setText("");
        }
    }

    JPanel buildPortPanel() {
        Insets insets;
        this.m_labelPortnumber1 = new JLabel(JAPMessages.getString("settingsPort"));
        this.m_tfListenerPortNumber = new JAPJIntField(65535);
        this.m_cbListenerIsLocal = new JCheckBox(JAPMessages.getString("settingsListenerCheckBox"));
        this.m_cbListenerIsLocal.setForeground(this.m_labelPortnumber1.getForeground());
        this.m_cbListenerIsLocal.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (!JAPConfNetwork.this.m_cbListenerIsLocal.isSelected()) {
                    JAPDialog.LinkedCheckBox linkedCheckBox = new JAPDialog.LinkedCheckBox(JAPMessages.getString(MSG_CONFIRM_DANGERS), false, JAPConfNetwork.this.getHelpContext());
                    JAPDialog.showWarningDialog((Component)JAPConfNetwork.this.getRootPanel(), JAPMessages.getString(MSG_Q_REALLY_LISTEN_ON_ALL), (JAPDialog.ILinkedInformation)linkedCheckBox);
                    if (!linkedCheckBox.getState()) {
                        JAPConfNetwork.this.m_cbListenerIsLocal.setSelected(true);
                    }
                }
            }
        });
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        this.m_borderSettingsListener = new TitledBorder(JAPMessages.getString("settingsListenerBorder"));
        jPanel.setBorder(this.m_borderSettingsListener);
        JPanel jPanel2 = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel2.setLayout(gridBagLayout);
        jPanel2.setBorder(new EmptyBorder(5, 10, 10, 10));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 0;
        gridBagLayout.setConstraints(this.m_tfListenerPortNumber, gridBagConstraints);
        jPanel2.add(this.m_tfListenerPortNumber);
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(this.m_labelPortnumber1, gridBagConstraints);
        jPanel2.add(this.m_labelPortnumber1);
        gridBagConstraints.insets = insets;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        gridBagLayout.setConstraints(this.m_cbListenerIsLocal, gridBagConstraints);
        if (JAPModel.getDefaultView() != 2) {
            jPanel2.add(this.m_cbListenerIsLocal);
        }
        jPanel.add((Component)jPanel2, "North");
        return jPanel;
    }

    JPanel buildProxyPanel() {
        this.m_cbProxy = new JCheckBox(JAPMessages.getString("settingsProxyCheckBox"));
        this.m_comboProxyType = new JComboBox();
        this.m_comboProxyType.addItem(JAPMessages.getString("settingsProxyTypeHTTP"));
        this.m_comboProxyType.addItem(JAPMessages.getString("settingsProxyTypeSOCKS"));
        this.m_tfProxyHost = new JTextField(20);
        this.m_tfProxyPortNumber = new JAPJIntField(65535);
        ProxyInterface proxyInterface = JAPModel.getInstance().getProxyInterface();
        boolean bl = proxyInterface != null && proxyInterface.isValid();
        this.m_tfProxyHost.setEnabled(bl);
        this.m_tfProxyPortNumber.setEnabled(bl);
        this.m_cbProxy.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                boolean bl = JAPConfNetwork.this.m_cbProxy.isSelected();
                JAPConfNetwork.this.m_comboProxyType.setEnabled(bl);
                JAPConfNetwork.this.m_tfProxyHost.setEnabled(bl);
                JAPConfNetwork.this.m_tfProxyPortNumber.setEnabled(bl);
                JAPConfNetwork.this.m_cbProxyAuthentication.setEnabled(bl);
                JAPConfNetwork.this.m_labelProxyHost.setEnabled(bl);
                JAPConfNetwork.this.m_labelProxyPort.setEnabled(bl);
                JAPConfNetwork.this.m_labelProxyType.setEnabled(bl);
                JAPConfNetwork.this.m_labelProxyAuthUserID.setEnabled(JAPConfNetwork.this.m_cbProxyAuthentication.isSelected() & bl);
                JAPConfNetwork.this.m_tfProxyAuthenticationUserID.setEnabled(JAPConfNetwork.this.m_labelProxyAuthUserID.isEnabled());
                JAPConfNetwork.this.m_tfProxyAuthenticationPassword.setEnabled(JAPConfNetwork.this.m_labelProxyAuthUserID.isEnabled());
            }
        });
        this.m_cbProxyAuthentication = new JCheckBox(JAPMessages.getString("settingsProxyAuthenticationCheckBox"));
        this.m_tfProxyAuthenticationUserID = new JTextField(10);
        this.m_tfProxyAuthenticationPassword = new JPasswordField(20);
        this.m_cbProxyAuthentication.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConfNetwork.this.m_tfProxyAuthenticationUserID.setEnabled(JAPConfNetwork.this.m_cbProxyAuthentication.isSelected());
                JAPConfNetwork.this.m_tfProxyAuthenticationPassword.setEnabled(JAPConfNetwork.this.m_cbProxyAuthentication.isSelected());
                JAPConfNetwork.this.m_labelProxyAuthUserID.setEnabled(JAPConfNetwork.this.m_cbProxyAuthentication.isSelected());
            }
        });
        this.m_labelProxyHost = new JLabel(JAPMessages.getString("settingsProxyHost"));
        this.m_labelProxyPort = new JLabel(JAPMessages.getString("settingsProxyPort"));
        this.m_labelProxyType = new JLabel(JAPMessages.getString("settingsProxyType"));
        this.m_labelProxyAuthUserID = new JLabel(JAPMessages.getString("settingsProxyAuthUserID"));
        this.m_cbProxy.setForeground(this.m_labelProxyPort.getForeground());
        this.m_cbProxyAuthentication.setForeground(this.m_labelProxyPort.getForeground());
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsProxyBorder"));
        jPanel.setBorder(titledBorder);
        JPanel jPanel2 = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel2.setLayout(gridBagLayout);
        if (JAPModel.isSmallDisplay()) {
            jPanel2.setBorder(new EmptyBorder(1, 10, 1, 10));
        } else {
            jPanel2.setBorder(new EmptyBorder(5, 10, 10, 10));
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        Insets insets = JAPModel.isSmallDisplay() ? new Insets(0, 0, 1, 0) : new Insets(0, 0, 3, 0);
        gridBagConstraints.insets = insets;
        gridBagLayout.setConstraints(this.m_cbProxy, gridBagConstraints);
        jPanel2.add(this.m_cbProxy);
        gridBagConstraints.gridy = 1;
        gridBagLayout.setConstraints(this.m_labelProxyType, gridBagConstraints);
        gridBagConstraints.gridy = 2;
        jPanel2.add(this.m_labelProxyType);
        gridBagLayout.setConstraints(this.m_comboProxyType, gridBagConstraints);
        gridBagConstraints.gridy = 3;
        jPanel2.add(this.m_comboProxyType);
        gridBagLayout.setConstraints(this.m_labelProxyHost, gridBagConstraints);
        jPanel2.add(this.m_labelProxyHost);
        gridBagConstraints.gridy = 4;
        gridBagLayout.setConstraints(this.m_tfProxyHost, gridBagConstraints);
        jPanel2.add(this.m_tfProxyHost);
        gridBagConstraints.gridy = 5;
        gridBagLayout.setConstraints(this.m_labelProxyPort, gridBagConstraints);
        jPanel2.add(this.m_labelProxyPort);
        gridBagConstraints.gridy = 6;
        gridBagLayout.setConstraints(this.m_tfProxyPortNumber, gridBagConstraints);
        jPanel2.add(this.m_tfProxyPortNumber);
        JSeparator jSeparator = new JSeparator();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = JAPModel.isSmallDisplay() ? new Insets(5, 0, 1, 0) : new Insets(10, 0, 3, 0);
        gridBagLayout.setConstraints(jSeparator, gridBagConstraints);
        jPanel2.add(jSeparator);
        gridBagConstraints.insets = insets;
        gridBagConstraints.gridy = 8;
        gridBagLayout.setConstraints(this.m_cbProxyAuthentication, gridBagConstraints);
        jPanel2.add(this.m_cbProxyAuthentication);
        gridBagConstraints.gridy = 9;
        gridBagLayout.setConstraints(this.m_labelProxyAuthUserID, gridBagConstraints);
        jPanel2.add(this.m_labelProxyAuthUserID);
        gridBagConstraints.gridy = 10;
        gridBagLayout.setConstraints(this.m_tfProxyAuthenticationUserID, gridBagConstraints);
        jPanel2.add(this.m_tfProxyAuthenticationUserID);
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_AUTH_PASSWORD) + ":");
        ++gridBagConstraints.gridy;
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel2.add(jLabel);
        ++gridBagConstraints.gridy;
        gridBagLayout.setConstraints(this.m_tfProxyAuthenticationPassword, gridBagConstraints);
        jPanel2.add(this.m_tfProxyAuthenticationPassword);
        jPanel.add((Component)jPanel2, "North");
        return jPanel;
    }

    public String getHelpContext() {
        return "net";
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

