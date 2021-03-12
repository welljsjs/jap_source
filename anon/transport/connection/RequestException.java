/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connection;

import anon.transport.connection.ConnectionException;

public class RequestException
extends ConnectionException {
    public static final int Reason_UNKNOWN = 1;
    public static final int Reason_SERVER_BUSY = 2;
    public static final int Reason_MISSING_CREDENTIALS = 3;
    public static final int Reason_OTHER = 4;
    private static final long serialVersionUID = 1L;
    private int m_reason;

    public RequestException(Throwable throwable, int n) {
        super(throwable);
        this.m_reason = n;
    }

    public RequestException(Throwable throwable) {
        super(throwable);
        this.m_reason = 1;
    }

    public RequestException(String string, int n) {
        super(string);
        this.m_reason = n;
    }

    public RequestException(String string) {
        super(string);
        this.m_reason = 1;
    }

    public int getReason() {
        return this.m_reason;
    }
}

