/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.io.Serializable;
import java.util.Enumeration;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.util.ListenerList;

public class ProtocolCommandSupport
implements Serializable {
    private Object __source;
    private ListenerList __listeners = new ListenerList();

    public ProtocolCommandSupport(Object object) {
        this.__source = object;
    }

    public void fireCommandSent(String string, String string2) {
        Enumeration enumeration = this.__listeners.getListeners();
        ProtocolCommandEvent protocolCommandEvent = new ProtocolCommandEvent(this.__source, string, string2);
        while (enumeration.hasMoreElements()) {
            ProtocolCommandListener protocolCommandListener = (ProtocolCommandListener)enumeration.nextElement();
            protocolCommandListener.protocolCommandSent(protocolCommandEvent);
        }
    }

    public void fireReplyReceived(int n, String string) {
        Enumeration enumeration = this.__listeners.getListeners();
        ProtocolCommandEvent protocolCommandEvent = new ProtocolCommandEvent(this.__source, n, string);
        while (enumeration.hasMoreElements()) {
            ProtocolCommandListener protocolCommandListener = (ProtocolCommandListener)enumeration.nextElement();
            protocolCommandListener.protocolReplyReceived(protocolCommandEvent);
        }
    }

    public void addProtocolCommandListener(ProtocolCommandListener protocolCommandListener) {
        this.__listeners.addListener(protocolCommandListener);
    }

    public void removeProtocolCommandListener(ProtocolCommandListener protocolCommandListener) {
        this.__listeners.removeListener(protocolCommandListener);
    }

    public int getListenerCount() {
        return this.__listeners.getListenerCount();
    }
}

