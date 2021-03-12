/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.pay.PayAccountsFile;
import anon.util.Updater;
import java.util.Hashtable;

public class PaymentInstanceUpdater
extends AbstractDatabaseUpdater {
    public static final long UPDATE_INTERVAL_MS = 900000L;
    private static final long MIN_UPDATE_INTERVAL_MS = 60000L;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;

    public PaymentInstanceUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(900000L), observableInfo);
    }

    public PaymentInstanceUpdater(long l, Updater.ObservableInfo observableInfo) {
        super(l, observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = PaymentInstanceUpdater.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry;
    }

    protected Hashtable getEntrySerials() {
        return new Hashtable();
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable hashtable2 = InfoServiceHolder.getInstance().getPaymentInstances();
        if (this.getUpdateInterval() instanceof Updater.DynamicUpdateInterval) {
            if (hashtable2 == null) {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(60000L);
            } else {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(900000L);
            }
        }
        if (hashtable2 != null && hashtable2.size() == 0) {
            hashtable2 = null;
        }
        return hashtable2;
    }

    protected boolean protectFromCleanup(AbstractDatabaseEntry abstractDatabaseEntry) {
        return PayAccountsFile.getInstance().getAccounts(abstractDatabaseEntry.getId()).size() > 0;
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

