/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.client;

public class ClientForwardException
extends Exception {
    public static final int ERROR_CONNECTION_ERROR = 1;
    public static final int ERROR_PROTOCOL_ERROR = 2;
    public static final int ERROR_VERSION_ERROR = 3;
    public static final int ERROR_UNKNOWN_ERROR = 255;
    private int m_errorCode;

    public ClientForwardException(int n, String string) {
        super(string);
        this.m_errorCode = n;
    }

    public int getErrorCode() {
        return this.m_errorCode;
    }
}

