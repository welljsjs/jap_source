/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.ByteSignature;
import anon.crypto.JAPCertificate;
import anon.util.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.IssuerAndSerialNumber;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.pkcs.SignerInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;

public class PKCS7SignedData
implements PKCSObjectIdentifiers {
    private int version;
    private int signerversion;
    private Hashtable digestalgos;
    private Vector certs;
    private JAPCertificate signCert;
    private byte[] digest;
    private String digestAlgorithm;
    private String digestEncryptionAlgorithm;
    private final String ID_MD5 = "1.2.840.113549.2.5";
    private final String ID_MD2 = "1.2.840.113549.2.2";
    private final String ID_SHA1 = "1.3.14.3.2.26";
    private final String ID_RSA = "1.2.840.113549.1.1.1";
    private final String ID_DSA = "1.2.840.10040.4.1";

    public PKCS7SignedData(byte[] arrby) throws SecurityException, InvalidKeyException, NoSuchAlgorithmException {
        ASN1Object aSN1Object;
        Object object;
        Enumeration enumeration;
        ASN1Primitive aSN1Primitive;
        Object object2;
        try {
            object2 = new ASN1InputStream(new ByteArrayInputStream(arrby));
            aSN1Primitive = ((ASN1InputStream)object2).readObject();
            Util.closeStream((InputStream)object2);
        }
        catch (IOException iOException) {
            throw new SecurityException("can't decode PKCS7SignedData object");
        }
        if (!(aSN1Primitive instanceof ASN1Sequence)) {
            throw new SecurityException("Not a valid PKCS#7 object - not a sequence");
        }
        object2 = ContentInfo.getInstance(aSN1Primitive);
        if (!((ContentInfo)object2).getContentType().equals(PKCSObjectIdentifiers.signedData)) {
            throw new SecurityException("Not a valid PKCS#7 signed-data object - wrong header " + ((ContentInfo)object2).getContentType().getId());
        }
        SignedData signedData = SignedData.getInstance(((ContentInfo)object2).getContent());
        this.certs = new Vector();
        if (signedData.getCertificates() != null) {
            enumeration = ASN1Set.getInstance(signedData.getCertificates()).getObjects();
            while (enumeration.hasMoreElements()) {
                object = null;
                try {
                    object = JAPCertificate.getInstance(Certificate.getInstance(enumeration.nextElement()));
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (object == null) continue;
                this.certs.addElement(object);
            }
        }
        this.version = signedData.getVersion().getValue().intValue();
        this.digestalgos = new Hashtable();
        enumeration = signedData.getDigestAlgorithms().getObjects();
        while (enumeration.hasMoreElements()) {
            object = (ASN1Sequence)enumeration.nextElement();
            aSN1Object = (ASN1ObjectIdentifier)((ASN1Sequence)object).getObjectAt(0);
            this.digestalgos.put(((ASN1ObjectIdentifier)aSN1Object).getId(), ((ASN1ObjectIdentifier)aSN1Object).getId());
        }
        object = signedData.getSignerInfos();
        if (((ASN1Set)object).size() != 1) {
            throw new SecurityException("This PKCS#7 object has multiple SignerInfos - only one is supported at this time");
        }
        aSN1Object = SignerInfo.getInstance(((ASN1Set)object).getObjectAt(0));
        this.signerversion = ((SignerInfo)aSN1Object).getVersion().getValue().intValue();
        IssuerAndSerialNumber issuerAndSerialNumber = ((SignerInfo)aSN1Object).getIssuerAndSerialNumber();
        BigInteger bigInteger = issuerAndSerialNumber.getCertificateSerialNumber().getValue();
        X500Name x500Name = issuerAndSerialNumber.getName();
        Object object3 = this.certs.elements();
        while (object3.hasMoreElements()) {
            JAPCertificate jAPCertificate = (JAPCertificate)object3.nextElement();
            boolean bl = bigInteger.equals(jAPCertificate.getSerialNumber());
            if (!bl || !x500Name.equals(jAPCertificate.getIssuer().getX500Name())) continue;
            this.signCert = jAPCertificate;
            break;
        }
        if (this.signCert == null) {
            throw new SecurityException("Can't find signing certificate with serial " + bigInteger.toString(16));
        }
        this.digestAlgorithm = ((SignerInfo)aSN1Object).getDigestAlgorithm().getAlgorithm().getId();
        this.digest = ((SignerInfo)aSN1Object).getEncryptedDigest().getOctets();
        this.digestEncryptionAlgorithm = ((SignerInfo)aSN1Object).getDigestEncryptionAlgorithm().getAlgorithm().getId();
        object3 = this.getDigestAlgorithm();
        if (!((String)object3).equalsIgnoreCase("sha1withdsa")) {
            throw new NoSuchAlgorithmException("Signature Algorithm unknown!");
        }
    }

    public String getDigestAlgorithm() {
        String string = this.digestAlgorithm;
        String string2 = this.digestEncryptionAlgorithm;
        if (this.digestAlgorithm.equals("1.2.840.113549.2.5")) {
            string = "MD5";
        } else if (this.digestAlgorithm.equals("1.2.840.113549.2.2")) {
            string = "MD2";
        } else if (this.digestAlgorithm.equals("1.3.14.3.2.26")) {
            string = "SHA1";
        }
        if (this.digestEncryptionAlgorithm.equals("1.2.840.113549.1.1.1")) {
            string2 = "RSA";
        } else if (this.digestEncryptionAlgorithm.equals("1.2.840.10040.4.1")) {
            string2 = "DSA";
        }
        return string + "with" + string2;
    }

    public JAPCertificate[] getCertificates() {
        Object[] arrobject = new JAPCertificate[this.certs.size()];
        this.certs.copyInto(arrobject);
        return arrobject;
    }

    public JAPCertificate getSigningCertificate() {
        return this.signCert;
    }

    public int getVersion() {
        return this.version;
    }

    public int getSigningInfoVersion() {
        return this.signerversion;
    }

    public boolean verify(byte[] arrby) throws SignatureException {
        return ByteSignature.verify(arrby, this.digest, this.signCert.getPublicKey());
    }

    private ASN1Encodable getIssuer(byte[] arrby) {
        try {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(new ByteArrayInputStream(arrby));
            ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
            return aSN1Sequence.getObjectAt(aSN1Sequence.getObjectAt(0) instanceof DERTaggedObject ? 3 : 2);
        }
        catch (IOException iOException) {
            throw new Error("IOException reading from ByteArray: " + iOException);
        }
    }
}

