/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.HTTPConnection;
import HTTPClient.HttpOutputStream;
import HTTPClient.NVPair;
import HTTPClient.RoRequest;

public final class Request
implements RoRequest,
Cloneable {
    private HTTPConnection connection;
    private String method;
    private String req_uri;
    private NVPair[] headers;
    private byte[] data;
    private HttpOutputStream stream;
    long delay_entity = 0L;
    int num_retries = 0;
    boolean dont_pipeline = false;
    boolean aborted = false;
    boolean internal_subrequest = false;

    public Request(HTTPConnection hTTPConnection, String string, String string2, NVPair[] arrnVPair, byte[] arrby, HttpOutputStream httpOutputStream) {
        this.connection = hTTPConnection;
        this.method = string;
        this.req_uri = string2;
        this.headers = arrnVPair;
        this.data = arrby;
        this.stream = httpOutputStream;
    }

    public HTTPConnection getConnection() {
        return this.connection;
    }

    public void setConnection(HTTPConnection hTTPConnection) {
        this.connection = hTTPConnection;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String string) {
        this.method = string;
    }

    public String getRequestURI() {
        return this.req_uri;
    }

    public void setRequestURI(String string) {
        this.req_uri = string;
    }

    public NVPair[] getHeaders() {
        return this.headers;
    }

    public void setHeaders(NVPair[] arrnVPair) {
        this.headers = arrnVPair;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] arrby) {
        this.data = arrby;
    }

    public HttpOutputStream getStream() {
        return this.stream;
    }

    public void setStream(HttpOutputStream httpOutputStream) {
        this.stream = httpOutputStream;
    }

    public Object clone() {
        Request request;
        try {
            request = (Request)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError(cloneNotSupportedException.toString());
        }
        request.headers = new NVPair[this.headers.length];
        System.arraycopy(this.headers, 0, request.headers, 0, this.headers.length);
        return request;
    }

    public void copyFrom(Request request) {
        this.connection = request.connection;
        this.method = request.method;
        this.req_uri = request.req_uri;
        this.headers = request.headers;
        this.data = request.data;
        this.stream = request.stream;
        this.delay_entity = request.delay_entity;
        this.num_retries = request.num_retries;
        this.dont_pipeline = request.dont_pipeline;
        this.aborted = request.aborted;
        this.internal_subrequest = request.internal_subrequest;
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.method + " " + this.req_uri;
    }
}

