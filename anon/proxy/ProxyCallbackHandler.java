/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AnonProxyRequest;
import anon.proxy.ProxyCallback;
import anon.proxy.ProxyCallbackBuffer;
import anon.proxy.ProxyCallbackDelayException;
import anon.proxy.ProxyCallbackNotProcessableException;
import java.util.Enumeration;
import java.util.Vector;

public class ProxyCallbackHandler {
    private Vector callbacks = new Vector();

    public void deliverUpstream(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer) throws ProxyCallbackNotProcessableException, ProxyCallbackDelayException {
        int n = 2;
        ProxyCallback[] arrproxyCallback = this.toArray();
        if (arrproxyCallback != null) {
            for (int i = 0; i < arrproxyCallback.length; ++i) {
                n = arrproxyCallback[i].handleUpstreamChunk(anonProxyRequest, proxyCallbackBuffer);
                if (n == 1) {
                    throw new ProxyCallbackDelayException();
                }
                if (n == 0) break;
            }
        }
    }

    public void deliverDownstream(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer) throws ProxyCallbackNotProcessableException, ProxyCallbackDelayException {
        if (anonProxyRequest == null) {
            throw new NullPointerException("AnonProxyRequest must not be null!");
        }
        int n = 2;
        ProxyCallback[] arrproxyCallback = this.toArray();
        if (arrproxyCallback != null) {
            for (int i = 0; i < arrproxyCallback.length; ++i) {
                n = arrproxyCallback[i].handleDownstreamChunk(anonProxyRequest, proxyCallbackBuffer);
                if (n == 1) {
                    throw new ProxyCallbackDelayException();
                }
                if (n == 0) break;
            }
        }
    }

    public synchronized void closeRequest(AnonProxyRequest anonProxyRequest) {
        if (anonProxyRequest == null) {
            throw new NullPointerException("AnonProxyRequest must not be null!");
        }
        Enumeration enumeration = this.callbacks.elements();
        while (enumeration.hasMoreElements()) {
            ProxyCallback proxyCallback = (ProxyCallback)enumeration.nextElement();
            proxyCallback.closeRequest(anonProxyRequest);
        }
    }

    private synchronized ProxyCallback[] toArray() {
        ProxyCallback[] arrproxyCallback = new ProxyCallback[this.callbacks.size()];
        for (int i = 0; i < arrproxyCallback.length; ++i) {
            arrproxyCallback[i] = (ProxyCallback)this.callbacks.elementAt(i);
        }
        return arrproxyCallback;
    }

    public synchronized void registerProxyCallback(ProxyCallback proxyCallback) {
        if (!this.callbacks.contains(proxyCallback)) {
            this.callbacks.addElement(proxyCallback);
        }
    }

    public synchronized void removeCallback(ProxyCallback proxyCallback) {
        this.callbacks.removeElement(proxyCallback);
    }
}

