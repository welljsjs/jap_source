/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Codecs;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.Response;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class ChunkedInputStream
extends FilterInputStream {
    byte[] one = new byte[1];
    private int chunk_len = -1;
    private boolean eof = false;

    ChunkedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public synchronized int read() throws IOException {
        int n = this.read(this.one, 0, 1);
        if (n == 1) {
            return this.one[0] & 0xFF;
        }
        return -1;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        if (this.eof) {
            return -1;
        }
        if (this.chunk_len == -1) {
            try {
                this.chunk_len = Codecs.getChunkLength(this.in);
            }
            catch (ParseException parseException) {
                throw new IOException(parseException.toString());
            }
        }
        if (this.chunk_len > 0) {
            int n3;
            if (n2 > this.chunk_len) {
                n2 = this.chunk_len;
            }
            if ((n3 = this.in.read(arrby, n, n2)) == -1) {
                throw new EOFException("Premature EOF encountered");
            }
            this.chunk_len -= n3;
            if (this.chunk_len == 0) {
                this.in.read();
                this.in.read();
                this.chunk_len = -1;
            }
            return n3;
        }
        Request request = new Request(null, null, null, null, null, null);
        new Response(request, null).readTrailers(this.in);
        this.eof = true;
        return -1;
    }

    public synchronized long skip(long l) throws IOException {
        byte[] arrby = new byte[(int)l];
        int n = this.read(arrby, 0, (int)l);
        if (n > 0) {
            return n;
        }
        return 0L;
    }

    public synchronized int available() throws IOException {
        if (this.eof) {
            return 0;
        }
        if (this.chunk_len != -1) {
            return this.chunk_len + this.in.available();
        }
        return this.in.available();
    }
}

