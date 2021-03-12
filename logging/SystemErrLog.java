/*
 * Decompiled with CFR 0.150.
 */
package logging;

import logging.AbstractLog;
import logging.LogLevel;
import logging.LogType;

public final class SystemErrLog
extends AbstractLog {
    private boolean m_bSimpleLog;

    public SystemErrLog(boolean bl) {
        this(7, LogType.ALL, bl);
    }

    public SystemErrLog() {
        this(7, LogType.ALL);
    }

    public SystemErrLog(int n, int n2) {
        this(n, n2, false);
    }

    public SystemErrLog(int n, int n2, boolean bl) {
        super(n, n2);
        this.m_bSimpleLog = bl;
    }

    public void log(int n, int n2, String string) {
        if (this.isLogged(n, n2)) {
            if (this.m_bSimpleLog) {
                System.err.println(string);
            } else {
                System.err.println("[" + LogLevel.getLevelName(n) + "] " + string);
            }
        }
    }
}

