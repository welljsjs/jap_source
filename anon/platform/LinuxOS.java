/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import anon.platform.AbstractOS;
import anon.util.RecursiveFileTool;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import logging.LogHolder;
import logging.LogType;

public final class LinuxOS
extends AbstractOS {
    private boolean m_bKDE = false;
    private int m_iKDEVersion = 0;
    private boolean m_bGnome = false;
    private static final String LINUX_VBOX_NETADAPTER = "vboxnet";
    public static final String[] BROWSERLIST = new String[]{"firefox", "iexplore", "explorer", "mozilla", "konqueror", "mozilla-firefox", "opera"};

    public LinuxOS() throws Exception {
        String string = System.getProperty("os.name", "").toLowerCase();
        if (string.toLowerCase().indexOf("linux") == -1) {
            throw new Exception("Operating system is not Linux");
        }
        Properties properties = new Properties();
        try {
            properties.load(Runtime.getRuntime().exec("env").getInputStream());
            this.m_bKDE = Boolean.valueOf(properties.getProperty("KDE_FULL_SESSION"));
            if (this.m_bKDE) {
                this.m_iKDEVersion = Integer.valueOf(properties.getProperty("KDE_SESSION_VERSION"));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_bGnome = properties.getProperty("GNOME_DESKTOP_SESSION_ID") != null;
        this.initEnv("env");
    }

    protected boolean openLink(String string) {
        if (string == null) {
            return false;
        }
        if (this.m_bKDE) {
            return this.execute("kfmclient exec " + string);
        }
        if (this.m_bGnome) {
            return this.execute("gnome-open " + string);
        }
        return false;
    }

    public String getAppdataDefaultDirectory(String string, boolean bl) {
        return null;
    }

    public String getConfigPath(String string, boolean bl) {
        return System.getProperty("user.home", "") + "/.";
    }

    public boolean isVirtualBoxInterface(Object object) {
        String string = AbstractOS.getInterfaceName(object);
        return string != null && string.indexOf(LINUX_VBOX_NETADAPTER) >= 0;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean copyAsRoot(File file, File file2, AbstractOS.AbstractRetryCopyProcess abstractRetryCopyProcess) {
        if (file == null || file2 == null || !file2.isDirectory()) {
            return false;
        }
        String string = "";
        String string2 = null;
        String[] arrstring = new String[3];
        String string3 = null;
        boolean bl = false;
        String string4 = file.getPath();
        String string5 = file2.getPath() + "/";
        abstractRetryCopyProcess.incrementProgress();
        if (this.m_bKDE) {
            if (this.m_iKDEVersion == 4) {
                try {
                    arrstring[0] = "kde4-config";
                    arrstring[1] = "--path";
                    arrstring[2] = "exe";
                    Process process = Runtime.getRuntime().exec(arrstring);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while (!this.procDone(process)) {
                        while ((string2 = bufferedReader.readLine()) != null) {
                            string = string + string2;
                        }
                    }
                    LogHolder.log(5, LogType.MISC, "The KDE-Path on your system is: " + string);
                    StringTokenizer stringTokenizer = new StringTokenizer(string, ":");
                    while (stringTokenizer.hasMoreTokens()) {
                        String string6 = stringTokenizer.nextToken();
                        File file3 = new File(string6 + "kdesu");
                        if (!file3.isFile()) continue;
                        string3 = string6 + "kdesu";
                        break;
                    }
                }
                catch (IOException iOException) {
                    LogHolder.log(2, LogType.MISC, iOException);
                }
                if (string3 == null) {
                    LogHolder.log(1, LogType.MISC, "No path to kdesu found, aborting...");
                    return false;
                }
                string3 = string3 + " 'cp -r " + string4 + " " + string5 + "'";
                this.executeShell(string3);
            } else {
                string3 = "kdesu 'cp -r " + string4 + " " + string5 + "'";
                this.executeShell(string3);
            }
        } else if (this.m_bGnome) {
            string3 = "gksu 'cp -r " + string4 + " " + string5 + "'";
            this.executeShell(string3);
        } else {
            string3 = "xterm -e su -c 'cp -r " + string4 + " " + string5 + "'";
            this.executeShell(string3);
            bl = true;
        }
        File file4 = new File(string5 + file.getName());
        if (RecursiveFileTool.equals(file4, file, true)) {
            while (abstractRetryCopyProcess.incrementProgress()) {
            }
            return true;
        }
        if (bl && abstractRetryCopyProcess != null && abstractRetryCopyProcess.checkRetry()) {
            abstractRetryCopyProcess.reset();
            return this.copyAsRoot(file, file2, abstractRetryCopyProcess);
        }
        return false;
    }

    private void executeShell(String string) {
        try {
            String[] arrstring = new String[]{"sh", "-c", string};
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(arrstring);
            process.waitFor();
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
        }
    }

    private boolean procDone(Process process) {
        try {
            int n = process.exitValue();
            return true;
        }
        catch (IllegalThreadStateException illegalThreadStateException) {
            return false;
        }
    }
}

