/*
 * Decompiled with CFR 0.150.
 */
package logging;

import java.io.File;
import java.io.IOException;
import logging.AbstractLog4jLog;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public final class FileLog
extends AbstractLog4jLog {
    RollingFileAppender m_appender;

    public FileLog(File file, int n, int n2) throws IOException {
        this(file.getAbsolutePath(), n, n2);
    }

    public FileLog(String string, int n, int n2) throws IOException {
        PatternLayout patternLayout = new PatternLayout("[%d{ISO8601} - %p] %m%n");
        this.m_appender = new RollingFileAppender(patternLayout, string, true);
        this.m_appender.setMaximumFileSize(n);
        this.m_appender.setMaxBackupIndex(n2);
        this.m_appender.setBufferedIO(false);
        this.m_appender.activateOptions();
        this.getLogger().removeAllAppenders();
        this.getLogger().addAppender(this.m_appender);
    }

    public String getFile() {
        return this.m_appender.getFile();
    }

    protected Logger getLogger() {
        return Logger.getRootLogger();
    }
}

