/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.Database;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.JavaVersionDBEntry;
import anon.infoservice.ListenerInterface;
import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.IProgressCallback;
import anon.util.IProgressCapsule;
import anon.util.JAPMessages;
import anon.util.RecursiveFileTool;
import anon.util.Util;
import anon.util.ZipArchiver;
import gui.GUIUtils;
import gui.dialog.DialogContentPane;
import gui.dialog.FileChooserContentPane;
import gui.dialog.FinishedContentPane;
import gui.dialog.JAPDialog;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.WorkerContentPane;
import jap.JAPController;
import jap.JAPModel;
import jap.gui.LinkRegistrator;
import jarify.JarVerifier;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.ZipFile;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import logging.LogHolder;
import logging.LogType;
import update.JAPUpdateWizard;
import update.JAPWelcomeWizardPage;

public class SoftwareUpdater {
    public static final int TYPE_CHOOSE = 0;
    public static final int TYPE_STABLE = 1;
    public static final int TYPE_BETA = 2;
    private static final String MSG_ERROR_NO_BACKUP = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".errorNoBackup";
    private static final String MSG_ERROR_DOWNLOAD_FAILED = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".downloadFailed";
    private static final String MSG_ERROR_SAVING_UPDATE_FAILED = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".savingUpdateFailed";
    private static final String MSG_ERROR_VERIFYING_UPDATE_FAILED = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".verifyingUpdateFailed";
    private static final String MSG_ERROR_CHECKING_UPDATE_VERSION = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".errorCheckingUpdateVersion";
    private static final String MSG_ERROR_UNKNOWN = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".errorUnknown";
    private static final String MSG_ERROR_OVERWRITING_FAILED = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".overwritingFailed";
    private static final String MSG_NO_VERSIONS_FOUND = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".errorNoVersionsFound";
    private static final String MSG_HELP_NO_CONNECTION = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".helpNoConnection";
    private static final String MSG_ERROR_RECOMMEND = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".errorRecommend";
    private static final String MSG_FILE_DIRECT_DOWNLOAD = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".fileDirectDownload";
    private static final String MSG_FILE_INSTALLATION = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".fileInstallation";
    private static final String MSG_FILE_INSTALLATION_PATH = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".fileInstallationPath";
    private static final String MSG_FINISHED = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".finished";
    private static final String MSG_RESTART = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".restart";
    private static final String MSG_CHOOSE_VERSION = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".chooseVersion";
    private static final String MSG_UPDATE_JAVA = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".updateJava";
    private static final String MSG_NO_BASE_JAR = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".noBaseJar";
    private static final String MSG_DOWNLOAD_REMAINING = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".downloadRemaining";
    private static final String MSG_SHOW_CHANGES = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".showChanges";
    private static final String MSG_HEAD_UPDATE = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".headUpdate";
    private static final String MSG_CREATE_BACKUP = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".createBackup";
    private static final String MSG_DOWNLOAD_UPDATE = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".downloadUpdate";
    private static final String MSG_SAVE_UPDATE = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".saveUpdate";
    private static final String MSG_VERIFY_UPDATE = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".verifyUpdate";
    private static final String MSG_OVERWRITE = (class$jap$SoftwareUpdater == null ? (class$jap$SoftwareUpdater = SoftwareUpdater.class$("jap.SoftwareUpdater")) : class$jap$SoftwareUpdater).getName() + ".overwrite";
    private static final String ICON_FINISHED = "&#10004;";
    private static final String ICON_BULLET = "&#8226;";
    private final String STR_MESSAGE = "<html>{3} {4}" + JAPMessages.getString(MSG_CREATE_BACKUP) + "...{5}<br>" + "{6} {7}" + JAPMessages.getString(MSG_DOWNLOAD_UPDATE) + "...{8}<br>" + "{9} {10}" + JAPMessages.getString(MSG_SAVE_UPDATE) + "...{11}<br>" + "{12} {13}" + JAPMessages.getString(MSG_VERIFY_UPDATE) + "...{14}<br>" + "{15} {16}" + JAPMessages.getString(MSG_OVERWRITE) + "...{17}" + "</html>";
    private static final File CLASSFILE = ClassUtil.getClassDirectory(ClassUtil.getClassStatic());
    private static final ZipFile JARFILE = ClassUtil.getJarFile(ClassUtil.getClassStatic());
    private JAPVersionInfo m_releaseVersion;
    private JAPVersionInfo m_devVersion;
    private JRadioButton m_radioRelease;
    private JRadioButton m_radioBeta;
    private ButtonGroup m_groupVersion;
    private DateFormat m_dateFormat = DateFormat.getDateInstance(2);
    private JAPController.IRestarter m_Restarter;
    private LinkRegistrator m_registrator;
    private JAPDialog m_dialog;
    private WorkerContentPane paneFetchUpdate;
    private WorkerContentPane initialPane;
    private int m_iType;
    private static final int ACTION_INIT = -1;
    private static final int ACTION_CREATE_BACKUP = 1;
    private static final int ACTION_DOWNLOAD_UPDATE = 2;
    private static final int ACTION_SAVE_UPDATE = 3;
    private static final int ACTION_VERIFY_NEW = 4;
    private static final int ACTION_OVERWRITE = 5;
    private static final Object SYNC_INSTANCE = new Object();
    private static boolean m_bIsShown = false;
    static /* synthetic */ Class class$jap$SoftwareUpdater;
    static /* synthetic */ Class class$anon$infoservice$JAPVersionInfo;

    private String createMessage(int n, boolean bl) {
        return this.createMessage(n, bl, null);
    }

    private String createMessage(int n, boolean bl, String string) {
        Object[] arrobject = new Object[18];
        for (int i = 0; i < 6; ++i) {
            arrobject[i * 3] = ICON_BULLET;
            arrobject[i * 3 + 1] = "";
            arrobject[i * 3 + 2] = "";
            if (i == n) {
                arrobject[i * 3 + 2] = string == null ? "" : string;
                if (bl) {
                    arrobject[i * 3] = ICON_FINISHED;
                    if (string == null) {
                        arrobject[i * 3 + 2] = "";
                    }
                } else {
                    arrobject[i * 3] = ICON_BULLET;
                }
                arrobject[i * 3 + 1] = "<b>";
                arrobject[i * 3 + 2] = (String)arrobject[i * 3 + 2] + "</b>";
                continue;
            }
            if (i >= n) continue;
            arrobject[i * 3] = ICON_FINISHED;
        }
        return MessageFormat.format(this.STR_MESSAGE, arrobject);
    }

    public static boolean isShown() {
        return m_bIsShown;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void show(JAPVersionInfo jAPVersionInfo, Component component) {
        Object object = SYNC_INSTANCE;
        synchronized (object) {
            if (m_bIsShown) {
                return;
            }
            m_bIsShown = true;
        }
        object = new SoftwareUpdater(jAPVersionInfo, component);
        super.show();
    }

    private SoftwareUpdater(final JAPVersionInfo jAPVersionInfo, Component component) {
        if (component == null) {
            component = JAPController.getInstance().getCurrentView();
        }
        this.m_dialog = new JAPDialog(component, JAPMessages.getString(MSG_HEAD_UPDATE));
        this.m_dialog.setDefaultCloseOperation(0);
        this.m_registrator = new LinkRegistrator(this.m_dialog.getRootPane(), JAPController.getInstance().getView());
        this.m_iType = jAPVersionInfo == null ? 0 : (jAPVersionInfo.getId().equals("/japDevelopment.jnlp") ? 2 : 1);
        this.initialPane = new WorkerContentPane(this.m_dialog, JAPMessages.getString("updateFetchVersionInfo"), new Runnable(){

            public void run() {
                SoftwareUpdater.this.m_releaseVersion = null;
                SoftwareUpdater.this.m_devVersion = null;
                if (jAPVersionInfo == null) {
                    SoftwareUpdater.this.m_releaseVersion = InfoServiceHolder.getInstance().getJAPVersionInfo(1);
                    SoftwareUpdater.this.m_devVersion = InfoServiceHolder.getInstance().getJAPVersionInfo(2);
                    Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = SoftwareUpdater.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).update(SoftwareUpdater.this.m_releaseVersion);
                    Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = SoftwareUpdater.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).update(SoftwareUpdater.this.m_devVersion);
                }
                if (SoftwareUpdater.this.m_releaseVersion == null) {
                    SoftwareUpdater.this.m_releaseVersion = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = SoftwareUpdater.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById("/japRelease.jnlp");
                }
                if (SoftwareUpdater.this.m_devVersion == null) {
                    SoftwareUpdater.this.m_devVersion = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = SoftwareUpdater.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById("/japDevelopment.jnlp");
                }
            }
        }){

            public boolean isMoveBackAllowed() {
                return false;
            }
        };
        final SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(this.m_dialog, "<font color=\"FF0000\">" + JAPMessages.getString(MSG_NO_VERSIONS_FOUND) + JAPMessages.getString(MSG_HELP_NO_CONNECTION) + "</font>", (DialogContentPane)this.initialPane){

            public boolean isSkippedAsNextContentPane() {
                return SoftwareUpdater.this.m_releaseVersion != null && (SoftwareUpdater.this.m_releaseVersion != null || SoftwareUpdater.this.m_devVersion != null);
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        final SimpleWizardContentPane simpleWizardContentPane2 = new SimpleWizardContentPane(this.m_dialog, "Dummy", simpleWizardContentPane){

            public boolean isMoveForwardAllowed() {
                return simpleWizardContentPane.isSkippedAsNextContentPane();
            }

            public DialogContentPane.CheckError checkYesOK() {
                if (SoftwareUpdater.this.m_groupVersion.getSelection() == null) {
                    return new DialogContentPane.CheckError(JAPMessages.getString(MSG_CHOOSE_VERSION));
                }
                JAPVersionInfo jAPVersionInfo = SoftwareUpdater.this.m_radioRelease.isSelected() ? SoftwareUpdater.this.m_releaseVersion : SoftwareUpdater.this.m_devVersion;
                if (!jAPVersionInfo.isJavaVersionStillSupported()) {
                    this.setText(JAPMessages.getString(JAPUpdateWizard.MSG_JAVA_TOO_OLD, new Object[]{JavaVersionDBEntry.CURRENT_JAVA_VERSION, jAPVersionInfo.getSupportedJavaVersion()}));
                    String string = JAPMessages.getString("updateReleaseVersion");
                    if (jAPVersionInfo == SoftwareUpdater.this.m_devVersion) {
                        string = JAPMessages.getString("updateDevelopmentVersion");
                    }
                    string = string + ": " + JAPMessages.getString(MSG_UPDATE_JAVA);
                    return new DialogContentPane.CheckError(string);
                }
                return null;
            }

            public DialogContentPane.CheckError checkUpdate() {
                if (SoftwareUpdater.this.m_releaseVersion != null && SoftwareUpdater.this.m_iType != 2) {
                    SoftwareUpdater.this.m_radioRelease.setText("<html>" + JAPMessages.getString("JAP.version") + ": " + SoftwareUpdater.this.m_releaseVersion.getJapVersion() + "<br/>" + "</html>");
                } else {
                    SoftwareUpdater.this.m_radioRelease.setVisible(false);
                }
                if (SoftwareUpdater.this.m_devVersion != null && SoftwareUpdater.this.m_iType != 1) {
                    SoftwareUpdater.this.m_radioBeta.setText("<html>" + JAPMessages.getString("JAP.version") + ": " + SoftwareUpdater.this.m_devVersion.getJapVersion() + "-beta" + "<br/>" + "(" + JAPMessages.getString("updateDevelopmentVersion") + ")" + "</html>");
                } else {
                    SoftwareUpdater.this.m_radioBeta.setVisible(false);
                }
                JAPVersionInfo jAPVersionInfo = SoftwareUpdater.this.m_iType == 1 || SoftwareUpdater.this.m_iType == 0 ? (SoftwareUpdater.this.m_releaseVersion != null ? SoftwareUpdater.this.m_releaseVersion : SoftwareUpdater.this.m_devVersion) : (SoftwareUpdater.this.m_devVersion != null ? SoftwareUpdater.this.m_devVersion : SoftwareUpdater.this.m_releaseVersion);
                String string = "00.20.001".compareTo(jAPVersionInfo.getJapVersion()) >= 0 ? JAPMessages.getString("japUpdate_YouHaveAlreadyTheNewestVersion") : JAPMessages.getString("japUpdate_NewVersionAvailable");
                if (!SoftwareUpdater.this.m_radioRelease.isVisible()) {
                    SoftwareUpdater.this.m_radioBeta.setSelected(true);
                } else if (!SoftwareUpdater.this.m_radioBeta.isVisible()) {
                    SoftwareUpdater.this.m_radioRelease.setSelected(true);
                } else {
                    SoftwareUpdater.this.m_radioRelease.setSelected(true);
                }
                this.setText(string);
                return null;
            }

            public void doAfterUpdate() {
                if (SoftwareUpdater.this.m_radioRelease.isSelected()) {
                    SoftwareUpdater.this.m_radioRelease.doClick();
                } else if (SoftwareUpdater.this.m_radioBeta.isSelected()) {
                    SoftwareUpdater.this.m_radioBeta.doClick();
                }
            }
        };
        JComponent jComponent = simpleWizardContentPane2.getContentPane();
        jComponent.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = 17;
        this.m_radioRelease = new JRadioButton();
        this.m_radioRelease.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                URL uRL = null;
                try {
                    uRL = new URL(JAPMessages.getString(JAPWelcomeWizardPage.MSG_CHANGELOG_URL) + "#" + SoftwareUpdater.this.m_releaseVersion.getJapVersion());
                }
                catch (MalformedURLException malformedURLException) {
                    LogHolder.log(2, LogType.MISC, malformedURLException);
                }
                simpleWizardContentPane2.printStatusMessage(JAPMessages.getString("updateReleaseVersion") + ": " + JAPMessages.getString(MSG_SHOW_CHANGES), 1, uRL);
            }
        });
        this.m_radioBeta = new JRadioButton();
        this.m_radioBeta.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                URL uRL = null;
                try {
                    uRL = new URL(JAPMessages.getString(JAPWelcomeWizardPage.MSG_CHANGELOG_URL_BETA) + "#" + SoftwareUpdater.this.m_devVersion.getJapVersion() + "-beta");
                }
                catch (MalformedURLException malformedURLException) {
                    LogHolder.log(2, LogType.MISC, malformedURLException);
                }
                simpleWizardContentPane2.printStatusMessage(JAPMessages.getString("updateDevelopmentVersion") + ": " + JAPMessages.getString(MSG_SHOW_CHANGES), 2, uRL);
            }
        });
        this.m_groupVersion = new ButtonGroup();
        this.m_groupVersion.add(this.m_radioRelease);
        this.m_groupVersion.add(this.m_radioBeta);
        jComponent.add((Component)this.m_radioRelease, gridBagConstraints);
        ++gridBagConstraints.gridy;
        jComponent.add((Component)this.m_radioBeta, gridBagConstraints);
        final FileChooserContentPane fileChooserContentPane = new FileChooserContentPane(this.m_dialog, JAPMessages.getString(MSG_NO_BASE_JAR), CLASSFILE.getAbsolutePath(), 2, "__FILE_CHOOSER_OPEN", new FileFilter(){

            public boolean accept(File file) {
                if (file == null) {
                    return false;
                }
                if (file.isDirectory()) {
                    return true;
                }
                return file.getName().endsWith(".jar");
            }

            public String getDescription() {
                return ".jar";
            }
        }, (DialogContentPane)simpleWizardContentPane2){

            public DialogContentPane.CheckError checkYesOK() {
                DialogContentPane.CheckError checkError = super.checkYesOK();
                if (checkError != null) {
                    return checkError;
                }
                if (this.getFile().isDirectory()) {
                    return new DialogContentPane.CheckError(JAPMessages.getString(FileChooserContentPane.MSG_CHOOSE_FILE));
                }
                return null;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return this.isSkippedAsNextContentPane();
            }

            public boolean isSkippedAsNextContentPane() {
                return JARFILE != null;
            }
        };
        final UpdateThread updateThread = new UpdateThread();
        this.paneFetchUpdate = new WorkerContentPane(this.m_dialog, this.createMessage(-1, false), fileChooserContentPane, updateThread, updateThread){

            public boolean hideButtonCancel() {
                return false;
            }

            public DialogContentPane.CheckError checkUpdate() {
                if (updateThread != null) {
                    updateThread.setCurrentFileIfNull(fileChooserContentPane.getFile());
                }
                return super.checkUpdate();
            }
        };
        SimpleWizardContentPane simpleWizardContentPane3 = new SimpleWizardContentPane(this.m_dialog, JAPMessages.getString(MSG_ERROR_UNKNOWN), new DialogContentPane.Layout(JAPMessages.getString(JAPDialog.MSG_TITLE_ERROR), 0), this.paneFetchUpdate){

            public boolean isSkippedAsNextContentPane() {
                return updateThread != null && updateThread.getStatus() == 0;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }

            public boolean isMoveBackAllowed() {
                return false;
            }

            public boolean hideButtonYesOK() {
                return true;
            }

            public DialogContentPane.CheckError checkUpdate() {
                this.getContentPane().removeAll();
                String string = updateThread.getFailedReason();
                if (string == null) {
                    string = JAPMessages.getString(MSG_ERROR_UNKNOWN);
                }
                this.setText("<font color=\"FF0000\">" + string + "</font>");
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = -1;
                gridBagConstraints.anchor = 17;
                SoftwareUpdater.this.m_registrator.addBrowserInstallationInfo(this.getContentPane(), gridBagConstraints, JAPMessages.getString(MSG_FILE_INSTALLATION) + "...", JAPMessages.getString(MSG_FILE_INSTALLATION_PATH), false, 1);
                return null;
            }
        };
        simpleWizardContentPane3.getContentPane().setLayout(new GridBagLayout());
        FinishedContentPane finishedContentPane = new FinishedContentPane(this.m_dialog, "Dummy", simpleWizardContentPane3){

            public DialogContentPane.CheckError checkUpdate() {
                if (updateThread.getUpdatedFile() != null && updateThread.getUpdatedFile().equals(CLASSFILE)) {
                    this.setText(JAPMessages.getString(MSG_FINISHED) + " " + JAPMessages.getString(MSG_RESTART));
                } else {
                    this.setText(JAPMessages.getString(MSG_FINISHED));
                }
                return null;
            }

            public DialogContentPane.CheckError checkYesOK() {
                if (updateThread != null && updateThread.getStatus() == 0 && updateThread.getUpdatedFile() != null && updateThread.getUpdatedFile().equals(CLASSFILE)) {
                    SoftwareUpdater.this.m_Restarter = null;
                    JAPController.goodBye(false);
                }
                return null;
            }
        };
        this.m_dialog.setAlwaysOnTop(true);
        this.m_dialog.setResizable(false);
    }

    private void show() {
        SwingUtilities.invokeLater(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                SoftwareUpdater.this.initialPane.pack();
                SoftwareUpdater.this.m_dialog.setVisible(true);
                SoftwareUpdater.this.m_dialog.dispose();
                if (SoftwareUpdater.this.m_Restarter != null) {
                    JAPController.getInstance().setRestarter(SoftwareUpdater.this.m_Restarter);
                }
                Object object = SYNC_INSTANCE;
                synchronized (object) {
                    m_bIsShown = false;
                }
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

    private class UpdateThread
    extends Observable
    implements Runnable,
    IProgressCapsule,
    IProgressCallback {
        private int m_iValue = 0;
        private int m_iRange = 0;
        private long m_lFileSize = 0L;
        private int m_iStatus = -1;
        private JAPVersionInfo m_versionInfo;
        private String m_strMessage = SoftwareUpdater.access$2300(SoftwareUpdater.this, -1, false);
        private String m_strCurrentVersion = "00.20.001";
        private String m_strFailedReason;
        private File m_fileAktJapJar = SoftwareUpdater.access$1500();
        private File m_fileNewJapJar;
        private File m_fileJapJarCopy;
        private String m_strAktJapJarFileName;
        private String m_strAktJapJarPath;
        private String m_strAktJapJarExtension;
        private String m_strTempDirectory;
        private boolean m_bCommandLineVersion = false;
        private static final String EXTENSION_BACKUP = ".backup";
        private static final String EXTENSION_NEW = ".new";

        private UpdateThread() {
        }

        public String getFailedReason() {
            return this.m_strFailedReason;
        }

        public int getCurrentMaximum() {
            return this.m_iRange;
        }

        public long getCurrentSize() {
            return this.m_lFileSize;
        }

        public void setValue(int n) {
            this.m_iValue = n;
            this.setChanged();
            this.notifyObservers(this);
        }

        private boolean verifyUpdate() {
            String string;
            this.m_iValue = 520;
            this.m_strMessage = SoftwareUpdater.this.createMessage(4, false);
            this.setChanged();
            this.notifyObservers(this);
            if (!JarVerifier.verify(this.m_fileNewJapJar, JAPModel.getJAPCodeSigningCert())) {
                this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_VERIFYING_UPDATE_FAILED);
                LogHolder.log(2, LogType.MISC, "Verifying update file failed!");
                return false;
            }
            this.setValue(560);
            if (this.m_bCommandLineVersion && ((string = this.getJarVersion(this.m_fileNewJapJar)) == null || !string.equals(this.m_versionInfo.getJapVersion()))) {
                this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_CHECKING_UPDATE_VERSION, string);
                LogHolder.log(2, LogType.MISC, "Update has wrong version number: " + string);
                return false;
            }
            this.m_strMessage = SoftwareUpdater.this.createMessage(4, true);
            this.setValue(600);
            return true;
        }

        public void run() {
            int n;
            this.m_iStatus = 1;
            this.m_strFailedReason = null;
            byte[] arrby = null;
            boolean bl = true;
            boolean bl2 = true;
            this.m_versionInfo = SoftwareUpdater.this.m_radioRelease.isSelected() ? SoftwareUpdater.this.m_releaseVersion : SoftwareUpdater.this.m_devVersion;
            this.parsePathToJapJar();
            bl2 = this.renameJapJar();
            if (this.m_iStatus != 1) {
                this.resetChanges(true);
                return;
            }
            boolean bl3 = false;
            URL[] arruRL = this.m_versionInfo.getCodeBase();
            boolean[] arrbl = new boolean[arruRL.length];
            for (n = 0; n < arrbl.length; ++n) {
                arrbl[n] = false;
            }
            for (n = 0; n < arruRL.length && !Thread.currentThread().isInterrupted(); ++n) {
                URL uRL = arruRL[n];
                URL uRL2 = null;
                try {
                    if (!arrbl[n] && bl2 && this.m_versionInfo.isIncrementalAllowed(n)) {
                        bl = true;
                        uRL2 = new URL(uRL, this.m_versionInfo.getJAPJarFileName() + "?version-id=" + this.m_versionInfo.getJapVersion() + "&current-version-id=" + this.m_strCurrentVersion);
                    } else {
                        bl = false;
                        uRL2 = new URL(uRL, this.m_versionInfo.getJAPJarFileName() + "?version-id=" + this.m_versionInfo.getJapVersion());
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, exception);
                    continue;
                }
                int n2 = uRL2.getPort();
                if (n2 == -1) {
                    n2 = 80;
                }
                if ((arrby = this.doDownload(new ListenerInterface(uRL2.getHost(), n2), uRL2.getFile())) == null) {
                    this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_DOWNLOAD_FAILED) + JAPMessages.getString(MSG_HELP_NO_CONNECTION);
                    continue;
                }
                if (bl) {
                    if (!this.applyJARDiffJAPJar(arrby)) {
                        arrbl[n] = true;
                        --n;
                        continue;
                    }
                } else if (!this.createNewJAPJar(arrby)) continue;
                if (this.verifyUpdate()) {
                    bl3 = true;
                    break;
                }
                if (!bl) continue;
                arrbl[n] = true;
                --n;
            }
            if (!bl3) {
                this.m_iStatus = 3;
                this.resetChanges(true);
                return;
            }
            if (Thread.currentThread().isInterrupted()) {
                this.m_iStatus = 2;
                this.resetChanges(true);
                return;
            }
            if (this.overwriteJapJar()) {
                this.m_iStatus = 0;
                this.resetChanges(false);
            } else {
                this.resetChanges(true);
                if (Thread.currentThread().isInterrupted()) {
                    this.m_iStatus = 2;
                } else if (this.m_iStatus == 1) {
                    this.m_iStatus = 3;
                }
            }
        }

        public File getUpdatedFile() {
            return this.m_fileAktJapJar;
        }

        public void setCurrentFileIfNull(File file) {
            this.m_fileAktJapJar = file;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        private boolean createNewJAPJar(byte[] arrby) {
            boolean bl;
            this.m_strMessage = SoftwareUpdater.this.createMessage(3, false);
            this.m_iValue = 420;
            this.setChanged();
            this.notifyObservers(this);
            FileOutputStream fileOutputStream = null;
            try {
                try {
                    this.m_fileNewJapJar = this.m_strTempDirectory == null ? new File(this.m_strAktJapJarPath + this.m_strAktJapJarFileName + this.m_versionInfo.getJapVersion() + EXTENSION_NEW + this.m_strAktJapJarExtension) : new File(this.m_strTempDirectory + this.m_fileAktJapJar.getName());
                    fileOutputStream = new FileOutputStream(this.m_fileNewJapJar);
                    fileOutputStream.write(arrby);
                    fileOutputStream.flush();
                    this.m_strMessage = SoftwareUpdater.this.createMessage(3, true);
                    this.m_iValue = 520;
                    this.setChanged();
                    this.notifyObservers(this);
                    bl = true;
                    Object var6_5 = null;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, exception);
                    this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_SAVING_UPDATE_FAILED);
                    boolean bl2 = false;
                    Object var6_6 = null;
                    try {
                        fileOutputStream.close();
                        return bl2;
                    }
                    catch (Exception exception3) {
                        // empty catch block
                    }
                    return bl2;
                }
            }
            catch (Throwable throwable) {
                Object var6_7 = null;
                try {}
                catch (Exception exception2) {
                    throw throwable;
                }
                fileOutputStream.close();
                throw throwable;
            }
            try {}
            catch (Exception exception) {
                // empty catch block
                return bl;
            }
            fileOutputStream.close();
            return bl;
        }

        private synchronized boolean applyJARDiffJAPJar(byte[] arrby) {
            this.m_strMessage = SoftwareUpdater.this.createMessage(3, false);
            this.setValue(420);
            try {
                this.m_fileNewJapJar = this.m_strTempDirectory == null ? new File(this.m_strAktJapJarPath + this.m_strAktJapJarFileName + this.m_versionInfo.getJapVersion() + EXTENSION_NEW + this.m_strAktJapJarExtension) : new File(this.m_strTempDirectory + this.m_fileAktJapJar.getName());
                ZipArchiver zipArchiver = new ZipArchiver(new ZipFile(this.m_fileAktJapJar));
                Observer observer = new Observer(){

                    public void update(Observable observable, Object object) {
                        if (object instanceof ZipArchiver.ZipEvent) {
                            ZipArchiver.ZipEvent zipEvent = (ZipArchiver.ZipEvent)object;
                            UpdateThread.this.m_iValue = zipEvent.getValue() * 100 / zipEvent.getMaximum() + 420;
                            UpdateThread.this.setChanged();
                            UpdateThread.this.notifyObservers(UpdateThread.this);
                        }
                    }
                };
                zipArchiver.addObserver(observer);
                if (zipArchiver.applyDiff(this.m_fileNewJapJar, arrby) != 0) {
                    this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_SAVING_UPDATE_FAILED);
                    return false;
                }
                this.m_strMessage = SoftwareUpdater.this.createMessage(3, true);
                this.m_iValue = 520;
                this.setChanged();
                this.notifyObservers(this);
                return true;
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, exception);
                this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_SAVING_UPDATE_FAILED);
                return false;
            }
        }

        private void parsePathToJapJar() {
            try {
                this.m_strAktJapJarFileName = this.m_fileAktJapJar.getName();
                this.m_strAktJapJarPath = this.m_fileAktJapJar.getCanonicalPath();
                this.m_strAktJapJarPath = this.m_strAktJapJarPath.substring(0, this.m_strAktJapJarPath.length() - this.m_strAktJapJarFileName.length());
                this.m_strAktJapJarExtension = this.m_fileAktJapJar.getName();
                int n = this.m_strAktJapJarExtension.lastIndexOf(46);
                this.m_strAktJapJarExtension = this.m_strAktJapJarExtension.substring(n);
                this.m_strAktJapJarFileName = this.m_strAktJapJarFileName.substring(0, n);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, exception);
            }
        }

        private void resetChanges(boolean bl) {
            if (bl && this.m_fileJapJarCopy != null) {
                this.m_fileJapJarCopy.delete();
            }
            if (this.m_fileNewJapJar != null) {
                this.m_fileNewJapJar.delete();
            }
        }

        private String getJarVersion(File file) {
            String string = null;
            try {
                int n;
                String string2 = "java -jar \"" + file.getCanonicalPath() + "\" -v";
                string = AbstractOS.getInstance().executeRuntime(string2);
                BufferedReader bufferedReader = new BufferedReader(new StringReader(string));
                string = bufferedReader.readLine();
                string = string != null && (n = string.toLowerCase().indexOf("version:")) > 0 ? string.substring(n + "version:".length()).trim() : null;
            }
            catch (IOException iOException) {
                LogHolder.log(2, LogType.MISC, "Could not get version info from file to update!", iOException);
                string = null;
            }
            return string;
        }

        private boolean renameJapJar() {
            LogHolder.log(7, LogType.MISC, "Start to make a copy of old jar-File!");
            this.m_strMessage = SoftwareUpdater.this.createMessage(1, false);
            this.setChanged();
            this.notifyObservers(this);
            boolean bl = JarVerifier.verify(this.m_fileAktJapJar, JAPModel.getJAPCodeSigningCert());
            this.m_iValue = 5;
            this.setChanged();
            this.notifyObservers(this);
            String string = this.getJarVersion(this.m_fileAktJapJar);
            if (string != null) {
                this.m_bCommandLineVersion = true;
                if (bl && !this.m_fileAktJapJar.equals(ClassUtil.getClassDirectory(ClassUtil.getClassStatic()))) {
                    this.m_strCurrentVersion = string;
                    LogHolder.log(4, LogType.MISC, "Version to update from: '" + this.m_strCurrentVersion + "'");
                }
            }
            try {
                this.m_iRange = 15;
                this.m_lFileSize = this.m_fileAktJapJar.length();
                try {
                    this.m_fileJapJarCopy = new File(this.m_strAktJapJarPath + this.m_strAktJapJarFileName + this.m_strCurrentVersion + EXTENSION_BACKUP + this.m_strAktJapJarExtension);
                    this.m_fileJapJarCopy.delete();
                    Util.copyStream(new FileInputStream(this.m_fileAktJapJar), new FileOutputStream(this.m_fileJapJarCopy), this);
                }
                catch (IOException iOException) {
                    this.setValue(5);
                    this.m_strTempDirectory = AbstractOS.getInstance().getTempPath();
                    if (this.m_strTempDirectory == null) {
                        throw iOException;
                    }
                    this.m_fileJapJarCopy = new File(this.m_strTempDirectory + this.m_strAktJapJarFileName + this.m_strCurrentVersion + EXTENSION_BACKUP + this.m_strAktJapJarExtension);
                    this.m_fileJapJarCopy.delete();
                    Util.copyStream(new FileInputStream(this.m_fileAktJapJar), new FileOutputStream(this.m_fileJapJarCopy), this);
                }
                this.m_strMessage = SoftwareUpdater.this.createMessage(1, true);
                this.setValue(20);
            }
            catch (IOException iOException) {
                this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_NO_BACKUP);
                this.m_iStatus = 3;
            }
            return bl;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Lifted jumps to return sites
         */
        private byte[] doDownload(ListenerInterface var1_1, String var2_2) {
            var3_3 = null;
            var4_4 = null;
            var5_5 = null;
            try {
                block31: {
                    try {
                        LogHolder.log(5, LogType.NET, "Try to download: " + var2_2);
                        this.m_strMessage = SoftwareUpdater.access$2300(SoftwareUpdater.this, 2, false);
                        this.setValue(20);
                        var6_6 = null;
                        var7_8 = false;
                        var8_9 = JAPModel.getInstance().getUpdateProxyInterface();
                        for (var10_10 = 0; var10_10 < 2 && !Thread.currentThread().isInterrupted(); ++var10_10) {
                            if (var10_10 == 1) {
                                var7_8 = true;
                            }
                            if ((var9_12 = var8_9.getProxyInterface(var7_8)) == null) continue;
                            try {
                                var3_3 = HTTPConnectionFactory.getInstance().createHTTPConnection(var1_1, var9_12.getProxyInterface());
                                var3_3.setTimeout(300000);
                                var6_6 = var3_3.Get(var2_2);
                            }
                            catch (Exception var11_13) {
                                LogHolder.log(2, LogType.NET, var11_13);
                                continue;
                            }
                            if (var6_6.getStatusCode() == 200) break;
                            LogHolder.log(4, LogType.NET, "Update broke with status code: " + var6_6.getStatusCode());
                        }
                        if (var6_6 == null || var6_6.getStatusCode() != 200) {
                            var10_11 = null;
                            var20_15 = null;
                            SoftwareUpdater.access$3400(SoftwareUpdater.this).clearStatusMessage();
                            if (var3_3 != null) {
                                try {
                                    var3_3.stop();
                                }
                                catch (Exception var21_20) {
                                    // empty catch block
                                }
                            }
                            if (var4_4 == null) return var10_11;
                            try {
                                var4_4.close();
                                return var10_11;
                            }
                            catch (Exception var21_20) {
                                // empty catch block
                            }
                            return var10_11;
                        }
                        LogHolder.log(5, LogType.NET, "Connection to update server established, continue update...");
                        var10_10 = var6_6.getHeaderAsInt("Content-Length");
                        var4_4 = var6_6.getInputStream();
                        var11_14 = new byte[2048];
                        var5_5 = new byte[var10_10];
                        var12_25 = var10_10;
                        var13_26 = 0;
                        var14_27 = var4_4.read(var11_14);
                        while (var14_27 > 0) {
                            System.arraycopy(var11_14, 0, var5_5, var13_26, var14_27);
                            var15_28 = (int)(400L * (long)(var13_26 += var14_27) / (long)var10_10);
                            var16_29 = this.m_iValue;
                            this.m_iValue = var15_28 + 20;
                            var17_30 = var12_25 -= var14_27;
                            if (var16_29 != this.m_iValue) {
                                SwingUtilities.invokeAndWait(new Runnable(){

                                    public void run() {
                                        SoftwareUpdater.this.paneFetchUpdate.printStatusMessage(JAPMessages.getString(MSG_DOWNLOAD_REMAINING) + ": " + Util.formatBytesValueWithUnit(var17_30));
                                    }
                                });
                            }
                            this.setChanged();
                            this.notifyObservers(this);
                            if (Thread.currentThread().isInterrupted()) {
                                block30: {
                                    var18_31 = null;
                                    var20_16 = null;
                                    SoftwareUpdater.access$3400(SoftwareUpdater.this).clearStatusMessage();
                                    if (var3_3 != null) {
                                        ** try [egrp 3[TRYBLOCK] [9 : 485->492)] { 
lbl71:
                                        // 1 sources

                                        var3_3.stop();
                                        break block30;
lbl73:
                                        // 1 sources

                                        catch (Exception var21_21) {
                                            // empty catch block
                                        }
                                    }
                                }
                                if (var4_4 == null) return var18_31;
                                ** try [egrp 4[TRYBLOCK] [10 : 499->507)] { 
lbl78:
                                // 1 sources

                                var4_4.close();
                                return var18_31;
lbl80:
                                // 1 sources

                                catch (Exception var21_21) {
                                    // empty catch block
                                }
                                return var18_31;
                            }
                            var14_27 = var4_4.read(var11_14);
                        }
                        this.m_strMessage = SoftwareUpdater.access$2300(SoftwareUpdater.this, 2, true);
                        this.setValue(420);
                    }
                    catch (Exception var6_7) {
                        block32: {
                            LogHolder.log(2, LogType.NET, var6_7);
                            var20_18 = null;
                            SoftwareUpdater.access$3400(SoftwareUpdater.this).clearStatusMessage();
                            if (var3_3 != null) {
                                ** try [egrp 3[TRYBLOCK] [9 : 485->492)] { 
lbl94:
                                // 1 sources

                                var3_3.stop();
                                break block32;
lbl96:
                                // 1 sources

                                catch (Exception var21_23) {
                                    // empty catch block
                                }
                            }
                        }
                        if (var4_4 == null) return var5_5;
                        try {}
                        catch (Exception var21_23) {
                            return var5_5;
                        }
                        var4_4.close();
                        return var5_5;
                    }
                    var20_17 = null;
                    SoftwareUpdater.access$3400(SoftwareUpdater.this).clearStatusMessage();
                    if (var3_3 != null) {
                        ** try [egrp 3[TRYBLOCK] [9 : 485->492)] { 
lbl110:
                        // 1 sources

                        var3_3.stop();
                        break block31;
lbl112:
                        // 1 sources

                        catch (Exception var21_22) {
                            // empty catch block
                        }
                    }
                }
                if (var4_4 == null) return var5_5;
                ** try [egrp 4[TRYBLOCK] [10 : 499->507)] { 
lbl117:
                // 1 sources

                catch (Exception var21_22) {
                    return var5_5;
                }
lbl119:
                // 1 sources

                var4_4.close();
                return var5_5;
            }
            catch (Throwable var19_32) {
                block33: {
                    var20_19 = null;
                    SoftwareUpdater.access$3400(SoftwareUpdater.this).clearStatusMessage();
                    if (var3_3 != null) {
                        ** try [egrp 3[TRYBLOCK] [9 : 485->492)] { 
lbl127:
                        // 1 sources

                        var3_3.stop();
                        break block33;
lbl129:
                        // 1 sources

                        catch (Exception var21_24) {
                            // empty catch block
                        }
                    }
                }
                if (var4_4 == null) throw var19_32;
                ** try [egrp 4[TRYBLOCK] [10 : 499->507)] { 
lbl134:
                // 1 sources

                var4_4.close();
                throw var19_32;
lbl136:
                // 1 sources

                catch (Exception var21_24) {
                    // empty catch block
                }
                throw var19_32;
            }
        }

        private boolean overwriteJapJar() {
            this.m_strMessage = SoftwareUpdater.this.createMessage(5, false);
            this.setValue(600);
            if (this.getUpdatedFile() != null && this.getUpdatedFile().equals(CLASSFILE)) {
                final JAPController.IRestarter iRestarter = JAPController.getInstance().getRestarter();
                SoftwareUpdater.this.m_Restarter = iRestarter;
                JAPController.getInstance().setRestarter(new JAPController.IRestarter(){

                    public void exec(String[] arrstring) throws IOException {
                        iRestarter.exec(arrstring);
                    }

                    public boolean isConfigFileSaved() {
                        return false;
                    }

                    public boolean hideWarnings() {
                        return true;
                    }
                });
                JAPController.getInstance().saveConfigFile();
            }
            this.setValue(620);
            boolean bl = false;
            try {
                if (this.m_fileAktJapJar != null && this.m_fileAktJapJar.equals(CLASSFILE)) {
                    GUIUtils.setLoadImages(false);
                }
                if (Thread.currentThread().isInterrupted()) {
                    GUIUtils.setLoadImages(true);
                    return false;
                }
                SoftwareUpdater.this.paneFetchUpdate.setButtonCancelEnabled(false);
                this.m_iRange = 80;
                this.m_lFileSize = this.m_fileNewJapJar.length();
                try {
                    Util.copyStream(new FileInputStream(this.m_fileNewJapJar), new FileOutputStream(this.m_fileAktJapJar), this);
                }
                catch (SecurityException securityException) {
                    LogHolder.log(4, LogType.MISC, securityException);
                    bl = true;
                    this.setValue(620);
                }
                catch (IOException iOException) {
                    LogHolder.log(4, LogType.MISC, iOException);
                    bl = true;
                    this.setValue(620);
                }
                if (Thread.currentThread().isInterrupted()) {
                    GUIUtils.setLoadImages(true);
                    return false;
                }
                if (bl || !RecursiveFileTool.equals(this.m_fileNewJapJar, this.m_fileAktJapJar, true)) {
                    if (this.m_strTempDirectory == null) {
                        this.m_strTempDirectory = AbstractOS.getInstance().getTempPath();
                        if (this.m_strTempDirectory == null) {
                            throw new Exception("Administrator copy failed!");
                        }
                        File file = new File(this.m_strTempDirectory + this.m_fileAktJapJar.getName());
                        this.m_iRange = 20;
                        Util.copyStream(new FileInputStream(this.m_fileNewJapJar), new FileOutputStream(file), this);
                        this.m_fileNewJapJar = file;
                    }
                    AbstractOS.AbstractRetryCopyProcess abstractRetryCopyProcess = new AbstractOS.AbstractRetryCopyProcess(12){

                        public boolean checkRetry() {
                            return JAPDialog.showYesNoDialog(SoftwareUpdater.this.paneFetchUpdate.getContentPane(), JAPMessages.getString(JAPUpdateWizard.MSG_ENTER_ADMIN_PASSWORD));
                        }

                        public boolean incrementProgress() {
                            if (super.incrementProgress()) {
                                UpdateThread.this.setValue(640 + (this.getCurrentStep() + 1) * 5);
                                return true;
                            }
                            return false;
                        }

                        public void reset() {
                            super.reset();
                            UpdateThread.this.setValue(620);
                        }
                    };
                    if (Thread.currentThread().isInterrupted()) {
                        GUIUtils.setLoadImages(true);
                        return false;
                    }
                    boolean bl2 = AbstractOS.getInstance().copyAsRoot(this.m_fileNewJapJar, new File(this.m_fileAktJapJar.getParent()), abstractRetryCopyProcess);
                    if (!bl2 || !RecursiveFileTool.equals(this.m_fileNewJapJar, this.m_fileAktJapJar, true)) {
                        throw new Exception("Administrator copy failed!");
                    }
                }
                this.m_strMessage = SoftwareUpdater.this.createMessage(5, false);
                this.setValue(700);
                return true;
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, exception);
                GUIUtils.setLoadImages(true);
                this.m_strFailedReason = JAPMessages.getString(MSG_ERROR_OVERWRITING_FAILED);
                return false;
            }
        }

        public int getMaximum() {
            return 700;
        }

        public int getMinimum() {
            return 0;
        }

        public int getValue() {
            return this.m_iValue;
        }

        public int getStatus() {
            return this.m_iStatus;
        }

        public void reset() {
        }

        public String getMessage() {
            return this.m_strMessage;
        }
    }
}

