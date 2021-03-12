/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.ImmutableProxyInterface;
import anon.proxy.DirectProxyConnection;
import anon.proxy.DirectProxyResponse;
import anon.util.BooleanVariable;
import anon.util.JAPMessages;
import anon.util.SocketGuard;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public final class DirectProxy {
    private static final String GENERAL_RULE = "*";
    private static final int BUFFER_PUSHBACK = 2000;
    private static final String JONDOPROXY = "JonDoProxy";
    private static final String JONDOPROXY_ARG = "JonDoProxy".toLowerCase() + "=";
    private static final long TEMPORARY_REMEMBER_TIME_SECONDS = 5000L;
    private static final long TEMPORARY_REMEMBER_TIME = 10000L;
    private static final long TEMPORARY_REMEMBER_TIME_NO_WARNING = 10000L;
    private AllowProxyConnectionCallback m_callback;
    private IMutableProxyInterface m_proxyInterface = new IMutableProxyInterface.DummyMutableProxyInterface();
    private ServerSocket m_socketListener;
    private ServerSocket m_socketListenerTwo;
    private final Object THREAD_SYNC = new Object();
    private boolean m_bInterrupted = false;
    private volatile Thread threadRunLoop;
    private BooleanVariable m_bIsRunningOne = new BooleanVariable(false);
    private volatile Thread threadRunLoopTwo;
    private BooleanVariable m_bIsRunningTwo = new BooleanVariable(false);
    private final Hashtable rememberedDomains = new Hashtable();
    private final Object SYNC_CALLBACK = new Object();
    private final Vector m_vecThreads = new Vector();
    static /* synthetic */ Class class$anon$proxy$DirectProxy;

    public DirectProxy(ServerSocket serverSocket, ServerSocket serverSocket2, IMutableProxyInterface iMutableProxyInterface, AllowProxyConnectionCallback allowProxyConnectionCallback) {
        this.m_socketListener = serverSocket;
        this.m_socketListenerTwo = serverSocket2;
        if (iMutableProxyInterface != null) {
            this.m_proxyInterface = iMutableProxyInterface;
        }
        this.setAllowUnprotectedConnectionCallback(allowProxyConnectionCallback);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void setAllowUnprotectedConnectionCallback(AllowProxyConnectionCallback allowProxyConnectionCallback) {
        Hashtable hashtable = this.rememberedDomains;
        synchronized (hashtable) {
            if (this.m_callback != allowProxyConnectionCallback) {
                this.m_callback = allowProxyConnectionCallback;
                this.rememberedDomains.clear();
                if (this.m_callback != null) {
                    this.m_callback.setRulesChanged(false);
                    this.rememberedDomains.put(GENERAL_RULE, new RememberedRequestRight(GENERAL_RULE, true, System.currentTimeMillis() + 5000L));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isConnected() {
        Object object = this.THREAD_SYNC;
        synchronized (object) {
            return this.threadRunLoop != null;
        }
    }

    public ServerSocket getSocketListener() {
        return this.m_socketListener;
    }

    public ServerSocket getSocketListenerTwo() {
        return this.m_socketListenerTwo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        AllowProxyConnectionCallback allowProxyConnectionCallback = this.m_callback;
        if (allowProxyConnectionCallback != null) {
            allowProxyConnectionCallback.setRulesChanged(false);
        }
        Hashtable hashtable = this.rememberedDomains;
        synchronized (hashtable) {
            this.rememberedDomains.clear();
            this.rememberedDomains.put(GENERAL_RULE, new RememberedRequestRight(GENERAL_RULE, true, System.currentTimeMillis() + 5000L, false));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized boolean start() {
        if (this.m_socketListener == null) {
            return false;
        }
        Object object = this.THREAD_SYNC;
        synchronized (object) {
            if (this.threadRunLoop != null && this.threadRunLoop.isAlive()) {
                return true;
            }
            this.stop();
            this.reset();
            this.m_bInterrupted = false;
            this.m_bIsRunningOne.set(true);
            this.threadRunLoop = new Thread((Runnable)new DirectProxyRunnable(this.m_socketListener, this.m_bIsRunningOne), "JAP - Direct Proxy");
            this.threadRunLoop.setDaemon(true);
            this.threadRunLoop.start();
            if (this.m_socketListenerTwo != null) {
                this.m_bIsRunningTwo.set(true);
                this.threadRunLoopTwo = new Thread((Runnable)new DirectProxyRunnable(this.m_socketListenerTwo, this.m_bIsRunningTwo), "JAP - Direct Proxy");
                this.threadRunLoopTwo.setDaemon(true);
                this.threadRunLoopTwo.start();
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RememberedRequestRight getRequestRight(RequestInfo requestInfo) {
        if (requestInfo == null) {
            return null;
        }
        Hashtable hashtable = this.rememberedDomains;
        synchronized (hashtable) {
            RememberedRequestRight rememberedRequestRight = (RememberedRequestRight)this.rememberedDomains.get(requestInfo.getHost());
            if (rememberedRequestRight == null) {
                rememberedRequestRight = (RememberedRequestRight)this.rememberedDomains.get(GENERAL_RULE);
            }
            if (rememberedRequestRight != null && rememberedRequestRight.isTimedOut()) {
                this.rememberedDomains.remove(rememberedRequestRight.getURI());
                rememberedRequestRight = null;
            }
            return rememberedRequestRight;
        }
    }

    public boolean allowDomain(URL uRL) {
        if (uRL == null) {
            return false;
        }
        RequestInfo requestInfo = DirectProxy.parseDomain(uRL.toString(), true, null);
        if (requestInfo == null) {
            return false;
        }
        RememberedRequestRight rememberedRequestRight = new RememberedRequestRight(requestInfo.getHost(), false, Long.MAX_VALUE);
        this.rememberedDomains.put(rememberedRequestRight.getURI(), rememberedRequestRight);
        return true;
    }

    private static ServerSocket closeThread(Thread thread, ServerSocket serverSocket, BooleanVariable booleanVariable) {
        if (thread == null) {
            return serverSocket;
        }
        int n = 0;
        while (thread.isAlive()) {
            if (booleanVariable.isTrue()) {
                thread.interrupt();
            }
            Thread.yield();
            try {
                thread.join(250L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (n > 10) {
                LogHolder.log(1, LogType.NET, "Shutting down direct proxy (" + (booleanVariable.isTrue() ? "Running" : "") + ") at " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort() + "...");
            }
            ++n;
        }
        return serverSocket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void stop() {
        Object object = this.THREAD_SYNC;
        synchronized (object) {
            if (this.threadRunLoop == null) {
                return;
            }
            LogHolder.log(4, LogType.NET, "Shutting down direct proxy...");
            this.m_bInterrupted = true;
            this.m_socketListener = DirectProxy.closeThread(this.threadRunLoop, this.m_socketListener, this.m_bIsRunningOne);
            this.m_socketListenerTwo = DirectProxy.closeThread(this.threadRunLoopTwo, this.m_socketListenerTwo, this.m_bIsRunningTwo);
            this.threadRunLoop = null;
            this.threadRunLoopTwo = null;
        }
    }

    protected ImmutableProxyInterface getProxyInterface() {
        return this.m_proxyInterface.getProxyInterface(false).getProxyInterface();
    }

    protected static RequestInfo parseDomain(String string, boolean bl, String string2) {
        int n = 80;
        if (string != null && string.length() > 0) {
            String string3 = null;
            int n2 = string.indexOf("//");
            if (n2 > 0 && string.length() > 2) {
                string = string.substring(n2 + 2, string.length());
            }
            if ((n2 = string.indexOf("?")) > 0) {
                if (string.length() >= n2 + 3) {
                    string3 = string.substring(n2 + 1, string.length());
                }
                string = string.substring(0, n2);
                if (string3 != null) {
                    n2 = string3.toLowerCase().indexOf(JONDOPROXY_ARG);
                    if (n2 >= 0 && string3.length() >= n2 + JONDOPROXY_ARG.length() + 1) {
                        if ((n2 = (string3 = string3.substring(n2 + JONDOPROXY_ARG.length(), string3.length())).indexOf("&")) > 0) {
                            string3 = string3.substring(0, n2);
                        }
                    } else {
                        string3 = null;
                    }
                } else {
                    string3 = null;
                }
            }
            if ((n2 = string.indexOf("/")) > 0) {
                string = string.substring(0, n2);
            }
            if ((n2 = string.lastIndexOf(":")) > 0 && string.length() > n2 + 1) {
                try {
                    n = Integer.parseInt(string.substring(n2 + 1, string.length()));
                }
                catch (NumberFormatException numberFormatException) {
                    LogHolder.log(3, LogType.NET, "Could not parse port!", numberFormatException);
                }
                string = string.substring(0, n2);
            }
            while (string.endsWith("/")) {
                string = string.substring(0, string.length() - 1);
            }
            if (bl && (n2 = string.lastIndexOf(".")) > 0 && string.length() > n2 + 1) {
                try {
                    Integer.parseInt(string.substring(n2 + 1, string.length()));
                }
                catch (NumberFormatException numberFormatException) {
                    StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
                    while (stringTokenizer.countTokens() > 2) {
                        stringTokenizer.nextToken();
                    }
                    string = stringTokenizer.nextToken() + "." + stringTokenizer.nextToken();
                }
            }
            return new RequestInfo(string, string2, n, string3);
        }
        return null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private final class DirectConViaHTTPProxy
    implements Runnable {
        private Socket m_clientSocket;
        private InputStream m_clientInputStream;

        public DirectConViaHTTPProxy(Socket socket, InputStream inputStream) {
            this.m_clientSocket = socket;
            this.m_clientInputStream = inputStream;
        }

        public void run() {
            try {
                int n;
                Object object;
                InputStream inputStream = this.m_clientInputStream != null ? this.m_clientInputStream : this.m_clientSocket.getInputStream();
                Socket socket = new Socket(DirectProxy.this.getProxyInterface().getHost(), DirectProxy.this.getProxyInterface().getPort());
                DirectProxyResponse directProxyResponse = new DirectProxyResponse(socket.getInputStream(), this.m_clientSocket.getOutputStream());
                Thread thread = new Thread((Runnable)directProxyResponse, "JAP - DirectProxyResponse");
                thread.start();
                OutputStream outputStream = socket.getOutputStream();
                if (DirectProxy.this.getProxyInterface().isAuthenticationUsed()) {
                    object = DirectProxyConnection.readLine(inputStream);
                    object = (String)object + "\r\n";
                    outputStream.write(((String)object).getBytes());
                    object = DirectProxy.this.getProxyInterface().getProxyAuthorizationHeaderAsString();
                    outputStream.write(((String)object).getBytes());
                    outputStream.flush();
                }
                object = new byte[1000];
                while ((n = inputStream.read((byte[])object)) != -1) {
                    if (n <= 0) continue;
                    outputStream.write((byte[])object, 0, n);
                    outputStream.flush();
                }
                thread.join();
                outputStream.close();
                inputStream.close();
                socket.close();
            }
            catch (IOException iOException) {
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.NET, "JAPDirectConViaProxy: Exception: " + exception);
            }
        }
    }

    private final class RememberedRequestRight {
        private long m_timeRemembered;
        private boolean m_bWarn;
        private String m_URI;
        private boolean m_bUserAction;

        public RememberedRequestRight(String string, boolean bl, long l) {
            this(string, bl, l, true);
        }

        public RememberedRequestRight(String string, boolean bl, long l, boolean bl2) {
            this.m_URI = string;
            this.m_timeRemembered = l;
            this.m_bWarn = bl;
            this.m_bUserAction = bl2;
        }

        public boolean isUserAction() {
            return this.m_bUserAction;
        }

        public String getURI() {
            return this.m_URI;
        }

        public boolean isWarningShown() {
            return this.m_bWarn;
        }

        public long getCountDown() {
            if (this.m_timeRemembered == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            long l = this.m_timeRemembered - System.currentTimeMillis();
            if (l < 0L) {
                l = 0L;
            }
            return l;
        }

        public boolean isTimedOut() {
            return this.m_timeRemembered < System.currentTimeMillis();
        }
    }

    private static final class SendAnonWarning
    implements Runnable {
        private static final String MSG_BLOCKED = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".blocked";
        private static final String MSG_BLOCKED_ALL = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".blockedAll";
        private static final String MSG_BLOCKED_DOMAIN = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".blockedDomain";
        private static final String MSG_COUNTDOWN = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".countdown";
        private static final String MSG_RELOAD = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".reload";
        private static final String MSG_BLOCKED_PERMANENTLY = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".blockedPermanently";
        private static final String MSG_ANON_MODE_OFF = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".htmlAnonModeOff";
        private static final String MSG_ANON_MODE_OFF_BUT_FORCED = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".htmlAnonModeOffButForced";
        private static final String MSG_CONNECTING = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".connecting";
        private static final String MSG_ANONYMITY_MODE = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".anonymityModeOff";
        private static final String MSG_WAIT_FOR_CONNECTION = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".waitForConnection";
        private static final String MSG_WAIT_FOR_CONNECTION_2 = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".waitForConnection2";
        private static final String MSG_HTML_ANON_MODE_SWITCH = (class$anon$proxy$DirectProxy == null ? (class$anon$proxy$DirectProxy = DirectProxy.class$("anon.proxy.DirectProxy")) : class$anon$proxy$DirectProxy).getName() + ".htmlAnonModeSwitch";
        private Socket socket;
        private SimpleDateFormat dateFormatHTTP;
        private InputStream m_clientInputStream;
        private RememberedRequestRight m_requestRight;
        private DirectProxy m_directProxy;
        private RequestInfo m_info;

        public SendAnonWarning(Socket socket, InputStream inputStream, RememberedRequestRight rememberedRequestRight, DirectProxy directProxy, RequestInfo requestInfo) {
            this.socket = socket;
            this.m_info = requestInfo;
            this.m_directProxy = directProxy;
            this.m_requestRight = rememberedRequestRight;
            this.m_clientInputStream = inputStream;
            this.dateFormatHTTP = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            this.dateFormatHTTP.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                if (this.m_clientInputStream != null) {
                    this.m_clientInputStream.read();
                } else {
                    this.socket.getInputStream().read();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                AllowProxyConnectionCallback allowProxyConnectionCallback = this.m_directProxy.m_callback;
                String string = null;
                String string2 = string = allowProxyConnectionCallback != null ? allowProxyConnectionCallback.getApplicationName() : "JonDo";
                if (string == null) {
                    string = "JonDo";
                }
                String string3 = this.dateFormatHTTP.format(new Date());
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
                bufferedWriter.write("HTTP/1.1 202 Accepted\r\n");
                bufferedWriter.write("Content-type: text/html; charset=UTF-8\r\n");
                bufferedWriter.write("Expires: " + string3 + "\r\n");
                bufferedWriter.write("Date: " + string3 + "\r\n");
                bufferedWriter.write("Pragma: no-cache\r\n");
                bufferedWriter.write("Cache-Control: no-cache\r\n\r\n");
                bufferedWriter.write("<HTML>\n<HEAD>\n<TITLE>" + string + "</TITLE>\n" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + (this.m_info != null && this.m_info.getJonDoProxyArgument() != null ? "<meta http-equiv=\"JonDoProxy\" content=\"" + this.m_info.getJonDoProxyArgument() + "\" />\n" : "") + "</HEAD>\n<BODY>\n");
                bufferedWriter.write("<PRE>" + string3 + "</PRE>\n");
                bufferedWriter.flush();
                boolean bl = allowProxyConnectionCallback != null && allowProxyConnectionCallback.isConnecting();
                String string4 = bl ? JAPMessages.getString(MSG_CONNECTING).toUpperCase() : JAPMessages.getString(MSG_ANONYMITY_MODE) + " " + JAPMessages.getString("ngAnonOff").toUpperCase();
                string4 = string + ": " + string4;
                string4 = "<CENTER><H1>" + string4 + "</H1>";
                if (this.m_requestRight == null || allowProxyConnectionCallback == null || allowProxyConnectionCallback.isNonAnonymousAccessForbidden()) {
                    string4 = allowProxyConnectionCallback != null && allowProxyConnectionCallback.isConnecting() ? (allowProxyConnectionCallback.isNonAnonymousAccessForbidden() && allowProxyConnectionCallback.getAllowNonAnonymousSettingName() != null ? string4 + JAPMessages.getString(MSG_ANON_MODE_OFF, new String[]{JAPMessages.getString(allowProxyConnectionCallback.getAllowNonAnonymousSettingName()), "<BR>" + JAPMessages.getString(MSG_WAIT_FOR_CONNECTION), DirectProxy.JONDOPROXY}) : string4 + JAPMessages.getString(MSG_WAIT_FOR_CONNECTION_2)) : (allowProxyConnectionCallback != null && allowProxyConnectionCallback.getAllowNonAnonymousSettingName() != null && allowProxyConnectionCallback.isNonAnonymousAccessForbidden() ? string4 + JAPMessages.getString(MSG_ANON_MODE_OFF_BUT_FORCED, new String[]{JAPMessages.getString("ngAnonOn")}) + "<br><br>" + JAPMessages.getString(MSG_ANON_MODE_OFF, new String[]{JAPMessages.getString(allowProxyConnectionCallback.getAllowNonAnonymousSettingName()), "", DirectProxy.JONDOPROXY}) : string4 + JAPMessages.getString(MSG_ANON_MODE_OFF_BUT_FORCED, new String[]{JAPMessages.getString("ngAnonOn")}));
                    byte[] arrby = (string4 + "</CENTER>").getBytes("UTF-8");
                    this.socket.getOutputStream().write(arrby, 0, arrby.length);
                } else {
                    long l = this.m_requestRight.getCountDown();
                    if (!bl) {
                        string4 = string4 + JAPMessages.getString(MSG_ANON_MODE_OFF_BUT_FORCED, new String[]{JAPMessages.getString("ngAnonOn")}) + "<BR><BR>";
                    }
                    String string5 = !this.m_requestRight.isUserAction() ? "" : (this.m_requestRight.getURI().equals(DirectProxy.GENERAL_RULE) ? JAPMessages.getString(MSG_BLOCKED_ALL) + "<BR>" : JAPMessages.getString(MSG_BLOCKED_DOMAIN, "<code>" + this.m_requestRight.getURI() + "</code>") + "<BR>");
                    Object[] arrobject = l == Long.MAX_VALUE ? new String[]{bl ? "<BR>" + JAPMessages.getString(MSG_WAIT_FOR_CONNECTION) : "", (bl ? JAPMessages.getString(MSG_HTML_ANON_MODE_SWITCH, JAPMessages.getString("ngAnonOff")) : JAPMessages.getString(MSG_BLOCKED_PERMANENTLY)) + "<BR>" + JAPMessages.getString(MSG_RELOAD), DirectProxy.JONDOPROXY} : new String[]{bl ? "<BR>" + JAPMessages.getString(MSG_WAIT_FOR_CONNECTION) : "", JAPMessages.getString(MSG_COUNTDOWN, new String[]{"" + l / 1000L, bl ? JAPMessages.getString(MSG_HTML_ANON_MODE_SWITCH, JAPMessages.getString("ngAnonOff")) : JAPMessages.getString(MSG_BLOCKED_PERMANENTLY), "<BR>" + JAPMessages.getString(MSG_RELOAD)}), DirectProxy.JONDOPROXY};
                    byte[] arrby = (string4 + string5 + JAPMessages.getString(MSG_BLOCKED, arrobject) + "</CENTER>").getBytes("UTF-8");
                    this.socket.getOutputStream().write(arrby, 0, arrby.length);
                }
                bufferedWriter.write("</BODY></HTML>\n");
                bufferedWriter.flush();
                bufferedWriter.close();
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.NET, exception);
            }
            finally {
                try {
                    SocketGuard.close(this.socket);
                }
                catch (IOException iOException) {
                    LogHolder.log(3, LogType.NET, iOException);
                }
            }
        }
    }

    private class DirectProxyRunnable
    implements Runnable {
        private ServerSocket m_serverSocket;
        private BooleanVariable m_bRunning;

        public DirectProxyRunnable(ServerSocket serverSocket, BooleanVariable booleanVariable) {
            this.m_serverSocket = serverSocket;
            this.m_bRunning = booleanVariable;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            ConnectionHandler connectionHandler;
            Object object;
            try {
                this.m_serverSocket.setSoTimeout(2000);
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.NET, "Could not set accept time out!", exception);
            }
            while (!DirectProxy.this.m_bInterrupted && !Thread.currentThread().isInterrupted()) {
                object = null;
                try {
                    object = this.m_serverSocket.accept();
                }
                catch (InterruptedIOException interruptedIOException) {
                    Thread.yield();
                    continue;
                }
                catch (SocketException socketException) {
                    LogHolder.log(3, LogType.NET, "Accept socket exception: " + socketException);
                    break;
                }
                catch (IOException iOException) {
                    LogHolder.log(2, LogType.NET, "Socket could not accept!" + iOException);
                    break;
                }
                if (DirectProxy.this.m_bInterrupted || Thread.currentThread().isInterrupted()) {
                    try {
                        SocketGuard.close((Socket)object);
                    }
                    catch (IOException iOException) {
                        LogHolder.log(3, LogType.NET, iOException);
                    }
                    break;
                }
                try {
                    ((Socket)object).setSoTimeout(0);
                }
                catch (SocketException socketException) {
                    LogHolder.log(3, LogType.NET, "Could not set socket to blocking mode! Exception: " + socketException);
                    try {
                        SocketGuard.close((Socket)object);
                    }
                    catch (IOException iOException) {
                        LogHolder.log(3, LogType.NET, iOException);
                    }
                    object = null;
                    continue;
                }
                if (DirectProxy.this.m_bInterrupted || Thread.currentThread().isInterrupted()) {
                    try {
                        SocketGuard.close((Socket)object);
                    }
                    catch (IOException iOException) {
                        LogHolder.log(3, LogType.NET, iOException);
                    }
                    break;
                }
                connectionHandler = new ConnectionHandler((Socket)object){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void run() {
                        this.handleConnection();
                        Vector vector = DirectProxy.this.m_vecThreads;
                        synchronized (vector) {
                            DirectProxy.this.m_vecThreads.removeElement(Thread.currentThread());
                            DirectProxy.this.m_vecThreads.notifyAll();
                        }
                    }
                };
                DirectProxy.this.m_vecThreads.addElement(connectionHandler);
                connectionHandler.start();
            }
            this.m_bRunning.set(false);
            LogHolder.log(5, LogType.NET, "Accept was stopped.");
            object = (Vector)DirectProxy.this.m_vecThreads.clone();
            if (((Vector)object).size() > 0) {
                LogHolder.log(4, LogType.NET, "Closing remaining threads...");
            }
            while (((Vector)object).size() > 0) {
                LogHolder.log(4, LogType.NET, "Closing thread...");
                connectionHandler = (ConnectionHandler)((Vector)object).lastElement();
                while (connectionHandler.isAlive()) {
                    LogHolder.log(4, LogType.NET, "Closing last thread from a total of " + ((Vector)object).size());
                    connectionHandler.interrupt();
                    connectionHandler.close();
                    Thread.yield();
                    LogHolder.log(6, LogType.NET, "Before sync...");
                    Vector vector = DirectProxy.this.m_vecThreads;
                    synchronized (vector) {
                        if (connectionHandler.isAlive()) {
                            try {
                                DirectProxy.this.m_vecThreads.wait(250L);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                        }
                    }
                }
                ((Vector)object).removeElement(connectionHandler);
                LogHolder.log(4, LogType.NET, "Thread closed. " + ((Vector)object).size() + " threads left.");
            }
            if (DirectProxy.this.m_vecThreads.size() > 0) {
                LogHolder.log(1, LogType.NET, "Possible insecurity or memory leak: Direct Proxy Server has " + DirectProxy.this.m_vecThreads.size() + " remaining threads!!");
            }
            LogHolder.log(6, LogType.NET, "Direct Proxy Server stopped.");
        }
    }

    private class ConnectionHandler
    extends Thread {
        private PushbackInputStream clientInputStream;
        private Socket m_socket;

        public ConnectionHandler(Socket socket) {
            this.m_socket = socket;
        }

        public synchronized void close() {
            try {
                SocketGuard.close(this.m_socket);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                if (this.clientInputStream != null) {
                    this.clientInputStream.close();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void handleConnection() {
            RememberedRequestRight rememberedRequestRight;
            RequestInfo requestInfo;
            if (this.m_socket == null) {
                return;
            }
            try {
                this.clientInputStream = new PushbackInputStream(this.m_socket.getInputStream(), 2000);
            }
            catch (IOException iOException) {
                LogHolder.log(3, LogType.NET, "Could not handle HTTP connection!", iOException);
                this.close();
                return;
            }
            Object object = this;
            synchronized (object) {
                if (SocketGuard.isClosed(this.m_socket)) {
                    LogHolder.log(3, LogType.NET, "Could not handle HTTP connection! Socket is closed.");
                    this.close();
                    return;
                }
            }
            object = DirectProxy.this.rememberedDomains;
            synchronized (object) {
                if (DirectProxy.this.m_callback == null || DirectProxy.this.m_callback.haveRulesChanged()) {
                    DirectProxy.this.rememberedDomains.clear();
                    if (DirectProxy.this.m_callback != null) {
                        DirectProxy.this.m_callback.setRulesChanged(false);
                    }
                }
                requestInfo = DirectProxyConnection.getURI(this.clientInputStream, 2000);
                rememberedRequestRight = DirectProxy.this.getRequestRight(requestInfo);
            }
            if (requestInfo == null) {
                try {
                    SocketGuard.close(this.m_socket);
                }
                catch (IOException iOException) {
                    LogHolder.log(3, LogType.NET, iOException);
                }
                try {
                    this.clientInputStream.close();
                }
                catch (IOException iOException) {
                    LogHolder.log(3, LogType.NET, iOException);
                }
                return;
            }
            if (requestInfo != null && rememberedRequestRight == null) {
                object = DirectProxy.this.SYNC_CALLBACK;
                synchronized (object) {
                    AllowProxyConnectionCallback.Answer answer;
                    rememberedRequestRight = DirectProxy.this.getRequestRight(requestInfo);
                    if (rememberedRequestRight == null && (answer = DirectProxy.this.m_callback != null ? DirectProxy.this.m_callback.callback(requestInfo) : new AllowProxyConnectionCallback.Answer(false, false, false)) != null) {
                        long l = !answer.isTimedOut() ? Long.MAX_VALUE : (answer.isAllowed() ? System.currentTimeMillis() + 10000L : System.currentTimeMillis() + 10000L);
                        Hashtable hashtable = DirectProxy.this.rememberedDomains;
                        synchronized (hashtable) {
                            if (DirectProxy.this.m_callback == null || DirectProxy.this.m_callback.haveRulesChanged()) {
                                DirectProxy.this.rememberedDomains.clear();
                                if (DirectProxy.this.m_callback != null) {
                                    DirectProxy.this.m_callback.setRulesChanged(false);
                                }
                            }
                            if (answer.isRemembered()) {
                                if (answer.isTimedOut() && DirectProxy.this.m_callback != null && DirectProxy.this.m_callback.isAskedForAnyNonAnonymousRequest()) {
                                    l = System.currentTimeMillis() + 5000L;
                                }
                                rememberedRequestRight = new RememberedRequestRight(DirectProxy.GENERAL_RULE, !answer.isAllowed(), l);
                            } else {
                                rememberedRequestRight = new RememberedRequestRight(requestInfo.getHost(), !answer.isAllowed(), l);
                            }
                            DirectProxy.this.rememberedDomains.put(rememberedRequestRight.getURI(), rememberedRequestRight);
                        }
                    }
                }
            }
            if (rememberedRequestRight != null && !rememberedRequestRight.isWarningShown()) {
                if (DirectProxy.this.getProxyInterface() != null && DirectProxy.this.getProxyInterface().isValid() && DirectProxy.this.getProxyInterface().getProtocol() == 1) {
                    new DirectConViaHTTPProxy(this.m_socket, this.clientInputStream).run();
                } else {
                    new DirectProxyConnection(this.m_socket, this.clientInputStream, DirectProxy.this);
                }
            } else {
                new SendAnonWarning(this.m_socket, this.clientInputStream, rememberedRequestRight, DirectProxy.this, requestInfo).run();
            }
        }
    }

    public static abstract class AllowProxyConnectionCallback
    implements Observer {
        public static final String RULES_CHANGED_OBSERVABLE_NOTIFIER = "RulesChanged";
        private boolean m_bRulesChanged = false;

        public abstract boolean isConnecting();

        public boolean haveRulesChanged() {
            return this.m_bRulesChanged;
        }

        public URL getHTMLHelpPath() {
            return null;
        }

        private void setRulesChanged(boolean bl) {
            this.m_bRulesChanged = bl;
        }

        public void update(Observable observable, Object object) {
            if (object != null && object == "RulesChanged") {
                this.m_bRulesChanged = true;
            }
        }

        public abstract boolean isNonAnonymousAccessForbidden();

        public abstract String getApplicationName();

        public abstract String getAllowNonAnonymousSettingName();

        public abstract boolean isAskedForAnyNonAnonymousRequest();

        public abstract Answer callback(RequestInfo var1);

        public static class Answer {
            private boolean m_bRemembered;
            private boolean m_bAllow;
            private boolean m_bTimeout;

            public Answer(boolean bl, boolean bl2, boolean bl3) {
                this.m_bAllow = bl;
                this.m_bRemembered = bl2;
                this.m_bTimeout = bl3;
            }

            public boolean isRemembered() {
                return this.m_bRemembered;
            }

            public boolean isTimedOut() {
                return this.m_bTimeout;
            }

            public boolean isAllowed() {
                return this.m_bAllow;
            }
        }
    }

    public static class RequestInfo {
        private String m_strURI;
        private String m_strMethod;
        private int m_port;
        private String m_jdpArg;

        protected RequestInfo(String string, String string2, int n, String string3) {
            this.m_strURI = string;
            this.m_strMethod = string2;
            this.m_port = n;
            this.m_jdpArg = string3;
        }

        public String getJonDoProxyArgument() {
            return this.m_jdpArg;
        }

        public String getHost() {
            return this.m_strURI;
        }

        public String getMethod() {
            return this.m_strMethod;
        }

        public int getPort() {
            return this.m_port;
        }
    }
}

