/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import java.io.IOException;

public class DataChainSendOrderStructure {
    private byte[] m_orderData;
    private Object m_additionalProtocolData;
    private int m_processedBytes;
    private IOException m_thrownException;
    private byte[] m_channelCell;
    private Object m_internalSynchronization;
    private boolean m_processingDone;

    public DataChainSendOrderStructure(byte[] arrby) {
        this.m_orderData = arrby;
        this.m_processedBytes = 0;
        this.m_thrownException = null;
        this.m_channelCell = null;
        this.m_internalSynchronization = new Object();
        this.m_processingDone = false;
        this.m_additionalProtocolData = null;
    }

    public byte[] getOrderData() {
        return this.m_orderData;
    }

    public Object getAdditionalProtocolData() {
        return this.m_additionalProtocolData;
    }

    public void setAdditionalProtocolData(Object object) {
        this.m_additionalProtocolData = object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processingDone() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            this.m_processingDone = true;
            this.m_internalSynchronization.notify();
        }
    }

    public boolean isProcessingDone() {
        return this.m_processingDone;
    }

    public Object getSynchronizationObject() {
        return this.m_internalSynchronization;
    }

    public void setThrownException(IOException iOException) {
        this.m_thrownException = iOException;
    }

    public IOException getThrownException() {
        return this.m_thrownException;
    }

    public void setProcessedBytes(int n) {
        this.m_processedBytes = n;
    }

    public int getProcessedBytes() {
        return this.m_processedBytes;
    }

    public void setChannelCell(byte[] arrby) {
        this.m_channelCell = arrby;
    }

    public byte[] getChannelCell() {
        return this.m_channelCell;
    }
}

