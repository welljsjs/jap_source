/*
 * Decompiled with CFR 0.150.
 */
package anon.client.replay;

import anon.client.MixParameters;
import anon.client.replay.ReplayControlChannel;
import anon.client.replay.ReplayTimestamp;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class TimestampUpdater
implements Observer {
    private MixParameters[] m_mixParameters;
    private boolean m_responseReceived;
    private Object m_internalSynchronization;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TimestampUpdater(MixParameters[] arrmixParameters, ReplayControlChannel replayControlChannel) throws Exception {
        this.m_mixParameters = arrmixParameters;
        this.m_responseReceived = false;
        Object object = this.m_internalSynchronization = new Object();
        synchronized (object) {
            replayControlChannel.getMessageDistributor().addObserver(this);
            replayControlChannel.requestTimestamps();
            while (!this.m_responseReceived) {
                this.m_internalSynchronization.wait();
            }
        }
        for (int i = 0; i < this.m_mixParameters.length; ++i) {
            if (this.m_mixParameters[i].getReplayTimestamp() != null) continue;
            throw new Exception("TimestampUpdater: Constructor: Timestamp of Mix '" + this.m_mixParameters[i].getMixId() + "' is missing.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        if (object instanceof Vector) {
            LogHolder.log(7, LogType.NET, "TimestampUpdater: update(): Received some timestamps.");
            Enumeration enumeration = ((Vector)object).elements();
            Vector<Integer> vector = new Vector<Integer>();
            while (enumeration.hasMoreElements()) {
                ReplayTimestamp replayTimestamp = (ReplayTimestamp)enumeration.nextElement();
                boolean bl = false;
                for (int i = 0; i < this.m_mixParameters.length && !bl; ++i) {
                    if (!this.m_mixParameters[i].getMixId().equals(replayTimestamp.getMixId())) continue;
                    this.m_mixParameters[i].setReplayTimestamp(replayTimestamp);
                    bl = true;
                    if (vector.contains(new Integer(i))) {
                        LogHolder.log(6, LogType.NET, "TimestampUpdater: update(): Received timestamp for Mix '" + replayTimestamp.getMixId() + "' twice.");
                        continue;
                    }
                    vector.addElement(new Integer(i));
                }
                if (bl) continue;
                LogHolder.log(6, LogType.NET, "TimestampUpdater: update(): Received timestamp of Mix '" + replayTimestamp.getMixId() + "' is not necessary for the current cascade.");
            }
            for (int i = 0; i < this.m_mixParameters.length; ++i) {
                if (vector.contains(new Integer(i))) continue;
                LogHolder.log(3, LogType.NET, "TimestampUpdater: update(): Timestamp of Mix '" + this.m_mixParameters[i].getMixId() + "' is missing.");
            }
            Object object2 = this.m_internalSynchronization;
            synchronized (object2) {
                this.m_responseReceived = true;
                this.m_internalSynchronization.notifyAll();
            }
        }
        if (object instanceof Exception) {
            LogHolder.log(3, LogType.NET, "TimestampUpdater: update(): Received exception: " + object.toString());
            Object object3 = this.m_internalSynchronization;
            synchronized (object3) {
                this.m_responseReceived = true;
                this.m_internalSynchronization.notifyAll();
            }
        }
    }
}

