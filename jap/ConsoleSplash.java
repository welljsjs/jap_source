/*
 * Decompiled with CFR 0.150.
 */
package jap;

import jap.ISplashResponse;
import logging.LogHolder;
import logging.LogType;

public class ConsoleSplash
implements ISplashResponse {
    private String m_lastMessage;

    public void setText(String string) {
        if (string != null && string.trim().length() > 0) {
            this.m_lastMessage = string;
            LogHolder.log(1, LogType.MISC, string + "...");
        }
    }

    public String getText() {
        return this.m_lastMessage;
    }
}

