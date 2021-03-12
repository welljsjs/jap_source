/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.externaldatabase.jdbc;

import anon.infoservice.externaldatabase.IEDBConfiguration;
import anon.infoservice.externaldatabase.IEDBDatabase;
import anon.infoservice.externaldatabase.jdbc.EDBDatabaseJDBC;

public class EDBConfigurationJDBC
implements IEDBConfiguration {
    private String m_dbURL;
    private String m_strDriver;

    public EDBConfigurationJDBC(String string, String string2) {
        this.m_dbURL = string2;
        this.m_strDriver = string;
    }

    public IEDBDatabase getEDBDatabaseInstance() {
        return new EDBDatabaseJDBC(this);
    }

    public String getDatabaseURL() {
        return this.m_dbURL;
    }

    public String getDriverClassName() {
        return this.m_strDriver;
    }
}

