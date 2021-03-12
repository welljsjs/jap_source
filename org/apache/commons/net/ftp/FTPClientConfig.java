/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.text.DateFormatSymbols;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

public class FTPClientConfig {
    public static final String SYST_UNIX = "UNIX";
    public static final String SYST_VMS = "VMS";
    public static final String SYST_NT = "WINDOWS";
    public static final String SYST_OS2 = "OS/2";
    public static final String SYST_OS400 = "OS/400";
    public static final String SYST_MVS = "MVS";
    private final String serverSystemKey;
    private String defaultDateFormatStr = null;
    private String recentDateFormatStr = null;
    private String serverLanguageCode = null;
    private String shortMonthNames = null;
    private String serverTimeZoneId = null;
    private static Hashtable LANGUAGE_CODE_MAP = new Hashtable();

    public FTPClientConfig(String string) {
        this.serverSystemKey = string;
    }

    public FTPClientConfig() {
        this(SYST_UNIX);
    }

    public FTPClientConfig(String string, String string2, String string3, String string4, String string5, String string6) {
        this(string);
        this.defaultDateFormatStr = string2;
        this.recentDateFormatStr = string3;
        this.serverLanguageCode = string4;
        this.shortMonthNames = string5;
        this.serverTimeZoneId = string6;
    }

    public String getServerSystemKey() {
        return this.serverSystemKey;
    }

    public String getDefaultDateFormatStr() {
        return this.defaultDateFormatStr;
    }

    public String getRecentDateFormatStr() {
        return this.recentDateFormatStr;
    }

    public String getServerTimeZoneId() {
        return this.serverTimeZoneId;
    }

    public String getShortMonthNames() {
        return this.shortMonthNames;
    }

    public String getServerLanguageCode() {
        return this.serverLanguageCode;
    }

    public void setDefaultDateFormatStr(String string) {
        this.defaultDateFormatStr = string;
    }

    public void setRecentDateFormatStr(String string) {
        this.recentDateFormatStr = string;
    }

    public void setServerTimeZoneId(String string) {
        this.serverTimeZoneId = string;
    }

    public void setShortMonthNames(String string) {
        this.shortMonthNames = string;
    }

    public void setServerLanguageCode(String string) {
        this.serverLanguageCode = string;
    }

    public static DateFormatSymbols lookupDateFormatSymbols(String string) {
        Object v = LANGUAGE_CODE_MAP.get(string);
        if (v != null) {
            if (v instanceof Locale) {
                return new DateFormatSymbols((Locale)v);
            }
            if (v instanceof String) {
                return FTPClientConfig.getDateFormatSymbols((String)v);
            }
        }
        return new DateFormatSymbols(Locale.US);
    }

    public static DateFormatSymbols getDateFormatSymbols(String string) {
        String[] arrstring = FTPClientConfig.splitShortMonthString(string);
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.US);
        dateFormatSymbols.setShortMonths(arrstring);
        return dateFormatSymbols;
    }

    private static String[] splitShortMonthString(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "|");
        int n = stringTokenizer.countTokens();
        if (12 != n) {
            throw new IllegalArgumentException("expecting a pipe-delimited string containing 12 tokens");
        }
        String[] arrstring = new String[13];
        int n2 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            arrstring[n2++] = stringTokenizer.nextToken();
        }
        arrstring[n2] = "";
        return arrstring;
    }

    static {
        LANGUAGE_CODE_MAP.put("en", Locale.ENGLISH);
        LANGUAGE_CODE_MAP.put("de", Locale.GERMAN);
        LANGUAGE_CODE_MAP.put("it", Locale.ITALIAN);
        LANGUAGE_CODE_MAP.put("es", new Locale("es", "", ""));
        LANGUAGE_CODE_MAP.put("pt", new Locale("pt", "", ""));
        LANGUAGE_CODE_MAP.put("da", new Locale("da", "", ""));
        LANGUAGE_CODE_MAP.put("sv", new Locale("sv", "", ""));
        LANGUAGE_CODE_MAP.put("no", new Locale("no", "", ""));
        LANGUAGE_CODE_MAP.put("nl", new Locale("nl", "", ""));
        LANGUAGE_CODE_MAP.put("ro", new Locale("ro", "", ""));
        LANGUAGE_CODE_MAP.put("sq", new Locale("sq", "", ""));
        LANGUAGE_CODE_MAP.put("sh", new Locale("sh", "", ""));
        LANGUAGE_CODE_MAP.put("sk", new Locale("sk", "", ""));
        LANGUAGE_CODE_MAP.put("sl", new Locale("sl", "", ""));
        LANGUAGE_CODE_MAP.put("fr", "jan|f\u00e9v|mar|avr|mai|jun|jui|ao\u00fb|sep|oct|nov|d\u00e9c");
    }
}

