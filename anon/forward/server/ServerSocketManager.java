/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.ForwardScheduler;
import anon.forward.server.IServerManager;
import anon.transport.connection.SocketConnection;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager
implements Runnable,
IServerManager {
    private static final int MAXIMUM_CONNECTION_REQUESTS = 5;
    private ServerSocket m_serverSocket;
    private Thread m_managerThread;
    private ForwardScheduler m_parentScheduler;
    private int m_portNumber;

    public ServerSocketManager(int n) {
        this.m_portNumber = n;
    }

    public Object getId() {
        return this.getClass().getName() + "%" + Integer.toString(this.m_portNumber);
    }

    public void startServerManager(ForwardScheduler forwardScheduler) throws Exception {
        this.m_serverSocket = new ServerSocket(this.m_portNumber, 5);
        this.m_serverSocket.setSoTimeout(0);
        this.m_parentScheduler = forwardScheduler;
        this.m_managerThread = new Thread(this);
        this.m_managerThread.setDaemon(true);
        this.m_managerThread.start();
    }

    public void shutdown() {
        try {
            this.m_serverSocket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.m_managerThread.join();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void run() {
        boolean bl = false;
        while (!bl) {
            Socket socket = null;
            try {
                socket = this.m_serverSocket.accept();
            }
            catch (Exception exception) {
                bl = true;
            }
            if (bl) continue;
            try {
                socket.setSoTimeout(200000);
                this.m_parentScheduler.handleNewConnection(new SocketConnection(socket));
            }
            catch (Exception exception) {}
        }
    }
}

