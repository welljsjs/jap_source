/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.AnonChannel;
import anon.AnonServerDescription;
import anon.AnonService;
import anon.AnonServiceEventListener;
import anon.IServiceContainer;
import anon.crypto.MyRandom;
import anon.error.AnonServiceException;
import anon.error.InvalidServiceException;
import anon.infoservice.Database;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.terms.TermsAndConditionConfirmation;
import anon.tor.Circuit;
import anon.tor.FirstOnionRouterConnection;
import anon.tor.FirstOnionRouterConnectionFactory;
import anon.tor.TorAnonServerDescription;
import anon.tor.TorSocksChannel;
import anon.tor.ordescription.InfoServiceORListFetcher;
import anon.tor.ordescription.ORDescriptor;
import anon.tor.ordescription.ORList;
import anon.tor.ordescription.PlainORListFetcher;
import anon.tor.util.DNSCacheEntry;
import java.io.IOException;
import java.net.ConnectException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class Tor
implements Runnable,
AnonService {
    public static final int MAX_ROUTE_LEN = 5;
    public static final int MIN_ROUTE_LEN = 2;
    public static final int DNS_TIME_OUT = 600000;
    private static Tor ms_theTorInstance = null;
    private ORList m_orList = new ORList(new PlainORListFetcher("moria.seul.org", 9031));
    private Vector m_allowedORNames;
    private Vector m_allowedFirstORNames;
    private Vector m_allowedExitNodeNames;
    private Circuit[] m_activeCircuits;
    private int m_MaxNrOfActiveCircuits = 5;
    private Object m_oActiveCircuitSync = new Object();
    private Object m_oStartStopSync = new Object();
    private FirstOnionRouterConnectionFactory m_firstORFactory = new FirstOnionRouterConnectionFactory(this);
    private Database m_DNSCache;
    private Hashtable m_CircuitForDestination;
    private Vector[] m_KeysForCircuit;
    private volatile boolean m_bIsStarted = false;
    private boolean m_bIsCreatingCircuit = false;
    private boolean m_useDNSCache = true;
    private int m_circuitLengthMin = 2;
    private int m_circuitLengthMax = 5;
    private int m_ConnectionsPerCircuit = 1000;
    private MyRandom m_rand = new MyRandom(new SecureRandom());
    public static final String DEFAULT_DIR_SERVER_ADDR = "moria.seul.org";
    public static final int DEFAULT_DIR_SERVER_PORT = 9031;
    private Thread m_circuitCreator;
    private volatile boolean m_bCloseCreator = false;
    private IMutableProxyInterface m_proxyInterface = null;
    static /* synthetic */ Class class$anon$tor$util$DNSCacheEntry;

    private Tor() {
        this.m_activeCircuits = new Circuit[this.m_MaxNrOfActiveCircuits];
        this.m_DNSCache = Database.getInstance(class$anon$tor$util$DNSCacheEntry == null ? (class$anon$tor$util$DNSCacheEntry = Tor.class$("anon.tor.util.DNSCacheEntry")) : class$anon$tor$util$DNSCacheEntry);
        this.m_CircuitForDestination = new Hashtable();
        this.m_KeysForCircuit = new Vector[this.m_MaxNrOfActiveCircuits];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateORList() {
        ORList oRList = this.m_orList;
        synchronized (oRList) {
            this.m_orList.updateList();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized Circuit getCircuitForDestination(String string, int n, Hashtable hashtable) {
        int n2;
        if (!this.m_bIsStarted) {
            return null;
        }
        Circuit circuit = null;
        if (!ListenerInterface.isValidIP(string) && !ListenerInterface.isValidIP(string = this.resolveDNS(string))) {
            return null;
        }
        String string2 = string + ":" + n;
        if (this.m_CircuitForDestination.containsKey(string2) && (circuit = this.m_activeCircuits[n2 = ((Integer)this.m_CircuitForDestination.get(string2)).intValue()]) != null && !circuit.isShutdown() && circuit.isAllowed(string, n) && (hashtable == null || !hashtable.containsKey(circuit))) {
            return circuit;
        }
        for (n2 = 0; n2 < this.m_MaxNrOfActiveCircuits; ++n2) {
            circuit = this.m_activeCircuits[n2];
            if (circuit == null || circuit.isShutdown() || !circuit.isAllowed(string, n) || hashtable != null && hashtable.containsKey(circuit)) continue;
            this.m_CircuitForDestination.put(string2, new Integer(n2));
            if (this.m_KeysForCircuit[n2] == null) {
                this.m_KeysForCircuit[n2] = new Vector();
            }
            this.m_KeysForCircuit[n2].addElement(string2);
            return circuit;
        }
        Object object = this.m_oActiveCircuitSync;
        synchronized (object) {
            for (int i = 0; i < 5; ++i) {
                Object e;
                Enumeration enumeration;
                int n3 = this.m_rand.nextInt(this.m_MaxNrOfActiveCircuits);
                int n4 = 0;
                for (int j = 0; j < this.m_MaxNrOfActiveCircuits; ++j) {
                    n4 = n3 % this.m_MaxNrOfActiveCircuits;
                    if (this.m_activeCircuits[n4] == null || this.m_activeCircuits[n4].isShutdown()) {
                        if (this.m_KeysForCircuit[n4] != null) {
                            enumeration = this.m_KeysForCircuit[n4].elements();
                            while (enumeration.hasMoreElements()) {
                                e = enumeration.nextElement();
                                this.m_CircuitForDestination.remove(e);
                            }
                            this.m_KeysForCircuit[n4] = null;
                        }
                        this.m_activeCircuits[n4] = this.createNewCircuit(string, n);
                        if (this.m_activeCircuits[n4] == null || this.m_activeCircuits[n4].isShutdown()) break;
                        this.m_CircuitForDestination.put(string2, new Integer(n4));
                        this.m_KeysForCircuit[n4] = new Vector();
                        this.m_KeysForCircuit[n4].addElement(string2);
                        return this.m_activeCircuits[n4];
                    }
                    if (this.m_activeCircuits[n4].isAllowed(string, n)) {
                        if (!this.m_KeysForCircuit[n4].contains(string2)) {
                            this.m_CircuitForDestination.put(string2, new Integer(n4));
                            this.m_KeysForCircuit[n4].addElement(string2);
                        }
                        return this.m_activeCircuits[n4];
                    }
                    ++n3;
                }
                if (this.m_activeCircuits[n4] == null || this.m_activeCircuits[n4].isShutdown()) continue;
                n4 = n3 % this.m_MaxNrOfActiveCircuits;
                this.m_activeCircuits[n4].shutdown();
                enumeration = this.m_KeysForCircuit[n4].elements();
                while (enumeration.hasMoreElements()) {
                    e = enumeration.nextElement();
                    this.m_CircuitForDestination.remove(e);
                }
                this.m_KeysForCircuit[n4] = null;
                this.m_activeCircuits[n4] = this.createNewCircuit(string, n);
                if (this.m_activeCircuits[n4] == null || this.m_activeCircuits[n4].isShutdown()) continue;
                this.m_CircuitForDestination.put(string2, new Integer(n4));
                this.m_KeysForCircuit[n4] = new Vector();
                this.m_KeysForCircuit[n4].addElement(string2);
                return this.m_activeCircuits[n4];
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Circuit createNewCircuit(String string, int n) {
        Circuit circuit;
        block24: {
            Circuit circuit2;
            block23: {
                Circuit circuit3;
                block22: {
                    if (!this.m_bIsStarted) {
                        return null;
                    }
                    Object object = this.m_oStartStopSync;
                    synchronized (object) {
                        this.m_bIsCreatingCircuit = true;
                    }
                    try {
                        try {
                            object = this.m_orList;
                            synchronized (object) {
                                Vector<ORDescriptor> vector = new Vector<ORDescriptor>();
                                int n2 = this.m_rand.nextInt(this.m_circuitLengthMax - this.m_circuitLengthMin + 1) + this.m_circuitLengthMin;
                                Date date = this.m_orList.getPublished();
                                if (this.m_orList.size() == 0 || date != null && date.getTime() < System.currentTimeMillis() - 3600000L) {
                                    this.updateORList();
                                    if (this.m_orList.size() == 0) {
                                        Circuit circuit4 = null;
                                        // MONITOREXIT [1, 17, 18, 2, 8] lbl18 : MonitorExitStatement: MONITOREXIT : var3_3
                                        Object var16_10 = null;
                                        this.m_bIsCreatingCircuit = false;
                                        return circuit4;
                                    }
                                }
                                ORDescriptor oRDescriptor = this.m_allowedFirstORNames != null ? this.m_orList.getByRandom(this.m_allowedFirstORNames) : this.m_orList.getByRandom(n2);
                                LogHolder.log(7, LogType.TOR, "added as first: " + oRDescriptor);
                                vector.addElement(oRDescriptor);
                                Vector vector2 = this.m_orList.getList();
                                Enumeration enumeration = ((Vector)vector2.clone()).elements();
                                while (enumeration.hasMoreElements()) {
                                    oRDescriptor = (ORDescriptor)enumeration.nextElement();
                                    if (this.m_allowedExitNodeNames != null && !this.m_allowedExitNodeNames.contains(oRDescriptor.getName())) {
                                        vector2.removeElement(oRDescriptor);
                                        continue;
                                    }
                                    if (string != null && !oRDescriptor.getAcl().isAllowed(string, n)) {
                                        vector2.removeElement(oRDescriptor);
                                        continue;
                                    }
                                    if (!vector.contains(oRDescriptor)) continue;
                                    vector2.removeElement(oRDescriptor);
                                }
                                if (vector2.size() <= 0) {
                                    circuit3 = null;
                                    // MONITOREXIT [1, 2, 21, 8] lbl44 : MonitorExitStatement: MONITOREXIT : var3_3
                                    break block22;
                                }
                                oRDescriptor = (ORDescriptor)vector2.elementAt(this.m_rand.nextInt(vector2.size()));
                                vector.addElement(oRDescriptor);
                                LogHolder.log(7, LogType.TOR, "added as last: " + oRDescriptor);
                                for (int i = 2; i < n2; ++i) {
                                    while (vector.contains(oRDescriptor = this.m_allowedORNames != null ? this.m_orList.getByRandom(this.m_allowedORNames) : this.m_orList.getByRandom(n2))) {
                                    }
                                    LogHolder.log(7, LogType.TOR, "added " + oRDescriptor);
                                    vector.insertElementAt(oRDescriptor, 1);
                                }
                                ORDescriptor oRDescriptor2 = (ORDescriptor)vector.elementAt(0);
                                FirstOnionRouterConnection firstOnionRouterConnection = this.m_firstORFactory.createFirstOnionRouterConnection(oRDescriptor2);
                                if (firstOnionRouterConnection == null) {
                                    LogHolder.log(7, LogType.TOR, "removed " + oRDescriptor2.getName());
                                    this.m_orList.remove(oRDescriptor2.getName());
                                    throw new IOException("Problem with router " + vector + ". Cannot connect.");
                                }
                                Circuit circuit5 = firstOnionRouterConnection.createCircuit(vector);
                                this.m_bIsCreatingCircuit = false;
                                if (circuit5 == null) {
                                    circuit2 = null;
                                    // MONITOREXIT [1, 2, 20, 8] lbl66 : MonitorExitStatement: MONITOREXIT : var3_3
                                    break block23;
                                }
                                circuit5.setMaxNrOfStreams(this.m_ConnectionsPerCircuit);
                                circuit = circuit5;
                            }
                            break block24;
                        }
                        catch (Exception exception) {
                            this.m_bIsCreatingCircuit = false;
                            Circuit circuit6 = null;
                            Object var16_14 = null;
                            this.m_bIsCreatingCircuit = false;
                            return circuit6;
                        }
                    }
                    catch (Throwable throwable) {
                        Object var16_15 = null;
                        this.m_bIsCreatingCircuit = false;
                        throw throwable;
                    }
                }
                Object var16_11 = null;
                this.m_bIsCreatingCircuit = false;
                return circuit3;
            }
            Object var16_12 = null;
            this.m_bIsCreatingCircuit = false;
            return circuit2;
        }
        Object var16_13 = null;
        this.m_bIsCreatingCircuit = false;
        return circuit;
    }

    public static Tor getInstance() {
        if (ms_theTorInstance == null) {
            ms_theTorInstance = new Tor();
        }
        return ms_theTorInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        boolean bl = false;
        while (!this.m_bCloseCreator && !this.m_bCloseCreator) {
            Object object = this.m_oActiveCircuitSync;
            synchronized (object) {
                int n = -1;
                for (int i = 0; i < this.m_MaxNrOfActiveCircuits; ++i) {
                    if (this.m_activeCircuits[i] != null && !this.m_activeCircuits[i].isShutdown()) continue;
                    n = i;
                    break;
                }
                if (n != -1) {
                    bl = true;
                    Circuit circuit = this.createNewCircuit("141.76.46.1", 80);
                    if (circuit == null) {
                        continue;
                    }
                    this.m_activeCircuits[n] = circuit;
                }
            }
            if (bl) {
                bl = false;
                try {
                    Thread.sleep(10000L);
                }
                catch (InterruptedException interruptedException) {}
                continue;
            }
            try {
                Thread.sleep(30000L);
            }
            catch (InterruptedException interruptedException) {}
        }
        this.m_circuitCreator = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void start(boolean bl) throws IOException {
        Object object = this.m_oStartStopSync;
        synchronized (object) {
            this.m_bIsStarted = true;
            this.m_bCloseCreator = false;
            this.m_activeCircuits = new Circuit[this.m_MaxNrOfActiveCircuits];
            if (bl) {
                this.m_circuitCreator = new Thread((Runnable)this, "TorCircuitCreator");
                this.m_circuitCreator.setDaemon(true);
                this.m_circuitCreator.start();
            } else {
                this.m_circuitCreator = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stop() {
        Object object = this.m_oStartStopSync;
        synchronized (object) {
            this.m_bIsStarted = false;
            this.m_bCloseCreator = true;
            if (this.m_circuitCreator != null) {
                try {
                    this.m_circuitCreator.interrupt();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    this.m_circuitCreator.join();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                this.m_circuitCreator = null;
            }
            if (this.m_bIsCreatingCircuit) {
                this.m_firstORFactory.closeAll();
                while (this.m_bIsCreatingCircuit) {
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
            this.m_firstORFactory.closeAll();
        }
    }

    private void setCircuitLength(int n, int n2) {
        if (n2 >= n && n >= 2 && n2 <= 5) {
            this.m_circuitLengthMax = n2;
            this.m_circuitLengthMin = n;
        }
    }

    private void setConnectionsPerRoute(int n) {
        this.m_ConnectionsPerCircuit = n;
    }

    private void setORListServer(boolean bl, String string, int n) {
        if (bl) {
            this.m_orList.setFetcher(new InfoServiceORListFetcher());
        } else {
            this.m_orList.setFetcher(new PlainORListFetcher(string, n));
        }
    }

    public void setUseDNSCache(boolean bl) {
        this.m_useDNSCache = bl;
    }

    public AnonChannel createChannel(int n) throws ConnectException {
        if (n != 1) {
            return null;
        }
        try {
            return new TorSocksChannel(this);
        }
        catch (Exception exception) {
            throw new ConnectException("Could not create Tor channel: " + exception.getMessage());
        }
    }

    public AnonChannel createChannel(String string, int n) throws ConnectException {
        try {
            Circuit circuit = this.getCircuitForDestination(string, n, null);
            return circuit.createChannel(string, n);
        }
        catch (Exception exception) {
            throw new ConnectException("Error creating Tor channel: " + exception.getMessage());
        }
    }

    public synchronized void initialize(AnonServerDescription anonServerDescription, IServiceContainer iServiceContainer, TermsAndConditionConfirmation termsAndConditionConfirmation, boolean bl) throws AnonServiceException {
        if (!(anonServerDescription instanceof TorAnonServerDescription)) {
            throw new InvalidServiceException(anonServerDescription);
        }
        TorAnonServerDescription torAnonServerDescription = (TorAnonServerDescription)anonServerDescription;
        this.setORListServer(torAnonServerDescription.useInfoService(), torAnonServerDescription.getTorDirServerAddr(), torAnonServerDescription.getTorDirServerPort());
        this.setCircuitLength(torAnonServerDescription.getMinRouteLen(), torAnonServerDescription.getMaxRouteLen());
        this.setConnectionsPerRoute(torAnonServerDescription.getMaxConnectionsPerRoute());
        try {
            this.start(torAnonServerDescription.startCircuitsAtStartup());
        }
        catch (Exception exception) {
            throw new AnonServiceException(anonServerDescription, exception.getMessage(), -9);
        }
    }

    public int setProxy(IMutableProxyInterface iMutableProxyInterface) {
        this.m_proxyInterface = iMutableProxyInterface;
        return 0;
    }

    public IMutableProxyInterface getProxy() {
        return this.m_proxyInterface;
    }

    public void shutdown(boolean bl) {
        try {
            this.stop();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void addEventListener(AnonServiceEventListener anonServiceEventListener) {
    }

    public void removeEventListeners() {
    }

    public void removeEventListener(AnonServiceEventListener anonServiceEventListener) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized String resolveDNS(String string) {
        DNSCacheEntry dNSCacheEntry;
        String string2 = null;
        if (this.m_useDNSCache && (dNSCacheEntry = (DNSCacheEntry)this.m_DNSCache.getEntryById(string)) != null) {
            LogHolder.log(7, LogType.TOR, "Resolved from Database : " + dNSCacheEntry.getId() + " - " + dNSCacheEntry.getIp());
            return dNSCacheEntry.getIp();
        }
        Object object = this.m_oActiveCircuitSync;
        synchronized (object) {
            for (int i = 0; i < 3; ++i) {
                String string3;
                int n = this.m_rand.nextInt(this.m_MaxNrOfActiveCircuits);
                if (this.m_activeCircuits[n] == null || this.m_activeCircuits[n].isShutdown()) {
                    this.m_activeCircuits[n] = this.createNewCircuit(null, -1);
                }
                if (this.m_activeCircuits[n] == null || this.m_activeCircuits[n].isShutdown() || (string3 = this.m_activeCircuits[n].resolveDNS(string)) == null) continue;
                string2 = string3;
                break;
            }
        }
        if (string2 != null) {
            dNSCacheEntry = new DNSCacheEntry(string, string2, System.currentTimeMillis() + 600000L);
            this.m_DNSCache.update(dNSCacheEntry);
            LogHolder.log(7, LogType.TOR, "Adding to Database : " + dNSCacheEntry.getId() + " - " + dNSCacheEntry.getIp());
        }
        return string2;
    }

    public boolean isConnected() {
        return this.m_bIsStarted;
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

