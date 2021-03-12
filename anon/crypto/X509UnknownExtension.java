/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import anon.util.Util;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Sequence;

public final class X509UnknownExtension
extends AbstractX509Extension {
    public static final String IDENTIFIER = null;

    X509UnknownExtension(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public String getName() {
        return "UnknownExtension";
    }

    public Vector getValues() {
        return Util.toVector(this.getIdentifier());
    }
}

