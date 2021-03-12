/*
 * Decompiled with CFR 0.150.
 */
package update;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class JarFileFilter
extends FileFilter {
    private final String jarExtension = "jar";

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String string = this.getExtension(file);
        if (string != null) {
            return string.equals("jar");
        }
        return false;
    }

    public String getDescription() {
        String string = "Jar File (*.jar)";
        return string;
    }

    private String getExtension(File file) {
        String string;
        String string2 = null;
        try {
            string = file.getName();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        int n = string.lastIndexOf(46);
        if (n > 0 && n < string.length() - 1) {
            string2 = string.substring(n + 1).toLowerCase();
        }
        return string2;
    }
}

