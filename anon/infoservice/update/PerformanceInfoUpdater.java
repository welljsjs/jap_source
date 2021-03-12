/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.InfoServiceHolder;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Hashtable;

public class PerformanceInfoUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL = 600000L;
    private static final long MIN_UPDATE_INTERVAL_MS = 300000L;
    static /* synthetic */ Class class$anon$infoservice$PerformanceInfo;

    public PerformanceInfoUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(600000L), observableInfo);
    }

    public PerformanceInfoUpdater(long l, Updater.ObservableInfo observableInfo) {
        super(l, observableInfo);
    }

    protected Hashtable getEntrySerials() {
        return new Hashtable();
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable hashtable2 = InfoServiceHolder.getInstance().getPerformanceInfos();
        if (this.getUpdateInterval() instanceof Updater.DynamicUpdateInterval) {
            if (hashtable2 == null) {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(300000L);
            } else {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(600000L);
            }
        }
        return hashtable2;
    }

    public Class getUpdatedClass() {
        return class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = PerformanceInfoUpdater.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo;
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

