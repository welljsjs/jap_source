/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;

public class DateTimeDateFormat
extends AbsoluteTimeDateFormat {
    String[] shortMonths = new DateFormatSymbols().getShortMonths();

    public DateTimeDateFormat() {
    }

    public DateTimeDateFormat(TimeZone timeZone) {
        this();
        this.setCalendar(Calendar.getInstance(timeZone));
    }

    public StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        this.calendar.setTime(date);
        int n = this.calendar.get(5);
        if (n < 10) {
            stringBuffer.append('0');
        }
        stringBuffer.append(n);
        stringBuffer.append(' ');
        stringBuffer.append(this.shortMonths[this.calendar.get(2)]);
        stringBuffer.append(' ');
        int n2 = this.calendar.get(1);
        stringBuffer.append(n2);
        stringBuffer.append(' ');
        return super.format(date, stringBuffer, fieldPosition);
    }

    public Date parse(String string, ParsePosition parsePosition) {
        return null;
    }
}

