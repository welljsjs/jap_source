/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TelnetOptionHandler;

public class EchoOptionHandler
extends TelnetOptionHandler {
    public EchoOptionHandler(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        super(TelnetOption.ECHO, bl, bl2, bl3, bl4);
    }

    public EchoOptionHandler() {
        super(TelnetOption.ECHO, false, false, false, false);
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

