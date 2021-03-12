/*
 * Decompiled with CFR 0.150.
 */
package gui.wizard;

import gui.wizard.WizardHost;
import gui.wizard.WizardPage;

public interface Wizard {
    public void setHost(WizardHost var1);

    public WizardHost getHost();

    public WizardPage invokeWizard();

    public WizardPage finish();

    public WizardPage next();

    public WizardPage back();

    public void help();

    public int initTotalSteps();

    public void wizardCompleted();

    public String getWizardTitle();
}

