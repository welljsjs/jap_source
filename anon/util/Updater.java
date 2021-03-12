/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.ClassUtil;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public abstract class Updater
implements Observer {
    private static final long MIN_WAITING_TIME_MS = 20000L;
    private IUpdateInterval m_updateInterval;
    private Thread m_updateThread;
    private boolean m_bAutoUpdateChanged = false;
    private boolean m_bInitialRun = true;
    private boolean m_interrupted = false;
    private boolean m_bUpdating = false;
    private Object UPDATE_SYNC = new Object();
    private ObservableInfo m_observable;
    private Vector m_queueUpdatersToCall = new Vector();

    public Updater(IUpdateInterval iUpdateInterval, ObservableInfo observableInfo) {
        if (iUpdateInterval == null) {
            throw new IllegalArgumentException("No update interval specified!");
        }
        if (observableInfo == null) {
            throw new IllegalArgumentException("No ObservableInfo specified!");
        }
        this.m_observable = observableInfo;
        this.m_updateInterval = iUpdateInterval;
        this.init();
    }

    private final void init() {
        this.m_observable.getObservable().addObserver(this);
        this.m_updateThread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                long l = System.currentTimeMillis();
                LogHolder.log(6, LogType.DB, Updater.this.getUpdatedClassName() + "update thread started.");
                while (!Thread.currentThread().isInterrupted() && !Updater.this.m_interrupted) {
                    Object object = Thread.currentThread();
                    synchronized (object) {
                        Updater.this.m_bAutoUpdateChanged = true;
                        while (Updater.this.m_bAutoUpdateChanged) {
                            Updater.this.m_bAutoUpdateChanged = false;
                            try {
                                Thread.currentThread().notify();
                                if (Updater.this.m_observable.isUpdateDisabled() || Updater.this.m_bInitialRun) {
                                    Thread.currentThread().wait();
                                } else {
                                    long l2 = Math.max(Updater.this.m_updateInterval.getUpdateInterval() - (System.currentTimeMillis() - l), 20000L);
                                    LogHolder.log(5, LogType.DB, "Update waiting time for " + Updater.this.getUpdatedClass().getName() + ": " + l2);
                                    Thread.currentThread().wait(l2);
                                }
                            }
                            catch (InterruptedException interruptedException) {
                                Thread.currentThread().notifyAll();
                                break;
                            }
                            if (Thread.currentThread().isInterrupted()) {
                                Thread.currentThread().notifyAll();
                                break;
                            }
                            if (!Updater.this.m_interrupted) continue;
                        }
                    }
                    if (Thread.currentThread().isInterrupted() || Updater.this.m_interrupted || Updater.this.isUpdatePaused()) continue;
                    LogHolder.log(6, LogType.DB, "Updating " + Updater.this.getUpdatedClassName() + "list.");
                    l = System.currentTimeMillis();
                    Updater.this.updateInternal();
                    object = Updater.this.m_queueUpdatersToCall;
                    synchronized (object) {
                        if (Updater.this.m_queueUpdatersToCall.size() > 0) {
                            Updater updater = (Updater)Updater.this.m_queueUpdatersToCall.elementAt(0);
                            Updater.this.m_queueUpdatersToCall.removeElementAt(0);
                            updater.update(true, (Vector)Updater.this.m_queueUpdatersToCall.clone());
                            Updater.this.m_queueUpdatersToCall.removeAllElements();
                        }
                    }
                }
                LogHolder.log(6, LogType.DB, Updater.this.getUpdatedClassName() + "update thread stopped.");
            }
        }, this.getUpdatedClassName() + "Update Thread");
        this.m_updateThread.setPriority(1);
        this.m_updateThread.setDaemon(true);
        this.m_updateThread.start();
    }

    protected ObservableInfo getObservableInfo() {
        return this.m_observable;
    }

    public void update(Observable observable, Object object) {
        if (!(object instanceof Integer) || !((Integer)object).equals(this.m_observable.getUpdateChanged())) {
            return;
        }
        final Updater updater = this;
        if (!this.m_observable.isUpdateDisabled()) {
            new Thread(new Runnable(){

                public void run() {
                    if (Updater.this.m_observable.updateImmediately()) {
                        updater.update(false, null);
                    } else {
                        updater.start(false);
                    }
                }
            }).start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start(boolean bl) {
        Object object = this.UPDATE_SYNC;
        synchronized (object) {
            if (this.m_bUpdating) {
                return;
            }
            this.m_bUpdating = true;
        }
        object = this;
        synchronized (object) {
            Thread thread = this.m_updateThread;
            synchronized (thread) {
                this.m_bAutoUpdateChanged = true;
                this.m_bInitialRun = false;
                this.m_updateThread.notifyAll();
                if (bl) {
                    try {
                        this.m_updateThread.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
        }
        object = this.UPDATE_SYNC;
        synchronized (object) {
            this.m_bUpdating = false;
        }
    }

    public final boolean update() {
        return this.update(true, null);
    }

    public final boolean update(Vector vector) {
        return this.update(true, vector);
    }

    public final void updateAsync(final Vector vector) {
        Thread thread = new Thread(new Runnable(){

            public void run() {
                Updater.this.update(false, vector);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean update(boolean bl, Vector vector) {
        Object object;
        Object object2;
        if (this.m_bInitialRun) {
            this.start(true);
        }
        if (vector != null) {
            object2 = vector;
            synchronized (object2) {
                object = this.m_queueUpdatersToCall;
                synchronized (object) {
                    for (int i = 0; i < vector.size(); ++i) {
                        if (vector.elementAt(i) == null || !(vector.elementAt(i) instanceof Updater)) continue;
                        this.m_queueUpdatersToCall.addElement(vector.elementAt(i));
                    }
                }
            }
        }
        object2 = this;
        synchronized (object2) {
            object = this.m_updateThread;
            synchronized (object) {
                if (this.m_updateThread.isInterrupted() || this.m_interrupted) {
                    return false;
                }
                this.m_bAutoUpdateChanged = false;
                this.m_updateThread.notifyAll();
                if (bl) {
                    try {
                        this.m_updateThread.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        LogHolder.log(3, LogType.NET, interruptedException);
                        return false;
                    }
                    return this.wasUpdateSuccessful();
                }
                return true;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void stop() {
        this.m_observable.getObservable().deleteObserver(this);
        if (this.m_updateThread == null) {
            return;
        }
        while (this.m_updateThread.isAlive()) {
            this.m_updateThread.interrupt();
            Thread thread = this.m_updateThread;
            synchronized (thread) {
                this.m_bAutoUpdateChanged = false;
                this.m_bInitialRun = false;
                this.m_interrupted = true;
                this.m_updateThread.notifyAll();
                this.m_updateThread.interrupt();
            }
            try {
                this.m_updateThread.join(500L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public final IUpdateInterval getUpdateInterval() {
        return this.m_updateInterval;
    }

    public abstract Class getUpdatedClass();

    protected abstract boolean wasUpdateSuccessful();

    public abstract long getLastUpdate();

    public boolean needsUpdate() {
        return !this.isFirstUpdateDone() || System.currentTimeMillis() < this.getLastUpdate() || System.currentTimeMillis() - this.getLastUpdate() > this.m_updateInterval.getUpdateInterval();
    }

    public final boolean isFirstUpdateDone() {
        return this.getLastUpdate() <= System.currentTimeMillis();
    }

    protected abstract void updateInternal();

    protected boolean isUpdatePaused() {
        return false;
    }

    protected final String getUpdatedClassName() {
        return ClassUtil.getShortClassName(this.getUpdatedClass()) + " ";
    }

    protected static interface IUpdateInterval {
        public long getUpdateInterval();
    }

    protected static final class ConstantUpdateInterval
    implements IUpdateInterval {
        private long m_updateInterval;

        public ConstantUpdateInterval(long l) {
            this.m_updateInterval = l;
        }

        public long getUpdateInterval() {
            return this.m_updateInterval;
        }
    }

    protected static class DynamicUpdateInterval
    implements IUpdateInterval {
        private long m_updateInterval;

        public DynamicUpdateInterval(long l) {
            this.setUpdateInterval(l);
        }

        public void setUpdateInterval(long l) {
            this.m_updateInterval = l;
        }

        public long getUpdateInterval() {
            return this.m_updateInterval;
        }
    }

    public static class ObservableInfoContainer
    extends ObservableInfo {
        private ObservableInfo m_observableInfo;

        public ObservableInfoContainer(ObservableInfo observableInfo) {
            super(observableInfo.getObservable());
            this.m_observableInfo = observableInfo;
        }

        public void notifyAdditionalObserversOnUpdate(Class class_) {
            this.m_observableInfo.notifyAdditionalObserversOnUpdate(class_);
        }

        public boolean updateImmediately() {
            return this.m_observableInfo.updateImmediately();
        }

        public Integer getUpdateChanged() {
            return this.m_observableInfo.getUpdateChanged();
        }

        public boolean isUpdateDisabled() {
            return this.m_observableInfo.isUpdateDisabled();
        }
    }

    public static abstract class ObservableInfo {
        private Observable m_observable;

        public ObservableInfo(Observable observable) {
            if (observable == null) {
                throw new IllegalArgumentException("No Observable specified!");
            }
            this.m_observable = observable;
        }

        public void notifyAdditionalObserversOnUpdate(Class class_) {
        }

        public boolean updateImmediately() {
            return false;
        }

        public final Observable getObservable() {
            return this.m_observable;
        }

        public abstract Integer getUpdateChanged();

        public abstract boolean isUpdateDisabled();
    }
}

