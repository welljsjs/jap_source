/*
 * Decompiled with CFR 0.150.
 */
package jap;

import jap.IJAPConfSavePoint;
import jap.JAPModel;

public class JAPConfNetworkSavePoint
implements IJAPConfSavePoint {
    private boolean m_connectViaForwarder;
    private boolean m_forwardInfoService;

    public void createSavePoint() {
        this.m_connectViaForwarder = JAPModel.getInstance().getRoutingSettings().isConnectViaForwarder();
        this.m_forwardInfoService = JAPModel.getInstance().getRoutingSettings().getForwardInfoService();
    }

    public void restoreSavePoint() {
        JAPModel.getInstance().getRoutingSettings().setConnectViaForwarder(this.m_connectViaForwarder);
        JAPModel.getInstance().getRoutingSettings().setForwardInfoService(this.m_forwardInfoService);
    }

    public void restoreDefaults() {
        JAPModel.getInstance().getRoutingSettings().setConnectViaForwarder(false);
        JAPModel.getInstance().getRoutingSettings().setForwardInfoService(false);
    }
}

