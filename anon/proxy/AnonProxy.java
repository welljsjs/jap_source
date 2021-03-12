/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.AnonChannel;
import anon.AnonServerDescription;
import anon.AnonService;
import anon.AnonServiceEventListener;
import anon.AnonServiceFactory;
import anon.NotConnectedToMixException;
import anon.client.AbstractAutoSwitchedMixCascadeContainer;
import anon.client.AnonClient;
import anon.client.BasicTrustModel;
import anon.client.ITrustModel;
import anon.client.TrustModel;
import anon.error.AccountEmptyException;
import anon.error.AnonServiceException;
import anon.error.ConnectionEstablishmentTimeoutException;
import anon.error.INotRecoverableException;
import anon.error.NotRecoverableException;
import anon.error.RecoverableExceptionContainer;
import anon.error.ServiceInterruptedException;
import anon.infoservice.AbstractMixCascadeContainer;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.mixminion.MixminionServiceDescription;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.NotRecoverableXMLError;
import anon.proxy.AbstractHTTPConnectionListener;
import anon.proxy.AnonProxyRequest;
import anon.proxy.DecompressionProxyCallback;
import anon.proxy.DirectProxy;
import anon.proxy.HTTPProxyCallback;
import anon.proxy.IProxyListener;
import anon.proxy.ProxyCallback;
import anon.proxy.ProxyCallbackHandler;
import anon.terms.TermsAndConditionConfirmation;
import anon.tor.TorAnonServerDescription;
import anon.transport.connection.IStreamConnection;
import anon.util.ExceptionVariable;
import anon.util.ObjectQueue;
import anon.util.SocketGuard;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public final class AnonProxy
implements AnonServiceEventListener {
    public static final int UNLIMITED_REQUESTS = Integer.MAX_VALUE;
    public static final int MIN_REQUESTS = 5;
    public static final int E_MIX_PROTOCOL_NOT_SUPPORTED = -10;
    public static final int E_SIGNATURE_CHECK_FIRSTMIX_FAILED = -22;
    public static final int E_SIGNATURE_CHECK_OTHERMIX_FAILED = -23;
    private static final int RECONNECT_INTERVAL = 800;
    private int m_maxRequests = Integer.MAX_VALUE;
    private int m_socketGuardTimeout = 0;
    private AnonClient m_Anon;
    private AnonService m_Tor;
    private AnonService m_Mixminion;
    private Vector m_anonServiceListener;
    private Thread threadRunOne;
    private Thread threadRunTwo;
    private final Object SYNC_THREAD_RUN = new Object();
    private Thread m_tInit;
    private ServerSocket m_socketListener;
    private ServerSocket m_socketListenerTwo;
    private IMutableProxyInterface m_proxyInterface = new IMutableProxyInterface.DummyMutableProxyInterface();
    private IProxyListener m_ProxyListener;
    private volatile int m_numChannels = 0;
    private boolean m_bReconnecting = false;
    private boolean m_bConnecting = false;
    private final Object THREAD_SYNC = new Object();
    private final Object SHUTDOWN_SYNC = new Object();
    private boolean bShuttingDown = false;
    private ProxyCallbackHandler m_callbackHandler;
    private final Object SYNC_CALLBACK_HANDLER = new Object();
    private final HTTPProxyCallback m_httpProxyCallback = new HTTPProxyCallback();
    private DecompressionProxyCallback m_decompressionProxyCallback = null;
    private TermsAndConditionConfirmation termsConfirmation = null;
    private AbstractMixCascadeContainer m_containerMixCascade = new DummyMixCascadeContainer();
    private Observer m_observer;
    private boolean m_bWeChanged = false;
    private TorAnonServerDescription m_currentTorParams;
    private MixminionServiceDescription m_currentMixminionParams;
    private boolean m_forwardedConnection;
    private int m_maxDummyTrafficInterval = 30000;
    private final Vector m_currentStartThreads = new Vector();
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public AnonProxy(ServerSocket serverSocket, TermsAndConditionConfirmation termsAndConditionConfirmation, int n) {
        this(serverSocket, null, termsAndConditionConfirmation);
    }

    public AnonProxy(DirectProxy directProxy, IMutableProxyInterface iMutableProxyInterface, TermsAndConditionConfirmation termsAndConditionConfirmation) {
        this(directProxy, null, iMutableProxyInterface, termsAndConditionConfirmation);
    }

    public AnonProxy(ServerSocket serverSocket, IMutableProxyInterface iMutableProxyInterface, TermsAndConditionConfirmation termsAndConditionConfirmation) {
        this(null, serverSocket, iMutableProxyInterface, termsAndConditionConfirmation);
    }

    private AnonProxy(DirectProxy directProxy, ServerSocket serverSocket, IMutableProxyInterface iMutableProxyInterface, TermsAndConditionConfirmation termsAndConditionConfirmation) {
        if ((directProxy == null || directProxy.getSocketListener() == null) && serverSocket == null) {
            throw new IllegalArgumentException("Socket listener is null!");
        }
        if (directProxy != null && serverSocket == null) {
            this.m_socketListener = directProxy.getSocketListener();
            this.m_socketListenerTwo = directProxy.getSocketListenerTwo();
        }
        if (this.m_socketListener == null) {
            this.m_socketListener = serverSocket;
        }
        if (iMutableProxyInterface != null) {
            this.m_proxyInterface = iMutableProxyInterface;
        }
        this.m_Anon = new AnonClient(directProxy);
        this.m_Anon.setProxy(this.m_proxyInterface);
        this.setDummyTraffic(Integer.MAX_VALUE);
        this.m_forwardedConnection = false;
        this.m_anonServiceListener = new Vector();
        this.m_Anon.removeEventListeners();
        this.m_Anon.addEventListener(this);
        this.termsConfirmation = termsAndConditionConfirmation;
    }

    public AnonProxy(DirectProxy directProxy, IStreamConnection iStreamConnection, MixCascade mixCascade, int n, TermsAndConditionConfirmation termsAndConditionConfirmation) {
        if (directProxy == null) {
            throw new IllegalArgumentException("Socket listener is null!");
        }
        this.m_socketListener = directProxy.getSocketListener();
        this.m_socketListenerTwo = directProxy.getSocketListenerTwo();
        this.m_Anon = new AnonClient(directProxy, iStreamConnection, mixCascade);
        this.m_forwardedConnection = true;
        this.m_maxDummyTrafficInterval = n;
        this.setDummyTraffic(n);
        this.m_anonServiceListener = new Vector();
        this.m_Anon.removeEventListeners();
        this.m_Anon.addEventListener(this);
        this.termsConfirmation = termsAndConditionConfirmation;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void enableProxyCallback(ProxyCallback proxyCallback) {
        Object object = this.SYNC_CALLBACK_HANDLER;
        synchronized (object) {
            if (proxyCallback == null) {
                return;
            }
            if (this.m_callbackHandler == null) {
                LogHolder.log(4, LogType.NET, "No ProxyCallbackHandler activated: cannot process HTTP headers.");
                return;
            }
            this.m_callbackHandler.registerProxyCallback(proxyCallback);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void disableProxyCallback(ProxyCallback proxyCallback) {
        Object object = this.SYNC_CALLBACK_HANDLER;
        synchronized (object) {
            if (proxyCallback != null && this.m_callbackHandler != null) {
                this.m_callbackHandler.removeCallback(proxyCallback);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setHTTPHeaderProcessingEnabled(boolean bl, boolean bl2) {
        Object object = this.SYNC_CALLBACK_HANDLER;
        synchronized (object) {
            if (bl2) {
                this.m_httpProxyCallback.blockHTTPListeners(!bl);
                if (this.m_callbackHandler == null) {
                    this.m_callbackHandler = new ProxyCallbackHandler();
                }
                this.enableProxyCallback(this.m_httpProxyCallback);
            } else if (bl) {
                if (this.m_callbackHandler == null) {
                    this.m_callbackHandler = new ProxyCallbackHandler();
                }
                this.enableProxyCallback(this.m_httpProxyCallback);
            } else {
                this.disableProxyCallback(this.m_httpProxyCallback);
                this.m_callbackHandler = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void setHTTPDecompressionEnabled(boolean bl) {
        Object object = this.SYNC_CALLBACK_HANDLER;
        synchronized (object) {
            if (bl) {
                if (this.m_decompressionProxyCallback == null) {
                    this.m_decompressionProxyCallback = new DecompressionProxyCallback();
                }
                this.enableProxyCallback(this.m_decompressionProxyCallback);
            } else {
                this.disableProxyCallback(this.m_decompressionProxyCallback);
                this.m_decompressionProxyCallback = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void removeHTTPConnectionListener(AbstractHTTPConnectionListener abstractHTTPConnectionListener) {
        if (abstractHTTPConnectionListener == null) {
            return;
        }
        Object object = this.SYNC_CALLBACK_HANDLER;
        synchronized (object) {
            this.m_httpProxyCallback.removeHTTPConnectionListener(abstractHTTPConnectionListener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHTTPConnectionListener(AbstractHTTPConnectionListener abstractHTTPConnectionListener) {
        Object object = this.SYNC_CALLBACK_HANDLER;
        synchronized (object) {
            this.m_httpProxyCallback.addHTTPConnectionListener(abstractHTTPConnectionListener);
            this.enableProxyCallback(this.m_httpProxyCallback);
        }
    }

    public MixCascade getMixCascade() {
        if (this.m_Anon.isConnected()) {
            return this.m_Anon.getCurrentService();
        }
        return this.m_containerMixCascade.getCurrentCascade();
    }

    public void setTorParams(TorAnonServerDescription torAnonServerDescription) {
        this.m_currentTorParams = torAnonServerDescription;
    }

    public TorAnonServerDescription getTorParams() {
        return this.m_currentTorParams;
    }

    public void setMixminionParams(MixminionServiceDescription mixminionServiceDescription) {
        this.m_currentMixminionParams = mixminionServiceDescription;
    }

    public MixminionServiceDescription getMixminionParams() {
        return this.m_currentMixminionParams;
    }

    public void setMaxConcurrentRequests(int n) {
        if (n > 5) {
            this.m_maxRequests = n;
        }
    }

    public int getMaxConcurrentRequests() {
        return this.m_maxRequests;
    }

    public void setDummyTraffic(int n) {
        try {
            if (!this.m_forwardedConnection || this.m_maxDummyTrafficInterval < 0 || n == Integer.MAX_VALUE) {
                this.m_Anon.setDummyTraffic(n);
            } else if (n >= 0) {
                this.m_Anon.setDummyTraffic(Math.min(n, this.m_maxDummyTrafficInterval));
            } else {
                this.m_Anon.setDummyTraffic(this.m_maxDummyTrafficInterval);
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
    }

    public void setInterfaceBlockTimeout(long l) {
        this.m_Anon.setInterfaceBlockTimout(l);
    }

    public void setDebug(boolean bl) {
        this.m_Anon.setDebug(bl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        Object object = this.SHUTDOWN_SYNC;
        synchronized (object) {
            this.m_tInit = null;
            Object object2 = this.SYNC_THREAD_RUN;
            synchronized (object2) {
                if (this.threadRunOne == null && this.threadRunTwo == null) {
                    this.disconnected();
                    return;
                }
            }
            this.bShuttingDown = true;
            this.m_Anon.shutdown(true);
            if (this.m_Tor != null) {
                this.m_Tor.shutdown(true);
            }
            if (this.m_Mixminion != null) {
                this.m_Mixminion.shutdown(true);
            }
            while (this.threadRunOne.isAlive()) {
                try {
                    this.threadRunOne.interrupt();
                    this.threadRunOne.join(500L);
                }
                catch (InterruptedException interruptedException) {}
            }
            while (this.threadRunTwo != null && this.threadRunTwo.isAlive()) {
                try {
                    this.threadRunTwo.interrupt();
                    this.threadRunTwo.join(500L);
                }
                catch (InterruptedException interruptedException) {}
            }
            this.m_Tor = null;
            this.m_Mixminion = null;
            object2 = this.SYNC_THREAD_RUN;
            synchronized (object2) {
                this.threadRunTwo = null;
                this.threadRunOne = null;
            }
            this.packetMixed(0L);
            while (this.m_bReconnecting) {
                try {
                    this.SHUTDOWN_SYNC.wait(100L);
                }
                catch (InterruptedException interruptedException) {}
            }
            this.disconnected();
            this.bShuttingDown = false;
            TrustModel.getCurrentTrustModel().unblockInterfacesFromDatabase();
            ListenerInterface.unblockInterfacesFromDatabase(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = AnonProxy.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry);
        }
    }

    AnonChannel createChannel(int n) throws NotConnectedToMixException, Exception {
        if (n == 1) {
            if (this.m_Tor != null) {
                return this.m_Tor.createChannel(1);
            }
            if (this.getMixCascade().isSocks5Supported()) {
                return this.m_Anon.createChannel(1);
            }
            LogHolder.log(3, LogType.NET, "Received SOCKS request, but no SOCKS server is available.");
        } else {
            if (n == 0) {
                return this.m_Anon.createChannel(0);
            }
            if (n == 2 && this.m_Mixminion != null) {
                return this.m_Mixminion.createChannel(2);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void reconnect() {
        Object object = this.THREAD_SYNC;
        synchronized (object) {
            if (this.m_Anon.isConnected() || this.bShuttingDown || Thread.currentThread().isInterrupted()) {
                this.m_bConnecting = false;
                return;
            }
            if (!this.m_containerMixCascade.isReconnectedAutomatically()) {
                this.m_bConnecting = false;
                this.stop();
                this.THREAD_SYNC.notifyAll();
                return;
            }
            if (this.m_bReconnecting) {
                return;
            }
            this.m_bReconnecting = true;
            this.m_bConnecting = true;
            while (this.threadRunOne != null && this.m_containerMixCascade.isReconnectedAutomatically() && !this.m_Anon.isConnected() && !this.bShuttingDown && !Thread.currentThread().isInterrupted()) {
                boolean bl = this.m_Anon.getCurrentService() == this.m_containerMixCascade.getCurrentCascade();
                this.m_bWeChanged = true;
                MixCascade mixCascade = this.m_containerMixCascade.getNextCascade();
                this.m_bWeChanged = false;
                int n = 4;
                try {
                    if (mixCascade.getListenerInterface(0).getHost().equals("0.0.0.0")) {
                        n = 6;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                LogHolder.log(n, LogType.NET, "Try reconnect to AN.ON service. Connecting to " + mixCascade.getName() + "...");
                Thread thread = null;
                ExceptionVariable exceptionVariable = new ExceptionVariable(null);
                if (!mixCascade.equals(this.m_Anon.getCurrentService())) {
                    this.fireCurrentServiceChanged(mixCascade);
                }
                thread = this.startInitThread(exceptionVariable, mixCascade, true, bl);
                this.joinInitThread(thread, exceptionVariable);
                this.finishInitThread(mixCascade, exceptionVariable, thread);
                if (exceptionVariable.get() == null) continue;
                LogHolder.log(5, LogType.NET, exceptionVariable.get());
                if (exceptionVariable.get() instanceof ServiceInterruptedException || !this.m_containerMixCascade.isReconnectedAutomatically() || !this.m_containerMixCascade.isServiceAutoSwitched() && exceptionVariable.get() instanceof AnonServiceException && exceptionVariable.get() instanceof INotRecoverableException && ((AnonServiceException)exceptionVariable.get()).getService() != AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) {
                    if (!(exceptionVariable.get() instanceof INotRecoverableException)) break;
                    this.connectionError((AnonServiceException)exceptionVariable.get());
                    break;
                }
                try {
                    this.THREAD_SYNC.wait(800L);
                }
                catch (InterruptedException interruptedException) {
                    break;
                }
            }
            Object object2 = this.SHUTDOWN_SYNC;
            synchronized (object2) {
                this.m_bReconnecting = false;
                this.m_bConnecting = false;
                if (!(this.bShuttingDown || this.threadRunOne != null && this.isConnected() || this.m_containerMixCascade.isReconnectedAutomatically())) {
                    this.stop();
                    this.THREAD_SYNC.notifyAll();
                }
                this.SHUTDOWN_SYNC.notify();
            }
            if (this.isConnected()) {
                this.m_containerMixCascade.reset();
            }
            return;
        }
    }

    public void setProxyListener(IProxyListener iProxyListener) {
        this.m_ProxyListener = iProxyListener;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean interruptInit(Thread thread) {
        Object object = this.THREAD_SYNC;
        synchronized (object) {
            if (thread != null) {
                LogHolder.log(4, LogType.NET, "Interrupting init...", new InterruptedException());
                while (thread.isAlive()) {
                    thread.interrupt();
                    try {
                        thread.join(100L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
                LogHolder.log(4, LogType.NET, "Init was interrupted successfully!");
                return true;
            }
        }
        return false;
    }

    public int countStartThreads() {
        return this.m_currentStartThreads.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start(AbstractMixCascadeContainer abstractMixCascadeContainer) throws AnonServiceException {
        Object object = this.THREAD_SYNC;
        synchronized (object) {
            this.m_currentStartThreads.addElement(Thread.currentThread());
            this.m_bConnecting = true;
            try {
                this.start_internal(abstractMixCascadeContainer);
                this.m_bConnecting = false;
                this.m_currentStartThreads.removeElement(Thread.currentThread());
            }
            catch (AnonServiceException anonServiceException) {
                this.m_currentStartThreads.removeElement(Thread.currentThread());
                throw anonServiceException;
            }
            catch (RuntimeException runtimeException) {
                this.m_bConnecting = false;
                this.m_currentStartThreads.removeElement(Thread.currentThread());
                throw runtimeException;
            }
        }
    }

    private void joinInitThread(Thread thread, ExceptionVariable exceptionVariable) {
        if (thread == null) {
            return;
        }
        long l = System.currentTimeMillis() + (long)AnonClient.getLoginTimeout();
        while (thread.isAlive()) {
            this.m_bConnecting = true;
            try {
                this.THREAD_SYNC.wait(100L);
            }
            catch (InterruptedException interruptedException) {
                this.interruptInit(thread);
            }
            if (this.m_tInit == null) {
                this.interruptInit(thread);
                continue;
            }
            if (l >= System.currentTimeMillis()) continue;
            this.interruptInit(thread);
            exceptionVariable.set(new ConnectionEstablishmentTimeoutException(this.m_Anon.getCurrentService()));
        }
        this.m_bConnecting = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void finishInitThread(MixCascade mixCascade, ExceptionVariable exceptionVariable, Thread thread) {
        Object object = this.SHUTDOWN_SYNC;
        synchronized (object) {
            if (this.m_tInit == null || this.bShuttingDown) {
                if (this.m_Anon.isConnected()) {
                    this.m_Anon.shutdown(false);
                    this.disconnected();
                }
                exceptionVariable.set(new ServiceInterruptedException(mixCascade));
            }
            if (this.m_tInit == thread) {
                this.m_tInit = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Thread startInitThread(final ExceptionVariable exceptionVariable, final MixCascade mixCascade, final boolean bl, final boolean bl2) {
        Object object = this.SHUTDOWN_SYNC;
        synchronized (object) {
            if (this.bShuttingDown) {
                return null;
            }
            this.m_tInit = new Thread(){

                public void run() {
                    if (mixCascade.isPayment() && PayAccountsFile.getInstance().getChargedAccount(mixCascade.getPIID()) == null) {
                        try {
                            PayAccountsFile.getInstance().signalAccountRequest(mixCascade);
                        }
                        catch (AccountEmptyException accountEmptyException) {
                            exceptionVariable.set(accountEmptyException);
                            return;
                        }
                    }
                    try {
                        AnonProxy.this.m_Anon.initialize(mixCascade, AnonProxy.this.m_containerMixCascade, AnonProxy.this.termsConfirmation, bl2);
                        if (bl) {
                            AnonProxy.this.m_containerMixCascade.keepCurrentService(true);
                        }
                    }
                    catch (AnonServiceException anonServiceException) {
                        exceptionVariable.set(anonServiceException);
                    }
                }
            };
            this.m_tInit.start();
            Thread thread = this.m_tInit;
            return thread;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void start_internal(AbstractMixCascadeContainer abstractMixCascadeContainer) throws AnonServiceException {
        Thread thread;
        MixCascade mixCascade;
        Object object;
        boolean bl = false;
        ExceptionVariable exceptionVariable = new ExceptionVariable(null);
        Object object2 = this.SHUTDOWN_SYNC;
        synchronized (object2) {
            abstractMixCascadeContainer = abstractMixCascadeContainer == null ? new DummyMixCascadeContainer() : new EncapsulatedMixCascadeContainer(abstractMixCascadeContainer);
            if (this.m_observer != null) {
                this.m_containerMixCascade.deleteObserver(this.m_observer);
            }
            this.m_containerMixCascade = abstractMixCascadeContainer;
            this.m_bWeChanged = true;
            object = this.m_containerMixCascade.getNextCascade();
            mixCascade = object;
            if (this.bShuttingDown) {
                throw new ServiceInterruptedException(mixCascade);
            }
            this.m_bWeChanged = false;
            this.m_observer = new Observer(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void update(Observable observable, Object object) {
                    if (object != null && object instanceof MixCascade) {
                        Object object2 = AnonProxy.this.THREAD_SYNC;
                        synchronized (object2) {
                            if (!AnonProxy.this.m_bWeChanged) {
                                new Thread(new Runnable(){

                                    /*
                                     * WARNING - Removed try catching itself - possible behaviour change.
                                     */
                                    public void run() {
                                        Object object = AnonProxy.this.THREAD_SYNC;
                                        synchronized (object) {
                                            if (AnonProxy.this.m_tInit != null || AnonProxy.this.m_Anon.isConnected()) {
                                                try {
                                                    AnonProxy.this.m_containerMixCascade.keepCurrentService(true);
                                                    AnonProxy.this.start(AnonProxy.this.m_containerMixCascade);
                                                }
                                                catch (AnonServiceException anonServiceException) {
                                                    LogHolder.log(3, LogType.NET, "Switching service failed!", anonServiceException);
                                                }
                                            }
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                }
            };
            this.m_containerMixCascade.addObserver(this.m_observer);
            if (object != null) {
                if ((this.m_Anon.isConnected() || this.m_tInit != null) && this.m_Anon.getCurrentService().equals(object) && this.m_containerMixCascade.getTrustModel().isTrusted((MixCascade)object)) {
                    return;
                }
            } else {
                exceptionVariable.set(new NotRecoverableException(null, "Could not get cascade to connect. Next cascade is null!", -1));
                throw (AnonServiceException)exceptionVariable.get();
            }
            this.THREAD_SYNC.notifyAll();
            this.interruptInit(this.m_tInit);
            if (this.threadRunOne != null || this.threadRunTwo != null) {
                this.m_Anon.shutdown(false);
                if (this.threadRunTwo != null) {
                    while (this.threadRunTwo.isAlive()) {
                        try {
                            this.threadRunTwo.interrupt();
                            this.threadRunTwo.join(500L);
                        }
                        catch (InterruptedException interruptedException) {}
                    }
                    this.threadRunTwo = null;
                }
                if (this.threadRunOne != null) {
                    while (this.threadRunOne.isAlive()) {
                        try {
                            this.threadRunOne.interrupt();
                            this.threadRunOne.join(500L);
                        }
                        catch (InterruptedException interruptedException) {}
                    }
                    this.threadRunOne = null;
                }
            } else {
                this.m_Anon.shutdown(true);
            }
            LogHolder.log(5, LogType.NET, "Connecting to AN.ON service " + ((MixCascade)object).getName() + "...");
            this.m_numChannels = 0;
            this.fireCurrentServiceChanged((AnonServerDescription)object);
            thread = this.startInitThread(exceptionVariable, (MixCascade)object, false, false);
        }
        this.joinInitThread(thread, exceptionVariable);
        object2 = this.SHUTDOWN_SYNC;
        synchronized (object2) {
            this.finishInitThread(mixCascade, exceptionVariable, thread);
            object = this.SYNC_THREAD_RUN;
            synchronized (object) {
                if (this.threadRunOne != null || this.threadRunTwo != null) {
                    return;
                }
            }
            if (exceptionVariable.get() != null) {
                if (exceptionVariable.get() instanceof ServiceInterruptedException || !this.m_containerMixCascade.isReconnectedAutomatically() || !this.m_containerMixCascade.isServiceAutoSwitched() && exceptionVariable.get() instanceof AnonServiceException && exceptionVariable.get() instanceof INotRecoverableException && ((AnonServiceException)exceptionVariable.get()).getService() != AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) {
                    this.connectionError((AnonServiceException)exceptionVariable.get());
                    throw (AnonServiceException)exceptionVariable.get();
                }
                bl = true;
            } else {
                this.m_containerMixCascade.keepCurrentService(true);
                this.m_containerMixCascade.reset();
            }
            LogHolder.log(6, LogType.NET, "AN.ON initialized");
            if (this.m_currentTorParams != null) {
                this.m_Tor = AnonServiceFactory.getAnonServiceInstance("TOR");
                this.m_Tor.setProxy(this.m_proxyInterface);
                try {
                    this.m_Tor.initialize(this.m_currentTorParams, null, this.termsConfirmation, false);
                    LogHolder.log(7, LogType.NET, "Tor initialized");
                }
                catch (AnonServiceException anonServiceException) {
                    LogHolder.log(2, LogType.NET, anonServiceException);
                }
            }
            if (this.m_currentMixminionParams != null) {
                this.m_Mixminion = AnonServiceFactory.getAnonServiceInstance("Mixminion");
                this.m_Mixminion.setProxy(this.m_proxyInterface);
                try {
                    this.m_Mixminion.initialize(this.m_currentMixminionParams, null, this.termsConfirmation, false);
                }
                catch (AnonServiceException anonServiceException) {
                    LogHolder.log(2, LogType.NET, anonServiceException);
                }
                LogHolder.log(7, LogType.NET, "Mixminion initialized");
            }
            object = this.SYNC_THREAD_RUN;
            synchronized (object) {
                if (this.m_socketListenerTwo != null) {
                    this.threadRunTwo = new Thread((Runnable)new RunnableProxy(this.m_socketListenerTwo), "JAP - AnonProxy 2nd");
                    this.threadRunTwo.setDaemon(true);
                    this.threadRunTwo.start();
                }
                this.threadRunOne = new Thread((Runnable)new RunnableProxy(this.m_socketListener), "JAP - AnonProxy");
                this.threadRunOne.setDaemon(true);
                this.threadRunOne.start();
            }
        }
        if (bl) {
            if (this.m_containerMixCascade.isReconnectedAutomatically() && (this.m_containerMixCascade.isServiceAutoSwitched() || !(exceptionVariable.get() instanceof AnonServiceException) || !(exceptionVariable.get() instanceof INotRecoverableException) || ((AnonServiceException)exceptionVariable.get()).getService() == AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) && exceptionVariable.get() instanceof INotRecoverableException) {
                if (exceptionVariable.get() instanceof NotRecoverableXMLError) {
                    exceptionVariable.set(((NotRecoverableXMLError)exceptionVariable.get()).getSource());
                } else {
                    exceptionVariable.set(new RecoverableExceptionContainer((AnonServiceException)exceptionVariable.get()));
                }
            }
            this.connectionError((AnonServiceException)exceptionVariable.get());
            throw (AnonServiceException)exceptionVariable.get();
        }
    }

    protected synchronized void decNumChannels() {
        --this.m_numChannels;
        if (this.m_ProxyListener != null) {
            this.m_ProxyListener.channelsChanged(this.m_numChannels);
        }
    }

    protected synchronized void incNumChannels() {
        ++this.m_numChannels;
        if (this.m_ProxyListener != null) {
            this.m_ProxyListener.channelsChanged(this.m_numChannels);
        }
    }

    protected synchronized void transferredBytes(long l, int n) {
        if (this.m_ProxyListener != null) {
            this.m_ProxyListener.transferedBytes(l, n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireDisconnected() {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).disconnected();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireConnecting(AnonServerDescription anonServerDescription, boolean bl) {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).connecting(anonServerDescription, bl);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireCurrentServiceChanged(AnonServerDescription anonServerDescription) {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).currentServiceChanged(anonServerDescription);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireConnectionEstablished(AnonServerDescription anonServerDescription) {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            if (anonServerDescription instanceof MixCascade) {
                int n = -1;
                boolean bl = false;
                MixCascade mixCascade = (MixCascade)anonServerDescription;
                MixInfo mixInfo = mixCascade.getMixInfo(mixCascade.getNumberOfMixes() - 1);
                if (mixInfo != null && (n = mixInfo.getPremiumProbability()) >= 0) {
                    bl = true;
                } else {
                    PaymentInstanceDBEntry paymentInstanceDBEntry = null;
                    if (mixCascade.isPayment()) {
                        paymentInstanceDBEntry = mixCascade.getPaymentInstance();
                    }
                    if (paymentInstanceDBEntry == null || paymentInstanceDBEntry.isTest()) {
                        bl = true;
                    }
                }
                this.m_httpProxyCallback.resetRedirect(n, bl);
            }
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).connectionEstablished(anonServerDescription);
            }
        }
    }

    public void connecting(AnonServerDescription anonServerDescription, boolean bl) {
        LogHolder.log(4, LogType.NET, "AnonProxy is connecting to: " + anonServerDescription);
        this.fireConnecting(anonServerDescription, bl);
    }

    public void currentServiceChanged(AnonServerDescription anonServerDescription) {
        LogHolder.log(1, LogType.NET, "AnonProxy changed current service to '" + anonServerDescription + "'.");
        this.fireCurrentServiceChanged(anonServerDescription);
    }

    public void connectionEstablished(AnonServerDescription anonServerDescription) {
        LogHolder.log(1, LogType.NET, "AnonProxy received connectionEstablished to '" + anonServerDescription + "'.");
        this.fireConnectionEstablished(anonServerDescription);
    }

    public void disconnected() {
        LogHolder.log(1, LogType.NET, "AnonProxy was disconnected from service " + this.m_containerMixCascade.getCurrentCascade().getName() + ".");
        this.fireDisconnected();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connectionError(AnonServiceException anonServiceException) {
        LogHolder.log(anonServiceException != null && anonServiceException.getService() == AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE ? 7 : (anonServiceException != null && anonServiceException instanceof ServiceInterruptedException ? 4 : 3), LogType.NET, "AnonProxy received connectionError", anonServiceException, 1);
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).connectionError(anonServiceException);
            }
        }
        new Thread(new Runnable(){

            public void run() {
                AnonProxy.this.reconnect();
            }
        }, "Connection error reconnect thread").start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void addEventListener(AnonServiceEventListener anonServiceEventListener) {
        if (anonServiceEventListener == null) {
            LogHolder.log(4, LogType.NET, "Tried to add NULL listener to AnonProxy.");
            return;
        }
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                if (!anonServiceEventListener.equals(enumeration.nextElement())) continue;
                return;
            }
            this.m_anonServiceListener.addElement(anonServiceEventListener);
        }
    }

    public synchronized void removeEventListener(AnonServiceEventListener anonServiceEventListener) {
        this.m_anonServiceListener.removeElement(anonServiceEventListener);
    }

    public boolean isConnected() {
        return this.m_Anon.isConnected();
    }

    public boolean isConnecting() {
        return this.m_bReconnecting || this.m_bConnecting;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void packetMixed(long l) {
        if (this.isConnected() || l == 0L) {
            Vector vector = this.m_anonServiceListener;
            synchronized (vector) {
                Enumeration enumeration = this.m_anonServiceListener.elements();
                while (enumeration.hasMoreElements()) {
                    ((AnonServiceEventListener)enumeration.nextElement()).packetMixed(l);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dataChainErrorSignaled(AnonServiceException anonServiceException) {
        LogHolder.log(3, LogType.NET, anonServiceException);
        this.m_containerMixCascade.keepCurrentService(false);
        this.m_Anon.shutdown(false);
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).dataChainErrorSignaled(anonServiceException);
            }
        }
        this.reconnect();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void integrityErrorSignaled(AnonServiceException anonServiceException) {
        Vector vector = this.m_anonServiceListener;
        synchronized (vector) {
            Enumeration enumeration = this.m_anonServiceListener.elements();
            while (enumeration.hasMoreElements()) {
                ((AnonServiceEventListener)enumeration.nextElement()).integrityErrorSignaled(anonServiceException);
            }
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

    private class EncapsulatedMixCascadeContainer
    extends AbstractMixCascadeContainer {
        private AbstractMixCascadeContainer m_mixCascadeContainer;

        public void addObserver(Observer observer) {
            this.m_mixCascadeContainer.addObserver(observer);
        }

        public void deleteObserver(Observer observer) {
            this.m_mixCascadeContainer.deleteObserver(observer);
        }

        public void deleteObservers() {
            this.m_mixCascadeContainer.deleteObservers();
        }

        public EncapsulatedMixCascadeContainer(AbstractMixCascadeContainer abstractMixCascadeContainer) {
            this.m_mixCascadeContainer = abstractMixCascadeContainer;
        }

        public void reset() {
            this.m_mixCascadeContainer.reset();
        }

        public ITrustModel getTrustModel() {
            return this.m_mixCascadeContainer.getTrustModel();
        }

        public MixCascade getNextRandomCascade() {
            return this.m_mixCascadeContainer.getNextRandomCascade();
        }

        public MixCascade getNextCascade() {
            return this.m_mixCascadeContainer.getNextCascade();
        }

        public MixCascade getCurrentCascade() {
            return this.m_mixCascadeContainer.getCurrentCascade();
        }

        public void keepCurrentService(boolean bl) {
            this.m_mixCascadeContainer.keepCurrentService(bl);
        }

        public boolean isServiceAutoSwitched() {
            return this.m_mixCascadeContainer.isServiceAutoSwitched();
        }

        public boolean isReconnectedAutomatically() {
            return !AnonProxy.this.m_forwardedConnection && this.m_mixCascadeContainer.isReconnectedAutomatically();
        }
    }

    private class DummyMixCascadeContainer
    extends AbstractMixCascadeContainer {
        public MixCascade getNextCascade() {
            return null;
        }

        public MixCascade getNextRandomCascade() {
            return null;
        }

        public MixCascade getCurrentCascade() {
            return null;
        }

        public void keepCurrentService(boolean bl) {
        }

        public boolean isServiceAutoSwitched() {
            return false;
        }

        public boolean isReconnectedAutomatically() {
            return false;
        }

        public ITrustModel getTrustModel() {
            return new BasicTrustModel();
        }
    }

    private class RunnableProxy
    implements Runnable {
        private ServerSocket m_serverSocket;

        public RunnableProxy(ServerSocket serverSocket) {
            this.m_serverSocket = serverSocket;
        }

        public void run() {
            int n;
            block20: {
                n = 0;
                LogHolder.log(7, LogType.NET, "AnonProxy is running as Thread");
                try {
                    n = this.m_serverSocket.getSoTimeout();
                }
                catch (Exception exception) {
                    if (!AnonProxy.this.bShuttingDown) break block20;
                    return;
                }
            }
            try {
                this.m_serverSocket.setSoTimeout(2000);
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.NET, "Could not set accept time out!", exception);
            }
            if (AnonProxy.this.bShuttingDown) {
                try {
                    this.m_serverSocket.setSoTimeout(n);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return;
            }
            RoundRobinRequestQueue roundRobinRequestQueue = new RoundRobinRequestQueue();
            OpenSocketRequester openSocketRequester = new OpenSocketRequester(AnonProxy.this, AnonProxy.this.THREAD_SYNC, roundRobinRequestQueue);
            Thread thread = new Thread((Runnable)openSocketRequester, openSocketRequester.getClass().getName());
            thread.start();
            block16: while (true) {
                try {
                    while (!Thread.currentThread().isInterrupted() && !AnonProxy.this.bShuttingDown) {
                        if (!AnonProxy.this.isConnected()) {
                            Thread.sleep(250L);
                            continue;
                        }
                        SocketGuard socketGuard = null;
                        try {
                            socketGuard = new SocketGuard(this.m_serverSocket.accept(), AnonProxy.this.m_socketGuardTimeout);
                        }
                        catch (InterruptedIOException interruptedIOException) {
                            continue;
                        }
                        try {
                            socketGuard.setSoTimeout(0);
                            openSocketRequester.pushSocket(socketGuard);
                            continue block16;
                        }
                        catch (SocketException socketException) {
                            socketGuard = null;
                            LogHolder.log(3, LogType.NET, "Could not set non-Blocking mode for Channel-Socket!", socketException);
                        }
                    }
                    break;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.NET, exception);
                    break;
                }
            }
            try {
                this.m_serverSocket.setSoTimeout(n);
            }
            catch (Exception exception) {
                // empty catch block
            }
            thread.interrupt();
            openSocketRequester.close();
            try {
                thread.join();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            LogHolder.log(4, LogType.NET, "JAPAnonProxyServer stopped socket: " + this.m_serverSocket.toString());
        }
    }

    private class OpenSocketRequester
    implements Runnable {
        private ObjectQueue m_socketQueue = new ObjectQueue();
        private AnonProxy m_proxy;
        private Object m_syncObject;
        private boolean m_bIsClosed = false;
        private RoundRobinRequestQueue m_queue;

        public OpenSocketRequester(AnonProxy anonProxy2, Object object, RoundRobinRequestQueue roundRobinRequestQueue) {
            this.m_proxy = anonProxy2;
            this.m_syncObject = object;
            this.m_queue = roundRobinRequestQueue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void pushSocket(SocketGuard socketGuard) {
            ObjectQueue objectQueue = this.m_socketQueue;
            synchronized (objectQueue) {
                this.m_socketQueue.push(socketGuard);
                this.m_socketQueue.notify();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() {
            this.m_bIsClosed = true;
            ObjectQueue objectQueue = this.m_socketQueue;
            synchronized (objectQueue) {
                this.m_socketQueue.notify();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            while (!Thread.currentThread().isInterrupted() && !this.m_bIsClosed) {
                if (this.m_socketQueue.getSize() > 0 && AnonProxyRequest.getNrOfRequests() < AnonProxy.this.m_maxRequests) {
                    try {
                        new AnonProxyRequest(this.m_proxy, (SocketGuard)this.m_socketQueue.pop(), this.m_syncObject, AnonProxy.this.m_callbackHandler, this.m_queue);
                    }
                    catch (Exception exception) {
                        LogHolder.log(3, LogType.NET, exception);
                    }
                    continue;
                }
                try {
                    ObjectQueue objectQueue = this.m_socketQueue;
                    synchronized (objectQueue) {
                        if (AnonProxyRequest.getNrOfRequests() >= AnonProxy.this.m_maxRequests) {
                            this.m_socketQueue.wait(100L);
                        } else {
                            this.m_socketQueue.wait();
                        }
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                    break;
                }
            }
            LogHolder.log(6, LogType.NET, "Open socket thread stopped.");
        }
    }

    public class RoundRobinRequestQueue {
        private final Vector vecPriorityRequests = new Vector();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addPriority(AnonProxyRequest anonProxyRequest) {
            Vector vector = this.vecPriorityRequests;
            synchronized (vector) {
                if (!this.vecPriorityRequests.contains(anonProxyRequest)) {
                    this.vecPriorityRequests.addElement(anonProxyRequest);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removePriority(AnonProxyRequest anonProxyRequest) {
            Vector vector = this.vecPriorityRequests;
            synchronized (vector) {
                this.vecPriorityRequests.removeElement(anonProxyRequest);
            }
        }

        public boolean isSlowDownUploads() {
            return this.vecPriorityRequests.size() > 0;
        }

        public boolean isBlockUploads() {
            return AnonProxy.this.m_Anon.isSendingControlMessage();
        }
    }
}

