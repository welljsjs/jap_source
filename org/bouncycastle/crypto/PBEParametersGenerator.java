/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Strings;

public abstract class PBEParametersGenerator {
    protected byte[] password;
    protected byte[] salt;
    protected int iterationCount;

    protected PBEParametersGenerator() {
    }

    public void init(byte[] arrby, byte[] arrby2, int n) {
        this.password = arrby;
        this.salt = arrby2;
        this.iterationCount = n;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public int getIterationCount() {
        return this.iterationCount;
    }

    public abstract CipherParameters generateDerivedParameters(int var1);

    public abstract CipherParameters generateDerivedParameters(int var1, int var2);

    public abstract CipherParameters generateDerivedMacParameters(int var1);

    public static byte[] PKCS5PasswordToBytes(char[] arrc) {
        if (arrc != null) {
            byte[] arrby = new byte[arrc.length];
            for (int i = 0; i != arrby.length; ++i) {
                arrby[i] = (byte)arrc[i];
            }
            return arrby;
        }
        return new byte[0];
    }

    public static byte[] PKCS5PasswordToUTF8Bytes(char[] arrc) {
        if (arrc != null) {
            return Strings.toUTF8ByteArray(arrc);
        }
        return new byte[0];
    }

    public static byte[] PKCS12PasswordToBytes(char[] arrc) {
        if (arrc != null && arrc.length > 0) {
            byte[] arrby = new byte[(arrc.length + 1) * 2];
            for (int i = 0; i != arrc.length; ++i) {
                arrby[i * 2] = (byte)(arrc[i] >>> 8);
                arrby[i * 2 + 1] = (byte)arrc[i];
            }
            return arrby;
        }
        return new byte[0];
    }
}

