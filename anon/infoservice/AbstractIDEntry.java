/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDatabaseEntry;

public abstract class AbstractIDEntry
extends AbstractDatabaseEntry {
    private long m_lastUpdate;
    private long m_versionNumber;
    private String m_id;

    public AbstractIDEntry(AbstractDatabaseEntry abstractDatabaseEntry, long l) {
        super(l);
        if (abstractDatabaseEntry == null) {
            throw new IllegalArgumentException("No database entry given.");
        }
        this.m_lastUpdate = abstractDatabaseEntry.getLastUpdate();
        this.m_versionNumber = abstractDatabaseEntry.getVersionNumber();
        this.m_id = abstractDatabaseEntry.getId();
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.m_versionNumber;
    }

    public String getId() {
        return this.m_id;
    }
}

