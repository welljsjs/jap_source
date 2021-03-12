/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;

public class FTPListParseEngine {
    private Vector entries = new Vector();
    private int _internalIterator;
    FTPFileEntryParser parser = null;

    public FTPListParseEngine(FTPFileEntryParser fTPFileEntryParser) {
        this.parser = fTPFileEntryParser;
    }

    public void readServerList(InputStream inputStream, String string) throws IOException {
        this.entries = new Vector();
        this.readStream(inputStream, string);
        this.parser.preParse(this.entries);
        this.resetIterator();
    }

    public void readServerList(InputStream inputStream) throws IOException {
        this.readServerList(inputStream, null);
    }

    private void readStream(InputStream inputStream, String string) throws IOException {
        BufferedReader bufferedReader = string == null ? new BufferedReader(new InputStreamReader(inputStream)) : new BufferedReader(new InputStreamReader(inputStream, string));
        String string2 = this.parser.readNextEntry(bufferedReader);
        while (string2 != null) {
            this.entries.addElement(string2);
            string2 = this.parser.readNextEntry(bufferedReader);
        }
        bufferedReader.close();
    }

    public FTPFile[] getNext(int n) {
        Object object;
        Vector<FTPFile> vector = new Vector<FTPFile>();
        for (int i = n; i > 0 && this.hasNext(); --i) {
            object = (String)this.entries.elementAt(++this._internalIterator);
            FTPFile fTPFile = this.parser.parseFTPEntry((String)object);
            vector.addElement(fTPFile);
        }
        object = new FTPFile[vector.size()];
        vector.copyInto((Object[])object);
        return object;
    }

    public FTPFile[] getPrevious(int n) {
        Object object;
        Vector<FTPFile> vector = new Vector<FTPFile>();
        for (int i = n; i > 0 && this.hasPrevious(); --i) {
            object = (String)this.entries.elementAt(--this._internalIterator);
            FTPFile fTPFile = this.parser.parseFTPEntry((String)object);
            vector.insertElementAt(fTPFile, 0);
        }
        object = new FTPFile[vector.size()];
        vector.copyInto((Object[])object);
        return object;
    }

    public FTPFile[] getFiles() throws IOException {
        FTPFile[] arrfTPFile;
        Vector<FTPFile> vector = new Vector<FTPFile>();
        Enumeration enumeration = this.entries.elements();
        while (enumeration.hasMoreElements()) {
            arrfTPFile = (FTPFile[])enumeration.nextElement();
            FTPFile fTPFile = this.parser.parseFTPEntry((String)arrfTPFile);
            vector.addElement(fTPFile);
        }
        arrfTPFile = new FTPFile[vector.size()];
        enumeration = vector.elements();
        int n = 0;
        while (enumeration.hasMoreElements()) {
            arrfTPFile[n++] = (FTPFile)enumeration.nextElement();
        }
        return arrfTPFile;
    }

    public boolean hasNext() {
        return this._internalIterator < this.entries.size() - 1;
    }

    public boolean hasPrevious() {
        return this._internalIterator > 0;
    }

    public void resetIterator() {
        this._internalIterator = 0;
    }
}

