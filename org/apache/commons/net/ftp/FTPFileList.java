/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileIterator;

public class FTPFileList {
    private Vector lines = null;
    private FTPFileEntryParser parser;
    private static final int EMPTY_DIR = -2;

    private FTPFileList(FTPFileEntryParser fTPFileEntryParser, String string) {
        this.parser = fTPFileEntryParser;
        this.lines = new Vector();
    }

    public static FTPFileList create(InputStream inputStream, FTPFileEntryParser fTPFileEntryParser, String string) throws IOException {
        FTPFileList fTPFileList = new FTPFileList(fTPFileEntryParser, string);
        fTPFileList.readStream(inputStream, string);
        fTPFileEntryParser.preParse(fTPFileList.lines);
        return fTPFileList;
    }

    public static FTPFileList create(InputStream inputStream, FTPFileEntryParser fTPFileEntryParser) throws IOException {
        return FTPFileList.create(inputStream, fTPFileEntryParser, null);
    }

    public void readStream(InputStream inputStream, String string) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, string));
        String string2 = this.parser.readNextEntry(bufferedReader);
        while (string2 != null) {
            this.lines.addElement(string2);
            string2 = this.parser.readNextEntry(bufferedReader);
        }
        bufferedReader.close();
    }

    public void readStream(InputStream inputStream) throws IOException {
        this.readStream(inputStream, null);
    }

    FTPFileEntryParser getParser() {
        return this.parser;
    }

    Vector getLines() {
        return this.lines;
    }

    public FTPFileIterator iterator() {
        return new FTPFileIterator(this);
    }

    public FTPFileIterator iterator(FTPFileEntryParser fTPFileEntryParser) {
        return new FTPFileIterator(this, fTPFileEntryParser);
    }

    public FTPFile[] getFiles() {
        return this.iterator().getFiles();
    }
}

