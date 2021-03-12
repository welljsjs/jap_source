/*
 * Decompiled with CFR 0.150.
 */
package jarify;

import jarify.JarManifest;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

final class JarSignatureFile
extends JarManifest {
    private Hashtable manifestdigests = new Hashtable();

    public JarSignatureFile(String string, long l, InputStream inputStream) {
        super(l, inputStream);
        this.name = string;
        this.parseManifestDigests();
    }

    private void parseManifestDigests() {
        int n;
        String string = this.contentStrRaw.substring(0, this.contentStrRaw.indexOf(this.newLine + "Name: "));
        while ((n = string.indexOf(this.newLine + " ")) != -1) {
            string = string.substring(0, n) + string.substring(n + this.newLine.length() + 1, string.length());
        }
        int n2 = 0;
        int n3 = 0;
        Vector<String> vector = new Vector<String>();
        while ((n3 = string.indexOf(this.newLine, n2)) != -1) {
            vector.addElement(string.substring(n2, n3));
            n2 = n3 + this.newLine.length();
        }
        for (int i = 0; i < vector.size(); ++i) {
            String string2 = (String)vector.elementAt(i);
            n2 = string2.indexOf("-Manifest: ");
            if (n2 == -1) continue;
            String string3 = string2.substring(0, n2);
            String string4 = string2.substring(n2 + 11);
            this.manifestdigests.put(string3, string4);
        }
    }

    public String getManifestDigest(String string) {
        return (String)this.manifestdigests.get(string);
    }

    public Vector getManifestDigestList() {
        Enumeration enumeration = this.manifestdigests.keys();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        return vector;
    }

    public String getAlias() {
        if (this.name.indexOf(".") == -1) {
            return null;
        }
        return this.name.substring(this.name.lastIndexOf("/") + 1, this.name.lastIndexOf("."));
    }
}

