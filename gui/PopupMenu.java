/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.infoservice.JavaVersionDBEntry;
import gui.GUIUtils;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class PopupMenu {
    private ExitHandler m_exitHandler;
    private Component m_popup;
    private GridBagConstraints m_constraints;
    private Window m_parent;
    private boolean m_bParentOnTop = false;
    private Vector m_popupListeners;
    private Vector m_registeredComponents;
    private boolean m_bCompatibilityMode;

    public PopupMenu() {
        this(new JPopupMenu());
    }

    public PopupMenu(boolean bl) {
        this(new JPopupMenu(), bl);
    }

    public PopupMenu(JPopupMenu jPopupMenu) {
        this(jPopupMenu, false);
    }

    private PopupMenu(JPopupMenu jPopupMenu, boolean bl) {
        Object object;
        if (jPopupMenu == null) {
            throw new IllegalArgumentException("Given argument is null!");
        }
        this.m_bCompatibilityMode = bl;
        if (this.m_bCompatibilityMode) {
            this.m_popup = new JWindow();
            object = new JPanel();
            ((JComponent)object).setBorder(new JPopupMenu().getBorder());
            ((JWindow)this.m_popup).setContentPane((Container)object);
            ((JWindow)this.m_popup).getContentPane().setLayout(new GridBagLayout());
            this.m_popup.addComponentListener(new ComponentAdapter(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void componentHidden(ComponentEvent componentEvent) {
                    Vector vector = PopupMenu.this.m_popupListeners;
                    synchronized (vector) {
                        for (int i = 0; i < PopupMenu.this.m_popupListeners.size(); ++i) {
                            ((PopupMenuListener)PopupMenu.this.m_popupListeners.elementAt(i)).popupMenuWillBecomeInvisible(new PopupMenuEvent(componentEvent.getSource()));
                        }
                    }
                }
            });
        } else {
            this.m_popup = jPopupMenu;
            jPopupMenu.addPopupMenuListener(new PopupMenuListener(){

                public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                    PopupMenu.this.resetParentOnTopAttribute();
                }

                public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
                }
            });
            jPopupMenu.addMouseMotionListener(new MouseMotionAdapter(){});
        }
        this.m_popupListeners = new Vector();
        this.m_registeredComponents = new Vector();
        this.m_constraints = new GridBagConstraints();
        this.m_constraints.gridx = 0;
        this.m_constraints.gridy = 0;
        this.m_constraints.weighty = 1.0;
        this.m_constraints.fill = 2;
        this.m_constraints.anchor = 17;
        this.m_popup.setName(Double.toString(new Random().nextDouble()));
        object = new MouseAdapter(){

            public void mouseExited(MouseEvent mouseEvent) {
                ExitHandler exitHandler = PopupMenu.this.m_exitHandler;
                if (exitHandler != null) {
                    exitHandler.exited();
                }
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    PopupMenu.this.dispose();
                }
            }
        };
        this.m_popup.addMouseListener((MouseListener)object);
        this.registerExitHandler(null);
    }

    protected void removeAll() {
        if (this.m_bCompatibilityMode) {
            ((JWindow)this.m_popup).getContentPane().removeAll();
            this.m_constraints.gridy = 0;
            this.m_registeredComponents.removeAllElements();
        } else {
            ((JPopupMenu)this.m_popup).removeAll();
        }
    }

    protected void insert(Component component, int n) {
        if (this.m_bCompatibilityMode) {
            this.add(component);
        } else {
            ((JPopupMenu)this.m_popup).insert(component, n);
        }
    }

    protected void addSeparator() {
        this.addSeparator(new JSeparator());
    }

    protected void addSeparator(JSeparator jSeparator) {
        this.add(jSeparator);
        ++this.m_constraints.gridy;
    }

    protected void pack() {
        if (this.m_bCompatibilityMode) {
            ((JWindow)this.m_popup).pack();
        } else {
            ((JPopupMenu)this.m_popup).pack();
        }
    }

    protected void add(Component component) {
        if (this.m_bCompatibilityMode) {
            if (component == null) {
                return;
            }
            ((JWindow)this.m_popup).getContentPane().add(component, this.m_constraints);
            ++this.m_constraints.gridy;
            this.m_registeredComponents.addElement(component);
        } else {
            ((JPopupMenu)this.m_popup).add(component);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void addPopupMenuListener(PopupMenuListener popupMenuListener) {
        Vector vector = this.m_popupListeners;
        synchronized (vector) {
            if (this.m_bCompatibilityMode) {
                if (popupMenuListener != null && !this.m_popupListeners.contains(popupMenuListener)) {
                    this.m_popupListeners.addElement(popupMenuListener);
                }
            } else {
                ((JPopupMenu)this.m_popup).addPopupMenuListener(popupMenuListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean removePopupMenuListener(PopupMenuListener popupMenuListener) {
        Vector vector = this.m_popupListeners;
        synchronized (vector) {
            if (this.m_bCompatibilityMode) {
                if (popupMenuListener != null) {
                    return this.m_popupListeners.removeElement(popupMenuListener);
                }
            } else {
                ((JPopupMenu)this.m_popup).removePopupMenuListener(popupMenuListener);
            }
        }
        return false;
    }

    public Component getParent() {
        return this.m_popup;
    }

    public final Point getRelativePosition(Point point) {
        return GUIUtils.getRelativePosition(point, this.m_popup);
    }

    public final Point getMousePosition() {
        return GUIUtils.getMousePosition(this.m_popup);
    }

    public final void registerExitHandler(ExitHandler exitHandler) {
        this.m_exitHandler = exitHandler != null ? exitHandler : new ExitHandler(){

            public void exited() {
            }
        };
    }

    public final synchronized void show(Component component, Point point) {
        this.show(component, null, point);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized void show(Component component, Window window, Point point) {
        Point point2 = this.calculateLocationOnScreen(component, point);
        Window window2 = GUIUtils.getParentWindow(component);
        this.m_popup.setLocation(point2);
        this.pack();
        this.m_parent = null;
        this.m_bParentOnTop = false;
        if (GUIUtils.isAlwaysOnTop(window)) {
            this.m_bParentOnTop = true;
            this.m_parent = window;
        } else if (GUIUtils.isAlwaysOnTop(window2)) {
            this.m_bParentOnTop = true;
            this.m_parent = window2;
        }
        if (!(this.m_bCompatibilityMode || JavaVersionDBEntry.CURRENT_JAVA_VENDOR.toLowerCase().indexOf("sun") < 0 && JavaVersionDBEntry.CURRENT_JAVA_VENDOR.toLowerCase().indexOf("apple") < 0)) {
            ((JPopupMenu)this.m_popup).setInvoker(window2);
        }
        if (this.m_bCompatibilityMode) {
            Vector vector = this.m_popupListeners;
            synchronized (vector) {
                for (int i = 0; i < this.m_popupListeners.size(); ++i) {
                    ((PopupMenuListener)this.m_popupListeners.elementAt(i)).popupMenuWillBecomeVisible(new PopupMenuEvent(this.m_popup));
                }
            }
        }
        this.setVisible(true);
        if (window2 != null && this.m_bParentOnTop) {
            GUIUtils.setAlwaysOnTop(this.m_popup, true);
        }
    }

    public void repaint() {
        this.m_popup.repaint();
    }

    public final void setLocation(Point point) {
        this.m_popup.setLocation(point);
    }

    public final Point calculateLocationOnScreen(Component component, Point point) {
        int n = point.x;
        int n2 = point.y;
        GUIUtils.Screen screen = GUIUtils.getCurrentScreen(component);
        Dimension dimension = this.m_popup.getPreferredSize();
        if (n + dimension.width > screen.getX() + screen.getWidth()) {
            n = screen.getX() + screen.getWidth() - dimension.width;
        }
        if (n2 + dimension.height > screen.getY() + screen.getHeight()) {
            n2 = screen.getY() + screen.getHeight() - dimension.height;
        }
        n = Math.max(n, screen.getX());
        n2 = Math.max(n2, screen.getY());
        return new Point(n, n2);
    }

    public final int getWidth() {
        return this.m_popup.getPreferredSize().width;
    }

    public final int getHeight() {
        return this.m_popup.getPreferredSize().height;
    }

    public final boolean isVisible() {
        return this.m_popup.isVisible();
    }

    public final synchronized void dispose() {
        this.setVisible(false);
        if (this.m_bCompatibilityMode) {
            ((JWindow)this.m_popup).dispose();
        }
    }

    private final synchronized void resetParentOnTopAttribute() {
        if (GUIUtils.isAlwaysOnTop(this.m_popup)) {
            GUIUtils.setAlwaysOnTop(this.m_popup, false);
            Window window = this.m_parent;
            if (window != null && this.m_bParentOnTop) {
                GUIUtils.setAlwaysOnTop(window, false);
                window.setVisible(true);
                GUIUtils.setAlwaysOnTop(window, true);
            }
            this.m_parent = null;
            this.m_bParentOnTop = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized void setVisible(boolean bl) {
        if (!bl) {
            this.resetParentOnTopAttribute();
        }
        if (bl && this.m_bCompatibilityMode) {
            Vector vector = this.m_popupListeners;
            synchronized (vector) {
                for (int i = 0; i < this.m_popupListeners.size(); ++i) {
                    ((PopupMenuListener)this.m_popupListeners.elementAt(i)).popupMenuWillBecomeVisible(new PopupMenuEvent(this.m_popup));
                }
            }
        }
        this.m_popup.setVisible(bl);
    }

    public static interface ExitHandler {
        public void exited();
    }
}

