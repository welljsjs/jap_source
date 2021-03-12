/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPathInfo;
import anon.crypto.CertificateInfoStructure;
import anon.crypto.JAPCertificate;
import anon.crypto.MyRandom;
import anon.crypto.SignatureVerifier;
import anon.crypto.X509AuthorityKeyIdentifier;
import anon.crypto.X509BasicConstraints;
import anon.crypto.X509KeyUsage;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertPath
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "CertPath";
    public static final String XML_ATTR_CLASS = "rootCertificateClass";
    public static final String XML_ATTR_TYPE = "certificateType";
    public static final int NO_ERRORS = 0;
    public static final int ERROR_VERIFICATION = 1;
    public static final int ERROR_VALIDITY = 2;
    public static final int ERROR_REVOCATION = 3;
    public static final int ERROR_UNKNOWN_CRITICAL_EXTENSION = 4;
    public static final int ERROR_BASIC_CONSTRAINTS_IS_CA = 5;
    public static final int ERROR_BASIC_CONSTRAINTS_IS_NO_CA = 6;
    public static final int ERROR_BASIC_CONSTRAINTS_PATH_TOO_LONG = 7;
    public static final int ERROR_KEY_USAGE = 8;
    public static final int ERROR_VALIDITY_SEVERE = 9;
    private static final int VERIFICATION_INTERVAL = 3600000;
    private static final int VERIFICATION_INTERVAL_MAX = 86400000;
    private static final MyRandom RANDOM_VERIFICATION = new MyRandom();
    private static final long GRACE_PERIOD = 5184000000L;
    private int m_documentType;
    private Vector m_certificates;
    private boolean m_rootFound;
    private boolean m_valid;
    private boolean m_verified;
    private long m_verificationTime;
    private int m_pathError;
    private int m_errorPosition;

    private CertPath(JAPCertificate jAPCertificate, int n) {
        this.m_certificates = new Vector();
        this.m_documentType = n;
        this.m_verificationTime = 0L;
        this.m_verified = false;
        this.m_pathError = 0;
        this.m_errorPosition = -1;
        this.appendCertificate(jAPCertificate);
        this.m_rootFound = false;
    }

    protected CertPath(Element element) throws XMLParseException {
        this.m_pathError = 0;
        if (element == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException("##__root__##", XML_ELEMENT_NAME);
        }
        XMLUtil.parseAttribute((Node)element, XML_ATTR_TYPE, -1);
        NodeList nodeList = element.getElementsByTagName("X509Certificate");
        if (nodeList.getLength() == 0) {
            throw new XMLParseException("No certificates found!");
        }
        this.m_certificates = new Vector(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); ++i) {
            this.m_certificates.addElement(JAPCertificate.getInstance(nodeList.item(i)));
        }
        this.m_valid = this.m_documentType == 0 ? true : this.buildAndValidate(null);
    }

    public static CertPath getRootInstance(JAPCertificate jAPCertificate) {
        CertPath certPath = new CertPath(jAPCertificate, 0);
        certPath.m_valid = true;
        return certPath;
    }

    public static CertPath getInstance(JAPCertificate jAPCertificate, int n, Vector vector) {
        if (jAPCertificate == null) {
            return null;
        }
        CertificateInfoStructure certificateInfoStructure = null;
        certificateInfoStructure = SignatureVerifier.getInstance().getVerificationCertificateStore().getCertificateInfoStructure(jAPCertificate, CertPath.getCertType(n));
        if (certificateInfoStructure != null && certificateInfoStructure.getCertPath().m_valid && (certificateInfoStructure.getCertPath().checkValidity(new Date()) || !CertPath.isPossiblyValid(jAPCertificate, vector))) {
            return certificateInfoStructure.getCertPath();
        }
        CertPath certPath = new CertPath(jAPCertificate, n);
        Vector vector2 = (Vector)vector.clone();
        certPath.m_valid = certPath.buildAndValidate(vector2);
        if (!certPath.m_valid && certificateInfoStructure != null) {
            return certificateInfoStructure.getCertPath();
        }
        SignatureVerifier.getInstance().getVerificationCertificateStore().addCertificateWithVerification(certPath, CertPath.getCertType(n), false);
        return certPath;
    }

    private static boolean isPossiblyValid(JAPCertificate jAPCertificate, Vector vector) {
        if (jAPCertificate.getValidity().isValid(new Date())) {
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                JAPCertificate jAPCertificate2 = (JAPCertificate)enumeration.nextElement();
                if (!jAPCertificate2.getValidity().isValid(new Date())) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean buildAndValidate(Vector vector) {
        JAPCertificate jAPCertificate = null;
        int n = 0;
        int n2 = 0;
        this.m_errorPosition = -1;
        this.build(vector);
        Vector vector2 = this.m_certificates;
        synchronized (vector2) {
            Enumeration enumeration = this.m_certificates.elements();
            if (enumeration.hasMoreElements()) {
                jAPCertificate = (JAPCertificate)enumeration.nextElement();
                do {
                    JAPCertificate jAPCertificate2 = null;
                    if (enumeration.hasMoreElements()) {
                        jAPCertificate2 = (JAPCertificate)enumeration.nextElement();
                    }
                    if ((n2 = this.validate(jAPCertificate, n, jAPCertificate2)) != 0 && (n2 == 1 || n2 == 3 || n2 == 4 || n2 == 9)) {
                        this.m_errorPosition = n;
                        this.m_pathError = n2;
                    }
                    jAPCertificate = jAPCertificate2;
                    ++n;
                } while (jAPCertificate != null);
            }
            return true;
        }
    }

    private void build(Vector vector) {
        JAPCertificate jAPCertificate = null;
        if (vector != null) {
            jAPCertificate = CertPath.doNameAndKeyChaining(this.getLastCertificate(), vector, false);
        }
        while (jAPCertificate != null) {
            this.appendCertificate(jAPCertificate);
            jAPCertificate = CertPath.doNameAndKeyChaining(jAPCertificate, vector, false);
        }
        this.findVerifier();
    }

    private void findVerifier() {
        Vector vector = SignatureVerifier.getInstance().getVerificationCertificateStore().getAvailableCertificatesByType(CertPath.getRootCertType(this.m_documentType));
        JAPCertificate jAPCertificate = CertPath.doNameAndKeyChaining(this.getLastCertificate(), vector, false);
        if (jAPCertificate == null) {
            vector = SignatureVerifier.getInstance().getVerificationCertificateStore().getUnavailableCertificatesByType(CertPath.getRootCertType(this.m_documentType));
            jAPCertificate = CertPath.doNameAndKeyChaining(this.getLastCertificate(), vector, false);
        }
        if (jAPCertificate != null) {
            this.m_rootFound = true;
            this.appendCertificate(jAPCertificate);
        }
    }

    private static JAPCertificate doNameAndKeyChaining(JAPCertificate jAPCertificate, Vector vector, boolean bl) {
        JAPCertificate jAPCertificate2 = null;
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            X509AuthorityKeyIdentifier x509AuthorityKeyIdentifier;
            Object e = enumeration.nextElement();
            JAPCertificate jAPCertificate3 = e instanceof JAPCertificate ? (JAPCertificate)e : ((CertificateInfoStructure)e).getCertificate();
            if (jAPCertificate.getIssuer() == null || jAPCertificate3.getSubject() == null || !bl && jAPCertificate.equals(jAPCertificate3) || !jAPCertificate.getIssuer().equals(jAPCertificate3.getSubject()) || (x509AuthorityKeyIdentifier = (X509AuthorityKeyIdentifier)jAPCertificate.getExtensions().getExtension(X509AuthorityKeyIdentifier.IDENTIFIER)) != null && !x509AuthorityKeyIdentifier.getValue().equals(jAPCertificate3.getSubjectKeyIdentifier())) continue;
            if (jAPCertificate.equals(jAPCertificate3)) {
                jAPCertificate2 = jAPCertificate3;
                continue;
            }
            return jAPCertificate3;
        }
        return jAPCertificate2;
    }

    private int validate(JAPCertificate jAPCertificate, int n, JAPCertificate jAPCertificate2) {
        X509KeyUsage x509KeyUsage;
        if (jAPCertificate2 != null && !jAPCertificate.verify(jAPCertificate2)) {
            return 1;
        }
        if (jAPCertificate.isRevoked()) {
            return 3;
        }
        if (jAPCertificate.getExtensions().hasUnknownCriticalExtensions()) {
            return 4;
        }
        Date date = new Date();
        if (!jAPCertificate.getValidity().isValid(date)) {
            if (jAPCertificate.getValidity().getValidTo().getTime() + 5184000000L < date.getTime()) {
                return 9;
            }
            return 2;
        }
        X509BasicConstraints x509BasicConstraints = (X509BasicConstraints)jAPCertificate.getExtensions().getExtension(X509BasicConstraints.IDENTIFIER);
        if (x509BasicConstraints != null) {
            if (x509BasicConstraints.isCA()) {
                if (n == 0) {
                    return 5;
                }
                int n2 = x509BasicConstraints.getPathLengthConstraint();
                if (n2 != -1 && n2 < n) {
                    return 7;
                }
            } else if (n > 0) {
                return 6;
            }
        }
        if ((x509KeyUsage = (X509KeyUsage)jAPCertificate.getExtensions().getExtension(X509KeyUsage.IDENTIFIER)) != null && (n == 0 ? !x509KeyUsage.allowsDigitalSignature() : !x509KeyUsage.allowsDigitalSignature() || !x509KeyUsage.allowsKeyCertSign())) {
            return 8;
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        if (document == null) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element, XML_ATTR_TYPE, this.m_documentType);
        Vector vector = this.m_certificates;
        synchronized (vector) {
            Enumeration enumeration = this.m_certificates.elements();
            while (enumeration.hasMoreElements()) {
                element.appendChild(((JAPCertificate)enumeration.nextElement()).toXmlElement(document));
            }
        }
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void appendCertificate(JAPCertificate jAPCertificate) {
        Vector vector = this.m_certificates;
        synchronized (vector) {
            if (!this.m_certificates.contains(jAPCertificate)) {
                this.m_certificates.addElement(jAPCertificate);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeLastCertificate() {
        Vector vector = this.m_certificates;
        synchronized (vector) {
            if (this.m_certificates.size() > 1) {
                this.m_certificates.removeElementAt(this.m_certificates.size() - 1);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JAPCertificate getLastCertificate() {
        Vector vector = this.m_certificates;
        synchronized (vector) {
            if (this.m_certificates.size() > 0) {
                return (JAPCertificate)this.m_certificates.lastElement();
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JAPCertificate getFirstCertificate() {
        Vector vector = this.m_certificates;
        synchronized (vector) {
            if (this.m_certificates.size() > 0) {
                return (JAPCertificate)this.m_certificates.firstElement();
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JAPCertificate getSecondCertificate() {
        Vector vector = this.m_certificates;
        synchronized (vector) {
            if (this.m_certificates.size() <= 1) {
                return null;
            }
            return (JAPCertificate)this.m_certificates.elementAt(1);
        }
    }

    private static int getRootCertType(int n) {
        switch (n) {
            case 1: {
                return 1;
            }
            case 2: {
                return 5;
            }
            case 3: {
                return 6;
            }
            case 4: {
                return 8;
            }
            case 5: {
                return 10;
            }
            case 0: {
                return 0;
            }
        }
        return -1;
    }

    private static int getCertType(int n) {
        switch (n) {
            case 1: {
                return 2;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 7;
            }
            case 5: {
                return 9;
            }
            case 0: {
                return 0;
            }
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean checkValidity(Date date) {
        if (date == null) {
            return false;
        }
        Vector vector = this.m_certificates;
        synchronized (vector) {
            Enumeration enumeration = this.m_certificates.elements();
            while (enumeration.hasMoreElements()) {
                if (((JAPCertificate)enumeration.nextElement()).getValidity().isValid(date)) continue;
                return false;
            }
            return true;
        }
    }

    protected boolean isVerifier(JAPCertificate jAPCertificate) {
        if (jAPCertificate == null) {
            return false;
        }
        if (!this.m_valid) {
            return false;
        }
        if (this.m_rootFound && jAPCertificate.equals(this.getLastCertificate())) {
            return true;
        }
        return this.getLastCertificate().verify(jAPCertificate);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public synchronized boolean verify() {
        if (this.m_documentType == 0) {
            return true;
        }
        var3_1 = System.currentTimeMillis() - this.m_verificationTime;
        if (var3_1 < 3600000L) {
            return this.m_verified;
        }
        if (var3_1 < 86400000L && 90 > CertPath.RANDOM_VERIFICATION.nextInt(100)) {
            return this.m_verified;
        }
        this.m_valid = this.buildAndValidate(null);
        this.m_verificationTime = System.currentTimeMillis();
        var1_2 = SignatureVerifier.getInstance().getVerificationCertificateStore().getCertificateInfoStructure(this.getLastCertificate());
        if (!this.m_rootFound) ** GOTO lbl25
        if (var1_2 != null && var1_2.getCertificateType() == CertPath.getRootCertType(this.m_documentType)) {
            if (var1_2.isAvailable() && this.m_valid) {
                this.m_verified = true;
                return true;
            }
        } else {
            if (var1_2 != null && var1_2.getCertificateType() != CertPath.getRootCertType(this.m_documentType)) {
                LogHolder.log(1, LogType.CRYPTO, "Verification root certificate found in wrong type path! Cert doctype: " + var1_2.getCertificateType() + " Expected doc type: " + CertPath.getRootCertType(this.m_documentType) + (var1_2.getCertificate() != null ? " SKI:" + var1_2.getCertificate().getSubjectKeyIdentifier() : ""));
                this.m_verified = false;
                return false;
            }
            this.removeLastCertificate();
            this.m_rootFound = false;
            this.resetVerification();
            return this.verify();
lbl25:
            // 1 sources

            var2_3 = new Vector();
            var2_3.addElement(this.getLastCertificate());
            if (CertPath.doNameAndKeyChaining(this.getLastCertificate(), var2_3, true) != null) {
                var2_3 = SignatureVerifier.getInstance().getVerificationCertificateStore().getAvailableCertificatesByType(CertPath.getCertType(this.m_documentType));
                if (this.m_valid && CertPath.doNameAndKeyChaining(this.getLastCertificate(), var2_3, true) != null) {
                    this.m_verified = true;
                    return true;
                }
            }
        }
        this.m_verified = false;
        return false;
    }

    public int length() {
        return this.m_certificates.size();
    }

    protected void resetVerification() {
        this.m_verificationTime = 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        Vector vector = this.m_certificates;
        synchronized (vector) {
            String string = new String("Certification Path (" + this.length() + "):");
            String string2 = new String();
            for (int i = this.m_certificates.size(); i > 0; --i) {
                string2 = string2 + "\t";
                string = string + "\n" + string2 + ((JAPCertificate)this.m_certificates.elementAt(i - 1)).getSubject().getCommonName();
            }
            return string;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CertPathInfo getPathInfo() {
        JAPCertificate jAPCertificate = null;
        JAPCertificate jAPCertificate2 = null;
        JAPCertificate jAPCertificate3 = null;
        Vector vector = null;
        Vector vector2 = this.m_certificates;
        synchronized (vector2) {
            boolean bl = this.verify();
            int n = this.length();
            jAPCertificate = this.getFirstCertificate();
            if (n > 1 && this.m_rootFound) {
                jAPCertificate3 = this.getLastCertificate();
                --n;
            }
            if (n > 1) {
                jAPCertificate2 = this.getSecondCertificate();
            }
            if (n > 2) {
                vector = new Vector();
                for (int i = 2; i < n; ++i) {
                    vector.addElement(this.m_certificates.elementAt(i));
                }
            }
        }
        CertPathInfo certPathInfo = new CertPathInfo(jAPCertificate, jAPCertificate2, jAPCertificate3, vector, 1);
        certPathInfo.setVerified(this.m_errorPosition);
        return certPathInfo;
    }

    public boolean isValidPath() {
        return this.m_valid;
    }

    protected Vector getCertificates() {
        Vector vector = (Vector)this.m_certificates.clone();
        if (this.m_rootFound) {
            vector.removeElementAt(vector.size() - 1);
        }
        return vector;
    }

    public int getErrorCode() {
        return this.m_pathError;
    }

    public int getErrorPosition() {
        return this.m_errorPosition;
    }
}

