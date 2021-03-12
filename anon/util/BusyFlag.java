/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public final class BusyFlag {
    protected Thread busyflag = null;
    protected int busycount = 0;

    public void getBusyFlag() {
        while (!this.tryGetBusyFlag()) {
            try {
                Thread.sleep(100L);
            }
            catch (Exception exception) {}
        }
    }

    public synchronized boolean tryGetBusyFlag() {
        if (this.busyflag == null) {
            this.busyflag = Thread.currentThread();
            this.busycount = 1;
            return true;
        }
        if (this.busyflag == Thread.currentThread()) {
            ++this.busycount;
            return true;
        }
        return false;
    }

    public synchronized void freeBusyFlag() {
        if (this.getBusyFlagOwner() == Thread.currentThread()) {
            --this.busycount;
            if (this.busycount == 0) {
                this.busyflag = null;
            }
        }
    }

    public synchronized Thread getBusyFlagOwner() {
        return this.busyflag;
    }
}

