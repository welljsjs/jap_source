/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net;

import java.util.EventObject;

public class ProtocolCommandEvent
extends EventObject {
    private int __replyCode;
    private boolean __isCommand;
    private String __message;
    private String __command;

    public ProtocolCommandEvent(Object object, String string, String string2) {
        super(object);
        this.__replyCode = 0;
        this.__message = string2;
        this.__isCommand = true;
        this.__command = string;
    }

    public ProtocolCommandEvent(Object object, int n, String string) {
        super(object);
        this.__replyCode = n;
        this.__message = string;
        this.__isCommand = false;
        this.__command = null;
    }

    public String getCommand() {
        return this.__command;
    }

    public int getReplyCode() {
        return this.__replyCode;
    }

    public boolean isCommand() {
        return this.__isCommand;
    }

    public boolean isReply() {
        return !this.isCommand();
    }

    public String getMessage() {
        return this.__message;
    }
}

