/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.TrustModel;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.util.ClassUtil;
import anon.util.JAPMessages;
import anon.util.ResourceLoader;
import anon.util.Util;
import gui.JAPHelpContext;
import gui.dialog.JAPDialog;
import gui.help.JAPHelp;
import jap.JAPController;
import jap.JAPModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import logging.LogHolder;
import logging.LogType;

public class MacOSXLib {
    public static final String JAP_MACOSX_LIB_REQUIRED_VERSION = "00.00.005";
    public static final String JAP_MACOSX_LIB = "MacOSX";
    public static final String JAP_MACOSX_LIB_FILENAME = "libMacOSX.jnilib";
    private static final String JAP_MACOSX_LIB_OLD_FILENAME = "libMacOSX.jnilib.old";
    public static final String JAP_MACOSX_LIB_REQUIRED_VERSION_FILENAME = "libMacOSX.jnilib.00.00.005";
    private static final String MSG_MACOSX_LIB_UPDATE = (class$jap$MacOSXLib == null ? (class$jap$MacOSXLib = MacOSXLib.class$("jap.MacOSXLib")) : class$jap$MacOSXLib).getName() + "_macOSXLibUpdate";
    private static final String UPDATE_PATH;
    private static final String MSG_SETTINGS;
    private static final String MSG_ANONYMITY_MODE;
    private static final String MSG_SHOW_DETAILS;
    private static boolean ms_bLibraryLoaded;
    static /* synthetic */ Class class$jap$MacOSXLib;
    static /* synthetic */ Class class$jap$SystrayPopupMenu;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    private MacOSXLib() {
    }

    public static void dockMenuCallback(String string) {
        final String string2 = string;
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                if (string2.equals(MSG_ANONYMITY_MODE)) {
                    if (JAPController.getInstance().getAnonMode()) {
                        JAPController.getInstance().stop();
                    } else {
                        JAPController.getInstance().start();
                    }
                } else if (string2.equals(MSG_SHOW_DETAILS)) {
                    JAPController.getInstance().showConfigDialog("ANON_TAB", JAPController.getInstance().getCurrentMixCascade());
                } else if (string2.equals(MSG_SETTINGS)) {
                    JAPController.getInstance().showConfigDialog();
                } else if (string2.equals(JAPHelp.MSG_HELP_MENU_ITEM)) {
                    JAPHelp jAPHelp = JAPHelp.getInstance();
                    jAPHelp.setContext(JAPHelpContext.createHelpContext("index", JAPController.getInstance().getViewWindow()));
                    jAPHelp.loadCurrentContext();
                } else {
                    StringTokenizer stringTokenizer = new StringTokenizer(string2, ",");
                    if (stringTokenizer.countTokens() == 2) {
                        long l = Long.parseLong(stringTokenizer.nextToken());
                        TrustModel.setCurrentTrustModel(l);
                        MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = MacOSXLib.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(stringTokenizer.nextToken());
                        if (mixCascade != null) {
                            JAPController.getInstance().setCurrentMixCascade(mixCascade);
                        }
                    }
                }
            }
        });
    }

    public static void init() {
        if (MacOSXLib.getUpdatePath() != null && JAPModel.getInstance().isMacOSXLibraryUpdateAtStartupNeeded()) {
            MacOSXLib.update();
        }
        MacOSXLib.load();
        MacOSXLib.checkLibVersion();
        if (ms_bLibraryLoaded) {
            MacOSXLib.nativeInit();
            MacOSXLib.nativeInitDockMenu();
        }
    }

    private static void load() {
        try {
            System.loadLibrary(JAP_MACOSX_LIB);
            ms_bLibraryLoaded = true;
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.GUI, "Could not initialise MacOSXLib", throwable);
            ms_bLibraryLoaded = false;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void checkLibVersion() {
        boolean bl = false;
        String string = null;
        if (ms_bLibraryLoaded) {
            try {
                string = MacOSXLib.getLibVersion();
                LogHolder.log(6, LogType.GUI, "Existing libMacOSX.jnilib version: " + string);
            }
            catch (Throwable throwable) {
                LogHolder.log(6, LogType.GUI, "libMacOSX.jnilib does not support version check. Update needed.");
                bl = true;
            }
        } else {
            LogHolder.log(6, LogType.GUI, "libMacOSX.jnilib does not exist or failed to load. Update needed.");
            bl = true;
        }
        LogHolder.log(6, LogType.GUI, "Required libMacOSX.jnilib version: 00.00.005");
        if (string != null && string.compareTo(JAP_MACOSX_LIB_REQUIRED_VERSION) < 0) {
            bl = true;
        }
        if (bl && JAPModel.getInstance().isMacOSXLibraryUpdateAtStartupNeeded()) {
            LogHolder.log(6, LogType.GUI, "Update failed twice. Giving up.");
            JAPModel.getInstance().setMacOSXLibraryUpdateAtStartupNeeded(false);
            JAPController.getInstance().saveConfigFile();
            return;
        }
        if (bl) {
            LogHolder.log(6, LogType.GUI, "Trying to fetch libMacOSX.jnilib.00.00.005 from JAP.jar.");
            if (ResourceLoader.getResourceURL(JAP_MACOSX_LIB_REQUIRED_VERSION_FILENAME) != null && MacOSXLib.getUpdatePath() != null) {
                if (MacOSXLib.update()) {
                    JAPModel.getInstance().setMacOSXLibraryUpdateAtStartupNeeded(false);
                    JAPController.getInstance().saveConfigFile();
                    MacOSXLib.load();
                    try {
                        string = MacOSXLib.getLibVersion();
                    }
                    catch (Throwable throwable) {
                        string = null;
                    }
                    if (string != null && string.compareTo(JAP_MACOSX_LIB_REQUIRED_VERSION) >= 0) {
                        LogHolder.log(6, LogType.GUI, "libMacOSX.jnilib successfully updated to version 00.00.005.");
                        return;
                    }
                    LogHolder.log(6, LogType.GUI, "libMacOSX.jnilib successfully updated to version 00.00.005. Restart needed.");
                    MacOSXLib.informUserAboutJapRestart();
                }
                LogHolder.log(6, LogType.GUI, "Update failed, trying to restart JAP to retry update.");
                JAPModel.getInstance().setMacOSXLibraryUpdateAtStartupNeeded(true);
                JAPController.getInstance().saveConfigFile();
                MacOSXLib.informUserAboutJapRestart();
                return;
            }
            LogHolder.log(6, LogType.GUI, "Required version not available in JAP.jar. Update aborted.");
            return;
        }
        if (!JAPModel.getInstance().isMacOSXLibraryUpdateAtStartupNeeded()) return;
        JAPModel.getInstance().setMacOSXLibraryUpdateAtStartupNeeded(false);
        JAPController.getInstance().saveConfigFile();
    }

    private static boolean update() {
        LogHolder.log(6, LogType.GUI, "Trying to update libMacOSX.jnilib to version 00.00.005.");
        if (MacOSXLib.renameLib(JAP_MACOSX_LIB_FILENAME, JAP_MACOSX_LIB_OLD_FILENAME) && MacOSXLib.extractDLL(new File(MacOSXLib.getLibFileName()))) {
            JAPModel.getInstance().setMacOSXLibraryUpdateAtStartupNeeded(false);
            JAPController.getInstance().saveConfigFile();
            return true;
        }
        MacOSXLib.renameLib(JAP_MACOSX_LIB_OLD_FILENAME, JAP_MACOSX_LIB_FILENAME);
        return false;
    }

    private static String getUpdatePath() {
        String string = MacOSXLib.getLibFileName();
        if (string != null) {
            string = new File(string).getParent();
        }
        return string;
    }

    public static String getLibFileName() {
        if (UPDATE_PATH != null) {
            if (!UPDATE_PATH.endsWith(File.separator)) {
                return UPDATE_PATH + File.separator + JAP_MACOSX_LIB_FILENAME;
            }
            return UPDATE_PATH + JAP_MACOSX_LIB_FILENAME;
        }
        return null;
    }

    private static boolean renameLib(String string, String string2) {
        try {
            File file = new File(MacOSXLib.getUpdatePath() + File.separator + string);
            if (file.exists()) {
                file.renameTo(new File(MacOSXLib.getUpdatePath() + File.separator + string2));
                return true;
            }
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.GUI, "Unable to copy " + MacOSXLib.getUpdatePath() + File.separator + string + ".", exception);
            return false;
        }
    }

    private static boolean extractDLL(File file) {
        boolean bl = false;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        LogHolder.log(7, LogType.GUI, "Extracting libMacOSX.jnilib.00.00.005 from jar-file to: " + file);
        try {
            inputStream = ResourceLoader.loadResourceAsStream(JAP_MACOSX_LIB_REQUIRED_VERSION_FILENAME);
            fileOutputStream = new FileOutputStream(file);
            Util.copyStream(inputStream, fileOutputStream);
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
            Util.closeStream(inputStream);
            Util.closeStream(fileOutputStream);
            return bl;
        }
    }

    private static void informUserAboutJapRestart() {
        JAPDialog.showMessageDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_MACOSX_LIB_UPDATE));
        JAPController.goodBye(false);
    }

    public static JMenu showDockMenu() {
        JMenu jMenu = new JMenu();
        JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(JAPMessages.getString(MSG_ANONYMITY_MODE));
        jCheckBoxMenuItem.setSelected(JAPController.getInstance().getAnonMode());
        jCheckBoxMenuItem.setActionCommand(MSG_ANONYMITY_MODE);
        jMenu.add(jCheckBoxMenuItem);
        JMenuItem jMenuItem = new JMenuItem(JAPMessages.getString(MSG_SHOW_DETAILS));
        jMenuItem.setActionCommand(MSG_SHOW_DETAILS);
        jMenu.add(jMenuItem);
        jMenu.add(new JSeparator());
        jMenuItem = new JMenuItem(JAPMessages.getString(MSG_SETTINGS));
        jMenuItem.setActionCommand(MSG_SETTINGS);
        jMenu.add(jMenuItem);
        jMenuItem = new JMenuItem(JAPMessages.getString(JAPHelp.MSG_HELP_MENU_ITEM));
        jMenuItem.setActionCommand(JAPHelp.MSG_HELP_MENU_ITEM);
        jMenu.add(jMenuItem);
        jMenu.add(new JSeparator());
        Vector vector = TrustModel.getTrustModels();
        for (int i = 0; i < vector.size(); ++i) {
            TrustModel trustModel = (TrustModel)vector.elementAt(i);
            if (!trustModel.isAdded()) continue;
            JMenu jMenu2 = trustModel == TrustModel.getCurrentTrustModel() ? new JMenu(trustModel.getName() + " (" + JAPMessages.getString("active") + ")") : new JMenu(trustModel.getName());
            Vector vector2 = Database.getInstance(class$anon$infoservice$MixCascade == null ? MacOSXLib.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryList();
            for (int j = 0; j < vector2.size(); ++j) {
                MixCascade mixCascade = (MixCascade)vector2.elementAt(j);
                if (!trustModel.isTrusted(mixCascade)) continue;
                jMenuItem = new JMenuItem(mixCascade.getName());
                if (JAPController.getInstance().getCurrentMixCascade() == mixCascade) {
                    jMenuItem.setSelected(true);
                }
                jMenuItem.setActionCommand(trustModel.getId() + "," + mixCascade.getId());
                jMenu2.add(jMenuItem);
            }
            jMenu.add(jMenu2);
        }
        return jMenu;
    }

    private static native void nativeInit();

    private static native void nativeInitDockMenu();

    private static native String getLibVersion();

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        MSG_SETTINGS = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = MacOSXLib.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_settings";
        MSG_ANONYMITY_MODE = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = MacOSXLib.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_anonymityMode";
        MSG_SHOW_DETAILS = (class$jap$SystrayPopupMenu == null ? (class$jap$SystrayPopupMenu = MacOSXLib.class$("jap.SystrayPopupMenu")) : class$jap$SystrayPopupMenu).getName() + "_showDetails";
        ms_bLibraryLoaded = false;
        File file = ClassUtil.getClassDirectory(class$jap$MacOSXLib == null ? (class$jap$MacOSXLib = MacOSXLib.class$("jap.MacOSXLib")) : class$jap$MacOSXLib);
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
    }
}

