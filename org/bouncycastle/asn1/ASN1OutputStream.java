/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DLOutputStream;

public class ASN1OutputStream {
    private OutputStream os;

    public ASN1OutputStream(OutputStream outputStream) {
        this.os = outputStream;
    }

    void writeLength(int n) throws IOException {
        if (n > 127) {
            int n2 = 1;
            int n3 = n;
            while ((n3 >>>= 8) != 0) {
                ++n2;
            }
            this.write((byte)(n2 | 0x80));
            for (int i = (n2 - 1) * 8; i >= 0; i -= 8) {
                this.write((byte)(n >> i));
            }
        } else {
            this.write((byte)n);
        }
    }

    void write(int n) throws IOException {
        this.os.write(n);
    }

    void write(byte[] arrby) throws IOException {
        this.os.write(arrby);
    }

    void write(byte[] arrby, int n, int n2) throws IOException {
        this.os.write(arrby, n, n2);
    }

    void writeEncoded(int n, byte[] arrby) throws IOException {
        this.write(n);
        this.writeLength(arrby.length);
        this.write(arrby);
    }

    void writeTag(int n, int n2) throws IOException {
        if (n2 < 31) {
            this.write(n | n2);
        } else {
            this.write(n | 0x1F);
            if (n2 < 128) {
                this.write(n2);
            } else {
                byte[] arrby = new byte[5];
                int n3 = arrby.length;
                arrby[--n3] = (byte)(n2 & 0x7F);
                do {
                    arrby[--n3] = (byte)((n2 >>= 7) & 0x7F | 0x80);
                } while (n2 > 127);
                this.write(arrby, n3, arrby.length - n3);
            }
        }
    }

    void writeEncoded(int n, int n2, byte[] arrby) throws IOException {
        this.writeTag(n, n2);
        this.writeLength(arrby.length);
        this.write(arrby);
    }

    protected void writeNull() throws IOException {
        this.os.write(5);
        this.os.write(0);
    }

    public void writeObject(ASN1Encodable aSN1Encodable) throws IOException {
        if (aSN1Encodable == null) {
            throw new IOException("null object detected");
        }
        aSN1Encodable.toASN1Primitive().encode(this);
    }

    void writeImplicitObject(ASN1Primitive aSN1Primitive) throws IOException {
        if (aSN1Primitive == null) {
            throw new IOException("null object detected");
        }
        aSN1Primitive.encode(new ImplicitOutputStream(this.os));
    }

    public void close() throws IOException {
        this.os.close();
    }

    public void flush() throws IOException {
        this.os.flush();
    }

    ASN1OutputStream getDERSubStream() {
        return new DEROutputStream(this.os);
    }

    ASN1OutputStream getDLSubStream() {
        return new DLOutputStream(this.os);
    }

    private class ImplicitOutputStream
    extends ASN1OutputStream {
        private boolean first = true;

        public ImplicitOutputStream(OutputStream outputStream) {
            super(outputStream);
        }

        public void write(int n) throws IOException {
            if (this.first) {
                this.first = false;
            } else {
                super.write(n);
            }
        }
    }
}

