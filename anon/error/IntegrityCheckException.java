/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;

public class IntegrityCheckException
extends AnonServiceException {
    private static final long serialVersionUID = 1L;

    public IntegrityCheckException(AnonServerDescription anonServerDescription, int n) {
        super(anonServerDescription, "Integrity check failed for " + anonServerDescription + "! This is alsmost for sure an attack!", n);
    }
}

