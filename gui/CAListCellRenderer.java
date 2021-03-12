/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.crypto.CertificateInfoStructure;
import gui.GUIUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public final class CAListCellRenderer
extends JLabel
implements ListCellRenderer {
    public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
        CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)object;
        String string = certificateInfoStructure.getCertificate().getSubject().getCommonName();
        if (string == null) {
            string = certificateInfoStructure.getCertificate().getSubject().toString();
        }
        this.setText(string);
        this.setEnabled(jList.isEnabled());
        if (bl) {
            this.setBackground(jList.getSelectionBackground());
            this.setForeground(jList.getSelectionForeground());
        } else {
            this.setBackground(jList.getBackground());
            this.setForeground(jList.getForeground());
        }
        if (certificateInfoStructure.isEnabled()) {
            this.setIcon(GUIUtils.loadImageIcon("cenabled.gif", false));
        } else {
            this.setForeground(Color.red);
            this.setIcon(GUIUtils.loadImageIcon("cdisabled.gif", false));
        }
        this.setFont(jList.getFont());
        this.setOpaque(true);
        return this;
    }
}

