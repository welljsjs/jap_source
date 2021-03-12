/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.util.Calendar;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.RegexFTPFileEntryParserImpl;

public class EnterpriseUnixFTPEntryParser
extends RegexFTPFileEntryParserImpl {
    private static final String MONTHS = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
    private static final String REGEX = "(([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z]))(\\S*)\\s*(\\S+)\\s*(\\S*)\\s*(\\d*)\\s*(\\d*)\\s*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*((?:[012]\\d*)|(?:3[01]))\\s*((\\d\\d\\d\\d)|((?:[01]\\d)|(?:2[0123])):([012345]\\d))\\s(\\S*)(\\s*.*)";

    public EnterpriseUnixFTPEntryParser() {
        super(REGEX);
    }

    public FTPFile parseFTPEntry(String string) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(string);
        if (this.matches(string)) {
            String string2 = this.group(14);
            String string3 = this.group(15);
            String string4 = this.group(16);
            String string5 = this.group(17);
            String string6 = this.group(18);
            String string7 = this.group(20);
            String string8 = this.group(21);
            String string9 = this.group(22);
            String string10 = this.group(23);
            fTPFile.setType(0);
            fTPFile.setUser(string2);
            fTPFile.setGroup(string3);
            try {
                fTPFile.setSize(Long.parseLong(string4));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(14, 0);
            calendar.set(13, 0);
            calendar.set(12, 0);
            calendar.set(11, 0);
            try {
                int n = MONTHS.indexOf(string5);
                int n2 = n / 4;
                if (string7 != null) {
                    calendar.set(1, Integer.parseInt(string7));
                } else {
                    int n3 = calendar.get(1);
                    if (calendar.get(2) < n2) {
                        --n3;
                    }
                    calendar.set(1, n3);
                    calendar.set(11, Integer.parseInt(string8));
                    calendar.set(12, Integer.parseInt(string9));
                }
                calendar.set(2, n2);
                calendar.set(5, Integer.parseInt(string6));
                fTPFile.setTimestamp(calendar);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            fTPFile.setName(string10);
            return fTPFile;
        }
        return null;
    }
}

