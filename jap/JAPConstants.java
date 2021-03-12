/*
 * Decompiled with CFR 0.150.
 */
package jap;

public final class JAPConstants {
    public static final String aktVersion = "00.20.001";
    public static final boolean m_bUnstableVersion = true;
    private static final String CVS_GENERATED_RELEASE_DATE = "$Date: 2016-11-14 15:51:52 +0100 (Mo, 14 Nov 2016) $";
    public static final boolean m_bReleasedVersion = true;
    private static final String RELEASE_DATE = "2016/12/18 20:00:01";
    public static final String CURRENT_CONFIG_VERSION = "00.20.001";
    public static final String strReleaseDate = "2016/12/18 20:00:01";
    public static final String PROGRAM_NAME_JAP = "JAP";
    public static final String PROGRAM_NAME_JAP_JONDO = "JAP/JonDo";
    public static final String PROGRAM_NAME_JONDO = "JonDo";
    public static final boolean DEBUG = false;
    public static final String APPLICATION_CONFIG_DIR_NAME = "JonDo";
    public static final int DEFAULT_PORT_NUMBER = 4001;
    public static final boolean DEFAULT_LISTENER_IS_LOCAL = true;
    static final int[] DEFAULT_ANON_PORT_NUMBERS = new int[]{6544};
    public static final String[] DEFAULT_INFOSERVICE_NAMES = new String[]{"880D9306B90EC8309178376B43AC26652CE52B74", "B1B2085A914FF9B838BD225F9D293C45E50812B4", "AE116ECB775FF127C02DF96F5466AECAF86B93A9", "C2E7C64A7D245A4879737699A273F24369E53567", "9CB78C5B9CF94DE8A7E77011E258B8C9E8D7F18D"};
    public static final String[] DEFAULT_INFOSERVICE_HOSTNAMES = new String[]{"infoservice.inf.tu-dresden.de", "is.beneficium.de", "72.55.137.241", "is1.anonmix.eu", "is2.anonmix.eu"};
    public static final int[][] DEFAULT_INFOSERVICE_PORT_NUMBERS = new int[][]{{80, 6543}, {80, 443}, {80, 443}, {80, 443}, {80, 443}, {80, 443}};
    public static final boolean DEFAULT_INFOSERVICE_DISABLED = false;
    public static final int DEFAULT_INFOSERVICE_TIMEOUT = 30;
    public static final boolean REMIND_OPTIONAL_UPDATE = true;
    public static final boolean REMIND_JAVA_UPDATE = true;
    public static final boolean DEFAULT_ANONYMIZED_HTTP_HEADERS = false;
    public static final boolean DEFAULT_DISABLE_ALL_HEADER_PROCESSING = false;
    public static final boolean DEFAULT_ANON_DEBUG_MODE = false;
    public static final boolean DEFAULT_SHOW_CONFIG_ASSISTANT = true;
    static final int SMALL_FONT_SIZE = 9;
    static final int SMALL_FONT_STYLE = 0;
    public static final int VIEW_NORMAL = 1;
    public static final int VIEW_SIMPLIFIED = 2;
    public static final int DEFAULT_VIEW = 2;
    static final boolean DEFAULT_SAVE_MAIN_WINDOW_POSITION = true;
    static final boolean DEFAULT_SAVE_MINI_WINDOW_POSITION = true;
    static final boolean DEFAULT_SAVE_CONFIG_WINDOW_POSITION = false;
    static final boolean DEFAULT_SAVE_HELP_WINDOW_POSITION = false;
    static final boolean DEFAULT_SAVE_HELP_WINDOW_SIZE = false;
    static final boolean DEFAULT_SAVE_CONFIG_WINDOW_SIZE = false;
    static final boolean DEFAULT_MOVE_TO_SYSTRAY_ON_STARTUP = false;
    static final boolean DEFAULT_MINIMIZE_ON_STARTUP = false;
    static final boolean DEFAULT_WARN_ON_CLOSE = true;
    static final long DEFAULT_INTERFACE_BLOCK_TIMEOUT = 180000L;
    static final String JAPLocalFilename = "JAP.jar";
    public static final String XMLCONFFN = "jap.conf";
    public static final String MESSAGESFN = "JAPMessages";
    public static final String BUSYFN = "busy.gif";
    static final String ABOUTFN = "info.gif";
    public static final String DOWNLOADFN = "install.gif";
    static final String IICON16FN = "icon16.gif";
    static final String ICON_JONDO = "JonDo.ico.gif";
    static final String ENLARGEYICONFN = "enlarge.gif";
    static final String METERICONFN = "icom.gif";
    public static final String IMAGE_ARROW = "arrow46.gif";
    public static final String IMAGE_BLANK = "blank.gif";
    public static final String IMAGE_STEPFINISHED = "haken.gif";
    public static final String IMAGE_ARROW_DOWN = "arrowDown.gif";
    public static final String IMAGE_ARROW_UP = "arrowUp.gif";
    public static final String IMAGE_SERVER = "server.gif";
    public static final String IMAGE_SERVER_BLAU = "server_blau.gif";
    public static final String IMAGE_SERVER_ROT = "server_rot.gif";
    public static final String IMAGE_RELOAD = "reload.gif";
    public static final String IMAGE_RELOAD_DISABLED = "reloaddisabled_anim.gif";
    public static final String IMAGE_RELOAD_ROLLOVER = "reloadrollover.gif";
    public static final String IMAGE_WARNING = "warning.gif";
    public static final String IMAGE_INFORMATION = "information.gif";
    public static final String IMAGE_ERROR = "error.gif";
    public static final String IMAGE_CASCADE_MANUAL_NOT_TRUSTED = "cdisabled.gif";
    public static final String IMAGE_CASCADE_MANUELL = "servermanuell.gif";
    public static final String IMAGE_CASCADE_INTERNET_NOT_TRUSTED = "cdisabled.gif";
    public static final String IMAGE_CASCADE_PAYMENT = "serverwithpayment.gif";
    public static final String IMAGE_CASCADE_PAYMENT_NOT_TRUSTED = "cdisabled.gif";
    public static final String IMAGE_CASCADE_INTERNET = "serverfrominternet.gif";
    public static final String IMAGE_INFOSERVICE_MANUELL = "infoservicemanuell.gif";
    public static final String IMAGE_INFOSERVICE_INTERNET = "infoservicefrominternet.gif";
    public static final String IMAGE_INFOSERVICE_BIGLOGO = "infoservicebiglogo.gif";
    public static final String IMAGE_SAVE = "saveicon.gif";
    public static final String IMAGE_EXIT = "exiticon.gif";
    public static final String IMAGE_DELETE = "deleteicon.gif";
    public static final String IMAGE_COPY = "copyicon.gif";
    public static final String IMAGE_COPY_CONFIG = "copyintoicon.gif";
    public static final String CERTENABLEDICON = "cenabled.gif";
    public static final String CERTDISABLEDICON = "cdisabled.gif";
    public static final String IMAGE_COINS_FULL = "coins-full.gif";
    public static final String IMAGE_COINS_QUITEFULL = "coins-quitefull.gif";
    public static final String IMAGE_COINS_MEDIUM = "coins-medium.gif";
    public static final String IMAGE_COINS_LOW = "coins-low.gif";
    public static final String IMAGE_COINS_EMPTY = "coins-empty.gif";
    public static final String IMAGE_COIN_COINSTACK = "coinstack.gif";
    public static final String IMAGE_PAYPAL = "paypal_logo.png";
    public static final String IMAGE_PAYSAFECARD = "psc_logo.png";
    public static final String IMAGE_BITCOIN = "bitcoin_logo.png";
    public static final String IMAGE_CASHU = "cashu_logo.gif";
    public static final String IMAGE_MONEYBOOKERS = "moneybookers_logo.gif";
    public static final String IMAGE_EGOLD = "e-gold_logo.png";
    public static final String[] ACCOUNTICONFNARRAY = new String[]{"accountDisabled.gif", "accountOk.gif", "accountBroken.gif"};
    public static final String[] BROWSERLIST = new String[]{"firefox", "iexplore", "explorer", "mozilla", "konqueror", "mozilla-firefox", "firebird", "opera"};
    public static final String[] PI_CERTS = new String[]{"bi.cer.dev", "Payment_Instance.cer"};
    public static final String CERTSPATH = "certificates/";
    public static final String INFOSERVICE_ROOT_CERTSPATH = "acceptedInfoServiceCAs/";
    public static final String INFOSERVICE_CERTSPATH = "acceptedInfoServices/";
    public static final String PAYMENT_ROOT_CERTSPATH = "acceptedPaymentCAs/";
    public static final String[] PAYMENT_ROOT_CERTS = new String[0];
    public static final String PAYMENT_DEFAULT_CERTSPATH = "acceptedPIs/";
    public static final String MIX_CERTSPATH = "acceptedMixCAs/";
    public static final String OPERATOR_CERTSPATH = "acceptedMixOperators/";
    public static final String[] MIX_ROOT_CERTS = new String[]{"japmixroot.cer", "Operator_CA.cer", "Test_CA.cer.dev", "gpf_jondonym_ca.cer"};
    public static final String[] INFOSERVICE_CERTS = new String[]{"yap-infoservice-1.cer"};
    public static final String[] INFOSERVICE_ROOT_CERTS = new String[]{"japinfoserviceroot.cer", "InfoService_CA.cer"};
    public static final String TERMS_CERTSPATH = "acceptedTaCTemplates/";
    public static final String[] TERMS_CERTS = new String[]{"Terms_and_Conditions.b64.cer"};
    public static final String CERT_JAPCODESIGNING = "japcodesigning.cer";
    public static final String CERT_JAPINFOSERVICEMESSAGES = "japupdatemessages.cer";
    public static final boolean DEFAULT_CERT_CHECK_ENABLED = true;
    public static final int TOR_MAX_CONNECTIONS_PER_ROUTE = 1000;
    public static final int TOR_MAX_ROUTE_LEN = 5;
    public static final int TOR_MIN_ROUTE_LEN = 2;
    public static final int MIXMINION_MAX_ROUTE_LEN = 10;
    public static final int MIXMINION_MIN_ROUTE_LEN = 2;
    public static final boolean DEFAULT_TOR_PRECREATE_ROUTES = false;
    public static final int DEFAULT_TOR_MIN_ROUTE_LEN = 2;
    public static final int DEFAULT_TOR_MAX_ROUTE_LEN = 3;
    public static final int DEFAULT_TOR_MAX_CONNECTIONS_PER_ROUTE = 1000;
    public static final boolean DEFAULT_TOR_USE_NONE_DEFAULT_DIR_SERVER = false;
    public static final int DEFAULT_MIXMINION_ROUTE_LEN = 2;
    public static final String DEFAULT_MIXMINION_EMAIL = "";
    public static final int ROUTING_BANDWIDTH_PER_USER = 4000;
    public static final String MAIL_SYSTEM_ADDRESS = "japmailsystem@infoservice.inf.tu-dresden.de";
    public static final String CONFIG_VERSION = "version";
    public static final String CONFIG_PORT_NUMBER = "portNumber";
    public static final String CONFIG_LISTENER_IS_LOCAL = "listenerIsLocal";
    public static final String CONFIG_NEVER_REMIND_ACTIVE_CONTENT = "neverRemindActiveContent";
    public static final String CONFIG_NEVER_EXPLAIN_FORWARD = "neverExplainForward";
    public static final String CONFIG_DO_NOT_ABUSE_REMINDER = "doNotAbuseReminder";
    public static final String CONFIG_NEVER_REMIND_GOODBYE = "neverRemindGoodBye";
    public static final String CONFIG_INFOSERVICE_DISABLED = "infoServiceDisabled";
    public static final String CONFIG_INFOSERVICE_TIMEOUT = "infoserviceTimeout";
    public static final String CONFIG_PROXY_HOST_NAME = "proxyHostName";
    public static final String CONFIG_PROXY_PORT_NUMBER = "proxyPortNumber";
    public static final String CONFIG_PROXY_TYPE = "proxyType";
    public static final String CONFIG_PROXY_AUTH_USER_ID = "proxyAuthUserID";
    public static final String CONFIG_PROXY_AUTH_PASSWORD = "proxyAuthPassword";
    public static final String CONFIG_PROXY_AUTHORIZATION = "proxyAuthorization";
    public static final String CONFIG_PROXY_MODE = "proxyMode";
    public static final String CONFIG_DUMMY_TRAFFIC_INTERVALL = "DummytrafficInterval";
    public static final String CONFIG_INTERFACE_BLOCK_TIMEOUT = "InterfaceBlockTimeout";
    public static final String CONFIG_AUTO_CONNECT = "autoconnect";
    public static final String CONFIG_AUTO_RECONNECT = "autoReconnect";
    public static final String CONFIG_MINIMIZED_STARTUP = "minimizedStartup";
    public static final String CONFIG_LOCALE = "Locale";
    public static final String CONFIG_LOOK_AND_FEEL = "LookAndFeel";
    public static final String CONFIG_UNKNOWN = "unknown";
    public static final String CONFIG_GUI = "GUI";
    public static final String CONFIG_LOG_DETAIL = "Detail";
    public static final String CONFIG_MAIN_WINDOW = "MainWindow";
    public static final String CONFIG_LOCATION = "Location";
    public static final String CONFIG_X = "x";
    public static final String CONFIG_Y = "y";
    public static final String CONFIG_DX = "dx";
    public static final String CONFIG_DY = "dy";
    public static final String CONFIG_MOVE_TO_SYSTRAY = "MoveToSystray";
    public static final String CONFIG_DEFAULT_VIEW = "DefaultView";
    public static final String CONFIG_START_PORTABLE_FIREFOX = "StartPortableFirefox";
    public static final String CONFIG_NORMAL = "Normal";
    public static final String CONFIG_SIMPLIFIED = "Simplified";
    public static final String CONFIG_DEBUG = "Debug";
    public static final String CONFIG_LEVEL = "Level";
    public static final String CONFIG_TYPE = "Type";
    public static final String CONFIG_OUTPUT = "Output";
    public static final String CONFIG_CONSOLE = "Console";
    public static final String CONFIG_WINDOW = "showWindow";
    public static final String CONFIG_FILE = "File";
    public static final String CONFIG_TOR = "TORSettings";
    public static final String CONFIG_TOR_DIR_SERVER = "DirectoryServer";
    public static final String CONFIG_XML_ATTR_TOR_NONE_DEFAULT_DIR_SERVER = "useNoneDefault";
    public static final String CONFIG_Mixminion = "MixMinion";
    public static final String CONFIG_MAX_CONNECTIONS_PER_ROUTE = "MaxConnectionsPerRoute";
    public static final String CONFIG_TOR_PRECREATE_ANON_ROUTES = "PreCreateAnonRoutes";
    public static final String CONFIG_ROUTE_LEN = "RouteLen";
    public static final String CONFIG_MIXMINION_REPLY_MAIL = "MixminionREPLYMail";
    public static final String CONFIG_MIXMINION_PASSWORD_HASH = "MixminionPasswordHash";
    public static final String CONFIG_MIXMINION_KEYRING = "MixminionKeyring";
    public static final String CONFIG_MIN = "min";
    public static final String CONFIG_MAX = "max";
    public static final String CONFIG_PAYMENT = "Payment";
    public static final String CONFIG_ENCRYPTED_DATA = "EncryptedData";
    public static final String CONFIG_JAP_FORWARDING_SETTINGS = "JapForwardingSettings";
    public static final String CONFIG_ACCEPTED_TERMS_AND_CONDITIONS = "AcceptedTermsAndConditions";
    public static final String PAYMENT_NONGENERIC = "CreditCard";
    public static final long TIME_RESTART_AFTER_SOCKET_ERROR = 60000L;
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"en", "de", "cs", "nl", "fr", "pl", "es", "fa"};
    public static final String IN_ADDR_ANY_IPV4 = "0.0.0.0";
    public static final String IN_ADDR_LOOPBACK_IPV4 = "127.0.0.1";
    public static final String IN_ADDR_LOOPBACK_IPV6 = "::1";

    public static String[] getSupportedLanguages() {
        String[] arrstring = new String[SUPPORTED_LANGUAGES.length];
        for (int i = 0; i < arrstring.length; ++i) {
            arrstring[i] = SUPPORTED_LANGUAGES[i];
        }
        return arrstring;
    }
}
