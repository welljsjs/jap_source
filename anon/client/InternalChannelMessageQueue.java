/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.InternalChannelMessage;
import java.util.Observable;
import java.util.Vector;

public class InternalChannelMessageQueue
extends Observable {
    private Vector m_messageQueue = new Vector();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addChannelMessage(InternalChannelMessage internalChannelMessage) {
        Vector vector = this.m_messageQueue;
        synchronized (vector) {
            this.m_messageQueue.addElement(internalChannelMessage);
            this.m_messageQueue.notify();
        }
        this.setChanged();
        this.notifyObservers(internalChannelMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InternalChannelMessage getFirstMessage() {
        InternalChannelMessage internalChannelMessage = null;
        Vector vector = this.m_messageQueue;
        synchronized (vector) {
            if (this.m_messageQueue.size() > 0) {
                internalChannelMessage = (InternalChannelMessage)this.m_messageQueue.firstElement();
            }
        }
        return internalChannelMessage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFirstMessage() {
        Vector vector = this.m_messageQueue;
        synchronized (vector) {
            if (this.m_messageQueue.size() > 0) {
                this.m_messageQueue.removeElementAt(0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InternalChannelMessage waitForNextMessage() throws InterruptedException {
        InternalChannelMessage internalChannelMessage;
        Vector vector = this.m_messageQueue;
        synchronized (vector) {
            while (this.m_messageQueue.size() == 0) {
                this.m_messageQueue.wait();
            }
            internalChannelMessage = (InternalChannelMessage)this.m_messageQueue.firstElement();
        }
        return internalChannelMessage;
    }
}

