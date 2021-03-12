/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

public interface IProtocolHandler {
    public int available() throws Exception;

    public int read(byte[] var1) throws Exception;

    public void write(byte[] var1) throws Exception;

    public void close();
}

