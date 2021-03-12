/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.ServiceOperator;
import gui.GUIUtils;
import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

public class OperatorsCellRenderer
extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public void setValue(Object object) {
        if (object == null) {
            this.setText("");
            return;
        }
        if (object instanceof ServiceOperator) {
            ServiceOperator serviceOperator = (ServiceOperator)object;
            this.setForeground(Color.black);
            if (serviceOperator.getCertificate() == null) {
                this.setForeground(Color.gray);
            }
            this.setText(serviceOperator.getOrganization());
            this.setIcon(GUIUtils.loadImageIcon("flags/" + serviceOperator.getCountryCode() + ".png"));
        }
    }
}

