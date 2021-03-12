/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.JavaVersionDBEntry;
import anon.infoservice.MixCascade;
import anon.infoservice.StatusInfo;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.GUIUtils;
import gui.dialog.JAPDialog;
import jap.AbstractJAPMainView;
import jap.JAPController;
import jap.JAPModel;
import jap.JAPUtil;
import jap.SystrayPopupMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import logging.LogHolder;
import logging.LogType;

public final class JAPViewIconified
extends JWindow
implements ActionListener {
    private static final long serialVersionUID = 1L;
    public static final String XML_LOCATION = "IconifiedLocation";
    public static final String MSG_ANON_LOW = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_anonLow";
    public static final String MSG_ANON_FAIR = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_anonFair";
    public static final String MSG_ANON_HIGH = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_anonHigh";
    public static final String MSG_ANON = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_anon";
    private static final String IMG_16_DISCONNECTED = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_icon16discon.gif";
    private static final String IMG_16_RED = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_icon16red.gif";
    private static final String MSG_TT_SWITCH_ANONYMITY = (class$jap$JAPViewIconified == null ? (class$jap$JAPViewIconified = JAPViewIconified.class$("jap.JAPViewIconified")) : class$jap$JAPViewIconified).getName() + "_ttSwitchAnonymity";
    private static final String STR_HIDDEN_WINDOW = Double.toString(Math.random());
    private static JFrame m_frameParent;
    private JAPController m_Controller;
    private AbstractJAPMainView m_mainView;
    private JLabel m_labelBytes;
    private JLabel m_labelAnon;
    private JToggleButton m_lblJAPIcon;
    private JAPDialog ms_popupWindow;
    private JLabel m_lblBytes;
    private Font m_fontDlg;
    private NumberFormat m_NumberFormat;
    private boolean m_anonModeDisabled = false;
    private Object SYNC_CURSOR = new Object();
    private GUIUtils.WindowDocker m_docker;
    private Runnable m_runnableValueUpdate;
    static /* synthetic */ Class class$jap$JAPViewIconified;

    private static JFrame getParentFrame() {
        if (m_frameParent == null) {
            m_frameParent = new JFrame(STR_HIDDEN_WINDOW);
        }
        return m_frameParent;
    }

    public JAPViewIconified(AbstractJAPMainView abstractJAPMainView) {
        super(JAPViewIconified.getParentFrame());
        this.m_fontDlg = new Font("Sans", 1, 11);
        this.setName(STR_HIDDEN_WINDOW);
        this.m_mainView = abstractJAPMainView;
        if (m_frameParent != null) {
            m_frameParent.setIconImage(this.m_mainView.getIconImage());
        }
        LogHolder.log(6, LogType.MISC, "Initializing...");
        this.m_Controller = JAPController.getInstance();
        this.m_NumberFormat = NumberFormat.getInstance();
        this.m_runnableValueUpdate = new Runnable(){

            public void run() {
                JAPViewIconified.this.updateValues1();
            }
        };
        this.init();
    }

    private void init() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanel = new JPanel(gridBagLayout);
        jPanel.setOpaque(false);
        this.m_lblBytes = new JLabel(JAPMessages.getString("iconifiedviewBytes") + ": ", 4);
        this.m_lblBytes.setFont(this.m_fontDlg);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(3, 3, 0, 0);
        gridBagConstraints.anchor = 18;
        gridBagLayout.setConstraints(this.m_lblBytes, gridBagConstraints);
        jPanel.add(this.m_lblBytes);
        gridBagConstraints.weightx = 1.0;
        this.m_labelBytes = new JLabel("000000,0", 2);
        this.m_labelBytes.setFont(this.m_fontDlg);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagLayout.setConstraints(this.m_labelBytes, gridBagConstraints);
        jPanel.add(this.m_labelBytes);
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_ANON) + ": ", 4);
        jLabel.setFont(this.m_fontDlg);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        jPanel.add((Component)jLabel, gridBagConstraints);
        int n = 0;
        n = Math.max(n, JAPMessages.getString(MSG_ANON_LOW).length());
        n = Math.max(n, JAPMessages.getString(MSG_ANON_FAIR).length());
        n = Math.max(n, JAPMessages.getString(MSG_ANON_HIGH).length());
        char[] arrc = new char[n];
        for (int i = 0; i < arrc.length; ++i) {
            arrc[i] = 65;
        }
        this.m_labelAnon = new JLabel(new String(arrc), 2);
        this.m_labelAnon.setFont(this.m_fontDlg);
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_labelAnon, gridBagConstraints);
        JButton jButton = new JButton(GUIUtils.loadImageIcon("enlarge.gif", true, false));
        jButton.setOpaque(false);
        jButton.addActionListener(this);
        jButton.setActionCommand("enlarge");
        jButton.setToolTipText(JAPMessages.getString("enlargeWindow"));
        JAPUtil.setMnemonic(jButton, JAPMessages.getString("iconifyButtonMn"));
        JPanel jPanel2 = new JPanel(new BorderLayout());
        jPanel2.setBorder(new LineBorder(Color.black, 1));
        jPanel2.add((Component)jPanel, "Center");
        JPanel jPanel3 = new JPanel(new BorderLayout());
        this.m_lblJAPIcon = new JToggleButton(GUIUtils.loadImageIcon("icon16.gif", true, false));
        this.m_lblJAPIcon.setPressedIcon(GUIUtils.loadImageIcon(IMG_16_DISCONNECTED, true, false));
        this.m_lblJAPIcon.setOpaque(false);
        this.m_lblJAPIcon.setToolTipText(JAPMessages.getString(MSG_TT_SWITCH_ANONYMITY));
        this.m_lblJAPIcon.addActionListener(this);
        this.m_lblJAPIcon.setActionCommand("switchanonymity");
        this.m_lblJAPIcon.setToolTipText(JAPMessages.getString(MSG_TT_SWITCH_ANONYMITY));
        this.m_lblJAPIcon.addMouseListener(new MouseAdapter(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseEntered(MouseEvent mouseEvent) {
                Object object = JAPViewIconified.this.SYNC_CURSOR;
                synchronized (object) {
                    if (!JAPViewIconified.this.m_anonModeDisabled) {
                        JAPViewIconified.this.setCursor(Cursor.getPredefinedCursor(12));
                        JAPViewIconified.this.getRootPane().setToolTipText(JAPMessages.getString(MSG_TT_SWITCH_ANONYMITY));
                    }
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseExited(MouseEvent mouseEvent) {
                Object object = JAPViewIconified.this.SYNC_CURSOR;
                synchronized (object) {
                    JAPViewIconified.this.setCursor(Cursor.getPredefinedCursor(0));
                    JAPViewIconified.this.getRootPane().setToolTipText(null);
                }
            }
        });
        jPanel3.add((Component)this.m_lblJAPIcon, "South");
        jPanel3.add((Component)jButton, "North");
        jPanel2.add((Component)jPanel3, "East");
        jPanel2.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isPopupTrigger()) {
                    SystrayPopupMenu systrayPopupMenu = new SystrayPopupMenu(new SystrayPopupMenu.MainWindowListener(){

                        public void onShowMainWindow() {
                            JAPViewIconified.this.switchBackToMainView();
                        }

                        public void onShowSettings(String string, Object object) {
                            JAPViewIconified.this.m_mainView.showConfigDialog(string, object);
                        }

                        public void onShowHelp() {
                            JAPViewIconified.this.switchBackToMainView();
                        }
                    });
                    if (JavaVersionDBEntry.CURRENT_JAVA_VENDOR.toLowerCase().indexOf("sun") >= 0 && JavaVersionDBEntry.CURRENT_JAVA_VERSION.compareTo("1.6.0_02") >= 0) {
                        if (JAPViewIconified.this.ms_popupWindow == null) {
                            JAPViewIconified.this.ms_popupWindow = new JAPDialog(JAPViewIconified.this, STR_HIDDEN_WINDOW, false);
                            JAPViewIconified.this.ms_popupWindow.setName(STR_HIDDEN_WINDOW);
                            JAPViewIconified.this.ms_popupWindow.pack();
                            JAPViewIconified.this.ms_popupWindow.setLocation(20000, 20000);
                        }
                        JAPViewIconified.this.ms_popupWindow.setVisible(true);
                        systrayPopupMenu.show(JAPViewIconified.this.ms_popupWindow.getContentPane(), JAPViewIconified.this, new Point(mouseEvent.getX() + JAPViewIconified.this.getLocation().x, mouseEvent.getY() + JAPViewIconified.this.getLocation().y));
                    } else {
                        systrayPopupMenu.show(JAPViewIconified.this, new Point(mouseEvent.getX() + JAPViewIconified.this.getLocation().x, mouseEvent.getY() + JAPViewIconified.this.getLocation().y));
                    }
                } else if (mouseEvent.getClickCount() > 1) {
                    JAPViewIconified.this.switchBackToMainView();
                }
            }
        });
        this.setContentPane(jPanel2);
        this.m_docker = new GUIUtils.WindowDocker(jPanel2);
        this.pack();
        GUIUtils.Screen screen = GUIUtils.getCurrentScreen(this);
        if (this.getSize().width > screen.getWidth() || this.getSize().height > screen.getHeight()) {
            LogHolder.log(3, LogType.GUI, "Packed iconified view with illegal size! Width:" + this.getSize().width + " Height:" + this.getSize().height + "\nSetting defaults...");
            if (JAPModel.getInstance().getIconifiedSize() != null && JAPModel.getInstance().getIconifiedSize().width > 0 && JAPModel.getInstance().getIconifiedSize().height > 0) {
                this.setSize(JAPModel.getInstance().getIconifiedSize());
            } else {
                this.setSize(new Dimension(151, 85));
            }
        } else {
            JAPModel.getInstance().setIconifiedSize(this.getSize());
        }
        GUIUtils.moveToUpRightCorner(this);
        GUIUtils.restoreLocation(this, JAPModel.getInstance().getIconifiedWindowLocation());
        this.m_labelBytes.setText(Util.formatBytesValueWithoutUnit(0L));
        this.m_lblBytes.setText(Util.formatBytesValueOnlyUnit(0L) + ": ");
        this.m_labelAnon.setText(JAPMessages.getString("iconifiedViewNA"));
    }

    public void setVisible(boolean bl) {
        if (bl) {
            GUIUtils.setAlwaysOnTop(this, JAPModel.getInstance().isMiniViewOnTop());
        }
        JAPController.getInstance().switchViewWindow(!bl);
        super.setVisible(bl);
    }

    public void switchBackToMainView() {
        if (this.m_mainView == null || !this.isVisible() && this.m_mainView.isVisible()) {
            return;
        }
        this.m_mainView.setVisible(true);
        this.setVisible(false);
        this.m_mainView.toFront();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if ("enlarge".equals(actionEvent.getActionCommand())) {
            this.switchBackToMainView();
        } else if ("switchanonymity".equals(actionEvent.getActionCommand())) {
            if (this.m_Controller.getAnonMode()) {
                this.m_Controller.stop();
            } else {
                this.m_Controller.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateValues1() {
        Runnable runnable = this.m_runnableValueUpdate;
        synchronized (runnable) {
            block12: {
                try {
                    if (this.m_Controller.isAnonConnected()) {
                        MixCascade mixCascade = this.m_Controller.getCurrentMixCascade();
                        StatusInfo statusInfo = mixCascade.getCurrentStatus();
                        int n = statusInfo.getAnonLevel();
                        this.m_labelAnon.setText(mixCascade.getDistribution() + "," + (n < 0 ? "?" : "" + n) + " / " + 6 + "," + 6);
                        JToggleButton jToggleButton = this.m_lblJAPIcon;
                        synchronized (jToggleButton) {
                            String string = JAPModel.getInstance().getProgramName().equals("JonDo") ? "JonDo.ico.gif" : "icon16.gif";
                            this.m_lblJAPIcon.setIcon(GUIUtils.loadImageIcon(string, true, false));
                            this.m_lblJAPIcon.getModel().setSelected(false);
                            break block12;
                        }
                    }
                    this.m_labelAnon.setText(JAPMessages.getString("iconifiedViewNA"));
                    JToggleButton jToggleButton = this.m_lblJAPIcon;
                    synchronized (jToggleButton) {
                        this.m_lblJAPIcon.setIcon(GUIUtils.loadImageIcon(IMG_16_DISCONNECTED, true, false));
                        this.m_lblJAPIcon.getModel().setSelected(true);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
    }

    public void dispose() {
        this.m_docker.finalize();
        super.dispose();
    }

    public void disableSetAnonMode() {
        this.m_anonModeDisabled = true;
        this.m_lblJAPIcon.getModel().setEnabled(false);
    }

    public void updateValues(boolean bl) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.m_runnableValueUpdate.run();
        } else {
            try {
                if (bl) {
                    SwingUtilities.invokeAndWait(this.m_runnableValueUpdate);
                }
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            SwingUtilities.invokeLater(this.m_runnableValueUpdate);
        }
    }

    public void channelsChanged(int n) {
    }

    public void packetMixed(final long l) {
        Runnable runnable = new Runnable(){

            public void run() {
                JAPViewIconified.this.m_lblBytes.setText(Util.formatBytesValueOnlyUnit(l) + ":");
                JAPViewIconified.this.m_labelBytes.setText(Util.formatBytesValueWithoutUnit(l));
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void blink() {
        if (this.isVisible()) {
            JToggleButton jToggleButton = this.m_lblJAPIcon;
            synchronized (jToggleButton) {
                if (this.m_Controller.isAnonConnected()) {
                    this.m_lblJAPIcon.setIcon(GUIUtils.loadImageIcon(IMG_16_RED, true, false));
                    try {
                        this.m_lblJAPIcon.wait(250L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
                if (this.m_Controller.isAnonConnected()) {
                    String string = JAPModel.getInstance().getProgramName().equals("JonDo") ? "JonDo.ico.gif" : "icon16.gif";
                    this.m_lblJAPIcon.setIcon(GUIUtils.loadImageIcon(string, true, false));
                    this.m_lblJAPIcon.getModel().setSelected(false);
                } else {
                    this.m_lblJAPIcon.setIcon(GUIUtils.loadImageIcon(IMG_16_DISCONNECTED, true, false));
                    this.m_lblJAPIcon.getModel().setSelected(true);
                }
            }
        }
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

