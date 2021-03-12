/*
 * Decompiled with CFR 0.150.
 */
package update;

import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPMultilineLabel;
import gui.wizard.BasicWizardPage;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

public class JAPFinishWizardPage
extends BasicWizardPage {
    public JLabel m_labelBackupOfJapJar;

    public JAPFinishWizardPage() {
        this.setIcon(GUIUtils.loadImageIcon("install.gif", false));
        this.setPageTitle(JAPMessages.getString("updateTitel_Update-WizardFertig"));
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_panelComponents.setLayout(gridBagLayout);
        JAPMultilineLabel jAPMultilineLabel = new JAPMultilineLabel(JAPMessages.getString("updateFinishMessage"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 18;
        gridBagLayout.setConstraints(jAPMultilineLabel, gridBagConstraints);
        this.m_panelComponents.add((Component)jAPMultilineLabel, gridBagConstraints);
        this.m_labelBackupOfJapJar = new JLabel();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.fill = 2;
        gridBagLayout.setConstraints(this.m_labelBackupOfJapJar, gridBagConstraints);
        this.m_panelComponents.add(this.m_labelBackupOfJapJar);
        JLabel jLabel = new JLabel();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        this.m_panelComponents.add(jLabel);
    }
}

