/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.helpers.DateTimeDateFormat;
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

public class PatternParser {
    private static final char ESCAPE_CHAR = '%';
    private static final int LITERAL_STATE = 0;
    private static final int CONVERTER_STATE = 1;
    private static final int MINUS_STATE = 2;
    private static final int DOT_STATE = 3;
    private static final int MIN_STATE = 4;
    private static final int MAX_STATE = 5;
    static final int FULL_LOCATION_CONVERTER = 1000;
    static final int METHOD_LOCATION_CONVERTER = 1001;
    static final int CLASS_LOCATION_CONVERTER = 1002;
    static final int LINE_LOCATION_CONVERTER = 1003;
    static final int FILE_LOCATION_CONVERTER = 1004;
    static final int RELATIVE_TIME_CONVERTER = 2000;
    static final int THREAD_CONVERTER = 2001;
    static final int LEVEL_CONVERTER = 2002;
    static final int NDC_CONVERTER = 2003;
    static final int MESSAGE_CONVERTER = 2004;
    int state;
    protected StringBuffer currentLiteral = new StringBuffer(32);
    protected int patternLength;
    protected int i;
    PatternConverter head;
    PatternConverter tail;
    protected FormattingInfo formattingInfo = new FormattingInfo();
    protected String pattern;
    static /* synthetic */ Class class$java$text$DateFormat;

    public PatternParser(String string) {
        this.pattern = string;
        this.patternLength = string.length();
        this.state = 0;
    }

    private void addToList(PatternConverter patternConverter) {
        if (this.head == null) {
            this.head = this.tail = patternConverter;
        } else {
            this.tail.next = patternConverter;
            this.tail = patternConverter;
        }
    }

    protected String extractOption() {
        int n;
        if (this.i < this.patternLength && this.pattern.charAt(this.i) == '{' && (n = this.pattern.indexOf(125, this.i)) > this.i) {
            String string = this.pattern.substring(this.i + 1, n);
            this.i = n + 1;
            return string;
        }
        return null;
    }

    protected int extractPrecisionOption() {
        String string = this.extractOption();
        int n = 0;
        if (string != null) {
            try {
                n = Integer.parseInt(string);
                if (n <= 0) {
                    LogLog.error("Precision option (" + string + ") isn't a positive integer.");
                    n = 0;
                }
            }
            catch (NumberFormatException numberFormatException) {
                LogLog.error("Category option \"" + string + "\" not a decimal integer.", numberFormatException);
            }
        }
        return n;
    }

    public PatternConverter parse() {
        this.i = 0;
        block15: while (this.i < this.patternLength) {
            char c = this.pattern.charAt(this.i++);
            block0 : switch (this.state) {
                case 0: {
                    if (this.i == this.patternLength) {
                        this.currentLiteral.append(c);
                        continue block15;
                    }
                    if (c == '%') {
                        switch (this.pattern.charAt(this.i)) {
                            case '%': {
                                this.currentLiteral.append(c);
                                ++this.i;
                                break block0;
                            }
                            case 'n': {
                                this.currentLiteral.append(Layout.LINE_SEP);
                                ++this.i;
                                break block0;
                            }
                        }
                        if (this.currentLiteral.length() != 0) {
                            this.addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
                        }
                        this.currentLiteral.setLength(0);
                        this.currentLiteral.append(c);
                        this.state = 1;
                        this.formattingInfo.reset();
                        break;
                    }
                    this.currentLiteral.append(c);
                    break;
                }
                case 1: {
                    this.currentLiteral.append(c);
                    switch (c) {
                        case '-': {
                            this.formattingInfo.leftAlign = true;
                            break block0;
                        }
                        case '.': {
                            this.state = 3;
                            break block0;
                        }
                    }
                    if (c >= '0' && c <= '9') {
                        this.formattingInfo.min = c - 48;
                        this.state = 4;
                        break;
                    }
                    this.finalizeConverter(c);
                    break;
                }
                case 4: {
                    this.currentLiteral.append(c);
                    if (c >= '0' && c <= '9') {
                        this.formattingInfo.min = this.formattingInfo.min * 10 + (c - 48);
                        break;
                    }
                    if (c == '.') {
                        this.state = 3;
                        break;
                    }
                    this.finalizeConverter(c);
                    break;
                }
                case 3: {
                    this.currentLiteral.append(c);
                    if (c >= '0' && c <= '9') {
                        this.formattingInfo.max = c - 48;
                        this.state = 5;
                        break;
                    }
                    LogLog.error("Error occured in position " + this.i + ".\n Was expecting digit, instead got char \"" + c + "\".");
                    this.state = 0;
                    break;
                }
                case 5: {
                    this.currentLiteral.append(c);
                    if (c >= '0' && c <= '9') {
                        this.formattingInfo.max = this.formattingInfo.max * 10 + (c - 48);
                        break;
                    }
                    this.finalizeConverter(c);
                    this.state = 0;
                }
            }
        }
        if (this.currentLiteral.length() != 0) {
            this.addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
        }
        return this.head;
    }

    protected void finalizeConverter(char c) {
        PatternConverter patternConverter = null;
        switch (c) {
            case 'c': {
                patternConverter = new CategoryPatternConverter(this.formattingInfo, this.extractPrecisionOption());
                this.currentLiteral.setLength(0);
                break;
            }
            case 'C': {
                patternConverter = new ClassNamePatternConverter(this.formattingInfo, this.extractPrecisionOption());
                this.currentLiteral.setLength(0);
                break;
            }
            case 'd': {
                DateFormat dateFormat;
                String string = "ISO8601";
                String string2 = this.extractOption();
                if (string2 != null) {
                    string = string2;
                }
                if (string.equalsIgnoreCase("ISO8601")) {
                    dateFormat = new ISO8601DateFormat();
                } else if (string.equalsIgnoreCase("ABSOLUTE")) {
                    dateFormat = new AbsoluteTimeDateFormat();
                } else if (string.equalsIgnoreCase("DATE")) {
                    dateFormat = new DateTimeDateFormat();
                } else {
                    try {
                        dateFormat = new SimpleDateFormat(string);
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        LogLog.error("Could not instantiate SimpleDateFormat with " + string, illegalArgumentException);
                        dateFormat = (DateFormat)OptionConverter.instantiateByClassName("org.apache.log4j.helpers.ISO8601DateFormat", class$java$text$DateFormat == null ? (class$java$text$DateFormat = PatternParser.class$("java.text.DateFormat")) : class$java$text$DateFormat, null);
                    }
                }
                patternConverter = new DatePatternConverter(this.formattingInfo, dateFormat);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'F': {
                patternConverter = new LocationPatternConverter(this.formattingInfo, 1004);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'l': {
                patternConverter = new LocationPatternConverter(this.formattingInfo, 1000);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'L': {
                patternConverter = new LocationPatternConverter(this.formattingInfo, 1003);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'm': {
                patternConverter = new BasicPatternConverter(this.formattingInfo, 2004);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'M': {
                patternConverter = new LocationPatternConverter(this.formattingInfo, 1001);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'p': {
                patternConverter = new BasicPatternConverter(this.formattingInfo, 2002);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'r': {
                patternConverter = new BasicPatternConverter(this.formattingInfo, 2000);
                this.currentLiteral.setLength(0);
                break;
            }
            case 't': {
                patternConverter = new BasicPatternConverter(this.formattingInfo, 2001);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'x': {
                patternConverter = new BasicPatternConverter(this.formattingInfo, 2003);
                this.currentLiteral.setLength(0);
                break;
            }
            case 'X': {
                String string = this.extractOption();
                patternConverter = new MDCPatternConverter(this.formattingInfo, string);
                this.currentLiteral.setLength(0);
                break;
            }
            default: {
                LogLog.error("Unexpected char [" + c + "] at position " + this.i + " in conversion patterrn.");
                patternConverter = new LiteralPatternConverter(this.currentLiteral.toString());
                this.currentLiteral.setLength(0);
            }
        }
        this.addConverter(patternConverter);
    }

    protected void addConverter(PatternConverter patternConverter) {
        this.currentLiteral.setLength(0);
        this.addToList(patternConverter);
        this.state = 0;
        this.formattingInfo.reset();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class CategoryPatternConverter
    extends NamedPatternConverter {
        CategoryPatternConverter(FormattingInfo formattingInfo, int n) {
            super(formattingInfo, n);
        }

        String getFullyQualifiedName(LoggingEvent loggingEvent) {
            return loggingEvent.getLoggerName();
        }
    }

    private class ClassNamePatternConverter
    extends NamedPatternConverter {
        ClassNamePatternConverter(FormattingInfo formattingInfo, int n) {
            super(formattingInfo, n);
        }

        String getFullyQualifiedName(LoggingEvent loggingEvent) {
            return loggingEvent.getLocationInformation().getClassName();
        }
    }

    private static abstract class NamedPatternConverter
    extends PatternConverter {
        int precision;

        NamedPatternConverter(FormattingInfo formattingInfo, int n) {
            super(formattingInfo);
            this.precision = n;
        }

        abstract String getFullyQualifiedName(LoggingEvent var1);

        public String convert(LoggingEvent loggingEvent) {
            String string = this.getFullyQualifiedName(loggingEvent);
            if (this.precision <= 0) {
                return string;
            }
            int n = string.length();
            int n2 = n - 1;
            for (int i = this.precision; i > 0; --i) {
                if ((n2 = string.lastIndexOf(46, n2 - 1)) != -1) continue;
                return string;
            }
            return string.substring(n2 + 1, n);
        }
    }

    private class LocationPatternConverter
    extends PatternConverter {
        int type;

        LocationPatternConverter(FormattingInfo formattingInfo, int n) {
            super(formattingInfo);
            this.type = n;
        }

        public String convert(LoggingEvent loggingEvent) {
            LocationInfo locationInfo = loggingEvent.getLocationInformation();
            switch (this.type) {
                case 1000: {
                    return locationInfo.fullInfo;
                }
                case 1001: {
                    return locationInfo.getMethodName();
                }
                case 1003: {
                    return locationInfo.getLineNumber();
                }
                case 1004: {
                    return locationInfo.getFileName();
                }
            }
            return null;
        }
    }

    private static class MDCPatternConverter
    extends PatternConverter {
        private String key;

        MDCPatternConverter(FormattingInfo formattingInfo, String string) {
            super(formattingInfo);
            this.key = string;
        }

        public String convert(LoggingEvent loggingEvent) {
            Object object = loggingEvent.getMDC(this.key);
            if (object == null) {
                return null;
            }
            return object.toString();
        }
    }

    private static class DatePatternConverter
    extends PatternConverter {
        private DateFormat df;
        private Date date = new Date();

        DatePatternConverter(FormattingInfo formattingInfo, DateFormat dateFormat) {
            super(formattingInfo);
            this.df = dateFormat;
        }

        public String convert(LoggingEvent loggingEvent) {
            this.date.setTime(loggingEvent.timeStamp);
            String string = null;
            try {
                string = this.df.format(this.date);
            }
            catch (Exception exception) {
                LogLog.error("Error occured while converting date.", exception);
            }
            return string;
        }
    }

    private static class LiteralPatternConverter
    extends PatternConverter {
        private String literal;

        LiteralPatternConverter(String string) {
            this.literal = string;
        }

        public final void format(StringBuffer stringBuffer, LoggingEvent loggingEvent) {
            stringBuffer.append(this.literal);
        }

        public String convert(LoggingEvent loggingEvent) {
            return this.literal;
        }
    }

    private static class BasicPatternConverter
    extends PatternConverter {
        int type;

        BasicPatternConverter(FormattingInfo formattingInfo, int n) {
            super(formattingInfo);
            this.type = n;
        }

        public String convert(LoggingEvent loggingEvent) {
            switch (this.type) {
                case 2000: {
                    return Long.toString(loggingEvent.timeStamp - LoggingEvent.getStartTime());
                }
                case 2001: {
                    return loggingEvent.getThreadName();
                }
                case 2002: {
                    return loggingEvent.getLevel().toString();
                }
                case 2003: {
                    return loggingEvent.getNDC();
                }
                case 2004: {
                    return loggingEvent.getRenderedMessage();
                }
            }
            return null;
        }
    }
}

