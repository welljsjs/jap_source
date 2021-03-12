/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ForwardSchedulerStatistics;
import jap.JAPModel;
import jap.forward.JAPRoutingMessage;
import java.util.Observable;
import java.util.Observer;

public class JAPRoutingServerStatisticsListener
extends Observable
implements Observer,
Runnable {
    private static final long SERVER_STATISTICS_UPDATE_INTERVAL = 1000L;
    private int m_rejectedConnections = 0;
    private int m_acceptedConnections = 0;
    private int m_currentlyForwardedConnections = 0;
    private long m_transferedBytes = 0L;
    private int m_currentBandwidthUsage = 0;
    private ForwardSchedulerStatistics m_currentStatisticsInstance = null;
    private Thread m_statisticsThread = null;

    public int getRejectedConnections() {
        return this.m_rejectedConnections;
    }

    public int getAcceptedConnections() {
        return this.m_acceptedConnections;
    }

    public int getCurrentlyForwardedConnections() {
        return this.m_currentlyForwardedConnections;
    }

    public int getCurrentBandwidthUsage() {
        return this.m_currentBandwidthUsage;
    }

    public long getTransferedBytes() {
        return this.m_transferedBytes;
    }

    public void update(Observable observable, Object object) {
        try {
            if (observable == JAPModel.getInstance().getRoutingSettings() && ((JAPRoutingMessage)object).getMessageCode() == 1) {
                if (JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2) {
                    this.startStatistics();
                } else {
                    this.stopStatistics();
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        boolean bl = false;
        while (!bl) {
            boolean bl2 = false;
            JAPRoutingServerStatisticsListener jAPRoutingServerStatisticsListener = this;
            synchronized (jAPRoutingServerStatisticsListener) {
                bl = Thread.interrupted();
                if (!bl) {
                    int n = this.m_rejectedConnections;
                    int n2 = this.m_acceptedConnections;
                    int n3 = this.m_currentlyForwardedConnections;
                    long l = this.m_transferedBytes;
                    int n4 = this.m_currentBandwidthUsage;
                    this.m_rejectedConnections = this.m_currentStatisticsInstance.getRejectedConnections();
                    this.m_acceptedConnections = this.m_currentStatisticsInstance.getAcceptedConnections();
                    this.m_currentlyForwardedConnections = JAPModel.getInstance().getRoutingSettings().getCurrentlyForwardedConnections();
                    this.m_transferedBytes = this.m_currentStatisticsInstance.getTransferedBytes();
                    this.m_currentBandwidthUsage = this.m_currentStatisticsInstance.getCurrentBandwidthUsage();
                    if (n != this.m_rejectedConnections || n2 != this.m_acceptedConnections || n3 != this.m_currentlyForwardedConnections || l != this.m_transferedBytes || n4 != this.m_currentBandwidthUsage) {
                        bl2 = true;
                    }
                }
            }
            if (bl) continue;
            if (bl2) {
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(13));
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                bl = true;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startStatistics() {
        JAPRoutingServerStatisticsListener jAPRoutingServerStatisticsListener = this;
        synchronized (jAPRoutingServerStatisticsListener) {
            this.stopStatistics();
            this.m_currentStatisticsInstance = JAPModel.getInstance().getRoutingSettings().getSchedulerStatistics();
            if (this.m_currentStatisticsInstance != null) {
                this.m_statisticsThread = new Thread(this);
                this.m_statisticsThread.setDaemon(true);
                this.m_statisticsThread.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stopStatistics() {
        JAPRoutingServerStatisticsListener jAPRoutingServerStatisticsListener = this;
        synchronized (jAPRoutingServerStatisticsListener) {
            if (this.m_statisticsThread != null) {
                this.m_statisticsThread.interrupt();
                this.m_statisticsThread = null;
            }
            int n = this.m_rejectedConnections;
            int n2 = this.m_acceptedConnections;
            int n3 = this.m_currentlyForwardedConnections;
            long l = this.m_transferedBytes;
            int n4 = this.m_currentBandwidthUsage;
            this.m_currentStatisticsInstance = null;
            this.m_rejectedConnections = 0;
            this.m_acceptedConnections = 0;
            this.m_currentlyForwardedConnections = 0;
            this.m_transferedBytes = 0L;
            this.m_currentBandwidthUsage = 0;
            if (n != this.m_rejectedConnections || n2 != this.m_acceptedConnections || n3 != this.m_currentlyForwardedConnections || l != this.m_transferedBytes || n4 != this.m_currentBandwidthUsage) {
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(13));
            }
        }
    }
}

