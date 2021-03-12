/*
 * Decompiled with CFR 0.150.
 */
package anon.platform;

import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.RecursiveFileTool;
import anon.util.Util;
import anon.util.ZipArchiver;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;

public final class MacOS
extends AbstractOS {
    public static final String OS_NAME = "Mac OS";
    static final String BUNDLE_CONTENTS;
    static final String BUNDLE_RESOURCES;
    static final String BUNDLE_MAC_OS_EXECUTABLES;
    static final String BUNDLE_PROPERTY_FILE_NAME = "Info.plist";
    static final String BUNDLE_EXECUTABLE_PROPERTY_KEY = "CFBundleExecutable";
    static final String ROOT_SHELLSCRIPT_NAME = "rootShellScript";
    static final String OSA_EXEC_SHELLSCRIPT_STMT = "do shell script rootShellScript with administrator privileges";
    static final String OSA_APPLET_NAME = "JonDoUpdater.app";
    static final String OSA_APPLET_PATH;
    static final String[] OSASCRIPT_CMD;
    static final String[] OSACOMPILE_CMD;
    static final String[] OPEN_UPDATER_CMD;
    private static final String MACOS_VBOX_NETADAPTER = "vboxnet";
    private String m_bundlePath = null;

    public MacOS() throws Exception {
        if (System.getProperty("mrj.version") == null) {
            throw new Exception("Operating system is not Mac OS");
        }
        this.setBundlePath();
    }

    protected boolean openLink(String string) {
        return this.execute("open " + Util.encodeWhiteSpaces(string));
    }

    public String getAppdataDefaultDirectory(String string, boolean bl) {
        return null;
    }

    public boolean isHelpAutoInstalled() {
        return true;
    }

    public String getConfigPath(String string, boolean bl) {
        if (System.getProperty("os.name").equalsIgnoreCase(OS_NAME)) {
            return System.getProperty("user.home", ".") + "/";
        }
        return System.getProperty("user.home", "") + "/Library/Preferences/";
    }

    public void setBundlePath() {
        String string;
        File file = ClassUtil.getClassDirectory(this.getClass());
        if (file != null && (string = file.getPath()) != null) {
            int n;
            if (!string.startsWith(File.separator)) {
                n = string.indexOf("/");
                String string2 = string = n != -1 ? string.substring(n) : string;
            }
            if ((n = string.indexOf(BUNDLE_CONTENTS)) != -1) {
                this.m_bundlePath = string.substring(0, n - 1);
                return;
            }
        }
        this.m_bundlePath = null;
    }

    public String getBundlePath() {
        return this.m_bundlePath;
    }

    public boolean isBundle() {
        return this.m_bundlePath != null;
    }

    public String getBundleExecutablePath() {
        return null;
    }

    private static int handleAppleScriptCmds(String[] arrstring, Process process) throws IOException, InterruptedException {
        PrintWriter printWriter = new PrintWriter(process.getOutputStream());
        for (int i = 0; i < arrstring.length; ++i) {
            printWriter.println(arrstring[i]);
        }
        printWriter.flush();
        printWriter.close();
        return process.waitFor();
    }

    public boolean isVirtualBoxInterface(Object object) {
        String string = AbstractOS.getInterfaceName(object);
        return string != null && string.indexOf(MACOS_VBOX_NETADAPTER) >= 0;
    }

    public boolean copyAsRoot(File file, File file2, AbstractOS.AbstractRetryCopyProcess abstractRetryCopyProcess) {
        String string = "set rootShellScript to \"cp " + file.getAbsolutePath() + " " + file2.getAbsolutePath() + "\"";
        String[] arrstring = new String[]{string, OSA_EXEC_SHELLSCRIPT_STMT};
        try {
            Object object;
            Runtime runtime = Runtime.getRuntime();
            int n = 1;
            if (OSACOMPILE_CMD != null) {
                object = runtime.exec(OSACOMPILE_CMD);
                n = MacOS.handleAppleScriptCmds(arrstring, (Process)object);
            }
            if (n == 0) {
                Object object2;
                object = ClassUtil.getJarFile();
                if (object != null) {
                    object2 = new ZipArchiver((ZipFile)object);
                    File file3 = new File(OSA_APPLET_PATH + File.separator + BUNDLE_RESOURCES + "applet.icns");
                    file3.delete();
                    ((ZipArchiver)object2).extractSingleEntry("images/JUpdate.icns", OSA_APPLET_PATH + File.separator + BUNDLE_RESOURCES + "applet.icns");
                }
                object2 = runtime.exec(OPEN_UPDATER_CMD);
                n = ((Process)object2).waitFor();
            } else {
                object = runtime.exec(OSASCRIPT_CMD);
                n = MacOS.handleAppleScriptCmds(arrstring, (Process)object);
            }
            if (OSA_APPLET_PATH != null && ((File)(object = new File(OSA_APPLET_PATH))).exists() && OSA_APPLET_PATH.endsWith(OSA_APPLET_NAME)) {
                RecursiveFileTool.deleteRecursion((File)object);
            }
            return RecursiveFileTool.equals(file, new File(file2.getAbsolutePath() + File.separator + file.getName()), true);
        }
        catch (IOException iOException) {
            LogHolder.log(6, LogType.MISC, "Mac OS root copy failed: ", iOException);
            return false;
        }
        catch (InterruptedException interruptedException) {
            LogHolder.log(2, LogType.MISC, "Interrupted while waiting for root copy process ", interruptedException);
            return false;
        }
    }

    static {
        String[] arrstring;
        String[] arrstring2;
        BUNDLE_CONTENTS = "Contents" + File.separator;
        BUNDLE_RESOURCES = BUNDLE_CONTENTS + "Resources" + File.separator;
        BUNDLE_MAC_OS_EXECUTABLES = BUNDLE_CONTENTS + "MacOS" + File.separator;
        OSA_APPLET_PATH = AbstractOS.getDefaultTempPath() != null ? AbstractOS.getDefaultTempPath() + OSA_APPLET_NAME : null;
        OSASCRIPT_CMD = new String[]{"osascript"};
        if (OSA_APPLET_PATH != null) {
            String[] arrstring3 = new String[3];
            arrstring3[0] = "osacompile";
            arrstring3[1] = "-xo";
            arrstring2 = arrstring3;
            arrstring3[2] = OSA_APPLET_PATH;
        } else {
            arrstring2 = OSACOMPILE_CMD = null;
        }
        if (OSA_APPLET_PATH != null) {
            String[] arrstring4 = new String[1];
            arrstring = arrstring4;
            arrstring4[0] = OSA_APPLET_PATH + File.separator + BUNDLE_MAC_OS_EXECUTABLES + "applet";
        } else {
            arrstring = null;
        }
        OPEN_UPDATER_CMD = arrstring;
    }
}

