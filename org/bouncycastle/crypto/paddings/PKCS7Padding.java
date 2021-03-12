/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class PKCS7Padding
implements BlockCipherPadding {
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
    }

    public String getPaddingName() {
        return "PKCS7";
    }

    public int addPadding(byte[] arrby, int n) {
        byte by = (byte)(arrby.length - n);
        while (n < arrby.length) {
            arrby[n] = by;
            ++n;
        }
        return by;
    }

    public int padCount(byte[] arrby) throws InvalidCipherTextException {
        int n = arrby[arrby.length - 1] & 0xFF;
        byte by = (byte)n;
        boolean bl = n > arrby.length | n == 0;
        for (int i = 0; i < arrby.length; ++i) {
            bl |= arrby.length - i <= n & arrby[i] != by;
        }
        if (bl) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return n;
    }
}

