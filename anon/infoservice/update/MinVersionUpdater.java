/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.InfoServiceHolder;
import anon.infoservice.JAPMinVersion;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Hashtable;

public class MinVersionUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL_MS = 43200000L;
    private static final long UPDATE_INTERVAL_MS_SHORT = 480000L;
    static /* synthetic */ Class class$anon$infoservice$JAPMinVersion;

    public MinVersionUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(480000L), observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$infoservice$JAPMinVersion == null ? (class$anon$infoservice$JAPMinVersion = MinVersionUpdater.class$("anon.infoservice.JAPMinVersion")) : class$anon$infoservice$JAPMinVersion;
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable<String, JAPMinVersion> hashtable2 = new Hashtable<String, JAPMinVersion>();
        JAPMinVersion jAPMinVersion = InfoServiceHolder.getInstance().getNewVersionNumber();
        if (jAPMinVersion != null) {
            ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(480000L);
            hashtable2.put(jAPMinVersion.getId(), jAPMinVersion);
        }
        ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(43200000L);
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

