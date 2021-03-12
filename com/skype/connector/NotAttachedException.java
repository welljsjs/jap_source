/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.ConnectorException;

public final class NotAttachedException
extends ConnectorException {
    private static final long serialVersionUID = 8424409627819350472L;
    private final int status;

    NotAttachedException(int n) {
        this.status = n;
    }

    NotAttachedException(int n, Throwable throwable) {
        this(n);
        this.initCause(throwable);
    }

    public int getStatus() {
        return this.status;
    }
}

