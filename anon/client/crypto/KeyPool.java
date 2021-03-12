/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import java.security.SecureRandom;
import logging.LogHolder;
import logging.LogType;

public final class KeyPool
implements Runnable {
    private SecureRandom m_SecureRandom;
    private KeyList m_keylistPool;
    private KeyList m_keylistAktKey;
    private int m_iKeySize;
    private int m_iPoolSize;
    private Object l1;
    private Object l2;
    private volatile boolean m_bRun;
    private static KeyPool m_KeyPool = null;
    private Thread m_KeyPoolThread = null;
    private boolean m_bDebug = false;

    private KeyPool(int n, int n2, boolean bl) {
        this.m_bDebug = bl;
        this.m_iKeySize = n2;
        this.m_iPoolSize = n;
        this.m_keylistPool = null;
        this.m_keylistAktKey = null;
        this.l1 = new Object();
        this.l2 = new Object();
        this.m_bRun = true;
        this.m_KeyPoolThread = new Thread((Runnable)this, "JAP - KeyPool");
        this.m_KeyPoolThread.setDaemon(true);
        this.m_KeyPoolThread.setPriority(1);
        this.m_KeyPoolThread.start();
    }

    public static synchronized KeyPool start(boolean bl) {
        if (m_KeyPool == null) {
            m_KeyPool = new KeyPool(20, 16, bl);
        } else if (KeyPool.m_KeyPool.m_bDebug != bl) {
            m_KeyPool.stop();
            m_KeyPool = new KeyPool(20, 16, bl);
        }
        return m_KeyPool;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stop() {
        this.m_bRun = false;
        Object object = this.l1;
        synchronized (object) {
            this.l1.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        KeyList keyList;
        byte[] arrby = null;
        try {
            arrby = SecureRandom.getSeed(20);
            this.m_SecureRandom = new SecureRandom(arrby);
        }
        catch (Throwable throwable) {
            this.m_SecureRandom = new SecureRandom();
        }
        arrby = null;
        this.m_keylistPool = new KeyList(this.m_iKeySize);
        for (int i = 1; i < this.m_iPoolSize; ++i) {
            keyList = new KeyList(this.m_iKeySize);
            keyList.next = this.m_keylistPool;
            this.m_keylistPool = keyList;
        }
        this.m_keylistAktKey = null;
        this.m_bRun = true;
        while (this.m_bRun) {
            if (this.m_keylistPool != null) {
                KeyPool keyPool = this;
                synchronized (keyPool) {
                    if (!this.m_bDebug) {
                        this.m_SecureRandom.nextBytes(this.m_keylistPool.key);
                    }
                    keyList = this.m_keylistPool;
                    this.m_keylistPool = this.m_keylistPool.next;
                    keyList.next = this.m_keylistAktKey;
                    this.m_keylistAktKey = keyList;
                    Object object = this.l2;
                    synchronized (object) {
                        this.l2.notify();
                    }
                }
            }
            try {
                Object object = this.l1;
                synchronized (object) {
                    this.l1.wait();
                }
            }
            catch (InterruptedException interruptedException) {
                LogHolder.log(7, LogType.MISC, "JAPKeyPool:run() waiting interrupted!");
            }
        }
    }

    public static int getKey(byte[] arrby) {
        return KeyPool.getKey(arrby, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getKey(byte[] arrby, int n) {
        Object object;
        if (m_KeyPool == null || arrby == null || arrby.length - n < KeyPool.m_KeyPool.m_iKeySize) {
            return -1;
        }
        if (KeyPool.m_KeyPool.m_keylistAktKey == null) {
            try {
                object = KeyPool.m_KeyPool.l2;
                synchronized (object) {
                    KeyPool.m_KeyPool.l2.wait();
                }
            }
            catch (InterruptedException interruptedException) {
                LogHolder.log(7, LogType.MISC, "JAPKeyPool:getKey() waiting interrupted!");
            }
        }
        object = m_KeyPool;
        synchronized (object) {
            System.arraycopy(KeyPool.m_KeyPool.m_keylistAktKey.key, 0, arrby, n, KeyPool.m_KeyPool.m_iKeySize);
            KeyList keyList = KeyPool.m_KeyPool.m_keylistAktKey;
            KeyPool.m_KeyPool.m_keylistAktKey = KeyPool.m_KeyPool.m_keylistAktKey.next;
            keyList.next = KeyPool.m_KeyPool.m_keylistPool;
            KeyPool.m_KeyPool.m_keylistPool = keyList;
        }
        object = KeyPool.m_KeyPool.l1;
        synchronized (object) {
            KeyPool.m_KeyPool.l1.notify();
        }
        return 0;
    }

    private final class KeyList {
        public byte[] key;
        public KeyList next;

        public KeyList(int n) {
            this.key = new byte[n];
            this.next = null;
        }
    }
}

