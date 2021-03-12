/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.JavaVersionDBEntry;
import anon.platform.AbstractOS;
import anon.platform.WindowsOS;
import anon.util.ClassUtil;
import anon.util.IReturnRunnable;
import anon.util.JAPMessages;
import anon.util.LanguageMapper;
import gui.GUIUtils;
import gui.JAPDll;
import gui.TitledGridBagPanel;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.WorkerContentPane;
import gui.help.JAPExternalHelpViewer;
import gui.help.JAPHelp;
import jap.AbstractJAPConfModule;
import jap.JAPConf;
import jap.JAPConstants;
import jap.JAPController;
import jap.JAPModel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import logging.LogHolder;
import logging.LogType;

final class JAPConfUI
extends AbstractJAPConfModule {
    private static final String MSG_ON_CLOSING_JAP = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_onClosingJAP";
    private static final String MSG_WARNING_ON_CLOSING_JAP = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_warningOnClosingJAP";
    private static final String MSG_FONT_SIZE = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_fontSize";
    private static final String MSG_WARNING_IMPORT_LNF = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_warningImportLNF";
    private static final String MSG_INCOMPATIBLE_JAVA = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_incompatibleJava";
    private static final String MSG_REMOVE = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_remove";
    private static final String MSG_IMPORT = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_import";
    private static final String MSG_COULD_NOT_REMOVE = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_couldNotRemove";
    private static final String MSG_TITLE_IMPORT = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_titleImport";
    private static final String MSG_PROGRESS_IMPORTING = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_progressImport";
    private static final String MSG_IMPORT_SUCCESSFUL = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_importSuccessful";
    private static final String MSG_NO_LNF_FOUND = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_noLNFFound";
    private static final String MSG_LOOK_AND_FEEL_CHANGED = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_lnfChanged";
    private static final String MSG_RESTART_TO_UNLOAD = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_restartToUnload";
    private static final String MSG_DIALOG_FORMAT = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_lblDialogFormat";
    private static final String MSG_DIALOG_FORMAT_TEST = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_dialogFormatTest";
    private static final String MSG_DIALOG_FORMAT_TEST_2 = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_dialogFormatTest2";
    private static final String MSG_DIALOG_FORMAT_TEST_BTN = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_dialogFormatTestBtn";
    private static final String MSG_DIALOG_FORMAT_GOLDEN_RATIO = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_dialogFormatGoldenRatio";
    private static final String MSG_TEST_BROWSER_PATH = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_testBrowserPath";
    private static final String MSG_BROWSER_SHOULD_OPEN = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserShouldOpen";
    private static final String MSG_BROWSER_DOES_NOT_OPEN = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserDoesNotStart";
    private static final String MSG_BROWSER_TEST_PATH = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserTestPath";
    private static final String MSG_BROWSER_NEW_PATH = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserNewPath";
    private static final String MSG_BROWSER_TEST_BUTTON = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserTestBtn";
    private static final String MSG_BROWSER_TEST_EXPLAIN = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserTestExplain";
    private static final String MSG_HELP_PATH = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_helpPath";
    private static final String MSG_HELP_PATH_CHOOSE = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_helpPathChange";
    private static final String MSG_BROWSER_PATH = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserPath";
    private static final String MSG_BROWSER_PATH_CHOOSE = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_browserPathChange";
    private static final String MSG_NO_NATIVE_LIBRARY = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_noNativeLibrary";
    private static final String MSG_NO_NATIVE_WINDOWS_LIBRARY = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_noNativeWindowsLibrary";
    private static final String MSG_WINDOW_POSITION = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_windowPosition";
    private static final String MSG_WINDOW_MAIN = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_windowMain";
    private static final String MSG_WINDOW_CONFIG = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_windowConfig";
    private static final String MSG_WINDOW_ICON = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_windowIcon";
    private static final String MSG_WINDOW_HELP = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_windowHelp";
    private static final String MSG_WINDOW_SIZE = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_windowSize";
    private static final String MSG_MINI_ON_TOP = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_miniOnTop";
    private static final String MSG_MINI_ON_TOP_TT = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_miniOnTopTT";
    private static final String MSG_ENABLE_CLOSE_BUTTON = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + ".enableCloseButton";
    private static final String MSG_CHOOSE_OTHER_DIR = (class$jap$JAPConfUI == null ? (class$jap$JAPConfUI = JAPConfUI.class$("jap.JAPConfUI")) : class$jap$JAPConfUI).getName() + "_chooseOtherDir";
    private TitledBorder m_borderLookAndFeel;
    private TitledBorder m_borderView;
    private JComboBox m_comboLanguage;
    private JComboBox m_comboUI;
    private JComboBox m_comboDialogFormat;
    private JCheckBox m_cbSaveWindowLocationMain;
    private JCheckBox m_cbSaveWindowLocationIcon;
    private JCheckBox m_cbSaveWindowLocationConfig;
    private JCheckBox m_cbSaveWindowLocationHelp;
    private JCheckBox m_cbSaveWindowSizeConfig;
    private JCheckBox m_cbSaveWindowSizeHelp;
    private JCheckBox m_cbAfterStart;
    private JCheckBox m_cbShowSplash;
    private JCheckBox m_cbStartPortableFirefox;
    private JRadioButton m_rbViewSimplified;
    private JRadioButton m_rbViewNormal;
    private JRadioButton m_rbViewMini;
    private JRadioButton m_rbViewSystray;
    private JCheckBox m_cbWarnOnClose;
    private JCheckBox m_cbMiniOnTop;
    private JCheckBox m_cbIgnoreDLLUpdate;
    private JCheckBox m_cbEnableCloseButton;
    private JSlider m_slidFontSize;
    private JButton m_btnAddUI;
    private JButton m_btnDeleteUI;
    private File m_currentDirectory;
    private JTextField m_helpPathField;
    private JButton m_helpPathButton;
    private JTextField m_portableBrowserPathField;
    private JButton m_portableBrowserPathButton;
    private Observer m_modelObserver = new Observer(){

        public void update(Observable observable, Object object) {
            if (object == JAPModel.CHANGED_HELP_PATH) {
                JAPConfUI.this.updateHelpPath();
                if (JAPModel.getInstance().isHelpPathChangeable()) {
                    JAPConfUI.this.m_helpPathButton.setEnabled(true);
                } else {
                    JAPConfUI.this.m_helpPathButton.setEnabled(false);
                }
            } else if (object == JAPModel.CHANGED_DLL_UPDATE) {
                JAPConfUI.this.m_cbIgnoreDLLUpdate.setSelected(!JAPModel.getInstance().isDLLWarningActive());
                JAPConfUI.this.m_cbIgnoreDLLUpdate.setEnabled(JAPModel.getInstance().getDllUpdatePath() != null);
            }
        }
    };
    private boolean m_bClickedBrowserPath = false;
    static /* synthetic */ Class class$jap$JAPConfUI;

    public JAPConfUI() {
        super(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                JAPModel.getInstance().addObserver(this.m_modelObserver);
                return true;
            }
        }
        return false;
    }

    public void chooseBrowserPath() {
        if (this.m_bClickedBrowserPath) {
            return;
        }
        this.m_bClickedBrowserPath = true;
        this.chooseBrowserPath(null);
        this.m_bClickedBrowserPath = false;
    }

    private void chooseBrowserPath(String string) {
        File file = null;
        JFileChooser jFileChooser = string != null && new File(string).exists() ? new JFileChooser(string) : (JAPModel.getInstance().getPortableBrowserpath() != null ? new JFileChooser(JAPModel.getInstance().getPortableBrowserpath()) : (AbstractOS.getInstance().getDefaultBrowserPath() != null ? new JFileChooser(AbstractOS.getInstance().getDefaultBrowserPath()) : new JFileChooser(System.getProperty("user.dir"))));
        jFileChooser.setFileSelectionMode(0);
        if (GUIUtils.showMonitoredFileChooser(jFileChooser, this.getRootPanel(), "__FILE_CHOOSER_OPEN") == 0) {
            file = jFileChooser.getSelectedFile();
        }
        if (file != null) {
            final String string2 = AbstractOS.toRelativePath(file.getPath());
            JAPDialog jAPDialog = new JAPDialog(this.getRootPanel(), JAPMessages.getString(MSG_TEST_BROWSER_PATH));
            DialogContentPane dialogContentPane = new DialogContentPane(jAPDialog, JAPMessages.getString(MSG_BROWSER_TEST_EXPLAIN), new DialogContentPaneOptions(1)){
                private boolean m_bValid = false;

                public DialogContentPane.CheckError checkNo() {
                    if (AbstractOS.getInstance().openBrowser(AbstractOS.createBrowserCommand(string2))) {
                        this.printStatusMessage(JAPMessages.getString(MSG_BROWSER_SHOULD_OPEN));
                        this.m_bValid = true;
                        return null;
                    }
                    return new DialogContentPane.CheckError(JAPMessages.getString(MSG_BROWSER_DOES_NOT_OPEN));
                }

                public DialogContentPane.CheckError checkYesOK() {
                    if (!this.m_bValid) {
                        return new DialogContentPane.CheckError(JAPMessages.getString(MSG_BROWSER_TEST_PATH));
                    }
                    return null;
                }

                public String getButtonYesOKText() {
                    return JAPMessages.getString(DialogContentPane.MSG_OK);
                }

                public String getButtonNoText() {
                    return JAPMessages.getString(MSG_BROWSER_TEST_BUTTON);
                }

                public String getButtonCancelText() {
                    return JAPMessages.getString(MSG_BROWSER_NEW_PATH);
                }
            };
            dialogContentPane.setDefaultButtonOperation(40960);
            dialogContentPane.pack();
            jAPDialog.setResizable(false);
            jAPDialog.setVisible(true);
            jAPDialog.dispose();
            if (dialogContentPane.getButtonValue() == 0) {
                this.m_portableBrowserPathField.setText(string2);
                this.m_portableBrowserPathField.repaint();
            } else if (dialogContentPane.getButtonValue() == 2) {
                this.chooseBrowserPath(file.getPath());
            }
        }
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        boolean bl = JAPModel.getDefaultView() == 2;
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        jPanel.setLayout(gridBagLayout);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 2;
        jPanel.add((Component)this.createLookAndFeelPanel(), gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridwidth = 1;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        jPanel.add((Component)this.createViewPanel(), gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 1;
        jPanel.add((Component)this.createAfterStartupPanel(), gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 1;
        JPanel jPanel2 = this.createWindowSizePanel();
        if (!bl) {
            jPanel.add((Component)jPanel2, gridBagConstraints);
        }
        ++gridBagConstraints.gridy;
        jPanel2 = this.createAfterShutdownPanel();
        if (!bl) {
            jPanel.add((Component)jPanel2, gridBagConstraints);
        }
        gridBagConstraints.gridx = 0;
        --gridBagConstraints.gridy;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = 1;
        jPanel2 = this.createWindowPanel();
        if (!bl) {
            jPanel.add((Component)jPanel2, gridBagConstraints);
        }
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy += 2;
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 1;
        jPanel2 = this.createHelpPathPanel();
        if (JAPModel.getInstance().isHelpPathChangeable()) {
            jPanel.add((Component)jPanel2, gridBagConstraints);
        }
        ++gridBagConstraints.gridy;
        jPanel2 = this.createBrowserPathPanel();
        if (JAPController.getInstance().isPortableMode()) {
            jPanel.add((Component)jPanel2, gridBagConstraints);
        }
        ++gridBagConstraints.gridy;
        gridBagConstraints.weighty = 1.0;
        jPanel.add((Component)new JPanel(), gridBagConstraints);
    }

    public void afterPack() {
        this.m_comboUI.setVisible(true);
    }

    public void beforePack() {
        this.m_comboUI.setVisible(false);
    }

    private JPanel createLookAndFeelPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_borderLookAndFeel = new TitledBorder(JAPMessages.getString("settingsLookAndFeelBorder"));
        JPanel jPanel = new JPanel(gridBagLayout);
        jPanel.setBorder(this.m_borderLookAndFeel);
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsLookAndFeel"));
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = 18;
        jPanel.add((Component)jLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        this.m_comboUI = new JComboBox();
        jPanel.add((Component)this.m_comboUI, gridBagConstraints);
        this.m_comboUI.setVisible(false);
        this.m_btnDeleteUI = new JButton(JAPMessages.getString(MSG_REMOVE));
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)this.m_btnDeleteUI, gridBagConstraints);
        this.m_btnDeleteUI.addActionListener(new ActionListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    JComboBox jComboBox = JAPConfUI.this.m_comboUI;
                    synchronized (jComboBox) {
                        int n;
                        UIManager.LookAndFeelInfo[] arrlookAndFeelInfo = UIManager.getInstalledLookAndFeels();
                        Vector<UIManager.LookAndFeelInfo> vector = new Vector<UIManager.LookAndFeelInfo>(arrlookAndFeelInfo.length - 1);
                        File file = ClassUtil.getClassDirectory(arrlookAndFeelInfo[JAPConfUI.this.m_comboUI.getSelectedIndex()].getClassName());
                        for (n = 0; n < arrlookAndFeelInfo.length; ++n) {
                            File file2 = ClassUtil.getClassDirectory(arrlookAndFeelInfo[n].getClassName());
                            if (file2 != null && file.equals(file2)) continue;
                            vector.addElement(arrlookAndFeelInfo[n]);
                        }
                        UIManager.LookAndFeelInfo[] arrlookAndFeelInfo2 = new UIManager.LookAndFeelInfo[vector.size()];
                        for (n = 0; n < arrlookAndFeelInfo2.length; ++n) {
                            arrlookAndFeelInfo2[n] = (UIManager.LookAndFeelInfo)vector.elementAt(n);
                        }
                        UIManager.setInstalledLookAndFeels(arrlookAndFeelInfo2);
                        JAPModel.getInstance().removeLookAndFeelFile(file);
                        JAPConfUI.this.updateUICombo();
                    }
                    JAPDialog.showMessageDialog(JAPConfUI.this.getRootPanel(), JAPMessages.getString(MSG_RESTART_TO_UNLOAD));
                }
                catch (Exception exception) {
                    JAPDialog.showErrorDialog((Component)JAPConfUI.this.getRootPanel(), JAPMessages.getString(MSG_COULD_NOT_REMOVE), (Throwable)exception);
                }
            }
        });
        this.m_btnAddUI = new JButton(JAPMessages.getString(MSG_IMPORT));
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_btnAddUI, gridBagConstraints);
        this.m_btnAddUI.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                final JFileChooser jFileChooser = new JFileChooser(JAPConfUI.this.m_currentDirectory);
                final JAPDialog jAPDialog = new JAPDialog(JAPConfUI.this.getRootPanel(), JAPMessages.getString(MSG_TITLE_IMPORT));
                LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
                FileFilter fileFilter = new FileFilter(){

                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".jar");
                    }

                    public String getDescription() {
                        return "*.jar";
                    }
                };
                jFileChooser.setFileFilter(fileFilter);
                final SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(jAPDialog, "<font color='red'>" + JAPMessages.getString(MSG_WARNING_IMPORT_LNF) + "</font>", new DialogContentPane.Layout(JAPMessages.getString(JAPDialog.MSG_TITLE_WARNING), 2), null){
                    boolean m_bCanceled = false;

                    public DialogContentPane.CheckError checkYesOK() {
                        this.m_bCanceled = false;
                        DialogContentPane.CheckError checkError = super.checkYesOK();
                        jFileChooser.setFileSelectionMode(0);
                        if (GUIUtils.showMonitoredFileChooser(jFileChooser, jAPDialog.getContentPane(), "__FILE_CHOOSER_OPEN") != 0) {
                            this.m_bCanceled = true;
                        }
                        return checkError;
                    }

                    public Object getValue() {
                        return new Boolean(this.m_bCanceled);
                    }
                };
                final IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                    Object m_value;

                    public Object getValue() {
                        return this.m_value;
                    }

                    public void run() {
                        if (jFileChooser.getSelectedFile() != null) {
                            JAPConfUI.this.m_currentDirectory = jFileChooser.getCurrentDirectory();
                            try {
                                Vector vector = GUIUtils.registerLookAndFeelClasses(jFileChooser.getSelectedFile());
                                if (vector.size() > 0) {
                                    for (int i = 0; i < vector.size(); ++i) {
                                        LogHolder.log(5, LogType.GUI, "Added new L&F class file: " + vector.elementAt(i));
                                        JAPModel.getInstance().addLookAndFeelFile((File)vector.elementAt(i));
                                    }
                                    JAPConfUI.this.updateUICombo();
                                    this.m_value = JAPMessages.getString(MSG_IMPORT_SUCCESSFUL);
                                } else {
                                    this.m_value = new Exception(JAPMessages.getString(MSG_NO_LNF_FOUND));
                                }
                            }
                            catch (IllegalAccessException illegalAccessException) {
                                this.m_value = new Exception(JAPMessages.getString(MSG_INCOMPATIBLE_JAVA));
                            }
                            jFileChooser.setSelectedFile(null);
                        }
                    }
                };
                WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_PROGRESS_IMPORTING) + "...", (DialogContentPane)simpleWizardContentPane, (Runnable)iReturnRunnable){

                    public boolean isSkippedAsNextContentPane() {
                        return (Boolean)simpleWizardContentPane.getValue();
                    }
                };
                SimpleWizardContentPane simpleWizardContentPane2 = new SimpleWizardContentPane(jAPDialog, "OK", new DialogContentPane.Layout(JAPMessages.getString(JAPDialog.MSG_TITLE_INFO), 1), new DialogContentPaneOptions(workerContentPane)){

                    public DialogContentPane.CheckError checkUpdate() {
                        this.setText((String)iReturnRunnable.getValue());
                        return null;
                    }

                    public boolean isSkippedAsNextContentPane() {
                        return (Boolean)simpleWizardContentPane.getValue() != false || iReturnRunnable.getValue() instanceof Exception;
                    }

                    public boolean isSkippedAsPreviousContentPane() {
                        return true;
                    }

                    public boolean hideButtonCancel() {
                        return true;
                    }
                };
                SimpleWizardContentPane simpleWizardContentPane3 = new SimpleWizardContentPane(jAPDialog, "ERROR", new DialogContentPane.Layout(JAPMessages.getString(JAPDialog.MSG_TITLE_ERROR), 0), new DialogContentPaneOptions(simpleWizardContentPane2)){

                    public boolean isSkippedAsPreviousContentPane() {
                        return true;
                    }

                    public DialogContentPane.CheckError checkUpdate() {
                        this.setText(((Exception)iReturnRunnable.getValue()).getMessage());
                        return null;
                    }

                    public boolean isSkippedAsNextContentPane() {
                        return (Boolean)simpleWizardContentPane.getValue() != false || !(iReturnRunnable.getValue() instanceof Exception);
                    }

                    public boolean hideButtonCancel() {
                        return true;
                    }
                };
                JLabel jLabel = new JLabel("AAAAAAAAAAAAAAAAAAAAAAAA");
                workerContentPane.getContentPane().add(jLabel);
                simpleWizardContentPane.pack();
                jLabel.setVisible(false);
                jAPDialog.setVisible(true);
                if (lookAndFeel != UIManager.getLookAndFeel()) {
                    JAPDialog.showMessageDialog(JAPConfUI.this.getRootPanel(), JAPMessages.getString(MSG_LOOK_AND_FEEL_CHANGED));
                }
            }
        });
        this.m_comboUI.addItemListener(new ItemListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void itemStateChanged(ItemEvent itemEvent) {
                JComboBox jComboBox = JAPConfUI.this.m_comboUI;
                synchronized (jComboBox) {
                    if (JAPConfUI.this.m_comboUI.getSelectedIndex() >= 0) {
                        String string = UIManager.getInstalledLookAndFeels()[JAPConfUI.this.m_comboUI.getSelectedIndex()].getClassName();
                        String string2 = JAPModel.getInstance().getLookAndFeel();
                        String string3 = UIManager.getLookAndFeel().getClass().getName();
                        File file = null;
                        File file2 = null;
                        File file3 = null;
                        file2 = ClassUtil.getClassDirectory(string3);
                        file = ClassUtil.getClassDirectory(string2);
                        file3 = ClassUtil.getClassDirectory(string);
                        if (file3 != null && file != null && file.equals(file3) || file3 != null && file2 != null && file2.equals(file3) || string2.equals(string) || string3.equals(string) || JAPModel.getInstance().isSystemLookAndFeel(string)) {
                            JAPConfUI.this.m_btnDeleteUI.setEnabled(false);
                        } else {
                            JAPConfUI.this.m_btnDeleteUI.setEnabled(true);
                        }
                    }
                }
            }
        });
        jLabel = new JLabel(JAPMessages.getString("settingsLanguage"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)jLabel, gridBagConstraints);
        this.m_comboLanguage = new JComboBox();
        String[] arrstring = JAPConstants.getSupportedLanguages();
        for (int i = 0; i < arrstring.length; ++i) {
            this.m_comboLanguage.addItem(new LanguageMapper(arrstring[i], new Locale(arrstring[i], "")));
        }
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 1;
        jPanel.add((Component)this.m_comboLanguage, gridBagConstraints);
        jLabel = new JLabel(JAPMessages.getString(JAPMessages.getString(MSG_DIALOG_FORMAT)));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel.setVisible(JAPModel.getInstance().isDialogFormatShown());
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = 2;
        this.m_comboDialogFormat = new JComboBox();
        this.m_comboDialogFormat.addItem(new DialogFormat(JAPMessages.getString(MSG_DIALOG_FORMAT_GOLDEN_RATIO), 0));
        this.m_comboDialogFormat.addItem(new DialogFormat("4:3", 1));
        this.m_comboDialogFormat.addItem(new DialogFormat("16:9", 2));
        jPanel.add((Component)this.m_comboDialogFormat, gridBagConstraints);
        JButton jButton = new JButton(JAPMessages.getString(MSG_DIALOG_FORMAT_TEST_BTN));
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                int n = JAPDialog.getOptimizedFormat();
                JAPDialog.setOptimizedFormat(((DialogFormat)JAPConfUI.this.m_comboDialogFormat.getSelectedItem()).getFormat());
                JAPDialog.showMessageDialog(JAPConfUI.this.getRootPanel(), JAPMessages.getString(MSG_DIALOG_FORMAT_TEST));
                JAPDialog.setOptimizedFormat(n);
            }
        });
        gridBagConstraints.gridx = 2;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)jButton, gridBagConstraints);
        this.m_comboDialogFormat.setVisible(JAPModel.getInstance().isDialogFormatShown());
        jButton.setVisible(JAPModel.getInstance().isDialogFormatShown());
        jButton = new JButton(JAPMessages.getString(MSG_DIALOG_FORMAT_TEST_BTN));
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                int n = JAPDialog.getOptimizedFormat();
                JAPDialog.setOptimizedFormat(((DialogFormat)JAPConfUI.this.m_comboDialogFormat.getSelectedItem()).getFormat());
                JAPDialog.showMessageDialog(JAPConfUI.this.getRootPanel(), JAPMessages.getString(MSG_DIALOG_FORMAT_TEST_2));
                JAPDialog.setOptimizedFormat(n);
            }
        });
        gridBagConstraints.gridx = 3;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)jButton, gridBagConstraints);
        jButton.setVisible(JAPModel.getInstance().isDialogFormatShown());
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridwidth = 1;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_FONT_SIZE)), gridBagConstraints);
        this.m_slidFontSize = new JSlider(0, 0, 3, JAPModel.getInstance().getFontSize());
        this.m_slidFontSize.setPaintTicks(false);
        this.m_slidFontSize.setPaintLabels(true);
        this.m_slidFontSize.setMajorTickSpacing(1);
        this.m_slidFontSize.setMinorTickSpacing(1);
        this.m_slidFontSize.setSnapToTicks(true);
        this.m_slidFontSize.setPaintTrack(true);
        Hashtable<Integer, JLabel> hashtable = new Hashtable<Integer, JLabel>(4);
        for (int i = 0; i <= 3; ++i) {
            hashtable.put(new Integer(i), new JLabel("1" + i + "0%"));
        }
        this.m_slidFontSize.setLabelTable(hashtable);
        gridBagConstraints.gridwidth = 3;
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_slidFontSize, gridBagConstraints);
        return jPanel;
    }

    private JPanel createWindowSizePanel() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_WINDOW_SIZE)));
        this.m_cbSaveWindowSizeConfig = new JCheckBox(JAPMessages.getString(MSG_WINDOW_CONFIG));
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        jPanel.add((Component)this.m_cbSaveWindowSizeConfig, gridBagConstraints);
        this.m_cbSaveWindowSizeHelp = new JCheckBox(JAPMessages.getString(MSG_WINDOW_HELP));
        ++gridBagConstraints.gridy;
        return jPanel;
    }

    private JPanel createWindowPanel() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_WINDOW_POSITION)));
        this.m_cbSaveWindowLocationMain = new JCheckBox(JAPMessages.getString(MSG_WINDOW_MAIN));
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        jPanel.add((Component)this.m_cbSaveWindowLocationMain, gridBagConstraints);
        this.m_cbSaveWindowLocationConfig = new JCheckBox(JAPMessages.getString(MSG_WINDOW_CONFIG));
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbSaveWindowLocationConfig, gridBagConstraints);
        this.m_cbSaveWindowLocationIcon = new JCheckBox(JAPMessages.getString(MSG_WINDOW_ICON));
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbSaveWindowLocationIcon, gridBagConstraints);
        this.m_cbSaveWindowLocationHelp = new JCheckBox(JAPMessages.getString(MSG_WINDOW_HELP));
        ++gridBagConstraints.gridy;
        return jPanel;
    }

    private JPanel createViewPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_borderView = new TitledBorder(JAPMessages.getString("ngSettingsViewBorder"));
        JPanel jPanel = new JPanel(gridBagLayout);
        jPanel.setBorder(this.m_borderView);
        this.m_rbViewNormal = new JRadioButton(JAPMessages.getString("ngSettingsViewNormal"));
        this.m_rbViewSimplified = new JRadioButton(JAPMessages.getString("ngSettingsViewSimplified"));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.m_rbViewNormal);
        buttonGroup.add(this.m_rbViewSimplified);
        gridBagConstraints.insets = new Insets(0, 10, 10, 10);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = 18;
        jPanel.add((Component)this.m_rbViewNormal, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        jPanel.add((Component)this.m_rbViewSimplified, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_cbMiniOnTop = new JCheckBox(JAPMessages.getString(MSG_MINI_ON_TOP));
        if (JAPDll.getDllVersion() == null && JavaVersionDBEntry.CURRENT_JAVA_VERSION.compareTo("1.5") < 0) {
            this.m_cbMiniOnTop.setEnabled(false);
            this.m_cbMiniOnTop.setToolTipText(JAPMessages.getString(MSG_MINI_ON_TOP_TT));
        }
        jPanel.add((Component)this.m_cbMiniOnTop, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_cbEnableCloseButton = new JCheckBox(JAPMessages.getString(MSG_ENABLE_CLOSE_BUTTON));
        if (JAPDll.getDllVersion() == null) {
            this.m_cbEnableCloseButton.setEnabled(false);
        }
        jPanel.add((Component)this.m_cbEnableCloseButton, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_cbIgnoreDLLUpdate = new JCheckBox(JAPMessages.getString(JAPDll.MSG_IGNORE_UPDATE));
        if (JAPDll.getDllVersion() == null || JAPModel.getInstance().getDllUpdatePath() == null) {
            this.m_cbIgnoreDLLUpdate.setEnabled(false);
        }
        jPanel.add((Component)this.m_cbIgnoreDLLUpdate, gridBagConstraints);
        return jPanel;
    }

    private JPanel createAfterShutdownPanel() {
        TitledGridBagPanel titledGridBagPanel = new TitledGridBagPanel(JAPMessages.getString(MSG_ON_CLOSING_JAP), new Insets(0, 10, 0, 10));
        this.m_cbWarnOnClose = new JCheckBox(JAPMessages.getString(MSG_WARNING_ON_CLOSING_JAP));
        this.m_cbWarnOnClose.setEnabled(!JAPController.getInstance().isPortableMode());
        titledGridBagPanel.addRow(this.m_cbWarnOnClose, null);
        return titledGridBagPanel;
    }

    private JPanel createAfterStartupPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = new JPanel(gridBagLayout);
        jPanel.setBorder(new TitledBorder(JAPMessages.getString("ngSettingsStartBorder")));
        this.m_cbAfterStart = new JCheckBox(JAPMessages.getString("ngViewAfterStart"));
        this.m_cbAfterStart.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                boolean bl = JAPConfUI.this.m_cbAfterStart.isSelected();
                JAPConfUI.this.updateThirdPanel(bl);
            }
        });
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)this.m_cbAfterStart, gridBagConstraints);
        this.m_rbViewMini = new JRadioButton(JAPMessages.getString("ngViewMini"));
        this.m_rbViewSystray = new JRadioButton(JAPMessages.getString("ngViewSystray"));
        if (JAPDll.getDllVersion() == null) {
            if (AbstractOS.getInstance() instanceof WindowsOS) {
                this.m_rbViewSystray.setToolTipText(JAPMessages.getString(MSG_NO_NATIVE_WINDOWS_LIBRARY));
            } else {
                this.m_rbViewSystray.setToolTipText(JAPMessages.getString(MSG_NO_NATIVE_LIBRARY));
            }
        }
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.m_rbViewMini);
        buttonGroup.add(this.m_rbViewSystray);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 30, 0, 10);
        jPanel.add((Component)this.m_rbViewMini, gridBagConstraints);
        gridBagConstraints.gridy = 2;
        jPanel.add((Component)this.m_rbViewSystray, gridBagConstraints);
        this.m_cbShowSplash = new JCheckBox(JAPMessages.getString("ngViewShowSplash"));
        this.m_cbShowSplash.setEnabled(!JAPModel.getInstance().getShowSplashDisabled());
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        jPanel.add((Component)this.m_cbShowSplash, gridBagConstraints);
        this.m_cbStartPortableFirefox = new JCheckBox(JAPMessages.getString("ngViewStartPortableFirefox"));
        this.m_cbStartPortableFirefox.setEnabled(JAPController.getInstance().isPortableMode());
        gridBagConstraints.gridy = 4;
        jPanel.add((Component)this.m_cbStartPortableFirefox, gridBagConstraints);
        return jPanel;
    }

    private JPanel createBrowserPathPanel() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_BROWSER_PATH)));
        gridBagConstraints.weightx = 1.0;
        this.m_portableBrowserPathField = new JTextField(10);
        this.m_portableBrowserPathField.setEditable(false);
        this.m_portableBrowserPathButton = new JButton(JAPMessages.getString(MSG_BROWSER_PATH_CHOOSE));
        if (JAPModel.getInstance().getPortableBrowserpath() != null) {
            this.m_portableBrowserPathField.setText(JAPModel.getInstance().getPortableBrowserpath());
        } else if (AbstractOS.getInstance().getDefaultBrowserPath() != null) {
            this.m_portableBrowserPathField.setText(AbstractOS.getInstance().getDefaultBrowserPath());
        } else {
            this.m_portableBrowserPathField.setText("");
        }
        ActionListener actionListener = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConfUI.this.chooseBrowserPath();
            }
        };
        this.m_portableBrowserPathButton.addActionListener(actionListener);
        if (!JAPController.getInstance().isPortableMode()) {
            this.m_portableBrowserPathButton.setEnabled(false);
        }
        gridBagConstraints.anchor = 18;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)this.m_portableBrowserPathField, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_portableBrowserPathButton, gridBagConstraints);
        return jPanel;
    }

    private JPanel createHelpPathPanel() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(new TitledBorder(JAPMessages.getString(MSG_HELP_PATH)));
        gridBagConstraints.weightx = 1.0;
        this.m_helpPathField = new JTextField(10);
        this.m_helpPathField.setEditable(false);
        this.m_helpPathButton = new JButton(JAPMessages.getString(MSG_HELP_PATH_CHOOSE));
        if (JAPModel.getInstance().isHelpPathDefined()) {
            this.m_helpPathField.setText(JAPModel.getInstance().getHelpPath());
        } else {
            this.m_helpPathField.setText("");
        }
        ActionListener actionListener = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPModel jAPModel = JAPModel.getInstance();
                File file = null;
                JFileChooser jFileChooser = new JFileChooser(jAPModel.getHelpPath());
                jFileChooser.setFileSelectionMode(1);
                if (GUIUtils.showMonitoredFileChooser(jFileChooser, JAPConfUI.this.getRootPanel(), "__FILE_CHOOSER_OPEN") == 0) {
                    file = jFileChooser.getSelectedFile();
                }
                if (file != null) {
                    String string = jAPModel.helpPathValidityCheck(file);
                    if (string.equals("HELP_IS_VALID") || string.equals("helpJonDoExists")) {
                        JAPConfUI.this.m_helpPathField.setText(file.getPath());
                        JAPConfUI.this.m_helpPathField.repaint();
                    } else {
                        JAPDialog.showErrorDialog((JAPDialog)JAPConf.getInstance(), JAPMessages.getString(string) + " " + JAPMessages.getString(MSG_CHOOSE_OTHER_DIR));
                    }
                }
            }
        };
        this.m_helpPathButton.addActionListener(actionListener);
        if (!JAPModel.getInstance().isHelpPathChangeable()) {
            this.m_helpPathButton.setEnabled(false);
        }
        gridBagConstraints.anchor = 18;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)this.m_helpPathField, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 0.0;
        jPanel.add((Component)this.m_helpPathButton, gridBagConstraints);
        return jPanel;
    }

    private void submitHelpPathChange() {
        if (this.m_helpPathField == null) {
            return;
        }
        final JAPModel jAPModel = JAPModel.getInstance();
        String string = JAPModel.getInstance().helpPathValidityCheck(this.m_helpPathField.getText());
        if (!(!string.equals("HELP_IS_VALID") && !string.equals("helpJonDoExists") || jAPModel.getHelpPath() != null && jAPModel.getHelpPath().equals(this.m_helpPathField.getText()))) {
            JAPDialog jAPDialog = new JAPDialog(this.getRootPanel(), JAPMessages.getString(JAPExternalHelpViewer.MSG_HELP_INSTALL));
            Runnable runnable = new Runnable(){

                public void run() {
                    jAPModel.setHelpPath(new File(JAPConfUI.this.m_helpPathField.getText()));
                }
            };
            WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString("helpInstallProgress"), runnable, jAPModel.getHelpFileStorageObservable());
            workerContentPane.updateDialog();
            jAPDialog.setResizable(false);
            jAPDialog.setVisible(true);
            if (workerContentPane.getProgressStatus() != 0) {
                this.resetHelpPath();
                JAPDialog.showErrorDialog((JAPDialog)JAPConf.getInstance(), JAPMessages.getString(JAPExternalHelpViewer.MSG_HELP_INSTALL_FAILED));
            }
        }
    }

    private void resetHelpPath() {
        if (this.m_helpPathField != null) {
            this.m_helpPathField.setText("");
        }
    }

    private void updateHelpPath() {
        if (this.m_helpPathField != null) {
            if (JAPModel.getInstance().isHelpPathDefined() && JAPModel.getInstance().isHelpPathChangeable()) {
                this.m_helpPathField.setText(JAPModel.getInstance().getHelpPath());
            } else {
                this.m_helpPathField.setText("");
            }
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("ngUIPanelTitle");
    }

    protected void onCancelPressed() {
        this.updateValues(false);
    }

    protected boolean onOkPressed() {
        JAPModel jAPModel = JAPModel.getInstance();
        int n = jAPModel.getFontSize();
        if (jAPModel.setFontSize(this.m_slidFontSize.getValue())) {
            if (!jAPModel.isConfigWindowSizeSaved()) {
                this.beforePack();
                JAPConf.getInstance().doPack();
                this.afterPack();
            }
        } else {
            n = -1;
        }
        jAPModel.setSaveMainWindowPosition(this.m_cbSaveWindowLocationMain.isSelected());
        jAPModel.setSaveConfigWindowPosition(this.m_cbSaveWindowLocationConfig.isSelected());
        jAPModel.setSaveIconifiedWindowPosition(this.m_cbSaveWindowLocationIcon.isSelected());
        jAPModel.setSaveHelpWindowPosition(this.m_cbSaveWindowLocationHelp.isSelected());
        jAPModel.setSaveHelpWindowSize(this.m_cbSaveWindowSizeHelp.isSelected());
        jAPModel.setSaveConfigWindowSize(this.m_cbSaveWindowSizeConfig.isSelected());
        if (JAPHelp.getHelpDialog() != null) {
            JAPHelp.getHelpDialog().resetAutomaticLocation(this.m_cbSaveWindowLocationHelp.isSelected());
        }
        if (JAPModel.getInstance().isConfigWindowSizeSaved()) {
            JAPModel.getInstance().setConfigSize(JAPConf.getInstance().getSize());
        }
        JAPController.getInstance().setMinimizeOnStartup(this.m_rbViewMini.isSelected() && this.m_cbAfterStart.isSelected());
        JAPController.getInstance().setMoveToSystrayOnStartup(this.m_rbViewSystray.isSelected() && this.m_cbAfterStart.isSelected());
        JAPModel.getInstance().setShowSplashScreen(this.m_cbShowSplash.isSelected());
        JAPModel.getInstance().setStartPortableFirefox(this.m_cbStartPortableFirefox.isSelected());
        JAPModel.getInstance().setNeverRemindGoodbye(!this.m_cbWarnOnClose.isSelected());
        JAPModel.getInstance().setMiniViewOnTop(this.m_cbMiniOnTop.isSelected());
        JAPModel.getInstance().setShowCloseButton(this.m_cbEnableCloseButton.isSelected());
        JAPModel.getInstance().setDllWarning(!this.m_cbIgnoreDLLUpdate.isSelected());
        Locale locale = this.m_comboLanguage.getSelectedIndex() >= 0 ? ((LanguageMapper)this.m_comboLanguage.getSelectedItem()).getLocale() : JAPMessages.getLocale();
        if (!JAPMessages.getLocale().equals(locale)) {
            final Locale locale2 = locale;
            JAPConf.getInstance().addNeedRestart(new JAPConf.AbstractRestartNeedingConfigChange(){

                public String getName() {
                    return JAPMessages.getString("settingsLanguage");
                }

                public void doChange() {
                    JAPMessages.setLocale(locale2);
                }
            });
        }
        int n2 = 1;
        if (this.m_rbViewSimplified.isSelected()) {
            n2 = 2;
        }
        if (JAPModel.getDefaultView() != n2) {
            final int n3 = n2;
            JAPConf.getInstance().addNeedRestart(new JAPConf.AbstractRestartNeedingConfigChange(){

                public String getName() {
                    return JAPMessages.getString("ngSettingsViewBorder");
                }

                public void doChange() {
                    JAPController.getInstance().setDefaultView(n3);
                }
            });
        }
        if (this.m_portableBrowserPathField.getText() != null && this.m_portableBrowserPathField.getText().trim().length() > 0 && !AbstractOS.toAbsolutePath(this.m_portableBrowserPathField.getText()).equals(AbstractOS.toAbsolutePath(JAPModel.getInstance().getPortableBrowserpath()))) {
            JAPConf.getInstance().addNeedRestart(new JAPConf.AbstractRestartNeedingConfigChange(){

                public String getName() {
                    return JAPMessages.getString(MSG_BROWSER_PATH);
                }

                public void doChange() {
                    JAPModel.getInstance().setPortableBrowserpath(JAPConfUI.this.m_portableBrowserPathField.getText());
                }
            });
        }
        JAPDialog.setOptimizedFormat(((DialogFormat)this.m_comboDialogFormat.getSelectedItem()).getFormat());
        String string = this.m_comboUI.getSelectedIndex() >= 0 ? UIManager.getInstalledLookAndFeels()[this.m_comboUI.getSelectedIndex()].getClassName() : UIManager.getLookAndFeel().getClass().getName();
        if (UIManager.getLookAndFeel().getClass().getName().equals(string)) {
            string = null;
        }
        if (string != null || n >= 0) {
            final String string2 = string;
            final int n4 = n;
            JAPConf.getInstance().addNeedRestart(new JAPConf.AbstractRestartNeedingConfigChange(){

                public String getName() {
                    return JAPMessages.getString("settingsLookAndFeel");
                }

                public void doChange() {
                    if (string2 != null) {
                        JAPModel.getInstance().setLookAndFeel(string2);
                    }
                }

                public void doCancel() {
                    if (n4 >= 0) {
                        JAPConfUI.this.m_slidFontSize.setValue(n4);
                        JAPModel.getInstance().setFontSize(n4);
                        JAPConfUI.this.beforePack();
                        JAPConf.getInstance().doPack();
                        JAPConfUI.this.afterPack();
                    }
                }
            });
        }
        this.submitHelpPathChange();
        return true;
    }

    private void setLanguageComboIndex(Locale locale) {
        int n;
        LanguageMapper languageMapper = new LanguageMapper(locale.getLanguage());
        for (n = 0; n < this.m_comboLanguage.getItemCount(); ++n) {
            if (!this.m_comboLanguage.getItemAt(n).equals(languageMapper)) continue;
            this.m_comboLanguage.setSelectedIndex(n);
            break;
        }
        if (n == this.m_comboLanguage.getItemCount()) {
            this.m_comboLanguage.setSelectedIndex(0);
        }
    }

    protected void onUpdateValues() {
        this.updateUICombo();
        if (JAPModel.getInstance().getPortableBrowserpath() != null) {
            this.m_portableBrowserPathField.setText(JAPModel.getInstance().getPortableBrowserpath());
        } else {
            this.m_portableBrowserPathField.setText(AbstractOS.getInstance().getDefaultBrowserPath());
        }
        this.m_slidFontSize.setValue(JAPModel.getInstance().getFontSize());
        this.setLanguageComboIndex(JAPMessages.getLocale());
        this.m_cbSaveWindowLocationMain.setSelected(JAPModel.isMainWindowLocationSaved());
        this.m_cbSaveWindowLocationConfig.setSelected(JAPModel.getInstance().isConfigWindowLocationSaved());
        this.m_cbSaveWindowLocationIcon.setSelected(JAPModel.getInstance().isIconifiedWindowLocationSaved());
        this.m_cbSaveWindowLocationHelp.setSelected(JAPModel.getInstance().isHelpWindowLocationSaved());
        this.m_cbSaveWindowSizeHelp.setSelected(JAPModel.getInstance().isHelpWindowSizeSaved());
        this.m_cbSaveWindowSizeConfig.setSelected(JAPModel.getInstance().isConfigWindowSizeSaved());
        this.m_rbViewNormal.setSelected(JAPModel.getDefaultView() == 1);
        this.m_rbViewSimplified.setSelected(JAPModel.getDefaultView() == 2);
        this.m_rbViewSystray.setSelected(JAPModel.getMoveToSystrayOnStartup());
        this.m_rbViewMini.setSelected(JAPModel.getMinimizeOnStartup());
        this.m_cbMiniOnTop.setSelected(JAPModel.getInstance().isMiniViewOnTop());
        this.m_cbEnableCloseButton.setSelected(JAPModel.getInstance().isCloseButtonShown());
        this.m_cbIgnoreDLLUpdate.setSelected(!JAPModel.getInstance().isDLLWarningActive());
        this.m_cbWarnOnClose.setSelected(!JAPModel.getInstance().isNeverRemindGoodbye());
        this.m_cbShowSplash.setSelected(JAPModel.getInstance().getShowSplashScreen());
        this.m_cbStartPortableFirefox.setSelected(JAPModel.getInstance().getStartPortableFirefox());
        boolean bl = JAPModel.getMoveToSystrayOnStartup() || JAPModel.getMinimizeOnStartup();
        for (int i = 0; i < this.m_comboDialogFormat.getItemCount(); ++i) {
            if (((DialogFormat)this.m_comboDialogFormat.getItemAt(i)).getFormat() != JAPDialog.getOptimizedFormat()) continue;
            this.m_comboDialogFormat.setSelectedIndex(i);
            break;
        }
        this.updateThirdPanel(bl);
        this.updateHelpPath();
    }

    public void onResetToDefaultsPressed() {
        this.setLanguageComboIndex(JAPMessages.getSystemLocale());
        UIManager.LookAndFeelInfo[] arrlookAndFeelInfo = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < arrlookAndFeelInfo.length; ++i) {
            if (!arrlookAndFeelInfo[i].getClassName().equals(UIManager.getCrossPlatformLookAndFeelClassName())) continue;
            this.m_comboUI.setSelectedIndex(i);
            break;
        }
        this.m_portableBrowserPathField.setText(AbstractOS.getInstance().getDefaultBrowserPath());
        this.m_cbSaveWindowLocationConfig.setSelected(false);
        this.m_cbSaveWindowLocationIcon.setSelected(true);
        this.m_cbSaveWindowLocationMain.setSelected(true);
        this.m_cbSaveWindowLocationHelp.setSelected(false);
        this.m_cbSaveWindowSizeHelp.setSelected(false);
        this.m_cbSaveWindowSizeConfig.setSelected(false);
        this.m_rbViewNormal.setSelected(false);
        this.m_rbViewSimplified.setSelected(true);
        this.m_rbViewSystray.setSelected(false);
        this.m_rbViewMini.setSelected(false);
        this.m_cbMiniOnTop.setSelected(true);
        this.m_cbEnableCloseButton.setSelected(false);
        this.m_cbIgnoreDLLUpdate.setSelected(false);
        this.m_cbShowSplash.setSelected(true);
        this.m_cbStartPortableFirefox.setSelected(true);
        this.m_cbWarnOnClose.setSelected(true);
        this.updateThirdPanel(false);
        this.resetHelpPath();
    }

    private void updateThirdPanel(boolean bl) {
        this.m_cbAfterStart.setSelected(bl);
        this.m_rbViewMini.setEnabled(bl);
        this.m_rbViewSystray.setEnabled(bl && JAPDll.getDllVersion() != null);
        if (bl && !this.m_rbViewSystray.isSelected() && !this.m_rbViewMini.isSelected()) {
            this.m_rbViewMini.setSelected(true);
        }
    }

    public String getHelpContext() {
        return "appearance";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateUICombo() {
        JComboBox jComboBox = this.m_comboUI;
        synchronized (jComboBox) {
            int n;
            UIManager.LookAndFeelInfo[] arrlookAndFeelInfo = UIManager.getInstalledLookAndFeels();
            Vector<UIManager.LookAndFeelInfo> vector = new Vector<UIManager.LookAndFeelInfo>(arrlookAndFeelInfo.length);
            Vector<String> vector2 = new Vector<String>(arrlookAndFeelInfo.length);
            String string = UIManager.getLookAndFeel().getClass().getName();
            for (n = 0; n < arrlookAndFeelInfo.length; ++n) {
                if (vector2.contains(arrlookAndFeelInfo[n].getClassName())) continue;
                vector2.addElement(arrlookAndFeelInfo[n].getClassName());
                vector.addElement(arrlookAndFeelInfo[n]);
            }
            arrlookAndFeelInfo = new UIManager.LookAndFeelInfo[vector.size()];
            for (n = 0; n < arrlookAndFeelInfo.length; ++n) {
                arrlookAndFeelInfo[n] = (UIManager.LookAndFeelInfo)vector.elementAt(n);
            }
            UIManager.setInstalledLookAndFeels(arrlookAndFeelInfo);
            this.m_comboUI.removeAllItems();
            for (n = 0; n < arrlookAndFeelInfo.length; ++n) {
                this.m_comboUI.addItem(arrlookAndFeelInfo[n].getName());
            }
            for (n = 0; n < arrlookAndFeelInfo.length; ++n) {
                if (!arrlookAndFeelInfo[n].getClassName().equals(string)) continue;
                this.m_comboUI.setSelectedIndex(n);
                break;
            }
            if (n >= arrlookAndFeelInfo.length) {
                this.m_comboUI.addItem("(unknown)");
                this.m_comboUI.setSelectedIndex(n);
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

    private class DialogFormat {
        String m_description;
        int m_format;

        public DialogFormat(String string, int n) {
            this.m_description = string;
            this.m_format = n;
        }

        public String toString() {
            return this.m_description;
        }

        public int getFormat() {
            return this.m_format;
        }
    }
}

