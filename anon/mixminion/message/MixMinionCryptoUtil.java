/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.crypto.MyAES;
import anon.util.ByteArrayUtil;
import anon.util.ZLibTools;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class MixMinionCryptoUtil {
    public static byte[] randomArray(int n) {
        byte[] arrby = new byte[n];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(arrby);
        return arrby;
    }

    static byte[] xor(byte[] arrby, byte[] arrby2) {
        if (arrby.length != arrby2.length) {
            return null;
        }
        byte[] arrby3 = new byte[arrby.length];
        for (int i = 0; i < arrby.length; ++i) {
            arrby3[i] = (byte)(arrby[i] ^ arrby2[i]);
        }
        return arrby3;
    }

    public static byte[] hash(byte[] arrby) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        sHA1Digest.update(arrby, 0, arrby.length);
        byte[] arrby2 = new byte[sHA1Digest.getDigestSize()];
        sHA1Digest.doFinal(arrby2, 0);
        return arrby2;
    }

    public static Vector subVector(Vector vector, int n, int n2) {
        Vector vector2 = new Vector();
        for (int i = n; i < n + n2; ++i) {
            vector2.addElement(vector.elementAt(i));
        }
        return vector2;
    }

    public static byte[] Encrypt(byte[] arrby, byte[] arrby2) {
        return MixMinionCryptoUtil.xor(arrby2, MixMinionCryptoUtil.createPRNG(arrby, arrby2.length));
    }

    static byte[] createPRNG(byte[] arrby, int n) {
        MyAES myAES = new MyAES();
        byte[] arrby2 = new byte[n];
        byte[] arrby3 = new byte[16];
        byte[] arrby4 = new byte[16];
        try {
            myAES.init(true, arrby);
            int n2 = 0;
            while (n >= 16) {
                myAES.processBlockECB(arrby3, arrby4);
                System.arraycopy(arrby4, 0, arrby2, n2, 16);
                int n3 = 1;
                for (int i = arrby3.length - 1; i >= 0; --i) {
                    int n4 = (arrby3[i] & 0xFF) + n3;
                    n3 = n4 > 255 ? 1 : 0;
                    arrby3[i] = (byte)n4;
                }
                n -= 16;
                n2 += 16;
            }
            if (n > 0) {
                myAES.processBlockECB(arrby3, arrby4);
                System.arraycopy(arrby4, 0, arrby2, n2, n);
            }
        }
        catch (Exception exception) {
            System.out.println(exception);
            return null;
        }
        return arrby2;
    }

    public static byte[] SPRP_Encrypt(byte[] arrby, byte[] arrby2) {
        byte[] arrby3 = new byte[20];
        byte[] arrby4 = arrby;
        arrby3[19] = 1;
        byte[] arrby5 = MixMinionCryptoUtil.xor(arrby, arrby3);
        arrby3[19] = 2;
        byte[] arrby6 = MixMinionCryptoUtil.xor(arrby, arrby3);
        arrby3[19] = 3;
        byte[] arrby7 = MixMinionCryptoUtil.xor(arrby, arrby3);
        byte[] arrby8 = ByteArrayUtil.copy(arrby2, 0, 20);
        byte[] arrby9 = ByteArrayUtil.copy(arrby2, 20, arrby2.length - 20);
        arrby9 = MixMinionCryptoUtil.Encrypt(ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby4, arrby8, arrby4)), 0, 16), arrby9);
        arrby8 = MixMinionCryptoUtil.xor(arrby8, MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby5, arrby9, arrby5)));
        arrby9 = MixMinionCryptoUtil.Encrypt(ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby6, arrby8, arrby6)), 0, 16), arrby9);
        arrby8 = MixMinionCryptoUtil.xor(arrby8, MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby7, arrby9, arrby7)));
        return ByteArrayUtil.conc(arrby8, arrby9);
    }

    public static byte[] SPRP_Decrypt(byte[] arrby, byte[] arrby2) {
        byte[] arrby3 = new byte[20];
        byte[] arrby4 = arrby;
        arrby3[19] = 1;
        byte[] arrby5 = MixMinionCryptoUtil.xor(arrby, arrby3);
        arrby3[19] = 2;
        byte[] arrby6 = MixMinionCryptoUtil.xor(arrby, arrby3);
        arrby3[19] = 3;
        byte[] arrby7 = MixMinionCryptoUtil.xor(arrby, arrby3);
        byte[] arrby8 = ByteArrayUtil.copy(arrby2, 0, 20);
        byte[] arrby9 = ByteArrayUtil.copy(arrby2, 20, arrby2.length - 20);
        arrby8 = MixMinionCryptoUtil.xor(arrby8, MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby7, arrby9, arrby7)));
        arrby9 = MixMinionCryptoUtil.Encrypt(ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby6, arrby8, arrby6)), 0, 16), arrby9);
        arrby8 = MixMinionCryptoUtil.xor(arrby8, MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby5, arrby9, arrby5)));
        arrby9 = MixMinionCryptoUtil.Encrypt(ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby4, arrby8, arrby4)), 0, 16), arrby9);
        return ByteArrayUtil.conc(arrby8, arrby9);
    }

    static byte[] compressData(byte[] arrby) {
        byte[] arrby2 = ZLibTools.compress(arrby);
        if (arrby2[0] != 120 || arrby2[1] + 256 != 218) {
            throw new RuntimeException("The Compressed Messege didn't start with 0x78DA");
        }
        return arrby2;
    }

    static byte[] decompressData(byte[] arrby) {
        return ZLibTools.decompress(arrby);
    }

    private static byte[] ZIPcompressData(byte[] arrby) {
        byte[] arrby2 = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.setLevel(9);
            zipOutputStream.setMethod(8);
            ZipEntry zipEntry = new ZipEntry("MixMinionZip");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(arrby);
            zipOutputStream.flush();
            zipOutputStream.close();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        arrby2 = byteArrayOutputStream.toByteArray();
        if (!ByteArrayUtil.equal(arrby, MixMinionCryptoUtil.ZIPextractData(arrby2))) {
            throw new RuntimeException("Something with Compression/Decompression was wrong!");
        }
        return arrby2;
    }

    private static byte[] ZIPextractData(byte[] arrby) {
        byte[] arrby2 = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        try {
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            boolean bl = true;
            int n = -1;
            while (bl) {
                ++n;
                int n2 = zipInputStream.read();
                byte[] arrby3 = new byte[]{(byte)n2};
                if (n2 != -1) {
                    arrby2 = ByteArrayUtil.conc(arrby2, arrby3);
                    continue;
                }
                bl = false;
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return arrby2;
    }

    private static byte[] GZIPcompressData(byte[] arrby) {
        byte[] arrby2 = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(arrby);
            gZIPOutputStream.flush();
            gZIPOutputStream.close();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        arrby2 = byteArrayOutputStream.toByteArray();
        return arrby2;
    }

    private static byte[] GZIPextractData(byte[] arrby) {
        byte[] arrby2 = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        try {
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            boolean bl = true;
            int n = -1;
            while (bl) {
                ++n;
                int n2 = gZIPInputStream.read();
                byte[] arrby3 = new byte[]{(byte)n2};
                if (n2 != -1) {
                    arrby2 = ByteArrayUtil.conc(arrby2, arrby3);
                    continue;
                }
                bl = false;
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return arrby2;
    }
}

