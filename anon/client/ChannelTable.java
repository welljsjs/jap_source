/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractChannel;
import anon.client.AbstractControlChannel;
import anon.client.AbstractDataChain;
import anon.client.AbstractDataChannel;
import anon.client.IDataChannelCreator;
import anon.client.IDataChannelFactory;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;

public class ChannelTable
implements IDataChannelCreator {
    public static final int CONTROL_CHANNEL_ID_PAY = 2;
    public static final int CONTROL_CHANNEL_ID_REPLAY = 3;
    public static final int CONTROL_CHANNEL_ID_DUMMY = 4;
    public static final int CONTROL_CHANNEL_ID_TEST = 255;
    private static final int MAX_OPEN_DATACHANNELS = 50;
    private static final int MIN_RESERVED_CHANNEL_ID = 0;
    private static final int MAX_RESERVED_CHANNEL_ID = 255;
    private IDataChannelFactory m_dataChannelFactory;
    private Hashtable m_channelTable;
    private Hashtable m_channelTableControl;
    private volatile int m_availableDataChannels;
    private SecureRandom m_channelIdGenerator;
    private volatile boolean m_tableClosed;

    public ChannelTable(IDataChannelFactory iDataChannelFactory, SecureRandom secureRandom) {
        this.m_dataChannelFactory = iDataChannelFactory;
        this.m_channelTable = new Hashtable();
        this.m_channelTableControl = new Hashtable();
        this.m_availableDataChannels = 50;
        this.m_channelIdGenerator = secureRandom;
        this.m_tableClosed = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractChannel getChannel(int n) {
        AbstractChannel abstractChannel = null;
        Hashtable hashtable = this.m_channelTable;
        synchronized (hashtable) {
            abstractChannel = (AbstractChannel)this.m_channelTable.get(new Integer(n));
        }
        return abstractChannel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeChannel(int n) {
        Hashtable hashtable = this.m_channelTable;
        synchronized (hashtable) {
            if (!this.m_tableClosed) {
                AbstractChannel abstractChannel = (AbstractChannel)this.m_channelTable.remove(new Integer(n));
                this.m_channelTableControl.remove(new Integer(n));
                if (abstractChannel instanceof AbstractDataChannel) {
                    ++this.m_availableDataChannels;
                    this.m_channelTable.notifyAll();
                }
                if (abstractChannel != null) {
                    LogHolder.log(7, LogType.NET, "ChannelTable: removeChannel(): Removed channel with ID '" + Integer.toString(n) + "' from table.");
                }
            }
        }
    }

    public boolean isSendingControlMessage() {
        Enumeration enumeration = ((Hashtable)this.m_channelTableControl.clone()).elements();
        while (enumeration.hasMoreElements()) {
            if (!((AbstractControlChannel)enumeration.nextElement()).isSending()) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerControlChannel(int n, AbstractControlChannel abstractControlChannel) {
        Hashtable hashtable = this.m_channelTable;
        synchronized (hashtable) {
            if (!this.m_tableClosed) {
                this.m_channelTable.put(new Integer(n), abstractControlChannel);
                this.m_channelTableControl.put(new Integer(n), abstractControlChannel);
                LogHolder.log(7, LogType.NET, "ChannelTable: registerControlChannel(): Registered ControlChannel with ID '" + Integer.toString(n) + "'.");
            } else {
                abstractControlChannel.multiplexerClosed();
            }
        }
    }

    public boolean isControlChannelId(int n) {
        return n > 0 && n <= 255;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractDataChannel createDataChannel(AbstractDataChain abstractDataChain) {
        AbstractDataChannel abstractDataChannel = null;
        Hashtable hashtable = this.m_channelTable;
        synchronized (hashtable) {
            try {
                while (this.m_availableDataChannels <= 0 && !this.m_tableClosed) {
                    this.m_channelTable.wait();
                }
                if (!this.m_tableClosed) {
                    int n = this.getFreeChannelId();
                    abstractDataChannel = this.m_dataChannelFactory.createDataChannel(n, abstractDataChain);
                    this.m_channelTable.put(new Integer(n), abstractDataChannel);
                    --this.m_availableDataChannels;
                    LogHolder.log(7, LogType.NET, "ChannelTable: createDataChannel(): Created DataChannel with ID '" + Integer.toString(n) + "'.");
                } else {
                    abstractDataChannel = this.m_dataChannelFactory.createDataChannel(0, abstractDataChain);
                    abstractDataChannel.multiplexerClosed();
                }
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        return abstractDataChannel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeChannelTable() {
        Hashtable hashtable = this.m_channelTable;
        synchronized (hashtable) {
            this.m_tableClosed = true;
            Enumeration enumeration = this.m_channelTable.elements();
            while (enumeration.hasMoreElements()) {
                AbstractChannel abstractChannel = (AbstractChannel)enumeration.nextElement();
                abstractChannel.multiplexerClosed();
            }
            this.m_channelTable.clear();
            this.m_channelTableControl.clear();
            this.m_availableDataChannels = 50;
            LogHolder.log(7, LogType.NET, "ChannelTable: closeChannelTable(): Removed all channels from table.");
            this.m_channelTable.notifyAll();
        }
    }

    private int getFreeChannelId() {
        int n = 0;
        while ((n = this.m_channelIdGenerator.nextInt()) >= 0 && n <= 255 || this.getChannel(n) != null) {
        }
        return n;
    }
}

