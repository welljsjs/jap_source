/*
 * Decompiled with CFR 0.150.
 */
import anon.client.crypto.KeyPool;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.platform.AbstractOS;
import anon.platform.MacOS;
import anon.platform.WindowsOS;
import anon.util.ClassUtil;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPAWTMsgBox;
import gui.JAPDll;
import gui.dialog.JAPDialog;
import jap.AbstractJAPMainView;
import jap.ConsoleJAPMainView;
import jap.ConsoleSplash;
import jap.IJAPMainView;
import jap.ISplashResponse;
import jap.JAPController;
import jap.JAPDebug;
import jap.JAPModel;
import jap.JAPNewView;
import jap.JAPSplash;
import jap.JAPViewIconified;
import jap.MacOSXLib;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;
import javax.swing.JLabel;
import javax.swing.UIManager;
import logging.AbstractLog;
import logging.DummyLog;
import logging.LogHolder;
import logging.LogType;
import logging.SystemErrLog;

public class JAP {
    private static final String MSG_ERROR_NEED_NEWER_JAVA = "errorNeedNewerJava";
    private static final String MSG_ERROR_JONDO_ALREADY_RUNNING = "errorAlreadyRunning";
    private static final String MSG_ERROR_JONDO_ALREADY_RUNNING_WIN = "errorAlreadyRunningWin";
    private static final String MSG_GNU_NOT_COMPATIBLE = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_gnuNotCompatible";
    private static final String MSG_LOADING_INTERNATIONALISATION = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_loadingInternationalisation";
    private static final String MSG_LOADING_SETTINGS = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_loadingSettings";
    private static final String MSG_STARTING_CONTROLLER = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_startingController";
    private static final String MSG_INIT_DLL = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_initLibrary";
    private static final String MSG_INIT_VIEW = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_initView";
    private static final String MSG_INIT_ICON_VIEW = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_initIconView";
    private static final String MSG_INIT_RANDOM = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_initRandom";
    private static final String MSG_FINISH_RANDOM = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_finishRandom";
    private static final String MSG_START_LISTENER = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_startListener";
    private static final String MSG_EXPLAIN_NO_FIREFOX_FOUND = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_explainNoFirefoxFound";
    private static final String MSG_USE_DEFAULT_BROWSER = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_useDefaultBrowser";
    private static final String MSG_CONFIGURE_BROWSER = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_configureBrowser";
    private static final String MSG_DISABLE_CLEAR_TYPE = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + ".disableClearType";
    private static final String MSG_VERSION = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + ".version";
    private static final String MSG_UNINSTALLING = (class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getName() + "_uninstalling";
    private static final String OPTION_CONTEXT = "--context";
    private JAPController m_controller;
    Hashtable m_arstrCmdnLnArgs = null;
    String[] m_temp = null;
    static /* synthetic */ Class class$JAP;

    public JAP() {
    }

    JAP(String[] arrstring) {
        this.m_temp = arrstring;
        if (arrstring != null) {
            this.m_arstrCmdnLnArgs = arrstring.length > 0 ? new Hashtable(arrstring.length) : new Hashtable();
            for (int i = 0; i < arrstring.length; ++i) {
                if (i + 1 < arrstring.length && !arrstring[i + 1].startsWith("-")) {
                    this.m_arstrCmdnLnArgs.put(arrstring[i], arrstring[i + 1]);
                    continue;
                }
                this.m_arstrCmdnLnArgs.put(arrstring[i], "");
            }
        } else {
            this.m_arstrCmdnLnArgs = new Hashtable();
        }
    }

    public void startJAP() {
        IJAPMainView iJAPMainView;
        Object object;
        String string;
        Object object2;
        String[] arrstring;
        Container container;
        ISplashResponse iSplashResponse;
        Object object3;
        String string2;
        MixCascade mixCascade;
        int n;
        String string3;
        boolean bl;
        boolean bl2;
        String string4;
        String string5;
        String string6;
        block105: {
            Object object4;
            Object object5;
            String string7;
            string6 = System.getProperty("java.version", "");
            String string8 = System.getProperty("java.vendor", "");
            string5 = System.getProperty("os.name", "");
            string4 = System.getProperty("mrj.version");
            bl2 = false;
            bl = false;
            string3 = null;
            n = 0;
            mixCascade = null;
            AbstractLog abstractLog = this.isArgumentSet("--noSystemErrorLog") ? new DummyLog() : new SystemErrLog();
            if (this.getArgumentValue("--programName") != null) {
                JAPModel.getInstance().setProgramName(this.getArgumentValue("--programName"));
                string7 = JAPModel.getInstance().getProgramName();
            } else {
                string7 = "JAP/JonDo";
            }
            if (this.isArgumentSet("--version") || this.isArgumentSet("-v")) {
                System.out.println(string7 + " version: " + "00.20.001");
                System.out.println("Java Vendor: " + string8);
                System.out.println("Java Version: " + string6);
                System.out.println("OS Version: " + string5);
                System.exit(0);
            }
            System.out.println("Starting up " + string7 + " version " + "00.20.001" + ". (" + string6 + "/" + string8 + "/" + string5 + (string4 != null ? "/" + string4 : "") + ")");
            LogHolder.setLogInstance(abstractLog);
            abstractLog.setLogType(LogType.ALL);
            abstractLog.setLogLevel(4);
            LogHolder.log(7, LogType.MISC, "Pre configuration debug output enabled.");
            ((Hashtable)System.getProperties()).remove("socksProxyHost");
            ((Hashtable)System.getProperties()).remove("socksProxyPort");
            try {
                object5 = Class.forName("java.net.ProxySelector");
                ((Class)object5).getMethod("setDefault", new Class[]{object5}).invoke(object5, new Object[]{null});
            }
            catch (Exception exception) {
                LogHolder.log(4, LogType.NET, "Could not reset ProxySelector!", exception);
            }
            object5 = string7 + " must run with a 1.1.3 or higher version Java!\nYou will find more information at the " + string7 + " webpage!\nYour Java Version: ";
            if (string6.compareTo("1.0.2") <= 0) {
                System.out.println((String)object5 + string6);
                System.exit(0);
            }
            if (this.isArgumentSet("--extractHelp")) {
                if (this.getArgumentValue("--extractHelp") == null) {
                    this.setArgument("--extractHelp", ".");
                }
                if (JAPModel.getInstance().extractHelpFiles(this.getArgumentValue("--extractHelp"))) {
                    System.out.println("Help files were extracted to the directory '" + this.getArgumentValue("--extractHelp") + "'.");
                } else {
                    System.out.println("Error: Help files could not be extracted to the directory '" + this.getArgumentValue("--extractHelp") + "'!");
                }
                System.exit(0);
            }
            if (this.isArgumentSet("--help") || this.isArgumentSet("-h")) {
                System.out.println("Usage:");
                System.out.println("--help, -h:                  Show this text.");
                System.out.println("--console:                   Start " + string7 + " in console-only mode.");
                System.out.println("--allow-multiple, -a         Allow " + string7 + " to start multiple instances.");
                System.out.println("--try, -t                    Try to start " + string7 + ". If it is already started, do nothing.");
                System.out.println("--minimized, -m:             Minimize " + string7 + " on startup.");
                System.out.println("--version, -v:               Print version information.");
                System.out.println("--showDialogFormat           Show and set dialog format options.");
                System.out.println("--noSplash, -s               Suppress splash screen on startup.");
                System.out.println("--hideUpdate,                Hide all internal update features.");
                System.out.println("--programName {JAP|JonDo}    Show this program in different distributor modes.");
                System.out.println("--presentation, -p           Presentation mode (slight GUI changes).");
                System.out.println("--forwarder, -f {port}       Act as a forwarder on a specified port.");
                System.out.println("--noSystemErrorLog           Disallow logging to the standard error stream.");
                System.out.println("--listen, -l {[host][:port]} Listen on the specified interface.");
                System.out.println("--uninstall, -u              Delete all configuration and help files.");
                System.out.println("--cascade {[host][:port][:id]} Connects to the specified Mix-Cascade.");
                System.out.println("--TorMixminion\t\t\t\t Allow Tor/MixMinion alpha interface.");
                System.out.println("--portable [path_to_browser] Tell " + string7 + " that it runs in a portable environment.");
                System.out.println("--portable-jre               Tell " + string7 + " that it runs with a portable JRE.");
                System.out.println("--help-path                  Path where external html help files should be installed.");
                System.out.println("--extractHelp [directory]    Extract the internal help files to a directory.");
                System.out.println("--config, -c {Filename}:     Force " + string7 + " to use a specific configuration file.");
                System.out.println("--context {Context}:         Start " + string7 + " with a specific service provider context.");
                System.out.println("--launcher path_to_launcher  Use the given launcher for restarting " + string7 + ".");
                System.exit(0);
            }
            if (this.isArgumentSet("-console") || this.isArgumentSet("--console")) {
                bl2 = true;
            }
            if (this.isArgumentSet("--uninstall") || this.isArgumentSet("-u")) {
                bl = true;
            }
            if (string8.startsWith("Transvirtual")) {
                if (string6.compareTo("1.3") <= 0) {
                    if (!JAPMessages.init("JAPMessages")) {
                        GUIUtils.exitWithNoMessagesError("MixConfigMessages");
                    }
                    if (bl2) {
                        System.out.println(JAPMessages.getString(MSG_ERROR_NEED_NEWER_JAVA));
                    } else {
                        JAPAWTMsgBox.MsgBox(JAPMessages.getString(MSG_ERROR_NEED_NEWER_JAVA), JAPMessages.getString("error"));
                    }
                    System.exit(0);
                }
            } else if (string8.toUpperCase().indexOf("FREE SOFTWARE FOUNDATION") >= 0) {
                JAPMessages.init("JAPMessages");
                System.out.println("\n" + JAPMessages.getString(MSG_GNU_NOT_COMPATIBLE) + "\n");
            } else {
                if (string6.compareTo("1.0.2") <= 0) {
                    System.out.println((String)object5 + string6);
                    System.exit(0);
                }
                if (string6.compareTo("1.1.2") <= 0) {
                    JAPMessages.init("JAPMessages");
                    if (bl2) {
                        System.out.println(JAPMessages.getString(MSG_ERROR_NEED_NEWER_JAVA));
                    } else {
                        JAPAWTMsgBox.MsgBox(JAPMessages.getString(MSG_ERROR_NEED_NEWER_JAVA), JAPMessages.getString("error"));
                    }
                    System.exit(0);
                }
            }
            if (!this.isArgumentSet("--allow-multiple") && !this.isArgumentSet("-a")) {
                LogHolder.log(7, LogType.MISC, "Allow multiple instances not set - try to detect running instances of JAP");
                if (AbstractOS.getInstance().isMultipleStart(class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP, "JAP", ClassUtil.getFile()) || AbstractOS.getInstance().isMultipleStart("jondo.console.JonDoConsole", "JonDoConsole", "JAPMacintosh")) {
                    JAPMessages.init("JAPMessages");
                    String string9 = JAPMessages.getString(MSG_ERROR_JONDO_ALREADY_RUNNING) + (AbstractOS.getInstance() instanceof WindowsOS ? "\n" + JAPMessages.getString(MSG_ERROR_JONDO_ALREADY_RUNNING_WIN) : "");
                    if (bl2 || this.isArgumentSet("--try") || this.isArgumentSet("-t")) {
                        System.out.println(string9);
                    } else {
                        object4 = new JAPDialog.LinkedInformationAdapter(){

                            public boolean isOnTop() {
                                return true;
                            }
                        };
                        JAPDialog.showErrorDialog((Component)null, string9, (JAPDialog.ILinkedInformation)object4);
                    }
                    System.exit(0);
                }
            }
            this.m_controller = JAPController.getInstance();
            boolean bl3 = this.isArgumentSet("--portable");
            this.m_controller.setPortableMode(bl3);
            this.m_controller.setAllowMultipleInstances(this.isArgumentSet("--allow-multiple") || this.isArgumentSet("-a"));
            if (this.isArgumentSet("--launcher")) {
                this.m_controller.setLauncher(this.getArgumentValue("--launcher"));
            }
            if ((object4 = this.getArgumentValue(OPTION_CONTEXT)) != null) {
                JAPModel.getInstance().setContext((String)object4);
            }
            string2 = null;
            boolean bl4 = false;
            string2 = this.getArgumentValue("--config");
            if (string2 == null) {
                string2 = this.getArgumentValue("-c");
            }
            if (string2 == null && bl3 && (object3 = ClassUtil.getClassDirectory(class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP)) != null) {
                string2 = ClassUtil.getClassDirectory(class$JAP == null ? (class$JAP = JAP.class$("JAP")) : class$JAP).getParent() + File.separator + "jap.conf";
                bl4 = true;
            }
            if (string2 != null) {
                LogHolder.log(5, LogType.MISC, "Loading config file '" + string2 + "'.");
            }
            object3 = null;
            try {
                this.m_controller.preLoadConfigFile(string2);
            }
            catch (FileNotFoundException fileNotFoundException) {
                LogHolder.log(1, LogType.MISC, fileNotFoundException);
                if (bl4) break block105;
                object3 = "File not found: " + fileNotFoundException.getMessage();
            }
        }
        Locale locale = Locale.getDefault();
        String string10 = locale.getLanguage().equals("de") ? "Lade Internationalisierung" : (locale.getLanguage().equals("fr") ? "Chargement des param\u00e8tres d'internationalisation" : (locale.getLanguage().equals("cs") ? "Nahr\u00e1v\u00e1m internacionalizaci" : "Loading internationalisation"));
        JAPDialog.setGlobalTitle("JonDo");
        if (bl2) {
            JAPDialog.setConsoleOnly(true);
            iSplashResponse = new ConsoleSplash();
            iSplashResponse.setText(string10);
        } else if (this.isArgumentSet("--noSplash") || this.isArgumentSet("-s") || !JAPModel.getInstance().getShowSplashScreen()) {
            iSplashResponse = new ConsoleSplash();
            iSplashResponse.setText(string10);
        } else {
            container = new Frame();
            iSplashResponse = new JAPSplash((Frame)container, string10);
            ((JAPSplash)iSplashResponse).centerOnScreen();
            ((JAPSplash)iSplashResponse).setVisible(true);
            GUIUtils.setAlwaysOnTop((JAPSplash)iSplashResponse, true);
        }
        if (object3 != null) {
            iSplashResponse.setText((String)object3);
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            System.exit(0);
        }
        if (!JAPMessages.isInitialised()) {
            JAPMessages.init("JAPMessages");
        }
        if (!bl2 && !bl) {
            JAPModel.getInstance().setDialogFormatShown(this.isArgumentSet("--showDialogFormat"));
            GUIUtils.setIconResizer(JAPModel.getInstance().getIconResizer());
            try {
                container = new JLabel();
                container = null;
            }
            catch (Throwable throwable) {
                if (iSplashResponse instanceof JAPSplash) {
                    ((JAPSplash)iSplashResponse).setVisible(false);
                }
                arrstring = "";
                if (AbstractOS.getInstance() instanceof WindowsOS) {
                    arrstring = "\n" + JAPMessages.getString(MSG_DISABLE_CLEAR_TYPE);
                }
                LogHolder.log(1, LogType.GUI, throwable);
                JAPAWTMsgBox.MsgBox(JAPMessages.getString("errorSwingNotInstalled", (Object)arrstring) + "\n\n" + ClassUtil.getShortClassName(throwable.getClass()) + "\n\n" + "http://www.jondonym.net", JAPMessages.getString("error"));
                System.exit(0);
            }
        }
        if (this.isArgumentSet("--noSystemErrorLog")) {
            JAPDebug.ms_bSystemErrorAllowed = false;
        }
        LogHolder.setLogInstance(JAPDebug.getInstance());
        JAPDebug.getInstance().setLogType(LogType.ALL);
        JAPDebug.getInstance().setLogLevel(4);
        if (bl) {
            int n2 = 0;
            iSplashResponse.setText(JAPMessages.getString(MSG_UNINSTALLING));
            try {
                this.m_controller.uninstall(string2);
            }
            catch (IOException iOException) {
                LogHolder.log(1, LogType.MISC, iOException);
                n2 = -1;
            }
            if (iSplashResponse instanceof JAPSplash) {
                ((JAPSplash)iSplashResponse).setVisible(false);
            }
            System.exit(n2);
        }
        iSplashResponse.setText(JAPMessages.getString(MSG_INIT_RANDOM));
        Thread thread = new Thread(new Runnable(){

            public void run() {
                new SecureRandom().nextInt();
                KeyPool.start(false);
            }
        });
        thread.setPriority(1);
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException interruptedException) {
            LogHolder.log(5, LogType.CRYPTO, interruptedException);
        }
        if (!bl2 && !string5.regionMatches(true, 0, "mac", 0, 3)) {
            LogHolder.log(7, LogType.GUI, "Setting Cross Platform Look-And-Feel!");
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.GUI, "Exception while setting Cross Platform Look-And-Feel!");
            }
        }
        if (this.isArgumentSet("--listen") || this.isArgumentSet("-l")) {
            string3 = this.getArgumentValue("--listen");
            if (string3 == null) {
                string3 = this.getArgumentValue("-l");
            }
            if (string3 != null) {
                try {
                    arrstring = new ListenerInterface(string3);
                    string3 = arrstring.getHost();
                    n = arrstring.getPort();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        if (this.isArgumentSet("--cascade")) {
            arrstring = this.getArgumentValue("--cascade");
            try {
                object2 = new StringTokenizer((String)arrstring, ":");
                string = null;
                object = null;
                int n3 = 6544;
                if (((StringTokenizer)object2).hasMoreTokens()) {
                    string = ((StringTokenizer)object2).nextToken();
                }
                if (((StringTokenizer)object2).hasMoreTokens()) {
                    n3 = Integer.parseInt(((StringTokenizer)object2).nextToken());
                }
                if (((StringTokenizer)object2).hasMoreTokens()) {
                    object = ((StringTokenizer)object2).nextToken();
                }
                mixCascade = new MixCascade("Commandline Cascade", (String)object, string, n3);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        iSplashResponse.setText(JAPMessages.getString(MSG_STARTING_CONTROLLER));
        this.m_controller.initialize(this.isArgumentSet("--presentation") || this.isArgumentSet("-p"), this.isArgumentSet("--TorMixminion"));
        if (this.isArgumentSet("--hideUpdate")) {
            this.m_controller.hideUpdateDialogs();
        }
        arrstring = this.m_temp;
        if (this.m_temp == null || !this.isArgumentSet("--allow-multiple") && !this.isArgumentSet("-a")) {
            if (this.m_temp == null) {
                arrstring = new String[1];
            } else {
                arrstring = new String[this.m_temp.length + 1];
                System.arraycopy(this.m_temp, 0, arrstring, 0, this.m_temp.length);
            }
            arrstring[arrstring.length - 1] = "-a";
        }
        this.m_controller.initCommandLineArgs(arrstring);
        if (this.isArgumentSet("--portable-jre")) {
            this.m_controller.setPortableJava(true);
        }
        boolean bl5 = false;
        if (this.isArgumentSet("-forwarding_state")) {
            bl5 = true;
        }
        object2 = this.buildPortableFFCommand(iSplashResponse);
        AbstractOS.getInstance().init(new AbstractOS.IURLErrorNotifier(){

            public void checkNotify(URL uRL) {
            }
        }, new AbstractOS.AbstractURLOpener((String)object2){
            private final /* synthetic */ String val$BROWSER_CMD;
            {
                this.val$BROWSER_CMD = string;
            }

            public boolean openURL(URL uRL, String string) {
                if (!JAP.this.m_controller.isPortableMode()) {
                    return false;
                }
                if (!super.openURL(uRL, string)) {
                    if (!(string == null || this.getBrowserCommand() != null && string.equals(this.getBrowserCommand()))) {
                        return false;
                    }
                    JAPDialog.LinkedInformationAdapter linkedInformationAdapter = new JAPDialog.LinkedInformationAdapter(){

                        public boolean isApplicationModalityForced() {
                            return true;
                        }

                        public boolean isOnTop() {
                            return true;
                        }
                    };
                    int n = JAPDialog.showConfirmDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString(MSG_EXPLAIN_NO_FIREFOX_FOUND), new JAPDialog.Options(1){

                        public String getYesOKText() {
                            return JAPMessages.getString(MSG_USE_DEFAULT_BROWSER);
                        }

                        public String getNoText() {
                            return JAPMessages.getString(MSG_CONFIGURE_BROWSER);
                        }
                    }, 2, (JAPDialog.ILinkedInformation)linkedInformationAdapter);
                    if (n == 0) {
                        return false;
                    }
                    if (n == 1) {
                        JAPController.getInstance().showConfigDialog("UI_TAB", this);
                    }
                }
                return true;
            }

            public URL getDefaultURL() {
                return JAPModel.getInstance().getHelpURL();
            }

            public String getBrowserPath() {
                return JAP.this.getArgumentValue("--portable");
            }

            public String getBrowserCommand() {
                return this.val$BROWSER_CMD;
            }
        });
        JAPModel.getInstance().setForwardingStateModuleVisible(bl5);
        iSplashResponse.setText(JAPMessages.getString(MSG_LOADING_SETTINGS));
        this.m_controller.loadConfigFile(string2, iSplashResponse);
        string = this.getArgumentValue("--forwarder");
        if (string == null) {
            string = this.getArgumentValue("-f");
        }
        if (string != null) {
            try {
                JAPModel.getInstance().getRoutingSettings().setServerPort(Integer.parseInt(string));
            }
            catch (NumberFormatException numberFormatException) {
                LogHolder.log(2, LogType.MISC, numberFormatException);
            }
        }
        iSplashResponse.setText(JAPMessages.getString(MSG_INIT_DLL));
        this.m_controller.setView(null, iSplashResponse);
        object = null;
        if (iSplashResponse instanceof Window) {
            object = (Window)((Object)iSplashResponse);
        }
        JAPDll.init(this.isArgumentSet("--dllAdminUpdate"), this.getArgumentValue("--dllAdminUpdate"), (Window)object);
        LogHolder.log(6, LogType.MISC, "Welcome! This is version 00.20.001 of JAP.");
        LogHolder.log(6, LogType.MISC, "Java " + string6 + " running on " + string5 + ".");
        if (string4 != null) {
            LogHolder.log(6, LogType.MISC, "MRJ Version is " + string4 + ".");
        }
        iSplashResponse.setText(JAPMessages.getString(MSG_INIT_VIEW));
        if (!bl2) {
            iJAPMainView = new JAPNewView(JAPModel.getInstance().getProgramName() + " (" + JAPMessages.getString(MSG_VERSION) + ": " + "00.20.001" + "" + ")", this.m_controller);
            iJAPMainView.create(true);
        } else {
            iJAPMainView = new ConsoleJAPMainView();
        }
        this.m_controller.addJAPObserver(iJAPMainView);
        this.m_controller.addEventListener(iJAPMainView);
        if (iSplashResponse instanceof JAPSplash) {
            this.m_controller.setView(iJAPMainView, new JAPSplash((Frame)((Object)iJAPMainView), JAPMessages.getString(JAPController.MSG_FINISHING)));
        } else {
            this.m_controller.setView(iJAPMainView, new ConsoleSplash());
        }
        if (!bl2) {
            iSplashResponse.setText(JAPMessages.getString(MSG_INIT_ICON_VIEW));
            JAPViewIconified jAPViewIconified = new JAPViewIconified((AbstractJAPMainView)iJAPMainView);
            iJAPMainView.registerViewIconified(jAPViewIconified);
        }
        if (this.isArgumentSet("--forwarder") || this.isArgumentSet("-f")) {
            this.m_controller.enableForwardingServer(true);
        }
        boolean bl6 = JAPModel.getMoveToSystrayOnStartup();
        if (this.isArgumentSet("-minimized") || this.isArgumentSet("--minimized") || this.isArgumentSet("-m")) {
            bl6 = true;
        }
        iSplashResponse.setText(JAPMessages.getString(MSG_START_LISTENER));
        if (!this.m_controller.startHTTPListener(string3, n)) {
            if (this.isArgumentSet("--try") || this.isArgumentSet("-t")) {
                System.exit(0);
            } else {
                iJAPMainView.disableSetAnonMode();
            }
        }
        if (!bl2) {
            AbstractJAPMainView abstractJAPMainView = (AbstractJAPMainView)iJAPMainView;
            if (bl6) {
                String string11 = JAPDll.getDllVersion();
                boolean bl7 = false;
                if (string11 == null || string11.compareTo("00.02.00") < 0) {
                    abstractJAPMainView.setVisible(true);
                    abstractJAPMainView.toFront();
                    bl7 = true;
                }
                if (!abstractJAPMainView.hideWindowInTaskbar() && !bl7) {
                    abstractJAPMainView.setVisible(true);
                    abstractJAPMainView.toFront();
                }
            } else if (JAPModel.getMinimizeOnStartup()) {
                abstractJAPMainView.setVisible(true);
                abstractJAPMainView.showIconifiedView(true);
            } else {
                GUIUtils.setAlwaysOnTop(abstractJAPMainView, true);
                abstractJAPMainView.setVisible(true);
                abstractJAPMainView.toFront();
                GUIUtils.setAlwaysOnTop(abstractJAPMainView, false);
            }
            if (iSplashResponse instanceof JAPSplash) {
                ((JAPSplash)iSplashResponse).dispose();
            }
        }
        JAPDll.checkDllVersion(true);
        if (mixCascade != null) {
            try {
                this.m_controller.setCurrentMixCascade(mixCascade);
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.MISC, "Could not set Cascade specified on the Command line! Ignoring information given and continue...");
            }
        }
        this.m_controller.initialRun(string3, n);
        if (bl2) {
            iJAPMainView.setVisible(true);
        }
        if (AbstractOS.getInstance() instanceof MacOS) {
            MacOSXLib.init();
        }
    }

    private String getArgumentValue(String string) {
        String string2 = (String)this.m_arstrCmdnLnArgs.get(string);
        if (string2 != null && string2.trim().length() == 0) {
            string2 = null;
        }
        return string2;
    }

    private void setArgument(String string, String string2) {
        this.m_arstrCmdnLnArgs.put(string, string2);
    }

    private boolean isArgumentSet(String string) {
        return this.m_arstrCmdnLnArgs.containsKey(string);
    }

    private String buildPortableFFCommand(ISplashResponse iSplashResponse) {
        String string;
        if (this.isArgumentSet("--portable")) {
            string = JAPModel.getInstance().getPortableBrowserpath();
            if (string == null) {
                string = this.getArgumentValue("--portable");
                JAPModel.getInstance().setPortableBrowserpath(string);
            }
            if (string != null) {
                string = AbstractOS.createBrowserCommand(string);
            }
        } else {
            string = null;
        }
        String string2 = this.isArgumentSet("--help-path") ? this.getArgumentValue("--help-path") : (this.isArgumentSet("--portable-help-path") ? this.getArgumentValue("--portable-help-path") : null);
        if (this.isArgumentSet("--portable")) {
            block16: {
                if (string2 == null && this.isArgumentSet("--jar-path")) {
                    String string3 = this.getArgumentValue("--jar-path");
                    String string4 = ".." + File.separator;
                    try {
                        String string5;
                        int n;
                        if (this.m_temp == null || this.m_temp.length <= 0 || string3 == null) break block16;
                        while (string3.startsWith(string4)) {
                            n = (string3 = string3.substring(string4.length(), string3.length())).indexOf(File.separator);
                            if (n >= 0 && string3.length() >= n + 1) {
                                string3 = string3.substring(string3.indexOf(File.separator) + File.separator.length(), string3.length());
                                continue;
                            }
                            string3 = "";
                        }
                        if (string3.trim().length() <= 0 || (n = (string5 = this.m_temp[0]).indexOf(string3)) <= 0) break block16;
                        string5 = string5.substring(0, n);
                        String[] arrstring = new File(string5).list();
                        for (int i = 0; i < arrstring.length; ++i) {
                            if (!arrstring[i].toUpperCase().equals("help".toUpperCase())) continue;
                            string2 = string5;
                            break;
                        }
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.MISC, exception);
                    }
                }
            }
            if (string2 == null) {
                ZipFile zipFile = ClassUtil.getJarFile();
                if (zipFile != null) {
                    string2 = new File(zipFile.getName()).getParent();
                } else if (!JAPModel.getInstance().isHelpPathChangeable()) {
                    string2 = ClassUtil.getClassDirectory(this.getClass()).getParent();
                }
            }
        }
        if (string2 != null) {
            String string6 = iSplashResponse.getText();
            iSplashResponse.setText(JAPMessages.getString(JAPController.MSG_UPDATING_HELP));
            JAPModel.getInstance().setHelpPath(new File(string2), true);
            iSplashResponse.setText(string6);
        }
        return string;
    }

    public static void main(String[] arrstring) {
        try {
            JAP jAP = new JAP(arrstring);
            jAP.startJAP();
        }
        catch (Throwable throwable) {
            System.out.println("A severe problem was encountered on startup!");
            if (throwable instanceof NoClassDefFoundError && throwable.getMessage().indexOf("java.awt") >= 0) {
                System.out.println("Either your system does not support a graphical interface, or you started the program with a user that has no permission to use the graphical interface.");
            }
            throwable.printStackTrace();
            System.exit(0);
        }
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

