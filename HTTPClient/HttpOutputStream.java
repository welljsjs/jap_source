/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Codecs;
import HTTPClient.GlobalConstants;
import HTTPClient.HttpOutputStreamFilter;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class HttpOutputStream
extends OutputStream
implements GlobalConstants {
    private int length;
    private int rcvd = 0;
    private Request req = null;
    private Response resp = null;
    private OutputStream os = null;
    private ByteArrayOutputStream bos = null;
    private Vector filters = new Vector();
    private int con_to = 0;
    private boolean ignore = false;
    private NVPair[] trailers = null;

    public HttpOutputStream() {
        this.length = -1;
    }

    public HttpOutputStream(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Length must be greater equal 0");
        }
        this.length = n;
    }

    void goAhead(Request request, OutputStream outputStream, int n) {
        this.req = request;
        this.con_to = n;
        if (outputStream == null) {
            this.bos = new ByteArrayOutputStream();
            outputStream = this.bos;
        }
        this.os = outputStream;
        int n2 = this.filters.size();
        for (int i = 0; i < n2; ++i) {
            this.os = ((HttpOutputStreamFilter)this.filters.elementAt(i)).pushStream(this.os, request);
        }
    }

    void ignoreData(Request request) {
        this.req = request;
        this.ignore = true;
    }

    synchronized Response getResponse() {
        while (this.resp == null) {
            try {
                this.wait();
            }
            catch (InterruptedException interruptedException) {}
        }
        return this.resp;
    }

    public int getLength() {
        return this.length;
    }

    public void write(int n) throws IOException, IllegalAccessError {
        byte[] arrby = new byte[]{(byte)n};
        this.write(arrby, 0, 1);
    }

    public synchronized void write(byte[] arrby, int n, int n2) throws IOException, IllegalAccessError {
        if (this.req == null) {
            throw new IllegalAccessError("Stream not associated with a request");
        }
        if (this.ignore) {
            return;
        }
        try {
            if (this.length != -1 && this.rcvd + n2 > this.length) {
                throw new IOException("Tried to write too many bytes (" + (this.rcvd + n2) + " > " + this.length + ")");
            }
            if (this.bos != null || this.length != -1) {
                this.os.write(arrby, n, n2);
            } else {
                this.os.write(Codecs.chunkedEncode(arrby, n, n2, null, false));
            }
        }
        catch (IOException iOException) {
            this.req.getConnection().closeDemux(iOException);
            this.req.getConnection().outputFinished();
            throw iOException;
        }
        this.rcvd += n2;
    }

    public synchronized void close() throws IOException, IllegalAccessError {
        if (this.req == null) {
            throw new IllegalAccessError("Stream not associated with a request");
        }
        if (this.ignore) {
            return;
        }
        if (this.bos != null) {
            this.os.close();
            this.req.setData(this.bos.toByteArray());
            this.req.setStream(null);
            if (this.trailers != null) {
                NVPair[] arrnVPair = this.req.getHeaders();
                int n = arrnVPair.length;
                for (int i = 0; i < n; ++i) {
                    if (!arrnVPair[i].getName().equalsIgnoreCase("Trailer")) continue;
                    System.arraycopy(arrnVPair, i + 1, arrnVPair, i, n - i - 1);
                    --n;
                }
                arrnVPair = Util.resizeArray(arrnVPair, n + this.trailers.length);
                System.arraycopy(this.trailers, 0, arrnVPair, n, this.trailers.length);
                this.req.setHeaders(arrnVPair);
            }
            try {
                this.resp = this.req.getConnection().sendRequest(this.req, this.con_to);
            }
            catch (ModuleException moduleException) {
                throw new IOException(moduleException.toString());
            }
            this.notify();
        } else {
            try {
                if (this.length == -1) {
                    this.os.write(Codecs.chunkedEncode(null, 0, 0, this.trailers, true));
                } else if (this.rcvd < this.length) {
                    throw new IOException("Premature close: only " + this.rcvd + " bytes written instead of exptected " + this.length);
                }
                this.os.flush();
            }
            catch (IOException iOException) {
                this.req.getConnection().closeDemux(iOException);
                throw iOException;
            }
            finally {
                this.req.getConnection().outputFinished();
            }
        }
    }

    public void flush() throws IOException {
        this.os.flush();
    }

    public void setTrailers(NVPair[] arrnVPair) throws IllegalAccessError, IllegalStateException {
        if (this.req == null) {
            throw new IllegalAccessError("Stream not associated with a request");
        }
        if (this.length != -1) {
            throw new IllegalStateException("Entity being sent with a Content-length");
        }
        this.trailers = new NVPair[arrnVPair.length];
        System.arraycopy(arrnVPair, 0, this.trailers, 0, arrnVPair.length);
    }

    public NVPair[] getTrailers() {
        NVPair[] arrnVPair = new NVPair[this.trailers.length];
        System.arraycopy(this.trailers, 0, arrnVPair, 0, this.trailers.length);
        return arrnVPair;
    }

    public void addFilter(HttpOutputStreamFilter httpOutputStreamFilter) {
        if (this.req != null) {
            throw new IllegalAccessError("Stream already bound to socket");
        }
        this.filters.addElement(httpOutputStreamFilter);
    }

    public void reset() {
        this.rcvd = 0;
        this.req = null;
        this.resp = null;
        this.os = null;
        this.bos = null;
        this.filters = new Vector();
        this.con_to = 0;
        this.ignore = false;
        this.trailers = null;
    }

    public String toString() {
        return this.getClass().getName() + "[length=" + this.length + "]";
    }
}

