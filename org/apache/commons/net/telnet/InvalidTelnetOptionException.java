/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

public class InvalidTelnetOptionException
extends Exception {
    private int optionCode = -1;
    private String msg;

    public InvalidTelnetOptionException(String string, int n) {
        this.optionCode = n;
        this.msg = string;
    }

    public String getMessage() {
        return this.msg + ": " + this.optionCode;
    }
}

