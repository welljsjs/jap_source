/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.JAPMessages;
import java.util.Locale;

public abstract class AbstractISOCodeMapper {
    private final int MAX_LENGTH;
    private boolean m_bUseDefaultLocale;
    private String m_iso2;
    private Locale m_locale;

    public AbstractISOCodeMapper() {
        this(null, 0);
    }

    public AbstractISOCodeMapper(int n) {
        this(null, n);
    }

    public AbstractISOCodeMapper(String string, int n) throws IllegalArgumentException {
        this(string, n, null);
    }

    public AbstractISOCodeMapper(String string) throws IllegalArgumentException {
        this(string, 0, null);
    }

    public AbstractISOCodeMapper(String string, Locale locale) throws IllegalArgumentException {
        this(string, 0, locale);
    }

    public AbstractISOCodeMapper(String string, int n, Locale locale) throws IllegalArgumentException {
        this.MAX_LENGTH = n;
        if (string == null || string.trim().length() == 0) {
            string = "";
        }
        if (string.length() > 0 && string.length() != 2) {
            throw new IllegalArgumentException("Mapped ISO code must have a length of two characters!");
        }
        this.m_iso2 = string.trim().toUpperCase();
        if (locale == null) {
            this.m_bUseDefaultLocale = true;
            this.m_locale = JAPMessages.getLocale();
        } else {
            this.m_bUseDefaultLocale = false;
            this.m_locale = locale;
        }
    }

    public final String getISOCode() {
        return this.m_iso2.toLowerCase();
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof AbstractISOCodeMapper)) {
            return false;
        }
        return this.getISOCode().equals(((AbstractISOCodeMapper)object).getISOCode());
    }

    public final int hashCode() {
        return this.getISOCode().hashCode();
    }

    protected abstract String getChooseMessage();

    protected abstract String getJRETransaltionOfISOCode(String var1, Locale var2);

    public final String toString() {
        String string;
        if (this.m_iso2.length() == 0) {
            string = this.getChooseMessage();
        } else {
            String string2;
            Locale locale = this.m_bUseDefaultLocale ? JAPMessages.getLocale() : this.m_locale;
            string = this.getJRETransaltionOfISOCode(this.m_iso2, locale);
            if ((string == null || string.trim().length() == 0 || string.equals(this.m_iso2) || string.equals(this.getJRETransaltionOfISOCode("AA", locale)) && string.equals(this.getJRETransaltionOfISOCode("ZZ", locale))) && (string = JAPMessages.getString(string2 = this.getClass().getName() + "_" + this.m_iso2)).equals(string2)) {
                string = this.m_iso2;
            }
        }
        if (this.MAX_LENGTH > 0 && string.length() > this.MAX_LENGTH) {
            string = string.substring(0, this.MAX_LENGTH);
        }
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
        }
        return string;
    }
}

