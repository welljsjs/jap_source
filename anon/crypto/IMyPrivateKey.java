/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureCreationAlgorithm;
import anon.util.IXMLEncodable;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

public interface IMyPrivateKey
extends PrivateKey,
IXMLEncodable {
    public IMyPublicKey createPublicKey();

    public ISignatureCreationAlgorithm getSignatureAlgorithm();

    public PrivateKeyInfo getAsPrivateKeyInfo();
}

