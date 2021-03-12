/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.QuietWriter;

public class FileAppender
extends WriterAppender {
    protected boolean fileAppend = true;
    protected String fileName = null;
    protected boolean bufferedIO = false;
    protected int bufferSize = 8192;

    public FileAppender() {
    }

    public FileAppender(Layout layout, String string, boolean bl, boolean bl2, int n) throws IOException {
        this.layout = layout;
        this.setFile(string, bl, bl2, n);
    }

    public FileAppender(Layout layout, String string, boolean bl) throws IOException {
        this.layout = layout;
        this.setFile(string, bl, false, this.bufferSize);
    }

    public FileAppender(Layout layout, String string) throws IOException {
        this(layout, string, true);
    }

    public void setFile(String string) {
        String string2;
        this.fileName = string2 = string.trim();
    }

    public boolean getAppend() {
        return this.fileAppend;
    }

    public String getFile() {
        return this.fileName;
    }

    public void activateOptions() {
        if (this.fileName != null) {
            try {
                this.setFile(this.fileName, this.fileAppend, this.bufferedIO, this.bufferSize);
            }
            catch (IOException iOException) {
                this.errorHandler.error("setFile(" + this.fileName + "," + this.fileAppend + ") call failed.", iOException, 4);
            }
        } else {
            LogLog.warn("File option not set for appender [" + this.name + "].");
            LogLog.warn("Are you using FileAppender instead of ConsoleAppender?");
        }
    }

    protected void closeFile() {
        if (this.qw != null) {
            try {
                this.qw.close();
            }
            catch (IOException iOException) {
                LogLog.error("Could not close " + this.qw, iOException);
            }
        }
    }

    public boolean getBufferedIO() {
        return this.bufferedIO;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setAppend(boolean bl) {
        this.fileAppend = bl;
    }

    public void setBufferedIO(boolean bl) {
        this.bufferedIO = bl;
        if (bl) {
            this.immediateFlush = false;
        }
    }

    public void setBufferSize(int n) {
        this.bufferSize = n;
    }

    public synchronized void setFile(String string, boolean bl, boolean bl2, int n) throws IOException {
        LogLog.debug("setFile called: " + string + ", " + bl);
        if (bl2) {
            this.setImmediateFlush(false);
        }
        this.reset();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(string, bl);
        }
        catch (FileNotFoundException fileNotFoundException) {
            String string2 = new File(string).getParent();
            if (string2 != null) {
                File file = new File(string2);
                if (!file.exists() && file.mkdirs()) {
                    fileOutputStream = new FileOutputStream(string, bl);
                }
                throw fileNotFoundException;
            }
            throw fileNotFoundException;
        }
        Writer writer = this.createWriter(fileOutputStream);
        if (bl2) {
            writer = new BufferedWriter(writer, n);
        }
        this.setQWForFiles(writer);
        this.fileName = string;
        this.fileAppend = bl;
        this.bufferedIO = bl2;
        this.bufferSize = n;
        this.writeHeader();
        LogLog.debug("setFile ended");
    }

    protected void setQWForFiles(Writer writer) {
        this.qw = new QuietWriter(writer, this.errorHandler);
    }

    protected void reset() {
        this.closeFile();
        this.fileName = null;
        super.reset();
    }
}

