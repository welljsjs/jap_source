/*
 * Decompiled with CFR 0.150.
 */
package gui.wizard;

import gui.dialog.JAPDialog;
import gui.wizard.WizardHost;
import gui.wizard.WizardPage;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BasicWizardPage
extends JPanel
implements WizardPage {
    private String m_strTitle;
    private JLabel m_labelTitle;
    private ImageIcon m_Icon;
    private JLabel m_labelIcon;
    protected JPanel m_panelComponents;
    private String message;

    public BasicWizardPage() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_labelTitle = new JLabel();
        this.m_labelIcon = new JLabel();
        this.m_panelComponents = new JPanel();
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 1.0;
        gridBagLayout.setConstraints(this.m_labelIcon, gridBagConstraints);
        this.add(this.m_labelIcon);
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagLayout.setConstraints(this.m_labelTitle, gridBagConstraints);
        this.add(this.m_labelTitle);
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 10, 10);
        gridBagLayout.setConstraints(this.m_panelComponents, gridBagConstraints);
        this.add(this.m_panelComponents);
    }

    public void setPageTitle(String string) {
        this.m_strTitle = string;
        this.m_labelTitle.setText(string);
    }

    public void deactivated(WizardHost wizardHost) {
    }

    public void setIcon(ImageIcon imageIcon) {
        this.m_Icon = imageIcon;
        this.m_labelIcon.setIcon(imageIcon);
    }

    public void activated(WizardHost wizardHost) {
    }

    public JComponent getPageComponent() {
        return this;
    }

    public boolean checkPage() {
        return false;
    }

    public void showInformationDialog(String string) {
        JAPDialog.showMessageDialog(this, string);
    }

    public ImageIcon getIcon() {
        return this.m_Icon;
    }
}

