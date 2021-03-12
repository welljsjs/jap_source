/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.MultiCertPath;

public interface IVerifyable {
    public boolean isVerified();

    public boolean isValid();

    public MultiCertPath getCertPath();
}

