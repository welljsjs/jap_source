/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ForwardServerManager;
import anon.forward.server.SkypeServerManager;
import anon.transport.address.AddressMappingException;
import anon.transport.address.Endpoint;
import anon.transport.address.IAddress;
import anon.transport.address.MalformedURNException;
import anon.transport.address.SkypeAddress;
import anon.util.JAPMessages;
import jap.forward.JAPRoutingMessage;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class JAPRoutingForwardingModeSelector
extends Observable {
    public static final int FORWARDING_MODE_UNKNOW = -1;
    public static final String FORWARDING_MODE_NAME_UNKNOW = "unknow";
    public static final int FORWARDING_MODE_TCP = 0;
    public static final String FORWARDING_MODE_NAME_TCP = "tcpip";
    public static final int FORWARDING_MODE_SKYPE = 1;
    public static final String FORWARDING_MODE_NAME_SKYPE = "skype";
    public static final int FORWARDING_MODE_LOCAL = 2;
    public static final String FORWARDING_MODE_NAME_LOCAL = "local";
    private static final String DEFAULT_SKYPE_FORWARDER_ADDRESS = "japforwarder";
    private static final String ALTERNATIVE_SKYPE_FORWARDER_ADDRESS = "jondosforwarder";
    private static final String DEFAULT_SKYPE_APP_NAME = "jap";
    private static String m_skypeForwarderAddress;
    private static String m_skypeApplicationName;
    private static int m_serverPort;
    private Hashtable m_forwardingModes;
    private int m_currentForwardingMode = 0;

    private static String buildSkypeURN(String string, String string2) {
        return "urn:endpoint:skype:user(" + string + "):application(" + string2 + ")";
    }

    public JAPRoutingForwardingModeSelector() {
        m_serverPort = (int)Math.round(Math.abs(Math.random() * 64511.0)) + 1025;
        m_skypeForwarderAddress = DEFAULT_SKYPE_FORWARDER_ADDRESS;
        m_skypeApplicationName = DEFAULT_SKYPE_APP_NAME;
        this.m_forwardingModes = new Hashtable();
        this.m_forwardingModes.put(new Integer(0), new TransportMode(0, FORWARDING_MODE_NAME_TCP, "settingsRoutingForwardingModeTCPIP", null));
        try {
            TransportMode transportMode = new TransportMode(1, FORWARDING_MODE_NAME_SKYPE, "settingsRoutingForwardingModeskype", new SkypeAddress(JAPRoutingForwardingModeSelector.getSkypeEndPoint(m_skypeForwarderAddress)));
            transportMode.addAddress(new SkypeAddress(JAPRoutingForwardingModeSelector.getSkypeEndPoint(ALTERNATIVE_SKYPE_FORWARDER_ADDRESS)));
            this.m_forwardingModes.put(new Integer(1), transportMode);
        }
        catch (AddressMappingException addressMappingException) {
            // empty catch block
        }
    }

    public String getDefaultSkypeForwarderAddress() {
        return DEFAULT_SKYPE_FORWARDER_ADDRESS;
    }

    public String getSkypeForwarderAddress() {
        return m_skypeForwarderAddress;
    }

    public void setSkypeForwarderAddress(String string) {
        LogHolder.log(7, LogType.GUI, "We set the forwarder address to " + string);
        m_skypeForwarderAddress = string;
        try {
            TransportMode transportMode = (TransportMode)this.m_forwardingModes.get(new Integer(1));
            transportMode.setAddress(new SkypeAddress(JAPRoutingForwardingModeSelector.getSkypeEndPoint(string)));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static String getSkypeApplicationName() {
        return m_skypeApplicationName;
    }

    public void setSkypeApplicationName(String string) {
        m_skypeApplicationName = string;
    }

    private static String getCurrentSkypeURN(String string) {
        return JAPRoutingForwardingModeSelector.buildSkypeURN(string, m_skypeApplicationName);
    }

    private static Endpoint getSkypeEndPoint(String string) {
        Endpoint endpoint = null;
        try {
            endpoint = new Endpoint(JAPRoutingForwardingModeSelector.getCurrentSkypeURN(string));
        }
        catch (MalformedURNException malformedURNException) {
            malformedURNException.printStackTrace();
        }
        return endpoint;
    }

    public TransportMode getCurrentForwardingMode() {
        TransportMode transportMode = null;
        transportMode = (TransportMode)this.m_forwardingModes.get(new Integer(this.m_currentForwardingMode));
        return transportMode;
    }

    public IAddress getUserProvidetForwarder() {
        return this.getCurrentForwardingMode().getAddress();
    }

    public boolean setCurrentForwardingMode(TransportMode transportMode) {
        if (transportMode == null) {
            return false;
        }
        Enumeration enumeration = this.m_forwardingModes.keys();
        while (enumeration.hasMoreElements()) {
            Integer n = (Integer)enumeration.nextElement();
            if (transportMode != this.m_forwardingModes.get(n)) continue;
            return this.setCurrentForwardingMode(n);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setCurrentForwardingMode(int n) {
        LogHolder.log(7, LogType.GUI, "We set the forwarding mode to " + n);
        if (n == -1) {
            return false;
        }
        if (n >= this.m_forwardingModes.size()) {
            return false;
        }
        if (n == this.m_currentForwardingMode) {
            return false;
        }
        Hashtable hashtable = this.m_forwardingModes;
        synchronized (hashtable) {
            this.m_currentForwardingMode = n;
            this.setChanged();
            this.notifyObservers(new JAPRoutingMessage(17));
        }
        return true;
    }

    public static int getServerPort() {
        return m_serverPort;
    }

    public void setServerPort(int n) {
        m_serverPort = n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getForwardingModes() {
        Vector vector = new Vector();
        Hashtable hashtable = this.m_forwardingModes;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_forwardingModes.elements();
            while (enumeration.hasMoreElements()) {
                vector.addElement(enumeration.nextElement());
            }
        }
        Collections.sort(vector);
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getIdFromIdentifier(String string) {
        Hashtable hashtable = this.m_forwardingModes;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_forwardingModes.elements();
            while (enumeration.hasMoreElements()) {
                TransportMode transportMode = (TransportMode)enumeration.nextElement();
                if (!string.equals(transportMode.getName())) continue;
                return transportMode.getIdentifier();
            }
        }
        return -1;
    }

    public static final class TransportMode
    implements Comparable {
        private int m_identifier;
        private String m_forwardModeName;
        private Vector m_forwadingAddress = new Vector();
        private int m_iCurrentForwardingAddress = 0;
        private String m_name;
        public static final TransportMode UNKNOWN = new TransportMode(-1, "unknow", "", null);

        public TransportMode(int n, String string, String string2, IAddress iAddress) {
            this.m_forwadingAddress.addElement(iAddress);
            this.m_identifier = n;
            this.m_forwardModeName = string2;
            this.m_name = string;
        }

        public int compareTo(Object object) {
            if (object == null || !(object instanceof TransportMode)) {
                return 0;
            }
            if (this.getIdentifier() > ((TransportMode)object).getIdentifier()) {
                return 1;
            }
            return -1;
        }

        public int getIdentifier() {
            return this.m_identifier;
        }

        public String toString() {
            return JAPMessages.getString(this.m_forwardModeName);
        }

        public Object startServer(String string) {
            return ForwardServerManager.getInstance().addServerManager(new SkypeServerManager(string));
        }

        public Object startServer() {
            if (this.getIdentifier() == 1) {
                return this.startServer(JAPRoutingForwardingModeSelector.getSkypeApplicationName());
            }
            if (this.getIdentifier() == 0) {
                return ForwardServerManager.getInstance().addListenSocket(JAPRoutingForwardingModeSelector.getServerPort());
            }
            return null;
        }

        public String getName() {
            return this.m_name;
        }

        public IAddress getAddress() {
            if (this.m_forwadingAddress.size() == 0) {
                return null;
            }
            return (IAddress)this.m_forwadingAddress.elementAt(this.m_iCurrentForwardingAddress);
        }

        public IAddress nextAddress() {
            if (this.m_forwadingAddress.size() == 0) {
                return null;
            }
            if (this.m_iCurrentForwardingAddress == this.m_forwadingAddress.size() - 1) {
                this.m_iCurrentForwardingAddress = -1;
            }
            ++this.m_iCurrentForwardingAddress;
            return this.getAddress();
        }

        public void setAddress(IAddress iAddress) {
            this.m_forwadingAddress = new Vector();
            this.m_forwadingAddress.addElement(iAddress);
            try {
                this.addAddress(new SkypeAddress(JAPRoutingForwardingModeSelector.getSkypeEndPoint(JAPRoutingForwardingModeSelector.DEFAULT_SKYPE_FORWARDER_ADDRESS)));
                this.addAddress(new SkypeAddress(JAPRoutingForwardingModeSelector.getSkypeEndPoint(JAPRoutingForwardingModeSelector.ALTERNATIVE_SKYPE_FORWARDER_ADDRESS)));
            }
            catch (AddressMappingException addressMappingException) {
                // empty catch block
            }
        }

        private void addAddress(IAddress iAddress) {
            if (this.m_forwadingAddress.indexOf(iAddress) < 0) {
                this.m_forwadingAddress.addElement(iAddress);
            }
        }

        public boolean isLocal() {
            return this.getIdentifier() == 2;
        }

        public boolean isSkype() {
            return this.getIdentifier() == 1;
        }
    }
}

