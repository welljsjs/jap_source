/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class OS2FTPEntryParser
extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy HH:mm";
    private static final String REGEX = "(\\s+|[0-9]+)\\s*(\\s+|[A-Z]+)\\s*(DIR|\\s+)\\s*(\\S+)\\s+(\\S+)\\s+(\\S.*)";

    public OS2FTPEntryParser() {
        this((FTPClientConfig)null);
    }

    public OS2FTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        this.configure(fTPClientConfig);
    }

    public FTPFile parseFTPEntry(String string) {
        FTPFile fTPFile = new FTPFile();
        if (this.matches(string)) {
            String string2 = this.group(1);
            String string3 = this.group(2);
            String string4 = this.group(3);
            String string5 = this.group(4) + " " + this.group(5);
            String string6 = this.group(6);
            try {
                fTPFile.setTimestamp(super.parseTimestamp(string5));
            }
            catch (ParseException parseException) {
                return null;
            }
            if (string4.trim().equals("DIR") || string3.trim().equals("DIR")) {
                fTPFile.setType(1);
            } else {
                fTPFile.setType(0);
            }
            fTPFile.setName(string6.trim());
            fTPFile.setSize(Long.parseLong(string2.trim()));
            return fTPFile;
        }
        return null;
    }

    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("OS/2", DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

