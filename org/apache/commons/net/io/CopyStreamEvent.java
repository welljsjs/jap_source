/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.util.EventObject;

public class CopyStreamEvent
extends EventObject {
    public static final long UNKNOWN_STREAM_SIZE = -1L;
    private int bytesTransferred;
    private long totalBytesTransferred;
    private long streamSize;

    public CopyStreamEvent(Object object, long l, int n, long l2) {
        super(object);
        this.bytesTransferred = n;
        this.totalBytesTransferred = l;
        this.streamSize = l2;
    }

    public int getBytesTransferred() {
        return this.bytesTransferred;
    }

    public long getTotalBytesTransferred() {
        return this.totalBytesTransferred;
    }

    public long getStreamSize() {
        return this.streamSize;
    }
}

