/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util.encoders;

public class EncoderException
extends IllegalStateException {
    private Throwable cause;

    EncoderException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

