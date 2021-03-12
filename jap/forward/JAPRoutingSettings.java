/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.ForwardUtils;
import anon.forward.LocalForwarder;
import anon.forward.client.ClientForwardException;
import anon.forward.client.DefaultClientProtocolHandler;
import anon.forward.client.ForwardConnectionDescriptor;
import anon.forward.client.ProgressCounter;
import anon.forward.server.ForwardSchedulerStatistics;
import anon.forward.server.ForwardServerManager;
import anon.forward.server.ServerSocketPropagandist;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.MixCascade;
import anon.infoservice.ProxyInterface;
import anon.proxy.AnonProxy;
import anon.proxy.DirectProxy;
import anon.terms.TermsAndConditionConfirmation;
import anon.transport.address.IAddress;
import anon.transport.address.TcpIpAddress;
import anon.transport.connection.IStreamConnection;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import jap.JAPController;
import jap.JAPModel;
import jap.forward.JAPRoutingConnectionClass;
import jap.forward.JAPRoutingConnectionClassSelector;
import jap.forward.JAPRoutingForwardingModeSelector;
import jap.forward.JAPRoutingMessage;
import jap.forward.JAPRoutingRegistrationInfoServices;
import jap.forward.JAPRoutingRegistrationStatusObserver;
import jap.forward.JAPRoutingServerStatisticsListener;
import jap.forward.JAPRoutingSettingsPropagandaThreadLock;
import jap.forward.JAPRoutingUseableMixCascades;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class JAPRoutingSettings
extends Observable
implements IXMLEncodable {
    public static final int ROUTING_MODE_DISABLED = 0;
    public static final int ROUTING_MODE_CLIENT = 1;
    public static final int ROUTING_MODE_SERVER = 2;
    public static final int REGISTRATION_NO_INFOSERVICES = 1;
    public static final int REGISTRATION_UNKNOWN_ERRORS = 2;
    public static final int REGISTRATION_INFOSERVICE_ERRORS = 3;
    public static final int REGISTRATION_VERIFY_ERRORS = 4;
    public static final int REGISTRATION_INTERRUPTED = 5;
    public static final int REGISTRATION_SUCCESS = 0;
    private int m_routingMode = 0;
    private Object m_currentServerManagerId;
    private int m_bandwidth;
    private int m_connections;
    private IStreamConnection m_forwardedConnection;
    private boolean m_forwardInfoService;
    private boolean m_connectViaForwarder;
    private boolean m_waitForShutdownCall;
    private DefaultClientProtocolHandler m_protocolHandler;
    private int m_maxDummyTrafficInterval;
    private Vector m_runningPropagandists;
    private Thread m_startPropagandaThread;
    private static JAPRoutingForwardingModeSelector m_forwardingModeSelector;
    private JAPRoutingConnectionClassSelector m_connectionClassSelector;
    private JAPRoutingRegistrationInfoServices m_registrationInfoServicesStore;
    private boolean m_propagandaStarted;
    private JAPRoutingUseableMixCascades m_useableMixCascadesStore;
    private JAPRoutingServerStatisticsListener m_serverStatisticsListener;
    private JAPRoutingRegistrationStatusObserver m_registrationStatusObserver;

    public JAPRoutingSettings() {
        m_forwardingModeSelector = new JAPRoutingForwardingModeSelector();
        this.m_connectionClassSelector = new JAPRoutingConnectionClassSelector();
        JAPRoutingConnectionClass jAPRoutingConnectionClass = this.m_connectionClassSelector.getCurrentConnectionClass();
        this.m_bandwidth = jAPRoutingConnectionClass.getCurrentBandwidth();
        this.m_connections = this.m_bandwidth / 4000;
        this.m_forwardedConnection = null;
        this.m_forwardInfoService = false;
        this.m_connectViaForwarder = false;
        this.m_waitForShutdownCall = false;
        this.m_protocolHandler = null;
        this.m_maxDummyTrafficInterval = -1;
        this.m_runningPropagandists = new Vector();
        this.m_startPropagandaThread = null;
        this.m_propagandaStarted = false;
        this.m_currentServerManagerId = null;
        this.m_registrationInfoServicesStore = new JAPRoutingRegistrationInfoServices();
        this.addObserver(this.m_registrationInfoServicesStore);
        this.m_useableMixCascadesStore = new JAPRoutingUseableMixCascades();
        this.addObserver(this.m_useableMixCascadesStore);
        this.m_serverStatisticsListener = new JAPRoutingServerStatisticsListener();
        this.addObserver(this.m_serverStatisticsListener);
        this.m_registrationStatusObserver = new JAPRoutingRegistrationStatusObserver();
        this.addObserver(this.m_registrationStatusObserver);
    }

    public int getRoutingMode() {
        return this.m_routingMode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setRoutingMode(int n) {
        boolean bl = false;
        boolean bl2 = false;
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (n != this.m_routingMode) {
                if (this.m_routingMode == 2) {
                    ForwardServerManager.getInstance().shutdownForwarding();
                    this.stopPropaganda();
                    this.m_currentServerManagerId = null;
                }
                if (this.m_routingMode == 1) {
                    if (this.getForwardInfoService()) {
                        JAPController.getInstance().applyProxySettingsToInfoService(JAPModel.getInstance().isProxyAuthenticationUsed());
                    }
                    JAPController.getInstance().stop();
                    if (this.getTransportMode().isLocal()) {
                        LocalForwarder.unregisterLocalForwarder();
                    }
                    try {
                        this.m_forwardedConnection.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    this.m_forwardedConnection = null;
                    this.m_protocolHandler = null;
                }
                this.m_routingMode = n;
                if (n == 2) {
                    ForwardServerManager.getInstance().startForwarding();
                    ForwardServerManager.getInstance().setNetBandwidth(this.getBandwidth());
                    ForwardServerManager.getInstance().setMaximumNumberOfConnections(this.getAllowedConnections());
                    if (this.getTransportMode() != null) {
                        this.m_currentServerManagerId = this.getTransportMode().startServer();
                    }
                    if (this.m_currentServerManagerId == null) {
                        ForwardServerManager.getInstance().shutdownForwarding();
                        this.m_routingMode = 0;
                    } else {
                        bl = true;
                    }
                }
                if (n == 1) {
                    LogHolder.log(7, LogType.NET, "JAPRountingSettings:setRoutingMode() start the client");
                    if (JAPController.getInstance().getAnonMode()) {
                        this.m_waitForShutdownCall = true;
                        JAPController.getInstance().stop();
                        try {
                            this.wait();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        this.m_waitForShutdownCall = false;
                    }
                    if (this.getTransportMode().isLocal()) {
                        LocalForwarder.registerLocalForwarder(this.getBandwidth());
                    }
                    LogHolder.log(7, LogType.NET, "JAPRountingSettings:setRoutingMode() try to connect to forwarder");
                    this.m_forwardedConnection = ForwardUtils.getInstance().createForwardingConnection(this.getTransportMode().getAddress());
                    if (this.m_forwardedConnection != null) {
                        this.updateInfoServiceProxySettings();
                        this.m_protocolHandler = new DefaultClientProtocolHandler(this.m_forwardedConnection);
                        bl = true;
                    } else {
                        this.m_routingMode = 0;
                    }
                }
                if (n == 0) {
                    bl = true;
                }
                bl2 = true;
            } else {
                bl = true;
            }
            if (bl2) {
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(1));
            }
        }
        return bl;
    }

    public static int getServerPort() {
        return JAPRoutingForwardingModeSelector.getServerPort();
    }

    public static String getApplicationName() {
        return JAPRoutingForwardingModeSelector.getSkypeApplicationName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setApplicationName(String string) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (JAPRoutingSettings.getApplicationName().equals(string)) {
                return true;
            }
            if (this.m_routingMode != 2) {
                m_forwardingModeSelector.setSkypeApplicationName(string);
                return true;
            }
            Object object = this.getTransportMode().startServer(string);
            if (object == null) {
                return false;
            }
            ForwardServerManager.getInstance().removeServerManager(this.m_currentServerManagerId);
            this.m_currentServerManagerId = object;
            m_forwardingModeSelector.setSkypeApplicationName(string);
            return true;
        }
    }

    public String getSkypeForwarderAddress() {
        return m_forwardingModeSelector.getSkypeForwarderAddress();
    }

    public void setSkypeForwarderAddress(String string) {
        this.setChanged();
        this.notifyObservers(new JAPRoutingMessage(18));
        m_forwardingModeSelector.setSkypeForwarderAddress(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setServerPort(int n) {
        boolean bl = false;
        if (n >= 1 && n <= 65535) {
            JAPRoutingSettings jAPRoutingSettings = this;
            synchronized (jAPRoutingSettings) {
                if (JAPRoutingSettings.getServerPort() != n) {
                    if (this.m_routingMode != 2) {
                        m_forwardingModeSelector.setServerPort(n);
                        bl = true;
                    } else {
                        Object object = ForwardServerManager.getInstance().addListenSocket(n);
                        if (object != null) {
                            ForwardServerManager.getInstance().removeServerManager(this.m_currentServerManagerId);
                            m_forwardingModeSelector.setServerPort(n);
                            this.m_currentServerManagerId = object;
                            bl = true;
                            if (this.m_propagandaStarted) {
                                this.startPropaganda(false);
                            }
                        }
                    }
                    if (bl) {
                        this.setChanged();
                        this.notifyObservers(new JAPRoutingMessage(15));
                    }
                } else {
                    bl = true;
                }
            }
        }
        return bl;
    }

    public int getBandwidth() {
        return this.m_bandwidth;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setBandwidth(int n) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            this.m_bandwidth = n;
            ForwardServerManager.getInstance().setNetBandwidth(n);
            this.setAllowedConnections(this.getAllowedConnections());
        }
    }

    public int getBandwidthMaxConnections() {
        return this.getBandwidth() / 4000;
    }

    public int getAllowedConnections() {
        return this.m_connections;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAllowedConnections(int n) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (n > this.getBandwidthMaxConnections()) {
                n = this.getBandwidthMaxConnections();
            }
            this.m_connections = n;
            ForwardServerManager.getInstance().setMaximumNumberOfConnections(n);
        }
    }

    public void setNewProxySettings(ProxyInterface proxyInterface) {
        ForwardUtils.getInstance().setProxySettings(proxyInterface);
    }

    public void setTCPForwarder(String string, int n) {
        this.setForwarderAddress(new TcpIpAddress(string, n));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setForwarderAddress(IAddress iAddress) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.m_routingMode != 1) {
                this.getTransportMode().setAddress(iAddress);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IAddress nextForwarderAddress() {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            try {
                return this.getTransportMode().nextAddress();
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.FORWARDING, exception);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IAddress getForwarderAddress() {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            try {
                return this.getTransportMode().getAddress();
            }
            catch (Exception exception) {
            }
        }
        return null;
    }

    public IAddress getUserProvidetForwarder() {
        return m_forwardingModeSelector.getUserProvidetForwarder();
    }

    public JAPRoutingForwardingModeSelector.TransportMode getTransportMode() {
        return m_forwardingModeSelector.getCurrentForwardingMode();
    }

    public synchronized boolean setTransportMode(JAPRoutingForwardingModeSelector.TransportMode transportMode) {
        return this.setTransportModeInternal(m_forwardingModeSelector.setCurrentForwardingMode(transportMode));
    }

    public synchronized boolean setTransportMode(int n) {
        LogHolder.log(7, LogType.GUI, "We try to set the transport mode to " + n);
        return this.setTransportModeInternal(m_forwardingModeSelector.setCurrentForwardingMode(n));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean setTransportModeInternal(boolean bl) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            LogHolder.log(7, LogType.GUI, "The transport mode is now " + this.getTransportMode());
            if (bl) {
                if (this.m_routingMode == 0 || this.m_routingMode == 1) {
                    return true;
                }
                if (this.m_routingMode == 2) {
                    Object object = null;
                    object = this.getTransportMode().startServer();
                    if (object == null) {
                        return false;
                    }
                    ForwardServerManager.getInstance().removeServerManager(this.m_currentServerManagerId);
                    this.m_currentServerManagerId = object;
                    return true;
                }
                return false;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setForwardInfoService(boolean bl) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.m_forwardInfoService != bl) {
                this.m_forwardInfoService = bl;
                if (bl && this.getRoutingMode() == 1) {
                    this.updateInfoServiceProxySettings();
                }
                if (!bl && this.getRoutingMode() == 1) {
                    JAPController.getInstance().applyProxySettingsToInfoService(JAPModel.getInstance().isProxyAuthenticationUsed());
                }
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(16));
            }
        }
    }

    public boolean getForwardInfoService() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setConnectViaForwarder(boolean bl) {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.m_connectViaForwarder != bl) {
                this.m_connectViaForwarder = bl;
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(16));
            }
        }
    }

    public boolean isConnectViaForwarder() {
        return this.m_connectViaForwarder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void httpListenerPortChanged() {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.getForwardInfoService() && this.getRoutingMode() == 1) {
                this.updateInfoServiceProxySettings();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void anonConnectionClosed() {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.getRoutingMode() == 1) {
                if (this.m_waitForShutdownCall) {
                    this.notify();
                } else {
                    this.setRoutingMode(0);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AnonProxy getAnonProxyInstance(DirectProxy directProxy) {
        AnonProxy anonProxy = null;
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.getRoutingMode() == 1) {
                anonProxy = new AnonProxy(directProxy, this.m_forwardedConnection, this.m_protocolHandler.getSelectedService(), this.m_maxDummyTrafficInterval, new TermsAndConditionConfirmation.AlwaysAccept());
            }
        }
        return anonProxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ForwardConnectionDescriptor getConnectionDescriptor() throws ClientForwardException {
        ForwardConnectionDescriptor forwardConnectionDescriptor = null;
        DefaultClientProtocolHandler defaultClientProtocolHandler = null;
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.getRoutingMode() == 1) {
                defaultClientProtocolHandler = this.m_protocolHandler;
            }
        }
        if (defaultClientProtocolHandler != null) {
            try {
                forwardConnectionDescriptor = defaultClientProtocolHandler.getConnectionDescriptor();
            }
            catch (ClientForwardException clientForwardException) {
                this.setRoutingMode(0);
                throw clientForwardException;
            }
        } else {
            throw new ClientForwardException(255, "JAPRoutingSettings: getConnectionDescriptor: Not in client routing mode.");
        }
        jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            this.m_maxDummyTrafficInterval = forwardConnectionDescriptor.getMinDummyTrafficInterval();
        }
        return forwardConnectionDescriptor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void selectMixCascade(MixCascade mixCascade) throws ClientForwardException {
        DefaultClientProtocolHandler defaultClientProtocolHandler = null;
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.getRoutingMode() == 1) {
                defaultClientProtocolHandler = this.m_protocolHandler;
            }
        }
        if (defaultClientProtocolHandler != null) {
            try {
                defaultClientProtocolHandler.selectMixCascade(mixCascade);
            }
            catch (ClientForwardException clientForwardException) {
                this.setRoutingMode(0);
                throw clientForwardException;
            }
        } else {
            throw new ClientForwardException(255, "JAPRoutingSettings: selectMixCascade: Not in client routing mode.");
        }
    }

    public ForwardSchedulerStatistics getSchedulerStatistics() {
        return ForwardServerManager.getInstance().getSchedulerStatistics();
    }

    public int getCurrentlyForwardedConnections() {
        return ForwardServerManager.getInstance().getCurrentlyForwardedConnections();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int startPropaganda(boolean bl) {
        Object object;
        JAPRoutingSettingsPropagandaThreadLock jAPRoutingSettingsPropagandaThreadLock = new JAPRoutingSettingsPropagandaThreadLock();
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.m_routingMode == 2) {
                Vector vector;
                this.stopPropaganda();
                object = this.getRegistrationInfoServicesStore().getRegistrationInfoServicesForStartup();
                this.m_runningPropagandists = vector = new Vector();
                this.m_startPropagandaThread = new Thread(new Runnable((Vector)object, jAPRoutingSettingsPropagandaThreadLock, vector){
                    private final /* synthetic */ Vector val$infoServiceList;
                    private final /* synthetic */ JAPRoutingSettingsPropagandaThreadLock val$masterThreadLock;
                    private final /* synthetic */ Vector val$currentPropagandists;
                    {
                        this.val$infoServiceList = vector;
                        this.val$masterThreadLock = jAPRoutingSettingsPropagandaThreadLock;
                        this.val$currentPropagandists = vector2;
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void run() {
                        Object object;
                        JAPRoutingSettings.this.setChanged();
                        JAPRoutingSettings.this.notifyObservers(new JAPRoutingMessage(3, null));
                        Enumeration enumeration = this.val$infoServiceList.elements();
                        boolean bl = false;
                        while (enumeration.hasMoreElements() && !bl) {
                            object = new ServerSocketPropagandist(JAPRoutingSettings.getServerPort(), (InfoServiceDBEntry)enumeration.nextElement());
                            JAPRoutingSettings jAPRoutingSettings = JAPModel.getInstance().getRoutingSettings();
                            synchronized (jAPRoutingSettings) {
                                bl = Thread.interrupted();
                                if (bl) {
                                    ((ServerSocketPropagandist)object).stopPropaganda();
                                    this.val$masterThreadLock.registrationWasInterrupted();
                                } else {
                                    this.val$currentPropagandists.addElement(object);
                                    this.val$masterThreadLock.updateRegistrationStatus((ServerSocketPropagandist)object);
                                    JAPRoutingSettings.this.setChanged();
                                    JAPRoutingSettings.this.notifyObservers(new JAPRoutingMessage(2, this.val$currentPropagandists.clone()));
                                }
                            }
                        }
                        object = JAPModel.getInstance().getRoutingSettings();
                        synchronized (object) {
                            JAPRoutingSettings.this.m_startPropagandaThread = null;
                            if (!Thread.interrupted() && !bl) {
                                JAPRoutingSettings.this.setChanged();
                                JAPRoutingSettings.this.notifyObservers(new JAPRoutingMessage(4, this.val$currentPropagandists.clone()));
                            }
                        }
                        object = this.val$masterThreadLock;
                        synchronized (object) {
                            this.val$masterThreadLock.propagandaThreadIsReady();
                            this.val$masterThreadLock.notify();
                        }
                    }
                });
                this.m_startPropagandaThread.setDaemon(true);
                this.m_propagandaStarted = true;
                this.m_startPropagandaThread.start();
            } else {
                jAPRoutingSettingsPropagandaThreadLock.propagandaThreadIsReady();
            }
        }
        int n = 0;
        object = jAPRoutingSettingsPropagandaThreadLock;
        synchronized (object) {
            if (bl && !jAPRoutingSettingsPropagandaThreadLock.isPropagandaThreadReady()) {
                try {
                    jAPRoutingSettingsPropagandaThreadLock.wait();
                    n = jAPRoutingSettingsPropagandaThreadLock.getRegistrationStatus();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopPropaganda() {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.m_startPropagandaThread != null) {
                try {
                    this.m_startPropagandaThread.interrupt();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            while (this.m_runningPropagandists.size() > 0) {
                ((ServerSocketPropagandist)this.m_runningPropagandists.firstElement()).stopPropaganda();
                this.m_runningPropagandists.removeElementAt(0);
            }
            this.m_propagandaStarted = false;
            this.setChanged();
            this.notifyObservers(new JAPRoutingMessage(5));
        }
    }

    public void addPropagandaInstance(final InfoServiceDBEntry infoServiceDBEntry) {
        Thread thread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                boolean bl = false;
                int n = -1;
                Observable observable = JAPModel.getInstance().getRoutingSettings();
                synchronized (observable) {
                    bl = JAPRoutingSettings.this.m_propagandaStarted;
                    n = JAPRoutingSettings.getServerPort();
                }
                observable = null;
                if (bl) {
                    observable = new ServerSocketPropagandist(n, infoServiceDBEntry);
                    JAPRoutingSettings jAPRoutingSettings = JAPModel.getInstance().getRoutingSettings();
                    synchronized (jAPRoutingSettings) {
                        if (JAPRoutingSettings.getServerPort() == n && JAPRoutingSettings.this.m_propagandaStarted) {
                            JAPRoutingSettings.this.m_runningPropagandists.addElement(observable);
                            JAPRoutingSettings.this.setChanged();
                            JAPRoutingSettings.this.notifyObservers(new JAPRoutingMessage(2, JAPRoutingSettings.this.m_runningPropagandists.clone()));
                        } else {
                            ((ServerSocketPropagandist)observable).stopPropaganda();
                        }
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getRunningPropagandaInstances() {
        Vector vector = null;
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            vector = (Vector)this.m_runningPropagandists.clone();
        }
        return vector;
    }

    public JAPRoutingForwardingModeSelector getForwardingModeSelector() {
        return m_forwardingModeSelector;
    }

    public JAPRoutingConnectionClassSelector getConnectionClassSelector() {
        return this.m_connectionClassSelector;
    }

    public JAPRoutingRegistrationInfoServices getRegistrationInfoServicesStore() {
        return this.m_registrationInfoServicesStore;
    }

    public JAPRoutingUseableMixCascades getUseableMixCascadesStore() {
        return this.m_useableMixCascadesStore;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        Element element = document.createElement("JapForwardingSettings");
        Element element2 = document.createElement("ForwardingServer");
        Element element3 = document.createElement("ServerPort");
        Element element4 = document.createElement("ServerRunning");
        Object object = this;
        synchronized (object) {
            XMLUtil.setValue((Node)element3, JAPRoutingSettings.getServerPort());
            if (this.getRoutingMode() == 2) {
                XMLUtil.setValue((Node)element4, true);
            } else {
                XMLUtil.setValue((Node)element4, false);
            }
        }
        element2.appendChild(element3);
        element2.appendChild(element4);
        element2.appendChild(this.getConnectionClassSelector().getSettingsAsXml(document));
        element2.appendChild(this.getRegistrationInfoServicesStore().getSettingsAsXml(document));
        element2.appendChild(this.getUseableMixCascadesStore().getSettingsAsXml(document));
        element.appendChild(element2);
        object = document.createElement("ForwardingClient");
        LogHolder.log(7, LogType.MISC, "Save the transport mode : " + this.getTransportMode().getName());
        XMLUtil.setAttribute((Element)object, "type", this.getTransportMode().getName());
        Element element5 = document.createElement("ConnectViaForwarder");
        Element element6 = document.createElement("ForwardInfoService");
        Element element7 = document.createElement("SkypeForwarderAddress");
        XMLUtil.setValue((Node)element5, this.isConnectViaForwarder());
        XMLUtil.setValue((Node)element6, this.getForwardInfoService());
        XMLUtil.setValue((Node)element7, this.getSkypeForwarderAddress());
        object.appendChild(element5);
        object.appendChild(element6);
        object.appendChild(element7);
        element.appendChild((Node)object);
        return element;
    }

    public int loadSettingsFromXml(Element element) {
        Element element2;
        Object object;
        Element element3;
        Element element4;
        boolean bl = true;
        Element element5 = (Element)XMLUtil.getFirstChildByName(element, "ForwardingServer");
        if (element5 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ForwardingServer node): Using default forwarding server settings.");
        } else {
            element4 = (Element)XMLUtil.getFirstChildByName(element5, "ServerPort");
            if (element4 == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ServerPort node): Using default server port.");
                bl = false;
            } else {
                int n = XMLUtil.parseValue((Node)element4, -1);
                if (n == -1) {
                    LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Invalid server port in XML structure: Using default server port.");
                    bl = false;
                } else if (!this.setServerPort(n)) {
                    LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error while setting the server port: Using default server port.");
                    bl = false;
                }
            }
            element3 = (Element)XMLUtil.getFirstChildByName(element5, "ConnectionClassSettings");
            if (element3 == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ConnectionClassSettings node): Using default connection class settings.");
                bl = false;
            } else if (!this.getConnectionClassSelector().loadSettingsFromXml(element3)) {
                bl = false;
            }
            object = (Element)XMLUtil.getFirstChildByName(element5, "InfoServiceRegistrationSettings");
            if (object == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (InfoServiceRegistrationSettings node): Using default infoservice registration settings.");
                bl = false;
            } else if (!this.getRegistrationInfoServicesStore().loadSettingsFromXml((Element)object)) {
                bl = false;
            }
            element2 = (Element)XMLUtil.getFirstChildByName(element5, "AllowedMixCascadesSettings");
            if (element2 == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (AllowedMixCascadesSettings node): Using default forwarding mixcascade settings.");
                bl = false;
            } else if (!this.getUseableMixCascadesStore().loadSettingsFromXml(element2)) {
                bl = false;
            }
            Element element6 = (Element)XMLUtil.getFirstChildByName(element5, "ServerRunning");
            if (element6 == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ServerRunning node): Server not started.");
            } else if (XMLUtil.parseValue((Node)element6, false)) {
                if (bl) {
                    if (this.setRoutingMode(2)) {
                        this.startPropaganda(false);
                        LogHolder.log(6, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: According to the configuration, the forwarding server was started.");
                    } else {
                        LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error while starting the forwarding server.");
                    }
                } else {
                    LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Because of errors while loading the configuration, the forwarding server was not started.");
                }
            }
        }
        element4 = (Element)XMLUtil.getFirstChildByName(element, "ForwardingClient");
        if (element4 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ForwardingClient node): Using default forwarding client settings.");
        } else {
            element3 = (Element)XMLUtil.getFirstChildByName(element4, "ConnectViaForwarder");
            if (element3 == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ConnectViaForwarder node): Using default value when enabling anonymity mode.");
            } else {
                object = XMLUtil.parseAttribute((Node)element4, "type", this.getTransportMode().getName());
                this.setTransportMode(m_forwardingModeSelector.getIdFromIdentifier((String)object));
                this.setConnectViaForwarder(XMLUtil.parseValue((Node)element3, false));
            }
            object = (Element)XMLUtil.getFirstChildByName(element4, "ForwardInfoService");
            if (object == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Error in XML structure (ForwardInfoService node): Using default value when creating a forwarded connection.");
            } else {
                this.setForwardInfoService(XMLUtil.parseValue((Node)object, false));
            }
            element2 = (Element)XMLUtil.getFirstChildByName(element4, "SkypeForwarderAddress");
            if (element2 == null) {
                LogHolder.log(4, LogType.MISC, "JAPRoutingSettings: loadSettingsFromXml: Skype forwarder address not found, using default value");
            } else {
                this.setSkypeForwarderAddress(XMLUtil.parseValue((Node)element2, m_forwardingModeSelector.getDefaultSkypeForwarderAddress()));
            }
        }
        return 0;
    }

    public JAPRoutingServerStatisticsListener getServerStatisticsListener() {
        return this.m_serverStatisticsListener;
    }

    public JAPRoutingRegistrationStatusObserver getRegistrationStatusObserver() {
        return this.m_registrationStatusObserver;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateInfoServiceProxySettings() {
        JAPRoutingSettings jAPRoutingSettings = this;
        synchronized (jAPRoutingSettings) {
            if (this.getForwardInfoService()) {
                HTTPConnectionFactory.getInstance().setNewProxySettings(new ProxyInterface("localhost", JAPModel.getHttpListenerPortNumber(), 1, null), JAPModel.getInstance().isProxyAuthenticationUsed());
            }
        }
    }

    public ProgressCounter getPacketCounter() {
        return this.m_protocolHandler.getPacketCounter();
    }
}

