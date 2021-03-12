/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connector;

import anon.transport.address.IAddress;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IConnection;

public interface IConnector {
    public IConnection connect(IAddress var1) throws ConnectionException;
}

