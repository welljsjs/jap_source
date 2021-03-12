/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509AlternativeName;
import anon.crypto.AbstractX509Extension;
import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.ByteSignature;
import anon.crypto.DSAKeyPair;
import anon.crypto.ICertificate;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.MyX509Extensions;
import anon.crypto.PKCS12;
import anon.crypto.RevokedCertifcateStore;
import anon.crypto.Validity;
import anon.crypto.X509DistinguishedName;
import anon.crypto.X509SubjectAlternativeName;
import anon.crypto.X509SubjectKeyIdentifier;
import anon.util.Base64;
import anon.util.IResourceInstantiator;
import anon.util.IXMLEncodable;
import anon.util.ResourceLoader;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.util.encoders.Hex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class JAPCertificate
implements IXMLEncodable,
Cloneable,
ICertificate {
    public static final int CERTIFICATE_TYPE_ROOT_MIX = 1;
    public static final int CERTIFICATE_TYPE_ROOT_INFOSERVICE = 5;
    public static final int CERTIFICATE_TYPE_ROOT_UPDATE = 6;
    public static final int CERTIFICATE_TYPE_ROOT_PAYMENT = 8;
    public static final int CERTIFICATE_TYPE_MIX = 2;
    public static final int CERTIFICATE_TYPE_INFOSERVICE = 3;
    public static final int CERTIFICATE_TYPE_UPDATE = 4;
    public static final int CERTIFICATE_TYPE_PAYMENT = 7;
    public static final int CERTIFICATE_TYPE_TERMS_AND_CONDITIONS = 9;
    public static final int CERTIFICATE_TYPE_ROOT_TERMS_AND_CONDITIONS = 10;
    public static final int CERTIFICATE_TYPE_ROOT = 0;
    public static final String XML_ELEMENT_NAME = "X509Certificate";
    public static final String XML_ELEMENT_CONTAINER_NAME = "X509Data";
    private static final String BASE64_TAG = "CERTIFICATE";
    private static final String BASE64_ALTERNATIVE_TAG = "X509 CERTIFICATE";
    private static final String IDENTIFIER_DSA_WITH_SHA1 = "1.2.840.10040.4.3";
    private static final String DSA_WITH_SHA1 = "dsaWithSHA1";
    private static final String IDENTIFIER_MD2_WITH_RSA_ENCRYPTION = "1.2.840.113549.1.1.2";
    private static final String MD2_WITH_RSA_ENCRYPTION = "md2WithRSAEncryption";
    private static final String IDENTIFIER_MD5_WITH_RSA_ENCRYPTION = "1.2.840.113549.1.1.4";
    private static final String MD5_WITH_RSA_ENCRYPTION = "md5WithRSAEncryption";
    private static final String IDENTIFIER_SHA1_WITH_RSA_ENCRYPTION = "1.2.840.113549.1.1.5";
    private static final String SHA1_WITH_RSA_ENCRYPTION = "sha-1WithRSAEncryption";
    private static final String IDENTIFIER_ECDSA_WITH_SHA1 = "1.2.840.10045.4.1";
    private static final String ECDSA_WITH_SHA1 = "ecdsa-with-SHA1";
    private static IMyPrivateKey ms_dummyPrivateKey;
    private Certificate m_bcCertificate;
    private X509DistinguishedName m_subject;
    private X509DistinguishedName m_issuer;
    private MyX509Extensions m_extensions;
    private X509SubjectKeyIdentifier m_subjectKeyIdentifier;
    private IMyPublicKey m_PubKey;
    private String m_id;
    private String m_sha1Fingerprint;
    private String m_md5Fingerprint;
    private String m_skeinFingerprint;
    private Validity m_validity;

    private JAPCertificate(Certificate certificate) throws IllegalArgumentException {
        DERTaggedObject dERTaggedObject = new DERTaggedObject(true, 3, certificate);
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(dERTaggedObject, true);
        this.m_bcCertificate = Certificate.getInstance(aSN1Sequence);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = certificate.getSignatureAlgorithm().getAlgorithm();
        try {
            this.m_PubKey = AsymmetricCryptoKeyPair.createPublicKey(certificate.getSubjectPublicKeyInfo());
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
            throw new IllegalArgumentException("Certificate structure contains invalid public key! " + exception);
        }
        byte[] arrby = this.toByteArray();
        this.m_sha1Fingerprint = JAPCertificate.createFingerprint(new SHA1Digest(), arrby);
        this.m_md5Fingerprint = JAPCertificate.createFingerprint(new MD5Digest(), arrby);
        this.m_skeinFingerprint = JAPCertificate.createFingerprint(new SkeinDigest(256, 128), arrby);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.m_bcCertificate.getStartDate().getDate());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(this.m_bcCertificate.getEndDate().getDate());
        this.m_validity = new Validity(calendar, calendar2);
        this.m_subject = new X509DistinguishedName(this.m_bcCertificate.getSubject());
        this.m_issuer = new X509DistinguishedName(this.m_bcCertificate.getIssuer());
        this.m_extensions = new MyX509Extensions(this.m_bcCertificate.getTBSCertificate().getExtensions());
        this.m_id = this.m_sha1Fingerprint + this.m_validity.getValidFrom() + this.m_validity.getValidTo();
        this.m_subjectKeyIdentifier = (X509SubjectKeyIdentifier)this.m_extensions.getExtension(X509SubjectKeyIdentifier.IDENTIFIER);
        if (this.m_subjectKeyIdentifier == null) {
            this.m_subjectKeyIdentifier = new X509SubjectKeyIdentifier(this.getPublicKey());
        }
    }

    public static JAPCertificate getInstance(Certificate certificate) {
        JAPCertificate jAPCertificate;
        try {
            jAPCertificate = new JAPCertificate(certificate);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        return jAPCertificate;
    }

    public static JAPCertificate getInstance(JAPCertificate jAPCertificate) {
        if (jAPCertificate == null) {
            return null;
        }
        return JAPCertificate.getInstance(jAPCertificate.m_bcCertificate);
    }

    public static JAPCertificate getInstance(byte[] arrby) {
        if (arrby == null || arrby.length == 0) {
            return null;
        }
        try {
            ASN1Sequence aSN1Sequence = JAPCertificate.toASN1Sequence(arrby, XML_ELEMENT_NAME);
            if (aSN1Sequence.size() > 1 && aSN1Sequence.getObjectAt(1) instanceof ASN1ObjectIdentifier && aSN1Sequence.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
                return JAPCertificate.getInstance(Certificate.getInstance(new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(1), true)).getCertificates().getObjectAt(0)));
            }
            Certificate certificate = Certificate.getInstance(aSN1Sequence);
            return JAPCertificate.getInstance(certificate);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static Hashtable getInstance(String string, boolean bl, String string2) {
        try {
            return ResourceLoader.loadResources(string, new X509CertificateInstantiator(string2), bl);
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, exception);
            return new Hashtable();
        }
    }

    public static Hashtable getInstance(String string, boolean bl) {
        return JAPCertificate.getInstance(string, bl, null);
    }

    public static JAPCertificate getInstance(InputStream inputStream) {
        byte[] arrby;
        try {
            arrby = ResourceLoader.getStreamAsBytes(inputStream);
        }
        catch (IOException iOException) {
            return null;
        }
        return JAPCertificate.getInstance(arrby);
    }

    public static JAPCertificate getInstance(Node node) {
        try {
            if (!node.getNodeName().equals(XML_ELEMENT_NAME)) {
                return null;
            }
            Element element = (Element)node;
            String string = XMLUtil.parseValue((Node)element, (String)null);
            byte[] arrby = Base64.decode(string);
            return JAPCertificate.getInstance(arrby);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static JAPCertificate getInstance(File file) {
        if (file == null) {
            return null;
        }
        byte[] arrby = null;
        try {
            arrby = new byte[(int)file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(arrby);
            fileInputStream.close();
        }
        catch (Exception exception) {
            return null;
        }
        return JAPCertificate.getInstance(arrby);
    }

    public static JAPCertificate getInstance(String string) {
        try {
            return JAPCertificate.getInstance(string.getBytes());
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static final JAPCertificate getInstance(IMyPublicKey iMyPublicKey, Calendar calendar) {
        return JAPCertificate.getInstance(new X509DistinguishedName("CN=void"), new X509DistinguishedName("CN=void"), JAPCertificate.getDummyPrivateKey(), iMyPublicKey, new Validity(calendar, -1), null, new BigInteger("1"));
    }

    public static JAPCertificate getInstance(X509DistinguishedName x509DistinguishedName, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, Validity validity) {
        return JAPCertificate.getInstance(x509DistinguishedName, asymmetricCryptoKeyPair, validity, null);
    }

    public static JAPCertificate getInstance(X509DistinguishedName x509DistinguishedName, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, Validity validity, MyX509Extensions myX509Extensions) {
        return JAPCertificate.getInstance(x509DistinguishedName, x509DistinguishedName, asymmetricCryptoKeyPair.getPrivate(), asymmetricCryptoKeyPair.getPublic(), validity, myX509Extensions, new BigInteger("1"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String calculateXORofSKIs(Vector vector) {
        String string;
        if (vector == null) {
            return null;
        }
        Vector vector2 = vector;
        synchronized (vector2) {
            if (vector.size() == 0) {
                return null;
            }
            byte[] arrby = new byte[20];
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                byte[] arrby2 = ((JAPCertificate)enumeration.nextElement()).getRawSubjectKeyIdentifier();
                if (arrby2 == null) continue;
                for (int i = 0; i < arrby.length; ++i) {
                    arrby[i] = (byte)(arrby[i] ^ arrby2[i]);
                }
            }
            string = new String(Hex.encode(arrby));
        }
        return string.toUpperCase();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof JAPCertificate)) {
            return false;
        }
        return this.getId().equals(((JAPCertificate)object).getId());
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public Object clone() {
        return JAPCertificate.getInstance(this.m_bcCertificate);
    }

    public String getId() {
        return this.m_id;
    }

    public IMyPublicKey getPublicKey() {
        return this.m_PubKey;
    }

    public MyX509Extensions getExtensions() {
        return this.m_extensions;
    }

    public String getSubjectKeyIdentifier() {
        return this.m_subjectKeyIdentifier.getValue();
    }

    public String getSubjectKeyIdentifierConcatenated() {
        return this.m_subjectKeyIdentifier.getValueWithoutColon();
    }

    public byte[] getRawSubjectKeyIdentifier() {
        String string = this.m_subjectKeyIdentifier.getValueWithoutColon();
        if (string == null) {
            return null;
        }
        return Hex.decode(string);
    }

    public String getSignatureAlgorithmName() {
        String string = this.m_bcCertificate.getSignatureAlgorithm().getAlgorithm().getId();
        if (string.equals(IDENTIFIER_DSA_WITH_SHA1)) {
            return DSA_WITH_SHA1;
        }
        if (string.equals(IDENTIFIER_SHA1_WITH_RSA_ENCRYPTION)) {
            return SHA1_WITH_RSA_ENCRYPTION;
        }
        if (string.equals(IDENTIFIER_MD5_WITH_RSA_ENCRYPTION)) {
            return MD5_WITH_RSA_ENCRYPTION;
        }
        if (string.equals(IDENTIFIER_MD2_WITH_RSA_ENCRYPTION)) {
            return MD2_WITH_RSA_ENCRYPTION;
        }
        if (string.equals(IDENTIFIER_ECDSA_WITH_SHA1)) {
            return ECDSA_WITH_SHA1;
        }
        return string;
    }

    public BigInteger getSerialNumber() {
        return this.m_bcCertificate.getSerialNumber().getPositiveValue();
    }

    public X509DistinguishedName getIssuer() {
        return this.m_issuer;
    }

    public X509DistinguishedName getSubject() {
        return this.m_subject;
    }

    public String getAnyEmailAddress() {
        try {
            X509DistinguishedName x509DistinguishedName = this.getSubject();
            String string = x509DistinguishedName.getE_EmailAddress();
            if (string != null) {
                return string;
            }
            string = x509DistinguishedName.getEmailAddress();
            if (string != null) {
                return string;
            }
            MyX509Extensions myX509Extensions = this.getExtensions();
            Vector vector = myX509Extensions.getExtensions();
            for (int i = 0; i < vector.size(); ++i) {
                AbstractX509Extension abstractX509Extension = (AbstractX509Extension)vector.elementAt(i);
                if (!(abstractX509Extension instanceof X509SubjectAlternativeName)) continue;
                X509SubjectAlternativeName x509SubjectAlternativeName = (X509SubjectAlternativeName)abstractX509Extension;
                Vector vector2 = x509SubjectAlternativeName.getTags();
                for (int j = 0; j < vector2.size(); ++j) {
                    Integer n = (Integer)vector2.elementAt(j);
                    if (!n.equals(AbstractX509AlternativeName.TAG_EMAIL) || (string = (String)x509SubjectAlternativeName.getValues().elementAt(n)) == null) continue;
                    return string;
                }
            }
        }
        catch (Exception exception) {
            return null;
        }
        return null;
    }

    public JAPCertificate getX509Certificate() {
        return this;
    }

    public String getSHA1Fingerprint() {
        return this.m_sha1Fingerprint;
    }

    public String getMD5Fingerprint() {
        return this.m_md5Fingerprint;
    }

    public String getSKEINFingerprint() {
        return this.m_skeinFingerprint;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            new DEROutputStream(byteArrayOutputStream).writeObject(this.m_bcCertificate);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] toByteArray(boolean bl) {
        if (bl) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayOutputStream.write(Base64.createBeginTag(BASE64_TAG).getBytes());
                byteArrayOutputStream.write(Base64.encode(this.toByteArray(), true).getBytes());
                byteArrayOutputStream.write(Base64.createEndTag(BASE64_TAG).getBytes());
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return byteArrayOutputStream.toByteArray();
        }
        return this.toByteArray();
    }

    public void store(OutputStream outputStream) throws IOException {
        DEROutputStream dEROutputStream = new DEROutputStream(outputStream);
        dEROutputStream.writeObject(this.m_bcCertificate);
    }

    public void store(OutputStream outputStream, boolean bl) throws IOException {
        outputStream.write(this.toByteArray(bl));
    }

    public Validity getValidity() {
        return this.m_validity;
    }

    public synchronized boolean verify(Vector vector) {
        return this.verify(vector.elements());
    }

    public synchronized boolean verify(Hashtable hashtable) {
        return this.verify(hashtable.elements());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized boolean verify(Enumeration enumeration) {
        if (enumeration == null) {
            return false;
        }
        Enumeration enumeration2 = enumeration;
        synchronized (enumeration2) {
            while (enumeration.hasMoreElements()) {
                JAPCertificate jAPCertificate = (JAPCertificate)enumeration.nextElement();
                if (!this.verify(jAPCertificate)) continue;
                return true;
            }
        }
        return false;
    }

    public synchronized boolean verify(JAPCertificate jAPCertificate) {
        if (jAPCertificate == null) {
            return false;
        }
        return this.verify(jAPCertificate.getPublicKey());
    }

    public synchronized boolean verify(IMyPublicKey iMyPublicKey) {
        if (iMyPublicKey == null) {
            return false;
        }
        AlgorithmIdentifier algorithmIdentifier = iMyPublicKey.getSignatureAlgorithm().getIdentifier();
        AlgorithmIdentifier algorithmIdentifier2 = this.m_bcCertificate.getSignatureAlgorithm();
        if (algorithmIdentifier.getAlgorithm().equals(algorithmIdentifier2.getAlgorithm())) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                new DEROutputStream(byteArrayOutputStream).writeObject(this.m_bcCertificate.getTBSCertificate());
                return ByteSignature.verify(byteArrayOutputStream.toByteArray(), this.m_bcCertificate.getSignature().getBytes(), iMyPublicKey);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return false;
    }

    public JAPCertificate sign(PKCS12 pKCS12) {
        X509CertificateGenerator x509CertificateGenerator = new X509CertificateGenerator(this.m_bcCertificate.getTBSCertificate());
        Certificate certificate = x509CertificateGenerator.sign(pKCS12);
        JAPCertificate jAPCertificate = JAPCertificate.getInstance(certificate);
        return jAPCertificate;
    }

    public JAPCertificate sign(PKCS12 pKCS12, Validity validity, MyX509Extensions myX509Extensions, BigInteger bigInteger) {
        return JAPCertificate.getInstance(new X509DistinguishedName(this.m_bcCertificate.getSubject()), pKCS12.getSubject(), pKCS12.getPrivateKey(), this.getPublicKey(), validity, myX509Extensions, bigInteger);
    }

    public static JAPCertificate getInstance(X509DistinguishedName x509DistinguishedName, X509DistinguishedName x509DistinguishedName2, IMyPrivateKey iMyPrivateKey, IMyPublicKey iMyPublicKey, Validity validity, MyX509Extensions myX509Extensions, BigInteger bigInteger) {
        X509CertificateGenerator x509CertificateGenerator = null;
        x509CertificateGenerator = new X509CertificateGenerator(x509DistinguishedName, validity.getValidFrom(), validity.getValidTo(), iMyPublicKey, myX509Extensions, bigInteger);
        Certificate certificate = x509CertificateGenerator.sign(x509DistinguishedName2.getX500Name(), iMyPrivateKey);
        JAPCertificate jAPCertificate = JAPCertificate.getInstance(certificate);
        return jAPCertificate;
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("xml:space", "preserve");
        XMLUtil.setValue((Node)element, Base64.encode(this.toByteArray(), true));
        return element;
    }

    ASN1Encodable getBouncyCastleCertificate() {
        return this.m_bcCertificate;
    }

    SubjectPublicKeyInfo getBouncyCastleSubjectPublicKeyInfo() {
        return this.m_bcCertificate.getSubjectPublicKeyInfo();
    }

    static ASN1Sequence toASN1Sequence(byte[] arrby, String string) {
        ByteArrayInputStream byteArrayInputStream = null;
        if (arrby == null || arrby.length == 0) {
            return null;
        }
        try {
            Object object;
            if (arrby[0] != 48) {
                String string2;
                object = new String(arrby);
                StringTokenizer stringTokenizer = new StringTokenizer((String)object);
                StringBuffer stringBuffer = new StringBuffer();
                boolean bl = false;
                if (string != null && (string.trim().length() == 0 || new StringTokenizer(string).countTokens() > 1)) {
                    string = null;
                }
                block2: while (stringTokenizer.hasMoreTokens()) {
                    int n;
                    int n2;
                    string2 = stringTokenizer.nextToken();
                    if (string2.startsWith("-----BEGIN ".trim())) {
                        while (!string2.endsWith("-----")) {
                            if (stringTokenizer.hasMoreTokens() && (string2 = stringTokenizer.nextToken()) != null) continue;
                            continue block2;
                        }
                        break;
                    }
                    if (string == null || (n2 = string2.indexOf("<" + string)) < 0 || n2 >= (n = ((String)object).indexOf(">"))) continue;
                    n2 = n + 1;
                    n = ((String)object).indexOf("</" + string + ">");
                    if (n < 0) continue;
                    bl = true;
                    stringBuffer.append(((String)object).substring(((String)object).indexOf(">") + 1, n));
                    break;
                }
                if (!bl) {
                    if (!stringTokenizer.hasMoreTokens()) {
                        throw new Exception();
                    }
                    block4: while (stringTokenizer.hasMoreTokens()) {
                        string2 = stringTokenizer.nextToken();
                        if (string2.startsWith("-----END ".trim())) {
                            do {
                                if (!string2.endsWith("-----")) continue;
                                bl = true;
                                break block4;
                            } while (stringTokenizer.hasMoreTokens() && (string2 = stringTokenizer.nextToken()) != null);
                        }
                        stringBuffer.append(string2);
                    }
                }
                if (!bl) {
                    throw new Exception();
                }
                byteArrayInputStream = new ByteArrayInputStream(Base64.decode(stringBuffer.toString()));
            }
            if (byteArrayInputStream == null && arrby[1] == 128) {
                object = new ASN1InputStream(new ByteArrayInputStream(arrby));
                return (ASN1Sequence)((ASN1InputStream)object).readObject();
            }
            if (byteArrayInputStream == null) {
                byteArrayInputStream = new ByteArrayInputStream(arrby);
            }
            return (ASN1Sequence)new ASN1InputStream(byteArrayInputStream).readObject();
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Byte array is no valid ASN1 sequence data!");
        }
    }

    protected static String createFingerprint(Digest digest, byte[] arrby) {
        byte[] arrby2 = new byte[digest.getDigestSize()];
        digest.update(arrby, 0, arrby.length);
        digest.doFinal(arrby2, 0);
        return ByteSignature.toHexString(arrby2);
    }

    private static IMyPrivateKey getDummyPrivateKey() {
        if (ms_dummyPrivateKey == null) {
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(58921787L);
            ms_dummyPrivateKey = DSAKeyPair.getInstance(secureRandom, 256, 100).getPrivate();
        }
        return ms_dummyPrivateKey;
    }

    public boolean isSelfSigned() {
        return this.verify(this.getPublicKey());
    }

    public boolean isRevoked() {
        return RevokedCertifcateStore.getInstance().isCertificateRevoked(this);
    }

    private static final class X509CertificateInstantiator
    implements IResourceInstantiator {
        private String m_strIgnoreCertMark;

        public X509CertificateInstantiator(String string) {
            this.m_strIgnoreCertMark = string;
        }

        public Object getInstance(File file, File file2) throws IOException {
            if (file == null || this.isBlocked(file.getName())) {
                return null;
            }
            JAPCertificate jAPCertificate = null;
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                jAPCertificate = JAPCertificate.getInstance(fileInputStream);
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            try {
                fileInputStream.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return jAPCertificate;
        }

        public Object getInstance(ZipEntry zipEntry, ZipFile zipFile) throws IOException {
            if (zipFile == null || this.isBlocked(zipEntry.getName())) {
                return null;
            }
            return JAPCertificate.getInstance(zipFile.getInputStream(zipEntry));
        }

        public Object getInstance(InputStream inputStream, String string) {
            if (string == null || this.isBlocked(string)) {
                return null;
            }
            return JAPCertificate.getInstance(inputStream);
        }

        private boolean isBlocked(String string) {
            if (this.m_strIgnoreCertMark == null || string == null || this.m_strIgnoreCertMark.trim().length() == 0) {
                return false;
            }
            if (string.endsWith(this.m_strIgnoreCertMark)) {
                return true;
            }
            int n = string.indexOf(this.m_strIgnoreCertMark);
            return n >= 0 && (string = string.substring(n, string.length())).indexOf("/") < 0 && string.indexOf(File.separator) < 0;
        }
    }

    private static final class X509CertificateGenerator
    extends V3TBSCertificateGenerator {
        public X509CertificateGenerator(X509DistinguishedName x509DistinguishedName, Date date, Date date2, IMyPublicKey iMyPublicKey, MyX509Extensions myX509Extensions, BigInteger bigInteger) {
            this.setStartDate(new DERUTCTime(date));
            this.setEndDate(new DERUTCTime(date2));
            if (bigInteger == null) {
                this.setSerialNumber(new ASN1Integer(1L));
            } else {
                this.setSerialNumber(new ASN1Integer(bigInteger));
            }
            this.setSubject(x509DistinguishedName.getX500Name());
            this.setSubjectPublicKeyInfo(iMyPublicKey.getAsSubjectPublicKeyInfo());
            if (myX509Extensions != null && myX509Extensions.getSize() > 0) {
                this.setExtensions(myX509Extensions.getExtensionsAsBCExtensions());
            } else {
                this.setExtensions(new MyX509Extensions(new Vector()).getExtensionsAsBCExtensions());
            }
        }

        public X509CertificateGenerator(TBSCertificate tBSCertificate) {
            this.setStartDate(tBSCertificate.getStartDate());
            this.setEndDate(tBSCertificate.getEndDate());
            this.setSerialNumber(tBSCertificate.getSerialNumber());
            this.setSubject(tBSCertificate.getSubject());
            this.setSubjectPublicKeyInfo(tBSCertificate.getSubjectPublicKeyInfo());
            this.setExtensions(tBSCertificate.getExtensions());
            this.setIssuer(tBSCertificate.getIssuer());
            this.setSignature(tBSCertificate.getSignature());
        }

        public Certificate sign(PKCS12 pKCS12) {
            return this.sign(pKCS12.getX509Certificate().m_bcCertificate.getSubject(), pKCS12.getPrivateKey());
        }

        public Certificate sign(X500Name x500Name, IMyPrivateKey iMyPrivateKey) {
            return this.sign_internal(x500Name, iMyPrivateKey);
        }

        private Certificate sign_internal(X500Name x500Name, IMyPrivateKey iMyPrivateKey) {
            try {
                this.setIssuer(x500Name);
                this.setSignature(iMyPrivateKey.getSignatureAlgorithm().getIdentifier());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TBSCertificate tBSCertificate = this.generateTBSCertificate();
                new DEROutputStream(byteArrayOutputStream).writeObject(tBSCertificate);
                byte[] arrby = ByteSignature.sign(byteArrayOutputStream.toByteArray(), iMyPrivateKey);
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                aSN1EncodableVector.add(tBSCertificate);
                aSN1EncodableVector.add(iMyPrivateKey.getSignatureAlgorithm().getIdentifier());
                aSN1EncodableVector.add(new DERBitString(arrby));
                return Certificate.getInstance(new DERSequence(aSN1EncodableVector));
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.MISC, throwable);
                return null;
            }
        }
    }
}

