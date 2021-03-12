/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.BusyFlag;

public final class CondVar {
    private BusyFlag SyncVar;

    public CondVar() {
        this(new BusyFlag());
    }

    public CondVar(BusyFlag busyFlag) {
        this.SyncVar = busyFlag;
    }

    public void cvWait() throws InterruptedException {
        this.cvTimedWait(this.SyncVar, 0);
    }

    public void cvWait(BusyFlag busyFlag) throws InterruptedException {
        this.cvTimedWait(busyFlag, 0);
    }

    public void cvTimedWait(int n) throws InterruptedException {
        this.cvTimedWait(this.SyncVar, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cvTimedWait(BusyFlag busyFlag, int n) throws InterruptedException {
        int n2 = 0;
        InterruptedException interruptedException = null;
        CondVar condVar = this;
        synchronized (condVar) {
            if (busyFlag.getBusyFlagOwner() != Thread.currentThread()) {
                throw new IllegalMonitorStateException("current thread not owner");
            }
            while (busyFlag.getBusyFlagOwner() == Thread.currentThread()) {
                ++n2;
                busyFlag.freeBusyFlag();
            }
            try {
                if (n == 0) {
                    this.wait();
                } else {
                    this.wait(n);
                }
            }
            catch (InterruptedException interruptedException2) {
                interruptedException = interruptedException2;
            }
        }
        while (n2 > 0) {
            busyFlag.getBusyFlag();
            --n2;
        }
        if (interruptedException != null) {
            throw interruptedException;
        }
    }

    public void cvSignal() {
        this.cvSignal(this.SyncVar);
    }

    public synchronized void cvSignal(BusyFlag busyFlag) {
        if (busyFlag.getBusyFlagOwner() != Thread.currentThread()) {
            throw new IllegalMonitorStateException("current thread not owner");
        }
        this.notify();
    }

    public void cvBroadcast() {
        this.cvBroadcast(this.SyncVar);
    }

    public synchronized void cvBroadcast(BusyFlag busyFlag) {
        if (busyFlag.getBusyFlagOwner() != Thread.currentThread()) {
            throw new IllegalMonitorStateException("current thread not owner");
        }
        this.notifyAll();
    }
}

