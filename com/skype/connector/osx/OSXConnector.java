/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector.osx;

import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.osx.AbstractSkypeFrameworkListener;
import com.skype.connector.osx.SkypeFramework;
import com.skype.connector.osx.SkypeFrameworkListener;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public final class OSXConnector
extends Connector {
    private static OSXConnector _instance = null;
    private static boolean _skypeEventLoopEnabled = true;
    private SkypeFrameworkListener listener = new AbstractSkypeFrameworkListener(){

        public void notificationReceived(String string) {
            OSXConnector.this.fireMessageReceived(string);
        }

        public void becameUnavailable() {
            OSXConnector.this.setStatus(4);
        }

        public void becameAvailable() {
        }
    };

    public static synchronized Connector getInstance() {
        if (_instance == null) {
            _instance = new OSXConnector();
        }
        return _instance;
    }

    public static void disableSkypeEventLoop() {
        _skypeEventLoopEnabled = false;
    }

    private OSXConnector() {
    }

    public boolean isRunning() throws ConnectorException {
        ((OSXConnector)OSXConnector.getInstance()).initialize();
        return SkypeFramework.isRunning();
    }

    public String getInstalledPath() {
        File file = new File("/Applications/Skype.app/Contents/MacOS/Skype");
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    protected void initializeImpl() throws ConnectorException {
        SkypeFramework.init(this.getApplicationName());
        SkypeFramework.addSkypeFrameworkListener(this.listener);
        if (_skypeEventLoopEnabled) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            Thread thread = new Thread("SkypeEventLoop"){

                public void run() {
                    countDownLatch.countDown();
                    SkypeFramework.runApplicationEventLoop();
                }
            };
            thread.setDaemon(true);
            thread.start();
            try {
                countDownLatch.await();
            }
            catch (InterruptedException interruptedException) {
                SkypeFramework.quitApplicationEventLoop();
                throw new ConnectorException("The connector initialization was interrupted.", interruptedException);
            }
        }
    }

    protected int connect(int n) throws ConnectorException {
        if (!SkypeFramework.isRunning()) {
            this.setStatus(6);
            return this.getStatus();
        }
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            AbstractSkypeFrameworkListener abstractSkypeFrameworkListener = new AbstractSkypeFrameworkListener(){

                public void attachResponse(int n) {
                    SkypeFramework.removeSkypeFrameworkListener(this);
                    switch (n) {
                        case 0: {
                            OSXConnector.this.setStatus(3);
                            countDownLatch.countDown();
                            break;
                        }
                        case 1: {
                            OSXConnector.this.setStatus(2);
                            countDownLatch.countDown();
                            break;
                        }
                        default: {
                            throw new IllegalStateException("not supported attachResponseCode");
                        }
                    }
                }
            };
            this.setStatus(1);
            SkypeFramework.addSkypeFrameworkListener(abstractSkypeFrameworkListener);
            SkypeFramework.connect();
            countDownLatch.await();
            return this.getStatus();
        }
        catch (InterruptedException interruptedException) {
            throw new ConnectorException("Trying to connect was interrupted.", interruptedException);
        }
    }

    protected void sendProtocol() throws ConnectorException {
    }

    protected void sendCommand(String string) {
        String string2 = SkypeFramework.sendCommand(string);
        if (string2 != null) {
            this.fireMessageReceived(string2);
        }
    }

    protected void disposeImpl() {
        SkypeFramework.removeSkypeFrameworkListener(this.listener);
        SkypeFramework.dispose();
        if (_skypeEventLoopEnabled) {
            SkypeFramework.quitApplicationEventLoop();
        }
    }
}

