/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

public class WriterAppender
extends AppenderSkeleton {
    protected boolean immediateFlush = true;
    protected String encoding;
    protected QuietWriter qw;

    public WriterAppender() {
    }

    public WriterAppender(Layout layout, OutputStream outputStream) {
        this(layout, new OutputStreamWriter(outputStream));
    }

    public WriterAppender(Layout layout, Writer writer) {
        this.layout = layout;
        this.setWriter(writer);
    }

    public void setImmediateFlush(boolean bl) {
        this.immediateFlush = bl;
    }

    public boolean getImmediateFlush() {
        return this.immediateFlush;
    }

    public void activateOptions() {
    }

    public void append(LoggingEvent loggingEvent) {
        if (!this.checkEntryConditions()) {
            return;
        }
        this.subAppend(loggingEvent);
    }

    protected boolean checkEntryConditions() {
        if (this.closed) {
            LogLog.warn("Not allowed to write to a closed appender.");
            return false;
        }
        if (this.qw == null) {
            this.errorHandler.error("No output stream or file set for the appender named [" + this.name + "].");
            return false;
        }
        if (this.layout == null) {
            this.errorHandler.error("No layout set for the appender named [" + this.name + "].");
            return false;
        }
        return true;
    }

    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.writeFooter();
        this.reset();
    }

    protected void closeWriter() {
        if (this.qw != null) {
            try {
                this.qw.close();
            }
            catch (IOException iOException) {
                LogLog.error("Could not close " + this.qw, iOException);
            }
        }
    }

    protected OutputStreamWriter createWriter(OutputStream outputStream) {
        OutputStreamWriter outputStreamWriter = null;
        String string = this.getEncoding();
        if (string != null) {
            try {
                outputStreamWriter = new OutputStreamWriter(outputStream, string);
            }
            catch (IOException iOException) {
                LogLog.warn("Error initializing output writer.");
                LogLog.warn("Unsupported encoding?");
            }
        }
        if (outputStreamWriter == null) {
            outputStreamWriter = new OutputStreamWriter(outputStream);
        }
        return outputStreamWriter;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }

    public synchronized void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            LogLog.warn("You have tried to set a null error-handler.");
        } else {
            this.errorHandler = errorHandler;
            if (this.qw != null) {
                this.qw.setErrorHandler(errorHandler);
            }
        }
    }

    public synchronized void setWriter(Writer writer) {
        this.reset();
        this.qw = new QuietWriter(writer, this.errorHandler);
        this.writeHeader();
    }

    protected void subAppend(LoggingEvent loggingEvent) {
        String[] arrstring;
        this.qw.write(this.layout.format(loggingEvent));
        if (this.layout.ignoresThrowable() && (arrstring = loggingEvent.getThrowableStrRep()) != null) {
            int n = arrstring.length;
            for (int i = 0; i < n; ++i) {
                this.qw.write(arrstring[i]);
                this.qw.write(Layout.LINE_SEP);
            }
        }
        if (this.immediateFlush) {
            this.qw.flush();
        }
    }

    public boolean requiresLayout() {
        return true;
    }

    protected void reset() {
        this.closeWriter();
        this.qw = null;
    }

    protected void writeFooter() {
        String string;
        if (this.layout != null && (string = this.layout.getFooter()) != null && this.qw != null) {
            this.qw.write(string);
            this.qw.flush();
        }
    }

    protected void writeHeader() {
        String string;
        if (this.layout != null && (string = this.layout.getHeader()) != null && this.qw != null) {
            this.qw.write(string);
        }
    }
}

