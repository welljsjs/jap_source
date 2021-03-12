/*
 * Decompiled with CFR 0.150.
 */
package gui;

import javax.swing.SwingUtilities;
import logging.LogHolder;
import logging.LogType;

public class AWTUpdateQueue {
    private Runnable m_awtRunnable;
    private int m_jobs;
    private Object JOB_LOCK = new Object();
    private Object UPDATE_LOCK = new Object();

    public AWTUpdateQueue(Runnable runnable) {
        this.m_awtRunnable = runnable;
        this.m_jobs = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(boolean bl) {
        Object object = this.JOB_LOCK;
        synchronized (object) {
            if (this.m_jobs >= 2 && !bl) {
                return;
            }
            ++this.m_jobs;
        }
        object = new Thread(new Runnable(){

            public void run() {
                AWTUpdateQueue.this.doUpdateQueue();
            }
        });
        ((Thread)object).setDaemon(true);
        ((Thread)object).start();
        if (bl) {
            try {
                ((Thread)object).join();
            }
            catch (InterruptedException interruptedException) {
                LogHolder.log(3, LogType.GUI, interruptedException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doUpdateQueue() {
        Runnable runnable = new Runnable(){

            public void run() {
                AWTUpdateQueue.this.m_awtRunnable.run();
                AWTUpdateQueue.this.m_jobs--;
            }
        };
        Object object = this.UPDATE_LOCK;
        synchronized (object) {
            try {
                SwingUtilities.invokeAndWait(runnable);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.GUI, exception);
            }
        }
    }
}

