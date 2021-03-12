/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Database;
import anon.util.Updater;
import java.util.Enumeration;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractDatabaseUpdater
extends Updater {
    public static final long KEEP_ENTRY_FACTOR = 3L;
    private boolean m_successfulUpdate = false;
    private long m_lLastUpdate = Long.MAX_VALUE;

    public AbstractDatabaseUpdater(long l, Updater.ObservableInfo observableInfo) {
        this(new Updater.ConstantUpdateInterval(l), observableInfo);
    }

    public AbstractDatabaseUpdater(Updater.IUpdateInterval iUpdateInterval, Updater.ObservableInfo observableInfo) {
        super(iUpdateInterval, observableInfo);
    }

    protected void updateInternal() {
        String string;
        Hashtable<String, AbstractDatabaseEntry> hashtable;
        Hashtable hashtable2;
        Hashtable hashtable3 = this.getEntrySerials();
        if (Thread.currentThread().isInterrupted()) {
            this.m_successfulUpdate = true;
            return;
        }
        if (hashtable3 == null) {
            LogHolder.log(3, LogType.MISC, this.getUpdatedClassName() + "update failed!");
            this.m_successfulUpdate = false;
            return;
        }
        if (hashtable3.size() > 0) {
            hashtable2 = new Hashtable(hashtable3.size());
            hashtable = new Hashtable(hashtable3.size());
        } else {
            hashtable2 = new Hashtable();
            hashtable = new Hashtable<String, AbstractDatabaseEntry>();
        }
        Enumeration enumeration = hashtable3.keys();
        while (enumeration.hasMoreElements()) {
            string = (String)enumeration.nextElement();
            AbstractDatabaseEntry abstractDatabaseEntry = Database.getInstance(this.getUpdatedClass()).getEntryById(string);
            if (abstractDatabaseEntry != null && ((AbstractDatabaseEntry)hashtable3.get(string)).getVersionNumber() == abstractDatabaseEntry.getVersionNumber()) {
                abstractDatabaseEntry.resetCreationTime();
                hashtable.put(abstractDatabaseEntry.getId(), abstractDatabaseEntry);
                continue;
            }
            hashtable2.put(string, hashtable3.get(string));
        }
        if (Thread.currentThread().isInterrupted()) {
            this.m_successfulUpdate = true;
            return;
        }
        Hashtable hashtable4 = hashtable.size() == 0 && hashtable3.size() == 0 ? this.getUpdatedEntries(null) : this.getUpdatedEntries(hashtable2);
        if (hashtable4 != null) {
            Enumeration enumeration2 = hashtable.keys();
            while (enumeration2.hasMoreElements()) {
                string = (String)enumeration2.nextElement();
                hashtable4.put(string, hashtable.get(string));
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            this.m_successfulUpdate = true;
        } else if (hashtable4 == null) {
            LogHolder.log(3, LogType.MISC, this.getUpdatedClassName() + "update failed!");
            this.m_successfulUpdate = false;
        } else {
            LogHolder.log(7, LogType.MISC, this.getUpdatedClassName() + "update was successful.");
            boolean bl = false;
            this.m_lLastUpdate = System.currentTimeMillis();
            this.m_successfulUpdate = true;
            Enumeration enumeration3 = hashtable4.elements();
            while (enumeration3.hasMoreElements()) {
                AbstractDatabaseEntry abstractDatabaseEntry;
                AbstractDatabaseEntry abstractDatabaseEntry2 = (AbstractDatabaseEntry)enumeration3.nextElement();
                if (Database.getInstance(this.getUpdatedClass()).update(abstractDatabaseEntry2)) {
                    bl = true;
                }
                if ((abstractDatabaseEntry = this.getPreferredEntry()) == null || !abstractDatabaseEntry.equals(abstractDatabaseEntry2)) continue;
                this.setPreferredEntry(abstractDatabaseEntry2);
            }
            if (this.doCleanup(hashtable4) || bl) {
                this.getObservableInfo().notifyAdditionalObserversOnUpdate(this.getUpdatedClass());
            }
        }
    }

    protected boolean wasUpdateSuccessful() {
        return this.m_successfulUpdate;
    }

    protected boolean doCleanup(Hashtable hashtable) {
        boolean bl = false;
        Enumeration enumeration = Database.getInstance(this.getUpdatedClass()).getEntryList().elements();
        while (enumeration.hasMoreElements()) {
            AbstractDatabaseEntry abstractDatabaseEntry = (AbstractDatabaseEntry)enumeration.nextElement();
            if (this.protectFromCleanup(abstractDatabaseEntry) || abstractDatabaseEntry.isUserDefined() || hashtable.contains(abstractDatabaseEntry) || !Database.getInstance(this.getUpdatedClass()).remove(abstractDatabaseEntry)) continue;
            bl = true;
        }
        return bl;
    }

    public final long getLastUpdate() {
        return this.m_lLastUpdate;
    }

    protected boolean protectFromCleanup(AbstractDatabaseEntry abstractDatabaseEntry) {
        return false;
    }

    protected AbstractDatabaseEntry getPreferredEntry() {
        return null;
    }

    protected void setPreferredEntry(AbstractDatabaseEntry abstractDatabaseEntry) {
    }

    protected abstract Hashtable getEntrySerials();

    protected abstract Hashtable getUpdatedEntries(Hashtable var1);
}

