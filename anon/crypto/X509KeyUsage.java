/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;

public class X509KeyUsage
extends AbstractX509Extension {
    public static final String IDENTIFIER = Extension.keyUsage.getId();
    public static final int DIGITAL_SIGNATURE = 128;
    public static final int NON_REPUDIATION = 64;
    public static final int KEY_ENCIPHERMENT = 32;
    public static final int DATA_ENCIPHERMENT = 16;
    public static final int KEY_AGREEMENT = 8;
    public static final int KEY_CERT_SIGN = 4;
    public static final int CRL_SIGN = 2;
    public static final int ENCIPHER_ONLY = 1;
    public static final int DECIPHER_ONLY = 32768;
    private static final String TXT_DIGITAL_SIGNATURE = "digitalSignature";
    private static final String TXT_NON_REPUDIATION = "nonRepudiation/contentCommitment";
    private static final String TXT_KEY_ENCIPHERMENT = "keyEncipherment";
    private static final String TXT_DATA_ENCIPHERMENT = "dataEncipherment";
    private static final String TXT_KEY_AGREEMENT = "keyAgreement";
    private static final String TXT_KEY_CERT_SIGN = "keyCertSign";
    private static final String TXT_CRL_SIGN = "cRLSign";
    private static final String TXT_ENCIPHER_ONLY = "encipherOnly";
    private static final String TXT_DECIPHER_ONLY = "decipherOnly";
    private static final int[] USAGES = new int[]{128, 64, 32, 16, 8, 4, 2, 1, 32768};
    private int m_usage;

    public X509KeyUsage(int n) {
        super(IDENTIFIER, true, X509KeyUsage.createDEROctet(n));
        this.m_usage = n;
    }

    public X509KeyUsage(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        this.createValue();
    }

    private static byte[] createDEROctet(int n) {
        try {
            return new KeyUsage(n).getEncoded("DER");
        }
        catch (IOException iOException) {
            return null;
        }
    }

    public String getName() {
        return "KeyUsage";
    }

    public Vector getValues() {
        Vector<String> vector = new Vector<String>();
        for (int i = 0; i < USAGES.length; ++i) {
            if ((USAGES[i] & this.m_usage) != USAGES[i]) continue;
            vector.addElement(this.getUsageString(USAGES[i]));
        }
        return vector;
    }

    public String getUsageString(int n) {
        switch (n) {
            case 128: {
                return TXT_DIGITAL_SIGNATURE;
            }
            case 64: {
                return TXT_NON_REPUDIATION;
            }
            case 32: {
                return TXT_KEY_ENCIPHERMENT;
            }
            case 16: {
                return TXT_DATA_ENCIPHERMENT;
            }
            case 8: {
                return TXT_KEY_AGREEMENT;
            }
            case 4: {
                return TXT_KEY_CERT_SIGN;
            }
            case 2: {
                return TXT_CRL_SIGN;
            }
            case 1: {
                return TXT_ENCIPHER_ONLY;
            }
            case 32768: {
                return TXT_DECIPHER_ONLY;
            }
        }
        return null;
    }

    private void createValue() {
        try {
            this.m_usage = ((DERBitString)new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets())).readObject()).intValue();
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not read key usage from byte array!");
        }
    }

    public boolean isAllowedUsage(int n) {
        return (this.m_usage & n) == this.m_usage;
    }

    public boolean allowsDigitalSignature() {
        return this.isAllowedUsage(128);
    }

    public boolean allowsNonRepudiation() {
        return this.isAllowedUsage(64);
    }

    public boolean allowsKeyEncipherment() {
        return this.isAllowedUsage(32);
    }

    public boolean allowsDataEncipherment() {
        return this.isAllowedUsage(16);
    }

    public boolean allowsKeyAgreement() {
        return this.isAllowedUsage(8);
    }

    public boolean allowsKeyCertSign() {
        return this.isAllowedUsage(4);
    }

    public boolean allowsCRLSign() {
        return this.isAllowedUsage(2);
    }

    public boolean allowsEncipherOnly() {
        return this.isAllowedUsage(1);
    }

    public boolean allowsDecipherOnly() {
        return this.isAllowedUsage(32768);
    }
}

