/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public class CompositeFileEntryParser
extends FTPFileEntryParserImpl {
    private final FTPFileEntryParser[] ftpFileEntryParsers;
    private FTPFileEntryParser cachedFtpFileEntryParser = null;

    public CompositeFileEntryParser(FTPFileEntryParser[] arrfTPFileEntryParser) {
        this.ftpFileEntryParsers = arrfTPFileEntryParser;
    }

    public FTPFile parseFTPEntry(String string) {
        if (this.cachedFtpFileEntryParser != null) {
            FTPFile fTPFile = this.cachedFtpFileEntryParser.parseFTPEntry(string);
            if (fTPFile != null) {
                return fTPFile;
            }
        } else {
            for (int i = 0; i < this.ftpFileEntryParsers.length; ++i) {
                FTPFileEntryParser fTPFileEntryParser = this.ftpFileEntryParsers[i];
                FTPFile fTPFile = fTPFileEntryParser.parseFTPEntry(string);
                if (fTPFile == null) continue;
                this.cachedFtpFileEntryParser = fTPFileEntryParser;
                return fTPFile;
            }
        }
        return null;
    }
}

