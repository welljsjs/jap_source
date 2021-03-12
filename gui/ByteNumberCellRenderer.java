/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.Util;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ByteNumberCellRenderer
extends DefaultTableCellRenderer {
    public ByteNumberCellRenderer() {
        this.setHorizontalAlignment(4);
    }

    public Component getTableCellRendererComponent(JTable jTable, Object object, boolean bl, boolean bl2, int n, int n2) {
        if (bl) {
            super.setForeground(jTable.getSelectionForeground());
            super.setBackground(jTable.getSelectionBackground());
        } else {
            super.setForeground(jTable.getForeground());
            super.setBackground(jTable.getBackground());
        }
        this.setFont(jTable.getFont());
        if (!(object instanceof Long)) {
            this.setText("Error - no Long!");
            return this;
        }
        this.setText(Util.formatBytesValueWithUnit((Long)object));
        return this;
    }
}

