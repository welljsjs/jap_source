/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.crypto.CertificateInfoStructure;
import anon.crypto.SignatureVerifier;
import jap.IJAPConfSavePoint;
import jap.JAPController;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class JAPConfCertSavePoint
implements IJAPConfSavePoint {
    private Vector m_unverifiedPersisitentCertificates = new Vector();

    public void createSavePoint() {
    }

    public void restoreSavePoint() {
    }

    public void restoreDefaults() {
        LogHolder.log(7, LogType.MISC, "JAPConfCertSavePoint: restoreDefaults: Restoring default certificate settings.");
        SignatureVerifier.getInstance().setCheckSignatures(true);
        Enumeration enumeration = SignatureVerifier.getInstance().getVerificationCertificateStore().getAllCertificates().elements();
        while (enumeration.hasMoreElements()) {
            CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)enumeration.nextElement();
            if (certificateInfoStructure.getCertificateNeedsVerification()) continue;
            SignatureVerifier.getInstance().getVerificationCertificateStore().removeCertificate(certificateInfoStructure);
        }
        JAPController.addDefaultCertificates();
    }
}

