/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.util.JAPMessages;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;

public class FinishedContentPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable {
    private static final String MSG_FINISHING = (class$gui$dialog$FinishedContentPane == null ? (class$gui$dialog$FinishedContentPane = FinishedContentPane.class$("gui.dialog.FinishedContentPane")) : class$gui$dialog$FinishedContentPane).getName() + "_finishing";
    static /* synthetic */ Class class$gui$dialog$FinishedContentPane;

    public FinishedContentPane(JAPDialog jAPDialog, String string, DialogContentPane dialogContentPane) {
        this(jAPDialog, string, JAPMessages.getString(MSG_FINISHING), dialogContentPane);
    }

    public FinishedContentPane(JAPDialog jAPDialog, String string, String string2, DialogContentPane dialogContentPane) {
        super(jAPDialog, string, new DialogContentPane.Layout(string2, 1), new DialogContentPaneOptions(dialogContentPane));
        this.setDefaultButtonOperation(41216);
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

