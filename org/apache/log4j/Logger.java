/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerFactory;

public class Logger
extends Category {
    private static final String FQCN = (class$org$apache$log4j$Logger == null ? (class$org$apache$log4j$Logger = Logger.class$("org.apache.log4j.Logger")) : class$org$apache$log4j$Logger).getName();
    static /* synthetic */ Class class$org$apache$log4j$Logger;

    protected Logger(String string) {
        super(string);
    }

    public static Logger getLogger(String string) {
        return LogManager.getLogger(string);
    }

    public static Logger getLogger(Class class_) {
        return LogManager.getLogger(class_.getName());
    }

    public static Logger getRootLogger() {
        return LogManager.getRootLogger();
    }

    public static Logger getLogger(String string, LoggerFactory loggerFactory) {
        return LogManager.getLogger(string, loggerFactory);
    }

    public void trace(Object object) {
        if (this.repository.isDisabled(5000)) {
            return;
        }
        if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.TRACE, object, null);
        }
    }

    public void trace(Object object, Throwable throwable) {
        if (this.repository.isDisabled(5000)) {
            return;
        }
        if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.TRACE, object, throwable);
        }
    }

    public boolean isTraceEnabled() {
        if (this.repository.isDisabled(5000)) {
            return false;
        }
        return Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel());
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

