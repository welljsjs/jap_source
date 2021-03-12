/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.fec;

import anon.mixminion.fec.FECCode;
import anon.mixminion.fec.FECMath;
import anon.mixminion.fec.PureCode;
import anon.util.ByteArrayUtil;

public class Pure16Code
extends PureCode {
    protected static final FECMath fecMath = new FECMath(16);

    public Pure16Code(int n, int n2) {
        super(n, n2, fecMath.createEncodeMatrix(n, n2));
    }

    public void encode(byte[][] arrby, int[] arrn, byte[][] arrby2, int[] arrn2, int[] arrn3, int n) {
        int n2;
        if (n % 2 != 0) {
            throw new IllegalArgumentException("For 16 bit codes, buffers must be 16 bit aligned.");
        }
        char[][] arrarrc = new char[arrby.length][];
        int[] arrn4 = new int[arrby.length];
        int n3 = n / 2;
        char[] arrc = new char[n3];
        for (n2 = 0; n2 < arrarrc.length; ++n2) {
            arrarrc[n2] = new char[n3];
            ByteArrayUtil.byteArrayToCharArray(arrby[n2], arrn[n2], arrarrc[n2], 0, n);
            arrn4[n2] = 0;
        }
        for (n2 = 0; n2 < arrby2.length; ++n2) {
            if (arrn3[n2] < this.k) {
                System.arraycopy(arrby[arrn3[n2]], arrn[arrn3[n2]], arrby2[n2], arrn2[n2], n);
                continue;
            }
            this.encode(arrarrc, arrn4, arrc, 0, arrn3[n2], n3);
            ByteArrayUtil.charArrayToByteArray(arrc, 0, arrby2[n2], arrn2[n2], n);
        }
    }

    protected void encode(char[][] arrc, int[] arrn, char[] arrc2, int n, int n2, int n3) {
        int n4 = n2 * this.k;
        ByteArrayUtil.bzero(arrc2, n, n3);
        for (int i = 0; i < this.k; ++i) {
            fecMath.addMul(arrc2, n, arrc[i], arrn[i], this.encMatrix[n4 + i], n3);
        }
    }

    public void decode(byte[][] arrby, int[] arrn, int[] arrn2, int n, boolean bl) {
        if (n % 2 != 0) {
            throw new IllegalArgumentException("For 16 bit codes, buffers must be 16 bit aligned.");
        }
        if (!bl) {
            FECCode.shuffle(arrby, arrn, arrn2, this.k);
        }
        char[][] arrarrc = new char[arrby.length][];
        int[] arrn3 = new int[arrby.length];
        int n2 = n / 2;
        for (int i = 0; i < arrarrc.length; ++i) {
            arrarrc[i] = new char[n2];
            ByteArrayUtil.byteArrayToCharArray(arrby[i], arrn[i], arrarrc[i], 0, n);
            arrn3[i] = 0;
        }
        char[][] arrc = this.decode(arrarrc, arrn3, arrn2, n2);
        for (int i = 0; i < arrc.length; ++i) {
            if (arrc[i] == null) continue;
            ByteArrayUtil.charArrayToByteArray(arrc[i], 0, arrby[i], arrn[i], n);
            arrn2[i] = i;
        }
    }

    protected char[][] decode(char[][] arrc, int[] arrn, int[] arrn2, int n) {
        char[] arrc2 = fecMath.createDecodeMatrix(this.encMatrix, arrn2, this.k, this.n);
        char[][] arrarrc = new char[this.k][];
        for (int i = 0; i < this.k; ++i) {
            if (arrn2[i] < this.k) continue;
            arrarrc[i] = new char[n];
            for (int j = 0; j < this.k; ++j) {
                fecMath.addMul(arrarrc[i], 0, arrc[j], arrn[j], arrc2[i * this.k + j], n);
            }
        }
        return arrarrc;
    }

    public String toString() {
        return new String("Pure16Code[k=" + this.k + ",n=" + this.n + "]");
    }
}

