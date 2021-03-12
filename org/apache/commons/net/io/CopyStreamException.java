/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.IOException;

public class CopyStreamException
extends IOException {
    private long totalBytesTransferred;
    private IOException ioException;

    public CopyStreamException(String string, long l, IOException iOException) {
        super(string);
        this.totalBytesTransferred = l;
        this.ioException = iOException;
    }

    public long getTotalBytesTransferred() {
        return this.totalBytesTransferred;
    }

    public IOException getIOException() {
        return this.ioException;
    }
}

