/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.ReasonFlags;

public class X509IssuingDistributionPoint
extends AbstractX509Extension {
    public static final String IDENTIFIER = Extension.issuingDistributionPoint.getId();
    private IssuingDistributionPoint m_issuingDistributionPoint;

    public X509IssuingDistributionPoint(DistributionPointName distributionPointName, boolean bl, boolean bl2, ReasonFlags reasonFlags, boolean bl3, boolean bl4) {
        super(IDENTIFIER, false, X509IssuingDistributionPoint.createDEROctets(distributionPointName, !bl, !bl2, reasonFlags, !bl3, !bl4));
        this.m_issuingDistributionPoint = IssuingDistributionPoint.getInstance(X509IssuingDistributionPoint.createDERObject(distributionPointName, !bl, !bl2, reasonFlags, !bl3, !bl4));
    }

    public X509IssuingDistributionPoint(boolean bl) {
        this(null, false, false, null, bl, false);
    }

    public X509IssuingDistributionPoint(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        this.createValue();
    }

    private void createValue() {
        try {
            this.m_issuingDistributionPoint = IssuingDistributionPoint.getInstance((DERSequence)new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets())).readObject());
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not read issuing distribution point extension from byte array!");
        }
    }

    private static byte[] createDEROctets(DistributionPointName distributionPointName, boolean bl, boolean bl2, ReasonFlags reasonFlags, boolean bl3, boolean bl4) {
        try {
            return X509IssuingDistributionPoint.createDERObject(distributionPointName, bl, bl2, reasonFlags, bl3, bl4).getEncoded("DER");
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private static DERSequence createDERObject(DistributionPointName distributionPointName, boolean bl, boolean bl2, ReasonFlags reasonFlags, boolean bl3, boolean bl4) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (distributionPointName != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, distributionPointName));
        }
        if (bl) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, ASN1Boolean.TRUE));
        }
        if (bl2) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, ASN1Boolean.TRUE));
        }
        if (reasonFlags != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 3, reasonFlags));
        }
        if (bl3) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 4, ASN1Boolean.TRUE));
        }
        if (bl4) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 5, ASN1Boolean.TRUE));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public String getName() {
        return "IssuingDistributionPoint";
    }

    public Vector getValues() {
        return null;
    }

    public boolean isIndirectCRL() {
        return this.m_issuingDistributionPoint.isIndirectCRL();
    }

    public IssuingDistributionPoint getIssuingDistributionPoint() {
        return this.m_issuingDistributionPoint;
    }
}

