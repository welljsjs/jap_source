/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.keyexchange;

import anon.crypto.IMyPrivateKey;
import anon.crypto.JAPCertificate;
import anon.crypto.tinytls.TLSException;
import java.math.BigInteger;

public abstract class Key_Exchange {
    public abstract byte[] generateServerKeyExchange(IMyPrivateKey var1, byte[] var2, byte[] var3) throws TLSException;

    public abstract void processServerKeyExchange(byte[] var1, int var2, int var3, byte[] var4, byte[] var5, JAPCertificate var6) throws TLSException;

    public abstract byte[] calculateServerFinished(byte[] var1);

    public abstract void processServerFinished(byte[] var1, int var2, byte[] var3) throws TLSException;

    public abstract void processClientKeyExchange(BigInteger var1);

    public abstract byte[] calculateClientKeyExchange() throws TLSException;

    public abstract void processClientFinished(byte[] var1, byte[] var2) throws TLSException;

    public abstract byte[] calculateClientFinished(byte[] var1) throws TLSException;

    public abstract byte[] calculateKeys();
}

