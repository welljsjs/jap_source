/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class WindowsRegistry {
    public static final int HKEY_CLASSES_ROOT = Integer.MIN_VALUE;
    public static final int HKEY_CURRENT_USER = -2147483647;
    public static final int HKEY_LOCAL_MACHINE = -2147483646;
    public static final int DELETE = 65536;
    public static final int KEY_QUERY_VALUE = 1;
    public static final int KEY_SET_VALUE = 2;
    public static final int KEY_CREATE_SUB_KEY = 4;
    public static final int KEY_ENUMERATE_SUB_KEYS = 8;
    public static final int KEY_READ = 131097;
    public static final int KEY_WRITE = 131078;
    public static final int KEY_ALL_ACCESS = 983103;
    public static final int ERROR_SUCCESS = 0;
    public static final int ERROR_FILE_NOT_FOUND = 2;
    public static final int ERROR_ACCESS_DENIED = 5;
    private static final int NATIVE_HANDLE = 0;
    private static final int ERROR_CODE = 1;
    private static Class ms_windowsPreferencesClass;
    private static Method ms_openKeyMethod;
    private static Method ms_queryValueMethod;
    private static Method ms_closeKeyMethod;
    private Vector m_vecHandles = new Vector();
    private boolean m_bClosed = false;
    static /* synthetic */ Class array$B;

    public WindowsRegistry(int n, String string, int n2) throws Exception {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\\/");
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            try {
                n = WindowsRegistry.openKey(n, string2, n2);
                this.m_vecHandles.addElement(new Integer(n));
            }
            catch (Exception exception) {
                this.close();
                throw exception;
            }
        }
    }

    public synchronized String read(String string) {
        if (this.isClosed()) {
            return null;
        }
        return WindowsRegistry.queryValue((Integer)this.m_vecHandles.lastElement(), string);
    }

    public boolean isClosed() {
        return this.m_bClosed;
    }

    public synchronized void close() {
        for (int i = this.m_vecHandles.size() - 1; i >= 0; --i) {
            WindowsRegistry.closeKey((Integer)this.m_vecHandles.elementAt(i));
        }
        this.m_bClosed = true;
    }

    private static int openKey(int n, String string, int n2) throws Exception {
        try {
            int[] arrn = (int[])ms_openKeyMethod.invoke(null, new Integer(n), (string + "\u0000").getBytes(), new Integer(n2));
            if (arrn != null && arrn[1] == 0) {
                return arrn[0];
            }
            if (arrn != null) {
                if (arrn[1] == 2) {
                    throw new FileNotFoundException(string);
                }
                if (arrn[1] == 5) {
                    throw new SecurityException(string);
                }
                throw new Exception("Registry error (" + arrn[1] + "): " + string);
            }
        }
        catch (InvocationTargetException invocationTargetException) {
            LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", invocationTargetException);
        }
        catch (IllegalAccessException illegalAccessException) {
            LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", illegalAccessException);
        }
        return -1;
    }

    private static String queryValue(int n, String string) {
        try {
            byte[] arrby = (byte[])ms_queryValueMethod.invoke(null, new Integer(n), (string + "\u0000").getBytes());
            if (arrby != null) {
                String string2 = new String(arrby);
                if (string2.charAt(string2.length() - 1) == '\u0000') {
                    string2 = string2.substring(0, string2.length() - 1);
                }
                return string2;
            }
        }
        catch (InvocationTargetException invocationTargetException) {
            LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", invocationTargetException);
        }
        catch (IllegalAccessException illegalAccessException) {
            LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", illegalAccessException);
        }
        return null;
    }

    private static int closeKey(int n) {
        try {
            Integer n2 = (Integer)ms_closeKeyMethod.invoke(null, new Integer(n));
            if (n2 != null) {
                return n2;
            }
        }
        catch (InvocationTargetException invocationTargetException) {
            LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", invocationTargetException);
        }
        catch (IllegalAccessException illegalAccessException) {
            LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", illegalAccessException);
        }
        return -1;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        if (System.getProperty("os.name", "").toLowerCase().indexOf("win") != -1) {
            try {
                Class<?> class_ = Class.forName("java.lang.reflect.AccessibleObject");
                Method method = class_.getMethod("setAccessible", Boolean.TYPE);
                ms_windowsPreferencesClass = Class.forName("java.util.prefs.WindowsPreferences");
                ms_openKeyMethod = ms_windowsPreferencesClass.getDeclaredMethod("WindowsRegOpenKey", Integer.TYPE, array$B == null ? (array$B = WindowsRegistry.class$("[B")) : array$B, Integer.TYPE);
                ms_queryValueMethod = ms_windowsPreferencesClass.getDeclaredMethod("WindowsRegQueryValueEx", Integer.TYPE, array$B == null ? (array$B = WindowsRegistry.class$("[B")) : array$B);
                ms_closeKeyMethod = ms_windowsPreferencesClass.getDeclaredMethod("WindowsRegCloseKey", Integer.TYPE);
                method.invoke(ms_openKeyMethod, Boolean.TRUE);
                method.invoke(ms_queryValueMethod, Boolean.TRUE);
                method.invoke(ms_closeKeyMethod, Boolean.TRUE);
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.GUI, "Error while accessing windows registry.", throwable);
            }
        }
    }
}

