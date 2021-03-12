/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.util.AbstractMessage;

public final class JAPRoutingMessage
extends AbstractMessage {
    public static final int ROUTING_MODE_CHANGED = 1;
    public static final int PROPAGANDA_INSTANCES_ADDED = 2;
    public static final int START_PROPAGANDA_BEGIN = 3;
    public static final int START_PROPAGANDA_READY = 4;
    public static final int STOP_PROPAGANDA_CALLED = 5;
    public static final int CONNECTION_CLASS_CHANGED = 6;
    public static final int CONNECTION_PARAMETERS_CHANGED = 7;
    public static final int ALLOWED_MIXCASCADES_POLICY_CHANGED = 9;
    public static final int ALLOWED_MIXCASCADES_LIST_CHANGED = 10;
    public static final int REGISTRATION_INFOSERVICES_POLICY_CHANGED = 11;
    public static final int REGISTRATION_INFOSERVICES_LIST_CHANGED = 12;
    public static final int SERVER_STATISTICS_UPDATED = 13;
    public static final int REGISTRATION_STATUS_CHANGED = 14;
    public static final int SERVER_PORT_CHANGED = 15;
    public static final int CLIENT_SETTINGS_CHANGED = 16;
    public static final int FORWARDING_MODE_CHANGED = 17;
    public static final int SKYPE_FORWARDER_CHANGED = 18;

    public JAPRoutingMessage(int n) {
        super(n);
    }

    public JAPRoutingMessage(int n, Object object) {
        super(n, object);
    }
}

