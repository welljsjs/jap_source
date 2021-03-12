/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.math.ec.WNafUtil;

public class RSAKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private RSAKeyGenerationParameters param;

    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.param = (RSAKeyGenerationParameters)keyGenerationParameters;
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = null;
        boolean bl = false;
        int n = this.param.getStrength();
        int n2 = (n + 1) / 2;
        int n3 = n - n2;
        int n4 = n / 3;
        int n5 = n >> 2;
        BigInteger bigInteger = BigInteger.valueOf(2L).pow(n / 2);
        while (!bl) {
            BigInteger bigInteger2;
            BigInteger bigInteger3;
            BigInteger bigInteger4;
            BigInteger bigInteger5;
            BigInteger bigInteger6;
            BigInteger bigInteger7;
            BigInteger bigInteger8;
            BigInteger bigInteger9;
            BigInteger bigInteger10 = this.param.getPublicExponent();
            BigInteger bigInteger11 = this.chooseRandomPrime(n2, bigInteger10);
            while (true) {
                if ((bigInteger9 = (bigInteger8 = this.chooseRandomPrime(n3, bigInteger10)).subtract(bigInteger11).abs()).bitLength() < n4) {
                    continue;
                }
                bigInteger7 = bigInteger11.multiply(bigInteger8);
                if (bigInteger7.bitLength() != n) {
                    bigInteger11 = bigInteger11.max(bigInteger8);
                    continue;
                }
                if (WNafUtil.getNafWeight(bigInteger7) >= n5) break;
                bigInteger11 = this.chooseRandomPrime(n2, bigInteger10);
            }
            if (bigInteger11.compareTo(bigInteger8) < 0) {
                bigInteger6 = bigInteger11;
                bigInteger11 = bigInteger8;
                bigInteger8 = bigInteger6;
            }
            if ((bigInteger5 = bigInteger10.modInverse(bigInteger4 = (bigInteger3 = bigInteger11.subtract(ONE)).divide(bigInteger6 = bigInteger3.gcd(bigInteger2 = bigInteger8.subtract(ONE))).multiply(bigInteger2))).compareTo(bigInteger) <= 0) continue;
            bl = true;
            bigInteger9 = bigInteger5.remainder(bigInteger3);
            BigInteger bigInteger12 = bigInteger5.remainder(bigInteger2);
            BigInteger bigInteger13 = bigInteger8.modInverse(bigInteger11);
            asymmetricCipherKeyPair = new AsymmetricCipherKeyPair(new RSAKeyParameters(false, bigInteger7, bigInteger10), new RSAPrivateCrtKeyParameters(bigInteger7, bigInteger10, bigInteger5, bigInteger11, bigInteger8, bigInteger9, bigInteger12, bigInteger13));
        }
        return asymmetricCipherKeyPair;
    }

    protected BigInteger chooseRandomPrime(int n, BigInteger bigInteger) {
        BigInteger bigInteger2;
        while ((bigInteger2 = new BigInteger(n, 1, this.param.getRandom())).mod(bigInteger).equals(ONE) || !bigInteger2.isProbablePrime(this.param.getCertainty()) || !bigInteger.gcd(bigInteger2.subtract(ONE)).equals(ONE)) {
        }
        return bigInteger2;
    }
}

