/*
 * Decompiled with CFR 0.150.
 */
package jap;

import java.util.Observable;

public class MessageSystem
extends Observable {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendMessage() {
        MessageSystem messageSystem = this;
        synchronized (messageSystem) {
            this.setChanged();
            this.notifyObservers();
        }
    }
}

