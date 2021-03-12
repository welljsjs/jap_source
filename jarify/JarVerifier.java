/*
 * Decompiled with CFR 0.150.
 */
package jarify;

import anon.crypto.JAPCertificate;
import anon.crypto.PKCS7SignedData;
import anon.util.Base64;
import jarify.JarFile;
import jarify.JarFileEntry;
import jarify.JarManifest;
import jarify.JarSignatureFile;
import java.io.File;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipException;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.crypto.Digest;

public final class JarVerifier {
    private JarFile m_jarFile;
    private JarManifest m_Manifest;
    private JAPCertificate m_certRoot = null;
    private Hashtable digestCache = new Hashtable();
    private Hashtable aliasSBF = new Hashtable();

    private JarVerifier(File file) throws ZipException, IOException, SecurityException {
        this.m_jarFile = new JarFile(file);
        this.m_Manifest = this.m_jarFile.getManifest();
    }

    private void close() {
        this.m_jarFile.close();
    }

    private Vector InitAliases(Vector vector) {
        Vector<String> vector2 = new Vector<String>();
        for (int i = 0; i < vector.size(); ++i) {
            String string = null;
            JAPCertificate[] arrjAPCertificate = null;
            PKCS7SignedData pKCS7SignedData = null;
            string = (String)vector.elementAt(i);
            JarFileEntry jarFileEntry = this.m_jarFile.getSignatureBlockFile(string);
            if (jarFileEntry == null) continue;
            LogHolder.log(7, LogType.MISC, "Checking certificate chain for alias: " + string);
            try {
                pKCS7SignedData = new PKCS7SignedData(jarFileEntry.getContent());
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.MISC, exception.getMessage());
                continue;
            }
            if (pKCS7SignedData == null) {
                LogHolder.log(7, LogType.MISC, "Could not get PKCS#7 data object!");
                continue;
            }
            this.aliasSBF.put(string, pKCS7SignedData);
            arrjAPCertificate = pKCS7SignedData.getCertificates();
            if (arrjAPCertificate == null) continue;
            try {
                arrjAPCertificate[arrjAPCertificate.length - 1].verify(this.m_certRoot.getPublicKey());
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.MISC, exception.getMessage());
                continue;
            }
            try {
                for (int j = 0; j < arrjAPCertificate.length - 1; ++j) {
                    LogHolder.log(7, LogType.MISC, "Checking certificate No. : " + j + 1);
                    arrjAPCertificate[j].verify(arrjAPCertificate[j + 1].getPublicKey());
                    LogHolder.log(7, LogType.MISC, "Certificate No. " + j + 1 + " verified OK.");
                }
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.MISC, exception.getMessage());
                continue;
            }
            vector2.addElement(string);
        }
        return vector2;
    }

    public static boolean verify(File file, JAPCertificate jAPCertificate) {
        try {
            JarVerifier jarVerifier = new JarVerifier(file);
            boolean bl = jarVerifier.verify(jAPCertificate);
            jarVerifier.close();
            return bl;
        }
        catch (Throwable throwable) {
            LogHolder.log(0, LogType.MISC, throwable);
            return false;
        }
    }

    private boolean verify(JAPCertificate jAPCertificate) {
        this.m_certRoot = jAPCertificate;
        if (this.m_certRoot == null) {
            return false;
        }
        LogHolder.log(7, LogType.MISC, "Searching for Signatures...");
        if (!this.isSignedJar()) {
            return false;
        }
        LogHolder.log(7, LogType.MISC, "This is a signed Jarfile.\n");
        LogHolder.log(7, LogType.MISC, "Verifying Manifest entries...");
        if (!this.verifyManifestDigests()) {
            return false;
        }
        LogHolder.log(7, LogType.MISC, "Manifest entries verified OK.\n");
        Vector vector = this.InitAliases(this.m_jarFile.getAliasList());
        if (vector.size() < 1) {
            LogHolder.log(7, LogType.MISC, "\nNo Aliases present that can be validated with the given root certificate!\n");
            return false;
        }
        String string = null;
        for (int i = 0; i < vector.size(); ++i) {
            string = (String)vector.elementAt(i);
            if (string == null || string == "") {
                LogHolder.log(7, LogType.MISC, "\nAlias error");
                return false;
            }
            LogHolder.log(7, LogType.MISC, "Verifying Signature File entries for alias \"" + string + "\"...");
            if (!this.verifySFDigests(string)) {
                return false;
            }
            LogHolder.log(7, LogType.MISC, "Entries verified OK.");
            LogHolder.log(7, LogType.MISC, "Verifying Signature for alias \"" + string + "\"...");
            if (!this.verifySignature(string)) {
                return false;
            }
            LogHolder.log(7, LogType.MISC, "Signature from \"" + string + "\" is genuine.\n");
        }
        return true;
    }

    private boolean verifySignature(String string) {
        boolean bl = false;
        JarSignatureFile jarSignatureFile = this.m_jarFile.getSignatureFile(string);
        if (jarSignatureFile == null) {
            return false;
        }
        JarFileEntry jarFileEntry = this.m_jarFile.getSignatureBlockFile(string);
        if (jarFileEntry == null) {
            return false;
        }
        byte[] arrby = jarSignatureFile.getContent();
        if (arrby == null) {
            return false;
        }
        String string2 = jarFileEntry.getName();
        if (string2.endsWith(".DSA") || string2.endsWith(".RSA")) {
            LogHolder.log(7, LogType.MISC, "Found " + string2.substring(string2.lastIndexOf(".") + 1) + " signature in : " + string2);
            try {
                PKCS7SignedData pKCS7SignedData = (PKCS7SignedData)this.aliasSBF.get(string);
                bl = pKCS7SignedData.verify(arrby);
                if (!bl) {
                    LogHolder.log(7, LogType.MISC, "Wrong Signature in " + string2);
                    return false;
                }
                LogHolder.log(7, LogType.MISC, "Signature in " + string2 + " verified OK.");
            }
            catch (SignatureException signatureException) {
                return false;
            }
        }
        return bl;
    }

    private boolean isSignedJar() {
        if (this.m_jarFile == null) {
            return false;
        }
        Vector vector = this.m_jarFile.getAliasList();
        String[] arrstring = new String[]{".DSA", ".RSA"};
        if (vector.size() < 1) {
            return false;
        }
        if (!this.m_jarFile.fileExists("META-INF/MANIFEST.MF")) {
            return false;
        }
        for (int i = 0; i < vector.size(); ++i) {
            boolean bl = false;
            String string = "META-INF/" + vector.elementAt(i);
            string = string.toUpperCase();
            for (int j = 0; j < arrstring.length; ++j) {
                if (!this.m_jarFile.fileExists(string + arrstring[j])) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }

    private boolean verifySFDigests(String string) {
        byte[] arrby;
        Object object;
        Object object2;
        String string2;
        JarSignatureFile jarSignatureFile = this.m_jarFile.getSignatureFile(string);
        if (jarSignatureFile == null) {
            return false;
        }
        Vector vector = jarSignatureFile.getManifestDigestList();
        for (int i = 0; i < vector.size(); ++i) {
            string2 = (String)vector.elementAt(i);
            String string3 = jarSignatureFile.getManifestDigest(string2);
            object2 = this.getDigestClass(string2);
            object = new byte[object2.getDigestSize()];
            try {
                arrby = this.m_Manifest.getContent();
                if (arrby == null) {
                    LogHolder.log(7, LogType.MISC, "Manifest file null.");
                    return false;
                }
                object2.update(arrby, 0, arrby.length);
                object2.doFinal((byte[])object, 0);
                String string4 = new String(Base64.encode((byte[])object, false));
                if (string3.equals(string4)) continue;
                LogHolder.log(2, LogType.MISC, "Digest verify failed for manifest file. Digest:\n" + string3 + "\n\nHash:\n" + string4);
                return false;
            }
            catch (Exception exception) {
                return false;
            }
        }
        Vector vector2 = jarSignatureFile.getFileNames();
        for (int i = 0; i < vector2.size(); ++i) {
            object2 = (String)vector2.elementAt(i);
            vector = this.m_Manifest.getDigestList((String)object2);
            for (int j = 0; j < vector.size(); ++j) {
                string2 = (String)vector.elementAt(j);
                object = jarSignatureFile.getDigest((String)object2, string2);
                arrby = this.m_Manifest.getEntry((String)object2);
                Digest digest = this.getDigestClass(string2);
                byte[] arrby2 = new byte[digest.getDigestSize()];
                try {
                    digest.update(arrby, 0, arrby.length);
                    digest.doFinal(arrby2, 0);
                    String string5 = new String(Base64.encode(arrby2, false));
                    if (((String)object).equals(string5)) continue;
                    LogHolder.log(2, LogType.MISC, "Digest verify failed for " + (String)object2 + ". Digest:\n" + (String)object + "\n\nHash:\n" + string5);
                    LogHolder.log(7, LogType.MISC, string2);
                    LogHolder.log(7, LogType.MISC, (String)object);
                    return false;
                }
                catch (Exception exception) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyManifestDigests() {
        Vector vector = this.m_Manifest.getFileNames();
        for (int i = 0; i < vector.size(); ++i) {
            String string = (String)vector.elementAt(i);
            JarFileEntry jarFileEntry = this.m_jarFile.getFileByName(string);
            if (jarFileEntry == null) {
                return false;
            }
            Vector vector2 = this.m_Manifest.getDigestList(string);
            for (int j = 0; j < vector2.size(); ++j) {
                String string2 = (String)vector2.elementAt(j);
                String string3 = this.m_Manifest.getDigest(jarFileEntry, string2);
                Digest digest = this.getDigestClass(string2);
                byte[] arrby = new byte[digest.getDigestSize()];
                try {
                    byte[] arrby2 = jarFileEntry.getContent();
                    if (arrby2 == null) {
                        return false;
                    }
                    digest.update(arrby2, 0, arrby2.length);
                    digest.doFinal(arrby, 0);
                    String string4 = new String(Base64.encode(arrby, false));
                    if (string3.equals(string4)) continue;
                    LogHolder.log(2, LogType.MISC, "Digest verify failed for " + string + ". Digest:\n" + string3 + "\n\nHash:\n" + string4);
                    return false;
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, exception);
                    return false;
                }
            }
        }
        return true;
    }

    private Digest getDigestClass(String string) {
        int n;
        while ((n = string.indexOf("-")) >= 0) {
            string = string.substring(0, n) + string.substring(n + 1);
        }
        if (this.digestCache.contains(string)) {
            Digest digest = (Digest)this.digestCache.get(string);
            digest.reset();
            return digest;
        }
        try {
            Class<?> class_ = Class.forName("org.bouncycastle.crypto.digests." + string);
            Digest digest = (Digest)class_.newInstance();
            this.digestCache.put(string, digest);
            return digest;
        }
        catch (ClassNotFoundException classNotFoundException) {
            LogHolder.log(0, LogType.CRYPTO, classNotFoundException);
        }
        catch (IllegalAccessException illegalAccessException) {
            LogHolder.log(0, LogType.CRYPTO, illegalAccessException);
        }
        catch (InstantiationException instantiationException) {
            LogHolder.log(0, LogType.CRYPTO, instantiationException);
        }
        return null;
    }
}

