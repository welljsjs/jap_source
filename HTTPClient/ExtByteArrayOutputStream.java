/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Util;
import java.io.IOException;
import java.io.OutputStream;

final class ExtByteArrayOutputStream {
    private byte[] buf;
    private int count;

    public ExtByteArrayOutputStream() {
        this(100);
    }

    public ExtByteArrayOutputStream(int n) {
        this.buf = new byte[n];
        this.count = 0;
    }

    public final void write(String string) {
        int n = string.length();
        if (this.buf.length - this.count < n) {
            this.buf = Util.resizeArray(this.buf, Math.max(this.buf.length * 2, this.count + 2 * n));
        }
        Util.getBytes(string, this.buf, this.count);
        this.count += n;
    }

    public final void write(String string, String string2) {
        int n;
        int n2 = string.length();
        if (this.buf.length - this.count < n2 + (n = string2.length())) {
            this.buf = Util.resizeArray(this.buf, Math.max(this.buf.length * 2, this.count + 2 * (n2 + n)));
        }
        Util.getBytes(string, this.buf, this.count);
        this.count += n2;
        Util.getBytes(string2, this.buf, this.count);
        this.count += n;
    }

    public final void write(String string, String string2, String string3) {
        int n;
        int n2;
        int n3 = string.length();
        if (this.buf.length - this.count < n3 + (n2 = string2.length()) + (n = string3.length())) {
            this.buf = Util.resizeArray(this.buf, Math.max(this.buf.length * 2, this.count + 2 * (n3 + n2 + n)));
        }
        Util.getBytes(string, this.buf, this.count);
        this.count += n3;
        Util.getBytes(string2, this.buf, this.count);
        this.count += n2;
        Util.getBytes(string3, this.buf, this.count);
        this.count += n;
    }

    public final void write(byte[] arrby) {
        if (this.buf.length - this.count < arrby.length) {
            this.buf = Util.resizeArray(this.buf, Math.max(this.buf.length * 2, this.count + 2 * arrby.length));
        }
        System.arraycopy(arrby, 0, this.buf, this.count, arrby.length);
        this.count += arrby.length;
    }

    public final void reset() {
        this.count = 0;
    }

    public final void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.count);
    }
}

