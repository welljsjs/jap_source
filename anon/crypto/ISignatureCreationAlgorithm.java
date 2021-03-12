/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface ISignatureCreationAlgorithm {
    public byte[] sign(byte[] var1);

    public byte[] encodeForXMLSignature(byte[] var1);

    public AlgorithmIdentifier getIdentifier();

    public String getXMLSignatureAlgorithmReference();
}

