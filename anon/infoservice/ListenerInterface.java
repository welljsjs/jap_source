/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import HTTPClient.ThreadInterruptedIOException;
import anon.infoservice.Database;
import anon.infoservice.ImmutableListenerInterface;
import anon.util.ClassUtil;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ListenerInterface
implements ImmutableListenerInterface,
IXMLEncodable {
    public static final int PORT_MIN_VALUE = 1;
    public static final int PORT_MAX_VALUE = 65535;
    public static final String XML_ELEMENT_NAME = "ListenerInterface";
    public static final String XML_ELEMENT_CONTAINER_NAME = "ListenerInterfaces";
    public static final String XML_ATTR_HIDDEN = "hidden";
    public static final String XML_ATTR_VIRTUAL = "virtual";
    public static final String XML_ELEM_HOST = "Host";
    public static final String XML_ELEM_PORT = "Port";
    public static final String XML_ELEM_FILE = "File";
    private long m_endOfBlocking = 0L;
    private String m_strHostname;
    private int m_iInetPort;
    private int m_iProtocolType;
    private boolean m_bUseInterface = false;
    private boolean m_bVirtual = false;
    private boolean m_bHidden = false;
    static /* synthetic */ Class class$anon$infoservice$ListenerInterface$IListenerInterfaceGetter;

    public ListenerInterface(Element element) throws XMLParseException {
        String string;
        String string2;
        this.m_bHidden = XMLUtil.parseAttribute((Node)element, XML_ATTR_HIDDEN, false);
        this.m_bVirtual = XMLUtil.parseAttribute((Node)element, XML_ATTR_VIRTUAL, false);
        if (this.m_bVirtual && this.m_bHidden) {
            this.m_bHidden = false;
            this.m_bVirtual = false;
        }
        if ((string2 = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "Type"), null)) == null) {
            string2 = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "NetworkProtocol"), null);
        }
        this.setProtocol(string2);
        if (this.getProtocol() == 5) {
            string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_FILE), null);
        } else {
            Node node = XMLUtil.getFirstChildByName(element, XML_ELEM_HOST);
            Node node2 = XMLUtil.getFirstChildByName(element, "IP");
            if (node == null && node2 == null) {
                throw new XMLParseException("Host,IP", "Neither Host nor IP are given.");
            }
            string = XMLUtil.parseValue(node, null);
            if (!ListenerInterface.isValidHostname(string) && !ListenerInterface.isValidIP(string = XMLUtil.parseValue(node2, null))) {
                throw new XMLParseException("Host, IP", "Invalid Host and IP.");
            }
        }
        this.setHostname(string);
        this.setPort(XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_PORT), -1));
        this.setUseInterface(true);
    }

    public ListenerInterface(String string, int n) throws IllegalArgumentException {
        this(string, n, 1);
    }

    public ListenerInterface(String string) throws IllegalArgumentException {
        if (string == null) {
            throw new IllegalArgumentException("Argument given to ListenInterface constructor is NULL!");
        }
        int n = string.indexOf(":");
        int n2 = -1;
        String string2 = null;
        try {
            n2 = Integer.parseInt(string.substring(n + 1, string.length()));
        }
        catch (Exception exception) {
            LogHolder.log(4, LogType.MISC, "Could not parse listener port: ", exception);
        }
        if (n > 0) {
            string2 = string.substring(0, n);
        }
        this.setHostname(string2);
        this.setPort(n2);
        this.setProtocol(1);
        this.setUseInterface(true);
    }

    public ListenerInterface(String string, int n, int n2) throws IllegalArgumentException {
        this.setHostname(string);
        this.setPort(n);
        this.setProtocol(n2);
        this.setUseInterface(true);
    }

    public static boolean isValidPort(int n) {
        return n >= 1 && n <= 65535;
    }

    public static boolean isValidProtocol(String string) {
        return ListenerInterface.recognizeProtocol(string) != -1;
    }

    public static boolean isValidProtocol(int n) {
        return ListenerInterface.recognizeProtocol(n) != -1;
    }

    public static boolean isValidHostname(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static boolean isValidIP(String string) {
        if (string == null || string.indexOf(45) != -1) {
            return false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
        try {
            if (stringTokenizer.countTokens() != 4 && stringTokenizer.countTokens() != 16) {
                throw new NumberFormatException();
            }
            while (stringTokenizer.hasMoreTokens()) {
                if (new Integer(stringTokenizer.nextToken()) <= 255) continue;
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

    public int getProtocol() {
        return this.m_iProtocolType;
    }

    public String getProtocolAsString() {
        switch (this.m_iProtocolType) {
            case 2: {
                return "RAW/TCP";
            }
            case 3: {
                return "socks";
            }
            case 4: {
                return "https";
            }
            case 1: {
                return "HTTP/TCP";
            }
            case 5: {
                return "RAW/UNIX";
            }
        }
        return "UNKNWON/UNKNOWN";
    }

    public String getHost() {
        return this.m_strHostname;
    }

    public int getPort() {
        return this.m_iInetPort;
    }

    public String getId() {
        String string = "";
        if (this.getHost() != null) {
            string = string + this.getHost() + "_";
        }
        string = string + this.getPort() + "_";
        string = string + this.getProtocol() + "_";
        return string;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof ListenerInterface) {
            return this.equals((ListenerInterface)object);
        }
        return false;
    }

    private boolean equals(ListenerInterface listenerInterface) {
        if (this == listenerInterface) {
            return true;
        }
        if (!Util.equals(this.getHost(), listenerInterface.getHost())) {
            return false;
        }
        return this.getPort() == listenerInterface.getPort() && this.getProtocol() == listenerInterface.getProtocol();
    }

    public Element toXmlElement(Document document) {
        return this.toXmlElementInternal(document, XML_ELEMENT_NAME);
    }

    public void setUseInterface(boolean bl) {
        this.m_bUseInterface = bl;
        if (bl) {
            this.m_endOfBlocking = 0L;
        }
    }

    public static boolean isBlockingRecommended(Throwable throwable) {
        boolean bl = false;
        Class<?> class_ = null;
        try {
            class_ = Class.forName("java.net.SocketTimeoutException");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        if (throwable == null) {
            return false;
        }
        if (throwable instanceof ConnectException) {
            bl = true;
        } else if (class_ != null && class_.isAssignableFrom(throwable.getClass())) {
            bl = true;
        } else if (throwable instanceof InterruptedIOException && !(throwable instanceof ThreadInterruptedIOException) && throwable.getMessage() != null && throwable.getMessage().indexOf("timed out") >= 0) {
            bl = true;
        }
        return bl;
    }

    public long getBlockingTimeRemaining() {
        return Math.max(0L, this.m_endOfBlocking - System.currentTimeMillis());
    }

    public void blockInterface(long l) {
        long l2 = System.currentTimeMillis() + l;
        if (l2 < l) {
            l2 = Long.MAX_VALUE;
        }
        if (l > 0L) {
            if (l2 > this.m_endOfBlocking) {
                this.m_endOfBlocking = l2;
                LogHolder.log(4, LogType.NET, "Blocked interface: " + this.toString());
            }
        } else if (this.m_endOfBlocking - System.currentTimeMillis() > 0L) {
            this.m_endOfBlocking = 0L;
            LogHolder.log(4, LogType.NET, "Unblocked/Released interface: " + this.toString());
        }
    }

    public boolean isValid() {
        return ListenerInterface.isValidPort(this.getPort()) && ListenerInterface.isValidHostname(this.getHost()) && this.m_bUseInterface && this.m_endOfBlocking <= System.currentTimeMillis();
    }

    public boolean isVirtual() {
        return this.m_bVirtual;
    }

    public boolean isHidden() {
        return this.m_bHidden;
    }

    protected static int recognizeProtocol(String string) {
        int n = -1;
        if (string != null) {
            if (string.equalsIgnoreCase("HTTP/TCP")) {
                n = 1;
            } else if (string.equalsIgnoreCase("https")) {
                n = 4;
            } else if (string.equalsIgnoreCase("socks")) {
                n = 3;
            } else if (string.equalsIgnoreCase("RAW/TCP")) {
                n = 2;
            } else if (string.equalsIgnoreCase("RAW/UNIX")) {
                n = 5;
            }
        }
        return n;
    }

    protected static int recognizeProtocol(int n) {
        if (n == 1 || n == 4 || n == 2 || n == 3) {
            return n;
        }
        return -1;
    }

    public void setProtocol(String string) {
        if (!ListenerInterface.isValidProtocol(string)) {
            if (ListenerInterface.isValidHostname(this.getHost())) {
                LogHolder.log(5, LogType.NET, "Host " + this.getHost() + " has listener with " + "invalid protocol '" + string + "'!");
            }
            this.m_iProtocolType = 2;
        } else {
            this.m_iProtocolType = ListenerInterface.recognizeProtocol(string);
        }
    }

    public void setProtocol(int n) {
        if (!ListenerInterface.isValidProtocol(n)) {
            if (ListenerInterface.isValidHostname(this.getHost())) {
                LogHolder.log(5, LogType.NET, "Host " + this.getHost() + " has listener with " + "invalid protocol '" + n + "'!");
            }
            this.m_iProtocolType = 2;
        } else {
            this.m_iProtocolType = ListenerInterface.recognizeProtocol(n);
        }
    }

    public void setPort(int n) {
        this.m_iInetPort = !ListenerInterface.isValidPort(n) ? -1 : n;
    }

    public void setHostname(String string) {
        if (!ListenerInterface.isValidHostname(string)) {
            LogHolder.log(5, LogType.NET, "Invalid host name: '" + string + "'");
        } else {
            this.m_strHostname = string.toLowerCase();
        }
    }

    public Vector toVector() {
        Vector<ListenerInterface> vector = new Vector<ListenerInterface>();
        vector.addElement(this);
        return vector;
    }

    protected Element toXmlElementInternal(Document document, String string) {
        Element element;
        Element element2 = document.createElement(string);
        Element element3 = document.createElement("Type");
        Element element4 = null;
        XMLUtil.setValue((Node)element3, this.getProtocolAsString());
        if (this.getProtocol() == 5) {
            element = document.createElement(XML_ELEM_FILE);
            XMLUtil.setValue((Node)element, this.m_strHostname);
        } else {
            element4 = document.createElement(XML_ELEM_PORT);
            XMLUtil.setValue((Node)element4, this.m_iInetPort);
            element = document.createElement(XML_ELEM_HOST);
            XMLUtil.setValue((Node)element, this.m_strHostname);
        }
        if (this.m_bHidden) {
            XMLUtil.setAttribute(element2, XML_ATTR_HIDDEN, this.m_bHidden);
        } else if (this.m_bVirtual) {
            XMLUtil.setAttribute(element2, XML_ATTR_VIRTUAL, this.m_bVirtual);
        }
        element2.appendChild(element3);
        if (element4 != null) {
            element2.appendChild(element4);
        }
        element2.appendChild(element);
        return element2;
    }

    public String toString() {
        return "ListenerInterface (Protocol: " + this.getProtocolAsString() + ")- Host: " + this.getHost() + " Port: " + this.getPort() + " Valid: " + this.isValid();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void unblockInterfacesFromDatabase(Class class_) {
        if (class_ == null || !(class$anon$infoservice$ListenerInterface$IListenerInterfaceGetter == null ? (class$anon$infoservice$ListenerInterface$IListenerInterfaceGetter = ListenerInterface.class$("anon.infoservice.ListenerInterface$IListenerInterfaceGetter")) : class$anon$infoservice$ListenerInterface$IListenerInterfaceGetter).isAssignableFrom(class_)) {
            LogHolder.log(3, LogType.MISC, "Tried to unblock network interfaces for incompatible class: " + class_);
            return;
        }
        Database database = Database.getInstance(class_);
        synchronized (database) {
            Enumeration enumeration = Database.getInstance(class_).getEntrySnapshotAsEnumeration();
            while (enumeration.hasMoreElements()) {
                Vector vector = ((IListenerInterfaceGetter)enumeration.nextElement()).getListenerInterfaces();
                if (vector == null) continue;
                for (int i = 0; i < vector.size(); ++i) {
                    ListenerInterface listenerInterface = (ListenerInterface)vector.elementAt(i);
                    listenerInterface.blockInterface(0L);
                }
            }
        }
    }

    public static void blockInterfacesFromDatabase(IListenerInterfaceGetter iListenerInterfaceGetter) {
        IListenerInterfaceGetter iListenerInterfaceGetter2 = (IListenerInterfaceGetter)((Object)Database.getInstance(iListenerInterfaceGetter.getClass()).getEntryById(iListenerInterfaceGetter.getId()));
        if (iListenerInterfaceGetter2 != null) {
            Vector vector = iListenerInterfaceGetter2.getListenerInterfaces();
            for (int i = 0; vector != null && i < vector.size(); ++i) {
                ListenerInterface listenerInterface = (ListenerInterface)vector.elementAt(i);
                if (iListenerInterfaceGetter.getListenerInterface(listenerInterface.getId()) == null || listenerInterface.getBlockingTimeRemaining() <= 0L) continue;
                iListenerInterfaceGetter.getListenerInterface(listenerInterface.getId()).blockInterface(listenerInterface.getBlockingTimeRemaining());
                LogHolder.log(4, LogType.NET, "Blocked interface " + listenerInterface.getId() + " of " + ClassUtil.getShortClassName(iListenerInterfaceGetter.getClass()) + " " + iListenerInterfaceGetter.getId() + ".");
            }
        }
    }

    public static ListenerInterface parseHostnamePort(String string) throws IllegalArgumentException, NumberFormatException {
        return ListenerInterface.parseHostnamePort(string, 1);
    }

    public static ListenerInterface parseHostnamePort(String string, int n) throws IllegalArgumentException, NumberFormatException {
        String string2 = null;
        int n2 = -1;
        if (string != null && string.trim().length() > 0) {
            int n3 = (string = string.trim()).lastIndexOf(":");
            if (n3 == string.length() - 1) {
                throw new IllegalArgumentException("Definition of [hostname]:[port] is invalid: " + string);
            }
            if (n3 == 0) {
                n2 = Integer.parseInt(string.substring(n3 + 1, string.length()));
            } else if (n3 < 0) {
                string2 = string;
            } else {
                string2 = string.substring(0, n3);
                n2 = Integer.parseInt(string.substring(n3 + 1, string.length()));
            }
        }
        return new ListenerInterface(string2, n2, n);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public static interface IListenerInterfaceGetter {
        public Vector getListenerInterfaces();

        public String getId();

        public ListenerInterface getListenerInterface(String var1);
    }
}

