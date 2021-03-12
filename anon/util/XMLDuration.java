/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.XMLParseException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Vector;

public class XMLDuration {
    public static final int DURATION = 0;
    public static final int DURATION_DAYTIME = 1;
    public static final int DURATION_YEARMONTH = 2;
    public static final int LESSER = -1;
    public static final int EQUAL = 0;
    public static final int GREATER = 1;
    public static final int INDETERMINATE = 2;
    private static final int YEARS = 1;
    private static final int MONTHS = 2;
    private static final int DAYS = 3;
    private static final int HOURS = 4;
    private static final int MINUTES = 5;
    private static final int SECONDS = 6;
    private static final String[] NAMES = new String[]{"years", "months", "days", "hours", "minutes", "seconds"};
    private long m_years;
    private long m_months;
    private long m_days;
    private long m_hours;
    private long m_minutes;
    private double m_seconds;
    private boolean m_bNegativeSign;
    private String m_theDuration;
    private Vector m_setFields;
    private long m_calcYears;
    private long m_calcMonths;
    private long m_calcDays;
    private long m_calcHours;
    private long m_calcMinutes;
    private double m_calcSeconds;
    private int m_hashCode;
    static /* synthetic */ Class class$java$math$BigDecimal;

    public XMLDuration() {
        this.m_years = 0L;
        this.m_months = 0L;
        this.m_days = 0L;
        this.m_hours = 0L;
        this.m_minutes = 0L;
        this.m_seconds = 0.0;
        this.m_bNegativeSign = false;
        this.m_theDuration = "P0Y";
        this.m_setFields = new Vector();
        this.m_setFields.addElement(new Integer(1));
        this.init();
    }

    public XMLDuration(XMLDuration xMLDuration) {
        if (xMLDuration == null) {
            throw new NullPointerException();
        }
        this.m_years = xMLDuration.m_years;
        this.m_months = xMLDuration.m_months;
        this.m_days = xMLDuration.m_days;
        this.m_hours = xMLDuration.m_hours;
        this.m_minutes = xMLDuration.m_minutes;
        this.m_seconds = xMLDuration.m_seconds;
        this.m_bNegativeSign = xMLDuration.m_bNegativeSign;
        this.m_theDuration = xMLDuration.m_theDuration;
        this.m_setFields = xMLDuration.m_setFields;
        this.init();
    }

    public XMLDuration(String string) throws XMLParseException {
        if (string == null) {
            throw new XMLParseException("##__null__##");
        }
        this.m_theDuration = string;
        if (string.length() == 0) {
            return;
        }
        if ((string = string.trim()).length() < 3) {
            throw new XMLParseException("Duration string is too short to parse: " + this.m_theDuration);
        }
        if (string.startsWith("-")) {
            this.m_bNegativeSign = true;
            string = string.substring(1, string.length());
        }
        if (!string.startsWith("P")) {
            throw new XMLParseException("Duration string has invalid format: " + this.m_theDuration);
        }
        this.m_setFields = new Vector();
        String string2 = string = string.substring(1, string.length());
        string = this.parseXMLSchemaPart(1, string);
        string = this.parseXMLSchemaPart(2, string);
        if ((string = this.parseXMLSchemaPart(3, string)).startsWith("T")) {
            string2 = string = string.substring(1, string.length());
            string = this.parseXMLSchemaPart(4, string);
            string = this.parseXMLSchemaPart(5, string);
            string = this.parseXMLSchemaPart(6, string);
        } else if (string.length() > 0) {
            throw new XMLParseException("Duration string has invalid format (T): " + this.m_theDuration);
        }
        if (string.equals(string2)) {
            throw new XMLParseException("Duration string has invalid format: " + this.m_theDuration);
        }
        this.init();
    }

    private void setField(int n, Number number) {
        if (n == 1) {
            this.m_years = number.intValue();
        } else if (n == 2) {
            this.m_months = number.intValue();
        } else if (n == 3) {
            this.m_days = number.intValue();
        } else if (n == 4) {
            this.m_hours = number.intValue();
        } else if (n == 5) {
            this.m_minutes = number.intValue();
        } else if (n == 6) {
            this.m_seconds = number.doubleValue();
        }
    }

    private String parseXMLSchemaPart(int n, String string) throws XMLParseException {
        String string2 = "";
        if (n == 1) {
            string2 = "Y";
        } else if (n == 2) {
            string2 = "M";
        } else if (n == 3) {
            string2 = "D";
        } else if (n == 4) {
            string2 = "H";
        } else if (n == 5) {
            string2 = "M";
        } else if (n == 6) {
            string2 = "S";
        }
        int n2 = string.indexOf(string2);
        if (n2 > 0) {
            String string3 = string.substring(0, n2);
            if (n == 2 && string3.indexOf("T") >= 0) {
                return string;
            }
            this.m_setFields.addElement(new Integer(n));
            try {
                if (n == 6) {
                    this.setField(n, Double.valueOf(string3));
                } else {
                    this.setField(n, Integer.valueOf(string3));
                }
                string = string.length() > n2 ? string.substring(n2 + 1, string.length()) : "";
            }
            catch (NumberFormatException numberFormatException) {
                throw new XMLParseException("Duration string has invalid format (" + string2 + ", " + "NumberFormatException: " + numberFormatException.getMessage() + "): " + this.m_theDuration);
            }
        }
        return string;
    }

    public String getXMLSchema() {
        return this.m_theDuration;
    }

    public int getXMLSchemaType() throws IllegalStateException {
        boolean bl = this.isSet(1);
        boolean bl2 = this.isSet(2);
        boolean bl3 = this.isSet(3);
        boolean bl4 = this.isSet(4);
        boolean bl5 = this.isSet(5);
        boolean bl6 = this.isSet(6);
        if (bl && bl2 && bl3 && bl4 && bl5 && bl6) {
            return 0;
        }
        if (!bl && !bl2 && bl3 && bl4 && bl5 && bl6) {
            return 1;
        }
        if (bl && bl2 && !bl3 && !bl4 && !bl5 && !bl6) {
            return 2;
        }
        throw new IllegalStateException("This Duration does not match one of the XML Schema date/time datatypes: year set = " + bl + " month set = " + bl2 + " day set = " + bl3 + " hour set = " + bl4 + " minute set = " + bl5 + " second set = " + bl6);
    }

    public int getSign() {
        if (this.m_bNegativeSign) {
            return -1;
        }
        return 1;
    }

    public long getYears() {
        return this.m_years;
    }

    public long getMonths() {
        return this.m_months;
    }

    public long getDays() {
        return this.m_days;
    }

    public long getHours() {
        return this.m_hours;
    }

    public long getMinutes() {
        return this.m_minutes;
    }

    public double getSeconds() {
        return this.m_seconds;
    }

    public static String getFieldName(Object object) {
        if (object == null || !(object instanceof Integer)) {
            return null;
        }
        return XMLDuration.getFieldName((Integer)object);
    }

    public static String getFieldName(int n) {
        if (n < 1 || n > 6) {
            return null;
        }
        return NAMES[n - 1];
    }

    public Enumeration getFields() {
        return this.m_setFields.elements();
    }

    public Number getField(Object object) {
        if (object == null || !(object instanceof Integer)) {
            return null;
        }
        return this.getField((Integer)object);
    }

    public Number getField(int n) {
        if (n == 1) {
            return BigInteger.valueOf(this.m_years);
        }
        if (n == 2) {
            return BigInteger.valueOf(this.m_months);
        }
        if (n == 3) {
            return BigInteger.valueOf(this.m_days);
        }
        if (n == 4) {
            return BigInteger.valueOf(this.m_hours);
        }
        if (n == 5) {
            return BigInteger.valueOf(this.m_minutes);
        }
        if (n == 6) {
            return new BigDecimal(this.m_seconds);
        }
        return null;
    }

    public boolean isSet(int n) {
        return this.m_setFields.contains(new Integer(n));
    }

    public XMLDuration negate() {
        XMLDuration xMLDuration = new XMLDuration(this);
        xMLDuration.m_bNegativeSign = !this.m_bNegativeSign;
        return xMLDuration;
    }

    public int compare(XMLDuration xMLDuration) {
        if (this.m_calcYears > xMLDuration.m_calcYears) {
            return 1;
        }
        if (this.m_calcYears < xMLDuration.m_calcYears) {
            return -1;
        }
        if (this.m_calcMonths > xMLDuration.m_calcMonths) {
            return 1;
        }
        if (this.m_calcMonths < xMLDuration.m_calcMonths) {
            return -1;
        }
        if (this.m_calcDays > xMLDuration.m_calcDays) {
            return 1;
        }
        if (this.m_calcDays < xMLDuration.m_calcDays) {
            return -1;
        }
        if (this.m_calcHours > xMLDuration.m_calcHours) {
            return 1;
        }
        if (this.m_calcHours < xMLDuration.m_calcHours) {
            return -1;
        }
        if (this.m_calcMinutes > xMLDuration.m_calcMinutes) {
            return 1;
        }
        if (this.m_calcMinutes < xMLDuration.m_calcMinutes) {
            return -1;
        }
        if (this.m_calcSeconds > xMLDuration.m_calcSeconds) {
            return 1;
        }
        if (this.m_calcSeconds < xMLDuration.m_calcSeconds) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof XMLDuration)) {
            return false;
        }
        return this.compare((XMLDuration)object) == 0;
    }

    public int hashCode() {
        return this.m_hashCode;
    }

    public boolean isLongerThan(XMLDuration xMLDuration) {
        return this.compare(xMLDuration) == 1;
    }

    public boolean isShorterThan(XMLDuration xMLDuration) {
        return this.compare(xMLDuration) == -1;
    }

    public int getLastFieldSet() {
        if (this.isSet(6)) {
            return 6;
        }
        if (this.isSet(5)) {
            return 5;
        }
        if (this.isSet(4)) {
            return 4;
        }
        if (this.isSet(3)) {
            return 3;
        }
        if (this.isSet(2)) {
            return 2;
        }
        return 1;
    }

    public String toString() {
        BigInteger bigInteger;
        BigInteger bigInteger2;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.getSign() < 0) {
            stringBuffer.append('-');
        }
        stringBuffer.append('P');
        BigInteger bigInteger3 = (BigInteger)this.getField(1);
        if (bigInteger3 != null) {
            stringBuffer.append(bigInteger3 + "Y");
        }
        if ((bigInteger2 = (BigInteger)this.getField(2)) != null) {
            stringBuffer.append(bigInteger2 + "M");
        }
        if ((bigInteger = (BigInteger)this.getField(3)) != null) {
            stringBuffer.append(bigInteger + "D");
        }
        BigInteger bigInteger4 = (BigInteger)this.getField(4);
        BigInteger bigInteger5 = (BigInteger)this.getField(5);
        BigDecimal bigDecimal = (BigDecimal)this.getField(6);
        if (bigInteger4 != null || bigInteger5 != null || bigDecimal != null) {
            stringBuffer.append('T');
            if (bigInteger4 != null) {
                stringBuffer.append(bigInteger4 + "H");
            }
            if (bigInteger5 != null) {
                stringBuffer.append(bigInteger5 + "M");
            }
            if (bigDecimal != null) {
                stringBuffer.append(this.toString(bigDecimal) + "S");
            }
        }
        return stringBuffer.toString();
    }

    private String toString(BigDecimal bigDecimal) {
        String string = this.toStringJDK5(bigDecimal);
        if (string == null) {
            string = bigDecimal.toString();
        }
        return string;
    }

    private String toStringJDK5(BigDecimal bigDecimal) {
        StringBuffer stringBuffer;
        BigInteger bigInteger;
        try {
            bigInteger = (BigInteger)(class$java$math$BigDecimal == null ? (class$java$math$BigDecimal = XMLDuration.class$("java.math.BigDecimal")) : class$java$math$BigDecimal).getMethod("unscaledValue", null).invoke(bigDecimal, null);
        }
        catch (Exception exception) {
            return null;
        }
        String string = bigInteger.toString();
        int n = bigDecimal.scale();
        if (n == 0) {
            return string;
        }
        int n2 = string.length() - n;
        if (n2 == 0) {
            return "0." + string;
        }
        if (n2 > 0) {
            stringBuffer = new StringBuffer(string);
            stringBuffer.insert(n2, '.');
        } else {
            stringBuffer = new StringBuffer(3 - n2 + string.length());
            stringBuffer.append("0.");
            for (int i = 0; i < -n2; ++i) {
                stringBuffer.append('0');
            }
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    private void init() {
        this.m_calcSeconds = this.m_seconds;
        this.m_calcMinutes = this.m_minutes;
        this.m_calcHours = this.m_hours;
        this.m_calcDays = this.m_days;
        this.m_calcMonths = this.m_months;
        this.m_calcYears = this.m_years;
        this.m_calcMinutes += (long)((int)this.m_calcSeconds / 60);
        this.m_calcSeconds -= (double)((int)this.m_calcSeconds / 60 * 60);
        this.m_calcHours += this.m_calcMinutes / 60L;
        this.m_calcMinutes %= 60L;
        this.m_calcDays += this.m_calcHours / 24L;
        this.m_calcHours %= 24L;
        this.m_calcYears += this.m_calcDays / 365L;
        this.m_calcDays %= 365L;
        this.m_calcMonths += 5L * (this.m_calcDays / 150L);
        this.m_calcDays %= 150L;
        this.m_calcMonths += this.m_calcDays / 28L;
        this.m_calcDays %= 28L;
        this.m_calcYears += this.m_calcMonths / 12L;
        this.m_calcMonths %= 12L;
        this.m_hashCode = (int)((long)this.m_calcSeconds + this.m_calcMinutes + this.m_calcHours + this.m_calcDays + this.m_calcMonths + this.m_calcYears) * this.getSign();
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

