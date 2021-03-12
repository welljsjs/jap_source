/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.ByteSignature;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyECPrivateKey;
import anon.crypto.MyRSAPrivateKey;
import anon.util.ClassUtil;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class AsymmetricCryptoKeyPair {
    public static final int KEY_LENGTH_512 = 512;
    public static final int KEY_LENGTH_1024 = 1024;
    private static final MyDSAPrivateKey dsaKey = null;
    private static final MyRSAPrivateKey rsaKey = null;
    private static final MyECPrivateKey ecKey = null;
    private static Vector ms_privateKeyClasses;
    private static Vector ms_publicKeyClasses;
    private IMyPrivateKey m_privateKey;
    private IMyPublicKey m_publicKey;
    static /* synthetic */ Class class$anon$crypto$IMyPrivateKey;
    static /* synthetic */ Class class$anon$crypto$MyDSAPrivateKey;
    static /* synthetic */ Class class$anon$crypto$MyRSAPrivateKey;
    static /* synthetic */ Class class$anon$crypto$IMyPublicKey;
    static /* synthetic */ Class class$anon$crypto$MyDSAPublicKey;
    static /* synthetic */ Class class$anon$crypto$MyRSAPublicKey;

    public AsymmetricCryptoKeyPair(IMyPrivateKey iMyPrivateKey) {
        this.m_privateKey = iMyPrivateKey;
        this.m_publicKey = iMyPrivateKey.createPublicKey();
    }

    public AsymmetricCryptoKeyPair(PrivateKeyInfo privateKeyInfo) throws InvalidKeyException {
        IMyPrivateKey iMyPrivateKey;
        try {
            iMyPrivateKey = (IMyPrivateKey)AsymmetricCryptoKeyPair.createAsymmetricCryptoKey(privateKeyInfo, AsymmetricCryptoKeyPair.getPrivateKeyClasses());
        }
        catch (ClassCastException classCastException) {
            throw new InvalidKeyException("The key that was created was no private key!");
        }
        this.m_privateKey = iMyPrivateKey;
        this.m_publicKey = iMyPrivateKey.createPublicKey();
    }

    public static final IMyPublicKey createPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws InvalidKeyException {
        IMyPublicKey iMyPublicKey;
        try {
            iMyPublicKey = (IMyPublicKey)AsymmetricCryptoKeyPair.createAsymmetricCryptoKey(subjectPublicKeyInfo, AsymmetricCryptoKeyPair.getPublicKeyClasses());
        }
        catch (ClassCastException classCastException) {
            throw new InvalidKeyException("The key that was created was no public key!");
        }
        return iMyPublicKey;
    }

    public final IMyPrivateKey getPrivate() {
        return this.m_privateKey;
    }

    public final IMyPublicKey getPublic() {
        return this.m_publicKey;
    }

    protected static final boolean isValidKeyPair(AsymmetricCryptoKeyPair asymmetricCryptoKeyPair) {
        if (asymmetricCryptoKeyPair == null) {
            return false;
        }
        Random random = new Random();
        random.setSeed(0L);
        byte[] arrby = new byte[1024];
        random.nextBytes(arrby);
        byte[] arrby2 = ByteSignature.sign(arrby, asymmetricCryptoKeyPair);
        if (arrby2 == null) {
            return false;
        }
        byte[] arrby3 = new byte[arrby2.length - 1];
        random.nextBytes(arrby3);
        try {
            if (ByteSignature.verify(arrby, arrby3, asymmetricCryptoKeyPair)) {
                return false;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return ByteSignature.verify(arrby, arrby2, asymmetricCryptoKeyPair);
    }

    private static Key createAsymmetricCryptoKey(Object object, Enumeration enumeration) throws InvalidKeyException {
        Key key = null;
        Class[] arrclass = new Class[1];
        Object[] arrobject = new Object[1];
        arrclass[0] = object.getClass();
        arrobject[0] = object;
        while (key == null && enumeration.hasMoreElements()) {
            Class class_ = (Class)enumeration.nextElement();
            try {
                key = (Key)class_.getConstructor(arrclass).newInstance(arrobject);
            }
            catch (Throwable throwable) {}
        }
        if (key == null) {
            throw new InvalidKeyException("No key available for this key info!");
        }
        return key;
    }

    private static Enumeration getPrivateKeyClasses() {
        if (ms_privateKeyClasses == null) {
            try {
                ms_privateKeyClasses = ClassUtil.findSubclasses(class$anon$crypto$IMyPrivateKey == null ? (class$anon$crypto$IMyPrivateKey = AsymmetricCryptoKeyPair.class$("anon.crypto.IMyPrivateKey")) : class$anon$crypto$IMyPrivateKey);
                ms_privateKeyClasses.removeElement(class$anon$crypto$IMyPrivateKey == null ? (class$anon$crypto$IMyPrivateKey = AsymmetricCryptoKeyPair.class$("anon.crypto.IMyPrivateKey")) : class$anon$crypto$IMyPrivateKey);
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.CRYPTO, throwable);
            }
            if (ms_privateKeyClasses == null) {
                ms_privateKeyClasses = new Vector();
            }
            if (ms_privateKeyClasses.size() < 2) {
                LogHolder.log(2, LogType.CRYPTO, "Private key classes have not been loaded automatically!");
                ms_privateKeyClasses.removeElement(class$anon$crypto$MyDSAPrivateKey == null ? (class$anon$crypto$MyDSAPrivateKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyDSAPrivateKey")) : class$anon$crypto$MyDSAPrivateKey);
                ms_privateKeyClasses.removeElement(class$anon$crypto$MyRSAPrivateKey == null ? (class$anon$crypto$MyRSAPrivateKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyRSAPrivateKey")) : class$anon$crypto$MyRSAPrivateKey);
                ms_privateKeyClasses.addElement(class$anon$crypto$MyDSAPrivateKey == null ? (class$anon$crypto$MyDSAPrivateKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyDSAPrivateKey")) : class$anon$crypto$MyDSAPrivateKey);
                ms_privateKeyClasses.addElement(class$anon$crypto$MyRSAPrivateKey == null ? (class$anon$crypto$MyRSAPrivateKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyRSAPrivateKey")) : class$anon$crypto$MyRSAPrivateKey);
            }
        }
        return ms_privateKeyClasses.elements();
    }

    private static Enumeration getPublicKeyClasses() {
        if (ms_publicKeyClasses == null) {
            try {
                ms_publicKeyClasses = ClassUtil.findSubclasses(class$anon$crypto$IMyPublicKey == null ? (class$anon$crypto$IMyPublicKey = AsymmetricCryptoKeyPair.class$("anon.crypto.IMyPublicKey")) : class$anon$crypto$IMyPublicKey);
                ms_publicKeyClasses.removeElement(class$anon$crypto$IMyPublicKey == null ? (class$anon$crypto$IMyPublicKey = AsymmetricCryptoKeyPair.class$("anon.crypto.IMyPublicKey")) : class$anon$crypto$IMyPublicKey);
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.CRYPTO, throwable);
            }
            if (ms_publicKeyClasses == null) {
                ms_publicKeyClasses = new Vector();
            }
            if (ms_publicKeyClasses.size() < 2) {
                int n = ClassUtil.isFindSubclassesEnabled() ? 2 : 5;
                LogHolder.log(n, LogType.CRYPTO, "Public key classes have not been loaded automatically.");
                ms_publicKeyClasses.removeElement(class$anon$crypto$MyDSAPublicKey == null ? (class$anon$crypto$MyDSAPublicKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyDSAPublicKey")) : class$anon$crypto$MyDSAPublicKey);
                ms_publicKeyClasses.removeElement(class$anon$crypto$MyRSAPublicKey == null ? (class$anon$crypto$MyRSAPublicKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyRSAPublicKey")) : class$anon$crypto$MyRSAPublicKey);
                ms_publicKeyClasses.addElement(class$anon$crypto$MyDSAPublicKey == null ? (class$anon$crypto$MyDSAPublicKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyDSAPublicKey")) : class$anon$crypto$MyDSAPublicKey);
                ms_publicKeyClasses.addElement(class$anon$crypto$MyRSAPublicKey == null ? (class$anon$crypto$MyRSAPublicKey = AsymmetricCryptoKeyPair.class$("anon.crypto.MyRSAPublicKey")) : class$anon$crypto$MyRSAPublicKey);
            }
        }
        return ms_publicKeyClasses.elements();
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

