/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class JAPProgressBar
extends JProgressBar {
    private static final long serialVersionUID = 1L;
    private final MyProgressBarUI m_ui = new MyProgressBarUI(true);

    public JAPProgressBar() {
        this.m_ui.setFilledBarColor(Color.blue);
        super.setUI(this.m_ui);
    }

    public void setUI(ProgressBarUI progressBarUI) {
    }

    public ProgressBarUI getUI() {
        return this.m_ui;
    }

    public void setFilledBarColor(Color color) {
        this.m_ui.setFilledBarColor(color);
    }

    public Color getFilledbarColor() {
        return this.m_ui.getFilledBarColor();
    }

    private final class MyProgressBarUI
    extends BasicProgressBarUI {
        static final int ms_dx = 13;
        static final int ms_width = 9;
        private boolean m_bOneBarPerValue = false;
        private Color m_colFilledBar;

        public MyProgressBarUI(boolean bl) {
            this.m_bOneBarPerValue = bl;
            this.m_colFilledBar = null;
        }

        public void paint(Graphics graphics, JComponent jComponent) {
            int n;
            JProgressBar jProgressBar = (JProgressBar)jComponent;
            int n2 = jProgressBar.getMaximum();
            int n3 = jProgressBar.getWidth() / 13;
            int n4 = jProgressBar.getValue() * n3 / n2;
            int n5 = 0;
            int n6 = 0;
            int n7 = jComponent.getHeight() - 1;
            Color color = graphics.getColor();
            if (this.m_colFilledBar != null) {
                graphics.setColor(this.m_colFilledBar);
            }
            for (n = 0; n < n4; ++n) {
                graphics.fill3DRect(n5, n6, 9, n7 + 1, false);
                n5 += 13;
            }
            graphics.setColor(color);
            for (n = n4; n < n3; ++n) {
                graphics.draw3DRect(n5, n6, 9, n7, false);
                n5 += 13;
            }
        }

        public void setFilledBarColor(Color color) {
            this.m_colFilledBar = color;
        }

        public Color getFilledBarColor() {
            return this.m_colFilledBar;
        }

        public Dimension getPreferredSize(JComponent jComponent) {
            if (!this.m_bOneBarPerValue) {
                return super.getPreferredSize(jComponent);
            }
            JProgressBar jProgressBar = (JProgressBar)jComponent;
            return new Dimension(13 * jProgressBar.getMaximum(), 12);
        }

        public Dimension getMinimumSize(JComponent jComponent) {
            if (!this.m_bOneBarPerValue) {
                return super.getMinimumSize(jComponent);
            }
            return this.getPreferredSize(jComponent);
        }

        public Dimension getMaximumSize(JComponent jComponent) {
            if (!this.m_bOneBarPerValue) {
                return super.getMaximumSize(jComponent);
            }
            return this.getPreferredSize(jComponent);
        }
    }
}

