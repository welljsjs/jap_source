/*
 * Decompiled with CFR 0.150.
 */
package gui.wizard;

import javax.swing.JComponent;

public interface WizardPage {
    public boolean checkPage();

    public void showInformationDialog(String var1);

    public JComponent getPageComponent();
}

