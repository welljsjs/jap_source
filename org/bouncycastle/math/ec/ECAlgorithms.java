/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.WNafPreCompInfo;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.PolynomialExtensionField;

public class ECAlgorithms {
    public static boolean isF2mCurve(ECCurve eCCurve) {
        return ECAlgorithms.isF2mField(eCCurve.getField());
    }

    public static boolean isF2mField(FiniteField finiteField) {
        return finiteField.getDimension() > 1 && finiteField.getCharacteristic().equals(ECConstants.TWO) && finiteField instanceof PolynomialExtensionField;
    }

    public static boolean isFpCurve(ECCurve eCCurve) {
        return ECAlgorithms.isFpField(eCCurve.getField());
    }

    public static boolean isFpField(FiniteField finiteField) {
        return finiteField.getDimension() == 1;
    }

    public static ECPoint sumOfMultiplies(ECPoint[] arreCPoint, BigInteger[] arrbigInteger) {
        if (arreCPoint == null || arrbigInteger == null || arreCPoint.length != arrbigInteger.length || arreCPoint.length < 1) {
            throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length");
        }
        int n = arreCPoint.length;
        switch (n) {
            case 1: {
                return arreCPoint[0].multiply(arrbigInteger[0]);
            }
            case 2: {
                return ECAlgorithms.sumOfTwoMultiplies(arreCPoint[0], arrbigInteger[0], arreCPoint[1], arrbigInteger[1]);
            }
        }
        ECPoint eCPoint = arreCPoint[0];
        ECCurve eCCurve = eCPoint.getCurve();
        ECPoint[] arreCPoint2 = new ECPoint[n];
        arreCPoint2[0] = eCPoint;
        for (int i = 1; i < n; ++i) {
            arreCPoint2[i] = ECAlgorithms.importPoint(eCCurve, arreCPoint[i]);
        }
        ECEndomorphism eCEndomorphism = eCCurve.getEndomorphism();
        if (eCEndomorphism instanceof GLVEndomorphism) {
            return ECAlgorithms.validatePoint(ECAlgorithms.implSumOfMultipliesGLV(arreCPoint2, arrbigInteger, (GLVEndomorphism)eCEndomorphism));
        }
        return ECAlgorithms.validatePoint(ECAlgorithms.implSumOfMultiplies(arreCPoint2, arrbigInteger));
    }

    public static ECPoint sumOfTwoMultiplies(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        Object object;
        ECCurve eCCurve = eCPoint.getCurve();
        eCPoint2 = ECAlgorithms.importPoint(eCCurve, eCPoint2);
        if (eCCurve instanceof ECCurve.AbstractF2m && ((ECCurve.AbstractF2m)(object = (ECCurve.AbstractF2m)eCCurve)).isKoblitz()) {
            return ECAlgorithms.validatePoint(eCPoint.multiply(bigInteger).add(eCPoint2.multiply(bigInteger2)));
        }
        object = eCCurve.getEndomorphism();
        if (object instanceof GLVEndomorphism) {
            return ECAlgorithms.validatePoint(ECAlgorithms.implSumOfMultipliesGLV(new ECPoint[]{eCPoint, eCPoint2}, new BigInteger[]{bigInteger, bigInteger2}, (GLVEndomorphism)object));
        }
        return ECAlgorithms.validatePoint(ECAlgorithms.implShamirsTrickWNaf(eCPoint, bigInteger, eCPoint2, bigInteger2));
    }

    public static ECPoint shamirsTrick(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        ECCurve eCCurve = eCPoint.getCurve();
        eCPoint2 = ECAlgorithms.importPoint(eCCurve, eCPoint2);
        return ECAlgorithms.validatePoint(ECAlgorithms.implShamirsTrickJsf(eCPoint, bigInteger, eCPoint2, bigInteger2));
    }

    public static ECPoint importPoint(ECCurve eCCurve, ECPoint eCPoint) {
        ECCurve eCCurve2 = eCPoint.getCurve();
        if (!eCCurve.equals(eCCurve2)) {
            throw new IllegalArgumentException("Point must be on the same curve");
        }
        return eCCurve.importPoint(eCPoint);
    }

    public static void montgomeryTrick(ECFieldElement[] arreCFieldElement, int n, int n2) {
        ECAlgorithms.montgomeryTrick(arreCFieldElement, n, n2, null);
    }

    public static void montgomeryTrick(ECFieldElement[] arreCFieldElement, int n, int n2, ECFieldElement eCFieldElement) {
        ECFieldElement[] arreCFieldElement2 = new ECFieldElement[n2];
        arreCFieldElement2[0] = arreCFieldElement[n];
        int n3 = 0;
        while (++n3 < n2) {
            arreCFieldElement2[n3] = arreCFieldElement2[n3 - 1].multiply(arreCFieldElement[n + n3]);
        }
        --n3;
        if (eCFieldElement != null) {
            arreCFieldElement2[n3] = arreCFieldElement2[n3].multiply(eCFieldElement);
        }
        ECFieldElement eCFieldElement2 = arreCFieldElement2[n3].invert();
        while (n3 > 0) {
            int n4 = n + n3--;
            ECFieldElement eCFieldElement3 = arreCFieldElement[n4];
            arreCFieldElement[n4] = arreCFieldElement2[n3].multiply(eCFieldElement2);
            eCFieldElement2 = eCFieldElement2.multiply(eCFieldElement3);
        }
        arreCFieldElement[n] = eCFieldElement2;
    }

    public static ECPoint referenceMultiply(ECPoint eCPoint, BigInteger bigInteger) {
        BigInteger bigInteger2 = bigInteger.abs();
        ECPoint eCPoint2 = eCPoint.getCurve().getInfinity();
        int n = bigInteger2.bitLength();
        if (n > 0) {
            if (bigInteger2.testBit(0)) {
                eCPoint2 = eCPoint;
            }
            for (int i = 1; i < n; ++i) {
                eCPoint = eCPoint.twice();
                if (!bigInteger2.testBit(i)) continue;
                eCPoint2 = eCPoint2.add(eCPoint);
            }
        }
        return bigInteger.signum() < 0 ? eCPoint2.negate() : eCPoint2;
    }

    public static ECPoint validatePoint(ECPoint eCPoint) {
        if (!eCPoint.isValid()) {
            throw new IllegalArgumentException("Invalid point");
        }
        return eCPoint;
    }

    static ECPoint implShamirsTrickJsf(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        ECCurve eCCurve = eCPoint.getCurve();
        ECPoint eCPoint3 = eCCurve.getInfinity();
        ECPoint eCPoint4 = eCPoint.add(eCPoint2);
        ECPoint eCPoint5 = eCPoint.subtract(eCPoint2);
        ECPoint[] arreCPoint = new ECPoint[]{eCPoint2, eCPoint5, eCPoint, eCPoint4};
        eCCurve.normalizeAll(arreCPoint);
        ECPoint[] arreCPoint2 = new ECPoint[]{arreCPoint[3].negate(), arreCPoint[2].negate(), arreCPoint[1].negate(), arreCPoint[0].negate(), eCPoint3, arreCPoint[0], arreCPoint[1], arreCPoint[2], arreCPoint[3]};
        byte[] arrby = WNafUtil.generateJSF(bigInteger, bigInteger2);
        ECPoint eCPoint6 = eCPoint3;
        int n = arrby.length;
        while (--n >= 0) {
            byte by = arrby[n];
            int n2 = by << 24 >> 28;
            int n3 = by << 28 >> 28;
            int n4 = 4 + n2 * 3 + n3;
            eCPoint6 = eCPoint6.twicePlus(arreCPoint2[n4]);
        }
        return eCPoint6;
    }

    static ECPoint implShamirsTrickWNaf(ECPoint eCPoint, BigInteger bigInteger, ECPoint eCPoint2, BigInteger bigInteger2) {
        boolean bl = bigInteger.signum() < 0;
        boolean bl2 = bigInteger2.signum() < 0;
        bigInteger = bigInteger.abs();
        bigInteger2 = bigInteger2.abs();
        int n = Math.max(2, Math.min(16, WNafUtil.getWindowSize(bigInteger.bitLength())));
        int n2 = Math.max(2, Math.min(16, WNafUtil.getWindowSize(bigInteger2.bitLength())));
        WNafPreCompInfo wNafPreCompInfo = WNafUtil.precompute(eCPoint, n, true);
        WNafPreCompInfo wNafPreCompInfo2 = WNafUtil.precompute(eCPoint2, n2, true);
        ECPoint[] arreCPoint = bl ? wNafPreCompInfo.getPreCompNeg() : wNafPreCompInfo.getPreComp();
        ECPoint[] arreCPoint2 = bl2 ? wNafPreCompInfo2.getPreCompNeg() : wNafPreCompInfo2.getPreComp();
        ECPoint[] arreCPoint3 = bl ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg();
        ECPoint[] arreCPoint4 = bl2 ? wNafPreCompInfo2.getPreComp() : wNafPreCompInfo2.getPreCompNeg();
        byte[] arrby = WNafUtil.generateWindowNaf(n, bigInteger);
        byte[] arrby2 = WNafUtil.generateWindowNaf(n2, bigInteger2);
        return ECAlgorithms.implShamirsTrickWNaf(arreCPoint, arreCPoint3, arrby, arreCPoint2, arreCPoint4, arrby2);
    }

    static ECPoint implShamirsTrickWNaf(ECPoint eCPoint, BigInteger bigInteger, ECPointMap eCPointMap, BigInteger bigInteger2) {
        boolean bl = bigInteger.signum() < 0;
        boolean bl2 = bigInteger2.signum() < 0;
        bigInteger = bigInteger.abs();
        bigInteger2 = bigInteger2.abs();
        int n = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(bigInteger.bitLength(), bigInteger2.bitLength()))));
        ECPoint eCPoint2 = WNafUtil.mapPointWithPrecomp(eCPoint, n, true, eCPointMap);
        WNafPreCompInfo wNafPreCompInfo = WNafUtil.getWNafPreCompInfo(eCPoint);
        WNafPreCompInfo wNafPreCompInfo2 = WNafUtil.getWNafPreCompInfo(eCPoint2);
        ECPoint[] arreCPoint = bl ? wNafPreCompInfo.getPreCompNeg() : wNafPreCompInfo.getPreComp();
        ECPoint[] arreCPoint2 = bl2 ? wNafPreCompInfo2.getPreCompNeg() : wNafPreCompInfo2.getPreComp();
        ECPoint[] arreCPoint3 = bl ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg();
        ECPoint[] arreCPoint4 = bl2 ? wNafPreCompInfo2.getPreComp() : wNafPreCompInfo2.getPreCompNeg();
        byte[] arrby = WNafUtil.generateWindowNaf(n, bigInteger);
        byte[] arrby2 = WNafUtil.generateWindowNaf(n, bigInteger2);
        return ECAlgorithms.implShamirsTrickWNaf(arreCPoint, arreCPoint3, arrby, arreCPoint2, arreCPoint4, arrby2);
    }

    private static ECPoint implShamirsTrickWNaf(ECPoint[] arreCPoint, ECPoint[] arreCPoint2, byte[] arrby, ECPoint[] arreCPoint3, ECPoint[] arreCPoint4, byte[] arrby2) {
        ECPoint eCPoint;
        int n = Math.max(arrby.length, arrby2.length);
        ECCurve eCCurve = arreCPoint[0].getCurve();
        ECPoint eCPoint2 = eCPoint = eCCurve.getInfinity();
        int n2 = 0;
        for (int i = n - 1; i >= 0; --i) {
            ECPoint[] arreCPoint5;
            int n3;
            byte by;
            byte by2 = i < arrby.length ? arrby[i] : (byte)0;
            byte by3 = by = i < arrby2.length ? arrby2[i] : (byte)0;
            if ((by2 | by) == 0) {
                ++n2;
                continue;
            }
            ECPoint eCPoint3 = eCPoint;
            if (by2 != 0) {
                n3 = Math.abs(by2);
                arreCPoint5 = by2 < 0 ? arreCPoint2 : arreCPoint;
                eCPoint3 = eCPoint3.add(arreCPoint5[n3 >>> 1]);
            }
            if (by != 0) {
                n3 = Math.abs(by);
                arreCPoint5 = by < 0 ? arreCPoint4 : arreCPoint3;
                eCPoint3 = eCPoint3.add(arreCPoint5[n3 >>> 1]);
            }
            if (n2 > 0) {
                eCPoint2 = eCPoint2.timesPow2(n2);
                n2 = 0;
            }
            eCPoint2 = eCPoint2.twicePlus(eCPoint3);
        }
        if (n2 > 0) {
            eCPoint2 = eCPoint2.timesPow2(n2);
        }
        return eCPoint2;
    }

    static ECPoint implSumOfMultiplies(ECPoint[] arreCPoint, BigInteger[] arrbigInteger) {
        int n = arreCPoint.length;
        boolean[] arrbl = new boolean[n];
        WNafPreCompInfo[] arrwNafPreCompInfo = new WNafPreCompInfo[n];
        byte[][] arrarrby = new byte[n][];
        for (int i = 0; i < n; ++i) {
            BigInteger bigInteger = arrbigInteger[i];
            arrbl[i] = bigInteger.signum() < 0;
            bigInteger = bigInteger.abs();
            int n2 = Math.max(2, Math.min(16, WNafUtil.getWindowSize(bigInteger.bitLength())));
            arrwNafPreCompInfo[i] = WNafUtil.precompute(arreCPoint[i], n2, true);
            arrarrby[i] = WNafUtil.generateWindowNaf(n2, bigInteger);
        }
        return ECAlgorithms.implSumOfMultiplies(arrbl, arrwNafPreCompInfo, arrarrby);
    }

    static ECPoint implSumOfMultipliesGLV(ECPoint[] arreCPoint, BigInteger[] arrbigInteger, GLVEndomorphism gLVEndomorphism) {
        BigInteger bigInteger = arreCPoint[0].getCurve().getOrder();
        int n = arreCPoint.length;
        BigInteger[] arrbigInteger2 = new BigInteger[n << 1];
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            BigInteger[] arrbigInteger3 = gLVEndomorphism.decomposeScalar(arrbigInteger[i].mod(bigInteger));
            arrbigInteger2[n2++] = arrbigInteger3[0];
            arrbigInteger2[n2++] = arrbigInteger3[1];
        }
        ECPointMap eCPointMap = gLVEndomorphism.getPointMap();
        if (gLVEndomorphism.hasEfficientPointMap()) {
            return ECAlgorithms.implSumOfMultiplies(arreCPoint, eCPointMap, arrbigInteger2);
        }
        ECPoint[] arreCPoint2 = new ECPoint[n << 1];
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            ECPoint eCPoint = arreCPoint[i];
            ECPoint eCPoint2 = eCPointMap.map(eCPoint);
            arreCPoint2[n3++] = eCPoint;
            arreCPoint2[n3++] = eCPoint2;
        }
        return ECAlgorithms.implSumOfMultiplies(arreCPoint2, arrbigInteger2);
    }

    static ECPoint implSumOfMultiplies(ECPoint[] arreCPoint, ECPointMap eCPointMap, BigInteger[] arrbigInteger) {
        int n = arreCPoint.length;
        int n2 = n << 1;
        boolean[] arrbl = new boolean[n2];
        WNafPreCompInfo[] arrwNafPreCompInfo = new WNafPreCompInfo[n2];
        byte[][] arrarrby = new byte[n2][];
        for (int i = 0; i < n; ++i) {
            int n3 = i << 1;
            int n4 = n3 + 1;
            BigInteger bigInteger = arrbigInteger[n3];
            arrbl[n3] = bigInteger.signum() < 0;
            bigInteger = bigInteger.abs();
            BigInteger bigInteger2 = arrbigInteger[n4];
            arrbl[n4] = bigInteger2.signum() < 0;
            bigInteger2 = bigInteger2.abs();
            int n5 = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(bigInteger.bitLength(), bigInteger2.bitLength()))));
            ECPoint eCPoint = arreCPoint[i];
            ECPoint eCPoint2 = WNafUtil.mapPointWithPrecomp(eCPoint, n5, true, eCPointMap);
            arrwNafPreCompInfo[n3] = WNafUtil.getWNafPreCompInfo(eCPoint);
            arrwNafPreCompInfo[n4] = WNafUtil.getWNafPreCompInfo(eCPoint2);
            arrarrby[n3] = WNafUtil.generateWindowNaf(n5, bigInteger);
            arrarrby[n4] = WNafUtil.generateWindowNaf(n5, bigInteger2);
        }
        return ECAlgorithms.implSumOfMultiplies(arrbl, arrwNafPreCompInfo, arrarrby);
    }

    private static ECPoint implSumOfMultiplies(boolean[] arrbl, WNafPreCompInfo[] arrwNafPreCompInfo, byte[][] arrby) {
        ECPoint eCPoint;
        int n = 0;
        int n2 = arrby.length;
        for (int i = 0; i < n2; ++i) {
            n = Math.max(n, arrby[i].length);
        }
        ECCurve eCCurve = arrwNafPreCompInfo[0].getPreComp()[0].getCurve();
        ECPoint eCPoint2 = eCPoint = eCCurve.getInfinity();
        int n3 = 0;
        for (int i = n - 1; i >= 0; --i) {
            ECPoint eCPoint3 = eCPoint;
            for (int j = 0; j < n2; ++j) {
                byte by;
                byte[] arrby2 = arrby[j];
                byte by2 = by = i < arrby2.length ? arrby2[i] : (byte)0;
                if (by == 0) continue;
                int n4 = Math.abs(by);
                WNafPreCompInfo wNafPreCompInfo = arrwNafPreCompInfo[j];
                ECPoint[] arreCPoint = by < 0 == arrbl[j] ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg();
                eCPoint3 = eCPoint3.add(arreCPoint[n4 >>> 1]);
            }
            if (eCPoint3 == eCPoint) {
                ++n3;
                continue;
            }
            if (n3 > 0) {
                eCPoint2 = eCPoint2.timesPow2(n3);
                n3 = 0;
            }
            eCPoint2 = eCPoint2.twicePlus(eCPoint3);
        }
        if (n3 > 0) {
            eCPoint2 = eCPoint2.timesPow2(n3);
        }
        return eCPoint2;
    }
}

