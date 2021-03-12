/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.externaldatabase.jdbc;

import anon.infoservice.externaldatabase.EDBException;
import anon.infoservice.externaldatabase.IEDBDatabase;
import anon.infoservice.externaldatabase.jdbc.EDBConfigurationJDBC;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class EDBDatabaseJDBC
implements IEDBDatabase {
    private EDBConfigurationJDBC m_dbConf;
    private Object SYNC_EXTERNAL_DATABASE;

    public EDBDatabaseJDBC(EDBConfigurationJDBC eDBConfigurationJDBC) {
        this.m_dbConf = eDBConfigurationJDBC;
        this.SYNC_EXTERNAL_DATABASE = new Object();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void doVacuum() throws EDBException {
        object = this.SYNC_EXTERNAL_DATABASE;
        // MONITORENTER : object
        if (this.m_dbConf == null || this.m_dbConf.getDatabaseURL() == null) {
            // MONITOREXIT : object
            return;
        }
        connection = null;
        try {
            try {
                connection = DriverManager.getConnection(this.m_dbConf.getDatabaseURL());
                connection.setAutoCommit(true);
                statement = connection.createStatement();
                statement.executeUpdate("VACUUM;");
            }
            catch (Exception exception) {
                throw new EDBException("Exception in doVacuum(): " + exception.getMessage());
            }
            var5_5 = null;
            connection.close();
            return;
            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
                return;
            }
        }
        catch (Throwable throwable) {
            var5_6 = null;
            ** try [egrp 3[TRYBLOCK] [3 : 110->119)] { 
lbl28:
            // 1 sources

            connection.close();
            throw throwable;
lbl30:
            // 1 sources

            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
            }
            throw throwable;
        }
    }

    public void testDB() throws Exception {
        Class.forName(this.m_dbConf.getDriverClassName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void insert(String string, String string2, String string3) throws EDBException {
        object = this.SYNC_EXTERNAL_DATABASE;
        // MONITORENTER : object
        connection = null;
        try {
            try {
                connection = DriverManager.getConnection(this.m_dbConf.getDatabaseURL());
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                string4 = string;
                statement.executeUpdate("create table if not exists " + string4 + " (id text PRIMARY KEY, xml text);");
                statement.execute("DELETE FROM " + string4 + " where id = '" + string2 + "';");
                statement.executeUpdate("INSERT INTO " + string4 + " values ('" + string2 + "', " + "'" + string3 + "');");
                connection.commit();
                connection.setAutoCommit(true);
            }
            catch (SQLException sQLException) {
                throw new EDBException("EXception in insert(): " + sQLException.getMessage());
            }
            var9_9 = null;
            if (connection == null) return;
            connection.close();
            return;
            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
                return;
            }
        }
        catch (Throwable throwable) {
            var9_10 = null;
            ** try [egrp 3[TRYBLOCK] [3 : 238->253)] { 
lbl33:
            // 1 sources

            if (connection == null) throw throwable;
            connection.close();
            throw throwable;
lbl36:
            // 1 sources

            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public Vector getAllTypes() throws EDBException {
        object = this.SYNC_EXTERNAL_DATABASE;
        // MONITORENTER : object
        connection = null;
        vector = new Vector<String>();
        try {
            try {
                connection = DriverManager.getConnection(this.m_dbConf.getDatabaseURL());
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("select name from sqlite_master where type = 'table';");
                while (resultSet.next()) {
                    vector.addElement(resultSet.getString("name"));
                }
                resultSet.close();
            }
            catch (SQLException sQLException) {
                throw new EDBException("Exception in getAllTypes(): " + sQLException.getMessage());
            }
            var7_7 = null;
            if (connection == null) return vector;
            connection.close();
            return vector;
            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
                return vector;
            }
        }
        catch (Throwable throwable) {
            var7_8 = null;
            ** try [egrp 3[TRYBLOCK] [3 : 136->149)] { 
lbl30:
            // 1 sources

            if (connection == null) throw throwable;
            connection.close();
            throw throwable;
lbl33:
            // 1 sources

            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public Vector getAllValuesOfType(String string) throws EDBException {
        object = this.SYNC_EXTERNAL_DATABASE;
        // MONITORENTER : object
        connection = null;
        vector = new Vector<String>();
        try {
            try {
                connection = DriverManager.getConnection(this.m_dbConf.getDatabaseURL());
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("select xml from " + string + ";");
                while (resultSet.next()) {
                    vector.addElement(resultSet.getString("xml"));
                }
                resultSet.close();
            }
            catch (SQLException sQLException) {
                throw new EDBException("Exception in getAllTypes(): " + sQLException.getMessage());
            }
            var8_8 = null;
            if (connection == null) return vector;
            connection.close();
            return vector;
            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
                return vector;
            }
        }
        catch (Throwable throwable) {
            var8_9 = null;
            ** try [egrp 3[TRYBLOCK] [3 : 160->173)] { 
lbl30:
            // 1 sources

            if (connection == null) throw throwable;
            connection.close();
            throw throwable;
lbl33:
            // 1 sources

            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void removeType(String string) throws EDBException {
        object = this.SYNC_EXTERNAL_DATABASE;
        // MONITORENTER : object
        connection = null;
        try {
            try {
                connection = DriverManager.getConnection(this.m_dbConf.getDatabaseURL());
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.executeUpdate("drop table " + string + ";");
                connection.commit();
                connection.setAutoCommit(true);
            }
            catch (SQLException sQLException) {
                throw new EDBException("Exception in removeType(): " + sQLException.getMessage());
            }
            var6_6 = null;
            if (connection == null) return;
            connection.close();
            return;
            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
                return;
            }
        }
        catch (Throwable throwable) {
            var6_7 = null;
            ** try [egrp 3[TRYBLOCK] [3 : 129->142)] { 
lbl28:
            // 1 sources

            if (connection == null) throw throwable;
            connection.close();
            throw throwable;
lbl31:
            // 1 sources

            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void remove(String string, String string2) throws EDBException {
        object = this.SYNC_EXTERNAL_DATABASE;
        // MONITORENTER : object
        connection = null;
        try {
            try {
                connection = DriverManager.getConnection(this.m_dbConf.getDatabaseURL());
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.executeUpdate("delete from " + string + " where id = ('" + string2 + "');");
                connection.commit();
                connection.setAutoCommit(true);
            }
            catch (SQLException sQLException) {
                throw new EDBException("EXception in insert(): " + sQLException.getMessage());
            }
            var7_7 = null;
            if (connection == null) return;
            connection.close();
            return;
            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
                return;
            }
        }
        catch (Throwable throwable) {
            var7_8 = null;
            ** try [egrp 3[TRYBLOCK] [3 : 144->159)] { 
lbl28:
            // 1 sources

            if (connection == null) throw throwable;
            connection.close();
            throw throwable;
lbl31:
            // 1 sources

            catch (SQLException sQLException) {
                LogHolder.log(2, LogType.DB, "Could not close database!", sQLException);
            }
            throw throwable;
        }
    }
}

