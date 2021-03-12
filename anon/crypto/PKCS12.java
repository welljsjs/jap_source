/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.ICertificate;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyX509Extensions;
import anon.crypto.PKCS10CertificationRequest;
import anon.crypto.Validity;
import anon.crypto.X509DistinguishedName;
import anon.crypto.X509SubjectKeyIdentifier;
import anon.util.Base64;
import anon.util.IMiscPasswordReader;
import anon.util.ResourceLoader;
import anon.util.SingleStringPasswordReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERConstructedOctetString;
import org.bouncycastle.asn1.BEROutputStream;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public final class PKCS12
implements PKCSObjectIdentifiers,
X509ObjectIdentifiers,
ICertificate {
    public static final String FILE_EXTENSION = ".pfx";
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 100;
    private static final String BASE64_TAG = "PKCS12";
    public static final String XML_ELEMENT_NAME = "X509PKCS12";
    private static final String KEY_ALGORITHM = "1.2.840.113549.1.12.1.3";
    private static final String CERT_ALGORITHM = "1.2.840.113549.1.12.1.6";
    private SecureRandom random = new SecureRandom();
    private AsymmetricCryptoKeyPair m_keyPair;
    private JAPCertificate m_x509certificate;

    public PKCS12(X509DistinguishedName x509DistinguishedName, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, Validity validity) {
        this(x509DistinguishedName, asymmetricCryptoKeyPair, validity, null);
    }

    public PKCS12(X509DistinguishedName x509DistinguishedName, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, Validity validity, MyX509Extensions myX509Extensions) {
        this.m_keyPair = asymmetricCryptoKeyPair;
        this.m_x509certificate = JAPCertificate.getInstance(x509DistinguishedName, asymmetricCryptoKeyPair, validity, myX509Extensions);
    }

    private PKCS12(AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, JAPCertificate jAPCertificate) {
        this.m_keyPair = asymmetricCryptoKeyPair;
        this.m_x509certificate = jAPCertificate;
    }

    public static PKCS12 getInstance(byte[] arrby, char[] arrc) {
        return PKCS12.getInstance(arrby, (IMiscPasswordReader)new SingleStringPasswordReader(arrc));
    }

    public static PKCS12 getInstance(byte[] arrby, String string) {
        return PKCS12.getInstance(arrby, (IMiscPasswordReader)new SingleStringPasswordReader(string));
    }

    public static PKCS12 getInstance(String string, String string2) {
        return PKCS12.getInstance(string.getBytes(), string2.toCharArray());
    }

    public static PKCS12 getInstance(byte[] arrby, IMiscPasswordReader iMiscPasswordReader) {
        if (arrby == null) {
            return null;
        }
        return PKCS12.getInstance((InputStream)new ByteArrayInputStream(arrby), iMiscPasswordReader);
    }

    public static PKCS12 getInstance(InputStream inputStream, char[] arrc) {
        return PKCS12.getInstance(inputStream, (IMiscPasswordReader)new SingleStringPasswordReader(arrc));
    }

    public static PKCS12 getInstance(InputStream inputStream, String string) {
        return PKCS12.getInstance(inputStream, (IMiscPasswordReader)new SingleStringPasswordReader(string));
    }

    public static PKCS12 getInstance(InputStream inputStream, IMiscPasswordReader iMiscPasswordReader) {
        boolean bl = false;
        char[] arrc = new char[]{};
        if (iMiscPasswordReader == null) {
            iMiscPasswordReader = new SingleStringPasswordReader(new char[0]);
        }
        try {
            String string = null;
            IMyPrivateKey iMyPrivateKey = null;
            Certificate certificate = null;
            ASN1Sequence aSN1Sequence = JAPCertificate.toASN1Sequence(ResourceLoader.getStreamAsBytes(inputStream), XML_ELEMENT_NAME);
            if (aSN1Sequence == null) {
                return null;
            }
            ContentInfo contentInfo = Pfx.getInstance(aSN1Sequence).getAuthSafe();
            if (!contentInfo.getContentType().equals(PKCSObjectIdentifiers.data)) {
                return null;
            }
            ASN1InputStream aSN1InputStream = new ASN1InputStream(new ByteArrayInputStream(((ASN1OctetString)contentInfo.getContent()).getOctets()));
            ContentInfo[] arrcontentInfo = AuthenticatedSafe.getInstance((ASN1Sequence)aSN1InputStream.readObject()).getContentInfo();
            for (int i = 0; i < arrcontentInfo.length; ++i) {
                Object object;
                Object object2;
                Object object3;
                Object object4;
                ASN1Sequence aSN1Sequence2;
                Object object5;
                if (arrcontentInfo[i].getContentType().equals(PKCSObjectIdentifiers.data)) {
                    object5 = new ASN1InputStream(new ByteArrayInputStream(((ASN1OctetString)arrcontentInfo[i].getContent()).getOctets()));
                    aSN1Sequence2 = (ASN1Sequence)((ASN1InputStream)object5).readObject();
                } else {
                    if (!arrcontentInfo[i].getContentType().equals(PKCSObjectIdentifiers.encryptedData)) continue;
                    object5 = EncryptedData.getInstance((ASN1Sequence)arrcontentInfo[i].getContent());
                    object4 = ((EncryptedData)object5).getEncryptionAlgorithm().getAlgorithm().getId();
                    object3 = PKCS12.getCipher((String)object4);
                    if (object3 == null) {
                        return null;
                    }
                    object2 = PKCS12PBEParams.getInstance((ASN1Sequence)((EncryptedData)object5).getEncryptionAlgorithm().getParameters());
                    object = null;
                    do {
                        try {
                            object = new ASN1InputStream(new ByteArrayInputStream(PKCS12.codeData(false, ((EncryptedData)object5).getContent().getOctets(), (PKCS12PBEParams)object2, arrc, ((MyCipher)object3).cipher, ((MyCipher)object3).keysize)));
                            aSN1Sequence2 = (ASN1Sequence)((ASN1InputStream)object).readObject();
                            ((FilterInputStream)object).close();
                            bl = true;
                        }
                        catch (Throwable throwable) {
                            aSN1Sequence2 = null;
                            ((FilterInputStream)object).close();
                            if (arrc.length == 0) {
                                arrc = new char[]{'\u0000'};
                                continue;
                            }
                            arrc = iMiscPasswordReader.readPassword(null).toCharArray();
                        }
                    } while (!bl);
                }
                for (int j = 0; j < aSN1Sequence2.size(); ++j) {
                    ASN1Encodable aSN1Encodable;
                    ASN1InputStream aSN1InputStream2;
                    object4 = SafeBag.getInstance((ASN1Sequence)aSN1Sequence2.getObjectAt(j));
                    if (((SafeBag)object4).getBagId().equals(PKCSObjectIdentifiers.certBag)) {
                        aSN1InputStream2 = new ASN1InputStream(new ByteArrayInputStream(((ASN1OctetString)CertBag.getInstance((ASN1Sequence)((SafeBag)object4).getBagValue()).getCertValue()).getOctets()));
                        object3 = (ASN1Sequence)aSN1InputStream2.readObject();
                        certificate = ((ASN1Sequence)object3).size() > 1 && ((ASN1Sequence)object3).getObjectAt(1) instanceof ASN1ObjectIdentifier && ((ASN1Sequence)object3).getObjectAt(0).equals(PKCSObjectIdentifiers.signedData) ? Certificate.getInstance(new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)((ASN1Sequence)object3).getObjectAt(1), true)).getCertificates().getObjectAt(0)) : Certificate.getInstance(object3);
                    } else if (((SafeBag)object4).getBagId().equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
                        object3 = EncryptedPrivateKeyInfo.getInstance((ASN1Sequence)((SafeBag)object4).getBagValue());
                        object2 = PKCS12.getCipher(((EncryptedPrivateKeyInfo)object3).getEncryptionAlgorithm().getAlgorithm().getId());
                        if (object2 == null) {
                            return null;
                        }
                        object = PKCS12PBEParams.getInstance((ASN1Sequence)((EncryptedPrivateKeyInfo)object3).getEncryptionAlgorithm().getParameters());
                        do {
                            aSN1InputStream2 = null;
                            try {
                                aSN1InputStream2 = new ASN1InputStream(new ByteArrayInputStream(PKCS12.codeData(false, ((EncryptedPrivateKeyInfo)object3).getEncryptedData(), (PKCS12PBEParams)object, arrc, ((MyCipher)object2).cipher, ((MyCipher)object2).keysize)));
                                aSN1Encodable = new PrivateKeyInfo((ASN1Sequence)aSN1InputStream2.readObject());
                                bl = true;
                            }
                            catch (Throwable throwable) {
                                aSN1Encodable = null;
                                aSN1InputStream2.close();
                                if (arrc.length == 0) {
                                    arrc = new char[]{'\u0000'};
                                    continue;
                                }
                                while ((arrc = iMiscPasswordReader.readPassword(null).toCharArray()).length == 0 || arrc.length == 1 && arrc[0] == '0') {
                                }
                            }
                        } while (!bl);
                        iMyPrivateKey = new AsymmetricCryptoKeyPair((PrivateKeyInfo)aSN1Encodable).getPrivate();
                    }
                    if (string != null || ((SafeBag)object4).getBagAttributes() == null) continue;
                    object3 = ((SafeBag)object4).getBagAttributes().getObjects();
                    while (object3.hasMoreElements()) {
                        object2 = (ASN1Sequence)object3.nextElement();
                        object = (ASN1ObjectIdentifier)((ASN1Sequence)object2).getObjectAt(0);
                        aSN1Encodable = ((ASN1Set)((ASN1Sequence)object2).getObjectAt(1)).getObjectAt(0);
                        if (!((ASN1Primitive)object).equals(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) continue;
                        string = ((DERBMPString)aSN1Encodable).getString();
                    }
                }
            }
            if (certificate != null) {
                return new PKCS12(new AsymmetricCryptoKeyPair(iMyPrivateKey), JAPCertificate.getInstance(certificate));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public byte[] toByteArray() {
        return this.toByteArray("".toCharArray());
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

    public byte[] toByteArray(char[] arrc, boolean bl) {
        if (bl) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayOutputStream.write(Base64.createBeginTag(BASE64_TAG).getBytes());
                byteArrayOutputStream.write(Base64.encode(this.toByteArray(arrc), true).getBytes());
                byteArrayOutputStream.write(Base64.createEndTag(BASE64_TAG).getBytes());
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return byteArrayOutputStream.toByteArray();
        }
        return this.toByteArray(arrc);
    }

    public byte[] toByteArray(char[] arrc) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            this.store(byteArrayOutputStream, arrc);
            byteArrayOutputStream.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void store(OutputStream outputStream, char[] arrc) throws IOException {
        Object object;
        Object object2;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (arrc == null) {
            arrc = new char[]{};
        }
        byte[] arrby = new byte[20];
        this.random.nextBytes(arrby);
        PKCS12PBEParams pKCS12PBEParams = new PKCS12PBEParams(arrby, 100);
        byte[] arrby2 = PKCS12.codeData(true, this.m_keyPair.getPrivate().getEncoded(), pKCS12PBEParams, arrc, new DESedeEngine(), 192);
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(KEY_ALGORITHM), pKCS12PBEParams.toASN1Primitive());
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(algorithmIdentifier, arrby2);
        ASN1Encodable[] arraSN1Encodable = new DERSequence[2];
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(PKCSObjectIdentifiers.pkcs_9_at_localKeyId);
        aSN1EncodableVector.add(new DERSet(this.createSubjectKeyId()));
        arraSN1Encodable[0] = new DERSequence(aSN1EncodableVector);
        aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
        aSN1EncodableVector.add(new DERSet(new DERBMPString(this.getAlias())));
        arraSN1Encodable[1] = new DERSequence(aSN1EncodableVector);
        DERSet dERSet = new DERSet(arraSN1Encodable);
        BERConstructedOctetString bERConstructedOctetString = new BERConstructedOctetString(new DERSequence(new SafeBag(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag, encryptedPrivateKeyInfo.toASN1Primitive(), dERSet)));
        byte[] arrby3 = new byte[20];
        this.random.nextBytes(arrby3);
        PKCS12PBEParams pKCS12PBEParams2 = new PKCS12PBEParams(arrby3, 100);
        AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(new ASN1ObjectIdentifier(CERT_ALGORITHM), pKCS12PBEParams2);
        CertBag certBag = new CertBag(PKCSObjectIdentifiers.x509certType, new DEROctetString(this.m_x509certificate.getBouncyCastleCertificate()));
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        aSN1EncodableVector2.add(PKCSObjectIdentifiers.pkcs_9_at_localKeyId);
        aSN1EncodableVector2.add(new DERSet(this.createSubjectKeyId()));
        arraSN1Encodable = new DERSequence[2];
        arraSN1Encodable[0] = new DERSequence(aSN1EncodableVector2);
        aSN1EncodableVector2 = new ASN1EncodableVector();
        aSN1EncodableVector2.add(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
        aSN1EncodableVector2.add(new DERSet(new DERBMPString(this.getAlias())));
        arraSN1Encodable[1] = new DERSequence(aSN1EncodableVector2);
        DERSet dERSet2 = new DERSet(arraSN1Encodable);
        SafeBag safeBag = new SafeBag(PKCSObjectIdentifiers.certBag, certBag.toASN1Primitive(), dERSet2);
        byteArrayOutputStream.reset();
        DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
        dEROutputStream.writeObject(new DERSequence(safeBag));
        dEROutputStream.close();
        byte[] arrby4 = PKCS12.codeData(true, byteArrayOutputStream.toByteArray(), pKCS12PBEParams2, arrc, new RC2Engine(), 40);
        EncryptedData encryptedData = new EncryptedData(PKCSObjectIdentifiers.data, algorithmIdentifier2, new BERConstructedOctetString(arrby4));
        ContentInfo[] arrcontentInfo = new ContentInfo[]{new ContentInfo(PKCSObjectIdentifiers.data, bERConstructedOctetString), new ContentInfo(PKCSObjectIdentifiers.encryptedData, encryptedData)};
        ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, new BERConstructedOctetString(new AuthenticatedSafe(arrcontentInfo)));
        byte[] arrby5 = new byte[20];
        int n = 100;
        this.random.nextBytes(arrby5);
        byte[] arrby6 = ((ASN1OctetString)contentInfo.getContent()).getOctets();
        MacData macData = null;
        try {
            object2 = new HMac(new SHA1Digest());
            object = PKCS12.makePBEMacParameters(arrc, new PKCS12PBEParams(arrby5, n), 160);
            object2.init((CipherParameters)object);
            object2.update(arrby6, 0, arrby6.length);
            byte[] arrby7 = new byte[object2.getMacSize()];
            object2.doFinal(arrby7, 0);
            AlgorithmIdentifier algorithmIdentifier3 = new AlgorithmIdentifier(X509ObjectIdentifiers.id_SHA1, null);
            DigestInfo digestInfo = new DigestInfo(algorithmIdentifier3, arrby7);
            macData = new MacData(digestInfo, arrby5, n);
        }
        catch (Exception exception) {
            throw new IOException("error constructing MAC: " + exception.toString());
        }
        object2 = new Pfx(contentInfo, macData);
        object = new BEROutputStream(outputStream);
        ((BEROutputStream)object).writeObject(object2);
    }

    public String getAlias() {
        Vector<String> vector = new Vector<String>();
        X509DistinguishedName x509DistinguishedName = this.getSubject();
        vector.addElement(x509DistinguishedName.getCommonName());
        vector.addElement(x509DistinguishedName.getEmailAddress());
        vector.addElement(x509DistinguishedName.getOrganisation());
        for (int i = 0; i < vector.size(); ++i) {
            if (vector.elementAt(i) == null || ((String)vector.elementAt(i)).trim().length() == 0) continue;
            return (String)vector.elementAt(i);
        }
        return "alias unknown";
    }

    public MyX509Extensions getExtensions() {
        return this.m_x509certificate.getExtensions();
    }

    public X509DistinguishedName getSubject() {
        return this.m_x509certificate.getSubject();
    }

    public X509DistinguishedName getIssuer() {
        return this.m_x509certificate.getIssuer();
    }

    public IMyPrivateKey getPrivateKey() {
        return this.m_keyPair.getPrivate();
    }

    public IMyPublicKey getPublicKey() {
        return this.m_keyPair.getPublic();
    }

    public AsymmetricCryptoKeyPair getKeyPair() {
        return this.m_keyPair;
    }

    public JAPCertificate getX509Certificate() {
        return this.m_x509certificate;
    }

    public PKCS10CertificationRequest createCertifcationRequest() {
        return new PKCS10CertificationRequest(this);
    }

    public boolean setX509Certificate(JAPCertificate jAPCertificate) {
        if (jAPCertificate != null && this.m_x509certificate.getPublicKey().equals(jAPCertificate.getPublicKey())) {
            this.m_x509certificate = (JAPCertificate)jAPCertificate.clone();
            return true;
        }
        return false;
    }

    public void sign(PKCS12 pKCS12) {
        this.m_x509certificate = this.m_x509certificate.sign(pKCS12);
    }

    public void sign(PKCS12 pKCS12, Validity validity, MyX509Extensions myX509Extensions, BigInteger bigInteger) {
        this.m_x509certificate = this.m_x509certificate.sign(pKCS12, validity, myX509Extensions, bigInteger);
    }

    private static byte[] codeData(boolean bl, byte[] arrby, PKCS12PBEParams pKCS12PBEParams, char[] arrc, BlockCipher blockCipher, int n) throws IOException {
        byte[] arrby2;
        try {
            PaddedBufferedBlockCipher paddedBufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(blockCipher));
            CipherParameters cipherParameters = PKCS12.makePBEParameters(arrc, pKCS12PBEParams, paddedBufferedBlockCipher.getUnderlyingCipher().getAlgorithmName(), n, 64);
            cipherParameters = new ParametersWithRandom(cipherParameters, new SecureRandom());
            ((BufferedBlockCipher)paddedBufferedBlockCipher).init(bl, cipherParameters);
            byte[] arrby3 = arrby;
            int n2 = arrby3.length;
            int n3 = 0;
            byte[] arrby4 = new byte[((BufferedBlockCipher)paddedBufferedBlockCipher).getOutputSize(n2)];
            if (n2 != 0) {
                n3 = ((BufferedBlockCipher)paddedBufferedBlockCipher).processBytes(arrby3, 0, n2, arrby4, 0);
            }
            try {
                n3 += ((BufferedBlockCipher)paddedBufferedBlockCipher).doFinal(arrby4, n3);
            }
            catch (Exception exception) {
                // empty catch block
            }
            arrby2 = new byte[n3];
            System.arraycopy(arrby4, 0, arrby2, 0, n3);
        }
        catch (Exception exception) {
            throw new IOException("exception encrypting data - " + exception.toString());
        }
        return arrby2;
    }

    private static CipherParameters makePBEMacParameters(char[] arrc, PKCS12PBEParams pKCS12PBEParams, int n) {
        PBEParametersGenerator pBEParametersGenerator = PKCS12.makePBEGenerator();
        byte[] arrby = PBEParametersGenerator.PKCS12PasswordToBytes(arrc);
        pBEParametersGenerator.init(arrby, pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
        CipherParameters cipherParameters = pBEParametersGenerator.generateDerivedMacParameters(n);
        for (int i = 0; i != arrby.length; ++i) {
            arrby[i] = 0;
        }
        return cipherParameters;
    }

    private static CipherParameters makePBEParameters(char[] arrc, PKCS12PBEParams pKCS12PBEParams, String string, int n, int n2) {
        PBEParametersGenerator pBEParametersGenerator = PKCS12.makePBEGenerator();
        byte[] arrby = PBEParametersGenerator.PKCS12PasswordToBytes(arrc);
        pBEParametersGenerator.init(arrby, pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
        CipherParameters cipherParameters = n2 != 0 ? pBEParametersGenerator.generateDerivedParameters(n, n2) : pBEParametersGenerator.generateDerivedParameters(n);
        if (string.startsWith("DES")) {
            KeyParameter keyParameter;
            if (cipherParameters instanceof ParametersWithIV) {
                keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
                DESParameters.setOddParity(keyParameter.getKey());
            } else {
                keyParameter = (KeyParameter)cipherParameters;
                DESParameters.setOddParity(keyParameter.getKey());
            }
        }
        for (int i = 0; i != arrby.length; ++i) {
            arrby[i] = 0;
        }
        return cipherParameters;
    }

    private static PBEParametersGenerator makePBEGenerator() {
        return new PKCS12ParametersGenerator(new SHA1Digest());
    }

    private static MyCipher getCipher(String string) {
        if (string.equals(KEY_ALGORITHM)) {
            return new MyCipher(new DESedeEngine(), 192);
        }
        if (string.equals("1.2.840.113549.1.12.1.4")) {
            return new MyCipher(new DESedeEngine(), 128);
        }
        if (string.equals("1.2.840.113549.1.12.1.5")) {
            return new MyCipher(new RC2Engine(), 128);
        }
        if (string.equals(CERT_ALGORITHM)) {
            return new MyCipher(new RC2Engine(), 40);
        }
        return null;
    }

    private SubjectKeyIdentifier createSubjectKeyId() {
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = this.m_x509certificate.getBouncyCastleSubjectPublicKeyInfo();
            byte[] arrby = X509SubjectKeyIdentifier.getDigest(subjectPublicKeyInfo);
            return new SubjectKeyIdentifier(arrby);
        }
        catch (Exception exception) {
            throw new RuntimeException("error creating key");
        }
    }

    private static class MyCipher {
        public BlockCipher cipher;
        public int keysize;

        MyCipher(BlockCipher blockCipher, int n) {
            this.cipher = blockCipher;
            this.keysize = n;
        }
    }
}

