/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Memoable;

public class SkeinDigest
implements ExtendedDigest,
Memoable {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private SkeinEngine engine;

    public SkeinDigest(int n, int n2) {
        this.engine = new SkeinEngine(n, n2);
        this.init(null);
    }

    public SkeinDigest(SkeinDigest skeinDigest) {
        this.engine = new SkeinEngine(skeinDigest.engine);
    }

    public void reset(Memoable memoable) {
        SkeinDigest skeinDigest = (SkeinDigest)memoable;
        this.engine.reset(skeinDigest.engine);
    }

    public Memoable copy() {
        return new SkeinDigest(this);
    }

    public String getAlgorithmName() {
        return "Skein-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
    }

    public int getDigestSize() {
        return this.engine.getOutputSize();
    }

    public int getByteLength() {
        return this.engine.getBlockSize();
    }

    public void init(SkeinParameters skeinParameters) {
        this.engine.init(skeinParameters);
    }

    public void reset() {
        this.engine.reset();
    }

    public void update(byte by) {
        this.engine.update(by);
    }

    public void update(byte[] arrby, int n, int n2) {
        this.engine.update(arrby, n, n2);
    }

    public int doFinal(byte[] arrby, int n) {
        return this.engine.doFinal(arrby, n);
    }
}

