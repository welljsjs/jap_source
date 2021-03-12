/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AsymmetricCryptoKeyPair;
import anon.crypto.DSAKeyPair;
import anon.util.ClassUtil;
import java.security.SecureRandom;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class DSAKeyPool {
    private Thread m_keyCreationThread;
    private Vector m_keys = new Vector();
    private boolean m_bInterrupted;
    private int m_poolSize;
    private int m_certainty;
    private int m_keyLength;
    static /* synthetic */ Class class$anon$crypto$DSAKeyPool$KeyCreationThread;

    public DSAKeyPool(int n) {
        if (n < 0) {
            this.m_poolSize = 0;
        } else if (n > 1000) {
            this.m_poolSize = 1000;
        }
        this.m_poolSize = n;
        this.m_certainty = 60;
        this.m_keyLength = 1024;
    }

    public DSAKeyPool() {
        this(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start() {
        Vector vector = this.m_keys;
        synchronized (vector) {
            if (this.m_keyCreationThread == null) {
                this.m_bInterrupted = false;
                this.m_keyCreationThread = new Thread((Runnable)new KeyCreationThread(), ClassUtil.getShortClassName(class$anon$crypto$DSAKeyPool$KeyCreationThread == null ? (class$anon$crypto$DSAKeyPool$KeyCreationThread = DSAKeyPool.class$("anon.crypto.DSAKeyPool$KeyCreationThread")) : class$anon$crypto$DSAKeyPool$KeyCreationThread));
                this.m_keyCreationThread.setPriority(1);
                this.m_keyCreationThread.setDaemon(true);
                this.m_keyCreationThread.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        Vector vector = this.m_keys;
        synchronized (vector) {
            while (this.m_keyCreationThread != null && this.m_keyCreationThread.isAlive()) {
                this.m_bInterrupted = true;
                this.m_keyCreationThread.interrupt();
                this.m_keys.notifyAll();
                try {
                    this.m_keys.wait(100L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                    break;
                }
            }
            this.m_keyCreationThread = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AsymmetricCryptoKeyPair popKeyPair() {
        DSAKeyPair dSAKeyPair = null;
        Vector vector = this.m_keys;
        synchronized (vector) {
            boolean bl = false;
            if (this.m_keyCreationThread == null) {
                this.start();
            }
            if (this.m_poolSize == 0) {
                bl = true;
                this.m_poolSize = 1;
            }
            while (this.m_keys.size() == 0 && this.m_keyCreationThread != null && !this.m_bInterrupted) {
                try {
                    this.m_keys.notify();
                    this.m_keys.wait(500L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                    break;
                }
            }
            if (this.m_keys.size() > 0) {
                if (bl) {
                    this.m_poolSize = 0;
                }
                dSAKeyPair = (DSAKeyPair)this.m_keys.firstElement();
                this.m_keys.removeElementAt(0);
                this.m_keys.notify();
            }
        }
        return dSAKeyPair;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class KeyCreationThread
    implements Runnable {
        private KeyCreationThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            DSAKeyPair dSAKeyPair = null;
            LogHolder.log(5, LogType.CRYPTO, "Starting DSA key pool with pool size " + DSAKeyPool.this.m_poolSize + "...");
            while (!DSAKeyPool.this.m_bInterrupted) {
                Vector vector = DSAKeyPool.this.m_keys;
                synchronized (vector) {
                    if (DSAKeyPool.this.m_bInterrupted) {
                        return;
                    }
                    if (DSAKeyPool.this.m_keys.size() >= DSAKeyPool.this.m_poolSize) {
                        try {
                            DSAKeyPool.this.m_keys.wait();
                        }
                        catch (InterruptedException interruptedException) {
                            return;
                        }
                    }
                    if (DSAKeyPool.this.m_keys.size() >= DSAKeyPool.this.m_poolSize) {
                        continue;
                    }
                }
                LogHolder.log(6, LogType.CRYPTO, "Creating DSA key pair " + (DSAKeyPool.this.m_keys.size() + 1) + " of " + DSAKeyPool.this.m_poolSize + "...");
                dSAKeyPair = DSAKeyPair.getInstance(new SecureRandom(), DSAKeyPool.this.m_keyLength, DSAKeyPool.this.m_certainty);
                LogHolder.log(6, LogType.CRYPTO, "DSA key pair " + (DSAKeyPool.this.m_keys.size() + 1) + " was created.");
                DSAKeyPool.this.m_keys.addElement(dSAKeyPair);
                LogHolder.log(6, LogType.CRYPTO, "DSA key pair " + DSAKeyPool.this.m_keys.size() + " was added to the pool of currently " + DSAKeyPool.this.m_keys.size() + ".");
            }
        }
    }
}

