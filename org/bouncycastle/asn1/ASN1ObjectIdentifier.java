/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.OIDTokenizer;
import org.bouncycastle.asn1.StreamUtil;
import org.bouncycastle.util.Arrays;

public class ASN1ObjectIdentifier
extends ASN1Primitive {
    private String identifier;
    private byte[] body;
    private static final long LONG_LIMIT = 0xFFFFFFFFFFFF80L;
    private static final Map pool = new HashMap();

    public static ASN1ObjectIdentifier getInstance(Object object) {
        if (object == null || object instanceof ASN1ObjectIdentifier) {
            return (ASN1ObjectIdentifier)object;
        }
        if (object instanceof ASN1Encodable && ((ASN1Encodable)object).toASN1Primitive() instanceof ASN1ObjectIdentifier) {
            return (ASN1ObjectIdentifier)((ASN1Encodable)object).toASN1Primitive();
        }
        if (object instanceof byte[]) {
            byte[] arrby = (byte[])object;
            try {
                return (ASN1ObjectIdentifier)ASN1Primitive.fromByteArray(arrby);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        ASN1Primitive aSN1Primitive = aSN1TaggedObject.getObject();
        if (bl || aSN1Primitive instanceof ASN1ObjectIdentifier) {
            return ASN1ObjectIdentifier.getInstance(aSN1Primitive);
        }
        return ASN1ObjectIdentifier.fromOctetString(ASN1OctetString.getInstance(aSN1TaggedObject.getObject()).getOctets());
    }

    ASN1ObjectIdentifier(byte[] arrby) {
        StringBuffer stringBuffer = new StringBuffer();
        long l = 0L;
        BigInteger bigInteger = null;
        boolean bl = true;
        for (int i = 0; i != arrby.length; ++i) {
            int n = arrby[i] & 0xFF;
            if (l <= 0xFFFFFFFFFFFF80L) {
                l += (long)(n & 0x7F);
                if ((n & 0x80) == 0) {
                    if (bl) {
                        if (l < 40L) {
                            stringBuffer.append('0');
                        } else if (l < 80L) {
                            stringBuffer.append('1');
                            l -= 40L;
                        } else {
                            stringBuffer.append('2');
                            l -= 80L;
                        }
                        bl = false;
                    }
                    stringBuffer.append('.');
                    stringBuffer.append(l);
                    l = 0L;
                    continue;
                }
                l <<= 7;
                continue;
            }
            if (bigInteger == null) {
                bigInteger = BigInteger.valueOf(l);
            }
            bigInteger = bigInteger.or(BigInteger.valueOf(n & 0x7F));
            if ((n & 0x80) == 0) {
                if (bl) {
                    stringBuffer.append('2');
                    bigInteger = bigInteger.subtract(BigInteger.valueOf(80L));
                    bl = false;
                }
                stringBuffer.append('.');
                stringBuffer.append(bigInteger);
                bigInteger = null;
                l = 0L;
                continue;
            }
            bigInteger = bigInteger.shiftLeft(7);
        }
        this.identifier = stringBuffer.toString();
        this.body = Arrays.clone(arrby);
    }

    public ASN1ObjectIdentifier(String string) {
        if (string == null) {
            throw new IllegalArgumentException("'identifier' cannot be null");
        }
        if (!ASN1ObjectIdentifier.isValidIdentifier(string)) {
            throw new IllegalArgumentException("string " + string + " not an OID");
        }
        this.identifier = string;
    }

    ASN1ObjectIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        if (!ASN1ObjectIdentifier.isValidBranchID(string, 0)) {
            throw new IllegalArgumentException("string " + string + " not a valid OID branch");
        }
        this.identifier = aSN1ObjectIdentifier.getId() + "." + string;
    }

    public String getId() {
        return this.identifier;
    }

    public ASN1ObjectIdentifier branch(String string) {
        return new ASN1ObjectIdentifier(this, string);
    }

    public boolean on(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = this.getId();
        String string2 = aSN1ObjectIdentifier.getId();
        return string.length() > string2.length() && string.charAt(string2.length()) == '.' && string.startsWith(string2);
    }

    private void writeField(ByteArrayOutputStream byteArrayOutputStream, long l) {
        byte[] arrby = new byte[9];
        int n = 8;
        arrby[n] = (byte)((int)l & 0x7F);
        while (l >= 128L) {
            arrby[--n] = (byte)((int)(l >>= 7) & 0x7F | 0x80);
        }
        byteArrayOutputStream.write(arrby, n, 9 - n);
    }

    private void writeField(ByteArrayOutputStream byteArrayOutputStream, BigInteger bigInteger) {
        int n = (bigInteger.bitLength() + 6) / 7;
        if (n == 0) {
            byteArrayOutputStream.write(0);
        } else {
            BigInteger bigInteger2 = bigInteger;
            byte[] arrby = new byte[n];
            for (int i = n - 1; i >= 0; --i) {
                arrby[i] = (byte)(bigInteger2.intValue() & 0x7F | 0x80);
                bigInteger2 = bigInteger2.shiftRight(7);
            }
            int n2 = n - 1;
            arrby[n2] = (byte)(arrby[n2] & 0x7F);
            byteArrayOutputStream.write(arrby, 0, arrby.length);
        }
    }

    private void doOutput(ByteArrayOutputStream byteArrayOutputStream) {
        OIDTokenizer oIDTokenizer = new OIDTokenizer(this.identifier);
        int n = Integer.parseInt(oIDTokenizer.nextToken()) * 40;
        String string = oIDTokenizer.nextToken();
        if (string.length() <= 18) {
            this.writeField(byteArrayOutputStream, (long)n + Long.parseLong(string));
        } else {
            this.writeField(byteArrayOutputStream, new BigInteger(string).add(BigInteger.valueOf(n)));
        }
        while (oIDTokenizer.hasMoreTokens()) {
            String string2 = oIDTokenizer.nextToken();
            if (string2.length() <= 18) {
                this.writeField(byteArrayOutputStream, Long.parseLong(string2));
                continue;
            }
            this.writeField(byteArrayOutputStream, new BigInteger(string2));
        }
    }

    private synchronized byte[] getBody() {
        if (this.body == null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            this.doOutput(byteArrayOutputStream);
            this.body = byteArrayOutputStream.toByteArray();
        }
        return this.body;
    }

    boolean isConstructed() {
        return false;
    }

    int encodedLength() throws IOException {
        int n = this.getBody().length;
        return 1 + StreamUtil.calculateBodyLength(n) + n;
    }

    void encode(ASN1OutputStream aSN1OutputStream) throws IOException {
        byte[] arrby = this.getBody();
        aSN1OutputStream.write(6);
        aSN1OutputStream.writeLength(arrby.length);
        aSN1OutputStream.write(arrby);
    }

    public int hashCode() {
        return this.identifier.hashCode();
    }

    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive == this) {
            return true;
        }
        if (!(aSN1Primitive instanceof ASN1ObjectIdentifier)) {
            return false;
        }
        return this.identifier.equals(((ASN1ObjectIdentifier)aSN1Primitive).identifier);
    }

    public String toString() {
        return this.getId();
    }

    private static boolean isValidBranchID(String string, int n) {
        boolean bl = false;
        int n2 = string.length();
        while (--n2 >= n) {
            char c = string.charAt(n2);
            if ('0' <= c && c <= '9') {
                bl = true;
                continue;
            }
            if (c == '.') {
                if (!bl) {
                    return false;
                }
                bl = false;
                continue;
            }
            return false;
        }
        return bl;
    }

    private static boolean isValidIdentifier(String string) {
        if (string.length() < 3 || string.charAt(1) != '.') {
            return false;
        }
        char c = string.charAt(0);
        if (c < '0' || c > '2') {
            return false;
        }
        return ASN1ObjectIdentifier.isValidBranchID(string, 2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ASN1ObjectIdentifier intern() {
        Map map = pool;
        synchronized (map) {
            OidHandle oidHandle = new OidHandle(this.getBody());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)pool.get(oidHandle);
            if (aSN1ObjectIdentifier != null) {
                return aSN1ObjectIdentifier;
            }
            pool.put(oidHandle, this);
            return this;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ASN1ObjectIdentifier fromOctetString(byte[] arrby) {
        OidHandle oidHandle = new OidHandle(arrby);
        Map map = pool;
        synchronized (map) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)pool.get(oidHandle);
            if (aSN1ObjectIdentifier != null) {
                return aSN1ObjectIdentifier;
            }
        }
        return new ASN1ObjectIdentifier(arrby);
    }

    private static class OidHandle {
        private int key;
        private final byte[] enc;

        OidHandle(byte[] arrby) {
            this.key = Arrays.hashCode(arrby);
            this.enc = arrby;
        }

        public int hashCode() {
            return this.key;
        }

        public boolean equals(Object object) {
            if (object instanceof OidHandle) {
                return Arrays.areEqual(this.enc, ((OidHandle)object).enc);
            }
            return false;
        }
    }
}

