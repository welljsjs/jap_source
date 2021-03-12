/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.TransferVolume;
import java.util.Enumeration;
import java.util.Vector;

public class ForwardSchedulerStatistics {
    private static final long BANDWIDTH_STATISTICS_INTERVAL = 1000L;
    private int m_rejectedConnections = 0;
    private int m_acceptedConnections = 0;
    private long m_transferedBytes = 0L;
    private Vector m_lastTransferVolumes = new Vector();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void incrementRejectedConnections() {
        ForwardSchedulerStatistics forwardSchedulerStatistics = this;
        synchronized (forwardSchedulerStatistics) {
            ++this.m_rejectedConnections;
        }
    }

    public synchronized int getRejectedConnections() {
        int n = 0;
        n = this.m_rejectedConnections;
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void incrementAcceptedConnections() {
        ForwardSchedulerStatistics forwardSchedulerStatistics = this;
        synchronized (forwardSchedulerStatistics) {
            ++this.m_acceptedConnections;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getAcceptedConnections() {
        int n = 0;
        ForwardSchedulerStatistics forwardSchedulerStatistics = this;
        synchronized (forwardSchedulerStatistics) {
            n = this.m_acceptedConnections;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void incrementTransferVolume(int n) {
        Object object = this.m_lastTransferVolumes;
        synchronized (object) {
            this.removeOutdatedTransferVolumes();
            this.m_lastTransferVolumes.addElement(new TransferVolume(n));
        }
        object = this;
        synchronized (object) {
            this.m_transferedBytes += (long)n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCurrentBandwidthUsage() {
        int n = 0;
        Vector vector = this.m_lastTransferVolumes;
        synchronized (vector) {
            this.removeOutdatedTransferVolumes();
            Enumeration enumeration = this.m_lastTransferVolumes.elements();
            while (enumeration.hasMoreElements()) {
                n += ((TransferVolume)enumeration.nextElement()).getVolume();
            }
        }
        return Math.round((float)n * 1000.0f / 1000.0f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getTransferedBytes() {
        long l = 0L;
        ForwardSchedulerStatistics forwardSchedulerStatistics = this;
        synchronized (forwardSchedulerStatistics) {
            l = this.m_transferedBytes;
        }
        return l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeOutdatedTransferVolumes() {
        Vector vector = this.m_lastTransferVolumes;
        synchronized (vector) {
            long l = System.currentTimeMillis();
            boolean bl = true;
            while (this.m_lastTransferVolumes.size() > 0 && bl) {
                if (((TransferVolume)this.m_lastTransferVolumes.firstElement()).getTimeStamp() + 1000L < l) {
                    this.m_lastTransferVolumes.removeElementAt(0);
                    continue;
                }
                bl = false;
            }
        }
    }
}

