/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AnonProxyRequest;
import anon.proxy.HTTPProxyCallback;

public class HTTPConnectionEvent {
    private HTTPProxyCallback.HTTPConnectionHeader connectionHeader;
    private volatile long upStreamContentBytes;
    private volatile long downStreamContentBytes;
    private AnonProxyRequest anonRequest;

    public HTTPConnectionEvent() {
    }

    HTTPConnectionEvent(HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader, long l, long l2, AnonProxyRequest anonProxyRequest) {
        this.connectionHeader = hTTPConnectionHeader;
        this.upStreamContentBytes = l;
        this.downStreamContentBytes = l2;
        this.anonRequest = anonProxyRequest;
    }

    public HTTPProxyCallback.HTTPConnectionHeader getConnectionHeader() {
        return this.connectionHeader;
    }

    public void setConnectionHeader(HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader) {
        this.connectionHeader = hTTPConnectionHeader;
    }

    public long getUpStreamContentBytes() {
        return this.upStreamContentBytes;
    }

    public void setUpStreamContentBytes(long l) {
        this.upStreamContentBytes = l;
    }

    public long getDownStreamContentBytes() {
        return this.downStreamContentBytes;
    }

    public void setDownStreamContentBytes(long l) {
        this.downStreamContentBytes = l;
    }

    public AnonProxyRequest getAnonRequest() {
        return this.anonRequest;
    }

    public void setAnonRequest(AnonProxyRequest anonProxyRequest) {
        this.anonRequest = anonProxyRequest;
    }
}

