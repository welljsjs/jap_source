/*
 * Decompiled with CFR 0.150.
 */
package gui.wizard;

import gui.dialog.JAPDialog;
import gui.wizard.WizardPage;

public interface WizardHost {
    public JAPDialog getDialogParent();

    public void setFinishEnabled(boolean var1);

    public void setNextEnabled(boolean var1);

    public void setCancelEnabled(boolean var1);

    public void setBackEnabled(boolean var1);

    public void setHelpEnabled(boolean var1);

    public void addWizardPage(int var1, WizardPage var2);

    public void showWizardPage(int var1);
}

