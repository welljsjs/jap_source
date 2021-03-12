/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.crypto.MultiCertPath;
import anon.infoservice.Database;
import anon.infoservice.DatabaseMessage;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.InfoServiceHolderMessage;
import anon.infoservice.ListenerInterface;
import anon.util.IXMLEncodable;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPHtmlMultiLineLabel;
import gui.JAPJIntField;
import gui.JAPMultilineLabel;
import gui.MixDetailsDialog;
import gui.MultiCertOverview;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.JAPConfInfoServiceSavePoint;
import jap.JAPController;
import jap.JAPControllerMessage;
import jap.JAPModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import logging.LogHolder;
import logging.LogType;

public class JAPConfInfoService
extends AbstractJAPConfModule
implements Observer {
    public static final String MSG_CONNECT_TIMEOUT = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_connectTimeout";
    private static final String MSG_ALLOW_DIRECT_CONNECTION = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_allowDirectConnection";
    private static final String MSG_VIEW_CERT = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_viewCert";
    private static final String MSG_REALLY_DELETE = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_reallyDelete";
    private static final String MSG_USE_MORE_IS = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_useMoreIS";
    private static final String MSG_EXPLANATION = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_explanation";
    private static final String MSG_ALL_INFO_SERVICES = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_allInfoServices";
    private static final String MSG_INACTIVE = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + "_inactive";
    private static final String MSG_LBL_IGNORE_ALL_ERRORS = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + ".lblHidePopups";
    private static final String MSG_LBL_NO_SYSTEM_INFO = (class$jap$JAPConfInfoService == null ? (class$jap$JAPConfInfoService = JAPConfInfoService.class$("jap.JAPConfInfoService")) : class$jap$JAPConfInfoService).getName() + ".lblSendSystemInfo";
    private static final Integer[] CONNECT_TIMEOUTS = new Integer[]{new Integer(10), new Integer(15), new Integer(20), new Integer(25), new Integer(30), new Integer(40), new Integer(50), new Integer(60)};
    private JAPMultilineLabel m_hostLabel;
    private JLabel m_portLabel;
    private JLabel m_lblInactive;
    private JList m_listKnownInfoServices;
    private JTextField addInfoServiceHostField;
    private JAPJIntField addInfoServicePortField;
    private JTextField addInfoServiceNameField;
    private JPanel addInfoServicePanel;
    private JPanel descriptionPanel;
    private JButton settingsInfoServiceConfigBasicSettingsRemoveButton;
    private JCheckBox m_allowAutomaticIS;
    private JCheckBox m_cbHidePopups;
    private JCheckBox m_cbSendSystemInfo;
    private JComboBox m_comboAnonymousConnection;
    private JCheckBox m_cbxUseRedundantISRequests;
    private JComboBox m_cmbAskedInfoServices;
    private JAPHtmlMultiLineLabel m_lblExplanation;
    private JAPHtmlMultiLineLabel m_settingsInfoServiceConfigBasicSettingsDescriptionLabel;
    private DefaultListModel knownInfoServicesListModel;
    private boolean mb_newInfoService = true;
    private MultiCertPath m_selectedISCertPaths;
    private String m_selectedISName;
    private JTabbedPane m_infoServiceTabPane;
    private JComboBox m_comboTimeout;
    static /* synthetic */ Class class$jap$JAPConfInfoService;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public JAPConfInfoService() {
        super(new JAPConfInfoServiceSavePoint());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            JAPModel.getInstance().addObserver(this);
            Observer observer = new Observer(){
                private boolean m_preferredInfoServiceIsAlsoInDatabase = false;
                private InfoServiceDBEntry m_currentPreferredInfoService = null;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void update(Observable observable, Object object) {
                    block43: {
                        try {
                            int n;
                            if (observable == Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoService.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry)) {
                                int n2;
                                DefaultListModel defaultListModel;
                                Enumeration enumeration;
                                int n3 = ((DatabaseMessage)object).getMessageCode();
                                if (n3 == 1 || n3 == 2) {
                                    enumeration = (InfoServiceDBEntry)((DatabaseMessage)object).getMessageData();
                                    defaultListModel = JAPConfInfoService.this.knownInfoServicesListModel;
                                    synchronized (defaultListModel) {
                                        n2 = JAPConfInfoService.this.knownInfoServicesListModel.indexOf(enumeration);
                                        if (n2 != -1) {
                                            JAPConfInfoService.this.knownInfoServicesListModel.setElementAt(enumeration, n2);
                                            if (((InfoServiceDBEntry)((Object)enumeration)).equals(this.m_currentPreferredInfoService)) {
                                                this.m_preferredInfoServiceIsAlsoInDatabase = true;
                                            }
                                        } else if (((InfoServiceDBEntry)((Object)enumeration)).isUserDefined()) {
                                            JAPConfInfoService.this.knownInfoServicesListModel.addElement(enumeration);
                                        } else {
                                            int n4 = JAPConfInfoService.this.findFirstUserDefinedListModelEntry(JAPConfInfoService.this.knownInfoServicesListModel);
                                            if (SwingUtilities.isEventDispatchThread()) {
                                                JAPConfInfoService.this.knownInfoServicesListModel.add(n4, enumeration);
                                            } else {
                                                final class Test
                                                implements Runnable {
                                                    int m_Index;
                                                    private final /* synthetic */ InfoServiceDBEntry val$updatedEntry;

                                                    protected Test(int n, InfoServiceDBEntry infoServiceDBEntry) {
                                                        this.val$updatedEntry = infoServiceDBEntry;
                                                        this.m_Index = n;
                                                    }

                                                    public void run() {
                                                        JAPConfInfoService.this.knownInfoServicesListModel.add(this.m_Index, this.val$updatedEntry);
                                                    }
                                                }
                                                SwingUtilities.invokeAndWait(new Test(n4, (InfoServiceDBEntry)((Object)enumeration)));
                                            }
                                        }
                                        if (JAPConfInfoService.this.m_listKnownInfoServices.getSelectedValue() == null && ((InfoServiceDBEntry)((Object)enumeration)).equals(this.m_currentPreferredInfoService)) {
                                            JAPConfInfoService.this.m_listKnownInfoServices.setSelectedValue(enumeration, true);
                                        }
                                    }
                                }
                                if (n3 == 3) {
                                    enumeration = (InfoServiceDBEntry)((DatabaseMessage)object).getMessageData();
                                    defaultListModel = JAPConfInfoService.this.knownInfoServicesListModel;
                                    synchronized (defaultListModel) {
                                        if (((InfoServiceDBEntry)((Object)enumeration)).equals(this.m_currentPreferredInfoService)) {
                                            this.m_preferredInfoServiceIsAlsoInDatabase = false;
                                        } else {
                                            JAPConfInfoService.this.knownInfoServicesListModel.removeElement(enumeration);
                                        }
                                    }
                                }
                                if (n3 == 4) {
                                    enumeration = JAPConfInfoService.this.knownInfoServicesListModel;
                                    synchronized (enumeration) {
                                        JAPConfInfoService.this.knownInfoServicesListModel.clear();
                                        JAPConfInfoService.this.knownInfoServicesListModel.addElement(this.m_currentPreferredInfoService);
                                        JAPConfInfoService.this.m_listKnownInfoServices.setSelectedIndex(0);
                                        this.m_preferredInfoServiceIsAlsoInDatabase = false;
                                    }
                                }
                                if (n3 != 5) break block43;
                                enumeration = ((Vector)((DatabaseMessage)object).getMessageData()).elements();
                                defaultListModel = JAPConfInfoService.this.knownInfoServicesListModel;
                                synchronized (defaultListModel) {
                                    n2 = 0;
                                    while (enumeration.hasMoreElements()) {
                                        JAPConfInfoService.this.knownInfoServicesListModel.add(JAPConfInfoService.this.findFirstUserDefinedListModelEntry(JAPConfInfoService.this.knownInfoServicesListModel), enumeration.nextElement());
                                        ++n2;
                                    }
                                    break block43;
                                }
                            }
                            if (observable != InfoServiceHolder.getInstance() || (n = ((InfoServiceHolderMessage)object).getMessageCode()) != 1) break block43;
                            InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)((InfoServiceHolderMessage)object).getMessageData();
                            DefaultListModel defaultListModel = JAPConfInfoService.this.knownInfoServicesListModel;
                            synchronized (defaultListModel) {
                                if (this.m_currentPreferredInfoService != null && this.m_currentPreferredInfoService.equals(infoServiceDBEntry)) {
                                    int n5 = JAPConfInfoService.this.knownInfoServicesListModel.indexOf(this.m_currentPreferredInfoService);
                                    if (n5 != -1) {
                                        JAPConfInfoService.this.knownInfoServicesListModel.setElementAt(infoServiceDBEntry, n5);
                                    }
                                    this.m_currentPreferredInfoService = infoServiceDBEntry;
                                } else {
                                    InfoServiceDBEntry infoServiceDBEntry2;
                                    int n6;
                                    if (this.m_currentPreferredInfoService != null) {
                                        if (this.m_preferredInfoServiceIsAlsoInDatabase) {
                                            n6 = JAPConfInfoService.this.knownInfoServicesListModel.indexOf(this.m_currentPreferredInfoService);
                                            if (n6 != -1) {
                                                JAPConfInfoService.this.knownInfoServicesListModel.setElementAt(JAPConfInfoService.this.knownInfoServicesListModel.elementAt(n6), n6);
                                            }
                                        } else {
                                            JAPConfInfoService.this.knownInfoServicesListModel.removeElement(this.m_currentPreferredInfoService);
                                        }
                                    }
                                    this.m_currentPreferredInfoService = infoServiceDBEntry;
                                    if (infoServiceDBEntry != null) {
                                        n6 = JAPConfInfoService.this.knownInfoServicesListModel.indexOf(infoServiceDBEntry);
                                        if (n6 != -1) {
                                            this.m_preferredInfoServiceIsAlsoInDatabase = true;
                                            JAPConfInfoService.this.knownInfoServicesListModel.setElementAt(infoServiceDBEntry, n6);
                                        } else {
                                            this.m_preferredInfoServiceIsAlsoInDatabase = false;
                                            this.update(Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoService.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry), new DatabaseMessage(1, infoServiceDBEntry));
                                        }
                                    }
                                    if ((infoServiceDBEntry2 = (InfoServiceDBEntry)JAPConfInfoService.this.m_listKnownInfoServices.getSelectedValue()) != null) {
                                        JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.setEnabled(infoServiceDBEntry2.isUserDefined() && !infoServiceDBEntry2.equals(infoServiceDBEntry));
                                    }
                                }
                            }
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.GUI, exception);
                        }
                    }
                }
            };
            Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoService.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).addObserver(observer);
            Object object = InfoServiceHolder.getInstance();
            synchronized (object) {
                InfoServiceHolder.getInstance().addObserver(observer);
                observer.update(InfoServiceHolder.getInstance(), new InfoServiceHolderMessage(1, InfoServiceHolder.getInstance().getPreferredInfoService()));
            }
            object = new Observer(){

                public void update(Observable observable, Object object) {
                    try {
                        int n;
                        if (observable == InfoServiceHolder.getInstance() && (n = ((InfoServiceHolderMessage)object).getMessageCode()) == 2) {
                            boolean bl = (Boolean)((InfoServiceHolderMessage)object).getMessageData();
                            JAPConfInfoService.this.m_cbxUseRedundantISRequests.setSelected(bl);
                        }
                        if (observable == JAPController.getInstance() && (n = ((JAPControllerMessage)object).getMessageCode()) == 1) {
                            JAPConfInfoService.this.m_allowAutomaticIS.setSelected(!JAPModel.isInfoServiceDisabled());
                        }
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.GUI, exception);
                    }
                }
            };
            InfoServiceHolder infoServiceHolder = InfoServiceHolder.getInstance();
            synchronized (infoServiceHolder) {
                InfoServiceHolder.getInstance().addObserver((Observer)object);
                object.update(InfoServiceHolder.getInstance(), new InfoServiceHolderMessage(2, new Boolean(InfoServiceHolder.getInstance().isChangeInfoServices())));
            }
            JAPController.getInstance().addObserver((Observer)object);
            object.update(JAPController.getInstance(), new JAPControllerMessage(1));
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        JAPConfInfoService jAPConfInfoService = this;
        synchronized (jAPConfInfoService) {
            this.m_infoServiceTabPane = new JTabbedPane();
            this.m_infoServiceTabPane.insertTab(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsTabTitle"), null, this.createInfoServiceConfigPanel(), null, 0);
            this.m_infoServiceTabPane.insertTab(JAPMessages.getString("settingsInfoServiceConfigAdvancedSettingsTabTitle"), null, this.createInfoServiceAdvancedPanel(), null, 1);
            GridBagLayout gridBagLayout = new GridBagLayout();
            jPanel.setLayout(gridBagLayout);
            jPanel.add((Component)this.m_infoServiceTabPane, AbstractJAPConfModule.createTabbedRootPanelContraints());
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("confTreeInfoServiceLeaf");
    }

    public void update(Observable observable, Object object) {
        if (object != null) {
            if (object.equals(JAPModel.CHANGED_ALLOW_INFOSERVICE_DIRECT_CONNECTION)) {
                this.m_comboAnonymousConnection.setSelectedIndex(JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting());
            } else if (object.equals(JAPModel.CHANGED_INFOSERVICE_AUTO_UPDATE)) {
                this.m_allowAutomaticIS.setSelected(!JAPModel.isInfoServiceDisabled());
            }
        }
    }

    private JPanel createInfoServiceConfigPanel() {
        JPanel jPanel = new JPanel();
        JPanel jPanel2 = new JPanel();
        JPanel jPanel3 = new JPanel();
        this.descriptionPanel = new JPanel();
        this.addInfoServicePanel = new JPanel();
        this.knownInfoServicesListModel = new DefaultListModel();
        this.m_listKnownInfoServices = new JList(this.knownInfoServicesListModel);
        this.m_listKnownInfoServices.setSelectionMode(0);
        Font font = this.m_listKnownInfoServices.getFont();
        this.m_listKnownInfoServices.setFixedCellWidth(15 * this.m_listKnownInfoServices.getFontMetrics(font).charWidth('W'));
        this.m_listKnownInfoServices.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    JAPConfInfoService.this.setPreferredInfoService();
                }
            }
        });
        this.m_listKnownInfoServices.setCellRenderer(new ListCellRenderer(){

            public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
                JLabel jLabel = null;
                jLabel = ((InfoServiceDBEntry)object).isUserDefined() ? new JLabel(((InfoServiceDBEntry)object).getName(), GUIUtils.loadImageIcon("infoservicemanuell.gif", true), 2) : new JLabel(((InfoServiceDBEntry)object).getName(), GUIUtils.loadImageIcon("infoservicefrominternet.gif", true), 2);
                jLabel.setFont(new Font(jLabel.getFont().getName(), jLabel.getFont().getStyle() & 0xFFFFFFFE, jLabel.getFont().getSize()));
                InfoServiceDBEntry infoServiceDBEntry = InfoServiceHolder.getInstance().getPreferredInfoService();
                if (infoServiceDBEntry != null && infoServiceDBEntry.equals((InfoServiceDBEntry)object)) {
                    jLabel.setFont(new Font(jLabel.getFont().getName(), jLabel.getFont().getStyle() | 1, jLabel.getFont().getSize()));
                }
                if (bl) {
                    jLabel.setOpaque(true);
                    jLabel.setBackground(Color.lightGray);
                }
                return jLabel;
            }
        });
        JScrollPane jScrollPane = new JScrollPane(this.m_listKnownInfoServices);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        final JButton jButton = new JButton(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsFetchInfoServicesButton"));
        jButton.setIcon(GUIUtils.loadImageIcon("reload.gif", true, false));
        jButton.setDisabledIcon(GUIUtils.loadImageIcon("reloaddisabled_anim.gif", true, false));
        jButton.setPressedIcon(GUIUtils.loadImageIcon("reloadrollover.gif", true, false));
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                jButton.setEnabled(false);
                Thread thread = new Thread(new Runnable(){

                    public void run() {
                        while (!JAPController.getInstance().updateInfoServices(false)) {
                            int n;
                            if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 1 && !JAPController.getInstance().isAnonConnected()) {
                                n = JAPDialog.showConfirmDialog((Component)JAPConfInfoService.this.getRootPanel(), JAPMessages.getString(JAPController.MSG_IS_NOT_ALLOWED), 0, 0);
                                if (n != 0) break;
                                JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                                JAPController.getInstance().updateInfoServices(false);
                                continue;
                            }
                            if (JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting() == 2 && JAPController.getInstance().isAnonConnected()) {
                                n = JAPDialog.showConfirmDialog((Component)JAPConfInfoService.this.getRootPanel(), JAPMessages.getString(JAPController.MSG_IS_NOT_ALLOWED_FOR_ANONYMOUS), 0, 0);
                                if (n != 0) break;
                                JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(0);
                                JAPController.getInstance().updateInfoServices(false);
                                continue;
                            }
                            JAPDialog.showErrorDialog((Component)JAPConfInfoService.this.getRootPanel(), JAPMessages.getString("settingsInfoServiceConfigBasicSettingsFetchInfoServicesError"));
                            break;
                        }
                        try {
                            SwingUtilities.invokeAndWait(new Runnable(){

                                public void run() {
                                    jButton.setEnabled(true);
                                }
                            });
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });
        final JButton jButton2 = new JButton(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsSetPreferredButton"));
        jButton2.setEnabled(false);
        jButton2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConfInfoService.this.setPreferredInfoService();
            }
        });
        final JButton jButton3 = new JButton(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsAddButton"));
        jButton3.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.setEnabled(false);
                JAPConfInfoService.this.descriptionPanel.setVisible(false);
                JAPConfInfoService.this.addInfoServiceHostField.setText("");
                JAPConfInfoService.this.addInfoServiceNameField.setText("");
                JAPConfInfoService.this.addInfoServicePortField.setText("");
                JAPConfInfoService.this.addInfoServicePanel.setVisible(true);
                JAPConfInfoService.this.mb_newInfoService = true;
            }
        });
        this.settingsInfoServiceConfigBasicSettingsRemoveButton = new JButton(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsRemoveButton"));
        this.settingsInfoServiceConfigBasicSettingsRemoveButton.setEnabled(false);
        this.settingsInfoServiceConfigBasicSettingsRemoveButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (JAPDialog.showYesNoDialog(JAPConfInfoService.this.getRootPanel(), JAPMessages.getString(MSG_REALLY_DELETE))) {
                    InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)JAPConfInfoService.this.m_listKnownInfoServices.getSelectedValue();
                    if (infoServiceDBEntry != null) {
                        Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoService.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).remove(infoServiceDBEntry);
                    }
                    JAPConfInfoService.this.m_listKnownInfoServices.setSelectedIndex(0);
                    JAPConfInfoService.this.addInfoServicePanel.setVisible(false);
                }
            }
        });
        final JButton jButton4 = new JButton(JAPMessages.getString(MixDetailsDialog.MSG_CERTIFICATES), GUIUtils.loadImageIcon("certs/trusted_black.png"));
        jButton4.setEnabled(false);
        jButton4.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (JAPConfInfoService.this.m_selectedISCertPaths != null) {
                    MultiCertOverview multiCertOverview = new MultiCertOverview(JAPConfInfoService.this.getRootPanel().getParent(), JAPConfInfoService.this.m_selectedISCertPaths, JAPConfInfoService.this.m_selectedISName, true);
                }
            }
        });
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsListLabel"));
        this.m_listKnownInfoServices.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    Object object;
                    InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)JAPConfInfoService.this.m_listKnownInfoServices.getSelectedValue();
                    if (infoServiceDBEntry != null) {
                        IXMLEncodable iXMLEncodable;
                        object = "";
                        String string = "";
                        Vector vector = infoServiceDBEntry.getListenerInterfaces();
                        Enumeration enumeration = vector.elements();
                        while (enumeration.hasMoreElements()) {
                            String string2;
                            iXMLEncodable = (ListenerInterface)enumeration.nextElement();
                            if (((String)object).indexOf(((ListenerInterface)iXMLEncodable).getHost()) == -1) {
                                if (!((String)object).equals("")) {
                                    object = (String)object + "\n";
                                }
                                object = (String)object + ((ListenerInterface)iXMLEncodable).getHost();
                            }
                            if (string.indexOf(string2 = Integer.toString(((ListenerInterface)iXMLEncodable).getPort())) != -1) continue;
                            if (!string.equals("")) {
                                string = string + ", ";
                            }
                            string = string + Integer.toString(((ListenerInterface)iXMLEncodable).getPort());
                        }
                        JAPConfInfoService.this.m_hostLabel.setText((String)object);
                        JAPConfInfoService.this.m_portLabel.setText(string);
                        if (JAPConfInfoService.this.m_hostLabel.getRootPane() != null) {
                            JAPConfInfoService.this.m_hostLabel.getRootPane().revalidate();
                        }
                        if (infoServiceDBEntry.isUserDefined()) {
                            JAPConfInfoService.this.addInfoServiceHostField.setText((String)object);
                            JAPConfInfoService.this.addInfoServicePortField.setText(string);
                            JAPConfInfoService.this.addInfoServiceNameField.setText(infoServiceDBEntry.getName());
                            JAPConfInfoService.this.descriptionPanel.setVisible(false);
                            JAPConfInfoService.this.addInfoServicePanel.setVisible(true);
                            JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.setEnabled(true);
                            JAPConfInfoService.this.mb_newInfoService = false;
                            jButton4.setEnabled(false);
                            jButton4.setIcon(null);
                            jButton4.setToolTipText(null);
                        } else {
                            JAPConfInfoService.this.addInfoServicePanel.setVisible(false);
                            JAPConfInfoService.this.descriptionPanel.setVisible(true);
                            iXMLEncodable = infoServiceDBEntry.getCertPath();
                            if (iXMLEncodable == null) {
                                jButton4.setEnabled(false);
                                jButton4.setIcon(null);
                                jButton4.setForeground(JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.getForeground());
                                JAPConfInfoService.this.m_lblInactive.setVisible(false);
                            } else {
                                jButton4.setEnabled(true);
                                if (!((MultiCertPath)iXMLEncodable).isVerified()) {
                                    jButton4.setIcon(GUIUtils.loadImageIcon("certs/not_trusted.png"));
                                    jButton4.setForeground(Color.red);
                                    JAPConfInfoService.this.m_lblInactive.setVisible(true);
                                } else {
                                    jButton4.setForeground(JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.getForeground());
                                    if (!((MultiCertPath)iXMLEncodable).isValid(new Date())) {
                                        JAPConfInfoService.this.m_lblInactive.setVisible(true);
                                        jButton4.setIcon(GUIUtils.loadImageIcon("certs/invalid.png"));
                                    } else if (((MultiCertPath)iXMLEncodable).countVerifiedAndValidPaths() > 2) {
                                        JAPConfInfoService.this.m_lblInactive.setVisible(false);
                                        jButton4.setIcon(GUIUtils.loadImageIcon("certs/trusted_green.png"));
                                    } else if (((MultiCertPath)iXMLEncodable).countVerifiedAndValidPaths() > 1) {
                                        JAPConfInfoService.this.m_lblInactive.setVisible(false);
                                        jButton4.setIcon(GUIUtils.loadImageIcon("certs/trusted_blue.png"));
                                    } else {
                                        JAPConfInfoService.this.m_lblInactive.setVisible(false);
                                        jButton4.setIcon(GUIUtils.loadImageIcon("certs/trusted_black.png"));
                                    }
                                }
                            }
                        }
                    }
                    if ((infoServiceDBEntry = (InfoServiceDBEntry)JAPConfInfoService.this.m_listKnownInfoServices.getSelectedValue()) != null) {
                        object = InfoServiceHolder.getInstance().getPreferredInfoService();
                        JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.setEnabled(infoServiceDBEntry.isUserDefined() && !infoServiceDBEntry.equals(object));
                        jButton2.setEnabled(!infoServiceDBEntry.equals(object));
                        JAPConfInfoService.this.m_selectedISCertPaths = infoServiceDBEntry.getCertPath();
                        JAPConfInfoService.this.m_selectedISName = infoServiceDBEntry.getName();
                    } else {
                        JAPConfInfoService.this.settingsInfoServiceConfigBasicSettingsRemoveButton.setEnabled(false);
                    }
                }
            }
        });
        JPanel jPanel4 = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel4.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 3;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagLayout.setConstraints(jButton, gridBagConstraints);
        jPanel4.add(jButton);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        gridBagLayout.setConstraints(jButton2, gridBagConstraints);
        jPanel4.add(jButton2);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel4.add((Component)jButton4, gridBagConstraints);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel4.add((Component)jButton3, gridBagConstraints);
        gridBagConstraints.gridx = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridy = 0;
        jPanel4.add((Component)this.settingsInfoServiceConfigBasicSettingsRemoveButton, gridBagConstraints);
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        jPanel2.setLayout(gridBagLayout2);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.anchor = 18;
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.insets = new Insets(10, 10, 0, 5);
        gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
        jPanel2.add(jLabel);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.insets = new Insets(10, 10, 0, 5);
        jPanel2.add((Component)new JLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsInformationInterfacesHostInfo")), gridBagConstraints2);
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(10, 0, 0, 5);
        this.m_hostLabel = new JAPMultilineLabel("                                                      ", null, null);
        jPanel2.add((Component)this.m_hostLabel, gridBagConstraints2);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.insets = new Insets(10, 10, 0, 5);
        jPanel2.add((Component)new JLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsInformationInterfacesPortInfo")), gridBagConstraints2);
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(10, 0, 0, 5);
        this.m_portLabel = new JLabel("                                                      ");
        jPanel2.add((Component)this.m_portLabel, gridBagConstraints2);
        this.m_lblInactive = new JLabel(JAPMessages.getString(MSG_INACTIVE));
        this.m_lblInactive.setIcon(GUIUtils.loadImageIcon("certs/invalid.png"));
        this.m_lblInactive.setVisible(false);
        ++gridBagConstraints2.gridy;
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.fill = 0;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.insets = new Insets(10, 10, 0, 5);
        jPanel2.add((Component)this.m_lblInactive, gridBagConstraints2);
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new Insets(10, 10, 5, 5);
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.gridheight = 8;
        gridBagLayout2.setConstraints(jScrollPane, gridBagConstraints2);
        jPanel2.add(jScrollPane);
        gridBagConstraints2.gridheight = 1;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 9;
        gridBagConstraints2.gridwidth = 5;
        gridBagConstraints2.weighty = 0.0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(10, 0, 5, 0);
        gridBagConstraints2.anchor = 18;
        gridBagConstraints2.fill = 2;
        gridBagLayout2.setConstraints(jPanel4, gridBagConstraints2);
        jPanel2.add(jPanel4);
        this.m_settingsInfoServiceConfigBasicSettingsDescriptionLabel = new JAPHtmlMultiLineLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsDescriptionLabel"));
        GridBagLayout gridBagLayout3 = new GridBagLayout();
        this.descriptionPanel.setLayout(gridBagLayout3);
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.anchor = 18;
        gridBagConstraints3.fill = 0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new Insets(10, 10, 10, 5);
        gridBagLayout3.setConstraints(this.m_settingsInfoServiceConfigBasicSettingsDescriptionLabel, gridBagConstraints3);
        this.descriptionPanel.add(this.m_settingsInfoServiceConfigBasicSettingsDescriptionLabel);
        this.addInfoServiceHostField = new JTextField(20);
        this.addInfoServicePortField = new JAPJIntField(65535);
        this.addInfoServiceNameField = new JTextField(20);
        JButton jButton5 = new JButton(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsAddInfoServiceAddButton"));
        jButton5.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    InfoServiceDBEntry infoServiceDBEntry;
                    String string = JAPConfInfoService.this.addInfoServiceNameField.getText().trim();
                    if (string.equals("")) {
                        string = null;
                    }
                    if (!JAPConfInfoService.this.mb_newInfoService && (infoServiceDBEntry = (InfoServiceDBEntry)JAPConfInfoService.this.m_listKnownInfoServices.getSelectedValue()) != null) {
                        Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoService.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).remove(infoServiceDBEntry);
                    }
                    infoServiceDBEntry = new InfoServiceDBEntry(string, null, new ListenerInterface(JAPConfInfoService.this.addInfoServiceHostField.getText().trim(), Integer.parseInt(JAPConfInfoService.this.addInfoServicePortField.getText().trim())).toVector(), false, true, 0L, 0L, false, null);
                    infoServiceDBEntry.setUserDefined(true);
                    Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoService.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).update(infoServiceDBEntry);
                    JAPConfInfoService.this.addInfoServicePanel.setVisible(false);
                    JAPConfInfoService.this.addInfoServiceHostField.setText("");
                    JAPConfInfoService.this.addInfoServicePortField.setText("");
                    JAPConfInfoService.this.addInfoServiceNameField.setText("");
                    JAPConfInfoService.this.descriptionPanel.setVisible(true);
                    jButton3.setEnabled(true);
                    JAPConfInfoService.this.m_listKnownInfoServices.setSelectedValue(infoServiceDBEntry, true);
                }
                catch (Exception exception) {
                    JAPDialog.showErrorDialog((Component)JAPConfInfoService.this.addInfoServicePanel, JAPMessages.getString("settingsInfoServiceConfigBasicSettingsAddInfoServiceAddError"));
                }
            }
        });
        JButton jButton6 = new JButton(JAPMessages.getString("cancelButton"));
        jButton6.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPConfInfoService.this.addInfoServicePanel.setVisible(false);
                JAPConfInfoService.this.addInfoServiceHostField.setText("");
                JAPConfInfoService.this.addInfoServicePortField.setText("");
                JAPConfInfoService.this.addInfoServiceNameField.setText("");
                JAPConfInfoService.this.descriptionPanel.setVisible(true);
                jButton3.setEnabled(true);
            }
        });
        JLabel jLabel2 = new JLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsAddInfoServiceHostLabel"));
        JLabel jLabel3 = new JLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsAddInfoServicePortLabel"));
        JLabel jLabel4 = new JLabel(JAPMessages.getString("settingsInfoServiceConfigBasicSettingsAddInfoServiceNameLabel"));
        JPanel jPanel5 = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(2);
        jPanel5.setLayout(flowLayout);
        jPanel5.add(jButton5);
        jPanel5.add(jButton6);
        GridBagLayout gridBagLayout4 = new GridBagLayout();
        this.addInfoServicePanel.setLayout(gridBagLayout4);
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = 0;
        gridBagConstraints4.weighty = 0.0;
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.anchor = 18;
        gridBagConstraints4.insets = new Insets(5, 10, 0, 10);
        gridBagLayout4.setConstraints(jLabel2, gridBagConstraints4);
        this.addInfoServicePanel.add(jLabel2);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.anchor = 18;
        gridBagConstraints4.gridheight = 1;
        gridBagConstraints4.insets = new Insets(0, 10, 5, 10);
        gridBagLayout4.setConstraints(this.addInfoServiceHostField, gridBagConstraints4);
        this.addInfoServicePanel.add(this.addInfoServiceHostField);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 2;
        gridBagConstraints4.insets = new Insets(0, 10, 0, 10);
        gridBagLayout4.setConstraints(jLabel3, gridBagConstraints4);
        this.addInfoServicePanel.add(jLabel3);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 3;
        gridBagConstraints4.gridheight = 1;
        gridBagConstraints4.insets = new Insets(0, 10, 5, 10);
        gridBagLayout4.setConstraints(this.addInfoServicePortField, gridBagConstraints4);
        this.addInfoServicePanel.add(this.addInfoServicePortField);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 4;
        gridBagConstraints4.insets = new Insets(0, 10, 0, 10);
        gridBagLayout4.setConstraints(jLabel4, gridBagConstraints4);
        this.addInfoServicePanel.add(jLabel4);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 5;
        gridBagConstraints4.weighty = 0.0;
        gridBagConstraints4.insets = new Insets(0, 10, 10, 10);
        gridBagLayout4.setConstraints(this.addInfoServiceNameField, gridBagConstraints4);
        this.addInfoServicePanel.add(this.addInfoServiceNameField);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 6;
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.weighty = 1.0;
        gridBagConstraints4.insets = new Insets(0, 10, 10, 10);
        this.addInfoServicePanel.add((Component)jPanel5, gridBagConstraints4);
        GridBagLayout gridBagLayout5 = new GridBagLayout();
        jPanel3.setLayout(gridBagLayout5);
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.anchor = 18;
        gridBagConstraints5.fill = 1;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.weighty = 1.0;
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.gridy = 0;
        gridBagLayout5.setConstraints(this.descriptionPanel, gridBagConstraints5);
        jPanel3.add(this.descriptionPanel);
        jPanel3.add((Component)this.addInfoServicePanel, gridBagConstraints5);
        jPanel3.setPreferredSize(new Dimension(Math.max(this.descriptionPanel.getPreferredSize().width, this.addInfoServicePanel.getPreferredSize().width), Math.max(this.descriptionPanel.getPreferredSize().height, this.addInfoServicePanel.getPreferredSize().height)));
        this.addInfoServicePanel.setVisible(false);
        GridBagLayout gridBagLayout6 = new GridBagLayout();
        jPanel.setLayout(gridBagLayout6);
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.anchor = 18;
        gridBagConstraints6.fill = 1;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.weighty = 1.0;
        gridBagConstraints6.insets = new Insets(0, 0, 5, 0);
        gridBagLayout6.setConstraints(jPanel2, gridBagConstraints6);
        jPanel.add(jPanel2);
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weighty = 1.0;
        gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
        gridBagLayout6.setConstraints(jPanel3, gridBagConstraints6);
        jPanel.add(jPanel3);
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 1;
        gridBagConstraints6.weighty = 0.0;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.fill = 2;
        gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
        jPanel.add((Component)new JSeparator(), gridBagConstraints6);
        return jPanel;
    }

    private JPanel createInfoServiceAdvancedPanel() {
        JPanel jPanel = new JPanel();
        this.m_allowAutomaticIS = new JCheckBox(JAPMessages.getString("settingsInfoServiceConfigAdvancedSettingsEnableAutomaticRequestsBox"));
        this.m_cbHidePopups = new JCheckBox(JAPMessages.getString(MSG_LBL_IGNORE_ALL_ERRORS));
        this.m_cbSendSystemInfo = new JCheckBox(JAPMessages.getString(MSG_LBL_NO_SYSTEM_INFO));
        this.m_cbxUseRedundantISRequests = new JCheckBox(JAPMessages.getString(MSG_USE_MORE_IS) + ":");
        this.m_cbxUseRedundantISRequests.setVisible(false);
        this.m_cbxUseRedundantISRequests.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                InfoServiceHolder.getInstance().setChangeInfoServices(JAPConfInfoService.this.m_cbxUseRedundantISRequests.isSelected());
            }
        });
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        JPanel jPanel2 = new JPanel();
        gridBagConstraints.gridwidth = 3;
        jPanel2.add(new JLabel(JAPMessages.getString(MSG_ALLOW_DIRECT_CONNECTION) + ":"));
        String[] arrstring = new String[JAPModel.getMsgConnectionAnonymous().length];
        System.arraycopy(JAPModel.getMsgConnectionAnonymous(), 0, arrstring, 0, arrstring.length);
        for (int i = 0; i < arrstring.length; ++i) {
            arrstring[i] = JAPMessages.getString(arrstring[i]);
        }
        this.m_comboAnonymousConnection = new JComboBox<String>(arrstring);
        jPanel2.add(this.m_comboAnonymousConnection);
        this.m_comboAnonymousConnection.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                if (JAPConfInfoService.this.m_comboAnonymousConnection.getSelectedIndex() == 1) {
                    JAPConfInfoService.this.m_cbSendSystemInfo.setEnabled(false);
                } else {
                    JAPConfInfoService.this.m_cbSendSystemInfo.setEnabled(true);
                }
            }
        });
        jPanel.add((Component)jPanel2, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 3;
        jPanel.add((Component)this.m_allowAutomaticIS, gridBagConstraints);
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbSendSystemInfo, gridBagConstraints);
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbHidePopups, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 2;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_USE_MORE_IS) + ":"), gridBagConstraints);
        Object[] arrobject = new Object[4];
        for (int i = 0; i < arrobject.length; ++i) {
            arrobject[i] = new Integer(i + 1);
        }
        this.m_cmbAskedInfoServices = new JComboBox<Object>(arrobject);
        gridBagConstraints.gridwidth = 1;
        ++gridBagConstraints.gridx;
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_cmbAskedInfoServices, gridBagConstraints);
        this.m_cbxUseRedundantISRequests.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                JAPConfInfoService.this.m_cmbAskedInfoServices.setEnabled(JAPConfInfoService.this.m_cbxUseRedundantISRequests.isSelected());
            }
        });
        Integer n = new Integer(3);
        this.m_lblExplanation = new JAPHtmlMultiLineLabel(JAPMessages.getString(MSG_EXPLANATION, new Object[]{n}));
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = 1;
        jPanel.add((Component)this.m_lblExplanation, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 1;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_CONNECT_TIMEOUT) + " (s):"), gridBagConstraints);
        this.m_comboTimeout = new JComboBox<Integer>(CONNECT_TIMEOUTS);
        gridBagConstraints.fill = 0;
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_comboTimeout, gridBagConstraints);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridwidth = 3;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)new JLabel(), gridBagConstraints);
        this.updateValues(false);
        return jPanel;
    }

    protected void showSettingsPanel() {
        this.m_infoServiceTabPane.setSelectedIndex(1);
    }

    public void onResetToDefaultsPressed() {
        this.m_comboAnonymousConnection.setSelectedIndex(0);
        this.m_cmbAskedInfoServices.setSelectedIndex(2);
        this.m_cbxUseRedundantISRequests.setSelected(true);
        this.m_allowAutomaticIS.setSelected(true);
        this.m_cbHidePopups.setSelected(false);
        this.m_cbSendSystemInfo.setSelected(true);
        this.setConnectionTimeout(15000);
    }

    protected void onRootPanelShown() {
        if (this.m_listKnownInfoServices.getSelectedIndex() < 0) {
            this.m_listKnownInfoServices.setSelectedValue(InfoServiceHolder.getInstance().getPreferredInfoService(), true);
        }
    }

    protected void onUpdateValues() {
        int n = InfoServiceHolder.getInstance().getNumberOfAskedInfoServices() - 1;
        if (n < 0) {
            n = 0;
        } else if (n >= this.m_cmbAskedInfoServices.getItemCount()) {
            n = this.m_cmbAskedInfoServices.getItemCount() - 1;
        }
        this.m_cmbAskedInfoServices.setSelectedIndex(n);
        this.m_cbHidePopups.setSelected(JAPModel.getInstance().isInfoServicePopupsHidden());
        this.m_cbSendSystemInfo.setSelected(InfoServiceDBEntry.isInfoServiceStatisticsUsed());
        this.m_allowAutomaticIS.setSelected(!JAPModel.isInfoServiceDisabled());
        this.m_comboAnonymousConnection.setSelectedIndex(JAPModel.getInstance().getInfoServiceAnonymousConnectionSetting());
        this.m_cmbAskedInfoServices.setEnabled(InfoServiceHolder.getInstance().isChangeInfoServices());
        this.m_lblExplanation.setFont(new JLabel().getFont());
        this.m_settingsInfoServiceConfigBasicSettingsDescriptionLabel.setFont(new JLabel().getFont());
        this.setConnectionTimeout(InfoServiceDBEntry.getConnectionTimeout());
    }

    public String getHelpContext() {
        return "infoservice";
    }

    protected boolean onOkPressed() {
        JAPModel.getInstance().setInfoServiceAnonymousConnectionSetting(this.m_comboAnonymousConnection.getSelectedIndex());
        InfoServiceHolder.getInstance().setNumberOfAskedInfoServices(this.m_cmbAskedInfoServices.getSelectedIndex() + 1);
        JAPModel.getInstance().setInfoServiceDisabled(!this.m_allowAutomaticIS.isSelected());
        JAPModel.getInstance().setHideInfoServicePopups(this.m_cbHidePopups.isSelected());
        InfoServiceDBEntry.setUseInfoServiceStatistics(this.m_cbSendSystemInfo.isSelected());
        InfoServiceDBEntry.setConnectionTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        return true;
    }

    private void setPreferredInfoService() {
        InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)this.m_listKnownInfoServices.getSelectedValue();
        if (infoServiceDBEntry != null) {
            InfoServiceHolder.getInstance().setPreferredInfoService(infoServiceDBEntry);
        }
    }

    private int findFirstUserDefinedListModelEntry(DefaultListModel defaultListModel) {
        int n;
        for (n = 0; n < defaultListModel.size() && !((InfoServiceDBEntry)defaultListModel.getElementAt(n)).isUserDefined(); ++n) {
        }
        return n;
    }

    private void setConnectionTimeout(int n) {
        int n2 = n / 1000;
        if (n2 >= (Integer)this.m_comboTimeout.getItemAt(this.m_comboTimeout.getItemCount() - 1)) {
            this.m_comboTimeout.setSelectedIndex(this.m_comboTimeout.getItemCount() - 1);
            InfoServiceDBEntry.setConnectionTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        } else if (n2 <= (Integer)this.m_comboTimeout.getItemAt(0)) {
            this.m_comboTimeout.setSelectedIndex(0);
            InfoServiceDBEntry.setConnectionTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        } else {
            for (int i = 1; i < this.m_comboTimeout.getItemCount(); ++i) {
                if (n2 > (Integer)this.m_comboTimeout.getItemAt(i)) continue;
                this.m_comboTimeout.setSelectedIndex(i);
                break;
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

