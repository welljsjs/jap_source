/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509KeyIdentifier;
import anon.crypto.ByteSignature;
import anon.crypto.IMyPublicKey;
import anon.crypto.X509DistinguishedName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class X509AuthorityKeyIdentifier
extends AbstractX509KeyIdentifier {
    public static final String IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    private BigInteger m_serial;
    private GeneralNames m_names;

    public X509AuthorityKeyIdentifier(IMyPublicKey iMyPublicKey) {
        super(IDENTIFIER, X509AuthorityKeyIdentifier.createDEROctets(iMyPublicKey, null, null));
        this.createValue();
    }

    public X509AuthorityKeyIdentifier(IMyPublicKey iMyPublicKey, GeneralNames generalNames, BigInteger bigInteger) {
        super(IDENTIFIER, X509AuthorityKeyIdentifier.createDEROctets(iMyPublicKey, generalNames, bigInteger));
        this.createValue();
    }

    public X509AuthorityKeyIdentifier(IMyPublicKey iMyPublicKey, X509DistinguishedName x509DistinguishedName, BigInteger bigInteger) {
        super(IDENTIFIER, X509AuthorityKeyIdentifier.createDEROctets(iMyPublicKey, new GeneralNames(new GeneralName(x509DistinguishedName.getX500Name())), bigInteger));
        this.createValue();
    }

    public X509AuthorityKeyIdentifier(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        this.createValue();
    }

    public String getName() {
        return "AuthorityKeyIdentifier";
    }

    private static byte[] createDEROctets(IMyPublicKey iMyPublicKey, GeneralNames generalNames, BigInteger bigInteger) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AuthorityKeyIdentifier authorityKeyIdentifier = generalNames != null && bigInteger != null ? new AuthorityKeyIdentifier(iMyPublicKey.getAsSubjectPublicKeyInfo(), generalNames, bigInteger) : new AuthorityKeyIdentifier(iMyPublicKey.getAsSubjectPublicKeyInfo());
        try {
            new DEROutputStream(byteArrayOutputStream).writeObject(authorityKeyIdentifier.toASN1Primitive());
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not write DER object to bytes!");
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void createValue() {
        try {
            AuthorityKeyIdentifier authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance(new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets())).readObject());
            byte[] arrby = authorityKeyIdentifier.getKeyIdentifier();
            this.m_value = ByteSignature.toHexString(arrby);
            this.m_serial = authorityKeyIdentifier.getAuthorityCertSerialNumber();
            this.m_names = authorityKeyIdentifier.getAuthorityCertIssuer();
        }
        catch (Exception exception) {
            throw new RuntimeException("Could not read authority key identifier from byte array!");
        }
    }

    public Vector getValues() {
        Vector<String> vector = new Vector<String>();
        vector.addElement(this.m_value);
        if (this.m_names != null) {
            GeneralName[] arrgeneralName = this.m_names.getNames();
            for (int i = 0; i < arrgeneralName.length; ++i) {
                String string;
                if (arrgeneralName[i].getTagNo() == 4) {
                    string = X500Name.getInstance((ASN1Sequence)arrgeneralName[i].getName().toASN1Primitive()).toString();
                } else {
                    try {
                        string = new String(arrgeneralName[i].getName().toASN1Primitive().getEncoded("DER")).trim();
                    }
                    catch (IOException iOException) {
                        string = null;
                    }
                }
                vector.addElement(string);
            }
        }
        if (this.m_serial != null) {
            vector.addElement("authorityCertSerialNumber: " + this.m_serial);
        }
        return vector;
    }
}

