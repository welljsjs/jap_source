/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;

public abstract class AbstractConnectorListener
implements ConnectorListener {
    public void messageReceived(ConnectorMessageEvent connectorMessageEvent) {
    }

    public void messageSent(ConnectorMessageEvent connectorMessageEvent) {
    }

    public void statusChanged(ConnectorStatusEvent connectorStatusEvent) {
    }
}

