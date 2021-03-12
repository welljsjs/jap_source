/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointPreCompInfo;
import org.bouncycastle.math.ec.PreCompInfo;

public class FixedPointUtil {
    public static final String PRECOMP_NAME = "bc_fixed_point";

    public static int getCombSize(ECCurve eCCurve) {
        BigInteger bigInteger = eCCurve.getOrder();
        return bigInteger == null ? eCCurve.getFieldSize() + 1 : bigInteger.bitLength();
    }

    public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo != null && preCompInfo instanceof FixedPointPreCompInfo) {
            return (FixedPointPreCompInfo)preCompInfo;
        }
        return new FixedPointPreCompInfo();
    }

    public static FixedPointPreCompInfo precompute(ECPoint eCPoint, int n) {
        ECCurve eCCurve = eCPoint.getCurve();
        int n2 = 1 << n;
        FixedPointPreCompInfo fixedPointPreCompInfo = FixedPointUtil.getFixedPointPreCompInfo(eCCurve.getPreCompInfo(eCPoint, PRECOMP_NAME));
        ECPoint[] arreCPoint = fixedPointPreCompInfo.getPreComp();
        if (arreCPoint == null || arreCPoint.length < n2) {
            int n3;
            int n4 = FixedPointUtil.getCombSize(eCCurve);
            int n5 = (n4 + n - 1) / n;
            ECPoint[] arreCPoint2 = new ECPoint[n];
            arreCPoint2[0] = eCPoint;
            for (n3 = 1; n3 < n; ++n3) {
                arreCPoint2[n3] = arreCPoint2[n3 - 1].timesPow2(n5);
            }
            eCCurve.normalizeAll(arreCPoint2);
            arreCPoint = new ECPoint[n2];
            arreCPoint[0] = eCCurve.getInfinity();
            for (n3 = n - 1; n3 >= 0; --n3) {
                int n6;
                ECPoint eCPoint2 = arreCPoint2[n3];
                for (int i = n6 = 1 << n3; i < n2; i += n6 << 1) {
                    arreCPoint[i] = arreCPoint[i - n6].add(eCPoint2);
                }
            }
            eCCurve.normalizeAll(arreCPoint);
            fixedPointPreCompInfo.setPreComp(arreCPoint);
            fixedPointPreCompInfo.setWidth(n);
            eCCurve.setPreCompInfo(eCPoint, PRECOMP_NAME, fixedPointPreCompInfo);
        }
        return fixedPointPreCompInfo;
    }
}

