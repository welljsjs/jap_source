/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.crypto.tinytls.TinyTLS;
import anon.infoservice.ImmutableProxyInterface;
import java.io.IOException;
import java.net.SocketException;

public class FirstOnionRouterConnectionThread
implements Runnable {
    private TinyTLS m_tls;
    private Thread m_thread = null;
    private String m_name;
    private int m_port;
    private long m_timeout;
    private Exception m_exception;
    private Object m_oNotifySync = new Object();
    private ImmutableProxyInterface m_proxyInterface;

    public FirstOnionRouterConnectionThread(String string, int n, long l, ImmutableProxyInterface immutableProxyInterface) {
        this.m_name = string;
        this.m_port = n;
        this.m_timeout = l;
        this.m_exception = null;
        this.m_proxyInterface = immutableProxyInterface;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TinyTLS getConnection() throws IOException {
        this.m_thread = new Thread((Runnable)this, "FirstOnionRouterConnectionThread");
        this.m_thread.setDaemon(true);
        this.m_thread.start();
        Object object = this.m_oNotifySync;
        synchronized (object) {
            try {
                this.m_oNotifySync.wait(this.m_timeout);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        if (this.m_exception != null) {
            throw new IOException(this.m_exception.getMessage());
        }
        if (this.m_tls == null) {
            throw new SocketException("Connection timed out");
        }
        return this.m_tls;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        TinyTLS tinyTLS = null;
        try {
            tinyTLS = new TinyTLS(this.m_name, this.m_port, this.m_proxyInterface);
        }
        catch (Exception exception) {
            this.m_exception = exception;
        }
        this.m_tls = tinyTLS;
        Object object = this.m_oNotifySync;
        synchronized (object) {
            this.m_oNotifySync.notify();
        }
    }
}

