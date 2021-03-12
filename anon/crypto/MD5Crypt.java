/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.ICrypt;
import anon.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MD5Crypt
implements ICrypt {
    private static final String magic = "$1$";
    private static final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static String cryptTo64(int n, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        while (--n2 >= 0) {
            stringBuffer.append(itoa64.substring(n & 0x3F, (n & 0x3F) + 1));
            n >>= 6;
        }
        return stringBuffer.toString();
    }

    public static String simpleHash(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes());
            byte[] arrby = messageDigest.digest();
            return Base64.encodeBytes(arrby);
        }
        catch (Exception exception) {
            return new String("");
        }
    }

    public final String crypt(String string) throws NoSuchAlgorithmException {
        StringBuffer stringBuffer = new StringBuffer();
        SecureRandom secureRandom = new SecureRandom();
        while (stringBuffer.length() < 8) {
            int n = (int)(secureRandom.nextFloat() * (float)itoa64.length());
            stringBuffer.append(itoa64.substring(n, n + 1));
        }
        return this.crypt(string, stringBuffer.toString());
    }

    public static final String getSalt(String string) {
        if (!string.startsWith(magic)) {
            return null;
        }
        String string2 = string.substring(magic.length(), string.length());
        int n = string2.indexOf("$");
        if (n >= 0 && string2.length() > n + 1) {
            return string2.substring(0, n);
        }
        return null;
    }

    public final boolean verify(String string, String string2) throws NoSuchAlgorithmException {
        String string3 = MD5Crypt.getSalt(string2);
        if (string3 != null) {
            return this.crypt(string, string3).equals(string2);
        }
        return MD5Crypt.simpleHash(string).equals(string2);
    }

    public final String crypt(String string, String string2) throws NoSuchAlgorithmException {
        int n;
        int n2;
        int n3;
        if (string2.startsWith(magic)) {
            string2 = string2.substring(magic.length());
        }
        if ((n3 = string2.indexOf(36)) != -1) {
            string2 = string2.substring(0, n3);
        }
        if (string2.length() > 8) {
            string2 = string2.substring(0, 8);
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(string.getBytes());
        messageDigest.update(magic.getBytes());
        messageDigest.update(string2.getBytes());
        MessageDigest messageDigest2 = MessageDigest.getInstance("MD5");
        messageDigest2.update(string.getBytes());
        messageDigest2.update(string2.getBytes());
        messageDigest2.update(string.getBytes());
        byte[] arrby = messageDigest2.digest();
        int n4 = arrby.length;
        for (int i = n2 = string.length(); i > 0; i -= n4) {
            messageDigest.update(arrby, 0, i > n4 ? n4 : i);
        }
        messageDigest2.reset();
        byte[] arrby2 = string.getBytes();
        for (int i = n2; i > 0; i >>= 1) {
            if ((i & 1) == 1) {
                messageDigest.update((byte)0);
                continue;
            }
            messageDigest.update(arrby2[0]);
        }
        StringBuffer stringBuffer = new StringBuffer(magic);
        stringBuffer.append(string2);
        stringBuffer.append("$");
        byte[] arrby3 = messageDigest.digest();
        byte[] arrby4 = string2.getBytes();
        for (n = 0; n < 1000; ++n) {
            messageDigest2.reset();
            if ((n & 1) == 1) {
                messageDigest2.update(arrby2);
            } else {
                messageDigest2.update(arrby3);
            }
            if (n % 3 != 0) {
                messageDigest2.update(arrby4);
            }
            if (n % 7 != 0) {
                messageDigest2.update(arrby2);
            }
            if ((n & 1) != 0) {
                messageDigest2.update(arrby3);
            } else {
                messageDigest2.update(arrby2);
            }
            arrby3 = messageDigest2.digest();
        }
        n = (arrby3[0] & 0xFF) << 16 | (arrby3[6] & 0xFF) << 8 | arrby3[12] & 0xFF;
        stringBuffer.append(MD5Crypt.cryptTo64(n, 4));
        n = (arrby3[1] & 0xFF) << 16 | (arrby3[7] & 0xFF) << 8 | arrby3[13] & 0xFF;
        stringBuffer.append(MD5Crypt.cryptTo64(n, 4));
        n = (arrby3[2] & 0xFF) << 16 | (arrby3[8] & 0xFF) << 8 | arrby3[14] & 0xFF;
        stringBuffer.append(MD5Crypt.cryptTo64(n, 4));
        n = (arrby3[3] & 0xFF) << 16 | (arrby3[9] & 0xFF) << 8 | arrby3[15] & 0xFF;
        stringBuffer.append(MD5Crypt.cryptTo64(n, 4));
        n = (arrby3[4] & 0xFF) << 16 | (arrby3[10] & 0xFF) << 8 | arrby3[5] & 0xFF;
        stringBuffer.append(MD5Crypt.cryptTo64(n, 4));
        n = arrby3[11] & 0xFF;
        stringBuffer.append(MD5Crypt.cryptTo64(n, 2));
        messageDigest = null;
        messageDigest2 = null;
        arrby3 = null;
        arrby = null;
        arrby2 = null;
        arrby4 = null;
        string2 = "";
        string = "";
        n2 = 0;
        return stringBuffer.toString();
    }
}

