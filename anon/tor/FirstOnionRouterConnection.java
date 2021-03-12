/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.crypto.JAPCertificate;
import anon.crypto.MyRSAPublicKey;
import anon.crypto.MyRandom;
import anon.crypto.PKCS12;
import anon.crypto.RSAKeyPair;
import anon.crypto.Validity;
import anon.crypto.X509DistinguishedName;
import anon.crypto.tinytls.TinyTLS;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.ImmutableProxyInterface;
import anon.tor.Circuit;
import anon.tor.FirstOnionRouterConnectionThread;
import anon.tor.Tor;
import anon.tor.cells.Cell;
import anon.tor.ordescription.ORDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class FirstOnionRouterConnection
implements Runnable {
    private static String OP_NAME = "JAPClient";
    private TinyTLS m_tinyTLS;
    private ORDescriptor m_description;
    private Thread m_readDataLoop = null;
    private InputStream m_istream;
    private OutputStream m_ostream;
    private Hashtable m_Circuits;
    private volatile boolean m_bRun = false;
    private boolean m_bIsClosed = true;
    private MyRandom m_rand;
    private Object m_oSendSync;
    private long m_inittimeout = 30000L;
    private Tor m_Tor;
    private RSAKeyPair m_keypairIdentityKey;

    public FirstOnionRouterConnection(ORDescriptor oRDescriptor, Tor tor) {
        this.m_description = oRDescriptor;
        this.m_rand = new MyRandom(new SecureRandom());
        this.m_oSendSync = new Object();
        this.m_Tor = tor;
    }

    public ORDescriptor getORDescription() {
        return this.m_description;
    }

    public boolean isClosed() {
        return this.m_bIsClosed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void send(Cell cell) throws IOException {
        Object object = this.m_oSendSync;
        synchronized (object) {
            while (true) {
                try {
                    this.m_ostream.write(cell.getCellData());
                    this.m_ostream.flush();
                    LogHolder.log(7, LogType.TOR, "OnionConnection " + this.m_description.getName() + " Send a cell");
                }
                catch (InterruptedIOException interruptedIOException) {
                    continue;
                }
                break;
            }
        }
    }

    private boolean dispatchCell(Cell cell) {
        try {
            int n = cell.getCircuitID();
            LogHolder.log(7, LogType.MISC, "OnionProxy read() Tor Cell - Circuit: " + n + " Type: " + cell.getCommand());
            Circuit circuit = (Circuit)this.m_Circuits.get(new Integer(n));
            if (circuit != null) {
                circuit.dispatchCell(cell);
            } else {
                this.m_Circuits.remove(new Integer(n));
            }
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public synchronized void connect() throws Exception {
        IMutableProxyInterface iMutableProxyInterface = this.m_Tor.getProxy();
        ImmutableProxyInterface immutableProxyInterface = null;
        if (iMutableProxyInterface != null) {
            immutableProxyInterface = iMutableProxyInterface.getProxyInterface(false).getProxyInterface();
        }
        FirstOnionRouterConnectionThread firstOnionRouterConnectionThread = new FirstOnionRouterConnectionThread(this.m_description.getAddress(), this.m_description.getPort(), this.m_inittimeout, immutableProxyInterface);
        this.m_tinyTLS = firstOnionRouterConnectionThread.getConnection();
        this.m_tinyTLS.setRootKey(this.m_description.getSigningKey());
        try {
            RSAKeyPair rSAKeyPair = RSAKeyPair.getInstance(new BigInteger(new byte[]{1, 0, 1}), new SecureRandom(), 1024, 100);
            JAPCertificate jAPCertificate = JAPCertificate.getInstance(new X509DistinguishedName("CN=" + OP_NAME), rSAKeyPair, new Validity(Calendar.getInstance(), 1));
            this.m_keypairIdentityKey = RSAKeyPair.getInstance(new BigInteger(new byte[]{1, 0, 1}), new SecureRandom(), 1024, 100);
            PKCS12 pKCS12 = new PKCS12(new X509DistinguishedName("CN=" + OP_NAME + " <identity>"), this.m_keypairIdentityKey, new Validity(Calendar.getInstance(), 1));
            JAPCertificate jAPCertificate2 = jAPCertificate.sign(pKCS12);
            JAPCertificate jAPCertificate3 = JAPCertificate.getInstance(pKCS12.getX509Certificate());
            this.m_tinyTLS.setClientCertificate(new JAPCertificate[]{jAPCertificate2, jAPCertificate3}, rSAKeyPair.getPrivate());
        }
        catch (Exception exception) {
            LogHolder.log(7, LogType.TOR, "Error while creating Certificates. Certificates are not used.");
        }
        this.m_tinyTLS.setSoTimeout(30000);
        this.m_tinyTLS.startHandshake();
        this.m_istream = this.m_tinyTLS.getInputStream();
        this.m_ostream = this.m_tinyTLS.getOutputStream();
        this.m_Circuits = new Hashtable();
        this.m_tinyTLS.setSoTimeout(1000);
        this.start();
        this.m_bIsClosed = false;
    }

    public synchronized Circuit createCircuit(Vector vector) {
        int n = 0;
        try {
            int n2 = 32768;
            if (this.m_description.getSigningKey().getModulus().compareTo(((MyRSAPublicKey)this.m_keypairIdentityKey.getPublic()).getModulus()) > 0) {
                n2 = 0;
            }
            do {
                n = this.m_rand.nextInt(32767);
            } while (this.m_Circuits.containsKey(new Integer(n |= n2)) && n != 0);
            Circuit circuit = new Circuit(n, this, vector);
            this.m_Circuits.put(new Integer(n), circuit);
            circuit.create();
            return circuit;
        }
        catch (Exception exception) {
            this.m_Circuits.remove(new Integer(n));
            return null;
        }
    }

    private void start() {
        if (this.m_readDataLoop == null) {
            this.m_bRun = true;
            this.m_readDataLoop = new Thread((Runnable)this, "FirstOnionRouterConnection - " + this.m_description.getName());
            this.m_readDataLoop.setDaemon(true);
            this.m_readDataLoop.start();
        }
    }

    public void run() {
        Cell cell = null;
        byte[] arrby = new byte[512];
        int n = 0;
        while (this.m_bRun) {
            n = 0;
            while (n < 512 && this.m_bRun) {
                int n2 = 0;
                try {
                    n2 = this.m_istream.read(arrby, n, 512 - n);
                }
                catch (InterruptedIOException interruptedIOException) {
                    continue;
                }
                catch (IOException iOException) {
                    break;
                }
                if (n2 <= 0) break;
                n += n2;
            }
            if (n != 512) {
                this.closedByPeer();
                return;
            }
            LogHolder.log(7, LogType.TOR, "OnionConnection " + this.m_description.getName() + " received a Cell!");
            cell = Cell.createCell(arrby);
            if (cell == null) {
                LogHolder.log(0, LogType.TOR, "OnionConnection " + this.m_description.getName() + " dont know about this Cell!");
            }
            if (cell != null && this.dispatchCell(cell)) continue;
            this.closedByPeer();
            return;
        }
    }

    private void stop() throws IOException {
        if (this.m_readDataLoop != null && this.m_bRun) {
            try {
                this.m_bRun = false;
                this.m_readDataLoop.interrupt();
                this.m_readDataLoop.join();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.m_readDataLoop = null;
    }

    public synchronized void close() {
        try {
            if (!this.m_bIsClosed) {
                this.m_bIsClosed = true;
                this.stop();
                this.m_tinyTLS.close();
                Enumeration enumeration = this.m_Circuits.elements();
                while (enumeration.hasMoreElements()) {
                    ((Circuit)enumeration.nextElement()).close();
                }
                this.m_Circuits.clear();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closedByPeer() {
        if (this.m_bIsClosed) {
            return;
        }
        FirstOnionRouterConnection firstOnionRouterConnection = this;
        synchronized (firstOnionRouterConnection) {
            try {
                this.stop();
                this.m_tinyTLS.close();
                Enumeration enumeration = this.m_Circuits.elements();
                while (enumeration.hasMoreElements()) {
                    ((Circuit)enumeration.nextElement()).destroyedByPeer();
                }
                this.m_Circuits.clear();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.m_bIsClosed = true;
        }
    }

    protected void notifyCircuitClosed(Circuit circuit) {
        this.m_Circuits.remove(new Integer(circuit.getCircID()));
    }
}

