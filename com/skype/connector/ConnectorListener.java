/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;
import java.util.EventListener;

public interface ConnectorListener
extends EventListener {
    public void messageReceived(ConnectorMessageEvent var1);

    public void messageSent(ConnectorMessageEvent var1);

    public void statusChanged(ConnectorStatusEvent var1);
}

