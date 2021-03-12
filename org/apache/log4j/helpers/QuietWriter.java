/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.spi.ErrorHandler;

public class QuietWriter
extends FilterWriter {
    protected ErrorHandler errorHandler;

    public QuietWriter(Writer writer, ErrorHandler errorHandler) {
        super(writer);
        this.setErrorHandler(errorHandler);
    }

    public void write(String string) {
        try {
            this.out.write(string);
        }
        catch (IOException iOException) {
            this.errorHandler.error("Failed to write [" + string + "].", iOException, 1);
        }
    }

    public void flush() {
        try {
            this.out.flush();
        }
        catch (IOException iOException) {
            this.errorHandler.error("Failed to flush writer,", iOException, 2);
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
        }
        this.errorHandler = errorHandler;
    }
}

