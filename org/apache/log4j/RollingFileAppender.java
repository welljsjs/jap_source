/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

public class RollingFileAppender
extends FileAppender {
    protected long maxFileSize = 0xA00000L;
    protected int maxBackupIndex = 1;

    public RollingFileAppender() {
    }

    public RollingFileAppender(Layout layout, String string, boolean bl) throws IOException {
        super(layout, string, bl);
    }

    public RollingFileAppender(Layout layout, String string) throws IOException {
        super(layout, string);
    }

    public int getMaxBackupIndex() {
        return this.maxBackupIndex;
    }

    public long getMaximumFileSize() {
        return this.maxFileSize;
    }

    public void rollOver() {
        LogLog.debug("rolling over count=" + ((CountingQuietWriter)this.qw).getCount());
        LogLog.debug("maxBackupIndex=" + this.maxBackupIndex);
        if (this.maxBackupIndex > 0) {
            File file;
            File file2 = new File(this.fileName + '.' + this.maxBackupIndex);
            if (file2.exists()) {
                file2.delete();
            }
            for (int i = this.maxBackupIndex - 1; i >= 1; --i) {
                file2 = new File(this.fileName + "." + i);
                if (!file2.exists()) continue;
                file = new File(this.fileName + '.' + (i + 1));
                LogLog.debug("Renaming file " + file2 + " to " + file);
                file2.renameTo(file);
            }
            file = new File(this.fileName + "." + 1);
            this.closeFile();
            file2 = new File(this.fileName);
            LogLog.debug("Renaming file " + file2 + " to " + file);
            file2.renameTo(file);
        }
        try {
            this.setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
        }
        catch (IOException iOException) {
            LogLog.error("setFile(" + this.fileName + ", false) call failed.", iOException);
        }
    }

    public synchronized void setFile(String string, boolean bl, boolean bl2, int n) throws IOException {
        super.setFile(string, bl, this.bufferedIO, this.bufferSize);
        if (bl) {
            File file = new File(string);
            ((CountingQuietWriter)this.qw).setCount(file.length());
        }
    }

    public void setMaxBackupIndex(int n) {
        this.maxBackupIndex = n;
    }

    public void setMaximumFileSize(long l) {
        this.maxFileSize = l;
    }

    public void setMaxFileSize(String string) {
        this.maxFileSize = OptionConverter.toFileSize(string, this.maxFileSize + 1L);
    }

    protected void setQWForFiles(Writer writer) {
        this.qw = new CountingQuietWriter(writer, this.errorHandler);
    }

    protected void subAppend(LoggingEvent loggingEvent) {
        super.subAppend(loggingEvent);
        if (this.fileName != null && ((CountingQuietWriter)this.qw).getCount() >= this.maxFileSize) {
            this.rollOver();
        }
    }
}

