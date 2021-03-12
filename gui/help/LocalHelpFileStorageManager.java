/*
 * Decompiled with CFR 0.150.
 */
package gui.help;

import anon.util.ClassUtil;
import gui.help.AbstractHelpFileStorageManager;
import java.io.File;
import java.util.Observable;

public class LocalHelpFileStorageManager
extends AbstractHelpFileStorageManager {
    private final Observable DUMMY = new Observable();
    private final String LOCAL_HELP_PATH;

    public LocalHelpFileStorageManager(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Application name is null!");
        }
        File file = ClassUtil.getClassDirectory(this.getClass());
        this.LOCAL_HELP_PATH = file != null ? file.getParent() : null;
    }

    public boolean ensureMostRecentVersion(String string) {
        return true;
    }

    public Observable getStorageObservable() {
        return this.DUMMY;
    }

    public boolean handleHelpPathChanged(String string, String string2, boolean bl) {
        return string2 != null && string2.equals(this.LOCAL_HELP_PATH) && (string == null || !string.equals(string2));
    }

    public boolean helpInstallationExists(String string) {
        return string != null && string.equals(this.LOCAL_HELP_PATH);
    }

    public String helpPathValidityCheck(String string, boolean bl) {
        if (string == null || !string.equals(this.LOCAL_HELP_PATH)) {
            return "invalidHelpPathNoWrite";
        }
        return "helpJonDoExists";
    }

    public String getInitPath() {
        return this.LOCAL_HELP_PATH;
    }
}

