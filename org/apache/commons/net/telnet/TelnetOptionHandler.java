/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

public abstract class TelnetOptionHandler {
    private int optionCode = -1;
    private boolean initialLocal = false;
    private boolean initialRemote = false;
    private boolean acceptLocal = false;
    private boolean acceptRemote = false;
    private boolean doFlag = false;
    private boolean willFlag = false;

    public TelnetOptionHandler(int n, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.optionCode = n;
        this.initialLocal = bl;
        this.initialRemote = bl2;
        this.acceptLocal = bl3;
        this.acceptRemote = bl4;
    }

    public int getOptionCode() {
        return this.optionCode;
    }

    public boolean getAcceptLocal() {
        return this.acceptLocal;
    }

    public boolean getAcceptRemote() {
        return this.acceptRemote;
    }

    public void setAcceptLocal(boolean bl) {
        this.acceptLocal = bl;
    }

    public void setAcceptRemote(boolean bl) {
        this.acceptRemote = bl;
    }

    public boolean getInitLocal() {
        return this.initialLocal;
    }

    public boolean getInitRemote() {
        return this.initialRemote;
    }

    public void setInitLocal(boolean bl) {
        this.initialLocal = bl;
    }

    public void setInitRemote(boolean bl) {
        this.initialRemote = bl;
    }

    public abstract int[] answerSubnegotiation(int[] var1, int var2);

    public abstract int[] startSubnegotiationLocal();

    public abstract int[] startSubnegotiationRemote();

    boolean getWill() {
        return this.willFlag;
    }

    void setWill(boolean bl) {
        this.willFlag = bl;
    }

    boolean getDo() {
        return this.doFlag;
    }

    void setDo(boolean bl) {
        this.doFlag = bl;
    }
}

