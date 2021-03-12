/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.ForwardCascadeDatabase;
import anon.forward.server.ForwardScheduler;
import anon.forward.server.ForwardSchedulerStatistics;
import anon.forward.server.IServerManager;
import anon.forward.server.ServerSocketManager;
import logging.LogHolder;
import logging.LogType;

public class ForwardServerManager {
    public static final int CLIENT_CONNECTION_TIMEOUT = 200000;
    public static final int CLIENT_DUMMYTRAFFIC_INTERVAL = 180000;
    private static ForwardServerManager ms_fsmInstance = null;
    private int m_dummyTrafficInterval = -1;
    private ForwardCascadeDatabase m_allowedCascadesDatabase = new ForwardCascadeDatabase();
    private ForwardScheduler m_forwardScheduler = null;

    public static ForwardServerManager getInstance() {
        if (ms_fsmInstance == null) {
            ms_fsmInstance = new ForwardServerManager();
        }
        return ms_fsmInstance;
    }

    private ForwardServerManager() {
    }

    public void setDummyTrafficInterval(int n) {
        this.m_dummyTrafficInterval = n >= 0 ? Math.min(n, 180000) : 180000;
    }

    public int getDummyTrafficInterval() {
        return this.m_dummyTrafficInterval;
    }

    public ForwardCascadeDatabase getAllowedCascadesDatabase() {
        return this.m_allowedCascadesDatabase;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaximumNumberOfConnections(int n) {
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                this.m_forwardScheduler.setMaximumNumberOfConnections(n);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setNetBandwidth(int n) {
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                this.m_forwardScheduler.setNetBandwidth(n);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object addListenSocket(int n) {
        Object object = null;
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                ServerSocketManager serverSocketManager = new ServerSocketManager(n);
                try {
                    this.m_forwardScheduler.addServerManager(serverSocketManager);
                    object = serverSocketManager.getId();
                    LogHolder.log(7, LogType.NET, "Establishing ServerManager with ID '" + object.toString() + "' was successful.");
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.NET, "Error establishing socket at port " + Integer.toString(n) + ". Reason: " + exception.toString());
                }
            }
        }
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object addServerManager(IServerManager iServerManager) {
        Object object = null;
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                try {
                    this.m_forwardScheduler.addServerManager(iServerManager);
                    object = iServerManager.getId();
                    LogHolder.log(7, LogType.NET, "Establishing ServerManager with ID '" + object.toString() + "' was successful.");
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.NET, "Error adding Servermanager of Type " + iServerManager.getClass().getName() + ". Reason: " + exception.toString());
                }
            }
        }
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeServerManager(Object object) {
        if (object != null) {
            ForwardServerManager forwardServerManager = this;
            synchronized (forwardServerManager) {
                if (this.m_forwardScheduler != null) {
                    this.m_forwardScheduler.removeServerManager(object);
                    LogHolder.log(7, LogType.NET, "ForwardServerManager: removeServerManager: ServerManager with ID '" + object.toString() + "' was removed (if it was running).");
                }
            }
        } else {
            LogHolder.log(2, LogType.NET, "ForwardServerManager: removeServerManager: ServerManager ID null is invalid.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllServerManagers() {
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                this.m_forwardScheduler.removeAllServerManagers();
                LogHolder.log(7, LogType.NET, "ForwardServerManager: removeAllServerManagers: All server managers removed.");
            }
        }
    }

    public boolean isRunning() {
        return this.m_forwardScheduler != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdownForwarding() {
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                this.m_forwardScheduler.shutdown();
                this.m_forwardScheduler = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startForwarding() {
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler == null) {
                this.m_forwardScheduler = new ForwardScheduler();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ForwardSchedulerStatistics getSchedulerStatistics() {
        ForwardSchedulerStatistics forwardSchedulerStatistics = null;
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                forwardSchedulerStatistics = this.m_forwardScheduler.getStatistics();
            }
        }
        return forwardSchedulerStatistics;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCurrentlyForwardedConnections() {
        int n = 0;
        ForwardServerManager forwardServerManager = this;
        synchronized (forwardServerManager) {
            if (this.m_forwardScheduler != null) {
                n = this.m_forwardScheduler.getCurrentlyForwardedConnections();
            }
        }
        return n;
    }
}

