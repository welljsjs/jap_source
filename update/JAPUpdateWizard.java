/*
 * Decompiled with CFR 0.150.
 */
package update;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.IProxyInterfaceGetter;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.JavaVersionDBEntry;
import anon.infoservice.ListenerInterface;
import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.JAPMessages;
import anon.util.RecursiveFileTool;
import anon.util.Util;
import anon.util.ZipArchiver;
import gui.GUIUtils;
import gui.dialog.JAPDialog;
import gui.wizard.BasicWizard;
import gui.wizard.BasicWizardHost;
import gui.wizard.Wizard;
import gui.wizard.WizardPage;
import jap.AbstractJAPMainView;
import jap.JAPController;
import jap.JAPModel;
import jarify.JarVerifier;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;
import update.JAPDownloadWizardPage;
import update.JAPFinishWizardPage;
import update.JAPWelcomeWizardPage;

public final class JAPUpdateWizard
extends BasicWizard
implements Runnable {
    public JAPWelcomeWizardPage welcomePage;
    public JAPDownloadWizardPage downloadPage;
    public JAPFinishWizardPage finishPage;
    private BasicWizardHost host;
    private String m_strTempDirectory;
    public static final String MSG_JAVA_TOO_OLD = (class$update$JAPUpdateWizard == null ? (class$update$JAPUpdateWizard = JAPUpdateWizard.class$("update.JAPUpdateWizard")) : class$update$JAPUpdateWizard).getName() + "_javaTooOld";
    public static final String MSG_ADMIN_RIGHTS_NEEDED = (class$update$JAPUpdateWizard == null ? (class$update$JAPUpdateWizard = JAPUpdateWizard.class$("update.JAPUpdateWizard")) : class$update$JAPUpdateWizard).getName() + "_adminRightsNeeded";
    public static final String MSG_ENTER_ADMIN_PASSWORD = (class$update$JAPUpdateWizard == null ? (class$update$JAPUpdateWizard = JAPUpdateWizard.class$("update.JAPUpdateWizard")) : class$update$JAPUpdateWizard).getName() + "_enterAdminPassword";
    private String m_strAktJapJarFileName;
    private String m_strAktJapJarExtension;
    private String m_strAktJapJarPath;
    private static final String EXTENSION_BACKUP = ".backup";
    private static final String EXTENSION_NEW = ".new";
    private static final File CLASSFILE = ClassUtil.getClassDirectory(class$anon$util$ClassUtil == null ? (class$anon$util$ClassUtil = JAPUpdateWizard.class$("anon.util.ClassUtil")) : class$anon$util$ClassUtil);
    private boolean updateAborted = false;
    private String m_strNewJapVersion;
    private JAPVersionInfo japVersionInfo;
    private File m_fileAktJapJar;
    private File m_fileJapJarCopy;
    private File m_fileNewJapJar;
    private File updJapJar;
    private byte[] m_arBufferNewJapJar = null;
    private Thread updateThread;
    private int m_Status;
    public static final int UPDATESTATUS_SUCCESS = 0;
    public static final int UPDATESTATUS_ABORTED = 1;
    public static final int UPDATESTATUS_ERROR = -1;
    static /* synthetic */ Class class$update$JAPUpdateWizard;
    static /* synthetic */ Class class$anon$util$ClassUtil;

    public JAPUpdateWizard(JAPVersionInfo jAPVersionInfo, Component component) {
        this(jAPVersionInfo, (Object)component);
    }

    /*
     * Enabled aggressive block sorting
     */
    private JAPUpdateWizard(JAPVersionInfo jAPVersionInfo, Object object) {
        this.setWizardTitle("JAP Update Wizard");
        if (object instanceof JAPDialog) {
            if (!jAPVersionInfo.isJavaVersionStillSupported()) {
                JAPDialog.showErrorDialog((JAPDialog)object, JAPMessages.getString(MSG_JAVA_TOO_OLD, new Object[]{JavaVersionDBEntry.CURRENT_JAVA_VERSION, jAPVersionInfo.getSupportedJavaVersion()}), (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("updateJava"));
                return;
            }
            this.host = new BasicWizardHost((JAPDialog)object, (Wizard)this);
        } else {
            if (!jAPVersionInfo.isJavaVersionStillSupported()) {
                JAPDialog.showErrorDialog((Component)object, JAPMessages.getString(MSG_JAVA_TOO_OLD, new Object[]{JavaVersionDBEntry.CURRENT_JAVA_VERSION, jAPVersionInfo.getSupportedJavaVersion()}), (JAPDialog.ILinkedInformation)new JAPDialog.LinkedHelpContext("updateJava"));
                return;
            }
            this.host = new BasicWizardHost((Component)object, (Wizard)this);
        }
        this.host.setHelpEnabled(false);
        this.setHost(this.host);
        this.m_Status = 1;
        this.japVersionInfo = jAPVersionInfo;
        this.m_strNewJapVersion = jAPVersionInfo.getJapVersion();
        this.welcomePage = new JAPWelcomeWizardPage(jAPVersionInfo);
        this.downloadPage = new JAPDownloadWizardPage();
        this.finishPage = new JAPFinishWizardPage();
        this.addWizardPage(0, this.welcomePage);
        this.addWizardPage(1, this.downloadPage);
        this.addWizardPage(2, this.finishPage);
        this.invokeWizard();
    }

    public int getStatus() {
        return this.m_Status;
    }

    private void startUpdateThread() {
        LogHolder.log(7, LogType.MISC, "Start update...");
        this.updateThread = new Thread((Runnable)this, "JAPUpdateWizard");
        this.updateThread.setDaemon(true);
        this.updateThread.start();
    }

    public void run() {
        this.m_Status = 0;
        if (this.renameJapJar() != 0) {
            this.downloadPage.showInformationDialog(JAPMessages.getString("updateInformationMsgStep1") + " " + JAPMessages.getString(MSG_ADMIN_RIGHTS_NEEDED));
            this.resetChanges();
            return;
        }
        if (this.downloadUpdate() != 0) {
            if (!this.updateAborted) {
                if (JAPModel.getInstance().getUpdateAnonymousConnectionSetting() == 1 && !JAPController.getInstance().isAnonConnected()) {
                    int n = JAPDialog.showConfirmDialog((Component)this.downloadPage, JAPMessages.getString("updateInformationMsgStep2") + JAPMessages.getString("updateInformationMsgStep2_noDirectConn"), 0, 0);
                    if (n == 0) {
                        JAPModel.getInstance().setUpdateAnonymousConnectionSetting(0);
                    }
                } else if (JAPModel.getInstance().getUpdateAnonymousConnectionSetting() == 2 && JAPController.getInstance().isAnonConnected()) {
                    int n = JAPDialog.showConfirmDialog((Component)this.downloadPage, JAPMessages.getString("updateInformationMsgStep2") + JAPMessages.getString("updateInformationMsgStep2_noAnonConn"), 0, 0);
                    if (n == 0) {
                        JAPModel.getInstance().setUpdateAnonymousConnectionSetting(0);
                    }
                } else {
                    this.downloadPage.showInformationDialog(JAPMessages.getString("updateInformationMsgStep2"));
                }
            }
            this.resetChanges();
            return;
        }
        if (this.welcomePage.isIncrementalUpdate()) {
            if (this.applyJARDiffJAPJar() != 0) {
                this.downloadPage.showInformationDialog(JAPMessages.getString("updateInformationMsgStep3") + " " + JAPMessages.getString(MSG_ADMIN_RIGHTS_NEEDED));
                this.resetChanges();
                return;
            }
        } else if (this.createNewJAPJar() != 0) {
            this.downloadPage.showInformationDialog(JAPMessages.getString("updateInformationMsgStep3"));
            this.resetChanges();
            return;
        }
        if (!this.checkSignature()) {
            this.downloadPage.showInformationDialog(JAPMessages.getString("updateInformationMsgStep4"));
            this.resetChanges();
            return;
        }
        if (this.overwriteJapJar() != 0) {
            this.downloadPage.showInformationDialog(JAPMessages.getString("updateInformationMsgStep5") + " " + JAPMessages.getString(MSG_ADMIN_RIGHTS_NEEDED));
            this.host.doCancel();
            return;
        }
        try {
            if (!this.m_fileNewJapJar.delete()) {
                this.downloadPage.showInformationDialog(JAPMessages.getString("updateM_DeletingofJAP_new.jarfailed"));
                return;
            }
            this.host.setNextEnabled(true);
            this.host.setFinishEnabled(false);
            this.host.setCancelEnabled(false);
        }
        catch (Exception exception) {
            this.downloadPage.showInformationDialog(exception.toString());
            return;
        }
    }

    private void setJapJarFile(File file) {
        this.m_fileAktJapJar = file;
        this.parsePathToJapJar();
        String string = this.m_strAktJapJarPath + this.m_strAktJapJarFileName + "00.20.001" + EXTENSION_BACKUP + this.m_strAktJapJarExtension;
        this.downloadPage.m_labelSaveFrom.setText(this.m_fileAktJapJar.getAbsolutePath());
        this.downloadPage.m_labelSaveTo.setText(string);
        this.downloadPage.m_labelStep3.setText(JAPMessages.getString("updateM_labelStep3Part1") + " " + this.m_strAktJapJarFileName + this.m_strNewJapVersion + EXTENSION_NEW + this.m_strAktJapJarExtension);
        this.finishPage.m_labelBackupOfJapJar.setText(string);
    }

    public WizardPage next() {
        if (!((WizardPage)this.m_Pages.elementAt(this.m_PageIndex)).checkPage()) {
            return null;
        }
        ++this.m_PageIndex;
        this.host.setBackEnabled(true);
        if (this.m_PageIndex == this.m_Pages.size() - 1) {
            this.host.setFinishEnabled(true);
            this.host.setNextEnabled(false);
            try {
                this.updateThread.join();
            }
            catch (InterruptedException interruptedException) {
                LogHolder.log(3, LogType.MISC, interruptedException);
            }
        }
        if (this.m_PageIndex == 1) {
            this.host.setBackEnabled(false);
            this.host.setFinishEnabled(false);
            this.host.setNextEnabled(false);
            this.setJapJarFile(this.welcomePage.getJapJarFile());
            this.host.showWizardPage(this.m_PageIndex);
            this.startUpdateThread();
        } else {
            this.host.showWizardPage(this.m_PageIndex);
        }
        return null;
    }

    public WizardPage finish() {
        Window window = this.host.getDialogParent().getOwner();
        this.host.getDialogParent().dispose();
        if (this.m_fileAktJapJar != null && this.m_fileAktJapJar.equals(CLASSFILE)) {
            if (!(window instanceof AbstractJAPMainView)) {
                ((Component)window).setVisible(false);
            }
            JAPController.goodBye(false);
        }
        return null;
    }

    public WizardPage back() {
        if (this.m_PageIndex == this.m_Pages.size() - 1) {
            this.host.setBackEnabled(false);
        }
        super.back();
        return null;
    }

    public void wizardCompleted() {
        this.updateAborted = true;
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

    private int renameJapJar() {
        LogHolder.log(7, LogType.MISC, "Start to make a copy of old jar-File!");
        this.downloadPage.m_labelIconStep1.setIcon(this.downloadPage.arrow);
        try {
            try {
                this.m_fileJapJarCopy = new File(this.m_strAktJapJarPath + this.m_strAktJapJarFileName + "00.20.001" + EXTENSION_BACKUP + this.m_strAktJapJarExtension);
                Util.copyStream(new FileInputStream(this.m_fileAktJapJar), new FileOutputStream(this.m_fileJapJarCopy));
            }
            catch (Throwable throwable) {
                this.m_strTempDirectory = AbstractOS.getInstance().getTempPath();
                if (this.m_strTempDirectory == null) {
                    throw throwable;
                }
                this.m_fileJapJarCopy = new File(this.m_strTempDirectory + this.m_strAktJapJarFileName + "00.20.001" + EXTENSION_BACKUP + this.m_strAktJapJarExtension);
                Util.copyStream(new FileInputStream(this.m_fileAktJapJar), new FileOutputStream(this.m_fileJapJarCopy));
                this.finishPage.m_labelBackupOfJapJar.setText(this.m_fileJapJarCopy.getAbsolutePath());
                this.downloadPage.m_labelSaveTo.setText(this.m_fileJapJarCopy.getAbsolutePath());
            }
            this.downloadPage.progressBar.setValue(5);
            this.downloadPage.progressBar.repaint();
            this.downloadPage.m_labelIconStep1.setIcon(this.downloadPage.stepfinished);
            return 0;
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.MISC, "Could not make a copy of old JAP.jar: " + throwable.getMessage());
            return -1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int downloadUpdate() {
        this.downloadPage.m_labelIconStep2.setIcon(this.downloadPage.arrow);
        URL[] arruRL = this.japVersionInfo.getCodeBase();
        for (int i = 0; i < arruRL.length; ++i) {
            URL uRL = arruRL[i];
            URL uRL2 = null;
            try {
                uRL2 = this.welcomePage.isIncrementalUpdate() ? new URL(uRL, this.japVersionInfo.getJAPJarFileName() + "?version-id=" + this.japVersionInfo.getJapVersion() + "&current-version-id=" + "00.20.001") : new URL(uRL, this.japVersionInfo.getJAPJarFileName() + "?version-id=" + this.japVersionInfo.getJapVersion());
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, exception);
                continue;
            }
            try {
                JapDownloadManager japDownloadManager;
                JapDownloadManager japDownloadManager2 = japDownloadManager = new JapDownloadManager(uRL2);
                synchronized (japDownloadManager2) {
                    japDownloadManager.startDownload();
                    japDownloadManager.wait();
                }
                if (japDownloadManager.getDownloadResult() == -1) continue;
                this.m_arBufferNewJapJar = japDownloadManager.getNewJar();
                this.downloadPage.m_labelIconStep2.setIcon(this.downloadPage.stepfinished);
                return 0;
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, exception);
            }
        }
        return -1;
    }

    private int createNewJAPJar() {
        try {
            if (this.m_strTempDirectory == null) {
                this.m_fileNewJapJar = new File(this.m_strAktJapJarPath + this.m_strAktJapJarFileName + this.m_strNewJapVersion + EXTENSION_NEW + this.m_strAktJapJarExtension);
            } else {
                this.m_fileNewJapJar = new File(this.m_strTempDirectory + this.m_fileAktJapJar.getName());
                this.downloadPage.m_labelStep3.setText(JAPMessages.getString("updateM_labelStep3Part1") + " " + this.m_fileAktJapJar.getName());
            }
            FileOutputStream fileOutputStream = new FileOutputStream(this.m_fileNewJapJar);
            if (this.m_arBufferNewJapJar == null) {
                fileOutputStream.close();
                return -1;
            }
            this.downloadPage.m_labelIconStep3.setIcon(this.downloadPage.arrow);
            fileOutputStream.write(this.m_arBufferNewJapJar);
            fileOutputStream.flush();
            fileOutputStream.close();
            this.downloadPage.progressBar.setValue(440);
            this.downloadPage.progressBar.repaint();
            this.downloadPage.m_labelIconStep3.setIcon(this.downloadPage.stepfinished);
            return 0;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, exception);
            return -1;
        }
    }

    private synchronized int applyJARDiffJAPJar() {
        try {
            this.m_fileNewJapJar = new File(this.m_strAktJapJarPath + this.m_strAktJapJarFileName + this.m_strNewJapVersion + EXTENSION_NEW + this.m_strAktJapJarExtension);
            ZipArchiver zipArchiver = new ZipArchiver(new ZipFile(this.m_fileAktJapJar));
            if (zipArchiver.applyDiff(this.m_fileNewJapJar, this.m_arBufferNewJapJar) != 0) {
                return -1;
            }
            this.downloadPage.m_labelIconStep3.setIcon(this.downloadPage.arrow);
            this.downloadPage.progressBar.setValue(440);
            this.downloadPage.progressBar.repaint();
            this.downloadPage.m_labelIconStep3.setIcon(this.downloadPage.stepfinished);
            return 0;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, exception);
            return -1;
        }
    }

    private boolean checkSignature() {
        return JarVerifier.verify(this.m_fileNewJapJar, JAPModel.getJAPCodeSigningCert());
    }

    private int overwriteJapJar() {
        boolean bl = false;
        try {
            if (this.m_fileAktJapJar != null && this.m_fileAktJapJar.equals(CLASSFILE)) {
                GUIUtils.setLoadImages(false);
            }
            this.downloadPage.m_labelIconStep5.setIcon(this.downloadPage.arrow);
            this.host.setCancelEnabled(false);
            try {
                Util.copyStream(new FileInputStream(this.m_fileNewJapJar), new FileOutputStream(this.m_fileAktJapJar));
            }
            catch (SecurityException securityException) {
                LogHolder.log(4, LogType.MISC, securityException);
                bl = true;
            }
            catch (IOException iOException) {
                LogHolder.log(4, LogType.MISC, iOException);
                bl = true;
            }
            if (bl || !RecursiveFileTool.equals(this.m_fileNewJapJar, this.m_fileAktJapJar, true)) {
                if (this.m_strTempDirectory == null) {
                    this.m_strTempDirectory = AbstractOS.getInstance().getTempPath();
                    if (this.m_strTempDirectory == null) {
                        throw new Exception("Administrator copy failed!");
                    }
                    File file = new File(this.m_strTempDirectory + this.m_fileAktJapJar.getName());
                    Util.copyStream(new FileInputStream(this.m_fileNewJapJar), new FileOutputStream(file));
                    this.m_fileNewJapJar = file;
                }
                AbstractOS.AbstractRetryCopyProcess abstractRetryCopyProcess = new AbstractOS.AbstractRetryCopyProcess(12){

                    public boolean checkRetry() {
                        return JAPDialog.showYesNoDialog(JAPUpdateWizard.this.downloadPage, JAPMessages.getString(MSG_ENTER_ADMIN_PASSWORD));
                    }

                    public boolean incrementProgress() {
                        if (super.incrementProgress()) {
                            JAPUpdateWizard.this.downloadPage.progressBar.setValue(440 + (this.getCurrentStep() + 1) * 5);
                            JAPUpdateWizard.this.downloadPage.progressBar.repaint();
                            return true;
                        }
                        return false;
                    }

                    public void reset() {
                        super.reset();
                        JAPUpdateWizard.this.downloadPage.progressBar.setValue(440);
                    }
                };
                this.host.lockDialog();
                boolean bl2 = AbstractOS.getInstance().copyAsRoot(this.m_fileNewJapJar, new File(this.m_fileAktJapJar.getParent()), abstractRetryCopyProcess);
                if (!bl2 || !RecursiveFileTool.equals(this.m_fileNewJapJar, this.m_fileAktJapJar, true)) {
                    throw new Exception("Administrator copy failed!");
                }
                this.host.unlockDialog();
            }
            this.downloadPage.progressBar.setValue(500);
            this.downloadPage.progressBar.repaint();
            this.downloadPage.m_labelIconStep5.setIcon(this.downloadPage.stepfinished);
            return 0;
        }
        catch (Exception exception) {
            this.host.unlockDialog();
            LogHolder.log(3, LogType.MISC, exception);
            GUIUtils.setLoadImages(true);
            return -1;
        }
    }

    private void resetChanges() {
        this.m_Status = this.updateAborted ? 1 : -1;
        if (this.m_fileJapJarCopy != null) {
            this.m_fileJapJarCopy.delete();
        }
        if (this.m_fileNewJapJar != null) {
            this.m_fileNewJapJar.delete();
        }
        if (this.updJapJar != null) {
            this.updJapJar.delete();
        }
        this.host.getDialogParent().dispose();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    final class JapDownloadManager
    implements Runnable {
        private ListenerInterface targetInterface;
        private String fileName;
        private int downloadResult = -1;
        private byte[] newJarBuff = null;

        public JapDownloadManager(URL uRL) throws Exception {
            String string = uRL.getHost();
            int n = uRL.getPort();
            if (n == -1) {
                n = 80;
            }
            this.targetInterface = new ListenerInterface(string, n);
            this.fileName = uRL.getFile();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                int n;
                HTTPResponse hTTPResponse = null;
                boolean bl = false;
                IMutableProxyInterface iMutableProxyInterface = JAPModel.getInstance().getUpdateProxyInterface();
                for (n = 0; n < 2 && !Thread.currentThread().isInterrupted(); ++n) {
                    IProxyInterfaceGetter iProxyInterfaceGetter;
                    if (n == 1) {
                        bl = true;
                    }
                    if ((iProxyInterfaceGetter = iMutableProxyInterface.getProxyInterface(bl)) == null) continue;
                    try {
                        HTTPConnection hTTPConnection = HTTPConnectionFactory.getInstance().createHTTPConnection(this.targetInterface, iProxyInterfaceGetter.getProxyInterface());
                        hTTPConnection.setTimeout(300000);
                        hTTPResponse = hTTPConnection.Get(this.fileName);
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.NET, exception);
                        continue;
                    }
                    if (hTTPResponse.getStatusCode() == 200) break;
                    LogHolder.log(4, LogType.NET, "Update broke with status code: " + hTTPResponse.getStatusCode());
                }
                if (hTTPResponse == null || hTTPResponse.getStatusCode() != 200) {
                    JapDownloadManager japDownloadManager = this;
                    synchronized (japDownloadManager) {
                        this.notifyAll();
                    }
                    return;
                }
                LogHolder.log(5, LogType.NET, "File downloaded, continue update...");
                n = hTTPResponse.getHeaderAsInt("Content-Length");
                InputStream inputStream = hTTPResponse.getInputStream();
                byte[] arrby = new byte[2048];
                this.newJarBuff = new byte[n];
                int n2 = 0;
                int n3 = inputStream.read(arrby);
                while (n3 > 0) {
                    System.arraycopy(arrby, 0, this.newJarBuff, n2, n3);
                    int n4 = (int)(400L * (long)(n2 += n3) / (long)n);
                    JAPUpdateWizard.this.downloadPage.progressBar.setValue(n4 + 5);
                    JAPUpdateWizard.this.downloadPage.progressBar.repaint();
                    if (JAPUpdateWizard.this.updateAborted) {
                        inputStream.close();
                        JapDownloadManager japDownloadManager = this;
                        synchronized (japDownloadManager) {
                            this.notifyAll();
                        }
                        return;
                    }
                    n3 = inputStream.read(arrby);
                }
                this.downloadResult = 0;
                JapDownloadManager japDownloadManager = this;
                synchronized (japDownloadManager) {
                    this.notifyAll();
                }
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.NET, exception);
                JapDownloadManager japDownloadManager = this;
                synchronized (japDownloadManager) {
                    this.notifyAll();
                }
            }
        }

        public void startDownload() {
            Thread thread = new Thread(this);
            thread.start();
        }

        public int getDownloadResult() {
            return this.downloadResult;
        }

        public byte[] getNewJar() {
            if (this.getDownloadResult() == 0) {
                return this.newJarBuff;
            }
            return null;
        }
    }
}

