/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class OS400FTPEntryParser
extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "yy/MM/dd HH:mm:ss";
    private static final String REGEX = "(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\*\\S+)\\s+(\\S+/?)\\s*";

    public OS400FTPEntryParser() {
        this((FTPClientConfig)null);
    }

    public OS400FTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        this.configure(fTPClientConfig);
    }

    public FTPFile parseFTPEntry(String string) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(string);
        if (this.matches(string)) {
            int n;
            String string2 = this.group(1);
            String string3 = this.group(2);
            String string4 = this.group(3) + " " + this.group(4);
            String string5 = this.group(5);
            String string6 = this.group(6);
            try {
                fTPFile.setTimestamp(super.parseTimestamp(string4));
            }
            catch (ParseException parseException) {
                return null;
            }
            int n2 = string5.equalsIgnoreCase("*STMF") ? 0 : (string5.equalsIgnoreCase("*DIR") ? 1 : 3);
            fTPFile.setType(n2);
            fTPFile.setUser(string2);
            try {
                fTPFile.setSize(Long.parseLong(string3));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (string6.endsWith("/")) {
                string6 = string6.substring(0, string6.length() - 1);
            }
            if ((n = string6.lastIndexOf(47)) > -1) {
                string6 = string6.substring(n + 1);
            }
            fTPFile.setName(string6);
            return fTPFile;
        }
        return null;
    }

    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("OS/400", DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

