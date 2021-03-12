/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertificateRevocationList;
import anon.crypto.JAPCertificate;
import anon.crypto.RevokedCertificate;
import anon.crypto.X509DistinguishedName;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;

public class RevokedCertifcateStore {
    private static RevokedCertifcateStore m_instance;
    private static final String CRL_PATH = "crls/";
    private Hashtable m_revokedCerts;
    static /* synthetic */ Class class$anon$crypto$RevokedCertifcateStore;

    private RevokedCertifcateStore() {
        CertificateRevocationList certificateRevocationList = null;
        this.m_revokedCerts = new Hashtable();
        Enumeration enumeration = CertificateRevocationList.getInstance(CRL_PATH, true, null).elements();
        while (enumeration.hasMoreElements()) {
            certificateRevocationList = (CertificateRevocationList)enumeration.nextElement();
            this.addRevocations(certificateRevocationList);
        }
        if (certificateRevocationList == null) {
            LogHolder.log(4, LogType.CRYPTO, "Could not load default CRLs!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addRevocations(CertificateRevocationList certificateRevocationList) {
        Enumeration enumeration = certificateRevocationList.getRevokedCertificates().elements();
        boolean bl = certificateRevocationList.isIndirectCRL();
        Hashtable hashtable = this.m_revokedCerts;
        synchronized (hashtable) {
            while (enumeration.hasMoreElements()) {
                RevokedCertificate revokedCertificate = (RevokedCertificate)enumeration.nextElement();
                X509DistinguishedName x509DistinguishedName = null;
                if (bl) {
                    x509DistinguishedName = revokedCertificate.getCertificateIssuer();
                }
                if (x509DistinguishedName == null) {
                    x509DistinguishedName = certificateRevocationList.getIssuer();
                }
                this.m_revokedCerts.put(x509DistinguishedName.toString() + revokedCertificate.getSerialNumber().toString(), revokedCertificate);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RevokedCertifcateStore getInstance() {
        Class class_ = class$anon$crypto$RevokedCertifcateStore == null ? (class$anon$crypto$RevokedCertifcateStore = RevokedCertifcateStore.class$("anon.crypto.RevokedCertifcateStore")) : class$anon$crypto$RevokedCertifcateStore;
        synchronized (class_) {
            if (m_instance == null) {
                m_instance = new RevokedCertifcateStore();
            }
            return m_instance;
        }
    }

    private static String keyValue(JAPCertificate jAPCertificate) {
        return jAPCertificate.getIssuer().toString() + RevokedCertificate.getUniqueSerial(jAPCertificate).toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isCertificateRevoked(JAPCertificate jAPCertificate) {
        Hashtable hashtable = this.m_revokedCerts;
        synchronized (hashtable) {
            return this.m_revokedCerts.containsKey(RevokedCertifcateStore.keyValue(jAPCertificate));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Date getRevocationDate(JAPCertificate jAPCertificate) {
        Hashtable hashtable = this.m_revokedCerts;
        synchronized (hashtable) {
            if (this.isCertificateRevoked(jAPCertificate)) {
                RevokedCertificate revokedCertificate = (RevokedCertificate)this.m_revokedCerts.get(RevokedCertifcateStore.keyValue(jAPCertificate));
                return revokedCertificate.getRevocationDate();
            }
            return null;
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

