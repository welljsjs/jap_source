/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Codecs;
import HTTPClient.GlobalConstants;
import HTTPClient.HashVerifier;
import HTTPClient.RoResponse;
import java.io.IOException;

class VerifyMD5
implements HashVerifier,
GlobalConstants {
    RoResponse resp;

    public VerifyMD5(RoResponse roResponse) {
        this.resp = roResponse;
    }

    public void verifyHash(byte[] arrby, long l) throws IOException {
        String string;
        try {
            string = this.resp.getHeader("Content-MD5");
            if (string == null) {
                string = this.resp.getTrailer("Content-MD5");
            }
        }
        catch (IOException iOException) {
            return;
        }
        if (string == null) {
            return;
        }
        string = string.trim();
        byte[] arrby2 = string.getBytes();
        arrby2 = Codecs.base64Decode(arrby2);
        for (int i = 0; i < arrby.length; ++i) {
            if (arrby[i] == arrby2[i]) continue;
            throw new IOException("MD5-Digest mismatch: expected " + VerifyMD5.hex(arrby2) + " but calculated " + VerifyMD5.hex(arrby));
        }
    }

    private static String hex(byte[] arrby) {
        StringBuffer stringBuffer = new StringBuffer(arrby.length * 3);
        for (int i = 0; i < arrby.length; ++i) {
            stringBuffer.append(Character.forDigit(arrby[i] >>> 4 & 0xF, 16));
            stringBuffer.append(Character.forDigit(arrby[i] & 0xF, 16));
            stringBuffer.append(':');
        }
        stringBuffer.setLength(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }
}

