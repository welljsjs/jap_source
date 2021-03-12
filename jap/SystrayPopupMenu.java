/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.TrustModel;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.infoservice.StatusInfo;
import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPHelpContext;
import gui.PopupMenu;
import gui.help.JAPHelp;
import jap.CascadePopupMenu;
import jap.JAPController;
import jap.SoftwareUpdater;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class SystrayPopupMenu
extends PopupMenu {
    private static final String MSG_EXIT = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_exit";
    private static final String MSG_SHOW_MAIN_WINDOW = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_showMainWindow";
    private static final String MSG_SETTINGS = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_settings";
    private static final String MSG_ANONYMITY_MODE = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_anonymityMode";
    private static final String MSG_CURRENT_SERVICE = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_currentService";
    private static final String MSG_CONNECTED = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_connected";
    private static final String MSG_NOT_CONNECTED = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_notConnected";
    private static final String MSG_USER_NUMBER = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_userNumber";
    private static final String MSG_SHOW_DETAILS = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_showDetails";
    private static final String MSG_OPEN_BROWSER = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_openBrowser";
    public static final String MSG_ANONYMITY = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_anonymity";
    public static final String MSG_ANONYMITY_ASCII = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_anonymityOnlyAsciiCharacters";
    private static final String MSG_UPDATE = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + ".update";
    private static final String MSG_AVAILABLE_SERVICES = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = SystrayPopupMenu.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + ".availableServices";
    private final Object SYNC_POPUP = new Object();
    private MainWindowListener m_mainWindowListener;
    static /* synthetic */ Class class$jap$SystrayPopupMenu;
    static /* synthetic */ Class class$jap$SystrayPopupMenu$MainWindowListener;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;

    public SystrayPopupMenu(MainWindowListener mainWindowListener) {
        JMenuItem jMenuItem;
        Object object;
        if (mainWindowListener == null) {
            throw new IllegalArgumentException((class$jap$SystrayPopupMenu$MainWindowListener == null ? (class$jap$SystrayPopupMenu$MainWindowListener = SystrayPopupMenu.class$("jap.SystrayPopupMenu$MainWindowListener")) : class$jap$SystrayPopupMenu$MainWindowListener).getName() + " is null!");
        }
        this.m_mainWindowListener = mainWindowListener;
        MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
        TrustModel trustModel = TrustModel.getCurrentTrustModel();
        String string = "";
        if (JAPController.getInstance().isAnonConnected()) {
            object = (StatusInfo)Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = SystrayPopupMenu.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).getEntryById(mixCascade.getId());
            string = JAPMessages.getString(MSG_ANONYMITY) + ": " + mixCascade.getDistribution() + "," + (object == null || ((StatusInfo)object).getAnonLevel() < 0 ? "?" : "" + ((StatusInfo)object).getAnonLevel()) + " / " + 6 + "," + 6;
        } else {
            string = JAPMessages.getString(MSG_NOT_CONNECTED);
        }
        ImageIcon imageIcon = mixCascade.isPayment() ? GUIUtils.loadImageIcon("serverwithpayment.gif") : (mixCascade.isUserDefined() ? GUIUtils.loadImageIcon("servermanuell.gif") : GUIUtils.loadImageIcon("serverfrominternet.gif"));
        JLabel jLabel = new JLabel(GUIUtils.trim(mixCascade.getName()));
        GUIUtils.setFontStyle(jLabel, 1);
        jLabel.setIcon(imageIcon);
        object = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 17;
        ((Container)object).add((Component)jLabel, gridBagConstraints);
        this.add((Component)object);
        object = new JPanel(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(0, imageIcon.getIconWidth() + jLabel.getIconTextGap(), 0, 5);
        jLabel = new JLabel("(" + string + ")");
        ((Container)object).add((Component)jLabel, gridBagConstraints);
        this.add((Component)object);
        final TrustModel trustModel2 = TrustModel.getCurrentTrustModel();
        JMenu jMenu = new JMenu(JAPMessages.getString(MSG_AVAILABLE_SERVICES));
        GUIUtils.setFontStyle(jMenu, 0);
        final CascadePopupMenu cascadePopupMenu = new CascadePopupMenu(jMenu.getPopupMenu());
        this.add(jMenu);
        final JMenu jMenu2 = jMenu;
        this.registerExitHandler(new PopupMenu.ExitHandler(){
            boolean bExitThreadRunning = false;

            public void exited() {
                new Thread(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void run() {
                        Object object = SystrayPopupMenu.this.SYNC_POPUP;
                        synchronized (object) {
                            if (bExitThreadRunning) {
                                return;
                            }
                            bExitThreadRunning = true;
                            do {
                                try {
                                    SystrayPopupMenu.this.SYNC_POPUP.wait(300L);
                                }
                                catch (InterruptedException interruptedException) {
                                    // empty catch block
                                }
                            } while (cascadePopupMenu.getMousePosition() != null || GUIUtils.getMousePosition(SystrayPopupMenu.this.getParent()) != null);
                            bExitThreadRunning = false;
                            SwingUtilities.invokeLater(new Runnable(){

                                public void run() {
                                    SystrayPopupMenu.this.dispose();
                                }
                            });
                        }
                    }
                }.start();
            }
        });
        jMenu2.addMouseListener(new MouseAdapter(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseEntered(MouseEvent mouseEvent) {
                jMenu2.setSelected(true);
                Object object = SystrayPopupMenu.this.SYNC_POPUP;
                synchronized (object) {
                    if (!cascadePopupMenu.isVisible()) {
                        Point point = SystrayPopupMenu.this.getParent().getLocationOnScreen();
                        int n = point.x + SystrayPopupMenu.this.getParent().getWidth() - 1;
                        int n2 = point.y + jMenu2.getY();
                        if (cascadePopupMenu.update(TrustModel.getCurrentTrustModel())) {
                            Point point2 = cascadePopupMenu.calculateLocationOnScreen(SystrayPopupMenu.this.getParent(), new Point(n, n2 -= cascadePopupMenu.getHeaderHeight()));
                            if (point2.x < n) {
                                n = point.x - cascadePopupMenu.getWidth() + 1;
                                point2 = cascadePopupMenu.calculateLocationOnScreen(SystrayPopupMenu.this.getParent(), new Point(n, n2));
                            }
                            cascadePopupMenu.setLocation(point2);
                            cascadePopupMenu.setVisible(true);
                        }
                    }
                    SystrayPopupMenu.this.SYNC_POPUP.notifyAll();
                }
            }

            public void mouseExited(MouseEvent mouseEvent) {
                new Thread(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void run() {
                        Object object = SystrayPopupMenu.this.SYNC_POPUP;
                        synchronized (object) {
                            try {
                                jMenu2.setSelected(false);
                                SystrayPopupMenu.this.SYNC_POPUP.wait(300L);
                                if (cascadePopupMenu.getMousePosition() == null && GUIUtils.getMousePosition(jMenu2) == null) {
                                    SwingUtilities.invokeLater(new Runnable(){

                                        public void run() {
                                            cascadePopupMenu.setVisible(false);
                                        }
                                    });
                                }
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            SystrayPopupMenu.this.SYNC_POPUP.notifyAll();
                        }
                    }
                }.start();
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                if (TrustModel.getCurrentTrustModel() == null || !TrustModel.getCurrentTrustModel().equals(trustModel2)) {
                    JAPController.getInstance().switchTrustFilter(trustModel2);
                    SystrayPopupMenu.this.setVisible(false);
                } else if (!JAPController.getInstance().getAnonMode() || !TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                    JAPController.getInstance().switchToNextMixCascade();
                    SystrayPopupMenu.this.setVisible(false);
                }
            }
        });
        jMenu.addMenuListener(new MenuListener(){

            public void menuSelected(MenuEvent menuEvent) {
                cascadePopupMenu.update(trustModel2);
            }

            public void menuDeselected(MenuEvent menuEvent) {
            }

            public void menuCanceled(MenuEvent menuEvent) {
            }
        });
        MouseAdapter mouseAdapter = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                ((JMenuItem)mouseEvent.getSource()).setArmed(true);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ((JMenuItem)mouseEvent.getSource()).setArmed(true);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ((JMenuItem)mouseEvent.getSource()).setArmed(false);
            }
        };
        final JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(JAPMessages.getString(MSG_ANONYMITY_MODE));
        jCheckBoxMenuItem.addMouseListener(mouseAdapter);
        GUIUtils.setFontStyle(jCheckBoxMenuItem, 0);
        jCheckBoxMenuItem.setSelected(JAPController.getInstance().getAnonMode());
        jCheckBoxMenuItem.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                if (jCheckBoxMenuItem.isSelected()) {
                    JAPController.getInstance().start();
                } else {
                    JAPController.getInstance().stop();
                }
            }
        });
        this.add(jCheckBoxMenuItem);
        mouseAdapter = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                SystrayPopupMenu.this.dispose();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ((JMenuItem)mouseEvent.getSource()).setArmed(true);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ((JMenuItem)mouseEvent.getSource()).setArmed(false);
            }
        };
        this.addSeparator();
        if (JAPController.getInstance().isPortableMode()) {
            jMenuItem = new JMenuItem(JAPMessages.getString(MSG_OPEN_BROWSER));
            jMenuItem.addMouseListener(mouseAdapter);
            GUIUtils.setFontStyle(jMenuItem, 0);
            jMenuItem.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent actionEvent) {
                    SystrayPopupMenu.this.dispose();
                    AbstractOS.getInstance().openURL(null);
                }
            });
            this.add(jMenuItem);
        }
        jMenuItem = new JMenuItem(JAPMessages.getString(MSG_SETTINGS));
        jMenuItem.addMouseListener(mouseAdapter);
        GUIUtils.setFontStyle(jMenuItem, 0);
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                SystrayPopupMenu.this.dispose();
                SystrayPopupMenu.this.m_mainWindowListener.onShowSettings("ANON_TAB", JAPController.getInstance().getCurrentMixCascade());
            }
        });
        this.add(jMenuItem);
        jMenuItem = new JMenuItem(JAPMessages.getString(JAPHelp.MSG_HELP_MENU_ITEM));
        jMenuItem.addMouseListener(mouseAdapter);
        GUIUtils.setFontStyle(jMenuItem, 0);
        this.add(jMenuItem);
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                SystrayPopupMenu.this.dispose();
                SystrayPopupMenu.this.m_mainWindowListener.onShowHelp();
                JAPHelp.getInstance().setContext(JAPHelpContext.createHelpContext("index", JAPController.getInstance().getViewWindow() instanceof JFrame ? (JFrame)JAPController.getInstance().getViewWindow() : null));
                JAPHelp.getInstance().loadCurrentContext();
            }
        });
        jMenuItem = new JMenuItem(JAPMessages.getString(MSG_UPDATE));
        jMenuItem.addMouseListener(mouseAdapter);
        GUIUtils.setFontStyle(jMenuItem, 0);
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                SystrayPopupMenu.this.dispose();
                SoftwareUpdater.show(null, null);
            }
        });
        this.add(jMenuItem);
        this.addSeparator();
        jMenuItem = new JMenuItem(JAPMessages.getString(MSG_EXIT));
        jMenuItem.addMouseListener(mouseAdapter);
        GUIUtils.setFontStyle(jMenuItem, 0);
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                SystrayPopupMenu.this.dispose();
                JAPController.goodBye(true);
            }
        });
        this.addSeparator();
        this.add(jMenuItem);
        this.pack();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public static interface MainWindowListener {
        public void onShowMainWindow();

        public void onShowSettings(String var1, Object var2);

        public void onShowHelp();
    }
}

