/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.util;

import anon.infoservice.AbstractDatabaseEntry;

public class DNSCacheEntry
extends AbstractDatabaseEntry {
    private String m_Id;
    private String m_Ip;
    private long m_lastUpdate = System.currentTimeMillis();

    public DNSCacheEntry(String string, String string2, long l) {
        super(l);
        this.m_Id = string;
        this.m_Ip = string2;
    }

    public String getId() {
        return this.m_Id;
    }

    public String getIp() {
        return this.m_Ip;
    }

    public long getVersionNumber() {
        return this.getExpireTime();
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }
}

