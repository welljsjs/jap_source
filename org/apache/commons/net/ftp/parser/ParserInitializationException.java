/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

public class ParserInitializationException
extends RuntimeException {
    private final Throwable rootCause;

    public ParserInitializationException(String string) {
        super(string);
        this.rootCause = null;
    }

    public ParserInitializationException(String string, Throwable throwable) {
        super(string);
        this.rootCause = throwable;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }
}

