/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.CompositeFileEntryParser;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.MVSFTPEntryParser;
import org.apache.commons.net.ftp.parser.NTFTPEntryParser;
import org.apache.commons.net.ftp.parser.OS2FTPEntryParser;
import org.apache.commons.net.ftp.parser.OS400FTPEntryParser;
import org.apache.commons.net.ftp.parser.ParserInitializationException;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.commons.net.ftp.parser.VMSVersioningFTPEntryParser;

public class DefaultFTPFileEntryParserFactory
implements FTPFileEntryParserFactory {
    private FTPClientConfig config = null;

    public FTPFileEntryParser createFileEntryParser(String string) {
        Class<?> class_ = null;
        FTPFileEntryParser fTPFileEntryParser = null;
        try {
            class_ = Class.forName(string);
            fTPFileEntryParser = (FTPFileEntryParser)class_.newInstance();
        }
        catch (ClassNotFoundException classNotFoundException) {
            String string2 = null;
            if (null != string) {
                string2 = string.toUpperCase();
            }
            if (string2.indexOf("UNIX") >= 0) {
                fTPFileEntryParser = this.createUnixFTPEntryParser();
            }
            if (string2.indexOf("VMS") >= 0) {
                fTPFileEntryParser = this.createVMSVersioningFTPEntryParser();
            }
            if (string2.indexOf("WINDOWS") >= 0) {
                fTPFileEntryParser = this.createNTFTPEntryParser();
            }
            if (string2.indexOf("OS/2") >= 0) {
                fTPFileEntryParser = this.createOS2FTPEntryParser();
            }
            if (string2.indexOf("OS/400") >= 0) {
                fTPFileEntryParser = this.createOS400FTPEntryParser();
            }
            if (string2.indexOf("MVS") >= 0) {
                fTPFileEntryParser = this.createMVSEntryParser();
            }
            throw new ParserInitializationException("Unknown parser type: " + string);
        }
        catch (ClassCastException classCastException) {
            throw new ParserInitializationException(class_.getName() + " does not implement the interface " + "org.apache.commons.net.ftp.FTPFileEntryParser.", classCastException);
        }
        catch (Throwable throwable) {
            throw new ParserInitializationException("Error initializing parser", throwable);
        }
        if (fTPFileEntryParser instanceof Configurable) {
            ((Configurable)((Object)fTPFileEntryParser)).configure(this.config);
        }
        return fTPFileEntryParser;
    }

    public FTPFileEntryParser createFileEntryParser(FTPClientConfig fTPClientConfig) throws ParserInitializationException {
        this.config = fTPClientConfig;
        String string = fTPClientConfig.getServerSystemKey();
        return this.createFileEntryParser(string);
    }

    public FTPFileEntryParser createUnixFTPEntryParser() {
        return new UnixFTPEntryParser();
    }

    public FTPFileEntryParser createVMSVersioningFTPEntryParser() {
        return new VMSVersioningFTPEntryParser();
    }

    public FTPFileEntryParser createNTFTPEntryParser() {
        if (this.config != null && "WINDOWS".equals(this.config.getServerSystemKey())) {
            return new NTFTPEntryParser();
        }
        return new CompositeFileEntryParser(new FTPFileEntryParser[]{new NTFTPEntryParser(), new UnixFTPEntryParser()});
    }

    public FTPFileEntryParser createOS2FTPEntryParser() {
        return new OS2FTPEntryParser();
    }

    public FTPFileEntryParser createOS400FTPEntryParser() {
        if (this.config != null && "OS/400".equals(this.config.getServerSystemKey())) {
            return new OS400FTPEntryParser();
        }
        return new CompositeFileEntryParser(new FTPFileEntryParser[]{new OS400FTPEntryParser(), new UnixFTPEntryParser()});
    }

    public FTPFileEntryParser createMVSEntryParser() {
        return new MVSFTPEntryParser();
    }
}

