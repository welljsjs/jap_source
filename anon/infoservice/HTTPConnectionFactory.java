/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import HTTPClient.AuthorizationInfo;
import HTTPClient.AuthorizationPrompter;
import HTTPClient.DefaultAuthHandler;
import HTTPClient.HTTPConnection;
import HTTPClient.NVPair;
import HTTPClient.SocksException;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.infoservice.ProxyInterface;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class HTTPConnectionFactory {
    public static final int HTTP_ENCODING_PLAIN = 0;
    public static final int HTTP_ENCODING_ZLIB = 1;
    public static final int HTTP_ENCODING_GZIP = 2;
    public static final String HTTP_ENCODING_ZLIB_STRING = "deflate";
    public static final String HTTP_ENCODING_GZIP_STRING = "gzip";
    private static Class ms_HTTPConnectionClass = class$HTTPClient$HTTPConnection == null ? (class$HTTPClient$HTTPConnection = HTTPConnectionFactory.class$("HTTPClient.HTTPConnection")) : class$HTTPClient$HTTPConnection;
    private static HTTPConnectionFactory ms_httpConnectionFactoryInstance;
    private Vector m_vecHTTPConnections;
    private int m_timeout;
    private ImmutableProxyInterface m_proxyInterface;
    private boolean m_bUseAuth = false;
    private Class m_classHTTPCLient_ContentEncodingeModule;
    static /* synthetic */ Class class$HTTPClient$HTTPConnection;
    static /* synthetic */ Class class$java$lang$String;

    private HTTPConnectionFactory() {
        this.setNewProxySettings(null, false);
        this.m_timeout = 10;
        try {
            this.m_classHTTPCLient_ContentEncodingeModule = Class.forName("HTTPClient.ContentEncodingModule");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public static HTTPConnectionFactory getInstance() {
        block0: {
            if (ms_httpConnectionFactoryInstance != null) break block0;
            ms_httpConnectionFactoryInstance = new HTTPConnectionFactory();
        }
        return ms_httpConnectionFactoryInstance;
    }

    public synchronized void setNewProxySettings(ImmutableProxyInterface immutableProxyInterface, boolean bl) {
        this.m_proxyInterface = immutableProxyInterface;
        this.m_bUseAuth = bl;
        if (immutableProxyInterface == null || !immutableProxyInterface.isValid()) {
            this.m_proxyInterface = null;
            HTTPConnection.setProxyServer(null, -1);
            HTTPConnection.setSocksServer(null, -1);
            return;
        }
        if (immutableProxyInterface.getProtocol() == 1) {
            HTTPConnection.setProxyServer(immutableProxyInterface.getHost(), immutableProxyInterface.getPort());
            HTTPConnection.setSocksServer(null, -1);
        } else if (immutableProxyInterface.getProtocol() == 3) {
            HTTPConnection.setProxyServer(null, -1);
            try {
                HTTPConnection.setSocksServer(immutableProxyInterface.getHost(), immutableProxyInterface.getPort(), 5);
            }
            catch (SocksException socksException) {
                // empty catch block
            }
            if (this.m_bUseAuth) {
                NVPair[] arrnVPair = new NVPair[]{new NVPair(immutableProxyInterface.getAuthenticationUserID(), immutableProxyInterface.getAuthenticationPassword())};
                AuthorizationInfo.addAuthorization(new AuthorizationInfo(immutableProxyInterface.getHost(), immutableProxyInterface.getPort(), "SOCKS5", "USER/PASS", arrnVPair, null));
            }
        }
    }

    public synchronized void setTimeout(int n) {
        if (n < 0) {
            n = 0;
        }
        this.m_timeout = n;
    }

    public synchronized int getTimeout() {
        return this.m_timeout;
    }

    public synchronized HTTPConnection createHTTPConnection(ListenerInterface listenerInterface) {
        return this.createHTTPConnection(listenerInterface, 0, true, null);
    }

    public synchronized HTTPConnection createHTTPConnection(ListenerInterface listenerInterface, int n, boolean bl) {
        return this.createHTTPConnection(listenerInterface, n, bl, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized HTTPConnection createHTTPConnection(ListenerInterface listenerInterface, int n, boolean bl, Vector vector) {
        HTTPConnection hTTPConnection = null;
        Object object = this;
        synchronized (object) {
            hTTPConnection = this.createHTTPConnectionInternal(listenerInterface);
            if (this.m_proxyInterface != null && this.m_proxyInterface.isAuthenticationUsed()) {
                DefaultAuthHandler.setAuthorizationPrompter(new AuthorizationPrompter(){
                    boolean bAlreadyTried = false;
                    String password;

                    public synchronized NVPair getUsernamePassword(AuthorizationInfo authorizationInfo) {
                        try {
                            this.password = HTTPConnectionFactory.this.m_proxyInterface.getAuthenticationPassword();
                            if (this.password == null) {
                                return null;
                            }
                            if (this.bAlreadyTried) {
                                if (HTTPConnectionFactory.this.m_proxyInterface instanceof ProxyInterface) {
                                    ((ProxyInterface)HTTPConnectionFactory.this.m_proxyInterface).clearAuthenticationPassword();
                                } else {
                                    return null;
                                }
                            }
                            this.bAlreadyTried = true;
                            return new NVPair(HTTPConnectionFactory.this.m_proxyInterface.getAuthenticationUserID(), this.password);
                        }
                        catch (Exception exception) {
                            LogHolder.log(2, LogType.NET, exception);
                            return null;
                        }
                    }
                });
            }
        }
        HTTPConnectionFactory.replaceHeader(hTTPConnection, new NVPair("Cache-Control", "no-cache"));
        HTTPConnectionFactory.replaceHeader(hTTPConnection, new NVPair("Pragma", "no-cache"));
        if (vector != null) {
            object = vector;
            synchronized (object) {
                for (int i = 0; i < vector.size(); ++i) {
                    HTTPConnectionFactory.replaceHeader(hTTPConnection, (NVPair)vector.elementAt(i));
                }
            }
        }
        if (n != 0) {
            if ((n & 1) > 0) {
                if (bl) {
                    hTTPConnection.addModule(this.m_classHTTPCLient_ContentEncodingeModule, -1);
                } else {
                    hTTPConnection.removeModule(this.m_classHTTPCLient_ContentEncodingeModule);
                    HTTPConnectionFactory.replaceHeader(hTTPConnection, new NVPair("Content-Encoding", HTTP_ENCODING_ZLIB_STRING));
                }
            }
        } else {
            hTTPConnection.removeModule(this.m_classHTTPCLient_ContentEncodingeModule);
        }
        hTTPConnection.setTimeout(this.getTimeout() * 1000);
        return hTTPConnection;
    }

    public synchronized HTTPConnection createHTTPConnection(ListenerInterface listenerInterface, ImmutableProxyInterface immutableProxyInterface) {
        return this.createHTTPConnection(listenerInterface, immutableProxyInterface, 0, true, null);
    }

    public synchronized HTTPConnection createHTTPConnection(ListenerInterface listenerInterface, ImmutableProxyInterface immutableProxyInterface, int n, boolean bl, Vector vector) {
        ImmutableProxyInterface immutableProxyInterface2 = this.m_proxyInterface;
        this.setNewProxySettings(immutableProxyInterface, this.m_bUseAuth);
        HTTPConnection hTTPConnection = this.createHTTPConnection(listenerInterface, n, bl, vector);
        this.setNewProxySettings(immutableProxyInterface2, this.m_bUseAuth);
        return hTTPConnection;
    }

    private static void replaceHeader(HTTPConnection hTTPConnection, NVPair nVPair) {
        NVPair[] arrnVPair = hTTPConnection.getDefaultHeaders();
        if (arrnVPair == null || arrnVPair.length == 0) {
            arrnVPair = new NVPair[]{nVPair};
            hTTPConnection.setDefaultHeaders(arrnVPair);
        } else {
            for (int i = 0; i < arrnVPair.length; ++i) {
                if (!arrnVPair[i].getName().equalsIgnoreCase(nVPair.getName())) continue;
                arrnVPair[i] = nVPair;
                hTTPConnection.setDefaultHeaders(arrnVPair);
                return;
            }
            NVPair[] arrnVPair2 = new NVPair[arrnVPair.length + 1];
            System.arraycopy(arrnVPair, 0, arrnVPair2, 0, arrnVPair.length);
            arrnVPair2[arrnVPair.length] = nVPair;
            hTTPConnection.setDefaultHeaders(arrnVPair2);
        }
    }

    private static void setHTTPConnectionClass(Class class_) {
        if (!(class$HTTPClient$HTTPConnection == null ? (class$HTTPClient$HTTPConnection = HTTPConnectionFactory.class$("HTTPClient.HTTPConnection")) : class$HTTPClient$HTTPConnection).isAssignableFrom(class_)) {
            throw new IllegalArgumentException("This is not a valid HTTPConnection class: " + class_);
        }
        ms_httpConnectionFactoryInstance = null;
        ms_HTTPConnectionClass = class_;
    }

    private Vector getCreatedHTTPConnections() {
        return this.m_vecHTTPConnections;
    }

    private HTTPConnection createHTTPConnectionInternal(ListenerInterface listenerInterface) {
        HTTPConnection hTTPConnection;
        if (ms_HTTPConnectionClass == (class$HTTPClient$HTTPConnection == null ? (class$HTTPClient$HTTPConnection = HTTPConnectionFactory.class$("HTTPClient.HTTPConnection")) : class$HTTPClient$HTTPConnection)) {
            hTTPConnection = new HTTPConnection(listenerInterface.getHost(), listenerInterface.getPort());
        } else {
            Class[] arrclass = new Class[2];
            Object[] arrobject = new Object[2];
            arrclass[0] = class$java$lang$String == null ? (class$java$lang$String = HTTPConnectionFactory.class$("java.lang.String")) : class$java$lang$String;
            arrclass[1] = Integer.TYPE;
            arrobject[0] = listenerInterface.getHost();
            arrobject[1] = new Integer(listenerInterface.getPort());
            try {
                hTTPConnection = (HTTPConnection)ms_HTTPConnectionClass.getConstructor(arrclass).newInstance(arrobject);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("Could not construct an HTTPConnection! " + exception);
            }
            if (this.m_vecHTTPConnections == null) {
                this.m_vecHTTPConnections = new Vector();
            }
            this.m_vecHTTPConnections.addElement(hTTPConnection);
        }
        return hTTPConnection;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

