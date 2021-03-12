/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.spi.LoggingEvent;

public abstract class PatternConverter {
    public PatternConverter next;
    int min = -1;
    int max = Integer.MAX_VALUE;
    boolean leftAlign = false;
    static String[] SPACES = new String[]{" ", "  ", "    ", "        ", "                ", "                                "};

    protected PatternConverter() {
    }

    protected PatternConverter(FormattingInfo formattingInfo) {
        this.min = formattingInfo.min;
        this.max = formattingInfo.max;
        this.leftAlign = formattingInfo.leftAlign;
    }

    protected abstract String convert(LoggingEvent var1);

    public void format(StringBuffer stringBuffer, LoggingEvent loggingEvent) {
        String string = this.convert(loggingEvent);
        if (string == null) {
            if (0 < this.min) {
                this.spacePad(stringBuffer, this.min);
            }
            return;
        }
        int n = string.length();
        if (n > this.max) {
            stringBuffer.append(string.substring(n - this.max));
        } else if (n < this.min) {
            if (this.leftAlign) {
                stringBuffer.append(string);
                this.spacePad(stringBuffer, this.min - n);
            } else {
                this.spacePad(stringBuffer, this.min - n);
                stringBuffer.append(string);
            }
        } else {
            stringBuffer.append(string);
        }
    }

    public void spacePad(StringBuffer stringBuffer, int n) {
        while (n >= 32) {
            stringBuffer.append(SPACES[5]);
            n -= 32;
        }
        for (int i = 4; i >= 0; --i) {
            if ((n & 1 << i) == 0) continue;
            stringBuffer.append(SPACES[i]);
        }
    }
}

