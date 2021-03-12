/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;

public class ServiceInterruptedException
extends AnonServiceException {
    private static final long serialVersionUID = 1L;

    public ServiceInterruptedException(AnonServerDescription anonServerDescription, String string) {
        super(anonServerDescription, string, -24);
    }

    public ServiceInterruptedException(AnonServerDescription anonServerDescription) {
        this(anonServerDescription, "We were interrupted while connecting to service " + anonServerDescription + ".");
    }
}

