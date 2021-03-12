/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.crypto.MyRandom;
import anon.tor.CellQueue;
import anon.tor.FirstOnionRouterConnection;
import anon.tor.OnionRouter;
import anon.tor.TorChannel;
import anon.tor.cells.Cell;
import anon.tor.cells.CreatedCell;
import anon.tor.cells.DestroyCell;
import anon.tor.cells.PaddingCell;
import anon.tor.cells.RelayCell;
import anon.tor.ordescription.ORDescriptor;
import anon.util.ByteArrayUtil;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public final class Circuit
implements Runnable {
    public static final int MAX_STREAMS_OVER_CIRCUIT = 1000;
    private OnionRouter m_FirstOR;
    private ORDescriptor m_lastORDescription;
    private FirstOnionRouterConnection m_FirstORConnection;
    private Vector m_onionRouters;
    private int m_circID;
    private Hashtable m_streams;
    private volatile int m_State;
    private volatile int m_iRelayErrors;
    private static final int STATE_CLOSED = 0;
    private static final int STATE_SHUTDOWN = 1;
    private static final int STATE_READY = 2;
    private static final int STATE_CREATING = 3;
    private int m_streamCounter;
    private int m_circuitLength;
    private int m_MaxStreamsPerCircuit;
    private volatile int m_recvCellCounter;
    private volatile int m_sendCellCounter;
    private boolean m_destroyed;
    private byte[] m_resolvedData;
    private Object m_oResolveSync = new Object();
    private Object m_oSendCellCounterSync = new Object();
    private Object m_oSendSync = new Object();
    private Object m_oDestroyedByPeerSync = new Object();
    private volatile boolean m_bReceivedCreatedOrExtendedCell;
    private Object m_oNotifySync = new Object();
    private MyRandom m_rand;
    private Thread m_threadSendCellLoop;
    private CellQueue m_cellqueueSend;

    public Circuit(int n, FirstOnionRouterConnection firstOnionRouterConnection, Vector vector) throws IOException {
        this.m_FirstORConnection = firstOnionRouterConnection;
        this.m_circID = n;
        this.m_streams = new Hashtable();
        this.m_streamCounter = 0;
        this.m_MaxStreamsPerCircuit = 1000;
        this.m_onionRouters = (Vector)vector.clone();
        this.m_circuitLength = vector.size();
        this.m_lastORDescription = (ORDescriptor)this.m_onionRouters.elementAt(this.m_circuitLength - 1);
        if (this.m_onionRouters.size() < 1) {
            throw new IOException("No Onionrouters defined for this circuit");
        }
        this.m_recvCellCounter = 1000;
        this.m_sendCellCounter = 1000;
        this.m_rand = new MyRandom(new SecureRandom());
        this.m_State = 3;
        this.m_destroyed = false;
        this.m_iRelayErrors = 0;
        this.m_cellqueueSend = new CellQueue();
        this.m_threadSendCellLoop = new Thread((Runnable)this, "Tor - Circuit - SendCellLoop");
        this.m_threadSendCellLoop.setDaemon(true);
        this.m_threadSendCellLoop.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addToSendCellCounter(int n) {
        Object object = this.m_oSendCellCounterSync;
        synchronized (object) {
            this.m_sendCellCounter += n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void create() throws IOException {
        LogHolder.log(7, LogType.TOR, "[TOR] Creating Circuit '" + this.m_circID + "'");
        this.m_FirstOR = new OnionRouter(this.m_circID, (ORDescriptor)this.m_onionRouters.elementAt(0));
        try {
            Object object = this.m_oNotifySync;
            synchronized (object) {
                this.m_bReceivedCreatedOrExtendedCell = false;
                this.m_FirstORConnection.send(this.m_FirstOR.createConnection());
                this.m_oNotifySync.wait(15000L);
            }
            if (this.m_State != 3 || !this.m_bReceivedCreatedOrExtendedCell) {
                throw new IOException("Error during Circuit creation");
            }
            LogHolder.log(7, LogType.TOR, "[TOR] created!");
            for (int i = 1; i < this.m_onionRouters.size(); ++i) {
                ORDescriptor oRDescriptor = (ORDescriptor)this.m_onionRouters.elementAt(i);
                LogHolder.log(7, LogType.TOR, "[TOR] trying to extend!");
                Object object2 = this.m_oNotifySync;
                synchronized (object2) {
                    this.m_bReceivedCreatedOrExtendedCell = false;
                    RelayCell relayCell = this.m_FirstOR.extendConnection(oRDescriptor);
                    this.m_FirstORConnection.send(relayCell);
                    this.m_oNotifySync.wait(25000L);
                }
                if (this.m_State != 3 || !this.m_bReceivedCreatedOrExtendedCell) {
                    throw new IOException("Error during Circuit creation");
                }
                LogHolder.log(7, LogType.TOR, "[TOR] extended!");
            }
            this.m_State = 2;
            LogHolder.log(7, LogType.MISC, "[TOR] Circuit '" + this.m_circID + "' ready!!! - Length of this Circuit : " + this.m_circuitLength + " Onionrouters");
        }
        catch (Exception exception) {
            try {
                if (!this.m_destroyed) {
                    this.send(new DestroyCell(this.m_circID));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.m_State = 0;
            throw new IOException(exception.getLocalizedMessage());
        }
    }

    public synchronized void shutdown() {
        if (this.m_State == 0 || this.m_State == 1) {
            return;
        }
        if (this.m_streams.isEmpty()) {
            this.close();
        }
        this.m_State = 1;
    }

    public synchronized void close() {
        if (this.m_State == 0) {
            return;
        }
        try {
            Enumeration enumeration = this.m_streams.elements();
            while (enumeration.hasMoreElements()) {
                try {
                    TorChannel torChannel = (TorChannel)enumeration.nextElement();
                    torChannel.close();
                }
                catch (Exception exception) {}
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_streams.clear();
        try {
            this.m_FirstORConnection.send(new DestroyCell(this.m_circID));
            LogHolder.log(7, LogType.TOR, "[TOR] circuit " + this.m_circID + " destroyed!");
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_State = 0;
        try {
            this.m_threadSendCellLoop.join(2000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        this.m_FirstORConnection.notifyCircuitClosed(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroyedByPeer() {
        Object object = this.m_oDestroyedByPeerSync;
        synchronized (object) {
            try {
                Enumeration enumeration = this.m_streams.elements();
                while (enumeration.hasMoreElements()) {
                    try {
                        TorChannel torChannel = (TorChannel)enumeration.nextElement();
                        torChannel.closedByPeer();
                    }
                    catch (Exception exception) {}
                }
                this.m_streams.clear();
                this.m_FirstORConnection.notifyCircuitClosed(this);
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.m_State = 0;
        }
        object = this.m_oNotifySync;
        synchronized (object) {
            this.m_oNotifySync.notify();
        }
    }

    public boolean isClosed() {
        return this.m_State == 0;
    }

    public boolean isShutdown() {
        return this.m_State == 1 || this.m_State == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispatchCell(Cell cell) throws IOException {
        block30: {
            try {
                if (cell instanceof RelayCell) {
                    RelayCell relayCell;
                    if (this.m_State == 3) {
                        if (!this.m_FirstOR.checkExtendedCell((RelayCell)cell)) {
                            this.send(new DestroyCell(this.m_circID));
                            this.m_State = 0;
                            this.destroyedByPeer();
                            this.m_destroyed = true;
                            break block30;
                        }
                        Object object = this.m_oNotifySync;
                        synchronized (object) {
                            this.m_bReceivedCreatedOrExtendedCell = true;
                            this.m_oNotifySync.notify();
                            break block30;
                        }
                    }
                    --this.m_recvCellCounter;
                    if (this.m_recvCellCounter < 900) {
                        relayCell = new RelayCell(this.m_circID, 5, 0, null);
                        this.send(relayCell);
                        this.m_recvCellCounter += 100;
                    }
                    relayCell = this.m_FirstOR.decryptCell((RelayCell)cell);
                    Integer n = relayCell.getStreamID();
                    if (relayCell.getStreamID() == 0) {
                        switch (relayCell.getRelayCommand()) {
                            case 5: {
                                this.addToSendCellCounter(100);
                                break;
                            }
                            default: {
                                LogHolder.log(7, LogType.TOR, "Upps...");
                                break;
                            }
                        }
                        break block30;
                    }
                    if (this.m_streams.containsKey(n)) {
                        if (relayCell.getRelayCommand() == 12) {
                            byte[] arrby = relayCell.getPayload();
                            this.m_resolvedData = ByteArrayUtil.copy(arrby, 11, ((arrby[9] & 0xFF) << 8) + (arrby[10] & 0xFF));
                            Object object = this.m_oNotifySync;
                            synchronized (object) {
                                this.m_oNotifySync.notify();
                                break block30;
                            }
                        }
                        TorChannel torChannel = (TorChannel)this.m_streams.get(n);
                        if (torChannel != null) {
                            if (torChannel.dispatchCell(relayCell) != 0) {
                                ++this.m_iRelayErrors;
                                if (this.m_iRelayErrors > 10) {
                                    this.shutdown();
                                }
                            }
                        } else {
                            LogHolder.log(7, LogType.TOR, "Upps...");
                        }
                        break block30;
                    }
                    LogHolder.log(7, LogType.TOR, "Upps...Unknown stream");
                    break block30;
                }
                if (cell instanceof CreatedCell) {
                    if (!this.m_FirstOR.checkCreatedCell(cell)) {
                        LogHolder.log(7, LogType.TOR, "[TOR] Should never be here - 'created' cell was wrong");
                        this.m_State = 0;
                        this.destroyedByPeer();
                        break block30;
                    }
                    LogHolder.log(7, LogType.TOR, "[TOR] Connected to the first OR");
                    Object object = this.m_oNotifySync;
                    synchronized (object) {
                        this.m_bReceivedCreatedOrExtendedCell = true;
                        this.m_oNotifySync.notify();
                        break block30;
                    }
                }
                if (!(cell instanceof PaddingCell)) {
                    if (cell instanceof DestroyCell) {
                        byte by = cell.getPayload()[0];
                        LogHolder.log(7, LogType.TOR, "[TOR] recieved destroycell - circuit destroyed - reason: " + Integer.toString(by));
                        this.m_destroyed = true;
                        this.destroyedByPeer();
                    } else {
                        LogHolder.log(7, LogType.MISC, "tor kein bekannter cell type");
                    }
                }
            }
            catch (Exception exception) {
                this.destroyedByPeer();
                throw new IOException("Unable to dispatch the cell \n" + exception.getLocalizedMessage());
            }
        }
    }

    public void send(Cell cell) throws IOException, Exception {
        if (this.m_State == 0) {
            throw new IOException("circuit alread closed");
        }
        this.m_cellqueueSend.addElement(cell);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendUrgent(Cell cell) throws IOException, Exception {
        if (this.m_State == 0) {
            throw new IOException("circuit alread closed");
        }
        Object object = this.m_oSendSync;
        synchronized (object) {
            if (cell instanceof RelayCell) {
                cell = this.m_FirstOR.encryptCell((RelayCell)cell);
                this.addToSendCellCounter(-1);
            }
            this.m_FirstORConnection.send(cell);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String resolveDNS(String string) {
        if (this.m_State != 2) {
            return null;
        }
        Object object = this.m_oResolveSync;
        synchronized (object) {
            Integer n;
            Object object2 = this.m_streams;
            synchronized (object2) {
                while (this.m_streams.containsKey(n = new Integer(this.m_rand.nextInt(65535)))) {
                }
                this.m_streams.put(n, n);
            }
            object2 = new RelayCell(this.getCircID(), 11, n, string.getBytes());
            Object object3 = this.m_oNotifySync;
            synchronized (object3) {
                try {
                    this.m_resolvedData = null;
                    this.send((Cell)object2);
                    this.m_oNotifySync.wait(20000L);
                }
                catch (Exception exception) {
                    this.m_streams.remove(n);
                    return null;
                }
            }
            this.m_streams.remove(n);
            if (this.m_State == 0 || this.m_resolvedData == null || this.m_resolvedData[0] != 4 || this.m_resolvedData[1] != 4) {
                return null;
            }
            object3 = new StringBuffer();
            ((StringBuffer)object3).append(Integer.toString(this.m_resolvedData[2] & 0xFF));
            ((StringBuffer)object3).append('.');
            ((StringBuffer)object3).append(Integer.toString(this.m_resolvedData[3] & 0xFF));
            ((StringBuffer)object3).append('.');
            ((StringBuffer)object3).append(Integer.toString(this.m_resolvedData[4] & 0xFF));
            ((StringBuffer)object3).append('.');
            ((StringBuffer)object3).append(Integer.toString(this.m_resolvedData[5] & 0xFF));
            return ((StringBuffer)object3).toString();
        }
    }

    protected void close(int n) throws Exception {
        if (this.m_State == 0) {
            return;
        }
        byte[] arrby = new byte[]{6};
        Integer n2 = new Integer(n);
        if (this.m_streams.containsKey(n2)) {
            this.m_streams.remove(n2);
            RelayCell relayCell = new RelayCell(this.m_circID, 3, n, arrby);
            this.send(relayCell);
            if (this.m_State == 1 && this.m_streams.isEmpty()) {
                this.close();
            }
        }
    }

    public int getCircID() {
        return this.m_circID;
    }

    public synchronized TorChannel createChannel(String string, int n) throws IOException {
        TorChannel torChannel = new TorChannel();
        int n2 = this.connectChannel(torChannel, string, n);
        if (n2 != 0) {
            throw new IOException("Circuit:createChannel(addr,port) failed! Reason:" + Integer.toString(n2));
        }
        return torChannel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int connectChannel(TorChannel torChannel, String string, int n) {
        try {
            Integer n2;
            if (this.isShutdown()) {
                LogHolder.log(7, LogType.TOR, "Circuit:connectChannel() - Circuit Closed - cannot connect");
                return -9;
            }
            int n3 = 0;
            Circuit circuit = this;
            synchronized (circuit) {
                ++this.m_streamCounter;
                Hashtable hashtable = this.m_streams;
                synchronized (hashtable) {
                    while (this.m_streams.contains(n2 = new Integer(this.m_rand.nextInt(65535)))) {
                    }
                    torChannel.setStreamID(n2);
                    torChannel.setCircuit(this);
                    this.m_streams.put(n2, torChannel);
                }
            }
            if (!torChannel.connect(string, n)) {
                circuit = this;
                synchronized (circuit) {
                    this.m_streams.remove(n2);
                }
                LogHolder.log(7, LogType.TOR, "Circuit:connectChannel() - Channel could not be created");
                n3 = -6;
            }
            if (this.m_streamCounter >= this.m_MaxStreamsPerCircuit) {
                this.shutdown();
            }
            return n3;
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.TOR, "Circuit:connectChannel() - Unkown Error", throwable);
            return -1;
        }
    }

    public boolean isAllowed(String string, int n) {
        return this.m_lastORDescription.getAcl().isAllowed(string, n);
    }

    public void setMaxNrOfStreams(int n) {
        if (n > 0 && n <= 1000) {
            this.m_MaxStreamsPerCircuit = n;
        }
        if (this.m_streamCounter >= 1000) {
            this.shutdown();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        try {
            while (this.m_State != 0) {
                while (this.m_cellqueueSend.isEmpty()) {
                    try {
                        if (this.m_State == 0) {
                            return;
                        }
                        Thread.sleep(100L);
                    }
                    catch (Exception exception) {}
                }
                Cell cell = this.m_cellqueueSend.removeElement();
                while (this.m_sendCellCounter <= 0 && this.m_State != 0) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (Exception exception) {}
                }
                Object object = this.m_oSendSync;
                synchronized (object) {
                    if (!(cell instanceof RelayCell)) {
                        LogHolder.log(7, LogType.TOR, "Tor-Circuit-sendCellLoop: sending no releay cell.");
                    } else {
                        TorChannel torChannel = (TorChannel)this.m_streams.get(((RelayCell)cell).getStreamID());
                        cell = this.m_FirstOR.encryptCell((RelayCell)cell);
                        this.addToSendCellCounter(-1);
                        if (torChannel != null) {
                            torChannel.decreaseSendRelayCellsWaitingForDelivery();
                        }
                    }
                    this.m_FirstORConnection.send(cell);
                }
            }
            return;
        }
        catch (Throwable throwable) {
            this.destroyedByPeer();
        }
    }
}

