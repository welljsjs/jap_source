/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPath;
import anon.crypto.JAPCertificate;

public class CertificateInfoStructure {
    private CertPath m_certPath;
    private JAPCertificate m_parentCertificate;
    private int m_certificateType;
    private boolean m_enabled;
    private boolean m_certificateNeedsVerification;
    private boolean m_onlyHardRemovable;
    private boolean m_bNotRemovable;

    public CertificateInfoStructure(CertPath certPath, JAPCertificate jAPCertificate, int n, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (certPath == null) {
            throw new IllegalArgumentException("CertPath may not be null");
        }
        this.m_certPath = certPath;
        this.m_parentCertificate = jAPCertificate;
        this.m_certificateType = n;
        this.m_enabled = bl;
        this.m_certificateNeedsVerification = bl2;
        this.m_onlyHardRemovable = bl3;
        this.m_bNotRemovable = bl4;
    }

    public JAPCertificate getCertificate() {
        return this.m_certPath.getFirstCertificate();
    }

    public JAPCertificate getParentCertificate() {
        return this.m_parentCertificate;
    }

    public CertPath getCertPath() {
        return this.m_certPath;
    }

    public int getCertificateType() {
        return this.m_certificateType;
    }

    public boolean getCertificateNeedsVerification() {
        return this.m_certificateNeedsVerification;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isAvailable() {
        boolean bl = false;
        CertificateInfoStructure certificateInfoStructure = this;
        synchronized (certificateInfoStructure) {
            bl = (!this.m_certificateNeedsVerification || this.m_parentCertificate != null) && this.m_enabled;
        }
        return bl;
    }

    public boolean isOnlyHardRemovable() {
        return this.m_onlyHardRemovable;
    }

    public boolean isNotRemovable() {
        return this.m_bNotRemovable;
    }

    public boolean isEnabled() {
        return this.m_enabled;
    }

    public void setEnabled(boolean bl) {
        this.m_enabled = bl;
    }
}

