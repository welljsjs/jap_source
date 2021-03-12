/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.DemultiplexorInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LazyReadInputStream
extends DemultiplexorInputStream {
    private byte[] m_buf = null;
    private byte[] m_oldBuf = new byte[0];
    private byte[] m_eodStr = null;
    private InputStream m_underlyingStream;
    private boolean m_endOfUnderlyingStreamReached;

    public LazyReadInputStream(InputStream inputStream) {
        super(inputStream);
        this.m_underlyingStream = inputStream;
        this.m_endOfUnderlyingStreamReached = false;
    }

    public synchronized int read() throws IOException {
        int n = -1;
        if (this.m_eodStr != null) {
            int n2;
            int n3;
            if (this.m_buf == null) {
                this.m_buf = new byte[this.m_eodStr.length];
                n3 = 0;
                while (n3 < this.m_buf.length) {
                    n2 = this.readAhead();
                    if (n2 != -1) {
                        this.m_buf[n3] = (byte)n2;
                        ++n3;
                        continue;
                    }
                    byte[] arrby = new byte[n3];
                    System.arraycopy(this.m_buf, 0, arrby, 0, n3);
                    this.m_buf = arrby;
                }
            }
            n3 = 1;
            if (this.m_buf.length == this.m_eodStr.length) {
                for (n2 = 0; n2 < this.m_eodStr.length; ++n2) {
                    if (this.m_buf[n2] == this.m_eodStr[n2]) continue;
                    n3 = 0;
                    break;
                }
            }
            if (this.m_buf.length > 0) {
                byte[] arrby = new byte[this.m_buf.length - 1];
                System.arraycopy(this.m_buf, 1, arrby, 0, arrby.length);
                n = this.m_buf[0] & 0xFF;
                if (n3 == 1) {
                    this.m_buf = arrby;
                } else {
                    int n4 = this.readAhead();
                    if (n4 != -1) {
                        System.arraycopy(arrby, 0, this.m_buf, 0, arrby.length);
                        this.m_buf[this.m_buf.length - 1] = (byte)n4;
                    } else {
                        this.m_buf = arrby;
                    }
                }
            }
        } else {
            n = this.readAhead();
        }
        return n;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        int n3 = 0;
        boolean bl = false;
        while (!bl && n3 < n2) {
            int n4 = this.read();
            if (n4 == -1) {
                bl = true;
                continue;
            }
            arrby[n + n3] = (byte)n4;
            ++n3;
        }
        if (bl && n3 == 0) {
            n3 = -1;
        }
        return n3;
    }

    public synchronized int available() throws IOException {
        int n = 0;
        n = this.m_eodStr == null ? this.m_oldBuf.length + this.m_underlyingStream.available() : (this.m_buf == null ? Math.max(0, Math.min(this.m_eodStr.length, this.m_oldBuf.length + this.m_underlyingStream.available() - this.m_eodStr.length)) : Math.min(this.m_buf.length, this.m_oldBuf.length + this.m_underlyingStream.available()));
        return n;
    }

    public synchronized void setTerminator(byte[] arrby, int[] arrn) {
        this.m_eodStr = arrby;
        if (this.m_buf != null) {
            byte[] arrby2 = new byte[this.m_oldBuf.length + this.m_buf.length];
            System.arraycopy(this.m_oldBuf, 0, arrby2, 0, this.m_oldBuf.length);
            System.arraycopy(this.m_buf, 0, arrby2, this.m_oldBuf.length, this.m_buf.length);
            this.m_oldBuf = arrby2;
        }
        this.m_buf = null;
    }

    public synchronized boolean atEnd() {
        boolean bl = false;
        if (this.m_eodStr != null && this.m_buf != null && !this.m_endOfUnderlyingStreamReached && this.m_buf.length == 0) {
            bl = true;
        }
        return bl;
    }

    public synchronized boolean startsWithCRLF() throws IOException {
        boolean bl = false;
        if (this.m_buf != null && this.m_buf.length > 0) {
            if (this.m_buf[0] == 13) {
                if (this.m_buf.length > 1) {
                    if (this.m_buf[1] == 10) {
                        bl = true;
                        this.read();
                        this.read();
                    }
                } else if (this.m_oldBuf.length > 0) {
                    if (this.m_oldBuf[0] == 10) {
                        bl = true;
                        this.readAhead();
                        this.read();
                    }
                } else {
                    int n = this.m_underlyingStream.read();
                    if (n != -1) {
                        if ((byte)n == 10) {
                            bl = true;
                            this.read();
                        } else {
                            this.m_oldBuf = new byte[1];
                            this.m_oldBuf[0] = (byte)n;
                        }
                    }
                }
            }
        } else if (this.m_oldBuf.length > 0) {
            if (this.m_oldBuf[0] == 13) {
                if (this.m_oldBuf.length > 1) {
                    if (this.m_oldBuf[1] == 10) {
                        bl = true;
                        this.readAhead();
                        this.readAhead();
                    }
                } else {
                    int n = this.m_underlyingStream.read();
                    if (n != -1) {
                        if ((byte)n == 10) {
                            bl = true;
                            this.readAhead();
                        } else {
                            byte[] arrby = new byte[]{this.m_oldBuf[0], (byte)n};
                            this.m_oldBuf = arrby;
                        }
                    }
                }
            }
        } else {
            int n = this.m_underlyingStream.read();
            if (n != -1) {
                if ((byte)n == 13) {
                    int n2 = this.m_underlyingStream.read();
                    if (n2 != -1) {
                        if ((byte)n2 == 10) {
                            bl = true;
                        } else {
                            this.m_oldBuf = new byte[2];
                            this.m_oldBuf[0] = (byte)n;
                            this.m_oldBuf[1] = (byte)n2;
                        }
                    } else {
                        this.m_oldBuf = new byte[1];
                        this.m_oldBuf[0] = (byte)n;
                    }
                } else {
                    this.m_oldBuf = new byte[1];
                    this.m_oldBuf[0] = (byte)n;
                }
            }
        }
        return bl;
    }

    private synchronized int readAhead() throws IOException {
        int n = -1;
        if (this.m_oldBuf.length > 0) {
            byte[] arrby = new byte[this.m_oldBuf.length - 1];
            System.arraycopy(this.m_oldBuf, 1, arrby, 0, arrby.length);
            n = this.m_oldBuf[0] & 0xFF;
            this.m_oldBuf = arrby;
        } else {
            n = this.m_underlyingStream.read();
            if (n == -1) {
                this.m_endOfUnderlyingStreamReached = true;
            }
        }
        return n;
    }
}

