/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector.linux;

import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.linux.SkypeFramework;
import com.skype.connector.linux.SkypeFrameworkListener;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public final class LinuxConnector
extends Connector {
    private static LinuxConnector _instance = null;
    private SkypeFrameworkListener listener = new SkypeFrameworkListener(){

        public void notificationReceived(String string) {
            LinuxConnector.this.fireMessageReceived(string);
        }
    };

    public static synchronized Connector getInstance() {
        if (_instance == null) {
            _instance = new LinuxConnector();
        }
        return _instance;
    }

    private LinuxConnector() {
    }

    public boolean isRunning() throws ConnectorException {
        SkypeFramework.init();
        return SkypeFramework.isRunning();
    }

    public String getInstalledPath() {
        File file = new File("/usr/bin/skype");
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    protected void initializeImpl() throws ConnectorException {
        SkypeFramework.init();
        SkypeFramework.addSkypeFrameworkListener(this.listener);
    }

    protected int connect(int n) throws ConnectorException {
        if (!SkypeFramework.isRunning()) {
            this.setStatus(6);
            return this.getStatus();
        }
        try {
            final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
            SkypeFrameworkListener skypeFrameworkListener = new SkypeFrameworkListener(){

                public void notificationReceived(String string) {
                    if ("OK".equals(string) || "CONNSTATUS OFFLINE".equals(string) || "ERROR 68".equals(string)) {
                        try {
                            linkedBlockingQueue.put(string);
                        }
                        catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            };
            this.setStatus(1);
            SkypeFramework.addSkypeFrameworkListener(skypeFrameworkListener);
            SkypeFramework.sendCommand("NAME " + this.getApplicationName());
            String string = (String)linkedBlockingQueue.take();
            SkypeFramework.removeSkypeFrameworkListener(skypeFrameworkListener);
            if ("OK".equals(string)) {
                this.setStatus(2);
            } else if ("CONNSTATUS OFFLINE".equals(string)) {
                this.setStatus(4);
            } else if ("ERROR 68".equals(string)) {
                this.setStatus(3);
            }
            return this.getStatus();
        }
        catch (InterruptedException interruptedException) {
            throw new ConnectorException("Trying to connect was interrupted.", interruptedException);
        }
    }

    protected void sendCommand(String string) {
        SkypeFramework.sendCommand(string);
    }

    protected void disposeImpl() {
        SkypeFramework.removeSkypeFrameworkListener(this.listener);
        SkypeFramework.dispose();
    }
}

