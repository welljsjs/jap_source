/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.InfoServiceHolder;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Hashtable;

public class JavaVersionUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL_MS = 43200000L;
    private static final long UPDATE_INTERVAL_MS_SHORT = 600000L;
    static /* synthetic */ Class class$anon$infoservice$JavaVersionDBEntry;

    public JavaVersionUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(600000L), observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$infoservice$JavaVersionDBEntry == null ? (class$anon$infoservice$JavaVersionDBEntry = JavaVersionUpdater.class$("anon.infoservice.JavaVersionDBEntry")) : class$anon$infoservice$JavaVersionDBEntry;
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable hashtable2 = InfoServiceHolder.getInstance().getLatestJavaVersions();
        if (hashtable2 == null) {
            ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(600000L);
            return new Hashtable();
        }
        ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(43200000L);
        return hashtable2;
    }

    protected Hashtable getEntrySerials() {
        Hashtable hashtable = InfoServiceHolder.getInstance().getLatestJavaVersionSerials();
        if (hashtable == null) {
            ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(600000L);
            return new Hashtable();
        }
        ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(43200000L);
        return hashtable;
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

