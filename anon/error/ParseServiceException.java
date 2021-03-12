/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.NotRecoverableException;
import anon.infoservice.MixCascade;

public class ParseServiceException
extends NotRecoverableException {
    private static final long serialVersionUID = 1L;

    public ParseServiceException(MixCascade mixCascade, String string) {
        super(mixCascade, string, -27);
    }
}

