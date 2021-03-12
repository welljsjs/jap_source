/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.IServiceContainer;
import anon.client.Multiplexer;
import anon.client.XmlControlChannel;
import anon.util.XMLUtil;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;

public class TestControlChannel
extends XmlControlChannel
implements Runnable {
    public static final int DT_MIN_INTERVAL_MS = 500;
    public static final int DT_MAX_INTERVAL_MS = 30000;
    public static final int DT_DISABLE = Integer.MAX_VALUE;
    private volatile boolean m_bRun = false;
    private Thread m_threadRunLoop = null;
    private long m_interval = -1L;
    private Object m_internalSynchronization = new Object();

    public TestControlChannel(Multiplexer multiplexer, IServiceContainer iServiceContainer) {
        super(255, multiplexer, iServiceContainer, true);
    }

    public void run() {
        LogHolder.log(5, LogType.NET, "Test control channel sent interval: " + this.m_interval + "ms");
        while (this.m_bRun) {
            try {
                Thread.sleep(this.m_interval);
                if (!this.m_bRun) continue;
                LogHolder.log(6, LogType.NET, "Sending control channel test message!");
                Document document = XMLUtil.createDocument();
                document.appendChild(document.createElement("TestControlChannel"));
                this.sendXmlMessage(document);
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
            if (this.m_threadRunLoop != null) {
                while (this.m_threadRunLoop.isAlive()) {
                    LogHolder.log(5, LogType.NET, "Shutting down test control channel...");
                    this.m_threadRunLoop.interrupt();
                    try {
                        this.m_threadRunLoop.join();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                LogHolder.log(5, LogType.NET, "Test control channel closed!");
                this.m_threadRunLoop = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMessageInterval(int n) {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.stop();
            if (n == Integer.MAX_VALUE) {
                LogHolder.log(4, LogType.NET, "Test control channel sent disabled!");
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
            }
        }
    }

    protected void processXmlMessage(Document document) {
        LogHolder.log(7, LogType.NET, "ControlChannelTest: Received control message:" + XMLUtil.toString(document));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void start() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (!this.m_bRun) {
                this.m_bRun = true;
                this.m_threadRunLoop = new Thread((Runnable)this, "JAP - Control Channel Test");
                this.m_threadRunLoop.setDaemon(true);
                this.m_threadRunLoop.start();
            }
        }
    }
}

