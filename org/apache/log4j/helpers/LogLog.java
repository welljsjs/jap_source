/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import org.apache.log4j.helpers.OptionConverter;

public class LogLog {
    public static final String DEBUG_KEY = "log4j.debug";
    public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
    protected static boolean debugEnabled = false;
    private static boolean quietMode = false;
    private static final String PREFIX = "log4j: ";
    private static final String ERR_PREFIX = "log4j:ERROR ";
    private static final String WARN_PREFIX = "log4j:WARN ";

    public static void setInternalDebugging(boolean bl) {
        debugEnabled = bl;
    }

    public static void debug(String string) {
        if (debugEnabled && !quietMode) {
            System.out.println(PREFIX + string);
        }
    }

    public static void debug(String string, Throwable throwable) {
        if (debugEnabled && !quietMode) {
            System.out.println(PREFIX + string);
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    public static void error(String string) {
        if (quietMode) {
            return;
        }
        System.err.println(ERR_PREFIX + string);
    }

    public static void error(String string, Throwable throwable) {
        if (quietMode) {
            return;
        }
        System.err.println(ERR_PREFIX + string);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public static void setQuietMode(boolean bl) {
        quietMode = bl;
    }

    public static void warn(String string) {
        if (quietMode) {
            return;
        }
        System.err.println(WARN_PREFIX + string);
    }

    public static void warn(String string, Throwable throwable) {
        if (quietMode) {
            return;
        }
        System.err.println(WARN_PREFIX + string);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    static {
        String string = OptionConverter.getSystemProperty(DEBUG_KEY, null);
        if (string == null) {
            string = OptionConverter.getSystemProperty(CONFIG_DEBUG_KEY, null);
        }
        if (string != null) {
            debugEnabled = OptionConverter.toBoolean(string, true);
        }
    }
}

