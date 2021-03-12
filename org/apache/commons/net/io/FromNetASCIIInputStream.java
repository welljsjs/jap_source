/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class FromNetASCIIInputStream
extends PushbackInputStream {
    static final boolean _noConversionRequired;
    static final String _lineSeparator;
    static final byte[] _lineSeparatorBytes;
    private int __length = 0;

    public static final boolean isConversionRequired() {
        return !_noConversionRequired;
    }

    public FromNetASCIIInputStream(InputStream inputStream) {
        super(inputStream, _lineSeparatorBytes.length + 1);
    }

    private int __read() throws IOException {
        int n = super.read();
        if (n == 13) {
            n = super.read();
            if (n == 10) {
                this.unread(_lineSeparatorBytes);
                n = super.read();
                --this.__length;
            } else {
                if (n != -1) {
                    this.unread(n);
                }
                return 13;
            }
        }
        return n;
    }

    public int read() throws IOException {
        if (_noConversionRequired) {
            return super.read();
        }
        return this.__read();
    }

    public int read(byte[] arrby) throws IOException {
        return this.read(arrby, 0, arrby.length);
    }

    public int read(byte[] arrby, int n, int n2) throws IOException {
        if (n2 < 1) {
            return 0;
        }
        int n3 = this.available();
        int n4 = this.__length = n2 > n3 ? n3 : n2;
        if (this.__length < 1) {
            this.__length = 1;
        }
        if (_noConversionRequired) {
            return super.read(arrby, n, this.__length);
        }
        n3 = this.__read();
        if (n3 == -1) {
            return -1;
        }
        int n5 = n;
        do {
            arrby[n++] = (byte)n3;
        } while (--this.__length > 0 && (n3 = this.__read()) != -1);
        return n - n5;
    }

    public int available() throws IOException {
        return this.buf.length - this.pos + this.in.available();
    }

    static {
        _lineSeparator = System.getProperty("line.separator");
        _noConversionRequired = _lineSeparator.equals("\r\n");
        _lineSeparatorBytes = _lineSeparator.getBytes();
    }
}

