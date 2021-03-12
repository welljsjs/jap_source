/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public abstract class GeneralDigest
implements ExtendedDigest,
Memoable {
    private static final int BYTE_LENGTH = 64;
    private final byte[] xBuf = new byte[4];
    private int xBufOff;
    private long byteCount;

    protected GeneralDigest() {
        this.xBufOff = 0;
    }

    protected GeneralDigest(GeneralDigest generalDigest) {
        this.copyIn(generalDigest);
    }

    protected GeneralDigest(byte[] arrby) {
        System.arraycopy(arrby, 0, this.xBuf, 0, this.xBuf.length);
        this.xBufOff = Pack.bigEndianToInt(arrby, 4);
        this.byteCount = Pack.bigEndianToLong(arrby, 8);
    }

    protected void copyIn(GeneralDigest generalDigest) {
        System.arraycopy(generalDigest.xBuf, 0, this.xBuf, 0, generalDigest.xBuf.length);
        this.xBufOff = generalDigest.xBufOff;
        this.byteCount = generalDigest.byteCount;
    }

    public void update(byte by) {
        this.xBuf[this.xBufOff++] = by;
        if (this.xBufOff == this.xBuf.length) {
            this.processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        ++this.byteCount;
    }

    public void update(byte[] arrby, int n, int n2) {
        while (this.xBufOff != 0 && n2 > 0) {
            this.update(arrby[n]);
            ++n;
            --n2;
        }
        while (n2 > this.xBuf.length) {
            this.processWord(arrby, n);
            n += this.xBuf.length;
            n2 -= this.xBuf.length;
            this.byteCount += (long)this.xBuf.length;
        }
        while (n2 > 0) {
            this.update(arrby[n]);
            ++n;
            --n2;
        }
    }

    public void finish() {
        long l = this.byteCount << 3;
        this.update((byte)-128);
        while (this.xBufOff != 0) {
            this.update((byte)0);
        }
        this.processLength(l);
        this.processBlock();
    }

    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; ++i) {
            this.xBuf[i] = 0;
        }
    }

    protected void populateState(byte[] arrby) {
        System.arraycopy(this.xBuf, 0, arrby, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, arrby, 4);
        Pack.longToBigEndian(this.byteCount, arrby, 8);
    }

    public int getByteLength() {
        return 64;
    }

    protected abstract void processWord(byte[] var1, int var2);

    protected abstract void processLength(long var1);

    protected abstract void processBlock();

    public abstract /* synthetic */ int doFinal(byte[] var1, int var2);

    public abstract /* synthetic */ int getDigestSize();

    public abstract /* synthetic */ String getAlgorithmName();

    public abstract /* synthetic */ void reset(Memoable var1);

    public abstract /* synthetic */ Memoable copy();
}

