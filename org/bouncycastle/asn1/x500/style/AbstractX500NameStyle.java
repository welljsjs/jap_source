/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;

public abstract class AbstractX500NameStyle
implements X500NameStyle {
    public static Hashtable copyHashTable(Hashtable hashtable) {
        Hashtable hashtable2 = new Hashtable();
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
            Object k = enumeration.nextElement();
            hashtable2.put(k, hashtable.get(k));
        }
        return hashtable2;
    }

    private int calcHashCode(ASN1Encodable aSN1Encodable) {
        String string = IETFUtils.valueToString(aSN1Encodable);
        string = IETFUtils.canonicalize(string);
        return string.hashCode();
    }

    public int calculateHashCode(X500Name x500Name) {
        int n = 0;
        RDN[] arrrDN = x500Name.getRDNs();
        for (int i = 0; i != arrrDN.length; ++i) {
            if (arrrDN[i].isMultiValued()) {
                AttributeTypeAndValue[] arrattributeTypeAndValue = arrrDN[i].getTypesAndValues();
                for (int j = 0; j != arrattributeTypeAndValue.length; ++j) {
                    n ^= arrattributeTypeAndValue[j].getType().hashCode();
                    n ^= this.calcHashCode(arrattributeTypeAndValue[j].getValue());
                }
                continue;
            }
            n ^= arrrDN[i].getFirst().getType().hashCode();
            n ^= this.calcHashCode(arrrDN[i].getFirst().getValue());
        }
        return n;
    }

    public ASN1Encodable stringToValue(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        if (string.length() != 0 && string.charAt(0) == '#') {
            try {
                return IETFUtils.valueFromHexString(string, 1);
            }
            catch (IOException iOException) {
                throw new ASN1ParsingException("can't recode value for oid " + aSN1ObjectIdentifier.getId());
            }
        }
        if (string.length() != 0 && string.charAt(0) == '\\') {
            string = string.substring(1);
        }
        return this.encodeStringValue(aSN1ObjectIdentifier, string);
    }

    protected ASN1Encodable encodeStringValue(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        return new DERUTF8String(string);
    }

    public boolean areEqual(X500Name x500Name, X500Name x500Name2) {
        RDN[] arrrDN;
        RDN[] arrrDN2 = x500Name.getRDNs();
        if (arrrDN2.length != (arrrDN = x500Name2.getRDNs()).length) {
            return false;
        }
        boolean bl = false;
        if (arrrDN2[0].getFirst() != null && arrrDN[0].getFirst() != null) {
            bl = !arrrDN2[0].getFirst().getType().equals(arrrDN[0].getFirst().getType());
        }
        for (int i = 0; i != arrrDN2.length; ++i) {
            if (this.foundMatch(bl, arrrDN2[i], arrrDN)) continue;
            return false;
        }
        return true;
    }

    private boolean foundMatch(boolean bl, RDN rDN, RDN[] arrrDN) {
        if (bl) {
            for (int i = arrrDN.length - 1; i >= 0; --i) {
                if (arrrDN[i] == null || !this.rdnAreEqual(rDN, arrrDN[i])) continue;
                arrrDN[i] = null;
                return true;
            }
        } else {
            for (int i = 0; i != arrrDN.length; ++i) {
                if (arrrDN[i] == null || !this.rdnAreEqual(rDN, arrrDN[i])) continue;
                arrrDN[i] = null;
                return true;
            }
        }
        return false;
    }

    protected boolean rdnAreEqual(RDN rDN, RDN rDN2) {
        return IETFUtils.rDNAreEqual(rDN, rDN2);
    }

    public abstract /* synthetic */ String[] oidToAttrNames(ASN1ObjectIdentifier var1);

    public abstract /* synthetic */ String oidToDisplayName(ASN1ObjectIdentifier var1);

    public abstract /* synthetic */ String toString(X500Name var1);

    public abstract /* synthetic */ RDN[] fromString(String var1);

    public abstract /* synthetic */ ASN1ObjectIdentifier attrNameToOID(String var1);
}

