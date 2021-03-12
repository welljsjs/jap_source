/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.util.IProgressCapsule;
import anon.util.IReturnRunnable;
import gui.GUIUtils;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import logging.LogHolder;
import logging.LogType;

public class WorkerContentPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable {
    public static final String IMG_BUSY = "busy.gif";
    public static final String MSG_PLEASE_WAIT = (class$gui$dialog$WorkerContentPane == null ? (class$gui$dialog$WorkerContentPane = WorkerContentPane.class$("gui.dialog.WorkerContentPane")) : class$gui$dialog$WorkerContentPane).getName() + "_pleaseWait";
    public static final String DOTS = "...";
    private final Object SYNC_INTERRUPT = new Object();
    private boolean m_bWasInterrupted = false;
    private boolean m_bIsBeingInterrupted = false;
    private Thread m_workerThread;
    private Runnable m_workerRunnable;
    private Thread m_internalThread;
    private boolean m_bInterruptThreadSafe = true;
    private int m_iProgressStatus;
    private JProgressBar progressBar;
    private Observable m_observable;
    private String m_strText;
    static /* synthetic */ Class class$gui$dialog$WorkerContentPane;

    public WorkerContentPane(JAPDialog jAPDialog, String string, Runnable runnable) {
        this(jAPDialog, string, "", null, runnable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, Runnable runnable, Observable observable) {
        this(jAPDialog, string, "", null, runnable, observable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, String string2, Runnable runnable) {
        this(jAPDialog, string, string2, null, runnable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, String string2, Runnable runnable, Observable observable) {
        this(jAPDialog, string, string2, null, runnable, observable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, DialogContentPane dialogContentPane, Runnable runnable) {
        this(jAPDialog, string, "", dialogContentPane, runnable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, DialogContentPane dialogContentPane, Runnable runnable, Observable observable) {
        this(jAPDialog, string, "", dialogContentPane, runnable, observable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, String string2, DialogContentPane dialogContentPane, Runnable runnable) {
        this(jAPDialog, string, string2, dialogContentPane, runnable, null);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, String string2, DialogContentPane dialogContentPane, Runnable runnable, Observable observable) {
        this(jAPDialog, string, new DialogContentPane.Layout(string2), dialogContentPane, runnable, observable);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, DialogContentPane dialogContentPane, Runnable runnable) {
        this(jAPDialog, string, layout, dialogContentPane, runnable, null);
    }

    public WorkerContentPane(JAPDialog jAPDialog, String string, DialogContentPane.Layout layout, DialogContentPane dialogContentPane, Runnable runnable, Observable observable) {
        super(jAPDialog, string, layout, new DialogContentPaneOptions(-2147483647, dialogContentPane));
        this.setDefaultButtonOperation(2);
        this.m_workerRunnable = runnable;
        this.m_strText = string;
        this.m_iProgressStatus = -1;
        this.addComponentListener(new WorkerComponentListener());
        this.getContentPane().setLayout(new BorderLayout());
        this.m_observable = observable;
        if (this.m_observable == null) {
            this.getContentPane().add((Component)new JLabel(GUIUtils.loadImageIcon(IMG_BUSY, true)), "Center");
        } else {
            this.progressBar = new JProgressBar();
            this.progressBar.setBorderPainted(true);
            this.progressBar.setStringPainted(true);
            this.getContentPane().add((Component)this.progressBar, "Center");
            Observer observer = new Observer(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void update(Observable observable, Object object) {
                    if (Thread.currentThread().isInterrupted() || WorkerContentPane.this.m_bWasInterrupted) {
                        return;
                    }
                    if (object != null && object instanceof IProgressCapsule) {
                        IProgressCapsule iProgressCapsule = (IProgressCapsule)object;
                        int n = iProgressCapsule.getValue();
                        int n2 = iProgressCapsule.getMaximum();
                        int n3 = iProgressCapsule.getMinimum();
                        int n4 = iProgressCapsule.getStatus();
                        if (iProgressCapsule.getMessage() != null) {
                            if (Thread.currentThread().isInterrupted()) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            if (iProgressCapsule.getStatus() != 0 || WorkerContentPane.this.progressBar.getValue() != WorkerContentPane.this.progressBar.getMaximum()) {
                                WorkerContentPane.this.setText(iProgressCapsule.getMessage());
                            }
                            if (Thread.currentThread().isInterrupted()) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        if (n4 == 1) {
                            JProgressBar jProgressBar = WorkerContentPane.this.progressBar;
                            synchronized (jProgressBar) {
                                if (WorkerContentPane.this.progressBar.getMaximum() != n2) {
                                    WorkerContentPane.this.progressBar.setMaximum(n2);
                                }
                                if (WorkerContentPane.this.progressBar.getMinimum() != n3) {
                                    WorkerContentPane.this.progressBar.setMinimum(n3);
                                }
                                WorkerContentPane.this.progressBar.setValue(n);
                                WorkerContentPane.this.progressBar.validate();
                            }
                        }
                        if (n4 == 0) {
                            JProgressBar jProgressBar = WorkerContentPane.this.progressBar;
                            synchronized (jProgressBar) {
                                WorkerContentPane.this.progressBar.setValue(WorkerContentPane.this.progressBar.getMaximum());
                                WorkerContentPane.this.progressBar.validate();
                            }
                        }
                        if (n4 == -1) {
                            JProgressBar jProgressBar = WorkerContentPane.this.progressBar;
                            synchronized (jProgressBar) {
                                WorkerContentPane.this.progressBar.setValue(WorkerContentPane.this.progressBar.getMinimum());
                                WorkerContentPane.this.progressBar.validate();
                            }
                        }
                        WorkerContentPane.this.m_iProgressStatus = n4;
                    }
                }
            };
            observable.addObserver(observer);
        }
    }

    public final boolean isInterruptThreadSafe() {
        return this.m_bInterruptThreadSafe;
    }

    private final void setInterruptThreadSafe(boolean bl) {
        this.m_bInterruptThreadSafe = bl;
    }

    public final void joinThread() {
        try {
            if (this.m_workerThread != null) {
                this.m_workerThread.join();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public final boolean isAlive() {
        try {
            return this.m_workerThread.isAlive();
        }
        catch (Exception exception) {
            return false;
        }
    }

    public final void joinThread(long l) {
        try {
            if (this.m_workerThread != null) {
                this.m_workerThread.join(l);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean isReady() {
        return true;
    }

    public boolean isSkippedAsPreviousContentPane() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DialogContentPane.CheckError checkCancel() {
        Object object = this.SYNC_INTERRUPT;
        synchronized (object) {
            this.setButtonValue(2);
            this.interruptWorkerThread(false);
        }
        this.reset();
        return null;
    }

    public DialogContentPane.CheckError checkUpdate() {
        while (this.m_bIsBeingInterrupted) {
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException interruptedException) {
                LogHolder.log(3, LogType.GUI, "Waiting for previous thread to finish...");
            }
        }
        this.m_bWasInterrupted = false;
        if (this.m_strText != null) {
            this.setText(this.m_strText);
        }
        return super.checkUpdate();
    }

    public synchronized void dispose() {
        super.dispose();
        this.interruptWorkerThread(false);
        this.m_internalThread = null;
    }

    public int getProgressStatus() {
        return this.m_iProgressStatus;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void interruptWorkerThread(boolean bl) {
        Object object = this.SYNC_INTERRUPT;
        synchronized (object) {
            boolean bl2 = bl && this.isInterruptThreadSafe();
            this.m_bIsBeingInterrupted = true;
            this.m_bWasInterrupted = true;
            final Thread thread = this.m_workerThread;
            Runnable runnable = new Runnable(){

                public void run() {
                    while (thread != null && thread.isAlive()) {
                        thread.interrupt();
                        Thread.yield();
                        WorkerContentPane.this.joinThread(200L);
                    }
                    WorkerContentPane.this.m_bIsBeingInterrupted = false;
                }
            };
            if (!bl2) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    this.joinThread(200L);
                }
                new Thread(runnable).start();
            } else {
                runnable.run();
            }
        }
    }

    public void reset() {
        if (this.m_workerRunnable != null && this.m_workerRunnable instanceof IResettable) {
            ((IResettable)((Object)this.m_workerRunnable)).reset();
        } else if (this.m_workerRunnable != null && this.m_workerRunnable instanceof IProgressCapsule) {
            ((IProgressCapsule)((Object)this.m_workerRunnable)).reset();
        }
        this.m_iProgressStatus = -1;
        if (this.progressBar != null) {
            this.progressBar.setValue(this.progressBar.getMinimum());
        }
    }

    public Object getValue() {
        if (this.m_workerRunnable instanceof IReturnRunnable && this.m_workerRunnable != null) {
            return ((IReturnRunnable)this.m_workerRunnable).getValue();
        }
        return null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class WorkerComponentListener
    extends ComponentAdapter
    implements Runnable {
        private WorkerComponentListener() {
        }

        public void componentHidden(ComponentEvent componentEvent) {
            if (WorkerContentPane.this.isReady()) {
                WorkerContentPane.this.interruptWorkerThread(true);
            }
        }

        public void componentShown(ComponentEvent componentEvent) {
            if (WorkerContentPane.this.m_workerRunnable == null) {
                WorkerContentPane.this.setButtonValue(0);
                WorkerContentPane.this.moveToNextContentPane();
                return;
            }
            while (WorkerContentPane.this.m_bIsBeingInterrupted) {
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException interruptedException) {
                    LogHolder.log(3, LogType.GUI, "Waiting for previous thread to finish...");
                }
            }
            WorkerContentPane.this.m_iProgressStatus = -1;
            if (WorkerContentPane.this.progressBar != null) {
                WorkerContentPane.this.progressBar.setValue(WorkerContentPane.this.progressBar.getMinimum());
            }
            WorkerContentPane.this.m_bWasInterrupted = false;
            if (WorkerContentPane.this.isVisible() && WorkerContentPane.this.isReady()) {
                WorkerContentPane.this.m_internalThread = new Thread((Runnable)this, "WorkerContentPane - componentShown()");
                WorkerContentPane.this.m_internalThread.setDaemon(true);
                WorkerContentPane.this.m_internalThread.start();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized void run() {
            if (WorkerContentPane.this.m_workerRunnable == null) {
                WorkerContentPane.this.interruptWorkerThread(true);
                WorkerContentPane.this.setButtonValue(0);
                WorkerContentPane.this.moveToNextContentPane();
                return;
            }
            WorkerContentPane.this.setButtonValue(Integer.MIN_VALUE);
            Object object = WorkerContentPane.this.SYNC_INTERRUPT;
            synchronized (object) {
                if (WorkerContentPane.this.m_bWasInterrupted) {
                    Object object2 = WorkerContentPane.this.SYNC_INTERRUPT;
                    synchronized (object2) {
                        WorkerContentPane.this.interruptWorkerThread(true);
                        WorkerContentPane.this.m_workerThread = null;
                        WorkerContentPane.this.moveToPreviousContentPane();
                    }
                    this.notifyAll();
                    return;
                }
                WorkerContentPane.this.m_workerThread = new InternalThread(WorkerContentPane.this.m_workerRunnable);
            }
            WorkerContentPane.this.m_workerThread.setPriority(1);
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException interruptedException) {
                Object object3 = WorkerContentPane.this.SYNC_INTERRUPT;
                synchronized (object3) {
                    WorkerContentPane.this.interruptWorkerThread(true);
                    WorkerContentPane.this.m_workerThread = null;
                    WorkerContentPane.this.moveToPreviousContentPane();
                }
                this.notifyAll();
                return;
            }
            WorkerContentPane.this.m_workerThread.start();
            while (WorkerContentPane.this.m_workerThread.isAlive()) {
                try {
                    WorkerContentPane.this.m_workerThread.join();
                }
                catch (InterruptedException interruptedException) {}
            }
            if (!WorkerContentPane.this.m_workerThread.isInterrupted() && !WorkerContentPane.this.m_bWasInterrupted && WorkerContentPane.this.progressBar != null && WorkerContentPane.this.m_observable instanceof IProgressCapsule && ((IProgressCapsule)((Object)WorkerContentPane.this.m_observable)).getStatus() == 0) {
                object = WorkerContentPane.this.progressBar;
                synchronized (object) {
                    WorkerContentPane.this.progressBar.setValue(WorkerContentPane.this.progressBar.getMaximum());
                    WorkerContentPane.this.progressBar.validate();
                }
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            boolean bl = false;
            boolean bl2 = false;
            Object object4 = WorkerContentPane.this.SYNC_INTERRUPT;
            synchronized (object4) {
                if (!WorkerContentPane.this.m_bIsBeingInterrupted) {
                    if (WorkerContentPane.this.m_bWasInterrupted || WorkerContentPane.this.m_workerThread.isInterrupted() || WorkerContentPane.this.getButtonValue() == 2 || WorkerContentPane.this.getButtonValue() == -1 || WorkerContentPane.this.m_observable instanceof IProgressCapsule && ((IProgressCapsule)((Object)WorkerContentPane.this.m_observable)).getStatus() == 2) {
                        if (WorkerContentPane.this.getButtonValue() == Integer.MIN_VALUE) {
                            bl = true;
                        }
                    } else {
                        bl2 = true;
                    }
                    WorkerContentPane.this.interruptWorkerThread(true);
                    WorkerContentPane.this.m_workerThread = null;
                }
            }
            if (bl) {
                if ((WorkerContentPane.this.getDefaultButtonOperation() & 0x8002) > 0) {
                    WorkerContentPane.this.closeDialog(true);
                } else if ((WorkerContentPane.this.getDefaultButtonOperation() & 0x1001) > 0) {
                    WorkerContentPane.this.closeDialog(false);
                } else if ((WorkerContentPane.this.getDefaultButtonOperation() & 0x240) > 0) {
                    WorkerContentPane.this.moveToPreviousContentPane();
                } else if ((WorkerContentPane.this.getDefaultButtonOperation() & 0x24) > 0) {
                    WorkerContentPane.this.moveToNextContentPane();
                }
            } else if (bl2) {
                WorkerContentPane.this.setButtonValue(0);
                WorkerContentPane.this.moveToNextContentPane();
            }
            this.notifyAll();
        }
    }

    private class InternalThread
    extends Thread {
        private Runnable m_runnable;
        private boolean m_bInterrupted = false;

        public InternalThread(Runnable runnable) {
            super(runnable, "WorkerContentPane - InternalThread");
            this.m_runnable = runnable;
        }

        public void run() {
            this.m_runnable.run();
            this.m_bInterrupted = this.isInterrupted();
        }

        public boolean isInterrupted() {
            return super.isInterrupted() || this.m_bInterrupted;
        }
    }

    public static interface IResettableReturnRunnable
    extends IResettable,
    IReturnRunnable {
    }

    public static interface IResettable {
        public void reset();
    }
}

