/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.InfoServiceHolder;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Hashtable;

public class ServiceExitAddressUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL_MS = 1200000L;
    private static final long MIN_UPDATE_INTERVAL_MS = 60000L;
    static /* synthetic */ Class class$anon$infoservice$MixCascadeExitAddresses;

    public ServiceExitAddressUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(1200000L), observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = ServiceExitAddressUpdater.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses;
    }

    protected boolean doCleanup(Hashtable hashtable) {
        return false;
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable hashtable2 = InfoServiceHolder.getInstance().updateExitAddresses();
        if (this.getUpdateInterval() instanceof Updater.DynamicUpdateInterval) {
            if (hashtable2 == null || hashtable2.size() <= 1) {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(60000L);
            } else {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(1200000L);
            }
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

