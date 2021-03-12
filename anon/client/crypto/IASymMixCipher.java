/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.crypto.MyRSAPublicKey;
import java.math.BigInteger;
import org.w3c.dom.Element;

public interface IASymMixCipher {
    public int encrypt(byte[] var1, int var2, byte[] var3, int var4);

    public int getOutputBlockSize();

    public int getInputBlockSize();

    public int getPaddingSize();

    public int setPublicKey(BigInteger var1, BigInteger var2);

    public int setPublicKey(Element var1);

    public MyRSAPublicKey getPublicKey();
}

