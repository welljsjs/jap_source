/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

public class ListenerList
implements Serializable {
    private Vector __listeners = new Vector();

    public synchronized void addListener(EventListener eventListener) {
        this.__listeners.addElement(eventListener);
    }

    public synchronized void removeListener(EventListener eventListener) {
        this.__listeners.removeElement(eventListener);
    }

    public synchronized Enumeration getListeners() {
        return ((Vector)this.__listeners.clone()).elements();
    }

    public int getListenerCount() {
        return this.__listeners.size();
    }
}

