/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.LimitedInputStream;

class StreamUtil {
    StreamUtil() {
    }

    static int findLimit(InputStream inputStream) {
        if (inputStream instanceof LimitedInputStream) {
            return ((LimitedInputStream)inputStream).getRemaining();
        }
        if (inputStream instanceof ASN1InputStream) {
            return ((ASN1InputStream)inputStream).getLimit();
        }
        if (inputStream instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream)inputStream).available();
        }
        return Integer.MAX_VALUE;
    }

    static int calculateBodyLength(int n) {
        int n2 = 1;
        if (n > 127) {
            int n3 = 1;
            int n4 = n;
            while ((n4 >>>= 8) != 0) {
                ++n3;
            }
            for (int i = (n3 - 1) * 8; i >= 0; i -= 8) {
                ++n2;
            }
        }
        return n2;
    }

    static int calculateTagLength(int n) throws IOException {
        int n2 = 1;
        if (n >= 31) {
            if (n < 128) {
                ++n2;
            } else {
                byte[] arrby = new byte[5];
                int n3 = arrby.length;
                arrby[--n3] = (byte)(n & 0x7F);
                do {
                    arrby[--n3] = (byte)((n >>= 7) & 0x7F | 0x80);
                } while (n > 127);
                n2 += arrby.length - n3;
            }
        }
        return n2;
    }
}

