/*
 * Decompiled with CFR 0.150.
 */
package update;

import anon.infoservice.JAPVersionInfo;
import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPMultilineLabel;
import gui.wizard.BasicWizardPage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import logging.LogHolder;
import logging.LogType;
import update.JarFileFilter;

public class JAPWelcomeWizardPage
extends BasicWizardPage
implements ActionListener {
    private static final long serialVersionUID = 1L;
    public static final String MSG_CHANGELOG_URL = (class$update$JAPWelcomeWizardPage == null ? (class$update$JAPWelcomeWizardPage = JAPWelcomeWizardPage.class$("update.JAPWelcomeWizardPage")) : class$update$JAPWelcomeWizardPage).getName() + "_changelogURL";
    public static final String MSG_CHANGELOG_URL_BETA = (class$update$JAPWelcomeWizardPage == null ? (class$update$JAPWelcomeWizardPage = JAPWelcomeWizardPage.class$("update.JAPWelcomeWizardPage")) : class$update$JAPWelcomeWizardPage).getName() + "_changelogURLBeta";
    public static final String MSG_CHANGELOG = (class$update$JAPWelcomeWizardPage == null ? (class$update$JAPWelcomeWizardPage = JAPWelcomeWizardPage.class$("update.JAPWelcomeWizardPage")) : class$update$JAPWelcomeWizardPage).getName() + "_changelog";
    private static final String MSG_CHANGELOG_TT = (class$update$JAPWelcomeWizardPage == null ? (class$update$JAPWelcomeWizardPage = JAPWelcomeWizardPage.class$("update.JAPWelcomeWizardPage")) : class$update$JAPWelcomeWizardPage).getName() + "_changelogTT";
    private JTextField m_tfJapPath = null;
    private JLabel m_labelClickNext;
    private JButton m_bttnChooseJapFile = null;
    private File m_fileAktJapJar;
    private JCheckBox m_cbIncrementalUpdate;
    private JarFileFilter jarFileFilter = new JarFileFilter();
    private final String COMMAND_SEARCH = "SEARCH";
    private boolean m_bIncrementalUpdate = false;
    final JFileChooser m_fileChooser = new JFileChooser(ClassUtil.getClassDirectory(class$anon$util$ClassUtil == null ? (class$anon$util$ClassUtil = JAPWelcomeWizardPage.class$("anon.util.ClassUtil")) : class$anon$util$ClassUtil).getParent());
    static /* synthetic */ Class class$update$JAPWelcomeWizardPage;
    static /* synthetic */ Class class$anon$util$ClassUtil;

    public JAPWelcomeWizardPage(JAPVersionInfo jAPVersionInfo) {
        Serializable serializable;
        this.setIcon(GUIUtils.loadImageIcon("install.gif", false));
        this.setPageTitle(JAPMessages.getString("updateWelcomeWizardPageTitle", new Object[]{jAPVersionInfo.getJapVersion() + (jAPVersionInfo.getId().equals("/japRelease.jnlp") ? "" : "-dev")}));
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_panelComponents.setLayout(gridBagLayout);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        try {
            serializable = jAPVersionInfo.getId().equals("/japRelease.jnlp") ? new URL(JAPMessages.getString(MSG_CHANGELOG_URL) + "#" + jAPVersionInfo.getJapVersion()) : new URL(JAPMessages.getString(MSG_CHANGELOG_URL_BETA) + "#" + jAPVersionInfo.getJapVersion() + "-beta");
            final URL uRL = serializable;
            JLabel jLabel = new JLabel(JAPMessages.getString(MSG_CHANGELOG));
            jLabel.setToolTipText(JAPMessages.getString(MSG_CHANGELOG_TT));
            jLabel.setCursor(Cursor.getPredefinedCursor(12));
            jLabel.setForeground(Color.blue);
            jLabel.addMouseListener(new MouseAdapter(){

                public void mouseClicked(MouseEvent mouseEvent) {
                    AbstractOS.getInstance().openURL(uRL);
                }
            });
            gridBagConstraints.insets = new Insets(10, 0, 10, 0);
            this.m_panelComponents.add((Component)jLabel, gridBagConstraints);
        }
        catch (MalformedURLException malformedURLException) {
            LogHolder.log(2, LogType.GUI, malformedURLException);
        }
        gridBagConstraints.gridy = 1;
        JAPMultilineLabel jAPMultilineLabel = new JAPMultilineLabel(JAPMessages.getString("updateIntroductionMessage"));
        this.m_panelComponents.add((Component)jAPMultilineLabel, gridBagConstraints);
        this.m_tfJapPath = new JTextField(20);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 10);
        gridBagConstraints.fill = 2;
        gridBagLayout.setConstraints(this.m_tfJapPath, gridBagConstraints);
        this.m_panelComponents.add((Component)this.m_tfJapPath, gridBagConstraints);
        this.m_tfJapPath.setText(ClassUtil.getClassDirectory(class$anon$util$ClassUtil == null ? (class$anon$util$ClassUtil = JAPWelcomeWizardPage.class$("anon.util.ClassUtil")) : class$anon$util$ClassUtil).getParent() + System.getProperty("file.separator", "/") + "JAP.jar");
        this.m_bttnChooseJapFile = new JButton(JAPMessages.getString("updateM_chooseFolder_bttn"));
        gridBagConstraints.anchor = 13;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints.fill = 0;
        gridBagLayout.setConstraints(this.m_bttnChooseJapFile, gridBagConstraints);
        this.m_panelComponents.add(this.m_bttnChooseJapFile);
        this.m_bttnChooseJapFile.addActionListener(this);
        this.m_bttnChooseJapFile.setActionCommand("SEARCH");
        this.m_cbIncrementalUpdate = new JCheckBox(JAPMessages.getString("updateM_doIncrementalUpdate"));
        this.m_cbIncrementalUpdate.setVisible(false);
        this.m_cbIncrementalUpdate.setToolTipText(JAPMessages.getString("updateM_doIncrementalUpdate"));
        this.m_cbIncrementalUpdate.setSelected(this.m_bIncrementalUpdate);
        gridBagConstraints.insets = new Insets(0, 10, 10, 0);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagLayout.setConstraints(this.m_cbIncrementalUpdate, gridBagConstraints);
        this.m_panelComponents.add(this.m_cbIncrementalUpdate);
        this.m_labelClickNext = new JLabel(JAPMessages.getString("updateM_labelClickNext"));
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagLayout.setConstraints(this.m_labelClickNext, gridBagConstraints);
        this.m_panelComponents.add(this.m_labelClickNext);
        serializable = new JLabel("");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 3;
        gridBagLayout.setConstraints((Component)serializable, gridBagConstraints);
        this.m_panelComponents.add((Component)serializable);
    }

    public boolean checkPage() {
        boolean bl = false;
        if (!this.m_tfJapPath.getText().equals("")) {
            File file = new File(this.m_tfJapPath.getText());
            if (file.isFile() && file.exists()) {
                this.m_fileAktJapJar = file;
                bl = true;
            } else {
                this.showInformationDialog(JAPMessages.getString("updateM_SelectedJapJarDoesNotExist"));
                bl = false;
            }
        }
        return bl;
    }

    public File getJapJarFile() {
        return this.m_fileAktJapJar;
    }

    public boolean isIncrementalUpdate() {
        return this.m_cbIncrementalUpdate.isSelected();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("SEARCH")) {
            this.m_fileChooser.setDialogTitle(JAPMessages.getString("updateM_fileChooserTitle"));
            this.m_fileChooser.setApproveButtonText(JAPMessages.getString("updateM_fileChooserApprove_bttn"));
            this.m_fileChooser.setFileSelectionMode(0);
            this.m_fileChooser.addChoosableFileFilter(this.jarFileFilter);
            int n = GUIUtils.showMonitoredFileChooser(this.m_fileChooser, this, "__FILE_CHOOSER_OPEN");
            if (n == 0) {
                this.m_fileAktJapJar = this.m_fileChooser.getSelectedFile();
                if (!this.m_fileAktJapJar.isFile()) {
                    this.m_fileChooser.cancelSelection();
                    this.showInformationDialog(JAPMessages.getString("updateM_fileChooserDialogNotAFile"));
                    this.m_tfJapPath.setText("");
                } else if (!this.m_fileAktJapJar.exists()) {
                    if (this.m_tfJapPath.getText().equals("")) {
                        this.m_fileChooser.cancelSelection();
                        this.showInformationDialog(JAPMessages.getString("updateM_fileChooserDialogFileNotExists"));
                        this.m_tfJapPath.setText("");
                    } else {
                        this.m_tfJapPath.getText();
                    }
                } else {
                    this.m_tfJapPath.setText(this.m_fileAktJapJar.getAbsolutePath());
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

