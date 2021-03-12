/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class NTFTPEntryParser
extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy hh:mma";
    private static final String REGEX = "(\\S+)\\s+(\\S+)\\s+(<DIR>)?\\s*([0-9]+)?\\s+(\\S.*)";

    public NTFTPEntryParser() {
        this((FTPClientConfig)null);
    }

    public NTFTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        this.configure(fTPClientConfig);
    }

    public FTPFile parseFTPEntry(String string) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(string);
        if (this.matches(string)) {
            String string2 = this.group(1) + " " + this.group(2);
            String string3 = this.group(3);
            String string4 = this.group(4);
            String string5 = this.group(5);
            try {
                fTPFile.setTimestamp(super.parseTimestamp(string2));
            }
            catch (ParseException parseException) {
                return null;
            }
            if (null == string5 || string5.equals(".") || string5.equals("..")) {
                return null;
            }
            fTPFile.setName(string5);
            if ("<DIR>".equals(string3)) {
                fTPFile.setType(1);
                fTPFile.setSize(0L);
            } else {
                fTPFile.setType(0);
                if (null != string4) {
                    fTPFile.setSize(Long.parseLong(string4));
                }
            }
            return fTPFile;
        }
        return null;
    }

    public FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("WINDOWS", DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

