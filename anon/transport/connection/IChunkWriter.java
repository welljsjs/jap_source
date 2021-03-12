/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.ConnectionException;
import java.io.IOException;

public interface IChunkWriter {
    public void writeChunk(byte[] var1) throws ConnectionException;

    public void close() throws IOException;
}

