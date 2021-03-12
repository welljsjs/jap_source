/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.NotRecoverableException;
import anon.infoservice.MixCascade;

public class TrustException
extends NotRecoverableException {
    private static final long serialVersionUID = 1L;

    public TrustException(MixCascade mixCascade, String string) {
        super(mixCascade, string, -26);
    }

    public TrustException(MixCascade mixCascade, String string, int n) {
        super(mixCascade, string, n);
    }
}

