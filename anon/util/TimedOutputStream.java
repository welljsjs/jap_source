/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

public final class TimedOutputStream
extends OutputStream {
    private OutputStream m_Out;
    private long m_TimeoutInTicks;
    private volatile long m_TimeOutTick;
    private volatile boolean m_bTimedOut;
    private static Thread ms_threadInterrupt;
    private static Hashtable ms_hashtableOutputStreams;
    private static volatile long ms_currentTick;
    static final long MS_PER_TICK = 5000L;

    private TimedOutputStream() {
    }

    public static void init() {
        ms_hashtableOutputStreams = new Hashtable(1000);
        ms_threadInterrupt = new Thread((Runnable)new TimedOutputStreamInterrupt(), "TimedOutputStream");
        ms_threadInterrupt.setDaemon(true);
        ms_threadInterrupt.start();
    }

    public TimedOutputStream(OutputStream outputStream, long l) {
        this.m_Out = outputStream;
        this.m_TimeoutInTicks = l / 5000L;
    }

    public void write(int n) throws IOException {
        this.m_TimeOutTick = ms_currentTick + this.m_TimeoutInTicks;
        ms_hashtableOutputStreams.put(this, this);
        this.m_bTimedOut = false;
        try {
            this.m_Out.write(n);
        }
        catch (IOException iOException) {
            ms_hashtableOutputStreams.remove(this);
            if (this.m_bTimedOut) {
                throw new InterruptedIOException("TimedOutputStream: write() timed out!");
            }
            throw iOException;
        }
        ms_hashtableOutputStreams.remove(this);
    }

    public void write(byte[] arrby) throws IOException {
        this.write(arrby, 0, arrby.length);
    }

    public void write(byte[] arrby, int n, int n2) throws IOException {
        this.m_TimeOutTick = ms_currentTick + this.m_TimeoutInTicks;
        ms_hashtableOutputStreams.put(this, this);
        this.m_bTimedOut = false;
        try {
            this.m_Out.write(arrby, n, n2);
        }
        catch (IOException iOException) {
            ms_hashtableOutputStreams.remove(this);
            if (this.m_bTimedOut) {
                throw new InterruptedIOException("TimedOutputStream: write() timed out!");
            }
            throw iOException;
        }
        ms_hashtableOutputStreams.remove(this);
    }

    public void close() throws IOException {
        this.m_Out.close();
    }

    public void flush() throws IOException {
        this.m_TimeOutTick = ms_currentTick + this.m_TimeoutInTicks;
        ms_hashtableOutputStreams.put(this, this);
        this.m_bTimedOut = false;
        try {
            this.m_Out.flush();
        }
        catch (IOException iOException) {
            ms_hashtableOutputStreams.remove(this);
            if (this.m_bTimedOut) {
                throw new InterruptedIOException("TimedOutputStream: flush() timed out!");
            }
            throw iOException;
        }
        ms_hashtableOutputStreams.remove(this);
    }

    private static final class TimedOutputStreamInterrupt
    implements Runnable {
        private TimedOutputStreamInterrupt() {
        }

        public void run() {
            ms_currentTick = 0L;
            block6: while (true) {
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException interruptedException) {
                    continue;
                }
                ms_currentTick++;
                try {
                    Enumeration enumeration = ms_hashtableOutputStreams.elements();
                    while (true) {
                        if (!enumeration.hasMoreElements()) continue block6;
                        TimedOutputStream timedOutputStream = (TimedOutputStream)enumeration.nextElement();
                        if (ms_currentTick <= timedOutputStream.m_TimeOutTick) continue;
                        try {
                            timedOutputStream.m_bTimedOut = true;
                            timedOutputStream.close();
                        }
                        catch (Throwable throwable) {}
                    }
                }
                catch (Exception exception) {
                    continue;
                }
                break;
            }
        }
    }
}

