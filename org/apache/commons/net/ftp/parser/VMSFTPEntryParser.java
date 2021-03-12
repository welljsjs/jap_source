/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.StringTokenizer;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class VMSFTPEntryParser
extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "d-MMM-yyyy HH:mm:ss";
    private static final String REGEX = "(.*;[0-9]+)\\s*(\\d+)/\\d+\\s*(\\S+)\\s+(\\S+)\\s+\\[(([0-9$A-Za-z_]+)|([0-9$A-Za-z_]+),([0-9$a-zA-Z_]+))\\]?\\s*\\([a-zA-Z]*,[a-zA-Z]*,[a-zA-Z]*,[a-zA-Z]*\\)";

    public VMSFTPEntryParser() {
        this((FTPClientConfig)null);
    }

    public VMSFTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        this.configure(fTPClientConfig);
    }

    public FTPFile[] parseFileList(InputStream inputStream) throws IOException {
        FTPListParseEngine fTPListParseEngine = new FTPListParseEngine(this);
        fTPListParseEngine.readServerList(inputStream);
        return fTPListParseEngine.getFiles();
    }

    public FTPFile parseFTPEntry(String string) {
        long l = 512L;
        if (this.matches(string)) {
            String string2;
            String string3;
            FTPFile fTPFile = new FTPFile();
            fTPFile.setRawListing(string);
            String string4 = this.group(1);
            String string5 = this.group(2);
            String string6 = this.group(3) + " " + this.group(4);
            String string7 = this.group(5);
            try {
                fTPFile.setTimestamp(super.parseTimestamp(string6));
            }
            catch (ParseException parseException) {
                return null;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(string7, ",");
            switch (stringTokenizer.countTokens()) {
                case 1: {
                    string3 = null;
                    string2 = stringTokenizer.nextToken();
                    break;
                }
                case 2: {
                    string3 = stringTokenizer.nextToken();
                    string2 = stringTokenizer.nextToken();
                    break;
                }
                default: {
                    string3 = null;
                    string2 = null;
                }
            }
            if (string4.lastIndexOf(".DIR") != -1) {
                fTPFile.setType(1);
            } else {
                fTPFile.setType(0);
            }
            if (this.isVersioning()) {
                fTPFile.setName(string4);
            } else {
                string4 = string4.substring(0, string4.lastIndexOf(";"));
                fTPFile.setName(string4);
            }
            long l2 = Long.parseLong(string5) * l;
            fTPFile.setSize(l2);
            fTPFile.setGroup(string3);
            fTPFile.setUser(string2);
            return fTPFile;
        }
        return null;
    }

    public String readNextEntry(BufferedReader bufferedReader) throws IOException {
        String string = bufferedReader.readLine();
        StringBuffer stringBuffer = new StringBuffer();
        while (string != null) {
            if (string.startsWith("Directory") || string.startsWith("Total")) {
                string = bufferedReader.readLine();
                continue;
            }
            stringBuffer.append(string);
            if (string.trim().endsWith(")")) break;
            string = bufferedReader.readLine();
        }
        return stringBuffer.length() == 0 ? null : stringBuffer.toString();
    }

    protected boolean isVersioning() {
        return false;
    }

    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig("VMS", DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

