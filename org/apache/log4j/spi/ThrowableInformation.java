/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.spi;

import java.io.Serializable;
import org.apache.log4j.spi.VectorWriter;

public class ThrowableInformation
implements Serializable {
    static final long serialVersionUID = -4748765566864322735L;
    private transient Throwable throwable;
    private String[] rep;

    public ThrowableInformation(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String[] getThrowableStrRep() {
        if (this.rep != null) {
            return (String[])this.rep.clone();
        }
        VectorWriter vectorWriter = new VectorWriter();
        this.throwable.printStackTrace(vectorWriter);
        this.rep = vectorWriter.toStringArray();
        return this.rep;
    }
}

