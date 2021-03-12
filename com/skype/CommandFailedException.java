/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.SkypeException;

public final class CommandFailedException
extends SkypeException {
    private static final long serialVersionUID = 5247715297475793607L;
    private int code;
    private String message;

    CommandFailedException(String string) {
        super(string);
        if (string.startsWith("ERROR ")) {
            string = string.substring("ERROR ".length());
        }
        int n = string.indexOf(32);
        this.code = Integer.parseInt(string.substring(0, n));
        this.message = string.substring(n + 1);
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

