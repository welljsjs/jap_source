/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.ConnectionException;

public class UnsuportedCommandException
extends ConnectionException {
    private static final long serialVersionUID = 1L;

    public UnsuportedCommandException(Throwable throwable) {
        super(throwable);
    }

    public UnsuportedCommandException(String string) {
        super(string);
    }
}

