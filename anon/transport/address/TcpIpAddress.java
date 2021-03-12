/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.address;

import anon.transport.address.AddressMappingException;
import anon.transport.address.AddressParameter;
import anon.transport.address.Endpoint;
import anon.transport.address.IAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TcpIpAddress
implements IAddress {
    public static final String TRANSPORT_IDENTIFIER = "tcpip";
    private static final String IP_PARAMETER = "ip-address";
    private static final String PORT_PARAMETER = "port";
    protected int m_port;
    protected InetAddress m_ipAddress;

    public TcpIpAddress(String string, int n) {
        try {
            this.m_ipAddress = InetAddress.getByName(string);
        }
        catch (UnknownHostException unknownHostException) {
            this.m_ipAddress = null;
        }
        this.m_port = n;
    }

    public TcpIpAddress(InetAddress inetAddress, int n) {
        this.m_ipAddress = inetAddress;
        this.m_port = n;
    }

    public TcpIpAddress(Endpoint endpoint) throws AddressMappingException {
        String string = endpoint.getParameter(IP_PARAMETER);
        if (string == null) {
            throw new AddressMappingException("IP Parameter is missing");
        }
        try {
            this.m_ipAddress = InetAddress.getByName(string);
        }
        catch (UnknownHostException unknownHostException) {
            throw new AddressMappingException("IP-Address could not be parsed.");
        }
        string = endpoint.getParameter(PORT_PARAMETER);
        if (string == null) {
            throw new AddressMappingException("Port Parameter is missing");
        }
        try {
            this.m_port = Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            throw new AddressMappingException("Port could not be parsed.");
        }
    }

    public InetAddress getIPAddress() {
        return this.m_ipAddress;
    }

    public int getPort() {
        return this.m_port;
    }

    public String getHostname() {
        return this.m_ipAddress.getHostName();
    }

    public AddressParameter[] getAllParameters() {
        AddressParameter[] arraddressParameter = new AddressParameter[]{new AddressParameter(IP_PARAMETER, this.m_ipAddress.getHostAddress()), new AddressParameter(PORT_PARAMETER, String.valueOf(this.m_port))};
        return arraddressParameter;
    }

    public String getTransportIdentifier() {
        return TRANSPORT_IDENTIFIER;
    }
}

