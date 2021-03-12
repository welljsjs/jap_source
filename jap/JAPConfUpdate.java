/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.Database;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.JavaVersionDBEntry;
import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.JAPConstants;
import jap.JAPController;
import jap.JAPModel;
import jap.SoftwareUpdater;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import logging.LogHolder;
import logging.LogType;
import update.JAPUpdateWizard;

final class JAPConfUpdate
extends AbstractJAPConfModule
implements ActionListener,
ItemListener,
Runnable,
Observer {
    public static final String MSG_DO_EXTERNAL_UPDATE = (class$jap$JAPConfUpdate == null ? (class$jap$JAPConfUpdate = JAPConfUpdate.class$("jap.JAPConfUpdate")) : class$jap$JAPConfUpdate).getName() + ".doExternalUpdate";
    private static final String COMMAND_UPGRADE = "UPGRADE";
    private static final String COMMAND_CHECKFORUPGRADE = "CHECKFORUPGRADE";
    private static final String MSG_ALLOW_DIRECT_CONN = (class$jap$JAPConfUpdate == null ? (class$jap$JAPConfUpdate = JAPConfUpdate.class$("jap.JAPConfUpdate")) : class$jap$JAPConfUpdate).getName() + "_allowDirectConnection";
    private static final String MSG_REMIND_OPTIONAL_UPDATE = (class$jap$JAPConfUpdate == null ? (class$jap$JAPConfUpdate = JAPConfUpdate.class$("jap.JAPConfUpdate")) : class$jap$JAPConfUpdate).getName() + "_remindOptionalUpdate";
    private static final String MSG_REMIND_JAVA_UPDATE = (class$jap$JAPConfUpdate == null ? (class$jap$JAPConfUpdate = JAPConfUpdate.class$("jap.JAPConfUpdate")) : class$jap$JAPConfUpdate).getName() + "_remindJavaUpdate";
    private static final String MSG_INFO = (class$jap$JAPConfUpdate == null ? (class$jap$JAPConfUpdate = JAPConfUpdate.class$("jap.JAPConfUpdate")) : class$jap$JAPConfUpdate).getName() + "_info";
    private JTextArea m_taInfo;
    private JScrollPane m_taInfoScrollPane;
    private JLabel m_labelVersion;
    private JLabel m_labelDate;
    private JComboBox m_comboType;
    private JButton m_bttnUpgrade;
    private JButton m_bttnCheckForUpgrade;
    private JComboBox m_comboAnonymousConnection;
    private JCheckBox m_cbxRemindOptionalUpdate;
    private JCheckBox m_cbxRemindJavaUpdate;
    private Thread m_threadGetVersionInfo;
    private JAPVersionInfo m_devVersion;
    private JAPVersionInfo m_releaseVersion;
    private DateFormat m_DateFormat;
    static /* synthetic */ Class class$jap$JAPConfUpdate;
    static /* synthetic */ Class class$anon$infoservice$JAPVersionInfo;

    public JAPConfUpdate() {
        super(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                JAPModel.getInstance().addObserver(this);
                return true;
            }
        }
        return false;
    }

    public void recreateRootPanel() {
        Serializable serializable;
        Serializable serializable2;
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        JPanel jPanel2 = new JPanel();
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        jPanel2.setLayout(gridBagLayout2);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = -1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 3;
        gridBagConstraints.anchor = 17;
        this.m_bttnUpgrade = new JButton(JAPMessages.getString("confUpgrade"));
        this.m_bttnUpgrade.addActionListener(this);
        this.m_bttnUpgrade.setActionCommand(COMMAND_UPGRADE);
        gridBagConstraints.anchor = 10;
        gridBagConstraints.gridx = 1;
        gridBagLayout2.setConstraints(this.m_bttnUpgrade, gridBagConstraints);
        this.m_bttnUpgrade.setEnabled(false);
        jPanel2.add(this.m_bttnUpgrade);
        this.m_bttnCheckForUpgrade = new JButton(JAPMessages.getString("confCheckForUpgrade"));
        this.m_bttnCheckForUpgrade.setIcon(GUIUtils.loadImageIcon("reload.gif", true, false));
        this.m_bttnCheckForUpgrade.setDisabledIcon(GUIUtils.loadImageIcon("reloaddisabled_anim.gif", true, false));
        this.m_bttnCheckForUpgrade.setPressedIcon(GUIUtils.loadImageIcon("reloadrollover.gif", true, false));
        this.m_bttnCheckForUpgrade.addActionListener(this);
        this.m_bttnCheckForUpgrade.setActionCommand(COMMAND_CHECKFORUPGRADE);
        gridBagConstraints.anchor = 10;
        gridBagConstraints.gridx = 0;
        gridBagLayout2.setConstraints(this.m_bttnCheckForUpgrade, gridBagConstraints);
        this.m_bttnCheckForUpgrade.setEnabled(true);
        jPanel2.add(this.m_bttnCheckForUpgrade);
        gridBagLayout2 = new GridBagLayout();
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        TitledBorder titledBorder = new TitledBorder(" " + JAPMessages.getString("updateTitleBorderInstalled") + " ");
        JPanel jPanel3 = new JPanel(gridBagLayout2);
        jPanel3.setBorder(titledBorder);
        JLabel jLabel = new JLabel("Version: ");
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.anchor = 18;
        gridBagConstraints2.weighty = 0.33;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel3.add(jLabel);
        jLabel = new JLabel("00.20.001");
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel3.add(jLabel);
        jLabel = new JLabel(JAPMessages.getString("updateLabelDate") + " ");
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel3.add(jLabel);
        String string = JAPConstants.strReleaseDate;
        try {
            serializable2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
            try {
                serializable = ((DateFormat)serializable2).parse(string + " GMT");
            }
            catch (ParseException parseException) {
                serializable2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                serializable = ((DateFormat)serializable2).parse(string + " GMT");
            }
            this.m_DateFormat = DateFormat.getDateTimeInstance(2, 2);
            string = this.m_DateFormat.format((Date)serializable);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
        }
        jLabel = new JLabel(string);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 1;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel3.add(jLabel);
        jLabel = new JLabel(JAPMessages.getString("updateType") + ": ");
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel3.add(jLabel);
        jLabel = new JLabel(JAPMessages.getString("updateReleaseVersion"));
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 1;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel3.add(jLabel);
        gridBagLayout2 = new GridBagLayout();
        titledBorder = new TitledBorder(" " + JAPMessages.getString("updateTitleBorderLatest") + " ");
        serializable2 = new JPanel(gridBagLayout2);
        ((JComponent)serializable2).setBorder(titledBorder);
        jLabel = new JLabel("Version: ");
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        ((Container)serializable2).add(jLabel);
        this.m_labelVersion = new JLabel(JAPMessages.getString("updateUnknown"));
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 1;
        gridBagLayout2.setConstraints(this.m_labelVersion, gridBagConstraints2);
        ((Container)serializable2).add(this.m_labelVersion);
        jLabel = new JLabel(JAPMessages.getString("updateLabelDate") + " ");
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        ((Container)serializable2).add(jLabel);
        this.m_labelDate = new JLabel(JAPMessages.getString("updateUnknown"));
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 1;
        gridBagLayout2.setConstraints(this.m_labelDate, gridBagConstraints2);
        ((Container)serializable2).add(this.m_labelDate);
        jLabel = new JLabel(JAPMessages.getString("updateType") + ": ");
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = 0;
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        ((Container)serializable2).add(jLabel);
        this.m_comboType = new JComboBox();
        this.m_comboType.addItem(JAPMessages.getString("updateReleaseVersion"));
        this.m_comboType.addItem(JAPMessages.getString("updateDevelopmentVersion"));
        this.m_comboType.setEnabled(false);
        this.m_comboType.addItemListener(this);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 1;
        gridBagLayout2.setConstraints(this.m_comboType, gridBagConstraints2);
        ((Container)serializable2).add(this.m_comboType);
        serializable = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        this.m_taInfo = new JTextArea();
        this.m_taInfo.setEditable(false);
        this.m_taInfo.setHighlighter(null);
        this.m_taInfoScrollPane = new JScrollPane(this.m_taInfo);
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.anchor = 18;
        ((Container)serializable).add(new JLabel(JAPMessages.getString(MSG_INFO)), gridBagConstraints3);
        ++gridBagConstraints3.gridy;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.insets = new Insets(10, 0, 0, 0);
        ((Container)serializable).add(this.m_taInfoScrollPane, gridBagConstraints3);
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.weightx = 0.0;
        gridBagConstraints4.weighty = 0.0;
        gridBagConstraints4.anchor = 18;
        gridBagConstraints4.fill = 1;
        gridBagLayout.setConstraints(jPanel3, gridBagConstraints4);
        jPanel.add(jPanel3);
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.gridy = 0;
        jPanel.add((Component)serializable2, gridBagConstraints4);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 2;
        gridBagConstraints4.gridwidth = 2;
        JPanel jPanel4 = new JPanel();
        jPanel4.add(new JLabel(JAPMessages.getString(MSG_ALLOW_DIRECT_CONN) + ":"));
        String[] arrstring = new String[JAPModel.getMsgConnectionAnonymous().length];
        System.arraycopy(JAPModel.getMsgConnectionAnonymous(), 0, arrstring, 0, arrstring.length);
        for (int i = 0; i < arrstring.length; ++i) {
            arrstring[i] = JAPMessages.getString(arrstring[i]);
        }
        this.m_comboAnonymousConnection = new JComboBox<String>(arrstring);
        jPanel4.add(this.m_comboAnonymousConnection);
        gridBagConstraints4.fill = 0;
        jPanel.add((Component)jPanel4, gridBagConstraints4);
        ++gridBagConstraints4.gridy;
        this.m_cbxRemindOptionalUpdate = new JCheckBox(JAPMessages.getString(MSG_REMIND_OPTIONAL_UPDATE));
        jPanel.add((Component)this.m_cbxRemindOptionalUpdate, gridBagConstraints4);
        ++gridBagConstraints4.gridy;
        this.m_cbxRemindJavaUpdate = new JCheckBox(JAPMessages.getString(MSG_REMIND_JAVA_UPDATE));
        if (JAPController.getInstance().hasPortableJava()) {
            this.m_cbxRemindJavaUpdate.setEnabled(false);
        }
        jPanel.add((Component)this.m_cbxRemindJavaUpdate, gridBagConstraints4);
        ++gridBagConstraints4.gridy;
        gridBagConstraints4.anchor = 10;
        gridBagConstraints4.fill = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.weighty = 1.0;
        gridBagLayout.setConstraints((Component)serializable, gridBagConstraints4);
        jPanel.add((Component)serializable);
        ++gridBagConstraints4.gridy;
        gridBagConstraints4.weighty = 0.0;
        gridBagConstraints4.fill = 2;
        gridBagConstraints4.anchor = 15;
        gridBagLayout.setConstraints(jPanel2, gridBagConstraints4);
        jPanel.add(jPanel2);
        this.updateValues(false);
    }

    public void update(Observable observable, Object object) {
        if (object != null) {
            if (object.equals(JAPModel.CHANGED_ALLOW_UPDATE_DIRECT_CONNECTION)) {
                this.m_comboAnonymousConnection.setSelectedIndex(JAPModel.getInstance().getUpdateAnonymousConnectionSetting());
            } else if (object.equals(JAPModel.CHANGED_NOTIFY_JAP_UPDATES)) {
                this.m_cbxRemindOptionalUpdate.setSelected(JAPModel.getInstance().isReminderForOptionalUpdateActivated());
            } else if (object.equals(JAPModel.CHANGED_NOTIFY_JAVA_UPDATES)) {
                this.m_cbxRemindJavaUpdate.setSelected(JAPModel.getInstance().isReminderForJavaUpdateActivated());
            }
        }
    }

    protected boolean onOkPressed() {
        JAPModel.getInstance().setUpdateAnonymousConnectionSetting(this.m_comboAnonymousConnection.getSelectedIndex());
        JAPModel.getInstance().setReminderForOptionalUpdate(this.m_cbxRemindOptionalUpdate.isSelected());
        JAPModel.getInstance().setReminderForJavaUpdate(this.m_cbxRemindJavaUpdate.isSelected());
        return true;
    }

    public void onResetToDefaultsPressed() {
        this.m_comboAnonymousConnection.setSelectedIndex(0);
        this.m_cbxRemindOptionalUpdate.setSelected(true);
        this.m_cbxRemindJavaUpdate.setSelected(!JAPController.getInstance().isPortableMode());
    }

    protected void onUpdateValues() {
        this.m_comboAnonymousConnection.setSelectedIndex(JAPModel.getInstance().getUpdateAnonymousConnectionSetting());
        this.m_cbxRemindOptionalUpdate.setSelected(JAPModel.getInstance().isReminderForOptionalUpdateActivated());
        this.m_cbxRemindJavaUpdate.setSelected(JAPModel.getInstance().isReminderForJavaUpdateActivated());
    }

    public void run() {
        this.updateVersionInfo(true);
    }

    public void updateVersionInfo(boolean bl) {
        JAPVersionInfo jAPVersionInfo;
        Object object;
        if (bl) {
            this.m_taInfo.setText(JAPMessages.getString("updateFetchVersionInfo"));
            this.m_releaseVersion = InfoServiceHolder.getInstance().getJAPVersionInfo(1);
            this.m_devVersion = InfoServiceHolder.getInstance().getJAPVersionInfo(2);
        } else {
            object = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPConfUpdate.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById("/japDevelopment.jnlp");
            jAPVersionInfo = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPConfUpdate.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById("/japRelease.jnlp");
            if (object != null && jAPVersionInfo != null) {
                this.m_releaseVersion = jAPVersionInfo;
                this.m_devVersion = object;
            } else {
                return;
            }
        }
        if (this.m_releaseVersion == null || this.m_devVersion == null) {
            this.m_taInfo.setText(JAPMessages.getString("updateFetchVersionInfoFailed"));
        } else {
            Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPConfUpdate.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).update(this.m_releaseVersion);
            Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPConfUpdate.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).update(this.m_devVersion);
            this.m_comboType.setEnabled(true);
            object = "";
            jAPVersionInfo = this.m_releaseVersion;
            if ("00.20.001".compareTo(jAPVersionInfo.getJapVersion()) >= 0) {
                object = JAPMessages.getString("japUpdate_YouHaveAlreadyTheNewestVersion");
            } else {
                object = JAPMessages.getString("japUpdate_NewVersionAvailable");
                if (!jAPVersionInfo.isJavaVersionStillSupported()) {
                    object = (String)object + "\n" + JAPMessages.getString(JAPUpdateWizard.MSG_JAVA_TOO_OLD, new Object[]{JavaVersionDBEntry.CURRENT_JAVA_VERSION, jAPVersionInfo.getSupportedJavaVersion()});
                }
            }
            this.m_taInfo.setText((String)object);
            this.m_taInfoScrollPane.getHorizontalScrollBar().setValue(0);
            this.m_labelVersion.setText(this.m_releaseVersion.getJapVersion());
            if (this.m_releaseVersion.getDate() != null) {
                this.m_labelDate.setText(this.m_DateFormat.format(this.m_releaseVersion.getDate()));
            } else {
                this.m_labelDate.setText(JAPMessages.getString("updateUnknown"));
            }
            this.m_bttnUpgrade.setEnabled(true);
            this.m_comboType.setSelectedIndex(0);
            this.itemStateChanged(new ItemEvent(this.m_comboType, 0, this.m_comboType, 1));
        }
        this.m_bttnCheckForUpgrade.setEnabled(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(COMMAND_UPGRADE)) {
            if (!AbstractOS.getInstance().isJavaWebstart()) {
                try {
                    this.m_threadGetVersionInfo.join();
                }
                catch (NullPointerException nullPointerException) {
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, exception);
                }
                if (this.m_comboType.getSelectedIndex() == 0) {
                    SoftwareUpdater.show(this.m_releaseVersion, this.getRootPanel());
                } else {
                    SoftwareUpdater.show(this.m_devVersion, this.getRootPanel());
                }
            } else {
                JAPDialog.showMessageDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString("webstartUpdateButton"), JAPMessages.getString("newVersionAvailableTitle"));
            }
        } else if (actionEvent.getActionCommand().equals(COMMAND_CHECKFORUPGRADE)) {
            this.m_bttnCheckForUpgrade.setEnabled(false);
            this.m_threadGetVersionInfo = new Thread(this);
            this.m_threadGetVersionInfo.start();
        }
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 1) {
            if (this.m_comboType.getSelectedIndex() == 0) {
                this.m_labelVersion.setText(this.m_releaseVersion.getJapVersion());
                if (this.m_releaseVersion.getDate() != null) {
                    this.m_labelDate.setText(this.m_DateFormat.format(this.m_releaseVersion.getDate()));
                } else {
                    this.m_labelDate.setText(JAPMessages.getString("updateUnknown"));
                }
            } else {
                this.m_labelVersion.setText(this.m_devVersion.getJapVersion());
                if (this.m_devVersion.getDate() != null) {
                    this.m_labelDate.setText(this.m_DateFormat.format(this.m_devVersion.getDate()));
                } else {
                    this.m_labelDate.setText(JAPMessages.getString("updateUnknown"));
                }
            }
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("ngUpdatePanelTitle");
    }

    public String getHelpContext() {
        return "update";
    }

    protected void onRootPanelShown() {
        this.updateVersionInfo(false);
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                JAPConfUpdate.this.m_taInfoScrollPane.getHorizontalScrollBar().setValue(0);
            }
        });
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

