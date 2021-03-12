/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.AbstractISOCodeMapper;
import anon.util.JAPMessages;
import anon.util.Util;
import java.util.Locale;
import java.util.Vector;

public class CountryMapper
extends AbstractISOCodeMapper {
    private static final String[] DEFAULT_COUNTRIES = new String[]{"AD", "AE", "AF", "AG", "AI", "AL", "AM", "AN", "AO", "AQ", "AR", "AS", "AT", "AU", "AW", "AX", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BM", "BN", "BO", "BR", "BS", "BT", "BV", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN", "CO", "CR", "CS", "CU", "CV", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC", "EE", "EG", "EH", "ER", "ES", "ET", "FI", "FJ", "FK", "GA", "GB", "FM", "FO", "GP", "GQ", "GR", "GS", "GT", "GU", "GW", "GY", "GD", "GE", "GF", "GH", "GI", "GL", "GM", "GN", "HM", "HN", "HR", "HT", "HU", "IQ", "IR", "IS", "IT", "LR", "LS", "LT", "LU", "LV", "HK", "ID", "IE", "IL", "IN", "IO", "KE", "KG", "KH", "KI", "JM", "JO", "JP", "KM", "KN", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LY", "MA", "MC", "MD", "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "PA", "PE", "PF", "PG", "PH", "OM", "PK", "PL", "PM", "PN", "RU", "RW", "SV", "PR", "PS", "PT", "QA", "RE", "PW", "PY", "SY", "SZ", "SA", "SB", "SC", "SD", "SE", "SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "RO", "SR", "ST", "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TR", "TT", "TV", "TW", "TZ", "UG", "UA", "UY", "UZ", "UM", "US", "VI", "VN", "ZM", "YT", "VU", "VA", "VC", "VE", "VG", "WF", "WS", "ZA", "YE", "ZW"};
    private static final String MSG_CHOOSE_COUNTRY = (class$anon$util$CountryMapper == null ? (class$anon$util$CountryMapper = CountryMapper.class$("anon.util.CountryMapper")) : class$anon$util$CountryMapper).getName() + "_ChooseCountry";
    private static final String[] COUNTRIES;
    static /* synthetic */ Class class$anon$util$CountryMapper;
    static /* synthetic */ Class class$java$util$Locale;

    public CountryMapper() {
    }

    public CountryMapper(int n) {
        super(n);
    }

    public CountryMapper(String string, int n) throws IllegalArgumentException {
        super(string, n);
    }

    public CountryMapper(String string) throws IllegalArgumentException {
        super(string);
    }

    public CountryMapper(String string, Locale locale) throws IllegalArgumentException {
        super(string, locale);
    }

    public CountryMapper(String string, int n, Locale locale) throws IllegalArgumentException {
        super(string, n, locale);
    }

    public static Vector getLocalisedCountries() {
        return CountryMapper.getLocalisedCountries(0, null);
    }

    public static Vector getLocalisedCountries(Locale locale) {
        return CountryMapper.getLocalisedCountries(0, locale);
    }

    public static Vector getLocalisedCountries(int n) {
        return CountryMapper.getLocalisedCountries(n, null);
    }

    public static Vector getLocalisedCountries(int n, Locale locale) {
        Vector<CountryMapper> vector = new Vector<CountryMapper>();
        for (int i = 0; i < COUNTRIES.length; ++i) {
            vector.addElement(new CountryMapper(COUNTRIES[i], n, locale));
        }
        return Util.sortStrings(vector);
    }

    protected final String getChooseMessage() {
        return JAPMessages.getString(MSG_CHOOSE_COUNTRY);
    }

    protected String getJRETransaltionOfISOCode(String string, Locale locale) {
        return new Locale(locale.getLanguage(), string).getDisplayCountry(locale);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        String[] arrstring = null;
        try {
            arrstring = (String[])(class$java$util$Locale == null ? (class$java$util$Locale = CountryMapper.class$("java.util.Locale")) : class$java$util$Locale).getMethod("getISOCountries", null).invoke(class$java$util$Locale == null ? (class$java$util$Locale = CountryMapper.class$("java.util.Locale")) : class$java$util$Locale, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        COUNTRIES = arrstring == null || arrstring.length < DEFAULT_COUNTRIES.length ? DEFAULT_COUNTRIES : arrstring;
    }
}

