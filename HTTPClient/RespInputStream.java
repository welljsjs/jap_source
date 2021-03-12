/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.ModuleException;
import HTTPClient.ResponseHandler;
import HTTPClient.StreamDemultiplexor;
import HTTPClient.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class RespInputStream
extends InputStream
implements GlobalConstants {
    private StreamDemultiplexor demux = null;
    private ResponseHandler resph;
    boolean closed = false;
    private boolean dont_truncate = false;
    private byte[] buffer = null;
    private boolean interrupted = false;
    private int offset = 0;
    private int end = 0;
    int count = 0;
    private byte[] ch = new byte[1];

    RespInputStream(StreamDemultiplexor streamDemultiplexor, ResponseHandler responseHandler) {
        this.demux = streamDemultiplexor;
        this.resph = responseHandler;
    }

    public synchronized int read() throws IOException {
        int n = this.read(this.ch, 0, 1);
        if (n == 1) {
            return this.ch[0] & 0xFF;
        }
        return -1;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        if (this.closed) {
            return -1;
        }
        int n3 = this.end - this.offset;
        if (!(this.buffer == null || n3 == 0 && this.interrupted)) {
            if (n3 == 0) {
                return -1;
            }
            n2 = n2 > n3 ? n3 : n2;
            System.arraycopy(this.buffer, this.offset, arrby, n, n2);
            this.offset += n2;
            return n2;
        }
        int n4 = this.demux.read(arrby, n, n2, this.resph, this.resph.resp.timeout);
        if (n4 != -1 && this.resph.resp.got_headers) {
            this.count += n4;
        }
        return n4;
    }

    public synchronized long skip(long l) throws IOException {
        if (this.closed) {
            return 0L;
        }
        int n = this.end - this.offset;
        if (!(this.buffer == null || n == 0 && this.interrupted)) {
            l = l > (long)n ? (long)n : l;
            this.offset = (int)((long)this.offset + l);
            return l;
        }
        long l2 = this.demux.skip(l, this.resph);
        if (this.resph.resp.got_headers) {
            this.count = (int)((long)this.count + l2);
        }
        return l2;
    }

    public synchronized int available() throws IOException {
        if (this.closed) {
            return 0;
        }
        if (!(this.buffer == null || this.end - this.offset == 0 && this.interrupted)) {
            return this.end - this.offset;
        }
        return this.demux.available(this.resph);
    }

    public synchronized void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (this.dont_truncate && (this.buffer == null || this.interrupted)) {
                this.readAll(this.resph.resp.timeout);
            }
            this.demux.closeSocketIfAllStreamsClosed();
            if (this.dont_truncate) {
                try {
                    this.resph.resp.http_resp.invokeTrailerHandlers(false);
                }
                catch (ModuleException moduleException) {
                    throw new IOException(moduleException.toString());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void readAll(int n) throws IOException {
        int n2;
        GlobalConstants globalConstants = this.resph.resp;
        synchronized (globalConstants) {
            if (!this.resph.resp.got_headers) {
                n2 = this.resph.resp.timeout;
                this.resph.resp.timeout = n;
                this.resph.resp.getStatusCode();
                this.resph.resp.timeout = n2;
            }
        }
        globalConstants = this;
        synchronized (globalConstants) {
            if (this.buffer != null && !this.interrupted) {
                return;
            }
            n2 = 0;
            try {
                if (this.closed) {
                    this.buffer = new byte[10000];
                    do {
                        this.count += n2;
                    } while ((n2 = this.demux.read(this.buffer, 0, this.buffer.length, this.resph, n)) != -1);
                    this.buffer = null;
                } else {
                    if (this.buffer == null) {
                        this.buffer = new byte[10000];
                        this.offset = 0;
                        this.end = 0;
                    }
                    while ((n2 = this.demux.read(this.buffer, this.end, this.buffer.length - this.end, this.resph, n)) >= 0) {
                        this.count += n2;
                        this.end += n2;
                        this.buffer = Util.resizeArray(this.buffer, this.end + 10000);
                    }
                }
            }
            catch (InterruptedIOException interruptedIOException) {
                this.interrupted = true;
                throw interruptedIOException;
            }
            catch (IOException iOException) {
                this.buffer = null;
            }
            this.interrupted = false;
        }
    }

    synchronized void dontTruncate() {
        this.dont_truncate = true;
    }
}

