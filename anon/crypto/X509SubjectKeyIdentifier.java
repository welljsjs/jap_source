/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509KeyIdentifier;
import anon.crypto.ByteSignature;
import anon.crypto.IMyPublicKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.digests.SHA1Digest;

public final class X509SubjectKeyIdentifier
extends AbstractX509KeyIdentifier {
    public static final String IDENTIFIER = Extension.subjectKeyIdentifier.getId();

    public X509SubjectKeyIdentifier(IMyPublicKey iMyPublicKey) {
        super(IDENTIFIER, X509SubjectKeyIdentifier.createDEROctets(iMyPublicKey));
        this.createValue();
    }

    public X509SubjectKeyIdentifier(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        this.createValue();
    }

    public String getName() {
        return "SubjectKeyIdentifier";
    }

    private static byte[] createDEROctets(IMyPublicKey iMyPublicKey) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
            SubjectPublicKeyInfo subjectPublicKeyInfo = iMyPublicKey.getAsSubjectPublicKeyInfo();
            byte[] arrby = X509SubjectKeyIdentifier.getDigest(subjectPublicKeyInfo);
            SubjectKeyIdentifier subjectKeyIdentifier = new SubjectKeyIdentifier(arrby);
            dEROutputStream.writeObject(subjectKeyIdentifier.toASN1Primitive());
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not write DER object to bytes!");
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void createValue() {
        byte[] arrby;
        try {
            arrby = ((DEROctetString)new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets())).readObject()).getOctets();
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not read subject key identifier from byte array!");
        }
        this.m_value = ByteSignature.toHexString(arrby);
    }

    public static byte[] getDigest(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        byte[] arrby = new byte[sHA1Digest.getDigestSize()];
        byte[] arrby2 = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        sHA1Digest.update(arrby2, 0, arrby2.length);
        sHA1Digest.doFinal(arrby, 0);
        return arrby;
    }
}

