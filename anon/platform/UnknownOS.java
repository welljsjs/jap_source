/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import anon.platform.AbstractOS;
import java.io.File;
import logging.LogHolder;
import logging.LogType;

public class UnknownOS
extends AbstractOS {
    public String getAppdataDefaultDirectory(String string, boolean bl) {
        return null;
    }

    public boolean openLink(String string) {
        LogHolder.log(6, LogType.MISC, "Class is uncapable of opening links");
        return false;
    }

    public String getConfigPath(String string, boolean bl) {
        return System.getProperty("user.home", "") + File.separator;
    }
}

