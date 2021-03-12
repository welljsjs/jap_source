/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;

public abstract class StreamBlockCipher
implements BlockCipher,
StreamCipher {
    private final BlockCipher cipher;

    protected StreamBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public final byte returnByte(byte by) {
        return this.calculateByte(by);
    }

    public int processBytes(byte[] arrby, int n, int n2, byte[] arrby2, int n3) throws DataLengthException {
        if (n3 + n2 > arrby2.length) {
            throw new DataLengthException("output buffer too short");
        }
        if (n + n2 > arrby.length) {
            throw new DataLengthException("input buffer too small");
        }
        int n4 = n;
        int n5 = n + n2;
        int n6 = n3;
        while (n4 < n5) {
            arrby2[n6++] = this.calculateByte(arrby[n4++]);
        }
        return n2;
    }

    protected abstract byte calculateByte(byte var1);

    public abstract /* synthetic */ void reset();

    public abstract /* synthetic */ int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException;

    public abstract /* synthetic */ int getBlockSize();

    public abstract /* synthetic */ String getAlgorithmName();

    public abstract /* synthetic */ void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;
}

