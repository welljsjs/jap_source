/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.client;

import anon.util.IProgressCapsule;
import java.util.Observable;

public class ProgressCounter
extends Observable {
    private ProgressCount m_capsule = new ProgressCount(1000000);
    private static final int MAXIMUM_PROTOCOLMESSAGE_SIZE = 1000000;
    private static final int MINIMUM_PACKET = 0;

    public void setMax(int n) {
        this.m_capsule.setMaximum(n);
    }

    public void incrValue() {
        this.incrValue(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void incrValue(int n) {
        ProgressCount progressCount = this.m_capsule;
        synchronized (progressCount) {
            if (this.m_capsule.getStatus() == 1) {
                this.m_capsule.incrValue(n);
                this.setChanged();
                this.notifyObservers(this.m_capsule);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        ProgressCount progressCount = this.m_capsule;
        synchronized (progressCount) {
            this.m_capsule.setStatus(0);
        }
    }

    private class ProgressCount
    implements IProgressCapsule {
        private int m_maxPacket;
        private int m_counterValue;
        private int m_status;

        ProgressCount(int n) {
            this.m_maxPacket = n;
            this.reset();
        }

        public void reset() {
            this.m_counterValue = 0;
            this.m_status = 1;
        }

        public int getMinimum() {
            return 0;
        }

        public int getValue() {
            return this.m_counterValue;
        }

        public int getStatus() {
            return this.m_status;
        }

        public void setStatus(int n) {
            this.m_status = n;
        }

        public String getMessage() {
            return null;
        }

        public int getMaximum() {
            return this.m_maxPacket;
        }

        public void setMaximum(int n) {
            this.m_maxPacket = n;
        }

        public void incrValue(int n) {
            this.m_counterValue += n;
        }
    }
}

