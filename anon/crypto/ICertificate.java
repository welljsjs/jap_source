/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;

public interface ICertificate {
    public IMyPublicKey getPublicKey();

    public JAPCertificate getX509Certificate();

    public byte[] toByteArray();
}

