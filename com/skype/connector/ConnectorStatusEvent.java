/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.ConnectorEvent;

public final class ConnectorStatusEvent
extends ConnectorEvent {
    private static final long serialVersionUID = -7285732323922562464L;
    private final int status;

    ConnectorStatusEvent(Object object, int n) {
        super(object);
        this.status = n;
    }

    public int getStatus() {
        return this.status;
    }
}

