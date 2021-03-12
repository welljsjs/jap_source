/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class FileChooserContentPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable {
    public static final String MSG_CHOOSE_FILE = (class$gui$dialog$FileChooserContentPane == null ? (class$gui$dialog$FileChooserContentPane = FileChooserContentPane.class$("gui.dialog.FileChooserContentPane")) : class$gui$dialog$FileChooserContentPane).getName() + "_errorChooseFile";
    private static final String MSG_CHOOSE_DIR = (class$gui$dialog$FileChooserContentPane == null ? (class$gui$dialog$FileChooserContentPane = FileChooserContentPane.class$("gui.dialog.FileChooserContentPane")) : class$gui$dialog$FileChooserContentPane).getName() + "_errorChooseDirectory";
    private JTextField pathField;
    private JFileChooser chooser;
    private int m_fileSelectionMode;
    static /* synthetic */ Class class$gui$dialog$FileChooserContentPane;

    public FileChooserContentPane(JAPDialog jAPDialog, String string, String string2, int n, String string3) {
        this(jAPDialog, string, new DialogContentPane.Layout(""), string2, n, string3, null, null);
    }

    public FileChooserContentPane(JAPDialog jAPDialog, String string, String string2, int n, String string3, FileFilter fileFilter) {
        this(jAPDialog, string, new DialogContentPane.Layout(""), string2, n, string3, fileFilter, null);
    }

    public FileChooserContentPane(JAPDialog jAPDialog, String string, String string2, int n, String string3, DialogContentPane dialogContentPane) {
        this(jAPDialog, string, new DialogContentPane.Layout(""), string2, n, string3, null, dialogContentPane);
    }

    public FileChooserContentPane(JAPDialog jAPDialog, String string, String string2, int n, String string3, FileFilter fileFilter, DialogContentPane dialogContentPane) {
        this(jAPDialog, string, new DialogContentPane.Layout(""), string2, n, string3, fileFilter, dialogContentPane);
    }

    public FileChooserContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, String string2, int n, String string3) {
        this(jAPDialog, string, layout, string2, n, string3, null, null);
    }

    public FileChooserContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, String string2, int n, String string3, FileFilter fileFilter) {
        this(jAPDialog, string, layout, string2, n, string3, fileFilter, null);
    }

    public FileChooserContentPane(final JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, String string2, int n, final String string3, FileFilter fileFilter, DialogContentPane dialogContentPane) {
        super(jAPDialog, string, layout, new DialogContentPaneOptions(2, dialogContentPane));
        JButton jButton = new JButton(JAPMessages.getString("bttnChoose"));
        this.pathField = new JTextField(15);
        this.pathField.setEditable(false);
        if (string2 != null) {
            this.pathField.setText(string2);
        }
        this.chooser = new JFileChooser();
        this.chooser.setFileSelectionMode(n);
        if (fileFilter != null) {
            this.chooser.setFileFilter(fileFilter);
        }
        this.m_fileSelectionMode = this.chooser.getFileSelectionMode();
        this.getContentPane().add(this.pathField);
        this.getContentPane().add(jButton);
        ActionListener actionListener = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                File file = FileChooserContentPane.this.getFile();
                if (file != null && file.isDirectory()) {
                    FileChooserContentPane.this.chooser.setCurrentDirectory(file);
                }
                if (GUIUtils.showMonitoredFileChooser(FileChooserContentPane.this.chooser, jAPDialog.getContentPane(), string3) == 0) {
                    File file2 = FileChooserContentPane.this.chooser.getSelectedFile();
                    if (file2 != null) {
                        FileChooserContentPane.this.pathField.setText(file2.getPath());
                    }
                    FileChooserContentPane.this.clearStatusMessage();
                }
            }
        };
        jButton.addActionListener(actionListener);
    }

    public File getFile() {
        String string = this.pathField.getText();
        if (string != null) {
            string = string.trim();
        }
        if (string.length() > 0) {
            return new File(string);
        }
        return null;
    }

    public DialogContentPane.CheckError checkYesOK() {
        String string = this.m_fileSelectionMode == 1 ? JAPMessages.getString(MSG_CHOOSE_DIR) : JAPMessages.getString(MSG_CHOOSE_FILE);
        File file = this.getFile();
        if (file == null || this.m_fileSelectionMode == 1 && !file.isDirectory() || this.m_fileSelectionMode == 0 && file.isDirectory()) {
            return new DialogContentPane.CheckError(string);
        }
        return null;
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

