/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import anon.infoservice.ListenerInterface;
import anon.infoservice.ProxyInterface;
import anon.platform.AbstractOS;
import anon.platform.WindowsRegistry;
import anon.util.IPasswordReader;
import anon.util.RecursiveFileTool;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.StringTokenizer;
import logging.LogHolder;
import logging.LogType;

public final class WindowsOS
extends AbstractOS {
    static /* synthetic */ Class class$java$io$File;

    public WindowsOS() throws Exception {
        String string = System.getProperty("os.name", "").toLowerCase();
        if (string.indexOf("win") == -1) {
            throw new Exception("Operating system is not Windows");
        }
        if (string.indexOf("windows 9") > -1) {
            this.initEnv("command.com /c set");
        } else {
            this.initEnv("cmd.exe /c set");
        }
        LogHolder.log(7, LogType.MISC, "platform.WindowsOS instantiated.");
    }

    protected final boolean openLink(String string) {
        return this.execute("rundll32 url.dll,FileProtocolHandler " + string);
    }

    protected String getAsString(URL uRL) {
        if (uRL == null) {
            return null;
        }
        String string = super.getAsString(uRL);
        if (new StringTokenizer(string).countTokens() > 1) {
            return "\"" + string + "\"";
        }
        return string;
    }

    public boolean isHelpAutoInstalled() {
        return true;
    }

    public String getDefaultHelpPath(String string) {
        String string2 = this.getAppdataDefaultDirectory(string, true);
        if (string2 == null) {
            string2 = super.getDefaultHelpPath(string);
        }
        return string2;
    }

    public String getConfigPath(String string, boolean bl) {
        String string2 = System.getProperty("java.vendor", "unknown");
        String string3 = "";
        if (string2.trim().toLowerCase().startsWith("microsoft")) {
            try {
                String string4;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("CMD /C SET").getInputStream()));
                while ((string4 = bufferedReader.readLine()) != null && !string4.startsWith("USERPROFILE")) {
                }
                if (string4 != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(string4, "=");
                    stringTokenizer.nextToken();
                    string3 = stringTokenizer.nextToken().trim();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (string3 == null) {
                string3 = System.getProperty("user.dir");
            }
        } else {
            string3 = this.getAppdataDefaultDirectory(string, bl);
            if (string3 == null) {
                string3 = System.getProperty("user.home");
            }
        }
        if (string3 == null) {
            return "";
        }
        return string3 + File.separator;
    }

    public String getAppdataDefaultDirectory(String string, boolean bl) {
        return this.getEnvPath(string, "APPDATA", bl);
    }

    public boolean copyAsRoot(File file, File file2, AbstractOS.AbstractRetryCopyProcess abstractRetryCopyProcess) {
        try {
            long l;
            byte[] arrby;
            Class<?> class_ = Class.forName("gui.JAPDll");
            Class[] arrclass = new Class[]{class$java$io$File == null ? (class$java$io$File = WindowsOS.class$("java.io.File")) : class$java$io$File, class$java$io$File == null ? (class$java$io$File = WindowsOS.class$("java.io.File")) : class$java$io$File, Boolean.TYPE};
            Method method = class_.getMethod("xcopy", arrclass);
            Object[] arrobject = new Object[]{file, file2, Boolean.TRUE};
            File file3 = new File(file2.getPath() + File.separator + file.getName());
            long l2 = RecursiveFileTool.getFileSize(file);
            long l3 = file3.length();
            try {
                arrby = RecursiveFileTool.createMD5Digest(file3);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, exception);
                arrby = null;
            }
            boolean bl = (Boolean)method.invoke(null, arrobject);
            boolean bl2 = false;
            if (!bl) {
                if (abstractRetryCopyProcess != null && abstractRetryCopyProcess.checkRetry()) {
                    return this.copyAsRoot(file, file2, abstractRetryCopyProcess);
                }
                LogHolder.log(3, LogType.MISC, "Root copy failed!");
                return false;
            }
            if (abstractRetryCopyProcess == null || abstractRetryCopyProcess.getMaxProgressSteps() <= 0) {
                return true;
            }
            long l4 = l = (long)(-1 * abstractRetryCopyProcess.getMaxProgressSteps());
            while (abstractRetryCopyProcess.incrementProgress()) {
                try {
                    l4 = l4 > 0L || !RecursiveFileTool.equals(file3, arrby, l3) ? RecursiveFileTool.getFileSize(file3) : ++l4;
                    if (l4 == l2) {
                        if (RecursiveFileTool.equals(file3, file, true)) {
                            while (abstractRetryCopyProcess.incrementProgress()) {
                            }
                            return true;
                        }
                        LogHolder.log(3, LogType.MISC, "Root copy failed!");
                        bl2 = true;
                        break;
                    }
                    if ((abstractRetryCopyProcess.getCurrentStep() > 1 || abstractRetryCopyProcess.getMaxProgressSteps() == 1) && l4 <= l) {
                        LogHolder.log(3, LogType.MISC, "Root copy failed!");
                        bl2 = true;
                        break;
                    }
                    l = l4;
                }
                catch (SecurityException securityException) {
                    LogHolder.log(3, LogType.MISC, securityException);
                    bl2 = true;
                    break;
                }
                Thread.sleep(abstractRetryCopyProcess.getProgressLoopWaitMilliseconds());
                Thread.yield();
            }
            if (RecursiveFileTool.equals(file3, file, true)) {
                return true;
            }
            if (bl2 && abstractRetryCopyProcess.checkRetry()) {
                abstractRetryCopyProcess.reset();
                return this.copyAsRoot(file, file2, abstractRetryCopyProcess);
            }
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.MISC, throwable);
        }
        return false;
    }

    public ProxyInterface getProxyInterface(IPasswordReader iPasswordReader) {
        WindowsRegistry windowsRegistry = null;
        String string = null;
        boolean bl = false;
        try {
            windowsRegistry = new WindowsRegistry(-2147483647, "Software/Microsoft/Windows/CurrentVersion/Internet Settings", 131097);
            String string2 = windowsRegistry.read("ProxyServer");
            if (string2 != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(string2, ";");
                while (stringTokenizer.hasMoreTokens() && (string = stringTokenizer.nextToken()).toLowerCase().startsWith("socks=")) {
                }
            }
            if (string == null) {
                return null;
            }
            if (string != null && string.indexOf("=") > 0) {
                if (string.toLowerCase().startsWith("socks=")) {
                    bl = true;
                }
                string = string.substring(string.indexOf("=") + 1, string.length());
            }
            ProxyInterface proxyInterface = new ProxyInterface(ListenerInterface.parseHostnamePort(string2, bl ? 3 : 1), iPasswordReader);
            windowsRegistry.close();
            return proxyInterface;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Could not get proxy registry settings...", exception);
            return null;
        }
    }

    public String getTempPath() {
        String string = super.getTempPath();
        if (string == null) {
            string = this.getenv("TEMP");
        }
        if (string == null) {
            string = this.getenv("TMP");
        }
        if (string != null && !string.endsWith(File.separator)) {
            string = string + File.separator;
        }
        return string;
    }

    private String getEnvPath(String string, String string2, boolean bl) {
        if (string == null) {
            throw new IllegalArgumentException("Application name is null!");
        }
        String string3 = null;
        string3 = this.getenv(string2);
        if (string3 != null && string3.trim().length() > 0 && new File(string3).exists()) {
            File file = new File((string3 = string3 + File.separator + string) + File.separator);
            if (!(file.exists() || bl && file.mkdir())) {
                if (bl) {
                    LogHolder.log(3, LogType.MISC, "Could not create storage directory: " + string3);
                }
                string3 = null;
            }
        } else {
            string3 = null;
        }
        return string3;
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

