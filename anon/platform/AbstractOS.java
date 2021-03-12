/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import anon.infoservice.ProxyInterface;
import anon.platform.VMPerfDataFile;
import anon.util.ClassUtil;
import anon.util.IPasswordReader;
import anon.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractOS {
    public static final String URL_MAIL_TO = "mailto:";
    private static Class[] REGISTERED_PLATFORM_CLASSES = new Class[]{class$anon$platform$AndroidOS == null ? (class$anon$platform$AndroidOS = AbstractOS.class$("anon.platform.AndroidOS")) : class$anon$platform$AndroidOS, class$anon$platform$LinuxOS == null ? (class$anon$platform$LinuxOS = AbstractOS.class$("anon.platform.LinuxOS")) : class$anon$platform$LinuxOS, class$anon$platform$WindowsOS == null ? (class$anon$platform$WindowsOS = AbstractOS.class$("anon.platform.WindowsOS")) : class$anon$platform$WindowsOS, class$anon$platform$MacOS == null ? (class$anon$platform$MacOS = AbstractOS.class$("anon.platform.MacOS")) : class$anon$platform$MacOS, class$anon$platform$UnknownOS == null ? (class$anon$platform$UnknownOS = AbstractOS.class$("anon.platform.UnknownOS")) : class$anon$platform$UnknownOS};
    private static final String[] BROWSERLIST = new String[]{"firefox", "mozilla-firefox", "chrome", "opera", "mozilla", "iexplore", "explorer", "konqueror"};
    private static final String WHITESPACE_ENCODED = "%20";
    private static AbstractOS ms_operatingSystem;
    private IURLErrorNotifier m_notifier;
    private AbstractURLOpener m_URLOpener;
    private Properties m_envVars;
    private static File ms_tmpDir;
    private static int ms_iJavaWebstart;
    static /* synthetic */ Class class$anon$platform$AndroidOS;
    static /* synthetic */ Class class$anon$platform$LinuxOS;
    static /* synthetic */ Class class$anon$platform$WindowsOS;
    static /* synthetic */ Class class$anon$platform$MacOS;
    static /* synthetic */ Class class$anon$platform$UnknownOS;

    public static final synchronized AbstractOS getInstance() {
        for (int i = 0; ms_operatingSystem == null && i < REGISTERED_PLATFORM_CLASSES.length; ++i) {
            try {
                ms_operatingSystem = (AbstractOS)REGISTERED_PLATFORM_CLASSES[i].newInstance();
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.MISC, "Cannot instantiate class " + REGISTERED_PLATFORM_CLASSES[i] + ". Trying to instanciate another platform class.");
            }
            if (ms_operatingSystem == null) continue;
            AbstractOS.ms_operatingSystem.m_notifier = new IURLErrorNotifier(){

                public void checkNotify(URL uRL) {
                }
            };
        }
        return ms_operatingSystem;
    }

    public static String createBrowserCommand(String string) {
        string = Util.replaceAll(string, "/", File.separator);
        StringBuffer stringBuffer = new StringBuffer("");
        int n = string.indexOf(WHITESPACE_ENCODED, 0);
        int n2 = 0;
        while (n != -1) {
            stringBuffer.append(string.substring(n2, n));
            stringBuffer.append(" ");
            n2 = n + WHITESPACE_ENCODED.length();
            n = string.indexOf(WHITESPACE_ENCODED, n + 1);
        }
        stringBuffer.append(string.substring(n2));
        string = AbstractOS.toAbsolutePath(stringBuffer.toString());
        return string;
    }

    public static String toRelativePath(String string) {
        if (string == null) {
            return null;
        }
        String string2 = System.getProperty("user.dir");
        String string3 = "";
        if (string.substring(1, 3).equals(":\\") && !string2.substring(0, 3).equals(string.substring(0, 3))) {
            return string;
        }
        if (string2.endsWith(File.separator)) {
            string2 = string2.substring(0, string2.lastIndexOf(File.separator));
        }
        while (true) {
            if (string2.length() == 0 || string.indexOf(string2) == 0) {
                if ((string = string.substring(string2.length(), string.length())).startsWith(File.separator)) {
                    string = string.substring(string.indexOf(File.separator) + 1, string.length());
                }
                break;
            }
            int n = string2.lastIndexOf(File.separator);
            if (n >= 0) {
                string2 = string2.substring(0, n);
                string3 = string3 + ".." + File.separator;
                continue;
            }
            string2 = "";
        }
        string = string3 + string;
        return string;
    }

    public static String toAbsolutePath(String string) {
        if (string != null) {
            if (File.separator.equals("\\") && !string.startsWith(File.separator) && string.length() >= 3 && !string.substring(1, 3).equals(":" + File.separator) || File.separator.equals("/") && !string.startsWith(File.separator)) {
                return System.getProperty("user.dir") + File.separator + string;
            }
            return string;
        }
        return null;
    }

    public void init(IURLErrorNotifier iURLErrorNotifier, AbstractURLOpener abstractURLOpener) {
        if (iURLErrorNotifier != null) {
            this.m_notifier = iURLErrorNotifier;
        }
        if (abstractURLOpener != null) {
            this.m_URLOpener = abstractURLOpener;
        }
    }

    public final boolean openEMail(String string) {
        if (string == null) {
            return false;
        }
        if (!string.startsWith(URL_MAIL_TO)) {
            return this.openLink(URL_MAIL_TO + string);
        }
        return this.openLink(string);
    }

    public final String getDefaultBrowserPath() {
        if (this.m_URLOpener != null) {
            return this.m_URLOpener.getBrowserPath();
        }
        return null;
    }

    public final boolean isDefaultURLAvailable() {
        if (this.m_URLOpener != null) {
            return this.m_URLOpener.getDefaultURL() != null && this.m_URLOpener.getBrowserCommand() != null;
        }
        return false;
    }

    public final boolean openBrowser() {
        if (this.m_URLOpener != null) {
            return this.m_URLOpener.openBrowser();
        }
        return false;
    }

    public final boolean openBrowser(String string) {
        if (this.m_URLOpener != null) {
            return this.m_URLOpener.openBrowser(string);
        }
        LogHolder.log(4, LogType.GUI, "No URL opener available!");
        return false;
    }

    public final boolean openURL(URL uRL) {
        boolean bl = false;
        String[] arrstring = BROWSERLIST;
        String string = this.getAsString(uRL);
        this.m_notifier.checkNotify(uRL);
        if (this.m_URLOpener != null) {
            bl = this.m_URLOpener.openURL(uRL);
        }
        if (!bl && uRL != null) {
            bl = this.openLink(string);
        }
        if (!bl && uRL != null) {
            for (int i = 0; i < arrstring.length; ++i) {
                try {
                    Runtime.getRuntime().exec(new String[]{arrstring[i], string});
                    bl = true;
                    break;
                }
                catch (SecurityException securityException) {
                    LogHolder.log(3, LogType.MISC, securityException);
                    break;
                }
                catch (Exception exception) {
                    continue;
                }
            }
        }
        if (!bl) {
            LogHolder.log(3, LogType.MISC, "Cannot open URL " + string + " in browser");
        }
        return bl;
    }

    public abstract String getConfigPath(String var1, boolean var2);

    protected abstract boolean openLink(String var1);

    public final String executeRuntime(String string) throws IOException {
        final StringBuffer stringBuffer = new StringBuffer();
        final Process process = Runtime.getRuntime().exec(string);
        Thread thread = new Thread(new Runnable(){

            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                try {
                    String string;
                    while ((string = bufferedReader.readLine()) != null) {
                        stringBuffer.append(string + "\n");
                    }
                }
                catch (IOException iOException) {
                    LogHolder.log(7, LogType.MISC, iOException);
                }
            }
        });
        thread.start();
        try {
            thread.join(500L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        Util.interrupt(thread);
        return stringBuffer.toString();
    }

    public final boolean execute(String string) {
        Exception exception = null;
        final StringBuffer stringBuffer = new StringBuffer();
        try {
            final Process process = Runtime.getRuntime().exec(string);
            Thread thread = new Thread(new Runnable(){

                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    try {
                        String string;
                        while ((string = bufferedReader.readLine()) != null) {
                            stringBuffer.append(string + "\n");
                        }
                    }
                    catch (IOException iOException) {
                        LogHolder.log(7, LogType.MISC, iOException);
                    }
                }
            });
            thread.start();
            try {
                thread.join(500L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            Util.interrupt(thread);
        }
        catch (Exception exception2) {
            exception = exception2;
        }
        if (exception != null || stringBuffer.length() > 0) {
            LogHolder.log(2, LogType.MISC, "Could not execute system command: '" + string + "'\n" + stringBuffer.toString(), exception);
            return false;
        }
        return true;
    }

    protected String getAsString(URL uRL) {
        if (uRL == null) {
            return null;
        }
        return uRL.toString();
    }

    public boolean isHelpAutoInstalled() {
        return false;
    }

    public abstract String getAppdataDefaultDirectory(String var1, boolean var2);

    public String getDefaultHelpPath(String string) {
        File file = ClassUtil.getClassDirectory(this.getClass());
        if (file != null) {
            return file.getParent();
        }
        return System.getProperty("user.dir");
    }

    public boolean isMultipleStart(String string, String string2, String string3) {
        Vector vector = AbstractOS.getInstance().getActiveVMs();
        Object object = null;
        int n = 0;
        for (int i = 0; i < vector.size(); ++i) {
            object = vector.elementAt(i);
            if (object == null || object.toString() == null) continue;
            if (string != null && object.toString().equals(string) || object.toString().indexOf(string2) > 0 || object.toString().indexOf(string2 + ".jar") > 0 || string3 != null && object.toString().indexOf(string3) > 0) {
                ++n;
            }
            if (n <= 1) continue;
            return true;
        }
        return false;
    }

    public boolean isMultipleStart(Class class_, String string, File file) {
        return this.isMultipleStart(class_ != null ? class_.getName() : null, string, file != null ? file.getName() : null);
    }

    public Vector getActiveVMs() {
        Vector<VMPerfDataFile> vector = new Vector<VMPerfDataFile>();
        int n = 0;
        if (!ms_tmpDir.isDirectory()) {
            return vector;
        }
        String[] arrstring = ms_tmpDir.list(new FilenameFilter(){

            public boolean accept(File file, String string) {
                return string.startsWith("hsperfdata_");
            }
        });
        if (arrstring == null) {
            return vector;
        }
        for (int i = 0; i < arrstring.length; ++i) {
            String[] arrstring2;
            File file = new File(ms_tmpDir + File.separator + arrstring[i]);
            if (!file.isDirectory() || (arrstring2 = file.list()) == null) continue;
            for (int j = 0; j < arrstring2.length; ++j) {
                File file2 = new File(file + File.separator + arrstring2[j]);
                if (!file2.isFile() || !file2.canRead()) continue;
                try {
                    n = Integer.parseInt(file2.getName());
                    if (n == 0) continue;
                    vector.addElement(new VMPerfDataFile(n));
                    continue;
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        return vector;
    }

    public static String getInterfaceName(Object object) {
        String string;
        try {
            Class<?> class_ = Class.forName("java.net.NetworkInterface");
            if (!class_.isInstance(object)) {
                return null;
            }
            Method method = class_.getMethod("getDisplayName", null);
            string = (String)method.invoke(object, null);
        }
        catch (Exception exception) {
            LogHolder.log(4, LogType.NET, exception);
            return null;
        }
        if (object == null || string == null) {
            return null;
        }
        return string;
    }

    public boolean isVirtualBoxInterface(Object object) {
        String string = AbstractOS.getInterfaceName(object);
        return string != null && string.indexOf("VirtualBox") >= 0;
    }

    public boolean copyAsRoot(File file, File file2, AbstractRetryCopyProcess abstractRetryCopyProcess) {
        return false;
    }

    public String getTempPath() {
        return AbstractOS.getDefaultTempPath();
    }

    public static String getDefaultTempPath() {
        String string = null;
        try {
            string = System.getProperty("java.io.tmpdir", null);
            if (string != null && !string.endsWith(File.separator)) {
                string = string + File.separator;
            }
        }
        catch (Throwable throwable) {
            LogHolder.log(3, LogType.MISC, throwable);
        }
        return string;
    }

    public ProxyInterface getProxyInterface(IPasswordReader iPasswordReader) {
        return null;
    }

    public String getProperty(String string) {
        String string2 = null;
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        try {
            string2 = System.getProperty(string, null);
        }
        catch (Throwable throwable) {
            LogHolder.log(3, LogType.MISC, "Could not get system property " + string);
        }
        return string2;
    }

    public String getenv(String string) {
        String string2 = null;
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        try {
            string2 = System.getenv(string);
        }
        catch (SecurityException securityException) {
            LogHolder.log(3, LogType.MISC, securityException);
        }
        catch (Error error) {
            // empty catch block
        }
        if (string2 == null && this.m_envVars != null) {
            string2 = this.m_envVars.getProperty(string);
        }
        if (string2 == null) {
            try {
                string2 = System.getProperty(string);
            }
            catch (Throwable throwable) {
                LogHolder.log(3, LogType.MISC, throwable);
            }
        }
        return string2;
    }

    protected void initEnv(String string) {
        try {
            this.m_envVars = new Properties();
            InitEnvRunner initEnvRunner = new InitEnvRunner();
            initEnvRunner.m_envCommand = string;
            Thread thread = new Thread(initEnvRunner);
            thread.setDaemon(true);
            thread.start();
            LogHolder.log(7, LogType.MISC, "initEnv -  killing the environment process starts sleeping");
            thread.join(5000L);
            LogHolder.log(7, LogType.MISC, "initEnv -  killing the environment process ends sleeping");
            if (initEnvRunner.envProcess != null) {
                initEnvRunner.envProcess.destroy();
            }
            LogHolder.log(7, LogType.MISC, "initEnv -  killing the environment process -- killed.");
            thread.interrupt();
            LogHolder.log(7, LogType.MISC, "initEnv -  killing the environment process ended.");
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.MISC, "initEnv - excpetion while killing the environment process");
            LogHolder.log(7, LogType.MISC, throwable);
        }
    }

    public synchronized boolean isJavaWebstart() {
        if (ms_iJavaWebstart < 0) {
            String string = System.getProperty("java.runtime.name");
            if (System.getProperty("jnlpx.home") != null) {
                LogHolder.log(6, LogType.MISC, "Detected a Webstart using Sun's JRE.");
                ms_iJavaWebstart = 1;
                return true;
            }
            if (string.indexOf("Java(TM)") > -1) {
                LogHolder.log(6, LogType.MISC, "Using Sun's JRE without Webstart.");
                ms_iJavaWebstart = 0;
                return false;
            }
            if (string.indexOf("OpenJDK") > -1) {
                File file = ClassUtil.getClassDirectory(this.getClass());
                if (file != null) {
                    if (file.getParent().indexOf(".netx") > -1) {
                        LogHolder.log(6, LogType.MISC, "Detected a Webstart using OpenJDK or IcedTea.");
                        ms_iJavaWebstart = 1;
                        return true;
                    }
                    LogHolder.log(6, LogType.MISC, "Using OpenJDK or IcedTea without Webstart.");
                    ms_iJavaWebstart = 0;
                    return false;
                }
                LogHolder.log(6, LogType.MISC, "We could not test for web start...");
                ms_iJavaWebstart = 0;
                return false;
            }
            LogHolder.log(3, LogType.MISC, "Could not determine Java Runtime name and, therefore, whether Webstart is used or not!");
            ms_iJavaWebstart = 0;
            return false;
        }
        if (ms_iJavaWebstart == 0) {
            return false;
        }
        return ms_iJavaWebstart == 1;
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
        ms_iJavaWebstart = -1;
        String string = System.getProperty("java.io.tmpdir");
        if (string != null) {
            if (string.compareTo("/var/tmp/") == 0) {
                string = "/tmp/";
            }
            if (string.lastIndexOf(File.pathSeparator) != string.length() - 1) {
                string = string + File.separator;
            }
        } else {
            string = "." + File.separator;
        }
        ms_tmpDir = new File(string);
    }

    private class InitEnvRunner
    implements Runnable {
        public Process envProcess;
        public BufferedReader br;
        public InputStream inProcess;
        public String m_envCommand;

        private InitEnvRunner() {
        }

        public void run() {
            try {
                this.envProcess = Runtime.getRuntime().exec(this.m_envCommand);
                this.inProcess = this.envProcess.getInputStream();
                this.br = new BufferedReader(new InputStreamReader(this.inProcess));
                String string = null;
                while ((string = this.br.readLine()) != null) {
                    LogHolder.log(7, LogType.MISC, "initEnv - read evironment line: " + string);
                    int n = string.indexOf(61);
                    String string2 = string.substring(0, n);
                    String string3 = string.substring(n + 1);
                    ((Hashtable)AbstractOS.this.m_envVars).put(string2, string3);
                }
                LogHolder.log(7, LogType.MISC, "initEnv - read evironment lines finished.");
            }
            catch (IOException iOException) {
                LogHolder.log(2, LogType.MISC, "Could not parse environment variables.", iOException);
            }
            catch (SecurityException securityException) {
                LogHolder.log(3, LogType.MISC, "Could not parse environment variables.", securityException);
            }
            catch (Throwable throwable) {
                LogHolder.log(7, LogType.MISC, "initEnv - excpetion");
                LogHolder.log(7, LogType.MISC, throwable);
            }
        }
    }

    public static abstract class AbstractRetryCopyProcess {
        private int m_maxSteps;
        private int m_currentStep;

        public AbstractRetryCopyProcess(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Max steps <=0! Value: " + n);
            }
            this.m_maxSteps = n;
            this.m_currentStep = 0;
        }

        public abstract boolean checkRetry();

        public final int getMaxProgressSteps() {
            return this.m_maxSteps;
        }

        public final long getProgressLoopWaitMilliseconds() {
            return 500L;
        }

        public final int getCurrentStep() {
            return this.m_currentStep;
        }

        public void reset() {
            this.m_currentStep = 0;
        }

        public boolean incrementProgress() {
            if (this.m_currentStep < this.m_maxSteps) {
                ++this.m_currentStep;
                return true;
            }
            return false;
        }
    }

    public static interface IURLErrorNotifier {
        public void checkNotify(URL var1);
    }

    public static abstract class AbstractURLOpener {
        private Process m_portableFirefoxProcess = null;
        private boolean m_bOneSessionOnly = false;

        public final synchronized boolean openURL(URL uRL) {
            return this.openURL(uRL, this.getBrowserCommand());
        }

        public synchronized boolean openURL(URL uRL, String string) {
            if (string == null) {
                LogHolder.log(3, LogType.GUI, "No path to portable browser was found. Maybe we should use the default browser instead. Browser command: " + string + " Default URL: " + uRL);
                return false;
            }
            if (this.m_portableFirefoxProcess != null && this.m_bOneSessionOnly) {
                try {
                    int n = this.m_portableFirefoxProcess.exitValue();
                    LogHolder.log(6, LogType.MISC, "previous portable firefox process exited " + (n == 0 ? "normally " : "anormally ") + "(exit value " + n + ").");
                }
                catch (IllegalThreadStateException illegalThreadStateException) {
                    LogHolder.log(4, LogType.MISC, "Portable Firefox process is still running!");
                    return true;
                }
            }
            String[] arrstring = uRL == null ? new String[]{string} : new String[]{string, uRL.toString()};
            try {
                this.m_portableFirefoxProcess = Runtime.getRuntime().exec(arrstring);
                return true;
            }
            catch (SecurityException securityException) {
                LogHolder.log(4, LogType.MISC, "You are not allowed to launch portable firefox: ", securityException);
            }
            catch (IOException iOException) {
                LogHolder.log(4, LogType.MISC, "Error occured while launching portable browser with command '" + arrstring[0] + (arrstring.length > 1 ? " " + arrstring[1] : "") + "'", iOException);
            }
            return false;
        }

        public abstract String getBrowserCommand();

        public abstract String getBrowserPath();

        public abstract URL getDefaultURL();

        public final synchronized boolean openBrowser() {
            return this.openBrowser(this.getBrowserCommand());
        }

        public final synchronized boolean openBrowser(String string) {
            this.m_bOneSessionOnly = true;
            boolean bl = this.openURL(this.getDefaultURL(), string);
            this.m_bOneSessionOnly = false;
            return bl;
        }
    }
}

