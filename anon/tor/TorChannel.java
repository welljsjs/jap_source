/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.shared.AbstractChannel;
import anon.tor.Circuit;
import anon.tor.cells.Cell;
import anon.tor.cells.RelayCell;
import anon.util.ByteArrayUtil;
import java.io.IOException;
import logging.LogHolder;
import logging.LogType;

public class TorChannel
extends AbstractChannel {
    private static final int MAX_CELL_DATA = 498;
    protected Circuit m_circuit;
    private volatile int m_recvcellcounter;
    private volatile int m_sendcellcounter;
    private volatile int m_iSendRelayCellsWaitingForDelivery = 0;
    private volatile boolean m_bChannelCreated;
    private volatile boolean m_bCreateError;
    private Object m_oWaitForOpen = new Object();
    private Object m_oSyncSendCellCounter = new Object();
    private Object m_oSyncSend = new Object();
    private Object m_oSyncSendRelayCellsWaitingForDelivery = new Object();
    private volatile boolean m_bDoNotCloseChannelOnError = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addToSendCellCounter(int n) {
        Object object = this.m_oSyncSendCellCounter;
        synchronized (object) {
            this.m_sendcellcounter += n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void decreaseSendRelayCellsWaitingForDelivery() {
        Object object = this.m_oSyncSendRelayCellsWaitingForDelivery;
        synchronized (object) {
            --this.m_iSendRelayCellsWaitingForDelivery;
        }
    }

    protected void setStreamID(int n) {
        this.m_id = n;
    }

    protected void setCircuit(Circuit circuit) {
        this.m_circuit = circuit;
    }

    public int getOutputBlockSize() {
        return 498;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void send(byte[] arrby, int n) throws IOException {
        if (this.m_bIsClosed || this.m_bIsClosedByPeer) {
            throw new IOException("Tor channel is closed");
        }
        Object object = this.m_oSyncSend;
        synchronized (object) {
            byte[] arrby2 = arrby;
            while (n != 0 && !this.m_bIsClosed) {
                RelayCell relayCell;
                if (n > 498) {
                    relayCell = new RelayCell(this.m_circuit.getCircID(), 2, this.m_id, ByteArrayUtil.copy(arrby2, 0, 498));
                    arrby2 = ByteArrayUtil.copy(arrby2, 498, n - 498);
                    n -= 498;
                } else {
                    relayCell = new RelayCell(this.m_circuit.getCircID(), 2, this.m_id, ByteArrayUtil.copy(arrby2, 0, n));
                    n = 0;
                }
                try {
                    while (!(this.m_sendcellcounter > 0 && this.m_iSendRelayCellsWaitingForDelivery <= 10 || this.m_bIsClosed || this.m_bIsClosedByPeer)) {
                        try {
                            Thread.sleep(100L);
                        }
                        catch (Exception exception) {}
                    }
                    Object object2 = this.m_oSyncSendRelayCellsWaitingForDelivery;
                    synchronized (object2) {
                        ++this.m_iSendRelayCellsWaitingForDelivery;
                    }
                    this.m_circuit.send(relayCell);
                }
                catch (Throwable throwable) {
                    throw new IOException("TorChannel send - error in sending a cell!");
                }
                this.addToSendCellCounter(-1);
            }
        }
    }

    private void internalClose() {
        this.m_bCreateError = true;
        if (!this.m_bDoNotCloseChannelOnError) {
            this.close();
        } else {
            byte[] arrby = new byte[]{6};
            RelayCell relayCell = new RelayCell(this.m_circuit.getCircID(), 3, this.m_id, arrby);
            try {
                this.m_circuit.sendUrgent(relayCell);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        this.m_bCreateError = true;
        super.close();
        Object object = this.m_oWaitForOpen;
        synchronized (object) {
            this.m_oWaitForOpen.notify();
        }
    }

    public boolean isClosed() {
        return this.m_bCreateError;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closedByPeer() {
        this.m_bCreateError = true;
        if (!this.m_bDoNotCloseChannelOnError) {
            super.closedByPeer();
        }
        Object object = this.m_oWaitForOpen;
        synchronized (object) {
            this.m_oWaitForOpen.notify();
        }
    }

    protected void close_impl() {
        try {
            if (!this.m_bIsClosed) {
                this.m_circuit.close(this.m_id);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected void setDoNotCloseChannelOnErrorDuringConnect(boolean bl) {
        this.m_bDoNotCloseChannelOnError = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean connect(String string, int n) {
        try {
            if (this.m_bIsClosed || this.m_bIsClosedByPeer) {
                return false;
            }
            this.m_recvcellcounter = 500;
            this.m_sendcellcounter = 500;
            byte[] arrby = (string + ":" + Integer.toString(n)).getBytes();
            arrby = ByteArrayUtil.conc(arrby, new byte[1]);
            RelayCell relayCell = new RelayCell(this.m_circuit.getCircID(), 1, this.m_id, arrby);
            this.m_bChannelCreated = false;
            this.m_bCreateError = false;
            this.m_circuit.sendUrgent(relayCell);
            Object object = this.m_oWaitForOpen;
            synchronized (object) {
                long l = System.currentTimeMillis();
                int n2 = 60000;
                while (n2 > 0) {
                    try {
                        this.m_oWaitForOpen.wait(n2);
                    }
                    catch (InterruptedException interruptedException) {
                        LogHolder.log(7, LogType.TOR, "InterruptedException in TorChannel:connect()");
                    }
                    if (this.m_bCreateError) {
                        LogHolder.log(7, LogType.TOR, "TorChannel - connect() - establishing channel over circuit NOT successful. Channel was closed before!");
                        return false;
                    }
                    if (this.m_bChannelCreated) {
                        this.m_bDoNotCloseChannelOnError = false;
                        LogHolder.log(7, LogType.TOR, "TorChannel - connect() - establishing channel over circuit successful. Time needed [ms]: " + Long.toString(System.currentTimeMillis() - l));
                        return true;
                    }
                    long l2 = System.currentTimeMillis() - l;
                    if (l2 < 0L) {
                        return false;
                    }
                    n2 = (int)((long)n2 - l2);
                }
            }
            LogHolder.log(7, LogType.TOR, "TorChannel - connect() - establishing channel over circuit NOT successful. Timed out!");
            this.internalClose();
            return false;
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.TOR, "Exception in TorChannel:connect()");
            this.internalClose();
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int dispatchCell(RelayCell relayCell) {
        int n = 0;
        switch (relayCell.getRelayCommand()) {
            case 4: {
                this.m_bChannelCreated = true;
                this.m_bDoNotCloseChannelOnError = false;
                Object object = this.m_oWaitForOpen;
                synchronized (object) {
                    this.m_oWaitForOpen.notify();
                    break;
                }
            }
            case 5: {
                this.addToSendCellCounter(50);
                break;
            }
            case 2: {
                Object object;
                --this.m_recvcellcounter;
                if (this.m_recvcellcounter < 250) {
                    object = new RelayCell(this.m_circuit.getCircID(), 5, this.m_id, null);
                    try {
                        this.m_circuit.sendUrgent((Cell)object);
                    }
                    catch (Throwable throwable) {
                        this.closedByPeer();
                        return n;
                    }
                    this.m_recvcellcounter += 50;
                }
                try {
                    object = relayCell.getRelayPayload();
                    this.recv((byte[])object, 0, ((byte[])object).length);
                    break;
                }
                catch (Exception exception) {
                    this.closedByPeer();
                    return n;
                }
            }
            case 3: {
                byte by = relayCell.getPayload()[0];
                LogHolder.log(7, LogType.TOR, "RELAY_END: Relay stream closed with reason: " + by);
                if (by == 1) {
                    n = -1;
                }
                this.closedByPeer();
                break;
            }
            default: {
                this.closedByPeer();
            }
        }
        return n;
    }

    public boolean isClosedByPeer() {
        return this.m_bIsClosedByPeer;
    }
}

