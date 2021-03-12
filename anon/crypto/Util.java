/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.JAPCertificate;
import anon.crypto.SignatureVerifier;
import anon.util.ResourceLoader;
import java.util.Enumeration;
import logging.LogHolder;
import logging.LogType;

public class Util {
    public static void addDefaultCertificates(String string, int n, String string2) {
        Util.addDefaultCertificates(string, null, n, string2);
    }

    public static void addDefaultCertificates(String string, int n) {
        Util.addDefaultCertificates(string, null, n, null);
    }

    public static void addDefaultCertificates(String string, String[] arrstring, int n) {
        Util.addDefaultCertificates(string, arrstring, n, null);
    }

    public static void addDefaultCertificates(String string, String[] arrstring, int n, String string2) {
        JAPCertificate jAPCertificate = null;
        if (arrstring != null) {
            for (int i = 0; i < arrstring.length; ++i) {
                if (arrstring[i] == null || string2 != null && arrstring[i].endsWith(string2) || (jAPCertificate = JAPCertificate.getInstance(ResourceLoader.loadResource("certificates/" + string + arrstring[i]))) == null) continue;
                SignatureVerifier.getInstance().getVerificationCertificateStore().addCertificateWithoutVerification(jAPCertificate, n, true, true);
            }
        }
        Enumeration enumeration = JAPCertificate.getInstance("certificates/" + string, true, string2).elements();
        while (enumeration.hasMoreElements()) {
            jAPCertificate = (JAPCertificate)enumeration.nextElement();
            SignatureVerifier.getInstance().getVerificationCertificateStore().addCertificateWithoutVerification(jAPCertificate, n, true, true);
        }
        if (jAPCertificate == null) {
            LogHolder.log(3, LogType.MISC, "Error loading certificates of type '" + n + "'.");
        }
    }
}

