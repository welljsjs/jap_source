/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPath;
import anon.crypto.CertPathInfo;
import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;
import anon.crypto.SignatureVerifier;
import anon.crypto.X509DistinguishedName;
import anon.util.IXMLEncodable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MultiCertPath
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "MultiCertPath";
    private CertPath[] m_certPaths;
    private X509DistinguishedName m_subject;
    private X509DistinguishedName m_issuer;
    private int m_documentType;

    protected MultiCertPath(CertPath[] arrcertPath, int n) {
        if (arrcertPath.length != 0 && arrcertPath[0] != null) {
            this.m_subject = arrcertPath[0].getFirstCertificate().getSubject();
            this.m_issuer = arrcertPath[0].getFirstCertificate().getIssuer();
            for (int i = 1; i < arrcertPath.length; ++i) {
                if (!this.m_subject.equals(arrcertPath[i].getFirstCertificate().getSubject())) {
                    throw new IllegalArgumentException("Wrong subject in MultiCertPath!");
                }
                if (this.m_issuer.equals(arrcertPath[i].getFirstCertificate().getIssuer())) continue;
                throw new IllegalArgumentException("Wrong issuer in MultiCertPath!");
            }
        }
        this.m_documentType = n;
        this.m_certPaths = arrcertPath;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isValid(Date date) {
        if (!this.needsVerification()) {
            return true;
        }
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            boolean bl = this.getFirstVerifiedPath() != null;
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                if ((!bl || !this.m_certPaths[i].verify()) && bl || !this.m_certPaths[i].checkValidity(date)) continue;
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return true;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return false;
        }
    }

    private boolean needsVerification() {
        return SignatureVerifier.getInstance().isCheckSignatures(this.m_documentType);
    }

    public boolean isVerified() {
        if (!this.needsVerification()) {
            return true;
        }
        return this.getFirstVerifiedPath() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CertPath getPath() {
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            CertPath certPath = this.getFirstVerifiedPath();
            if (certPath == null) {
                certPath = this.m_certPaths[0];
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return certPath;
        }
    }

    public Vector getPaths() {
        Vector<CertPath> vector = new Vector<CertPath>();
        for (int i = 0; i < this.m_certPaths.length; ++i) {
            vector.addElement(this.m_certPaths[i]);
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CertPath getFirstVerifiedPath() {
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                if (this.m_certPaths[i] == null || !this.m_certPaths[i].verify()) continue;
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return this.m_certPaths[i];
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getEndEntityKeys() {
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            Vector<IMyPublicKey> vector = new Vector<IMyPublicKey>();
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                if (this.needsVerification() && !this.m_certPaths[i].verify()) continue;
                vector.addElement(this.m_certPaths[i].getFirstCertificate().getPublicKey());
            }
            if (vector.size() != 0) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return vector;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return null;
        }
    }

    public X509DistinguishedName getSubject() {
        return this.m_subject;
    }

    public X509DistinguishedName getIssuer() {
        return this.m_issuer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int countPaths() {
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.m_certPaths.length;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int countVerifiedPaths() {
        int n = 0;
        if (!this.needsVerification()) {
            return this.countPaths();
        }
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                if (!this.m_certPaths[i].verify()) continue;
                ++n;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int countVerifiedAndValidPaths() {
        int n = 0;
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                if (this.needsVerification() && !this.m_certPaths[i].verify() || !this.m_certPaths[i].checkValidity(new Date())) continue;
                ++n;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getMaxLength() {
        int n = 0;
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                if (this.m_certPaths[i].length() <= n) continue;
                n = this.m_certPaths[i].length();
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CertPathInfo[] getPathInfos() {
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            CertPathInfo[] arrcertPathInfo = new CertPathInfo[this.m_certPaths.length];
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                arrcertPathInfo[i] = this.m_certPaths[i].getPathInfo();
                if (this.needsVerification()) continue;
                arrcertPathInfo[i].setVerified(-1);
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return arrcertPathInfo;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        if (document == null) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        CertPath[] arrcertPath = this.m_certPaths;
        synchronized (this.m_certPaths) {
            for (int i = 0; i < this.m_certPaths.length; ++i) {
                Enumeration enumeration = this.m_certPaths[i].getCertificates().elements();
                while (enumeration.hasMoreElements()) {
                    element.appendChild(((JAPCertificate)enumeration.nextElement()).toXmlElement(document));
                }
            }
            // ** MonitorExit[var4_3] (shouldn't be in output)
            return element;
        }
    }
}

