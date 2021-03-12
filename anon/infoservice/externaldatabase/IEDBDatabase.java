/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.externaldatabase;

import anon.infoservice.externaldatabase.EDBException;
import java.util.Vector;

public interface IEDBDatabase {
    public void testDB() throws Exception;

    public void insert(String var1, String var2, String var3) throws EDBException;

    public Vector getAllTypes() throws EDBException;

    public Vector getAllValuesOfType(String var1) throws EDBException;

    public void removeType(String var1) throws EDBException;

    public void remove(String var1, String var2) throws EDBException;

    public void doVacuum() throws EDBException;
}

