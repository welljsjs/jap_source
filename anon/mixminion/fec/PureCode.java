/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.fec;

import anon.mixminion.fec.FECCode;
import anon.mixminion.fec.FECMath;
import anon.util.ByteArrayUtil;

public class PureCode
extends FECCode {
    public static final int FEC_MAGIC = -20181524;
    protected static final FECMath fecMath = new FECMath(8);
    protected char[] encMatrix;

    public PureCode(int n, int n2) {
        this(n, n2, fecMath.createEncodeMatrix(n, n2));
    }

    public PureCode(int n, int n2, char[] arrc) {
        super(n, n2);
        this.encMatrix = arrc;
    }

    public void encode(byte[][] arrby, int[] arrn, byte[][] arrby2, int[] arrn2, int[] arrn3, int n) {
        for (int i = 0; i < arrby2.length; ++i) {
            this.encode(arrby, arrn, arrby2[i], arrn2[i], arrn3[i], n);
        }
    }

    protected void encode(byte[][] arrby, int[] arrn, byte[] arrby2, int n, int n2, int n3) {
        if (n2 < this.k) {
            System.arraycopy(arrby[n2], arrn[n2], arrby2, n, n3);
        } else {
            int n4 = n2 * this.k;
            ByteArrayUtil.bzero(arrby2, n, n3);
            for (int i = 0; i < this.k; ++i) {
                fecMath.addMul(arrby2, n, arrby[i], arrn[i], (byte)this.encMatrix[n4 + i], n3);
            }
        }
    }

    public void decode(byte[][] arrby, int[] arrn, int[] arrn2, int n, boolean bl) {
        int n2;
        if (!bl) {
            FECCode.shuffle(arrby, arrn, arrn2, this.k);
        }
        char[] arrc = fecMath.createDecodeMatrix(this.encMatrix, arrn2, this.k, this.n);
        byte[][] arrarrby = new byte[this.k][];
        for (n2 = 0; n2 < this.k; ++n2) {
            if (arrn2[n2] < this.k) continue;
            arrarrby[n2] = new byte[n];
            for (int i = 0; i < this.k; ++i) {
                fecMath.addMul(arrarrby[n2], 0, arrby[i], arrn[i], (byte)arrc[n2 * this.k + i], n);
            }
        }
        for (n2 = 0; n2 < this.k; ++n2) {
            if (arrn2[n2] < this.k) continue;
            System.arraycopy(arrarrby[n2], 0, arrby[n2], arrn[n2], n);
            arrn2[n2] = n2;
        }
    }

    public String toString() {
        return new String("PureCode[k=" + this.k + ",n=" + this.n + "]");
    }
}

