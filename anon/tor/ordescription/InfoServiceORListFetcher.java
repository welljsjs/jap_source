/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.ordescription;

import anon.infoservice.InfoServiceHolder;
import anon.tor.ordescription.ORListFetcher;

public final class InfoServiceORListFetcher
implements ORListFetcher {
    public byte[] getORList() {
        return InfoServiceHolder.getInstance().getTorNodesList();
    }

    public byte[] getAllDescriptors() {
        return InfoServiceHolder.getInstance().getTorNodesList();
    }

    public byte[] getDescriptor(String string) {
        return null;
    }

    public byte[] getStatus(String string) {
        return null;
    }

    public byte[] getDescriptorByFingerprint(String string) {
        return null;
    }

    public byte[] getRouterStatus() {
        return null;
    }
}

