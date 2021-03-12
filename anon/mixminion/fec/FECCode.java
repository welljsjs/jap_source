/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.fec;

public abstract class FECCode {
    protected int k;
    protected int n;

    protected FECCode(int n, int n2) {
        this.k = n;
        this.n = n2;
    }

    public abstract void encode(byte[][] var1, int[] var2, byte[][] var3, int[] var4, int[] var5, int var6);

    public abstract void decode(byte[][] var1, int[] var2, int[] var3, int var4, boolean var5);

    protected static final void shuffle(byte[][] arrby, int[] arrn, int[] arrn2, int n) {
        int n2 = 0;
        while (n2 < n) {
            if (arrn2[n2] >= n || arrn2[n2] == n2) {
                ++n2;
                continue;
            }
            int n3 = arrn2[n2];
            if (arrn2[n3] == n3) {
                throw new IllegalArgumentException("Shuffle error at " + n2);
            }
            byte[] arrby2 = arrby[n2];
            arrby[n2] = arrby[n3];
            arrby[n3] = arrby2;
            int n4 = arrn[n2];
            arrn[n2] = arrn[n3];
            arrn[n3] = n4;
            n4 = arrn2[n2];
            arrn2[n2] = arrn2[n3];
            arrn2[n3] = n4;
        }
    }
}

