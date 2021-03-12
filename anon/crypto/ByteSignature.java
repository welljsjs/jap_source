/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureVerificationAlgorithm;
import logging.LogHolder;
import logging.LogType;

public final class ByteSignature {
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ByteSignature() {
    }

    public static boolean verify(byte[] arrby, byte[] arrby2, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair) {
        return ByteSignature.verify(arrby, arrby2, asymmetricCryptoKeyPair.getPublic());
    }

    public static boolean verify(byte[] arrby, byte[] arrby2, IMyPublicKey iMyPublicKey) {
        if (iMyPublicKey == null) {
            LogHolder.log(7, LogType.CRYPTO, "key==null!");
            return false;
        }
        ISignatureVerificationAlgorithm iSignatureVerificationAlgorithm = iMyPublicKey.getSignatureAlgorithm();
        if (iSignatureVerificationAlgorithm == null) {
            LogHolder.log(2, LogType.CRYPTO, "Try to verify a message - unknown algorithm!");
            return false;
        }
        return iSignatureVerificationAlgorithm.verify(arrby, arrby2);
    }

    public static byte[] sign(byte[] arrby, AsymmetricCryptoKeyPair asymmetricCryptoKeyPair) {
        return ByteSignature.sign(arrby, asymmetricCryptoKeyPair.getPrivate());
    }

    public static byte[] sign(byte[] arrby, IMyPrivateKey iMyPrivateKey) {
        if (iMyPrivateKey == null) {
            return null;
        }
        return iMyPrivateKey.getSignatureAlgorithm().sign(arrby);
    }

    public static String toHexString(byte[] arrby) {
        int n;
        if (arrby == null || arrby.length == 0) {
            return "";
        }
        char[] arrc = new char[arrby.length * 3 - 1];
        int n2 = 0;
        for (int i = 0; i < arrby.length - 1; ++i) {
            n = 0xFF & arrby[i];
            arrc[n2++] = HEX_CHARS[n >> 4];
            arrc[n2++] = HEX_CHARS[n & 0xF];
            arrc[n2++] = 58;
        }
        n = 0xFF & arrby[arrby.length - 1];
        arrc[n2++] = HEX_CHARS[n >> 4];
        arrc[n2++] = HEX_CHARS[n & 0xF];
        return new String(arrc);
    }
}

