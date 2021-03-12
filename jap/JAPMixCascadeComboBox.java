/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.AbstractAutoSwitchedMixCascadeContainer;
import anon.client.TrustModel;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.PopupMenu;
import jap.CascadePopupMenu;
import jap.JAPConfAnon;
import jap.JAPController;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class JAPMixCascadeComboBox
extends JComboBox {
    static final String ITEM_AVAILABLE_SERVERS = "ITEM_AVAILABLE_SERVERS";
    static final String ITEM_NO_SERVERS_AVAILABLE = "ITEM_NO_SERVERS_AVAILABLE";
    private MixCascade m_currentCascade;
    private JPopupMenu m_comboPopup;
    private JAPMixCascadeComboBoxListCellRender m_renderer;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    public JAPMixCascadeComboBox() {
        this.setModel(new JAPMixCascadeComboBoxModel());
        this.m_renderer = new JAPMixCascadeComboBoxListCellRender();
        this.setRenderer(this.m_renderer);
        this.setEditable(false);
    }

    public void addItem(Object object) {
    }

    public MixCascade getMixCascade() {
        return this.m_currentCascade;
    }

    public void showStaticPopup() {
        if (this.m_comboPopup != null) {
            this.m_comboPopup.setVisible(true);
        } else {
            super.showPopup();
        }
    }

    public boolean isPopupVisible() {
        if (this.m_comboPopup != null) {
            return this.m_comboPopup.isVisible();
        }
        return super.isPopupVisible();
    }

    public void closeCascadePopupMenu() {
        this.m_renderer.closeCascadePopupMenu();
        if (this.m_comboPopup != null) {
            this.m_comboPopup.setVisible(false);
        } else {
            super.hidePopup();
        }
    }

    public synchronized void setMixCascade(MixCascade mixCascade) {
        this.m_currentCascade = mixCascade;
        this.removeAllItems();
    }

    public void validate() {
        if (!JAPController.getInstance().getCurrentMixCascade().equals(this.m_currentCascade)) {
            this.removeAllItems();
        }
        super.validate();
    }

    public synchronized void removeAllItems() {
        MixCascade mixCascade;
        this.setModel(new JAPMixCascadeComboBoxModel());
        Vector vector = TrustModel.getTrustModels();
        if (this.m_currentCascade != null) {
            super.addItem(this.m_currentCascade);
        }
        TrustModel trustModel = TrustModel.getCurrentTrustModel();
        Hashtable hashtable = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPMixCascadeComboBox.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryHash();
        Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPMixCascadeComboBox.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
        if (trustModel.hasFreeCascades()) {
            while (enumeration.hasMoreElements()) {
                mixCascade = (MixCascade)enumeration.nextElement();
                if (!hashtable.containsKey(mixCascade.getId()) || mixCascade.isPayment() || !trustModel.isTrusted(mixCascade) || this.m_currentCascade != null && this.m_currentCascade.getId().equals(mixCascade.getId())) continue;
                hashtable.remove(mixCascade.getId());
                super.addItem(mixCascade);
            }
        }
        enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = JAPMixCascadeComboBox.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
        while (enumeration.hasMoreElements()) {
            mixCascade = (MixCascade)enumeration.nextElement();
            if (!hashtable.containsKey(mixCascade.getId()) || !trustModel.isTrusted(mixCascade) || this.m_currentCascade != null && this.m_currentCascade.getId().equals(mixCascade.getId())) continue;
            hashtable.remove(mixCascade.getId());
            super.addItem(mixCascade);
        }
    }

    public void setNoDataAvailable() {
        super.insertItemAt(ITEM_NO_SERVERS_AVAILABLE, 0);
    }

    public Dimension getPreferredSize() {
        Dimension dimension = super.getPreferredSize();
        if (dimension.width > 50) {
            dimension.width = 50;
        }
        return dimension;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    final class JAPMixCascadeComboBoxListCellRender
    implements ListCellRenderer {
        private final Color m_newCascadeColor = new Color(255, 255, 170);
        private JLabel m_componentNoServer;
        private JLabel m_componentAvailableServer;
        private JLabel m_componentUserServer;
        private JLabel m_componentAvailableCascade;
        private JLabel[] m_flags;
        private JLabel[] m_names;
        private JPanel m_componentPanel;
        private GridBagConstraints m_componentConstraints;
        private Object SYNC_POPUP = new Object();
        private JLabel m_lblCascadePopupMenu;
        private JLabel m_lblMenuArrow;
        private JPanel m_cascadePopupMenu;
        private CascadePopupMenu m_currentCascadePopup;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public JAPMixCascadeComboBoxListCellRender() {
            this.m_componentPanel = new JPanel(new GridBagLayout());
            this.m_componentPanel.setOpaque(true);
            this.m_componentConstraints = new GridBagConstraints();
            this.m_componentConstraints.anchor = 17;
            this.m_componentConstraints.gridy = 0;
            this.m_componentConstraints.insets = new Insets(0, 0, 0, 0);
            this.m_componentConstraints.fill = 2;
            this.m_componentNoServer = new JLabel(JAPMessages.getString("ngMixComboNoServers"));
            this.m_componentNoServer.setIcon(GUIUtils.loadImageIcon("error.gif", true));
            this.m_componentNoServer.setBorder(new EmptyBorder(0, 3, 0, 3));
            this.m_componentNoServer.setForeground(Color.red);
            this.m_componentAvailableServer = new JLabel(JAPMessages.getString("ngMixComboAvailableServers"));
            this.m_componentAvailableServer.setOpaque(true);
            this.m_componentAvailableServer.setHorizontalAlignment(2);
            this.m_componentAvailableServer.setBorder(new EmptyBorder(1, 3, 1, 3));
            this.m_componentUserServer = new JLabel(JAPMessages.getString("ngMixComboUserServers"));
            this.m_componentUserServer.setBorder(new EmptyBorder(1, 3, 1, 3));
            this.m_componentUserServer.setHorizontalAlignment(2);
            this.m_componentUserServer.setOpaque(true);
            this.m_componentAvailableCascade = new JLabel();
            this.m_componentAvailableCascade.setHorizontalAlignment(2);
            this.m_componentAvailableCascade.setOpaque(true);
            this.m_componentAvailableCascade.setBorder(new EmptyBorder(1, 3, 1, 3));
            this.m_flags = new JLabel[3];
            this.m_names = new JLabel[3];
            for (int i = 0; i < this.m_flags.length; ++i) {
                this.m_flags[i] = new JLabel();
                this.m_flags[i].setHorizontalAlignment(2);
                this.m_flags[i].setOpaque(false);
                this.m_flags[i].setBorder(new EmptyBorder(0, 1, 0, 2));
                this.m_names[i] = new JLabel();
                this.m_names[i].setHorizontalAlignment(2);
                this.m_names[i].setOpaque(false);
            }
            this.m_lblCascadePopupMenu = new JLabel();
            this.m_lblCascadePopupMenu.setOpaque(true);
            this.m_cascadePopupMenu = new JPanel(new GridBagLayout());
            this.m_cascadePopupMenu.setBorder(new EmptyBorder(1, 3, 1, 1));
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            this.m_cascadePopupMenu.add((Component)this.m_lblCascadePopupMenu, gridBagConstraints);
            ++gridBagConstraints.gridx;
            gridBagConstraints.anchor = 13;
            gridBagConstraints.weightx = 1.0;
            this.m_lblMenuArrow = new JLabel(GUIUtils.loadImageIcon("arrow46.gif", true));
            this.m_lblMenuArrow.setOpaque(true);
            this.m_cascadePopupMenu.add((Component)this.m_lblMenuArrow, gridBagConstraints);
            this.m_cascadePopupMenu.setOpaque(true);
            Object object = this.SYNC_POPUP;
            synchronized (object) {
                this.m_currentCascadePopup = new CascadePopupMenu(true);
                this.m_currentCascadePopup.registerExitHandler(new PopupMenu.ExitHandler(){

                    public void exited() {
                        JAPMixCascadeComboBoxListCellRender.this.m_currentCascadePopup.setVisible(false);
                        if (JAPMixCascadeComboBox.this.m_comboPopup == null || !JAPMixCascadeComboBox.this.m_comboPopup.isVisible()) {
                            JAPMixCascadeComboBox.this.showPopup();
                        }
                    }
                });
            }
            GUIUtils.addAWTEventListener(new GUIUtils.AWTEventListener(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void eventDispatched(AWTEvent aWTEvent) {
                    if (aWTEvent instanceof MouseEvent) {
                        MouseEvent mouseEvent = (MouseEvent)aWTEvent;
                        if (aWTEvent.getSource() instanceof Component) {
                            Component component = (Component)aWTEvent.getSource();
                            Point point = null;
                            try {
                                point = component.getLocationOnScreen();
                                point.x += mouseEvent.getX();
                                point.y += mouseEvent.getY();
                            }
                            catch (IllegalComponentStateException illegalComponentStateException) {
                                // empty catch block
                            }
                            Object object = JAPMixCascadeComboBoxListCellRender.this.SYNC_POPUP;
                            synchronized (object) {
                                if (JAPMixCascadeComboBoxListCellRender.this.m_currentCascadePopup.getRelativePosition(point) == null && GUIUtils.getRelativePosition(point, JAPMixCascadeComboBox.this.m_comboPopup) == null && JAPMixCascadeComboBoxListCellRender.this.m_currentCascadePopup.isVisible() && (JAPMixCascadeComboBox.this.m_comboPopup == null || !JAPMixCascadeComboBox.this.m_comboPopup.isVisible())) {
                                    JAPMixCascadeComboBox.this.showStaticPopup();
                                }
                            }
                        }
                    }
                }
            });
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void closeCascadePopupMenu() {
            Object object = this.SYNC_POPUP;
            synchronized (object) {
                if (this.m_currentCascadePopup != null) {
                    this.m_currentCascadePopup.setVisible(false);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
            int n2;
            Color color;
            Color color2;
            Object object2;
            if (JAPMixCascadeComboBox.this.m_comboPopup == null) {
                GUIUtils.getMousePosition();
                object2 = this.m_cascadePopupMenu.getParent();
                while (object2 != null) {
                    if (!((object2 = ((Component)object2).getParent()) instanceof JPopupMenu)) continue;
                    JAPMixCascadeComboBox.this.m_comboPopup = (JPopupMenu)object2;
                    break;
                }
            }
            if (object == null) {
                return new JLabel();
            }
            object2 = this.SYNC_POPUP;
            synchronized (object2) {
                if (bl && this.m_currentCascadePopup.isVisible() && this.m_currentCascadePopup.getTrustModel() != null && !this.m_currentCascadePopup.getTrustModel().equals(object) && this.m_currentCascadePopup.getMousePosition() == null) {
                    this.m_currentCascadePopup.setVisible(false);
                }
            }
            if (object instanceof TrustModel) {
                if (bl) {
                    object2 = this.SYNC_POPUP;
                    synchronized (object2) {
                        if (!this.m_currentCascadePopup.isVisible()) {
                            Point point = jList.getLocationOnScreen();
                            int n3 = point.x + jList.getWidth();
                            int n4 = point.y + jList.indexToLocation((int)n).y;
                            if (this.m_currentCascadePopup.update((TrustModel)object)) {
                                Point point2 = this.m_currentCascadePopup.calculateLocationOnScreen(jList, new Point(n3, n4 -= this.m_currentCascadePopup.getHeaderHeight()));
                                if (point2.x < n3) {
                                    n3 = point.x - this.m_currentCascadePopup.getWidth();
                                    point2 = this.m_currentCascadePopup.calculateLocationOnScreen(jList, new Point(n3, n4));
                                }
                                this.m_currentCascadePopup.setLocation(point2);
                                this.m_currentCascadePopup.setVisible(true);
                            }
                        }
                    }
                    this.m_cascadePopupMenu.setBackground(jList.getSelectionBackground());
                    this.m_cascadePopupMenu.setForeground(jList.getSelectionForeground());
                } else {
                    this.m_cascadePopupMenu.setBackground(jList.getBackground());
                    this.m_cascadePopupMenu.setForeground(jList.getForeground());
                }
                this.m_lblMenuArrow.setBackground(this.m_cascadePopupMenu.getBackground());
                this.m_lblMenuArrow.setForeground(this.m_cascadePopupMenu.getForeground());
                this.m_lblCascadePopupMenu.setBackground(this.m_cascadePopupMenu.getBackground());
                this.m_lblCascadePopupMenu.setForeground(this.m_cascadePopupMenu.getForeground());
                object2 = "";
                if (((TrustModel)object).equals(TrustModel.getCurrentTrustModel())) {
                    this.m_lblCascadePopupMenu.setFont(new Font(this.m_lblCascadePopupMenu.getFont().getName(), 1, this.m_lblCascadePopupMenu.getFont().getSize()));
                } else {
                    this.m_lblCascadePopupMenu.setFont(new Font(this.m_lblCascadePopupMenu.getFont().getName(), 0, this.m_lblCascadePopupMenu.getFont().getSize()));
                }
                this.m_lblCascadePopupMenu.setText(JAPMessages.getString(JAPConfAnon.MSG_FILTER) + ": " + ((TrustModel)object).getName() + (String)object2);
                return this.m_cascadePopupMenu;
            }
            if (object.equals(JAPMixCascadeComboBox.ITEM_NO_SERVERS_AVAILABLE)) {
                return this.m_componentNoServer;
            }
            if (object.equals(JAPMixCascadeComboBox.ITEM_AVAILABLE_SERVERS)) {
                return this.m_componentAvailableServer;
            }
            object2 = (MixCascade)object;
            ImageIcon imageIcon = ((MixCascade)object2).isUserDefined() ? (TrustModel.getCurrentTrustModel().isTrusted((MixCascade)object2) ? GUIUtils.loadImageIcon("servermanuell.gif", true) : GUIUtils.loadImageIcon("cdisabled.gif", true)) : (((MixCascade)object2).isPayment() ? (TrustModel.getCurrentTrustModel().isTrusted((MixCascade)object2) ? GUIUtils.loadImageIcon("serverwithpayment.gif", true) : GUIUtils.loadImageIcon("cdisabled.gif", true)) : (TrustModel.getCurrentTrustModel().isTrusted((MixCascade)object2) ? GUIUtils.loadImageIcon("serverfrominternet.gif", true) : GUIUtils.loadImageIcon("cdisabled.gif", true)));
            if (((MixCascade)object2).isSocks5Supported()) {
                imageIcon = GUIUtils.combine(imageIcon, GUIUtils.loadImageIcon("socks_icon.gif", true));
            }
            JLabel jLabel = this.m_componentAvailableCascade;
            jLabel.setIcon(imageIcon);
            if (bl) {
                color2 = jList.getSelectionBackground();
                color = jList.getSelectionForeground();
            } else {
                color2 = jList.getBackground();
                color = jList.getForeground();
            }
            this.setSelectionColors(color2, color);
            Vector vector = ((MixCascade)object2).getDecomposedCascadeName();
            this.m_componentConstraints.gridx = 0;
            this.m_componentConstraints.weightx = 0.0;
            this.m_componentPanel.add((Component)jLabel, this.m_componentConstraints);
            while (vector.size() > 1 && vector.size() > ((MixCascade)object2).getNumberOfOperatorsShown()) {
                vector.removeElementAt(vector.size() - 1);
            }
            String string = "";
            boolean bl3 = false;
            if (JAPMixCascadeComboBox.this.m_currentCascade != null && JAPMixCascadeComboBox.this.m_currentCascade.getId().equals(((MixCascade)object2).getId()) && JAPMixCascadeComboBox.this.m_currentCascade != AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) {
                bl3 = true;
            }
            for (int i = 0; i < this.m_flags.length; ++i) {
                this.m_flags[i].setIcon(null);
                this.m_flags[i].setText("");
                this.m_names[i].setText("");
                n2 = 0;
                if (bl3) {
                    n2 = 1;
                }
                this.m_flags[i].setFont(new Font(this.m_lblCascadePopupMenu.getFont().getName(), n2, this.m_lblCascadePopupMenu.getFont().getSize()));
                this.m_names[i].setFont(this.m_flags[i].getFont());
            }
            Insets insets = new Insets(0, 2, 0, 0);
            for (n2 = 0; n2 < vector.size() && n2 < this.m_flags.length; ++n2) {
                Object object3;
                ++this.m_componentConstraints.gridx;
                this.m_names[n2].setText((String)vector.elementAt(n2));
                this.m_componentPanel.add((Component)this.m_names[n2], this.m_componentConstraints);
                if (n2 + 1 == ((MixCascade)object2).getNumberOfOperatorsShown() || n2 + 1 == vector.size()) {
                    this.m_componentConstraints.weightx = 1.0;
                    this.m_flags[n2].setText(string);
                } else {
                    this.m_flags[n2].setText("-");
                }
                ++this.m_componentConstraints.gridx;
                MixInfo mixInfo = vector.size() == 1 ? ((MixCascade)object2).getMixInfo(((MixCascade)object2).getNumberOfMixes() - 1) : ((MixCascade)object2).getMixInfo(n2);
                if (mixInfo != null && mixInfo.getCertPath() != null && mixInfo.getCertPath().getSubject() != null) {
                    object3 = mixInfo.getCertPath().getSubject().getCountryCode();
                    this.m_flags[n2].setIcon(GUIUtils.loadImageIcon("flags/" + (String)object3 + ".png"));
                } else {
                    this.m_flags[n2].setIcon(null);
                }
                object3 = this.m_componentConstraints.insets;
                this.m_componentConstraints.insets = insets;
                this.m_componentPanel.add((Component)this.m_flags[n2], this.m_componentConstraints);
                this.m_componentConstraints.insets = object3;
            }
            return this.m_componentPanel;
        }

        private void setSelectionColors(Color color, Color color2) {
            this.m_componentAvailableCascade.setBackground(color);
            this.m_componentAvailableCascade.setForeground(color2);
            for (int i = 0; i < this.m_flags.length; ++i) {
                this.m_names[i].setBackground(color);
                this.m_names[i].setForeground(color2);
                this.m_flags[i].setBackground(color);
                this.m_flags[i].setForeground(color2);
            }
            this.m_componentPanel.setBackground(color);
            this.m_componentPanel.setForeground(color2);
        }
    }

    final class JAPMixCascadeComboBoxModel
    extends DefaultComboBoxModel {
        JAPMixCascadeComboBoxModel() {
        }

        public void setSelectedItem(Object object) {
            if (object instanceof TrustModel) {
                boolean bl = false;
                boolean bl2 = false;
                if (TrustModel.getCurrentTrustModel() == null || !TrustModel.getCurrentTrustModel().equals(object)) {
                    bl2 = true;
                    bl = true;
                } else if (!JAPController.getInstance().getAnonMode() || !TrustModel.getCurrentTrustModel().isTrusted(JAPController.getInstance().getCurrentMixCascade())) {
                    bl2 = true;
                    bl = false;
                } else if (TrustModel.getCurrentTrustModel().equals(object)) {
                    JAPMixCascadeComboBox.this.closeCascadePopupMenu();
                    return;
                }
                if (bl2) {
                    MixCascade mixCascade = null;
                    for (int i = 0; i < this.getSize(); ++i) {
                        if (!(this.getElementAt(i) instanceof MixCascade)) continue;
                        mixCascade = (MixCascade)this.getElementAt(i);
                        break;
                    }
                    if (mixCascade != null) {
                        JAPMixCascadeComboBox.this.closeCascadePopupMenu();
                        super.setSelectedItem(mixCascade);
                        if (bl) {
                            JAPController.getInstance().switchTrustFilter((TrustModel)object);
                        } else {
                            JAPController.getInstance().switchToNextMixCascade();
                        }
                    }
                }
                return;
            }
            if (object == null || object.equals(JAPMixCascadeComboBox.ITEM_NO_SERVERS_AVAILABLE) || object.equals(JAPMixCascadeComboBox.ITEM_AVAILABLE_SERVERS)) {
                return;
            }
            if (object instanceof MixCascade) {
                final MixCascade mixCascade = (MixCascade)object;
                final TrustModel trustModel = TrustModel.getCurrentTrustModel();
                if (!JAPController.getInstance().getCurrentMixCascade().getId().equals(mixCascade.getId()) && !trustModel.isTrusted(mixCascade) && JAPController.getInstance().getAnonMode()) {
                    new Thread(){

                        public void run() {
                            JAPConfAnon.showServiceUntrustedBox(mixCascade, JAPMixCascadeComboBox.this, trustModel);
                        }
                    }.start();
                    JAPMixCascadeComboBox.this.closeCascadePopupMenu();
                    return;
                }
            }
            super.setSelectedItem(object);
        }
    }
}

