/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connector;

import anon.transport.address.IAddress;
import anon.transport.address.TcpIpAddress;
import anon.transport.connection.CommunicationException;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IConnection;
import anon.transport.connection.IStreamConnection;
import anon.transport.connection.SocketConnection;
import anon.transport.connector.IConnector;
import java.io.IOException;
import java.net.Socket;

public class TcpIpConnector
implements IConnector {
    public IStreamConnection connect(TcpIpAddress tcpIpAddress) throws ConnectionException {
        try {
            Socket socket = new Socket(tcpIpAddress.getIPAddress(), tcpIpAddress.getPort());
            return new SocketConnection(socket);
        }
        catch (IOException iOException) {
            throw new CommunicationException(iOException);
        }
    }

    public IConnection connect(IAddress iAddress) throws ConnectionException {
        if (!(iAddress instanceof TcpIpAddress)) {
            throw new IllegalArgumentException("Connector can only handel Address of type TcpIpAddress");
        }
        return null;
    }
}

