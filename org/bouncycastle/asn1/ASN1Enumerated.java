/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.StreamUtil;
import org.bouncycastle.util.Arrays;

public class ASN1Enumerated
extends ASN1Primitive {
    private final byte[] bytes;
    private static ASN1Enumerated[] cache = new ASN1Enumerated[12];

    public static ASN1Enumerated getInstance(Object object) {
        if (object == null || object instanceof ASN1Enumerated) {
            return (ASN1Enumerated)object;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1Enumerated)ASN1Primitive.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1Enumerated getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        ASN1Primitive aSN1Primitive = aSN1TaggedObject.getObject();
        if (bl || aSN1Primitive instanceof ASN1Enumerated) {
            return ASN1Enumerated.getInstance(aSN1Primitive);
        }
        return ASN1Enumerated.fromOctetString(((ASN1OctetString)aSN1Primitive).getOctets());
    }

    public ASN1Enumerated(int n) {
        this.bytes = BigInteger.valueOf(n).toByteArray();
    }

    public ASN1Enumerated(BigInteger bigInteger) {
        this.bytes = bigInteger.toByteArray();
    }

    public ASN1Enumerated(byte[] arrby) {
        this.bytes = arrby;
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    boolean isConstructed() {
        return false;
    }

    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.bytes.length) + this.bytes.length;
    }

    void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        aSN1OutputStream.writeEncoded(10, this.bytes);
    }

    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Enumerated)) {
            return false;
        }
        ASN1Enumerated aSN1Enumerated = (ASN1Enumerated)aSN1Primitive;
        return Arrays.areEqual(this.bytes, aSN1Enumerated.bytes);
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    static ASN1Enumerated fromOctetString(byte[] arrby) {
        if (arrby.length > 1) {
            return new ASN1Enumerated(Arrays.clone(arrby));
        }
        if (arrby.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        int n = arrby[0] & 0xFF;
        if (n >= cache.length) {
            return new ASN1Enumerated(Arrays.clone(arrby));
        }
        ASN1Enumerated aSN1Enumerated = cache[n];
        if (aSN1Enumerated == null) {
            aSN1Enumerated = ASN1Enumerated.cache[n] = new ASN1Enumerated(Arrays.clone(arrby));
        }
        return aSN1Enumerated;
    }
}

