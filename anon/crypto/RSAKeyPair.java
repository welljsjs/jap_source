/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.MyRSAPrivateKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

public class RSAKeyPair
extends AsymmetricCryptoKeyPair {
    public static final int KEY_LENGTH_2048 = 2048;

    public RSAKeyPair(MyRSAPrivateKey myRSAPrivateKey) {
        super(myRSAPrivateKey);
    }

    public static RSAKeyPair getInstance(BigInteger bigInteger, SecureRandom secureRandom, int n, int n2) {
        RSAKeyPair rSAKeyPair;
        RSAKeyPairGenerator rSAKeyPairGenerator = new RSAKeyPairGenerator();
        RSAKeyGenerationParameters rSAKeyGenerationParameters = new RSAKeyGenerationParameters(bigInteger, secureRandom, n, n2);
        rSAKeyPairGenerator.init(rSAKeyGenerationParameters);
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = rSAKeyPairGenerator.generateKeyPair();
        try {
            rSAKeyPair = new RSAKeyPair(new MyRSAPrivateKey((RSAPrivateCrtKeyParameters)asymmetricCipherKeyPair.getPrivate()));
        }
        catch (Exception exception) {
            rSAKeyPair = null;
        }
        if (!AsymmetricCryptoKeyPair.isValidKeyPair(rSAKeyPair)) {
            return null;
        }
        return rSAKeyPair;
    }

    public static RSAKeyPair getInstance(SecureRandom secureRandom, int n, int n2) {
        return RSAKeyPair.getInstance(new BigInteger("65537"), secureRandom, n, n2);
    }
}

