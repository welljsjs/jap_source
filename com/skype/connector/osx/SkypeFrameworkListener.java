/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector.osx;

import java.util.EventListener;

interface SkypeFrameworkListener
extends EventListener {
    public void becameAvailable();

    public void becameUnavailable();

    public void attachResponse(int var1);

    public void notificationReceived(String var1);
}

