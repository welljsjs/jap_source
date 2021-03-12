/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.io.StreamOverflowException;

public final class Streams {
    private static int BUFFER_SIZE = 4096;

    public static void drain(InputStream inputStream) throws IOException {
        byte[] arrby = new byte[BUFFER_SIZE];
        while (inputStream.read(arrby, 0, arrby.length) >= 0) {
        }
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Streams.pipeAll(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] readAllLimited(InputStream inputStream, int n) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Streams.pipeAllLimited(inputStream, n, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static int readFully(InputStream inputStream, byte[] arrby) throws IOException {
        return Streams.readFully(inputStream, arrby, 0, arrby.length);
    }

    public static int readFully(InputStream inputStream, byte[] arrby, int n, int n2) throws IOException {
        int n3;
        int n4;
        for (n3 = 0; n3 < n2 && (n4 = inputStream.read(arrby, n + n3, n2 - n3)) >= 0; n3 += n4) {
        }
        return n3;
    }

    public static void pipeAll(InputStream inputStream, OutputStream outputStream) throws IOException {
        int n;
        byte[] arrby = new byte[BUFFER_SIZE];
        while ((n = inputStream.read(arrby, 0, arrby.length)) >= 0) {
            outputStream.write(arrby, 0, n);
        }
    }

    public static long pipeAllLimited(InputStream inputStream, long l, OutputStream outputStream) throws IOException {
        int n;
        long l2 = 0L;
        byte[] arrby = new byte[BUFFER_SIZE];
        while ((n = inputStream.read(arrby, 0, arrby.length)) >= 0) {
            if (l - l2 < (long)n) {
                throw new StreamOverflowException("Data Overflow");
            }
            l2 += (long)n;
            outputStream.write(arrby, 0, n);
        }
        return l2;
    }
}

