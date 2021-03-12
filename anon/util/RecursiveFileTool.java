/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.ResourceLoader;
import anon.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.crypto.digests.MD5Digest;

public class RecursiveFileTool {
    private static final int INIT_DEPTH = 0;
    private static final int MAX_DEPTH_IGNORE = -2;
    private static final int COPY_BUFFER_SIZE = 1024;
    private static final int EOF = -1;

    public static File readFileName(String string) {
        int n = -1;
        if (string == null) {
            return null;
        }
        if (System.getProperty("os.name", "").toLowerCase().indexOf("win") < 0) {
            int n2;
            while ((n2 = string.indexOf(92, n + 1)) >= 0) {
                if (string.length() > n2 + 1) {
                    if (string.charAt(n2 + 1) == '\\') {
                        n = n2;
                    }
                    if (n == n2 || Character.isWhitespace(string.charAt(n2 + 1))) {
                        string = string.substring(0, n2) + string.substring(n2 + 1, string.length());
                    }
                    n = n2;
                    continue;
                }
                string = string.substring(0, n2);
            }
        }
        return new File(string);
    }

    public static void copy(File file, File file2) {
        if (file == null) {
            LogHolder.log(2, LogType.MISC, "Source file is null: This should never happen");
            return;
        }
        if (file2 == null) {
            LogHolder.log(2, LogType.MISC, "Destination file is null: This should never happen");
            return;
        }
        if (file.isDirectory()) {
            LogHolder.log(3, LogType.MISC, "File " + file.getName() + " is a directory: cannot copy it");
            return;
        }
        if (!file.exists()) {
            LogHolder.log(3, LogType.MISC, "There is no such file or directory: " + file.getName());
            return;
        }
        try {
            RecursiveFileTool.copySingleFile(file, file2);
        }
        catch (IOException iOException) {
            LogHolder.log(2, LogType.MISC, "An IO Exception while copying file " + file.getName() + ": " + iOException.getMessage());
        }
    }

    public static void copyRecursive(File file, File file2) {
        RecursiveFileTool.copyRecursion(file, file2, 0, -2);
    }

    public static void copyRecursive(File file, File file2, int n) {
        RecursiveFileTool.copyRecursion(file, file2, 0, n);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void copyRecursion(File file, File file2, int n, int n2) {
        if (file == null) {
            LogHolder.log(2, LogType.MISC, "Source file is null: This should never happen");
            return;
        }
        if (file2 == null) {
            LogHolder.log(2, LogType.MISC, "Destination file is null: This should never happen");
            return;
        }
        if (file2.getAbsolutePath().startsWith(file.getAbsolutePath())) {
            LogHolder.log(3, LogType.MISC, "destination path is in source path: to avoid endless loops, operation is not allowed");
            return;
        }
        if (!file.exists()) {
            LogHolder.log(3, LogType.MISC, "There is no such file or directory: " + file.getName());
            return;
        }
        if (file.isDirectory()) {
            String[] arrstring = file.list();
            boolean bl = file2.mkdir();
            if (!bl) {
                LogHolder.log(3, LogType.MISC, "Cannot create directory: " + file2.getName());
                return;
            }
            int n3 = 0;
            while (n3 < arrstring.length) {
                String string = arrstring[n3];
                if (n2 == -2 || n < n2) {
                    RecursiveFileTool.copyRecursion(new File(file.getAbsolutePath() + File.separator + string), new File(file2.getAbsolutePath() + File.separator + string), n + 1, n2);
                }
                ++n3;
            }
            return;
        }
        try {
            RecursiveFileTool.copySingleFile(file, file2);
            return;
        }
        catch (IOException iOException) {
            LogHolder.log(2, LogType.MISC, "An IO Exception while copying file " + file.getName() + ": " + iOException.getMessage());
        }
    }

    static void copySingleFile(File file, File file2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        RecursiveFileTool.copySingleFile(fileInputStream, file2);
        Util.closeStream(fileInputStream);
    }

    public static boolean deleteRecursion(File file) {
        Object object;
        if (file == null) {
            LogHolder.log(2, LogType.MISC, "Source file is null: This should never happen");
            return true;
        }
        if (!file.exists()) {
            LogHolder.log(3, LogType.MISC, "There is no such file or directory: " + file.getName());
            return true;
        }
        if (file.isDirectory()) {
            object = file.list();
            for (int i = 0; object != null && i < ((String[])object).length; ++i) {
                String string = object[i];
                RecursiveFileTool.deleteRecursion(new File(file.getAbsolutePath() + File.separator + string));
            }
        }
        object = file.getName() + (file.delete() ? " was successfully deleted." : " was not successfully deleted.");
        LogHolder.log(7, LogType.MISC, (String)object);
        return !file.exists();
    }

    static void copySingleFile(InputStream inputStream, File file) throws IOException {
        IOException iOException = null;
        if (inputStream == null) {
            LogHolder.log(3, LogType.MISC, "Abort copy process: InputStream is null");
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            byte[] arrby = new byte[1024];
            int n = 1;
            while (inputStream.available() > 0 && (n = inputStream.read(arrby)) != -1) {
                fileOutputStream.write(arrby, 0, n);
            }
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            inputStream.close();
        }
        catch (IOException iOException3) {
            // empty catch block
        }
        if (iOException != null) {
            throw iOException;
        }
    }

    public static boolean equals(File file, byte[] arrby, long l) {
        block3: {
            try {
                if (Util.arraysEqual(RecursiveFileTool.createMD5Digest(file), arrby)) {
                    return true;
                }
            }
            catch (Exception exception) {
                if (file.length() != l) break block3;
                return true;
            }
        }
        return false;
    }

    public static boolean equals(File file, File file2, boolean bl) {
        boolean bl2 = false;
        if (file == null || file2 == null) {
            return false;
        }
        try {
            if (!file.exists() || !file2.exists()) {
                return false;
            }
            bl2 = true;
        }
        catch (SecurityException securityException) {
            LogHolder.log(2, LogType.MISC, securityException);
        }
        try {
            if (file.length() != file2.length()) {
                return false;
            }
            bl2 = true;
        }
        catch (SecurityException securityException) {
            LogHolder.log(2, LogType.MISC, securityException);
        }
        if (!bl && bl2) {
            return true;
        }
        try {
            if (!Util.arraysEqual(RecursiveFileTool.createMD5Digest(file), RecursiveFileTool.createMD5Digest(file2))) {
                return false;
            }
        }
        catch (IOException iOException) {
            LogHolder.log(2, LogType.MISC, iOException);
            return false;
        }
        catch (SecurityException securityException) {
            LogHolder.log(2, LogType.MISC, securityException);
        }
        return true;
    }

    public static long getFileSize(File file) throws SecurityException {
        if (file == null || !file.exists()) {
            return -1L;
        }
        return file.length();
    }

    public static byte[] createMD5Digest(File file) throws IOException, SecurityException {
        byte[] arrby = ResourceLoader.getStreamAsBytes(new FileInputStream(file));
        MD5Digest mD5Digest = new MD5Digest();
        byte[] arrby2 = new byte[mD5Digest.getDigestSize()];
        mD5Digest.update(arrby, 0, arrby.length);
        mD5Digest.doFinal(arrby2, 0);
        return arrby2;
    }
}

