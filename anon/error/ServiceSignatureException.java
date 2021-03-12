/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.NotRecoverableException;
import anon.infoservice.MixCascade;

public class ServiceSignatureException
extends NotRecoverableException {
    private static final long serialVersionUID = 1L;

    public ServiceSignatureException(MixCascade mixCascade, String string, int n) {
        super(mixCascade, string, n == 0 ? -22 : -23, n);
    }

    public ServiceSignatureException(MixCascade mixCascade, String string) {
        this(mixCascade, string, -1);
    }
}

