/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.StatusInfo;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import jap.JAPController;
import java.util.Hashtable;

final class JAPFeedback
extends AbstractDatabaseUpdater {
    public static final long UPDATE_INTERVAL_MS = 300000L;
    private static final long MIN_UPDATE_INTERVAL_MS = 180000L;
    private Updater.DynamicUpdateInterval m_updateInterval = (Updater.DynamicUpdateInterval)this.getUpdateInterval();
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;

    public JAPFeedback(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(300000L), observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = JAPFeedback.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo;
    }

    protected boolean doCleanup(Hashtable hashtable) {
        return false;
    }

    protected boolean isUpdatePaused() {
        return !JAPController.getInstance().getAnonMode() || JAPController.getInstance().getCurrentMixCascade().isUserDefined();
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        StatusInfo statusInfo = JAPController.getInstance().getCurrentMixCascade().fetchCurrentStatus();
        Hashtable<String, StatusInfo> hashtable2 = new Hashtable<String, StatusInfo>();
        if (statusInfo != null) {
            if (statusInfo.getExpireTime() <= System.currentTimeMillis() + 300000L) {
                this.m_updateInterval.setUpdateInterval(180000L);
            } else if ((double)statusInfo.getExpireTime() <= (double)System.currentTimeMillis() + 450000.0) {
                this.m_updateInterval.setUpdateInterval(Math.max(150000L, 180000L));
            } else {
                this.m_updateInterval.setUpdateInterval(300000L);
            }
            hashtable2.put(statusInfo.getId(), statusInfo);
        } else {
            this.m_updateInterval.setUpdateInterval(180000L);
        }
        return hashtable2;
    }

    protected Hashtable getEntrySerials() {
        return new Hashtable();
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

