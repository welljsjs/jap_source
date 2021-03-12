/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.SkypeException;
import com.skype.Stream;

public interface ApplicationListener {
    public void connected(Stream var1) throws SkypeException;

    public void disconnected(Stream var1) throws SkypeException;
}

