/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.IMyPrivateKey;
import anon.crypto.MyECPrivateKey;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;

public class ECKeyPair
extends AsymmetricCryptoKeyPair {
    ECKeyPair(IMyPrivateKey iMyPrivateKey) {
        super(iMyPrivateKey);
    }

    public static ECKeyPair getInstance(SecureRandom secureRandom) {
        return ECKeyPair.getInstance(SECObjectIdentifiers.secp160r1, secureRandom);
    }

    public static ECKeyPair getInstance(ASN1ObjectIdentifier aSN1ObjectIdentifier, SecureRandom secureRandom) {
        ECKeyPair eCKeyPair;
        X9ECParameters x9ECParameters = SECNamedCurves.getByOID(aSN1ObjectIdentifier);
        if (x9ECParameters == null) {
            x9ECParameters = X962NamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = NISTNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = TeleTrusTNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            throw new IllegalArgumentException("Unknown Named Curve Identifier!");
        }
        ECDomainParameters eCDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
        ECKeyGenerationParameters eCKeyGenerationParameters = new ECKeyGenerationParameters(eCDomainParameters, secureRandom);
        ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
        eCKeyPairGenerator.init(eCKeyGenerationParameters);
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = eCKeyPairGenerator.generateKeyPair();
        try {
            eCKeyPair = new ECKeyPair(new MyECPrivateKey((ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate(), aSN1ObjectIdentifier));
        }
        catch (Exception exception) {
            return null;
        }
        if (!AsymmetricCryptoKeyPair.isValidKeyPair(eCKeyPair)) {
            return null;
        }
        return eCKeyPair;
    }
}

