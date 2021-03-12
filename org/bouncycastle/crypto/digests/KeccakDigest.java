/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

public class KeccakDigest
implements ExtendedDigest {
    private static long[] KeccakRoundConstants = KeccakDigest.keccakInitializeRoundConstants();
    private static int[] KeccakRhoOffsets = KeccakDigest.keccakInitializeRhoOffsets();
    protected byte[] state = new byte[200];
    protected byte[] dataQueue = new byte[192];
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;
    protected int bitsAvailableForSqueezing;
    protected byte[] chunk;
    protected byte[] oneByte;
    long[] C = new long[5];
    long[] tempA = new long[25];
    long[] chiC = new long[5];

    private static long[] keccakInitializeRoundConstants() {
        long[] arrl = new long[24];
        byte[] arrby = new byte[]{1};
        for (int i = 0; i < 24; ++i) {
            arrl[i] = 0L;
            for (int j = 0; j < 7; ++j) {
                int n = (1 << j) - 1;
                if (!KeccakDigest.LFSR86540(arrby)) continue;
                int n2 = i;
                arrl[n2] = arrl[n2] ^ 1L << n;
            }
        }
        return arrl;
    }

    private static boolean LFSR86540(byte[] arrby) {
        boolean bl = (arrby[0] & 1) != 0;
        arrby[0] = (arrby[0] & 0x80) != 0 ? (byte)(arrby[0] << 1 ^ 0x71) : (byte)(arrby[0] << 1);
        return bl;
    }

    private static int[] keccakInitializeRhoOffsets() {
        int[] arrn = new int[25];
        arrn[0] = 0;
        int n = 1;
        int n2 = 0;
        for (int i = 0; i < 24; ++i) {
            arrn[n % 5 + 5 * (n2 % 5)] = (i + 1) * (i + 2) / 2 % 64;
            int n3 = (0 * n + 1 * n2) % 5;
            int n4 = (2 * n + 3 * n2) % 5;
            n = n3;
            n2 = n4;
        }
        return arrn;
    }

    private void clearDataQueueSection(int n, int n2) {
        for (int i = n; i != n + n2; ++i) {
            this.dataQueue[i] = 0;
        }
    }

    public KeccakDigest() {
        this(288);
    }

    public KeccakDigest(int n) {
        this.init(n);
    }

    public KeccakDigest(KeccakDigest keccakDigest) {
        System.arraycopy(keccakDigest.state, 0, this.state, 0, keccakDigest.state.length);
        System.arraycopy(keccakDigest.dataQueue, 0, this.dataQueue, 0, keccakDigest.dataQueue.length);
        this.rate = keccakDigest.rate;
        this.bitsInQueue = keccakDigest.bitsInQueue;
        this.fixedOutputLength = keccakDigest.fixedOutputLength;
        this.squeezing = keccakDigest.squeezing;
        this.bitsAvailableForSqueezing = keccakDigest.bitsAvailableForSqueezing;
        this.chunk = Arrays.clone(keccakDigest.chunk);
        this.oneByte = Arrays.clone(keccakDigest.oneByte);
    }

    public String getAlgorithmName() {
        return "Keccak-" + this.fixedOutputLength;
    }

    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }

    public void update(byte by) {
        this.oneByte[0] = by;
        this.absorb(this.oneByte, 0, 8L);
    }

    public void update(byte[] arrby, int n, int n2) {
        this.absorb(arrby, n, (long)n2 * 8L);
    }

    public int doFinal(byte[] arrby, int n) {
        this.squeeze(arrby, n, this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }

    protected int doFinal(byte[] arrby, int n, byte by, int n2) {
        if (n2 > 0) {
            this.oneByte[0] = by;
            this.absorb(this.oneByte, 0, n2);
        }
        this.squeeze(arrby, n, this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }

    public void reset() {
        this.init(this.fixedOutputLength);
    }

    public int getByteLength() {
        return this.rate / 8;
    }

    private void init(int n) {
        switch (n) {
            case 288: {
                this.initSponge(1024, 576);
                break;
            }
            case 128: {
                this.initSponge(1344, 256);
                break;
            }
            case 224: {
                this.initSponge(1152, 448);
                break;
            }
            case 256: {
                this.initSponge(1088, 512);
                break;
            }
            case 384: {
                this.initSponge(832, 768);
                break;
            }
            case 512: {
                this.initSponge(576, 1024);
                break;
            }
            default: {
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
            }
        }
    }

    private void initSponge(int n, int n2) {
        if (n + n2 != 1600) {
            throw new IllegalStateException("rate + capacity != 1600");
        }
        if (n <= 0 || n >= 1600 || n % 64 != 0) {
            throw new IllegalStateException("invalid rate value");
        }
        this.rate = n;
        Arrays.fill(this.state, (byte)0);
        Arrays.fill(this.dataQueue, (byte)0);
        this.bitsInQueue = 0;
        this.squeezing = false;
        this.bitsAvailableForSqueezing = 0;
        this.fixedOutputLength = n2 / 2;
        this.chunk = new byte[n / 8];
        this.oneByte = new byte[1];
    }

    private void absorbQueue() {
        this.KeccakAbsorb(this.state, this.dataQueue, this.rate / 8);
        this.bitsInQueue = 0;
    }

    protected void absorb(byte[] arrby, int n, long l) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue.");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing.");
        }
        long l2 = 0L;
        while (l2 < l) {
            if (this.bitsInQueue == 0 && l >= (long)this.rate && l2 <= l - (long)this.rate) {
                long l3 = (l - l2) / (long)this.rate;
                for (long i = 0L; i < l3; ++i) {
                    System.arraycopy(arrby, (int)((long)n + l2 / 8L + i * (long)this.chunk.length), this.chunk, 0, this.chunk.length);
                    this.KeccakAbsorb(this.state, this.chunk, this.chunk.length);
                }
                l2 += l3 * (long)this.rate;
                continue;
            }
            int n2 = (int)(l - l2);
            if (n2 + this.bitsInQueue > this.rate) {
                n2 = this.rate - this.bitsInQueue;
            }
            int n3 = n2 % 8;
            System.arraycopy(arrby, n + (int)(l2 / 8L), this.dataQueue, this.bitsInQueue / 8, (n2 -= n3) / 8);
            this.bitsInQueue += n2;
            l2 += (long)n2;
            if (this.bitsInQueue == this.rate) {
                this.absorbQueue();
            }
            if (n3 <= 0) continue;
            int n4 = (1 << n3) - 1;
            this.dataQueue[this.bitsInQueue / 8] = (byte)(arrby[n + (int)(l2 / 8L)] & n4);
            this.bitsInQueue += n3;
            l2 += (long)n3;
        }
    }

    private void padAndSwitchToSqueezingPhase() {
        if (this.bitsInQueue + 1 == this.rate) {
            int n = this.bitsInQueue / 8;
            this.dataQueue[n] = (byte)(this.dataQueue[n] | 1 << this.bitsInQueue % 8);
            this.absorbQueue();
            this.clearDataQueueSection(0, this.rate / 8);
        } else {
            this.clearDataQueueSection((this.bitsInQueue + 7) / 8, this.rate / 8 - (this.bitsInQueue + 7) / 8);
            int n = this.bitsInQueue / 8;
            this.dataQueue[n] = (byte)(this.dataQueue[n] | 1 << this.bitsInQueue % 8);
        }
        int n = (this.rate - 1) / 8;
        this.dataQueue[n] = (byte)(this.dataQueue[n] | 1 << (this.rate - 1) % 8);
        this.absorbQueue();
        if (this.rate == 1024) {
            this.KeccakExtract1024bits(this.state, this.dataQueue);
            this.bitsAvailableForSqueezing = 1024;
        } else {
            this.KeccakExtract(this.state, this.dataQueue, this.rate / 64);
            this.bitsAvailableForSqueezing = this.rate;
        }
        this.squeezing = true;
    }

    protected void squeeze(byte[] arrby, int n, long l) {
        int n2;
        if (!this.squeezing) {
            this.padAndSwitchToSqueezingPhase();
        }
        if (l % 8L != 0L) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        }
        for (long i = 0L; i < l; i += (long)n2) {
            if (this.bitsAvailableForSqueezing == 0) {
                this.keccakPermutation(this.state);
                if (this.rate == 1024) {
                    this.KeccakExtract1024bits(this.state, this.dataQueue);
                    this.bitsAvailableForSqueezing = 1024;
                } else {
                    this.KeccakExtract(this.state, this.dataQueue, this.rate / 64);
                    this.bitsAvailableForSqueezing = this.rate;
                }
            }
            if ((long)(n2 = this.bitsAvailableForSqueezing) > l - i) {
                n2 = (int)(l - i);
            }
            System.arraycopy(this.dataQueue, (this.rate - this.bitsAvailableForSqueezing) / 8, arrby, n + (int)(i / 8L), n2 / 8);
            this.bitsAvailableForSqueezing -= n2;
        }
    }

    private void fromBytesToWords(long[] arrl, byte[] arrby) {
        for (int i = 0; i < 25; ++i) {
            arrl[i] = 0L;
            int n = i * 8;
            for (int j = 0; j < 8; ++j) {
                int n2 = i;
                arrl[n2] = arrl[n2] | ((long)arrby[n + j] & 0xFFL) << 8 * j;
            }
        }
    }

    private void fromWordsToBytes(byte[] arrby, long[] arrl) {
        for (int i = 0; i < 25; ++i) {
            int n = i * 8;
            for (int j = 0; j < 8; ++j) {
                arrby[n + j] = (byte)(arrl[i] >>> 8 * j & 0xFFL);
            }
        }
    }

    private void keccakPermutation(byte[] arrby) {
        long[] arrl = new long[arrby.length / 8];
        this.fromBytesToWords(arrl, arrby);
        this.keccakPermutationOnWords(arrl);
        this.fromWordsToBytes(arrby, arrl);
    }

    private void keccakPermutationAfterXor(byte[] arrby, byte[] arrby2, int n) {
        for (int i = 0; i < n; ++i) {
            int n2 = i;
            arrby[n2] = (byte)(arrby[n2] ^ arrby2[i]);
        }
        this.keccakPermutation(arrby);
    }

    private void keccakPermutationOnWords(long[] arrl) {
        for (int i = 0; i < 24; ++i) {
            this.theta(arrl);
            this.rho(arrl);
            this.pi(arrl);
            this.chi(arrl);
            this.iota(arrl, i);
        }
    }

    private void theta(long[] arrl) {
        int n;
        for (n = 0; n < 5; ++n) {
            this.C[n] = 0L;
            for (int i = 0; i < 5; ++i) {
                int n2 = n;
                this.C[n2] = this.C[n2] ^ arrl[n + 5 * i];
            }
        }
        for (n = 0; n < 5; ++n) {
            long l = this.C[(n + 1) % 5] << 1 ^ this.C[(n + 1) % 5] >>> 63 ^ this.C[(n + 4) % 5];
            for (int i = 0; i < 5; ++i) {
                int n3 = n + 5 * i;
                arrl[n3] = arrl[n3] ^ l;
            }
        }
    }

    private void rho(long[] arrl) {
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                int n = i + 5 * j;
                arrl[n] = KeccakRhoOffsets[n] != 0 ? arrl[n] << KeccakRhoOffsets[n] ^ arrl[n] >>> 64 - KeccakRhoOffsets[n] : arrl[n];
            }
        }
    }

    private void pi(long[] arrl) {
        System.arraycopy(arrl, 0, this.tempA, 0, this.tempA.length);
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                arrl[j + 5 * ((2 * i + 3 * j) % 5)] = this.tempA[i + 5 * j];
            }
        }
    }

    private void chi(long[] arrl) {
        for (int i = 0; i < 5; ++i) {
            int n;
            for (n = 0; n < 5; ++n) {
                this.chiC[n] = arrl[n + 5 * i] ^ (arrl[(n + 1) % 5 + 5 * i] ^ 0xFFFFFFFFFFFFFFFFL) & arrl[(n + 2) % 5 + 5 * i];
            }
            for (n = 0; n < 5; ++n) {
                arrl[n + 5 * i] = this.chiC[n];
            }
        }
    }

    private void iota(long[] arrl, int n) {
        arrl[0] = arrl[0] ^ KeccakRoundConstants[n];
    }

    private void KeccakAbsorb(byte[] arrby, byte[] arrby2, int n) {
        this.keccakPermutationAfterXor(arrby, arrby2, n);
    }

    private void KeccakExtract1024bits(byte[] arrby, byte[] arrby2) {
        System.arraycopy(arrby, 0, arrby2, 0, 128);
    }

    private void KeccakExtract(byte[] arrby, byte[] arrby2, int n) {
        System.arraycopy(arrby, 0, arrby2, 0, n * 8);
    }
}

