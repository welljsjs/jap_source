/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.Application;
import com.skype.SkypeException;
import com.skype.SkypeExceptionHandler;
import com.skype.User;
import com.skype.Utils;

public final class Skype {
    public static final String LIBRARY_VERSION = "1.0.0.0";
    private static SkypeExceptionHandler defaultExceptionHandler;
    private static SkypeExceptionHandler exceptionHandler;

    public static String getVersion() throws SkypeException {
        return Utils.getProperty("SKYPEVERSION");
    }

    public static Application addApplication(String string) throws SkypeException {
        Utils.checkNotNull("name", string);
        return Application.getInstance(string);
    }

    public static User getUser(String string) {
        return User.getInstance(string);
    }

    public static void setSkypeExceptionHandler(SkypeExceptionHandler skypeExceptionHandler) {
        if (skypeExceptionHandler == null) {
            skypeExceptionHandler = defaultExceptionHandler;
        }
        exceptionHandler = skypeExceptionHandler;
    }

    static void handleUncaughtException(Throwable throwable) {
        exceptionHandler.uncaughtExceptionHappened(throwable);
    }

    private Skype() {
    }

    static {
        exceptionHandler = defaultExceptionHandler = new SkypeExceptionHandler(){

            public void uncaughtExceptionHappened(Throwable throwable) {
            }
        };
    }
}

