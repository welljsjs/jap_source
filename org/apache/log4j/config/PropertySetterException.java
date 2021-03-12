/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.config;

public class PropertySetterException
extends Exception {
    protected Throwable rootCause;

    public PropertySetterException(String string) {
        super(string);
    }

    public PropertySetterException(Throwable throwable) {
        this.rootCause = throwable;
    }

    public String getMessage() {
        String string = super.getMessage();
        if (string == null && this.rootCause != null) {
            string = this.rootCause.getMessage();
        }
        return string;
    }
}

