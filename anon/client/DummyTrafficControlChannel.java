/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.IServiceContainer;
import anon.client.AbstractControlChannel;
import anon.client.Multiplexer;
import java.util.Observable;
import java.util.Observer;
import logging.LogHolder;
import logging.LogType;

public class DummyTrafficControlChannel
extends AbstractControlChannel
implements Runnable,
Observer {
    public static final int DT_MIN_INTERVAL_MS = 500;
    public static final int DT_MAX_INTERVAL_MS = 30000;
    public static final int DT_DISABLE = Integer.MAX_VALUE;
    private Observable m_observedMultiplexer;
    private volatile boolean m_bRun = false;
    private Thread m_threadRunLoop = null;
    private long m_interval = -1L;
    private Object m_internalSynchronization = new Object();

    public DummyTrafficControlChannel(Multiplexer multiplexer, IServiceContainer iServiceContainer) {
        super(4, multiplexer, iServiceContainer);
        this.m_observedMultiplexer = multiplexer;
    }

    public void run() {
        LogHolder.log(5, LogType.NET, "Dummy traffic interval: " + this.m_interval + "ms");
        while (this.m_bRun) {
            try {
                Thread.sleep(this.m_interval);
                if (!this.m_bRun) continue;
                LogHolder.log(6, LogType.NET, "Sending Dummy!");
                this.sendRawMessage(new byte[0]);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.m_bRun = false;
            this.m_observedMultiplexer.deleteObserver(this);
            if (this.m_threadRunLoop != null) {
                while (this.m_threadRunLoop.isAlive()) {
                    LogHolder.log(5, LogType.NET, "Shutting down dummy traffic channel...");
                    this.m_threadRunLoop.interrupt();
                    try {
                        this.m_threadRunLoop.join();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                LogHolder.log(5, LogType.NET, "Dummy traffic channel closed!");
                this.m_threadRunLoop = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        Object object2 = this.m_internalSynchronization;
        synchronized (object2) {
            if (this.m_threadRunLoop != null) {
                this.m_threadRunLoop.interrupt();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDummyTrafficInterval(int n) {
        boolean bl = false;
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.stop();
            if (n == Integer.MAX_VALUE) {
                LogHolder.log(4, LogType.NET, "Dummy traffic disabled!");
                return;
            }
            if (n < 500) {
                n = 500;
            } else if (n > 30000) {
                n = 30000;
            }
            this.m_interval = n;
            if (n > 0) {
                this.start();
                bl = true;
            }
        }
        if (bl) {
            LogHolder.log(7, LogType.NET, "Sending Dummy!");
            this.sendRawMessage(new byte[0]);
        }
    }

    protected void processPacketData(byte[] arrby) {
        LogHolder.log(7, LogType.NET, "DummyTrafficControlChannel: processPacketData(): Received a dummy-packet.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void start() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (!this.m_bRun) {
                this.m_bRun = true;
                this.m_threadRunLoop = new Thread((Runnable)this, "JAP - Dummy Traffic");
                this.m_threadRunLoop.setDaemon(true);
                this.m_observedMultiplexer.addObserver(this);
                this.m_threadRunLoop.start();
            }
        }
    }
}

