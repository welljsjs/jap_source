/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import HTTPClient.HTTPConnection;
import anon.infoservice.ListenerInterface;

public class HTTPConnectionDescriptor {
    private HTTPConnection connection;
    private ListenerInterface targetInterface;

    public HTTPConnectionDescriptor(HTTPConnection hTTPConnection, ListenerInterface listenerInterface) {
        this.connection = hTTPConnection;
        this.targetInterface = listenerInterface;
    }

    public HTTPConnection getConnection() {
        return this.connection;
    }

    public ListenerInterface getTargetInterface() {
        return this.targetInterface;
    }
}

