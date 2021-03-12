/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.KeccakDigest;

public class SHA3Digest
extends KeccakDigest {
    private static int checkBitLength(int n) {
        switch (n) {
            case 224: 
            case 256: 
            case 384: 
            case 512: {
                return n;
            }
        }
        throw new IllegalArgumentException("'bitLength' " + n + " not supported for SHA-3");
    }

    public SHA3Digest() {
        this(256);
    }

    public SHA3Digest(int n) {
        super(SHA3Digest.checkBitLength(n));
    }

    public SHA3Digest(SHA3Digest sHA3Digest) {
        super(sHA3Digest);
    }

    public String getAlgorithmName() {
        return "SHA3-" + this.fixedOutputLength;
    }

    public int doFinal(byte[] arrby, int n) {
        this.absorb(new byte[]{2}, 0, 2L);
        return super.doFinal(arrby, n);
    }

    protected int doFinal(byte[] arrby, int n, byte by, int n2) {
        if (n2 < 0 || n2 > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int n3 = by & (1 << n2) - 1 | 2 << n2;
        int n4 = n2 + 2;
        if (n4 >= 8) {
            this.oneByte[0] = (byte)n3;
            this.absorb(this.oneByte, 0, 8L);
            n4 -= 8;
            n3 >>>= 8;
        }
        return super.doFinal(arrby, n, (byte)n3, n4);
    }
}

