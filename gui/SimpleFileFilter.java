/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class SimpleFileFilter
extends FileFilter {
    private String m_strDesc = "Public X.509 Certificate (*.cer)";
    private String m_strExtension = ".cer";
    private int filterType;

    public int getFilterType() {
        return this.filterType;
    }

    public boolean accept(File file) {
        return file.isDirectory() || file.getName().endsWith(this.m_strExtension);
    }

    public String getDescription() {
        return this.m_strDesc;
    }
}

