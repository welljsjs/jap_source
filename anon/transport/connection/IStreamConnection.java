/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.IConnection;
import java.io.InputStream;
import java.io.OutputStream;

public interface IStreamConnection
extends IConnection {
    public InputStream getInputStream();

    public OutputStream getOutputStream();
}

