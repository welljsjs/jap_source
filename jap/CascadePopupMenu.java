/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.TrustModel;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.PopupMenu;
import jap.JAPController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import logging.LogHolder;
import logging.LogType;

public class CascadePopupMenu
extends PopupMenu {
    private static final String MSG_EDIT_FILTER = (class$jap$JAPConfAnon == null ? (class$jap$JAPConfAnon = CascadePopupMenu.class$("jap.JAPConfAnon")) : class$jap$JAPConfAnon).getName() + "_editFilter";
    private final Color m_newCascadeColor = new Color(255, 255, 170);
    private final ImageIcon ICON_MANUELL = GUIUtils.loadImageIcon("servermanuell.gif");
    private final ImageIcon ICON_PAYMENT = GUIUtils.loadImageIcon("serverwithpayment.gif");
    private final ImageIcon ICON_INTERNET = GUIUtils.loadImageIcon("serverfrominternet.gif");
    private final ImageIcon ICON_SOCKS = GUIUtils.loadImageIcon("socks_icon.gif");
    private final ImageIcon ICON_SOCKS_MANUELL = GUIUtils.combine(this.ICON_MANUELL, this.ICON_SOCKS);
    private final ImageIcon ICON_SOCKS_PAYMENT = GUIUtils.combine(this.ICON_PAYMENT, this.ICON_SOCKS);
    private final ImageIcon ICON_SOCKS_INTERNET = GUIUtils.combine(this.ICON_INTERNET, this.ICON_SOCKS);
    private Hashtable m_menuItems = new Hashtable();
    private JMenuItem m_editFilter;
    private ActionListener m_cascadeItemListener = new CascadeItemListener();
    private TrustModel m_trustModel;
    private int m_headerHeight = 0;
    static /* synthetic */ Class class$jap$JAPConfAnon;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$NewCascadeIDEntry;

    public CascadePopupMenu() {
        this(new JPopupMenu());
    }

    public CascadePopupMenu(boolean bl) {
        super(bl);
    }

    public CascadePopupMenu(JPopupMenu jPopupMenu) {
        super(jPopupMenu);
    }

    public TrustModel getTrustModel() {
        return this.m_trustModel;
    }

    public int getHeaderHeight() {
        return this.m_headerHeight;
    }

    public synchronized boolean update(TrustModel trustModel) {
        boolean bl = false;
        if (trustModel == null) {
            throw new IllegalArgumentException("Given argument is null!");
        }
        this.m_trustModel = trustModel;
        Hashtable hashtable = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = CascadePopupMenu.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryHash();
        MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
        if (mixCascade != null && hashtable.containsKey(mixCascade.getId())) {
            mixCascade = (MixCascade)hashtable.get(mixCascade.getId());
        }
        Enumeration enumeration = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = CascadePopupMenu.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration();
        if (mixCascade != null || enumeration.hasMoreElements()) {
            Serializable serializable;
            Object object;
            Vector<JMenuItem> vector = new Vector<JMenuItem>();
            this.removeAll();
            this.m_menuItems.clear();
            if (TrustModel.getTrustModels().size() > 1) {
                object = new JPanel(new GridBagLayout());
                serializable = new GridBagConstraints();
                ((GridBagConstraints)serializable).gridx = 0;
                ((GridBagConstraints)serializable).gridy = 0;
                ((GridBagConstraints)serializable).anchor = 10;
                ((Container)object).add((Component)new JLabel(this.m_trustModel.getName()), serializable);
                this.add((Component)object);
                JSeparator jSeparator = new JSeparator();
                this.addSeparator(jSeparator);
                this.m_headerHeight = object.getPreferredSize().height + jSeparator.getPreferredSize().height;
            }
            object = new MouseAdapter(){

                public void mouseEntered(MouseEvent mouseEvent) {
                    ((JMenuItem)mouseEvent.getSource()).setArmed(true);
                }

                public void mouseExited(MouseEvent mouseEvent) {
                    ((JMenuItem)mouseEvent.getSource()).setArmed(false);
                }
            };
            if (trustModel.isEditable()) {
                this.m_editFilter = new JMenuItem(JAPMessages.getString(MSG_EDIT_FILTER));
                this.m_editFilter.addMouseListener((MouseListener)object);
                this.m_editFilter.addActionListener(this.m_cascadeItemListener);
                this.m_editFilter.setIcon(GUIUtils.loadImageIcon("servermanuell.gif", true));
                this.add(this.m_editFilter);
                if (TrustModel.getTrustModels().size() <= 1) {
                    serializable = new JSeparator();
                    this.addSeparator((JSeparator)serializable);
                    this.m_headerHeight = this.m_editFilter.getPreferredSize().height + serializable.getPreferredSize().height;
                }
            }
            boolean bl2 = this.m_trustModel.hasFreeCascades();
            while (mixCascade != null || enumeration.hasMoreElements()) {
                MixCascade mixCascade2;
                ImageIcon imageIcon = null;
                if (mixCascade != null) {
                    mixCascade2 = mixCascade;
                } else {
                    mixCascade2 = (MixCascade)enumeration.nextElement();
                    if (!hashtable.containsKey(mixCascade2.getId()) || !this.m_trustModel.isTrusted(mixCascade2)) continue;
                }
                hashtable.remove(mixCascade2.getId());
                imageIcon = mixCascade2.isUserDefined() ? (mixCascade2.isSocks5Supported() ? this.ICON_SOCKS_MANUELL : this.ICON_MANUELL) : (mixCascade2.isPayment() ? (mixCascade2.isSocks5Supported() ? this.ICON_SOCKS_PAYMENT : this.ICON_PAYMENT) : (mixCascade2.isSocks5Supported() ? this.ICON_SOCKS_INTERNET : this.ICON_INTERNET));
                JMenuItem jMenuItem = new JMenuItem(GUIUtils.trim(mixCascade2.getName(), 35), imageIcon);
                jMenuItem.addMouseListener((MouseListener)object);
                if (this.isNewCascade(mixCascade2)) {
                    jMenuItem.setBackground(this.m_newCascadeColor);
                }
                if (mixCascade != null && mixCascade.equals(mixCascade2)) {
                    jMenuItem.setFont(jMenuItem.getFont().deriveFont(1));
                    this.add(jMenuItem);
                    mixCascade = null;
                } else {
                    jMenuItem.setFont(jMenuItem.getFont().deriveFont(0));
                    if (bl2 && mixCascade2.isPayment()) {
                        vector.addElement(jMenuItem);
                    } else {
                        this.add(jMenuItem);
                    }
                }
                jMenuItem.addActionListener(this.m_cascadeItemListener);
                this.m_menuItems.put(jMenuItem, mixCascade2);
                bl = true;
            }
            for (int i = 0; i < vector.size(); ++i) {
                this.add((JMenuItem)vector.elementAt(i));
            }
        }
        if (bl) {
            try {
                this.pack();
            }
            catch (RuntimeException runtimeException) {
                LogHolder.log(3, LogType.GUI, runtimeException);
            }
        }
        return bl;
    }

    private boolean isNewCascade(MixCascade mixCascade) {
        return Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = CascadePopupMenu.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).getNumberOfEntries() * 2 < Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = CascadePopupMenu.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getNumberOfEntries() && Database.getInstance(class$anon$infoservice$NewCascadeIDEntry == null ? (class$anon$infoservice$NewCascadeIDEntry = CascadePopupMenu.class$("anon.infoservice.NewCascadeIDEntry")) : class$anon$infoservice$NewCascadeIDEntry).getEntryById(mixCascade.getMixIDsAsString()) != null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class CascadeItemListener
    implements ActionListener {
        private CascadeItemListener() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == CascadePopupMenu.this.m_editFilter) {
                JAPController.getInstance().showConfigDialog("ANON_TAB", JAPController.getInstance().getCurrentMixCascade());
                CascadePopupMenu.this.dispose();
            } else {
                MixCascade mixCascade = (MixCascade)CascadePopupMenu.this.m_menuItems.get(actionEvent.getSource());
                if (mixCascade != null) {
                    TrustModel.setCurrentTrustModel(CascadePopupMenu.this.m_trustModel);
                    JAPController.getInstance().setCurrentMixCascade(mixCascade);
                    CascadePopupMenu.this.dispose();
                }
            }
        }
    }
}

