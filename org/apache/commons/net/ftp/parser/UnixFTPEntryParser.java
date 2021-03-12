/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class UnixFTPEntryParser
extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_MONTHS = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
    static final String DEFAULT_DATE_FORMAT = "MMM d yyyy";
    static final String DEFAULT_RECENT_DATE_FORMAT = "MMM d HH:mm";
    static final String NUMERIC_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final FTPClientConfig NUMERIC_DATE_CONFIG = new FTPClientConfig("UNIX", "yyyy-MM-dd HH:mm", null, null, null, null);
    private static final String REGEX = "([bcdlfmpSs-])(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s+(\\d+)\\s+(\\S+)\\s+(?:(\\S+)\\s+)?(\\d+)\\s+((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S+\\s+\\S+))\\s+(\\d+(?::\\d+)?)\\s+(\\S*)(\\s*.*)";

    public UnixFTPEntryParser() {
        this((FTPClientConfig)null);
    }

    public UnixFTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        this.configure(fTPClientConfig);
    }

    public FTPFile parseFTPEntry(String string) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(string);
        boolean bl = false;
        if (this.matches(string)) {
            int n;
            String string2 = this.group(1);
            String string3 = this.group(15);
            String string4 = this.group(16);
            String string5 = this.group(17);
            String string6 = this.group(18);
            String string7 = this.group(19) + " " + this.group(20);
            String string8 = this.group(21);
            String string9 = this.group(22);
            try {
                fTPFile.setTimestamp(super.parseTimestamp(string7));
            }
            catch (ParseException parseException) {
                return null;
            }
            switch (string2.charAt(0)) {
                case 'd': {
                    n = 1;
                    break;
                }
                case 'l': {
                    n = 2;
                    break;
                }
                case 'b': 
                case 'c': {
                    bl = true;
                }
                case '-': 
                case 'f': {
                    n = 0;
                    break;
                }
                default: {
                    n = 3;
                }
            }
            fTPFile.setType(n);
            int n2 = 4;
            int n3 = 0;
            while (n3 < 3) {
                fTPFile.setPermission(n3, 0, !this.group(n2).equals("-"));
                fTPFile.setPermission(n3, 1, !this.group(n2 + 1).equals("-"));
                String string10 = this.group(n2 + 2);
                if (!string10.equals("-") && !Character.isUpperCase(string10.charAt(0))) {
                    fTPFile.setPermission(n3, 2, true);
                } else {
                    fTPFile.setPermission(n3, 2, false);
                }
                ++n3;
                n2 += 4;
            }
            if (!bl) {
                try {
                    fTPFile.setHardLinkCount(Integer.parseInt(string3));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            fTPFile.setUser(string4);
            fTPFile.setGroup(string5);
            try {
                fTPFile.setSize(Long.parseLong(string6));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (null == string9) {
                fTPFile.setName(string8);
            } else {
                string8 = string8 + string9;
                if (n == 2) {
                    int n4 = string8.indexOf(" -> ");
                    if (n4 == -1) {
                        fTPFile.setName(string8);
                    } else {
                        fTPFile.setName(string8.substring(0, n4));
                        fTPFile.setLink(string8.substring(n4 + 4));
                    }
                } else {
                    fTPFile.setName(string8);
                }
            }
            return fTPFile;
        }
        return null;
    }

    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("UNIX", DEFAULT_DATE_FORMAT, DEFAULT_RECENT_DATE_FORMAT, null, null, null);
    }
}

