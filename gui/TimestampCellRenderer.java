/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.Util;
import java.awt.Component;
import java.sql.Timestamp;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TimestampCellRenderer
extends DefaultTableCellRenderer {
    private boolean m_bWithTime;

    public TimestampCellRenderer(boolean bl) {
        this.m_bWithTime = bl;
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
        if (!(object instanceof Timestamp)) {
            this.setText("Error - not a Timestamp!");
            return this;
        }
        this.setFont(jTable.getFont());
        this.setText(Util.formatTimestamp((Timestamp)object, this.m_bWithTime));
        return this;
    }
}

