/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.ISignatureVerificationAlgorithm;
import anon.util.IXMLEncodable;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public interface IMyPublicKey
extends PublicKey,
IXMLEncodable {
    public ISignatureVerificationAlgorithm getSignatureAlgorithm();

    public SubjectPublicKeyInfo getAsSubjectPublicKeyInfo();

    public int getKeyLength();

    public boolean equals(Object var1);

    public int hashCode();
}

