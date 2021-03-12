/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.JAPMessages;
import anon.util.RecursiveFileTool;
import anon.util.ResourceLoader;
import anon.util.Util;
import gui.GUIUtils;
import gui.dialog.JAPDialog;
import jap.JAPController;
import jap.JAPModel;
import jap.SystrayPopupMenu;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import logging.LogHolder;
import logging.LogType;

public final class JAPDll {
    public static final String MSG_IGNORE_UPDATE = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_ignoreUpdate";
    public static final String JAP_DLL_REQUIRED_VERSION = "00.04.009";
    public static final String START_PARAMETER_ADMIN = "--dllAdminUpdate";
    private static final String UPDATE_PATH;
    private static final String DLL_LIBRARY_NAME = "japdll";
    private static final String DLL_LIBRARY_NAME_32bit = "japdll";
    private static final String DLL_LIBRARY_NAME_64bit = "japdll_x64";
    private static final String JAP_DLL = "japdll.dll";
    private static final String JAP_DLL_NEW_32bit = "japdll.dll.00.04.009";
    private static final String JAP_DLL_NEW_64bit = "japdll.dll.00.04.009";
    private static final String JAP_DLL_OLD = "japdll.old";
    private static final String MSG_DLL_UPDATE;
    private static final String MSG_DLL_UPDATE_SUCCESS_ADMIN;
    private static final String MSG_DLL_UPDATE_FAILED;
    private static final String MSG_CONFIRM_OVERWRITE;
    private static final String MSG_PERMISSION_PROBLEM;
    private static final String MSG_COULD_NOT_SAVE;
    private static Hashtable ms_hashOnTop;
    private static boolean ms_bInTaskbar;
    private static final Object SYNC_POPUP;
    private static SystrayPopupMenu ms_popupMenu;
    private static Window ms_popupWindow;
    private static boolean m_sbHasOnTraffic;
    private static boolean m_bStartedAsAdmin;
    private static final String STR_HIDDEN_WINDOW;
    static /* synthetic */ Class class$gui$JAPDll;

    private static void loadDll() {
        String string;
        try {
            string = System.getProperty("sun.arch.data.model");
        }
        catch (SecurityException securityException) {
            string = null;
        }
        if (string != null && string.equals("64")) {
            try {
                System.loadLibrary(DLL_LIBRARY_NAME_64bit);
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            try {
                System.loadLibrary("japdll");
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void init(boolean bl, String string, Window window) {
        block11: {
            String string2 = System.getProperty("os.name", "");
            m_bStartedAsAdmin = bl;
            try {
                File file;
                File file2;
                Object object;
                if (string2 != null && string2.toLowerCase().indexOf("win") <= -1) break block11;
                GUIUtils.setNativeGUILibrary(new GUIUtils.NativeGUILibrary(){

                    public boolean setAlwaysOnTop(Window window, boolean bl) {
                        return JAPDll.setWindowOnTop(window, bl);
                    }

                    public boolean isAlwaysOnTop(Window window) {
                        return JAPDll.isWindowOnTop(window);
                    }
                });
                try {
                    String string3 = AbstractOS.getInstance().getTempPath();
                    if (string3 != null && ((File)(object = new File(string3 + "japdll"))).exists() && !RecursiveFileTool.deleteRecursion((File)object)) {
                        throw new Exception("Delete recursive");
                    }
                }
                catch (Throwable throwable) {
                    LogHolder.log(2, LogType.MISC, "Could not delete temporary DLL!", throwable);
                }
                boolean bl2 = false;
                if (JAPDll.getUpdatePath() != null && JAPModel.getInstance().getDllUpdatePath() != null) {
                    JAPDll.update(window);
                    bl2 = true;
                }
                JAPDll.loadDll();
                if (JAPDll.getUpdatePath() == null) {
                    LogHolder.log(3, LogType.GUI, "Could not get DLL update path. Maybe Java Webstart?");
                    return;
                }
                object = JAPDll.getDllVersion();
                if (bl2 && (object == null || ((String)object).compareTo(JAP_DLL_REQUIRED_VERSION) < 0)) {
                    JAPModel.getInstance().setDLLupdate(JAPDll.getUpdatePath());
                    JAPController.getInstance().saveConfigFile();
                } else {
                    JAPModel.getInstance().setDLLupdate(null);
                }
                JAPController.getInstance().addProgramExitListener(new JAPController.ProgramExitListener(){

                    public void programExiting() {
                        try {
                            if (JAPDll.getDllVersion() != null) {
                                JAPDll.hideSystray_dll();
                            }
                        }
                        catch (Throwable throwable) {
                            LogHolder.log(2, LogType.GUI, throwable);
                        }
                    }
                });
                if (m_bStartedAsAdmin && string != null && (file2 = ClassUtil.getClassDirectory(class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll)) != null && file2.getPath().endsWith(".jar") && (file = new File(string + "\\AppData\\Local\\VirtualStore" + file2.getPath().substring(2, file2.getPath().length()))).exists() && !file.equals(file2)) {
                    String string4 = AbstractOS.getInstance().getProperty("java.home") + File.separator + "bin" + File.separator + "javaw -jar ";
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(string4 + "\"" + file.getPath() + "\" --version").getInputStream()));
                    String string5 = bufferedReader.readLine();
                    bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(string4 + "\"" + file2.getPath() + "\" --version").getInputStream()));
                    String string6 = bufferedReader.readLine();
                    if (string5 != null && string6 != null && !string5.equals(string6)) {
                        Util.copyStream(new FileInputStream(file), new FileOutputStream(file2));
                        final JAPController.IRestarter iRestarter = JAPController.getInstance().getRestarter();
                        JAPController.getInstance().setRestarter(new JAPController.IRestarter(){

                            public void exec(String[] arrstring) throws IOException {
                                iRestarter.exec(arrstring);
                            }

                            public boolean isConfigFileSaved() {
                                return false;
                            }

                            public boolean hideWarnings() {
                                return true;
                            }
                        });
                        JAPController.goodBye(false);
                    }
                }
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.GUI, throwable);
            }
        }
    }

    public static void checkDllVersion(boolean bl) {
        if (System.getProperty("os.name", "").toLowerCase().indexOf("win") < 0) {
            return;
        }
        LogHolder.log(6, LogType.GUI, "Existing japdll.dll version: " + JAPDll.getDllVersion());
        LogHolder.log(6, LogType.GUI, "Required japdll.dll version: 00.04.009");
        if (JAPDll.getDllVersion() != null && JAPDll.getDllVersion().compareTo(JAP_DLL_REQUIRED_VERSION) < 0 && ResourceLoader.getResourceURL("japdll.dll.00.04.009") != null && JAPDll.getUpdatePath() != null) {
            File file = new File(JAPDll.getDllFileName());
            if (!file.exists()) {
                JAPDll.askUserWhatToDo();
                return;
            }
            if (JAPModel.getInstance().getDllUpdatePath() != null) {
                if (bl) {
                    JAPDll.askUserWhatToDo();
                }
                return;
            }
            if (JAPDll.update(JAPController.getInstance().getCurrentView()) && JAPDll.getDllVersion() != null && JAPDll.getDllVersion().compareTo(JAP_DLL_REQUIRED_VERSION) < 0) {
                LogHolder.log(6, LogType.GUI, "Update successful, existing japdll.dll version: " + JAPDll.getDllVersion());
                JAPDll.loadDll();
                if (JAPDll.getDllVersion().compareTo(JAP_DLL_REQUIRED_VERSION) < 0) {
                    bl = true;
                } else {
                    return;
                }
            }
            JAPModel.getInstance().setDLLupdate(JAPDll.getUpdatePath());
            JAPController.getInstance().saveConfigFile();
            if (bl) {
                JAPDll.informUserAboutJapRestart();
            }
        } else if (JAPModel.getInstance().getDllUpdatePath() != null) {
            JAPModel.getInstance().setDLLupdate(null);
            JAPController.getInstance().saveConfigFile();
        }
    }

    private static boolean update(Component component) {
        if (JAPDll.renameDLL(JAP_DLL, JAP_DLL_OLD) && JAPDll.extractDLL(new File(JAPDll.getDllFileName()))) {
            JAPModel.getInstance().setDLLupdate(null);
            JAPController.getInstance().saveConfigFile();
            if (m_bStartedAsAdmin) {
                if (component != null) {
                    JAPDialog.showMessageDialog(component, JAPMessages.getString(MSG_DLL_UPDATE_SUCCESS_ADMIN));
                }
                final JAPController.IRestarter iRestarter = JAPController.getInstance().getRestarter();
                JAPController.getInstance().setRestarter(new JAPController.IRestarter(){

                    public void exec(String[] arrstring) throws IOException {
                        iRestarter.exec(arrstring);
                    }

                    public boolean isConfigFileSaved() {
                        return true;
                    }

                    public boolean hideWarnings() {
                        return true;
                    }
                });
                JAPController.goodBye(true);
            }
            return true;
        }
        JAPDll.renameDLL(JAP_DLL_OLD, JAP_DLL);
        return false;
    }

    private static boolean renameDLL(String string, String string2) {
        try {
            File file = new File(JAPDll.getUpdatePath() + File.separator + string);
            if (file.exists()) {
                file.renameTo(new File(JAPDll.getUpdatePath() + File.separator + string2));
                return true;
            }
            return false;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.GUI, "Unable to copy " + JAPDll.getUpdatePath() + File.separator + string + ".", exception);
            return false;
        }
    }

    private static boolean extractDLL(File file) {
        boolean bl = false;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        LogHolder.log(7, LogType.GUI, "Extracting japdll.dll.00.04.009 from jar-file to: " + file);
        try {
            inputStream = ResourceLoader.loadResourceAsStream("japdll.dll.00.04.009");
            fileOutputStream = new FileOutputStream(file);
            Util.copyStream(inputStream, fileOutputStream);
            bl = true;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
        }
        Util.closeStream(inputStream);
        Util.closeStream(fileOutputStream);
        return bl;
    }

    private static void askUserWhatToDo() {
        if (!JAPModel.getInstance().isDLLWarningActive()) {
            return;
        }
        JAPDialog.LinkedCheckBox linkedCheckBox = new JAPDialog.LinkedCheckBox(JAPMessages.getString(MSG_IGNORE_UPDATE), false);
        Object[] arrobject = new String[]{JAP_DLL, JAPDll.getUpdatePath()};
        int n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_DLL_UPDATE_FAILED, arrobject) + "<br>&nbsp;", JAPMessages.getString(JAPDialog.MSG_TITLE_ERROR), 2, 2, (JAPDialog.ILinkedInformation)linkedCheckBox);
        JAPModel.getInstance().setDllWarning(!linkedCheckBox.getState());
        if (n == 0) {
            JAPController.getInstance().setRestarter(new JAPController.IRestarter(){

                public boolean isConfigFileSaved() {
                    return true;
                }

                public boolean hideWarnings() {
                    return false;
                }

                public void exec(String[] arrstring) throws IOException {
                    String string = null;
                    String string2 = "";
                    String string3 = AbstractOS.getInstance().getProperty("user.home");
                    if (arrstring != null && arrstring.length > 1) {
                        string = "\"" + arrstring[0] + "\"";
                        for (int i = 1; i < arrstring.length; ++i) {
                            string2 = new StringTokenizer(arrstring[i]).countTokens() > 1 ? string2 + " \"" + arrstring[i] + "\"" : string2 + " " + arrstring[i];
                        }
                        if (!m_bStartedAsAdmin) {
                            string2 = string2 + " --dllAdminUpdate";
                            if (string3 != null) {
                                string2 = string2 + " " + string3;
                            }
                        }
                    }
                    if (string == null || !JAPDll.shellExecute(string, string2, true)) {
                        this.showExplorerFiles();
                    }
                }

                private void showExplorerFiles() {
                    boolean bl = false;
                    String string = AbstractOS.getInstance().getTempPath();
                    if (string == null) {
                        string = AbstractOS.getInstance().getConfigPath("JonDo", true);
                    }
                    string = string + "japdll" + File.separator;
                    try {
                        File file = new File(string);
                        if (file.exists() && !file.isDirectory()) {
                            file.delete();
                        }
                        bl = !file.exists() ? new File(string).mkdir() : true;
                    }
                    catch (SecurityException securityException) {
                        LogHolder.log(2, LogType.MISC, "Could not create temporary directory!", securityException);
                    }
                    if (bl && JAPDll.extractDLL(new File(string + JAPDll.JAP_DLL))) {
                        try {
                            Runtime.getRuntime().exec(new String[]{"CMD", "/C", "EXPLORER.EXE", string});
                            Runtime.getRuntime().exec(new String[]{"CMD", "/C", "EXPLORER.EXE", JAPDll.getUpdatePath()});
                        }
                        catch (IOException iOException) {
                            LogHolder.log(2, LogType.MISC, iOException);
                        }
                    }
                }
            });
            JAPController.goodBye(false);
        }
    }

    private static void informUserAboutJapRestart() {
        JAPDialog.showMessageDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_DLL_UPDATE, "'japdll.dll'"));
        JAPController.goodBye(false);
    }

    private static boolean isWindowOnTop(Window window) {
        if (window == null) {
            return false;
        }
        return ms_hashOnTop.contains(window.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean setWindowOnTop(Window window, boolean bl) {
        if (window == null) {
            return false;
        }
        String string = window.getName();
        if (string == null) {
            return false;
        }
        try {
            Hashtable hashtable = ms_hashOnTop;
            synchronized (hashtable) {
                JAPDll.setWindowOnTop_dll(string, bl);
                if (bl) {
                    ms_hashOnTop.put(string, string);
                } else {
                    ms_hashOnTop.remove(string);
                }
            }
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static synchronized boolean showWindowFromTaskbar() {
        try {
            if (ms_bInTaskbar) {
                ms_bInTaskbar = false;
                boolean bl = JAPDll.showWindowFromTaskbar_dll();
                JAPDll.showMainWindow();
                ms_bInTaskbar = !bl;
                return bl;
            }
            return false;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static synchronized boolean hideWindowInTaskbar(String string) {
        try {
            boolean bl = JAPDll.hideWindowInTaskbar_dll(string);
            if (!ms_bInTaskbar) {
                ms_bInTaskbar = bl;
            }
            return bl;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static boolean setWindowIcon(String string) {
        try {
            return JAPDll.setWindowIcon_dll(string);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static boolean onTraffic() {
        if (m_sbHasOnTraffic) {
            try {
                JAPDll.onTraffic_dll();
                return true;
            }
            catch (Throwable throwable) {
                m_sbHasOnTraffic = false;
                return false;
            }
        }
        return false;
    }

    public static boolean xcopy(File file, File file2, boolean bl) {
        if (file == null || file2 == null || !file2.isDirectory()) {
            return false;
        }
        String string = "";
        if (file.isDirectory()) {
            string = "/E ";
        }
        String string2 = " /Y /R /Q /I /H " + string + "\"" + file + "\" \"" + file2 + "\"";
        LogHolder.log(5, LogType.MISC, "Doing xcopy: " + string2);
        return JAPDll.shellExecute("xcopy", string2, bl);
    }

    public static String getDllVersion() {
        String string = null;
        try {
            string = JAPDll.getDllVersion_dll();
            StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
            if (stringTokenizer.countTokens() > 1) {
                string = "";
                int n = Integer.parseInt(stringTokenizer.nextToken());
                if (n < 10) {
                    string = string + "0";
                }
                string = string + n + ".";
                n = Integer.parseInt(stringTokenizer.nextToken());
                if (n < 10) {
                    string = string + "0";
                }
                string = string + n + ".";
                n = Integer.parseInt(stringTokenizer.nextToken());
                if (n < 10) {
                    string = string + "0";
                }
                if (n < 100) {
                    string = string + "0";
                }
                string = string + n;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return string;
    }

    private static String getUpdatePath() {
        String string = JAPDll.getDllFileName();
        if (string != null) {
            string = new File(string).getParent();
        }
        return string;
    }

    public static String getDllFileName() {
        String string = JAPModel.getInstance().getDllUpdatePath();
        if (string == null) {
            try {
                String string2 = JAPDll.getDllFileName_dll();
                if (string2 == null || string2.length() == 0) {
                    return null;
                }
                return string2;
            }
            catch (Throwable throwable) {
                string = UPDATE_PATH;
            }
        }
        if (string != null) {
            string = !string.endsWith(File.separator) ? string + File.separator + JAP_DLL : string + JAP_DLL;
        }
        return string;
    }

    public static long showMainWindow() {
        Window window = JAPController.getInstance().getViewWindow();
        window.setVisible(true);
        window.toFront();
        window.repaint();
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long closePopupMenu() {
        Object object = SYNC_POPUP;
        synchronized (object) {
            if (ms_popupMenu != null) {
                Runnable runnable = new Runnable(){

                    public void run() {
                        ms_popupMenu.setVisible(false);
                        ms_popupWindow.setVisible(false);
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            }
        }
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long showPopupMenu(long l, long l2) {
        Object object = SYNC_POPUP;
        synchronized (object) {
            if (ms_popupWindow == null) {
                ms_popupWindow = new JDialog(new Frame(STR_HIDDEN_WINDOW), false);
                ms_popupWindow.setName(STR_HIDDEN_WINDOW);
                ms_popupWindow.pack();
                ms_popupWindow.setLocation(20000, 20000);
            }
            Point point = new Point((int)l, (int)l2);
            ms_popupMenu = new SystrayPopupMenu(new SystrayPopupMenu.MainWindowListener(){

                public void onShowMainWindow() {
                    JAPDll.showWindowFromTaskbar();
                }

                public void onShowHelp() {
                }

                public void onShowSettings(final String string, final Object object) {
                    new Thread(new Runnable(){

                        public void run() {
                            JAPController.getInstance().showConfigDialog(string, object);
                        }
                    }).start();
                }
            });
            GUIUtils.setAlwaysOnTop(ms_popupWindow, true);
            ms_popupWindow.setVisible(true);
            ms_popupMenu.addPopupMenuListener(new PopupMenuListener(){

                public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                    JAPDll.popupClosed_dll();
                }

                public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
                }
            });
            Point point2 = new Point(point.x, point.y - ms_popupMenu.getHeight());
            ms_popupMenu.show(ms_popupWindow, point2);
            ms_popupMenu.repaint();
            return 0L;
        }
    }

    public static void setSystrayTooltip(String string) {
        if (string == null) {
            return;
        }
        if (string.length() >= 60) {
            string = string.substring(0, 60);
        }
        if ((string = string.trim()).length() == 0) {
            return;
        }
        try {
            JAPDll.setTooltipText_dll(string);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static boolean shellExecute(String string, String string2, boolean bl) {
        boolean bl2;
        try {
            bl2 = JAPDll.shellExecute_dll(string, string2, bl);
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.GUI, throwable);
            bl2 = false;
        }
        return bl2;
    }

    private static native void setWindowOnTop_dll(String var0, boolean var1);

    private static native boolean hideWindowInTaskbar_dll(String var0);

    private static native boolean showWindowFromTaskbar_dll();

    private static native boolean setTooltipText_dll(String var0);

    private static native boolean setWindowIcon_dll(String var0);

    private static native void onTraffic_dll();

    private static native void popupClosed_dll();

    private static native void hideSystray_dll();

    private static native String getDllVersion_dll();

    private static native String getDllFileName_dll();

    private static native boolean shellExecute_dll(String var0, String var1, boolean var2);

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        MSG_DLL_UPDATE = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_updateRestartMessage";
        MSG_DLL_UPDATE_SUCCESS_ADMIN = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_dllUpdateSuccessAdmin";
        MSG_DLL_UPDATE_FAILED = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_updateFailed";
        MSG_CONFIRM_OVERWRITE = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_confirmOverwrite";
        MSG_PERMISSION_PROBLEM = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_permissionProblem";
        MSG_COULD_NOT_SAVE = (class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll).getName() + "_couldNotSave";
        ms_hashOnTop = new Hashtable();
        ms_bInTaskbar = false;
        SYNC_POPUP = new Object();
        m_sbHasOnTraffic = true;
        m_bStartedAsAdmin = false;
        File file = ClassUtil.getClassDirectory(class$gui$JAPDll == null ? (class$gui$JAPDll = JAPDll.class$("gui.JAPDll")) : class$gui$JAPDll);
        if (file == null) {
            String string = null;
            try {
                string = System.getProperty("user.dir", null);
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            UPDATE_PATH = string;
        } else {
            UPDATE_PATH = file.getParent();
        }
        STR_HIDDEN_WINDOW = Double.toString(Math.random());
    }

    private static class MyFileFilter
    extends FileFilter {
        public static final String DLL_EXTENSION = ".dll";
        private final String ACCOUNT_DESCRIPTION = "JAP dll file (*.dll)";
        private int filterType;

        private MyFileFilter() {
        }

        public int getFilterType() {
            return this.filterType;
        }

        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(DLL_EXTENSION);
        }

        public String getDescription() {
            return "JAP dll file (*.dll)";
        }
    }
}

