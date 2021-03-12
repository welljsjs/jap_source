/*
 * Decompiled with CFR 0.150.
 */
package logging;

import logging.AbstractLog;
import logging.LogType;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractLog4jLog
extends AbstractLog {
    protected abstract Logger getLogger();

    public AbstractLog4jLog() {
        this.setLogType(LogType.ALL);
        this.setLogLevel(7);
    }

    public void log(int n, int n2, String string) {
        if (!this.isLogged(n, n2)) {
            return;
        }
        Level level = Level.DEBUG;
        if (n == 7) {
            level = Level.DEBUG;
        } else if (n == 6 || n == 5) {
            level = Level.INFO;
        } else if (n == 4) {
            level = Level.WARN;
        } else if (n == 3 || n == 2) {
            level = Level.ERROR;
        } else if (n == 1 || n == 0) {
            level = Level.FATAL;
        }
        this.getLogger().log(null, level, string, null);
    }

    public synchronized void setLogLevel(int n) {
        super.setLogLevel(n);
        Level level = Level.ALL;
        switch (n) {
            case 7: {
                level = Level.DEBUG;
                break;
            }
            case 5: 
            case 6: {
                level = Level.INFO;
                break;
            }
            case 0: 
            case 1: {
                level = Level.FATAL;
                break;
            }
            case 4: {
                level = Level.WARN;
                break;
            }
            case 2: 
            case 3: {
                level = Level.ERROR;
            }
        }
        this.getLogger().setLevel(level);
    }

    public synchronized int getLogLevel() {
        int n = super.getLogLevel();
        if (this.getLogger().isEnabledFor(Level.DEBUG)) {
            n = 7;
        } else if (this.getLogger().isEnabledFor(Level.INFO)) {
            if (n != 5) {
                n = 6;
            }
        } else if (this.getLogger().isEnabledFor(Level.WARN)) {
            n = 4;
        } else if (this.getLogger().isEnabledFor(Level.ERROR)) {
            if (n != 2) {
                n = 3;
            }
        } else if (this.getLogger().isEnabledFor(Level.FATAL) && n != 0) {
            n = 1;
        }
        super.setLogLevel(n);
        return n;
    }
}

