/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.test;

import anon.crypto.tinytls.TinyTLS;
import java.io.InputStream;
import logging.LogHolder;
import logging.SystemErrLog;

public class tlsclienttest {
    public static void main(String[] arrstring) throws Exception {
        LogHolder.setLogInstance(new SystemErrLog());
        TinyTLS tinyTLS = new TinyTLS("localhost", 3456);
        tinyTLS.checkRootCertificate(false);
        tinyTLS.startHandshake();
        InputStream inputStream = tinyTLS.getInputStream();
        byte[] arrby = new byte[1000000];
        tinyTLS.setSoTimeout(1000);
        try {
            int n;
            while ((n = inputStream.read()) > 0) {
                System.out.print((char)n);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        tinyTLS.close();
    }
}

