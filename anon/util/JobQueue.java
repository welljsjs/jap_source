/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class JobQueue {
    private Vector m_jobs = new Vector();
    private Vector m_jobThreads = new Vector();
    private Thread m_threadQueue;
    private boolean m_bInterrupted = false;
    private Job m_currentJob;
    private Thread m_currentJobThread;

    public JobQueue(String string) {
        this.m_threadQueue = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Thread thread = Thread.currentThread();
                synchronized (thread) {
                    while (!Thread.currentThread().isInterrupted() && !JobQueue.this.m_bInterrupted) {
                        try {
                            Thread.currentThread().wait();
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            Thread.currentThread().notifyAll();
                            break;
                        }
                        if (JobQueue.this.m_jobs.size() > 0 && JobQueue.this.m_currentJob == JobQueue.this.m_jobs.firstElement() && JobQueue.this.m_currentJobThread.isAlive()) {
                            if (!((Job)JobQueue.this.m_jobs.lastElement()).isInterrupting()) continue;
                            while (JobQueue.this.m_currentJobThread.isAlive()) {
                                JobQueue.this.m_currentJobThread.interrupt();
                                try {
                                    Thread.currentThread().wait(100L);
                                }
                                catch (InterruptedException interruptedException) {
                                    // empty catch block
                                    break;
                                }
                            }
                        }
                        if (JobQueue.this.m_jobs.size() <= 0) continue;
                        while (JobQueue.this.m_jobs.size() > 1) {
                            JobQueue.this.m_jobs.removeElementAt(0);
                            JobQueue.this.m_jobThreads.removeElementAt(0);
                        }
                        JobQueue.this.m_currentJob = (Job)JobQueue.this.m_jobs.elementAt(0);
                        JobQueue.this.m_currentJobThread = (Thread)JobQueue.this.m_jobThreads.elementAt(0);
                        try {
                            JobQueue.this.m_currentJobThread.start();
                        }
                        catch (IllegalThreadStateException illegalThreadStateException) {
                            LogHolder.log(3, LogType.MISC, illegalThreadStateException);
                            JobQueue.this.m_currentJobThread.interrupt();
                            JobQueue.this.removeAllJobs();
                        }
                    }
                    while (JobQueue.this.m_jobs.size() > 0) {
                        if (JobQueue.this.m_currentJob == JobQueue.this.m_jobs.firstElement()) {
                            JobQueue.this.m_currentJobThread.interrupt();
                            try {
                                Thread.currentThread().wait(500L);
                            }
                            catch (InterruptedException interruptedException) {}
                            continue;
                        }
                        JobQueue.this.m_jobs.removeAllElements();
                        JobQueue.this.m_jobThreads.removeAllElements();
                    }
                }
            }
        }, string);
        this.m_threadQueue.setDaemon(true);
        this.m_threadQueue.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addJob(Job job) {
        Thread thread = null;
        Job job2 = null;
        if (job == null) {
            return;
        }
        if (!job.isSkippedIfDuplicate() && this.m_bInterrupted) {
            return;
        }
        if (job.m_queue != null) {
            return;
        }
        Thread thread2 = this.m_threadQueue;
        synchronized (thread2) {
            Object object;
            if (job.isSkippedIfDuplicate() && this.m_jobs.contains(job)) {
                return;
            }
            if (this.m_jobs.size() > 0) {
                object = (Job)this.m_jobs.lastElement();
                if (((Job)object).isSkippedIfDuplicate() && job.isSkippedIfDuplicate()) {
                    return;
                }
                while (this.m_jobs.size() > 0) {
                    job2 = (Job)this.m_jobs.lastElement();
                    this.removeJob(job2, false);
                }
            }
            thread = new Thread((Runnable)job, "JobQueue Job");
            thread.setDaemon(true);
            job.m_queue = this;
            this.m_jobs.addElement(job);
            this.m_jobThreads.addElement(thread);
            this.m_threadQueue.notify();
            object = job.getAddedJobLogMessage();
            if (object != null) {
                LogHolder.log(7, LogType.MISC, (String)object);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        while (this.m_threadQueue.isAlive()) {
            this.m_threadQueue.interrupt();
            Thread thread = this.m_threadQueue;
            synchronized (thread) {
                this.m_bInterrupted = true;
                this.m_threadQueue.notifyAll();
                this.m_threadQueue.interrupt();
            }
            try {
                this.m_threadQueue.join(500L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllJobs() {
        Thread thread = this.m_threadQueue;
        synchronized (thread) {
            this.m_jobs.removeAllElements();
            this.m_jobThreads.removeAllElements();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeJob(Job job, boolean bl) {
        if (job == null) {
            return;
        }
        Thread thread = this.m_threadQueue;
        synchronized (thread) {
            int n = this.m_jobs.indexOf(job);
            if (n >= 0) {
                ((Thread)this.m_jobThreads.elementAt(n)).interrupt();
                this.m_jobs.removeElementAt(n);
                this.m_jobThreads.removeElementAt(n);
                if (bl) {
                    this.m_threadQueue.notify();
                }
            }
        }
    }

    public static abstract class Job
    implements Runnable {
        private boolean m_bMayBeSkippedIfDuplicate;
        private JobQueue m_queue;

        public Job(boolean bl) {
            this.m_bMayBeSkippedIfDuplicate = bl;
        }

        public Job() {
            this(false);
        }

        public abstract void runJob();

        public final void run() {
            this.runJob();
            if (this.m_queue != null) {
                this.m_queue.removeJob(this, true);
            }
        }

        public String getAddedJobLogMessage() {
            return null;
        }

        public boolean isInterrupting() {
            return false;
        }

        public final boolean isSkippedIfDuplicate() {
            return this.m_bMayBeSkippedIfDuplicate;
        }
    }
}

