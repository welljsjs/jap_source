/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import java.security.SignatureException;

public class ExpiredSignatureException
extends SignatureException {
    private static final long serialVersionUID = 1L;

    public ExpiredSignatureException(String string) {
        super(string);
    }
}

