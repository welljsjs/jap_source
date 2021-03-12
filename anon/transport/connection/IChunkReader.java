/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.ConnectionException;
import java.io.IOException;

public interface IChunkReader {
    public byte[] readChunk() throws ConnectionException;

    public int availableChunks() throws ConnectionException;

    public void close() throws IOException;
}

