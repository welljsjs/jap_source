/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.client.TrustModel;
import anon.crypto.JAPCertificate;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.IProxyInterfaceGetter;
import anon.infoservice.IServiceContextContainer;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ProxyInterface;
import anon.mixminion.mmrdescription.MMRList;
import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.JAPMessages;
import anon.util.RecursiveFileTool;
import anon.util.ResourceLoader;
import anon.util.Util;
import gui.GUIUtils;
import gui.JAPDll;
import gui.dialog.JAPDialog;
import gui.help.AbstractHelpFileStorageManager;
import gui.help.IHelpModel;
import gui.help.LocalHelpFileStorageManager;
import jap.JAPController;
import jap.JARHelpFileStorageManager;
import jap.forward.JAPRoutingSettings;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import javax.swing.UIManager;
import logging.LogHolder;
import logging.LogType;

public final class JAPModel
extends Observable
implements IHelpModel,
IServiceContextContainer {
    public static final String MACOSX_LIB_NEEDS_UPDATE = "macOSXLibNeedsUpdate";
    public static final String DLL_VERSION_UPDATE = "dllVersionUpdate";
    public static final String DLL_VERSION_WARNING_BELOW = "dllWarningVersion";
    public static final int CONNECTION_ALLOW_ANONYMOUS = 0;
    public static final int CONNECTION_FORCE_ANONYMOUS = 1;
    public static final int CONNECTION_BLOCK_ANONYMOUS = 2;
    public static final String XML_ANONYMIZED_HTTP_HEADERS = "httpHeaderAnonymization";
    public static final String XML_DISABLE_ALL_HEADER_PROCESSING = "disableAllHeaderProcessing";
    public static final String XML_ANON_DEBUG_MODE = "enableANONDebugMode";
    public static final String XML_REMIND_OPTIONAL_UPDATE = "remindOptionalUpdate";
    public static final String XML_REMIND_JAVA_UPDATE = "remindJavaUpdate";
    public static final String XML_RESTRICT_CASCADE_AUTO_CHANGE = "restrictCascadeAutoChange";
    public static final String XML_ASK_FOR_NON_ANONYMOUS_SURFING = "askForUnprotectedSurfing";
    public static final String XML_ALLOW_NON_ANONYMOUS_SURFING = "allowUnprotectedSurfing";
    public static final String XML_ATTR_ACTIVATED = "activated";
    public static final String XML_FONT_SIZE = "fontSize";
    public static final String XML_CONFIG_WINDOW = "ConfigWindow";
    public static final String XML_SIZE = "Size";
    public static final String XML_ICONIFIED_WINDOW = "IconifiedWindow";
    public static final String XML_ATTR_ICONIFIED_ON_TOP = "alwaysOnTop";
    public static final String XML_HELP_WINDOW = "HelpWindow";
    public static final String XML_ATTR_WIDTH = "width";
    public static final String XML_ATTR_HEIGHT = "height";
    public static final String XML_ATTR_SAVE = "save";
    public static final String AUTO_CHANGE_NO_RESTRICTION = "none";
    public static final String AUTO_CHANGE_RESTRICT_TO_PAY = "pay";
    public static final String AUTO_CHANGE_RESTRICT = "restrict";
    public static final String NO_HELP_STORAGE_MANAGER = "help_internal";
    public static final int MAX_FONT_SIZE = 3;
    public static final Integer CHANGED_INFOSERVICE_AUTO_UPDATE = new Integer(0);
    public static final Integer CHANGED_ALLOW_INFOSERVICE_DIRECT_CONNECTION = new Integer(1);
    public static final Integer CHANGED_ALLOW_UPDATE_DIRECT_CONNECTION = new Integer(2);
    public static final Integer CHANGED_NOTIFY_JAP_UPDATES = new Integer(3);
    public static final Integer CHANGED_NOTIFY_JAVA_UPDATES = new Integer(4);
    public static final Integer CHANGED_AUTO_CONNECT = new Integer(5);
    public static final Integer CHANGED_AUTO_RECONNECT = new Integer(6);
    public static final Integer CHANGED_CASCADE_AUTO_CHANGE = new Integer(7);
    public static final Integer CHANGED_HELP_PATH = new Integer(9);
    public static final Integer CHANGED_DLL_UPDATE = new Integer(10);
    public static final Integer CHANGED_MACOSX_LIBRARY_UPDATE = new Integer(11);
    public static final Integer CHANGED_ANONYMIZED_HTTP_HEADERS = new Integer(12);
    public static final Integer CHANGED_CONTEXT = new Integer(13);
    public static final Integer CHANGED_SHOW_CLOSED_BUTTON = new Integer(14);
    public static final Integer CHANGED_PROXY = new Integer(15);
    public static final Integer CHANGED_CONFIG_ASSISTANT_SHOWUP = new Integer(16);
    private static final String[] MSG_CONNECTION_ANONYMOUS = new String[]{(class$jap$JAPModel == null ? (class$jap$JAPModel = JAPModel.class$("jap.JAPModel")) : class$jap$JAPModel).getName() + "_anonymousConnectionAllow", (class$jap$JAPModel == null ? (class$jap$JAPModel = JAPModel.class$("jap.JAPModel")) : class$jap$JAPModel).getName() + "_anonymousConnectionForce", (class$jap$JAPModel == null ? (class$jap$JAPModel = JAPModel.class$("jap.JAPModel")) : class$jap$JAPModel).getName() + "_anonymousConnectionBlock"};
    private static final int DIRECT_CONNECTION_INFOSERVICE = 0;
    private static final int DIRECT_CONNECTION_PAYMENT = 1;
    private static final int DIRECT_CONNECTION_UPDATE = 2;
    private int m_HttpListenerPortNumber = 4001;
    private boolean m_bHttpListenerIsLocal = true;
    private ProxyInterface m_proxyInterface = null;
    private ProxyInterface m_proxyAnon;
    private final Object SYNC_ANON_PROXY = new Object();
    private IMutableProxyInterface m_mutableProxyInterface;
    private boolean m_bAutoConnect;
    private boolean m_bAutoReConnect;
    private int m_iDummyTrafficIntervall = -1;
    private long m_msInterfaceBlockTimeout = 180000L;
    private boolean m_bSmallDisplay = false;
    private boolean m_bInfoServiceDisabled = false;
    private boolean m_bMinimizeOnStartup = false;
    private boolean m_bMoveToSystrayOnStartup = false;
    private int m_iDefaultView = 2;
    private boolean m_bSaveMainWindowPosition;
    private boolean m_bSaveConfigWindowPosition;
    private boolean m_bSaveIconifiedWindowPosition;
    private boolean m_bSaveHelpWindowPosition;
    private Point m_OldMainWindowLocation = null;
    private Point m_iconifiedWindowLocation = null;
    private Point m_configWindowLocation = null;
    private Point m_helpWindowLocation = null;
    private boolean m_bGoodByMessageNeverRemind = false;
    private int m_iPaymentAnonymousConnectionSetting;
    private int m_iInfoServiceAnonymousConnectionSetting;
    private int m_iUpdateAnonymousConnectionSetting;
    private boolean m_bAskForAnyNonAnonymousRequest;
    private boolean m_bAllowNonAnonymous = true;
    private boolean m_bRemindOptionalUpdate;
    private boolean m_bRemindJavaUpdate;
    private boolean m_bTorActivated;
    private boolean m_bMixMinionActivated;
    private boolean m_bANONDebugMode = false;
    private boolean m_bChooseCascasdeConnectionAutomatically;
    private boolean m_bChooseCascasdeAutomaticallyOnStartup;
    private boolean m_bHideAnonymityPopups = false;
    private boolean m_bHideInfoServicePopups = false;
    private boolean m_bHidePaymentPopups = false;
    private boolean m_bMiniViewOnTop;
    private String m_strLookAndFeel;
    private Vector m_vecLookAndFeels = new Vector();
    private UIManager.LookAndFeelInfo[] m_systemLookAndFeels;
    private Object LOOK_AND_FEEL_SYNC = new Object();
    private boolean m_bShowDialogFormat = false;
    private boolean m_bShowCloseButton = false;
    private boolean m_bAnonymizedHttpHeaders = false;
    private boolean m_bDisableAllHeaderProcessing = false;
    private boolean m_bShowConfigAssistant = true;
    private String m_context = "jondonym";
    private String m_strDistributorMode = "JAP/JonDo";
    private String m_strRelativeBrowserPath;
    private String m_strLauncher = null;
    private int m_fontSize = 0;
    private GUIUtils.IIconResizer m_resizer = new GUIUtils.IIconResizer(){

        public double getResizeFactor() {
            return 1.0 + (double)JAPModel.this.getFontSize() * 0.1;
        }
    };
    private static JAPModel ms_TheModel = null;
    private JAPCertificate m_certJAPCodeSigning = null;
    private int m_TorMaxConnectionsPerRoute = 1000;
    private int m_TorMaxRouteLen = 3;
    private int m_TorMinRouteLen = 2;
    private boolean m_bTorUseNoneDefaultDirServer = false;
    private int m_mixminionRouteLen = 2;
    private String m_mixminionMyEMail = "";
    private String m_mixminionPassword = null;
    private byte[] m_mixminionPasswordHash = null;
    private String m_mixminionKeyring = "";
    private Vector m_mixminionMessages = null;
    private MMRList m_mixminionRouters = null;
    private Vector m_mixminionFragments = null;
    private boolean m_bPreCreateAnonRoutes = false;
    private boolean m_bUseProxyAuthentication = false;
    private JAPController.AnonConnectionChecker m_connectionChecker;
    private boolean m_bShowSplashScreen = true;
    private boolean m_bShowSplashDisabled = false;
    private boolean m_bStartPortableFirefox = true;
    private String m_helpPath = null;
    private boolean m_bPortableHelp = false;
    private Dimension m_iconifiedSize;
    private Dimension m_configSize;
    private Dimension m_helpSize;
    private boolean m_bSaveHelpSize;
    private boolean m_bSaveConfigSize;
    private JAPRoutingSettings m_routingSettings;
    private String m_configFileName;
    private boolean m_forwardingStateModuleVisible;
    private String m_paymentPassword;
    private String m_bDllUpdatePath;
    private long m_noWarningForDllVersionBelow = 0L;
    private boolean m_bMacOSXLibraryUpdateAtStartupNeeded = false;
    private BigInteger m_iDialogVersion = new BigInteger("-1");
    private AbstractHelpFileStorageManager m_helpFileStorageManager;
    private Hashtable m_acceptedTCs = new Hashtable();
    static /* synthetic */ Class class$jap$JAPModel;

    private JAPModel() {
        try {
            this.m_certJAPCodeSigning = JAPCertificate.getInstance(ResourceLoader.loadResource("certificates/japcodesigning.cer"));
        }
        catch (Throwable throwable) {
            this.m_certJAPCodeSigning = null;
        }
        this.m_routingSettings = new JAPRoutingSettings();
        this.m_configFileName = null;
        this.m_forwardingStateModuleVisible = false;
        this.m_mutableProxyInterface = new IMutableProxyInterface(){

            public IProxyInterfaceGetter getProxyInterface(boolean bl) {
                return new IProxyInterfaceGetter(){

                    public ImmutableProxyInterface getProxyInterface() {
                        ProxyInterface proxyInterface = JAPModel.this.m_proxyInterface;
                        if (proxyInterface != null && proxyInterface.isValid()) {
                            return proxyInterface;
                        }
                        return null;
                    }
                };
            }
        };
        this.m_helpFileStorageManager = ClassUtil.getJarFile() == null ? new LocalHelpFileStorageManager("JonDo") : new JARHelpFileStorageManager();
        this.m_bANONDebugMode = false;
    }

    public static JAPModel getInstance() {
        if (ms_TheModel == null) {
            ms_TheModel = new JAPModel();
        }
        return ms_TheModel;
    }

    public String getPortableBrowserpath() {
        return this.m_strRelativeBrowserPath;
    }

    public void setPortableBrowserpath(String string) {
        this.m_strRelativeBrowserPath = string == null || string.trim().length() <= 0 ? null : string;
    }

    public static String[] getMsgConnectionAnonymous() {
        return MSG_CONNECTION_ANONYMOUS;
    }

    public ProxyInterface getProxyInterface() {
        return this.m_proxyInterface;
    }

    public IMutableProxyInterface getMutableProxyInterface() {
        return this.m_mutableProxyInterface;
    }

    synchronized void setProxyListener(ProxyInterface proxyInterface) {
        if (!Util.equals(this.m_proxyInterface, proxyInterface)) {
            if (proxyInterface != null) {
                this.m_proxyInterface = proxyInterface;
            }
            this.setChanged();
        }
        this.notifyObservers(CHANGED_PROXY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setAutoConnect(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bAutoConnect != bl) {
                this.m_bAutoConnect = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_AUTO_CONNECT);
        }
    }

    public static boolean isAutoConnect() {
        return JAPModel.ms_TheModel.m_bAutoConnect;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAutoReConnect(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bAutoReConnect != bl) {
                this.m_bAutoReConnect = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_AUTO_RECONNECT);
        }
    }

    public static boolean isAutomaticallyReconnected() {
        return JAPModel.ms_TheModel.m_bAutoReConnect;
    }

    public void setLookAndFeel(String string) {
        this.m_strLookAndFeel = string;
    }

    public Vector getLookAndFeelFiles() {
        return (Vector)this.m_vecLookAndFeels.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addLookAndFeelFile(File file) {
        if (file != null) {
            Vector vector = this.m_vecLookAndFeels;
            synchronized (vector) {
                if (!this.m_vecLookAndFeels.contains(file)) {
                    this.m_vecLookAndFeels.addElement(file);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeLookAndFeelFile(File file) {
        return this.m_vecLookAndFeels.removeElement(file);
    }

    public String getLookAndFeel() {
        return this.m_strLookAndFeel;
    }

    public boolean isTorActivated() {
        return this.m_bTorActivated;
    }

    public void setTorActivated(boolean bl) {
        this.m_bTorActivated = bl;
    }

    public void setMixMinionActivated(boolean bl) {
        this.m_bMixMinionActivated = bl;
    }

    public boolean isMixMinionActivated() {
        return this.m_bMixMinionActivated;
    }

    public boolean isANONDebugMode() {
        return this.m_bANONDebugMode;
    }

    public void setANONDebugMode(boolean bl) {
        this.m_bANONDebugMode = bl;
    }

    protected void setMinimizeOnStartup(boolean bl) {
        this.m_bMinimizeOnStartup = bl;
    }

    public static boolean getMinimizeOnStartup() {
        return JAPModel.ms_TheModel.m_bMinimizeOnStartup;
    }

    protected void setMoveToSystrayOnStartup(boolean bl) {
        this.m_bMoveToSystrayOnStartup = bl;
    }

    public static boolean getMoveToSystrayOnStartup() {
        return JAPModel.ms_TheModel.m_bMoveToSystrayOnStartup;
    }

    protected void setDefaultView(int n) {
        this.m_iDefaultView = n;
    }

    public static int getDefaultView() {
        return JAPModel.ms_TheModel.m_iDefaultView;
    }

    protected void setSaveMainWindowPosition(boolean bl) {
        this.m_bSaveMainWindowPosition = bl;
    }

    public void setSaveConfigWindowPosition(boolean bl) {
        this.m_bSaveConfigWindowPosition = bl;
    }

    public void setSaveIconifiedWindowPosition(boolean bl) {
        this.m_bSaveIconifiedWindowPosition = bl;
    }

    public void setSaveHelpWindowPosition(boolean bl) {
        this.m_bSaveHelpWindowPosition = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSystemLookAndFeels() {
        Object object = this.LOOK_AND_FEEL_SYNC;
        synchronized (object) {
            this.m_systemLookAndFeels = UIManager.getInstalledLookAndFeels();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isSystemLookAndFeel(String string) {
        Object object = this.LOOK_AND_FEEL_SYNC;
        synchronized (object) {
            if (this.m_systemLookAndFeels == null || string == null) {
                return false;
            }
            for (int i = 0; i < this.m_systemLookAndFeels.length; ++i) {
                if (this.m_systemLookAndFeels[i] == null || !this.m_systemLookAndFeels[i].getClassName().equals(string)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isIconifiedWindowLocationSaved() {
        return this.m_bSaveIconifiedWindowPosition;
    }

    public void setIconifiedWindowLocation(Point point) {
        this.m_iconifiedWindowLocation = point;
    }

    public Point getIconifiedWindowLocation() {
        if (this.isIconifiedWindowLocationSaved()) {
            return this.m_iconifiedWindowLocation;
        }
        return null;
    }

    public boolean isHelpWindowLocationSaved() {
        return this.m_bSaveHelpWindowPosition;
    }

    public void setHelpWindowLocation(Point point) {
        this.m_helpWindowLocation = point;
    }

    public Point getHelpWindowLocation() {
        if (this.isHelpWindowLocationSaved()) {
            return this.m_helpWindowLocation;
        }
        return null;
    }

    public boolean isConfigWindowLocationSaved() {
        return this.m_bSaveConfigWindowPosition;
    }

    public void setConfigWindowLocation(Point point) {
        this.m_configWindowLocation = point;
    }

    public Point getConfigWindowLocation() {
        if (this.isConfigWindowLocationSaved()) {
            return this.m_configWindowLocation;
        }
        return null;
    }

    public static boolean isMainWindowLocationSaved() {
        return JAPModel.ms_TheModel.m_bSaveMainWindowPosition;
    }

    protected void setMainWindowLocation(Point point) {
        this.m_OldMainWindowLocation = point;
    }

    public static Point getMainWindowLocation() {
        if (JAPModel.isMainWindowLocationSaved()) {
            return JAPModel.ms_TheModel.m_OldMainWindowLocation;
        }
        return null;
    }

    public boolean isDialogFormatShown() {
        return this.m_bShowDialogFormat;
    }

    public void setDialogFormatShown(boolean bl) {
        this.m_bShowDialogFormat = bl;
    }

    protected void setDummyTraffic(int n) {
        this.m_iDummyTrafficIntervall = n;
    }

    public static int getDummyTraffic() {
        return JAPModel.ms_TheModel.m_iDummyTrafficIntervall;
    }

    protected void setInterfaceBlockTimeout(long l) {
        this.m_msInterfaceBlockTimeout = l;
    }

    public static long getInterfaceBlockTimeout() {
        return JAPModel.ms_TheModel.m_msInterfaceBlockTimeout;
    }

    protected void setHttpListenerPortNumber(int n) {
        this.m_HttpListenerPortNumber = n;
    }

    public void setAnonConnectionChecker(JAPController.AnonConnectionChecker anonConnectionChecker) {
        this.m_connectionChecker = anonConnectionChecker;
    }

    public boolean isReminderForOptionalUpdateActivated() {
        return this.m_bRemindOptionalUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReminderForOptionalUpdate(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bRemindOptionalUpdate != bl) {
                this.m_bRemindOptionalUpdate = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_NOTIFY_JAP_UPDATES);
        }
    }

    public boolean isReminderForJavaUpdateActivated() {
        return this.m_bRemindJavaUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReminderForJavaUpdate(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bRemindJavaUpdate != bl) {
                this.m_bRemindJavaUpdate = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_NOTIFY_JAVA_UPDATES);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCascadeAutoSwitch(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bChooseCascasdeConnectionAutomatically != bl) {
                this.m_bChooseCascasdeConnectionAutomatically = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_CASCADE_AUTO_CHANGE);
        }
    }

    public boolean isCascadeAutoSwitched() {
        return this.m_bChooseCascasdeConnectionAutomatically;
    }

    public void setAutoChooseCascadeOnStartup(boolean bl) {
        this.m_bChooseCascasdeAutomaticallyOnStartup = false;
    }

    public boolean isCascadeAutoChosenOnStartup() {
        return this.m_bChooseCascasdeAutomaticallyOnStartup;
    }

    public boolean isAnonConnected() {
        return this.m_connectionChecker.checkAnonConnected();
    }

    public boolean isNonAnonymousAllowed() {
        return this.m_bAllowNonAnonymous;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setNonAnonymousAllowed(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bAllowNonAnonymous != bl) {
                this.m_bAllowNonAnonymous = bl;
                this.setChanged();
            }
            this.notifyObservers("RulesChanged");
        }
    }

    public boolean isAnonymityPopupsHidden() {
        return this.m_bHideAnonymityPopups;
    }

    public void setHideAnonymityPopups(boolean bl) {
        this.m_bHideAnonymityPopups = bl;
    }

    public boolean isPaymentPopupsHidden() {
        return this.m_bHidePaymentPopups;
    }

    public void setHidePaymentPopups(boolean bl) {
        this.m_bHidePaymentPopups = bl;
    }

    public void setHideInfoServicePopups(boolean bl) {
        this.m_bHideInfoServicePopups = bl;
    }

    public boolean isInfoServicePopupsHidden() {
        return this.m_bHideInfoServicePopups;
    }

    public boolean isAskForAnyNonAnonymousRequest() {
        return this.m_bAskForAnyNonAnonymousRequest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAskForAnyNonAnonymousRequest(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bAskForAnyNonAnonymousRequest != bl) {
                this.m_bAskForAnyNonAnonymousRequest = bl;
                this.setChanged();
            }
            this.notifyObservers("RulesChanged");
        }
    }

    public int getPaymentAnonymousConnectionSetting() {
        return this.m_iPaymentAnonymousConnectionSetting;
    }

    public int getUpdateAnonymousConnectionSetting() {
        return this.m_iUpdateAnonymousConnectionSetting;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setUpdateAnonymousConnectionSetting(int n) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_iUpdateAnonymousConnectionSetting != n) {
                this.m_iUpdateAnonymousConnectionSetting = n;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_ALLOW_UPDATE_DIRECT_CONNECTION);
        }
    }

    public int getInfoServiceAnonymousConnectionSetting() {
        return this.m_iInfoServiceAnonymousConnectionSetting;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInfoServiceAnonymousConnectionSetting(int n) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_iInfoServiceAnonymousConnectionSetting != n) {
                this.m_iInfoServiceAnonymousConnectionSetting = n;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_ALLOW_INFOSERVICE_DIRECT_CONNECTION);
        }
    }

    public void setPaymentAnonymousConnectionSetting(int n) {
        this.m_iPaymentAnonymousConnectionSetting = n;
    }

    public IMutableProxyInterface getInfoServiceProxyInterface() {
        return new IMutableProxyInterface(){

            public IProxyInterfaceGetter getProxyInterface(boolean bl) {
                return JAPModel.getInstance().getProxyInterface(0, bl);
            }
        };
    }

    public IMutableProxyInterface getPaymentProxyInterface() {
        return new IMutableProxyInterface(){

            public IProxyInterfaceGetter getProxyInterface(boolean bl) {
                return JAPModel.getInstance().getProxyInterface(1, bl);
            }
        };
    }

    public IMutableProxyInterface getUpdateProxyInterface() {
        return new IMutableProxyInterface(){

            public IProxyInterfaceGetter getProxyInterface(boolean bl) {
                return JAPModel.getInstance().getProxyInterface(2, bl);
            }
        };
    }

    public ImmutableProxyInterface getTorProxyInterface() {
        return new ProxyInterface("localhost", JAPModel.getHttpListenerPortNumber(), 3, null);
    }

    public static int getHttpListenerPortNumber() {
        return JAPModel.ms_TheModel.m_HttpListenerPortNumber;
    }

    protected void setHttpListenerIsLocal(boolean bl) {
        this.m_bHttpListenerIsLocal = bl;
    }

    public static boolean isHttpListenerLocal() {
        return JAPModel.ms_TheModel.m_bHttpListenerIsLocal;
    }

    public void setSmallDisplay(boolean bl) {
        this.m_bSmallDisplay = bl;
    }

    public static boolean isSmallDisplay() {
        return JAPModel.ms_TheModel.m_bSmallDisplay;
    }

    public boolean isCloseButtonShown() {
        return this.m_bShowCloseButton;
    }

    public void setShowCloseButton(boolean bl) {
        if (this.m_bShowCloseButton != bl) {
            this.m_bShowCloseButton = bl;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_SHOW_CLOSED_BUTTON);
    }

    public boolean isNeverRemindGoodbye() {
        return this.m_bGoodByMessageNeverRemind;
    }

    public void setNeverRemindGoodbye(boolean bl) {
        this.m_bGoodByMessageNeverRemind = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setInfoServiceDisabled(boolean bl) {
        JAPModel jAPModel = this;
        synchronized (jAPModel) {
            if (this.m_bInfoServiceDisabled != bl) {
                this.m_bInfoServiceDisabled = bl;
                this.setChanged();
            }
            this.notifyObservers(CHANGED_INFOSERVICE_AUTO_UPDATE);
        }
    }

    public static boolean isInfoServiceDisabled() {
        return JAPModel.ms_TheModel.m_bInfoServiceDisabled;
    }

    public boolean isMiniViewOnTop() {
        return this.m_bMiniViewOnTop;
    }

    public void setMiniViewOnTop(boolean bl) {
        this.m_bMiniViewOnTop = bl;
    }

    public GUIUtils.IIconResizer getIconResizer() {
        return this.m_resizer;
    }

    public int getFontSize() {
        return this.m_fontSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setFontSize(int n) {
        if (n < 0) {
            n = 0;
        } else if (n > 3) {
            n = 3;
        }
        if (this.m_fontSize != n) {
            JAPModel jAPModel = this;
            synchronized (jAPModel) {
                FontResize fontResize = new FontResize(this.m_fontSize, n);
                if (!JAPDialog.isConsoleOnly()) {
                    GUIUtils.resizeAllFonts(1.0f / (1.0f + 0.1f * (float)fontResize.getOldSize()));
                    GUIUtils.resizeAllFonts(1.0f + 0.1f * (float)fontResize.getNewSize());
                }
                this.m_fontSize = n;
                this.setChanged();
                this.notifyObservers(fontResize);
            }
            return true;
        }
        return false;
    }

    public String toString() {
        boolean bl;
        StringBuffer stringBuffer = new StringBuffer(2048);
        stringBuffer.append("Configuration for JAP Version ");
        stringBuffer.append("00.20.001");
        stringBuffer.append("\n");
        String string = JAPDll.getDllVersion();
        if (string != null) {
            stringBuffer.append("Using JAPDll Version: ");
            stringBuffer.append(string);
            stringBuffer.append("\n");
        }
        if ((string = JAPDll.getDllFileName()) != null) {
            stringBuffer.append("Using JAPDll File: ");
            stringBuffer.append(string);
            stringBuffer.append("\n");
        }
        stringBuffer.append("Config path: ");
        stringBuffer.append(this.getConfigFile());
        stringBuffer.append("\n");
        stringBuffer.append("Help path: ");
        stringBuffer.append(this.getHelpPath());
        stringBuffer.append("\n");
        if (this.m_bDllUpdatePath != null) {
            stringBuffer.append("DLL update path: ");
            stringBuffer.append(this.m_bDllUpdatePath);
            stringBuffer.append("\n");
        }
        stringBuffer.append("Command line arguments: '");
        String[] arrstring = JAPController.getInstance().getCommandlineArgs();
        for (bl = false; bl < arrstring.length; bl += 1) {
            stringBuffer.append(arrstring[bl]);
            if (bl >= arrstring.length - 1) continue;
            stringBuffer.append(" ");
        }
        stringBuffer.append("'\n");
        stringBuffer.append("HttpListenerPortNumber: ");
        stringBuffer.append(this.m_HttpListenerPortNumber);
        stringBuffer.append("\n");
        stringBuffer.append("HttpListenerIsLocal: ");
        stringBuffer.append(this.m_bHttpListenerIsLocal);
        stringBuffer.append("\n");
        stringBuffer.append("UseFirewall: ");
        bl = this.m_proxyInterface != null && this.m_proxyInterface.isValid();
        stringBuffer.append(bl);
        stringBuffer.append("\n");
        if (bl) {
            stringBuffer.append("FirewallType: ");
            stringBuffer.append(this.m_proxyInterface.getProtocol());
            stringBuffer.append("\n");
            stringBuffer.append("FirewallHost: ");
            stringBuffer.append(this.m_proxyInterface.getHost());
            stringBuffer.append("\n");
            stringBuffer.append("FirewallPort: ");
            stringBuffer.append(this.m_proxyInterface.getPort());
            stringBuffer.append("\n");
        }
        stringBuffer.append("AutoConnect: ");
        stringBuffer.append(this.m_bAutoConnect);
        stringBuffer.append("\n");
        stringBuffer.append("AutoReConnect: ");
        stringBuffer.append(this.m_bAutoReConnect);
        stringBuffer.append("\n");
        stringBuffer.append("Launcher: ");
        stringBuffer.append(this.m_strLauncher);
        stringBuffer.append("\n");
        return stringBuffer.toString();
    }

    public static boolean isPreCreateAnonRoutesEnabled() {
        return JAPModel.ms_TheModel.m_bPreCreateAnonRoutes;
    }

    void setPreCreateAnonRoutes(boolean bl) {
        this.m_bPreCreateAnonRoutes = bl;
    }

    public static JAPCertificate getJAPCodeSigningCert() {
        return JAPModel.ms_TheModel.m_certJAPCodeSigning;
    }

    public void setConfigFile(String string) {
        this.m_configFileName = string;
    }

    public void setIconifiedSize(Dimension dimension) {
        this.m_iconifiedSize = dimension;
    }

    public Dimension getIconifiedSize() {
        return this.m_iconifiedSize;
    }

    public void setHelpWindowSize(Dimension dimension) {
        this.m_helpSize = dimension;
    }

    public Dimension getHelpWindowSize() {
        return this.m_helpSize;
    }

    public boolean isHelpWindowSizeSaved() {
        return this.m_bSaveHelpSize;
    }

    public void setSaveHelpWindowSize(boolean bl) {
        this.m_bSaveHelpSize = bl;
    }

    public void setSaveConfigWindowSize(boolean bl) {
        this.m_bSaveConfigSize = bl;
    }

    public boolean isConfigWindowSizeSaved() {
        return this.m_bSaveConfigSize;
    }

    public void setConfigSize(Dimension dimension) {
        this.m_configSize = dimension;
    }

    public Dimension getConfigSize() {
        return this.m_configSize;
    }

    public String getConfigFile() {
        return this.m_configFileName;
    }

    public JAPRoutingSettings getRoutingSettings() {
        return this.m_routingSettings;
    }

    public void setForwardingStateModuleVisible(boolean bl) {
        this.m_forwardingStateModuleVisible = bl;
    }

    public boolean isForwardingStateModuleVisible() {
        return this.m_forwardingStateModuleVisible;
    }

    public static int getTorMaxConnectionsPerRoute() {
        return JAPModel.ms_TheModel.m_TorMaxConnectionsPerRoute;
    }

    protected void setTorMaxConnectionsPerRoute(int n) {
        this.m_TorMaxConnectionsPerRoute = n;
    }

    public static int getTorMaxRouteLen() {
        return JAPModel.ms_TheModel.m_TorMaxRouteLen;
    }

    protected void setTorMaxRouteLen(int n) {
        this.m_TorMaxRouteLen = n;
    }

    public static int getTorMinRouteLen() {
        return JAPModel.ms_TheModel.m_TorMinRouteLen;
    }

    protected void setTorMinRouteLen(int n) {
        this.m_TorMinRouteLen = n;
    }

    public static boolean isTorNoneDefaultDirServerEnabled() {
        return JAPModel.ms_TheModel.m_bTorUseNoneDefaultDirServer;
    }

    protected void setTorUseNoneDefaultDirServer(boolean bl) {
        this.m_bTorUseNoneDefaultDirServer = bl;
    }

    protected void setMixminionRouteLen(int n) {
        this.m_mixminionRouteLen = n;
    }

    public static int getMixminionRouteLen() {
        return JAPModel.ms_TheModel.m_mixminionRouteLen;
    }

    protected void setMixminionMyEMail(String string) {
        this.m_mixminionMyEMail = string;
    }

    public static String getMixminionMyEMail() {
        return JAPModel.ms_TheModel.m_mixminionMyEMail;
    }

    protected void setMixMinionPassword(String string) {
        this.m_mixminionPassword = string;
    }

    public static String getMixMinionPassword() {
        return JAPModel.ms_TheModel.m_mixminionPassword;
    }

    protected void setMixinionPasswordHash(byte[] arrby) {
        this.m_mixminionPasswordHash = arrby;
    }

    public static byte[] getMixMinionPasswordHash() {
        return JAPModel.ms_TheModel.m_mixminionPasswordHash;
    }

    protected void resetMixMinionKeyringandPw() {
        this.m_mixminionPasswordHash = null;
        this.m_mixminionPassword = null;
        this.m_mixminionKeyring = "";
    }

    protected void setMixminionMessages(Vector vector) {
        this.m_mixminionMessages = vector;
    }

    public static Vector getMixminionMessages() {
        return JAPModel.ms_TheModel.m_mixminionMessages;
    }

    protected void setMixminionKeyring(String string) {
        this.m_mixminionKeyring = string;
    }

    public static String getMixminionKeyring() {
        return JAPModel.ms_TheModel.m_mixminionKeyring;
    }

    protected void setMixminionMMRList(MMRList mMRList) {
        this.m_mixminionRouters = mMRList;
    }

    public static MMRList getMixminionMMRlist() {
        return JAPModel.ms_TheModel.m_mixminionRouters;
    }

    protected void setMixminionFragments(Vector vector) {
        this.m_mixminionFragments = vector;
    }

    public static Vector getMixminionFragments() {
        return JAPModel.ms_TheModel.m_mixminionFragments;
    }

    protected void setUseProxyAuthentication(boolean bl) {
        this.m_bUseProxyAuthentication = bl;
    }

    public boolean isProxyAuthenticationUsed() {
        return this.m_bUseProxyAuthentication;
    }

    public void setPaymentPassword(String string) {
        this.m_paymentPassword = string;
    }

    public String getPaymentPassword() {
        return this.m_paymentPassword;
    }

    public synchronized String getHelpPath() {
        return this.m_helpPath != null || this.m_bPortableHelp ? this.m_helpPath : AbstractOS.getInstance().getDefaultHelpPath("JonDo");
    }

    public synchronized URL getHelpURL(String string) {
        URL uRL = null;
        if (string != null && this.isHelpPathDefined() && this.m_helpFileStorageManager.ensureMostRecentVersion(this.m_helpPath)) {
            try {
                if (new File(this.m_helpPath + File.separator + this.m_helpFileStorageManager.getLocalisedHelpDir() + File.separator + string).exists()) {
                    uRL = new URL("file://" + this.m_helpPath + "/" + this.m_helpFileStorageManager.getLocalisedHelpDir() + "/" + string);
                }
            }
            catch (SecurityException securityException) {
                LogHolder.log(4, LogType.MISC, securityException);
            }
            catch (MalformedURLException malformedURLException) {
                LogHolder.log(4, LogType.MISC, malformedURLException);
            }
        }
        return uRL;
    }

    public URL getHelpURL() {
        return this.getHelpURL("index.html");
    }

    synchronized void initHelpPath(String string) {
        String string2;
        if (this.m_bPortableHelp) {
            return;
        }
        String string3 = AbstractOS.getInstance().getenv("ALLUSERSPROFILE");
        if (string3 != null && string != null && string.startsWith(string3)) {
            if (string.indexOf("JonDo") >= 0) {
                RecursiveFileTool.deleteRecursion(new File(string));
            }
            string = null;
        }
        this.m_helpPath = (string2 = this.helpPathValidityCheck(string)).equals("HELP_IS_VALID") || string2.equals("helpJonDoExists") || string2.equals(NO_HELP_STORAGE_MANAGER) ? string : this.m_helpFileStorageManager.getInitPath();
    }

    public synchronized void setHelpPath(File file) {
        this.setHelpPath(file, false);
    }

    public synchronized void setHelpPath(File file, boolean bl) {
        if (this.m_bPortableHelp && !bl) {
            return;
        }
        if (file == null) {
            this.resetHelpPath();
        } else {
            file = new File(file.getAbsolutePath());
            if (bl) {
                this.m_bPortableHelp = true;
                if (file.isFile()) {
                    String string;
                    int n = file.getPath().toUpperCase().indexOf(("help" + File.pathSeparator + "de" + File.pathSeparator + "help").toUpperCase());
                    file = n >= 0 || (n = file.getPath().toUpperCase().indexOf(("help" + File.pathSeparator + "en" + File.pathSeparator + "help").toUpperCase())) >= 0 ? (n > 0 ? new File(file.getPath().substring(0, n)) : null) : ((string = file.getParent()) != null ? new File(string) : null);
                }
                if (file != null && file.isDirectory()) {
                    String string = this.m_helpFileStorageManager.helpPathValidityCheck(file.getPath(), true);
                    if (string.equals("HELP_IS_VALID") || string.equals("helpJonDoExists")) {
                        if (this.m_helpFileStorageManager.handleHelpPathChanged(this.m_helpPath, file.getPath(), true)) {
                            if (this.m_helpPath == null || !this.m_helpPath.equals(file.getPath())) {
                                this.m_helpPath = file.getPath();
                                this.setChanged();
                            }
                        } else {
                            this.resetHelpPath();
                            LogHolder.log(4, LogType.GUI, "Help path resetted because we could not change it.");
                        }
                    } else {
                        this.resetHelpPath();
                        LogHolder.log(4, LogType.GUI, "Help path resetted because it was invalid.");
                    }
                } else {
                    this.resetHelpPath();
                    LogHolder.log(4, LogType.GUI, "Help path resetted because it was no directory.");
                }
            } else {
                File file2;
                if (file.getPath().toUpperCase().endsWith("help".toUpperCase()) && file.getParent() != null && (file2 = new File(file.getParent())).isDirectory()) {
                    file = file2;
                }
                this.setHelpPath(file.getPath());
            }
        }
        this.notifyObservers(CHANGED_HELP_PATH);
    }

    private synchronized void setHelpPath(String string) {
        if (string == null) {
            this.resetHelpPath();
            return;
        }
        if (string.equals("")) {
            this.resetHelpPath();
            return;
        }
        String string2 = this.helpPathValidityCheck(string);
        if (string2.equals("HELP_IS_VALID") || string2.equals("helpJonDoExists")) {
            boolean bl;
            String string3 = this.m_helpPath;
            boolean bl2 = this.isHelpPathDefined() ? !this.m_helpPath.equals(string) : (bl = true);
            if (bl) {
                boolean bl3 = true;
                bl3 = this.m_helpFileStorageManager.handleHelpPathChanged(string3, string, false);
                if (bl3) {
                    this.m_helpPath = string;
                    this.setChanged();
                }
            }
        }
    }

    public boolean extractHelpFiles(String string) {
        return this.m_helpFileStorageManager.extractHelpFiles(string);
    }

    protected synchronized void resetHelpPath() {
        String string = this.m_helpPath;
        if (string != null && !this.m_bPortableHelp) {
            this.m_helpFileStorageManager.handleHelpPathChanged(string, null, false);
            this.setChanged();
            this.m_helpPath = null;
        }
    }

    public synchronized String helpPathValidityCheck(String string) {
        return this.m_helpFileStorageManager.helpPathValidityCheck(string, false);
    }

    public synchronized String helpPathValidityCheck(File file) {
        if (file == null) {
            return JAPMessages.getString("invalidHelpPathNull");
        }
        return this.helpPathValidityCheck(file.getPath());
    }

    public boolean isHelpPathChangeable() {
        if (this.m_helpFileStorageManager instanceof LocalHelpFileStorageManager) {
            return false;
        }
        return !this.m_bPortableHelp;
    }

    public synchronized boolean isHelpPathDefined() {
        boolean bl;
        boolean bl2 = this.m_helpPath != null;
        String string = null;
        boolean bl3 = this.m_helpFileStorageManager.helpInstallationExists(this.m_helpPath);
        boolean bl4 = bl = bl3 && (string = this.helpPathValidityCheck(this.m_helpPath)).equals("helpJonDoExists");
        if (bl2 && !bl) {
            LogHolder.log(4, LogType.MISC, "Help path " + this.m_helpPath + " configured but no valid help could be found! Exists: " + bl3 + " Valid: " + string);
            this.m_helpPath = null;
            this.setChanged();
        }
        if (!this.m_bPortableHelp && this.m_helpPath == null && this.m_helpFileStorageManager.helpInstallationExists(AbstractOS.getInstance().getDefaultHelpPath("JonDo")) && this.helpPathValidityCheck(AbstractOS.getInstance().getDefaultHelpPath("JonDo")).equals("helpJonDoExists")) {
            this.m_helpPath = AbstractOS.getInstance().getDefaultHelpPath("JonDo");
            bl = true;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_HELP_PATH);
        return bl;
    }

    public Observable getHelpFileStorageObservable() {
        return this.m_helpFileStorageManager.getStorageObservable();
    }

    public synchronized void setDLLupdate(String string) {
        if (!(string == null || this.m_bDllUpdatePath != null && this.m_bDllUpdatePath.equals(string))) {
            File file = new File(string);
            if (file.exists() && file.isDirectory()) {
                this.m_bDllUpdatePath = file.getAbsolutePath();
                this.setChanged();
            }
        } else if (string == null && this.m_bDllUpdatePath != null) {
            this.m_bDllUpdatePath = null;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_DLL_UPDATE);
    }

    public synchronized void setMacOSXLibraryUpdateAtStartupNeeded(boolean bl) {
        if (this.m_bMacOSXLibraryUpdateAtStartupNeeded != bl) {
            this.m_bMacOSXLibraryUpdateAtStartupNeeded = bl;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_MACOSX_LIBRARY_UPDATE);
    }

    public synchronized void setShowConfigAssistantAutomatically(boolean bl) {
        if (this.m_bShowConfigAssistant != bl) {
            this.m_bShowConfigAssistant = bl;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_CONFIG_ASSISTANT_SHOWUP);
    }

    public boolean isConfigAssistantAutomaticallyShown() {
        return this.m_bShowConfigAssistant;
    }

    public synchronized void setAnonymizedHttpHeaders(boolean bl) {
        if (this.m_bAnonymizedHttpHeaders != bl) {
            this.m_bAnonymizedHttpHeaders = bl;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_ANONYMIZED_HTTP_HEADERS);
    }

    public boolean isAnonymizedHttpHeaders() {
        return this.m_bAnonymizedHttpHeaders;
    }

    public boolean isHeaderProcessingDisabled() {
        return this.m_bDisableAllHeaderProcessing;
    }

    public void setDiableAllHeaderProcessing(boolean bl) {
        this.m_bDisableAllHeaderProcessing = bl;
    }

    public boolean isMacOSXLibraryUpdateAtStartupNeeded() {
        return this.m_bMacOSXLibraryUpdateAtStartupNeeded;
    }

    public String getDllUpdatePath() {
        return this.m_bDllUpdatePath;
    }

    public synchronized void setDllWarning(boolean bl) {
        String string = "00.04.009";
        long l = this.m_noWarningForDllVersionBelow;
        if (bl) {
            l = 0L;
        } else if (string != null) {
            l = Util.convertVersionStringToNumber(string);
        }
        if (this.m_noWarningForDllVersionBelow != l) {
            this.m_noWarningForDllVersionBelow = l;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_DLL_UPDATE);
    }

    protected synchronized void setDllWarningVersion(long l) {
        if (this.m_noWarningForDllVersionBelow != l) {
            this.m_noWarningForDllVersionBelow = l;
            this.setChanged();
        }
        this.notifyObservers(CHANGED_DLL_UPDATE);
    }

    protected long getDLLWarningVersion() {
        return this.m_noWarningForDllVersionBelow;
    }

    public boolean isDLLWarningActive() {
        long l = Util.convertVersionStringToNumber("00.04.009");
        return this.m_noWarningForDllVersionBelow != l;
    }

    public void setShowSplashScreen(boolean bl) {
        this.m_bShowSplashScreen = bl;
    }

    public boolean getShowSplashScreen() {
        return this.m_bShowSplashScreen;
    }

    public void setShowSplashDisabled(boolean bl) {
        this.m_bShowSplashDisabled = bl;
    }

    public boolean getShowSplashDisabled() {
        return this.m_bShowSplashDisabled;
    }

    public void setStartPortableFirefox(boolean bl) {
        this.m_bStartPortableFirefox = bl;
    }

    public boolean getStartPortableFirefox() {
        return this.m_bStartPortableFirefox;
    }

    public boolean isShuttingDown() {
        return JAPController.getInstance().isShuttingDown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ImmutableProxyInterface getAnonymityProxy() {
        Object object = this.SYNC_ANON_PROXY;
        synchronized (object) {
            InetAddress inetAddress;
            if ((this.m_proxyAnon == null || this.m_proxyAnon.getPort() != JAPModel.getHttpListenerPortNumber()) && (inetAddress = JAPController.getInstance().getListenerInetAddress()) != null) {
                String string = inetAddress.getHostAddress();
                if (inetAddress.getHostAddress().equals("0.0.0.0")) {
                    try {
                        string = InetAddress.getLocalHost().getHostAddress();
                    }
                    catch (UnknownHostException unknownHostException) {
                        string = "127.0.0.1";
                    }
                }
                this.m_proxyAnon = new ProxyInterface(string, JAPModel.getHttpListenerPortNumber(), null);
            }
        }
        return this.m_proxyAnon;
    }

    private IProxyInterfaceGetter getProxyInterface(int n, boolean bl) {
        if (this.isShuttingDown()) {
            return null;
        }
        IProxyInterfaceGetter iProxyInterfaceGetter = new IProxyInterfaceGetter(){

            public ImmutableProxyInterface getProxyInterface() {
                return JAPModel.getInstance().getProxyInterface();
            }
        };
        IProxyInterfaceGetter iProxyInterfaceGetter2 = new IProxyInterfaceGetter(){

            public ImmutableProxyInterface getProxyInterface() {
                if (JAPController.getInstance().isAnonConnected()) {
                    return JAPModel.this.getAnonymityProxy();
                }
                return null;
            }
        };
        if (1 == n && this.m_iPaymentAnonymousConnectionSetting == 1 || 0 == n && this.m_iInfoServiceAnonymousConnectionSetting == 1 || 2 == n && this.m_iUpdateAnonymousConnectionSetting == 1) {
            if (bl) {
                return iProxyInterfaceGetter2;
            }
            return null;
        }
        if (!this.m_connectionChecker.checkAnonConnected()) {
            if (bl) {
                return null;
            }
            return iProxyInterfaceGetter;
        }
        if (bl) {
            if (1 == n && 2 == this.m_iPaymentAnonymousConnectionSetting || 0 == n && 2 == this.m_iInfoServiceAnonymousConnectionSetting || 2 == n && 2 == this.m_iUpdateAnonymousConnectionSetting) {
                return null;
            }
            return iProxyInterfaceGetter2;
        }
        return iProxyInterfaceGetter;
    }

    public BigInteger getDialogVersion() {
        return this.m_iDialogVersion;
    }

    public void setDialogVersion(BigInteger bigInteger) {
        this.m_iDialogVersion = bigInteger;
    }

    public Hashtable getAcceptedTCs() {
        return this.m_acceptedTCs;
    }

    public String getContext() {
        return this.m_context;
    }

    public String getProgramName() {
        return this.m_strDistributorMode;
    }

    public void setProgramName(String string) {
        if (string != null && (string.equals("JAP") || string.equals("JonDo"))) {
            this.m_strDistributorMode = string;
        }
    }

    public synchronized void setContext(String string) {
        TrustModel.updateContext(string);
    }

    void setLauncher(String string) {
        this.m_strLauncher = string;
    }

    String getLauncher() {
        return this.m_strLauncher;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public static class FontResize {
        private int m_oldSize;
        private int m_newSize;

        public FontResize(int n, int n2) {
            this.m_oldSize = n;
            this.m_newSize = n2;
        }

        public int getOldSize() {
            return this.m_oldSize;
        }

        public int getNewSize() {
            return this.m_newSize;
        }
    }
}

