/*
 * Decompiled with CFR 0.150.
 */
package jarify;

import jarify.JarFileEntry;
import jarify.JarManifest;
import jarify.JarSignatureFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

final class JarFile {
    private String m_FileName;
    private ZipFile m_ZipFile;
    private JarManifest m_Manifest;

    public JarFile(File file) throws ZipException, IOException, SecurityException {
        this.m_ZipFile = new ZipFile(file);
        this.m_FileName = this.m_ZipFile.getName();
        this.init();
    }

    private void init() throws IOException {
        ZipEntry zipEntry = this.m_ZipFile.getEntry("META-INF/MANIFEST.MF");
        if (zipEntry != null) {
            this.m_Manifest = new JarManifest(zipEntry.getSize(), this.m_ZipFile.getInputStream(zipEntry));
        }
    }

    public JarManifest getManifest() {
        return this.m_Manifest;
    }

    public boolean fileExists(String string) {
        return this.m_ZipFile.getEntry(string) != null;
    }

    public JarSignatureFile getSignatureFile(String string) {
        ZipEntry zipEntry = this.m_ZipFile.getEntry("META-INF/" + string + ".SF");
        try {
            if (zipEntry != null) {
                return new JarSignatureFile(zipEntry.getName(), zipEntry.getSize(), this.m_ZipFile.getInputStream(zipEntry));
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    public JarFileEntry getSignatureBlockFile(String string) {
        Enumeration<? extends ZipEntry> enumeration = this.m_ZipFile.entries();
        string = string.toUpperCase();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            try {
                if (!zipEntry.getName().startsWith("META-INF/" + string) || !zipEntry.getName().toUpperCase().endsWith(string + ".DSA") && !zipEntry.getName().toUpperCase().endsWith(string + ".RSA")) continue;
                return new JarFileEntry(zipEntry.getName(), zipEntry.getSize(), this.m_ZipFile.getInputStream(zipEntry));
            }
            catch (IOException iOException) {
            }
        }
        return null;
    }

    public Vector getSignatureBlockFiles(String string) {
        Vector<JarFileEntry> vector = new Vector<JarFileEntry>();
        Enumeration<? extends ZipEntry> enumeration = this.m_ZipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            try {
                if (!zipEntry.getName().startsWith("META-INF/" + string) || zipEntry.getName().toLowerCase().endsWith(".sf")) continue;
                vector.addElement(new JarFileEntry(zipEntry.getName(), zipEntry.getSize(), this.m_ZipFile.getInputStream(zipEntry)));
            }
            catch (IOException iOException) {}
        }
        return vector;
    }

    public JarFileEntry getFileByName(String string) {
        ZipEntry zipEntry = null;
        try {
            zipEntry = this.m_ZipFile.getEntry(string);
        }
        catch (Exception exception) {
            // empty catch block
        }
        URL uRL = null;
        if (zipEntry == null) {
            try {
                uRL = new URL(string);
            }
            catch (MalformedURLException malformedURLException) {
                return null;
            }
            catch (Exception exception) {
                return null;
            }
            try {
                return new JarFileEntry(uRL.getFile(), uRL.openConnection().getContentLength(), uRL.openStream());
            }
            catch (Exception exception) {
                return null;
            }
        }
        try {
            if (zipEntry != null) {
                return new JarFileEntry(zipEntry.getName(), zipEntry.getSize(), this.m_ZipFile.getInputStream(zipEntry));
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    public String getName() {
        return this.m_FileName;
    }

    public Vector getAliasList() {
        Vector<String> vector = new Vector<String>();
        Enumeration<? extends ZipEntry> enumeration = this.m_ZipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            String string = zipEntry.getName().replace('\\', '/');
            int n = zipEntry.getName().lastIndexOf("/");
            if (n == -1 || !string.substring(0, n).equals("META-INF") || !string.toLowerCase().endsWith(".sf")) continue;
            vector.addElement(string.substring(n + 1, string.length() - 3));
        }
        return vector;
    }

    public boolean close() {
        try {
            this.m_ZipFile.close();
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }
}

