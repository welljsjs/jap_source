/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class JAPMultilineLabel
extends JPanel {
    private Font m_font;

    public JAPMultilineLabel(String string) {
        this(string, new JLabel().getFont());
    }

    public JAPMultilineLabel(String string, Color color) {
        this(string, new JLabel().getFont(), color);
    }

    public JAPMultilineLabel(Font font) {
        this("", font);
    }

    public JAPMultilineLabel(String string, Font font) {
        this(string, font, null);
    }

    public JAPMultilineLabel(String string, Font font, Color color) {
        this.m_font = font;
        this.setText(string, color);
    }

    public void setText(String string, Color color) {
        this.removeAll();
        this.setLayout(new GridLayout(0, 1, 0, 0));
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\n");
        while (stringTokenizer.hasMoreElements()) {
            JLabel jLabel = new JLabel(stringTokenizer.nextToken());
            if (this.m_font != null) {
                jLabel.setFont(this.m_font);
            }
            if (color != null) {
                jLabel.setForeground(color);
            }
            this.add(jLabel);
        }
    }

    public void setText(String string) {
        this.setText(string, null);
    }
}

