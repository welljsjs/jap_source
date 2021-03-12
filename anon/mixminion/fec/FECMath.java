/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.fec;

import anon.util.ByteArrayUtil;

public class FECMath {
    public int gfBits;
    public int gfSize;
    public static final String[] prim_polys = new String[]{null, null, "111", "1101", "11001", "101001", "1100001", "10010001", "101110001", "1000100001", "10010000001", "101000000001", "1100101000001", "11011000000001", "110000100010001", "1100000000000001", "11010000000010001"};
    public char[] gf_exp;
    public int[] gf_log;
    public char[] inverse;
    public char[][] gf_mul_table;

    public FECMath() {
        this(8);
    }

    public FECMath(int n) {
        this.gfBits = n;
        this.gfSize = (1 << n) - 1;
        this.gf_exp = new char[2 * this.gfSize];
        this.gf_log = new int[this.gfSize + 1];
        this.inverse = new char[this.gfSize + 1];
        if (n < 2 || n > 16) {
            throw new IllegalArgumentException("gfBits must be 2 .. 16");
        }
        this.generateGF();
        if (n <= 8) {
            this.initMulTable();
        }
    }

    public final void generateGF() {
        String string = prim_polys[this.gfBits];
        char c = '\u0001';
        this.gf_exp[this.gfBits] = '\u0000';
        int n = 0;
        while (n < this.gfBits) {
            this.gf_exp[n] = c;
            this.gf_log[this.gf_exp[n]] = n;
            if (string.charAt(n) == '1') {
                int n2 = this.gfBits;
                this.gf_exp[n2] = (char)(this.gf_exp[n2] ^ c);
            }
            ++n;
            c = (char)(c << 1);
        }
        this.gf_log[this.gf_exp[this.gfBits]] = this.gfBits;
        c = (char)(1 << this.gfBits - 1);
        for (n = this.gfBits + 1; n < this.gfSize; ++n) {
            this.gf_exp[n] = this.gf_exp[n - 1] >= c ? (char)(this.gf_exp[this.gfBits] ^ (this.gf_exp[n - 1] ^ c) << 1) : (char)(this.gf_exp[n - 1] << 1);
            this.gf_log[this.gf_exp[n]] = n;
        }
        this.gf_log[0] = this.gfSize;
        for (n = 0; n < this.gfSize; ++n) {
            this.gf_exp[n + this.gfSize] = this.gf_exp[n];
        }
        this.inverse[0] = '\u0000';
        this.inverse[1] = '\u0001';
        for (n = 2; n <= this.gfSize; ++n) {
            this.inverse[n] = this.gf_exp[this.gfSize - this.gf_log[n]];
        }
    }

    public final void initMulTable() {
        if (this.gfBits <= 8) {
            int n;
            this.gf_mul_table = new char[this.gfSize + 1][this.gfSize + 1];
            for (int i = 0; i < this.gfSize + 1; ++i) {
                for (n = 0; n < this.gfSize + 1; ++n) {
                    this.gf_mul_table[i][n] = this.gf_exp[this.modnn(this.gf_log[i] + this.gf_log[n])];
                }
            }
            for (n = 0; n < this.gfSize + 1; ++n) {
                this.gf_mul_table[n][0] = '\u0000';
                this.gf_mul_table[0][n] = '\u0000';
            }
        }
    }

    public final char modnn(int n) {
        while (n >= this.gfSize) {
            n -= this.gfSize;
            n = (n >> this.gfBits) + (n & this.gfSize);
        }
        return (char)n;
    }

    public final char mul(char c, char c2) {
        if (this.gfBits <= 8) {
            return this.gf_mul_table[c][c2];
        }
        if (c == '\u0000' || c2 == '\u0000') {
            return '\u0000';
        }
        return this.gf_exp[this.gf_log[c] + this.gf_log[c2]];
    }

    public static final char[] createGFMatrix(int n, int n2) {
        return new char[n * n2];
    }

    public final void addMul(char[] arrc, int n, char[] arrc2, int n2, char c, int n3) {
        if (c == '\u0000') {
            return;
        }
        int n4 = 16;
        int n5 = n;
        int n6 = n2;
        int n7 = n + n3;
        if (this.gfBits <= 8) {
            char[] arrc3 = this.gf_mul_table[c];
            while (n5 < n7 && n7 - n5 > n4) {
                int n8 = n5;
                arrc[n8] = (char)(arrc[n8] ^ arrc3[arrc2[n6]]);
                int n9 = n5 + 1;
                arrc[n9] = (char)(arrc[n9] ^ arrc3[arrc2[n6 + 1]]);
                int n10 = n5 + 2;
                arrc[n10] = (char)(arrc[n10] ^ arrc3[arrc2[n6 + 2]]);
                int n11 = n5 + 3;
                arrc[n11] = (char)(arrc[n11] ^ arrc3[arrc2[n6 + 3]]);
                int n12 = n5 + 4;
                arrc[n12] = (char)(arrc[n12] ^ arrc3[arrc2[n6 + 4]]);
                int n13 = n5 + 5;
                arrc[n13] = (char)(arrc[n13] ^ arrc3[arrc2[n6 + 5]]);
                int n14 = n5 + 6;
                arrc[n14] = (char)(arrc[n14] ^ arrc3[arrc2[n6 + 6]]);
                int n15 = n5 + 7;
                arrc[n15] = (char)(arrc[n15] ^ arrc3[arrc2[n6 + 7]]);
                int n16 = n5 + 8;
                arrc[n16] = (char)(arrc[n16] ^ arrc3[arrc2[n6 + 8]]);
                int n17 = n5 + 9;
                arrc[n17] = (char)(arrc[n17] ^ arrc3[arrc2[n6 + 9]]);
                int n18 = n5 + 10;
                arrc[n18] = (char)(arrc[n18] ^ arrc3[arrc2[n6 + 10]]);
                int n19 = n5 + 11;
                arrc[n19] = (char)(arrc[n19] ^ arrc3[arrc2[n6 + 11]]);
                int n20 = n5 + 12;
                arrc[n20] = (char)(arrc[n20] ^ arrc3[arrc2[n6 + 12]]);
                int n21 = n5 + 13;
                arrc[n21] = (char)(arrc[n21] ^ arrc3[arrc2[n6 + 13]]);
                int n22 = n5 + 14;
                arrc[n22] = (char)(arrc[n22] ^ arrc3[arrc2[n6 + 14]]);
                int n23 = n5 + 15;
                arrc[n23] = (char)(arrc[n23] ^ arrc3[arrc2[n6 + 15]]);
                n5 += n4;
                n6 += n4;
            }
            while (n5 < n7) {
                int n24 = n5++;
                arrc[n24] = (char)(arrc[n24] ^ arrc3[arrc2[n6]]);
                ++n6;
            }
        } else {
            int n25 = this.gf_log[c];
            while (n5 < n7) {
                char c2 = arrc2[n6];
                if (c2 != '\u0000') {
                    int n26 = n5;
                    arrc[n26] = (char)(arrc[n26] ^ this.gf_exp[n25 + this.gf_log[c2]]);
                }
                ++n5;
                ++n6;
            }
        }
    }

    public final void addMul(byte[] arrby, int n, byte[] arrby2, int n2, byte by, int n3) {
        if (by == 0) {
            return;
        }
        int n4 = 16;
        int n5 = n;
        int n6 = n2;
        int n7 = n + n3;
        char[] arrc = this.gf_mul_table[by & 0xFF];
        while (n5 < n7 && n7 - n5 > n4) {
            int n8 = n5;
            arrby[n8] = (byte)(arrby[n8] ^ arrc[arrby2[n6] & 0xFF]);
            int n9 = n5 + 1;
            arrby[n9] = (byte)(arrby[n9] ^ arrc[arrby2[n6 + 1] & 0xFF]);
            int n10 = n5 + 2;
            arrby[n10] = (byte)(arrby[n10] ^ arrc[arrby2[n6 + 2] & 0xFF]);
            int n11 = n5 + 3;
            arrby[n11] = (byte)(arrby[n11] ^ arrc[arrby2[n6 + 3] & 0xFF]);
            int n12 = n5 + 4;
            arrby[n12] = (byte)(arrby[n12] ^ arrc[arrby2[n6 + 4] & 0xFF]);
            int n13 = n5 + 5;
            arrby[n13] = (byte)(arrby[n13] ^ arrc[arrby2[n6 + 5] & 0xFF]);
            int n14 = n5 + 6;
            arrby[n14] = (byte)(arrby[n14] ^ arrc[arrby2[n6 + 6] & 0xFF]);
            int n15 = n5 + 7;
            arrby[n15] = (byte)(arrby[n15] ^ arrc[arrby2[n6 + 7] & 0xFF]);
            int n16 = n5 + 8;
            arrby[n16] = (byte)(arrby[n16] ^ arrc[arrby2[n6 + 8] & 0xFF]);
            int n17 = n5 + 9;
            arrby[n17] = (byte)(arrby[n17] ^ arrc[arrby2[n6 + 9] & 0xFF]);
            int n18 = n5 + 10;
            arrby[n18] = (byte)(arrby[n18] ^ arrc[arrby2[n6 + 10] & 0xFF]);
            int n19 = n5 + 11;
            arrby[n19] = (byte)(arrby[n19] ^ arrc[arrby2[n6 + 11] & 0xFF]);
            int n20 = n5 + 12;
            arrby[n20] = (byte)(arrby[n20] ^ arrc[arrby2[n6 + 12] & 0xFF]);
            int n21 = n5 + 13;
            arrby[n21] = (byte)(arrby[n21] ^ arrc[arrby2[n6 + 13] & 0xFF]);
            int n22 = n5 + 14;
            arrby[n22] = (byte)(arrby[n22] ^ arrc[arrby2[n6 + 14] & 0xFF]);
            int n23 = n5 + 15;
            arrby[n23] = (byte)(arrby[n23] ^ arrc[arrby2[n6 + 15] & 0xFF]);
            n5 += n4;
            n6 += n4;
        }
        while (n5 < n7) {
            int n24 = n5++;
            arrby[n24] = (byte)(arrby[n24] ^ arrc[arrby2[n6] & 0xFF]);
            ++n6;
        }
    }

    public final void matMul(char[] arrc, char[] arrc2, char[] arrc3, int n, int n2, int n3) {
        this.matMul(arrc, 0, arrc2, 0, arrc3, 0, n, n2, n3);
    }

    public final void matMul(char[] arrc, int n, char[] arrc2, int n2, char[] arrc3, int n3, int n4, int n5, int n6) {
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n6; ++j) {
                int n7 = i * n5;
                int n8 = j;
                char c = '\u0000';
                int n9 = 0;
                while (n9 < n5) {
                    c = (char)(c ^ this.mul(arrc[n + n7], arrc2[n2 + n8]));
                    ++n9;
                    ++n7;
                    n8 += n6;
                }
                arrc3[n3 + (i * n6 + j)] = c;
            }
        }
    }

    public static final boolean isIdentity(char[] arrc, int n) {
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i == j && arrc[n2] != '\u0001' || i != j && arrc[n2] != '\u0000') {
                    return false;
                }
                ++n2;
            }
        }
        return true;
    }

    public final void invertMatrix(char[] arrc, int n) throws IllegalArgumentException {
        int n2;
        int n3;
        int n4;
        int[] arrn = new int[n];
        int[] arrn2 = new int[n];
        int[] arrn3 = new int[n];
        char[] arrc2 = FECMath.createGFMatrix(1, n);
        char[] arrc3 = FECMath.createGFMatrix(1, n);
        for (n4 = 0; n4 < n; ++n4) {
            int n5;
            char c;
            int n6;
            n3 = -1;
            n2 = -1;
            boolean bl = false;
            if (arrn3[n4] != 1 && arrc[n4 * n + n4] != '\u0000') {
                n3 = n4;
                n2 = n4;
                bl = true;
            }
            if (!bl) {
                block1: for (n6 = 0; n6 < n; ++n6) {
                    if (arrn3[n6] == 1) continue;
                    for (c = '\u0000'; c < n; ++c) {
                        if (arrn3[c] == 0) {
                            if (arrc[n6 * n + c] == '\u0000') continue;
                            n3 = n6;
                            n2 = c;
                            bl = true;
                            break block1;
                        }
                        if (arrn3[c] <= 1) continue;
                        throw new IllegalArgumentException("singular matrix");
                    }
                }
            }
            if (!bl && n2 == -1) {
                throw new IllegalArgumentException("XXX pivot not found!");
            }
            bl = false;
            arrn3[n2] = arrn3[n2] + 1;
            if (n3 != n2) {
                for (n6 = 0; n6 < n; ++n6) {
                    c = arrc[n3 * n + n6];
                    arrc[n3 * n + n6] = arrc[n2 * n + n6];
                    arrc[n2 * n + n6] = c;
                }
            }
            arrn2[n4] = n3;
            arrn[n4] = n2;
            n6 = n2 * n;
            c = arrc[n6 + n2];
            if (c == '\u0000') {
                throw new IllegalArgumentException("singular matrix 2");
            }
            if (c != '\u0001') {
                c = this.inverse[c];
                arrc[n6 + n2] = '\u0001';
                for (n5 = 0; n5 < n; ++n5) {
                    arrc[n6 + n5] = this.mul(c, arrc[n6 + n5]);
                }
            }
            arrc2[n2] = '\u0001';
            if (!ByteArrayUtil.equal(arrc, n6, arrc2, 0, n)) {
                n5 = 0;
                int n7 = 0;
                while (n7 < n) {
                    if (n7 != n2) {
                        c = arrc[n5 + n2];
                        arrc[n5 + n2] = '\u0000';
                        this.addMul(arrc, n5, arrc, n6, c, n);
                    }
                    ++n7;
                    n5 += n;
                }
            }
            arrc2[n2] = '\u0000';
        }
        for (n4 = n - 1; n4 >= 0; --n4) {
            if (arrn2[n4] < 0 || arrn2[n4] >= n) {
                System.err.println("AARGH, indxr[col] " + arrn2[n4]);
                continue;
            }
            if (arrn[n4] < 0 || arrn[n4] >= n) {
                System.err.println("AARGH, indxc[col] " + arrn[n4]);
                continue;
            }
            if (arrn2[n4] == arrn[n4]) continue;
            for (n3 = 0; n3 < n; ++n3) {
                n2 = arrc[n3 * n + arrn[n4]];
                arrc[n3 * n + arrn[n4]] = arrc[n3 * n + arrn2[n4]];
                arrc[n3 * n + arrn2[n4]] = n2;
            }
        }
    }

    public final void invertVandermonde(char[] arrc, int n) {
        char c;
        if (n == 1) {
            return;
        }
        char[] arrc2 = FECMath.createGFMatrix(1, n);
        char[] arrc3 = FECMath.createGFMatrix(1, n);
        char[] arrc4 = FECMath.createGFMatrix(1, n);
        int n2 = 1;
        char c2 = '\u0000';
        while (c2 < n) {
            arrc2[c2] = '\u0000';
            arrc4[c2] = arrc[n2];
            ++c2;
            n2 += n;
        }
        arrc2[n - 1] = arrc4[0];
        for (n2 = 1; n2 < n; ++n2) {
            c2 = arrc4[n2];
            for (c = n - 1 - (n2 - 1); c < n - 1; ++c) {
                char c3 = c;
                arrc2[c3] = (char)(arrc2[c3] ^ this.mul(c2, arrc2[c + 1]));
            }
            int n3 = n - 1;
            arrc2[n3] = (char)(arrc2[n3] ^ c2);
        }
        for (n2 = 0; n2 < n; ++n2) {
            int n4;
            c2 = arrc4[n2];
            c = '\u0001';
            arrc3[n - 1] = '\u0001';
            for (n4 = n - 2; n4 >= 0; --n4) {
                arrc3[n4] = (char)(arrc2[n4 + 1] ^ this.mul(c2, arrc3[n4 + 1]));
                c = (char)(this.mul(c2, c) ^ arrc3[n4]);
            }
            for (n4 = 0; n4 < n; ++n4) {
                arrc[n4 * n + n2] = this.mul(this.inverse[c], arrc3[n4]);
            }
        }
    }

    public final char[] createEncodeMatrix(int n, int n2) {
        if (n > this.gfSize + 1 || n2 > this.gfSize + 1 || n > n2) {
            throw new IllegalArgumentException("Invalid parameters n=" + n2 + ",k=" + n + ",gfSize=" + this.gfSize);
        }
        char[] arrc = FECMath.createGFMatrix(n2, n);
        char[] arrc2 = FECMath.createGFMatrix(n2, n);
        arrc2[0] = '\u0001';
        int n3 = n;
        int n4 = 0;
        while (n4 < n2 - 1) {
            for (int i = 0; i < n; ++i) {
                arrc2[n3 + i] = this.gf_exp[this.modnn(n4 * i)];
            }
            ++n4;
            n3 += n;
        }
        this.invertVandermonde(arrc2, n);
        this.matMul(arrc2, n * n, arrc2, 0, arrc, n * n, n2 - n, n, n);
        ByteArrayUtil.bzero(arrc, 0, n * n);
        n3 = 0;
        n4 = 0;
        while (n4 < n) {
            arrc[n3] = '\u0001';
            ++n4;
            n3 += n + 1;
        }
        return arrc;
    }

    protected final char[] createDecodeMatrix(char[] arrc, int[] arrn, int n, int n2) {
        char[] arrc2 = FECMath.createGFMatrix(n, n);
        int n3 = 0;
        int n4 = 0;
        while (n3 < n) {
            System.arraycopy(arrc, arrn[n3] * n, arrc2, n4, n);
            ++n3;
            n4 += n;
        }
        this.invertMatrix(arrc2, n);
        return arrc2;
    }
}

