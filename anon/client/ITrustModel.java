/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.error.ServiceSignatureException;
import anon.error.TrustException;
import anon.infoservice.MixCascade;

public interface ITrustModel {
    public boolean isTrusted(MixCascade var1);

    public void checkTrust(MixCascade var1, boolean var2) throws TrustException, ServiceSignatureException;

    public void checkTrust(MixCascade var1) throws TrustException, ServiceSignatureException;
}

