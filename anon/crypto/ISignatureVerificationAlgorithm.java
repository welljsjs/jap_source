/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface ISignatureVerificationAlgorithm {
    public boolean verify(byte[] var1, byte[] var2);

    public boolean verify(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6);

    public byte[] decodeForXMLSignature(byte[] var1);

    public String getXMLSignatureAlgorithmReference();

    public AlgorithmIdentifier getIdentifier();
}

