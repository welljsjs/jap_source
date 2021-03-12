/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.DefaultProtocolHandler;
import anon.forward.server.ForwardScheduler;
import anon.forward.server.IProtocolHandler;
import anon.transport.address.Endpoint;
import anon.transport.connection.IStreamConnection;
import java.io.InputStream;
import logging.LogHolder;
import logging.LogType;

public class ForwardConnection {
    private IStreamConnection m_clientConnection;
    private IProtocolHandler m_serverConnection;
    private ForwardScheduler m_parentScheduler;
    private boolean m_closeConnection;
    private int m_transferFromClient;
    private int m_transferFromServer;
    private Thread m_clientReadThread;
    private Thread m_serverReadThread;
    private Thread m_timeoutThread;

    public ForwardConnection(IStreamConnection iStreamConnection, ForwardScheduler forwardScheduler) throws Exception {
        this.m_clientConnection = iStreamConnection;
        this.m_parentScheduler = forwardScheduler;
        this.m_serverConnection = new DefaultProtocolHandler(this);
        this.m_transferFromServer = 0;
        this.m_transferFromClient = 0;
        this.m_closeConnection = false;
        this.m_clientReadThread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                InputStream inputStream = ForwardConnection.this.m_clientConnection.getInputStream();
                while (!ForwardConnection.this.m_closeConnection) {
                    block19: {
                        Object object;
                        Object object2 = null;
                        int n = 1;
                        if (ForwardConnection.this.m_transferFromClient > 0) {
                            int n2 = 0;
                            try {
                                n2 = inputStream.available();
                            }
                            catch (Exception exception) {
                                LogHolder.log(3, LogType.NET, exception);
                                ForwardConnection.this.closeConnection();
                            }
                            n = Math.max(Math.min(ForwardConnection.this.m_transferFromClient, n2), n);
                            object = ForwardConnection.this.m_clientReadThread;
                            synchronized (object) {
                                ForwardConnection.this.m_transferFromClient = 0;
                            }
                        }
                        object2 = new byte[n];
                        try {
                            int n3 = inputStream.read((byte[])object2);
                            LogHolder.log(7, LogType.FORWARDING, "ForwardingConnection Client --> Server: We read " + n3 + " Bytes");
                            if (n3 == -1) {
                                LogHolder.log(7, LogType.TRANSPORT, "Close connection with client");
                                ForwardConnection.this.closeConnection();
                                break block19;
                            }
                            if (n3 < ((byte[])object2).length) {
                                object = new byte[n3];
                                System.arraycopy(object2, 0, object, 0, n3);
                                object2 = object;
                            }
                            if (n3 <= 0) break block19;
                            try {
                                ForwardConnection.this.m_timeoutThread.interrupt();
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                            ForwardConnection.this.m_parentScheduler.getStatistics().incrementTransferVolume(n3);
                            LogHolder.log(7, LogType.FORWARDING, n3 + " bytes send to the server (forward from client)");
                            ForwardConnection.this.m_serverConnection.write((byte[])object2);
                        }
                        catch (Exception exception) {
                            LogHolder.log(3, LogType.NET, exception);
                            ForwardConnection.this.closeConnection();
                        }
                    }
                    Thread thread = ForwardConnection.this.m_clientReadThread;
                    synchronized (thread) {
                        if (!ForwardConnection.this.m_closeConnection) {
                            try {
                                ForwardConnection.this.m_clientReadThread.wait();
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    }
                }
            }
        }, "Client to server forwarding");
        this.m_clientReadThread.setDaemon(true);
        this.m_serverReadThread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                while (!ForwardConnection.this.m_closeConnection) {
                    Thread thread;
                    block19: {
                        byte[] arrby = null;
                        thread = ForwardConnection.this.m_serverReadThread;
                        synchronized (thread) {
                            arrby = new byte[ForwardConnection.this.m_transferFromServer];
                            try {
                                int n = ForwardConnection.this.m_serverConnection.available();
                                if (arrby.length > n) {
                                    arrby = new byte[n];
                                }
                            }
                            catch (Exception exception) {
                                LogHolder.log(3, LogType.NET, exception);
                                ForwardConnection.this.closeConnection();
                            }
                            ForwardConnection.this.m_transferFromServer = 0;
                        }
                        try {
                            int n = ForwardConnection.this.m_serverConnection.read(arrby);
                            if (n == -1) {
                                ForwardConnection.this.closeConnection();
                                break block19;
                            }
                            if (n < arrby.length) {
                                byte[] arrby2 = new byte[n];
                                System.arraycopy(arrby, 0, arrby2, 0, n);
                                arrby = arrby2;
                            }
                            if (n <= 0) break block19;
                            try {
                                ForwardConnection.this.m_timeoutThread.interrupt();
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                            ForwardConnection.this.m_parentScheduler.getStatistics().incrementTransferVolume(n);
                            LogHolder.log(7, LogType.FORWARDING, n + " bytes send to the client (forward from )");
                            ForwardConnection.this.m_clientConnection.getOutputStream().write(arrby);
                            ForwardConnection.this.m_clientConnection.getOutputStream().flush();
                        }
                        catch (Exception exception) {
                            LogHolder.log(3, LogType.NET, exception);
                            ForwardConnection.this.closeConnection();
                        }
                    }
                    thread = ForwardConnection.this.m_serverReadThread;
                    synchronized (thread) {
                        if (!ForwardConnection.this.m_closeConnection) {
                            try {
                                ForwardConnection.this.m_serverReadThread.wait();
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    }
                }
            }
        }, "Server to client forwarding");
        this.m_serverReadThread.setDaemon(true);
        this.m_timeoutThread = new Thread(new Runnable(){

            public void run() {
                while (!ForwardConnection.this.m_closeConnection) {
                    try {
                        Thread.sleep(200000L);
                        ForwardConnection.this.closeConnection();
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        }, "Client timeout thread");
        this.m_timeoutThread.setDaemon(true);
        this.m_clientReadThread.start();
        this.m_serverReadThread.start();
        this.m_timeoutThread.start();
    }

    public int getAvailableBytes() {
        int n = 0;
        try {
            n = this.m_clientConnection.getInputStream().available() + this.m_serverConnection.available();
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.NET, exception);
            this.closeConnection();
        }
        return n;
    }

    public ForwardScheduler getParentScheduler() {
        return this.m_parentScheduler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeConnection() {
        boolean bl = false;
        Thread thread = this.m_serverReadThread;
        synchronized (thread) {
            Thread thread2 = this.m_clientReadThread;
            synchronized (thread2) {
                bl = this.m_closeConnection;
                this.m_closeConnection = true;
            }
        }
        if (!bl) {
            try {
                this.m_clientConnection.close();
                if (this.m_serverConnection != null) {
                    this.m_serverConnection.close();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.m_parentScheduler.removeConnection(this);
            thread = this.m_clientReadThread;
            synchronized (thread) {
                this.m_clientReadThread.notify();
            }
            thread = this.m_serverReadThread;
            synchronized (thread) {
                this.m_serverReadThread.notify();
            }
            try {
                this.m_timeoutThread.interrupt();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void allowTransfer(int n) {
        int n2 = 0;
        int n3 = 0;
        n2 = n / 2;
        n3 = n / 2;
        Thread thread = this.m_clientReadThread;
        synchronized (thread) {
            this.m_transferFromClient = n3;
            this.m_clientReadThread.notify();
        }
        thread = this.m_serverReadThread;
        synchronized (thread) {
            this.m_transferFromServer = n2;
            this.m_serverReadThread.notify();
        }
    }

    public String toString() {
        return Endpoint.toURN(this.m_clientConnection.getRemoteAddress());
    }
}

