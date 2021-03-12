/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.ServiceLocation;
import anon.infoservice.ServiceOperator;
import anon.util.CountryMapper;
import anon.util.JAPMessages;
import gui.GUIUtils;
import jap.JAPModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

public final class ServerListPanel
extends JPanel
implements ActionListener {
    private static final String MSG_MIX_CLICK = (class$jap$ServerListPanel == null ? (class$jap$ServerListPanel = ServerListPanel.class$("jap.ServerListPanel")) : class$jap$ServerListPanel).getName() + "_mixClick";
    private static final String MSG_MIX_COUNTRY = (class$jap$ServerListPanel == null ? (class$jap$ServerListPanel = ServerListPanel.class$("jap.ServerListPanel")) : class$jap$ServerListPanel).getName() + "_mixCountry";
    private static final String MSG_OPERATOR_COUNTRY = (class$jap$ServerListPanel == null ? (class$jap$ServerListPanel = ServerListPanel.class$("jap.ServerListPanel")) : class$jap$ServerListPanel).getName() + "_operatorCountry";
    private static final String MSG_MIX_AND_OPERATOR_COUNTRY = (class$jap$ServerListPanel == null ? (class$jap$ServerListPanel = ServerListPanel.class$("jap.ServerListPanel")) : class$jap$ServerListPanel).getName() + "_mixAndOperatorCountry";
    private boolean m_bEnabled;
    private ButtonGroup m_bgMixe;
    private int m_selectedIndex;
    private Vector m_itemListeners;
    private JRadioButton[] m_mixButtons;
    private JLabel[] m_mixFlags;
    private JLabel[] m_operatorFlags;
    static /* synthetic */ Class class$jap$ServerListPanel;

    public ServerListPanel(int n, boolean bl, int n2) {
        int n3 = 0;
        if (n < 1) {
            n = 1;
        }
        if (n2 > 0 && n2 < n) {
            n3 = n2;
        }
        this.m_mixButtons = new JRadioButton[n];
        this.m_mixFlags = new JLabel[n];
        this.m_operatorFlags = new JLabel[n];
        this.m_itemListeners = new Vector();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_bgMixe = new ButtonGroup();
        this.m_selectedIndex = 0;
        this.setLayout(gridBagLayout);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        for (int i = 0; i < n; ++i) {
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridx = i * 2;
            gridBagConstraints.gridheight = 3;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            this.m_mixButtons[i] = new JRadioButton();
            if (bl) {
                this.m_mixButtons[i].setToolTipText(JAPMessages.getString("serverPanelAdditional"));
            }
            this.m_mixButtons[i].addActionListener(this);
            this.m_mixButtons[i].setBorder(null);
            this.m_mixButtons[i].setFocusPainted(false);
            this.m_mixButtons[i].setRolloverEnabled(true);
            this.m_mixButtons[i].setIcon(GUIUtils.loadImageIcon("server.gif", true));
            this.m_mixButtons[i].setRolloverIcon(GUIUtils.loadImageIcon("server_blau.gif", true));
            this.m_mixButtons[i].setSelectedIcon(GUIUtils.loadImageIcon("server_rot.gif", true));
            this.m_mixButtons[i].setCursor(Cursor.getPredefinedCursor(12));
            if (i == n3) {
                this.m_selectedIndex = i;
                this.m_mixButtons[i].setSelected(true);
            }
            this.add((Component)this.m_mixButtons[i], gridBagConstraints);
            this.m_bgMixe.add(this.m_mixButtons[i]);
            this.m_bEnabled = bl;
            this.m_mixButtons[i].setEnabled(this.m_bEnabled);
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridx = i * 2 + 1;
            gridBagConstraints.weightx = 0.0;
            this.m_mixFlags[i] = new JLabel(" ");
            this.m_mixFlags[i].setFont(new Font("", 0, (int)(14.0 * (1.0 + (double)JAPModel.getInstance().getFontSize() * 0.1))));
            this.add((Component)this.m_mixFlags[i], gridBagConstraints);
            gridBagConstraints.gridx = i * 2 + 1;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.weightx = n == 1 ? 0.5 : 0.5 / (double)(n - 1);
            JComponent jComponent = new JSeparator();
            this.add((Component)jComponent, gridBagConstraints);
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridx = i * 2 + 1;
            gridBagConstraints.weightx = 0.0;
            this.m_operatorFlags[i] = new JLabel("");
            this.m_operatorFlags[i].setFont(new Font("", 0, (int)(10.0 * (1.0 + (double)JAPModel.getInstance().getFontSize() * 0.1))));
            jComponent = new JPanel(new GridBagLayout());
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.fill = 0;
            gridBagConstraints2.anchor = 17;
            jComponent.add((Component)this.m_operatorFlags[i], gridBagConstraints2);
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.fill = 2;
            jComponent.add((Component)new JLabel(), gridBagConstraints2);
            this.add((Component)jComponent, gridBagConstraints);
        }
        ++gridBagConstraints.gridx;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 13;
        ImageIcon imageIcon = GUIUtils.loadImageIcon("cloud.png", true);
        this.add((Component)new JLabel(imageIcon), gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 0.7;
        this.add((Component)new JLabel(""), gridBagConstraints);
    }

    public boolean areMixButtonsEnabled() {
        return this.m_bEnabled;
    }

    public int getNumberOfMixes() {
        return this.m_mixButtons.length;
    }

    public synchronized void moveToPrevious() {
        JRadioButton jRadioButton = this.setSelectedIndex(this.m_selectedIndex - 1);
        if (jRadioButton != null) {
            this.actionPerformed(new ActionEvent(jRadioButton, 1001, null));
        }
    }

    public synchronized void moveToNext() {
        JRadioButton jRadioButton = this.setSelectedIndex(this.m_selectedIndex + 1);
        if (jRadioButton != null) {
            this.actionPerformed(new ActionEvent(jRadioButton, 1001, null));
        }
    }

    public synchronized void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        Enumeration<AbstractButton> enumeration = this.m_bgMixe.getElements();
        int n = 0;
        while (enumeration.hasMoreElements()) {
            if (object == enumeration.nextElement()) {
                this.m_selectedIndex = n;
                ItemEvent itemEvent = new ItemEvent((AbstractButton)object, 701, object, 1);
                Enumeration enumeration2 = this.m_itemListeners.elements();
                while (enumeration2.hasMoreElements()) {
                    ((ItemListener)enumeration2.nextElement()).itemStateChanged(itemEvent);
                }
                return;
            }
            ++n;
        }
    }

    public void addItemListener(ItemListener itemListener) {
        this.m_itemListeners.addElement(itemListener);
    }

    public void removeItemListener(ItemListener itemListener) {
        this.m_itemListeners.removeElement(itemListener);
    }

    public synchronized JRadioButton setSelectedIndex(int n) {
        int n2;
        if (n < 0) {
            return null;
        }
        Enumeration<AbstractButton> enumeration = this.m_bgMixe.getElements();
        for (n2 = 0; n2 < n && enumeration.hasMoreElements(); ++n2) {
            enumeration.nextElement();
        }
        if (!enumeration.hasMoreElements()) {
            return null;
        }
        this.m_selectedIndex = n2;
        JRadioButton jRadioButton = (JRadioButton)enumeration.nextElement();
        jRadioButton.setSelected(true);
        return jRadioButton;
    }

    private synchronized void updateFlag(int n, ServiceLocation serviceLocation) {
        if (serviceLocation != null) {
            try {
                CountryMapper countryMapper = new CountryMapper(serviceLocation.getCountryCode(), JAPMessages.getLocale());
                this.m_mixFlags[n].setIcon(GUIUtils.loadImageIcon("flags/" + countryMapper.getISOCode() + ".png"));
                this.m_mixFlags[n].setToolTipText(JAPMessages.getString(MSG_MIX_COUNTRY, countryMapper.toString()));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                this.m_mixFlags[n].setIcon(null);
                this.m_mixFlags[n].setToolTipText(null);
            }
        } else {
            this.m_mixFlags[n].setIcon(null);
            this.m_mixFlags[n].setToolTipText(null);
        }
    }

    public synchronized void update(int n, ServiceOperator serviceOperator, ServiceLocation serviceLocation) {
        if (serviceOperator != null && serviceOperator.getCountryCode() != null) {
            if (serviceLocation != null && serviceLocation.getCountryCode() != null && !serviceLocation.getCountryCode().equals(serviceOperator.getCountryCode())) {
                this.updateFlag(n, serviceLocation);
                this.updateOperatorFlag(n, serviceOperator, false);
            } else {
                this.updateOperatorFlag(n, serviceOperator, true);
            }
            if (serviceOperator.getCertPath().isVerified()) {
                if (!serviceOperator.getCertPath().isValid(new Date())) {
                    this.m_operatorFlags[n].setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
                } else if (serviceOperator.getCertPath().countVerifiedAndValidPaths() > 2) {
                    this.m_operatorFlags[n].setBorder(BorderFactory.createLineBorder(Color.green, 2));
                } else if (serviceOperator.getCertPath().countVerifiedAndValidPaths() > 1) {
                    this.m_operatorFlags[n].setBorder(BorderFactory.createLineBorder(new Color(100, 215, 255), 2));
                } else {
                    this.m_operatorFlags[n].setBorder(BorderFactory.createEmptyBorder());
                }
            } else {
                this.m_operatorFlags[n].setBorder(BorderFactory.createLineBorder(Color.red, 2));
            }
        } else {
            this.updateOperatorFlag(n, serviceOperator, false);
            this.updateFlag(n, serviceLocation);
        }
    }

    private synchronized void updateOperatorFlag(int n, ServiceOperator serviceOperator, boolean bl) {
        if (serviceOperator != null && serviceOperator.getCountryCode() != null) {
            CountryMapper countryMapper = new CountryMapper(serviceOperator.getCountryCode(), JAPMessages.getLocale());
            this.m_operatorFlags[n].setIcon(GUIUtils.loadImageIcon("flags/" + countryMapper.getISOCode() + ".png"));
            if (bl) {
                this.m_operatorFlags[n].setToolTipText(JAPMessages.getString(MSG_MIX_AND_OPERATOR_COUNTRY, countryMapper.toString()));
                this.updateFlag(n, null);
            } else {
                this.m_operatorFlags[n].setToolTipText(JAPMessages.getString(MSG_OPERATOR_COUNTRY, countryMapper.toString()));
            }
        } else {
            this.m_operatorFlags[n].setIcon(null);
            this.m_operatorFlags[n].setToolTipText(null);
        }
    }

    public int getSelectedIndex() {
        return this.m_selectedIndex;
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

