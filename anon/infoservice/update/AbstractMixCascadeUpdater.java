/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.client.TrustModel;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.infoservice.StatusInfo;
import anon.infoservice.update.AbstractDatabaseUpdater;
import anon.util.Updater;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractMixCascadeUpdater
extends AbstractDatabaseUpdater {
    private static final long UPDATE_INTERVAL_MS = 600000L;
    private static final long MIN_UPDATE_INTERVAL_MS = 300000L;
    private boolean m_bDoMixInfoCleanup = true;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$MixInfo;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;

    public AbstractMixCascadeUpdater(Updater.ObservableInfo observableInfo) {
        super(new Updater.DynamicUpdateInterval(600000L), observableInfo);
    }

    public AbstractMixCascadeUpdater(long l, boolean bl, Updater.ObservableInfo observableInfo) {
        super(l, observableInfo);
        this.m_bDoMixInfoCleanup = bl;
    }

    protected abstract AbstractDatabaseEntry getPreferredEntry();

    protected abstract void setPreferredEntry(AbstractDatabaseEntry var1);

    public final Class getUpdatedClass() {
        return class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractMixCascadeUpdater.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade;
    }

    protected final boolean doCleanup(Hashtable hashtable) {
        boolean bl = super.doCleanup(hashtable);
        MixCascade mixCascade = (MixCascade)this.getPreferredEntry();
        if (this.m_bDoMixInfoCleanup) {
            LogHolder.log(7, LogType.MISC, "Do MixInfo database cleanup.");
            Vector vector = Database.getInstance(class$anon$infoservice$MixInfo == null ? (class$anon$infoservice$MixInfo = AbstractMixCascadeUpdater.class$("anon.infoservice.MixInfo")) : class$anon$infoservice$MixInfo).getEntryList();
            Vector vector2 = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractMixCascadeUpdater.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
            if (mixCascade != null) {
                vector2.addElement(mixCascade);
            }
            block0: for (int i = 0; i < vector.size(); ++i) {
                MixInfo mixInfo = (MixInfo)vector.elementAt(i);
                if (Database.getInstance(class$anon$infoservice$MixCascade == null ? AbstractMixCascadeUpdater.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryById(mixInfo.getId()) != null || mixCascade != null && mixCascade.getMixId(0).equals(mixInfo.getId())) continue;
                for (int j = 0; j < vector2.size(); ++j) {
                    Vector vector3 = ((MixCascade)vector2.elementAt(j)).getMixIds();
                    for (int k = 1; k < vector3.size(); ++k) {
                        if (vector3.elementAt(k).equals(mixInfo.getId())) continue block0;
                    }
                }
                Database.getInstance(class$anon$infoservice$MixInfo == null ? AbstractMixCascadeUpdater.class$("anon.infoservice.MixInfo") : class$anon$infoservice$MixInfo).remove(mixInfo);
                LogHolder.log(5, LogType.MISC, "Cleaned MixInfo DB entry: " + mixInfo.getId());
            }
        }
        return bl;
    }

    protected final Hashtable getEntrySerials() {
        Hashtable hashtable = InfoServiceHolder.getInstance().getMixCascadeSerials(TrustModel.getContext());
        if (this.getUpdateInterval() instanceof Updater.DynamicUpdateInterval) {
            if (hashtable == null) {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(300000L);
            } else {
                ((Updater.DynamicUpdateInterval)this.getUpdateInterval()).setUpdateInterval(600000L);
            }
        }
        return hashtable;
    }

    protected Hashtable getUpdatedEntries(Hashtable hashtable) {
        MixCascade mixCascade;
        Hashtable hashtable2 = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractMixCascadeUpdater.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryHash();
        Enumeration enumeration = hashtable2.elements();
        Hashtable hashtable3 = new Hashtable();
        while (enumeration.hasMoreElements()) {
            mixCascade = (MixCascade)enumeration.nextElement();
            if (!this.fetchCurrentStatus(mixCascade)) continue;
            hashtable3.put(mixCascade.getId(), mixCascade);
        }
        hashtable2 = hashtable3;
        hashtable3 = this.getUpdatedEntries_internal(hashtable);
        if (hashtable3 != null) {
            enumeration = hashtable3.elements();
            while (enumeration.hasMoreElements()) {
                mixCascade = (MixCascade)enumeration.nextElement();
                if (hashtable2.contains(mixCascade)) continue;
                this.fetchCurrentStatus(mixCascade);
            }
        }
        return hashtable3;
    }

    private final boolean fetchCurrentStatus(MixCascade mixCascade) {
        StatusInfo statusInfo = null;
        if (!mixCascade.isUserDefined()) {
            statusInfo = mixCascade.fetchCurrentStatus(1800000L);
            return Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = AbstractMixCascadeUpdater.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).update(statusInfo);
        }
        return statusInfo != null;
    }

    protected final Hashtable getUpdatedEntries_internal(Hashtable hashtable) {
        MixCascade mixCascade;
        Enumeration<Object> enumeration;
        Hashtable<String, MixCascade> hashtable2;
        Hashtable hashtable3;
        if (hashtable == null) {
            hashtable3 = InfoServiceHolder.getInstance().getMixCascades(TrustModel.getContext());
        } else if (hashtable.size() == 0) {
            hashtable3 = new Hashtable();
        } else {
            hashtable2 = new Hashtable<String, MixCascade>(hashtable.size());
            enumeration = hashtable.keys();
            while (enumeration.hasMoreElements()) {
                mixCascade = InfoServiceHolder.getInstance().getMixCascadeInfo((String)enumeration.nextElement());
                if (mixCascade == null) continue;
                hashtable2.put(mixCascade.getId(), mixCascade);
            }
            hashtable3 = hashtable2;
        }
        if (!TrustModel.isFreeAllowed()) {
            hashtable2 = new Hashtable();
            enumeration = hashtable3.elements();
            while (enumeration.hasMoreElements()) {
                mixCascade = (MixCascade)enumeration.nextElement();
                if (!mixCascade.isPayment()) continue;
                hashtable2.put(mixCascade.getId(), mixCascade);
            }
            hashtable3 = hashtable2;
        }
        return hashtable3;
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

