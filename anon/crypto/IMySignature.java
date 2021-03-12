/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureCreationAlgorithm;
import anon.crypto.ISignatureVerificationAlgorithm;
import java.security.InvalidKeyException;

public interface IMySignature
extends ISignatureVerificationAlgorithm,
ISignatureCreationAlgorithm {
    public void initVerify(IMyPublicKey var1) throws InvalidKeyException;

    public void initSign(IMyPrivateKey var1) throws InvalidKeyException;

    public boolean verify(byte[] var1, byte[] var2);

    public byte[] sign(byte[] var1);

    public byte[] encodeForXMLSignature(byte[] var1);

    public byte[] decodeForXMLSignature(byte[] var1);

    public String getXMLSignatureAlgorithmReference();
}

