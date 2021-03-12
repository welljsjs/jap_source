/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ToNetASCIIInputStream
extends FilterInputStream {
    private static final int __NOTHING_SPECIAL = 0;
    private static final int __LAST_WAS_CR = 1;
    private static final int __LAST_WAS_NL = 2;
    private int __status = 0;

    public ToNetASCIIInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public int read() throws IOException {
        if (this.__status == 2) {
            this.__status = 0;
            return 10;
        }
        int n = this.in.read();
        switch (n) {
            case 13: {
                this.__status = 1;
                return 13;
            }
            case 10: {
                if (this.__status == 1) break;
                this.__status = 2;
                return 13;
            }
        }
        this.__status = 0;
        return n;
    }

    public int read(byte[] arrby) throws IOException {
        return this.read(arrby, 0, arrby.length);
    }

    public int read(byte[] arrby, int n, int n2) throws IOException {
        if (n2 < 1) {
            return 0;
        }
        int n3 = this.available();
        if (n2 > n3) {
            n2 = n3;
        }
        if (n2 < 1) {
            n2 = 1;
        }
        if ((n3 = this.read()) == -1) {
            return -1;
        }
        int n4 = n;
        do {
            arrby[n++] = (byte)n3;
        } while (--n2 > 0 && (n3 = this.read()) != -1);
        return n - n4;
    }

    public boolean markSupported() {
        return false;
    }

    public int available() throws IOException {
        int n = this.in.available();
        if (this.__status == 2) {
            return n + 1;
        }
        return n;
    }
}

