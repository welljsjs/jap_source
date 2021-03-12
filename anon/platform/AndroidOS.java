/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import anon.platform.AbstractOS;

public final class AndroidOS
extends AbstractOS {
    public AndroidOS() throws Exception {
        String string = System.getProperty("java.vendor", "").toLowerCase();
        if (string.indexOf("android") == -1) {
            throw new Exception("Operating system is not Android");
        }
    }

    public String getAppdataDefaultDirectory(String string, boolean bl) {
        return null;
    }

    public String getConfigPath(String string, boolean bl) {
        return "/sdcard/test/";
    }

    protected boolean openLink(String string) {
        return false;
    }
}

