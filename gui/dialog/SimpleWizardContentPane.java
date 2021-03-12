/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;

public class SimpleWizardContentPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable {
    public SimpleWizardContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, DialogContentPaneOptions dialogContentPaneOptions) {
        super(jAPDialog, string, layout, dialogContentPaneOptions);
    }

    public SimpleWizardContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, DialogContentPane dialogContentPane) {
        super(jAPDialog, string, layout, new DialogContentPaneOptions(dialogContentPane));
    }

    public SimpleWizardContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout) {
        super(jAPDialog, string, layout, null);
    }

    public SimpleWizardContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, int n, DialogContentPane dialogContentPane) {
        super(jAPDialog, string, layout, new DialogContentPaneOptions(n, dialogContentPane));
    }

    public SimpleWizardContentPane(JAPDialog jAPDialog, String string, DialogContentPaneOptions dialogContentPaneOptions) {
        super(jAPDialog, string, new DialogContentPane.Layout(), dialogContentPaneOptions);
    }

    public SimpleWizardContentPane(JAPDialog jAPDialog, String string, DialogContentPane dialogContentPane) {
        super(jAPDialog, string, new DialogContentPane.Layout(), new DialogContentPaneOptions(-1, dialogContentPane));
    }
}

