/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractIDEntry;
import anon.infoservice.InfoServiceDBEntry;

public class InfoServiceIDEntry
extends AbstractIDEntry {
    private static final long EXPIRE_TIME = 43200000L;

    public InfoServiceIDEntry(InfoServiceDBEntry infoServiceDBEntry) {
        super(infoServiceDBEntry, infoServiceDBEntry.getLastUpdate() + 43200000L);
    }
}

