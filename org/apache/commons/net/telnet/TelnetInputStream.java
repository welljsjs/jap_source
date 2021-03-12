/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetOption;

final class TelnetInputStream
extends BufferedInputStream
implements Runnable {
    static final int _STATE_DATA = 0;
    static final int _STATE_IAC = 1;
    static final int _STATE_WILL = 2;
    static final int _STATE_WONT = 3;
    static final int _STATE_DO = 4;
    static final int _STATE_DONT = 5;
    static final int _STATE_SB = 6;
    static final int _STATE_SE = 7;
    static final int _STATE_CR = 8;
    static final int _STATE_IAC_SB = 9;
    private boolean __hasReachedEOF;
    private boolean __isClosed;
    private boolean __readIsWaiting;
    private int __receiveState;
    private int __queueHead;
    private int __queueTail;
    private int __bytesAvailable;
    private int[] __queue;
    private TelnetClient __client;
    private Thread __thread;
    private IOException __ioException;
    private int[] __suboption = new int[256];
    private int __suboption_count = 0;
    private boolean __threaded;

    TelnetInputStream(InputStream inputStream, TelnetClient telnetClient, boolean bl) {
        super(inputStream);
        this.__client = telnetClient;
        this.__receiveState = 0;
        this.__isClosed = true;
        this.__hasReachedEOF = false;
        this.__queue = new int[2049];
        this.__queueHead = 0;
        this.__queueTail = 0;
        this.__bytesAvailable = 0;
        this.__ioException = null;
        this.__readIsWaiting = false;
        this.__threaded = false;
        this.__thread = bl ? new Thread(this) : null;
    }

    TelnetInputStream(InputStream inputStream, TelnetClient telnetClient) {
        this(inputStream, telnetClient, true);
    }

    void _start() {
        if (this.__thread == null) {
            return;
        }
        this.__isClosed = false;
        int n = Thread.currentThread().getPriority() + 1;
        if (n > 10) {
            n = 10;
        }
        this.__thread.setPriority(n);
        this.__thread.setDaemon(true);
        this.__thread.start();
        this.__threaded = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int __read() throws IOException {
        int n;
        block46: while (true) {
            if ((n = super.read()) < 0) {
                return -1;
            }
            n &= 0xFF;
            TelnetClient telnetClient = this.__client;
            synchronized (telnetClient) {
                this.__client._processAYTResponse();
            }
            this.__client._spyRead(n);
            switch (this.__receiveState) {
                case 8: {
                    if (n == 0) continue block46;
                }
                case 0: {
                    if (n == 255) {
                        this.__receiveState = 1;
                        continue block46;
                    }
                    if (n == 13) {
                        telnetClient = this.__client;
                        synchronized (telnetClient) {
                            this.__receiveState = this.__client._requestedDont(TelnetOption.BINARY) ? 8 : 0;
                            break block46;
                        }
                    }
                    this.__receiveState = 0;
                    break block46;
                }
                case 1: {
                    switch (n) {
                        case 251: {
                            this.__receiveState = 2;
                            continue block46;
                        }
                        case 252: {
                            this.__receiveState = 3;
                            continue block46;
                        }
                        case 253: {
                            this.__receiveState = 4;
                            continue block46;
                        }
                        case 254: {
                            this.__receiveState = 5;
                            continue block46;
                        }
                        case 250: {
                            this.__suboption_count = 0;
                            this.__receiveState = 6;
                            continue block46;
                        }
                        case 255: {
                            this.__receiveState = 0;
                            break;
                        }
                    }
                    this.__receiveState = 0;
                    continue block46;
                }
                case 2: {
                    telnetClient = this.__client;
                    synchronized (telnetClient) {
                        this.__client._processWill(n);
                        this.__client._flushOutputStream();
                    }
                    this.__receiveState = 0;
                    continue block46;
                }
                case 3: {
                    telnetClient = this.__client;
                    synchronized (telnetClient) {
                        this.__client._processWont(n);
                        this.__client._flushOutputStream();
                    }
                    this.__receiveState = 0;
                    continue block46;
                }
                case 4: {
                    telnetClient = this.__client;
                    synchronized (telnetClient) {
                        this.__client._processDo(n);
                        this.__client._flushOutputStream();
                    }
                    this.__receiveState = 0;
                    continue block46;
                }
                case 5: {
                    telnetClient = this.__client;
                    synchronized (telnetClient) {
                        this.__client._processDont(n);
                        this.__client._flushOutputStream();
                    }
                    this.__receiveState = 0;
                    continue block46;
                }
                case 6: {
                    switch (n) {
                        case 255: {
                            this.__receiveState = 9;
                            continue block46;
                        }
                    }
                    this.__suboption[this.__suboption_count++] = n;
                    this.__receiveState = 6;
                    continue block46;
                }
                case 9: {
                    switch (n) {
                        case 240: {
                            telnetClient = this.__client;
                            synchronized (telnetClient) {
                                this.__client._processSuboption(this.__suboption, this.__suboption_count);
                                this.__client._flushOutputStream();
                            }
                            this.__receiveState = 0;
                            continue block46;
                        }
                    }
                    this.__receiveState = 6;
                    this.__receiveState = 0;
                    continue block46;
                }
            }
            break;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void __processChar(int n) throws InterruptedException {
        int[] arrn = this.__queue;
        synchronized (this.__queue) {
            while (this.__bytesAvailable >= this.__queue.length - 1) {
                if (!this.__threaded) continue;
                this.__queue.notify();
                this.__queue.wait();
            }
            if (this.__readIsWaiting && this.__threaded) {
                this.__queue.notify();
            }
            this.__queue[this.__queueTail] = n;
            ++this.__bytesAvailable;
            if (++this.__queueTail >= this.__queue.length) {
                this.__queueTail = 0;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int read() throws IOException {
        int[] arrn = this.__queue;
        synchronized (this.__queue) {
            int n;
            while (true) {
                if (this.__ioException != null) {
                    IOException iOException = this.__ioException;
                    this.__ioException = null;
                    throw iOException;
                }
                if (this.__bytesAvailable != 0) break;
                if (this.__hasReachedEOF) {
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return -1;
                }
                if (this.__threaded) {
                    this.__queue.notify();
                    try {
                        this.__readIsWaiting = true;
                        this.__queue.wait();
                        this.__readIsWaiting = false;
                    }
                    catch (InterruptedException interruptedException) {
                        throw new IOException("Fatal thread interruption during read.");
                    }
                }
                this.__readIsWaiting = true;
                do {
                    try {
                        n = this.__read();
                        if (n < 0 && n != -2) {
                            // ** MonitorExit[var1_1] (shouldn't be in output)
                            return n;
                        }
                    }
                    catch (InterruptedIOException interruptedIOException) {
                        int[] arrn2 = this.__queue;
                        synchronized (this.__queue) {
                            this.__ioException = interruptedIOException;
                            this.__queue.notifyAll();
                            try {
                                this.__queue.wait(100L);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            return -1;
                        }
                    }
                    try {
                        if (n == -2) continue;
                        this.__processChar(n);
                    }
                    catch (InterruptedException interruptedException) {
                        if (!this.__isClosed) continue;
                        // ** MonitorExit[var1_1] (shouldn't be in output)
                        return -1;
                    }
                } while (super.available() > 0);
                this.__readIsWaiting = false;
            }
            n = this.__queue[this.__queueHead];
            if (++this.__queueHead >= this.__queue.length) {
                this.__queueHead = 0;
            }
            --this.__bytesAvailable;
            if (this.__bytesAvailable == 0 && this.__threaded) {
                this.__queue.notify();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return n;
        }
    }

    public int read(byte[] arrby) throws IOException {
        return this.read(arrby, 0, arrby.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int read(byte[] arrby, int n, int n2) throws IOException {
        if (n2 < 1) {
            return 0;
        }
        int[] arrn = this.__queue;
        synchronized (this.__queue) {
            if (n2 > this.__bytesAvailable) {
                n2 = this.__bytesAvailable;
            }
            // ** MonitorExit[var6_4] (shouldn't be in output)
            int n3 = this.read();
            if (n3 == -1) {
                return -1;
            }
            int n4 = n;
            do {
                arrby[n++] = (byte)n3;
            } while (--n2 > 0 && (n3 = this.read()) != -1);
            return n - n4;
        }
    }

    public boolean markSupported() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int available() throws IOException {
        int[] arrn = this.__queue;
        synchronized (this.__queue) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.__bytesAvailable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        super.close();
        int[] arrn = this.__queue;
        synchronized (this.__queue) {
            this.__hasReachedEOF = true;
            this.__isClosed = true;
            if (this.__thread != null && this.__thread.isAlive()) {
                this.__thread.interrupt();
            }
            this.__queue.notifyAll();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            this.__threaded = false;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void run() {
        try {
            while (!this.__isClosed) {
                int n;
                block16: {
                    try {
                        n = this.__read();
                        if (n >= 0) break block16;
                        break;
                    }
                    catch (InterruptedIOException interruptedIOException) {
                        block18: {
                            int[] arrn = this.__queue;
                            // MONITORENTER : this.__queue
                            this.__ioException = interruptedIOException;
                            this.__queue.notifyAll();
                            try {
                                this.__queue.wait(100L);
                            }
                            catch (InterruptedException interruptedException) {
                                if (!this.__isClosed) break block18;
                                // MONITOREXIT : arrn
                                break;
                            }
                        }
                        // MONITOREXIT : arrn
                        continue;
                    }
                    catch (RuntimeException runtimeException) {
                        super.close();
                        break;
                    }
                }
                try {
                    this.__processChar(n);
                }
                catch (InterruptedException interruptedException) {
                    if (!this.__isClosed) continue;
                    break;
                }
            }
        }
        catch (IOException iOException) {
            int[] arrn = this.__queue;
            // MONITORENTER : this.__queue
            this.__ioException = iOException;
            // MONITOREXIT : arrn
        }
        int[] arrn = this.__queue;
        // MONITORENTER : this.__queue
        this.__isClosed = true;
        this.__hasReachedEOF = true;
        this.__queue.notify();
        // MONITOREXIT : arrn
        this.__threaded = false;
    }
}

