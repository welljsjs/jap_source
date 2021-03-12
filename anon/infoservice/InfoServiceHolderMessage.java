/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.util.AbstractMessage;

public class InfoServiceHolderMessage
extends AbstractMessage {
    public static final int PREFERRED_INFOSERVICE_CHANGED = 1;
    public static final int INFOSERVICE_MANAGEMENT_CHANGED = 2;
    public static final int INFOSERVICES_NOT_VERIFYABLE = 3;

    public InfoServiceHolderMessage(int n) {
        super(n);
    }

    public InfoServiceHolderMessage(int n, Object object) {
        super(n, object);
    }
}

