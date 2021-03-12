/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;

public class DERApplicationSpecific
extends ASN1ApplicationSpecific {
    DERApplicationSpecific(boolean bl, int n, byte[] arrby) {
        super(bl, n, arrby);
    }

    public DERApplicationSpecific(int n, byte[] arrby) {
        this(false, n, arrby);
    }

    public DERApplicationSpecific(int n, ASN1Encodable aSN1Encodable) throws IOException {
        this(true, n, aSN1Encodable);
    }

    public DERApplicationSpecific(boolean bl, int n, ASN1Encodable aSN1Encodable) throws IOException {
        super(bl || aSN1Encodable.toASN1Primitive().isConstructed(), n, DERApplicationSpecific.getEncoding(bl, aSN1Encodable));
    }

    private static byte[] getEncoding(boolean bl, ASN1Encodable aSN1Encodable) throws IOException {
        byte[] arrby = aSN1Encodable.toASN1Primitive().getEncoded("DER");
        if (bl) {
            return arrby;
        }
        int n = ASN1ApplicationSpecific.getLengthOfHeader(arrby);
        byte[] arrby2 = new byte[arrby.length - n];
        System.arraycopy(arrby, n, arrby2, 0, arrby2.length);
        return arrby2;
    }

    public DERApplicationSpecific(int n, ASN1EncodableVector aSN1EncodableVector) {
        super(true, n, DERApplicationSpecific.getEncodedVector(aSN1EncodableVector));
    }

    private static byte[] getEncodedVector(ASN1EncodableVector aSN1EncodableVector) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != aSN1EncodableVector.size(); ++i) {
            try {
                byteArrayOutputStream.write(((ASN1Object)aSN1EncodableVector.get(i)).getEncoded("DER"));
                continue;
            }
            catch (IOException iOException) {
                throw new ASN1ParsingException("malformed object: " + iOException, iOException);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        int n = 64;
        if (this.isConstructed) {
            n |= 0x20;
        }
        aSN1OutputStream.writeEncoded(n, this.tag, this.octets);
    }
}

