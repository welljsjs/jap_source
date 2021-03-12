/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.IResourceInstantiator;
import anon.util.ResourceLoader;
import anon.util.URLCoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;

public final class ClassUtil {
    private static final String JAR_FILE = "jar:file:";
    private static final String FILE = "file:";
    private static Hashtable ms_loadedClasses = new Hashtable();
    private static Vector ms_loadedDirectories = new Vector();
    private static boolean ms_bEnableFindSubclasses = true;
    static /* synthetic */ Class class$java$io$File;
    static /* synthetic */ Class class$java$lang$ClassLoader;
    static /* synthetic */ Class class$java$net$URL;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class class$anon$util$ClassUtil;

    private ClassUtil() {
    }

    public static void addFileToClasspath(String string) throws IOException, IllegalAccessException {
        File file = new File(string);
        ClassUtil.addFileToClasspath(file);
    }

    public static void addFileToClasspath(File file) throws IllegalAccessException {
        URL uRL;
        try {
            uRL = (URL)(class$java$io$File == null ? (class$java$io$File = ClassUtil.class$("java.io.File")) : class$java$io$File).getMethod("toURL", new Class[0]).invoke(file, new Object[0]);
        }
        catch (Exception exception) {
            throw new IllegalAccessException(exception.getMessage());
        }
        ClassUtil.addURLToClasspath(uRL);
    }

    public static void addURLToClasspath(URL uRL) throws IllegalAccessException {
        try {
            Object object = (class$java$lang$ClassLoader == null ? (class$java$lang$ClassLoader = ClassUtil.class$("java.lang.ClassLoader")) : class$java$lang$ClassLoader).getMethod("getSystemClassLoader", new Class[0]).invoke(null, new Object[0]);
            Class<?> class_ = Class.forName("java.net.URLClassLoader");
            Method method = class_.getDeclaredMethod("addURL", class$java$net$URL == null ? (class$java$net$URL = ClassUtil.class$("java.net.URL")) : class$java$net$URL);
            (class$java$lang$reflect$Method == null ? (class$java$lang$reflect$Method = ClassUtil.class$("java.lang.reflect.Method")) : class$java$lang$reflect$Method).getMethod("setAccessible", Boolean.TYPE).invoke(method, new Boolean(true));
            method.invoke(object, uRL);
        }
        catch (Throwable throwable) {
            throw new IllegalAccessException("Error, could not add URL to system classloader");
        }
    }

    public static String getShortClassName(Class class_) {
        String string = class_.getName();
        int n = string.lastIndexOf(46);
        if (n >= 0) {
            string = string.substring(n + 1, string.length());
        }
        return string;
    }

    public static Class getClassStatic() {
        return new ClassGetter().getCurrentClassStatic();
    }

    public static String getClassNameStatic() {
        Class class_ = ClassUtil.getCallingClassStatic();
        if (class_ == null) {
            return "UNKNOWN";
        }
        return class_.getName();
    }

    public static Class getCallingClassStatic() {
        return new ClassGetter().getCallingClassStatic();
    }

    public static String getUserDir() {
        try {
            return System.getProperty("user.dir");
        }
        catch (SecurityException securityException) {
            return new File(".").toString();
        }
    }

    public static String getClassPath() {
        return ClassUtil.getClassPath(false);
    }

    public static void enableFindSubclasses(boolean bl) {
        ms_bEnableFindSubclasses = bl;
    }

    public static boolean isFindSubclassesEnabled() {
        return ms_bEnableFindSubclasses;
    }

    public static Vector findSubclasses(Class class_) {
        if (!ms_bEnableFindSubclasses) {
            return new Vector();
        }
        ClassUtil.loadClasses(class_);
        Enumeration enumeration = ClassUtil.loadClasses(ClassUtil.getCallingClassStatic());
        Vector<Class> vector = new Vector<Class>();
        while (enumeration.hasMoreElements()) {
            Class class_2 = (Class)enumeration.nextElement();
            if (!class_.isAssignableFrom(class_2)) continue;
            vector.addElement(class_2);
        }
        return vector;
    }

    public static Enumeration loadClasses() {
        Class class_ = ClassUtil.getCallingClassStatic();
        ClassUtil.loadClasses(class_);
        return ms_loadedClasses.elements();
    }

    public static Enumeration loadClasses(Class class_) {
        return ClassUtil.loadClasses(class_, null);
    }

    public static Enumeration loadClasses(File file) {
        return ClassUtil.loadClasses(null, file);
    }

    private static Enumeration loadClasses(Class class_, File file) {
        block6: {
            PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
            Class class_2 = ClassUtil.getClassStatic();
            Class class_3 = ClassUtil.getCallingClassStatic();
            PrintStream printStream2 = System.err;
            try {
                if (file != null) {
                    System.setErr(printStream);
                    ClassUtil.loadClassesInternal(class_, file);
                    System.setErr(printStream2);
                } else if (class_ != null) {
                    System.setErr(printStream);
                    ClassUtil.loadClassesInternal(class_, file);
                    System.setErr(printStream2);
                    ClassUtil.loadClassesInternal(class_2, null);
                    if (class_3 != class_ && class_3 != class_2) {
                        ClassUtil.loadClassesInternal(class_3, null);
                    }
                }
            }
            catch (Throwable throwable) {
                System.setErr(printStream2);
                if (!(throwable instanceof Exception) || throwable instanceof RuntimeException) break block6;
                throwable.printStackTrace();
            }
        }
        return ms_loadedClasses.elements();
    }

    public static File getClassDirectory(String string) {
        if (string == null) {
            return null;
        }
        try {
            return ClassUtil.getClassDirectory(Class.forName(string));
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    public static File getFile() {
        ZipFile zipFile = ClassUtil.getJarFile();
        if (zipFile == null) {
            return null;
        }
        return new File(zipFile.getName());
    }

    public static ZipFile getJarFile() {
        return ClassUtil.getJarFile(class$anon$util$ClassUtil == null ? (class$anon$util$ClassUtil = ClassUtil.class$("anon.util.ClassUtil")) : class$anon$util$ClassUtil);
    }

    public static ZipFile getJarFile(Class class_) {
        if (class_ == null) {
            return null;
        }
        File file = null;
        try {
            file = ClassUtil.getClassDirectory(class_);
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "An exception occured while opening the JAR file: ", exception);
        }
        if (file != null && file.getPath().endsWith(".jar")) {
            try {
                return new ZipFile(file);
            }
            catch (IOException iOException) {
                LogHolder.log(3, LogType.MISC, "An I/O error occured while opening the JAR file: ", iOException);
            }
        }
        return null;
    }

    public static File getClassDirectory(Class class_) throws NullPointerException {
        String string = "/" + ClassUtil.toRelativeResourcePath(class_);
        return ClassUtil.getResourceDirectory(string, class_);
    }

    public static File getResourceDirectory(String string, Class class_) throws NullPointerException {
        URL uRL = class_.getResource(string);
        if (uRL == null) {
            LogHolder.log(0, LogType.MISC, "Get class resource failed for resource: " + string);
            throw new NullPointerException("Class resource not found: " + string);
        }
        File file = ResourceLoader.getSystemResource(uRL.toString());
        if (file == null) {
            String string2 = URLCoder.decode(uRL.toString());
            if (string2.startsWith(JAR_FILE)) {
                if ((string2 = string2.substring(JAR_FILE.length(), string2.lastIndexOf(string) - 1)).charAt(2) == ':') {
                    string2 = string2.substring(1, string2.length());
                }
                string2 = ResourceLoader.replaceFileSeparatorsSystemSpecific(string2);
                file = new File(string2);
            } else if (string2.startsWith(FILE)) {
                string2 = string2.substring(FILE.length(), string2.lastIndexOf(string));
                file = new File(string2);
            } else {
                file = null;
            }
            if (file == null || !file.exists()) {
                return null;
            }
        }
        return file;
    }

    public static String toRelativeResourcePath(Class class_) {
        String string = class_.getName();
        string = string.replace('.', '/');
        string = string + ".class";
        return string;
    }

    public static Class getFirstClassFound(File file) {
        Hashtable hashtable = new Hashtable();
        ResourceLoader.loadResources("/", file, new ClassInstantiator(3), true, true, hashtable);
        if (hashtable.size() == 1) {
            return (Class)hashtable.elements().nextElement();
        }
        return null;
    }

    protected static String getClassPath(boolean bl) {
        String string = "";
        if (!bl) {
            try {
                string = ClassUtil.getClassDirectory(class$anon$util$ClassUtil == null ? (class$anon$util$ClassUtil = ClassUtil.class$("anon.util.ClassUtil")) : class$anon$util$ClassUtil).toString() + File.pathSeparator;
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (string == null || string.length() == 0) {
                try {
                    string = ClassUtil.getResourceDirectory("/JAPMessages_de.properties", class$anon$util$ClassUtil == null ? (class$anon$util$ClassUtil = ClassUtil.class$("anon.util.ClassUtil")) : class$anon$util$ClassUtil).toString() + File.pathSeparator;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        try {
            return string + System.getProperty("java.class.path");
        }
        catch (SecurityException securityException) {
            return string;
        }
    }

    private static void loadClassesInternal(Class class_, File file) throws IOException {
        File file2;
        if (file != null) {
            file2 = file;
        } else if (class_ == null || class_.getName().startsWith("java.") || class_.getName().startsWith("javax.") || (file2 = ClassUtil.getClassDirectory(class_)) == null) {
            return;
        }
        if (ms_loadedDirectories.contains(file2.getAbsolutePath())) {
            return;
        }
        ms_loadedDirectories.addElement(file2.getAbsolutePath());
        ResourceLoader.loadResources("/", file2, new ClassInstantiator(), true, false, ms_loadedClasses);
    }

    private static Class toClass(File file, File file2) {
        Class<?> class_;
        String string;
        if (file == null || !file.getName().endsWith(".class")) {
            return null;
        }
        int n = file2 == null || !file2.isDirectory() ? 0 : ((string = file2.toString()).endsWith(System.getProperty("file.separator")) ? string.length() : string.length() + 1);
        try {
            String string2 = file.toString();
            string2 = string2.substring(n, string2.lastIndexOf(".class"));
            string2 = string2.replace(File.separatorChar, '.');
            class_ = Class.forName(string2);
        }
        catch (Throwable throwable) {
            class_ = null;
        }
        return class_;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private static class ClassInstantiator
    implements IResourceInstantiator {
        private int m_invalidAfterFailure;
        private int m_currentFailure;

        public ClassInstantiator() {
            this.m_invalidAfterFailure = 0;
            this.m_currentFailure = 0;
        }

        public ClassInstantiator(int n) {
            this.m_invalidAfterFailure = n;
            this.m_currentFailure = 0;
        }

        public Object getInstance(File file, File file2) throws IResourceInstantiator.ResourceInstantiationException {
            Class class_ = ClassUtil.toClass(file, file2);
            if (this.m_invalidAfterFailure > 0) {
                this.checkValidity(class_, file.getName());
            }
            return class_;
        }

        public Object getInstance(ZipEntry zipEntry, ZipFile zipFile) throws IResourceInstantiator.ResourceInstantiationException {
            Class class_ = ClassUtil.toClass(new File(zipEntry.toString()), null);
            if (this.m_invalidAfterFailure > 0) {
                this.checkValidity(class_, zipEntry.getName());
            }
            return class_;
        }

        private void checkValidity(Class class_, String string) throws IResourceInstantiator.ResourceInstantiationException {
            if (class_ == null && string.endsWith(".class")) {
                ++this.m_currentFailure;
            }
            if (this.m_currentFailure >= this.m_invalidAfterFailure) {
                throw new IResourceInstantiator.ResourceInstantiationException();
            }
        }

        public Object getInstance(InputStream inputStream, String string) {
            return null;
        }
    }

    private static class ClassGetter
    extends SecurityManager {
        private ClassGetter() {
        }

        public Class getCurrentClassStatic() {
            Class<?>[] arrclass = this.getClassContext();
            if (arrclass == null || arrclass.length < 3) {
                return null;
            }
            return arrclass[2];
        }

        public Class getCallingClassStatic() {
            Class<?>[] arrclass = this.getClassContext();
            if (arrclass == null || arrclass.length < 4) {
                return null;
            }
            return arrclass[3];
        }
    }

    public static final class Package {
        private String m_strPackage;

        public Package(Class class_) {
            this.m_strPackage = class_ == null || class_.getName().indexOf(".") < 0 ? "" : class_.getName().substring(0, class_.getName().lastIndexOf("."));
        }

        public Package(String string) {
            if (this.m_strPackage == null || this.m_strPackage.trim().length() == 0) {
                this.m_strPackage = "";
            } else {
                if (new StringTokenizer(this.m_strPackage).countTokens() > 1) {
                    throw new IllegalArgumentException("Package names may not contain whitespaces!");
                }
                for (int i = 0; i < this.m_strPackage.length(); ++i) {
                    if (Character.isLetterOrDigit(this.m_strPackage.charAt(i)) || this.m_strPackage.charAt(i) == '.') continue;
                    if (this.m_strPackage.charAt(i) == '\\' && this.m_strPackage.length() > i + 5 && this.m_strPackage.charAt(i + 1) == 'u') {
                        boolean bl = true;
                        for (int j = i + 2; j < i + 5; ++j) {
                            if (Character.isDigit(this.m_strPackage.charAt(j))) continue;
                            bl = false;
                            break;
                        }
                        if (bl) {
                            i += 5;
                            continue;
                        }
                    }
                    throw new IllegalArgumentException("Illegal character in package name: " + this.m_strPackage.charAt(i));
                }
                this.m_strPackage = string;
            }
        }

        public String getPackage() {
            return this.m_strPackage;
        }
    }
}

