/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import java.lang.reflect.Method;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;

public final class VMPerfDataFile {
    private Hashtable m_tblEntries;
    private Object m_buff;
    private Object m_perf;
    private boolean m_bUsable = false;
    private int m_nextEntry;
    private int m_numEntries;
    private static final Integer PERFDATA_MAGIC_POSITION = new Integer(0);
    private static final Integer PERFDATA_BYTEORDER_POSITION = new Integer(4);
    private static final Integer PERFDATA_ACCESSIBLE_POSITION = new Integer(7);
    private static final Integer PERFDATA_ENTRYOFFSET_POSITION = new Integer(24);
    private static final Integer PERFDATA_NUMENTRIES_POSITION = new Integer(28);
    private static final int PERFDATA_MAGIC = -889274176;
    private static final int PERFDATA_SYNC_TIMEOUT = 5000;
    private static Class m_javaNioByteBufferClass;
    private static Class m_javaNioByteOrderClass;
    private static Class m_sunMiscPerfClass;
    private static Method m_byteBufferPositionMethod;
    private static Method m_byteBufferGetMethod;
    private static Method m_byteBufferGetIntMethod;
    private int m_vmId;
    static /* synthetic */ Class class$java$lang$String;

    public VMPerfDataFile(int n) {
        this.m_vmId = n;
        try {
            m_javaNioByteBufferClass = Class.forName("java.nio.ByteBuffer");
            m_javaNioByteOrderClass = Class.forName("java.nio.ByteOrder");
            m_sunMiscPerfClass = Class.forName("sun.misc.Perf");
            m_byteBufferPositionMethod = m_javaNioByteBufferClass.getMethod("position", Integer.TYPE);
            m_byteBufferGetMethod = m_javaNioByteBufferClass.getMethod("get", null);
            m_byteBufferGetIntMethod = m_javaNioByteBufferClass.getMethod("getInt", null);
            this.m_perf = Class.forName("java.security.AccessController").getMethod("doPrivileged", Class.forName("java.security.PrivilegedAction")).invoke(null, Class.forName("sun.misc.Perf$GetPerfAction").newInstance());
            this.m_buff = m_sunMiscPerfClass.getMethod("attach", Integer.TYPE, class$java$lang$String == null ? (class$java$lang$String = VMPerfDataFile.class$("java.lang.String")) : class$java$lang$String).invoke(this.m_perf, new Integer(n), "r");
            if (this.m_buff == null) {
                return;
            }
            if (this.getMagic() != -889274176) {
                return;
            }
            m_javaNioByteBufferClass.getMethod("order", m_javaNioByteOrderClass).invoke(this.m_buff, this.getByteOrder());
            this.m_bUsable = this.buildEntries();
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, "Java VM < 1.4 found, can't use multiple-instances feature.");
        }
    }

    private synchronized boolean buildEntries() throws Exception {
        if (this.m_buff == null) {
            return false;
        }
        long l = System.currentTimeMillis() + 5000L;
        while (!this.isAccessible()) {
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (System.currentTimeMillis() <= l) continue;
            return false;
        }
        m_byteBufferPositionMethod.invoke(this.m_buff, PERFDATA_ENTRYOFFSET_POSITION);
        this.m_nextEntry = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        m_byteBufferPositionMethod.invoke(this.m_buff, PERFDATA_NUMENTRIES_POSITION);
        this.m_numEntries = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        this.m_tblEntries = new Hashtable();
        while (this.buildNextEntry()) {
        }
        return true;
    }

    private synchronized boolean buildNextEntry() throws Exception {
        byte by;
        if (this.m_buff == null) {
            return false;
        }
        if (this.m_nextEntry % 4 != 0) {
            return false;
        }
        if (this.m_nextEntry < 0 || this.m_nextEntry >= (Integer)m_javaNioByteBufferClass.getMethod("limit", null).invoke(this.m_buff, null)) {
            return false;
        }
        m_byteBufferPositionMethod.invoke(this.m_buff, new Integer(this.m_nextEntry));
        int n = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        if (this.m_nextEntry + n > (Integer)m_javaNioByteBufferClass.getMethod("limit", null).invoke(this.m_buff, null) || n == 0) {
            return false;
        }
        int n2 = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        int n3 = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        byte by2 = (Byte)m_byteBufferGetMethod.invoke(this.m_buff, null);
        m_byteBufferGetMethod.invoke(this.m_buff, null);
        byte by3 = (Byte)m_byteBufferGetMethod.invoke(this.m_buff, null);
        m_byteBufferGetMethod.invoke(this.m_buff, null);
        int n4 = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        int n5 = n4 - n2;
        byte[] arrby = new byte[n5];
        int n6 = 0;
        while ((by = ((Byte)m_byteBufferGetMethod.invoke(this.m_buff, null)).byteValue()) != 0 && n5 > n6) {
            arrby[n6++] = by;
        }
        String string = new String(arrby, 0, n6);
        m_byteBufferPositionMethod.invoke(this.m_buff, new Integer(this.m_nextEntry + n4));
        if (n3 > 0 && by2 == 66 && by3 == 5) {
            arrby = new byte[n3];
            int n7 = 0;
            while ((by = ((Byte)m_byteBufferGetMethod.invoke(this.m_buff, null)).byteValue()) != 0 && n3 > n7) {
                arrby[n7++] = by;
            }
            String string2 = new String(arrby, 0, n7);
            this.m_tblEntries.put(string, string2);
        }
        this.m_nextEntry += n;
        return true;
    }

    private boolean isAccessible() throws Exception {
        if (this.m_buff == null) {
            return false;
        }
        m_byteBufferPositionMethod.invoke(this.m_buff, PERFDATA_ACCESSIBLE_POSITION);
        byte by = (Byte)m_byteBufferGetMethod.invoke(this.m_buff, null);
        return by != 0;
    }

    private int getMagic() throws Exception {
        if (this.m_buff == null) {
            return 0;
        }
        Object object = m_javaNioByteBufferClass.getMethod("order", null).invoke(this.m_buff, null);
        m_javaNioByteBufferClass.getMethod("order", m_javaNioByteOrderClass).invoke(this.m_buff, m_javaNioByteOrderClass.getField("BIG_ENDIAN").get(null));
        m_byteBufferPositionMethod.invoke(this.m_buff, PERFDATA_MAGIC_POSITION);
        int n = (Integer)m_byteBufferGetIntMethod.invoke(this.m_buff, null);
        m_javaNioByteBufferClass.getMethod("order", m_javaNioByteOrderClass).invoke(this.m_buff, object);
        return n;
    }

    private Object getByteOrder() throws Exception {
        if (this.m_buff == null) {
            return null;
        }
        m_byteBufferPositionMethod.invoke(this.m_buff, PERFDATA_BYTEORDER_POSITION);
        byte by = (Byte)m_byteBufferGetMethod.invoke(this.m_buff, null);
        if (by == 0) {
            return m_javaNioByteOrderClass.getField("BIG_ENDIAN").get(null);
        }
        return m_javaNioByteOrderClass.getField("LITTLE_ENDIAN").get(null);
    }

    public String getMainClass() {
        if (!this.m_bUsable) {
            return null;
        }
        String string = (String)this.m_tblEntries.get("sun.rt.javaCommand");
        if (string != null) {
            int n = string.indexOf(32);
            if (n > 0) {
                return string.substring(0, n);
            }
            return string;
        }
        return null;
    }

    public int getId() {
        return this.m_vmId;
    }

    public String toString() {
        return this.getMainClass();
    }

    public boolean isUsable() {
        return this.m_bUsable;
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

