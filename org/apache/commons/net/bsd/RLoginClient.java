/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.bsd;

import java.io.IOException;
import org.apache.commons.net.bsd.RCommandClient;

public class RLoginClient
extends RCommandClient {
    public static final int DEFAULT_PORT = 513;

    public RLoginClient() {
        this.setDefaultPort(513);
    }

    public void rlogin(String string, String string2, String string3, int n) throws IOException {
        this.rexec(string, string2, string3 + "/" + n, false);
    }

    public void rlogin(String string, String string2, String string3) throws IOException {
        this.rexec(string, string2, string3, false);
    }
}

