/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AbstractHTTPConnectionListener;
import anon.proxy.HTTPConnectionEvent;
import anon.proxy.HTTPProxyCallback;

public final class JonDonymXHeaders
extends AbstractHTTPConnectionListener {
    private static final String HTTP_X_JONDONYM_PROXY_KEEP_ALIVE = "X-JonDonym-Proxy-Connection";
    private static final String HTTP_X_JONDONYM_DISTRIBUTION = "X-JonDonym-Distribution";
    public static final String HTTP_X_JONDONYM_PREMIUM = "X-JonDonym-Premium";

    public JonDonymXHeaders(int n) {
        super(n);
    }

    public void handleRequest(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public boolean isBlockable() {
        return false;
    }

    public void downstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void requestHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        if (hTTPConnectionEvent == null) {
            return;
        }
        HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader = hTTPConnectionEvent.getConnectionHeader();
        boolean bl = false;
        if (HTTPProxyCallback.isAnonymityTestDomain(hTTPConnectionHeader)) {
            hTTPConnectionHeader.setRequestHeader(HTTP_X_JONDONYM_DISTRIBUTION, "" + hTTPConnectionEvent.getAnonRequest().getAnonymityDistribution());
            if (bl || hTTPConnectionEvent.getAnonRequest().isProxyKeepAliveEnabled()) {
                hTTPConnectionHeader.setRequestHeader(HTTP_X_JONDONYM_PROXY_KEEP_ALIVE, "keep-alive");
            } else {
                hTTPConnectionHeader.setRequestHeader(HTTP_X_JONDONYM_PROXY_KEEP_ALIVE, "close");
            }
        }
    }

    public void responseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void upstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }
}

