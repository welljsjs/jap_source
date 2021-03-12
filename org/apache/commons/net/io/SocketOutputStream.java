/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketOutputStream
extends FilterOutputStream {
    private Socket __socket;

    public SocketOutputStream(Socket socket, OutputStream outputStream) {
        super(outputStream);
        this.__socket = socket;
    }

    public void write(byte[] arrby, int n, int n2) throws IOException {
        this.out.write(arrby, n, n2);
    }

    public void close() throws IOException {
        super.close();
        this.__socket.close();
    }
}

