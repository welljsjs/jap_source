/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.util.Vector;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileList;

public class FTPFileIterator {
    private Vector rawlines;
    private FTPFileEntryParser parser;
    private static final int UNINIT = -1;
    private static final int DIREMPTY = -2;
    private int itemptr = 0;
    private int firstGoodEntry = -1;
    private static final FTPFile[] EMPTY = new FTPFile[0];

    FTPFileIterator(FTPFileList fTPFileList) {
        this(fTPFileList, fTPFileList.getParser());
    }

    FTPFileIterator(FTPFileList fTPFileList, FTPFileEntryParser fTPFileEntryParser) {
        this.rawlines = fTPFileList.getLines();
        this.parser = fTPFileEntryParser;
    }

    private FTPFile parseFTPEntry(String string) {
        return this.parser.parseFTPEntry(string);
    }

    private int getFirstGoodEntry() {
        FTPFile fTPFile = null;
        for (int i = 0; i < this.rawlines.size(); ++i) {
            String string = (String)this.rawlines.elementAt(i);
            fTPFile = this.parseFTPEntry(string);
            if (null == fTPFile) continue;
            return i;
        }
        return -2;
    }

    private void init() {
        this.itemptr = 0;
        this.firstGoodEntry = -1;
    }

    public FTPFile[] getFiles() {
        if (this.itemptr != -2) {
            this.init();
        }
        return this.getNext(0);
    }

    public FTPFile[] getNext(int n) {
        if (this.firstGoodEntry == -1) {
            this.firstGoodEntry = this.getFirstGoodEntry();
        }
        if (this.firstGoodEntry == -2) {
            return EMPTY;
        }
        int n2 = this.rawlines.size() - this.firstGoodEntry;
        int n3 = n == 0 ? n2 : n;
        n3 = n3 + this.itemptr < this.rawlines.size() ? n3 : this.rawlines.size() - this.itemptr;
        FTPFile[] arrfTPFile = new FTPFile[n3];
        int n4 = 0;
        int n5 = this.firstGoodEntry + this.itemptr;
        while (n4 < n3) {
            arrfTPFile[n4] = this.parseFTPEntry((String)this.rawlines.elementAt(n5));
            ++this.itemptr;
            ++n4;
            ++n5;
        }
        return arrfTPFile;
    }

    public boolean hasNext() {
        int n = this.firstGoodEntry;
        if (n == -2) {
            return false;
        }
        if (n < 0) {
            n = this.getFirstGoodEntry();
        }
        return n + this.itemptr < this.rawlines.size();
    }

    public FTPFile next() {
        FTPFile[] arrfTPFile = this.getNext(1);
        if (arrfTPFile.length > 0) {
            return arrfTPFile[0];
        }
        return null;
    }

    public FTPFile[] getPrevious(int n) {
        int n2 = n;
        if (n2 > this.itemptr) {
            n2 = this.itemptr;
        }
        FTPFile[] arrfTPFile = new FTPFile[n2];
        int n3 = n2;
        int n4 = this.firstGoodEntry + this.itemptr;
        while (n3 > 0) {
            arrfTPFile[--n3] = this.parseFTPEntry((String)this.rawlines.elementAt(--n4));
            --this.itemptr;
        }
        return arrfTPFile;
    }

    public boolean hasPrevious() {
        int n = this.firstGoodEntry;
        if (n == -2) {
            return false;
        }
        if (n < 0) {
            n = this.getFirstGoodEntry();
        }
        return this.itemptr > n;
    }

    public FTPFile previous() {
        FTPFile[] arrfTPFile = this.getPrevious(1);
        if (arrfTPFile.length > 0) {
            return arrfTPFile[0];
        }
        return null;
    }
}

