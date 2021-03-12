/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.tor.ordescription.InfoServiceORListFetcher;
import anon.tor.ordescription.ORDescriptor;
import anon.tor.ordescription.ORList;
import anon.tor.ordescription.PlainORListFetcher;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPJIntField;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.JAPController;
import jap.JAPModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

final class JAPConfTor
extends AbstractJAPConfModule
implements ActionListener {
    public static final String MSG_ACTIVATE = (class$jap$JAPConfTor == null ? (class$jap$JAPConfTor = JAPConfTor.class$("jap.JAPConfTor")) : class$jap$JAPConfTor).getName() + "_activate";
    private static final int MIN_CON_PER_PATH = 1;
    private static final int MAX_CON_PER_PATH = 5;
    private JCheckBox m_cbxActive;
    private JTable m_tableRouters;
    private JSlider m_sliderMaxPathLen;
    private JSlider m_sliderMinPathLen;
    private JSlider m_sliderConnectionsPerPath;
    private JButton m_bttnFetchRouters;
    private JLabel m_labelAvailableRouters;
    private JCheckBox m_cbPreCreateRoutes;
    private JCheckBox m_cbNoDefaultTorServer;
    private JTextField m_tfTorDirServerHostName;
    private JAPJIntField m_jintfieldTorDirServerPort;
    private JLabel m_lblMaxPathLen;
    private JLabel m_lblMinPathLen;
    private JLabel m_lblPathSwitchTime;
    private JScrollPane m_scrollPane;
    private JPanel m_panelSlider;
    private TitledBorder m_border;
    private DateFormat ms_dateFormat = DateFormat.getDateTimeInstance(2, 3);
    static /* synthetic */ Class class$jap$JAPConfTor;

    public JAPConfTor() {
        super(null);
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = 18;
        jPanel.setLayout(gridBagLayout);
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        this.m_cbxActive = new JCheckBox(JAPMessages.getString(MSG_ACTIVATE), true);
        this.m_cbxActive.addChangeListener(new ChangeListener(){

            public void stateChanged(ChangeEvent changeEvent) {
                int n;
                JAPConfTor.this.m_labelAvailableRouters.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_tableRouters.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_bttnFetchRouters.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_sliderMinPathLen.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_sliderMaxPathLen.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_sliderConnectionsPerPath.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_cbPreCreateRoutes.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_lblMinPathLen.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_lblMaxPathLen.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_scrollPane.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_lblPathSwitchTime.setEnabled(JAPConfTor.this.m_cbxActive.isSelected());
                JAPConfTor.this.m_border = new TitledBorder(JAPConfTor.this.m_border.getTitle());
                if (JAPConfTor.this.m_cbxActive.isSelected()) {
                    JAPConfTor.this.m_bttnFetchRouters.setDisabledIcon(GUIUtils.loadImageIcon("reloaddisabled_anim.gif", true, false));
                } else {
                    JAPConfTor.this.m_border.setTitleColor(Color.gray);
                    JAPConfTor.this.m_bttnFetchRouters.setDisabledIcon(GUIUtils.loadImageIcon("reloadrollover.gif", true, false));
                }
                JAPConfTor.this.m_panelSlider.setBorder(JAPConfTor.this.m_border);
                Dictionary dictionary = JAPConfTor.this.m_sliderMaxPathLen.getLabelTable();
                for (n = 2; n <= 5; ++n) {
                    ((JLabel)dictionary.get(new Integer(n))).setEnabled(JAPConfTor.this.m_sliderMaxPathLen.isEnabled());
                }
                dictionary = JAPConfTor.this.m_sliderMinPathLen.getLabelTable();
                for (n = 2; n <= 5; ++n) {
                    ((JLabel)dictionary.get(new Integer(n))).setEnabled(JAPConfTor.this.m_sliderMinPathLen.isEnabled());
                }
                dictionary = JAPConfTor.this.m_sliderConnectionsPerPath.getLabelTable();
                for (n = 1; n <= 5; ++n) {
                    ((JLabel)dictionary.get(new Integer(n))).setEnabled(JAPConfTor.this.m_sliderConnectionsPerPath.isEnabled());
                }
            }
        });
        jPanel.add((Component)this.m_cbxActive, gridBagConstraints);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        JPanel jPanel2 = new JPanel(gridBagLayout2);
        this.m_labelAvailableRouters = new JLabel(JAPMessages.getString("torBorderAvailableRouters") + ":");
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 0.0;
        jPanel2.add((Component)this.m_labelAvailableRouters, gridBagConstraints2);
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn(JAPMessages.getString("torRouterName"));
        defaultTableModel.addColumn(JAPMessages.getString("torRouterAdr"));
        defaultTableModel.addColumn(JAPMessages.getString("torRouterPort"));
        defaultTableModel.addColumn(JAPMessages.getString("torRouterSoftware"));
        defaultTableModel.setNumRows(3);
        this.m_tableRouters = new MyJTable(defaultTableModel);
        this.m_tableRouters.setPreferredScrollableViewportSize(new Dimension(70, this.m_tableRouters.getRowHeight() * 5));
        this.m_tableRouters.setCellSelectionEnabled(false);
        this.m_tableRouters.setColumnSelectionAllowed(false);
        this.m_tableRouters.setRowSelectionAllowed(true);
        this.m_tableRouters.setSelectionMode(0);
        this.m_scrollPane = new JScrollPane(this.m_tableRouters);
        this.m_scrollPane.setAutoscrolls(true);
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.gridwidth = 2;
        jPanel2.add((Component)this.m_scrollPane, gridBagConstraints2);
        this.m_bttnFetchRouters = new JButton(JAPMessages.getString("torBttnFetchRouters"));
        this.m_bttnFetchRouters.setIcon(GUIUtils.loadImageIcon("reload.gif", true, false));
        this.m_bttnFetchRouters.setDisabledIcon(GUIUtils.loadImageIcon("reloaddisabled_anim.gif", true, false));
        this.m_bttnFetchRouters.setPressedIcon(GUIUtils.loadImageIcon("reloadrollover.gif", true, false));
        this.m_bttnFetchRouters.setActionCommand("fetchRouters");
        this.m_bttnFetchRouters.addActionListener(this);
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.weighty = 0.0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.anchor = 13;
        gridBagConstraints2.insets = new Insets(5, 5, 5, 0);
        jPanel2.add((Component)this.m_bttnFetchRouters, gridBagConstraints2);
        jPanel.add((Component)jPanel2, gridBagConstraints);
        jPanel2 = new JPanel(new GridBagLayout());
        jPanel2.setBorder(new TitledBorder(JAPMessages.getString("torBorderTorDirServer")));
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        this.m_cbNoDefaultTorServer = new JCheckBox(JAPMessages.getString("torCheckBoxNoDefaultDirServer"));
        jPanel2.add((Component)this.m_cbNoDefaultTorServer, gridBagConstraints3);
        gridBagConstraints3.gridx = 1;
        jPanel2.add((Component)new JLabel(JAPMessages.getString("torDirServerHostName")), gridBagConstraints3);
        this.m_tfTorDirServerHostName = new JTextField();
        gridBagConstraints3.gridx = 2;
        jPanel2.add((Component)this.m_tfTorDirServerHostName, gridBagConstraints3);
        this.m_jintfieldTorDirServerPort = new JAPJIntField(65535);
        gridBagConstraints3.gridx = 3;
        jPanel2.add((Component)this.m_jintfieldTorDirServerPort, gridBagConstraints3);
        gridBagConstraints3.gridx = 4;
        jPanel2.add((Component)new JLabel(JAPMessages.getString("torDirServerPort")), gridBagConstraints3);
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.anchor = 18;
        gridBagConstraints4.insets = new Insets(2, 5, 2, 5);
        gridBagConstraints4.fill = 0;
        this.m_lblMinPathLen = new JLabel(JAPMessages.getString("torPrefMinPathLen"));
        jPanel2.add((Component)this.m_lblMinPathLen, gridBagConstraints4);
        this.m_sliderMinPathLen = new JSlider();
        this.m_sliderMinPathLen.setPaintLabels(true);
        this.m_sliderMinPathLen.setPaintTicks(true);
        this.m_sliderMinPathLen.setMajorTickSpacing(1);
        this.m_sliderMinPathLen.setSnapToTicks(true);
        this.m_sliderMinPathLen.setMinimum(2);
        this.m_sliderMinPathLen.setMaximum(5);
        this.m_sliderMinPathLen.addChangeListener(new ChangeListener(){

            public void stateChanged(ChangeEvent changeEvent) {
                if (JAPConfTor.this.m_sliderMaxPathLen.getValue() < JAPConfTor.this.m_sliderMinPathLen.getValue()) {
                    JAPConfTor.this.m_sliderMaxPathLen.setValue(JAPConfTor.this.m_sliderMinPathLen.getValue());
                }
            }
        });
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.fill = 2;
        jPanel2.add((Component)this.m_sliderMinPathLen, gridBagConstraints4);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.fill = 0;
        this.m_lblMaxPathLen = new JLabel(JAPMessages.getString("torPrefMaxPathLen"));
        jPanel2.add((Component)this.m_lblMaxPathLen, gridBagConstraints4);
        this.m_sliderMaxPathLen = new JSlider();
        this.m_sliderMaxPathLen.setMinimum(2);
        this.m_sliderMaxPathLen.setMaximum(5);
        this.m_sliderMaxPathLen.setPaintLabels(true);
        this.m_sliderMaxPathLen.setPaintTicks(true);
        this.m_sliderMaxPathLen.setMajorTickSpacing(1);
        this.m_sliderMaxPathLen.setMinorTickSpacing(1);
        this.m_sliderMaxPathLen.setSnapToTicks(true);
        this.m_sliderMaxPathLen.addChangeListener(new ChangeListener(){

            public void stateChanged(ChangeEvent changeEvent) {
                if (JAPConfTor.this.m_sliderMaxPathLen.getValue() < JAPConfTor.this.m_sliderMinPathLen.getValue()) {
                    JAPConfTor.this.m_sliderMinPathLen.setValue(JAPConfTor.this.m_sliderMaxPathLen.getValue());
                }
            }
        });
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.fill = 2;
        jPanel2.add((Component)this.m_sliderMaxPathLen, gridBagConstraints4);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 2;
        gridBagConstraints4.fill = 0;
        this.m_lblPathSwitchTime = new JLabel(JAPMessages.getString("torPrefPathSwitchTime"));
        jPanel2.add((Component)this.m_lblPathSwitchTime, gridBagConstraints4);
        this.m_sliderConnectionsPerPath = new JSlider();
        Hashtable<Integer, JLabel> hashtable = new Hashtable<Integer, JLabel>();
        hashtable.put(new Integer(1), new JLabel("10"));
        hashtable.put(new Integer(2), new JLabel("50"));
        hashtable.put(new Integer(3), new JLabel("100"));
        hashtable.put(new Integer(4), new JLabel("500"));
        hashtable.put(new Integer(5), new JLabel("1000"));
        this.m_sliderConnectionsPerPath.setLabelTable(hashtable);
        this.m_sliderConnectionsPerPath.setMinimum(1);
        this.m_sliderConnectionsPerPath.setMaximum(5);
        this.m_sliderConnectionsPerPath.setMajorTickSpacing(1);
        this.m_sliderConnectionsPerPath.setMinorTickSpacing(1);
        this.m_sliderConnectionsPerPath.setSnapToTicks(true);
        this.m_sliderConnectionsPerPath.setPaintLabels(true);
        this.m_sliderConnectionsPerPath.setPaintTicks(true);
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.fill = 2;
        jPanel2.add((Component)this.m_sliderConnectionsPerPath, gridBagConstraints4);
        this.m_cbPreCreateRoutes = new JCheckBox(JAPMessages.getString("ngConfAnonGeneralPreCreateRoutes"));
        ++gridBagConstraints4.gridy;
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridwidth = 2;
        jPanel2.add((Component)this.m_cbPreCreateRoutes, gridBagConstraints4);
        this.m_border = new TitledBorder(JAPMessages.getString("torBorderPreferences"));
        jPanel2.setBorder(this.m_border);
        this.m_panelSlider = jPanel2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)jPanel2, gridBagConstraints);
    }

    public String getTabTitle() {
        return "Tor";
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("enableTor")) {
            this.updateGuiOutput();
        } else if (actionEvent.getActionCommand().equals("fetchRouters")) {
            this.fetchRoutersAsync(true);
        }
    }

    protected boolean onOkPressed() {
        JAPModel.getInstance().setTorActivated(this.m_cbxActive.isSelected());
        int n = this.m_sliderConnectionsPerPath.getValue();
        int[] arrn = new int[]{10, 50, 100, 500, 1000};
        JAPController.setTorMaxConnectionsPerRoute(arrn[n - 1]);
        JAPController.setTorRouteLen(this.m_sliderMinPathLen.getValue(), this.m_sliderMaxPathLen.getValue());
        JAPController.setPreCreateAnonRoutes(this.m_cbPreCreateRoutes.isSelected());
        JAPController.setTorUseNoneDefaultDirServer(this.m_cbNoDefaultTorServer.isSelected());
        return true;
    }

    protected void onUpdateValues() {
        this.updateGuiOutput();
    }

    public String getHelpContext() {
        return "services_tor";
    }

    private void updateGuiOutput() {
        int n = JAPModel.getTorMaxConnectionsPerRoute();
        n = n < 25 ? 1 : (n < 75 ? 2 : (n < 250 ? 3 : (n < 750 ? 4 : 5)));
        this.m_sliderConnectionsPerPath.setValue(n);
        this.m_sliderMaxPathLen.setValue(JAPModel.getTorMaxRouteLen());
        this.m_sliderMinPathLen.setValue(JAPModel.getTorMinRouteLen());
        this.m_cbPreCreateRoutes.setSelected(JAPModel.isPreCreateAnonRoutesEnabled());
        this.m_cbxActive.setSelected(JAPModel.getInstance().isTorActivated());
        this.m_cbNoDefaultTorServer.setSelected(JAPModel.isTorNoneDefaultDirServerEnabled());
    }

    private void fetchRoutersAsync(final boolean bl) {
        this.m_bttnFetchRouters.setEnabled(false);
        Runnable runnable = new Runnable(){

            public void run() {
                Object object;
                ORList oRList = null;
                oRList = JAPModel.isTorNoneDefaultDirServerEnabled() ? new ORList(new PlainORListFetcher("141.76.45.45", 9030)) : new ORList(new InfoServiceORListFetcher());
                if (!oRList.updateList()) {
                    if (bl) {
                        JAPDialog.showErrorDialog((Component)JAPConfTor.this.getRootPanel(), JAPMessages.getString("torErrorFetchRouters"));
                    }
                    JAPConfTor.this.m_bttnFetchRouters.setEnabled(true);
                    return;
                }
                DefaultTableModel defaultTableModel = (DefaultTableModel)JAPConfTor.this.m_tableRouters.getModel();
                Vector vector = oRList.getList();
                defaultTableModel.setNumRows(vector.size());
                for (int i = 0; i < vector.size(); ++i) {
                    object = (ORDescriptor)vector.elementAt(i);
                    JAPConfTor.this.m_tableRouters.setValueAt(((ORDescriptor)object).getName(), i, 0);
                    JAPConfTor.this.m_tableRouters.setValueAt(((ORDescriptor)object).getAddress(), i, 1);
                    JAPConfTor.this.m_tableRouters.setValueAt(new Integer(((ORDescriptor)object).getPort()), i, 2);
                    JAPConfTor.this.m_tableRouters.setValueAt(((ORDescriptor)object).getSoftware(), i, 3);
                    JAPConfTor.this.m_tableRouters.invalidate();
                }
                Date date = oRList.getPublished();
                object = JAPMessages.getString("unknown");
                if (date != null) {
                    object = JAPConfTor.this.ms_dateFormat.format(date);
                }
                JAPConfTor.this.m_labelAvailableRouters.setText(JAPMessages.getString("torBorderAvailableRouters") + " (" + (String)object + "):");
                JAPConfTor.this.m_labelAvailableRouters.invalidate();
                JAPConfTor.this.getRootPanel().validate();
                JAPConfTor.this.m_bttnFetchRouters.setEnabled(true);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void onResetToDefaultsPressed() {
        this.m_cbPreCreateRoutes.setSelected(false);
        this.m_sliderMaxPathLen.setValue(3);
        this.m_sliderMinPathLen.setValue(2);
        this.m_sliderConnectionsPerPath.setValue(1000);
        this.m_cbxActive.setSelected(false);
        this.m_cbNoDefaultTorServer.setSelected(false);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class MyJTable
    extends JTable {
        private static final long serialVersionUID = 1L;

        public MyJTable(DefaultTableModel defaultTableModel) {
            super(defaultTableModel);
        }

        public boolean isCellEditable(int n, int n2) {
            return false;
        }
    }
}

