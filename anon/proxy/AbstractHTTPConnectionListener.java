/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.HTTPConnectionEvent;

public abstract class AbstractHTTPConnectionListener {
    private int m_priority;

    public AbstractHTTPConnectionListener(int n) {
        this.m_priority = n;
    }

    public final int getPriority() {
        return this.m_priority;
    }

    public boolean isBlockable() {
        return true;
    }

    public abstract void requestHeadersReceived(HTTPConnectionEvent var1);

    public abstract void responseHeadersReceived(HTTPConnectionEvent var1);

    public abstract void upstreamContentBytesReceived(HTTPConnectionEvent var1);

    public abstract void downstreamContentBytesReceived(HTTPConnectionEvent var1);

    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        return object.getClass() == this.getClass();
    }

    public final int hashCode() {
        return this.getClass().getName().hashCode();
    }
}

