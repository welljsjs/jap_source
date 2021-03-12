/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.MixCascade;
import anon.util.JAPMessages;
import jap.AbstractJAPConfModule;
import jap.JAPConfAnon;
import jap.JAPConfAnonGeneral;
import jap.JAPConfMixminion;
import jap.JAPConfTC;
import jap.JAPConfTor;
import jap.JAPController;
import jap.JAPModel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class JAPConfServices
extends AbstractJAPConfModule {
    private JAPConfAnon m_anonModule;
    private JAPConfTor m_torModule;
    private JAPConfMixminion m_mixminionModule;
    private JAPConfAnonGeneral m_anonGeneralModule;
    private JAPConfTC m_tcModule;
    private JTabbedPane m_tabsAnon;
    private Vector m_tabbedModules;

    public JAPConfServices() {
        super(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                this.m_anonModule.initObservers();
                this.m_torModule.initObservers();
                this.m_mixminionModule.initObservers();
                this.m_anonGeneralModule.initObservers();
                this.m_tcModule.initObservers();
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        JAPConfAnon jAPConfAnon = this.getAnonModule();
        JAPConfTor jAPConfTor = this.getTorModule();
        JAPConfMixminion jAPConfMixminion = this.getMixminionModule();
        JAPConfAnonGeneral jAPConfAnonGeneral = this.getAnonGeneralModule();
        JAPConfTC jAPConfTC = this.getTCModule();
        JAPConfServices jAPConfServices = this;
        synchronized (jAPConfServices) {
            jPanel.removeAll();
            this.m_tabsAnon = new JTabbedPane();
            this.m_tabbedModules = new Vector();
            GridBagLayout gridBagLayout = new GridBagLayout();
            jPanel.setLayout(gridBagLayout);
            GridBagConstraints gridBagConstraints = AbstractJAPConfModule.createTabbedRootPanelContraints();
            if (JAPModel.getDefaultView() != 2) {
                this.m_tabsAnon.addTab(jAPConfAnon.getTabTitle(), jAPConfAnon.getRootPanel());
                this.m_tabbedModules.addElement(jAPConfAnon);
                if (JAPController.getInstance().isTorMixminionAllowed()) {
                    this.m_tabsAnon.addTab(jAPConfTor.getTabTitle(), jAPConfTor.getRootPanel());
                    this.m_tabbedModules.addElement(jAPConfTor);
                    this.m_tabsAnon.addTab(jAPConfMixminion.getTabTitle(), jAPConfMixminion.getRootPanel());
                    this.m_tabbedModules.addElement(jAPConfMixminion);
                }
                this.m_tabsAnon.addTab(jAPConfAnonGeneral.getTabTitle(), jAPConfAnonGeneral.getRootPanel());
                this.m_tabbedModules.addElement(jAPConfAnonGeneral);
                jPanel.add((Component)this.m_tabsAnon, gridBagConstraints);
            } else {
                gridBagConstraints.weightx = 0.0;
                gridBagConstraints.weighty = 0.0;
                jPanel.add((Component)jAPConfAnon.getRootPanel(), gridBagConstraints);
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                jPanel.add((Component)new JLabel(), gridBagConstraints);
            }
        }
    }

    public String getTabTitle() {
        return JAPMessages.getString("ngAnonymitaet");
    }

    protected boolean onOkPressed() {
        boolean bl = this.m_anonModule.okPressed();
        boolean bl2 = this.m_torModule.okPressed();
        boolean bl3 = this.m_mixminionModule.okPressed();
        boolean bl4 = this.m_anonGeneralModule.okPressed();
        boolean bl5 = this.m_tcModule.okPressed();
        return bl && bl2 && bl3 && bl4 && bl5;
    }

    protected void onCancelPressed() {
        this.m_anonModule.cancelPressed();
        this.m_torModule.cancelPressed();
        this.m_mixminionModule.cancelPressed();
        this.m_anonGeneralModule.cancelPressed();
        this.m_tcModule.cancelPressed();
    }

    public String getHelpContext() {
        if (JAPModel.getDefaultView() != 2) {
            AbstractJAPConfModule abstractJAPConfModule = (AbstractJAPConfModule)this.m_tabbedModules.elementAt(this.m_tabsAnon.getSelectedIndex());
            return abstractJAPConfModule.getHelpContext();
        }
        return this.m_anonModule.getHelpContext();
    }

    protected void onRootPanelShown() {
        if (JAPModel.getDefaultView() != 2) {
            ((AbstractJAPConfModule)this.m_tabbedModules.elementAt(this.m_tabsAnon.getSelectedIndex())).onRootPanelShown();
        } else {
            this.m_anonModule.onRootPanelShown();
        }
    }

    protected void onResetToDefaultsPressed() {
        this.m_anonModule.resetToDefaultsPressed();
        this.m_torModule.resetToDefaultsPressed();
        this.m_mixminionModule.resetToDefaultsPressed();
        this.m_anonGeneralModule.resetToDefaultsPressed();
        this.m_tcModule.resetToDefaultsPressed();
    }

    protected void onUpdateValues() {
        this.m_anonModule.updateValues(false);
        this.m_torModule.updateValues(false);
        this.m_mixminionModule.updateValues(false);
        this.m_anonGeneralModule.updateValues(false);
        this.m_tcModule.updateValues(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JAPConfAnon getAnonModule() {
        JAPConfServices jAPConfServices = this;
        synchronized (jAPConfServices) {
            if (this.m_anonModule == null) {
                this.m_anonModule = new JAPConfAnon(null);
            }
        }
        return this.m_anonModule;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JAPConfTor getTorModule() {
        JAPConfServices jAPConfServices = this;
        synchronized (jAPConfServices) {
            if (this.m_torModule == null) {
                this.m_torModule = new JAPConfTor();
            }
        }
        return this.m_torModule;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JAPConfMixminion getMixminionModule() {
        JAPConfServices jAPConfServices = this;
        synchronized (jAPConfServices) {
            if (this.m_mixminionModule == null) {
                this.m_mixminionModule = new JAPConfMixminion();
            }
        }
        return this.m_mixminionModule;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JAPConfAnonGeneral getAnonGeneralModule() {
        JAPConfServices jAPConfServices = this;
        synchronized (jAPConfServices) {
            if (this.m_anonGeneralModule == null) {
                this.m_anonGeneralModule = new JAPConfAnonGeneral(null);
            }
        }
        return this.m_anonGeneralModule;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JAPConfTC getTCModule() {
        JAPConfServices jAPConfServices = this;
        synchronized (jAPConfServices) {
            if (this.m_tcModule == null) {
                this.m_tcModule = new JAPConfTC(null);
            }
        }
        return this.m_tcModule;
    }

    public synchronized void selectAnonTab(MixCascade mixCascade, boolean bl, boolean bl2) {
        if (JAPModel.getDefaultView() != 2) {
            if (bl2) {
                this.m_tabsAnon.setSelectedIndex(this.m_tabsAnon.getTabCount() - 1);
            } else {
                this.m_tabsAnon.setSelectedIndex(0);
            }
        }
        this.m_anonModule.setSelectedCascade(mixCascade);
        if (bl) {
            this.m_anonModule.showFilter();
        }
    }
}

