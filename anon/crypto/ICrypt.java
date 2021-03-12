/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import java.security.NoSuchAlgorithmException;

public interface ICrypt {
    public String crypt(String var1) throws NoSuchAlgorithmException;

    public String crypt(String var1, String var2) throws NoSuchAlgorithmException;
}

