/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.MixCascade;
import anon.infoservice.update.AbstractMixCascadeUpdater;
import anon.util.Updater;
import jap.JAPController;

public class MixCascadeUpdater
extends AbstractMixCascadeUpdater {
    public MixCascadeUpdater(Updater.ObservableInfo observableInfo) {
        super(observableInfo);
    }

    public MixCascadeUpdater(long l, boolean bl, Updater.ObservableInfo observableInfo) {
        super(l, bl, observableInfo);
    }

    protected AbstractDatabaseEntry getPreferredEntry() {
        return JAPController.getInstance().getCurrentMixCascade();
    }

    protected void setPreferredEntry(AbstractDatabaseEntry abstractDatabaseEntry) {
        if (abstractDatabaseEntry instanceof MixCascade) {
            JAPController.getInstance().setCurrentMixCascade((MixCascade)abstractDatabaseEntry);
        }
    }
}

