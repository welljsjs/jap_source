/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import logging.LogHolder;
import logging.LogType;

public final class ZLibTools {
    public static byte[] compress(byte[] arrby) {
        byte[] arrby2 = null;
        try {
            Deflater deflater = new Deflater(9);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream((OutputStream)byteArrayOutputStream, deflater);
            deflaterOutputStream.write(arrby, 0, arrby.length);
            deflaterOutputStream.finish();
            arrby2 = byteArrayOutputStream.toByteArray();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return arrby2;
    }

    public static byte[] decompress(byte[] arrby) {
        byte[] arrby2 = null;
        try {
            int n;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Inflater inflater = new Inflater();
            inflater.setInput(arrby);
            byte[] arrby3 = new byte[10000];
            while ((n = inflater.inflate(arrby3)) > 0) {
                byteArrayOutputStream.write(arrby3, 0, n);
            }
            byteArrayOutputStream.flush();
            arrby2 = byteArrayOutputStream.toByteArray();
        }
        catch (Throwable throwable) {
            LogHolder.log(6, LogType.MISC, "ZLIb decompress() decommpressed failed!", throwable);
        }
        return arrby2;
    }
}

