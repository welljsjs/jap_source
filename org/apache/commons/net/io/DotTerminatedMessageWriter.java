/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.IOException;
import java.io.Writer;

public final class DotTerminatedMessageWriter
extends Writer {
    private static final int __NOTHING_SPECIAL_STATE = 0;
    private static final int __LAST_WAS_CR_STATE = 1;
    private static final int __LAST_WAS_NL_STATE = 2;
    private int __state;
    private Writer __output;

    public DotTerminatedMessageWriter(Writer writer) {
        super(writer);
        this.__output = writer;
        this.__state = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(int n) throws IOException {
        Object object = this.lock;
        synchronized (object) {
            switch (n) {
                case 13: {
                    this.__state = 1;
                    this.__output.write(13);
                    return;
                }
                case 10: {
                    if (this.__state != 1) {
                        this.__output.write(13);
                    }
                    this.__output.write(10);
                    this.__state = 2;
                    return;
                }
                case 46: {
                    if (this.__state != 2) break;
                    this.__output.write(46);
                }
            }
            this.__state = 0;
            this.__output.write(n);
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(char[] arrc, int n, int n2) throws IOException {
        Object object = this.lock;
        synchronized (object) {
            while (n2-- > 0) {
                this.write(arrc[n++]);
            }
        }
    }

    public void write(char[] arrc) throws IOException {
        this.write(arrc, 0, arrc.length);
    }

    public void write(String string) throws IOException {
        this.write(string.toCharArray());
    }

    public void write(String string, int n, int n2) throws IOException {
        this.write(string.toCharArray(), n, n2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flush() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            this.__output.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            if (this.__output == null) {
                return;
            }
            if (this.__state == 1) {
                this.__output.write(10);
            } else if (this.__state != 2) {
                this.__output.write("\r\n");
            }
            this.__output.write(".\r\n");
            this.__output.flush();
            this.__output = null;
        }
    }
}

