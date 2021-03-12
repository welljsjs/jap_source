/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.mmrdescription;

import anon.infoservice.InfoServiceHolder;
import anon.mixminion.mmrdescription.MMRListFetcher;

public final class InfoServiceMMRListFetcher
implements MMRListFetcher {
    public byte[] getMMRList() {
        return InfoServiceHolder.getInstance().getMixminionNodesList();
    }
}

