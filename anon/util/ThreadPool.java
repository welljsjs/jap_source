/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.BusyFlag;
import anon.util.CondVar;
import java.util.Vector;

public final class ThreadPool {
    private Vector objects;
    private int nObjects = 0;
    private int nMaxThreads = 0;
    private CondVar cvAvailable;
    private CondVar cvEmpty;
    private BusyFlag cvFlag = new BusyFlag();
    private ThreadPoolThread[] poolThreads;
    private boolean terminated = false;

    public ThreadPool(String string, int n) {
        this(string, n, 5);
    }

    public ThreadPool(String string, int n, int n2) {
        this.cvAvailable = new CondVar(this.cvFlag);
        this.cvEmpty = new CondVar(this.cvFlag);
        this.objects = new Vector();
        this.nMaxThreads = n;
        this.poolThreads = new ThreadPoolThread[n];
        if (string == null) {
            string = "";
        }
        for (int i = 0; i < n; ++i) {
            this.poolThreads[i] = new ThreadPoolThread(this, i, string);
            this.poolThreads[i].setPriority(n2);
            this.poolThreads[i].setDaemon(true);
            this.poolThreads[i].start();
        }
    }

    public void shutdown() {
        for (int i = 0; i < this.poolThreads.length; ++i) {
            this.poolThreads[i].shutdown();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void add(Runnable runnable, Object object) {
        try {
            this.cvFlag.getBusyFlag();
            if (this.terminated) {
                throw new IllegalStateException("Thread pool has shutdown");
            }
            this.objects.addElement(new ThreadPoolRequest(runnable, object));
            ++this.nObjects;
            this.cvAvailable.cvSignal();
            while (this.nObjects > this.nMaxThreads) {
                try {
                    this.cvEmpty.cvWait();
                }
                catch (InterruptedException interruptedException) {}
            }
            Object var5_4 = null;
            this.cvFlag.freeBusyFlag();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            this.cvFlag.freeBusyFlag();
            throw throwable;
        }
    }

    public void addRequest(Runnable runnable) {
        this.add(runnable, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addRequestAndWait(Runnable runnable) throws InterruptedException {
        Object object;
        Object object2 = object = new Object();
        synchronized (object2) {
            this.add(runnable, object);
            object.wait();
        }
    }

    private final class ThreadPoolThread
    extends Thread {
        ThreadPool parent;
        boolean shouldRun = true;

        ThreadPoolThread(ThreadPool threadPool2, int n, String string) {
            super(string + " - ThreadPoolThread " + n);
            this.parent = threadPool2;
        }

        public void shutdown() {
            this.shouldRun = false;
            while (this.isAlive()) {
                this.interrupt();
                Thread.yield();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            ThreadPoolRequest threadPoolRequest = null;
            while (this.shouldRun) {
                Object var6_8;
                Object var4_7;
                try {
                    this.parent.cvFlag.getBusyFlag();
                    while (threadPoolRequest == null && this.shouldRun) {
                        try {
                            threadPoolRequest = (ThreadPoolRequest)this.parent.objects.elementAt(0);
                            this.parent.objects.removeElementAt(0);
                        }
                        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                            threadPoolRequest = null;
                        }
                        catch (ClassCastException classCastException) {
                            threadPoolRequest = null;
                        }
                        if (threadPoolRequest != null) continue;
                        try {
                            this.parent.cvAvailable.cvWait();
                        }
                        catch (InterruptedException interruptedException) {
                            var4_7 = null;
                            this.parent.cvFlag.freeBusyFlag();
                            return;
                        }
                    }
                    var4_7 = null;
                    this.parent.cvFlag.freeBusyFlag();
                }
                catch (Throwable throwable) {
                    var4_7 = null;
                    this.parent.cvFlag.freeBusyFlag();
                    throw throwable;
                }
                if (!this.shouldRun) {
                    return;
                }
                try {
                    threadPoolRequest.target.run();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    this.parent.cvFlag.getBusyFlag();
                    ThreadPool.this.nObjects--;
                    if (ThreadPool.this.nObjects < ThreadPool.this.nMaxThreads) {
                        this.parent.cvEmpty.cvSignal();
                    }
                    var6_8 = null;
                    this.parent.cvFlag.freeBusyFlag();
                }
                catch (Throwable throwable) {
                    var6_8 = null;
                    this.parent.cvFlag.freeBusyFlag();
                    throw throwable;
                }
                if (threadPoolRequest.lock != null) {
                    Object object = threadPoolRequest.lock;
                    synchronized (object) {
                        threadPoolRequest.lock.notify();
                    }
                }
                threadPoolRequest = null;
            }
        }
    }

    private final class ThreadPoolRequest {
        Runnable target;
        Object lock;

        ThreadPoolRequest(Runnable runnable, Object object) {
            this.target = runnable;
            this.lock = object;
        }
    }
}

