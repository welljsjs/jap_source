/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.AbstractISOCodeMapper;
import anon.util.JAPMessages;
import anon.util.Util;
import java.util.Locale;
import java.util.Vector;

public class LanguageMapper
extends AbstractISOCodeMapper {
    private static final String[] ms_languageCodes = new String[]{"AA", "AB", "AF", "AM", "AR", "AS", "AY", "AZ", "BA", "BE", "BG", "BH", "BI", "BN", "BO", "BR", "CA", "CO", "CS", "CY", "DA", "DE", "DZ", "EL", "EN", "EO", "ES", "ET", "EU", "FA", "FI", "FJ", "FO", "FR", "FY", "GA", "GD", "GL", "GN", "GU", "HA", "HI", "HR", "HU", "HY", "IA", "IE", "IK", "IN", "IS", "IT", "IW", "JA", "JI", "JW", "KA", "KK", "KL", "KM", "KN", "KO", "KS", "KU", "KY", "LA", "LN", "LO", "LT", "LV", "MG", "MI", "MK", "ML", "MN", "MO", "MR", "MS", "MT", "MY", "NA", "NE", "NL", "NO", "OC", "OM", "OR", "PA", "PL", "PS", "PT", "QU", "RM", "RN", "RO", "RU", "RW", "SA", "SD", "SG", "SH", "SI", "SK", "SL", "SM", "SN", "SO", "SQ", "SR", "SS", "ST", "SU", "SV", "SW", "TA", "TE", "TG", "TH", "TI", "TK", "TL", "TN", "TO", "TR", "TS", "TT", "TW", "UK", "UR", "UZ", "VI", "VO", "WO", "XH", "YO", "ZH", "ZU"};
    private static final String MSG_CHOOSE_LANGUAGE = (class$anon$util$LanguageMapper == null ? (class$anon$util$LanguageMapper = LanguageMapper.class$("anon.util.LanguageMapper")) : class$anon$util$LanguageMapper).getName() + "_ChooseLanguage";
    private Locale m_locale;
    static /* synthetic */ Class class$anon$util$LanguageMapper;

    public LanguageMapper() {
        this.createLocale();
    }

    public LanguageMapper(int n) {
        super(n);
        this.createLocale();
    }

    public LanguageMapper(String string, int n) throws IllegalArgumentException {
        super(string, n);
        this.createLocale();
    }

    public LanguageMapper(String string) throws IllegalArgumentException {
        super(string);
        this.createLocale();
    }

    public LanguageMapper(String string, Locale locale) throws IllegalArgumentException {
        super(string, locale);
        this.createLocale();
    }

    public LanguageMapper(String string, int n, Locale locale) throws IllegalArgumentException {
        super(string, n, locale);
        this.createLocale();
    }

    public Locale getLocale() {
        return this.m_locale;
    }

    public Locale getLocale(String string) {
        if (this.getISOCode().length() == 0) {
            return null;
        }
        return new Locale(this.getISOCode(), string);
    }

    public static Vector getLocalisedLanguages() {
        return LanguageMapper.getLocalisedLanguages(0, null);
    }

    public static Vector getLocalisedLanguages(Locale locale) {
        return LanguageMapper.getLocalisedLanguages(0, locale);
    }

    public static Vector getLocalisedLanguages(int n) {
        return LanguageMapper.getLocalisedLanguages(n, null);
    }

    public static Vector getLocalisedLanguages(int n, Locale locale) {
        Vector<LanguageMapper> vector = new Vector<LanguageMapper>();
        for (int i = 0; i < ms_languageCodes.length; ++i) {
            vector.addElement(new LanguageMapper(ms_languageCodes[i], n, locale));
        }
        return Util.sortStrings(vector);
    }

    protected final String getChooseMessage() {
        return JAPMessages.getString(MSG_CHOOSE_LANGUAGE);
    }

    protected String getJRETransaltionOfISOCode(String string, Locale locale) {
        return new Locale(string, locale.getCountry()).getDisplayLanguage(locale);
    }

    private void createLocale() {
        if (this.getISOCode().length() > 0) {
            this.m_locale = new Locale(this.getISOCode(), "");
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

