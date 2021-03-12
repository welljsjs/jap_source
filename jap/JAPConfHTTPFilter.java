/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.util.JAPMessages;
import jap.AbstractJAPConfModule;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

final class JAPConfHTTPFilter
extends AbstractJAPConfModule
implements Observer,
ActionListener {
    private TitledBorder m_borderCert;
    private JRadioButton m_btnFilterOn;
    private JRadioButton m_btnFilterOff;
    private JCheckBox m_boxUserAgent;
    private JCheckBox m_boxLanguage;
    private JCheckBox m_boxEncoding;
    private JCheckBox m_boxFileTypes;

    public JAPConfHTTPFilter() {
        super(null);
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        this.m_borderCert = new TitledBorder(JAPMessages.getString("confHTTPFilterTab"));
        jPanel.setBorder(this.m_borderCert);
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 18;
        this.m_btnFilterOn = new JRadioButton("jap.JAPConfHTTPFilter_filterOn");
        this.m_btnFilterOn.setSelected(true);
        this.m_btnFilterOn.addActionListener(this);
        jPanel.add((Component)this.m_btnFilterOn, gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 20, 0, 0);
        ++gridBagConstraints.gridy;
        this.m_boxUserAgent = new JCheckBox("jap.JAPConfHTTPFilter_userAgent");
        this.m_boxUserAgent.setSelected(true);
        jPanel.add((Component)this.m_boxUserAgent, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_boxLanguage = new JCheckBox("jap.JAPConfHTTPFilter_language");
        this.m_boxLanguage.setSelected(true);
        jPanel.add((Component)this.m_boxLanguage, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_boxEncoding = new JCheckBox("jap.JAPConfHTTPFilter_encoding");
        this.m_boxEncoding.setSelected(true);
        jPanel.add((Component)this.m_boxEncoding, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_boxFileTypes = new JCheckBox("jap.JAPConfHTTPFilter_fileTypes");
        this.m_boxFileTypes.setSelected(true);
        jPanel.add((Component)this.m_boxFileTypes, gridBagConstraints);
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        ++gridBagConstraints.gridy;
        gridBagConstraints.weighty = 1.0;
        this.m_btnFilterOff = new JRadioButton("jap.JAPConfHTTPFilter_filterOff");
        this.m_btnFilterOff.addActionListener(this);
        jPanel.add((Component)this.m_btnFilterOff, gridBagConstraints);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.m_btnFilterOn);
        buttonGroup.add(this.m_btnFilterOff);
    }

    public String getTabTitle() {
        return JAPMessages.getString("confHTTPFilterTab");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.m_btnFilterOff) {
            this.m_boxUserAgent.setEnabled(false);
            this.m_boxLanguage.setEnabled(false);
            this.m_boxEncoding.setEnabled(false);
            this.m_boxFileTypes.setEnabled(false);
        } else if (actionEvent.getSource() == this.m_btnFilterOn) {
            this.m_boxUserAgent.setEnabled(true);
            this.m_boxLanguage.setEnabled(true);
            this.m_boxEncoding.setEnabled(true);
            this.m_boxFileTypes.setEnabled(true);
        }
    }

    public void update(Observable observable, Object object) {
    }

    protected void onUpdateValues() {
    }

    protected boolean onOkPressed() {
        return true;
    }

    protected void onResetToDefaultsPressed() {
    }

    public String getHelpContext() {
        return "httpFilter";
    }
}

