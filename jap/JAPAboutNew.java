/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.util.JAPMessages;
import anon.util.ResourceLoader;
import gui.JAPDll;
import gui.JAPHyperlinkAdapter;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

public class JAPAboutNew
extends JAPDialog {
    private static final String MSG_VERSION = (class$jap$JAPAboutNew == null ? (class$jap$JAPAboutNew = JAPAboutNew.class$("jap.JAPAboutNew")) : class$jap$JAPAboutNew).getName() + "_version";
    private static final String MSG_DLL_VERSION = (class$jap$JAPAboutNew == null ? (class$jap$JAPAboutNew = JAPAboutNew.class$("jap.JAPAboutNew")) : class$jap$JAPAboutNew).getName() + "_dllVersion";
    static /* synthetic */ Class class$jap$JAPAboutNew;

    public JAPAboutNew(Component component) {
        super(component, "JAP/JonDo 00.20.001" + (JAPDll.getDllVersion() != null ? " (" + JAPMessages.getString(MSG_DLL_VERSION) + ": " + JAPDll.getDllVersion() + ")" : ""));
        DialogContentPane dialogContentPane = new DialogContentPane((JAPDialog)this, (DialogContentPane.Layout)null, new DialogContentPaneOptions(-1));
        dialogContentPane.setDefaultButtonOperation(2);
        String string = JAPAboutNew.loadAboutText();
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        jEditorPane.addHyperlinkListener(new JAPHyperlinkAdapter());
        jEditorPane.setDoubleBuffered(false);
        this.setResizable(false);
        jEditorPane.setContentType("text/html");
        jEditorPane.setText(string.trim());
        JScrollPane jScrollPane = new JScrollPane(jEditorPane, 20, 31);
        dialogContentPane.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.weightx = 1.0;
        dialogContentPane.getContentPane().add((Component)jScrollPane, gridBagConstraints);
        jScrollPane.setPreferredSize(new Dimension(400, 300));
        dialogContentPane.updateDialog();
        this.pack();
    }

    public static String loadAboutText() {
        String string = JAPMessages.getString("htmlfileAbout");
        String string2 = new String(ResourceLoader.loadResource(string));
        int n = -1;
        int n2 = -1;
        String string3 = "</table>";
        n = string2.indexOf(string3);
        n2 = string2.lastIndexOf("<table");
        if (n2 < 0) {
            n2 = string2.length();
        }
        if (n >= 0 && n2 > 0 && n != n2) {
            string2 = string2.substring(n + string3.length(), n2);
        }
        return string2;
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

