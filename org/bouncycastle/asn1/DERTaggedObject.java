/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.StreamUtil;

public class DERTaggedObject
extends ASN1TaggedObject {
    private static final byte[] ZERO_BYTES = new byte[0];

    public DERTaggedObject(boolean bl, int n, ASN1Encodable aSN1Encodable) {
        super(bl, n, aSN1Encodable);
    }

    public DERTaggedObject(int n, ASN1Encodable aSN1Encodable) {
        super(true, n, aSN1Encodable);
    }

    boolean isConstructed() {
        if (!this.empty) {
            if (this.explicit) {
                return true;
            }
            ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDERObject();
            return aSN1Primitive.isConstructed();
        }
        return true;
    }

    int encodedLength() throws IOException {
        if (!this.empty) {
            ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDERObject();
            int n = aSN1Primitive.encodedLength();
            if (this.explicit) {
                return StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(n) + n;
            }
            return StreamUtil.calculateTagLength(this.tagNo) + --n;
        }
        return StreamUtil.calculateTagLength(this.tagNo) + 1;
    }

    void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        if (!this.empty) {
            ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDERObject();
            if (this.explicit) {
                aSN1OutputStream.writeTag(160, this.tagNo);
                aSN1OutputStream.writeLength(aSN1Primitive.encodedLength());
                aSN1OutputStream.writeObject(aSN1Primitive);
            } else {
                int n = aSN1Primitive.isConstructed() ? 160 : 128;
                aSN1OutputStream.writeTag(n, this.tagNo);
                aSN1OutputStream.writeImplicitObject(aSN1Primitive);
            }
        } else {
            aSN1OutputStream.writeEncoded(160, this.tagNo, ZERO_BYTES);
        }
    }
}

