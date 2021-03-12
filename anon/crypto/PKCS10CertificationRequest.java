/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.CertificationRequest;
import anon.crypto.CertificationRequestInfo;
import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyX509Extensions;
import anon.crypto.PKCS12;
import anon.crypto.Validity;
import anon.crypto.X509DistinguishedName;
import anon.util.Base64;
import anon.util.ResourceLoader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

public final class PKCS10CertificationRequest {
    public static final String FILE_EXTENSION = ".csr";
    private static final String BASE64_TAG = "CERTIFICATE REQUEST";
    private static final String BASE64_ALTERNATIVE_TAG = "NEW CERTIFICATE REQUEST";
    private CertificationRequest m_certificationRequest;
    private String m_sha1Fingerprint;
    private String m_md5Fingerprint;

    public PKCS10CertificationRequest(InputStream inputStream) throws IOException {
        this(ResourceLoader.getStreamAsBytes(inputStream));
    }

    public PKCS10CertificationRequest(byte[] arrby) {
        ASN1Sequence aSN1Sequence = JAPCertificate.toASN1Sequence(arrby, null);
        this.m_certificationRequest = new CertificationRequest(aSN1Sequence);
        this.createFingerprints();
    }

    public PKCS10CertificationRequest(X509DistinguishedName x509DistinguishedName, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair, MyX509Extensions myX509Extensions) {
        CertificationRequestInfo certificationRequestInfo = new CertificationRequestInfo(x509DistinguishedName, asymmetricCryptoKeyPair.getPublic(), myX509Extensions);
        this.m_certificationRequest = new CertificationRequest(certificationRequestInfo, asymmetricCryptoKeyPair);
        this.createFingerprints();
    }

    public PKCS10CertificationRequest(PKCS12 pKCS12) {
        this(pKCS12.getSubject(), pKCS12.getKeyPair(), pKCS12.getExtensions());
    }

    public void toOutputStream(OutputStream outputStream, boolean bl) throws IOException {
        outputStream.write(this.toByteArray(bl));
    }

    public byte[] toByteArray(boolean bl) {
        if (bl) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayOutputStream.write(Base64.createBeginTag(BASE64_TAG).getBytes());
                byteArrayOutputStream.write(Base64.encode(this.getEncoded(), true).getBytes());
                byteArrayOutputStream.write(Base64.createEndTag(BASE64_TAG).getBytes());
            }
            catch (IOException iOException) {
                throw new RuntimeException("Could not write encoded bytes to byte array: " + iOException.getMessage());
            }
            return byteArrayOutputStream.toByteArray();
        }
        return this.getEncoded();
    }

    public boolean verify() {
        return this.m_certificationRequest.verify();
    }

    public JAPCertificate createX509Certificate(PKCS12 pKCS12, Validity validity, MyX509Extensions myX509Extensions, BigInteger bigInteger) {
        return JAPCertificate.getInstance(this.getX509DistinguishedName(), pKCS12.getSubject(), pKCS12.getPrivateKey(), pKCS12.getPublicKey(), validity, myX509Extensions, bigInteger);
    }

    public IMyPublicKey getPublicKey() {
        return this.m_certificationRequest.getPublicKey();
    }

    public String getSHA1Fingerprint() {
        return this.m_sha1Fingerprint;
    }

    public String getMD5Fingerprint() {
        return this.m_md5Fingerprint;
    }

    public X509DistinguishedName getX509DistinguishedName() {
        return this.m_certificationRequest.getCertificationRequestInfo().getX509DistinguishedName();
    }

    public MyX509Extensions getExtensions() {
        return this.m_certificationRequest.getCertificationRequestInfo().getExtensions();
    }

    private void createFingerprints() {
        byte[] arrby = this.toByteArray(false);
        this.m_sha1Fingerprint = JAPCertificate.createFingerprint(new SHA1Digest(), arrby);
        this.m_md5Fingerprint = JAPCertificate.createFingerprint(new MD5Digest(), arrby);
    }

    private byte[] getEncoded() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
        try {
            dEROutputStream.writeObject(this.m_certificationRequest);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException.toString());
        }
        return byteArrayOutputStream.toByteArray();
    }
}

