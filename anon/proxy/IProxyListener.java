/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

public interface IProxyListener {
    public static final int PROTOCOL_OTHER = 0;
    public static final int PROTOCOL_WWW = 1;

    public void channelsChanged(int var1);

    public void transferedBytes(long var1, int var3);
}

