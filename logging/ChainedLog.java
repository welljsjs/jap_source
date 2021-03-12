/*
 * Decompiled with CFR 0.150.
 */
package logging;

import logging.AbstractLog;
import logging.ILog;

public final class ChainedLog
implements ILog {
    private ILog m_Log1;
    private ILog m_Log2;
    private boolean m_bConstantLevel1;
    private boolean m_bConstantLevel2;
    private boolean m_bConstantType1;
    private boolean m_bConstantType2;

    public ChainedLog(ILog iLog, ILog iLog2) {
        this.m_Log1 = iLog;
        this.m_Log2 = iLog2;
    }

    public synchronized void log(int n, int n2, String string) {
        this.m_Log1.log(n, n2, string);
        this.m_Log2.log(n, n2, string);
    }

    public synchronized void setLogType(int n) {
        if (!this.m_bConstantType1) {
            this.m_Log1.setLogType(n);
        }
        if (!this.m_bConstantType2) {
            this.m_Log2.setLogType(n);
        }
    }

    public synchronized void setConstantLogLevel(ILog iLog, boolean bl) {
        if (this.m_Log1 == iLog) {
            this.m_bConstantLevel1 = bl;
        } else if (this.m_Log2 == iLog) {
            this.m_bConstantLevel2 = bl;
        }
    }

    public synchronized void setConstantLogType(ILog iLog, boolean bl) {
        if (this.m_Log1 == iLog) {
            this.m_bConstantType1 = bl;
        } else if (this.m_Log2 == iLog) {
            this.m_bConstantType2 = bl;
        }
    }

    public boolean isLogged(int n, int n2) {
        return AbstractLog.isLogged(this.m_Log1, n, n2) || AbstractLog.isLogged(this.m_Log2, n, n2);
    }

    public int getLogType() {
        if (!this.m_bConstantType1) {
            return this.m_Log1.getLogType();
        }
        return this.m_Log2.getLogType();
    }

    public synchronized void setLogLevel(int n) {
        if (!this.m_bConstantLevel1) {
            this.m_Log1.setLogLevel(n);
        }
        if (!this.m_bConstantLevel2) {
            this.m_Log2.setLogLevel(n);
        }
    }

    public int getLogLevel() {
        if (!this.m_bConstantLevel1) {
            return this.m_Log1.getLogLevel();
        }
        return this.m_Log2.getLogLevel();
    }
}

