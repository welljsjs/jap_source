/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.InfoServiceHolder;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Enumeration;
import java.util.Hashtable;

public class TermsAndConditionsUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL_MS = 3600000L;
    private static final long UPDATE_INTERVAL_MS_SHORT = 600000L;
    static /* synthetic */ Class class$anon$terms$TermsAndConditions;

    public TermsAndConditionsUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(600000L), observableInfo);
    }

    public Class getUpdatedClass() {
        return class$anon$terms$TermsAndConditions == null ? (class$anon$terms$TermsAndConditions = TermsAndConditionsUpdater.class$("anon.terms.TermsAndConditions")) : class$anon$terms$TermsAndConditions;
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        Hashtable hashtable2 = InfoServiceHolder.getInstance().getTermsAndConditions();
        if (hashtable2 == null) {
            ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(600000L);
            return new Hashtable();
        }
        ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(3600000L);
        Enumeration enumeration = hashtable2.elements();
        return hashtable2;
    }

    protected Hashtable getEntrySerials() {
        Hashtable hashtable = InfoServiceHolder.getInstance().getMessageSerials();
        if (hashtable == null) {
            ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(600000L);
            return new Hashtable();
        }
        ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(3600000L);
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

