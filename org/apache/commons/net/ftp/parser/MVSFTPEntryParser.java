/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class MVSFTPEntryParser
extends ConfigurableFTPFileEntryParserImpl {
    private static final String REGEX = "(.*)\\s+([^\\s]+)\\s*";
    static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";

    public MVSFTPEntryParser() {
        super(REGEX);
    }

    public FTPFile parseFTPEntry(String string) {
        FTPFile fTPFile = null;
        if (this.matches(string)) {
            fTPFile = new FTPFile();
            String string2 = this.group(2);
            fTPFile.setType(0);
            fTPFile.setName(string2);
            return fTPFile;
        }
        return null;
    }

    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("MVS", DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

