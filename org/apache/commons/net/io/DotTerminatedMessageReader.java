/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public final class DotTerminatedMessageReader
extends Reader {
    private static final String LS = System.getProperty("line.separator");
    private static final char[] LS_CHARS = LS.toCharArray();
    private boolean atBeginning = true;
    private boolean eof = false;
    private int pos;
    private char[] internalBuffer = new char[LS_CHARS.length + 3];
    private PushbackReader internalReader;

    public DotTerminatedMessageReader(Reader reader) {
        super(reader);
        this.pos = this.internalBuffer.length;
        this.internalReader = new PushbackReader(reader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int read() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            if (this.pos < this.internalBuffer.length) {
                return this.internalBuffer[this.pos++];
            }
            if (this.eof) {
                return -1;
            }
            int n = this.internalReader.read();
            if (n == -1) {
                this.eof = true;
                return -1;
            }
            if (this.atBeginning) {
                this.atBeginning = false;
                if (n == 46) {
                    n = this.internalReader.read();
                    if (n != 46) {
                        this.eof = true;
                        this.internalReader.read();
                        return -1;
                    }
                    return 46;
                }
            }
            if (n == 13) {
                n = this.internalReader.read();
                if (n == 10) {
                    n = this.internalReader.read();
                    if (n == 46) {
                        n = this.internalReader.read();
                        if (n != 46) {
                            this.internalReader.read();
                            this.eof = true;
                        } else {
                            this.internalBuffer[--this.pos] = (char)n;
                        }
                    } else {
                        this.internalReader.unread(n);
                    }
                    this.pos -= LS_CHARS.length;
                    System.arraycopy(LS_CHARS, 0, this.internalBuffer, this.pos, LS_CHARS.length);
                    n = this.internalBuffer[this.pos++];
                } else {
                    this.internalBuffer[--this.pos] = (char)n;
                    return 13;
                }
            }
            return n;
        }
    }

    public int read(char[] arrc) throws IOException {
        return this.read(arrc, 0, arrc.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int read(char[] arrc, int n, int n2) throws IOException {
        Object object = this.lock;
        synchronized (object) {
            if (n2 < 1) {
                return 0;
            }
            int n3 = this.read();
            if (n3 == -1) {
                return -1;
            }
            int n4 = n;
            do {
                arrc[n++] = (char)n3;
            } while (--n2 > 0 && (n3 = this.read()) != -1);
            return n - n4;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean ready() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            return this.pos < this.internalBuffer.length || this.internalReader.ready();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            if (this.internalReader == null) {
                return;
            }
            if (!this.eof) {
                while (this.read() != -1) {
                }
            }
            this.eof = true;
            this.atBeginning = false;
            this.pos = this.internalBuffer.length;
            this.internalReader = null;
        }
    }
}

