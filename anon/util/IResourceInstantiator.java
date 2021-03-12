/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public interface IResourceInstantiator {
    public Object getInstance(File var1, File var2) throws Exception;

    public Object getInstance(ZipEntry var1, ZipFile var2) throws Exception;

    public Object getInstance(InputStream var1, String var2) throws Exception;

    public static class ResourceInstantiationException
    extends Exception {
        private static final long serialVersionUID = 1L;
    }
}

