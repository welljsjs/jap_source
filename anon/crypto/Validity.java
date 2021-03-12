/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import java.util.Calendar;
import java.util.Date;

public class Validity {
    private Calendar m_validFrom;
    private Calendar m_validTo;
    private static final int TEMPORARY_VALIDITY_IN_MINUTES = 10;

    public Validity(Calendar calendar, int n) {
        this(calendar, Validity.createValidTo(calendar, n));
    }

    public Validity(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            throw new IllegalArgumentException("Calendars for validity must not be null!");
        }
        this.m_validFrom = (Calendar)calendar.clone();
        this.m_validTo = calendar2.before(calendar) ? this.m_validFrom : (Calendar)calendar2.clone();
    }

    public Date getValidFrom() {
        return this.m_validFrom.getTime();
    }

    public Date getValidTo() {
        return this.m_validTo.getTime();
    }

    public boolean isValid(Date date) {
        return !date.before(this.getValidFrom()) && !date.after(this.getValidTo());
    }

    private static Calendar createValidTo(Calendar calendar, int n) {
        if (calendar == null) {
            return null;
        }
        Calendar calendar2 = (Calendar)calendar.clone();
        if (n < 0) {
            calendar2.add(12, 10);
        } else {
            calendar2.add(1, n);
        }
        return calendar2;
    }
}

