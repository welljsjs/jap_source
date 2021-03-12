/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.IMyPublicKey;
import anon.crypto.MyX509Extensions;
import anon.crypto.X509DistinguishedName;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

final class CertificationRequestInfo
extends DERSequence {
    private IMyPublicKey m_publicKey;
    private X509DistinguishedName m_subject;
    private MyX509Extensions m_extensions;

    public CertificationRequestInfo(X509DistinguishedName x509DistinguishedName, IMyPublicKey iMyPublicKey, MyX509Extensions myX509Extensions) {
        super(CertificationRequestInfo.createRequestInfo(new ASN1Integer(0L), x509DistinguishedName.getX500Name(), iMyPublicKey.getAsSubjectPublicKeyInfo(), myX509Extensions));
        this.m_subject = x509DistinguishedName;
        try {
            this.m_publicKey = AsymmetricCryptoKeyPair.createPublicKey(iMyPublicKey.getAsSubjectPublicKeyInfo());
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not create public key: " + exception.getMessage());
        }
        this.m_extensions = myX509Extensions;
    }

    CertificationRequestInfo(ASN1Sequence aSN1Sequence) {
        super(CertificationRequestInfo.createRequestInfo(aSN1Sequence));
        try {
            this.m_publicKey = AsymmetricCryptoKeyPair.createPublicKey(SubjectPublicKeyInfo.getInstance(aSN1Sequence.getObjectAt(2)));
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not create public key: " + exception.getMessage());
        }
        this.m_subject = new X509DistinguishedName(X500Name.getInstance(this.getObjectAt(1)));
        ASN1Primitive aSN1Primitive = ((DERTaggedObject)this.getObjectAt(3)).getObject();
        this.m_extensions = aSN1Primitive instanceof DERSet ? new MyX509Extensions((DERSet)aSN1Primitive) : new MyX509Extensions(new DERSet());
    }

    public IMyPublicKey getPublicKey() {
        return this.m_publicKey;
    }

    public MyX509Extensions getExtensions() {
        return this.m_extensions;
    }

    public X509DistinguishedName getX509DistinguishedName() {
        return this.m_subject;
    }

    private static ASN1EncodableVector createRequestInfo(ASN1Integer aSN1Integer, X500Name x500Name, SubjectPublicKeyInfo subjectPublicKeyInfo, MyX509Extensions myX509Extensions) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(aSN1Integer);
        aSN1EncodableVector.add(x500Name);
        aSN1EncodableVector.add(subjectPublicKeyInfo);
        if (myX509Extensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, myX509Extensions.getExtensionsAsBCExtensions()));
        }
        return aSN1EncodableVector;
    }

    private static ASN1EncodableVector createRequestInfo(ASN1Sequence aSN1Sequence) {
        ASN1Integer aSN1Integer = (ASN1Integer)aSN1Sequence.getObjectAt(0);
        X500Name x500Name = X500Name.getInstance(aSN1Sequence.getObjectAt(1));
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(aSN1Sequence.getObjectAt(2));
        MyX509Extensions myX509Extensions = null;
        if (aSN1Sequence.size() > 3) {
            myX509Extensions = new MyX509Extensions(ASN1Set.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(3), false));
        }
        return CertificationRequestInfo.createRequestInfo(aSN1Integer, x500Name, subjectPublicKeyInfo, myX509Extensions);
    }
}

