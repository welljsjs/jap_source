/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;

public class X509BasicConstraints
extends AbstractX509Extension {
    public static final String IDENTIFIER = Extension.basicConstraints.getId();
    private boolean m_cA;
    private int m_pathLenConstraint = -1;

    public X509BasicConstraints(boolean bl) {
        super(IDENTIFIER, true, X509BasicConstraints.createDEROctets(bl));
        this.m_cA = bl;
    }

    public X509BasicConstraints(int n) {
        super(IDENTIFIER, true, X509BasicConstraints.createDEROctets(n));
        this.m_cA = true;
        this.m_pathLenConstraint = n;
    }

    public X509BasicConstraints(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        this.createValue();
    }

    private static byte[] createDEROctets(int n) {
        try {
            return new BasicConstraints(n).getEncoded("DER");
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private static byte[] createDEROctets(boolean bl) {
        try {
            return new BasicConstraints(bl).getEncoded("DER");
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private void createValue() {
        try {
            BasicConstraints basicConstraints = BasicConstraints.getInstance(new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets())).readObject());
            this.m_cA = basicConstraints.isCA();
            BigInteger bigInteger = basicConstraints.getPathLenConstraint();
            if (bigInteger != null) {
                this.m_pathLenConstraint = basicConstraints.getPathLenConstraint().intValue();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("Could not read basic constraints from byte array!");
        }
    }

    public boolean isCA() {
        return this.m_cA;
    }

    public int getPathLengthConstraint() {
        return this.m_pathLenConstraint;
    }

    public String getName() {
        return "BasicConstraints";
    }

    public Vector getValues() {
        Vector<String> vector = new Vector<String>();
        vector.addElement(new String("cA=" + this.m_cA));
        if (this.m_pathLenConstraint != -1) {
            vector.addElement(new String("pathLenConstraint=" + this.m_pathLenConstraint));
        }
        return vector;
    }
}

