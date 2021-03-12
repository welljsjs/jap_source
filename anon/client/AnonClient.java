/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import HTTPClient.HTTPConnection;
import HTTPClient.ThreadInterruptedIOException;
import anon.AnonChannel;
import anon.AnonServerDescription;
import anon.AnonService;
import anon.AnonServiceEventListener;
import anon.IServiceContainer;
import anon.NotConnectedToMixException;
import anon.client.DataChainErrorListener;
import anon.client.DummyTrafficControlChannel;
import anon.client.FixedRatioChannelsDescription;
import anon.client.IntegrityErrorListener;
import anon.client.KeyExchangeManager;
import anon.client.MixPacket;
import anon.client.MixParameters;
import anon.client.Multiplexer;
import anon.client.PacketCounter;
import anon.client.SequentialChannelDataChain;
import anon.client.SingleChannelDataChain;
import anon.client.SocketHandler;
import anon.client.TestControlChannel;
import anon.client.TrustModel;
import anon.client.TypeFilterDataChain;
import anon.client.replay.ReplayControlChannel;
import anon.client.replay.TimestampUpdater;
import anon.error.AlreadyConnectedException;
import anon.error.AnonServiceException;
import anon.error.ConnectionEstablishmentTimeoutException;
import anon.error.IntegrityCheckException;
import anon.error.InvalidServiceException;
import anon.error.ParseServiceException;
import anon.error.ServiceInterruptedException;
import anon.infoservice.Database;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.infoservice.RandomListenerInterfaceSwitcher;
import anon.pay.AIControlChannel;
import anon.pay.IAIEventListener;
import anon.pay.PayAccount;
import anon.proxy.DirectProxy;
import anon.terms.TermsAndConditionConfirmation;
import anon.terms.TermsAndConditionsReadException;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IStreamConnection;
import anon.transport.connection.SocketConnection;
import anon.util.JobQueue;
import anon.util.XMLParseException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class AnonClient
implements AnonService,
Observer,
DataChainErrorListener,
IntegrityErrorListener {
    private static boolean ENABLE_CONTROL_CHANNEL_TEST = false;
    private static boolean ms_bIsTestInstance = false;
    private static boolean ms_bIsTestInstanceSet = false;
    public static final int DEFAULT_LOGIN_TIMEOUT = 30000;
    private static final int FAST_LOGIN_TIMEOUT = 4000;
    private static final int CONNECT_TIMEOUT = 8000;
    public static final long DEFAULT_BLOCK_TIMEOUT = 180000L;
    private long m_msBlockTimeout = 180000L;
    private static int m_loginTimeout = 30000;
    private static int m_loginTimeoutFastAvailable;
    private static boolean ms_bBlockOnHttpError;
    private static int ms_preferredConnectionPort;
    private static Vector ms_vecTimedOutPorts;
    private Multiplexer m_multiplexer;
    private static JobQueue ms_queuePacketCount;
    private IMutableProxyInterface m_proxyInterface;
    private Object m_internalSynchronization;
    private IServiceContainer m_serviceContainer;
    private Thread m_threadInitialise;
    private Object SYNC_SHUTDOWN = new Object();
    private Object m_internalSynchronizationForSocket;
    private Object m_internalSynchronizationForDummyTraffic;
    private SocketHandler m_socketHandler;
    private Vector m_eventListeners;
    private PacketCounter m_packetCounter;
    private DummyTrafficControlChannel m_dummyTrafficControlChannel;
    private int m_dummyTrafficInterval = 30000;
    private KeyExchangeManager m_keyExchangeManager;
    private IStreamConnection m_streamConnection;
    private boolean m_connected;
    private MixCascade m_currentService;
    private DirectProxy m_directProxy;
    private boolean m_bDebug = false;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    public AnonClient(DirectProxy directProxy) {
        this.m_directProxy = directProxy;
        this.m_socketHandler = null;
        this.m_multiplexer = null;
        this.m_packetCounter = null;
        this.m_dummyTrafficControlChannel = null;
        this.m_dummyTrafficInterval = -1;
        this.m_keyExchangeManager = null;
        this.m_streamConnection = null;
        this.m_internalSynchronization = new Object();
        this.m_internalSynchronizationForSocket = new Object();
        this.m_internalSynchronizationForDummyTraffic = new Object();
        this.m_eventListeners = new Vector();
        this.m_connected = false;
        this.m_proxyInterface = new IMutableProxyInterface.DummyMutableProxyInterface();
        this.m_bDebug = false;
    }

    public AnonClient(DirectProxy directProxy, IStreamConnection iStreamConnection, MixCascade mixCascade) {
        this(directProxy);
        this.m_streamConnection = iStreamConnection;
        this.m_currentService = mixCascade;
        TrustModel.cleanAttributeWhitelist(this.m_currentService);
    }

    public void setDebug(boolean bl) {
        this.m_bDebug = bl;
    }

    public static void setTestInstance(boolean bl) {
        if (!ms_bIsTestInstanceSet) {
            ms_bIsTestInstance = bl;
            ms_bIsTestInstanceSet = true;
        }
    }

    public static boolean isTestInstance() {
        return ms_bIsTestInstance;
    }

    public MixCascade getCurrentService() {
        return this.m_currentService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void initialize(final AnonServerDescription anonServerDescription, final IServiceContainer iServiceContainer, final TermsAndConditionConfirmation termsAndConditionConfirmation, final boolean bl) throws AnonServiceException {
        if (!(anonServerDescription instanceof MixCascade)) {
            throw new InvalidServiceException(anonServerDescription);
        }
        if (this.isConnected()) {
            throw new AlreadyConnectedException(anonServerDescription);
        }
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.m_currentService = (MixCascade)anonServerDescription;
            TrustModel.cleanAttributeWhitelist(this.m_currentService);
            this.m_serviceContainer = iServiceContainer;
        }
        object = new StatusThread(){
            AnonServiceException status = null;

            public AnonServiceException getStatus() {
                return this.status;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Object object = AnonClient.this.m_internalSynchronization;
                synchronized (object) {
                    if (AnonClient.this.isConnected()) {
                        LogHolder.log(3, LogType.NET, "AnonClient was already connected when connecting!");
                        this.status = new AlreadyConnectedException(anonServerDescription);
                        Thread thread = AnonClient.this.m_threadInitialise;
                        synchronized (thread) {
                            AnonClient.this.m_threadInitialise.notifyAll();
                        }
                        return;
                    }
                    IStreamConnection iStreamConnection = null;
                    if (AnonClient.this.m_streamConnection != null) {
                        iStreamConnection = AnonClient.this.m_streamConnection;
                        AnonClient.this.m_streamConnection = null;
                    } else {
                        try {
                            iServiceContainer.getTrustModel().checkTrust((MixCascade)anonServerDescription);
                            iStreamConnection = AnonClient.this.connectMixCascade((MixCascade)anonServerDescription, AnonClient.this.m_proxyInterface.getProxyInterface(false).getProxyInterface(), iServiceContainer, bl);
                        }
                        catch (InterruptedException interruptedException) {
                            this.status = new ServiceInterruptedException(anonServerDescription);
                            Thread thread = AnonClient.this.m_threadInitialise;
                            synchronized (thread) {
                                AnonClient.this.m_threadInitialise.notifyAll();
                            }
                            return;
                        }
                        catch (AnonServiceException anonServiceException) {
                            this.status = anonServiceException;
                            Thread thread = AnonClient.this.m_threadInitialise;
                            synchronized (thread) {
                                AnonClient.this.m_threadInitialise.notifyAll();
                            }
                            return;
                        }
                    }
                    if (iStreamConnection == null) {
                        this.status = new AnonServiceException(anonServerDescription, "Could not connect to service " + anonServerDescription + "  for an unknown reason.", -6);
                        Thread thread = AnonClient.this.m_threadInitialise;
                        synchronized (thread) {
                            AnonClient.this.m_threadInitialise.notifyAll();
                        }
                        return;
                    }
                    try {
                        AnonClient.this.initializeProtocol(iStreamConnection, (MixCascade)anonServerDescription, iServiceContainer, termsAndConditionConfirmation);
                    }
                    catch (AnonServiceException anonServiceException) {
                        this.status = anonServiceException;
                    }
                    Thread thread = AnonClient.this.m_threadInitialise;
                    synchronized (thread) {
                        AnonClient.this.m_threadInitialise.notifyAll();
                    }
                }
            }
        };
        Object object2 = this.SYNC_SHUTDOWN;
        synchronized (object2) {
            this.m_threadInitialise = new Thread((Runnable)object);
        }
        this.m_threadInitialise.start();
        try {
            this.m_threadInitialise.join();
        }
        catch (InterruptedException interruptedException) {
            Thread thread = this.m_threadInitialise;
            synchronized (thread) {
                while (this.m_threadInitialise.isAlive()) {
                    this.m_threadInitialise.interrupt();
                    try {
                        this.m_threadInitialise.wait(500L);
                    }
                    catch (InterruptedException interruptedException2) {}
                }
            }
            throw new ServiceInterruptedException(anonServerDescription);
        }
        if (object.getStatus() != null) {
            throw object.getStatus();
        }
    }

    public static void setLoginTimeout(int n) {
        if (n >= 1000) {
            m_loginTimeout = n;
        }
    }

    public static void setBlockOnHttpConnectionError(boolean bl) {
        ms_bBlockOnHttpError = bl;
    }

    public static boolean isBlockedOnHttpConnectionError() {
        return ms_bBlockOnHttpError;
    }

    private static void resetInternalLoginTimeout() {
        int n = 30;
        m_loginTimeoutFastAvailable = Math.max(m_loginTimeout / 1000, m_loginTimeout / 4000);
        if (m_loginTimeoutFastAvailable > n) {
            m_loginTimeoutFastAvailable = n;
        }
    }

    private static int getInternalLoginTimeout(IServiceContainer iServiceContainer) {
        if (iServiceContainer != null && m_loginTimeoutFastAvailable > 0 && iServiceContainer.isReconnectedAutomatically() && iServiceContainer.isServiceAutoSwitched()) {
            --m_loginTimeoutFastAvailable;
            return 4000;
        }
        return m_loginTimeout;
    }

    public static int getLoginTimeout() {
        return m_loginTimeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int setProxy(IMutableProxyInterface iMutableProxyInterface) {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.m_proxyInterface = iMutableProxyInterface == null ? new IMutableProxyInterface.DummyMutableProxyInterface() : iMutableProxyInterface;
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown(boolean bl) {
        Object object;
        Object object2 = this.m_internalSynchronizationForSocket;
        synchronized (object2) {
            if (this.m_socketHandler != null) {
                this.m_socketHandler.deleteObservers();
            }
        }
        object2 = this.m_internalSynchronization;
        synchronized (object2) {
            if (this.m_multiplexer != null) {
                this.m_multiplexer.close();
            }
        }
        object2 = this.m_internalSynchronizationForSocket;
        synchronized (object2) {
            if (this.m_socketHandler != null) {
                this.m_socketHandler.closeSocket();
                this.m_socketHandler = null;
            }
        }
        object2 = this.SYNC_SHUTDOWN;
        synchronized (object2) {
            if (this.m_threadInitialise != null) {
                object = this.m_threadInitialise;
                synchronized (object) {
                    while (this.m_threadInitialise.isAlive()) {
                        this.m_threadInitialise.interrupt();
                        try {
                            this.m_threadInitialise.wait(100L);
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                            break;
                        }
                    }
                }
            }
        }
        object2 = this.m_internalSynchronization;
        synchronized (object2) {
            if (this.m_multiplexer != null) {
                this.m_multiplexer.deleteObservers();
            }
            this.m_multiplexer = null;
            this.m_connected = false;
            object = this.m_internalSynchronizationForDummyTraffic;
            synchronized (object) {
                if (this.m_dummyTrafficControlChannel != null) {
                    this.m_dummyTrafficControlChannel.stop();
                    this.m_dummyTrafficControlChannel = null;
                }
            }
            if (this.m_packetCounter != null) {
                this.m_packetCounter.deleteObserver(this);
                if (bl) {
                    this.m_packetCounter = null;
                }
            }
            if (this.m_keyExchangeManager != null) {
                this.m_keyExchangeManager.removeCertificateLock();
                this.m_keyExchangeManager = null;
            }
            if (this.m_directProxy != null) {
                this.m_directProxy.start();
            }
        }
    }

    public boolean isConnected() {
        return this.m_connected;
    }

    public boolean isSendingControlMessage() {
        Multiplexer multiplexer = this.m_multiplexer;
        if (multiplexer == null) {
            return false;
        }
        return multiplexer.isSendingControlMessage();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AnonChannel createChannel(int n) throws ConnectException {
        Multiplexer multiplexer = null;
        KeyExchangeManager keyExchangeManager = null;
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (this.m_multiplexer == null) {
                throw new NotConnectedToMixException("AnonClient: createChannel(): The AN.ON client is currently not connected to a mixcascade.");
            }
            multiplexer = this.m_multiplexer;
            keyExchangeManager = this.m_keyExchangeManager;
        }
        object = keyExchangeManager.getFixedRatioChannelsDescription();
        if (object == null) {
            return new SingleChannelDataChain(multiplexer.getChannelTable(), this, this, n, keyExchangeManager.isChainProtocolWithFlowControl(), keyExchangeManager.isChainProtocolWithUpstreamFlowControl(), keyExchangeManager.getUpstreamSendMe(), keyExchangeManager.getDownstreamSendMe(), keyExchangeManager.isProtocolWithEnhancedChannelEncryption(), keyExchangeManager.isProtocolWithIntegrityCheck());
        }
        return new TypeFilterDataChain(new SequentialChannelDataChain(multiplexer.getChannelTable(), this, this, ((FixedRatioChannelsDescription)object).getChainTimeout()), n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEventListener(AnonServiceEventListener anonServiceEventListener) {
        Vector vector = this.m_eventListeners;
        synchronized (vector) {
            this.m_eventListeners.addElement(anonServiceEventListener);
        }
    }

    public void removeEventListeners() {
        this.m_eventListeners.removeAllElements();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeEventListener(AnonServiceEventListener anonServiceEventListener) {
        Vector vector = this.m_eventListeners;
        synchronized (vector) {
            this.m_eventListeners.removeElement(anonServiceEventListener);
        }
    }

    private void reconnect(final MixCascade mixCascade, final IOException iOException) {
        new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                AnonClient.this.shutdown(!AnonClient.this.m_serviceContainer.isReconnectedAutomatically());
                Vector vector = AnonClient.this.m_eventListeners;
                synchronized (vector) {
                    final Enumeration enumeration = AnonClient.this.m_eventListeners.elements();
                    Thread thread = new Thread(new Runnable(){

                        public void run() {
                            if (iOException != null) {
                                LogHolder.log(4, LogType.NET, iOException);
                            }
                            while (enumeration.hasMoreElements()) {
                                ((AnonServiceEventListener)enumeration.nextElement()).connectionError(new AnonServiceException(mixCascade, "Reconnect..."));
                            }
                        }
                    }, "ConnectionError notification");
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        }).start();
    }

    public void update(Observable observable, Object object) {
        if (observable == this.m_socketHandler && object instanceof IOException) {
            this.reconnect(this.getCurrentService(), (IOException)object);
        } else if (observable == this.m_packetCounter) {
            JobQueue.Job job = new JobQueue.Job(true){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void runJob() {
                    Vector vector = AnonClient.this.m_eventListeners;
                    PacketCounter packetCounter = AnonClient.this.m_packetCounter;
                    if (vector != null && packetCounter != null) {
                        Vector vector2 = vector;
                        synchronized (vector2) {
                            Enumeration enumeration = vector.elements();
                            while (enumeration.hasMoreElements()) {
                                ((AnonServiceEventListener)enumeration.nextElement()).packetMixed(packetCounter.getProcessedPackets() * (long)MixPacket.getPacketSize());
                            }
                        }
                    }
                }
            };
            ms_queuePacketCount.addJob(job);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dataChainErrorSignaled(AnonServiceException anonServiceException) {
        AnonServiceException anonServiceException2 = anonServiceException;
        if (anonServiceException2 == null) {
            anonServiceException2 = new AnonServiceException(this.getCurrentService(), "Proxy at the last mix was nuked!");
        }
        final AnonServiceException anonServiceException3 = anonServiceException2;
        Vector vector = this.m_eventListeners;
        synchronized (vector) {
            final Enumeration enumeration = this.m_eventListeners.elements();
            Thread thread = new Thread(new Runnable(){

                public void run() {
                    while (enumeration.hasMoreElements()) {
                        ((AnonServiceEventListener)enumeration.nextElement()).dataChainErrorSignaled(anonServiceException3);
                    }
                }
            }, "AnonClient: DataChainErrorSignaled notification");
            thread.setDaemon(true);
            thread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void integrityErrorSignaled(final int n) {
        Vector vector = this.m_eventListeners;
        synchronized (vector) {
            final Enumeration enumeration = this.m_eventListeners.elements();
            Thread thread = new Thread(new Runnable(){

                public void run() {
                    while (enumeration.hasMoreElements()) {
                        ((AnonServiceEventListener)enumeration.nextElement()).integrityErrorSignaled(new IntegrityCheckException((AnonServerDescription)AnonClient.this.getCurrentService(), n));
                    }
                }
            }, "AnonClient: integrityErrorSignaled notification");
            thread.setDaemon(true);
            thread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDummyTraffic(int n) {
        Object object = this.m_internalSynchronizationForDummyTraffic;
        synchronized (object) {
            this.m_dummyTrafficInterval = n;
            if (this.m_dummyTrafficControlChannel != null) {
                this.m_dummyTrafficControlChannel.setDummyTrafficInterval(n);
            }
        }
    }

    public void setInterfaceBlockTimout(long l) {
        this.m_msBlockTimeout = l;
    }

    public SocketHandler getConnectionToMixCascade() {
        return this.m_socketHandler;
    }

    private IStreamConnection connectMixCascade(final MixCascade mixCascade, ImmutableProxyInterface immutableProxyInterface, IServiceContainer iServiceContainer, final boolean bl) throws InterruptedException, AnonServiceException {
        ListenerInterface listenerInterface;
        LogHolder.log(7, LogType.NET, "Trying to connect to MixCascade '" + mixCascade.toString() + "'...");
        Thread thread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Vector vector = AnonClient.this.m_eventListeners;
                synchronized (vector) {
                    Enumeration enumeration = AnonClient.this.m_eventListeners.elements();
                    while (enumeration.hasMoreElements()) {
                        ((AnonServiceEventListener)enumeration.nextElement()).connecting(mixCascade, bl);
                    }
                }
            }
        }, "AnonClient: Connecting notification");
        thread.setDaemon(true);
        thread.start();
        Socket socket = null;
        RandomListenerInterfaceSwitcher randomListenerInterfaceSwitcher = new RandomListenerInterfaceSwitcher(mixCascade.getListenerInterfaces(), ms_preferredConnectionPort, 2, ms_vecTimedOutPorts);
        boolean bl2 = false;
        while ((listenerInterface = randomListenerInterfaceSwitcher.getNextInterface()) != null && listenerInterface.isValid() && socket == null && !Thread.currentThread().isInterrupted()) {
            iServiceContainer.getTrustModel().checkTrust(mixCascade);
            bl2 = true;
            LogHolder.log(6, LogType.NET, "Trying cascade connection (Preferred port: " + ms_preferredConnectionPort + ") to interface: " + listenerInterface);
            try {
                HTTPConnection hTTPConnection = HTTPConnectionFactory.getInstance().createHTTPConnection(listenerInterface, immutableProxyInterface);
                hTTPConnection.setTimeout(8000);
                socket = hTTPConnection.Connect();
            }
            catch (Exception exception) {
                if (exception instanceof ThreadInterruptedIOException) {
                    LogHolder.log(5, LogType.NET, "Interrupted while connecting to MixCascade '" + mixCascade.toString() + "'.");
                    throw new InterruptedException();
                }
                int n = 3;
                try {
                    if (mixCascade.getListenerInterface(0).getHost().equals("0.0.0.0")) {
                        n = 6;
                    }
                }
                catch (Exception exception2) {
                    // empty catch block
                }
                if (ms_bBlockOnHttpError || ListenerInterface.isBlockingRecommended(exception)) {
                    listenerInterface.blockInterface(this.m_msBlockTimeout);
                }
                LogHolder.log(n, LogType.NET, "Error while connecting to MixCascade " + mixCascade.toString() + " via " + listenerInterface.toString() + "!", exception);
            }
        }
        if (socket != null) {
            if (ms_preferredConnectionPort != socket.getPort()) {
                ms_preferredConnectionPort = socket.getPort();
                LogHolder.log(5, LogType.NET, "New preferred cascade connection port is: " + ms_preferredConnectionPort);
            }
            LogHolder.log(7, LogType.NET, "Connection to MixCascade '" + mixCascade.toString() + "' successfully established - starting key-exchange...");
            return new SocketConnection(socket);
        }
        int n = 3;
        try {
            if (mixCascade.getListenerInterface(0).getHost().equals("0.0.0.0")) {
                n = 6;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        String string = "";
        if (!bl2) {
            string = " No listener interfaces available!";
        }
        LogHolder.log(n, LogType.NET, "Failed to connect to MixCascade '" + mixCascade.toString() + "'." + string);
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializeProtocol(IStreamConnection iStreamConnection, final MixCascade mixCascade, final IServiceContainer iServiceContainer, final TermsAndConditionConfirmation termsAndConditionConfirmation) throws AnonServiceException {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            Object object2;
            try {
                try {
                    iStreamConnection.setTimeout(AnonClient.getInternalLoginTimeout(iServiceContainer));
                }
                catch (ConnectionException connectionException) {
                    // empty catch block
                }
                object2 = this.m_internalSynchronizationForSocket;
                synchronized (object2) {
                    if (this.m_socketHandler != null) {
                        this.m_socketHandler.deleteObservers();
                    }
                    this.m_socketHandler = new SocketHandler(iStreamConnection);
                }
                object2 = new Vector();
                Thread thread = new Thread(new Runnable((Vector)object2){
                    private final /* synthetic */ Vector val$exceptionCache;
                    {
                        this.val$exceptionCache = vector;
                    }

                    public void run() {
                        boolean bl = true;
                        int n = 0;
                        try {
                            while (bl) {
                                try {
                                    AnonClient.this.m_keyExchangeManager = new KeyExchangeManager(AnonClient.this.m_socketHandler.getInputStream(), AnonClient.this.m_socketHandler.getOutputStream(), mixCascade, iServiceContainer.getTrustModel(), AnonClient.this.m_bDebug);
                                    bl = false;
                                }
                                catch (TermsAndConditionsReadException termsAndConditionsReadException) {
                                    if (!termsAndConditionConfirmation.confirmTermsAndConditions(termsAndConditionsReadException.getOperators(), termsAndConditionsReadException.getTermsTermsAndConditonsToRead())) {
                                        iServiceContainer.keepCurrentService(false);
                                        throw new InterruptedException("Client rejected T&C after reading.");
                                    }
                                    if (++n > 1) {
                                        LogHolder.log(3, LogType.NET, "Requesting t&cs after the first try is not allowed!");
                                        throw new InterruptedException("A second tc request must never be sent.");
                                    }
                                    AnonClient.this.m_socketHandler = new SocketHandler(AnonClient.this.connectMixCascade(mixCascade, AnonClient.this.m_proxyInterface.getProxyInterface(false).getProxyInterface(), iServiceContainer, true));
                                }
                            }
                        }
                        catch (Exception exception) {
                            this.val$exceptionCache.addElement(exception);
                        }
                    }
                }, "Login Thread");
                thread.start();
                thread.join();
                if (((Vector)object2).size() > 0) {
                    throw (Exception)((Vector)object2).firstElement();
                }
            }
            catch (AnonServiceException anonServiceException) {
                LogHolder.log(3, LogType.NET, anonServiceException);
                this.closeSocketHandler();
                throw anonServiceException;
            }
            catch (InterruptedException interruptedException) {
                LogHolder.log(6, LogType.NET, interruptedException);
                this.closeSocketHandler();
                throw new ServiceInterruptedException(mixCascade);
            }
            catch (XMLParseException xMLParseException) {
                LogHolder.log(3, LogType.NET, xMLParseException);
                this.closeSocketHandler();
                throw new ParseServiceException(mixCascade, xMLParseException.getMessage());
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.NET, exception);
                this.closeSocketHandler();
                Class<?> class_ = null;
                try {
                    class_ = Class.forName("java.net.SocketTimeoutException");
                }
                catch (ClassNotFoundException classNotFoundException) {
                    LogHolder.log(6, LogType.NET, "Old JRE does not have SocketTimeoutException. Please update your Java ASAP!");
                }
                if (class_ != null && class_.isInstance(exception)) {
                    if (ms_preferredConnectionPort > 0) {
                        ms_vecTimedOutPorts.addElement(new Integer(ms_preferredConnectionPort));
                    }
                    ms_preferredConnectionPort = 0;
                    throw new ConnectionEstablishmentTimeoutException(mixCascade);
                }
                throw new AnonServiceException(mixCascade, exception.getMessage());
            }
            try {
                iStreamConnection.setTimeout(0);
            }
            catch (ConnectionException connectionException) {
                // empty catch block
            }
            this.m_multiplexer = new Multiplexer(this.m_socketHandler.getInputStream(), this.m_socketHandler.getOutputStream(), this.m_keyExchangeManager, new SecureRandom());
            this.m_socketHandler.addObserver(this);
            this.m_packetCounter = this.m_packetCounter != null ? new PacketCounter(this.m_packetCounter.getProcessedPackets()) : new PacketCounter();
            this.m_multiplexer.addObserver(this.m_packetCounter);
            this.m_packetCounter.addObserver(this);
            object2 = this.m_internalSynchronizationForDummyTraffic;
            synchronized (object2) {
                this.m_dummyTrafficControlChannel = new DummyTrafficControlChannel(this.m_multiplexer, iServiceContainer);
                this.m_dummyTrafficControlChannel.setDummyTrafficInterval(this.m_dummyTrafficInterval);
            }
            if (ENABLE_CONTROL_CHANNEL_TEST) {
                object2 = new TestControlChannel(this.m_multiplexer, iServiceContainer);
                ((TestControlChannel)object2).setMessageInterval(30000);
            }
            try {
                this.finishInitialization(this.m_multiplexer, this.m_keyExchangeManager, this.m_packetCounter, iServiceContainer, this.m_keyExchangeManager.getConnectedCascade());
            }
            catch (AnonServiceException anonServiceException) {
                this.shutdown(!iServiceContainer.isReconnectedAutomatically());
                throw anonServiceException;
            }
            this.m_currentService = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AnonClient.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_keyExchangeManager.getConnectedCascade().getId()) == null || !mixCascade.getId().equals(this.m_keyExchangeManager.getConnectedCascade().getId()) ? this.m_keyExchangeManager.getConnectedCascade() : mixCascade;
            TrustModel.cleanAttributeWhitelist(this.m_currentService);
            if (this.m_directProxy != null) {
                this.m_directProxy.stop();
            }
            this.connectionEstablished(this.m_currentService);
        }
    }

    private void connectionEstablished(final AnonServerDescription anonServerDescription) {
        AnonClient.resetInternalLoginTimeout();
        this.m_connected = true;
        TrustModel.cleanAttributeWhitelist(null);
        Thread thread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Vector vector = AnonClient.this.m_eventListeners;
                synchronized (vector) {
                    Enumeration enumeration = AnonClient.this.m_eventListeners.elements();
                    while (enumeration.hasMoreElements()) {
                        ((AnonServiceEventListener)enumeration.nextElement()).connectionEstablished(anonServerDescription);
                    }
                }
            }
        }, "AnonClient: ConnectionEstablished notification");
        thread.setDaemon(true);
        thread.start();
        LogHolder.log(6, LogType.NET, "Connected to MixCascade '" + anonServerDescription.toString() + "'!");
    }

    private void finishInitialization(Multiplexer multiplexer, KeyExchangeManager keyExchangeManager, PacketCounter packetCounter, IServiceContainer iServiceContainer, MixCascade mixCascade) throws AnonServiceException {
        Object object;
        if (keyExchangeManager.isProtocolWithTimestamp()) {
            object = keyExchangeManager.getMixParameters();
            if (keyExchangeManager.getFirstMixSymmetricCipher() != null) {
                object = new MixParameters[keyExchangeManager.getMixParameters().length - 1];
                for (int i = 0; i < keyExchangeManager.getMixParameters().length - 1; ++i) {
                    object[i] = keyExchangeManager.getMixParameters()[i + 1];
                }
            }
            try {
                new TimestampUpdater((MixParameters[])object, new ReplayControlChannel(multiplexer, iServiceContainer));
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.NET, "Fetching of timestamps failed - closing connection.", exception);
                throw new AnonServiceException(mixCascade, "Fetching of timestamps failed - closing connection.");
            }
        }
        if (keyExchangeManager.isPaymentRequired()) {
            object = new AIControlChannel(multiplexer, packetCounter, iServiceContainer, mixCascade);
            ((AIControlChannel)object).addAIListener(new IAIEventListener(){

                public void accountEmpty(PayAccount payAccount, MixCascade mixCascade) {
                    LogHolder.log(4, LogType.PAY, "Account empty: " + payAccount + " Reconnect!");
                    AnonClient.this.reconnect(mixCascade, null);
                }

                public void accountChanged(PayAccount payAccount, MixCascade mixCascade) {
                    LogHolder.log(4, LogType.PAY, "Account changed: " + payAccount + " Reconnect!");
                    AnonClient.this.reconnect(mixCascade, null);
                }
            });
            ((AIControlChannel)object).setAILoginTimeout(m_loginTimeout);
            ((AIControlChannel)object).sendAccountCert();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closeSocketHandler() {
        Object object = this.m_internalSynchronizationForSocket;
        synchronized (object) {
            if (this.m_socketHandler != null) {
                this.m_socketHandler.closeSocket();
                this.m_socketHandler = null;
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

    static {
        ms_preferredConnectionPort = 0;
        ms_vecTimedOutPorts = new Vector();
        ms_queuePacketCount = new JobQueue("AnonClient Packet count updater");
        AnonClient.resetInternalLoginTimeout();
    }

    private static interface StatusThread
    extends Runnable {
        public AnonServiceException getStatus();
    }
}

