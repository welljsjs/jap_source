/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector.linux;

import com.skype.connector.ConnectorUtils;
import com.skype.connector.LoadLibraryException;
import com.skype.connector.linux.SkypeFrameworkListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

final class SkypeFramework {
    private static Object initializedFieldMutex = new Object();
    private static Object readWriteMutex = new Object();
    private static boolean initialized = false;
    private static CountDownLatch eventLoopFinishedLatch;
    private static Thread eventLoop;
    private static final Vector listeners;

    SkypeFramework() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void init() throws LoadLibraryException {
        Object object = initializedFieldMutex;
        synchronized (object) {
            if (!initialized) {
                try {
                    ConnectorUtils.loadLibrary("skype_dbus_x86");
                }
                catch (Exception exception) {
                    ConnectorUtils.loadLibrary("skype_dbus_x64");
                }
                SkypeFramework.setup0();
                eventLoopFinishedLatch = new CountDownLatch(1);
                eventLoop = new Thread(new Runnable(){

                    public void run() {
                        SkypeFramework.runEventLoop0();
                        eventLoopFinishedLatch.countDown();
                    }
                }, "Skype4Java Event Loop");
                eventLoop.setDaemon(true);
                eventLoop.start();
                initialized = true;
            }
        }
    }

    private static native void setup0();

    private static native void runEventLoop0();

    static void addSkypeFrameworkListener(SkypeFrameworkListener skypeFrameworkListener) {
        listeners.add(skypeFrameworkListener);
    }

    static void removeSkypeFrameworkListener(SkypeFrameworkListener skypeFrameworkListener) {
        listeners.remove(skypeFrameworkListener);
    }

    static boolean isRunning() {
        return SkypeFramework.isRunning0();
    }

    private static native boolean isRunning0();

    private static native void sendCommand0(String var0);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void sendCommand(String string) {
        Object object = readWriteMutex;
        synchronized (object) {
            SkypeFramework.sendCommand0(string);
        }
    }

    static void fireNotificationReceived(String string) {
        Enumeration enumeration = listeners.elements();
        while (enumeration.hasMoreElements()) {
            ((SkypeFrameworkListener)enumeration.nextElement()).notificationReceived(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void dispose() {
        Object object = initializedFieldMutex;
        synchronized (object) {
            if (initialized) {
                listeners.clear();
                SkypeFramework.stopEventLoop0();
                try {
                    eventLoopFinishedLatch.await();
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                SkypeFramework.closeDisplay0();
                initialized = false;
            }
        }
    }

    private static native void stopEventLoop0();

    private static native void closeDisplay0();

    static {
        listeners = new Vector();
    }
}

