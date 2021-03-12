/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Codecs;
import HTTPClient.DemultiplexorInputStream;
import HTTPClient.ExtBufferedInputStream;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPConnection;
import HTTPClient.LazyReadInputStream;
import HTTPClient.LinkedList;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.RespInputStream;
import HTTPClient.Response;
import HTTPClient.ResponseHandler;
import HTTPClient.RetryException;
import HTTPClient.Util;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;

class StreamDemultiplexor
implements GlobalConstants {
    private int Protocol;
    private HTTPConnection Connection;
    private DemultiplexorInputStream Stream;
    private Socket Sock = null;
    private ResponseHandler MarkedForClose;
    private SocketTimeout.TimeoutEntry Timer = null;
    private static SocketTimeout TimerThread = null;
    private LinkedList RespHandlerList;
    private int chunk_len;
    private static byte[] hdr_end = new byte[]{13, 10, 13, 10};
    private static int[] hdr_cmp = Util.compile_search(hdr_end);
    private boolean hdr_term_set = false;
    private boolean trl_term_set = false;
    private int cur_timeout = 0;
    private boolean m_httpConnectCompatibilityMode;
    private Socket m_hiddenSocket;

    StreamDemultiplexor(int n, Socket socket, HTTPConnection hTTPConnection, boolean bl) throws IOException {
        this.Protocol = n;
        this.Connection = hTTPConnection;
        this.RespHandlerList = new LinkedList();
        this.m_httpConnectCompatibilityMode = bl;
        this.m_hiddenSocket = null;
        this.init(socket);
    }

    private void init(Socket socket) throws IOException {
        this.Sock = socket;
        this.Stream = this.m_httpConnectCompatibilityMode ? new LazyReadInputStream(socket.getInputStream()) : new ExtBufferedInputStream(socket.getInputStream());
        this.MarkedForClose = null;
        this.chunk_len = -1;
        this.Timer = TimerThread.setTimeout(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void register(Response response, Request request) throws RetryException {
        LinkedList linkedList = this.RespHandlerList;
        synchronized (linkedList) {
            if (this.Sock == null) {
                throw new RetryException();
            }
            this.RespHandlerList.addToEnd(new ResponseHandler(response, request, this));
        }
    }

    RespInputStream getStream(Response response) {
        ResponseHandler responseHandler = (ResponseHandler)this.RespHandlerList.enumerate();
        while (responseHandler != null && responseHandler.resp != response) {
            responseHandler = (ResponseHandler)this.RespHandlerList.next();
        }
        if (responseHandler != null) {
            return responseHandler.stream;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Socket releaseSocket() {
        Socket socket = null;
        StreamDemultiplexor streamDemultiplexor = this;
        synchronized (streamDemultiplexor) {
            if (this.m_httpConnectCompatibilityMode) {
                try {
                    LinkedList linkedList = this.RespHandlerList;
                    synchronized (linkedList) {
                        ResponseHandler responseHandler = (ResponseHandler)this.RespHandlerList.enumerate();
                        while (responseHandler != null) {
                            ResponseHandler responseHandler2 = (ResponseHandler)this.RespHandlerList.next();
                            if (responseHandler2 == null) {
                                responseHandler.resp.getStatusCode();
                            }
                            responseHandler = responseHandler2;
                        }
                        this.close(null, false);
                    }
                    socket = this.m_hiddenSocket;
                    this.m_hiddenSocket = null;
                    this.m_httpConnectCompatibilityMode = false;
                }
                catch (IOException iOException) {
                    this.m_httpConnectCompatibilityMode = false;
                    this.close(null, false);
                }
            }
        }
        return socket;
    }

    public boolean isHttpConnectCompatibilityModeUsed() {
        return this.m_httpConnectCompatibilityMode;
    }

    void restartTimer() {
        if (this.Timer != null) {
            this.Timer.reset();
        }
    }

    int read(byte[] arrby, int n, int n2, ResponseHandler responseHandler, int n3) throws IOException {
        ResponseHandler responseHandler2;
        if (responseHandler.exception != null) {
            throw (IOException)responseHandler.exception.fillInStackTrace();
        }
        if (responseHandler.eof) {
            return -1;
        }
        while ((responseHandler2 = (ResponseHandler)this.RespHandlerList.getFirst()) != null && responseHandler2 != responseHandler) {
            try {
                responseHandler2.stream.readAll(n3);
            }
            catch (IOException iOException) {
                if (iOException instanceof InterruptedIOException) {
                    throw iOException;
                }
                throw (IOException)responseHandler.exception.fillInStackTrace();
            }
        }
        StreamDemultiplexor streamDemultiplexor = this;
        synchronized (streamDemultiplexor) {
            if (responseHandler.exception != null) {
                throw (IOException)responseHandler.exception.fillInStackTrace();
            }
            if (this.Timer != null) {
                this.Timer.hyber();
            }
            try {
                int n4 = -1;
                if (n3 != this.cur_timeout) {
                    try {
                        this.Sock.setSoTimeout(n3);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    this.cur_timeout = n3;
                }
                switch (responseHandler.resp.cd_type) {
                    case 0: {
                        n4 = this.Stream.read(arrby, n, n2);
                        if (n4 != -1) break;
                        throw new EOFException("Premature EOF encountered");
                    }
                    case 1: {
                        if (!this.hdr_term_set) {
                            this.Stream.setTerminator(hdr_end, hdr_cmp);
                            this.hdr_term_set = true;
                        }
                        if (this.Stream.atEnd()) {
                            this.Stream.setTerminator(null, null);
                            this.hdr_term_set = false;
                            n4 = 0;
                        } else {
                            n4 = this.Stream.read(arrby, n, n2);
                        }
                        if (n4 != -1) break;
                        throw new EOFException("Premature EOF encountered");
                    }
                    case 2: {
                        n4 = -1;
                        this.close(responseHandler);
                        break;
                    }
                    case 3: {
                        n4 = this.Stream.read(arrby, n, n2);
                        if (n4 != -1) break;
                        this.close(responseHandler);
                        break;
                    }
                    case 4: {
                        int n5 = responseHandler.resp.ContentLength;
                        if (n2 > n5 - responseHandler.stream.count) {
                            n2 = n5 - responseHandler.stream.count;
                        }
                        if ((n4 = this.Stream.read(arrby, n, n2)) == -1) {
                            throw new EOFException("Premature EOF encountered");
                        }
                        if (responseHandler.stream.count + n4 != n5) break;
                        this.close(responseHandler);
                        break;
                    }
                    case 5: {
                        if (this.chunk_len == -1) {
                            this.chunk_len = Codecs.getChunkLength(this.Stream);
                        }
                        if (this.chunk_len > 0) {
                            if (n2 > this.chunk_len) {
                                n2 = this.chunk_len;
                            }
                            if ((n4 = this.Stream.read(arrby, n, n2)) == -1) {
                                throw new EOFException("Premature EOF encountered");
                            }
                            this.chunk_len -= n4;
                            if (this.chunk_len != 0) break;
                            this.Stream.read();
                            this.Stream.read();
                            this.chunk_len = -1;
                            break;
                        }
                        if (this.trl_term_set || !this.Stream.startsWithCRLF()) {
                            if (!this.trl_term_set) {
                                this.Stream.setTerminator(hdr_end, hdr_cmp);
                                this.trl_term_set = true;
                            }
                            responseHandler.resp.readTrailers(this.Stream);
                            if (!this.Stream.atEnd()) {
                                throw new EOFException("Premature EOF encountered");
                            }
                            this.Stream.setTerminator(null, null);
                            this.trl_term_set = false;
                        }
                        n4 = -1;
                        this.close(responseHandler);
                        this.chunk_len = -1;
                        break;
                    }
                    case 6: {
                        responseHandler.setupBoundary(this.Stream);
                        n4 = this.Stream.read(arrby, n, n2);
                        if (n4 == -1) {
                            throw new EOFException("Premature EOF encountered");
                        }
                        if (!this.Stream.atEnd()) break;
                        this.Stream.setTerminator(null, null);
                        this.close(responseHandler);
                        break;
                    }
                    default: {
                        throw new Error("Internal Error in StreamDemultiplexor: Invalid cd_type " + responseHandler.resp.cd_type);
                    }
                }
                this.restartTimer();
                return n4;
            }
            catch (InterruptedIOException interruptedIOException) {
                this.restartTimer();
                throw interruptedIOException;
            }
            catch (IOException iOException) {
                this.close(iOException, true);
                throw responseHandler.exception;
            }
            catch (ParseException parseException) {
                this.close(new IOException(parseException.toString()), true);
                throw responseHandler.exception;
            }
        }
    }

    synchronized long skip(long l, ResponseHandler responseHandler) throws IOException {
        if (responseHandler.exception != null) {
            throw (IOException)responseHandler.exception.fillInStackTrace();
        }
        if (responseHandler.eof) {
            return 0L;
        }
        byte[] arrby = new byte[(int)l];
        int n = this.read(arrby, 0, (int)l, responseHandler, 0);
        if (n == -1) {
            return 0L;
        }
        return n;
    }

    synchronized int available(ResponseHandler responseHandler) throws IOException {
        int n = this.Stream.available();
        if (responseHandler == null) {
            return n;
        }
        if (responseHandler.exception != null) {
            throw (IOException)responseHandler.exception.fillInStackTrace();
        }
        if (responseHandler.eof) {
            return 0;
        }
        switch (responseHandler.resp.cd_type) {
            case 0: {
                return n;
            }
            case 1: {
                return n > 0 ? 1 : 0;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return n;
            }
            case 4: {
                int n2 = responseHandler.resp.ContentLength;
                return n < (n2 -= responseHandler.stream.count) ? n : n2;
            }
            case 5: {
                return n;
            }
            case 6: {
                return n;
            }
        }
        throw new Error("Internal Error in StreamDemultiplexor: Invalid cd_type " + responseHandler.resp.cd_type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void close(IOException iOException, boolean bl) {
        if (this.Sock == null) {
            return;
        }
        if (this.m_httpConnectCompatibilityMode) {
            this.Stream = new ExtBufferedInputStream(new ByteArrayInputStream(new byte[0]));
        }
        try {
            this.Stream.close();
        }
        catch (IOException iOException2) {
            // empty catch block
        }
        if (this.m_httpConnectCompatibilityMode) {
            this.m_hiddenSocket = this.Sock;
        } else {
            try {
                this.Sock.close();
            }
            catch (IOException iOException3) {
                // empty catch block
            }
        }
        this.Sock = null;
        if (this.Timer != null) {
            this.Timer.kill();
            this.Timer = null;
        }
        this.Connection.DemuxList.remove(this);
        if (iOException != null) {
            LinkedList linkedList = this.RespHandlerList;
            synchronized (linkedList) {
                this.retry_requests(iOException, bl);
            }
        }
    }

    private void retry_requests(IOException iOException, boolean bl) {
        RetryException retryException = null;
        RetryException retryException2 = null;
        ResponseHandler responseHandler = (ResponseHandler)this.RespHandlerList.enumerate();
        while (responseHandler != null) {
            if (responseHandler.resp.got_headers) {
                responseHandler.exception = iOException;
            } else {
                RetryException retryException3 = new RetryException(iOException.getMessage());
                if (retryException == null) {
                    retryException = retryException3;
                }
                retryException3.request = responseHandler.request;
                retryException3.response = responseHandler.resp;
                retryException3.exception = iOException;
                retryException3.conn_reset = bl;
                retryException3.first = retryException;
                retryException3.addToListAfter(retryException2);
                retryException2 = retryException3;
                responseHandler.exception = retryException3;
            }
            this.RespHandlerList.remove(responseHandler);
            responseHandler = (ResponseHandler)this.RespHandlerList.next();
        }
    }

    synchronized void close(ResponseHandler responseHandler) {
        if (responseHandler != (ResponseHandler)this.RespHandlerList.getFirst()) {
            return;
        }
        responseHandler.eof = true;
        this.RespHandlerList.remove(responseHandler);
        if (responseHandler == this.MarkedForClose) {
            this.close(new IOException("Premature end of Keep-Alive"), false);
        } else {
            this.closeSocketIfAllStreamsClosed();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void closeSocketIfAllStreamsClosed() {
        LinkedList linkedList = this.RespHandlerList;
        synchronized (linkedList) {
            ResponseHandler responseHandler = (ResponseHandler)this.RespHandlerList.enumerate();
            while (responseHandler != null && responseHandler.stream.closed) {
                if (responseHandler == this.MarkedForClose) {
                    ResponseHandler responseHandler2;
                    do {
                        responseHandler2 = (ResponseHandler)this.RespHandlerList.getFirst();
                        this.RespHandlerList.remove(responseHandler2);
                    } while (responseHandler2 != responseHandler);
                    this.close(new IOException("Premature end of Keep-Alive"), false);
                    return;
                }
                responseHandler = (ResponseHandler)this.RespHandlerList.next();
            }
        }
    }

    synchronized Socket getSocket() {
        if (this.MarkedForClose != null) {
            return null;
        }
        if (this.Timer != null) {
            this.Timer.hyber();
        }
        return this.Sock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void markForClose(Response response) {
        Object object = this.RespHandlerList;
        synchronized (object) {
            if (this.RespHandlerList.getFirst() == null) {
                this.close(new IOException("Premature end of Keep-Alive"), false);
                return;
            }
        }
        if (this.Timer != null) {
            this.Timer.kill();
            this.Timer = null;
        }
        Object object2 = null;
        object = (ResponseHandler)this.RespHandlerList.enumerate();
        while (object != null) {
            if (((ResponseHandler)object).resp == response) {
                this.MarkedForClose = object;
                this.closeSocketIfAllStreamsClosed();
                return;
            }
            if (this.MarkedForClose == object) {
                return;
            }
            object2 = object;
            object = (ResponseHandler)this.RespHandlerList.next();
        }
        if (object2 == null) {
            return;
        }
        this.MarkedForClose = object2;
        this.closeSocketIfAllStreamsClosed();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void abort() {
        LinkedList linkedList = this.RespHandlerList;
        synchronized (linkedList) {
            ResponseHandler responseHandler = (ResponseHandler)this.RespHandlerList.enumerate();
            while (responseHandler != null) {
                if (responseHandler.resp.http_resp != null) {
                    responseHandler.resp.http_resp.markAborted();
                }
                if (responseHandler.exception == null) {
                    responseHandler.exception = new IOException("Request aborted by user");
                }
                responseHandler = (ResponseHandler)this.RespHandlerList.next();
            }
            if (this.Sock != null) {
                try {
                    if (!this.m_httpConnectCompatibilityMode) {
                        try {
                            this.Sock.setSoLinger(false, 0);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    if (this.m_httpConnectCompatibilityMode) {
                        this.Stream = new ExtBufferedInputStream(new ByteArrayInputStream(new byte[0]));
                    }
                    try {
                        this.Stream.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    if (this.m_httpConnectCompatibilityMode) {
                        this.m_hiddenSocket = this.Sock;
                    } else {
                        try {
                            this.Sock.close();
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    }
                    this.Sock = null;
                    if (this.Timer != null) {
                        this.Timer.kill();
                        this.Timer = null;
                    }
                }
                catch (NullPointerException nullPointerException) {
                    // empty catch block
                }
                this.Connection.DemuxList.remove(this);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void releaseHttpConnectResources() {
        StreamDemultiplexor streamDemultiplexor = this;
        synchronized (streamDemultiplexor) {
            this.m_httpConnectCompatibilityMode = false;
            if (this.m_hiddenSocket != null) {
                try {
                    this.m_hiddenSocket.getInputStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                try {
                    this.m_hiddenSocket.getOutputStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                try {
                    this.m_hiddenSocket.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.m_hiddenSocket = null;
            }
        }
    }

    protected void finalize() throws Throwable {
        if (this.m_hiddenSocket != null) {
            try {
                this.m_hiddenSocket.getInputStream().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                this.m_hiddenSocket.getOutputStream().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                this.m_hiddenSocket.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.m_hiddenSocket = null;
        }
        this.m_httpConnectCompatibilityMode = false;
        this.close(null, false);
        super.finalize();
    }

    public String toString() {
        String string;
        switch (this.Protocol) {
            case 0: {
                string = "HTTP";
                break;
            }
            case 1: {
                string = "HTTPS";
                break;
            }
            case 2: {
                string = "SHTTP";
                break;
            }
            case 3: {
                string = "HTTP_NG";
                break;
            }
            default: {
                throw new Error("HTTPClient Internal Error: invalid protocol " + this.Protocol);
            }
        }
        return this.getClass().getName() + "[Protocol=" + string + "]";
    }

    static {
        TimerThread = new SocketTimeout(60);
        TimerThread.start();
    }

    private static class SocketTimeout
    extends Thread
    implements GlobalConstants {
        private TimeoutEntry[] time_list;
        private int current;

        SocketTimeout(int n) {
            super("SocketTimeout");
            try {
                this.setDaemon(true);
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            this.setPriority(10);
            this.time_list = new TimeoutEntry[n];
            for (int i = 0; i < n; ++i) {
                this.time_list[i] = new TimeoutEntry(null);
                this.time_list[i].next = this.time_list[i].prev = this.time_list[i];
            }
            this.current = 0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public TimeoutEntry setTimeout(StreamDemultiplexor streamDemultiplexor) {
            TimeoutEntry timeoutEntry = new TimeoutEntry(streamDemultiplexor);
            TimeoutEntry[] arrtimeoutEntry = this.time_list;
            synchronized (this.time_list) {
                timeoutEntry.next = this.time_list[this.current];
                timeoutEntry.prev = this.time_list[this.current].prev;
                timeoutEntry.prev.next = timeoutEntry;
                timeoutEntry.next.prev = timeoutEntry;
                // ** MonitorExit[var3_3] (shouldn't be in output)
                return timeoutEntry;
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
            block8: while (true) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Object object = this.time_list;
                // MONITORENTER : this.time_list
                TimeoutEntry timeoutEntry = this.time_list[this.current].next;
                while (timeoutEntry != this.time_list[this.current]) {
                    timeoutEntry.restart = false;
                    timeoutEntry = timeoutEntry.next;
                }
                ++this.current;
                if (this.current >= this.time_list.length) {
                    this.current = 0;
                }
                timeoutEntry = this.time_list[this.current].next;
                TimeoutEntry timeoutEntry2 = this.time_list[this.current];
                // MONITOREXIT : object
                while (true) {
                    if (timeoutEntry == timeoutEntry2) continue block8;
                    try {
                        object = timeoutEntry.demux;
                        // MONITORENTER : object
                        if (timeoutEntry.alive && !timeoutEntry.hyber) {
                            timeoutEntry.demux.markForClose(null);
                            timeoutEntry.kill();
                        }
                        // MONITOREXIT : object
                    }
                    catch (NullPointerException nullPointerException) {
                        // empty catch block
                    }
                    timeoutEntry = timeoutEntry.next;
                }
                break;
            }
        }

        private class TimeoutEntry {
            boolean restart = false;
            boolean hyber = false;
            boolean alive = true;
            StreamDemultiplexor demux;
            TimeoutEntry next = null;
            TimeoutEntry prev = null;

            TimeoutEntry(StreamDemultiplexor streamDemultiplexor) {
                this.demux = streamDemultiplexor;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            void reset() {
                this.hyber = false;
                if (this.restart) {
                    return;
                }
                this.restart = true;
                TimeoutEntry[] arrtimeoutEntry = SocketTimeout.this.time_list;
                synchronized (arrtimeoutEntry) {
                    this.next.prev = this.prev;
                    this.prev.next = this.next;
                    this.next = SocketTimeout.this.time_list[SocketTimeout.this.current];
                    this.prev = ((SocketTimeout)SocketTimeout.this).time_list[((SocketTimeout)SocketTimeout.this).current].prev;
                    this.prev.next = this;
                    this.next.prev = this;
                }
            }

            void hyber() {
                if (this.alive) {
                    this.hyber = true;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            void kill() {
                this.alive = false;
                this.restart = false;
                this.hyber = false;
                TimeoutEntry[] arrtimeoutEntry = SocketTimeout.this.time_list;
                synchronized (arrtimeoutEntry) {
                    this.next.prev = this.prev;
                    this.prev.next = this.next;
                }
            }
        }
    }
}

