/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorHandler;

public class CountingQuietWriter
extends QuietWriter {
    protected long count;

    public CountingQuietWriter(Writer writer, ErrorHandler errorHandler) {
        super(writer, errorHandler);
    }

    public void write(String string) {
        try {
            this.out.write(string);
            this.count += (long)string.length();
        }
        catch (IOException iOException) {
            this.errorHandler.error("Write failure.", iOException, 1);
        }
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long l) {
        this.count = l;
    }
}

