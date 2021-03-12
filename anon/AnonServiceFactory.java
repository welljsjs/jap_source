/*
 * Decompiled with CFR 0.150.
 */
package anon;

import anon.AnonService;
import anon.mixminion.Mixminion;
import anon.tor.Tor;

public final class AnonServiceFactory {
    public static final String SERVICE_TOR = "TOR";
    public static final String SERVICE_MIXMINION = "Mixminion";
    private static AnonService ms_AnonService = null;

    private AnonServiceFactory() {
    }

    public static AnonService getAnonServiceInstance(String string) {
        if (string == null) {
            return null;
        }
        if (string.equals(SERVICE_TOR)) {
            return Tor.getInstance();
        }
        if (string.equals(SERVICE_MIXMINION)) {
            return Mixminion.getInstance();
        }
        return null;
    }
}

