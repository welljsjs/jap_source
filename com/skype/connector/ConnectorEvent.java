/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.Connector;
import java.util.Date;
import java.util.EventObject;

class ConnectorEvent
extends EventObject {
    private static final long serialVersionUID = -4743437008394579910L;
    private final long time = System.currentTimeMillis();

    ConnectorEvent(Object object) {
        super(object);
    }

    public final Connector getConnector() {
        return (Connector)this.getSource();
    }

    public final Date getTime() {
        return new Date(this.time);
    }
}

