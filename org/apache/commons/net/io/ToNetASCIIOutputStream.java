/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ToNetASCIIOutputStream
extends FilterOutputStream {
    private boolean __lastWasCR = false;

    public ToNetASCIIOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    public synchronized void write(int n) throws IOException {
        switch (n) {
            case 13: {
                this.__lastWasCR = true;
                this.out.write(13);
                return;
            }
            case 10: {
                if (this.__lastWasCR) break;
                this.out.write(13);
            }
        }
        this.__lastWasCR = false;
        this.out.write(n);
    }

    public synchronized void write(byte[] arrby) throws IOException {
        this.write(arrby, 0, arrby.length);
    }

    public synchronized void write(byte[] arrby, int n, int n2) throws IOException {
        while (n2-- > 0) {
            this.write(arrby[n++]);
        }
    }
}

