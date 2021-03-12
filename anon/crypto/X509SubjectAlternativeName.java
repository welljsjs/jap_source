/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509AlternativeName;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extension;

public final class X509SubjectAlternativeName
extends AbstractX509AlternativeName {
    public static final String IDENTIFIER = Extension.subjectAlternativeName.getId();

    public X509SubjectAlternativeName(String string, Integer n) {
        super(IDENTIFIER, string, n);
    }

    public X509SubjectAlternativeName(boolean bl, String string, Integer n) {
        super(IDENTIFIER, bl, string, n);
    }

    public X509SubjectAlternativeName(Vector vector, Vector vector2) {
        super(IDENTIFIER, vector, vector2);
    }

    public X509SubjectAlternativeName(boolean bl, Vector vector, Vector vector2) {
        super(IDENTIFIER, bl, vector, vector2);
    }

    public X509SubjectAlternativeName(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public String getName() {
        return "SubjectAlternativeName";
    }
}

