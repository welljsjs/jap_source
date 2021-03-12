/*
 * Decompiled with CFR 0.150.
 */
package anon.util.captcha;

import anon.util.captcha.MyImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class BinaryImageExtractor {
    public static MyImage binaryToImage(byte[] arrby) {
        MyImage myImage = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            int n = dataInputStream.readInt();
            int n2 = dataInputStream.readInt();
            if (n < 0 || n2 < 0 || arrby.length != 8 + (n2 * n + 7) / 8) {
                throw new Exception("BinaryImageExtractor: binaryToImage: The binary image has an invalid size.");
            }
            int[] arrn = new int[n * n2];
            int n3 = byteArrayInputStream.read();
            for (int i = 0; i < n * n2; ++i) {
                arrn[i] = (n3 & 0x80) == 128 ? -12566464 : -1;
                n3 <<= 1;
                if (i % 8 != 7) continue;
                n3 = byteArrayInputStream.read();
            }
            myImage = new MyImage(arrn, n, n2);
        }
        catch (Exception exception) {
            myImage = null;
        }
        return myImage;
    }
}

