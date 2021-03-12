/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.ClassUtil;
import anon.util.IResourceInstantiator;
import anon.util.IResourceLoaderHelper;
import anon.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;

public final class ResourceLoader {
    public static final String SYSTEM_RESOURCE_TYPE_ZIP = "zip";
    public static final String SYSTEM_RESOURCE_TYPE_JAR = "jar";
    public static final String SYSTEM_RESOURCE_TYPE_FILE = "file";
    public static final String SYSTEM_RESOURCE_TYPE_GENERIC = "systemresource";
    private static final String SYSTEM_RESOURCE = "systemresource:/";
    private static final String SYSTEM_RESOURCE_ENDSIGN = "/+/";
    private static final String DIR_UP = "../";
    private static final int READ_BUFFER = 2000;
    private static final String RESOURCE_NO_CLASSES_FOUND = "";
    private static Vector ms_classpathFiles;
    private static Object ms_classpathResourceLock;
    private static Vector ms_classpathResourceTypes;
    private static File ms_parentResourceFile;
    private static boolean ms_bTriedToLoadParentResourceFile;
    private static final Object SYNC_RESOURCE;
    private static String ms_parentResourceFileResourceURL;
    private static String ms_parentResourceFileResourceType;
    private static String ms_classpath;
    private static IResourceLoaderHelper ms_ResourceLoaderHelper;
    static /* synthetic */ Class class$anon$util$ResourceLoader;

    private ResourceLoader() {
        try {
            ms_parentResourceFile = new File(ClassUtil.getClassDirectory(class$anon$util$ResourceLoader == null ? (class$anon$util$ResourceLoader = ResourceLoader.class$("anon.util.ResourceLoader")) : class$anon$util$ResourceLoader).getAbsolutePath());
        }
        catch (Exception exception) {
            ms_parentResourceFile = null;
        }
    }

    public static void setReourceLoaderHelper(IResourceLoaderHelper iResourceLoaderHelper) {
        ms_ResourceLoaderHelper = iResourceLoaderHelper;
    }

    public static Vector getFilesInClassPath() {
        try {
            return (Vector)ms_classpathFiles.clone();
        }
        catch (NullPointerException nullPointerException) {
            return null;
        }
    }

    public static byte[] getStreamAsBytes(InputStream inputStream) throws IOException {
        int n = 1;
        if (inputStream == null) {
            throw new IOException("Stream is null!");
        }
        byte[] arrby = new byte[]{};
        while (n >= 0) {
            byte[] arrby2 = inputStream.available() > 0 ? new byte[inputStream.available()] : new byte[2000];
            n = inputStream.read(arrby2);
            arrby = ResourceLoader.trimByteArray(arrby2, n, arrby);
        }
        inputStream.close();
        return arrby;
    }

    public static URL getResourceURL(String string) {
        File file;
        URL uRL = null;
        if ((string = ResourceLoader.formatResourcePath(string)) == null || string.endsWith("/")) {
            return null;
        }
        uRL = (class$anon$util$ResourceLoader == null ? (class$anon$util$ResourceLoader = ResourceLoader.class$("anon.util.ResourceLoader")) : class$anon$util$ResourceLoader).getResource("/" + string);
        if (uRL == null && (file = new File(string)).exists() && file.canRead()) {
            try {
                uRL = new URL("file:" + file.getAbsolutePath());
            }
            catch (MalformedURLException malformedURLException) {
                // empty catch block
            }
        }
        if (uRL == null && ResourceLoader.getParentResourceFile() != null && !ResourceLoader.readFilesFromClasspath(false).contains(ResourceLoader.getParentResourceFile())) {
            Vector<File> vector = new Vector<File>();
            Vector<String> vector2 = new Vector<String>();
            Vector<String> vector3 = new Vector<String>();
            vector.addElement(ResourceLoader.getParentResourceFile());
            vector2.addElement(ms_parentResourceFileResourceURL);
            vector3.addElement(ms_parentResourceFileResourceType);
            uRL = ResourceLoader.getResourceURL(string, vector, vector2, vector3);
            ms_parentResourceFileResourceURL = (String)vector2.firstElement();
            ms_parentResourceFileResourceType = (String)vector3.firstElement();
        }
        return uRL;
    }

    public static InputStream loadResourceAsStream(String string) {
        return ResourceLoader.loadResourceAsStream(string, false);
    }

    public static InputStream loadResourceAsStream(String string, boolean bl) {
        String string2;
        File file;
        InputStream inputStream = null;
        if ((string = ResourceLoader.formatResourcePath(string)) == null || string.endsWith("/")) {
            return null;
        }
        if (bl && ResourceLoader.getParentResourceFile() != null) {
            try {
                file = ResourceLoader.getParentResourceFile();
                if (file.isFile() && (string2 = file.getParent()) != null) {
                    file = new File(string2);
                }
                inputStream = new FileInputStream(new File(file, ResourceLoader.replaceFileSeparatorsSystemSpecific(string)));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            if (inputStream == null) {
                inputStream = (class$anon$util$ResourceLoader == null ? (class$anon$util$ResourceLoader = ResourceLoader.class$("anon.util.ResourceLoader")) : class$anon$util$ResourceLoader).getResourceAsStream("/" + string);
            }
        }
        catch (Throwable throwable) {
            LogHolder.log(3, LogType.MISC, throwable);
        }
        try {
            if (inputStream == null && !bl && ResourceLoader.getParentResourceFile() != null && !ResourceLoader.readFilesFromClasspath(false).contains(ResourceLoader.getParentResourceFile())) {
                file = ResourceLoader.getParentResourceFile();
                if (file.isFile() && (string2 = file.getParent()) != null) {
                    file = new File(string2);
                }
                inputStream = new FileInputStream(new File(file, ResourceLoader.replaceFileSeparatorsSystemSpecific(string)));
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            if (inputStream == null) {
                inputStream = new FileInputStream(string);
            }
            return inputStream;
        }
        catch (IOException iOException) {
            return null;
        }
    }

    public static byte[] loadResource(String string) {
        InputStream inputStream = ResourceLoader.loadResourceAsStream(string);
        byte[] arrby = null;
        if (inputStream == null) {
            return null;
        }
        try {
            arrby = ResourceLoader.getStreamAsBytes(inputStream);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        Util.closeStream(inputStream);
        return arrby;
    }

    public static Hashtable loadResources(String string, boolean bl) {
        return ResourceLoader.loadResources(string, new ResourceLoader().createByteArrayInstantiator(), bl);
    }

    public static Hashtable loadResources(String string, IResourceInstantiator iResourceInstantiator, boolean bl) {
        Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
        Object object = null;
        InputStream inputStream = null;
        try {
            inputStream = ResourceLoader.loadResourceAsStream(string);
            object = iResourceInstantiator.getInstance(inputStream, string);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
        }
        Util.closeStream(inputStream);
        if (object != null) {
            hashtable.put(string, object);
            return hashtable;
        }
        Enumeration enumeration = ResourceLoader.readFilesFromClasspath(false).elements();
        while (enumeration.hasMoreElements()) {
            ResourceLoader.loadResources(string, (File)enumeration.nextElement(), iResourceInstantiator, bl, false, hashtable);
        }
        ResourceLoader.loadResources(string, new File(ClassUtil.getUserDir()), iResourceInstantiator, bl, false, hashtable);
        if (ms_ResourceLoaderHelper != null) {
            ms_ResourceLoaderHelper.loadResources(string, null, iResourceInstantiator, bl, false, hashtable);
        }
        return hashtable;
    }

    public static Hashtable loadResources(String string, File file, boolean bl) {
        Hashtable hashtable = new Hashtable();
        ResourceLoader.loadResources(string, file, new ResourceLoader().createByteArrayInstantiator(), bl, false, hashtable);
        return hashtable;
    }

    public static Hashtable loadResources(String string, File file, IResourceInstantiator iResourceInstantiator, boolean bl) {
        Hashtable hashtable = new Hashtable();
        ResourceLoader.loadResources(string, file, iResourceInstantiator, bl, false, hashtable);
        return hashtable;
    }

    public static String replaceFileSeparatorsSystemSpecific(String string) {
        if (string == null) {
            return null;
        }
        string = string.replace('/', File.separatorChar);
        string = string.replace('\\', File.separatorChar);
        return string;
    }

    protected static File getSystemResource(String string) {
        if (string.indexOf(SYSTEM_RESOURCE) != 0) {
            return null;
        }
        if ((string = string.substring(SYSTEM_RESOURCE.length(), string.length())).toLowerCase().startsWith(SYSTEM_RESOURCE_TYPE_ZIP)) {
            string = string.substring(SYSTEM_RESOURCE_TYPE_ZIP.length(), string.length());
        } else if (string.toLowerCase().startsWith(SYSTEM_RESOURCE_TYPE_JAR)) {
            string = string.substring(SYSTEM_RESOURCE_TYPE_JAR.length(), string.length());
        } else if (string.toLowerCase().startsWith(SYSTEM_RESOURCE_TYPE_FILE)) {
            string = string.substring(SYSTEM_RESOURCE_TYPE_FILE.length(), string.length());
        }
        int n = string.indexOf(SYSTEM_RESOURCE_ENDSIGN);
        if (n >= 0) {
            string = string.substring(0, n);
        }
        try {
            int n2 = Integer.parseInt(string);
            return (File)ResourceLoader.readFilesFromClasspath(true).elementAt(n2);
        }
        catch (Exception exception) {
            return new File(string);
        }
    }

    protected static void loadResources(String string, File file, IResourceInstantiator iResourceInstantiator, boolean bl, boolean bl2, Hashtable hashtable) {
        if ((string = ResourceLoader.formatResourcePath(string)) == null || hashtable == null || file == null || iResourceInstantiator == null || !file.exists() || !file.canRead()) {
            return;
        }
        try {
            Enumeration<ZipEntry> enumeration;
            ZipEntry zipEntry;
            if (file.isDirectory()) {
                throw new IOException("This is a directory.");
            }
            ZipFile zipFile = new ZipFile(file);
            if (!string.endsWith("/")) {
                zipEntry = zipFile.getEntry(string);
                if (zipEntry == null) {
                    throw new IOException("Requested entry not found.");
                }
                Vector<ZipEntry> vector = new Vector<ZipEntry>();
                vector.addElement(zipEntry);
                enumeration = vector.elements();
            } else {
                enumeration = zipFile.entries();
            }
            while (enumeration.hasMoreElements()) {
                String string2;
                zipEntry = enumeration.nextElement();
                if (zipEntry.isDirectory() || !ResourceLoader.isResourceInSearchPath(zipEntry.toString(), string, bl)) continue;
                Object object = null;
                try {
                    object = iResourceInstantiator.getInstance(zipEntry, zipFile);
                }
                catch (IResourceInstantiator.ResourceInstantiationException resourceInstantiationException) {
                    return;
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (object == null || hashtable.containsKey(string2 = ResourceLoader.getCurrentResourcePath(zipEntry))) continue;
                hashtable.put(string2, object);
                if (string.endsWith("/") && !bl2) continue;
                return;
            }
        }
        catch (Exception exception) {
            try {
                ResourceLoader.loadResourcesFromFile(string, file, file, iResourceInstantiator, hashtable, bl, bl2);
            }
            catch (IResourceInstantiator.ResourceInstantiationException resourceInstantiationException) {
                return;
            }
        }
    }

    private static void loadResourcesFromFile(String string, File file, File file2, IResourceInstantiator iResourceInstantiator, Hashtable hashtable, boolean bl, boolean bl2) throws IResourceInstantiator.ResourceInstantiationException {
        if ((!string.endsWith("/") || bl2) && hashtable.size() > 0) {
            return;
        }
        if (file != null && file.exists()) {
            String string2 = ResourceLoader.getCurrentResourcePath(file, file2);
            if (string2.indexOf(string) != 0 && !string.equals("/")) {
                file = new File(file2, ResourceLoader.replaceFileSeparatorsSystemSpecific(string));
                ResourceLoader.loadResourcesFromFile(string, file, file2, iResourceInstantiator, hashtable, bl, bl2);
                return;
            }
            if (file.isFile() && ResourceLoader.isResourceInSearchPath(string2, string, bl)) {
                Object object = null;
                if (hashtable.containsKey(string2)) {
                    return;
                }
                try {
                    object = iResourceInstantiator.getInstance(file, file2);
                }
                catch (IResourceInstantiator.ResourceInstantiationException resourceInstantiationException) {
                    throw resourceInstantiationException;
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (object != null) {
                    hashtable.put(string2, object);
                    if (!string.endsWith("/") || bl2) {
                        return;
                    }
                }
            } else if (file.isDirectory() && ResourceLoader.isResourceInSearchPath(string2, string, bl)) {
                String[] arrstring = file.list();
                for (int i = 0; i < arrstring.length; ++i) {
                    String string3 = RESOURCE_NO_CLASSES_FOUND + File.separatorChar;
                    if (file.getAbsolutePath().endsWith(string3)) {
                        string3 = RESOURCE_NO_CLASSES_FOUND;
                    }
                    ResourceLoader.loadResourcesFromFile(string, new File(file.getAbsolutePath() + string3 + arrstring[i]), file2, iResourceInstantiator, hashtable, bl, bl2);
                }
            }
        }
    }

    private static URL getResourceURL(String string, Vector vector, Vector vector2, Vector vector3) {
        Enumeration enumeration = vector.elements();
        FileTypeInstantiator fileTypeInstantiator = new ResourceLoader().createFileTypeInstantiator();
        int n = 0;
        while (enumeration.hasMoreElements()) {
            block6: {
                String string2;
                block11: {
                    File file;
                    block10: {
                        block9: {
                            block7: {
                                Class class_;
                                block8: {
                                    file = (File)enumeration.nextElement();
                                    string2 = (String)vector2.elementAt(n);
                                    if (string2 != null) break block7;
                                    class_ = ClassUtil.getFirstClassFound(file);
                                    if (class_ != null) break block8;
                                    vector2.setElementAt(RESOURCE_NO_CLASSES_FOUND, n);
                                    break block6;
                                }
                                String string3 = ClassUtil.toRelativeResourcePath(class_);
                                Hashtable hashtable = new Hashtable();
                                ResourceLoader.loadResources(string3, file, fileTypeInstantiator, false, true, hashtable);
                                vector3.setElementAt(hashtable.elements().nextElement(), n);
                                string3 = "/" + string3;
                                string2 = class_.getResource(string3).toString();
                                if (!string2.endsWith(string3)) break block6;
                                string2 = string2.substring(0, string2.lastIndexOf(string3));
                                vector2.setElementAt(string2, n);
                                break block9;
                            }
                            if (string2.trim().equals(RESOURCE_NO_CLASSES_FOUND)) break block6;
                        }
                        if (!vector3.elementAt(n).equals(SYSTEM_RESOURCE_TYPE_FILE)) break block10;
                        File file2 = new File(file, ResourceLoader.replaceFileSeparatorsSystemSpecific(string));
                        if (file2.exists()) break block11;
                        break block6;
                    }
                    try {
                        if (new ZipFile(file).getEntry(string) == null) break block6;
                    }
                    catch (Exception exception) {
                        break block6;
                    }
                }
                if (!string.startsWith("/")) {
                    string = "/" + string;
                }
                try {
                    return new URL(string2 + string);
                }
                catch (MalformedURLException malformedURLException) {
                    // empty catch block
                }
            }
            ++n;
        }
        return null;
    }

    private static String getCurrentResourcePath(File file, File file2) {
        if (file.toString().equals(file2.toString())) {
            return "/";
        }
        int n = 1;
        if (file2.toString().endsWith(File.separator)) {
            n = 0;
        }
        String string = file.toString().substring(file2.toString().length() + n, file.toString().length());
        string = string.replace('\\', '/');
        if (file.isDirectory() && !string.endsWith("/")) {
            string = string + "/";
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static File getParentResourceFile() {
        if (ms_parentResourceFile == null && !ms_bTriedToLoadParentResourceFile) {
            Object object = SYNC_RESOURCE;
            synchronized (object) {
                if (!ms_bTriedToLoadParentResourceFile) {
                    ms_bTriedToLoadParentResourceFile = true;
                    try {
                        ms_parentResourceFile = new File(ClassUtil.getClassDirectory(class$anon$util$ResourceLoader == null ? (class$anon$util$ResourceLoader = ResourceLoader.class$("anon.util.ResourceLoader")) : class$anon$util$ResourceLoader).getAbsolutePath());
                    }
                    catch (Throwable throwable) {
                        LogHolder.log(2, LogType.GUI, "Exception in ResourceLoader.getParentResourceFile(): " + throwable.getMessage(), throwable);
                    }
                }
            }
        }
        return ms_parentResourceFile;
    }

    private static String getCurrentResourcePath(ZipEntry zipEntry) {
        if (zipEntry.isDirectory() && !zipEntry.toString().endsWith("/")) {
            return zipEntry.toString() + "/";
        }
        return zipEntry.toString();
    }

    private static boolean isResourceInSearchPath(String string, String string2, boolean bl) {
        if (string.equals(string2) || string.equals("/")) {
            return true;
        }
        if (string2.equals("/")) {
            if (bl) {
                return true;
            }
            if (string.indexOf("/") >= 0) {
                return false;
            }
        }
        if (string.length() <= string2.length()) {
            return false;
        }
        if (string.startsWith(string2) && string2.endsWith("/")) {
            if (bl) {
                return true;
            }
            if (string.substring(string2.length()).indexOf("/") < 0) {
                return true;
            }
        }
        return false;
    }

    public static String formatResourcePath(String string) {
        int n;
        if (string == null) {
            return null;
        }
        if ((string = string.trim().replace('\\', '/')).equals("/")) {
            return string;
        }
        if (string.length() == 0 || string.startsWith("/")) {
            return null;
        }
        while ((n = string.indexOf("/../")) >= 0) {
            if (string.startsWith(DIR_UP)) {
                return null;
            }
            String string2 = string.substring(0, n);
            int n2 = string2.lastIndexOf("/");
            string2 = n2 >= 0 ? string2.substring(0, n2 + 1) : "/";
            string = string2 = string2 + string.substring(n + "/../".length(), string.length());
            while (string.startsWith("/") && !string.equals("/")) {
                string = string.substring(1, string.length());
            }
        }
        if (string.startsWith(DIR_UP)) {
            return null;
        }
        return string;
    }

    private static byte[] trimByteArray(byte[] arrby, int n, byte[] arrby2) {
        byte[] arrby3;
        if (n <= 0) {
            arrby3 = arrby2;
        } else {
            int n2 = arrby.length > n ? n : arrby.length;
            arrby3 = new byte[arrby2.length + n2];
            System.arraycopy(arrby2, 0, arrby3, 0, arrby2.length);
            System.arraycopy(arrby, 0, arrby3, arrby2.length, n2);
        }
        return arrby3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Vector readFilesFromClasspath(boolean bl) {
        String string = ClassUtil.getClassPath(bl);
        if (ms_classpath == null || !ms_classpath.equals(string)) {
            Object object = ms_classpathResourceLock;
            synchronized (object) {
                ms_classpath = string;
                ms_classpathFiles = new Vector();
                ms_classpathResourceTypes = new Vector();
                StringTokenizer stringTokenizer = new StringTokenizer(ms_classpath, File.pathSeparator);
                while (stringTokenizer.hasMoreTokens()) {
                    File file = new File(new File(stringTokenizer.nextToken()).getAbsolutePath());
                    if (ms_classpathFiles.contains(file)) continue;
                    ms_classpathFiles.addElement(file);
                    ms_classpathResourceTypes.addElement(null);
                }
            }
        }
        return ms_classpathFiles;
    }

    private ByteArrayInstantiator createByteArrayInstantiator() {
        return new ByteArrayInstantiator();
    }

    private FileTypeInstantiator createFileTypeInstantiator() {
        return new FileTypeInstantiator();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        ms_classpathResourceLock = new Object();
        ms_bTriedToLoadParentResourceFile = false;
        SYNC_RESOURCE = new Object();
        ms_ResourceLoaderHelper = null;
    }

    private final class FileTypeInstantiator
    implements IResourceInstantiator {
        private FileTypeInstantiator() {
        }

        public Object getInstance(File file, File file2) {
            return ResourceLoader.SYSTEM_RESOURCE_TYPE_FILE;
        }

        public Object getInstance(ZipEntry zipEntry, ZipFile zipFile) {
            return ResourceLoader.SYSTEM_RESOURCE_TYPE_ZIP;
        }

        public Object getInstance(InputStream inputStream, String string) {
            return null;
        }
    }

    private final class ByteArrayInstantiator
    implements IResourceInstantiator {
        private ByteArrayInstantiator() {
        }

        public Object getInstance(File file, File file2) throws IOException {
            return ResourceLoader.getStreamAsBytes(new FileInputStream(file));
        }

        public Object getInstance(ZipEntry zipEntry, ZipFile zipFile) throws IOException {
            return ResourceLoader.getStreamAsBytes(zipFile.getInputStream(zipEntry));
        }

        public Object getInstance(InputStream inputStream, String string) throws IOException {
            return ResourceLoader.getStreamAsBytes(inputStream);
        }
    }
}

