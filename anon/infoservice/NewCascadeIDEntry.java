/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractCascadeIDEntry;
import anon.infoservice.CascadeIDEntry;

public class NewCascadeIDEntry
extends AbstractCascadeIDEntry {
    private static final long EXPIRE_TIME = 43200000L;

    public NewCascadeIDEntry(CascadeIDEntry cascadeIDEntry) {
        super(cascadeIDEntry, System.currentTimeMillis() + 43200000L);
    }
}

