/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import anon.crypto.X509DistinguishedName;
import anon.util.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class X509CertificateIssuer
extends AbstractX509Extension {
    public static final String IDENTIFIER = Extension.certificateIssuer.getId();
    private X509DistinguishedName m_issuer;

    public X509CertificateIssuer(X509DistinguishedName x509DistinguishedName) {
        super(IDENTIFIER, true, X509CertificateIssuer.createDEROctets(x509DistinguishedName));
        this.m_issuer = x509DistinguishedName;
    }

    public X509CertificateIssuer(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        this.createValue();
    }

    private static byte[] createDEROctets(X509DistinguishedName x509DistinguishedName) {
        try {
            return new GeneralNames(new GeneralName(x509DistinguishedName.getX500Name())).getEncoded("DER");
        }
        catch (IOException iOException) {
            return null;
        }
    }

    public String getName() {
        return "CertificateIssuer";
    }

    private void createValue() {
        try {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets()));
            ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
            aSN1InputStream.close();
            GeneralName generalName = GeneralNames.getInstance(aSN1Sequence).getNames()[0];
            if (generalName.getTagNo() != 4) {
                throw new Exception();
            }
            aSN1Sequence = (DERSequence)generalName.getName();
            this.m_issuer = new X509DistinguishedName(X500Name.getInstance(aSN1Sequence));
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not read certificate issuer extension from byte array!");
        }
    }

    public Vector getValues() {
        return Util.toVector(this.m_issuer.toString());
    }

    public boolean equalsIssuer(Object object) {
        return this.m_issuer.equals(object);
    }

    public X509DistinguishedName getDistinguishedName() {
        return this.m_issuer;
    }
}

