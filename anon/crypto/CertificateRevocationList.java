/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.ByteSignature;
import anon.crypto.IMyPrivateKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyX509Extensions;
import anon.crypto.PKCS12;
import anon.crypto.RevokedCertificate;
import anon.crypto.X509CertificateIssuer;
import anon.crypto.X509DistinguishedName;
import anon.crypto.X509IssuingDistributionPoint;
import anon.util.Base64;
import anon.util.IResourceInstantiator;
import anon.util.IXMLEncodable;
import anon.util.ResourceLoader;
import anon.util.XMLUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CertificateRevocationList
implements IXMLEncodable {
    private static final String BASE64_TAG = "X509 CRL";
    private static final String XML_ELEMENT_NAME = "X509CRL";
    private CertificateList m_crl;
    private Date m_thisUpdate;
    private Date m_nextUpdate;
    private X509DistinguishedName m_issuer;
    private MyX509Extensions m_extensions;

    public CertificateRevocationList(PKCS12 pKCS12, Vector vector, Date date, MyX509Extensions myX509Extensions) {
        this(new CRLGenerator(pKCS12.getSubject().getX500Name(), vector, date, myX509Extensions).sign(pKCS12));
    }

    public CertificateRevocationList(CertificateList certificateList) {
        this.m_crl = certificateList;
        this.m_issuer = new X509DistinguishedName(this.m_crl.getIssuer());
        this.m_extensions = new MyX509Extensions(this.m_crl.getTBSCertList().getExtensions());
        this.m_thisUpdate = this.m_crl.getThisUpdate().getDate();
        if (this.m_crl.getNextUpdate() != null) {
            this.m_nextUpdate = this.m_crl.getNextUpdate().getDate();
        }
    }

    public static CertificateRevocationList getInstance(byte[] arrby) {
        if (arrby == null || arrby.length == 0) {
            return null;
        }
        try {
            ASN1Sequence aSN1Sequence = JAPCertificate.toASN1Sequence(arrby, XML_ELEMENT_NAME);
            return new CertificateRevocationList(CertificateList.getInstance(aSN1Sequence));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static CertificateRevocationList getInstance(File file) {
        if (file != null) {
            try {
                return CertificateRevocationList.getInstance(new FileInputStream(file));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static CertificateRevocationList getInstance(InputStream inputStream) {
        byte[] arrby;
        try {
            arrby = ResourceLoader.getStreamAsBytes(inputStream);
        }
        catch (IOException iOException) {
            return null;
        }
        return CertificateRevocationList.getInstance(arrby);
    }

    public static Hashtable getInstance(String string, boolean bl, String string2) {
        try {
            return ResourceLoader.loadResources(string, new CRLInstantiator(string2), bl);
        }
        catch (Exception exception) {
            return new Hashtable();
        }
    }

    public X509DistinguishedName getIssuer() {
        return this.m_issuer;
    }

    public boolean isIndirectCRL() {
        X509IssuingDistributionPoint x509IssuingDistributionPoint = (X509IssuingDistributionPoint)this.m_extensions.getExtension(X509IssuingDistributionPoint.IDENTIFIER);
        if (x509IssuingDistributionPoint != null) {
            return x509IssuingDistributionPoint.isIndirectCRL();
        }
        return false;
    }

    public Date getThisUpdate() {
        return this.m_thisUpdate;
    }

    public Date getNextUpdate() {
        return this.m_nextUpdate;
    }

    public MyX509Extensions getExtensions() {
        return this.m_extensions;
    }

    public Vector getRevokedCertificates() {
        Vector<RevokedCertificate> vector = new Vector<RevokedCertificate>();
        TBSCertList.CRLEntry[] arrcRLEntry = this.m_crl.getRevokedCertificates();
        for (int i = 0; i < arrcRLEntry.length; ++i) {
            vector.addElement(new RevokedCertificate(arrcRLEntry[i]));
        }
        return vector;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            new DEROutputStream(byteArrayOutputStream).writeObject(this.m_crl);
        }
        catch (Exception exception) {
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

    public boolean verifiy(JAPCertificate jAPCertificate) {
        if (jAPCertificate == null) {
            return false;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new DEROutputStream(byteArrayOutputStream).writeObject(this.m_crl.getTBSCertList());
            return ByteSignature.verify(byteArrayOutputStream.toByteArray(), this.m_crl.getSignature().getBytes(), jAPCertificate.getPublicKey());
        }
        catch (IOException iOException) {
            return false;
        }
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("xml:space", "preserve");
        XMLUtil.setValue((Node)element, Base64.encode(this.toByteArray(), true));
        return element;
    }

    private static final class CRLInstantiator
    implements IResourceInstantiator {
        private String m_ignoreCRLMark;

        public CRLInstantiator(String string) {
            this.m_ignoreCRLMark = string;
        }

        public Object getInstance(File file, File file2) throws Exception {
            if (file == null || this.isBlocked(file.getName())) {
                return null;
            }
            return CertificateRevocationList.getInstance(file);
        }

        public Object getInstance(ZipEntry zipEntry, ZipFile zipFile) throws Exception {
            if (zipFile == null || this.isBlocked(zipEntry.getName())) {
                return null;
            }
            return CertificateRevocationList.getInstance(zipFile.getInputStream(zipEntry));
        }

        public Object getInstance(InputStream inputStream, String string) {
            if (string == null || this.isBlocked(string)) {
                return null;
            }
            return CertificateRevocationList.getInstance(inputStream);
        }

        private boolean isBlocked(String string) {
            if (this.m_ignoreCRLMark == null || string == null || this.m_ignoreCRLMark.trim().length() == 0) {
                return false;
            }
            if (string.endsWith(this.m_ignoreCRLMark)) {
                return true;
            }
            int n = string.indexOf(this.m_ignoreCRLMark);
            return n >= 0 && (string = string.substring(n, string.length())).indexOf("/") < 0 && string.indexOf(File.separator) < 0;
        }
    }

    private static final class CRLGenerator
    extends V2TBSCertListGenerator {
        public CRLGenerator(X500Name x500Name, Vector vector, Date date, MyX509Extensions myX509Extensions) {
            this.setIssuer(x500Name);
            this.setThisUpdate(new Time(new Date()));
            if (date != null) {
                this.setNextUpdate(new Time(date));
            }
            this.setExtensions(myX509Extensions.getExtensionsAsBCExtensions());
            if (vector != null) {
                Enumeration enumeration = vector.elements();
                while (enumeration.hasMoreElements()) {
                    MyX509Extensions myX509Extensions2 = null;
                    JAPCertificate jAPCertificate = (JAPCertificate)enumeration.nextElement();
                    if (!jAPCertificate.getIssuer().equals(x500Name)) {
                        myX509Extensions2 = new MyX509Extensions(new X509CertificateIssuer(jAPCertificate.getIssuer()));
                    }
                    RevokedCertificate revokedCertificate = new RevokedCertificate(jAPCertificate, new Date(), myX509Extensions2);
                    this.addCRLEntry(revokedCertificate.toASN1Sequence());
                }
            }
        }

        public CertificateList sign(PKCS12 pKCS12) {
            return this.sign(pKCS12.getPrivateKey());
        }

        public CertificateList sign(IMyPrivateKey iMyPrivateKey) {
            try {
                this.setSignature(iMyPrivateKey.getSignatureAlgorithm().getIdentifier());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TBSCertList tBSCertList = this.generateTBSCertList();
                new DEROutputStream(byteArrayOutputStream).writeObject(tBSCertList);
                byte[] arrby = ByteSignature.sign(byteArrayOutputStream.toByteArray(), iMyPrivateKey);
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                aSN1EncodableVector.add(tBSCertList);
                aSN1EncodableVector.add(iMyPrivateKey.getSignatureAlgorithm().getIdentifier());
                aSN1EncodableVector.add(new DERBitString(arrby));
                return new CertificateList(new DERSequence(aSN1EncodableVector));
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.CRYPTO, throwable);
                return null;
            }
        }
    }
}

