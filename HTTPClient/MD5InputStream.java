/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.HashVerifier;
import HTTPClient.MD5;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class MD5InputStream
extends FilterInputStream {
    private HashVerifier verifier;
    private MD5 md5;
    private long rcvd = 0L;
    private boolean closed = false;

    public MD5InputStream(InputStream inputStream, HashVerifier hashVerifier) {
        super(inputStream);
        this.verifier = hashVerifier;
        this.md5 = new MD5();
    }

    public synchronized int read() throws IOException {
        int n = this.in.read();
        if (n != -1) {
            this.md5.Update((byte)n);
        } else {
            this.real_close();
        }
        ++this.rcvd;
        return n;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        int n3 = this.in.read(arrby, n, n2);
        if (n3 > 0) {
            this.md5.Update(arrby, n, n3);
        } else {
            this.real_close();
        }
        this.rcvd += (long)n3;
        return n3;
    }

    public synchronized long skip(long l) throws IOException {
        byte[] arrby = new byte[(int)l];
        int n = this.read(arrby, 0, (int)l);
        if (n > 0) {
            return n;
        }
        return 0L;
    }

    public synchronized void close() throws IOException {
        while (this.skip(10000L) > 0L) {
        }
        this.real_close();
    }

    private void real_close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.in.close();
        this.verifier.verifyHash(this.md5.Final(), this.rcvd);
    }
}

