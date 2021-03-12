/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.terms.TermsAndConditions;
import anon.util.JAPMessages;
import gui.JapHtmlPane;
import gui.UpperLeftStartViewport;
import gui.dialog.JAPDialog;
import gui.dialog.TermsAndConditionsPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class TermsAndConditionsDialog
extends JAPDialog {
    TermsAndConditionsPane m_panel;
    TermsAndConditonsDialogReturnValues m_ret = new TermsAndConditonsDialogReturnValues();
    boolean acceptInitialValue = false;
    public static final String HTML_EXPORT_ENCODING = "ISO-8859-1";
    public static final String MSG_DIALOG_TITLE = (class$gui$TermsAndConditionsDialog == null ? (class$gui$TermsAndConditionsDialog = TermsAndConditionsDialog.class$("gui.TermsAndConditionsDialog")) : class$gui$TermsAndConditionsDialog).getName() + "_dialogTitle";
    static /* synthetic */ Class class$gui$TermsAndConditionsDialog;

    public TermsAndConditionsDialog(Component component, boolean bl, TermsAndConditions termsAndConditions) {
        this(component, bl, termsAndConditions, JAPMessages.getLocale().getLanguage());
    }

    public TermsAndConditionsDialog(Component component, boolean bl, TermsAndConditions termsAndConditions, String string) {
        super(component, JAPMessages.getString(MSG_DIALOG_TITLE, termsAndConditions.getOperator().getOrganization()));
        this.setResizable(false);
        this.m_panel = new TermsAndConditionsPane((JAPDialog)this, bl, new TermsAndConditionsPane.TermsAndConditionsMessages());
        this.m_panel.setText(termsAndConditions.getHTMLText(string));
        this.m_panel.updateDialog();
        this.pack();
    }

    public static void previewTranslation(Component component, TermsAndConditions.Translation translation) {
        final String string = TermsAndConditions.getHTMLText(translation);
        JapHtmlPane japHtmlPane = new JapHtmlPane(string, new UpperLeftStartViewport());
        japHtmlPane.setPreferredSize(new Dimension(800, 600));
        final JAPDialog jAPDialog = new JAPDialog(component, "Translation preview [" + translation + "]");
        Container container = jAPDialog.getContentPane();
        container.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel();
        final JButton jButton = new JButton(JAPMessages.getString("bttnSaveAs"));
        final JButton jButton2 = new JButton(JAPMessages.getString("bttnClose"));
        String string2 = translation.getOperator() != null ? (translation.getOperator().getOrganization() != null ? translation.getOperator().getOrganization() : "???") : "???";
        final String string3 = "Terms_" + string2 + "_" + translation.getLocale() + ".html";
        ActionListener actionListener = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getSource() == jButton) {
                    TermsAndConditionsDialog.actionExportHTMLToFile(jAPDialog.getContentPane(), string, string3);
                } else if (actionEvent.getSource() == jButton2) {
                    jAPDialog.dispose();
                }
            }
        };
        jButton.addActionListener(actionListener);
        jButton2.addActionListener(actionListener);
        jPanel.add(jButton);
        jPanel.add(jButton2);
        container.add((Component)jPanel, "North");
        container.add((Component)japHtmlPane, "South");
        container.add(japHtmlPane);
        jAPDialog.setDefaultCloseOperation(2);
        jAPDialog.pack();
        jAPDialog.setVisible(true);
    }

    public TermsAndConditonsDialogReturnValues getReturnValues() {
        this.m_ret.setCancelled(this.m_panel.getButtonValue() != 0);
        this.m_ret.setAccepted(this.m_panel.isTermsAccepted());
        return this.m_ret;
    }

    private static void actionExportHTMLToFile(Component component, String string, String string2) {
        JFileChooser jFileChooser = new JFileChooser();
        File file = new File(jFileChooser.getCurrentDirectory() + File.separator + string2);
        jFileChooser.setSelectedFile(file);
        int n = jFileChooser.showSaveDialog(component);
        switch (n) {
            case 0: {
                File file2 = jFileChooser.getSelectedFile();
                boolean bl = true;
                if (file2.exists()) {
                    boolean bl2 = bl = JAPDialog.showConfirmDialog(component, "File already " + file2.getName() + " already exists. Do you want to replace it?", 0, 3) == 0;
                }
                if (!bl) break;
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream(file2), HTML_EXPORT_ENCODING);
                    outputStreamWriter.write(string);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                }
                catch (IOException iOException) {
                    JAPDialog.showErrorDialog(component, "Could not export to " + file2.getName(), (Throwable)iOException);
                }
                break;
            }
            case 1: {
                break;
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

    public class TermsAndConditonsDialogReturnValues {
        private boolean cancelled = false;
        private boolean accepted = false;

        public boolean isCancelled() {
            return this.cancelled;
        }

        public void setCancelled(boolean bl) {
            this.cancelled = bl;
        }

        public boolean isAccepted() {
            return this.accepted;
        }

        public void setAccepted(boolean bl) {
            this.accepted = bl;
        }
    }
}

