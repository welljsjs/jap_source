/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;

public class ConsoleAppender
extends WriterAppender {
    public static final String SYSTEM_OUT = "System.out";
    public static final String SYSTEM_ERR = "System.err";
    protected String target = "System.out";
    private boolean follow = false;

    public ConsoleAppender() {
    }

    public ConsoleAppender(Layout layout) {
        this(layout, SYSTEM_OUT);
    }

    public ConsoleAppender(Layout layout, String string) {
        this.setLayout(layout);
        this.setTarget(string);
        this.activateOptions();
    }

    public void setTarget(String string) {
        String string2 = string.trim();
        if (SYSTEM_OUT.equalsIgnoreCase(string2)) {
            this.target = SYSTEM_OUT;
        } else if (SYSTEM_ERR.equalsIgnoreCase(string2)) {
            this.target = SYSTEM_ERR;
        } else {
            this.targetWarn(string);
        }
    }

    public String getTarget() {
        return this.target;
    }

    public final void setFollow(boolean bl) {
        this.follow = bl;
    }

    public final boolean getFollow() {
        return this.follow;
    }

    void targetWarn(String string) {
        LogLog.warn("[" + string + "] should be System.out or System.err.");
        LogLog.warn("Using previously set target, System.out by default.");
    }

    public void activateOptions() {
        if (this.follow) {
            if (this.target.equals(SYSTEM_ERR)) {
                this.setWriter(this.createWriter(new SystemErrStream()));
            } else {
                this.setWriter(this.createWriter(new SystemOutStream()));
            }
        } else if (this.target.equals(SYSTEM_ERR)) {
            this.setWriter(this.createWriter(System.err));
        } else {
            this.setWriter(this.createWriter(System.out));
        }
        super.activateOptions();
    }

    protected final void closeWriter() {
        if (this.follow) {
            super.closeWriter();
        }
    }

    private static class SystemOutStream
    extends OutputStream {
        public void close() {
        }

        public void flush() {
            System.out.flush();
        }

        public void write(byte[] arrby) throws IOException {
            System.out.write(arrby);
        }

        public void write(byte[] arrby, int n, int n2) throws IOException {
            System.out.write(arrby, n, n2);
        }

        public void write(int n) throws IOException {
            System.out.write(n);
        }
    }

    private static class SystemErrStream
    extends OutputStream {
        public void close() {
        }

        public void flush() {
            System.err.flush();
        }

        public void write(byte[] arrby) throws IOException {
            System.err.write(arrby);
        }

        public void write(byte[] arrby, int n, int n2) throws IOException {
            System.err.write(arrby, n, n2);
        }

        public void write(int n) throws IOException {
            System.err.write(n);
        }
    }
}

