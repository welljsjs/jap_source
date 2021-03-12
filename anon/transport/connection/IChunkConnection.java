/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.IChunkReader;
import anon.transport.connection.IChunkWriter;
import anon.transport.connection.IConnection;

public interface IChunkConnection
extends IConnection {
    public IChunkReader getChunkReader();

    public IChunkWriter getChunkWriter();
}

