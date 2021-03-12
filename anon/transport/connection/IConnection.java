/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.address.IAddress;
import anon.transport.connection.ConnectionException;
import java.io.IOException;

public interface IConnection {
    public static final int ConnectionState_OPEN = 1;
    public static final int ConnectionState_CLOSE = 2;

    public void setTimeout(int var1) throws ConnectionException;

    public int getTimeout() throws ConnectionException;

    public IAddress getLocalAddress();

    public IAddress getRemoteAddress();

    public int getCurrentState();

    public void close() throws IOException;
}

