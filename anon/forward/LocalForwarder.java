/*
 * Decompiled with CFR 0.150.
 */
package anon.forward;

import anon.forward.LocalAddress;
import anon.forward.server.ForwardScheduler;
import anon.forward.server.ForwardServerManager;
import anon.forward.server.IServerManager;
import anon.transport.address.IAddress;
import anon.transport.connection.ChunkConnectionAdapter;
import anon.transport.connection.CommunicationException;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IConnection;
import anon.transport.connection.IStreamConnection;
import anon.transport.connection.RequestException;
import anon.transport.connection.util.QueuedChunkConnection;
import anon.transport.connector.IConnector;
import anon.util.ObjectQueue;
import logging.LogHolder;
import logging.LogType;

public class LocalForwarder
implements IServerManager,
IConnector {
    private static LocalForwarder m_instance = new LocalForwarder();
    private static Object m_currentServerManagerId = null;
    private ForwardScheduler m_scheduler;
    private boolean m_isListing = false;

    public static void registerLocalForwarder(int n) {
        if (m_currentServerManagerId != null) {
            return;
        }
        ForwardServerManager.getInstance().startForwarding();
        ForwardServerManager.getInstance().setNetBandwidth(n);
        ForwardServerManager.getInstance().setMaximumNumberOfConnections(1);
        m_currentServerManagerId = ForwardServerManager.getInstance().addServerManager(m_instance);
        LogHolder.log(5, LogType.TRANSPORT, "Local Forwarder registert");
    }

    public static void unregisterLocalForwarder() {
        if (m_currentServerManagerId == null) {
            return;
        }
        ForwardServerManager.getInstance().shutdownForwarding();
        ForwardServerManager.getInstance().removeServerManager(m_currentServerManagerId);
        m_currentServerManagerId = null;
        LogHolder.log(5, LogType.TRANSPORT, "Local Forwarder removed");
    }

    public static IServerManager getServerManager() {
        return m_instance;
    }

    public static IConnector getConnector() {
        return m_instance;
    }

    private LocalForwarder() {
    }

    public Object getId() {
        return this.getClass().getName();
    }

    public void shutdown() {
        this.m_isListing = false;
        this.m_scheduler = null;
    }

    public void startServerManager(ForwardScheduler forwardScheduler) throws Exception {
        this.m_scheduler = forwardScheduler;
        this.m_isListing = true;
        LogHolder.log(5, LogType.TRANSPORT, "Local Forwarder listning");
    }

    public IStreamConnection connect(LocalAddress localAddress) throws ConnectionException {
        if (!this.m_isListing) {
            throw new CommunicationException("Remoteend could not be reached");
        }
        ObjectQueue objectQueue = new ObjectQueue();
        ObjectQueue objectQueue2 = new ObjectQueue();
        ChunkConnectionAdapter chunkConnectionAdapter = new ChunkConnectionAdapter(new QueuedChunkConnection(objectQueue, objectQueue2));
        ChunkConnectionAdapter chunkConnectionAdapter2 = new ChunkConnectionAdapter(new QueuedChunkConnection(objectQueue2, objectQueue));
        this.m_scheduler.handleNewConnection(chunkConnectionAdapter);
        if (chunkConnectionAdapter.getCurrentState() == 2) {
            throw new RequestException("Reques denied for unknown Reason");
        }
        return chunkConnectionAdapter2;
    }

    public IConnection connect(IAddress iAddress) throws ConnectionException {
        return null;
    }
}

