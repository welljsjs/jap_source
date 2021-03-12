/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

public class Loader {
    static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    private static boolean java1;
    private static boolean ignoreTCL;
    static /* synthetic */ Class class$org$apache$log4j$helpers$Loader;
    static /* synthetic */ Class class$java$lang$Thread;

    public static URL getResource(String string, Class class_) {
        return Loader.getResource(string);
    }

    public static URL getResource(String string) {
        ClassLoader classLoader = null;
        URL uRL = null;
        try {
            if (!java1 && (classLoader = Loader.getTCL()) != null) {
                LogLog.debug("Trying to find [" + string + "] using context classloader " + classLoader + ".");
                uRL = classLoader.getResource(string);
                if (uRL != null) {
                    return uRL;
                }
            }
            if ((classLoader = (class$org$apache$log4j$helpers$Loader == null ? (class$org$apache$log4j$helpers$Loader = Loader.class$("org.apache.log4j.helpers.Loader")) : class$org$apache$log4j$helpers$Loader).getClassLoader()) != null) {
                LogLog.debug("Trying to find [" + string + "] using " + classLoader + " class loader.");
                uRL = classLoader.getResource(string);
                if (uRL != null) {
                    return uRL;
                }
            }
        }
        catch (Throwable throwable) {
            LogLog.warn(TSTR, throwable);
        }
        LogLog.debug("Trying to find [" + string + "] using ClassLoader.getSystemResource().");
        return ClassLoader.getSystemResource(string);
    }

    public static boolean isJava1() {
        return java1;
    }

    private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException {
        Method method = null;
        try {
            method = (class$java$lang$Thread == null ? (class$java$lang$Thread = Loader.class$("java.lang.Thread")) : class$java$lang$Thread).getMethod("getContextClassLoader", null);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
        return (ClassLoader)method.invoke(Thread.currentThread(), null);
    }

    public static Class loadClass(String string) throws ClassNotFoundException {
        if (java1 || ignoreTCL) {
            return Class.forName(string);
        }
        try {
            return Loader.getTCL().loadClass(string);
        }
        catch (Throwable throwable) {
            return Class.forName(string);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        String string;
        int n;
        java1 = true;
        ignoreTCL = false;
        String string2 = OptionConverter.getSystemProperty("java.version", null);
        if (string2 != null && (n = string2.indexOf(46)) != -1 && string2.charAt(n + 1) != '1') {
            java1 = false;
        }
        if ((string = OptionConverter.getSystemProperty("log4j.ignoreTCL", null)) != null) {
            ignoreTCL = OptionConverter.toBoolean(string, true);
        }
    }
}

