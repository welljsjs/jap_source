/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TelnetOptionHandler;

public class SuppressGAOptionHandler
extends TelnetOptionHandler {
    public SuppressGAOptionHandler(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        super(TelnetOption.SUPPRESS_GO_AHEAD, bl, bl2, bl3, bl4);
    }

    public SuppressGAOptionHandler() {
        super(TelnetOption.SUPPRESS_GO_AHEAD, false, false, false, false);
    }

    public int[] answerSubnegotiation(int[] arrn, int n) {
        return null;
    }

    public int[] startSubnegotiationLocal() {
        return null;
    }

    public int[] startSubnegotiationRemote() {
        return null;
    }
}

