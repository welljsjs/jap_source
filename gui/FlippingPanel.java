/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.JAPMessages;
import gui.GUIUtils;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FlippingPanel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String MSG_CLICK_TO_SHOW = (class$gui$FlippingPanel == null ? (class$gui$FlippingPanel = FlippingPanel.class$("gui.FlippingPanel")) : class$gui$FlippingPanel).getName() + "_clickToShow";
    private JPanel m_panelContainer;
    private JPanel m_panelSmall;
    private JPanel m_panelFull;
    private JLabel m_labelBttn;
    private CardLayout m_Layout;
    private Window m_Parent;
    private boolean m_bIsFlipped = false;
    private static final String IMG_UP = "arrow.gif";
    private static final String IMG_DOWN = "arrow90.gif";
    private static final String IMG_NULL = "arrow_null.gif";
    static /* synthetic */ Class class$gui$FlippingPanel;

    public FlippingPanel(Window window) {
        this(window, false);
    }

    public FlippingPanel(Window window, boolean bl) {
        this.m_Parent = window;
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.setLayout(gridBagLayout);
        if (bl) {
            this.m_labelBttn = new JLabel(GUIUtils.loadImageIcon(IMG_NULL, true));
        } else {
            this.m_labelBttn = new JLabel(GUIUtils.loadImageIcon(IMG_UP, true));
            this.m_labelBttn.setCursor(Cursor.getPredefinedCursor(12));
            this.m_labelBttn.setToolTipText(JAPMessages.getString(MSG_CLICK_TO_SHOW));
            this.m_labelBttn.addMouseListener(new MouseListener(){

                public void mouseClicked(MouseEvent mouseEvent) {
                    FlippingPanel.this.m_bIsFlipped = !FlippingPanel.this.m_bIsFlipped;
                    FlippingPanel.this.m_Layout.next(FlippingPanel.this.m_panelContainer);
                    if (FlippingPanel.this.m_bIsFlipped) {
                        FlippingPanel.this.m_labelBttn.setIcon(GUIUtils.loadImageIcon(FlippingPanel.IMG_DOWN, true));
                    } else {
                        FlippingPanel.this.m_labelBttn.setIcon(GUIUtils.loadImageIcon(FlippingPanel.IMG_UP, true));
                    }
                    FlippingPanel.this.m_Parent.pack();
                }

                public void mouseEntered(MouseEvent mouseEvent) {
                }

                public void mouseExited(MouseEvent mouseEvent) {
                }

                public void mousePressed(MouseEvent mouseEvent) {
                }

                public void mouseReleased(MouseEvent mouseEvent) {
                }
            });
        }
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = new Insets(3, 0, 0, 0);
        gridBagConstraints.anchor = 18;
        this.add((Component)this.m_labelBttn, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        this.m_panelContainer = new JPanel();
        this.m_Layout = new CardLayout();
        this.m_panelContainer.setLayout(this.m_Layout);
        this.add((Component)this.m_panelContainer, gridBagConstraints);
        this.m_panelSmall = new JPanel(new GridLayout(1, 1));
        this.m_panelContainer.add(this.m_panelSmall, "SMALL", 0);
        this.m_panelFull = new JPanel(new GridLayout(1, 1));
        this.m_Layout.addLayoutComponent(this.m_panelFull, "FULL");
        this.m_panelContainer.add(this.m_panelFull, "FULL", 1);
    }

    public final void setFullPanel(JPanel jPanel) {
        this.m_panelFull.removeAll();
        this.m_panelFull.add(jPanel);
    }

    public JPanel getFullPanel() {
        return (JPanel)this.m_panelFull.getComponent(0);
    }

    public void setSmallPanel(JPanel jPanel) {
        this.m_panelSmall.removeAll();
        this.m_panelSmall.add(jPanel);
    }

    public JPanel getSmallPanel() {
        return (JPanel)this.m_panelSmall.getComponent(0);
    }

    public Dimension getPreferredSize() {
        Dimension dimension = this.m_panelFull.getPreferredSize();
        Dimension dimension2 = this.m_panelSmall.getPreferredSize();
        dimension.width = Math.max(dimension.width, dimension2.width);
        dimension.width += GUIUtils.loadImageIcon(IMG_UP, true).getIconWidth();
        if (!this.m_bIsFlipped) {
            dimension.height = dimension2.height;
        }
        return dimension;
    }

    public Dimension getMinimumSize() {
        Dimension dimension = this.m_panelFull.getMinimumSize();
        Dimension dimension2 = this.m_panelSmall.getMinimumSize();
        dimension.width = Math.max(dimension.width, dimension2.width);
        dimension.width += GUIUtils.loadImageIcon(IMG_UP, true).getIconWidth();
        if (!this.m_bIsFlipped) {
            dimension.height = dimension2.height;
        }
        dimension.height = Math.max(dimension.height, GUIUtils.loadImageIcon(IMG_DOWN, true).getIconHeight());
        return dimension;
    }

    public Dimension getMaximumSize() {
        Dimension dimension = this.m_panelFull.getMaximumSize();
        Dimension dimension2 = this.m_panelSmall.getMaximumSize();
        dimension.width = Math.max(dimension.width, dimension2.width);
        dimension.width += GUIUtils.loadImageIcon(IMG_UP, true).getIconWidth();
        if (!this.m_bIsFlipped) {
            dimension.height = dimension2.height;
        }
        return dimension;
    }

    public void setFlipped(boolean bl) {
        if (bl == this.m_bIsFlipped) {
            return;
        }
        this.m_labelBttn.dispatchEvent(new MouseEvent(this.m_labelBttn, 500, 0L, 0, 0, 0, 1, false));
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

