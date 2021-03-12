/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.io.CopyStreamListener;

public final class Util {
    public static final int DEFAULT_COPY_BUFFER_SIZE = 1024;

    private Util() {
    }

    public static final long copyStream(InputStream inputStream, OutputStream outputStream, int n, long l, CopyStreamListener copyStreamListener, boolean bl) throws CopyStreamException {
        byte[] arrby = new byte[n];
        long l2 = 0L;
        try {
            int n2;
            while ((n2 = inputStream.read(arrby)) != -1) {
                if (n2 == 0) {
                    n2 = inputStream.read();
                    if (n2 >= 0) {
                        outputStream.write(n2);
                        if (bl) {
                            outputStream.flush();
                        }
                        ++l2;
                        if (copyStreamListener == null) continue;
                        copyStreamListener.bytesTransferred(l2, 1, l);
                        continue;
                    }
                    break;
                }
                outputStream.write(arrby, 0, n2);
                if (bl) {
                    outputStream.flush();
                }
                l2 += (long)n2;
                if (copyStreamListener == null) continue;
                copyStreamListener.bytesTransferred(l2, n2, l);
            }
        }
        catch (IOException iOException) {
            throw new CopyStreamException("IOException caught while copying.", l2, iOException);
        }
        return l2;
    }

    public static final long copyStream(InputStream inputStream, OutputStream outputStream, int n, long l, CopyStreamListener copyStreamListener) throws CopyStreamException {
        return Util.copyStream(inputStream, outputStream, n, l, copyStreamListener, true);
    }

    public static final long copyStream(InputStream inputStream, OutputStream outputStream, int n) throws CopyStreamException {
        return Util.copyStream(inputStream, outputStream, n, -1L, null);
    }

    public static final long copyStream(InputStream inputStream, OutputStream outputStream) throws CopyStreamException {
        return Util.copyStream(inputStream, outputStream, 1024);
    }

    public static final long copyReader(Reader reader, Writer writer, int n, long l, CopyStreamListener copyStreamListener) throws CopyStreamException {
        char[] arrc = new char[n];
        long l2 = 0L;
        try {
            int n2;
            while ((n2 = reader.read(arrc)) != -1) {
                if (n2 == 0) {
                    n2 = reader.read();
                    if (n2 >= 0) {
                        writer.write(n2);
                        writer.flush();
                        ++l2;
                        if (copyStreamListener == null) continue;
                        copyStreamListener.bytesTransferred(l2, n2, l);
                        continue;
                    }
                    break;
                }
                writer.write(arrc, 0, n2);
                writer.flush();
                l2 += (long)n2;
                if (copyStreamListener == null) continue;
                copyStreamListener.bytesTransferred(l2, n2, l);
            }
        }
        catch (IOException iOException) {
            throw new CopyStreamException("IOException caught while copying.", l2, iOException);
        }
        return l2;
    }

    public static final long copyReader(Reader reader, Writer writer, int n) throws CopyStreamException {
        return Util.copyReader(reader, writer, n, -1L, null);
    }

    public static final long copyReader(Reader reader, Writer writer) throws CopyStreamException {
        return Util.copyReader(reader, writer, 1024);
    }
}

