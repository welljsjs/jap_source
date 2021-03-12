/*
 * Decompiled with CFR 0.150.
 */
package logging;

import logging.ILog;
import logging.LogLevel;
import logging.LogType;

public abstract class AbstractLog
implements ILog {
    private int m_logLevel;
    private int m_logType;

    public AbstractLog() {
        this(7, LogType.ALL);
    }

    public AbstractLog(int n, int n2) {
        this.m_logLevel = n;
        this.m_logType = n2;
    }

    public boolean isLogged(int n, int n2) {
        return AbstractLog.isLogged(this, n, n2);
    }

    public static boolean isLogged(ILog iLog, int n, int n2) {
        return n <= iLog.getLogLevel() && (n2 & iLog.getLogType()) == n2;
    }

    public void setLogLevel(int n) {
        if (n >= 0 && n < LogLevel.getLevelCount()) {
            this.m_logLevel = n;
        }
    }

    public void setLogType(int n) {
        this.m_logType = n;
    }

    public int getLogType() {
        return this.m_logType;
    }

    public int getLogLevel() {
        return this.m_logLevel;
    }

    public abstract /* synthetic */ void log(int var1, int var2, String var3);
}

