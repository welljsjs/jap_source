/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.ConnectorEvent;

public final class ConnectorMessageEvent
extends ConnectorEvent {
    private static final long serialVersionUID = -8610258526127376241L;
    private final String message;

    ConnectorMessageEvent(Object object, String string) {
        super(object);
        this.message = string;
    }

    public String getMessage() {
        return this.message;
    }
}

