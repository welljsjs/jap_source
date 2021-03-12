/*
 * Decompiled with CFR 0.150.
 */
package anon.shared;

import java.io.IOException;

final class IOQueue {
    private byte[] buff = new byte[10000];
    private int readPos = 0;
    private int writePos = 0;
    private boolean bWriteClosed = false;
    private boolean bReadClosed = false;
    private static final int BUFF_SIZE = 10000;
    private boolean bFull = false;

    public synchronized void write(byte[] arrby, int n, int n2) throws IOException {
        while (n2 > 0) {
            if (this.bReadClosed || this.bWriteClosed) {
                throw new IOException("IOQueue closed");
            }
            if (this.bFull) {
                this.notify();
                try {
                    this.wait();
                    continue;
                }
                catch (InterruptedException interruptedException) {
                    throw new IOException("IOQueue write interrupted");
                }
            }
            int n3 = this.readPos <= this.writePos ? 10000 - this.writePos : this.readPos - this.writePos;
            if (n3 > n2) {
                n3 = n2;
            }
            System.arraycopy(arrby, n, this.buff, this.writePos, n3);
            n += n3;
            this.writePos += n3;
            n2 -= n3;
            if (this.writePos >= 10000) {
                this.writePos = 0;
            }
            if (this.readPos != this.writePos) continue;
            this.bFull = true;
        }
        this.notify();
    }

    public synchronized int read() throws IOException {
        while (true) {
            if (this.bReadClosed) {
                throw new IOException("IOQueue closed");
            }
            if (this.readPos != this.writePos || this.bFull) break;
            if (this.bWriteClosed) {
                return -1;
            }
            this.notify();
            try {
                this.wait();
            }
            catch (InterruptedException interruptedException) {
                throw new IOException("IOQueue read() interrupted");
            }
        }
        int n = this.buff[this.readPos++] & 0xFF;
        if (this.readPos >= 10000) {
            this.readPos = 0;
        }
        if (this.bFull) {
            this.bFull = false;
            this.notify();
        }
        return n;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        if (n2 <= 0) {
            return 0;
        }
        while (true) {
            if (this.bReadClosed) {
                throw new IOException("IOQueue closed");
            }
            if (this.readPos != this.writePos || this.bFull) break;
            if (this.bWriteClosed) {
                return -1;
            }
            this.notify();
            try {
                this.wait();
            }
            catch (InterruptedException interruptedException) {
                throw new IOException("IOQueue read() interrupted");
            }
        }
        int n3 = this.writePos <= this.readPos ? 10000 - this.readPos : this.writePos - this.readPos;
        if (n3 > n2) {
            n3 = n2;
        }
        System.arraycopy(this.buff, this.readPos, arrby, n, n3);
        this.readPos += n3;
        if (this.readPos >= 10000) {
            this.readPos = 0;
        }
        if (this.bFull) {
            this.bFull = false;
            this.notify();
        }
        return n3;
    }

    public synchronized int available() {
        if (this.bFull) {
            return 10000;
        }
        if (this.readPos == this.writePos && !this.bFull) {
            return 0;
        }
        if (this.writePos <= this.readPos) {
            return 10000 - this.readPos;
        }
        return this.writePos - this.readPos;
    }

    public synchronized void closeWrite() {
        this.bWriteClosed = true;
        this.notify();
    }

    public synchronized void closeRead() {
        this.bReadClosed = true;
        this.notify();
    }

    public synchronized void finalize() throws Throwable {
        this.bReadClosed = true;
        this.bWriteClosed = true;
        this.notify();
        this.buff = null;
        super.finalize();
    }
}

