/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.AnonServiceException;
import anon.infoservice.MixCascade;

public class ConnectionEstablishmentTimeoutException
extends AnonServiceException {
    private static final long serialVersionUID = 1L;

    public ConnectionEstablishmentTimeoutException(MixCascade mixCascade) {
        super(mixCascade, "Connection establishement timed out.", -6);
    }
}

