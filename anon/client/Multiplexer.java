/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractChannel;
import anon.client.ChannelTable;
import anon.client.DefaultDataChannelFactory;
import anon.client.ISendCallbackHandler;
import anon.client.KeyExchangeManager;
import anon.client.MixPacket;
import anon.client.PacketProcessedEvent;
import anon.client.crypto.ControlChannelCipher;
import anon.client.crypto.SymCipher;
import anon.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class Multiplexer
extends Observable
implements Runnable {
    private Vector m_sendJobQueue;
    private Vector m_controlMessageQueue;
    private Object m_waitQueueObject;
    private ChannelTable m_channelTable;
    private InputStream m_inputStream;
    private OutputStream m_outputStream;
    private SymCipher m_inputStreamCipher;
    private SymCipher m_outputStreamCipher;
    private ControlChannelCipher m_controlchannelCiper;
    private Object m_internalEventSynchronization = new Object();
    private boolean m_bClosed = false;
    private boolean m_bWithIntegrityCheck;
    private boolean m_bDebug = false;

    public Multiplexer(InputStream inputStream, OutputStream outputStream, KeyExchangeManager keyExchangeManager, SecureRandom secureRandom) {
        this.m_sendJobQueue = new Vector();
        this.m_controlMessageQueue = new Vector();
        this.m_waitQueueObject = new Object();
        this.m_channelTable = new ChannelTable(new DefaultDataChannelFactory(keyExchangeManager, this), secureRandom);
        this.m_inputStream = inputStream;
        this.m_inputStreamCipher = keyExchangeManager.getMultiplexerInputStreamCipher();
        this.m_outputStream = outputStream;
        this.m_outputStreamCipher = keyExchangeManager.getMultiplexerOutputStreamCipher();
        this.m_controlchannelCiper = keyExchangeManager.getControlChannelCipher();
        this.m_bWithIntegrityCheck = keyExchangeManager.isProtocolWithIntegrityCheck();
        this.m_bDebug = keyExchangeManager.isDebug();
        Thread thread = new Thread((Runnable)this, "Multiplexer: Receive-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public boolean isSendingControlMessage() {
        return this.m_channelTable.isSendingControlMessage();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPacket(MixPacket mixPacket) throws IOException {
        Object object;
        Object object2;
        Object object3;
        Enumeration enumeration = new Enumeration();
        boolean bl = this.m_channelTable.isControlChannelId(mixPacket.getChannelId());
        Vector vector = bl ? this.m_controlMessageQueue : this.m_sendJobQueue;
        Enumeration enumeration2 = enumeration;
        synchronized (enumeration2) {
            boolean bl2 = false;
            object3 = this.m_waitQueueObject;
            synchronized (object3) {
                if (!bl) {
                    if (this.m_controlMessageQueue.size() > 0 || this.m_sendJobQueue.size() > 0) {
                        bl2 = true;
                    }
                } else if (this.m_controlMessageQueue.size() > 0) {
                    bl2 = true;
                    LogHolder.log(4, LogType.NET, "Control channel congestion");
                }
                vector.addElement(enumeration);
            }
            if (bl2) {
                try {
                    enumeration.wait();
                }
                catch (InterruptedException interruptedException) {
                    Object object4 = null;
                    Object object5 = this.m_waitQueueObject;
                    synchronized (object5) {
                        vector.removeElement(enumeration);
                        if (this.m_controlMessageQueue.size() > 0) {
                            object4 = this.m_controlMessageQueue.firstElement();
                        } else if (this.m_sendJobQueue.size() > 0) {
                            object4 = this.m_sendJobQueue.firstElement();
                        }
                    }
                    if (object4 != null) {
                        object5 = object4;
                        synchronized (object5) {
                            object4.notify();
                        }
                    }
                    throw new InterruptedIOException(interruptedException.toString());
                }
            }
        }
        enumeration2 = mixPacket.getSendCallbackHandlers().elements();
        while (enumeration2.hasMoreElements()) {
            ((ISendCallbackHandler)enumeration2.nextElement()).finalizePacket(mixPacket);
        }
        byte[] arrby = mixPacket.getRawPacket();
        if (this.m_outputStreamCipher != null) {
            this.m_outputStreamCipher.encryptAES1(arrby, 0, arrby, 0, 16);
        }
        try {
            this.m_outputStream.write(arrby);
            this.m_outputStream.flush();
            LogHolder.log(7, LogType.TRANSPORT, "PacketSent: " + System.currentTimeMillis());
            object3 = this.m_internalEventSynchronization;
            synchronized (object3) {
                this.setChanged();
                if (bl) {
                    this.notifyObservers(new PacketProcessedEvent(6));
                } else {
                    this.notifyObservers(new PacketProcessedEvent(5));
                }
            }
            Object var15_17 = null;
            object2 = null;
            object = this.m_waitQueueObject;
        }
        catch (Throwable throwable) {
            Object var15_18 = null;
            Object object6 = null;
            Object object7 = this.m_waitQueueObject;
            synchronized (object7) {
                vector.removeElement(enumeration);
                if (this.m_controlMessageQueue.size() > 0) {
                    object6 = this.m_controlMessageQueue.firstElement();
                } else if (this.m_sendJobQueue.size() > 0) {
                    object6 = this.m_sendJobQueue.firstElement();
                } else {
                    this.m_waitQueueObject.notify();
                }
            }
            if (object6 != null) {
                object7 = object6;
                synchronized (object7) {
                    object6.notify();
                }
            }
            throw throwable;
        }
        synchronized (object) {
            vector.removeElement(enumeration);
            if (this.m_controlMessageQueue.size() > 0) {
                object2 = this.m_controlMessageQueue.firstElement();
            } else if (this.m_sendJobQueue.size() > 0) {
                object2 = this.m_sendJobQueue.firstElement();
            } else {
                this.m_waitQueueObject.notify();
            }
        }
        if (object2 != null) {
            object = object2;
            synchronized (object) {
                object2.notify();
            }
        }
    }

    protected void close() {
        this.m_bClosed = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        int n = 0;
        try {
            while (true) {
                Object object;
                AbstractChannel abstractChannel;
                MixPacket mixPacket = new MixPacket(this.m_inputStream, this.m_inputStreamCipher);
                ++n;
                int n2 = mixPacket.getChannelId();
                if (this.m_bDebug) {
                    LogHolder.log(7, LogType.NET, "AN.ON debug packet received (PacketCounter: " + n + ", Channel:" + n2 + "): " + Base64.encode(mixPacket.getPayloadData(), 0, MixPacket.getPayloadSize(), false));
                }
                if ((abstractChannel = this.m_channelTable.getChannel(n2)) != null) {
                    object = this.m_internalEventSynchronization;
                    synchronized (object) {
                        this.setChanged();
                        if (this.m_channelTable.isControlChannelId(n2)) {
                            this.notifyObservers(new PacketProcessedEvent(2));
                        } else {
                            this.notifyObservers(new PacketProcessedEvent(1));
                        }
                    }
                    abstractChannel.processReceivedPacket(mixPacket);
                } else {
                    if (LogHolder.isLogged(6, LogType.NET)) {
                        LogHolder.log(6, LogType.NET, "Received a packet for unknown channel '" + Long.toString((long)n2 & 0xFFFFFFL) + "' Maybe we have already closed it and do not want to get more data for it.");
                    }
                    object = this.m_internalEventSynchronization;
                    synchronized (object) {
                        this.setChanged();
                        if (this.m_channelTable.isControlChannelId(n2)) {
                            this.notifyObservers(new PacketProcessedEvent(4));
                        } else {
                            this.notifyObservers(new PacketProcessedEvent(3));
                        }
                    }
                }
                Thread.yield();
            }
        }
        catch (IOException iOException) {
            if (this.m_bClosed) {
                if (LogHolder.isLogged(5, LogType.NET)) {
                    LogHolder.log(5, LogType.NET, Thread.currentThread().getName() + ": terminated!", iOException);
                }
            } else {
                LogHolder.log(2, LogType.NET, Thread.currentThread().getName() + ": terminated!", iOException);
            }
            this.m_channelTable.closeChannelTable();
            return;
        }
    }

    public ChannelTable getChannelTable() {
        return this.m_channelTable;
    }

    public ControlChannelCipher getControlChannelCipher() {
        return this.m_controlchannelCiper;
    }

    public boolean isProtocolWithIntegrityCheck() {
        return this.m_bWithIntegrityCheck;
    }

    public boolean isDebug() {
        return this.m_bDebug;
    }
}

