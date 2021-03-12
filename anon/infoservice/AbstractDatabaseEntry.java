/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

public abstract class AbstractDatabaseEntry {
    public static final String XML_LAST_UPDATE = "LastUpdate";
    public static final String XML_ATTR_LAST_UPDATE = "lastUpdate";
    private long m_expireTime;
    private long m_creationTime;

    public AbstractDatabaseEntry(long l) {
        this.m_expireTime = l;
        this.m_creationTime = System.currentTimeMillis();
    }

    public final boolean isNewerThan(AbstractDatabaseEntry abstractDatabaseEntry) {
        return abstractDatabaseEntry == null || this.getVersionNumber() > abstractDatabaseEntry.getVersionNumber() || (this.getLastUpdate() > abstractDatabaseEntry.getLastUpdate() || this.getLastUpdate() == 0L && abstractDatabaseEntry.getLastUpdate() == 0L) && this.getVersionNumber() == abstractDatabaseEntry.getVersionNumber();
    }

    public boolean isUserDefined() {
        return false;
    }

    public boolean isPersistanceDeletionAllowed() {
        return false;
    }

    public void deletePersistence() {
    }

    public abstract String getId();

    public final long getExpireTime() {
        return this.m_expireTime;
    }

    public abstract long getLastUpdate();

    public final long getCreationTime() {
        return this.m_creationTime;
    }

    public final void resetCreationTime() {
        this.m_creationTime = System.currentTimeMillis();
    }

    public abstract long getVersionNumber();
}

