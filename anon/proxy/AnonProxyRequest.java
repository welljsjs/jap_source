/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.AnonChannel;
import anon.NotConnectedToMixException;
import anon.TooMuchDataForPacketException;
import anon.proxy.AnonProxy;
import anon.proxy.ProxyCallbackBuffer;
import anon.proxy.ProxyCallbackDelayException;
import anon.proxy.ProxyCallbackHandler;
import anon.proxy.ProxyCallbackNotProcessableException;
import anon.util.SocketGuard;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;

public final class AnonProxyRequest
implements Runnable {
    private static int ms_nrOfRequests = 0;
    private static final long TIMEOUT_RECONNECT = 60000L;
    private static final int CHUNK_SIZE = 1000;
    private static int ms_currentRequest;
    private InputStream m_InChannel;
    private OutputStream m_OutChannel;
    private InputStream m_InSocket;
    private OutputStream m_OutSocket;
    private SocketGuard m_clientSocket;
    private Thread m_threadResponse;
    private Thread m_threadRequest;
    private AnonChannel m_Channel;
    private Hashtable m_hashParsedDomains = new Hashtable();
    private boolean m_bShowBrowserWarning = false;
    private AnonProxy m_Proxy;
    private volatile boolean m_bRequestIsAlive;
    private int m_iProtocol;
    private final Object m_syncObject;
    private ProxyCallbackHandler m_callbackHandler = null;
    private String[] contentEncodings;
    private boolean internalEncodingRequired = false;

    AnonProxyRequest(AnonProxy anonProxy, SocketGuard socketGuard, Object object, ProxyCallbackHandler proxyCallbackHandler, AnonProxy.RoundRobinRequestQueue roundRobinRequestQueue) throws IOException {
        this.m_Proxy = anonProxy;
        this.m_clientSocket = socketGuard;
        this.m_syncObject = object;
        this.m_clientSocket.setSoTimeout(0);
        this.m_InSocket = socketGuard.getInputStream();
        this.m_OutSocket = socketGuard.getOutputStream();
        this.m_threadRequest = new Thread((Runnable)this, "JAP - AnonProxy Request " + Integer.toString(ms_currentRequest));
        ++ms_currentRequest;
        this.m_callbackHandler = proxyCallbackHandler;
        this.m_threadRequest.setDaemon(true);
        this.m_threadRequest.start();
    }

    public int getAnonymityDistribution() {
        return this.m_Proxy.getMixCascade().getDistribution();
    }

    public static int getNrOfRequests() {
        return ms_nrOfRequests;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        Object object;
        ++ms_nrOfRequests;
        this.m_bRequestIsAlive = true;
        AnonChannel anonChannel = null;
        boolean bl = false;
        int n = 0;
        try {
            n = this.m_InSocket.read();
        }
        catch (InterruptedIOException interruptedIOException) {
            try {
                bl = true;
                anonChannel = this.m_Proxy.createChannel(2);
                this.m_iProtocol = 0;
                if (anonChannel == null) {
                    this.closeRequest();
                    return;
                }
            }
            catch (Throwable throwable) {
                LogHolder.log(3, LogType.NET, "AnonProxyRequest - something was wrong with seting up a new SMTP channel -- Exception: " + throwable);
                this.closeRequest();
                return;
            }
        }
        catch (Throwable throwable) {
            this.closeRequest();
            return;
        }
        if (anonChannel == null) {
            n &= 0xFF;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    anonChannel = null;
                    if (n == 4 || n == 5) {
                        anonChannel = this.m_Proxy.createChannel(1);
                        this.m_iProtocol = 0;
                        break;
                    }
                    anonChannel = this.m_Proxy.createChannel(0);
                    this.m_iProtocol = 1;
                    break;
                }
                catch (NotConnectedToMixException notConnectedToMixException) {
                    LogHolder.log(3, LogType.NET, "AnonProxyRequest - Connection to Mix lost");
                    Thread thread = new Thread(new Runnable(){

                        public void run() {
                            AnonProxyRequest.this.m_Proxy.reconnect();
                        }
                    }, "Request reconnect thread");
                    thread.start();
                    long l = System.currentTimeMillis();
                    try {
                        thread.join(60000L);
                    }
                    catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    boolean bl2 = true;
                    object = this.m_syncObject;
                    synchronized (object) {
                        long l2;
                        if (!this.m_Proxy.isConnected() && !Thread.currentThread().isInterrupted() && (l2 = l + 60000L - System.currentTimeMillis()) > 0L) {
                            try {
                                this.m_syncObject.wait(l2);
                            }
                            catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                        if (!this.m_Proxy.isConnected()) {
                            bl2 = false;
                        }
                    }
                    if (bl2) continue;
                    LogHolder.log(3, LogType.NET, "Requests terminated due to loss of connection to service!");
                    this.closeRequest();
                    return;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.NET, "AnonProxyRequest - something was wrong with seting up a new channel Exception: " + exception);
                    this.closeRequest();
                    return;
                }
            }
            if (anonChannel == null) {
                this.closeRequest();
                return;
            }
        }
        int n2 = 0;
        int n3 = 0;
        if (!bl) {
            n3 = 1;
        }
        byte[] arrby = null;
        ProxyCallbackBuffer proxyCallbackBuffer = null;
        try {
            this.m_InChannel = anonChannel.getInputStream();
            this.m_OutChannel = anonChannel.getOutputStream();
            this.m_Channel = anonChannel;
            this.m_threadResponse = new Thread((Runnable)new Response(), "JAP - AnonProxy Response for " + Thread.currentThread().getName());
            this.m_threadResponse.start();
            arrby = new byte[1900];
            arrby[0] = (byte)n;
        }
        catch (Throwable throwable) {
            this.closeRequest();
            return;
        }
        this.m_Proxy.incNumChannels();
        try {
            while (true) {
                try {
                    n2 = Math.min(this.m_Channel.getOutputBlockSize(), 1900);
                    n2 -= n3;
                    n2 = this.m_InSocket.read(arrby, n3, n2);
                }
                catch (InterruptedIOException interruptedIOException) {
                    n3 += interruptedIOException.bytesTransferred;
                    continue;
                }
                if ((n2 += n3) < 0) break;
                try {
                    if (this.m_callbackHandler != null && n2 > 0) {
                        proxyCallbackBuffer = new ProxyCallbackBuffer(arrby, 0, n2);
                        try {
                            this.m_callbackHandler.deliverUpstream(this, proxyCallbackBuffer);
                        }
                        catch (ProxyCallbackDelayException proxyCallbackDelayException) {
                            n3 = 0;
                            continue;
                        }
                        this.m_OutChannel.write(proxyCallbackBuffer.getChunk(), 0, proxyCallbackBuffer.getPayloadLength());
                    } else {
                        this.m_OutChannel.write(arrby, 0, n2);
                    }
                    n3 = 0;
                }
                catch (TooMuchDataForPacketException tooMuchDataForPacketException) {
                    if (this.m_callbackHandler != null) {
                        AnonProxyRequest.sendRemainingBytesRecursion(proxyCallbackBuffer, tooMuchDataForPacketException.getBytesSent(), this.m_OutChannel);
                        n3 = 0;
                    }
                    object = new byte[arrby.length - tooMuchDataForPacketException.getBytesSent()];
                    System.arraycopy(arrby, tooMuchDataForPacketException.getBytesSent(), object, 0, ((Object)object).length);
                    System.arraycopy(object, 0, arrby, 0, ((Object)object).length);
                    n3 = ((Object)object).length;
                }
                this.m_Proxy.transferredBytes(n2 - n3, this.m_iProtocol);
                Thread.yield();
            }
        }
        catch (IOException iOException) {
            LogHolder.log(7, LogType.NET, "Exception in AnonProxyRequest - upstream loop.", iOException);
        }
        catch (ProxyCallbackNotProcessableException proxyCallbackNotProcessableException) {
            try {
                this.m_OutSocket.write(proxyCallbackNotProcessableException.getErrorResponse());
            }
            catch (IOException iOException) {
                // empty catch block
            }
            LogHolder.log(3, LogType.NET, "chunk could not be processed. Terminating", proxyCallbackNotProcessableException);
        }
        this.closeRequest();
        this.m_Proxy.decNumChannels();
    }

    private static void sendRemainingBytesRecursion(ProxyCallbackBuffer proxyCallbackBuffer, int n, OutputStream outputStream) throws IOException {
        byte[] arrby = new byte[proxyCallbackBuffer.getPayloadLength() - n];
        System.arraycopy(proxyCallbackBuffer.getChunk(), n, arrby, 0, arrby.length);
        System.arraycopy(arrby, 0, proxyCallbackBuffer.getChunk(), 0, arrby.length);
        try {
            outputStream.write(arrby);
        }
        catch (TooMuchDataForPacketException tooMuchDataForPacketException) {
            proxyCallbackBuffer.setChunk(arrby);
            AnonProxyRequest.sendRemainingBytesRecursion(proxyCallbackBuffer, tooMuchDataForPacketException.getBytesSent(), outputStream);
        }
    }

    private synchronized void closeRequest() {
        if (this.m_bRequestIsAlive) {
            --ms_nrOfRequests;
            this.m_bRequestIsAlive = false;
        }
        try {
            if (this.m_Channel != null) {
                this.m_Channel.close();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.m_InSocket.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.m_OutSocket.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.m_clientSocket.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (this.m_callbackHandler != null) {
            this.m_callbackHandler.closeRequest(this);
        }
    }

    protected void setHttpParsed(String string) {
        if (string != null && !this.isProxyKeepAliveEnabled()) {
            this.m_hashParsedDomains.put(string, string);
        }
    }

    protected boolean isProxyKeepAliveEnabled() {
        return this.m_hashParsedDomains.size() > 0;
    }

    public boolean isInternalEncodingRequired() {
        return this.internalEncodingRequired;
    }

    public void showBrowserWarning(boolean bl) {
        this.m_bShowBrowserWarning = bl;
    }

    public boolean isBrowserWarningShown() {
        return this.m_bShowBrowserWarning;
    }

    protected void setInternalEncodingRequired(boolean bl) {
        this.internalEncodingRequired = bl;
    }

    protected String[] getContentEncodings() {
        return this.contentEncodings;
    }

    protected void setContentEncodings(String[] arrstring) {
        this.contentEncodings = arrstring;
    }

    final class Response
    implements Runnable {
        Response() {
        }

        public void run() {
            byte[] arrby;
            block22: {
                int n = 0;
                arrby = new byte[2900];
                try {
                    ProxyCallbackBuffer proxyCallbackBuffer = null;
                    while ((n = AnonProxyRequest.this.m_InChannel.read(arrby, 0, 1000)) >= 0) {
                        block21: {
                            int n2 = 0;
                            while (true) {
                                try {
                                    if (AnonProxyRequest.this.m_callbackHandler != null && n > 0) {
                                        proxyCallbackBuffer = new ProxyCallbackBuffer(arrby, 0, n);
                                        try {
                                            AnonProxyRequest.this.m_callbackHandler.deliverDownstream(AnonProxyRequest.this, proxyCallbackBuffer);
                                        }
                                        catch (ProxyCallbackDelayException proxyCallbackDelayException) {
                                            break block21;
                                        }
                                        AnonProxyRequest.this.m_OutSocket.write(proxyCallbackBuffer.getChunk(), 0, proxyCallbackBuffer.getPayloadLength());
                                        if (proxyCallbackBuffer.getStatus() == 0) {
                                            break block22;
                                        }
                                    } else {
                                        AnonProxyRequest.this.m_OutSocket.write(arrby, 0, n);
                                    }
                                    AnonProxyRequest.this.m_OutSocket.flush();
                                }
                                catch (InterruptedIOException interruptedIOException) {
                                    LogHolder.log(0, LogType.NET, "Should never be here: Timeout in sending to Browser!");
                                    if (++n2 <= 3) continue;
                                    throw new IOException("Could not send to Browser...");
                                }
                                break;
                            }
                            AnonProxyRequest.this.m_Proxy.transferredBytes(n, AnonProxyRequest.this.m_iProtocol);
                            Thread.yield();
                        }
                        if (n >= 0 && !AnonProxyRequest.this.m_Channel.isClosed()) continue;
                        break;
                    }
                }
                catch (IOException iOException) {
                    if (!AnonProxyRequest.this.m_Proxy.isConnected() && !AnonProxyRequest.this.m_Proxy.isConnecting()) {
                        LogHolder.log(3, LogType.NET, iOException);
                    } else {
                        LogHolder.log(6, LogType.NET, iOException);
                    }
                }
                catch (ProxyCallbackNotProcessableException proxyCallbackNotProcessableException) {
                    LogHolder.log(3, LogType.NET, proxyCallbackNotProcessableException);
                    try {
                        AnonProxyRequest.this.m_OutSocket.write(proxyCallbackNotProcessableException.getErrorResponse());
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            try {
                AnonProxyRequest.this.m_clientSocket.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (AnonProxyRequest.this.m_bRequestIsAlive) {
                AnonProxyRequest.this.m_threadRequest.interrupt();
            }
            arrby = null;
        }
    }
}

