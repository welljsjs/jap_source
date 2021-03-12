/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.LoadLibraryException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ConnectorUtils {
    private static Vector loadedLibraries = new Vector();
    static /* synthetic */ Class class$com$skype$connector$ConnectorUtils;

    public static void checkNotNull(String string, Object object) {
        if (object == null) {
            throw new NullPointerException("The " + string + " must not be null.");
        }
    }

    public static String extractFromJarToTemp(String string) {
        return ConnectorUtils.extractFromJar(string, string, System.getProperty("java.io.tmpdir"));
    }

    public static String extractFromJar(String string, String string2) {
        return ConnectorUtils.extractFromJar(string, string, string2);
    }

    public static String extractFromJar(String string, String string2, String string3) {
        String string4 = ConnectorUtils.extractFromJarZipMethod(string2, string2, string3);
        if (string4 == null) {
            string4 = ConnectorUtils.extractFromJarUsingClassLoader(string2, string2, string3);
        }
        return string4;
    }

    private static String extractFromJarZipMethod(String string, String string2, String string3) {
        String string4 = null;
        String string5 = ConnectorUtils.getExtendedClasspath();
        File file = null;
        byte[] arrby = new byte[1024];
        StringTokenizer stringTokenizer = new StringTokenizer(string5, File.pathSeparator);
        while (stringTokenizer.hasMoreTokens() && string4 == null) {
            String string6 = stringTokenizer.nextToken();
            file = new File(string6);
            if (!file.exists() || !file.isFile()) continue;
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(string6);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
                ZipEntry zipEntry = null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    int n;
                    File file2;
                    if (!zipEntry.getName().endsWith(string)) continue;
                    if (!string3.endsWith(File.separator)) {
                        string3 = string3 + File.separator;
                    }
                    if ((file2 = new File(string3 + string2)).exists()) {
                        file2.delete();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(string3 + string2);
                    while ((n = zipInputStream.read(arrby, 0, 1024)) > -1) {
                        fileOutputStream.write(arrby, 0, n);
                    }
                    fileOutputStream.close();
                    string4 = string3 + string2;
                    file2.deleteOnExit();
                }
            }
            catch (Exception exception) {
                string4 = null;
            }
        }
        return string4;
    }

    private static String extractFromJarUsingClassLoader(String string, String string2, String string3) {
        Object object;
        Object object2;
        Serializable serializable;
        ClassLoader classLoader = null;
        try {
            serializable = Class.forName("com.simontuffs.onejar.JarClassLoader");
            object2 = ((Class)serializable).getConstructors();
            object = new Object[]{ClassLoader.getSystemClassLoader()};
            classLoader = (ClassLoader)object2[1].newInstance((Object[])object);
        }
        catch (Throwable throwable) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        serializable = classLoader.getResource(string2);
        if (serializable == null) {
            return null;
        }
        if (!string3.endsWith(File.separator)) {
            string3 = string3 + File.separator;
        }
        try {
            object2 = new File(string3 + string2);
            if (((File)object2).exists()) {
                ((File)object2).delete();
            }
            object = ((URL)serializable).openStream();
            FileOutputStream fileOutputStream = new FileOutputStream(string3 + string2);
            byte[] arrby = new byte[4096];
            int n = ((InputStream)object).read(arrby);
            while (n > 0) {
                ((OutputStream)fileOutputStream).write(arrby, 0, n);
                n = ((InputStream)object).read(arrby);
            }
            ((OutputStream)fileOutputStream).close();
            ((InputStream)object).close();
            ((File)object2).deleteOnExit();
        }
        catch (Exception exception) {
            return null;
        }
        return string3 + string2;
    }

    public static boolean isInJar(String string) {
        boolean bl = false;
        String string2 = ConnectorUtils.getExtendedClasspath();
        File file = null;
        StringTokenizer stringTokenizer = new StringTokenizer(string2, File.pathSeparator);
        while (stringTokenizer.hasMoreTokens() && !bl) {
            String string3 = stringTokenizer.nextToken();
            file = new File(string3);
            if (!file.exists() || !file.isFile()) continue;
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(string3);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
                ZipEntry zipEntry = null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    if (!zipEntry.getName().endsWith(string)) continue;
                    bl = true;
                }
            }
            catch (Exception exception) {
                bl = false;
            }
        }
        return bl;
    }

    private static String getLibrarySearchPath() {
        return System.getProperty("java.library.path") + File.pathSeparatorChar + System.getProperty("user.dir") + File.pathSeparatorChar;
    }

    private static String getExtendedClasspath() {
        String string = System.getProperty("java.class.path") + File.pathSeparatorChar;
        String string2 = System.getProperty("user.dir");
        File file = new File(string2);
        if (!(string2 = file.getAbsolutePath()).endsWith(File.separator)) {
            string2 = string2 + File.separator;
        }
        String[] arrstring = file.list();
        for (int i = 0; i < arrstring.length; ++i) {
            if (!arrstring[i].endsWith("jar")) continue;
            string = string + File.pathSeparatorChar + string2 + arrstring[i];
        }
        return string;
    }

    public static boolean checkLibraryInPath(String string) {
        boolean bl = false;
        String string2 = ConnectorUtils.getLibrarySearchPath();
        File file = new File("");
        StringTokenizer stringTokenizer = new StringTokenizer(string2, File.pathSeparator);
        while (stringTokenizer.hasMoreTokens() && !bl) {
            file = new File(stringTokenizer.nextToken() + File.separatorChar + string);
            bl = file.exists();
        }
        return bl;
    }

    public static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] arrstring = file.list();
            for (int i = 0; i < arrstring.length; ++i) {
                boolean bl = ConnectorUtils.deleteDir(new File(file, arrstring[i]));
                if (bl) continue;
                return false;
            }
        }
        return file.delete();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadLibrary(String string) throws LoadLibraryException {
        Vector vector = loadedLibraries;
        synchronized (vector) {
            if (loadedLibraries.contains(string)) {
                return;
            }
            try {
                System.loadLibrary(string);
            }
            catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                File file;
                String string2 = System.mapLibraryName(string);
                URL uRL = (class$com$skype$connector$ConnectorUtils == null ? (class$com$skype$connector$ConnectorUtils = ConnectorUtils.class$("com.skype.connector.ConnectorUtils")) : class$com$skype$connector$ConnectorUtils).getResource("/" + string2);
                if (uRL.getProtocol().toLowerCase().equals("file")) {
                    try {
                        file = new File(URLDecoder.decode(uRL.getPath(), "UTF-8"));
                    }
                    catch (UnsupportedEncodingException unsupportedEncodingException) {
                        throw new LoadLibraryException("UTF-8 is not supported encoding.");
                    }
                } else {
                    ConnectorUtils.cleanUpOldLibraryFiles(string2);
                    file = ConnectorUtils.createTempLibraryFile(string2);
                }
                try {
                    System.load(file.getAbsolutePath());
                }
                catch (UnsatisfiedLinkError unsatisfiedLinkError2) {
                    throw new LoadLibraryException("Loading " + string2 + " failed.");
                }
            }
            loadedLibraries.add(string);
        }
    }

    private static void cleanUpOldLibraryFiles(String string) {
        final String string2 = string.substring(0, string.indexOf(46));
        final String string3 = string.substring(string.lastIndexOf(46));
        File[] arrfile = new File(System.getProperty("java.io.tmpdir")).listFiles(new FilenameFilter(){

            public boolean accept(File file, String string) {
                return string.startsWith(string2) && string.endsWith(string3);
            }
        });
        for (int i = 0; i < arrfile.length; ++i) {
            arrfile[i].delete();
        }
    }

    private static File createTempLibraryFile(String string) throws LoadLibraryException {
        InputStream inputStream = (class$com$skype$connector$ConnectorUtils == null ? (class$com$skype$connector$ConnectorUtils = ConnectorUtils.class$("com.skype.connector.ConnectorUtils")) : class$com$skype$connector$ConnectorUtils).getResourceAsStream("/" + string);
        if (inputStream == null) {
            throw new LoadLibraryException(string + " is not contained in the jar.");
        }
        FileOutputStream fileOutputStream = null;
        try {
            int n;
            String string2 = string.substring(0, string.indexOf(46));
            String string3 = string.substring(string.lastIndexOf(46));
            File file = File.createTempFile(string2, string3);
            file.deleteOnExit();
            fileOutputStream = new FileOutputStream(file);
            byte[] arrby = new byte[1024];
            while (0 < (n = inputStream.read(arrby))) {
                fileOutputStream.write(arrby, 0, n);
            }
            File file2 = file;
            return file2;
        }
        catch (IOException iOException) {
            throw new LoadLibraryException("Writing " + string + " failed.");
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException iOException) {}
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private ConnectorUtils() {
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

