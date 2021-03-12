/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.util;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class hash {
    public static byte[] sha(byte[] arrby) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        sHA1Digest.update(arrby, 0, arrby.length);
        byte[] arrby2 = new byte[sHA1Digest.getDigestSize()];
        sHA1Digest.doFinal(arrby2, 0);
        return arrby2;
    }

    public static byte[] sha(byte[] arrby, byte[] arrby2) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        sHA1Digest.reset();
        sHA1Digest.update(arrby, 0, arrby.length);
        sHA1Digest.update(arrby2, 0, arrby2.length);
        byte[] arrby3 = new byte[sHA1Digest.getDigestSize()];
        sHA1Digest.doFinal(arrby3, 0);
        return arrby3;
    }

    public static byte[] sha(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        sHA1Digest.reset();
        sHA1Digest.update(arrby, 0, arrby.length);
        sHA1Digest.update(arrby2, 0, arrby2.length);
        sHA1Digest.update(arrby3, 0, arrby3.length);
        byte[] arrby4 = new byte[sHA1Digest.getDigestSize()];
        sHA1Digest.doFinal(arrby4, 0);
        return arrby4;
    }

    public static byte[] md5(byte[] arrby) {
        MD5Digest mD5Digest = new MD5Digest();
        mD5Digest.reset();
        mD5Digest.update(arrby, 0, arrby.length);
        byte[] arrby2 = new byte[mD5Digest.getDigestSize()];
        mD5Digest.doFinal(arrby2, 0);
        return arrby2;
    }

    public static byte[] md5(byte[] arrby, byte[] arrby2) {
        MD5Digest mD5Digest = new MD5Digest();
        mD5Digest.reset();
        mD5Digest.update(arrby, 0, arrby.length);
        mD5Digest.update(arrby2, 0, arrby2.length);
        byte[] arrby3 = new byte[mD5Digest.getDigestSize()];
        mD5Digest.doFinal(arrby3, 0);
        return arrby3;
    }

    public static byte[] md5(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        MD5Digest mD5Digest = new MD5Digest();
        mD5Digest.reset();
        mD5Digest.update(arrby, 0, arrby.length);
        mD5Digest.update(arrby2, 0, arrby2.length);
        mD5Digest.update(arrby3, 0, arrby3.length);
        byte[] arrby4 = new byte[mD5Digest.getDigestSize()];
        mD5Digest.doFinal(arrby4, 0);
        return arrby4;
    }
}

