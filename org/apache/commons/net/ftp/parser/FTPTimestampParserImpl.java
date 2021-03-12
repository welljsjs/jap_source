/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.parser.FTPTimestampParser;

public class FTPTimestampParserImpl
implements FTPTimestampParser,
Configurable {
    private SimpleDateFormat defaultDateFormat;
    private SimpleDateFormat recentDateFormat;

    public FTPTimestampParserImpl() {
        this.setDefaultDateFormat("MMM d yyyy");
        this.setRecentDateFormat("MMM d HH:mm");
    }

    public Calendar parseTimestamp(String string) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(this.getServerTimeZone());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeZone(this.getServerTimeZone());
        ParsePosition parsePosition = new ParsePosition(0);
        Date date = null;
        if (this.recentDateFormat != null) {
            date = this.recentDateFormat.parse(string, parsePosition);
        }
        if (date != null && parsePosition.getIndex() == string.length()) {
            calendar2.setTime(date);
            calendar2.set(1, calendar.get(1));
            if (calendar2.after(calendar)) {
                calendar2.add(1, -1);
            }
        } else {
            parsePosition = new ParsePosition(0);
            date = this.defaultDateFormat.parse(string, parsePosition);
            if (date != null && parsePosition.getIndex() == string.length()) {
                calendar2.setTime(date);
            } else {
                throw new ParseException("Timestamp could not be parsed with older or recent DateFormat", parsePosition.getIndex());
            }
        }
        return calendar2;
    }

    public SimpleDateFormat getDefaultDateFormat() {
        return this.defaultDateFormat;
    }

    public String getDefaultDateFormatString() {
        return this.defaultDateFormat.toPattern();
    }

    private void setDefaultDateFormat(String string) {
        if (string != null) {
            this.defaultDateFormat = new SimpleDateFormat(string);
            this.defaultDateFormat.setLenient(false);
        }
    }

    public SimpleDateFormat getRecentDateFormat() {
        return this.recentDateFormat;
    }

    public String getRecentDateFormatString() {
        return this.recentDateFormat.toPattern();
    }

    private void setRecentDateFormat(String string) {
        if (string != null) {
            this.recentDateFormat = new SimpleDateFormat(string);
            this.recentDateFormat.setLenient(false);
        }
    }

    public String[] getShortMonths() {
        return this.defaultDateFormat.getDateFormatSymbols().getShortMonths();
    }

    public TimeZone getServerTimeZone() {
        return this.defaultDateFormat.getTimeZone();
    }

    private void setServerTimeZone(String string) {
        TimeZone timeZone = TimeZone.getDefault();
        if (string != null) {
            timeZone = TimeZone.getTimeZone(string);
        }
        this.defaultDateFormat.setTimeZone(timeZone);
        if (this.recentDateFormat != null) {
            this.recentDateFormat.setTimeZone(timeZone);
        }
    }

    public void configure(FTPClientConfig fTPClientConfig) {
        DateFormatSymbols dateFormatSymbols = null;
        String string = fTPClientConfig.getServerLanguageCode();
        String string2 = fTPClientConfig.getShortMonthNames();
        dateFormatSymbols = string2 != null ? FTPClientConfig.getDateFormatSymbols(string2) : (string != null ? FTPClientConfig.lookupDateFormatSymbols(string) : FTPClientConfig.lookupDateFormatSymbols("en"));
        String string3 = fTPClientConfig.getRecentDateFormatStr();
        if (string3 == null) {
            this.recentDateFormat = null;
        } else {
            this.recentDateFormat = new SimpleDateFormat(string3, dateFormatSymbols);
            this.recentDateFormat.setLenient(false);
        }
        String string4 = fTPClientConfig.getDefaultDateFormatStr();
        if (string4 == null) {
            throw new IllegalArgumentException("defaultFormatString cannot be null");
        }
        this.defaultDateFormat = new SimpleDateFormat(string4, dateFormatSymbols);
        this.defaultDateFormat.setLenient(false);
        this.setServerTimeZone(fTPClientConfig.getServerTimeZoneId());
    }
}

