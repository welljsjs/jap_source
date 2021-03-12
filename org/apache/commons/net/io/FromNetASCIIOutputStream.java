/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.io.FromNetASCIIInputStream;

public final class FromNetASCIIOutputStream
extends FilterOutputStream {
    private boolean __lastWasCR = false;

    public FromNetASCIIOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    private void __write(int n) throws IOException {
        switch (n) {
            case 13: {
                this.__lastWasCR = true;
                break;
            }
            case 10: {
                if (this.__lastWasCR) {
                    this.out.write(FromNetASCIIInputStream._lineSeparatorBytes);
                    this.__lastWasCR = false;
                    break;
                }
                this.__lastWasCR = false;
                this.out.write(10);
                break;
            }
            default: {
                if (this.__lastWasCR) {
                    this.out.write(13);
                    this.__lastWasCR = false;
                }
                this.out.write(n);
            }
        }
    }

    public synchronized void write(int n) throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            this.out.write(n);
            return;
        }
        this.__write(n);
    }

    public synchronized void write(byte[] arrby) throws IOException {
        this.write(arrby, 0, arrby.length);
    }

    public synchronized void write(byte[] arrby, int n, int n2) throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            this.out.write(arrby, n, n2);
            return;
        }
        while (n2-- > 0) {
            this.__write(arrby[n++]);
        }
    }

    public synchronized void close() throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            super.close();
            return;
        }
        if (this.__lastWasCR) {
            this.out.write(13);
        }
        super.close();
    }
}

