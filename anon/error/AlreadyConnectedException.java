/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;

public class AlreadyConnectedException
extends AnonServiceException {
    private static final long serialVersionUID = 1L;

    public AlreadyConnectedException(AnonServerDescription anonServerDescription) {
        super(anonServerDescription, "Could not initialize with service " + anonServerDescription + ". Stop the running connection first", -4);
    }
}

