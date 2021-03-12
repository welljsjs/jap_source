/*
 * Decompiled with CFR 0.150.
 */
package jarify;

import jarify.JarFileEntry;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class JarManifest
extends JarFileEntry {
    private Hashtable entries = new Hashtable();
    private Vector fileNameList = new Vector();
    protected String contentStrRaw;
    protected String newLine;

    public JarManifest(long l, InputStream inputStream) {
        super("META-INF/MANIFEST.MF", l, inputStream);
        this.init();
    }

    protected void init() {
        byte[] arrby = this.getContent();
        if (arrby == null) {
            return;
        }
        this.contentStrRaw = new String(arrby);
        this.newLine = this.contentStrRaw.indexOf("\r\n") != -1 ? "\r\n" : (this.contentStrRaw.indexOf("\r") != -1 ? "\r" : "\n");
        this.parse();
    }

    private void parse() {
        int n = 0;
        int n2 = 0;
        while ((n = this.contentStrRaw.indexOf(this.newLine + "Name: ", n)) != -1) {
            int n3 = this.contentStrRaw.indexOf(this.newLine + this.newLine, n += this.newLine.length());
            String string = this.contentStrRaw.substring(n, n3 + this.newLine.length() * 2);
            EntryData entryData = new EntryData(string.getBytes());
            int n4 = 0;
            while ((n4 = string.indexOf(this.newLine + " ")) != -1) {
                string = string.substring(0, n4) + string.substring(n4 + this.newLine.length() + 1, string.length());
            }
            String string2 = string.substring("Name: ".length(), string.indexOf(this.newLine));
            this.fileNameList.addElement(string2);
            this.entries.put(string2, entryData);
            int n5 = n = string.indexOf(this.newLine);
            while ((n5 = string.indexOf("-Digest: ", n5 + 1)) != -1) {
                int n6 = 0;
                while ((n6 = string.indexOf(this.newLine, n6 + 1)) < n5) {
                    n2 = n6;
                }
                String string3 = string.substring(n2 + this.newLine.length(), n5 + 7);
                String string4 = string.substring(n5 + 9, string.indexOf(this.newLine, n5));
                entryData.digests.put(string3, string4);
            }
            n = n3 - 3;
        }
    }

    public Vector getFileNames() {
        return this.fileNameList;
    }

    public String getDigest(JarFileEntry jarFileEntry, String string) {
        return this.getDigest(jarFileEntry.getName(), string);
    }

    public String getDigest(String string, String string2) {
        EntryData entryData = (EntryData)this.entries.get(string);
        if (entryData == null) {
            return null;
        }
        return (String)entryData.digests.get(string2);
    }

    public byte[] getEntry(String string) {
        EntryData entryData = (EntryData)this.entries.get(string);
        if (entryData == null) {
            return null;
        }
        return entryData.raw;
    }

    public Vector getDigestList(String string) {
        EntryData entryData = (EntryData)this.entries.get(string);
        if (entryData == null) {
            return null;
        }
        Enumeration enumeration = entryData.digests.keys();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        return vector;
    }

    private class EntryData {
        byte[] raw;
        Hashtable digests;

        public EntryData(byte[] arrby) {
            this.raw = arrby;
            this.digests = new Hashtable();
        }
    }
}

