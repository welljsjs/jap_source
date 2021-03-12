/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector.win32;

import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorUtils;

public final class Win32Connector
extends Connector {
    private static final int ATTACH_SUCCESS = 0;
    private static final int ATTACH_PENDING_AUTHORIZATION = 1;
    private static final int ATTACH_REFUSED = 2;
    private static final int ATTACH_NOT_AVAILABLE = 3;
    private static final int ATTACH_API_AVAILABLE = 32769;
    private static final String LIBFILENAME_x86 = "skypeforanon.dll";
    private static final String LIBFILENAME_x64 = "skypeforanon_x64.dll";
    private static Win32Connector instance = null;
    private Thread eventDispatcher = null;

    public static synchronized Connector getInstance() {
        if (instance == null) {
            instance = new Win32Connector();
        }
        return instance;
    }

    private Win32Connector() {
    }

    public String getInstalledPath() {
        return this.jni_getInstalledPath();
    }

    private boolean loadNativeLibrary(String string) {
        try {
            System.loadLibrary(string);
            return true;
        }
        catch (Throwable throwable) {
            try {
                String string2;
                if (!ConnectorUtils.checkLibraryInPath(string) && (string2 = ConnectorUtils.extractFromJarToTemp(string)) != null) {
                    System.load(string2);
                    return true;
                }
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
            return false;
        }
    }

    protected void initializeImpl() {
        if (!this.loadNativeLibrary(LIBFILENAME_x86) && !this.loadNativeLibrary(LIBFILENAME_x64)) {
            throw new UnsatisfiedLinkError("SkypeForAN.ON DLL not loaded");
        }
        this.jni_init();
        this.eventDispatcher = new Thread(new Runnable(){

            public void run() {
                Win32Connector.this.jni_windowProc();
            }
        }, "SkypeBridge WindowProc Thread");
        this.eventDispatcher.setDaemon(true);
        this.eventDispatcher.start();
    }

    protected int connect(int n) throws ConnectorException {
        try {
            while (true) {
                int n2;
                this.jni_connect();
                long l = System.currentTimeMillis();
                if ((long)n <= System.currentTimeMillis() - l) {
                    this.setStatus(6);
                }
                if ((n2 = this.getStatus()) != 1 && n2 != 6) {
                    return n2;
                }
                Thread.sleep(1000L);
            }
        }
        catch (InterruptedException interruptedException) {
            throw new ConnectorException("Trying to connect was interrupted.", interruptedException);
        }
    }

    protected void sendApplicationName(String string) throws ConnectorException {
        String string2 = "NAME " + string;
        this.execute(string2, new String[]{string2}, false);
    }

    public void jni_onAttach(int n) {
        switch (n) {
            case 1: {
                this.setStatus(1);
                break;
            }
            case 0: {
                this.setStatus(2);
                break;
            }
            case 2: {
                this.setStatus(3);
                break;
            }
            case 3: {
                this.setStatus(4);
                break;
            }
            case 32769: {
                this.setStatus(5);
                break;
            }
            default: {
                this.setStatus(6);
            }
        }
    }

    public void jni_onSkypeMessage(String string) {
        this.fireMessageReceived(string);
    }

    protected void disposeImpl() {
        throw new UnsupportedOperationException("WindowsConnector#disposeImpl() is not implemented yet.");
    }

    protected void sendCommand(String string) {
        this.jni_sendMessage(string);
    }

    private native void jni_init();

    private native void jni_windowProc();

    private native void jni_sendMessage(String var1);

    private native void jni_connect();

    private native String jni_getInstalledPath();
}

