/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MixCascadeExitAddresses
extends AbstractDatabaseEntry
implements IXMLEncodable {
    public static final long EXIT_ADDRESS_TTL = 604800000L;
    public static final long EXIT_ADDRESS_TTL_INFOSERVICE = 86400000L;
    public static final String XML_ELEMENT_CONTAINER_NAME = "ExitAddressesList";
    public static final String XML_ELEMENT_NAME = "ExitAddresses";
    public static final String XML_ELEMENT_ADDRESS_NAME = "ExitAddress";
    private static final String XML_ATTR_LAST_UPDATE = "lastUpdate";
    public static final String XML_ATTR_DISTRIBUTION = "distribution";
    private static final String XML_ATTR_INFO_SERVICE = "infoservice";
    public static final String XML_ATTR_PAYMENT = "payment";
    private long m_lastUpdate;
    private String m_strCascadeId = null;
    private Hashtable m_tblAddresses = new Hashtable();
    private int m_distribution = 0;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$MixCascadeExitAddresses;
    static /* synthetic */ Class class$java$net$InetAddress;

    private MixCascadeExitAddresses(String string, long l) {
        super(l + 604800000L);
        this.m_strCascadeId = string;
        this.m_lastUpdate = l;
    }

    public MixCascadeExitAddresses(Element element) throws XMLParseException {
        super(System.currentTimeMillis() + 604800000L);
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        XMLUtil.assertNotNull(element, "id");
        this.m_strCascadeId = XMLUtil.parseAttribute((Node)element, "id", null);
        this.m_distribution = XMLUtil.parseAttribute((Node)element, XML_ATTR_DISTRIBUTION, 6);
        MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = MixCascadeExitAddresses.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_strCascadeId);
        if (mixCascade != null) {
            this.m_distribution = mixCascade.getDistribution();
        }
        this.m_lastUpdate = System.currentTimeMillis();
        NodeList nodeList = element.getElementsByTagName(XML_ELEMENT_ADDRESS_NAME);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            InetAddress inetAddress;
            long l = XMLUtil.parseAttribute(nodeList.item(i), XML_ATTR_LAST_UPDATE, System.currentTimeMillis());
            XMLUtil.assertNotNull(nodeList.item(i));
            try {
                inetAddress = InetAddress.getByName(XMLUtil.parseValue(nodeList.item(i), null));
            }
            catch (UnknownHostException unknownHostException) {
                LogHolder.log(4, LogType.NET, unknownHostException);
                continue;
            }
            this.addInetAddress(inetAddress, this.m_distribution, l, null);
        }
    }

    public String getId() {
        return this.m_strCascadeId;
    }

    public int getDistribution() {
        return this.m_distribution;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.m_lastUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String createExitAddressAsString() {
        Hashtable hashtable = this.m_tblAddresses;
        synchronized (hashtable) {
            Enumeration<Object> enumeration = this.m_tblAddresses.keys();
            if (this.m_tblAddresses.size() == 1) {
                InetAddress inetAddress = (InetAddress)enumeration.nextElement();
                Hashtable hashtable2 = (Hashtable)this.m_tblAddresses.get(inetAddress);
                enumeration = hashtable2.elements();
                while (enumeration.hasMoreElements()) {
                    if (((InfoServiceID)enumeration.nextElement()).getLastUpdate() < System.currentTimeMillis() - 604800000L) continue;
                    return inetAddress.getHostAddress();
                }
                return null;
            }
            if (this.m_tblAddresses.size() > 1) {
                int n;
                boolean bl = false;
                String string = null;
                Vector<InetAddress> vector = new Vector<InetAddress>();
                while (enumeration.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)enumeration.nextElement();
                    Hashtable hashtable3 = (Hashtable)this.m_tblAddresses.get(inetAddress);
                    Enumeration enumeration2 = hashtable3.elements();
                    n = 0;
                    while (enumeration2.hasMoreElements()) {
                        InfoServiceID infoServiceID = (InfoServiceID)enumeration2.nextElement();
                        if (infoServiceID.getLastUpdate() < System.currentTimeMillis() - 604800000L) continue;
                        ++n;
                        if (infoServiceID.getID() != "OWN_ID") continue;
                        n = Integer.MAX_VALUE;
                        break;
                    }
                    if (n <= 1) continue;
                    vector.addElement(inetAddress);
                }
                if (vector.size() == 0) {
                    return null;
                }
                String string2 = ((InetAddress)vector.elementAt(0)).getHostAddress();
                if (vector.size() == 1) {
                    return string2;
                }
                for (n = 1; n < vector.size(); ++n) {
                    string = ((InetAddress)vector.elementAt(n)).getHostAddress();
                    int n2 = string.lastIndexOf(".");
                    if (n2 < 0) {
                        n2 = string.lastIndexOf(":");
                        bl = true;
                    }
                    if (string2.startsWith(string = string.substring(0, n2))) continue;
                    return null;
                }
                return string + (bl ? ":" : ".") + "*";
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MixCascadeExitAddresses addInetAddress(String string, InetAddress inetAddress, int n, String string2) {
        Database database = Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = MixCascadeExitAddresses.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses);
        synchronized (database) {
            MixCascadeExitAddresses mixCascadeExitAddresses = null;
            MixCascadeExitAddresses mixCascadeExitAddresses2 = null;
            long l = System.currentTimeMillis();
            if (string != null && string.trim().length() > 0 && inetAddress != null) {
                mixCascadeExitAddresses = (MixCascadeExitAddresses)Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = MixCascadeExitAddresses.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses).getEntryById(string);
                if (mixCascadeExitAddresses != null) {
                    l = mixCascadeExitAddresses.getLastUpdate() + 1L;
                }
                mixCascadeExitAddresses2 = new MixCascadeExitAddresses(string, l);
                if (mixCascadeExitAddresses != null) {
                    Hashtable hashtable = mixCascadeExitAddresses.m_tblAddresses;
                    synchronized (hashtable) {
                        Enumeration enumeration = mixCascadeExitAddresses.m_tblAddresses.keys();
                        while (enumeration.hasMoreElements()) {
                            InetAddress inetAddress2 = (InetAddress)enumeration.nextElement();
                            Hashtable hashtable2 = (Hashtable)mixCascadeExitAddresses.m_tblAddresses.get(inetAddress2);
                            Enumeration enumeration2 = hashtable2.elements();
                            while (enumeration2.hasMoreElements()) {
                                InfoServiceID infoServiceID = (InfoServiceID)enumeration2.nextElement();
                                mixCascadeExitAddresses2.addInetAddress(inetAddress2, n, infoServiceID.getLastUpdate(), infoServiceID.getID());
                            }
                        }
                    }
                }
                if (mixCascadeExitAddresses2.addInetAddress(inetAddress, n, string2) && Database.getInstance(class$anon$infoservice$MixCascadeExitAddresses == null ? (class$anon$infoservice$MixCascadeExitAddresses = MixCascadeExitAddresses.class$("anon.infoservice.MixCascadeExitAddresses")) : class$anon$infoservice$MixCascadeExitAddresses).update(mixCascadeExitAddresses2)) {
                    return mixCascadeExitAddresses2;
                }
            }
            return mixCascadeExitAddresses;
        }
    }

    public static boolean isValidAddress(InetAddress inetAddress) {
        return MixCascadeExitAddresses.isValidAddress(inetAddress, "isAnyLocalAddress") || MixCascadeExitAddresses.isValidAddress(inetAddress, "isLoopbackAddress") || MixCascadeExitAddresses.isValidAddress(inetAddress, "isLinkLocalAddress") || MixCascadeExitAddresses.isValidAddress(inetAddress, "isMulticastAddress") || MixCascadeExitAddresses.isValidAddress(inetAddress, "isSiteLocalAddress");
    }

    private static boolean isValidAddress(InetAddress inetAddress, String string) {
        try {
            return (Boolean)(class$java$net$InetAddress == null ? (class$java$net$InetAddress = MixCascadeExitAddresses.class$("java.net.InetAddress")) : class$java$net$InetAddress).getMethod(string, null).invoke(inetAddress, null) == false;
        }
        catch (Exception exception) {
            return true;
        }
    }

    private boolean addInetAddress(InetAddress inetAddress, int n, String string) {
        return this.addInetAddress(inetAddress, n, System.currentTimeMillis(), string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean addInetAddress(InetAddress inetAddress, int n, long l, String string) {
        boolean bl = false;
        if (string == null) {
            string = "OWN_ID";
        }
        Hashtable hashtable = this.m_tblAddresses;
        synchronized (hashtable) {
            int n2;
            InfoServiceID infoServiceID;
            Hashtable hashtable2;
            Enumeration enumeration = this.m_tblAddresses.keys();
            Vector<InfoServiceID> vector = new Vector<InfoServiceID>();
            while (enumeration.hasMoreElements()) {
                InetAddress inetAddress2 = (InetAddress)enumeration.nextElement();
                hashtable2 = (Hashtable)this.m_tblAddresses.get(inetAddress2);
                Enumeration enumeration2 = hashtable2.elements();
                while (enumeration2.hasMoreElements()) {
                    infoServiceID = (InfoServiceID)enumeration2.nextElement();
                    if (infoServiceID.getLastUpdate() >= System.currentTimeMillis() - 604800000L) continue;
                    LogHolder.log(4, LogType.DB, "Exit address expired: " + infoServiceID);
                    vector.addElement(infoServiceID);
                    bl = true;
                }
            }
            for (n2 = 0; n2 < vector.size(); ++n2) {
                infoServiceID = (InfoServiceID)vector.elementAt(n2);
                hashtable2 = (Hashtable)this.m_tblAddresses.get(infoServiceID.getInetAddress());
                hashtable2.remove(infoServiceID.getID());
                if (hashtable2.size() != 0) continue;
                this.m_tblAddresses.remove(infoServiceID.getInetAddress());
            }
            if (this.m_distribution != n) {
                this.m_distribution = n;
                bl = true;
            }
            if (l < System.currentTimeMillis() - 604800000L) {
                return false;
            }
            n2 = 0;
            if (!this.m_tblAddresses.containsKey(inetAddress)) {
                hashtable2 = new Hashtable();
                hashtable2.put(string, new InfoServiceID(inetAddress, string, l));
                bl = true;
                n2 = 1;
            } else {
                hashtable2 = (Hashtable)this.m_tblAddresses.get(inetAddress);
                if (!hashtable2.contains(string) || ((InfoServiceID)hashtable2.get(string)).getLastUpdate() < l) {
                    hashtable2.put(string, new InfoServiceID(inetAddress, string, l));
                    bl = true;
                    n2 = 1;
                }
            }
            this.m_tblAddresses.put(inetAddress, hashtable2);
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element, "id", this.getId());
        XMLUtil.setAttribute(element, XML_ATTR_DISTRIBUTION, this.getDistribution());
        Hashtable hashtable = this.m_tblAddresses;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_tblAddresses.keys();
            Vector<InetAddress> vector = new Vector<InetAddress>();
            while (enumeration.hasMoreElements()) {
                InfoServiceID infoServiceID;
                InetAddress inetAddress = (InetAddress)enumeration.nextElement();
                Hashtable hashtable2 = (Hashtable)this.m_tblAddresses.get(inetAddress);
                if (!hashtable2.containsKey("OWN_ID") && hashtable2.size() <= 1) continue;
                String string = null;
                long l = 0L;
                if (hashtable2.size() > 1) {
                    hashtable2.remove("OWN_ID");
                    if (hashtable2.size() > 1) {
                        l = 0L;
                        Enumeration enumeration2 = hashtable2.elements();
                        while (enumeration2.hasMoreElements()) {
                            infoServiceID = (InfoServiceID)enumeration2.nextElement();
                            if (infoServiceID.getLastUpdate() <= l) continue;
                            l = infoServiceID.getLastUpdate();
                            string = infoServiceID.getID();
                        }
                    }
                } else if (hashtable2.containsKey("OWN_ID")) {
                    infoServiceID = (InfoServiceID)hashtable2.get("OWN_ID");
                    l = infoServiceID.getLastUpdate();
                    string = "OWN_ID";
                }
                if (l >= System.currentTimeMillis() - 604800000L) {
                    Element element2 = document.createElement(XML_ELEMENT_ADDRESS_NAME);
                    XMLUtil.setAttribute(element2, XML_ATTR_LAST_UPDATE, l);
                    if (string != null && !string.equals("OWN_ID")) {
                        XMLUtil.setAttribute(element2, XML_ATTR_INFO_SERVICE, string);
                    }
                    XMLUtil.setValue((Node)element2, inetAddress.getHostAddress());
                    element.appendChild(element2);
                    continue;
                }
                vector.addElement(inetAddress);
            }
            for (int i = 0; i < vector.size(); ++i) {
                this.m_tblAddresses.remove(vector.elementAt(i));
            }
        }
        return element;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private static class InfoServiceID {
        private static final String OWN_ID = "OWN_ID";
        private String m_ID;
        private long m_lLastUpdate;
        private InetAddress m_addInet;

        private InfoServiceID(InetAddress inetAddress, String string, long l) {
            this.m_ID = string;
            this.m_addInet = inetAddress;
            this.m_lLastUpdate = l;
        }

        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (!(object instanceof InfoServiceID)) {
                return false;
            }
            return ((InfoServiceID)object).getID().equals(this.getID());
        }

        public int hashCode() {
            return this.m_ID.hashCode();
        }

        public InetAddress getInetAddress() {
            return this.m_addInet;
        }

        public String getID() {
            return this.m_ID;
        }

        public long getLastUpdate() {
            return this.m_lLastUpdate;
        }

        public String toString() {
            return this.m_ID + " / " + this.m_addInet;
        }
    }
}

