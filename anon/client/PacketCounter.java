/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.MixPacket;
import anon.client.PacketProcessedEvent;
import java.util.Observable;
import java.util.Observer;

public class PacketCounter
extends Observable
implements Observer {
    private volatile long m_processedDataPackets;
    private volatile long m_payPacketCounter;
    private Object m_internalSynchronization;

    public PacketCounter(long l) {
        this.m_processedDataPackets = l > 0L ? l : 0L;
        this.m_payPacketCounter = 0L;
        this.m_internalSynchronization = new Object();
    }

    public PacketCounter() {
        this(0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        if (object instanceof PacketProcessedEvent) {
            int n = ((PacketProcessedEvent)object).getCode();
            Object object2 = this.m_internalSynchronization;
            synchronized (object2) {
                switch (n) {
                    case 1: 
                    case 3: 
                    case 5: {
                        ++this.m_processedDataPackets;
                        ++this.m_payPacketCounter;
                        this.setChanged();
                        break;
                    }
                }
                this.notifyObservers();
            }
        }
    }

    public long getProcessedPackets() {
        return this.m_processedDataPackets;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getAndResetBytesForPayment() {
        long l = 0L;
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            l = this.m_payPacketCounter * (long)MixPacket.getPacketSize();
            this.m_payPacketCounter = 0L;
        }
        return l;
    }
}

