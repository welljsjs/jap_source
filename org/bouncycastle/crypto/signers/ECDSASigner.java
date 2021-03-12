/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.RandomDSAKCalculator;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECDSASigner
implements ECConstants,
DSA {
    private final DSAKCalculator kCalculator;
    private ECKeyParameters key;
    private SecureRandom random;

    public ECDSASigner() {
        this.kCalculator = new RandomDSAKCalculator();
    }

    public ECDSASigner(DSAKCalculator dSAKCalculator) {
        this.kCalculator = dSAKCalculator;
    }

    public void init(boolean bl, CipherParameters cipherParameters) {
        SecureRandom secureRandom = null;
        if (bl) {
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.key = (ECPrivateKeyParameters)parametersWithRandom.getParameters();
                secureRandom = parametersWithRandom.getRandom();
            } else {
                this.key = (ECPrivateKeyParameters)cipherParameters;
            }
        } else {
            this.key = (ECPublicKeyParameters)cipherParameters;
        }
        this.random = this.initSecureRandom(bl && !this.kCalculator.isDeterministic(), secureRandom);
    }

    public BigInteger[] generateSignature(byte[] arrby) {
        BigInteger bigInteger;
        BigInteger bigInteger2;
        ECPoint eCPoint;
        BigInteger bigInteger3;
        ECDomainParameters eCDomainParameters = this.key.getParameters();
        BigInteger bigInteger4 = eCDomainParameters.getN();
        BigInteger bigInteger5 = this.calculateE(bigInteger4, arrby);
        BigInteger bigInteger6 = ((ECPrivateKeyParameters)this.key).getD();
        if (this.kCalculator.isDeterministic()) {
            this.kCalculator.init(bigInteger4, bigInteger6, arrby);
        } else {
            this.kCalculator.init(bigInteger4, this.random);
        }
        ECMultiplier eCMultiplier = this.createBasePointMultiplier();
        do {
            bigInteger2 = this.kCalculator.nextK();
        } while ((bigInteger3 = (eCPoint = eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger2).normalize()).getAffineXCoord().toBigInteger().mod(bigInteger4)).equals(ECConstants.ZERO) || (bigInteger = bigInteger2.modInverse(bigInteger4).multiply(bigInteger5.add(bigInteger6.multiply(bigInteger3))).mod(bigInteger4)).equals(ECConstants.ZERO));
        return new BigInteger[]{bigInteger3, bigInteger};
    }

    public boolean verifySignature(byte[] arrby, BigInteger bigInteger, BigInteger bigInteger2) {
        ECPoint eCPoint;
        ECDomainParameters eCDomainParameters = this.key.getParameters();
        BigInteger bigInteger3 = eCDomainParameters.getN();
        BigInteger bigInteger4 = this.calculateE(bigInteger3, arrby);
        if (bigInteger.compareTo(ECConstants.ONE) < 0 || bigInteger.compareTo(bigInteger3) >= 0) {
            return false;
        }
        if (bigInteger2.compareTo(ECConstants.ONE) < 0 || bigInteger2.compareTo(bigInteger3) >= 0) {
            return false;
        }
        BigInteger bigInteger5 = bigInteger2.modInverse(bigInteger3);
        BigInteger bigInteger6 = bigInteger4.multiply(bigInteger5).mod(bigInteger3);
        BigInteger bigInteger7 = bigInteger.multiply(bigInteger5).mod(bigInteger3);
        ECPoint eCPoint2 = eCDomainParameters.getG();
        ECPoint eCPoint3 = ECAlgorithms.sumOfTwoMultiplies(eCPoint2, bigInteger6, eCPoint = ((ECPublicKeyParameters)this.key).getQ(), bigInteger7).normalize();
        if (eCPoint3.isInfinity()) {
            return false;
        }
        BigInteger bigInteger8 = eCPoint3.getAffineXCoord().toBigInteger().mod(bigInteger3);
        return bigInteger8.equals(bigInteger);
    }

    protected BigInteger calculateE(BigInteger bigInteger, byte[] arrby) {
        int n = bigInteger.bitLength();
        int n2 = arrby.length * 8;
        BigInteger bigInteger2 = new BigInteger(1, arrby);
        if (n < n2) {
            bigInteger2 = bigInteger2.shiftRight(n2 - n);
        }
        return bigInteger2;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected SecureRandom initSecureRandom(boolean bl, SecureRandom secureRandom) {
        return !bl ? null : (secureRandom != null ? secureRandom : new SecureRandom());
    }
}

