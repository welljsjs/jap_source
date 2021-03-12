/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;

public class InvalidServiceException
extends AnonServiceException {
    public InvalidServiceException(AnonServerDescription anonServerDescription) {
        super(anonServerDescription, "Invalid service class: " + (anonServerDescription == null ? null : anonServerDescription.getClass()), -5);
    }
}

