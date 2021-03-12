/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.client.BasicTrustModel;
import anon.client.ITrustModel;
import anon.infoservice.AbstractMixCascadeContainer;
import anon.infoservice.MixCascade;

public class SimpleMixCascadeContainer
extends AbstractMixCascadeContainer {
    private MixCascade m_mixCascade;
    private boolean m_bAutoReConnect = false;

    public SimpleMixCascadeContainer(MixCascade mixCascade) {
        this.m_mixCascade = mixCascade;
    }

    public MixCascade getNextCascade() {
        return this.m_mixCascade;
    }

    public MixCascade getNextRandomCascade() {
        return this.m_mixCascade;
    }

    public MixCascade getCurrentCascade() {
        return this.m_mixCascade;
    }

    public boolean isServiceAutoSwitched() {
        return false;
    }

    public void setAutoReConnect(boolean bl) {
        this.m_bAutoReConnect = bl;
    }

    public boolean isReconnectedAutomatically() {
        return this.m_bAutoReConnect;
    }

    public void keepCurrentService(boolean bl) {
    }

    public ITrustModel getTrustModel() {
        return new BasicTrustModel();
    }
}

