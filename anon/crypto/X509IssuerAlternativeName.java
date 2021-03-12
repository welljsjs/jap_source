/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509AlternativeName;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extension;

public final class X509IssuerAlternativeName
extends AbstractX509AlternativeName {
    public static final String IDENTIFIER = Extension.issuerAlternativeName.getId();

    public X509IssuerAlternativeName(String string, Integer n) {
        super(IDENTIFIER, string, n);
    }

    public X509IssuerAlternativeName(boolean bl, String string, Integer n) {
        super(IDENTIFIER, bl, string, n);
    }

    public X509IssuerAlternativeName(Vector vector, Vector vector2) {
        super(IDENTIFIER, vector, vector2);
    }

    public X509IssuerAlternativeName(boolean bl, Vector vector, Vector vector2) {
        super(IDENTIFIER, bl, vector, vector2);
    }

    public X509IssuerAlternativeName(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public String getName() {
        return "IssuerAlternativeName";
    }
}

