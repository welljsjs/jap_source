/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector.osx;

import com.skype.connector.ConnectorUtils;
import com.skype.connector.LoadLibraryException;
import com.skype.connector.osx.SkypeFrameworkListener;
import java.util.Enumeration;
import java.util.Vector;

final class SkypeFramework {
    private static Object initializedFieldMutex = new Object();
    private static boolean initialized = false;
    private static final Vector listeners = new Vector();
    private static Object sendCommandMutex = new Object();
    private static Object notificationReceivedMutex = new Object();

    SkypeFramework() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void init(String string) throws LoadLibraryException {
        ConnectorUtils.checkNotNull("applicationName", string);
        Object object = initializedFieldMutex;
        synchronized (object) {
            if (!initialized) {
                ConnectorUtils.loadLibrary("skype");
                SkypeFramework.setup0(string);
                initialized = true;
            }
        }
    }

    private static native void setup0(String var0);

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

    static boolean isAvailable() {
        return SkypeFramework.isAvailable0();
    }

    private static native boolean isAvailable0();

    static void fireBecameAvailable() {
        Enumeration enumeration = listeners.elements();
        while (enumeration.hasMoreElements()) {
            ((SkypeFrameworkListener)enumeration.nextElement()).becameAvailable();
        }
    }

    static void fireBecameUnavailable() {
        Enumeration enumeration = listeners.elements();
        while (enumeration.hasMoreElements()) {
            ((SkypeFrameworkListener)enumeration.nextElement()).becameUnavailable();
        }
    }

    static void connect() {
        SkypeFramework.connect0();
    }

    private static native void connect0();

    static void fireAttachResponse(int n) {
        Enumeration enumeration = listeners.elements();
        while (enumeration.hasMoreElements()) {
            ((SkypeFrameworkListener)enumeration.nextElement()).attachResponse(n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String sendCommand(String string) {
        Object object = sendCommandMutex;
        synchronized (object) {
            return SkypeFramework.sendCommand0(string);
        }
    }

    private static native String sendCommand0(String var0);

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
                SkypeFramework.dispose0();
                listeners.clear();
                initialized = false;
            }
        }
    }

    private static native void dispose0();

    static int runCurrentEventLoop(double d) {
        return SkypeFramework.runCurrentEventLoop0(d);
    }

    private static native int runCurrentEventLoop0(double var0);

    static void runApplicationEventLoop() {
        SkypeFramework.runApplicationEventLoop0();
    }

    private static native void runApplicationEventLoop0();

    static void quitApplicationEventLoop() {
        SkypeFramework.quitApplicationEventLoop0();
    }

    private static native void quitApplicationEventLoop0();
}

