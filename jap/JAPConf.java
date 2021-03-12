/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.MixCascade;
import anon.pay.PayAccount;
import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPMultilineLabel;
import gui.dialog.JAPDialog;
import gui.help.JAPHelp;
import jap.AbstractJAPMainView;
import jap.JAPConfCert;
import jap.JAPConfInfoService;
import jap.JAPConfModuleSystem;
import jap.JAPConfNetwork;
import jap.JAPConfServices;
import jap.JAPConfUI;
import jap.JAPConfUpdate;
import jap.JAPController;
import jap.JAPDebug;
import jap.JAPModel;
import jap.forward.JAPConfForwardingServer;
import jap.forward.JAPConfForwardingState;
import jap.pay.AccountSettingsPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import logging.LogHolder;
import logging.LogLevel;
import logging.LogType;

public final class JAPConf
extends JAPDialog
implements ActionListener,
WindowListener {
    public static final String MSG_READ_PANEL_HELP = (class$jap$JAPConf == null ? (class$jap$JAPConf = JAPConf.class$("jap.JAPConf")) : class$jap$JAPConf).getName() + "_readPanelHelp";
    private static final String MSG_DETAILLEVEL = (class$jap$JAPConf == null ? (class$jap$JAPConf = JAPConf.class$("jap.JAPConf")) : class$jap$JAPConf).getName() + "_detaillevel";
    private static final String MSG_BTN_SAVE = (class$jap$JAPConf == null ? (class$jap$JAPConf = JAPConf.class$("jap.JAPConf")) : class$jap$JAPConf).getName() + "_btnSave";
    private static final String MSG_ASK_RESET_DEFAULTS = (class$jap$JAPConf == null ? (class$jap$JAPConf = JAPConf.class$("jap.JAPConf")) : class$jap$JAPConf).getName() + "_askResetDefaults";
    private static final String MSG_NEED_RESTART = (class$jap$JAPConf == null ? (class$jap$JAPConf = JAPConf.class$("jap.JAPConf")) : class$jap$JAPConf).getName() + "_needRestart";
    private static final String MSG_COULD_NOT_OPEN_LOG = (class$jap$JAPConf == null ? (class$jap$JAPConf = JAPConf.class$("jap.JAPConf")) : class$jap$JAPConf).getName() + "_couldNotOpenLog";
    public static final String NETWORK_TAB = "NETWORK_TAB";
    public static final String UI_TAB = "UI_TAB";
    public static final String UPDATE_TAB = "UPDATE_TAB";
    public static final String PROXY_TAB = "PROXY_TAB";
    public static final String INFOSERVICE_TAB = "INFOSERVICE_TAB";
    public static final String ANON_TAB = "ANON_TAB";
    public static final String ANON_SERVICES_TAB = "SERVICES_TAB";
    public static final String ANON_TRUST_TAB = "ANON_TRUST_TAB";
    public static final String CERT_TAB = "CERT_TAB";
    public static final String TOR_TAB = "TOR_TAB";
    public static final String DEBUG_TAB = "DEBUG_TAB";
    public static final String PAYMENT_TAB = "PAYMENT_TAB";
    public static final String HTTP_FILTER_TAB = "HTTP_FILTER_TAB";
    public static final String FORWARDING_CLIENT_TAB = "FORWARDING_CLIENT_TAB";
    public static final String FORWARDING_SERVER_TAB = "FORWARDING_SERVER_TAB";
    public static final String FORWARDING_STATE_TAB = "FORWARDING_STATE_TAB";
    private static JAPConf ms_JapConfInstance = null;
    private JAPController m_Controller;
    private JCheckBox[] m_cbLogTypes;
    private JCheckBox m_cbShowDebugConsole;
    private JCheckBox m_cbDebugToFile;
    private JTextField m_tfDebugFileName;
    private JButton m_bttnDebugFileNameSearch;
    private JAPMultilineLabel m_labelConfDebugLevel;
    private JAPMultilineLabel m_labelConfDebugTypes;
    private JSlider m_sliderDebugLevel;
    private JSlider m_sliderDebugDetailLevel;
    private JPanel m_pMisc;
    private JButton m_bttnDefaultConfig;
    private JButton m_bttnCancel;
    private JButton m_bttnHelp;
    private boolean m_bWithPayment = false;
    private boolean m_bIsSimpleView;
    private Vector m_vecConfigChangesNeedRestart = new Vector();
    private JAPConfModuleSystem m_moduleSystem;
    private JAPConfServices m_confServices;
    private JAPConfInfoService m_confInfoService;
    private AbstractJAPMainView m_parentView;
    private AccountSettingsPanel m_accountSettings;
    private JAPConfUI m_confUI;
    static /* synthetic */ Class class$jap$JAPConf;

    public static JAPConf getInstance() {
        return ms_JapConfInstance;
    }

    public AbstractJAPMainView getMainView() {
        return this.m_parentView;
    }

    public JAPConf(AbstractJAPMainView abstractJAPMainView, boolean bl) {
        super(abstractJAPMainView, JAPMessages.getString("settingsDialog"), true);
        Serializable serializable;
        this.m_parentView = abstractJAPMainView;
        this.setDefaultCloseOperation(1);
        this.m_bWithPayment = bl;
        this.m_bIsSimpleView = JAPModel.getDefaultView() == 2;
        ms_JapConfInstance = this;
        this.m_Controller = JAPController.getInstance();
        JPanel jPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        this.m_pMisc = this.buildMiscPanel();
        this.m_moduleSystem = new JAPConfModuleSystem();
        DefaultMutableTreeNode defaultMutableTreeNode = this.m_moduleSystem.getConfigurationTreeRootNode();
        this.m_confUI = new JAPConfUI();
        this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode, this.m_confUI, UI_TAB);
        if (this.m_bWithPayment) {
            this.m_accountSettings = new AccountSettingsPanel();
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode, this.m_accountSettings, PAYMENT_TAB);
        }
        if (!this.m_bIsSimpleView && !JAPController.getInstance().isHideUpdateDialogs()) {
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode, new JAPConfUpdate(), UPDATE_TAB);
        }
        this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode, new JAPConfNetwork(), NETWORK_TAB);
        this.m_confServices = new JAPConfServices();
        DefaultMutableTreeNode defaultMutableTreeNode2 = this.m_moduleSystem.addComponent(defaultMutableTreeNode, null, "ngTreeAnonService", null, null);
        if (!this.m_bIsSimpleView) {
            this.m_confInfoService = new JAPConfInfoService();
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode2, this.m_confServices, ANON_SERVICES_TAB);
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode2, this.m_confInfoService, INFOSERVICE_TAB);
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode2, new JAPConfForwardingServer(), FORWARDING_SERVER_TAB);
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode2, new JAPConfCert(), CERT_TAB);
            serializable = this.m_moduleSystem.addComponent(defaultMutableTreeNode, this.m_pMisc, "ngTreeDebugging", DEBUG_TAB, "debugging");
            if (JAPModel.getInstance().isForwardingStateModuleVisible()) {
                this.m_moduleSystem.addConfigurationModule((DefaultMutableTreeNode)serializable, new JAPConfForwardingState(), FORWARDING_STATE_TAB);
            }
        } else {
            this.m_moduleSystem.addConfigurationModule(defaultMutableTreeNode2, this.m_confServices, ANON_SERVICES_TAB);
        }
        this.m_moduleSystem.getConfigurationTree().expandPath(new TreePath(defaultMutableTreeNode2.getPath()));
        serializable = new JPanel();
        ((Container)serializable).setLayout(new FlowLayout(2));
        this.m_bttnHelp = new JButton(JAPMessages.getString(JAPHelp.MSG_HELP_BUTTON));
        ((Container)serializable).add(this.m_bttnHelp);
        this.m_bttnHelp.addActionListener(this);
        this.m_bttnDefaultConfig = new JButton(JAPMessages.getString("bttnDefaultConfig"));
        final JAPConf jAPConf = this;
        this.m_bttnDefaultConfig.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (JAPDialog.showConfirmDialog(jAPConf, JAPMessages.getString(MSG_ASK_RESET_DEFAULTS), 2, 2) == 0) {
                    JAPConf.this.resetToDefault();
                }
            }
        });
        if (!JAPModel.isSmallDisplay()) {
            ((Container)serializable).add(this.m_bttnDefaultConfig);
        }
        this.m_bttnCancel = new JButton(JAPMessages.getString("cancelButton"));
        this.m_bttnCancel.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConf.this.cancelPressed();
            }
        });
        ((Container)serializable).add(this.m_bttnCancel);
        JButton jButton = new JButton(JAPMessages.getString(MSG_BTN_SAVE));
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConf.this.okPressed(false);
            }
        });
        ((Container)serializable).add(jButton);
        JButton jButton2 = new JButton(JAPMessages.getString("okButton"));
        jButton2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConf.this.okPressed(true);
            }
        });
        ((Container)serializable).add(jButton2);
        ((Container)serializable).add(new JLabel("   "));
        this.getRootPane().setDefaultButton(jButton2);
        JPanel jPanel2 = this.m_moduleSystem.getRootPanel();
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        jPanel.setLayout(gridBagLayout2);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagLayout2.setConstraints(jPanel2, gridBagConstraints);
        jPanel.add(jPanel2);
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagLayout2.setConstraints((Component)serializable, gridBagConstraints);
        jPanel.add((Component)serializable);
        this.setContentPane(jPanel);
        this.updateValues();
        if (JAPModel.getDefaultView() == 2) {
            this.m_moduleSystem.selectNode(ANON_SERVICES_TAB);
        } else {
            this.m_moduleSystem.selectNode(DEBUG_TAB);
        }
        if (JAPModel.isSmallDisplay()) {
            this.setSize(240, 300);
            this.setLocation(0, 0);
        } else if (JAPModel.getInstance().isConfigWindowSizeSaved() && JAPModel.getInstance().getConfigSize() != null) {
            this.setSize(JAPModel.getInstance().getConfigSize());
        } else {
            this.doPack();
        }
        this.m_confUI.afterPack();
        this.m_moduleSystem.getConfigurationTree().setMinimumSize(this.m_moduleSystem.getConfigurationTree().getPreferredSize());
        this.m_moduleSystem.selectNode(UI_TAB);
        this.restoreLocation(JAPModel.getInstance().getConfigWindowLocation());
        this.addWindowListener(this);
        this.m_moduleSystem.initObservers();
        JAPModel.getInstance().addObserver(new Observer(){

            public void update(Observable observable, Object object) {
                if (object instanceof JAPModel.FontResize) {
                    Runnable runnable = new Runnable(){

                        public void run() {
                            SwingUtilities.updateComponentTreeUI(JAPConf.this.getContentPane());
                        }
                    };
                    if (SwingUtilities.isEventDispatchThread()) {
                        runnable.run();
                    } else {
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            }
        });
    }

    public void windowClosed(WindowEvent windowEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
        this.cancelPressed();
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    protected synchronized void doPack() {
        boolean bl = false;
        boolean bl2 = false;
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                this.m_moduleSystem.revalidate();
            } else {
                SwingUtilities.invokeAndWait(new Runnable(){

                    public void run() {
                        JAPConf.this.m_moduleSystem.revalidate();
                    }
                });
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.GUI, exception);
        }
        while (!bl) {
            this.pack();
            if (this.getSize().width < this.getSize().height) {
                LogHolder.log(3, LogType.GUI, "Could not pack config properly. Width is smaller than height! Width:" + this.getSize().width + " Height:" + this.getSize().height);
                bl = true;
            } else if (this.getSize().width > this.getScreenBounds().width || this.getSize().height > this.getScreenBounds().height) {
                LogHolder.log(3, LogType.GUI, "Packed config view with illegal size! " + this.getSize());
                bl = true;
            } else {
                JAPModel.getInstance().setConfigSize(this.getSize());
            }
            if (!bl) break;
            this.m_moduleSystem.revalidate();
            if (bl2) {
                bl = false;
                bl2 = false;
                continue;
            }
            if (JAPModel.getInstance().getConfigSize() != null && JAPModel.getInstance().getConfigSize().width > 0 && JAPModel.getInstance().getConfigSize().height > 0) {
                this.setSize(JAPModel.getInstance().getConfigSize());
            } else {
                this.setSize(new Dimension(786, 545));
            }
            LogHolder.log(3, LogType.GUI, "Setting default config size to " + this.getSize());
            break;
        }
    }

    public void setVisible(boolean bl) {
        if (bl) {
            this.m_parentView.getViewIconified().switchBackToMainView();
            this.m_moduleSystem.createSavePoints();
        }
        super.setVisible(bl);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.m_bttnHelp) {
            JAPHelp.getInstance().setContext(this.m_moduleSystem);
            JAPHelp.getInstance().loadCurrentContext();
        }
    }

    private JPanel buildMiscPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(new TitledBorder("Debugging"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        JPanel jPanel2 = new JPanel(new GridLayout(0, 1));
        this.m_cbLogTypes = new JCheckBox[LogType.getNumberOfLogTypes()];
        int[] arrn = LogType.getAvailableLogTypes();
        for (int i = 0; i < this.m_cbLogTypes.length; ++i) {
            this.m_cbLogTypes[i] = new JCheckBox(LogType.getLogTypeName(arrn[i]));
            if (i <= 0) continue;
            jPanel2.add(this.m_cbLogTypes[i]);
        }
        this.m_labelConfDebugTypes = new JAPMultilineLabel(JAPMessages.getString("ConfDebugTypes"));
        jPanel.add((Component)this.m_labelConfDebugTypes, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        this.m_cbShowDebugConsole = new JCheckBox(JAPMessages.getString("ConfDebugShowConsole"));
        this.m_cbShowDebugConsole.setSelected(JAPDebug.isShowConsole());
        JAPDebug.getInstance().addObserver(new Observer(){

            public void update(Observable observable, Object object) {
                JAPConf.this.m_cbShowDebugConsole.setSelected(false);
            }
        });
        this.m_cbShowDebugConsole.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                JAPDebug.showConsole(itemEvent.getStateChange() == 1, JAPController.getInstance().getViewWindow());
            }
        });
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        jPanel.add((Component)this.m_cbShowDebugConsole, gridBagConstraints);
        this.m_cbDebugToFile = new JCheckBox(JAPMessages.getString("ConfDebugFile"));
        this.m_cbDebugToFile.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                boolean bl = JAPConf.this.m_cbDebugToFile.isSelected();
                JAPConf.this.m_bttnDebugFileNameSearch.setEnabled(bl);
                JAPConf.this.m_tfDebugFileName.setEnabled(bl);
            }
        });
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.0;
        jPanel.add((Component)this.m_cbDebugToFile, gridBagConstraints);
        JPanel jPanel3 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        this.m_tfDebugFileName = new JTextField(20);
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints2.fill = 2;
        jPanel3.add((Component)this.m_tfDebugFileName, gridBagConstraints2);
        this.m_bttnDebugFileNameSearch = new JButton(JAPMessages.getString("ConfDebugFileNameSearch"));
        this.m_bttnDebugFileNameSearch.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                int n;
                JFileChooser jFileChooser = new JFileChooser();
                String string = ms_JapConfInstance.m_tfDebugFileName.getText().trim();
                if (!string.equals("")) {
                    try {
                        jFileChooser.setCurrentDirectory(new File(new File(string).getParent()));
                    }
                    catch (Exception exception) {
                        string = "";
                    }
                }
                if (JAPController.getInstance().isPortableMode() && string.equals("") && (string = AbstractOS.getInstance().getProperty("user.dir")) != null) {
                    jFileChooser.setCurrentDirectory(new File(string));
                }
                if ((n = GUIUtils.showMonitoredFileChooser(jFileChooser, ms_JapConfInstance.getContentPane(), "__FILE_CHOOSER_OPEN")) == 0) {
                    try {
                        if (JAPController.getInstance().isPortableMode()) {
                            JAPConf.this.m_tfDebugFileName.setText(AbstractOS.toRelativePath(jFileChooser.getSelectedFile().getCanonicalPath()));
                        } else {
                            JAPConf.this.m_tfDebugFileName.setText(jFileChooser.getSelectedFile().getCanonicalPath());
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
        });
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 0.0;
        jPanel3.add((Component)this.m_bttnDebugFileNameSearch, gridBagConstraints2);
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)jPanel3, gridBagConstraints);
        JPanel jPanel4 = new JPanel();
        this.m_sliderDebugLevel = new JSlider(1, 0, 7, 0);
        this.m_sliderDebugLevel.addChangeListener(new ChangeListener(){

            public void stateChanged(ChangeEvent changeEvent) {
                Dictionary dictionary = JAPConf.this.m_sliderDebugLevel.getLabelTable();
                for (int i = 0; i < LogLevel.getLevelCount(); ++i) {
                    ((JLabel)dictionary.get(new Integer(i))).setEnabled(i <= JAPConf.this.m_sliderDebugLevel.getValue());
                }
            }
        });
        Hashtable<Integer, JLabel> hashtable = new Hashtable<Integer, JLabel>(LogLevel.getLevelCount(), 1.0f);
        for (int i = 0; i < LogLevel.getLevelCount(); ++i) {
            hashtable.put(new Integer(i), new JLabel(" " + LogLevel.getLevelName(i)));
        }
        this.m_sliderDebugLevel.setLabelTable(hashtable);
        this.m_sliderDebugLevel.setPaintLabels(true);
        this.m_sliderDebugLevel.setMajorTickSpacing(1);
        this.m_sliderDebugLevel.setMinorTickSpacing(1);
        this.m_sliderDebugLevel.setSnapToTicks(true);
        this.m_sliderDebugLevel.setPaintTrack(true);
        this.m_sliderDebugLevel.setPaintTicks(false);
        jPanel4.add(this.m_sliderDebugLevel);
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 3;
        jPanel.add((Component)new JSeparator(1), gridBagConstraints);
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.m_labelConfDebugLevel = new JAPMultilineLabel(JAPMessages.getString("ConfDebugLevels"));
        jPanel.add((Component)this.m_labelConfDebugLevel, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)jPanel4, gridBagConstraints);
        JPanel jPanel5 = new JPanel();
        this.m_sliderDebugDetailLevel = new JSlider(1, 0, 3, LogHolder.getDetailLevel());
        this.m_sliderDebugDetailLevel.setPaintTicks(false);
        this.m_sliderDebugDetailLevel.setPaintLabels(true);
        this.m_sliderDebugDetailLevel.setMajorTickSpacing(1);
        this.m_sliderDebugDetailLevel.setMinorTickSpacing(1);
        this.m_sliderDebugDetailLevel.setSnapToTicks(true);
        this.m_sliderDebugDetailLevel.setPaintTrack(true);
        jPanel5.add(this.m_sliderDebugDetailLevel);
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 3;
        jPanel.add((Component)new JSeparator(1), gridBagConstraints);
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.m_labelConfDebugLevel = new JAPMultilineLabel(JAPMessages.getString(MSG_DETAILLEVEL));
        jPanel.add((Component)this.m_labelConfDebugLevel, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)jPanel5, gridBagConstraints);
        return jPanel;
    }

    void cancelPressed() {
        this.m_vecConfigChangesNeedRestart.removeAllElements();
        this.m_moduleSystem.processCancelPressedEvent();
        this.setVisible(false);
    }

    private boolean checkValues() {
        return true;
    }

    private void resetToDefault() {
        this.m_vecConfigChangesNeedRestart.removeAllElements();
        this.m_moduleSystem.processResetToDefaultsPressedEvent();
        this.m_cbShowDebugConsole.setSelected(false);
        this.m_sliderDebugLevel.setValue(4);
        for (int i = 0; i < this.m_cbLogTypes.length; ++i) {
            this.m_cbLogTypes[i].setSelected(true);
        }
        this.m_sliderDebugDetailLevel.setValue(3);
        this.m_cbDebugToFile.setSelected(false);
    }

    private boolean onOkPressed() {
        String string = this.m_tfDebugFileName.getText().trim();
        if (!this.m_cbDebugToFile.isSelected()) {
            string = null;
        }
        try {
            JAPDebug.setLogToFile(string);
        }
        catch (IOException iOException) {
            JAPDialog.showErrorDialog((JAPDialog)this, JAPMessages.getString(MSG_COULD_NOT_OPEN_LOG), (Throwable)iOException);
            return false;
        }
        int[] arrn = LogType.getAvailableLogTypes();
        int n = LogType.NUL;
        for (int i = 0; i < this.m_cbLogTypes.length; ++i) {
            n |= this.m_cbLogTypes[i].isSelected() ? arrn[i] : LogType.NUL;
        }
        JAPDebug.getInstance().setLogType(n);
        JAPDebug.getInstance().setLogLevel(this.m_sliderDebugLevel.getValue());
        LogHolder.setDetailLevel(this.m_sliderDebugDetailLevel.getValue());
        return true;
    }

    private void okPressed(final boolean bl) {
        Object object;
        if (!this.checkValues()) {
            return;
        }
        this.m_vecConfigChangesNeedRestart.removeAllElements();
        if (!this.m_moduleSystem.processOkPressedEvent()) {
            this.m_vecConfigChangesNeedRestart.removeAllElements();
            return;
        }
        this.onOkPressed();
        this.resetAutomaticLocation(JAPModel.getInstance().isConfigWindowLocationSaved());
        if (this.m_vecConfigChangesNeedRestart.size() > 0) {
            int n;
            object = "<ul>";
            for (n = 0; n < this.m_vecConfigChangesNeedRestart.size(); ++n) {
                AbstractRestartNeedingConfigChange abstractRestartNeedingConfigChange = (AbstractRestartNeedingConfigChange)this.m_vecConfigChangesNeedRestart.elementAt(n);
                object = (String)object + "<li>" + abstractRestartNeedingConfigChange.getName();
                if (abstractRestartNeedingConfigChange.getMessage() != null && abstractRestartNeedingConfigChange.getMessage().trim().length() > 0) {
                    object = (String)object + "<br>" + abstractRestartNeedingConfigChange.getMessage();
                }
                object = (String)object + "</li>";
            }
            if (JAPDialog.showYesNoDialog(this, JAPMessages.getString(MSG_NEED_RESTART, object = (String)object + "</ul>"))) {
                for (n = 0; n < this.m_vecConfigChangesNeedRestart.size(); ++n) {
                    ((AbstractRestartNeedingConfigChange)this.m_vecConfigChangesNeedRestart.elementAt(n)).doChange();
                }
            } else {
                for (n = 0; n < this.m_vecConfigChangesNeedRestart.size(); ++n) {
                    ((AbstractRestartNeedingConfigChange)this.m_vecConfigChangesNeedRestart.elementAt(n)).doCancel();
                }
                this.m_vecConfigChangesNeedRestart.removeAllElements();
                return;
            }
        }
        object = new Thread(new Runnable(){

            public void run() {
                JAPConf.this.m_Controller.saveConfigFile();
                if (bl && !JAPConf.this.isRestartNeeded()) {
                    JAPConf.this.setVisible(false);
                }
                if (JAPConf.this.isRestartNeeded()) {
                    JAPController.goodBye(false);
                }
            }
        });
        ((Thread)object).setDaemon(true);
        ((Thread)object).start();
    }

    public void selectCard(String string, final Object object) {
        if (string != null) {
            if (string.equals(UI_TAB)) {
                this.m_moduleSystem.selectNode(UI_TAB);
                new Thread(new Runnable(){

                    public void run() {
                        JAPConf.this.m_confUI.chooseBrowserPath();
                    }
                }).start();
            } else if (string.equals(INFOSERVICE_TAB)) {
                this.m_moduleSystem.selectNode(INFOSERVICE_TAB);
                if (object != null && object instanceof Boolean && ((Boolean)object).booleanValue()) {
                    this.m_confInfoService.showSettingsPanel();
                }
            } else if (string.equals(NETWORK_TAB)) {
                this.m_moduleSystem.selectNode(NETWORK_TAB);
            } else if (string.equals(ANON_TAB)) {
                this.m_moduleSystem.selectNode(ANON_SERVICES_TAB);
                if (object instanceof MixCascade) {
                    this.m_confServices.selectAnonTab((MixCascade)object, false, false);
                } else if (object instanceof Boolean) {
                    this.m_confServices.selectAnonTab(null, (Boolean)object, false);
                } else {
                    this.m_confServices.selectAnonTab(null, false, true);
                }
            } else if (string.equals(PAYMENT_TAB)) {
                this.m_moduleSystem.selectNode(PAYMENT_TAB);
                if (object != null) {
                    new Thread(new Runnable(){

                        public void run() {
                            if (object instanceof Boolean && ((Boolean)object).booleanValue()) {
                                JAPConf.this.m_accountSettings.doCreateAccount(null);
                            } else if (object instanceof String) {
                                JAPConf.this.m_accountSettings.doCreateAccount((String)object);
                            } else if (object instanceof PayAccount) {
                                JAPConf.this.m_accountSettings.showOpenTransaction((PayAccount)object);
                            } else if (object instanceof Boolean && !((Boolean)object).booleanValue()) {
                                JAPConf.this.m_accountSettings.backupAccount();
                            }
                        }
                    }).start();
                }
            } else {
                this.m_moduleSystem.selectNode(string);
            }
        }
    }

    private synchronized void updateValues() {
        boolean bl;
        this.m_moduleSystem.processUpdateValuesEvent(true);
        this.m_cbShowDebugConsole.setSelected(JAPDebug.isShowConsole());
        int[] arrn = LogType.getAvailableLogTypes();
        for (bl = false; bl < this.m_cbLogTypes.length; bl += 1) {
            this.m_cbLogTypes[bl].setSelected((JAPDebug.getInstance().getLogType() & arrn[bl]) != 0);
        }
        this.m_sliderDebugLevel.setValue(JAPDebug.getInstance().getLogLevel());
        this.m_sliderDebugDetailLevel.setValue(LogHolder.getDetailLevel());
        bl = (JAPDebug.isLogToFile() ? 1 : 0) != 0;
        this.m_tfDebugFileName.setEnabled(bl);
        this.m_bttnDebugFileNameSearch.setEnabled(bl);
        this.m_cbDebugToFile.setSelected(bl);
        if (bl) {
            this.m_tfDebugFileName.setText(JAPDebug.getLogFilename());
        }
    }

    protected void addNeedRestart(AbstractRestartNeedingConfigChange abstractRestartNeedingConfigChange) {
        if (abstractRestartNeedingConfigChange != null) {
            this.m_vecConfigChangesNeedRestart.addElement(abstractRestartNeedingConfigChange);
        }
    }

    private boolean isRestartNeeded() {
        return this.m_vecConfigChangesNeedRestart.size() > 0;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public static abstract class AbstractRestartNeedingConfigChange {
        public abstract String getName();

        public abstract void doChange();

        public void doCancel() {
        }

        public String getMessage() {
            return "";
        }
    }
}

