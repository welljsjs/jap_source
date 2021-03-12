/*
 * Decompiled with CFR 0.150.
 */
package gui.help;

import java.io.File;
import java.net.URL;
import java.util.Observable;

public interface IHelpModel {
    public void setHelpPath(File var1);

    public boolean isHelpPathDefined();

    public String getHelpPath();

    public Observable getHelpFileStorageObservable();

    public boolean isHelpPathChangeable();

    public String helpPathValidityCheck(File var1);

    public URL getHelpURL(String var1);
}

