/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.ForwardConnection;
import anon.forward.server.ForwardSchedulerStatistics;
import anon.forward.server.IServerManager;
import anon.transport.address.Endpoint;
import anon.transport.connection.IStreamConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class ForwardScheduler
implements Runnable {
    private static final long CYCLE_TIME = 100L;
    private int m_nrOfConnections = 0;
    private int m_netBandwidth = 0;
    private Vector m_connectionHandler = new Vector();
    private boolean m_shutdown = false;
    private Thread m_schedulerThread;
    private Hashtable m_serverManagers = new Hashtable();
    private ForwardSchedulerStatistics m_statistics;

    public ForwardScheduler() {
        this.m_schedulerThread = new Thread((Runnable)this, "ForwardScheduler");
        this.m_schedulerThread.setDaemon(true);
        this.m_schedulerThread.start();
        this.m_statistics = new ForwardSchedulerStatistics();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleNewConnection(IStreamConnection iStreamConnection) {
        boolean bl = false;
        ForwardConnection forwardConnection = null;
        Vector vector = this.m_connectionHandler;
        synchronized (vector) {
            if (this.m_connectionHandler.size() < this.m_nrOfConnections && !this.m_shutdown) {
                try {
                    forwardConnection = new ForwardConnection(iStreamConnection, this);
                    this.m_connectionHandler.addElement(forwardConnection);
                    bl = true;
                    LogHolder.log(6, LogType.NET, "ForwardScheduler: handleNewConnection: New forwarding connection from " + Endpoint.toURN(iStreamConnection.getRemoteAddress()) + " accepted.");
                    this.m_statistics.incrementAcceptedConnections();
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.NET, "ForwardScheduler: handleNewConnection: Error initializing protocol on forwarding connection from " + Endpoint.toURN(iStreamConnection.getRemoteAddress()) + " (" + exception.toString() + ").");
                }
            }
        }
        if (!bl) {
            LogHolder.log(6, LogType.NET, "ForwardScheduler: handleNewConnection: New forwarding connection from " + Endpoint.toURN(iStreamConnection.getRemoteAddress()) + " rejected (maximum number of connections is reached).");
            this.m_statistics.incrementRejectedConnections();
            try {
                iStreamConnection.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeConnection(ForwardConnection forwardConnection) {
        Vector vector = this.m_connectionHandler;
        synchronized (vector) {
            this.m_connectionHandler.removeElement(forwardConnection);
        }
        LogHolder.log(6, LogType.NET, "ForwardScheduler: removeConnection: Forwarded connection from " + forwardConnection.toString() + " was closed.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        this.m_shutdown = true;
        this.removeAllServerManagers();
        Vector vector = this.m_connectionHandler;
        synchronized (vector) {
            Enumeration enumeration = this.m_connectionHandler.elements();
            while (enumeration.hasMoreElements()) {
                ((ForwardConnection)enumeration.nextElement()).closeConnection();
            }
        }
        try {
            this.m_schedulerThread.interrupt();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public int getMaximumBandwidth() {
        return this.m_netBandwidth;
    }

    public int getGuaranteedBandwidth() {
        return this.m_netBandwidth / this.m_nrOfConnections;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaximumNumberOfConnections(int n) {
        if (n >= 0) {
            if (n < this.m_nrOfConnections) {
                this.m_nrOfConnections = n;
                Vector vector = this.m_connectionHandler;
                synchronized (vector) {
                    while (this.m_connectionHandler.size() > this.m_nrOfConnections) {
                        try {
                            ((ForwardConnection)this.m_connectionHandler.elementAt((int)Math.round(Math.abs(Math.random() * (double)this.m_connectionHandler.size())))).closeConnection();
                        }
                        catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
                    }
                }
            } else {
                this.m_nrOfConnections = n;
            }
        }
    }

    public void setNetBandwidth(int n) {
        this.m_netBandwidth = n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addServerManager(IServerManager iServerManager) throws Exception {
        Hashtable hashtable = this.m_serverManagers;
        synchronized (hashtable) {
            if (!this.m_shutdown) {
                if (!this.m_serverManagers.containsKey(iServerManager.getId())) {
                    iServerManager.startServerManager(this);
                    this.m_serverManagers.put(iServerManager.getId(), iServerManager);
                } else {
                    throw new Exception("ForwardScheduler: addServerManager: Already a ServerManager with this ID running.");
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeServerManager(Object object) {
        if (object != null) {
            Hashtable hashtable = this.m_serverManagers;
            synchronized (hashtable) {
                IServerManager iServerManager = (IServerManager)this.m_serverManagers.get(object);
                if (iServerManager != null) {
                    iServerManager.shutdown();
                    this.m_serverManagers.remove(object);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllServerManagers() {
        Hashtable hashtable = this.m_serverManagers;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_serverManagers.elements();
            while (enumeration.hasMoreElements()) {
                ((IServerManager)enumeration.nextElement()).shutdown();
            }
            this.m_serverManagers.clear();
        }
    }

    public ForwardSchedulerStatistics getStatistics() {
        return this.m_statistics;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCurrentlyForwardedConnections() {
        int n = 0;
        Vector vector = this.m_connectionHandler;
        synchronized (vector) {
            n = this.m_connectionHandler.size();
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        while (!this.m_shutdown) {
            Vector vector = this.m_connectionHandler;
            synchronized (vector) {
                int n;
                int n2;
                int n3;
                int n4 = this.m_connectionHandler.size();
                int[] arrn = new int[n4];
                Vector<Integer> vector2 = new Vector<Integer>();
                for (n3 = 0; n3 < n4; ++n3) {
                    n2 = 0;
                    for (n = 0; n < n3; ++n) {
                        if (arrn[n] >= arrn[n3]) continue;
                        ++n2;
                    }
                    vector2.insertElementAt(new Integer(n3), n2);
                }
                n3 = this.m_netBandwidth * 100 / 1000;
                if (n4 > 0) {
                    n2 = n3 / n4;
                    for (n = 0; n < n4; ++n) {
                        int n5 = (Integer)vector2.elementAt(n);
                        ((ForwardConnection)this.m_connectionHandler.elementAt(n5)).allowTransfer(n2);
                    }
                }
            }
            long l = (System.currentTimeMillis() / 100L + 1L) * 100L;
            long l2 = System.currentTimeMillis();
            while (l2 < l) {
                try {
                    Thread.sleep(l - l2);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                l2 = System.currentTimeMillis();
            }
        }
    }
}

