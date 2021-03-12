/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1.x500;

import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500NameBuilder {
    private X500NameStyle template;
    private Vector rdns = new Vector();

    public X500NameBuilder() {
        this(BCStyle.INSTANCE);
    }

    public X500NameBuilder(X500NameStyle x500NameStyle) {
        this.template = x500NameStyle;
    }

    public X500NameBuilder addRDN(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        this.addRDN(aSN1ObjectIdentifier, this.template.stringToValue(aSN1ObjectIdentifier, string));
        return this;
    }

    public X500NameBuilder addRDN(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.rdns.addElement(new RDN(aSN1ObjectIdentifier, aSN1Encodable));
        return this;
    }

    public X500NameBuilder addRDN(AttributeTypeAndValue attributeTypeAndValue) {
        this.rdns.addElement(new RDN(attributeTypeAndValue));
        return this;
    }

    public X500NameBuilder addMultiValuedRDN(ASN1ObjectIdentifier[] arraSN1ObjectIdentifier, String[] arrstring) {
        ASN1Encodable[] arraSN1Encodable = new ASN1Encodable[arrstring.length];
        for (int i = 0; i != arraSN1Encodable.length; ++i) {
            arraSN1Encodable[i] = this.template.stringToValue(arraSN1ObjectIdentifier[i], arrstring[i]);
        }
        return this.addMultiValuedRDN(arraSN1ObjectIdentifier, arraSN1Encodable);
    }

    public X500NameBuilder addMultiValuedRDN(ASN1ObjectIdentifier[] arraSN1ObjectIdentifier, ASN1Encodable[] arraSN1Encodable) {
        AttributeTypeAndValue[] arrattributeTypeAndValue = new AttributeTypeAndValue[arraSN1ObjectIdentifier.length];
        for (int i = 0; i != arraSN1ObjectIdentifier.length; ++i) {
            arrattributeTypeAndValue[i] = new AttributeTypeAndValue(arraSN1ObjectIdentifier[i], arraSN1Encodable[i]);
        }
        return this.addMultiValuedRDN(arrattributeTypeAndValue);
    }

    public X500NameBuilder addMultiValuedRDN(AttributeTypeAndValue[] arrattributeTypeAndValue) {
        this.rdns.addElement(new RDN(arrattributeTypeAndValue));
        return this;
    }

    public X500Name build() {
        RDN[] arrrDN = new RDN[this.rdns.size()];
        for (int i = 0; i != arrrDN.length; ++i) {
            arrrDN[i] = (RDN)this.rdns.elementAt(i);
        }
        return new X500Name(this.template, arrrDN);
    }
}

