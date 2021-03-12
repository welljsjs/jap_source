/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.ConnectionException;

public class CommunicationException
extends ConnectionException {
    private static final long serialVersionUID = 1L;

    public CommunicationException(String string) {
        super(string);
    }

    public CommunicationException(Throwable throwable) {
        super(throwable);
    }
}

