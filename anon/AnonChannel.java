/*
 * Decompiled with CFR 0.150.
 */
package anon;

import java.io.InputStream;
import java.io.OutputStream;

public interface AnonChannel {
    public static final int HTTP = 0;
    public static final int SOCKS = 1;
    public static final int SMTP = 2;

    public InputStream getInputStream();

    public OutputStream getOutputStream();

    public int getOutputBlockSize();

    public void close();

    public boolean isClosed();
}

