/*
 * Decompiled with CFR 0.150.
 */
package gui.wizard;

import gui.wizard.Wizard;
import gui.wizard.WizardHost;
import gui.wizard.WizardPage;
import java.util.Vector;

public class BasicWizard
implements Wizard {
    private WizardHost wizardHost = null;
    protected Vector m_Pages = new Vector();
    private String m_strTitle;
    protected int m_PageIndex = 0;

    public void setHost(WizardHost wizardHost) {
        this.wizardHost = wizardHost;
    }

    public WizardHost getHost() {
        return this.wizardHost;
    }

    public void help() {
    }

    public WizardPage invokeWizard() {
        this.wizardHost.setBackEnabled(false);
        this.wizardHost.setFinishEnabled(false);
        this.wizardHost.showWizardPage(0);
        this.m_PageIndex = 0;
        return null;
    }

    public WizardPage next() {
        ++this.m_PageIndex;
        this.wizardHost.setBackEnabled(true);
        if (this.m_PageIndex == this.m_Pages.size() - 1) {
            this.wizardHost.setFinishEnabled(true);
            this.wizardHost.setNextEnabled(false);
        }
        this.wizardHost.showWizardPage(this.m_PageIndex);
        return null;
    }

    public WizardPage back() {
        --this.m_PageIndex;
        this.wizardHost.setNextEnabled(true);
        this.wizardHost.setFinishEnabled(false);
        if (this.m_PageIndex == 0) {
            this.wizardHost.setBackEnabled(false);
        }
        this.wizardHost.showWizardPage(this.m_PageIndex);
        return null;
    }

    public void addWizardPage(int n, WizardPage wizardPage) {
        this.m_Pages.insertElementAt(wizardPage, n);
        this.wizardHost.addWizardPage(n, wizardPage);
    }

    public int initTotalSteps() {
        return this.m_Pages.size();
    }

    public WizardPage finish() {
        return null;
    }

    public void wizardCompleted() {
    }

    public void setWizardTitle(String string) {
        this.m_strTitle = string;
    }

    public String getWizardTitle() {
        return this.m_strTitle;
    }
}

