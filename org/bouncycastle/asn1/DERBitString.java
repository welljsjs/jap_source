/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.StreamUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public class DERBitString
extends ASN1Primitive
implements ASN1String {
    private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    protected byte[] data;
    protected int padBits;

    protected static int getPadBits(int n) {
        int n2;
        int n3 = 0;
        for (n2 = 3; n2 >= 0; --n2) {
            if (n2 != 0) {
                if (n >> n2 * 8 == 0) continue;
                n3 = n >> n2 * 8 & 0xFF;
                break;
            }
            if (n == 0) continue;
            n3 = n & 0xFF;
            break;
        }
        if (n3 == 0) {
            return 7;
        }
        n2 = 1;
        while (((n3 <<= 1) & 0xFF) != 0) {
            ++n2;
        }
        return 8 - n2;
    }

    protected static byte[] getBytes(int n) {
        int n2 = 4;
        for (int i = 3; i >= 1 && (n & 255 << i * 8) == 0; --i) {
            --n2;
        }
        byte[] arrby = new byte[n2];
        for (int i = 0; i < n2; ++i) {
            arrby[i] = (byte)(n >> i * 8 & 0xFF);
        }
        return arrby;
    }

    public static DERBitString getInstance(Object object) {
        if (object == null || object instanceof DERBitString) {
            return (DERBitString)object;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static DERBitString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        ASN1Primitive aSN1Primitive = aSN1TaggedObject.getObject();
        if (bl || aSN1Primitive instanceof DERBitString) {
            return DERBitString.getInstance(aSN1Primitive);
        }
        return DERBitString.fromOctetString(((ASN1OctetString)aSN1Primitive).getOctets());
    }

    protected DERBitString(byte by, int n) {
        this.data = new byte[1];
        this.data[0] = by;
        this.padBits = n;
    }

    public DERBitString(byte[] arrby, int n) {
        this.data = arrby;
        this.padBits = n;
    }

    public DERBitString(byte[] arrby) {
        this(arrby, 0);
    }

    public DERBitString(int n) {
        this.data = DERBitString.getBytes(n);
        this.padBits = DERBitString.getPadBits(n);
    }

    public DERBitString(ASN1Encodable aSN1Encodable) throws IOException {
        this.data = aSN1Encodable.toASN1Primitive().getEncoded("DER");
        this.padBits = 0;
    }

    public byte[] getBytes() {
        return this.data;
    }

    public int getPadBits() {
        return this.padBits;
    }

    public int intValue() {
        int n = 0;
        for (int i = 0; i != this.data.length && i != 4; ++i) {
            n |= (this.data[i] & 0xFF) << 8 * i;
        }
        return n;
    }

    boolean isConstructed() {
        return false;
    }

    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.data.length + 1) + this.data.length + 1;
    }

    void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        byte[] arrby = new byte[this.getBytes().length + 1];
        arrby[0] = (byte)this.getPadBits();
        System.arraycopy(this.getBytes(), 0, arrby, 1, arrby.length - 1);
        aSN1OutputStream.writeEncoded(3, arrby);
    }

    public int hashCode() {
        return this.padBits ^ Arrays.hashCode(this.data);
    }

    protected boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof DERBitString)) {
            return false;
        }
        DERBitString dERBitString = (DERBitString)aSN1Primitive;
        return this.padBits == dERBitString.padBits && Arrays.areEqual(this.data, dERBitString.data);
    }

    public String getString() {
        StringBuffer stringBuffer = new StringBuffer("#");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
        try {
            aSN1OutputStream.writeObject(this);
        }
        catch (IOException iOException) {
            throw new ASN1ParsingException("Internal error encoding BitString: " + iOException.getMessage(), iOException);
        }
        byte[] arrby = byteArrayOutputStream.toByteArray();
        for (int i = 0; i != arrby.length; ++i) {
            stringBuffer.append(table[arrby[i] >>> 4 & 0xF]);
            stringBuffer.append(table[arrby[i] & 0xF]);
        }
        return stringBuffer.toString();
    }

    public String toString() {
        return this.getString();
    }

    static DERBitString fromOctetString(byte[] arrby) {
        if (arrby.length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        byte by = arrby[0];
        byte[] arrby2 = new byte[arrby.length - 1];
        if (arrby2.length != 0) {
            System.arraycopy(arrby, 1, arrby2, 0, arrby.length - 1);
        }
        return new DERBitString(arrby2, (int)by);
    }

    static DERBitString fromInputStream(int n, InputStream inputStream) throws IOException {
        if (n < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int n2 = inputStream.read();
        byte[] arrby = new byte[n - 1];
        if (arrby.length != 0 && Streams.readFully(inputStream, arrby) != arrby.length) {
            throw new EOFException("EOF encountered in middle of BIT STRING");
        }
        return new DERBitString(arrby, n2);
    }
}

