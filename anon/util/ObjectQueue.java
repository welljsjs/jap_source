/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class ObjectQueue {
    private QueueItem m_head = null;
    private QueueItem m_foot = null;
    private int m_size = 0;
    private volatile boolean m_bClosed;

    public int getSize() {
        return this.m_size;
    }

    public void close() {
        this.m_bClosed = true;
    }

    public synchronized void push(Object object) {
        QueueItem queueItem = new QueueItem(object);
        ++this.m_size;
        if (this.m_head == null) {
            this.m_head = queueItem;
            this.m_foot = queueItem;
        } else {
            this.m_head.m_previous = queueItem;
            this.m_head = queueItem;
        }
    }

    public synchronized Object pop() {
        Object object;
        if (this.m_head == null) {
            return null;
        }
        if (this.m_head == this.m_foot) {
            object = this.m_foot.m_object;
            this.m_head = null;
            this.m_foot = null;
        } else {
            object = this.m_foot.m_object;
            this.m_foot = this.m_foot.m_previous;
        }
        --this.m_size;
        return object;
    }

    public Object take() {
        while (!this.m_bClosed) {
            Object object = this.pop();
            if (object != null) {
                return object;
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {}
        }
        return null;
    }

    public Object poll(int n) throws InterruptedException {
        Object object = this.pop();
        if (object != null) {
            return object;
        }
        Thread.sleep(n);
        return this.pop();
    }

    public synchronized boolean isEmpty() {
        return this.m_size == 0;
    }

    private final class QueueItem {
        private Object m_object;
        private QueueItem m_previous = null;

        public QueueItem(Object object) {
            this.m_object = object;
            ObjectQueue.this.m_bClosed = false;
        }
    }
}

