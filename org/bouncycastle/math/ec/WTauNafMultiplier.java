/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECMultiplier;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.Tnaf;
import org.bouncycastle.math.ec.WTauNafPreCompInfo;
import org.bouncycastle.math.ec.ZTauElement;

public class WTauNafMultiplier
extends AbstractECMultiplier {
    static final String PRECOMP_NAME = "bc_wtnaf";

    protected ECPoint multiplyPositive(ECPoint eCPoint, BigInteger bigInteger) {
        if (!(eCPoint instanceof ECPoint.AbstractF2m)) {
            throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
        }
        ECPoint.AbstractF2m abstractF2m = (ECPoint.AbstractF2m)eCPoint;
        ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m)abstractF2m.getCurve();
        int n = abstractF2m2.getFieldSize();
        byte by = abstractF2m2.getA().toBigInteger().byteValue();
        byte by2 = Tnaf.getMu(by);
        BigInteger[] arrbigInteger = abstractF2m2.getSi();
        ZTauElement zTauElement = Tnaf.partModReduction(bigInteger, n, by, arrbigInteger, by2, (byte)10);
        return this.multiplyWTnaf(abstractF2m, zTauElement, abstractF2m2.getPreCompInfo(abstractF2m, PRECOMP_NAME), by, by2);
    }

    private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m abstractF2m, ZTauElement zTauElement, PreCompInfo preCompInfo, byte by, byte by2) {
        ZTauElement[] arrzTauElement = by == 0 ? Tnaf.alpha0 : Tnaf.alpha1;
        BigInteger bigInteger = Tnaf.getTw(by2, 4);
        byte[] arrby = Tnaf.tauAdicWNaf(by2, zTauElement, (byte)4, BigInteger.valueOf(16L), bigInteger, arrzTauElement);
        return WTauNafMultiplier.multiplyFromWTnaf(abstractF2m, arrby, preCompInfo);
    }

    private static ECPoint.AbstractF2m multiplyFromWTnaf(ECPoint.AbstractF2m abstractF2m, byte[] arrby, PreCompInfo preCompInfo) {
        ECPoint.AbstractF2m[] arrabstractF2m;
        ECPoint.AbstractF2m[] arrabstractF2m2;
        ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m)abstractF2m.getCurve();
        byte by = abstractF2m2.getA().toBigInteger().byteValue();
        if (preCompInfo == null || !(preCompInfo instanceof WTauNafPreCompInfo)) {
            arrabstractF2m2 = Tnaf.getPreComp(abstractF2m, by);
            arrabstractF2m = new WTauNafPreCompInfo();
            arrabstractF2m.setPreComp(arrabstractF2m2);
            abstractF2m2.setPreCompInfo(abstractF2m, PRECOMP_NAME, (PreCompInfo)arrabstractF2m);
        } else {
            arrabstractF2m2 = ((WTauNafPreCompInfo)preCompInfo).getPreComp();
        }
        arrabstractF2m = new ECPoint.AbstractF2m[arrabstractF2m2.length];
        for (int i = 0; i < arrabstractF2m2.length; ++i) {
            arrabstractF2m[i] = (ECPoint.AbstractF2m)arrabstractF2m2[i].negate();
        }
        ECPoint.AbstractF2m abstractF2m3 = (ECPoint.AbstractF2m)abstractF2m.getCurve().getInfinity();
        int n = 0;
        for (int i = arrby.length - 1; i >= 0; --i) {
            ++n;
            byte by2 = arrby[i];
            if (by2 == 0) continue;
            abstractF2m3 = abstractF2m3.tauPow(n);
            n = 0;
            ECPoint.AbstractF2m abstractF2m4 = by2 > 0 ? arrabstractF2m2[by2 >>> 1] : arrabstractF2m[-by2 >>> 1];
            abstractF2m3 = (ECPoint.AbstractF2m)abstractF2m3.add(abstractF2m4);
        }
        if (n > 0) {
            abstractF2m3 = abstractF2m3.tauPow(n);
        }
        return abstractF2m3;
    }
}

