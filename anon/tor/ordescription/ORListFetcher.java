/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.ordescription;

public interface ORListFetcher {
    public byte[] getRouterStatus();

    public byte[] getDescriptor(String var1);

    public byte[] getDescriptorByFingerprint(String var1);

    public byte[] getAllDescriptors();

    public byte[] getStatus(String var1);
}

