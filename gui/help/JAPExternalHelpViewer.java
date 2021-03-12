/*
 * Decompiled with CFR 0.150.
 */
package gui.help;

import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.JAPHelpContext;
import gui.dialog.DialogContentPane;
import gui.dialog.FileChooserContentPane;
import gui.dialog.JAPDialog;
import gui.dialog.SimpleWizardContentPane;
import gui.dialog.WorkerContentPane;
import gui.help.IHelpModel;
import gui.help.JAPHelp;
import gui.help.JAPInternalHelpViewer;
import java.awt.Component;
import java.awt.Frame;
import java.net.URL;
import logging.LogHolder;
import logging.LogType;

public final class JAPExternalHelpViewer
extends JAPHelp {
    public static final String MSG_HELP_INSTALL = (class$gui$help$JAPExternalHelpViewer == null ? (class$gui$help$JAPExternalHelpViewer = JAPExternalHelpViewer.class$("gui.help.JAPExternalHelpViewer")) : class$gui$help$JAPExternalHelpViewer).getName() + "_helpInstall";
    public static final String MSG_HELP_INSTALL_PROGRESS = "helpInstallProgress";
    public static final String MSG_HELP_INSTALL_FAILED = (class$gui$help$JAPExternalHelpViewer == null ? (class$gui$help$JAPExternalHelpViewer = JAPExternalHelpViewer.class$("gui.help.JAPExternalHelpViewer")) : class$gui$help$JAPExternalHelpViewer).getName() + "_helpInstallFailed";
    private static final String MSG_HELP_PATH_CHOICE = (class$gui$help$JAPExternalHelpViewer == null ? (class$gui$help$JAPExternalHelpViewer = JAPExternalHelpViewer.class$("gui.help.JAPExternalHelpViewer")) : class$gui$help$JAPExternalHelpViewer).getName() + "_helpPathChoice";
    private static final String MSG_HELP_INTERNAL = (class$gui$help$JAPExternalHelpViewer == null ? (class$gui$help$JAPExternalHelpViewer = JAPExternalHelpViewer.class$("gui.help.JAPExternalHelpViewer")) : class$gui$help$JAPExternalHelpViewer).getName() + "_helpInstallOpenInternal";
    private static final String MSG_HELP_INSTALL_SUCCESS = (class$gui$help$JAPExternalHelpViewer == null ? (class$gui$help$JAPExternalHelpViewer = JAPExternalHelpViewer.class$("gui.help.JAPExternalHelpViewer")) : class$gui$help$JAPExternalHelpViewer).getName() + "_helpInstallSucceded";
    private Object SYNC_INSTALL = new Object();
    private boolean m_bInstallationDialogShown = false;
    private JAPHelp m_alternativeHelp = null;
    private IHelpModel m_helpModel;
    private long m_timeLastSetVisible = 0L;
    static /* synthetic */ Class class$gui$help$JAPExternalHelpViewer;

    JAPExternalHelpViewer(Frame frame, IHelpModel iHelpModel) {
        this.m_helpModel = iHelpModel;
        this.m_alternativeHelp = new JAPInternalHelpViewer(frame).getHelp();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setVisible(boolean bl) {
        boolean bl2;
        Object object;
        if (System.currentTimeMillis() - this.m_timeLastSetVisible < 1000L) {
            this.m_alternativeHelp.setContext(JAPHelpContext.INDEX_CONTEXT);
            this.m_alternativeHelp.setVisible(bl);
            return;
        }
        this.m_timeLastSetVisible = System.currentTimeMillis();
        JAPHelpContext.IHelpContext iHelpContext = this.getHelpContext();
        if (this.getHelpContext() == null) {
            LogHolder.log(3, LogType.GUI, "Cannot show help externally: No help context specified");
            this.m_alternativeHelp.setContext(JAPHelpContext.INDEX_CONTEXT);
            this.m_alternativeHelp.setVisible(bl);
            return;
        }
        Component component = iHelpContext.getHelpExtractionDisplayContext();
        if (!this.m_helpModel.isHelpPathDefined()) {
            if (component == null) {
                LogHolder.log(3, LogType.GUI, "Cannot show help externally: No display context specified");
                this.m_alternativeHelp.setContext(this.getHelpContext());
                this.m_alternativeHelp.setVisible(bl);
                return;
            }
            if (this.m_bInstallationDialogShown) {
                LogHolder.log(4, LogType.GUI, "Help installation dialog is already being shown. Cannot display help files!");
                return;
            }
            object = this.SYNC_INSTALL;
            synchronized (object) {
                this.m_bInstallationDialogShown = true;
                bl2 = false;
                if (!(this.m_helpModel.isHelpPathDefined() || this.m_helpModel.isHelpPathChangeable() && (bl2 = this.showInstallDialog(component)))) {
                    this.m_bInstallationDialogShown = false;
                    LogHolder.log(3, LogType.GUI, "Cannot show help externally: Help installation failed (changeable: " + this.m_helpModel.isHelpPathChangeable() + " showDialog: " + bl2 + ")");
                    this.m_alternativeHelp.setContext(this.getHelpContext());
                    this.m_alternativeHelp.setVisible(bl);
                    return;
                }
                this.m_bInstallationDialogShown = false;
            }
        }
        object = this.m_helpModel.getHelpURL(iHelpContext.getHelpContext() + ".html");
        bl2 = true;
        if (object == null || !(bl2 = AbstractOS.getInstance().openURL((URL)object))) {
            if (component != null && this.showInstallDialog(component) && (object = this.m_helpModel.getHelpURL(iHelpContext.getHelpContext() + ".html")) != null) {
                AbstractOS.getInstance().openURL((URL)object);
            } else {
                bl2 = false;
            }
        }
        if (!bl2) {
            LogHolder.log(3, LogType.GUI, "Error while trying to show context '" + iHelpContext.getHelpContext() + "' in external help!");
            this.m_alternativeHelp.setContext(this.getHelpContext());
            this.m_alternativeHelp.setVisible(bl);
        }
    }

    private boolean showInstallDialog(Component component) {
        if (this.m_helpModel.getHelpPath() == null || !this.m_helpModel.isHelpPathChangeable()) {
            return false;
        }
        final JAPDialog jAPDialog = new JAPDialog(component, JAPMessages.getString(MSG_HELP_INSTALL));
        final FileChooserContentPane fileChooserContentPane = new FileChooserContentPane(jAPDialog, JAPMessages.getString(MSG_HELP_PATH_CHOICE), this.m_helpModel.getHelpPath(), 1, "__FILE_CHOOSER_OPEN"){

            public DialogContentPane.CheckError checkYesOK() {
                DialogContentPane.CheckError checkError = super.checkYesOK();
                if (checkError != null) {
                    return checkError;
                }
                String string = JAPExternalHelpViewer.this.m_helpModel.helpPathValidityCheck(this.getFile());
                if (!string.equals("HELP_IS_VALID") && !string.equals("helpJonDoExists")) {
                    checkError = new DialogContentPane.CheckError(JAPMessages.getString(string));
                }
                return checkError;
            }

            public boolean isSkippedAsPreviousContentPane() {
                return true;
            }
        };
        Runnable runnable = new Runnable(){

            public void run() {
                JAPExternalHelpViewer.this.m_helpModel.setHelpPath(fileChooserContentPane.getFile());
            }
        };
        final WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_HELP_INSTALL_PROGRESS), fileChooserContentPane, runnable, this.m_helpModel.getHelpFileStorageObservable()){

            public boolean isSkippedAsNextContentPane() {
                return JAPExternalHelpViewer.this.m_helpModel.isHelpPathDefined() && fileChooserContentPane.getFile().getPath().equals(JAPExternalHelpViewer.this.m_helpModel.getHelpPath());
            }
        };
        SimpleWizardContentPane simpleWizardContentPane = new SimpleWizardContentPane(jAPDialog, JAPMessages.getString(MSG_HELP_INSTALL_SUCCESS), workerContentPane){

            public DialogContentPane.CheckError checkUpdate() {
                if (workerContentPane.getProgressStatus() != 0) {
                    jAPDialog.setTitle(JAPMessages.getString(JAPDialog.MSG_TITLE_ERROR));
                    this.setText("<font color='red'>" + JAPMessages.getString(MSG_HELP_INSTALL_FAILED) + " " + JAPMessages.getString(MSG_HELP_INTERNAL) + "</font>");
                }
                return null;
            }

            public boolean hideButtonCancel() {
                return true;
            }
        };
        fileChooserContentPane.pack();
        jAPDialog.setResizable(false);
        jAPDialog.setVisible(true);
        return workerContentPane.getProgressStatus() == 0;
    }

    protected JAPDialog getOwnDialog() {
        return null;
    }

    public URL getContextURL(String string) {
        URL uRL = this.m_helpModel.getHelpURL(string + ".html");
        if (uRL == null) {
            uRL = this.m_alternativeHelp.getContextURL(string);
        }
        return uRL;
    }

    public void loadCurrentContext() {
        if (this.getHelpContext() != null) {
            if (this.getHelpContext().getHelpExtractionDisplayContext() != null) {
                this.setVisible(true);
            } else {
                LogHolder.log(3, LogType.GUI, "Cannot show help externally: No display context specified");
                this.m_alternativeHelp.setContext(this.getHelpContext());
                this.m_alternativeHelp.loadCurrentContext();
            }
        } else {
            LogHolder.log(3, LogType.GUI, "Cannot show help externally: No help context specified");
            this.m_alternativeHelp.setContext(JAPHelpContext.INDEX_CONTEXT);
            this.m_alternativeHelp.loadCurrentContext();
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

