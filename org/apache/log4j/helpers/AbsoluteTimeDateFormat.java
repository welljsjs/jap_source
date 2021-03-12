/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AbsoluteTimeDateFormat
extends DateFormat {
    public static final String ABS_TIME_DATE_FORMAT = "ABSOLUTE";
    public static final String DATE_AND_TIME_DATE_FORMAT = "DATE";
    public static final String ISO8601_DATE_FORMAT = "ISO8601";
    private static long previousTime;
    private static char[] previousTimeWithoutMillis;

    public AbsoluteTimeDateFormat() {
        this.setCalendar(Calendar.getInstance());
    }

    public AbsoluteTimeDateFormat(TimeZone timeZone) {
        this.setCalendar(Calendar.getInstance(timeZone));
    }

    public StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        int n;
        long l = date.getTime();
        if (l - (long)(n = (int)(l % 1000L)) != previousTime) {
            this.calendar.setTime(date);
            int n2 = stringBuffer.length();
            int n3 = this.calendar.get(11);
            if (n3 < 10) {
                stringBuffer.append('0');
            }
            stringBuffer.append(n3);
            stringBuffer.append(':');
            int n4 = this.calendar.get(12);
            if (n4 < 10) {
                stringBuffer.append('0');
            }
            stringBuffer.append(n4);
            stringBuffer.append(':');
            int n5 = this.calendar.get(13);
            if (n5 < 10) {
                stringBuffer.append('0');
            }
            stringBuffer.append(n5);
            stringBuffer.append(',');
            stringBuffer.getChars(n2, stringBuffer.length(), previousTimeWithoutMillis, 0);
            previousTime = l - (long)n;
        } else {
            stringBuffer.append(previousTimeWithoutMillis);
        }
        if (n < 100) {
            stringBuffer.append('0');
        }
        if (n < 10) {
            stringBuffer.append('0');
        }
        stringBuffer.append(n);
        return stringBuffer;
    }

    public Date parse(String string, ParsePosition parsePosition) {
        return null;
    }

    static {
        previousTimeWithoutMillis = new char[9];
    }
}

