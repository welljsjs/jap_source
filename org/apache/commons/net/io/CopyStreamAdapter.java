/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.util.Enumeration;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.ListenerList;

public class CopyStreamAdapter
implements CopyStreamListener {
    private ListenerList internalListeners = new ListenerList();

    public void bytesTransferred(CopyStreamEvent copyStreamEvent) {
        this.bytesTransferred(copyStreamEvent.getTotalBytesTransferred(), copyStreamEvent.getBytesTransferred(), copyStreamEvent.getStreamSize());
    }

    public void bytesTransferred(long l, int n, long l2) {
        Enumeration enumeration = this.internalListeners.getListeners();
        CopyStreamEvent copyStreamEvent = new CopyStreamEvent(this, l, n, l2);
        while (enumeration.hasMoreElements()) {
            ((CopyStreamListener)enumeration.nextElement()).bytesTransferred(copyStreamEvent);
        }
    }

    public void addCopyStreamListener(CopyStreamListener copyStreamListener) {
        this.internalListeners.addListener(copyStreamListener);
    }

    public void removeCopyStreamListener(CopyStreamListener copyStreamListener) {
        this.internalListeners.removeListener(copyStreamListener);
    }
}

