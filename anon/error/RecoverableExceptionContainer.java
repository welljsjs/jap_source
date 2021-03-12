/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.AnonServiceException;

public class RecoverableExceptionContainer
extends AnonServiceException {
    private static final long serialVersionUID = 1L;
    private AnonServiceException m_source;

    public RecoverableExceptionContainer(AnonServiceException anonServiceException) {
        super(anonServiceException.getService(), anonServiceException.getMessage(), anonServiceException.getErrorCode());
        this.m_source = anonServiceException;
    }

    public AnonServiceException getSource() {
        return this.m_source;
    }
}

