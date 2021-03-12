/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.LongDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA512Digest
extends LongDigest {
    private static final int DIGEST_LENGTH = 64;

    public SHA512Digest() {
    }

    public SHA512Digest(SHA512Digest sHA512Digest) {
        super(sHA512Digest);
    }

    public SHA512Digest(byte[] arrby) {
        this.restoreState(arrby);
    }

    public String getAlgorithmName() {
        return "SHA-512";
    }

    public int getDigestSize() {
        return 64;
    }

    public int doFinal(byte[] arrby, int n) {
        this.finish();
        Pack.longToBigEndian(this.H1, arrby, n);
        Pack.longToBigEndian(this.H2, arrby, n + 8);
        Pack.longToBigEndian(this.H3, arrby, n + 16);
        Pack.longToBigEndian(this.H4, arrby, n + 24);
        Pack.longToBigEndian(this.H5, arrby, n + 32);
        Pack.longToBigEndian(this.H6, arrby, n + 40);
        Pack.longToBigEndian(this.H7, arrby, n + 48);
        Pack.longToBigEndian(this.H8, arrby, n + 56);
        this.reset();
        return 64;
    }

    public void reset() {
        super.reset();
        this.H1 = 7640891576956012808L;
        this.H2 = -4942790177534073029L;
        this.H3 = 4354685564936845355L;
        this.H4 = -6534734903238641935L;
        this.H5 = 5840696475078001361L;
        this.H6 = -7276294671716946913L;
        this.H7 = 2270897969802886507L;
        this.H8 = 6620516959819538809L;
    }

    public Memoable copy() {
        return new SHA512Digest(this);
    }

    public void reset(Memoable memoable) {
        SHA512Digest sHA512Digest = (SHA512Digest)memoable;
        this.copyIn(sHA512Digest);
    }

    public byte[] getEncodedState() {
        byte[] arrby = new byte[this.getEncodedStateSize()];
        super.populateState(arrby);
        return arrby;
    }
}

