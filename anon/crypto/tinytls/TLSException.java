/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls;

import java.io.IOException;

public class TLSException
extends IOException {
    public static final int LEVEL_WARNING = 1;
    public static final int LEVEL_FATAL = 2;
    public static final String MSG_EOF = "EOF";
    public static final int DESC_CLOSE_NOTIFY = 0;
    private byte m_AlertLevel;
    private byte m_AlertDescription;
    private boolean m_Alert;

    public TLSException(String string) {
        super(string);
        this.m_Alert = false;
        this.m_AlertLevel = 0;
        this.m_AlertDescription = 0;
    }

    public TLSException(String string, int n, int n2) {
        super(string);
        this.m_Alert = true;
        this.m_AlertLevel = (byte)(n & 0xFF);
        this.m_AlertDescription = (byte)(n2 & 0xFF);
    }

    public boolean Alert() {
        return this.m_Alert;
    }

    public byte getAlertLevel() {
        return this.m_AlertLevel;
    }

    public byte getAlertDescription() {
        return this.m_AlertDescription;
    }
}

