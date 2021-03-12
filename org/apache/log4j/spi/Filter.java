/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.spi;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

public abstract class Filter
implements OptionHandler {
    public Filter next;
    public static final int DENY = -1;
    public static final int NEUTRAL = 0;
    public static final int ACCEPT = 1;

    public void activateOptions() {
    }

    public abstract int decide(LoggingEvent var1);

    public void setNext(Filter filter) {
        this.next = filter;
    }

    public Filter getNext() {
        return this.next;
    }
}

