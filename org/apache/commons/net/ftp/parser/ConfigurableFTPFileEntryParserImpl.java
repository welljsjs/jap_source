/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import java.util.Calendar;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.parser.FTPTimestampParser;
import org.apache.commons.net.ftp.parser.FTPTimestampParserImpl;
import org.apache.commons.net.ftp.parser.RegexFTPFileEntryParserImpl;

public abstract class ConfigurableFTPFileEntryParserImpl
extends RegexFTPFileEntryParserImpl
implements Configurable {
    private FTPTimestampParser timestampParser = new FTPTimestampParserImpl();

    public ConfigurableFTPFileEntryParserImpl(String string) {
        super(string);
    }

    public Calendar parseTimestamp(String string) throws ParseException {
        return this.timestampParser.parseTimestamp(string);
    }

    public void configure(FTPClientConfig fTPClientConfig) {
        if (this.timestampParser instanceof Configurable) {
            FTPClientConfig fTPClientConfig2 = this.getDefaultConfiguration();
            if (fTPClientConfig != null) {
                if (null == fTPClientConfig.getDefaultDateFormatStr()) {
                    fTPClientConfig.setDefaultDateFormatStr(fTPClientConfig2.getDefaultDateFormatStr());
                }
                if (null == fTPClientConfig.getRecentDateFormatStr()) {
                    fTPClientConfig.setRecentDateFormatStr(fTPClientConfig2.getRecentDateFormatStr());
                }
                ((Configurable)((Object)this.timestampParser)).configure(fTPClientConfig);
            } else {
                ((Configurable)((Object)this.timestampParser)).configure(fTPClientConfig2);
            }
        }
    }

    protected abstract FTPClientConfig getDefaultConfiguration();
}

