/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.JAPCertificate;
import anon.crypto.MyX509Extensions;
import anon.crypto.X509CertificateIssuer;
import anon.crypto.X509DistinguishedName;
import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class RevokedCertificate {
    public static final Class[] CRL_ENTRY_EXTENSIONS = new Class[]{class$anon$crypto$X509CertificateIssuer == null ? (class$anon$crypto$X509CertificateIssuer = RevokedCertificate.class$("anon.crypto.X509CertificateIssuer")) : class$anon$crypto$X509CertificateIssuer};
    private static BigInteger ZERO = BigInteger.valueOf(0L);
    private static BigInteger ONE = BigInteger.valueOf(1L);
    private BigInteger m_serial;
    private Date m_revocationDate;
    private MyX509Extensions m_extensions;
    static /* synthetic */ Class class$anon$crypto$X509CertificateIssuer;

    public RevokedCertificate(JAPCertificate jAPCertificate, Date date, MyX509Extensions myX509Extensions) {
        this.m_revocationDate = date;
        this.m_serial = RevokedCertificate.getUniqueSerial(jAPCertificate);
        this.m_extensions = myX509Extensions;
    }

    protected RevokedCertificate(TBSCertList.CRLEntry cRLEntry) {
        this.m_serial = cRLEntry.getUserCertificate().getPositiveValue();
        this.m_revocationDate = cRLEntry.getRevocationDate().getDate();
        if (cRLEntry.getExtensions() != null) {
            this.m_extensions = new MyX509Extensions(cRLEntry.getExtensions());
        }
    }

    protected static BigInteger getUniqueSerial(JAPCertificate jAPCertificate) {
        if (jAPCertificate.getSerialNumber().equals(ZERO) || jAPCertificate.getSerialNumber().equals(ONE)) {
            return RevokedCertificate.createPseudoSerial(jAPCertificate.toByteArray());
        }
        return jAPCertificate.getSerialNumber();
    }

    private static BigInteger createPseudoSerial(byte[] arrby) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        byte[] arrby2 = new byte[sHA1Digest.getDigestSize()];
        sHA1Digest.update(arrby, 0, arrby.length);
        sHA1Digest.doFinal(arrby2, 0);
        return new BigInteger(arrby2).abs();
    }

    protected ASN1Sequence toASN1Sequence() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.m_serial));
        aSN1EncodableVector.add(new Time(this.m_revocationDate));
        if (this.m_extensions != null) {
            aSN1EncodableVector.add(this.m_extensions.getExtensionsAsBCExtensions());
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public BigInteger getSerialNumber() {
        return this.m_serial;
    }

    public X509DistinguishedName getCertificateIssuer() {
        X509CertificateIssuer x509CertificateIssuer;
        if (this.m_extensions != null && (x509CertificateIssuer = (X509CertificateIssuer)this.m_extensions.getExtension(X509CertificateIssuer.IDENTIFIER)) != null) {
            return x509CertificateIssuer.getDistinguishedName();
        }
        return null;
    }

    public Date getRevocationDate() {
        return this.m_revocationDate;
    }

    public MyX509Extensions getExtensions() {
        return this.m_extensions;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

