/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.WNafPreCompInfo;

public abstract class WNafUtil {
    public static final String PRECOMP_NAME = "bc_wnaf";
    private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS = new int[]{13, 41, 121, 337, 897, 2305};
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final int[] EMPTY_INTS = new int[0];
    private static final ECPoint[] EMPTY_POINTS = new ECPoint[0];

    public static int[] generateCompactNaf(BigInteger bigInteger) {
        if (bigInteger.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (bigInteger.signum() == 0) {
            return EMPTY_INTS;
        }
        BigInteger bigInteger2 = bigInteger.shiftLeft(1).add(bigInteger);
        int n = bigInteger2.bitLength();
        int[] arrn = new int[n >> 1];
        BigInteger bigInteger3 = bigInteger2.xor(bigInteger);
        int n2 = n - 1;
        int n3 = 0;
        int n4 = 0;
        for (int i = 1; i < n2; ++i) {
            if (!bigInteger3.testBit(i)) {
                ++n4;
                continue;
            }
            int n5 = bigInteger.testBit(i) ? -1 : 1;
            arrn[n3++] = n5 << 16 | n4;
            n4 = 1;
            ++i;
        }
        arrn[n3++] = 0x10000 | n4;
        if (arrn.length > n3) {
            arrn = WNafUtil.trim(arrn, n3);
        }
        return arrn;
    }

    public static int[] generateCompactWindowNaf(int n, BigInteger bigInteger) {
        if (n == 2) {
            return WNafUtil.generateCompactNaf(bigInteger);
        }
        if (n < 2 || n > 16) {
            throw new IllegalArgumentException("'width' must be in the range [2, 16]");
        }
        if (bigInteger.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (bigInteger.signum() == 0) {
            return EMPTY_INTS;
        }
        int[] arrn = new int[bigInteger.bitLength() / n + 1];
        int n2 = 1 << n;
        int n3 = n2 - 1;
        int n4 = n2 >>> 1;
        boolean bl = false;
        int n5 = 0;
        int n6 = 0;
        while (n6 <= bigInteger.bitLength()) {
            if (bigInteger.testBit(n6) == bl) {
                ++n6;
                continue;
            }
            bigInteger = bigInteger.shiftRight(n6);
            int n7 = bigInteger.intValue() & n3;
            if (bl) {
                ++n7;
            }
            boolean bl2 = bl = (n7 & n4) != 0;
            if (bl) {
                n7 -= n2;
            }
            int n8 = n5 > 0 ? n6 - 1 : n6;
            arrn[n5++] = n7 << 16 | n8;
            n6 = n;
        }
        if (arrn.length > n5) {
            arrn = WNafUtil.trim(arrn, n5);
        }
        return arrn;
    }

    public static byte[] generateJSF(BigInteger bigInteger, BigInteger bigInteger2) {
        int n = Math.max(bigInteger.bitLength(), bigInteger2.bitLength()) + 1;
        byte[] arrby = new byte[n];
        BigInteger bigInteger3 = bigInteger;
        BigInteger bigInteger4 = bigInteger2;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        while (n3 | n4 || bigInteger3.bitLength() > n5 || bigInteger4.bitLength() > n5) {
            int n6;
            int n7 = (bigInteger3.intValue() >>> n5) + n3 & 7;
            int n8 = (bigInteger4.intValue() >>> n5) + n4 & 7;
            int n9 = n7 & 1;
            if (n9 != 0 && n7 + (n9 -= n7 & 2) == 4 && (n8 & 3) == 2) {
                n9 = -n9;
            }
            if ((n6 = n8 & 1) != 0 && n8 + (n6 -= n8 & 2) == 4 && (n7 & 3) == 2) {
                n6 = -n6;
            }
            if (n3 << 1 == 1 + n9) {
                n3 ^= 1;
            }
            if (n4 << 1 == 1 + n6) {
                n4 ^= 1;
            }
            if (++n5 == 30) {
                n5 = 0;
                bigInteger3 = bigInteger3.shiftRight(30);
                bigInteger4 = bigInteger4.shiftRight(30);
            }
            arrby[n2++] = (byte)(n9 << 4 | n6 & 0xF);
        }
        if (arrby.length > n2) {
            arrby = WNafUtil.trim(arrby, n2);
        }
        return arrby;
    }

    public static byte[] generateNaf(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return EMPTY_BYTES;
        }
        BigInteger bigInteger2 = bigInteger.shiftLeft(1).add(bigInteger);
        int n = bigInteger2.bitLength() - 1;
        byte[] arrby = new byte[n];
        BigInteger bigInteger3 = bigInteger2.xor(bigInteger);
        for (int i = 1; i < n; ++i) {
            if (!bigInteger3.testBit(i)) continue;
            arrby[i - 1] = (byte)(bigInteger.testBit(i) ? -1 : 1);
            ++i;
        }
        arrby[n - 1] = 1;
        return arrby;
    }

    public static byte[] generateWindowNaf(int n, BigInteger bigInteger) {
        if (n == 2) {
            return WNafUtil.generateNaf(bigInteger);
        }
        if (n < 2 || n > 8) {
            throw new IllegalArgumentException("'width' must be in the range [2, 8]");
        }
        if (bigInteger.signum() == 0) {
            return EMPTY_BYTES;
        }
        byte[] arrby = new byte[bigInteger.bitLength() + 1];
        int n2 = 1 << n;
        int n3 = n2 - 1;
        int n4 = n2 >>> 1;
        boolean bl = false;
        int n5 = 0;
        int n6 = 0;
        while (n6 <= bigInteger.bitLength()) {
            if (bigInteger.testBit(n6) == bl) {
                ++n6;
                continue;
            }
            bigInteger = bigInteger.shiftRight(n6);
            int n7 = bigInteger.intValue() & n3;
            if (bl) {
                ++n7;
            }
            boolean bl2 = bl = (n7 & n4) != 0;
            if (bl) {
                n7 -= n2;
            }
            n5 += n5 > 0 ? n6 - 1 : n6;
            arrby[n5++] = (byte)n7;
            n6 = n;
        }
        if (arrby.length > n5) {
            arrby = WNafUtil.trim(arrby, n5);
        }
        return arrby;
    }

    public static int getNafWeight(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return 0;
        }
        BigInteger bigInteger2 = bigInteger.shiftLeft(1).add(bigInteger);
        BigInteger bigInteger3 = bigInteger2.xor(bigInteger);
        return bigInteger3.bitCount();
    }

    public static WNafPreCompInfo getWNafPreCompInfo(ECPoint eCPoint) {
        return WNafUtil.getWNafPreCompInfo(eCPoint.getCurve().getPreCompInfo(eCPoint, PRECOMP_NAME));
    }

    public static WNafPreCompInfo getWNafPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo != null && preCompInfo instanceof WNafPreCompInfo) {
            return (WNafPreCompInfo)preCompInfo;
        }
        return new WNafPreCompInfo();
    }

    public static int getWindowSize(int n) {
        return WNafUtil.getWindowSize(n, DEFAULT_WINDOW_SIZE_CUTOFFS);
    }

    public static int getWindowSize(int n, int[] arrn) {
        int n2;
        for (n2 = 0; n2 < arrn.length && n >= arrn[n2]; ++n2) {
        }
        return n2 + 2;
    }

    public static ECPoint mapPointWithPrecomp(ECPoint eCPoint, int n, boolean bl, ECPointMap eCPointMap) {
        Object object;
        ECCurve eCCurve = eCPoint.getCurve();
        WNafPreCompInfo wNafPreCompInfo = WNafUtil.precompute(eCPoint, n, bl);
        ECPoint eCPoint2 = eCPointMap.map(eCPoint);
        WNafPreCompInfo wNafPreCompInfo2 = WNafUtil.getWNafPreCompInfo(eCCurve.getPreCompInfo(eCPoint2, PRECOMP_NAME));
        ECPoint eCPoint3 = wNafPreCompInfo.getTwice();
        if (eCPoint3 != null) {
            object = eCPointMap.map(eCPoint3);
            wNafPreCompInfo2.setTwice((ECPoint)object);
        }
        object = wNafPreCompInfo.getPreComp();
        ECPoint[] arreCPoint = new ECPoint[((ECPoint[])object).length];
        for (int i = 0; i < ((ECPoint[])object).length; ++i) {
            arreCPoint[i] = eCPointMap.map(object[i]);
        }
        wNafPreCompInfo2.setPreComp(arreCPoint);
        if (bl) {
            ECPoint[] arreCPoint2 = new ECPoint[arreCPoint.length];
            for (int i = 0; i < arreCPoint2.length; ++i) {
                arreCPoint2[i] = arreCPoint[i].negate();
            }
            wNafPreCompInfo2.setPreCompNeg(arreCPoint2);
        }
        eCCurve.setPreCompInfo(eCPoint2, PRECOMP_NAME, wNafPreCompInfo2);
        return eCPoint2;
    }

    public static WNafPreCompInfo precompute(ECPoint eCPoint, int n, boolean bl) {
        ECCurve eCCurve = eCPoint.getCurve();
        WNafPreCompInfo wNafPreCompInfo = WNafUtil.getWNafPreCompInfo(eCCurve.getPreCompInfo(eCPoint, PRECOMP_NAME));
        int n2 = 0;
        int n3 = 1 << Math.max(0, n - 2);
        ECPoint[] arreCPoint = wNafPreCompInfo.getPreComp();
        if (arreCPoint == null) {
            arreCPoint = EMPTY_POINTS;
        } else {
            n2 = arreCPoint.length;
        }
        if (n2 < n3) {
            arreCPoint = WNafUtil.resizeTable(arreCPoint, n3);
            if (n3 == 1) {
                arreCPoint[0] = eCPoint.normalize();
            } else {
                int n4 = n2;
                if (n4 == 0) {
                    arreCPoint[0] = eCPoint;
                    n4 = 1;
                }
                ECFieldElement eCFieldElement = null;
                if (n3 == 2) {
                    arreCPoint[1] = eCPoint.threeTimes();
                } else {
                    ECPoint eCPoint2 = wNafPreCompInfo.getTwice();
                    ECPoint eCPoint3 = arreCPoint[n4 - 1];
                    if (eCPoint2 == null) {
                        eCPoint2 = arreCPoint[0].twice();
                        wNafPreCompInfo.setTwice(eCPoint2);
                        if (ECAlgorithms.isFpCurve(eCCurve) && eCCurve.getFieldSize() >= 64) {
                            switch (eCCurve.getCoordinateSystem()) {
                                case 2: 
                                case 3: 
                                case 4: {
                                    eCFieldElement = eCPoint2.getZCoord(0);
                                    eCPoint2 = eCCurve.createPoint(eCPoint2.getXCoord().toBigInteger(), eCPoint2.getYCoord().toBigInteger());
                                    ECFieldElement eCFieldElement2 = eCFieldElement.square();
                                    ECFieldElement eCFieldElement3 = eCFieldElement2.multiply(eCFieldElement);
                                    eCPoint3 = eCPoint3.scaleX(eCFieldElement2).scaleY(eCFieldElement3);
                                    if (n2 != 0) break;
                                    arreCPoint[0] = eCPoint3;
                                    break;
                                }
                            }
                        }
                    }
                    while (n4 < n3) {
                        arreCPoint[n4++] = eCPoint3 = eCPoint3.add(eCPoint2);
                    }
                }
                eCCurve.normalizeAll(arreCPoint, n2, n3 - n2, eCFieldElement);
            }
        }
        wNafPreCompInfo.setPreComp(arreCPoint);
        if (bl) {
            int n5;
            ECPoint[] arreCPoint2 = wNafPreCompInfo.getPreCompNeg();
            if (arreCPoint2 == null) {
                n5 = 0;
                arreCPoint2 = new ECPoint[n3];
            } else {
                n5 = arreCPoint2.length;
                if (n5 < n3) {
                    arreCPoint2 = WNafUtil.resizeTable(arreCPoint2, n3);
                }
            }
            while (n5 < n3) {
                arreCPoint2[n5] = arreCPoint[n5].negate();
                ++n5;
            }
            wNafPreCompInfo.setPreCompNeg(arreCPoint2);
        }
        eCCurve.setPreCompInfo(eCPoint, PRECOMP_NAME, wNafPreCompInfo);
        return wNafPreCompInfo;
    }

    private static byte[] trim(byte[] arrby, int n) {
        byte[] arrby2 = new byte[n];
        System.arraycopy(arrby, 0, arrby2, 0, arrby2.length);
        return arrby2;
    }

    private static int[] trim(int[] arrn, int n) {
        int[] arrn2 = new int[n];
        System.arraycopy(arrn, 0, arrn2, 0, arrn2.length);
        return arrn2;
    }

    private static ECPoint[] resizeTable(ECPoint[] arreCPoint, int n) {
        ECPoint[] arreCPoint2 = new ECPoint[n];
        System.arraycopy(arreCPoint, 0, arreCPoint2, 0, arreCPoint.length);
        return arreCPoint2;
    }
}

