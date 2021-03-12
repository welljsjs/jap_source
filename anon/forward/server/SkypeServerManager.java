/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.ForwardScheduler;
import anon.forward.server.IServerManager;
import anon.transport.connection.ChunkConnectionAdapter;
import anon.transport.connection.SkypeConnection;
import com.skype.Application;
import com.skype.ApplicationListener;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Stream;
import logging.LogHolder;
import logging.LogType;

public class SkypeServerManager
implements IServerManager {
    private final String m_appName;
    private ForwardScheduler m_scheduler;
    private Application m_application;
    private RequestListener m_listner;
    private boolean m_isListning;

    public SkypeServerManager(String string) {
        this.m_appName = string;
        this.m_isListning = false;
    }

    public Object getId() {
        return this.toString();
    }

    public synchronized void shutdown() {
        if (!this.m_isListning) {
            return;
        }
        this.m_application.removeApplicationListener(this.m_listner);
        try {
            this.m_application.finish();
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.TRANSPORT, exception);
        }
        this.m_scheduler = null;
        this.m_listner = null;
        this.m_isListning = false;
    }

    public synchronized void startServerManager(ForwardScheduler forwardScheduler) throws Exception {
        if (this.m_isListning) {
            return;
        }
        this.m_scheduler = forwardScheduler;
        this.m_listner = new RequestListener();
        try {
            this.m_application = Skype.addApplication(this.m_appName);
            this.m_application.addApplicationListener(this.m_listner);
            this.m_isListning = true;
        }
        catch (SkypeException skypeException) {
            LogHolder.log(3, LogType.TRANSPORT, "Could not Start Skype forwarding Server.");
            this.shutdown();
            throw skypeException;
        }
    }

    public String toString() {
        return "skype:app(" + this.m_appName + ")";
    }

    private class RequestListener
    implements ApplicationListener {
        private RequestListener() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void connected(Stream stream) throws SkypeException {
            SkypeServerManager skypeServerManager = SkypeServerManager.this;
            synchronized (skypeServerManager) {
                if (!SkypeServerManager.this.m_isListning || SkypeServerManager.this.m_scheduler == null) {
                    stream.disconnect();
                    return;
                }
                SkypeConnection skypeConnection = new SkypeConnection(stream);
                SkypeServerManager.this.m_scheduler.handleNewConnection(new ChunkConnectionAdapter(skypeConnection));
            }
        }

        public void disconnected(Stream stream) throws SkypeException {
        }
    }
}

