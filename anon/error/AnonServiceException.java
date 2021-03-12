/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.AnonServerDescription;

public class AnonServiceException
extends Exception {
    private static final long serialVersionUID = 1L;
    private AnonServerDescription m_service;
    private int m_iErrorCode;

    public AnonServiceException(AnonServerDescription anonServerDescription, String string, int n) {
        super(string);
        this.m_service = anonServerDescription;
        this.m_iErrorCode = n;
    }

    public AnonServiceException(AnonServerDescription anonServerDescription, String string) {
        this(anonServerDescription, string, -1);
    }

    public final int getErrorCode() {
        return this.m_iErrorCode;
    }

    public final AnonServerDescription getService() {
        return this.m_service;
    }
}

