/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TelnetOptionHandler;

public class TerminalTypeOptionHandler
extends TelnetOptionHandler {
    private String termType = null;
    protected static final int TERMINAL_TYPE = 24;
    protected static final int TERMINAL_TYPE_SEND = 1;
    protected static final int TERMINAL_TYPE_IS = 0;

    public TerminalTypeOptionHandler(String string, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        super(TelnetOption.TERMINAL_TYPE, bl, bl2, bl3, bl4);
        this.termType = string;
    }

    public TerminalTypeOptionHandler(String string) {
        super(TelnetOption.TERMINAL_TYPE, false, false, false, false);
        this.termType = string;
    }

    public int[] answerSubnegotiation(int[] arrn, int n) {
        if (arrn != null && n > 1 && this.termType != null && arrn[0] == 24 && arrn[1] == 1) {
            int[] arrn2 = new int[this.termType.length() + 2];
            arrn2[0] = 24;
            arrn2[1] = 0;
            for (int i = 0; i < this.termType.length(); ++i) {
                arrn2[i + 2] = this.termType.charAt(i);
            }
            return arrn2;
        }
        return null;
    }

    public int[] startSubnegotiationLocal() {
        return null;
    }

    public int[] startSubnegotiationRemote() {
        return null;
    }
}

