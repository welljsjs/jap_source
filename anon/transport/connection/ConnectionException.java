/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

public class ConnectionException
extends Exception {
    private static final long serialVersionUID = 1L;

    public ConnectionException(Throwable throwable) {
        super(throwable == null ? null : throwable.toString());
    }

    public ConnectionException(String string) {
        super(string);
    }
}

