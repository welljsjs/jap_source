/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.infoservice.Database;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.MixCascade;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.AbstractJAPConfModule;
import jap.JAPController;
import jap.JAPModel;
import jap.forward.JAPConfForwardingServerSavePoint;
import jap.forward.JAPRoutingConnectionClass;
import jap.forward.JAPRoutingForwardingModeSelector;
import jap.forward.JAPRoutingMessage;
import jap.forward.JAPRoutingSettings;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import logging.LogHolder;
import logging.LogType;

public class JAPConfForwardingServer
extends AbstractJAPConfModule {
    private DefaultListModel m_knownCascadesListModel;
    private DefaultListModel m_knownInfoServicesListModel;
    private DefaultListModel m_allowedCascadesListModel;
    private DefaultListModel m_registrationInfoServicesListModel;
    private JCheckBox m_startServerBox;
    private JTextField serverPortField;
    private JTextField uploadBandwidthField;
    private JComboBox connectionClassesComboBox;
    private JLabel settingsForwardingServerConfigCurrentBandwidthLabel;
    private JTextField relativeBandwidthField;
    private JButton increaseRelativeBandwidthButton;
    private JButton decreaseRelativeBandwidthButton;
    private JLabel settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel;
    private JLabel settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel;
    private JList knownCascadesList;
    private JList allowedCascadesList;
    private JButton settingsForwardingServerConfigAllowedCascadesReloadButton;
    private JButton settingsForwardingServerConfigAllowedCascadesAddButton;
    private JButton settingsForwardingServerConfigAllowedCascadesRemoveButton;
    private JCheckBox settingsForwardingServerConfigAllowedCascadesAllowAllBox;
    private JLabel settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel;
    private JLabel settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel;
    private JList knownInfoServicesList;
    private JList registrationInfoServicesList;
    private JButton settingsForwardingServerConfigRegistrationInfoServicesReloadButton;
    private JButton settingsForwardingServerConfigRegistrationInfoServicesAddButton;
    private JButton settingsForwardingServerConfigRegistrationInfoServicesRemoveButton;
    private JCheckBox settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox;
    private JComboBox forwardingModeComboBox;
    private JTextField skypeForwarderAddressField;
    private JLabel settingsRoutingForwardingModeLabel;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    public JAPConfForwardingServer() {
        super(new JAPConfForwardingServerSavePoint());
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
                            if (observable == JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector() && ((JAPRoutingMessage)object).getMessageCode() == 17) {
                                JAPRoutingForwardingModeSelector.TransportMode transportMode = JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector().getCurrentForwardingMode();
                                LogHolder.log(7, LogType.GUI, "Routing mode set to " + transportMode.toString());
                                JAPConfForwardingServer.this.forwardingModeComboBox.setSelectedItem(transportMode);
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector().addObserver(observer);
                observer.update(JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector(), new JAPRoutingMessage(17));
                Observer observer2 = new Observer(){

                    public void update(Observable observable, Object object) {
                        try {
                            if (((JAPRoutingMessage)object).getMessageCode() == 18) {
                                JAPConfForwardingServer.this.skypeForwarderAddressField.setText(JAPModel.getInstance().getRoutingSettings().getSkypeForwarderAddress());
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().addObserver(observer2);
                observer2.update(JAPModel.getInstance().getRoutingSettings(), new JAPRoutingMessage(18));
                Observer observer3 = new Observer(){

                    public void update(Observable observable, Object object) {
                        try {
                            if (observable == JAPModel.getInstance().getRoutingSettings() && ((JAPRoutingMessage)object).getMessageCode() == 15) {
                                JTextField jTextField = JAPConfForwardingServer.this.serverPortField;
                                JAPModel.getInstance().getRoutingSettings();
                                jTextField.setText(Integer.toString(JAPRoutingSettings.getServerPort()));
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().addObserver(observer3);
                observer3.update(JAPModel.getInstance().getRoutingSettings(), new JAPRoutingMessage(15));
                Observer observer4 = new Observer(){

                    public void update(Observable observable, Object object) {
                        try {
                            if (observable == JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector() && ((JAPRoutingMessage)object).getMessageCode() == 6) {
                                JAPRoutingConnectionClass jAPRoutingConnectionClass = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getCurrentConnectionClass();
                                JAPConfForwardingServer.this.connectionClassesComboBox.setSelectedItem(jAPRoutingConnectionClass);
                                if (jAPRoutingConnectionClass.getIdentifier() == 8) {
                                    JAPConfForwardingServer.this.uploadBandwidthField.setEnabled(true);
                                } else {
                                    JAPConfForwardingServer.this.uploadBandwidthField.setEnabled(false);
                                }
                                JAPConfForwardingServer.this.uploadBandwidthField.setText(Integer.toString(jAPRoutingConnectionClass.getMaximumBandwidth() * 8 / 1000));
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().addObserver(observer4);
                observer4.update(JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector(), new JAPRoutingMessage(6));
                Observer observer5 = new Observer(){

                    public void update(Observable observable, Object object) {
                        try {
                            if (observable == JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector()) {
                                if (((JAPRoutingMessage)object).getMessageCode() == 6 || ((JAPRoutingMessage)object).getMessageCode() == 7) {
                                    JAPRoutingConnectionClass jAPRoutingConnectionClass = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getCurrentConnectionClass();
                                    JAPConfForwardingServer.this.relativeBandwidthField.setText(Integer.toString(jAPRoutingConnectionClass.getRelativeBandwidth()) + "%");
                                    if (jAPRoutingConnectionClass.getRelativeBandwidth() < 100) {
                                        JAPConfForwardingServer.this.increaseRelativeBandwidthButton.setEnabled(true);
                                    } else {
                                        JAPConfForwardingServer.this.increaseRelativeBandwidthButton.setEnabled(false);
                                    }
                                    if (jAPRoutingConnectionClass.getRelativeBandwidth() > (jAPRoutingConnectionClass.getMinimumRelativeBandwidth() + 9) / 10 * 10) {
                                        JAPConfForwardingServer.this.decreaseRelativeBandwidthButton.setEnabled(true);
                                    } else {
                                        JAPConfForwardingServer.this.decreaseRelativeBandwidthButton.setEnabled(false);
                                    }
                                }
                                if (((JAPRoutingMessage)object).getMessageCode() == 7) {
                                    JAPConfForwardingServer.this.settingsForwardingServerConfigCurrentBandwidthLabel.setText(JAPMessages.getString("settingsForwardingServerConfigCurrentBandwidthLabelPart1") + " " + Integer.toString(JAPModel.getInstance().getRoutingSettings().getBandwidth() * 8 / 1000) + " " + JAPMessages.getString("settingsForwardingServerConfigCurrentBandwidthLabelPart2") + " " + Integer.toString(JAPModel.getInstance().getRoutingSettings().getAllowedConnections()) + " " + JAPMessages.getString("settingsForwardingServerConfigCurrentBandwidthLabelPart3"));
                                }
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().addObserver(observer5);
                observer5.update(JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector(), new JAPRoutingMessage(7));
                Observer observer6 = new Observer(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void update(Observable observable, Object object) {
                        block9: {
                            try {
                                if (observable != JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore()) break block9;
                                int n = ((JAPRoutingMessage)object).getMessageCode();
                                if (n == 9) {
                                    if (JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().getAllowAllAvailableMixCascades()) {
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel.setEnabled(false);
                                        JAPConfForwardingServer.this.knownCascadesList.setEnabled(false);
                                        JAPConfForwardingServer.this.allowedCascadesList.setEnabled(false);
                                        JAPConfForwardingServer.this.knownCascadesList.setModel(new DefaultListModel());
                                        JAPConfForwardingServer.this.allowedCascadesList.setModel(new DefaultListModel());
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesReloadButton.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAddButton.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesRemoveButton.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAllowAllBox.setSelected(true);
                                    } else {
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel.setEnabled(true);
                                        JAPConfForwardingServer.this.knownCascadesList.setModel(JAPConfForwardingServer.this.m_knownCascadesListModel);
                                        JAPConfForwardingServer.this.allowedCascadesList.setModel(JAPConfForwardingServer.this.m_allowedCascadesListModel);
                                        JAPConfForwardingServer.this.knownCascadesList.setEnabled(true);
                                        JAPConfForwardingServer.this.allowedCascadesList.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesReloadButton.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAddButton.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesRemoveButton.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAllowAllBox.setSelected(false);
                                    }
                                }
                                if (n != 10) break block9;
                                DefaultListModel defaultListModel = JAPConfForwardingServer.this.m_allowedCascadesListModel;
                                synchronized (defaultListModel) {
                                    JAPConfForwardingServer.this.m_allowedCascadesListModel.clear();
                                    Enumeration enumeration = JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().getAllowedMixCascades().elements();
                                    while (enumeration.hasMoreElements()) {
                                        JAPConfForwardingServer.this.m_allowedCascadesListModel.addElement(enumeration.nextElement());
                                    }
                                }
                            }
                            catch (Exception exception) {
                                LogHolder.log(2, LogType.GUI, exception);
                            }
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().addObserver(observer6);
                observer6.update(JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore(), new JAPRoutingMessage(10));
                observer6.update(JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore(), new JAPRoutingMessage(9));
                Observer observer7 = new Observer(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void update(Observable observable, Object object) {
                        block9: {
                            try {
                                if (observable != JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore()) break block9;
                                int n = ((JAPRoutingMessage)object).getMessageCode();
                                if (n == 11) {
                                    if (JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().getRegisterAtAllAvailableInfoServices()) {
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel.setEnabled(false);
                                        JAPConfForwardingServer.this.knownInfoServicesList.setEnabled(false);
                                        JAPConfForwardingServer.this.registrationInfoServicesList.setEnabled(false);
                                        JAPConfForwardingServer.this.knownInfoServicesList.setModel(new DefaultListModel());
                                        JAPConfForwardingServer.this.registrationInfoServicesList.setModel(new DefaultListModel());
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesReloadButton.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesAddButton.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesRemoveButton.setEnabled(false);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox.setSelected(true);
                                    } else {
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel.setEnabled(true);
                                        JAPConfForwardingServer.this.knownInfoServicesList.setModel(JAPConfForwardingServer.this.m_knownInfoServicesListModel);
                                        JAPConfForwardingServer.this.registrationInfoServicesList.setModel(JAPConfForwardingServer.this.m_registrationInfoServicesListModel);
                                        JAPConfForwardingServer.this.knownInfoServicesList.setEnabled(true);
                                        JAPConfForwardingServer.this.registrationInfoServicesList.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesReloadButton.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesAddButton.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesRemoveButton.setEnabled(true);
                                        JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox.setSelected(false);
                                    }
                                }
                                if (n != 12) break block9;
                                DefaultListModel defaultListModel = JAPConfForwardingServer.this.m_registrationInfoServicesListModel;
                                synchronized (defaultListModel) {
                                    JAPConfForwardingServer.this.m_registrationInfoServicesListModel.clear();
                                    Enumeration enumeration = JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().getRegistrationInfoServices().elements();
                                    while (enumeration.hasMoreElements()) {
                                        JAPConfForwardingServer.this.m_registrationInfoServicesListModel.addElement(enumeration.nextElement());
                                    }
                                }
                            }
                            catch (Exception exception) {
                                LogHolder.log(2, LogType.GUI, exception);
                            }
                        }
                    }
                };
                JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().addObserver(observer7);
                observer7.update(JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore(), new JAPRoutingMessage(12));
                observer7.update(JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore(), new JAPRoutingMessage(11));
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        JAPConfForwardingServer jAPConfForwardingServer = this;
        synchronized (jAPConfForwardingServer) {
            jPanel.removeAll();
            JPanel jPanel2 = this.createForwardingServerConfigPanel();
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
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("confTreeForwardingServerLeaf");
    }

    private JPanel createForwardingServerConfigPanel() {
        final JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsForwardingServerConfigPortLabel"));
        JLabel jLabel2 = new JLabel(JAPMessages.getString("settingsForwardingServerConfigSkypeAddressLabel"));
        this.skypeForwarderAddressField = new JTextField(){
            private static final long serialVersionUID = 1L;

            protected Document createDefaultModel() {
                return new PlainDocument(){
                    private static final long serialVersionUID = 1L;

                    public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
                        try {
                            super.insertString(n, string, attributeSet);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                };
            }
        };
        this.skypeForwarderAddressField.addFocusListener(new FocusAdapter(){

            public void focusLost(FocusEvent focusEvent) {
                try {
                    String string = JAPConfForwardingServer.this.skypeForwarderAddressField.getText();
                    JAPModel.getInstance().getRoutingSettings().setSkypeForwarderAddress(string);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        });
        this.serverPortField = new JTextField(5){
            private static final long serialVersionUID = 1L;

            protected Document createDefaultModel() {
                return new PlainDocument(){
                    private static final long serialVersionUID = 1L;

                    public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
                        try {
                            int n2 = Integer.parseInt(this.getText(0, this.getLength()) + string);
                            if (n2 >= 1 && n2 <= 65535) {
                                super.insertString(n, string, attributeSet);
                            }
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                };
            }
        };
        this.serverPortField.addFocusListener(new FocusAdapter(){

            public void focusLost(FocusEvent focusEvent) {
                try {
                    int n = Integer.parseInt(JAPConfForwardingServer.this.serverPortField.getText());
                    if (!JAPModel.getInstance().getRoutingSettings().setServerPort(n)) {
                        throw new Exception("Error while changing server port.");
                    }
                }
                catch (Exception exception) {
                    JAPDialog.showErrorDialog((Component)jPanel, JAPMessages.getString("settingsForwardingServerConfigChangeServerPortError"));
                    JTextField jTextField = JAPConfForwardingServer.this.serverPortField;
                    JAPModel.getInstance().getRoutingSettings();
                    jTextField.setText(Integer.toString(JAPRoutingSettings.getServerPort()));
                }
            }
        });
        JLabel jLabel3 = new JLabel(JAPMessages.getString("settingsRoutingForwardingModeLabel"));
        this.forwardingModeComboBox = new JComboBox(JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector().getForwardingModes());
        this.forwardingModeComboBox.setEditable(false);
        this.forwardingModeComboBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                LogHolder.log(7, LogType.GUI, "Action performed from forwardingModecomboBox");
                JAPModel.getInstance().getRoutingSettings().getForwardingModeSelector().setCurrentForwardingMode(((JAPRoutingForwardingModeSelector.TransportMode)JAPConfForwardingServer.this.forwardingModeComboBox.getSelectedItem()).getIdentifier());
            }
        });
        JLabel jLabel4 = new JLabel(JAPMessages.getString("settingsForwardingServerConfigMyConnectionLabel"));
        this.connectionClassesComboBox = new JComboBox(JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getConnectionClasses());
        this.connectionClassesComboBox.setEditable(false);
        this.connectionClassesComboBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().setCurrentConnectionClass(((JAPRoutingConnectionClass)JAPConfForwardingServer.this.connectionClassesComboBox.getSelectedItem()).getIdentifier());
            }
        });
        JLabel jLabel5 = new JLabel(JAPMessages.getString("settingsForwardingServerConfigMaxUploadBandwidthLabel"));
        this.uploadBandwidthField = new JTextField(){
            private static final long serialVersionUID = 1L;

            protected Document createDefaultModel() {
                return new PlainDocument(){
                    private static final long serialVersionUID = 1L;

                    public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
                        try {
                            int n2 = Integer.parseInt(this.getText(0, this.getLength()) + string);
                            if (n2 >= 1) {
                                super.insertString(n, string, attributeSet);
                            }
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                };
            }
        };
        this.uploadBandwidthField.addFocusListener(new FocusAdapter(){

            public void focusLost(FocusEvent focusEvent) {
                try {
                    int n = Integer.parseInt(JAPConfForwardingServer.this.uploadBandwidthField.getText()) * 1000 / 8;
                    if (n < 4000) {
                        throw new Exception("JAPConfForwardingServer: Error while changing maximum upload bandwidth.");
                    }
                    JAPRoutingConnectionClass jAPRoutingConnectionClass = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getCurrentConnectionClass();
                    jAPRoutingConnectionClass.setMaximumBandwidth(n);
                    JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().setCurrentConnectionClass(jAPRoutingConnectionClass.getIdentifier());
                }
                catch (Exception exception) {
                    JAPDialog.showErrorDialog((Component)jPanel, JAPMessages.getString("settingsForwardingServerConfigChangeMaximumUploadBandwidthErrorPart1") + " " + Integer.toString(32) + " " + JAPMessages.getString("settingsForwardingServerConfigChangeMaximumUploadBandwidthErrorPart2"));
                    JAPConfForwardingServer.this.uploadBandwidthField.setText(Integer.toString(JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getCurrentConnectionClass().getMaximumBandwidth() * 8 / 1000));
                }
            }
        });
        this.uploadBandwidthField.setColumns(7);
        JLabel jLabel6 = new JLabel(JAPMessages.getString("settingsForwardingServerConfigForwardingPercentageLabel"));
        this.relativeBandwidthField = new JTextField();
        this.relativeBandwidthField.setColumns(4);
        this.relativeBandwidthField.setHorizontalAlignment(4);
        this.relativeBandwidthField.setDisabledTextColor(this.relativeBandwidthField.getForeground());
        this.relativeBandwidthField.setEnabled(false);
        this.increaseRelativeBandwidthButton = new JButton(GUIUtils.loadImageIcon("arrowUp.gif", true));
        this.increaseRelativeBandwidthButton.setBorder(new EmptyBorder(0, 1, 0, 1));
        this.increaseRelativeBandwidthButton.setFocusPainted(false);
        this.increaseRelativeBandwidthButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPRoutingConnectionClass jAPRoutingConnectionClass = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getCurrentConnectionClass();
                int n = Math.min(100, ((jAPRoutingConnectionClass.getRelativeBandwidth() + 9) / 10 + 1) * 10);
                jAPRoutingConnectionClass.setRelativeBandwidth(n);
                JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().setCurrentConnectionClass(jAPRoutingConnectionClass.getIdentifier());
            }
        });
        this.decreaseRelativeBandwidthButton = new JButton(GUIUtils.loadImageIcon("arrowDown.gif", true));
        this.decreaseRelativeBandwidthButton.setBorder(new EmptyBorder(0, 1, 0, 1));
        this.decreaseRelativeBandwidthButton.setFocusPainted(false);
        this.decreaseRelativeBandwidthButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPRoutingConnectionClass jAPRoutingConnectionClass = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getCurrentConnectionClass();
                int n = (Math.max(jAPRoutingConnectionClass.getMinimumRelativeBandwidth(), jAPRoutingConnectionClass.getRelativeBandwidth() - 10) + 9) / 10 * 10;
                jAPRoutingConnectionClass.setRelativeBandwidth(n);
                JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().setCurrentConnectionClass(jAPRoutingConnectionClass.getIdentifier());
            }
        });
        this.settingsForwardingServerConfigCurrentBandwidthLabel = new JLabel();
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.insertTab(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesTabTitle"), null, this.createForwardingServerConfigAllowedCascadesPanel(), null, 0);
        jTabbedPane.insertTab(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesTabTitle"), null, this.createForwardingServerConfigRegistrationInfoServicesPanel(), null, 1);
        JPanel jPanel2 = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel2.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 17;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 20, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel2.add(jLabel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 20, 5);
        gridBagLayout.setConstraints(this.serverPortField, gridBagConstraints);
        jPanel2.add(this.serverPortField);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 0, 20, 5);
        gridBagLayout.setConstraints(jLabel2, gridBagConstraints);
        jPanel2.add(jLabel2);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new Insets(5, 0, 20, 5);
        gridBagLayout.setConstraints(this.skypeForwarderAddressField, gridBagConstraints);
        jPanel2.add(this.skypeForwarderAddressField);
        this.m_startServerBox = new JCheckBox(JAPMessages.getString("forwardingServerStart"), JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = 12;
        this.m_startServerBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPController.getInstance().enableForwardingServer(JAPConfForwardingServer.this.m_startServerBox.isSelected());
            }
        });
        gridBagConstraints.anchor = 18;
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsForwardingServerConfigBorder"));
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        jPanel.setLayout(gridBagLayout2);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.anchor = 18;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.insets = new Insets(0, 5, 0, 10);
        gridBagLayout2.setConstraints(jLabel3, gridBagConstraints2);
        jPanel.add(jLabel3);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridheight = 1;
        gridBagConstraints2.fill = 3;
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.insets = new Insets(0, 5, 10, 10);
        gridBagLayout2.setConstraints(this.forwardingModeComboBox, gridBagConstraints2);
        jPanel.add(this.forwardingModeComboBox);
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 0.0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.gridwidth = 4;
        gridBagLayout2.setConstraints(jPanel2, gridBagConstraints2);
        jPanel.add(jPanel2);
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 3;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.insets = new Insets(0, 5, 0, 10);
        gridBagLayout2.setConstraints(jLabel4, gridBagConstraints2);
        jPanel.add(jLabel4);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 3;
        gridBagConstraints2.insets = new Insets(0, 10, 0, 10);
        gridBagLayout2.setConstraints(jLabel5, gridBagConstraints2);
        jPanel.add(jLabel5);
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.gridy = 3;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(0, 10, 0, 5);
        gridBagLayout2.setConstraints(jLabel6, gridBagConstraints2);
        jPanel.add(jLabel6);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 4;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridheight = 1;
        gridBagConstraints2.fill = 3;
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.insets = new Insets(0, 5, 10, 10);
        gridBagLayout2.setConstraints(this.connectionClassesComboBox, gridBagConstraints2);
        jPanel.add(this.connectionClassesComboBox);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 4;
        gridBagConstraints2.insets = new Insets(0, 10, 10, 10);
        gridBagLayout2.setConstraints(this.uploadBandwidthField, gridBagConstraints2);
        jPanel.add(this.uploadBandwidthField);
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.gridy = 4;
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.insets = new Insets(0, 10, 10, 0);
        gridBagLayout2.setConstraints(this.relativeBandwidthField, gridBagConstraints2);
        jPanel.add(this.relativeBandwidthField);
        JPanel jPanel3 = new JPanel(new GridLayout(2, 1, 0, 0));
        jPanel3.add(this.increaseRelativeBandwidthButton);
        jPanel3.add(this.decreaseRelativeBandwidthButton);
        gridBagConstraints2.gridx = 3;
        gridBagConstraints2.gridy = 4;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 3;
        gridBagConstraints2.anchor = 16;
        gridBagConstraints2.insets = new Insets(0, 0, 10, 5);
        jPanel.add((Component)jPanel3, gridBagConstraints2);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 6;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.gridwidth = 4;
        gridBagConstraints2.insets = new Insets(0, 5, 20, 5);
        gridBagLayout2.setConstraints(this.settingsForwardingServerConfigCurrentBandwidthLabel, gridBagConstraints2);
        jPanel.add(this.settingsForwardingServerConfigCurrentBandwidthLabel);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 7;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.insets = new Insets(0, 5, 5, 5);
        gridBagLayout2.setConstraints(jTabbedPane, gridBagConstraints2);
        jPanel.add(jTabbedPane);
        return jPanel;
    }

    private JPanel createForwardingServerConfigAllowedCascadesPanel() {
        final JPanel jPanel = new JPanel();
        this.settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel = new JLabel(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel"));
        this.settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel = new JLabel(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel"));
        this.m_knownCascadesListModel = new DefaultListModel();
        this.m_knownInfoServicesListModel = new DefaultListModel();
        this.knownCascadesList = new JList(this.m_knownCascadesListModel);
        this.knownCascadesList.setSelectionMode(0);
        JScrollPane jScrollPane = new JScrollPane(this.knownCascadesList);
        jScrollPane.setPreferredSize(new JTextArea(4, 20).getPreferredSize());
        this.m_allowedCascadesListModel = new DefaultListModel();
        this.allowedCascadesList = new JList(this.m_allowedCascadesListModel);
        this.allowedCascadesList.setSelectionMode(0);
        JScrollPane jScrollPane2 = new JScrollPane(this.allowedCascadesList);
        jScrollPane2.setPreferredSize(new JTextArea(4, 20).getPreferredSize());
        this.settingsForwardingServerConfigAllowedCascadesReloadButton = new JButton(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesReloadButton"));
        this.settingsForwardingServerConfigAllowedCascadesReloadButton.addActionListener(new ActionListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultListModel defaultListModel = JAPConfForwardingServer.this.m_knownCascadesListModel;
                synchronized (defaultListModel) {
                    JAPConfForwardingServer.this.m_knownCascadesListModel.clear();
                    Enumeration enumeration = JAPConfForwardingServer.this.showFetchMixCascadesDialog(jPanel).elements();
                    while (enumeration.hasMoreElements()) {
                        JAPConfForwardingServer.this.m_knownCascadesListModel.addElement(enumeration.nextElement());
                    }
                }
            }
        });
        this.settingsForwardingServerConfigAllowedCascadesAddButton = new JButton(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesAddButton"));
        this.settingsForwardingServerConfigAllowedCascadesAddButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                MixCascade mixCascade = (MixCascade)JAPConfForwardingServer.this.knownCascadesList.getSelectedValue();
                if (mixCascade != null) {
                    JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().addToAllowedMixCascades(mixCascade);
                    JAPConfForwardingServer.this.m_knownCascadesListModel.removeElement(mixCascade);
                }
            }
        });
        this.settingsForwardingServerConfigAllowedCascadesRemoveButton = new JButton(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesRemoveButton"));
        this.settingsForwardingServerConfigAllowedCascadesRemoveButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                MixCascade mixCascade = (MixCascade)JAPConfForwardingServer.this.allowedCascadesList.getSelectedValue();
                if (mixCascade != null) {
                    JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().removeFromAllowedMixCascades(mixCascade.getId());
                    JAPConfForwardingServer.this.m_knownCascadesListModel.addElement(mixCascade);
                }
            }
        });
        this.settingsForwardingServerConfigAllowedCascadesAllowAllBox = new JCheckBox(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesAllowAllBox"), JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().getAllowAllAvailableMixCascades());
        this.settingsForwardingServerConfigAllowedCascadesAllowAllBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().setAllowAllAvailableMixCascades(JAPConfForwardingServer.this.settingsForwardingServerConfigAllowedCascadesAllowAllBox.isSelected());
            }
        });
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesBorder"));
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(0, 5, 10, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigAllowedCascadesAllowAllBox, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigAllowedCascadesAllowAllBox);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigAllowedCascadesKnownCascadesLabel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigAllowedCascadesAllowedCascadesLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigAllowedCascadesReloadButton, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigAllowedCascadesReloadButton);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(jScrollPane, gridBagConstraints);
        jPanel.add(jScrollPane);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(jScrollPane2, gridBagConstraints);
        jPanel.add(jScrollPane2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigAllowedCascadesAddButton, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigAllowedCascadesAddButton);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigAllowedCascadesRemoveButton, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigAllowedCascadesRemoveButton);
        return jPanel;
    }

    private JPanel createForwardingServerConfigRegistrationInfoServicesPanel() {
        JPanel jPanel = new JPanel();
        this.settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel = new JLabel(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel"));
        this.settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel = new JLabel(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel"));
        this.knownInfoServicesList = new JList(this.m_knownInfoServicesListModel);
        this.knownInfoServicesList.setSelectionMode(0);
        JScrollPane jScrollPane = new JScrollPane(this.knownInfoServicesList);
        jScrollPane.setPreferredSize(new JTextArea(4, 20).getPreferredSize());
        this.m_registrationInfoServicesListModel = new DefaultListModel();
        this.registrationInfoServicesList = new JList(this.m_registrationInfoServicesListModel);
        this.registrationInfoServicesList.setSelectionMode(0);
        JScrollPane jScrollPane2 = new JScrollPane(this.registrationInfoServicesList);
        jScrollPane2.setPreferredSize(new JTextArea(4, 20).getPreferredSize());
        this.settingsForwardingServerConfigRegistrationInfoServicesReloadButton = new JButton(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesReloadButton"));
        this.settingsForwardingServerConfigRegistrationInfoServicesReloadButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConfForwardingServer.this.startLoadInfoServicesThread();
            }
        });
        this.settingsForwardingServerConfigRegistrationInfoServicesAddButton = new JButton(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesAddButton"));
        this.settingsForwardingServerConfigRegistrationInfoServicesAddButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)JAPConfForwardingServer.this.knownInfoServicesList.getSelectedValue();
                if (infoServiceDBEntry != null) {
                    JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().addToRegistrationInfoServices(infoServiceDBEntry);
                    JAPConfForwardingServer.this.m_knownInfoServicesListModel.removeElement(infoServiceDBEntry);
                }
            }
        });
        this.settingsForwardingServerConfigRegistrationInfoServicesRemoveButton = new JButton(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesRemoveButton"));
        this.settingsForwardingServerConfigRegistrationInfoServicesRemoveButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)JAPConfForwardingServer.this.registrationInfoServicesList.getSelectedValue();
                if (infoServiceDBEntry != null) {
                    JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().removeFromRegistrationInfoServices(infoServiceDBEntry.getId());
                    JAPConfForwardingServer.this.m_knownInfoServicesListModel.addElement(infoServiceDBEntry);
                }
            }
        });
        this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox = new JCheckBox(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox"), JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().getRegisterAtAllAvailableInfoServices());
        this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().setRegisterAtAllAvailableInfoServices(JAPConfForwardingServer.this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox.isSelected());
            }
        });
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsForwardingServerConfigRegistrationInfoServicesBorder"));
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(0, 5, 10, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigRegistrationInfoServicesRegisterAtAllBox);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigRegistrationInfoServicesKnownInfoServicesLabel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigRegistrationInfoServicesSelectedInfoServicesLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigRegistrationInfoServicesReloadButton, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigRegistrationInfoServicesReloadButton);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(jScrollPane, gridBagConstraints);
        jPanel.add(jScrollPane);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(jScrollPane2, gridBagConstraints);
        jPanel.add(jScrollPane2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigRegistrationInfoServicesAddButton, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigRegistrationInfoServicesAddButton);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagLayout.setConstraints(this.settingsForwardingServerConfigRegistrationInfoServicesRemoveButton, gridBagConstraints);
        jPanel.add(this.settingsForwardingServerConfigRegistrationInfoServicesRemoveButton);
        return jPanel;
    }

    private Vector showFetchMixCascadesDialog(JComponent jComponent) {
        JAPDialog jAPDialog = new JAPDialog(jComponent, JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesFetchMixCascadesDialogTitle"));
        jAPDialog.setResizable(false);
        jAPDialog.setDefaultCloseOperation(2);
        final Vector vector = new Vector();
        final Vector vector2 = new Vector();
        Runnable runnable = new Runnable(){

            public void run() {
                MixCascade mixCascade;
                Hashtable hashtable = InfoServiceHolder.getInstance().getMixCascades();
                Thread.interrupted();
                if (hashtable == null) {
                    vector2.addElement(new NullPointerException());
                    hashtable = new Hashtable();
                }
                Enumeration enumeration = hashtable.elements();
                while (enumeration.hasMoreElements()) {
                    mixCascade = (MixCascade)enumeration.nextElement();
                    if (JAPConfForwardingServer.this.m_allowedCascadesListModel == null || JAPConfForwardingServer.this.m_allowedCascadesListModel.contains(mixCascade)) continue;
                    vector.addElement(mixCascade);
                }
                enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfForwardingServer.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
                while (enumeration.hasMoreElements()) {
                    mixCascade = (MixCascade)enumeration.nextElement();
                    if (!mixCascade.isUserDefined()) continue;
                    vector.addElement(mixCascade);
                }
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesFetchMixCascadesDialogFetchLabel"), runnable);
        workerContentPane.updateDialog();
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        if (vector2.size() > 0) {
            JAPDialog.showErrorDialog((Component)jComponent, JAPMessages.getString("settingsForwardingServerConfigAllowedCascadesFetchMixCascadesDialogFetchCascadesError"));
        }
        return vector;
    }

    public String getHelpContext() {
        return "forwarding_server";
    }

    protected void onRootPanelShown() {
        if (!JAPModel.isInfoServiceDisabled()) {
            this.fillLists();
        }
        this.m_startServerBox.setSelected(JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2);
    }

    private void fillLists() {
        this.m_knownCascadesListModel.clear();
        this.m_knownInfoServicesListModel.clear();
        Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPConfForwardingServer.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
        MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
        boolean bl = false;
        while (enumeration.hasMoreElements()) {
            MixCascade mixCascade2 = (MixCascade)enumeration.nextElement();
            if (this.m_allowedCascadesListModel != null && !this.m_allowedCascadesListModel.contains(mixCascade2)) {
                this.m_knownCascadesListModel.addElement(mixCascade2);
            }
            if (!mixCascade2.equals(mixCascade)) continue;
            bl = true;
        }
        if (!bl) {
            this.m_knownCascadesListModel.addElement(mixCascade);
        }
        this.startLoadInfoServicesThread();
    }

    private void startLoadInfoServicesThread() {
        Runnable runnable = new Runnable(){

            public void run() {
                JAPConfForwardingServer.this.loadInfoServices();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void loadInfoServices() {
        InfoServiceHolder infoServiceHolder = InfoServiceHolder.getInstance();
        synchronized (infoServiceHolder) {
            this.m_knownInfoServicesListModel.clear();
            Vector vector = InfoServiceHolder.getInstance().getInfoservicesWithForwarderList();
            if (vector != null) {
                Enumeration enumeration = vector.elements();
                while (enumeration.hasMoreElements()) {
                    InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)enumeration.nextElement();
                    if (this.m_registrationInfoServicesListModel == null || this.m_registrationInfoServicesListModel.contains(infoServiceDBEntry)) continue;
                    this.m_knownInfoServicesListModel.addElement(infoServiceDBEntry);
                }
            }
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

