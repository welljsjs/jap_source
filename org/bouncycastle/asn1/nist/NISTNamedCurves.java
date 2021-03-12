/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1.nist;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.util.Strings;

public class NISTNamedCurves {
    static final Hashtable objIds = new Hashtable();
    static final Hashtable names = new Hashtable();

    static void defineCurveAlias(String string, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        objIds.put(string.toUpperCase(), aSN1ObjectIdentifier);
        names.put(aSN1ObjectIdentifier, string);
    }

    public static X9ECParameters getByName(String string) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = NISTNamedCurves.getOID(string);
        return aSN1ObjectIdentifier == null ? null : NISTNamedCurves.getByOID(aSN1ObjectIdentifier);
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return SECNamedCurves.getByOID(aSN1ObjectIdentifier);
    }

    public static ASN1ObjectIdentifier getOID(String string) {
        return (ASN1ObjectIdentifier)objIds.get(Strings.toUpperCase(string));
    }

    public static String getName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (String)names.get(aSN1ObjectIdentifier);
    }

    public static Enumeration getNames() {
        return names.elements();
    }

    static {
        NISTNamedCurves.defineCurveAlias("B-163", SECObjectIdentifiers.sect163r2);
        NISTNamedCurves.defineCurveAlias("B-233", SECObjectIdentifiers.sect233r1);
        NISTNamedCurves.defineCurveAlias("B-283", SECObjectIdentifiers.sect283r1);
        NISTNamedCurves.defineCurveAlias("B-409", SECObjectIdentifiers.sect409r1);
        NISTNamedCurves.defineCurveAlias("B-571", SECObjectIdentifiers.sect571r1);
        NISTNamedCurves.defineCurveAlias("K-163", SECObjectIdentifiers.sect163k1);
        NISTNamedCurves.defineCurveAlias("K-233", SECObjectIdentifiers.sect233k1);
        NISTNamedCurves.defineCurveAlias("K-283", SECObjectIdentifiers.sect283k1);
        NISTNamedCurves.defineCurveAlias("K-409", SECObjectIdentifiers.sect409k1);
        NISTNamedCurves.defineCurveAlias("K-571", SECObjectIdentifiers.sect571k1);
        NISTNamedCurves.defineCurveAlias("P-192", SECObjectIdentifiers.secp192r1);
        NISTNamedCurves.defineCurveAlias("P-224", SECObjectIdentifiers.secp224r1);
        NISTNamedCurves.defineCurveAlias("P-256", SECObjectIdentifiers.secp256r1);
        NISTNamedCurves.defineCurveAlias("P-384", SECObjectIdentifiers.secp384r1);
        NISTNamedCurves.defineCurveAlias("P-521", SECObjectIdentifiers.secp521r1);
    }
}

