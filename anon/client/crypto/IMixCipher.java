/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import java.util.Vector;

public interface IMixCipher {
    public byte[] encrypt(byte[] var1, int var2, Vector var3);

    public boolean decrypt(byte[] var1);

    public int getNextPacketEncryptionOverhead();
}

