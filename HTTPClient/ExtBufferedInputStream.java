/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.DemultiplexorInputStream;
import HTTPClient.Util;
import java.io.IOException;
import java.io.InputStream;

final class ExtBufferedInputStream
extends DemultiplexorInputStream {
    private byte[] buf;
    private int count;
    private int pos;
    private byte[] eod_str;
    private int[] eod_cmp;
    private int eod_pos = -1;

    public ExtBufferedInputStream(InputStream inputStream) {
        this(inputStream, 2048);
    }

    public ExtBufferedInputStream(InputStream inputStream, int n) {
        super(inputStream);
        this.buf = new byte[n];
    }

    private void fill() throws IOException {
        if (this.eod_str == null) {
            this.pos = 0;
        } else if (this.pos >= this.buf.length) {
            if (this.buf.length > this.eod_str.length) {
                System.arraycopy(this.buf, this.pos - this.eod_str.length, this.buf, 0, this.eod_str.length);
                this.pos = this.eod_str.length;
            } else {
                this.buf = Util.resizeArray(this.buf, this.eod_str.length * 2);
            }
        }
        this.count = this.pos;
        int n = this.in.read(this.buf, this.pos, this.buf.length - this.pos);
        int n2 = this.count = n <= 0 ? this.pos : n + this.pos;
        if (this.eod_str != null) {
            int n3 = this.pos - this.eod_str.length;
            if (n3 < 0) {
                n3 = 0;
            }
            this.eod_pos = Util.findStr(this.eod_str, this.eod_cmp, this.buf, n3, this.count);
            if (this.eod_pos >= 0) {
                this.eod_pos += this.eod_str.length;
            }
        }
    }

    public synchronized int read() throws IOException {
        if (this.eod_pos >= 0 && this.pos >= this.eod_pos) {
            return -1;
        }
        if (this.pos >= this.count) {
            this.fill();
            if (this.pos >= this.count) {
                return -1;
            }
        }
        return this.buf[this.pos++] & 0xFF;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        int n3;
        if (this.eod_pos >= 0 && this.pos >= this.eod_pos) {
            return -1;
        }
        int n4 = this.count - this.pos;
        if (n4 <= 0) {
            if (n2 >= this.buf.length && this.eod_str == null) {
                return this.in.read(arrby, n, n2);
            }
            this.fill();
            n4 = this.count - this.pos;
            if (n4 <= 0) {
                return -1;
            }
        }
        int n5 = n3 = n4 < n2 ? n4 : n2;
        if (this.eod_pos >= 0 && this.eod_pos - this.pos < n3) {
            n3 = this.eod_pos - this.pos;
        }
        System.arraycopy(this.buf, this.pos, arrby, n, n3);
        this.pos += n3;
        return n3;
    }

    public synchronized int available() throws IOException {
        if (this.eod_pos < 0) {
            return this.count - this.pos + this.in.available();
        }
        return this.eod_pos - this.pos;
    }

    public void setTerminator(byte[] arrby, int[] arrn) {
        this.eod_str = arrby;
        this.eod_cmp = arrn;
        if (this.eod_str != null) {
            this.eod_pos = Util.findStr(this.eod_str, this.eod_cmp, this.buf, this.pos, this.count);
            if (this.eod_pos >= 0) {
                this.eod_pos += this.eod_str.length;
            }
        } else {
            this.eod_pos = -1;
        }
    }

    public boolean atEnd() {
        return this.eod_pos >= 0 && this.pos >= this.eod_pos;
    }

    public boolean startsWithCRLF() throws IOException {
        if (this.count - this.pos >= 2) {
            if (this.buf[this.pos] == 13 && this.buf[this.pos + 1] == 10) {
                this.pos += 2;
                return true;
            }
            return false;
        }
        if (this.count - this.pos == 1 && this.buf[this.pos] != 13) {
            return false;
        }
        this.count -= this.pos;
        if (this.count == 1) {
            this.buf[0] = this.buf[this.pos];
        }
        this.pos = 0;
        if (this.count == 0) {
            this.buf[this.count++] = (byte)this.in.read();
        }
        if (this.buf[this.pos] != 13) {
            return false;
        }
        this.buf[this.count++] = (byte)this.in.read();
        if (this.buf[this.pos + 1] != 10) {
            return false;
        }
        this.pos += 2;
        return true;
    }
}

