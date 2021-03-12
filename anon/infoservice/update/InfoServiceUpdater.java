/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Hashtable;

public class InfoServiceUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL_MS = 1200000L;
    private static final long MIN_UPDATE_INTERVAL_MS = 60000L;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public InfoServiceUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(1200000L), observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceUpdater.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry;
    }

    protected AbstractDatabaseEntry getPreferredEntry() {
        return InfoServiceHolder.getInstance().getPreferredInfoService();
    }

    protected void setPreferredEntry(AbstractDatabaseEntry abstractDatabaseEntry) {
        if (abstractDatabaseEntry instanceof InfoServiceDBEntry) {
            InfoServiceHolder.getInstance().setPreferredInfoService((InfoServiceDBEntry)abstractDatabaseEntry);
        }
    }

    protected Hashtable getEntrySerials() {
        return InfoServiceHolder.getInstance().getInfoServiceSerials();
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable hashtable2 = InfoServiceHolder.getInstance().getInfoServices();
        if (this.getUpdateInterval() instanceof Updater.DynamicUpdateInterval) {
            if (hashtable2 == null || hashtable2.size() <= 1) {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(60000L);
            } else {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(1200000L);
            }
        }
        return hashtable2;
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

