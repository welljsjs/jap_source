/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class X9ECPoint
extends ASN1Object {
    private ASN1OctetString encoding;
    private ECCurve c;
    private ECPoint p;

    public X9ECPoint(ECPoint eCPoint) {
        this(eCPoint, false);
    }

    public X9ECPoint(ECPoint eCPoint, boolean bl) {
        this.p = eCPoint.normalize();
        this.encoding = new DEROctetString(eCPoint.getEncoded(bl));
    }

    public X9ECPoint(ECCurve eCCurve, byte[] arrby) {
        this.c = eCCurve;
        this.encoding = new DEROctetString(Arrays.clone(arrby));
    }

    public X9ECPoint(ECCurve eCCurve, ASN1OctetString aSN1OctetString) {
        this(eCCurve, aSN1OctetString.getOctets());
    }

    public byte[] getPointEncoding() {
        return Arrays.clone(this.encoding.getOctets());
    }

    public ECPoint getPoint() {
        if (this.p == null) {
            this.p = this.c.decodePoint(this.encoding.getOctets()).normalize();
        }
        return this.p;
    }

    public boolean isPointCompressed() {
        byte[] arrby = this.encoding.getOctets();
        return arrby != null && arrby.length > 0 && (arrby[0] == 2 || arrby[0] == 3);
    }

    public ASN1Primitive toASN1Primitive() {
        return this.encoding;
    }
}

