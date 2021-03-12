/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.MyDSAPrivateKey;
import java.security.SecureRandom;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;

public class DSAKeyPair
extends AsymmetricCryptoKeyPair {
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$security$SecureRandom;

    public DSAKeyPair(MyDSAPrivateKey myDSAPrivateKey) {
        super(myDSAPrivateKey);
    }

    public static DSAKeyPair getInstance(SecureRandom secureRandom, int n, int n2) {
        DSAKeyPair dSAKeyPair = DSAKeyPair.getInstanceJCE(secureRandom, n, n2);
        if (dSAKeyPair == null) {
            DSAParametersGenerator dSAParametersGenerator = new DSAParametersGenerator();
            dSAParametersGenerator.init(n, n2, secureRandom);
            DSAKeyPairGenerator dSAKeyPairGenerator = new DSAKeyPairGenerator();
            dSAKeyPairGenerator.init(new DSAKeyGenerationParameters(secureRandom, dSAParametersGenerator.generateParameters()));
            AsymmetricCipherKeyPair asymmetricCipherKeyPair = dSAKeyPairGenerator.generateKeyPair();
            try {
                dSAKeyPair = new DSAKeyPair(new MyDSAPrivateKey((DSAPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate()));
            }
            catch (Exception exception) {
                dSAKeyPair = null;
            }
        }
        if (!AsymmetricCryptoKeyPair.isValidKeyPair(dSAKeyPair)) {
            return null;
        }
        return dSAKeyPair;
    }

    private static DSAKeyPair getInstanceJCE(SecureRandom secureRandom, int n, int n2) {
        DSAKeyPair dSAKeyPair;
        try {
            Class<?> class_ = Class.forName("java.security.KeyPairGenerator");
            Class<?> class_2 = Class.forName("java.security.KeyPair");
            Class<?> class_3 = Class.forName("org.bouncycastle.jce.provider.DSAUtil");
            Class<?> class_4 = Class.forName("java.security.PrivateKey");
            Object object = class_.getMethod("getInstance", class$java$lang$String == null ? (class$java$lang$String = DSAKeyPair.class$("java.lang.String")) : class$java$lang$String).invoke(class_, "DSA");
            class_.getMethod("initialize", Integer.TYPE, class$java$security$SecureRandom == null ? (class$java$security$SecureRandom = DSAKeyPair.class$("java.security.SecureRandom")) : class$java$security$SecureRandom).invoke(object, new Integer(n), secureRandom);
            Object object2 = class_.getMethod("generateKeyPair", null).invoke(object, null);
            Object object3 = class_2.getMethod("getPrivate", null).invoke(object2, null);
            DSAPrivateKeyParameters dSAPrivateKeyParameters = (DSAPrivateKeyParameters)class_3.getMethod("generatePrivateKeyParameter", class_4).invoke(class_3, object3);
            try {
                dSAKeyPair = new DSAKeyPair(new MyDSAPrivateKey(dSAPrivateKeyParameters));
                LogHolder.log(6, LogType.CRYPTO, "Used JCE for creating DSA key pair.");
            }
            catch (Exception exception) {
                dSAKeyPair = null;
            }
            if (dSAKeyPair != null && !AsymmetricCryptoKeyPair.isValidKeyPair(dSAKeyPair)) {
                LogHolder.log(3, LogType.CRYPTO, "Created illegal DSA certificate with JCE!");
                dSAKeyPair = null;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            LogHolder.log(7, LogType.CRYPTO, "Optional class was not loaded: " + classNotFoundException.getMessage());
            dSAKeyPair = null;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            LogHolder.log(7, LogType.CRYPTO, noSuchMethodException);
            dSAKeyPair = null;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.CRYPTO, "Could not create DSA certificate with JCE!", exception);
            dSAKeyPair = null;
        }
        return dSAKeyPair;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

