/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileList;
import org.apache.commons.net.ftp.FTPFileListParser;

public abstract class FTPFileEntryParserImpl
implements FTPFileEntryParser,
FTPFileListParser {
    public FTPFile[] parseFileList(InputStream inputStream, String string) throws IOException {
        FTPFileList fTPFileList = FTPFileList.create(inputStream, this, string);
        return fTPFileList.getFiles();
    }

    public FTPFile[] parseFileList(InputStream inputStream) throws IOException {
        return this.parseFileList(inputStream, null);
    }

    public String readNextEntry(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    public Vector preParse(Vector vector) {
        String string;
        while (!vector.isEmpty() && null == this.parseFTPEntry(string = (String)vector.firstElement())) {
            vector.removeElementAt(0);
        }
        return vector;
    }

    public abstract /* synthetic */ FTPFile parseFTPEntry(String var1);
}

