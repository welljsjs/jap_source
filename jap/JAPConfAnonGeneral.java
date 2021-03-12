/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.AnonClient;
import anon.infoservice.BlacklistedCascadeIDEntry;
import anon.util.JAPMessages;
import jap.AbstractJAPConfModule;
import jap.IJAPConfSavePoint;
import jap.JAPController;
import jap.JAPModel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public final class JAPConfAnonGeneral
extends AbstractJAPConfModule
implements Observer {
    public static final String MSG_CONNECTION_TIMEOUT = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_loginTimeout";
    public static final String MSG_CONFIRM_ANY_SINGLE_REQUEST = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + ".confirmEverySingleRequest";
    public static final String MSG_DENY_NON_ANONYMOUS_SURFING = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + ".denyNonAnonymousSurfing";
    public static final String MSG_ANONYMIZED_HTTP_HEADERS = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_anonymizedHttpHeaders";
    public static final String MSG_SHOW_CONFIG_ASSISTANT = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + ".lblShowConfigAssistant";
    private static final String MSG_AUTO_CHOOSE_CASCADES = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_autoChooseCascades";
    private static final String MSG_RESTRICT_AUTO_CHOOSE = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_RestrictAutoChoosing";
    private static final String MSG_DO_NOT_RESTRICT_AUTO_CHOOSE = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_doNotRestrictAutoChoosing";
    private static final String MSG_RESTRICT_AUTO_CHOOSE_PAY = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_restrictAutoChoosingPay";
    private static final String MSG_KNOWN_CASCADES = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_knownCascades";
    private static final String MSG_ALLOWED_CASCADES = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_allowedCascades";
    private static final String MSG_AUTO_CHOOSE_ON_START = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_autoChooseOnStart";
    private static final String MSG_TITLE_ASSIGN_SERVICES = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_titleAssignServices";
    private static final String MSG_EXPLAIN_ASSIGN_SERVICES = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_explainAssignServices";
    private static final String MSG_EXPLAIN_ASSIGN_SERVICES_BETA = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_explainAssignServicesBeta";
    private static final String MSG_SERVICE_HTTP = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_serviceHttp";
    private static final String MSG_SERVICE_FTP = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_serviceFtp";
    private static final String MSG_SERVICE_EMAIL = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_serviceEMail";
    private static final String MSG_SERVICE_SOCKS = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_serviceSocks";
    private static final String MSG_PASSIVE_FTP = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_passiveFTP";
    private static final String MSG_TOOLTIP_SERVICE_DEACTIVATED = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_tooltipServiceDeactivated";
    private static final String MSG_EVERY_SECONDS = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_everySeconds";
    private static final String MSG_LBL_WHITELIST = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_autoBlacklist";
    private static final String MSG_AUTO_CHOOSE_ON_STARTUP = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_autoChooseOnStartup";
    private static final String MSG_LBL_IGNORE_ALL_ERRORS = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + ".lblHidePopups";
    private static final String IMG_ARROW_RIGHT = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_arrowRight.gif";
    private static final String IMG_ARROW_LEFT = (class$jap$JAPConfAnonGeneral == null ? (class$jap$JAPConfAnonGeneral = JAPConfAnonGeneral.class$("jap.JAPConfAnonGeneral")) : class$jap$JAPConfAnonGeneral).getName() + "_arrowLeft.gif";
    private static final int DT_INTERVAL_STEPLENGTH = 5;
    private static final int DT_INTERVAL_STEPS = 6;
    private static final int DT_INTERVAL_DEFAULT = 4;
    private static final int DT_INTERVAL_MIN_STEP = 2;
    public static final int DEFAULT_DUMMY_TRAFFIC_INTERVAL_SECONDS = 20000;
    private static final Integer[] LOGIN_TIMEOUTS = new Integer[]{new Integer(5), new Integer(10), new Integer(15), new Integer(20), new Integer(25), new Integer(30), new Integer(40), new Integer(50), new Integer(60)};
    private JCheckBox m_cbConfirmAnySingleRequest;
    private JCheckBox m_cbDenyNonAnonymousSurfing;
    private JCheckBox m_cbAnonymizedHttpHeaders;
    private JCheckBox m_cbDummyTraffic;
    private JCheckBox m_cbAutoConnect;
    private JCheckBox m_cbAutoReConnect;
    private JCheckBox m_cbAutoBlacklist;
    private JCheckBox m_cbAutoChooseCascades;
    private JCheckBox m_cbShowConfigAssistant;
    private JCheckBox m_cbHidePopups;
    private JCheckBox m_cbAutoChooseCascadesOnStartup;
    private JSlider m_sliderDummyTrafficIntervall;
    private JAPController m_Controller = JAPController.getInstance();
    private JComboBox m_comboTimeout;
    static /* synthetic */ Class class$jap$JAPConfAnonGeneral;

    protected JAPConfAnonGeneral(IJAPConfSavePoint iJAPConfSavePoint) {
        super(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                JAPModel.getInstance().addObserver(this);
                return true;
            }
        }
        return false;
    }

    public String getTabTitle() {
        return JAPMessages.getString("settingsInfoServiceConfigAdvancedSettingsTabTitle");
    }

    public void update(Observable observable, Object object) {
        if (object != null) {
            if (object.equals(JAPModel.CHANGED_AUTO_RECONNECT)) {
                this.m_cbAutoReConnect.setSelected(JAPModel.isAutomaticallyReconnected());
            } else if (object.equals(JAPModel.CHANGED_CASCADE_AUTO_CHANGE)) {
                this.m_cbAutoChooseCascades.setSelected(JAPModel.getInstance().isCascadeAutoSwitched());
            } else if (object.equals(JAPModel.CHANGED_AUTO_CONNECT)) {
                this.m_cbAutoConnect.setSelected(JAPModel.isAutoConnect());
            } else if (object.equals("RulesChanged")) {
                this.m_cbConfirmAnySingleRequest.setSelected(JAPModel.getInstance().isAskForAnyNonAnonymousRequest());
                this.m_cbDenyNonAnonymousSurfing.setSelected(!JAPModel.getInstance().isNonAnonymousAllowed());
            } else if (object.equals(JAPModel.CHANGED_ANONYMIZED_HTTP_HEADERS)) {
                this.m_cbAnonymizedHttpHeaders.setSelected(JAPModel.getInstance().isAnonymizedHttpHeaders());
            } else if (object.equals(JAPModel.CHANGED_CONFIG_ASSISTANT_SHOWUP)) {
                this.m_cbShowConfigAssistant.setSelected(JAPModel.getInstance().isConfigAssistantAutomaticallyShown());
            }
        }
    }

    protected void onUpdateValues() {
        int n = JAPModel.getDummyTraffic();
        this.m_cbDummyTraffic.setSelected(n != Integer.MAX_VALUE);
        if (n != Integer.MAX_VALUE) {
            int n2 = n / 1000;
            if (n2 < 5) {
                n2 = 5;
            } else if (n2 > 30) {
                n2 = 30;
            }
            this.m_sliderDummyTrafficIntervall.setValue(n2);
        }
        this.m_sliderDummyTrafficIntervall.setEnabled(n != Integer.MAX_VALUE);
        Dictionary dictionary = this.m_sliderDummyTrafficIntervall.getLabelTable();
        for (int i = 1; i <= 6; ++i) {
            ((JLabel)dictionary.get(new Integer(i * 5))).setEnabled(this.m_sliderDummyTrafficIntervall.isEnabled());
        }
        this.m_cbConfirmAnySingleRequest.setSelected(JAPModel.getInstance().isAskForAnyNonAnonymousRequest());
        this.m_cbDenyNonAnonymousSurfing.setSelected(!JAPModel.getInstance().isNonAnonymousAllowed());
        this.m_cbConfirmAnySingleRequest.setEnabled(!this.m_cbDenyNonAnonymousSurfing.isSelected());
        this.m_cbAnonymizedHttpHeaders.setSelected(JAPModel.getInstance().isAnonymizedHttpHeaders());
        this.m_cbShowConfigAssistant.setSelected(JAPModel.getInstance().isConfigAssistantAutomaticallyShown());
        this.m_cbHidePopups.setSelected(JAPModel.getInstance().isAnonymityPopupsHidden());
        this.m_cbAutoConnect.setSelected(JAPModel.isAutoConnect());
        this.m_cbAutoReConnect.setSelected(JAPModel.isAutomaticallyReconnected());
        this.m_cbAutoBlacklist.setSelected(BlacklistedCascadeIDEntry.areNewCascadesInBlacklist());
        this.m_cbAutoChooseCascades.setSelected(JAPModel.getInstance().isCascadeAutoSwitched());
        this.m_cbAutoChooseCascadesOnStartup.setSelected(JAPModel.getInstance().isCascadeAutoChosenOnStartup());
        this.m_cbAutoChooseCascadesOnStartup.setEnabled(this.m_cbAutoChooseCascades.isSelected());
        this.setLoginTimeout(AnonClient.getLoginTimeout());
    }

    protected boolean onOkPressed() {
        int n = this.m_cbDummyTraffic.isSelected() ? this.m_sliderDummyTrafficIntervall.getValue() * 1000 : Integer.MAX_VALUE;
        final int n2 = n;
        new Thread(new Runnable(){

            public void run() {
                JAPConfAnonGeneral.this.m_Controller.setDummyTraffic(n2);
            }
        }).start();
        JAPModel.getInstance().setAskForAnyNonAnonymousRequest(this.m_cbConfirmAnySingleRequest.isSelected());
        JAPModel.getInstance().setNonAnonymousAllowed(!this.m_cbDenyNonAnonymousSurfing.isSelected());
        BlacklistedCascadeIDEntry.putNewCascadesInBlacklist(this.m_cbAutoBlacklist.isSelected());
        JAPModel.getInstance().setAutoConnect(this.m_cbAutoConnect.isSelected());
        JAPModel.getInstance().setAutoReConnect(this.m_cbAutoReConnect.isSelected());
        JAPModel.getInstance().setCascadeAutoSwitch(this.m_cbAutoChooseCascades.isSelected());
        JAPModel.getInstance().setAutoChooseCascadeOnStartup(this.m_cbAutoChooseCascadesOnStartup.isSelected());
        JAPModel.getInstance().setAnonymizedHttpHeaders(this.m_cbAnonymizedHttpHeaders.isSelected());
        JAPModel.getInstance().setShowConfigAssistantAutomatically(this.m_cbShowConfigAssistant.isSelected());
        JAPModel.getInstance().setHideAnonymityPopups(this.m_cbHidePopups.isSelected());
        AnonClient.setLoginTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        return true;
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        this.m_cbConfirmAnySingleRequest = new JCheckBox(JAPMessages.getString(MSG_CONFIRM_ANY_SINGLE_REQUEST));
        this.m_cbDenyNonAnonymousSurfing = new JCheckBox(JAPMessages.getString(MSG_DENY_NON_ANONYMOUS_SURFING));
        this.m_cbAnonymizedHttpHeaders = new JCheckBox(JAPMessages.getString(MSG_ANONYMIZED_HTTP_HEADERS));
        this.m_cbShowConfigAssistant = new JCheckBox(JAPMessages.getString(MSG_SHOW_CONFIG_ASSISTANT));
        this.m_cbHidePopups = new JCheckBox(JAPMessages.getString(MSG_LBL_IGNORE_ALL_ERRORS));
        this.m_cbAutoConnect = new JCheckBox(JAPMessages.getString("settingsautoConnectCheckBox"));
        this.m_cbAutoReConnect = new JCheckBox(JAPMessages.getString("settingsautoReConnectCheckBox"));
        this.m_cbAutoChooseCascades = new JCheckBox(JAPMessages.getString(MSG_AUTO_CHOOSE_CASCADES));
        this.m_cbAutoChooseCascadesOnStartup = new JCheckBox(JAPMessages.getString(MSG_AUTO_CHOOSE_ON_STARTUP));
        this.m_cbAutoChooseCascadesOnStartup.setVisible(false);
        this.m_cbAutoBlacklist = new JCheckBox(JAPMessages.getString(MSG_LBL_WHITELIST));
        this.m_cbHidePopups.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                JAPConfAnonGeneral.this.m_cbShowConfigAssistant.setEnabled(itemEvent.getStateChange() != 1);
            }
        });
        this.m_cbAutoChooseCascades.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                JAPConfAnonGeneral.this.m_cbAutoChooseCascadesOnStartup.setEnabled(itemEvent.getStateChange() == 1);
            }
        });
        this.m_cbDummyTraffic = new JCheckBox(JAPMessages.getString("ngConfAnonGeneralSendDummy"));
        this.m_cbDummyTraffic.setEnabled(false);
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.weighty = 0.0;
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbDenyNonAnonymousSurfing, gridBagConstraints);
        this.m_cbDenyNonAnonymousSurfing.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                JAPConfAnonGeneral.this.m_cbConfirmAnySingleRequest.setEnabled(itemEvent.getStateChange() != 1);
            }
        });
        ++gridBagConstraints.gridy;
        gridBagConstraints.insets = new Insets(10, 30, 0, 10);
        jPanel.add((Component)this.m_cbConfirmAnySingleRequest, gridBagConstraints);
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbHidePopups, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.insets = new Insets(10, 30, 0, 10);
        jPanel.add((Component)this.m_cbShowConfigAssistant, gridBagConstraints);
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbAutoConnect, gridBagConstraints);
        ++gridBagConstraints.gridy;
        jPanel.add((Component)this.m_cbAutoReConnect, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 0);
        jPanel.add((Component)this.m_cbAutoChooseCascades, gridBagConstraints);
        ++gridBagConstraints.gridx;
        ++gridBagConstraints.gridx;
        ++gridBagConstraints.gridx;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        jPanel.add((Component)this.m_cbAutoChooseCascadesOnStartup, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        jPanel.add((Component)this.m_cbAutoBlacklist, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 1;
        jPanel.add((Component)this.m_cbDummyTraffic, gridBagConstraints);
        this.m_sliderDummyTrafficIntervall = new JSlider(0, 2, 30, 20);
        Hashtable<Integer, JLabel> hashtable = new Hashtable<Integer, JLabel>(6);
        for (int i = 1; i <= 6; ++i) {
            hashtable.put(new Integer(i * 5), new JLabel(i * 5 + "s"));
        }
        this.m_sliderDummyTrafficIntervall.setLabelTable(hashtable);
        this.m_sliderDummyTrafficIntervall.setMajorTickSpacing(5);
        this.m_sliderDummyTrafficIntervall.setMinorTickSpacing(1);
        this.m_sliderDummyTrafficIntervall.setPaintLabels(true);
        this.m_sliderDummyTrafficIntervall.setPaintTicks(true);
        this.m_sliderDummyTrafficIntervall.setSnapToTicks(true);
        ++gridBagConstraints.gridx;
        gridBagConstraints.gridwidth = 3;
        jPanel.add((Component)this.m_sliderDummyTrafficIntervall, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 1;
        jPanel.add((Component)new JLabel(JAPMessages.getString(MSG_CONNECTION_TIMEOUT) + " (s):"), gridBagConstraints);
        this.m_comboTimeout = new JComboBox<Integer>(LOGIN_TIMEOUTS);
        gridBagConstraints.fill = 0;
        ++gridBagConstraints.gridx;
        jPanel.add((Component)this.m_comboTimeout, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = 1;
        jPanel.add((Component)new JLabel(), gridBagConstraints);
        this.m_cbDummyTraffic.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                JAPConfAnonGeneral.this.m_sliderDummyTrafficIntervall.setEnabled(itemEvent.getStateChange() == 1);
                Dictionary dictionary = JAPConfAnonGeneral.this.m_sliderDummyTrafficIntervall.getLabelTable();
                for (int i = 1; i <= 6; ++i) {
                    ((JLabel)dictionary.get(new Integer(i * 5))).setEnabled(itemEvent.getStateChange() == 1);
                }
            }
        });
        this.updateValues(false);
    }

    public void onResetToDefaultsPressed() {
        this.m_cbConfirmAnySingleRequest.setSelected(true);
        this.m_cbDenyNonAnonymousSurfing.setSelected(false);
        this.m_cbAnonymizedHttpHeaders.setSelected(false);
        this.m_cbShowConfigAssistant.setSelected(true);
        this.m_cbHidePopups.setSelected(false);
        this.m_cbDummyTraffic.setSelected(true);
        this.m_cbAutoBlacklist.setSelected(false);
        this.m_sliderDummyTrafficIntervall.setEnabled(true);
        this.m_sliderDummyTrafficIntervall.setValue(4);
        this.m_cbAutoConnect.setSelected(true);
        this.m_cbAutoReConnect.setSelected(true);
        this.m_cbAutoChooseCascades.setSelected(true);
        this.m_cbAutoChooseCascadesOnStartup.setSelected(true);
        this.setLoginTimeout(30000);
    }

    public String getHelpContext() {
        return "services_general";
    }

    protected void onRootPanelShown() {
    }

    private void setLoginTimeout(int n) {
        int n2 = n / 1000;
        if (n2 >= (Integer)this.m_comboTimeout.getItemAt(this.m_comboTimeout.getItemCount() - 1)) {
            this.m_comboTimeout.setSelectedIndex(this.m_comboTimeout.getItemCount() - 1);
            AnonClient.setLoginTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
        } else if (n2 <= (Integer)this.m_comboTimeout.getItemAt(0)) {
            this.m_comboTimeout.setSelectedIndex(0);
            AnonClient.setLoginTimeout((Integer)this.m_comboTimeout.getSelectedItem() * 1000);
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

