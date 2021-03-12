/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.connector;

import anon.transport.address.IAddress;
import anon.transport.address.SkypeAddress;
import anon.transport.connection.ChunkConnectionAdapter;
import anon.transport.connection.CommunicationException;
import anon.transport.connection.ConnectionException;
import anon.transport.connection.IConnection;
import anon.transport.connection.IStreamConnection;
import anon.transport.connection.SkypeConnection;
import anon.transport.connector.IConnector;
import com.skype.Application;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.connector.Connector;
import logging.LogHolder;
import logging.LogType;

public class SkypeConnector
implements IConnector {
    public IStreamConnection connect(SkypeAddress skypeAddress) throws ConnectionException {
        Application application = null;
        try {
            Connector.getInstance().setApplicationName(skypeAddress.getApplicationName());
        }
        catch (Exception exception) {
            LogHolder.log(7, LogType.NET, "Skype Connector - exception : " + exception);
        }
        LogHolder.log(7, LogType.NET, "Skype Connector - try to get user id");
        LogHolder.log(7, LogType.TRANSPORT, "Try to register Skype forwarding application");
        try {
            application = Skype.addApplication(skypeAddress.getApplicationName());
        }
        catch (SkypeException skypeException) {
            throw new CommunicationException("Unable to create desired Skype Application " + skypeAddress.getApplicationName());
        }
        if (application == null) {
            throw new CommunicationException("Unable to create desired Skype Application " + skypeAddress.getApplicationName());
        }
        LogHolder.log(7, LogType.TRANSPORT, "Try to get a stream from Skype");
        Stream[] arrstream = null;
        try {
            arrstream = application.connect(skypeAddress.getUserID());
        }
        catch (SkypeException skypeException) {
            throw new CommunicationException("Unable to connect to User with ID " + skypeAddress.getUserID());
        }
        if (arrstream == null || arrstream.length == 0) {
            throw new CommunicationException("Unable to connect to User with ID " + skypeAddress.getUserID());
        }
        LogHolder.log(7, LogType.TRANSPORT, "Setup the base Skype connection");
        SkypeConnection skypeConnection = new SkypeConnection(arrstream[0]);
        return new ChunkConnectionAdapter(skypeConnection);
    }

    public IConnection connect(IAddress iAddress) throws ConnectionException {
        if (!(iAddress instanceof SkypeAddress)) {
            throw new IllegalArgumentException("Connector can only handel Address of type SkypeAddress");
        }
        return this.connect((SkypeAddress)iAddress);
    }
}

