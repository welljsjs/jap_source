/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.SkypeException;

public interface StreamListener {
    public void textReceived(String var1) throws SkypeException;

    public void datagramReceived(String var1) throws SkypeException;
}

