/*
 * Decompiled with CFR 0.150.
 */
package logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;
import logging.AbstractLog;
import logging.DummyLog;
import logging.ILog;

public final class LogHolder {
    public static final int CALLING_METHOD_IGNORE = 0;
    public static final int CALLING_METHOD_ADD = 1;
    public static final int CALLING_METHOD_ONLY = 2;
    public static final int DETAIL_LEVEL_LOWEST = 0;
    public static final int DETAIL_LEVEL_LOWER = 1;
    public static final int DETAIL_LEVEL_HIGH = 2;
    public static final int DETAIL_LEVEL_HIGHEST = 3;
    private static final String[] DETAIL_LEVEL_NAMES = new String[]{"_detailLowest", "_detailLower", "_detailHigh", "_detailHighest"};
    private static final String TRACED_LOG_MESSAGE = "[Traced log Message]:";
    private static final String LOGGED_THROWABLE = " Logged Throwable: ";
    private static final int LINE_LENGTH_HIGH_DETAIL = 40;
    private static final int LINE_LENGTH_HIGHEST_DETAIL = 70;
    private static LogHolder ms_logHolderInstance;
    private static int m_messageDetailLevel;
    private static ILog ms_logInstance;
    static /* synthetic */ Class class$logging$LogHolder;
    static /* synthetic */ Class class$java$lang$Throwable;
    static /* synthetic */ Class class$java$lang$Exception;

    private LogHolder() {
        ms_logInstance = new DummyLog();
    }

    public void finalize() throws Throwable {
        if (this.equals(ms_logHolderInstance)) {
            ms_logHolderInstance = null;
        }
        super.finalize();
    }

    public static int getDetailLevelCount() {
        return DETAIL_LEVEL_NAMES.length;
    }

    public static String getDetailLevelName(int n) {
        if (n < 0 || n >= DETAIL_LEVEL_NAMES.length) {
            return null;
        }
        return DETAIL_LEVEL_NAMES[n];
    }

    public static boolean setDetailLevel(int n) {
        if (n < 0) {
            return false;
        }
        if (n > 3) {
            return false;
        }
        m_messageDetailLevel = n;
        return true;
    }

    public static int getDetailLevel() {
        return m_messageDetailLevel;
    }

    public static synchronized void log(int n, int n2, Throwable throwable) {
        LogHolder.log(n, n2, null, throwable);
    }

    public static synchronized void log(int n, int n2, Throwable throwable, int n3) {
        LogHolder.log(n, n2, null, throwable, n3);
    }

    public static synchronized void log(int n, int n2, String string, Throwable throwable) {
        LogHolder.log(n, n2, string, throwable, 0);
    }

    public static synchronized void log(int n, int n2, String string, Throwable throwable, int n3) {
        if (LogHolder.isLogged(n, n2)) {
            String string2 = "";
            if (string != null && string.length() > 0) {
                string2 = string;
            }
            if (m_messageDetailLevel <= 0) {
                if (throwable != null && string2.trim().length() == 0 && (string2 = throwable.getMessage()) == null) {
                    string2 = throwable.toString();
                }
                LogHolder.getLogInstance().log(n, n2, string2);
            } else if (m_messageDetailLevel > 0 && m_messageDetailLevel < 2) {
                if (throwable != null) {
                    if (string2.trim().length() == 0) {
                        string2 = throwable.getMessage();
                        if (string2 == null) {
                            string2 = throwable.toString();
                        }
                    } else if (throwable.getMessage() != null) {
                        string2 = string2 + "\n Logged Throwable: " + throwable.getMessage();
                    }
                }
                LogHolder.getLogInstance().log(n, n2, string2);
            } else if (m_messageDetailLevel == 2) {
                if (throwable != null) {
                    string2 = string2.trim().length() == 0 ? throwable.toString() : string2 + "\n Logged Throwable: " + throwable.toString();
                }
                if (n3 == 1) {
                    ms_logInstance.log(n, n2, LogHolder.normaliseString(LogHolder.getCallingClassFile(false) + ": ", 40) + TRACED_LOG_MESSAGE);
                }
                ms_logInstance.log(n, n2, LogHolder.normaliseString(LogHolder.getCallingClassFile(n3 != 0) + ": ", 40) + string2);
            } else if (m_messageDetailLevel >= 3) {
                if (throwable != null) {
                    string2 = string2.trim().length() == 0 ? LogHolder.getStackTrace(throwable) : string2 + "\n Logged Throwable: " + LogHolder.getStackTrace(throwable);
                }
                if (n3 == 1) {
                    ms_logInstance.log(n, n2, LogHolder.normaliseString(LogHolder.getCallingMethod(false) + ": ", 70) + TRACED_LOG_MESSAGE);
                }
                ms_logInstance.log(n, n2, LogHolder.normaliseString(LogHolder.getCallingMethod(n3 != 0) + ": ", 70) + string2);
            }
        }
    }

    public static void log(int n, int n2, String string, int n3) {
        LogHolder.log(n, n2, string, null, n3);
    }

    public static void log(int n, int n2, String string) {
        LogHolder.log(n, n2, string, 0);
    }

    public static synchronized void setLogInstance(ILog iLog) {
        ms_logInstance = iLog;
        if (ms_logInstance == null) {
            ms_logInstance = new DummyLog();
        }
    }

    private static ILog getLogInstance() {
        return ms_logInstance;
    }

    public static synchronized boolean isLogged(int n, int n2) {
        if (ms_logInstance instanceof AbstractLog) {
            return ((AbstractLog)ms_logInstance).isLogged(n, n2);
        }
        return AbstractLog.isLogged(ms_logInstance, n, n2);
    }

    private static String getCallingClassFile(boolean bl) {
        String string = LogHolder.getCallingMethod(bl);
        string = string.substring(string.indexOf(40), string.indexOf(41) + 1);
        return string;
    }

    public static String getCallingMethod(boolean bl) {
        String string = "";
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        String string2 = "   ";
        new Exception().printStackTrace(printWriter);
        StringTokenizer stringTokenizer = new StringTokenizer(stringWriter.toString());
        stringTokenizer.nextToken();
        while (stringTokenizer.hasMoreTokens()) {
            int n;
            stringTokenizer.nextToken();
            string = stringTokenizer.nextToken().replace('/', '.');
            if (string.indexOf(40) > 0) {
                while (string.indexOf(41) < 0) {
                    string = string + stringTokenizer.nextToken();
                }
            }
            if (string.startsWith((class$logging$LogHolder == null ? LogHolder.class$("logging.LogHolder") : class$logging$LogHolder).getName()) || string.startsWith(string2) || string.startsWith((class$java$lang$Throwable == null ? LogHolder.class$("java.lang.Throwable") : class$java$lang$Throwable).getName()) || string.startsWith((class$java$lang$Exception == null ? LogHolder.class$("java.lang.Exception") : class$java$lang$Exception).getName())) continue;
            if (!bl || string2.trim().length() != 0) break;
            string2 = string;
            int n2 = string.indexOf(40);
            if (n2 > 0) {
                string2 = string.substring(0, n2);
            }
            if ((n = string2.lastIndexOf(46)) >= 0) {
                string2 = string2.substring(0, n);
            }
            if (string2.indexOf("$") <= 0) continue;
            string2 = string2.substring(0, string2.indexOf("$"));
        }
        return string;
    }

    private static String normaliseString(String string, int n) {
        if (string.length() < n) {
            char[] arrc = new char[n - string.length()];
            for (int i = 0; i < arrc.length; ++i) {
                arrc[i] = 32;
            }
            string = string + new String(arrc);
        }
        return string;
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
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
        m_messageDetailLevel = 3;
        ms_logInstance = new DummyLog();
    }
}

