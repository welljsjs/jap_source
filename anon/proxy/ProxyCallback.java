/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AnonProxyRequest;
import anon.proxy.ProxyCallbackBuffer;
import anon.proxy.ProxyCallbackNotProcessableException;

public interface ProxyCallback {
    public static final int STATUS_FINISHED = 0;
    public static final int STATUS_DELAY = 1;
    public static final int STATUS_PROCESSABLE = 2;

    public int handleUpstreamChunk(AnonProxyRequest var1, ProxyCallbackBuffer var2) throws ProxyCallbackNotProcessableException;

    public int handleDownstreamChunk(AnonProxyRequest var1, ProxyCallbackBuffer var2) throws ProxyCallbackNotProcessableException;

    public void closeRequest(AnonProxyRequest var1);
}

