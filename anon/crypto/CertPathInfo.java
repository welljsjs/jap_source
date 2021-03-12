/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.JAPCertificate;
import java.util.Vector;

public class CertPathInfo {
    private JAPCertificate m_firstCert;
    private JAPCertificate m_secondCert;
    private JAPCertificate m_rootCert;
    private Vector m_subCACerts;
    private int m_errorPosition = Integer.MAX_VALUE;
    private int m_docType;
    public static final int VERIFIED_NO_ERROR = -1;
    public static final int UNVERIFIED = Integer.MAX_VALUE;

    public CertPathInfo(JAPCertificate jAPCertificate, JAPCertificate jAPCertificate2, JAPCertificate jAPCertificate3, Vector vector, int n) {
        this.m_firstCert = jAPCertificate;
        this.m_secondCert = jAPCertificate2;
        this.m_rootCert = jAPCertificate3;
        this.m_subCACerts = vector;
    }

    public void setVerified(int n) {
        this.m_errorPosition = n;
    }

    public boolean isVerified(int n) {
        return this.m_errorPosition < n;
    }

    public JAPCertificate getFirstCertificate() {
        return this.m_firstCert;
    }

    public JAPCertificate getSecondCertificate() {
        return this.m_secondCert;
    }

    public JAPCertificate getRootCertificate() {
        return this.m_rootCert;
    }

    public Vector getSubCACerts() {
        return this.m_subCACerts;
    }

    public int getDocType() {
        return this.m_docType;
    }

    public int getlength() {
        int n = 0;
        if (this.m_firstCert != null) {
            ++n;
        }
        if (this.m_secondCert != null) {
            ++n;
        }
        if (this.m_rootCert != null) {
            ++n;
        }
        if (this.m_subCACerts != null) {
            n += this.m_subCACerts.size();
        }
        return n;
    }

    public String toString() {
        String string = new String();
        String string2 = "\t";
        if (this.m_rootCert != null) {
            string = string + this.m_rootCert.getSubject().getCommonName() + "\n";
        }
        if (this.m_subCACerts != null) {
            for (int i = this.m_subCACerts.size() - 1; i >= 0; --i) {
                string = string + string2 + ((JAPCertificate)this.m_subCACerts.elementAt(i)).getSubject().getCommonName() + "\n";
                string2 = string2 + string2;
            }
        }
        if (this.m_secondCert != null) {
            string = string + string2 + this.m_secondCert.getSubject().getCommonName() + "\n";
            string2 = string2 + string2;
        }
        if (this.m_firstCert != null) {
            string = string + string2 + this.m_firstCert.getSubject().getCommonName() + "\n";
        }
        return string;
    }
}

