/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ServerSocketPropagandist;

public class JAPRoutingSettingsPropagandaThreadLock {
    private boolean m_propagandaThreadReady = false;
    private int m_registrationStatus = 1;

    public void propagandaThreadIsReady() {
        this.m_propagandaThreadReady = true;
    }

    public boolean isPropagandaThreadReady() {
        return this.m_propagandaThreadReady;
    }

    public void updateRegistrationStatus(ServerSocketPropagandist serverSocketPropagandist) {
        if (this.m_registrationStatus == 1) {
            this.m_registrationStatus = 2;
        }
        if (serverSocketPropagandist.getCurrentState() == 0) {
            this.m_registrationStatus = 0;
        } else {
            if (serverSocketPropagandist.getCurrentErrorCode() == 2 && this.m_registrationStatus == 2) {
                this.m_registrationStatus = 3;
            }
            if (serverSocketPropagandist.getCurrentErrorCode() == 1 && (this.m_registrationStatus == 2 || this.m_registrationStatus == 3)) {
                this.m_registrationStatus = 4;
            }
        }
    }

    public void registrationWasInterrupted() {
        this.m_registrationStatus = 5;
    }

    public int getRegistrationStatus() {
        return this.m_registrationStatus;
    }
}

