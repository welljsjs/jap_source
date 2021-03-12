/*
 * Decompiled with CFR 0.150.
 */
package anon.forward;

import anon.client.TrustModel;
import anon.forward.LocalAddress;
import anon.forward.LocalForwarder;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.shared.ProxyConnection;
import anon.transport.address.IAddress;
import anon.transport.address.SkypeAddress;
import anon.transport.address.TcpIpAddress;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IStreamConnection;
import anon.transport.connector.SkypeConnector;
import java.net.Socket;
import logging.LogHolder;
import logging.LogType;

public class ForwardUtils {
    private static ForwardUtils ms_fuInstance = null;
    ImmutableProxyInterface m_proxyInterface;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public static ForwardUtils getInstance() {
        if (ms_fuInstance == null) {
            ms_fuInstance = new ForwardUtils();
        }
        return ms_fuInstance;
    }

    private ForwardUtils() {
    }

    public synchronized void setProxySettings(ImmutableProxyInterface immutableProxyInterface) {
        this.m_proxyInterface = immutableProxyInterface;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ProxyConnection createProxyConnection(String string, int n) {
        ProxyConnection proxyConnection = null;
        try {
            ForwardUtils forwardUtils = this;
            synchronized (forwardUtils) {
                proxyConnection = new ProxyConnection(HTTPConnectionFactory.getInstance().createHTTPConnection(new ListenerInterface(string, n), this.m_proxyInterface).Connect());
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.NET, exception);
        }
        return proxyConnection;
    }

    public IStreamConnection createForwardingConnection(IAddress iAddress) {
        TrustModel.getCurrentTrustModel().unblockInterfacesFromDatabase();
        ListenerInterface.unblockInterfacesFromDatabase(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = ForwardUtils.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry);
        if (iAddress instanceof TcpIpAddress) {
            TcpIpAddress tcpIpAddress = (TcpIpAddress)iAddress;
            return this.createProxyConnection(tcpIpAddress.getHostname(), tcpIpAddress.getPort());
        }
        if (iAddress instanceof SkypeAddress) {
            LogHolder.log(7, LogType.NET, "forwardUtils:createconnection() start connection to skype forwarder");
            SkypeConnector skypeConnector = new SkypeConnector();
            LogHolder.log(7, LogType.NET, "forwardUtils:createconnection() skype conector object created");
            try {
                return skypeConnector.connect((SkypeAddress)iAddress);
            }
            catch (ConnectionException connectionException) {
                LogHolder.log(3, LogType.TRANSPORT, "Unable to create Skype Forwarding Connection. Cause: " + connectionException.getMessage());
            }
        } else if (iAddress instanceof LocalAddress) {
            try {
                return (IStreamConnection)LocalForwarder.getConnector().connect(iAddress);
            }
            catch (ConnectionException connectionException) {
                LogHolder.log(3, LogType.TRANSPORT, "unable to contact local forwarder. " + connectionException.getMessage());
            }
        }
        return null;
    }

    public Socket createConnection(String string, int n) {
        ProxyConnection proxyConnection = this.createProxyConnection(string, n);
        Socket socket = null;
        if (proxyConnection != null) {
            socket = proxyConnection.getSocket();
        }
        return socket;
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

