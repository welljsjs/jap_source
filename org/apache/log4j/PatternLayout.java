/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class PatternLayout
extends Layout {
    public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
    public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
    protected final int BUF_SIZE = 256;
    protected final int MAX_CAPACITY = 1024;
    private StringBuffer sbuf = new StringBuffer(256);
    private String pattern;
    private PatternConverter head;
    private String timezone;

    public PatternLayout() {
        this(DEFAULT_CONVERSION_PATTERN);
    }

    public PatternLayout(String string) {
        this.pattern = string;
        this.head = this.createPatternParser(string == null ? DEFAULT_CONVERSION_PATTERN : string).parse();
    }

    public void setConversionPattern(String string) {
        this.pattern = string;
        this.head = this.createPatternParser(string).parse();
    }

    public String getConversionPattern() {
        return this.pattern;
    }

    public void activateOptions() {
    }

    public boolean ignoresThrowable() {
        return true;
    }

    protected PatternParser createPatternParser(String string) {
        return new PatternParser(string);
    }

    public String format(LoggingEvent loggingEvent) {
        if (this.sbuf.capacity() > 1024) {
            this.sbuf = new StringBuffer(256);
        } else {
            this.sbuf.setLength(0);
        }
        PatternConverter patternConverter = this.head;
        while (patternConverter != null) {
            patternConverter.format(this.sbuf, loggingEvent);
            patternConverter = patternConverter.next;
        }
        return this.sbuf.toString();
    }
}

