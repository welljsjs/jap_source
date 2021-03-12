/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.util.AbstractMessage;

public class DatabaseMessage
extends AbstractMessage {
    public static final int ENTRY_ADDED = 1;
    public static final int ENTRY_RENEWED = 2;
    public static final int ENTRY_REMOVED = 3;
    public static final int ALL_ENTRIES_REMOVED = 4;
    public static final int INITIAL_OBSERVER_MESSAGE = 5;

    public DatabaseMessage(int n) {
        super(n);
    }

    public DatabaseMessage(int n, Object object) {
        super(n, object);
    }
}

