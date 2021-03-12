/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class DemultiplexorInputStream
extends FilterInputStream {
    public DemultiplexorInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public abstract int read() throws IOException;

    public abstract int read(byte[] var1, int var2, int var3) throws IOException;

    public abstract int available() throws IOException;

    public abstract void setTerminator(byte[] var1, int[] var2);

    public abstract boolean atEnd();

    public abstract boolean startsWithCRLF() throws IOException;
}

